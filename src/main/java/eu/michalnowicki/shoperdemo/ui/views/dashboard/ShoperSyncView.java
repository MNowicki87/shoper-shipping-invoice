package eu.michalnowicki.shoperdemo.ui.views.dashboard;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import eu.michalnowicki.shoperdemo.backend.shoper.ShoperFacade;
import eu.michalnowicki.shoperdemo.ui.MainLayout;

@PageTitle("Synchronizacja")
@Route(value = "sync", layout = MainLayout.class)
public class ShoperSyncView extends VerticalLayout {
   
   public ShoperSyncView(final ShoperFacade shoper) {
      
      addClassName("dashboard-view");
      setDefaultHorizontalComponentAlignment(Alignment.CENTER);
      
      Button renewTokenBtn = new Button("Odnów token");
      renewTokenBtn.addClickListener(click -> shoper.updateBearerAuthToken());
      
      Button getStatusesBtn = new Button("Pobierz statusy");
      getStatusesBtn.addClickListener(click -> shoper.syncStatusRepo());
      
      Button getOrdersBtn = new Button("Pobierz wszystkie zamówienia");
      getOrdersBtn.addClickListener(click -> shoper.getAllOrders());
      
      Button getNewOrdersBtn = new Button("Pobierz nowe zamówienia");
      getNewOrdersBtn.addClickListener(click -> shoper.getNewOrders());
      
      final Button syncAllBtn = new Button("Synchronizuj wszystko");
      syncAllBtn.addClickListener((click -> {
         shoper.updateBearerAuthToken();
         shoper.syncStatusRepo();
         shoper.getNewOrders();
      }));
      
      add(renewTokenBtn, getStatusesBtn, getOrdersBtn, getNewOrdersBtn, syncAllBtn);
   }
   
}
