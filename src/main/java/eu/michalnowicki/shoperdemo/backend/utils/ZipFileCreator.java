package eu.michalnowicki.shoperdemo.backend.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Log
public final class ZipFileCreator {
   
   public static File zip(final List<File> files) {
      final ZipFile zipFile = new ZipFile("out" + File.separator + "invoices_" + System.currentTimeMillis() + ".zip");
      addFilesToZip(files, zipFile);
      clearZippedFiles(files);
      
      return zipFile.getFile();
   }
   
   private static void addFilesToZip(final List<File> files, final ZipFile zipFile) {
      try {
         zipFile.addFiles(files);
      } catch (ZipException e) {
         e.printStackTrace();
      }
   }
   
   private static void clearZippedFiles(final List<File> files) {
      files.forEach(file -> {
         try {
            Files.deleteIfExists(file.toPath());
         } catch (IOException e) {
            e.printStackTrace();
         }
      });
   }
   
}
