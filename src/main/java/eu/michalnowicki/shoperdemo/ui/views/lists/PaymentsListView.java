package eu.michalnowicki.shoperdemo.ui.views.lists;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import eu.michalnowicki.shoperdemo.backend.domain.delivery_invoice.DeliveryInvoice;
import eu.michalnowicki.shoperdemo.backend.domain.delivery_invoice.DeliveryInvoiceFacade;
import eu.michalnowicki.shoperdemo.backend.domain.incoming_payment.Payment;
import eu.michalnowicki.shoperdemo.backend.domain.incoming_payment.PaymentFacade;
import eu.michalnowicki.shoperdemo.backend.domain.order.Order;
import eu.michalnowicki.shoperdemo.backend.domain.order.OrderFacade;
import eu.michalnowicki.shoperdemo.backend.shoper.ShoperFacade;
import eu.michalnowicki.shoperdemo.backend.utils.ZipFileCreator;
import eu.michalnowicki.shoperdemo.ui.MainLayout;
import eu.michalnowicki.shoperdemo.ui.views.forms.SinglePaymentView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static eu.michalnowicki.shoperdemo.backend.utils.Constants.DATE_FORMAT;
import static eu.michalnowicki.shoperdemo.backend.utils.Constants.PLN_NUMBER_FORMAT;

@PageTitle("Płatności")
@Route(value = "payments", layout = MainLayout.class)
public class PaymentsListView extends VerticalLayout {
   
   private final transient PaymentFacade paymentFacade;
   private final SinglePaymentView paymentView;
   private final transient DeliveryInvoiceFacade invoiceFacade;
   private final transient ShoperFacade communicationManager;
   
   private final TextField filterText = new TextField();
   private final Grid<Payment> paymentGrid = new Grid<>(Payment.class);
   
   public PaymentsListView(final PaymentFacade paymentFacade,
                           final OrderFacade orderFacade,
                           final DeliveryInvoiceFacade invoiceFacade,
                           final ShoperFacade shoper) {
      this.paymentFacade = paymentFacade;
      this.invoiceFacade = invoiceFacade;
      this.communicationManager = shoper;
      
      addClassName("list-view");
      setSizeFull();
      configureGrid();
      configureFilter();
      
      paymentView = new SinglePaymentView(orderFacade.findPotentialOrdersForPayment());
      paymentView.addListener(SinglePaymentView.CloseEvent.class, e -> closeSinglePaymentView());
      paymentView.addListener(SinglePaymentView.SaveEvent.class, e -> updatePayment(paymentView.getPayment()));
      paymentView.addListener(SinglePaymentView.DeleteEvent.class, e -> deletePayment(paymentView.getPayment()));
      
      filterText.addFocusShortcut(Key.KEY_F);
      
      final Div content = new Div(paymentGrid, paymentView);
      content.addClassName("orders-content");
      content.setSizeFull();
      
      add(filterText, content, getButtons());
      updateList();
      closeSinglePaymentView();
   }
   
   private void configureGrid() {
      paymentGrid.addClassName("order-grid");
      paymentGrid.setSizeFull();
      paymentGrid.removeAllColumns();
      paymentGrid.setSelectionMode(Grid.SelectionMode.MULTI);
      paymentGrid.addColumn(new LocalDateRenderer<>(Payment::getDate, DATE_FORMAT))
            .setHeader("Data płatności");
      paymentGrid.addColumn(Payment::getTitle).setHeader("Tytuł");
      paymentGrid.addColumn(Payment::getSender).setHeader("Nadawca");
      paymentGrid.addColumn(new NumberRenderer<>(Payment::getAmount, PLN_NUMBER_FORMAT))
            .setHeader("Kwota");
      paymentGrid.addComponentColumn(payment -> {
         if (payment.areAllInvoicesIssued()) {
            return getDownloadButton(payment);
         } else {
            final var btn = new Button("Wystaw");
            btn.addClickListener(e -> issueInvoicesForPayments(Set.of(payment)));
            return btn;
         }
      }).setHeader("Faktury wystawione");
      paymentGrid.addColumn(payment ->
            payment.getOrders().stream()
                  .map(Order::getId)
                  .collect(Collectors.toList())
      ).setHeader("Zamówienia");
      
      paymentGrid.asMultiSelect().addValueChangeListener(e ->
            e.getValue().stream().filter(Payment::areAllInvoicesIssued).forEach(paymentGrid::deselect));
      paymentGrid.asMultiSelect().addValueChangeListener(
            e -> e.getValue().stream()
                  .filter(payment ->
                        payment.getOrders().stream()
                              .anyMatch(order ->
                                    order.getShippingCost() == 0)
                  ).forEach(payment -> {
                     Notification.show(
                           "Nie można wystawić faktur dla zamówień bez kosztów dostawy",
                           1000,
                           Notification.Position.MIDDLE);
                     paymentGrid.deselect(payment);
                  }));
   }
   
