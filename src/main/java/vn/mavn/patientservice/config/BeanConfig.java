package vn.mavn.patientservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import vn.mavn.patientservice.gracefulshd.GracefulShutdown;

/**
 * Created by TaiND on 2020-02-16.
 **/
@Configuration
public class BeanConfig {

  @Value("${graceful.waiting-time}")
  private Long gracefulShutdownTimeInSecond;

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public GracefulShutdown gracefulShutdown() {
    return new GracefulShutdown(gracefulShutdownTimeInSecond);
  }

  @Bean
  public ConfigurableServletWebServerFactory webServerFactory(
      final GracefulShutdown gracefulShutdown) {
    TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
    factory.addConnectorCustomizers(gracefulShutdown);
    return factory;
  }
}
