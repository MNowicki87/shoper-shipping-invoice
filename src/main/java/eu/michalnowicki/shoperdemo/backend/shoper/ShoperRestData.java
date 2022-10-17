package eu.michalnowicki.shoperdemo.backend.shoper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@RequiredArgsConstructor
@Component
final class ShoperRestData {
   
   @Value("${shoper.client_id}")
   private String clientId;
   
   @Value("${shoper.client_secret}")
   private String clientSecret;
   
   @Value("${shoper.rest-url}")
   private String restApiBaseUrl;
   
   @Setter
   private String token;
   
   private LocalDateTime lastOrderUpdateAt = LocalDate.EPOCH.atTime(LocalTime.MIN);
   
   void setNewDateOfLastOrdersUpdate() {
      this.lastOrderUpdateAt = LocalDateTime.now().minusHours(1);
   }
   
   String getToken() {
      return "Bearer " + this.token;
   }
   
}
