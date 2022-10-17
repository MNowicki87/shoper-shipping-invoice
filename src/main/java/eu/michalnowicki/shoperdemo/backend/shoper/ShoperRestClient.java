package eu.michalnowicki.shoperdemo.backend.shoper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

import static okhttp3.internal.Util.EMPTY_REQUEST;

@Log
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ShoperRestClient {
   
   private static final OkHttpClient client = new OkHttpClient();
   private static final int API_COOLDOWN_MILLIS = 1000;
   private static final int API_CALLS_LIMIT = 10;
   private static int activeApiRequests = 1;
   private static LocalDateTime lastRequestTime = LocalDateTime.now();
   
   
   public static String sendRequest(final Request request) {
      if (isLimitExceeded()) {
         log.warning("API calls limit reached - cooling down for " + API_COOLDOWN_MILLIS / 1000 + " s");
         cooldown();
      }
      return executeRequest(request);
   }
   
   private static boolean isLimitExceeded() {
      return activeApiRequests == API_CALLS_LIMIT
            && lastRequestTime.plusSeconds(2).isAfter(LocalDateTime.now());
   }
   
   private static void cooldown() {
      try {
         Thread.sleep(API_COOLDOWN_MILLIS);
      } catch (InterruptedException e) {
         log.severe("Interrupted while waiting for Shoper API cooldown");
      }
   }
   
   private static String executeRequest(final Request request) {
      try (final var response = client.newCall(request).execute()) {
         activeApiRequests = Integer.getInteger(response.header("X-Shop-Api-Calls"), 1);
         lastRequestTime = LocalDateTime.now();
         return response.body() != null ? response.body().string() : null;
      } catch (IOException e) {
         log.severe("Shoper API request failed: " + e.getMessage());
         return "";
      }
   }
   
   static String getToken(final String clientId, final String clientSecret, final String authEndpoint) {
      final var credentials = Credentials.basic(clientId, clientSecret);
      final var url = HttpUrl.parse(authEndpoint);
      final var request = new Request.Builder()
            .url(Objects.requireNonNull(url))
            .post(EMPTY_REQUEST)
            .addHeader("Authorization", credentials)
            .build();
      
      final var json = sendRequest(request);
      
      final var mapper = new ObjectMapper();
      try {
         return mapper.readTree(json).path("access_token").toString().replace("\"", "").trim();
      } catch (JsonProcessingException e) {
         return "";
      }
   }
   
   
}
