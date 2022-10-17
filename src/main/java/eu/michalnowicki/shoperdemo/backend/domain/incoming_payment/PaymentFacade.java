package eu.michalnowicki.shoperdemo.backend.domain.incoming_payment;

import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import eu.michalnowicki.shoperdemo.backend.domain.order.Order;
import eu.michalnowicki.shoperdemo.backend.domain.order.OrderFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentFacade {
   
   private final PaymentRepository paymentRepo;
   private final OrderFacade orderFacade;
   
   
   public Optional<PaymentDto> getLast() {
      var optional = paymentRepo.findFirst(PageRequest.of(0, 1)).stream().findFirst();
      if (optional.isPresent()) {
         return optional.map(Payment::toDto);
      }
      return Optional.empty();
   }
   
   /**
    * Verifies if payment is not yet persisted. Persists payment and updates relevant {@link Order} records.
    *
    * @param dto The payment object to be persisted.
    */
   public void registerPayment(@Validated final PaymentDto dto) {
      if (!isPaymentRegistered(dto)) {
         final var payment = PaymentFactory.from(dto);
         final var persisted = paymentRepo.save(payment);
         persisted.getOrders().forEach(o -> orderFacade.registerPayment(o.getId(), persisted));
      }
   }
   
   public boolean isPaymentRegistered(final PaymentDto dto) {
      final Payment temp = Payment.builder()
            .sender(dto.getSender())
            .title(dto.getTitle())
            .date(dto.getDate())
            .amount(dto.getAmount())
            .build();
      final ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
      final Example<Payment> paymentExample = Example.of(temp, matcher);
      return paymentRepo.exists(paymentExample);
   }
   
   public List<Payment> findAll(final String value) {
      return paymentRepo.search(value);
   }
   
   public void delete(final Payment payment) {
      paymentRepo.deleteById(payment.getId());
   }
   
   public void updatePayment(final Payment item) {
      Payment entity = paymentRepo.getOne(item.getId());
      entity.getOrders().forEach(o -> orderFacade.findById(o.getId()).setPayment(null));
      entity.toBuilder()
            .paymentType(item.getPaymentType())
            .orders(item.getOrders())
            .build();
      paymentRepo.save(entity);
      item.getOrders().forEach(
            o -> orderFacade.findById(o.getId()).setPayment(item)
      );
   }
   
   public Set<Order> parseBlueMedia(final MemoryBuffer buffer, final String title) {
      return new BlueMediaCsvParser(orderFacade).parse(buffer, title);
   }
   
   public List<PaymentDto> parseMbank(final MemoryBuffer buffer) {
      return new MBankCsvParser().parse(buffer);
   }
}
