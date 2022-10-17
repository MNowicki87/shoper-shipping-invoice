package eu.michalnowicki.shoperdemo.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import eu.michalnowicki.shoperdemo.ui.views.dashboard.DashboardView;
import eu.michalnowicki.shoperdemo.ui.views.dashboard.SettingsView;
import eu.michalnowicki.shoperdemo.ui.views.dashboard.ShoperSyncView;
import eu.michalnowicki.shoperdemo.ui.views.lists.ImportedPaymentsListView;
import eu.michalnowicki.shoperdemo.ui.views.lists.InvoiceListView;
import eu.michalnowicki.shoperdemo.ui.views.lists.OrdersListView;
import eu.michalnowicki.shoperdemo.ui.views.lists.PaymentsListView;

import static com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.CENTER;

@CssImport("./styles/styles.css")
@Viewport("width=device-width, minimum-scale=1, initial-scale=1, user-scalable=yes, viewport-fit=cover")
@Theme(value = Lumo.class, variant = Lumo.DARK)
public class MainLayout extends AppLayout {
   
   
   public MainLayout() {
      createHeader();
      crateDrawer();
   }
   
   private void createHeader() {
      H1 logo = new H1("Panel Shoper");
      logo.addClassName("logo");
      
      final Anchor logout = new Anchor("/logout", "Wyloguj");
      logout.setMinWidth("5em");
      
      final HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo);
      header.addClassName("header");
      header.setWidth("100%");
      header.expand(logo);
      header.setDefaultVerticalComponentAlignment(CENTER);
      
      addToNavbar(header, logout);
   }
   
   private void crateDrawer() {
      final RouterLink ordersLink = new RouterLink("Zamówienia", OrdersListView.class);
      final RouterLink panelLink = new RouterLink("Panel", DashboardView.class);
      final RouterLink syncLink = new RouterLink("Synchronizacja", ShoperSyncView.class);
      final RouterLink paymentsLink = new RouterLink("Płatności", PaymentsListView.class);
      final RouterLink importPaymentsLink = new RouterLink("Importuj płatności", ImportedPaymentsListView.class);
      final RouterLink invoicesLink = new RouterLink("Faktury", InvoiceListView.class);
      final RouterLink setNumLink = new RouterLink("Ustaw. Numeracji", SettingsView.class);
      
      panelLink.setHighlightCondition(HighlightConditions.sameLocation());
      
      VerticalLayout mainLinks = new VerticalLayout(panelLink, ordersLink, syncLink, paymentsLink, importPaymentsLink, invoicesLink);
      VerticalLayout settings = new VerticalLayout(setNumLink);
      
      addToDrawer(mainLinks, new Div(), settings);
      setDrawerOpened(false);
   }
   
}
