package eu.michalnowicki.shoperdemo.backend.domain.order;

import eu.michalnowicki.shoperdemo.backend.domain.address.Address;
import eu.michalnowicki.shoperdemo.backend.domain.address.AddressFactory;
import eu.michalnowicki.shoperdemo.backend.shoper.dto.OrderDto;
import eu.michalnowicki.shoperdemo.backend.shoper.dto.OrderWebhookDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public final class OrderFactory {
   
   public static Order from(OrderWebhookDto source) {
      
      return Order.builder()
            .id(source.getOrderId())
            .email(source.getEmail())
            .createdAt(source.getDate())
            .updatedAt(source.getStatusDate())
            .clientNotes(source.getNotes())
            .adminPrivNotes(source.getNotesPriv())
            .adminPubNotes(source.getNotesPub())
            .total(source.getSum())
            .paid(source.getPaid())
            .promoCode(source.getPromoCode())
            .shippingCost(source.getShippingCost())
            .build();
   }
   
   public static Order from(OrderDto source) {
      final Address billingAddress = AddressFactory.map(source.billingAddress);
      final Address deliveryAddress = AddressFactory.map(source.deliveryAddress);
      final Status status = null;
      
      return Order.builder()
            .id(source.orderId)
            .email(source.email)
            .createdAt(source.date)
            .updatedAt(source.statusDate)
            .billingAddress(billingAddress)
            .deliveryAddress(deliveryAddress)
            .status(status)
            .clientNotes(source.notes)
            .adminPrivNotes(source.notesPriv)
            .adminPubNotes(source.notesPub)
            .total(source.sum)
            .paid(source.paid)
            .promoCode(source.promoCode)
            .shippingCost(source.shippingCost)
            .build();
   }
   
}
