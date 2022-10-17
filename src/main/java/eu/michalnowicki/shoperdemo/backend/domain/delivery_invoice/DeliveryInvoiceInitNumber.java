package eu.michalnowicki.shoperdemo.backend.domain.delivery_invoice;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "delivery_invoice_init_values")
class DeliveryInvoiceInitNumber {
   
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Integer id;
   
   @Column(name = "year")
   @NotNull
   private int year;
   
   @Column(name = "value")
   @NotNull
   private Integer value;
   
}
