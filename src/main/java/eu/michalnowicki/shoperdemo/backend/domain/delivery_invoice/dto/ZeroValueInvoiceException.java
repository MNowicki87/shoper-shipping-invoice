package eu.michalnowicki.shoperdemo.backend.domain.delivery_invoice.dto;

public class ZeroValueInvoiceException extends RuntimeException {
   
   public ZeroValueInvoiceException(final String message, final Throwable cause) {
      super(message, cause);
   }
   
   public ZeroValueInvoiceException(final String message) {
      super(message);
   }
}
