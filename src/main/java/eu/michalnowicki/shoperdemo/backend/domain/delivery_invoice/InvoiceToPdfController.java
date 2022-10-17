package eu.michalnowicki.shoperdemo.backend.domain.delivery_invoice;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import eu.michalnowicki.shoperdemo.backend.domain.delivery_invoice.dto.DeliveryInvoiceDto;
import eu.michalnowicki.shoperdemo.backend.utils.Constants;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
class InvoiceToPdfController {
   
   private final TemplateEngine templateEngine;
   
   InvoiceToPdfController(final TemplateEngine templateEngine) {
      this.templateEngine = templateEngine;
      createOutputDir();
   }
   
   public File createPdfInvoice(final DeliveryInvoiceDto invoice) {
      Context context = new Context();
      context.setVariable("invoice", invoice);
      
      String processHtml = templateEngine.process("pdf-templates/shipping_invoice_template", context);
      
      final String fileName = getFileName(invoice);
      return makePdf(processHtml, fileName);
      
   }
   
   private void createOutputDir() {
      final var out = new File("out");
      if (!out.exists()) {
         try {
            Files.createDirectory(Path.of("out"));
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }
   
   @NotNull
   private File makePdf(final String processHtml, final String fileName) {
      final File file = new File("out" + File.separator + fileName);
      try (OutputStream outputStream = new FileOutputStream(file)) {
         ITextRenderer renderer = new ITextRenderer();
         ITextFontResolver resolver = renderer.getFontResolver();
         final var resource = new ClassPathResource("/static/fonts/Roboto.ttf");
         resolver.addFont(resource.getPath(), BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
         
         renderer.setDocumentFromString(processHtml);
         renderer.layout();
         renderer.createPDF(outputStream, true);
      } catch (IOException | DocumentException e) {
         e.printStackTrace();
      }
      return file;
   }
   
   @NotNull
   private String getFileName(final DeliveryInvoiceDto invoice) {
      final var invoiceDate = LocalDate.parse(invoice.getIssueDate(), DateTimeFormatter.ofPattern(Constants.DATE_FORMAT)).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
      final var invoiceNumber = invoice.getInvoiceNumber().split(" ")[0];
      final var orderNumber = invoice.getOrderNumShort();
      
      final String fileName = MessageFormat.format("{0}_{1}_{2}.pdf",
            invoiceDate,
            invoiceNumber,
            orderNumber
      );
      return fileName;
   }
   
}
