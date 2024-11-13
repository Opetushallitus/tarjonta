package fi.vm.sade.tarjonta.service.resources;

import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

@Path("/komo")
public interface KomoResource {

  @GET
  @Path("/hello")
  @Produces(MediaType.TEXT_PLAIN)
  public String hello();

  @GET
  @Path("{oid}")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  public KomoDTO getByOID(@PathParam("oid") String oid);

  @GET
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  public List<OidRDTO> search(
      @QueryParam("searchTerms") String searchTerms,
      @QueryParam("count") int count,
      @QueryParam("startIndex") int startIndex,
      @QueryParam("lastModifiedBefore") Date lastModifiedBefore,
      @QueryParam("lastModifiedSince") Date lastModifiedSince);

  @GET
  @Path("{oid}/komoto")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  public List<OidRDTO> getKomotosByKomoOID(
      @PathParam("oid") String oid,
      @QueryParam("count") int count,
      @QueryParam("startIndex") int startIndex);
}
