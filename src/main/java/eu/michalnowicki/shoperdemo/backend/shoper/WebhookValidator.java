package eu.michalnowicki.shoperdemo.backend.shoper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.MultiValueMap;

@Log
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class WebhookValidator {
   
   static boolean validate(final String json, final MultiValueMap<String, String> headers, final String secret) {
      if (headers.isEmpty()) {
         log.warning("Webhook header is missing!");
         return false;
      }
      
      final String webhookId = headers.get("x-webhook-id").get(0);
      final String webhookSha1 = headers.get("x-webhook-sha1").get(0);
      final String sha = DigestUtils.sha1Hex(webhookId + ":" + secret + ":" + json);
      
      if (sha.equals(webhookSha1)) {
         log.info("Webhook security key confirmed");
         return true;
      } else {
         log.warning("Webhook secret mismatch!");
         return false;
      }
   }
   
}
