package eu.michalnowicki.shoperdemo.ui.views.lists;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import eu.michalnowicki.shoperdemo.backend.domain.order.Order;
import eu.michalnowicki.shoperdemo.backend.domain.order.OrderFacade;
import eu.michalnowicki.shoperdemo.backend.shoper.ShoperFacade;
import eu.michalnowicki.shoperdemo.ui.MainLayout;

import static eu.michalnowicki.shoperdemo.backend.utils.Constants.PLN_NUMBER_FORMAT;

@Route(value = "orders", layout = MainLayout.class)
@PageTitle("Zamówienia")
public class OrdersListView extends VerticalLayout {
   
   private final Grid<Order> orderGrid = new Grid<>(Order.class);
   private final TextField filterText = new TextField();
   private final Button syncNewBtn = new Button("Pobierz nowe");
   private final Button syncAllBtn = new Button("Synchronizuj");
   
   private final transient OrderFacade orderFacade;
   private final transient ShoperFacade shoper;
   
   public OrdersListView(final OrderFacade orderFacade, final ShoperFacade shoper) {
      this.orderFacade = orderFacade;
      this.shoper = shoper;
      
      addClassName("list-view");
      setSizeFull();
      configureGrid();
      
      filterText.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);
      
      final var content = new Div(orderGrid);
      content.addClassName("orders-content");
      content.setSizeFull();
      
      add(configureFilterBar(), content);
      updateList();
   }
   
   private Component configureFilterBar() {
      var bar = new HorizontalLayout();
      bar.setWidthFull();
      filterText.setWidthFull();
      filterText.setPlaceholder("Filtruj...");
      filterText.setClearButtonVisible(true);
      filterText.setValueChangeMode(ValueChangeMode.LAZY);
      filterText.addValueChangeListener(e -> updateList());
      syncNewBtn.addClickListener(e -> {
         shoper.getNewOrders();
         updateList();
      });
      syncAllBtn.addClickListener(e -> {
         shoper.getAllOrders();
         updateList();
      });
      bar.add(filterText, syncNewBtn, syncAllBtn);
      return bar;
   }
   
   private void updateList() {
      orderGrid.setItems(orderFacade.findAll(filterText.getValue()));
      
   }
   
   private void configureGrid() {
      orderGrid.addClassName("order-grid");
      orderGrid.setSizeFull();
      orderGrid.removeAllColumns();
      orderGrid.setSelectionMode(Grid.SelectionMode.NONE);
      
      orderGrid.addComponentColumn(order -> {
         if (order.getPayment() != null) {
            return new Icon(VaadinIcon.CASH);
         } else {
            return new Span();
         }
      });
      
      
      orderGrid.addColumn("id").setHeader("ID");
      orderGrid.addColumn(order -> order.getBillingAddress().getLastName() + ", " + order.getBillingAddress().getFirstName()).setHeader("Nazwisko").setSortable(true);
      orderGrid.addColumn("email");
      orderGrid.addColumn(new LocalDateTimeRenderer<>(
            Order::getCreatedAt, "dd/MM/yyyy"))
            .setHeader("Data zamówienia")
            .setSortable(true);
      
      orderGrid.addColumn(order -> order.getStatus().getName())
            .setHeader("Status")
            .setSortable(true);
      orderGrid.addColumn(new NumberRenderer<>(Order::getShippingCost, PLN_NUMBER_FORMAT))
            .setHeader("Koszt dostawy");
      orderGrid.addColumn(new NumberRenderer<>(Order::getTotal, PLN_NUMBER_FORMAT)).setHeader("Zapłacono");
      
      orderGrid.getColumns().forEach(col -> col.setAutoWidth(true));
      
   }
   
}