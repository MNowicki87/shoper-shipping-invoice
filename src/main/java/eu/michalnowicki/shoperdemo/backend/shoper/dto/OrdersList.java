package eu.michalnowicki.shoperdemo.backend.shoper.dto;

import com.fasterxml.jackson.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrdersList {
   
   @JsonProperty("count")
   public Integer count;
   @JsonProperty("pages")
   public Integer pages;
   @JsonProperty("page")
   public Integer page;
   @JsonProperty("list")
   @Valid
   public List<OrderDto> dtos = null;
   
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