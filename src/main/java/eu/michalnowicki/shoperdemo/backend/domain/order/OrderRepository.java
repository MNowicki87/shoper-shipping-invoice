package eu.michalnowicki.shoperdemo.backend.domain.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

interface OrderRepository extends JpaRepository<Order, Long> {
   
   
   List<Order> findAllByOrderByIdDesc();
   
   @Query(value = "select o from Order o " +
         "where lower(o.email) like lower(concat('%', :searchTerm, '%')) " +
         "or lower(o.billingAddress.firstName) like lower(concat('%', :searchTerm, '%'))" +
         "or lower(o.billingAddress.lastName) like lower(concat('%', :searchTerm, '%'))" +
         "or lower(o.status.name) like lower(concat('%', :searchTerm, '%'))" +
         "or lower(o.billingAddress.city) like lower(concat('%', :searchTerm, '%')) " +
         "or concat(o.id, '') like concat('%', :searchTerm, '%')")
   List<Order> search(@Param("searchTerm") String searchTerm);
   
   @Query("select o from Order o " +
         "where lower(o.status.type) not like lower('NOT_COMPLETED') " +
         "and o.shippingCost > 0 " +
         "and o.payment is null " +
         "and o.createdAt > :date " +
         "order by o.id desc")
   List<Order> findNotCancelledOrdersWithShippingNotOlderThan(@Param("date") LocalDateTime date);
   
   Optional<Order> findFirstByOrderByIdDesc();
   
   Optional<Order> findFirstByOrderByUpdatedAtDesc();
   
   @Query(nativeQuery = true, value = "select avg(o.total) from orders o " +
         "left join statuses s " +
         "where lower(s.type) not like lower('NOT_COMPLETED') ")
   Optional<Double> avgOrderValue();
   
}
