package eu.michalnowicki.shoperdemo.ui.views.forms;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.shared.Registration;
import eu.michalnowicki.shoperdemo.backend.domain.incoming_payment.Payment;
import eu.michalnowicki.shoperdemo.backend.domain.incoming_payment.PaymentType;
import eu.michalnowicki.shoperdemo.backend.domain.order.Order;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.util.List;

public class SinglePaymentView extends FormLayout {
   private transient Payment payment;
   
   TextField sender = new TextField("Nadawca");
   TextField title = new TextField("Tytuł");
   NumberField amount = new NumberField("Kwota");
   DatePicker date = new DatePicker("Data");
   ComboBox<PaymentType> paymentTypeField = new ComboBox<>("Rodzaj płatności");
   
   Button saveBtn = new Button("Zapisz");
   Button deleteBtn = new Button("Usuń");
   Button closeBtn = new Button("Anuluj");
   
   Binder<Payment> binder = new BeanValidationBinder<>(Payment.class);
   
   public SinglePaymentView(final List<Order> orderList) {
      addClassName("single-order");
      
      var orders = configureOrderSelectBox(orderList);
      paymentTypeField.setItems(PaymentType.values());
      binder.bindInstanceFields(this);
      binder.bind(paymentTypeField, "paymentType");
      binder.bind(orders, "orders");
      
      add(date, sender, title, amount, paymentTypeField, orders, createButtonsLayout());
   }
   
   public void setPayment(final Payment payment) {
      this.payment = payment;
      binder.readBean(payment);
   }
   
   public Payment getPayment() {
      return payment;
   }
   
   private Component createButtonsLayout() {
      saveBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
      deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
      closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
      
      saveBtn.addClickShortcut(Key.ENTER, KeyModifier.SHIFT);
      deleteBtn.addClickShortcut(Key.BACKSPACE, KeyModifier.SHIFT);
      closeBtn.addClickShortcut(Key.ESCAPE);
      
      saveBtn.addClickListener(click -> validateAndSave());
      deleteBtn.addClickListener(click -> fireEvent(new SinglePaymentView.DeleteEvent(this, payment)));
      closeBtn.addClickListener(click -> fireEvent(new SinglePaymentView.CloseEvent(this)));
      binder.addStatusChangeListener(e -> saveBtn.setEnabled(binder.isValid()));
      
      final HorizontalLayout layout = new HorizontalLayout(saveBtn, deleteBtn, closeBtn);
      layout.addClassName("buttons");
      return layout;
   }
   
   private void validateAndSave() {
      try {
         binder.writeBean(payment);
         fireEvent(new SinglePaymentView.SaveEvent(this, payment));
      } catch (ValidationException e) {
         e.printStackTrace();
      }
   }
   
   public abstract static class SinglePaymentViewEvent extends ComponentEvent<SinglePaymentView> {
      private final transient Payment payment;
      
      protected SinglePaymentViewEvent(SinglePaymentView source, Payment payment) {
         super(source, false);
         this.payment = payment;
      }
      
      public Payment getPayment() {
         return payment;
      }
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
   
   public static class CloseEvent extends SinglePaymentView.SinglePaymentViewEvent {
      CloseEvent(SinglePaymentView source) {
         super(source, null);
      }
   }
   
   public static class DeleteEvent extends SinglePaymentView.SinglePaymentViewEvent {
      DeleteEvent(SinglePaymentView source, Payment payment) {
         super(source, payment);
      }
   }
   
   public static class SaveEvent extends SinglePaymentView.SinglePaymentViewEvent {
      SaveEvent(SinglePaymentView source, Payment payment) {
         super(source, payment);
      }
   }
   
   @Override
   public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                 ComponentEventListener<T> listener) {
      return getEventBus().addListener(eventType, listener);
      
      
   }
}
