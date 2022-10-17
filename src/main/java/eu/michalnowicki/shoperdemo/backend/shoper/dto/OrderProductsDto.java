package eu.michalnowicki.shoperdemo.backend.shoper.dto;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderProductsDto {
   
   @JsonProperty("id")
   public Long id;
   @JsonProperty("order_id")
   public Long orderId;
   @JsonProperty("product_id")
   public Long productId;
   @JsonProperty("stock_id")
   public Long stockId;
   @JsonProperty("price")
   public double price;
   @JsonProperty("discount_perc")
   public double discountPerc;
   @JsonProperty("quantity")
   public Integer quantity;
   @JsonProperty("name")
   public String name;
   @JsonProperty("code")
   public String code;
   @JsonProperty("tax_value")
   public Integer taxValue;
   @JsonProperty("unit")
   public String unit;
   @JsonProperty("weight")
   public Integer weight;
   
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