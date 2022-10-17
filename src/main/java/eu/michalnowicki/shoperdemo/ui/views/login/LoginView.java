package eu.michalnowicki.shoperdemo.ui.views.login;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Collections;

@PageTitle("Logowanie")
@Route("login")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {
   LoginForm login = new LoginForm();
   
   public LoginView() {
      addClassName("login-view");
      setSizeFull();
      
      setJustifyContentMode(JustifyContentMode.CENTER);
      setAlignItems(Alignment.CENTER);
      
      UI.getCurrent().getPage().executeJs("document.getElementById(\"vaadinLoginUsername\").focus();");
      login.setForgotPasswordButtonVisible(false);
      login.setAction("login");
      add(login);
   }
   
   @Override
   public void beforeEnter(final BeforeEnterEvent event) {
      if (isError(event)) {
         login.setError(true);
      }
   }
   
   private boolean isError(final BeforeEnterEvent event) {
      return !event.getLocation().getQueryParameters().getParameters().getOrDefault("error", Collections.emptyList()).isEmpty();
   }
}
