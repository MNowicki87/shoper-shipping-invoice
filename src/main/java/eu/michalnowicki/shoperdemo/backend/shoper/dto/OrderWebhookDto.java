
package eu.michalnowicki.shoperdemo.backend.shoper.dto;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eu.michalnowicki.shoperdemo.backend.utils.JsonDateDeserializer;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class OrderWebhookDto {
   
   
   @JsonProperty("order_id")
   private Long orderId;
   @JsonProperty("date")
   @JsonDeserialize(using = JsonDateDeserializer.class)
   private LocalDateTime date;
   @JsonProperty("status_date")
   @JsonDeserialize(using = JsonDateDeserializer.class)
   private LocalDateTime statusDate;
   @JsonProperty("status_id")
   private int statusId;
   @JsonProperty("sum")
   private Double sum;
   @JsonProperty("shipping_cost")
   private Double shippingCost;
   
   @JsonProperty("email")
   @Email
   @NotBlank
   private String email;
   
   @JsonProperty("notes")
   private String notes;
   @JsonProperty("notes_priv")
   private String notesPriv;
   @JsonProperty("notes_pub")
   private String notesPub;
   
   @JsonProperty("paid")
   private Double paid;
   
   @JsonProperty("promo_code")
   private String promoCode;
   
   @JsonIgnore
   private Map<String, Object> additionalProperties = new HashMap<>();
   
   @JsonAnyGetter
   public Map<String, Object> getAdditionalProperties() {
      return this.additionalProperties;
   }
   
   @JsonAnySetter
   public void setAdditionalProperty(String name, Object value) {
      this.additionalProperties.put(name, value);
   }
   
}
