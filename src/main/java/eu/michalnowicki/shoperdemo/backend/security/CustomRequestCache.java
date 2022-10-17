package eu.michalnowicki.shoperdemo.backend.security;


import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomRequestCache extends HttpSessionRequestCache {
   @Override
   public void saveRequest(final HttpServletRequest request, final HttpServletResponse response) {
      if (!SecurityUtils.isFrameworkInternalRequest(request)) {
         super.saveRequest(request, response);
      }
   }
}
