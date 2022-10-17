package eu.michalnowicki.shoperdemo.backend.domain.order;

import eu.michalnowicki.shoperdemo.backend.domain.address.AddressService;
import eu.michalnowicki.shoperdemo.backend.domain.incoming_payment.Payment;
import eu.michalnowicki.shoperdemo.backend.shoper.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderFacade {
   
   private final OrderRepository orderRepo;
   private final OrderProductsRepository orderProductsRepo;
   private final StatusFacade statusFacade;
   private final AddressService addressService;
   
   public Order findById(final Long id) {
      final Order order = orderRepo.getOne(id);
      Hibernate.initialize(order);
      return order;
   }
   
   private void createOrUpdate(final Order orderInfo) {
      orderRepo.findById(orderInfo.getId())
            .ifPresentOrElse(
                  order -> {
                     final var is_updated = order.getUpdatedAt().isBefore(orderInfo.getUpdatedAt());
                     if (is_updated) {
                        final var updatedOrder = order.restUpdate(orderInfo);
                        orderRepo.saveAndFlush(updatedOrder);
                     }
                  },
                  () -> orderRepo.save(orderInfo));
   }
   
   /**
    * Creates new {@link Order} or updates existing one.
    *
    * @param dto {@link OrderDto} object mapped from Shoper REST API response
    */
   public void processNewOrderInfo(final OrderDto dto) {
      final var order = OrderFactory.from(dto);
      var updated = order.toBuilder()
            .status(statusFacade.getById(dto.statusId))
            .billingAddress(addressService.getOrSave(order.getBillingAddress()))
            .deliveryAddress(addressService.getOrSave(order.getDeliveryAddress()))
            .build();
      createOrUpdate(updated);
   }
   
   public List<Order> findAll() {
      return orderRepo.findAllByOrderByIdDesc();
   }
   
   public List<Order> findAll(final String filterText) {
      if (filterText == null || filterText.isEmpty()) {
         return findAll();
      }
      return orderRepo.search(filterText);
   }
   
   public long count() {
      return orderRepo.count();
   }
   
   public Map<String, Integer> getCitiesStats() {
      return findAll().stream()
            .collect(Collectors.groupingBy(o -> o.getBillingAddress().getCity()))
            .entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()));
   }
   
   public List<OrderProduct> getProductsForOrder(final Order order) {
      return orderProductsRepo.findAllByOrder(order);
   }
   
   public void setProductsForOrder(final List<OrderProduct> products) {
      orderProductsRepo.saveAll(products);
   }
   
   public List<Order> findPotentialOrdersForPayment() {
      return orderRepo.findNotCancelledOrdersWithShippingNotOlderThan(LocalDateTime.now().minusMonths(6));
   }
   
   public long getLatestOrderNumber() {
      return orderRepo.findFirstByOrderByIdDesc().orElse(Order.builder().id(0L).build()).getId();
   }
   
   public double getAverageOrderValue() {
      return orderRepo.avgOrderValue().orElse(0d);
   }
   
   public void registerPayment(final Long orderId, final Payment payment) {
      orderRepo.getOne(orderId).setPayment(payment);
   }
}
