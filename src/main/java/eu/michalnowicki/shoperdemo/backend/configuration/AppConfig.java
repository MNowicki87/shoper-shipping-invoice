package eu.michalnowicki.shoperdemo.backend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
@EnableScheduling
class AppConfig {
   
   @Bean
   ClassLoaderTemplateResolver templateResolver() {
      ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
      templateResolver.setPrefix("templates/");
      templateResolver.setTemplateMode(TemplateMode.HTML);
      templateResolver.setSuffix(".html");
      templateResolver.setCharacterEncoding("UTF-8");
      templateResolver.setOrder(1);
      return templateResolver;
   }
   
}
