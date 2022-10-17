package eu.michalnowicki.shoperdemo.backend.domain.delivery_invoice;

import eu.michalnowicki.shoperdemo.backend.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

interface DeliveryInvoiceRepository extends JpaRepository<DeliveryInvoice, Long> {
   
   Optional<DeliveryInvoice> findByInvoiceNumber(long invoiceNumber);
   
   Optional<DeliveryInvoice> findByOrder(Order order);
   
   boolean existsByOrder(Order order);
   
   /**
    * Queries Database for highest number of invoice for given year, that is currently stored in the Delivery_Invoice
    * table.
    *
    * @param year - year to be checked for.
    * @return Will return empty if no invoices were issued in given year.
    */
   @Query("select max(i.invoiceNumber) from DeliveryInvoice i " + "where i.year = :year ")
   Optional<Integer> getCurrentNumberForYear(@Param("year") int year);
   
   
   /**
    * Gets latest invoice issued in year.
    *
    * @param year - year to query for.
    * @return Optional of {@link DeliveryInvoice}. Will return empty if no invoices were issued in given year.
    */
   Optional<DeliveryInvoice> findFirstByYearOrderByIssueDateDesc(int year);
   
   
   List<DeliveryInvoice> findAllByIssueDateBetween(LocalDate start, LocalDate end);
}
