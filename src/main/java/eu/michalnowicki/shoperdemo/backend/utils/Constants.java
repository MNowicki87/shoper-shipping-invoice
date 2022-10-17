package eu.michalnowicki.shoperdemo.backend.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.NumberFormat;
import java.util.Locale;

@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public final class Constants {
   
   public static final String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
   public static final String DATE_FORMAT = "dd-MM-yyyy";
   public static final double NET_GROSS_MULTIPLIER = 1.23;
   public static final Locale LOCALE_PL = new Locale("pl", "PL");
   public static final NumberFormat PLN_NUMBER_FORMAT = NumberFormat.getCurrencyInstance(LOCALE_PL);
}
