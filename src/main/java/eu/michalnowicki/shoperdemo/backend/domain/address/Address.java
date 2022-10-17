package eu.michalnowicki.shoperdemo.backend.domain.address;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "addresses")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Validated
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Address {
   
   @Id
   @EqualsAndHashCode.Include
   private Integer id;
   
   @NotBlank
   @Column(name = "first_name")
   private String firstName;
   
   @NotBlank
   @Column(name = "last_name")
   private String lastName;
   
   @Column(name = "company")
   private String company;
   
   
   @Column(name = "nip")
   private String nip;
   
   @Column(name = "pesel")
   private String pesel;
   
   @NotBlank
   @Column(name = "city", nullable = false)
   private String city;
   
   @NotBlank
   @Size(min = 2, max = 6)
   @Column(name = "postcode")
   private String postcode;
   
   @NotBlank
   @Column(name = "street1")
   private String street1;
   
   @Column(name = "street2")
   private String street2;
   
   @NotBlank
   @Column(name = "phone_number")
   private String phoneNumber;
}
