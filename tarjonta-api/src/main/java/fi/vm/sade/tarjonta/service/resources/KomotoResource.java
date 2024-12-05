package fi.vm.sade.tarjonta.service.resources;

import fi.vm.sade.tarjonta.service.resources.dto.KomoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KomotoDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

@Path("/komoto")
public interface KomotoResource {

  @GET
  @Path("/hello")
  @Produces(MediaType.TEXT_PLAIN)
  public String hello();

  @GET
  @Path("{oid}")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  public KomotoDTO getByOID(@PathParam("oid") String oid);

  @GET
  @Path("{oid}/komo")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  public KomoDTO getKomoByKomotoOID(@PathParam("oid") String oid);

  @GET
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  public List<OidRDTO> search(
      @QueryParam("searchTerms") String searchTerms,
      @QueryParam("count") int count,
      @QueryParam("startIndex") int startIndex,
      @QueryParam("lastModifiedBefore") Date lastModifiedBefore,
      @QueryParam("lastModifiedSince") Date lastModifiedSince);

  @GET
  @Path("{oid}/hakukohde")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  public List<OidRDTO> getHakukohdesByKomotoOID(@PathParam("oid") String oid);
}
