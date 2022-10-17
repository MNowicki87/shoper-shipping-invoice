package eu.michalnowicki.shoperdemo.backend.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class JsonDateDeserializer extends JsonDeserializer<LocalDateTime> {
   
   @Override
   public LocalDateTime deserialize(final JsonParser jsonParser,
                                    final DeserializationContext deserializationContext) throws IOException {
      DateTimeFormatter format = DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT_PATTERN);
      String date = jsonParser.getText();
      return LocalDateTime.from(format.parse(date));
   }
   
}
