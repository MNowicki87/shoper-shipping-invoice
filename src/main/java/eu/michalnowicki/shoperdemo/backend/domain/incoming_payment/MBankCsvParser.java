package eu.michalnowicki.shoperdemo.backend.domain.incoming_payment;

import com.vaadin.flow.component.upload.receivers.MemoryBuffer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//@Service
class MBankCsvParser {
   
   private static final String HEADER_SPECIFIC_STRING = "#Nadawca/Odbiorca";
   private static final String DATA_FILE_TYPE = "Elektroniczne zestawienie operacji";
   private static final int DATE_INDEX = 1;
   private static final int SENDER_INDEX = 4;
   private static final int SENDER_CHAR_LIMIT = 24;
   private static final int AMOUNT_INDEX = 6;
   private static final int TITLE_INDEX = 3;
   private static final int OPERATIONS_TABLE_COLUMN_COUNT = 8;
   private static final String VALUE_DELIMITER = ";";
   private static final Charset FILE_CHARSET = Charset.forName("Cp1250");
   private static final String BLUE_MEDIA = "BLUE MEDIA";
   private static final String SPACE = " ";
   
   List<PaymentDto> parse(final MemoryBuffer buffer) {
      
      final List<String> lines = getLines(buffer);
      validateCsvData(lines);
      return getCsvPaymentDetails(lines);
   }
   
   private void validateCsvData(final List<String> lines) {
      if (lines.stream().filter(l -> l.contains(DATA_FILE_TYPE)).count() < 1) {
         throw new UnsupportedFileTypeException("Nieobsługiwany zestaw danych! [mBank - Zestawienie operacji]");
      }
   }
   
   private List<PaymentDto> getCsvPaymentDetails(final List<String> lines) {
      final int operationsBeginIndex = getOperationsBeginIndex(lines);
      final List<PaymentDto> detailsList = new ArrayList<>();
      
      for (int i = operationsBeginIndex; i < lines.size(); i++) {
         String currentLine = lines.get(i);
         if (currentLine.isBlank()) break;
         detailsList.add(readValues(currentLine));
      }
      return detailsList;
   }
   
   private List<String> getLines(final MemoryBuffer buffer) {
      InputStreamReader inputStreamReader = new InputStreamReader(buffer.getInputStream(), FILE_CHARSET);
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
      
      return bufferedReader.lines().collect(Collectors.toList());
   }
   
   private int getOperationsBeginIndex(final List<String> lines) {
      return lines.indexOf(
            lines.stream()
                  .filter(s -> s.contains(HEADER_SPECIFIC_STRING))
                  .findFirst().orElseThrow()) + 1;
   }
   
   PaymentDto readValues(String line) {
      final String[] fields = line.split(VALUE_DELIMITER);
      if (fields.length != OPERATIONS_TABLE_COLUMN_COUNT) return null;
      
      String sender = getSender(fields[SENDER_INDEX]);
      String title = getTitle(fields[TITLE_INDEX]);
      
      if (sender.contains(BLUE_MEDIA)) {
         title = title.substring(0, title.indexOf(SPACE));
      }
      
      return PaymentDto.builder()
            .date(LocalDate.parse(fields[DATE_INDEX]))
            .sender(sender)
            .amount(getAmount(fields[AMOUNT_INDEX]))
            .title(title)
            .build();
   }
   
   private String getTitle(final String field) {
      return field.replaceAll("[\"]", "").trim();
   }
   
   private String getSender(final String field) {
      try {
         return field.substring(0, field.indexOf(SPACE, SENDER_CHAR_LIMIT)).replaceAll("[\"]", "").trim();
      } catch (IndexOutOfBoundsException e) {
         throw new UnsupportedFileTypeException("Wrong file uploaded. Sender field is missing");
      }
   }
   
   private double getAmount(final String field) {
      return Double.parseDouble(field.replace(SPACE, "").replace(",", "."));
   }
   
}
