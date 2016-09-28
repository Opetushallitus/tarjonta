package fi.vm.sade.tarjonta.service.impl;

import fi.vm.sade.tarjonta.shared.UrlConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/frontProperties.js")
@Component
public class FrontPropertiesResource {

    @Autowired
    UrlConfiguration urlConfiguration;

    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public String frontProperties() {
        return "window.urls.override=" + urlConfiguration.frontPropertiesToJson();
    }
}