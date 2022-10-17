package eu.michalnowicki.shoperdemo.backend.domain.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {
   
   private final ProductRepository productRepo;
   
   public Product getProduct(final long productId) {
      return productRepo.getOne(productId);
   }
   
   public Product getOrSave(final Product product) {
      return productRepo.findById(product.getId()).orElseGet(() ->
            productRepo.saveAndFlush(product));
   }
   
   
}
