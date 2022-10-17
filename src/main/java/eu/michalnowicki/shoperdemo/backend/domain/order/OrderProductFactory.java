package eu.michalnowicki.shoperdemo.backend.domain.order;

import eu.michalnowicki.shoperdemo.backend.domain.product.Product;
import eu.michalnowicki.shoperdemo.backend.shoper.dto.OrderProductsDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public final class OrderProductFactory {
   
   public static OrderProduct map(final OrderProductsDto dto, Order order, Product product) {
      
      return OrderProduct.builder()
            .id(dto.id)
            .order(order)
            .product(product)
            .discountPerc(dto.discountPerc)
            .quantity(dto.quantity)
            .tax(dto.taxValue)
            .unitWeight(dto.weight)
            .build();
   }
   
}
