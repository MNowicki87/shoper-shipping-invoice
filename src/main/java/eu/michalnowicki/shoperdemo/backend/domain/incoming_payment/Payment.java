package eu.michalnowicki.shoperdemo.backend.domain.incoming_payment;

import eu.michalnowicki.shoperdemo.backend.domain.delivery_invoice.DeliveryInvoice;
import eu.michalnowicki.shoperdemo.backend.domain.order.Order;
import eu.michalnowicki.shoperdemo.backend.utils.Constants;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "incoming_payments")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Valid
public class Payment {
   
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   
   @Column(name = "title")
   private String title;
   
   @Column(name = "amount")
   private double amount;
   
   @Column(name = "payment_date")
   @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT_PATTERN)
   @PastOrPresent
   private LocalDate date;
   
   @Column(name = "sender")
   private String sender;
   
   @Column(name = "payment_type")
   @Enumerated(EnumType.STRING)
   @Setter
   private PaymentType paymentType;
   
   @OneToMany(mappedBy = "payment", fetch = FetchType.EAGER)
   @Setter
   private Set<Order> orders;
   
   @OneToMany(mappedBy = "payment", fetch = FetchType.EAGER)
   @Setter
   private Set<DeliveryInvoice> deliveryInvoices;
   
   public boolean areAllInvoicesIssued() {
      return !orders.isEmpty() && orders.size() == deliveryInvoices.size();
   }
   
   PaymentDto toDto() {
      return PaymentDto.builder()
            .title(title)
            .amount(amount)
            .date(date)
            .sender(sender)
            .paymentType(paymentType)
            .orders(orders)
            .build();
   }
}
