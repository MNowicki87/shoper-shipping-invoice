package eu.michalnowicki.shoperdemo.backend.utils;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NumberToWordsConverterPlTest {
   
   @Test
   void shouldReturnCorrectValue() {
      assertThat(NumberToWordsConverterPl.convert(100.0)).isEqualTo("sto 0/100");
      assertThat(NumberToWordsConverterPl.convert(810.0)).isEqualTo("osiemset dziesięć 0/100");
      assertThat(NumberToWordsConverterPl.convert(1350.0)).isEqualTo("jeden tysiąc trzysta pięćdziesiąt 0/100");
      assertThat(NumberToWordsConverterPl.convert(1D)).isEqualTo("jeden 0/100");
      assertThat(NumberToWordsConverterPl.convert(9_101_321.0)).isEqualTo("dziewięć milionów sto jeden tysięcy trzysta dwadzieścia jeden 0/100");
      assertThat(NumberToWordsConverterPl.convert(-100.0)).isEqualTo("- sto 0/100");
      assertThat(NumberToWordsConverterPl.convert(0D)).isEqualTo("zero 0/100");
   }
   
}