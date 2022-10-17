package eu.michalnowicki.shoperdemo.backend.domain.order;

import eu.michalnowicki.shoperdemo.backend.domain.product.Product;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "order_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Validated
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OrderProduct {
   
   @Id
   @EqualsAndHashCode.Include
   private Long id;
   
   @ManyToOne
   @NotNull
   @JoinColumn(name = "order_id")
   @EqualsAndHashCode.Include
   private Order order;
   
   @ManyToOne
   @JoinColumn(name = "product_id")
   private Product product;
   
   @Column(name = "discount_percentage")
   private double discountPerc;
   
   @Column(name = "tax")
   private double tax;
   
   @Column(name = "quantity", nullable = false)
   @Min(1)
   private int quantity;
   
   @Column(name = "weight")
   private double unitWeight;
   
}
