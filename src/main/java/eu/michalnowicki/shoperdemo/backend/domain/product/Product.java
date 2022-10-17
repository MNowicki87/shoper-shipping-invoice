package eu.michalnowicki.shoperdemo.backend.domain.product;

import eu.michalnowicki.shoperdemo.backend.shoper.dto.ProductDto;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product {
   
   @Id
   @EqualsAndHashCode.Include
   private Long id;
   
   @Column(name = "code")
   private String code;
   
   Product toDto(ProductDto productDto) {
      return Product.builder()
            .id(productDto.productId)
            .code(productDto.code)
            .build();
   }
   
}
