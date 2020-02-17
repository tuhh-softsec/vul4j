package vn.mavn.patientservice.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by TaiND on 2019-10-24.
 **/
@Configuration
@EnableSwagger2
@Profile("!prod")
public class SwaggerConfig {

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(Predicates.not(PathSelectors.regex("/error")))
        .paths(Predicates.not(PathSelectors.regex("/oauth/confirm_access")))
        .paths(Predicates.not(PathSelectors.regex("/oauth/error")))
        .paths(Predicates.not(PathSelectors.regex("/actuator")))
        .paths(Predicates.not(PathSelectors.regex("/actuator.*")))
        .paths(Predicates.not(PathSelectors.regex("/oauth/authorize")))
        .build()
        .apiInfo(apiInfo())
        .useDefaultResponseMessages(false);
  }

  private ApiInfo apiInfo() {
    ApiInfoBuilder apiInfoBuilder = new ApiInfoBuilder();
    apiInfoBuilder.title("REST API");
    apiInfoBuilder.description("PM Patient service");
    apiInfoBuilder.version("1.0.0-RELEASE");
    apiInfoBuilder
        .contact(new Contact("Tai Nguyen", "https://toihieuroi.com",
            "taingdinh@gmail.com"));
    return apiInfoBuilder.build();
  }
}
