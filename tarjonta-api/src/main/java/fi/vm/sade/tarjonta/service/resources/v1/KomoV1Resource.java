package fi.vm.sade.tarjonta.service.resources.v1;

import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KomoV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KuvausV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.ModuuliTuloksetV1RDTO;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/v1/komo")
@Api(value = "/v1/komo", description = "KOulutusmoduulin versio 1 operaatiot")
public interface KomoV1Resource {

  @DELETE
  @Path("/{oid}")
  @ApiOperation(
      value = "Poistaa koulutusmoduulin annetulla koulutusmoduulin oid:lla",
      notes = "Operaatio poistaa koulutusmoduulin annetulla koulutusmoduulin oid:lla")
  public ResultV1RDTO deleteByOid(@PathParam("oid") String oid);

  @POST
  @Path("/")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @ApiOperation(
      value = "Luo tai päivittää yksittäisen koulutusmoduulin",
      notes = "Operaatio luo tai päivittää yksittäisen koulutusmoduulin",
      response = KomoV1RDTO.class)
  public ResultV1RDTO<KomoV1RDTO> postKomo(KomoV1RDTO dto);

  @GET
  @Path("/{oid}")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @ApiOperation(
      value = "Näyttää yhden koulutusmoduulin annetulla oid:lla",
      notes =
          "Operaatio näyttää yhden koulutusmoduulin annetulla oid:lla."
              + " Muut parametrit : "
              + "1. meta=false poistaa koodisto-palvelun metatietoa haettavaan koulutuksen dataan. "
              + "2. lang=FI näyttää yksittäisen metadatan annetun kielikoodin mukaan.",
      response = KomoV1RDTO.class)
  public ResultV1RDTO<KomoV1RDTO> findKomoByOid(
      @PathParam("oid") String oid,
      @QueryParam("meta") Boolean meta,
      @QueryParam("lang") String lang);

  @GET
  @Path("/search")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @ApiOperation(
      value = "Näyttää koulutusmoduulien tulosjoukon annetuilla parametreillä",
      notes = "Operaatio näyttää koulutusmoduulien tulosjoukon annetuilla parametreillä",
      response = ResultV1RDTO.class)
  public ResultV1RDTO<List<KomoV1RDTO>> searchInfo(
      @QueryParam("koulutus") String koulutuskoodi,
      @QueryParam("meta") Boolean meta,
      @QueryParam("lang") String lang);

  @GET
  @Path("/search/{koulutustyyppi}")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @ApiOperation(
      value = "Näyttää supistetun koulutusmoduulien tulosjoukon annetuilla parametreillä",
      notes = "Operaatio näyttää supistetun koulutusmoduulien tulosjoukon annetuilla parametreillä",
      response = ResultV1RDTO.class)
  public ResultV1RDTO<List<ModuuliTuloksetV1RDTO>> searchModule(
      @PathParam("koulutustyyppi") ToteutustyyppiEnum koulutustyyppiUri,
      @QueryParam("koulutus") String koulutuskoodiUri,
      @QueryParam("ohjelma") String ohjelmaUri,
      @QueryParam("tila") String tila);

  @GET
  @Path("/search/{koulutustyyppi}/{moduuli}")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @ApiOperation(
      value = "Näyttää supistetun koulutusmoduulien tulosjoukon annetuilla parametreillä",
      notes = "Operaatio näyttää supistetun koulutusmoduulien tulosjoukon annetuilla parametreillä",
      response = ResultV1RDTO.class)
  public ResultV1RDTO<List<ModuuliTuloksetV1RDTO>> searchModule(
      @PathParam("koulutustyyppi") ToteutustyyppiEnum koulutustyyppiUri,
      @PathParam("moduuli") KoulutusmoduuliTyyppi koulutusmoduuliTyyppi,
      @QueryParam("koulutus") String koulutuskoodiUri,
      @QueryParam("ohjelma") String ohjelmaUri,
      @QueryParam("tila") String tila);

  @GET
  @Path("/{oid}/tekstis")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @ApiOperation(
      value = "Näyttää koulutusmoduulin monikieliset tekstit",
      notes = "Operaatio näyttää koulutusmoduulin monikieliset tekstit",
      response = ResultV1RDTO.class)
  public ResultV1RDTO<KuvausV1RDTO> loadKomoTekstis(@PathParam("oid") String oid);

  @POST
  @Path("/{oid}/tekstis")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @ApiOperation(
      value = "Lisää koulutusmoduuliin monikielisen kuvaustekstin",
      notes = "Operaatio lisää koulutusmoduuliin monikielisen kuvaustekstin")
  public ResultV1RDTO saveKomoTekstis(@PathParam("oid") String oid, KuvausV1RDTO<KomoTeksti> dto);

  @POST
  @Path("/import/{koulutus}")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @ApiOperation(
      value =
          "Luo uuden ryhman tai lisaa puuttuvia moduuleita, seka liitää ne hierarkisesti yhteen. Toiminto ei poista eika paivita olemassa olevia moduuleita.",
      notes =
          "Operaatio luo uuden ryhman tai lisaa puuttuvia moduuleita, seka liitää ne hierarkisesti yhteen. Toiminto ei poista eika paivita olemassa olevia moduuleita.",
      response = KomoV1RDTO.class)
  public ResultV1RDTO<List<ModuuliTuloksetV1RDTO>> importModuleGroupByKoulutusUri(
      @PathParam("koulutus") String koulutusUri, List<KomoV1RDTO> dtos);
}
