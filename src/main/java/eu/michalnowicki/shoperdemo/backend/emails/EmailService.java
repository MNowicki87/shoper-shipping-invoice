package eu.michalnowicki.shoperdemo.backend.emails;

import eu.michalnowicki.shoperdemo.backend.domain.delivery_invoice.dto.DeliveryInvoiceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

@Log
@Service
@RequiredArgsConstructor
public class EmailService {
   
   private final JavaMailSender mailSender;
   private final TemplateEngine templateEngine;
   
   
   @Value("${email.senderAddress}")
   private String senderEmail;
   
   @Value("${email.senderPersonal}")
   private String senderPersonal;
   
   
   @Value("${email.bccAddress}")
   private String bccAddress;
   
   @Value("${email.mainAddress}")
   private String mainEmail;
   
   @Value("${email.mainPersonal}")
   private String mainPersonal;
   
   public void sendInvoiceByEmail(final DeliveryInvoiceDto invoice, final String email, final File pdf) {
      MimeMessage message = mailSender.createMimeMessage();
      
      try {
         MimeMessageHelper helper = new MimeMessageHelper(message, true);
         helper.setTo(new InternetAddress(email, invoice.getName()));
         String[] bcc = {senderEmail, bccAddress};
         helper.setBcc(bcc);
         helper.setFrom(senderEmail, senderPersonal);
         helper.setSubject("Zamówienie nr " + invoice.getOrderNumShort() + " - Faktura za dostawę");
         helper.setText(prepareText(invoice), true);
         helper.setReplyTo(new InternetAddress(mainEmail, mainPersonal));
         
         final var file = new FileSystemResource(pdf);
         helper.addAttachment("faktura" + invoice.getOrderNumShort() + ".pdf", file);
         
      } catch (MessagingException | UnsupportedEncodingException e) {
         log.severe(e.getMessage());
      }
      mailSender.send(message);
      log.info(MessageFormat.format("Message sent to: {0} <{1}>", invoice.getName(), email));
   }
   
   private String prepareText(final DeliveryInvoiceDto invoice) {
      Context context = new Context();
      context.setVariable("invoice", invoice);
      return templateEngine.process("email/invoice", context);
   }
   
}
