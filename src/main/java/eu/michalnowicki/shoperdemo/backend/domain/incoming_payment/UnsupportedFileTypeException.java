package eu.michalnowicki.shoperdemo.backend.domain.incoming_payment;

public class UnsupportedFileTypeException extends RuntimeException {
   
   public UnsupportedFileTypeException(final String message, final Throwable cause) {
      super(message, cause);
   }
   
   public UnsupportedFileTypeException(final String message) {
      super(message);
   }
}
