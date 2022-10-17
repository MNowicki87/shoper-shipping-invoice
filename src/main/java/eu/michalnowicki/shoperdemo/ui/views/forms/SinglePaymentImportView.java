package eu.michalnowicki.shoperdemo.ui.views.forms;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.shared.Registration;
import eu.michalnowicki.shoperdemo.backend.domain.incoming_payment.PaymentDto;
import eu.michalnowicki.shoperdemo.backend.domain.incoming_payment.PaymentFacade;
import eu.michalnowicki.shoperdemo.backend.domain.incoming_payment.PaymentType;
import eu.michalnowicki.shoperdemo.backend.domain.order.Order;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.util.List;
import java.util.Set;

public class SinglePaymentImportView extends FormLayout {
   
   final Div uploadDiv = new Div();
   private final transient PaymentFacade paymentFacade;
   
   
   TextField sender = new TextField("Nadawca");
   TextField title = new TextField("Tytuł");
   NumberField amount = new NumberField("Kwota");
   DatePicker date = new DatePicker("Data");
   ComboBox<PaymentType> paymentTypeField = new ComboBox<>("Rodzaj płatności");
   
   Button saveBtn = new Button("Zapisz");
   Button deleteBtn = new Button("Usuń");
   Button closeBtn = new Button("Anuluj");
   MultiselectComboBox<Order> ordersBox;
   Binder<PaymentDto> binder = new BeanValidationBinder<>(PaymentDto.class);
   private transient PaymentDto payment;
   
   public SinglePaymentImportView(final PaymentFacade paymentFacade,
                                  final List<Order> orderList) {
      this.paymentFacade = paymentFacade;
      
      addClassName("single-order");
      ordersBox = configureOrderSelectBox(orderList);
      ordersBox.setRequired(true);
      paymentTypeField.setItems(PaymentType.values());
      binder.bindInstanceFields(this);
      binder.bind(paymentTypeField, "paymentType");
      binder.bind(ordersBox, "orders");
      
      add(date, sender, title, amount, paymentTypeField, ordersBox, createButtonsLayout());
   }
   
   private MultiselectComboBox<Order> configureOrderSelectBox(List<Order> orders) {
      MultiselectComboBox<Order> comboBox = new MultiselectComboBox<>();
      comboBox.setLabel("Zamówienia");
      
      MultiselectComboBox.ItemFilter<Order> filter = (order, filterString) ->
            order.getId().toString().contains(filterString)
                  || order.getBillingAddress().getLastName().toLowerCase()
                  .contains(filterString.toLowerCase());
      
      comboBox.setItems(filter, orders);
      comboBox.setClearButtonVisible(true);
      comboBox.setItemLabelGenerator(o -> String.valueOf(o.getId()));
      comboBox.setRenderer(getOrderListItemRenderer());
      return comboBox;
   }
   
   private Component createButtonsLayout() {
      saveBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
      deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
      closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
      
      saveBtn.addClickShortcut(Key.ENTER, KeyModifier.SHIFT);
      deleteBtn.addClickShortcut(Key.BACKSPACE, KeyModifier.SHIFT);
      closeBtn.addClickShortcut(Key.ESCAPE);
      
      saveBtn.addClickListener(click -> validateAndSave());
      deleteBtn.addClickListener(click -> fireEvent(new DeleteEvent(this, payment)));
      closeBtn.addClickListener(click -> fireEvent(new CloseEvent(this)));
      binder.addStatusChangeListener(e -> saveBtn.setEnabled(binder.isValid()));
      
      final HorizontalLayout layout = new HorizontalLayout(saveBtn, deleteBtn, closeBtn);
      layout.addClassName("buttons");
      return layout;
   }
   
   private TemplateRenderer<Order> getOrderListItemRenderer() {
      return TemplateRenderer.<Order>of(
            "<div>" +
                  "[[item.id]]<br>" +
                  "<small>" +
                  "[[item.firstName]] [[item.lastName]] | [[item.company]] <br>" +
                  "Dostawa: [[item.shipping]] zł | Razem: [[item.total]] zł" +
                  "</small>" +
                  "</div>")
            .withProperty("id", Order::getId)
            .withProperty("firstName", o -> o.getBillingAddress().getFirstName())
            .withProperty("lastName", o -> o.getBillingAddress().getLastName())
            .withProperty("company", o -> o.getBillingAddress().getCompany())
            .withProperty("total", Order::getTotal)
            .withProperty("shipping", Order::getTotal);
   }
   
   private void validateAndSave() {
      try {
         binder.writeBean(payment);
         fireEvent(new SaveEvent(this, payment));
      } catch (ValidationException e) {
         e.printStackTrace();
      }
   }
   
   public PaymentDto getPayment() {
      return payment;
   }
   
   public void setPayment(final PaymentDto payment) {
      this.payment = payment;
      binder.readBean(this.payment);
      uploadDiv.removeAll();
      
      if (payment != null && payment.getSender().contains("BLUE MEDIA")) {
         paymentTypeField.setValue(PaymentType.EPAY);
         add(configureUpload());
      } else {
         paymentTypeField.setValue(PaymentType.TRANSFER);
      }
      
   }
   
   private Component configureUpload() {
      var buffer = new MemoryBuffer();
      var upload = new Upload(buffer);
      
      upload.setDropLabel(new Label("Załaduj plik .csv maks. 50kB"));
      upload.setMaxFiles(1);
      upload.setAcceptedFileTypes("text/csv");
      upload.setMaxFileSize(50_000);
      
      uploadDiv.add(upload);
      add(uploadDiv);
      upload.addFinishedListener(e -> {
         final Set<Order> orders = paymentFacade.parseBlueMedia(buffer, payment.getTitle());
         ordersBox.setValue(orders);
         uploadDiv.remove(upload);
      });
      return uploadDiv;
   }
   
   @Override
   public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                 ComponentEventListener<T> listener) {
      return getEventBus().addListener(eventType, listener);
   }
   
   public abstract static class SinglePaymentViewEvent extends ComponentEvent<SinglePaymentImportView> {
      private final transient PaymentDto payment;
      
      protected SinglePaymentViewEvent(SinglePaymentImportView source, PaymentDto payment) {
         super(source, false);
         this.payment = payment;
      }
      
      public PaymentDto getPayment() {
         return payment;
      }
   }
   
   public static class CloseEvent extends SinglePaymentImportView.SinglePaymentViewEvent {
      CloseEvent(SinglePaymentImportView source) {
         super(source, null);
      }
   }
   
   public static class DeleteEvent extends SinglePaymentImportView.SinglePaymentViewEvent {
      DeleteEvent(SinglePaymentImportView source, PaymentDto paymentDto) {
         super(source, paymentDto);
      }
   }
   
   public static class SaveEvent extends SinglePaymentImportView.SinglePaymentViewEvent {
      SaveEvent(SinglePaymentImportView source, PaymentDto paymentDto) {
         super(source, paymentDto);
      }
   }
}
