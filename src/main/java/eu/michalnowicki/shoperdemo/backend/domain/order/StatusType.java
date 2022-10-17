package eu.michalnowicki.shoperdemo.backend.domain.order;

import java.util.HashMap;
import java.util.Map;

public enum StatusType {
   NEW(1),
   OPENED(2),
   CLOSED(3),
   NOT_COMPLETED(4);
   
   private final int value;
   private static final Map<Integer, StatusType> map = new HashMap<>();
   
   StatusType(final int value) {
      this.value = value;
   }
   
   static {
      for (StatusType statusType : StatusType.values()) {
         map.put(statusType.value, statusType);
      }
   }
   
   public static StatusType valueOf(int statusType) {
      return map.get(statusType);
   }
   
   public int getValue() {
      return value;
   }
   
}
