package fi.vm.sade.tarjonta.shared;

import fi.vm.sade.javautils.httpclient.OphHttpClient;
import fi.vm.sade.javautils.httpclient.apache.ApacheOphHttpClient;
import fi.vm.sade.properties.OphProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfiguration {

    public static final String CALLER_ID = "1.2.246.562.10.00000000001.tarjonta";
    public static final String CSRF_VALUE = "CSRF";

    @Bean
    public OphHttpClient httpClient(OphProperties properties) {
        return ApacheOphHttpClient.createDefaultOphClient(CALLER_ID, properties);
    }

}
