package eu.michalnowicki.shoperdemo.backend.shoper.dto;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eu.michalnowicki.shoperdemo.backend.utils.JsonDateDeserializer;
import lombok.Data;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class OrderDto {
   
   @JsonProperty("order_id")
   public Long orderId;
   @JsonProperty("user_id")
   public Integer userId;
   @JsonProperty("date")
   @JsonDeserialize(using = JsonDateDeserializer.class)
   public LocalDateTime date;
   @JsonProperty("status_date")
   @JsonDeserialize(using = JsonDateDeserializer.class)
   public LocalDateTime statusDate;
   @JsonProperty("status_id")
   public Integer statusId;
   @JsonProperty("sum")
   public double sum;
   @JsonProperty("payment_id")
   public String paymentId;
   @JsonProperty("shipping_cost")
   public double shippingCost;
   @JsonProperty("email")
   public String email;
   @JsonProperty("code")
   public String code;
   @JsonProperty("notes")
   public String notes;
   @JsonProperty("notes_priv")
   public String notesPriv;
   @JsonProperty("notes_pub")
   public String notesPub;
   @JsonProperty("paid")
   public double paid;
   @JsonProperty("parent_order_id")
   public Object parentOrderId;
   @JsonProperty("promo_code")
   public String promoCode;
   
   @JsonProperty("delivery_address")
   @Valid
   public AddressDto deliveryAddress;
   @JsonProperty("billing_address")
   @Valid
   public AddressDto billingAddress;
   
   @JsonIgnore
   private final Map<String, Object> additionalProperties = new HashMap<>();
   
   @JsonAnyGetter
   public Map<String, Object> getAdditionalProperties() {
      return this.additionalProperties;
   }
   
   @JsonAnySetter
   public void setAdditionalProperty(String name, Object value) {
      this.additionalProperties.put(name, value);
   }
   
}