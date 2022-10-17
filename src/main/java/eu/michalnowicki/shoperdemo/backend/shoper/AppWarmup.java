package eu.michalnowicki.shoperdemo.backend.shoper;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class AppWarmup implements ApplicationListener<ContextRefreshedEvent> {
   private final ShoperFacade shoperFacade;
   
   @Override
   public void onApplicationEvent(@NonNull final ContextRefreshedEvent event) {
      shoperFacade.updateBearerAuthToken();
      shoperFacade.syncStatusRepo();
      shoperFacade.getAllOrders();
   }
   
}