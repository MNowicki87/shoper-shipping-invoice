package eu.michalnowicki.shoperdemo.backend.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Objects;
import java.util.stream.Stream;

@EnableAsync
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
final class TempFileCleaner {
   
   // Delay String format : PnDTnHnMn.nS
   @Scheduled(fixedDelayString = "PT1H", initialDelay = 5_000L)
   static void cleanOutputDirectory() {
      final var out = new File("out");
      if (isDirContainingFiles(out)) {
         Stream.of(Objects.requireNonNull(out.listFiles()))
               .filter(TempFileCleaner::isFileOlderThanOneHour)
               .forEach(TempFileCleaner::delete);
      }
   }
   
   private static boolean isDirContainingFiles(final File out) {
      return out.exists() && out.isDirectory() && out.listFiles() != null;
   }
   
   private static boolean isFileOlderThanOneHour(final File file) {
      try {
         final var time = Files.getLastModifiedTime(file.toPath());
         return time.toInstant().isBefore(Instant.now().minusSeconds(600L));
      } catch (IOException e) {
         e.printStackTrace();
         return false;
      }
   }
   
   private static void delete(final File file) {
      try {
         Files.deleteIfExists(file.toPath());
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   
}
