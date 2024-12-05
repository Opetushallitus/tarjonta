package fi.vm.sade.tarjonta.service.resources;

import fi.vm.sade.tarjonta.service.resources.dto.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

@Path("/hakukohde")
public interface HakukohdeResource {

  @GET
  @Path("{oid}/kela")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  HakukohdeKelaDTO getHakukohdeKelaByOID(@PathParam("oid") String oid);

  @POST
  @Path("/tilastokeskus")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  List<HakukohdeKelaDTO> gatHakukohdeKelaDTOs(List<String> hakukohdeOids);

  @GET
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  public List<OidRDTO> search(
      @QueryParam("searchTerms") String searchTerms,
      @QueryParam("count") int count,
      @QueryParam("startIndex") int startIndex,
      @QueryParam("lastModifiedBefore") Date lastModifiedBefore,
      @QueryParam("lastModifiedSince") Date lastModifiedSince,
      @QueryParam("organisationOid") List<String> organisationOids,
      @QueryParam("hakukohdeTila") List<String> hakukohdeTilas);

  @GET
  @Path("{oid}")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  public HakukohdeDTO getByOID(@PathParam("oid") String oid);

  @GET
  @Path("{oid}/haku")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  public HakuDTO getHakuByHakukohdeOID(@PathParam("oid") String oid);

  @GET
  @Path("{oid}/komoto")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  public List<OidRDTO> getKomotosByHakukohdeOID(@PathParam("oid") String oid);

  @GET
  @Path("{oid}/paasykoe")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  public List<String> getPaasykoesByHakukohdeOID(@PathParam("oid") String oid);

  @GET
  @Path("{oid}/valintakoe")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  public List<ValintakoeRDTO> getValintakoesByHakukohdeOID(@PathParam("oid") String oid);

  @GET
  @Path("{oid}/nimi")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  public HakukohdeNimiRDTO getHakukohdeNimi(@PathParam("oid") String oid);

  @GET
  @Path("{oid}/valintaperusteet")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  public HakukohdeValintaperusteetDTO getHakukohdeValintaperusteet(@PathParam("oid") String oid);
}
