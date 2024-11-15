package fi.vm.sade.tarjonta.spring.properties;

import fi.vm.sade.properties.OphProperties;
import org.springframework.core.env.Environment;

// @Configuration
public class UrlConfiguration extends OphProperties {

  // @Autowired
  public UrlConfiguration(Environment environment) {
    addFiles("/tarjonta-service-oph.properties");
    addOverride("host-cas", environment.getRequiredProperty("host.host-cas"));
    addOverride("host-virkailija", environment.getRequiredProperty("host.host-virkailija"));
  }
}
