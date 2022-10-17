package eu.michalnowicki.shoperdemo.backend.domain.delivery_invoice.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DeliveryInvoiceDto {
   
   String name;
   String company;
   String address;
   String postCode;
   String city;
   String nip;
   
   String issueDate;
   
   String paymentType;
   
   String invoiceNumber;
   String orderNumShort;
   
   String grossAmount;
   String netAmount;
   String vatAmount;
   String grossInWords;
}
