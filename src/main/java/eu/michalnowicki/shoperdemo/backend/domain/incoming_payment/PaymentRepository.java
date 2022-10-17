package eu.michalnowicki.shoperdemo.backend.domain.incoming_payment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface PaymentRepository extends JpaRepository<Payment, Long> {
   
   @Query("select p from Payment p " +
         "where lower(p.paymentType) like lower(concat('%', :searchTerm, '%')) " +
         "or lower(p.sender) like lower(concat('%', :searchTerm, '%'))" +
         "or lower(p.title) like lower(concat('%', :searchTerm, '%'))")
   List<Payment> search(@Param("searchTerm") String searchTerm);
   
   @Query("select p from Payment p order by p.date desc")
   List<Payment> findFirst(Pageable pageable);
   
   void deleteById(Long id);
   
   Payment save(Payment payment);
   
   Payment getOne(Long id);
}
