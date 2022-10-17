package eu.michalnowicki.shoperdemo.ui.views.dashboard;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import eu.michalnowicki.shoperdemo.backend.domain.delivery_invoice.DeliveryInvoiceFacade;
import eu.michalnowicki.shoperdemo.ui.MainLayout;

import java.time.LocalDate;

@PageTitle("Ustawienia wstępne")
@Route(value = "settings", layout = MainLayout.class)
public class SettingsView extends VerticalLayout {
   int currentYear = LocalDate.now().getYear();
   
   public SettingsView(final DeliveryInvoiceFacade invoiceService) {
      
      FormLayout form = new FormLayout();
      NumberField yearField = new NumberField("Rok", String.valueOf(currentYear));
      NumberField numberField = new NumberField("Numer",
            String.valueOf(invoiceService.getNextInvoiceNumberInYear(currentYear)));
      
      Button saveButton = new Button("Zapisz");
      
      saveButton.addClickListener(e ->
            invoiceService.setNewInitNumber(yearField.getValue().intValue(),
                  numberField.getValue().intValue()));
      
      form.add(yearField, numberField, saveButton);
      
      add(form);
      
   }
   
}
