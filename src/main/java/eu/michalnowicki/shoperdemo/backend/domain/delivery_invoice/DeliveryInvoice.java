package eu.michalnowicki.shoperdemo.backend.domain.delivery_invoice;

import eu.michalnowicki.shoperdemo.backend.domain.delivery_invoice.dto.DeliveryInvoiceDto;
import eu.michalnowicki.shoperdemo.backend.domain.incoming_payment.Payment;
import eu.michalnowicki.shoperdemo.backend.domain.order.Order;
import eu.michalnowicki.shoperdemo.backend.utils.Constants;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import java.time.LocalDate;

@Entity
@Table(name = "delivery_invoices", uniqueConstraints = {@UniqueConstraint(columnNames = {"invoice_number", "year"})})
@Getter
@Builder(toBuilder = true)
@Valid
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DeliveryInvoice {
   
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @EqualsAndHashCode.Include
   private Long id;
   
   @Column(name = "invoice_number", nullable = false)
   @EqualsAndHashCode.Include
   private Integer invoiceNumber;
   
   @Column(name = "year", nullable = false)
   @EqualsAndHashCode.Include
   private int year;
   
   @Column(name = "issue_date", nullable = false)
   @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT_PATTERN)
   private LocalDate issueDate;
   
   @Column(name = "gross_amount", nullable = false)
   private double grossAmount;
   
   @OneToOne
   @JoinColumn(name = "order_id")
   @EqualsAndHashCode.Include
   private Order order;
   
   @ManyToOne
   @JoinColumn(name = "payment_id")
   @EqualsAndHashCode.Include
   private Payment payment;
   
   @PrePersist
   void setYear() {
      this.year = payment.getDate().getYear();
   }
   
   @AssertTrue(message = "Shipping costs must be greater than 0")
   private boolean isShippingCostsGreaterThanZero() {
      return order.getShippingCost() > 0;
   }
   
   
   public DeliveryInvoiceDto toDto() {
      return DeliveryInvoiceToDtoMapper.map(this);
   }
}
