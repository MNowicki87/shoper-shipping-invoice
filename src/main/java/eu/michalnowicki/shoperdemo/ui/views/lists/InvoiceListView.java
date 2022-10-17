package eu.michalnowicki.shoperdemo.ui.views.lists;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import eu.michalnowicki.shoperdemo.backend.domain.delivery_invoice.DeliveryInvoice;
import eu.michalnowicki.shoperdemo.backend.domain.delivery_invoice.DeliveryInvoiceFacade;
import eu.michalnowicki.shoperdemo.backend.emails.EmailService;
import eu.michalnowicki.shoperdemo.backend.utils.ZipFileCreator;
import eu.michalnowicki.shoperdemo.ui.MainLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

import static eu.michalnowicki.shoperdemo.backend.utils.Constants.DATE_FORMAT;
import static eu.michalnowicki.shoperdemo.backend.utils.Constants.PLN_NUMBER_FORMAT;

@Route(value = "delivery-invoices", layout = MainLayout.class)
@PageTitle("Faktury zaliczkowe")
public class InvoiceListView extends VerticalLayout {
   
   private final Grid<DeliveryInvoice> invoicesGrid = new Grid<>(DeliveryInvoice.class);
   private final transient DeliveryInvoiceFacade invoiceFacade;
   private final transient EmailService emailService;
   
   
   public InvoiceListView(final DeliveryInvoiceFacade invoiceFacade,
                          final EmailService emailService) {
      this.invoiceFacade = invoiceFacade;
      this.emailService = emailService;
      
      addClassName("list-view");
      final var content = new Div(invoicesGrid);
      
      setSizeFull();
      content.addClassName("orders-content");
      
      configureGrid();
      updateList();
      
      add(content, getButtons());
   }
   
   private void configureGrid() {
      invoicesGrid.addClassName("order-grid");
      invoicesGrid.setSizeFull();
      invoicesGrid.removeAllColumns();
      invoicesGrid.addColumn(new LocalDateRenderer<>(DeliveryInvoice::getIssueDate, DATE_FORMAT))
            .setHeader("Data wystawienia");
      invoicesGrid.addColumn(invoice -> invoice.toDto().getInvoiceNumber())
            .setHeader("Nr faktury");
      invoicesGrid.addColumn(new NumberRenderer<>(DeliveryInvoice::getGrossAmount, PLN_NUMBER_FORMAT))
            .setHeader("Kwota brutto");
      invoicesGrid.addColumn(invoice -> invoice.getOrder().getId()).setHeader("Zamówienie");
      invoicesGrid.addColumn(invoice -> {
         final var dto = invoice.toDto();
         return dto.getCompany().isBlank() ? dto.getName() : dto.getCompany();
      }).setHeader("Kupujący");
      invoicesGrid.addComponentColumn(this::getDownloadButton).setHeader("Pobierz PDF");
      invoicesGrid.addComponentColumn(this::getSendButton).setHeader("Wyślij PDF");
      invoicesGrid.setSelectionMode(Grid.SelectionMode.MULTI);
      
      invoicesGrid.getColumns().forEach(col -> col.setAutoWidth(true));
      add(invoicesGrid);
   }
   
   private void updateList() {
      final var items = invoiceFacade.findAll();
      items.sort(Comparator.comparing(DeliveryInvoice::getIssueDate).reversed());
      invoicesGrid.setItems(items);
   }
   
   private Component getButtons() {
      final var downloadSelectedBtn = new Button("Pobierz zaznaczone");
      downloadSelectedBtn.setIcon(new Icon(VaadinIcon.FILE_ZIP));
      downloadSelectedBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
      downloadSelectedBtn.addClickListener(event -> downloadPdfs(invoicesGrid.getSelectedItems()));
      
      final var downloadLastMonthBtn = new Button("Pobierz z ubiegłego miesiąca");
      downloadLastMonthBtn.addClickListener(event -> downloadPdfs(new HashSet<>(invoiceFacade.getFromLastMonth())));
      
      final var sendSelectedBtn = new Button("Wyślij zaznaczone");
      sendSelectedBtn.setIcon(new Icon(VaadinIcon.PACKAGE));
      sendSelectedBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
      sendSelectedBtn.addClickListener(event -> sendPdfs(invoicesGrid.getSelectedItems()));
      
      return new HorizontalLayout(downloadSelectedBtn, downloadLastMonthBtn, sendSelectedBtn);
   }
   
   private void sendPdfs(final Set<DeliveryInvoice> invoices) {
      invoices.forEach(inv -> {
         final var email = inv.getOrder().getEmail();
         final var pdf = generatePdf(inv);
         final var dto = inv.toDto();
         emailService.sendInvoiceByEmail(dto, email, pdf);
      });
   }
   
   private Button getDownloadButton(final DeliveryInvoice invoice) {
      final var downloadButton = new Button(new Icon(VaadinIcon.PRINT));
      downloadButton.addClickListener(e -> {
         final var pdf = generatePdf(invoice);
         initializeFileDownload(pdf);
      });
      return downloadButton;
   }
   
   private Button getSendButton(final DeliveryInvoice invoice) {
      final var downloadButton = new Button(new Icon(VaadinIcon.ENVELOPE));
      downloadButton.addClickListener(e -> {
         final var pdf = generatePdf(invoice);
         emailService.sendInvoiceByEmail(invoice.toDto(), invoice.getOrder().getEmail(), pdf);
      });
      return downloadButton;
   }
   
   private void downloadPdfs(final Set<DeliveryInvoice> invoiceSet) {
      List<File> pdfs = new ArrayList<>();
      invoiceSet.forEach(invoice -> pdfs.add(generatePdf(invoice)));
      final var zip = ZipFileCreator.zip(pdfs);
      initializeFileDownload(zip);
   }
   
   private File generatePdf(final DeliveryInvoice invoice) {
      return invoiceFacade.createPdfInvoice(invoice.toDto());
   }
   
   private void initializeFileDownload(final File file) {
      final var anchor = new Anchor(getResource(file), "");
      anchor.getElement().setAttribute("download", true);
      anchor.setId(file.getName());
      add(anchor);
      click(UI.getCurrent(), file.getName());
      anchor.getStyle().set("display", "none");
   }
   
   private StreamResource getResource(final File file) {
      return new StreamResource(file.getName(), () -> getFileInputStream(file));
   }
   
   private FileInputStream getFileInputStream(final File file) {
      try {
         return new FileInputStream(file);
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }
      return null;
   }
   
   private static void click(final UI ui, final String id) {
      final var page = ui.getPage();
      page.executeJs("document.getElementById('" + id + "').click();");
   }
}
