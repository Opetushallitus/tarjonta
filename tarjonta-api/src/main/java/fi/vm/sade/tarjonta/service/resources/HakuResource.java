package fi.vm.sade.tarjonta.service.resources;

import fi.vm.sade.tarjonta.service.resources.dto.HakuDTO;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeTulosRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OidRDTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Path("/haku")
public interface HakuResource {

  @GET
  @Path("/hello")
  @Produces(MediaType.TEXT_PLAIN)
  public String hello();

  @GET
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  public List<OidRDTO> search(
      @QueryParam("searchTerms") String searchTerms,
      @QueryParam("count") int count,
      @QueryParam("startIndex") int startIndex,
      @QueryParam("lastModifiedBefore") Date lastModifiedBefore,
      @QueryParam("lastModifiedSince") Date lastModifiedSince);

  @GET
  @Path("/findAll")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  public List<HakuDTO> findAllHakus();

  @GET
  @Path("{oid}")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  public HakuDTO getByOID(@PathParam("oid") String oid);

  @GET
  @Path("{oid}/hakukohde")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  public List<OidRDTO> getByOIDHakukohde(
      @PathParam("oid") String oid,
      @QueryParam("searchTerms") String searchTerms,
      @QueryParam("count") int count,
      @QueryParam("startIndex") int startIndex,
      @QueryParam("lastModifiedBefore") Date lastModifiedBefore,
      @QueryParam("lastModifiedSince") Date lastModifiedSince,
      @QueryParam("organisationOids") String organisationOidsStr,
      @QueryParam("hakukohdeTilas") String hakukohdeTilasStr);

  @GET
  @Path("{oid}/hakukohdeTulos")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  public HakukohdeTulosRDTO getByOIDHakukohdeTulos(
      @PathParam("oid") String oid,
      @QueryParam("searchTerms") String searchTerms,
      @QueryParam("count") int count,
      @QueryParam("startIndex") int startIndex,
      @QueryParam("lastModifiedBefore") Date lastModifiedBefore,
      @QueryParam("lastModifiedSince") Date lastModifiedSince,
      @QueryParam("organisationOids") String organisationOidsStr,
      @QueryParam("hakukohdeTilas") String hakukohdeTilasStr,
      @QueryParam("alkamisVuosi") Integer alkamisVuosi,
      @QueryParam("alkamisKausi") String alkamisKausi);

  @GET
  @Path("{oid}/hakukohdeWithName")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  public List<Map<String, String>> getByOIDHakukohdeExtra(
      @PathParam("oid") String oid,
      @QueryParam("searchTerms") String searchTerms,
      @QueryParam("count") int count,
      @QueryParam("startIndex") int startIndex,
      @QueryParam("lastModifiedBefore") Date lastModifiedBefore,
      @QueryParam("lastModifiedSince") Date lastModifiedSince,
      @QueryParam("organisationOids") String organisationOidsStr,
      @QueryParam("hakukohdeTilas") String hakukohdeTilasStr);

  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  public String createHaku(HakuDTO dto);

  @PUT
  @Path("{oid}")
  @Consumes(MediaType.APPLICATION_JSON)
  public void replaceHaku(HakuDTO dto);

  @DELETE
  @Path("{oid}")
  public void deleteHaku(@PathParam("oid") String hakuOid);

  @PUT
  @Path("{oid}/state")
  @Consumes(MediaType.TEXT_PLAIN)
  public void updateHakuState(@PathParam("oid") String hakuOid, String state);
}
