package eu.michalnowicki.shoperdemo.backend.domain.delivery_invoice;

import eu.michalnowicki.shoperdemo.backend.domain.delivery_invoice.dto.DeliveryInvoiceDto;
import eu.michalnowicki.shoperdemo.backend.domain.delivery_invoice.dto.ExpiredPaymentDateException;
import eu.michalnowicki.shoperdemo.backend.domain.delivery_invoice.dto.ZeroValueInvoiceException;
import eu.michalnowicki.shoperdemo.backend.domain.incoming_payment.Payment;
import eu.michalnowicki.shoperdemo.backend.domain.order.Order;
import eu.michalnowicki.shoperdemo.backend.domain.order.OrderFacade;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.File;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryInvoiceFacade {
   
   private final DeliveryInvoiceRepository deliveryRepo;
   private final OrderFacade orderFacade;
   private final DeliveryInvoiceInitNumberRepository initValueRepo;
   private final InvoiceToPdfController pdfExporter;
   
   public DeliveryInvoiceDto findByOrderId(final long orderId) {
      final Order order = orderFacade.findById(orderId);
      return findByOrder(order).toDto();
   }
   
   public DeliveryInvoice findByOrder(final Order order) {
      return deliveryRepo.findByOrder(order).orElseThrow(() -> {
         throw new EntityNotFoundException("Brak faktury dla zamówienia nr " + order.getId());
      });
   }
   
   public DeliveryInvoice findById(final long invoiceId) {
      final DeliveryInvoice invoice = deliveryRepo.getOne(invoiceId);
      Hibernate.initialize(invoice);
      return invoice;
   }
   
   public void createInvoiceForOrderId(final long orderId) {
      final Order order = orderFacade.findById(orderId);
      if (order.getShippingCost() > 0) {
         if (deliveryRepo.findByOrder(order).isEmpty()) {
            createAndSaveNewInvoice(order);
         }
      } else {
         throw new ZeroValueInvoiceException(
               "Order " + order.getId() + " has no shipping cost. Invoice cannot be created.");
      }
   }
   
   public File createPdfInvoice(final DeliveryInvoiceDto invoice) {
      return pdfExporter.createPdfInvoice(invoice);
   }
   
   private void createAndSaveNewInvoice(final Order order) {
      final Payment payment = order.getPayment();
      if (paymentDateIsValid(payment.getDate())) {
         final int nextInvoiceNumber = getNextInvoiceNumberInYear(payment.getDate().getYear());
         final DeliveryInvoice deliveryInvoice = DeliveryInvoice.builder()
               .invoiceNumber(nextInvoiceNumber)
               .issueDate(payment.getDate())
               .grossAmount(order.getShippingCost())
               .payment(payment)
               .order(order)
               .build();
         deliveryRepo.save(deliveryInvoice);
      } else {
         throw new ExpiredPaymentDateException("Invoice(s) issued after date of payment for order " + order.getId() + ", paid on :" + payment.getDate());
      }
      
   }
   
   private boolean paymentDateIsValid(final LocalDate paymentDate) {
      final var latestInvoiceInYearOfPayment = deliveryRepo.findFirstByYearOrderByIssueDateDesc(paymentDate.getYear());
      return latestInvoiceInYearOfPayment.isEmpty() || !latestInvoiceInYearOfPayment.get().getIssueDate().isAfter(paymentDate);
   }
   
   public Integer getNextInvoiceNumberInYear(final int year) {
      int initNum = getInitNumberForYear(year);
      int dbNum = deliveryRepo.getCurrentNumberForYear(year).orElse(0) + 1;
      
      return Integer.max(initNum, dbNum);
   }
   
   private Integer getInitNumberForYear(final int year) {
      return initValueRepo.getMaxValueForYear(year).orElse(0) + 1;
   }
   
   public void setNewInitNumber(final int year, final int value) {
      DeliveryInvoiceInitNumber newNum = new DeliveryInvoiceInitNumber(null, year, value);
      initValueRepo.save(newNum);
   }
   
   public List<DeliveryInvoice> findAll() {
      return deliveryRepo.findAll();
   }
   
   public List<DeliveryInvoice> getFromLastMonth() {
      return deliveryRepo.findAllByIssueDateBetween(LocalDate.now().minusMonths(1).withDayOfMonth(1), LocalDate.now().withDayOfMonth(1).minusDays(1));
   }
}
