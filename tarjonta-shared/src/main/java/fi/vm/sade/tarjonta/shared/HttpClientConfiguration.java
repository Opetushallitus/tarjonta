package fi.vm.sade.tarjonta.shared;

import fi.vm.sade.javautils.httpclient.OphHttpClient;
import fi.vm.sade.javautils.httpclient.apache.ApacheOphHttpClient;
import fi.vm.sade.properties.OphProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfiguration {

    private static final String CLIENT_SUBSYSTEM_CODE = "tarjonta";

    @Bean
    public OphHttpClient httpClient(OphProperties properties) {
        return ApacheOphHttpClient.createDefaultOphClient(CLIENT_SUBSYSTEM_CODE, properties);
    }

}
