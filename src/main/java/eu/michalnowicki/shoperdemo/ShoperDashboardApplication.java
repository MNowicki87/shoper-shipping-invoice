package eu.michalnowicki.shoperdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
@EnableConfigurationProperties
public class ShoperDashboardApplication {
   
   public static void main(String[] args) {
      SpringApplication.run(ShoperDashboardApplication.class, args);
   }
   
   
}
