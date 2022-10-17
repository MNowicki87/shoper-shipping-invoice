package eu.michalnowicki.shoperdemo.backend.shoper.dto;

import com.fasterxml.jackson.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatusDto {
   
   @JsonProperty("status_id")
   public Integer statusId;
   @JsonProperty("type")
   public Integer type;
   @JsonProperty("order")
   public String order;
   
   public String name;
   
   @JsonProperty("translations")
   private void unpackNameFromNestedObject(Map<String, Map<String, String>> translation) {
      this.name = translation.get("pl_PL").get("name");
   }
   
   @JsonIgnore
   @Valid
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