package eu.michalnowicki.shoperdemo.backend.domain.product;

import eu.michalnowicki.shoperdemo.backend.shoper.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class ProductFactory {
   
   public Product createFrom(ProductDto dto) {
      return Product.builder()
            .id(dto.productId)
            .code(dto.code)
            .build();
   }
}
