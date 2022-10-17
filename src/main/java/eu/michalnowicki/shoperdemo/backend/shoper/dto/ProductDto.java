package eu.michalnowicki.shoperdemo.backend.shoper.dto;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDto {
   @JsonProperty("code")
   public String code;
   String type;
   @JsonProperty("product_id")
   public Long productId;
   @JsonProperty("unit_id")
   public String unitId;
   @JsonProperty("tax_id")
   public String taxId;
   @JsonProperty("producer_id")
   public int producer;
   
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