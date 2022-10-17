package eu.michalnowicki.shoperdemo.backend.domain.incoming_payment;

import java.util.Set;

public class PaymentFactory {
   
   public static Payment from(final PaymentDto dto) {
      return Payment.builder()
            .amount(dto.getAmount())
            .date(dto.getDate())
            .sender(dto.getSender())
            .title(dto.getTitle())
            .deliveryInvoices(null)
            .orders(Set.copyOf(dto.getOrders()))
            .paymentType(dto.getPaymentType())
            .build();
   }
}