   private void configureFilter() {
      filterText.setPlaceholder("Filtruj...");
      filterText.setClearButtonVisible(true);
      filterText.setWidthFull();
      filterText.setValueChangeMode(ValueChangeMode.LAZY);
      filterText.addValueChangeListener(e -> updateList());
   }
   
   private void closeSinglePaymentView() {
      paymentView.setPayment(null);
      paymentView.setVisible(false);
      paymentGrid.select(null);
      removeClassName("viewing");
   }
   
   private void updatePayment(final Payment item) {
      paymentFacade.updatePayment(item);
   }
   
   private void deletePayment(final Payment payment) {
      closeSinglePaymentView();
      paymentFacade.delete(payment);
      paymentGrid.getDataProvider().refreshAll();
   }
   
   private Button getDownloadButton(final Payment payment) {
      final var invoices = payment.getDeliveryInvoices();
      final var downloadButton = new Button(new Icon(VaadinIcon.PRINT));
      downloadButton.addClickListener(e -> {
         if (invoices.size() == 1) {
            initializeFileDownload(invoiceFacade.createPdfInvoice(invoices.iterator().next().toDto()));
         } else {
            initializeFileDownload(getZipped(invoices));
         }
      });
      return downloadButton;
   }
   
   private File getZipped(final Set<DeliveryInvoice> invoiceSet) {
      List<File> pdfs = new ArrayList<>();
      invoiceSet.forEach(invoice -> pdfs.add(invoiceFacade.createPdfInvoice(invoice.toDto())));
      return ZipFileCreator.zip(pdfs);
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
   
   private static void click(final UI ui, final String id) {
      final var page = ui.getPage();
      page.executeJs("document.getElementById('" + id + "').click();");
   }
   
   private FileInputStream getFileInputStream(final File file) {
      try {
         return new FileInputStream(file);
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }
      return null;
   }
   
   private Component getButtons() {
      final Div div = new Div();
      
      final var issueInvoicesBtn = new Button("Wystaw faktury");
      issueInvoicesBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
      issueInvoicesBtn.addClickListener(event -> issueInvoicesForPayments(paymentGrid.getSelectedItems()));
      
      div.add(issueInvoicesBtn);
      return div;
   }
   
   
   private void updateList() {
      final List<Payment> items = paymentFacade.findAll(filterText.getValue());
      items.sort(Comparator.comparing(Payment::getDate).reversed());
      paymentGrid.setItems(items);
   }
   
   private void issueInvoicesForPayments(final Set<Payment> paymentSet) {
      paymentSet.stream()
            .filter(payment -> !payment.getOrders().isEmpty())
            .filter(payment -> !payment.areAllInvoicesIssued())
            .sorted(Comparator.comparing(Payment::getDate))
            .forEachOrdered(payment -> payment.getOrders().forEach(order -> {
                     communicationManager.getProductsOfOrder(order.getId());
                     invoiceFacade.createInvoiceForOrderId(order.getId());
                     Notification.show(
                           "Wystawiono faktury dot. płatności " + payment.getTitle(),
                           1000,
                           Notification.Position.BOTTOM_END
                     );
                  }
            ));
      updateList();
   }
   
}
