package eu.michalnowicki.shoperdemo.backend.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public final class NumberToWordsConverterPl {
   
   public static String convert(Double number) {
      long wholePart = number.longValue();
      double decimal = number - wholePart;
      
      final String integerString = convertLong(wholePart);
      
      return integerString + (int) (decimal * 100) + "/100";
   }
   
   private static String convertLong(long number) {
      final String[] singles = {"", "jeden ", "dwa ", "trzy ", "cztery ",
            "pięć ", "sześć ", "siedem ", "osiem ", "dziewięć "};
      
      final String[] teens = {"", "jedenaście ", "dwanaście ", "trzynaście ",
            "czternaście ", "piętnaście ", "szesnaście ", "siedemnaście ",
            "osiemnaście ", "dziewiętnaście "};
      
      final String[] tens = {"", "dziesięć ", "dwadzieścia ", "trzydzieści ", "czterdzieści ",
            "pięćdziesiąt ", "sześćdziesiąt ", "siedemdziesiąt ", "osiemdziesiąt ", "dziewięćdziesiąt "};
      
      final String[] hundreds = {"", "sto ", "dwieście ", "trzysta ", "czterysta ",
            "pięćset ", "sześćset ", "siedemset ", "osiemset ", "dziewięćset "};
      
      final String[][] groups = {{"", "", ""},
            {"tysiąc ", "tysiące ", "tysięcy "},
            {"milion ", "miliony ", "milionów "},
            {"miliard ", "miliardy ", "miliardów "},
            {"bilion ", "biliony ", "bilionów "},
            {"biliard ", "biliardy ", "biliardów "},
            {"trylion ", "tryliony ", "trylionów "}};
      
      long single, teen = 0, ten = 0, hundred = 0, group = 0, ending = 0;
      
      StringBuilder numberToWords = new StringBuilder();
      String sign = "";
      
      if (number < 0) {
         sign = "- ";
         number = -number;
      }
      
      if (number == 0) {
         sign = "zero ";
      }
      
      while (number != 0) {
         hundred = number % 1000 / 100;
         ten = number % 100 / 10;
         single = number % 10;
         
         if (ten == 1 && single > 0) {
            teen = single;
            ten = 0;
            single = 0;
         } else {
            teen = 0;
         }
         
         if (single == 1 && hundred + ten + teen == 0) {
            ending = 0;
         } else if (single == 2 || single == 3 || single == 4) {
            ending = 1;
         } else {
            ending = 2;
         }
         
         if (hundred + ten + teen + single > 0) {
            numberToWords.insert(0, hundreds[(int) hundred] + tens[(int) ten]
                  + teens[(int) teen] + singles[(int) single]
                  + groups[(int) group][(int) ending]);
         }
         
         number = number / 1000;
         group = group + 1;
      }
      
      numberToWords.insert(0, sign);
      
      return numberToWords.toString();
   }
   
}
