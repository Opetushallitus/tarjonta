package fi.vm.sade.tarjonta.service.impl;

import fi.vm.sade.tarjonta.shared.UrlConfiguration;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Path("/frontProperties.js")
@Component
public class FrontPropertiesResource {

  @Autowired UrlConfiguration urlConfiguration;

  @GET
  @Produces("application/javascript;charset=UTF-8")
  public String frontProperties() {
    return "window.urls.addOverrides(" + urlConfiguration.frontPropertiesToJson() + ")";
  }
}
