package eu.michalnowicki.shoperdemo.backend.domain.incoming_payment;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;

class MBankCsvParserTest {
   
   @Test
   void shouldParseLine() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
      
      String line = "2020-11-12;2020-11-12;PRZELEW ZEWNĘTRZNY PRZYCHODZĄCY;\"SETT_0111216056 Srodki z platnosci online Blue Media\";\"BLUE MEDIA SPÓŁKA AKCYJNA          UL.POWSTAŃCÓW WARSZAWY 6  81-718 SOPOT\";'56105000861000009030919386';3 792,87;30 816,58;";
      
      final MBankCsvParser parser = new MBankCsvParser();
      
      final var details = parser.readValues(line);
      
      
      assertThat(details.getAmount()).isEqualTo(3792.87);
      assertThat(details.getDate()).isEqualTo("2020-11-12");
      assertThat(details.getSender()).isEqualTo("BLUE MEDIA SPÓŁKA AKCYJNA");
      assertThat(details.getTitle()).isEqualTo("SETT_0111216056");
      
   }
   
}