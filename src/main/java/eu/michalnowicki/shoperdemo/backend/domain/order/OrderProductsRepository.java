package eu.michalnowicki.shoperdemo.backend.domain.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderProductsRepository extends JpaRepository<OrderProduct, Long> {
   
   List<OrderProduct> findAllByOrder(Order order);
   
}
