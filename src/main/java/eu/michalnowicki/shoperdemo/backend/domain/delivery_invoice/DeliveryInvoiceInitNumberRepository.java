package eu.michalnowicki.shoperdemo.backend.domain.delivery_invoice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

interface DeliveryInvoiceInitNumberRepository extends JpaRepository<DeliveryInvoiceInitNumber, Integer> {
   
   @Query("select d.value from DeliveryInvoiceInitNumber d where d.year = :year ")
   Optional<Integer> getMaxValueForYear(@Param("year") int year);
   
}
