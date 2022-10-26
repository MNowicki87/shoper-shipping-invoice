package eu.michalnowicki.shoperdemo.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
   private static final String LOGIN_PROCESSING_URL = "/login";
   private static final String LOGIN_FAILURE_URL = "/login?error";
   private static final String LOGIN_URL = "/login";
   private static final String LOGOUT_SUCCESS_URL = "/login";
   
   @Bean
   public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
   }
   
   @Value("${app.login}")
   private String login;
   
   @Value("${app.password}")
   private String password;
   
   
   @Override
   protected void configure(HttpSecurity http) throws Exception {
      http.csrf().disable()
            .requestCache().requestCache(new CustomRequestCache())
            
            .and()
            .authorizeRequests()
            .requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()
            .antMatchers("/api/orders").permitAll()
            .anyRequest().authenticated()
            
            .and().formLogin()
            .loginPage(LOGIN_URL)
            .permitAll()
            .loginProcessingUrl(LOGIN_PROCESSING_URL)
            .failureUrl(LOGIN_FAILURE_URL)
            
            .and()
            .logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL)
            
            .and()
            .headers().frameOptions().disable();
   }
   
   @Bean
   @Override
   public UserDetailsService userDetailsService() {
      UserDetails user =
            User.withUsername(login)
                  .password(passwordEncoder().encode(password))
                  .roles("USER")
                  .build();
      
      return new InMemoryUserDetailsManager(user);
   }
   
   @Override
   public void configure(WebSecurity web) {
      web.ignoring().antMatchers(
            "/VAADIN/**",
            "/favicon.ico",
            "/robots.txt",
            "/manifest.webmanifest",
            "/sw.js",
            "/offline.html",
            "/icons/**",
            "/images/**",
            "/styles/**"
      );
   }
   
}
