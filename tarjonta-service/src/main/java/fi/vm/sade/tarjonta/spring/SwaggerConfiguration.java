package fi.vm.sade.tarjonta.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfiguration {
  @Bean
  public Docket tarjontaServiceApi() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.basePackage("fi.vm.sade.tarjonta.service.resources"))
        .paths(PathSelectors.any())
        .build()
        .apiInfo(apiInfo());
  }

  @Bean
  public ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("Tarjonta-service")
        .description("Koulutustarjontaa sisälvän Tarjonta-järjestelmän backend")
        .build();
  }
}
