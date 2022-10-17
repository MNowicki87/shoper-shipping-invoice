package eu.michalnowicki.shoperdemo.backend.domain.delivery_invoice.dto;

public class ExpiredPaymentDateException extends RuntimeException {
   public ExpiredPaymentDateException(String message) {
      super(message);
   }
   
   public ExpiredPaymentDateException(String message, Throwable cause) {
      super(message, cause);
   }
}
