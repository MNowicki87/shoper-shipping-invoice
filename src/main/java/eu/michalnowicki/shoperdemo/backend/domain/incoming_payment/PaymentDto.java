package eu.michalnowicki.shoperdemo.backend.domain.incoming_payment;

import eu.michalnowicki.shoperdemo.backend.domain.order.Order;
import eu.michalnowicki.shoperdemo.backend.utils.Constants;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder
@Valid
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PaymentDto {
   
   @EqualsAndHashCode.Include
   private String title;
   
   @EqualsAndHashCode.Include
   private double amount;
   
   @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT_PATTERN)
   @PastOrPresent
   @EqualsAndHashCode.Include
   private LocalDate date;
   
   @EqualsAndHashCode.Include
   private String sender;
   
   @NotNull
   private PaymentType paymentType;
   
   @NotEmpty
   private Set<Order> orders;
   
}
