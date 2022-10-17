package eu.michalnowicki.shoperdemo.backend.domain.delivery_invoice;

import eu.michalnowicki.shoperdemo.backend.domain.delivery_invoice.dto.DeliveryInvoiceDto;
import eu.michalnowicki.shoperdemo.backend.utils.Constants;
import eu.michalnowicki.shoperdemo.backend.utils.NumberToWordsConverterPl;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

import static eu.michalnowicki.shoperdemo.backend.utils.Constants.NET_GROSS_MULTIPLIER;
import static eu.michalnowicki.shoperdemo.backend.utils.Constants.PLN_NUMBER_FORMAT;

@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
final class DeliveryInvoiceToDtoMapper {
   
   private static final String INVOICE_NUMBER_FORMAT = "%04d / %d";
   
   public static DeliveryInvoiceDto map(DeliveryInvoice invoice) {
      final var buyer = invoice.getOrder().getBillingAddress();
      final var buyerName = buyer.getFirstName() + " " + buyer.getLastName();
      final var year = invoice.getOrder().getPayment().getDate().getYear();
      final var invoiceNumber = String.format(INVOICE_NUMBER_FORMAT, invoice.getInvoiceNumber(), year);
      
      final var netAmount = Math.round((invoice.getGrossAmount() / NET_GROSS_MULTIPLIER) * 100) / 100.0;
      final var vatAmount = invoice.getGrossAmount() - netAmount;
      
      final var grossInWords = NumberToWordsConverterPl.convert(invoice.getGrossAmount());
      
      final var orderId = String.valueOf(invoice.getOrder().getId());
      
      var date = DateTimeFormatter.ofPattern(
            Constants.DATE_FORMAT)
            .format(invoice.getOrder().getPayment().getDate()
            );
      
      return DeliveryInvoiceDto.builder()
            .company(buyer.getCompany())
            .name(buyerName)
            .address(buyer.getStreet1())
            .postCode(buyer.getPostcode())
            .city(buyer.getCity())
            .nip(buyer.getNip())
            .issueDate(date)
            .paymentType(invoice.getOrder().getPayment().getPaymentType().toString())
            .invoiceNumber(invoiceNumber)
            .orderNumShort(orderId)
            .grossAmount(PLN_NUMBER_FORMAT.format(invoice.getGrossAmount()))
            .netAmount(PLN_NUMBER_FORMAT.format(netAmount))
            .vatAmount(PLN_NUMBER_FORMAT.format(vatAmount))
            .grossInWords(grossInWords)
            .build();
   }
}
