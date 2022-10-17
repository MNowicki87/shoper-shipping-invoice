package eu.michalnowicki.shoperdemo.backend.shoper.dto;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressDto {
   
   @JsonProperty("address_id")
   public Integer addressId;
   @JsonProperty("order_id")
   public Integer orderId;
   @JsonProperty("firstname")
   public String firstname;
   @JsonProperty("lastname")
   public String lastname;
   @JsonProperty("company")
   public String company;
   @JsonProperty("pesel")
   public String pesel;
   @JsonProperty("city")
   public String city;
   @JsonProperty("postcode")
   public String postcode;
   @JsonProperty("street1")
   public String street1;
   @JsonProperty("street2")
   public String street2;
   @JsonProperty("state")
   public String state;
   @JsonProperty("country")
   public String country;
   @JsonProperty("phone")
   public String phone;
   @JsonProperty("tax_identification_number")
   public String taxIdentificationNumber;
   
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