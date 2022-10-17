package eu.michalnowicki.shoperdemo.backend.domain.product;

import eu.michalnowicki.shoperdemo.backend.shoper.dto.ProductDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class ProductMapper {
   
   public Product map(ProductDto productDto) {
      return Product.builder()
            .id(productDto.productId)
            .code(productDto.code)
            .build();
   }
   
}
