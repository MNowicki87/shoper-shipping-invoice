package eu.michalnowicki.shoperdemo.ui.views.lists;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import eu.michalnowicki.shoperdemo.backend.domain.incoming_payment.PaymentDto;
import eu.michalnowicki.shoperdemo.backend.domain.incoming_payment.PaymentFacade;
import eu.michalnowicki.shoperdemo.backend.domain.order.Order;
import eu.michalnowicki.shoperdemo.backend.domain.order.OrderFacade;
import eu.michalnowicki.shoperdemo.ui.MainLayout;
import eu.michalnowicki.shoperdemo.ui.views.forms.SinglePaymentImportView;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static eu.michalnowicki.shoperdemo.backend.utils.Constants.DATE_FORMAT;
import static eu.michalnowicki.shoperdemo.backend.utils.Constants.PLN_NUMBER_FORMAT;

@Route(value = "payment-import", layout = MainLayout.class)
@PageTitle("Import płatności")
public class ImportedPaymentsListView extends VerticalLayout {
   
   private final SinglePaymentImportView paymentView;
   private final transient PaymentFacade paymentFacade;
   Grid<PaymentDto> paymentDetailsGrid = new Grid<>(PaymentDto.class);
   transient List<PaymentDto> detailsList;
   
   public ImportedPaymentsListView(final OrderFacade orderFacade,
                                   final PaymentFacade paymentFacade) {
      
      this.paymentFacade = paymentFacade;
      
      addClassName("list-view");
      setSizeFull();
      configureGrid();
      
      paymentView = new SinglePaymentImportView(this.paymentFacade, orderFacade.findPotentialOrdersForPayment());
      paymentView.addListener(SinglePaymentImportView.CloseEvent.class, e -> closeSinglePaymentView());
      paymentView.addListener(SinglePaymentImportView.SaveEvent.class, e -> savePayment(paymentView.getPayment()));
      paymentView.addListener(SinglePaymentImportView.DeleteEvent.class, e -> deletePayment(paymentView.getPayment()));
      
      final Div content = new Div(paymentDetailsGrid, paymentView);
      content.addClassName("orders-content");
      content.setSizeFull();
      
      add(createLastPaymentDiv(), content, configureUpload());
      closeSinglePaymentView();
      
   }
   
   private Div createLastPaymentDiv() {
      Paragraph latestPaymentData = new Paragraph();
      final Div lastPaymentDiv = new Div(latestPaymentData);
      
      final Optional<PaymentDto> latestPayment = paymentFacade.getLast();
      latestPayment.ifPresent(payment -> {
               latestPaymentData.add(new Span("Ostatnia zarejestrowana płatność: "));
               latestPaymentData.add(new Span(MessageFormat.format("Data: {0}; Zamówienia: {1}; Suma: {2}",
                     payment.getDate(),
                     Arrays.toString(payment.getOrders().stream().mapToLong(Order::getId).toArray()),
                     PLN_NUMBER_FORMAT.format(payment.getAmount()))));
            }
      );
      return lastPaymentDiv;
   }
   
   private void configureGrid() {
      paymentDetailsGrid.addClassName("order-grid");
      paymentDetailsGrid.setSizeFull();
      paymentDetailsGrid.removeAllColumns();
      paymentDetailsGrid.addColumn(new LocalDateRenderer<>(PaymentDto::getDate, DATE_FORMAT))
            .setHeader("Data płatności");
      paymentDetailsGrid.addColumn(PaymentDto::getTitle).setHeader("Tytuł");
      paymentDetailsGrid.addColumn(PaymentDto::getSender).setHeader("Nadawca");
      paymentDetailsGrid.addColumn(new NumberRenderer<>(PaymentDto::getAmount, PLN_NUMBER_FORMAT))
            .setHeader("Kwota");
      
      paymentDetailsGrid.getColumns().forEach(col -> col.setAutoWidth(true));
      
      paymentDetailsGrid.asSingleSelect().addValueChangeListener(e -> showPayment(e.getValue()));
      
   }
   
   private Component configureUpload() {
      final Div div = new Div();
      var buffer = new MemoryBuffer();
      var upload = new Upload(buffer);
      
      upload.setDropLabel(new Label("Załaduj plik .csv maks. 50kB"));
      upload.setMaxFiles(1);
      upload.setAcceptedFileTypes("text/csv");
      upload.setMaxFileSize(50_000);
      
      div.add(upload);
      add(div);
      upload.addFinishedListener(e -> {
         this.detailsList = paymentFacade.parseMbank(buffer);
         detailsList = removeRegistered(detailsList);
         paymentDetailsGrid.setItems(detailsList);
         paymentDetailsGrid.getDataProvider().refreshAll();
      });
      return div;
   }
   
   private List<PaymentDto> removeRegistered(final List<PaymentDto> detailsList) {
      return detailsList.stream()
            .filter(Predicate.not(paymentFacade::isPaymentRegistered))
            .collect(Collectors.toList());
   }
   
   private void showPayment(final PaymentDto payment) {
      if (payment == null) {
         closeSinglePaymentView();
      } else {
         paymentView.setPayment(payment);
         paymentView.setVisible(true);
         addClassName("viewing");
      }
   }
   
   private void closeSinglePaymentView() {
      paymentView.setPayment(null);
      paymentView.setVisible(false);
      paymentDetailsGrid.select(null);
      removeClassName("viewing");
   }
   
   private void savePayment(final PaymentDto item) {
      paymentFacade.registerPayment(item);
      deletePayment(item);
   }
   
   private void deletePayment(final PaymentDto payment) {
      closeSinglePaymentView();
      detailsList.remove(payment);
      paymentDetailsGrid.getDataProvider().refreshAll();
   }
   
   
}
