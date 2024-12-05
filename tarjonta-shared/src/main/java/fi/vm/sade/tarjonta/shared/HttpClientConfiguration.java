package fi.vm.sade.tarjonta.shared;

import fi.vm.sade.javautils.httpclient.OphHttpClient;
import fi.vm.sade.javautils.httpclient.apache.ApacheOphHttpClient;
import fi.vm.sade.properties.OphProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfiguration {

  public static final String CALLER_ID = "1.2.246.562.10.00000000001.tarjonta";
  public static final String CSRF_VALUE = "CSRF";

  @Bean
  public OphHttpClient httpClient(
      OphProperties properties,
      @Value("${tarjonta-service.httpclient.timeout.millis:59999}") int timeoutMs,
      @Value("${tarjonta-service.httpclient.keepalive.seconds:59}") int connectionTimeToLiveSec) {
    return ApacheOphHttpClient.createDefaultOphClient(
        CALLER_ID, properties, timeoutMs, connectionTimeToLiveSec);
  }
}
