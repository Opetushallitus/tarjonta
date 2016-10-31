package fi.vm.sade.tarjonta.shared;

import fi.vm.sade.properties.OphProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;

@Configuration
public class UrlConfiguration extends OphProperties {
    public UrlConfiguration() {
        debugMode();
        addFiles("/tarjonta-service-oph.properties");
        addOptionalFiles(Paths.get(System.getProperties().getProperty("user.home"), "/oph-configuration/common.properties").toString());
        frontProperties.put("koulutusinformaatio-app-web.baseUrl", "https://${host.haku}");
        frontProperties.put("ataru-app-web.baseUrl", "https://${host.haku}");
    }
}