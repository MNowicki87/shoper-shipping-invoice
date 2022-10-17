package eu.michalnowicki.shoperdemo.ui.views.dashboard;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import eu.michalnowicki.shoperdemo.backend.domain.order.Order;
import eu.michalnowicki.shoperdemo.backend.domain.order.OrderFacade;
import eu.michalnowicki.shoperdemo.ui.MainLayout;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static eu.michalnowicki.shoperdemo.backend.utils.Constants.PLN_NUMBER_FORMAT;

@PageTitle("Panel")
@Route(value = "", layout = MainLayout.class)
public class DashboardView extends VerticalLayout {
   
   private final transient OrderFacade orderFacade;
   
   public DashboardView(final OrderFacade orderFacade) {
      this.orderFacade = orderFacade;
      
      addClassName("dashboard-view");
      setDefaultHorizontalComponentAlignment(Alignment.CENTER);
      
      add(getOrderStats());
   }
   
   private Component getOrderStats() {
      final Paragraph count = new Paragraph(orderFacade.count() + " zamówień");
      
      final List<Order> all = orderFacade.findAll();
      
      final double averageOrderValue = orderFacade.getAverageOrderValue();
      
      final List<Order> allCurrentYear = all.stream()
            .filter(o -> o.getCreatedAt().getYear() == LocalDateTime.now().getYear())
            .collect(Collectors.toList());
      
      final double totalCurrentYear = allCurrentYear.stream()
            .mapToDouble(Order::getTotal)
            .sum();
      
      final double shippingTotal = allCurrentYear.stream()
            .mapToDouble(Order::getShippingCost)
            .sum();
      
      final List<Order> allCurrentMonth = allCurrentYear.stream()
            .filter(o -> o.getCreatedAt().getMonth().equals(LocalDate.now().getMonth()))
            .collect(Collectors.toList());
      
      final double totalCurrentMonth = allCurrentMonth.stream()
            .mapToDouble(Order::getTotal)
            .sum();
      
      final double shippingCurrentMonth = allCurrentMonth.stream()
            .mapToDouble(Order::getShippingCost)
            .sum();
      
      final Paragraph avg = new Paragraph("Średnia wartość zamówienia: " + PLN_NUMBER_FORMAT.format(averageOrderValue));
      final Paragraph total = new Paragraph("Suma zamówień w tym roku (z transportem): " + PLN_NUMBER_FORMAT.format(totalCurrentYear));
      final Paragraph netTotal = new Paragraph("Suma zamówień tym roku (bez transportu): " + PLN_NUMBER_FORMAT.format(totalCurrentYear - shippingTotal));
      final Paragraph currentMonth = new Paragraph("Suma zamówień w tym miesiącu: " + PLN_NUMBER_FORMAT.format(totalCurrentMonth));
      final Paragraph currentMonthNet = new Paragraph("Wartość zamówień w tym miesiącu (bez transportu): " + PLN_NUMBER_FORMAT.format(totalCurrentMonth - shippingCurrentMonth));
      count.addClassName("stats");
      
      return new Div(count, avg, total, netTotal, currentMonth, currentMonthNet);
   }
   
   
}
