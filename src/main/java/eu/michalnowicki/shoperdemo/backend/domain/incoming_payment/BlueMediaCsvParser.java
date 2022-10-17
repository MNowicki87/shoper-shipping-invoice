package eu.michalnowicki.shoperdemo.backend.domain.incoming_payment;

import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import eu.michalnowicki.shoperdemo.backend.domain.order.Order;
import eu.michalnowicki.shoperdemo.backend.domain.order.OrderFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class BlueMediaCsvParser {
   
   // todo: remove dependecy:
   private final OrderFacade orderFacade;
   
   private static final String HEADER_SPECIFIC_STRING = "TheCompany,SETT_";
   private static final String VALUE_DELIMITER = ",";
   private static final Charset FILE_CHARSET = StandardCharsets.US_ASCII;
   
   Set<Order> parse(final MemoryBuffer buffer, final String bmPaymentID) {
      final List<String> lines = getLines(buffer);
      validateCsvData(lines, bmPaymentID);
      return getOrders(lines);
   }
   
   Set<Order> getOrders(final List<String> lines) {
      final Set<Order> orders = new HashSet<>();
      
      for (final String currentLine : lines) {
         if (currentLine.isBlank()) break;
         final Order order = getOrder(currentLine);
         if (order != null) {
            orders.add(order);
         }
      }
      return orders;
   }
   
   private void validateCsvData(final List<String> lines, final String bmPaymentID) {
      if (invalidDataFormat(lines)) {
         throw new UnsupportedFileTypeException("Nieobsługiwany format danych! [wymagane BlueMedia - Raport dzienny]");
      }
      if (dataNotRelatedToPayment(lines, bmPaymentID)) {
         throw new UnsupportedFileTypeException("Plik nie zawiera informacji o wybranej płatności: " + bmPaymentID);
      }
      
   }
   
   private boolean dataNotRelatedToPayment(final List<String> lines, final String bmPaymentID) {
      return lines.stream().noneMatch(l -> l.contains(bmPaymentID));
   }
   
   private boolean invalidDataFormat(final List<String> lines) {
      return lines.stream().filter(l -> l.contains(HEADER_SPECIFIC_STRING)).count() < 1;
   }
   
   private List<String> getLines(final MemoryBuffer buffer) {
      InputStreamReader inputStreamReader = new InputStreamReader(buffer.getInputStream(), FILE_CHARSET);
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
      
      return bufferedReader.lines().collect(Collectors.toList());
   }
   
   private Order getOrder(String line) {
      
      final String[] fields = line.split(VALUE_DELIMITER);
      
      if ("u".equals(fields[1])) {
         final long orderId = Long.parseLong(fields[2].split("_")[0]);
         return orderFacade.findById(orderId);
      }
      return null;
   }
   
}
