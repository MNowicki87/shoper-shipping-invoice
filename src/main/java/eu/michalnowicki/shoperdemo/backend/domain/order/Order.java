package eu.michalnowicki.shoperdemo.backend.domain.order;

import eu.michalnowicki.shoperdemo.backend.domain.address.Address;
import eu.michalnowicki.shoperdemo.backend.domain.incoming_payment.Payment;
import eu.michalnowicki.shoperdemo.backend.utils.Constants;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "orders")
@Validated
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Order {
   
   // imported values:
   
   @Id
   private Long id;
   
   @Email(message = "Email not validated")
   @NotBlank(message = "Email field is mandatory")
   @Column(name = "email")
   private String email;
   
   @Past(message = "Date of creation must be in the past")
   @Column(name = "created_at")
   @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT_PATTERN)
   private LocalDateTime createdAt;
   
   @Past(message = "Modification date must be in the past")
   @Column(name = "updated_at")
   @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT_PATTERN)
   private LocalDateTime updatedAt;
   
   @ManyToOne
   @JoinColumn(name = "status_id")
   private Status status;
   
   @Column(name = "total")
   private double total;
   
   @Column(name = "shipping_cost")
   private double shippingCost;
   
   @Column(name = "paid")
   private double paid;
   
   @Column(name = "client_notes", length = 500)
   private String clientNotes;
   
   @Column(name = "admin_private_notes", length = 500)
   private String adminPrivNotes;
   
   @Column(name = "admin_public_notes", length = 500)
   private String adminPubNotes;
   
   @ManyToOne
   @JoinColumn(name = "billing_address_id")
   private Address billingAddress;
   
   @ManyToOne
   @JoinColumn(name = "delivery_address_id")
   private Address deliveryAddress;
   
   @Column(name = "promo_code")
   private String promoCode;
   
   @OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
   @ToString.Exclude
   private Set<OrderProduct> products;
   
   // not imported:
   
   @Column(name = "shipping_paid")
   @Setter
   private boolean isShippingPaid;
   
   @ManyToOne(fetch = FetchType.EAGER)
   @JoinColumn(name = "payment_id")
   @Setter
   private Payment payment;
   
   @Column(name = "order_source")
   @Enumerated(EnumType.STRING)
   private OrderSource source;
   
   @PrePersist
   @PreUpdate
   private void setShippingPaid() {
      if (isPaidInFull()) setShippingPaid(true);
   }
   
   private boolean isPaidInFull() {
      return this.total == this.paid;
   }
   
   Order restUpdate(final Order newInfo) {
      return this.toBuilder()
            .billingAddress(newInfo.billingAddress)
            .deliveryAddress(newInfo.deliveryAddress)
            .products(newInfo.products)
            .total(newInfo.total)
            .paid(newInfo.paid)
            .updatedAt(newInfo.updatedAt)
            .status(newInfo.status)
            .adminPrivNotes(newInfo.adminPrivNotes)
            .adminPubNotes(newInfo.adminPubNotes)
            .clientNotes(newInfo.clientNotes)
            .build();
   }
   
   @Override
   public boolean equals(final Object o) {
      if (this == o) return true;
      if (!(o instanceof Order)) return false;
      final Order order = (Order) o;
      return getId().equals(order.getId()) && getCreatedAt().equals(order.getCreatedAt());
   }
   
   @Override
   public int hashCode() {
      return Objects.hash(getId(), getCreatedAt());
   }
}
