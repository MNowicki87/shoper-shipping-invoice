package eu.michalnowicki.shoperdemo.backend.shoper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.michalnowicki.shoperdemo.backend.shoper.dto.OrderWebhookDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@Log
@RequiredArgsConstructor
class OrdersWebhookReceiver {
   
   private final ShoperFacade shoper;
   
   @Value("${shoper.webhook-secret}")
   private String secret;
   
   @PostMapping
   public ResponseEntity<String> acceptOrderStatusWebhook(@RequestBody String json,
                                                          @RequestHeader MultiValueMap<String, String> headers) {
      
      final boolean isValidated = WebhookValidator.validate(json, headers, secret);
      if (!isValidated) {
         return new ResponseEntity<>("invalid_request", HttpStatus.UNAUTHORIZED);
      }
      
      OrderWebhookDto orderWebhookDto = null;
      
      ObjectMapper objectMapper = new ObjectMapper();
      try {
         orderWebhookDto = objectMapper.readValue(json, OrderWebhookDto.class);
      } catch (JsonProcessingException e) {
         log.warning(e.getMessage());
         e.printStackTrace();
      }
      
      assert orderWebhookDto != null;
      shoper.getOrderById(orderWebhookDto.getOrderId());
      
      return new ResponseEntity<>("OK", HttpStatus.OK);
   }
}
