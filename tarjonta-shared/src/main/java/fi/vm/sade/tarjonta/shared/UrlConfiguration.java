package fi.vm.sade.tarjonta.shared;

import fi.vm.sade.properties.OphProperties;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class UrlConfiguration extends OphProperties {

  @Autowired
  public UrlConfiguration(Environment environment) {
    // debugMode();
    addFiles("/tarjonta-service-oph.properties");
    frontProperties.put("koulutusinformaatio-app-web.baseUrl", "https://${host.haku}");
    frontProperties.put("ataru-service.baseUrl", "https://${host.virkailija}");
    frontProperties.put("ataru-app-web.baseUrl", "https://${host.haku}");
    addOverride("host-cas", environment.getRequiredProperty("host.host-cas"));
    addOverride("host-virkailija", environment.getRequiredProperty("host.host-virkailija"));
  }
}
