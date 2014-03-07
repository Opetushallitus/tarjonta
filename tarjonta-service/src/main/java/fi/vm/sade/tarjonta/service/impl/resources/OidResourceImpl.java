package fi.vm.sade.tarjonta.service.impl.resources;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;
import com.google.gson.Gson;

import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;

/**
 * A temporary resource to support vaadin ui to generate oids. do not use with
 * anything new!!! Normally tarjonta service should generate oids.
 */
@Path("/oid")
public class OidResourceImpl {

    @Autowired
    OidService oidService;

    @GET
    public Response getOid(@QueryParam("type") TarjontaOidType type) {
        if(type==null){
            return Response.status(Status.BAD_REQUEST).entity("type is required").build();
        }
        try {
            Map<String, String> oid = Maps.newHashMap();
            oid.put("oid", oidService.get(type));
            return Response.ok(new Gson().toJson(oid)).build();
        } catch (OIDCreationException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build();
        }
    }

}
