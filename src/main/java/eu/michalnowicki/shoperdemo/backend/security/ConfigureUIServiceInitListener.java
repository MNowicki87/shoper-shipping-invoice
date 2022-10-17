package eu.michalnowicki.shoperdemo.backend.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import eu.michalnowicki.shoperdemo.ui.views.login.LoginView;
import org.springframework.stereotype.Component;

@Component
public class ConfigureUIServiceInitListener implements VaadinServiceInitListener {
   
   /**
    * Generated Serial UID
    */
   private static final long serialVersionUID = -2245343958568149999L;
   
   @Override
   public void serviceInit(ServiceInitEvent event) {
      event.getSource().addUIInitListener(uiEvent -> {
         final UI ui = uiEvent.getUI();
         ui.addBeforeEnterListener(this::authenticateNavigation);
      });
   }
   
   private void authenticateNavigation(BeforeEnterEvent event) {
      if (!LoginView.class.equals(event.getNavigationTarget())
            && !SecurityUtils.isUserLoggedIn()) {
         event.rerouteTo(LoginView.class);
      }
   }
}