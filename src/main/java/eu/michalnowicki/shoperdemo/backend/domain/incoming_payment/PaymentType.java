package eu.michalnowicki.shoperdemo.backend.domain.incoming_payment;

import java.util.HashMap;
import java.util.Map;

public enum PaymentType {
   
   TRANSFER("Przelew"),
   EPAY("Płatność elektroniczna"),
   CREDIT("Raty");
   
   private final String label;
   
   private static final Map<String, PaymentType> BY_LABEL = new HashMap<>();
   
   static {
      for (PaymentType p : values()) {
         BY_LABEL.put(p.label, p);
      }
   }
   
   public static PaymentType valueOfLabel(String label) {
      return BY_LABEL.get(label);
   }
   
   PaymentType(String label) {
      this.label = label;
   }
   
   @Override
   public String toString() {
      return this.label;
   }
}
