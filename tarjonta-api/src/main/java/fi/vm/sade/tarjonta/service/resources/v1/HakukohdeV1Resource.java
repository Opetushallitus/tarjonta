package fi.vm.sade.tarjonta.service.resources.v1;

import fi.vm.sade.tarjonta.service.resources.dto.NimiJaOidRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.*;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.Tilamuutokset;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;

@Path("/v1/hakukohde")
@Tag(name = "/v1/hakukohde", description = "Hakukohteen versio 1 operaatiot")
public interface HakukohdeV1Resource {

  @POST
  @Path("/{oid}/tila")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Operation(
      summary = "Päivittää hakukohteen tilan",
      description = "Operaatio päivittää hakukohteen tilan, mikäli kyseinen tilasiirtymä on sallittu.")
  public ResultV1RDTO<Tilamuutokset> updateTila(
      @PathParam("oid") String oid,
      @QueryParam("state") TarjontaTila tila,
      @Context HttpServletRequest request);

  @GET
  @Operation(
      summary = "Palauttaa kaikki hakukohteiden oid:t",
      description = "Listaa kaikki hakukohteiden oidit")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  public ResultV1RDTO<List<OidV1RDTO>> search();

  @GET
  @Path("/{oid}")
  @Operation(
      summary = "Palauttaa hakukohteen oid:lla",
          description = "Operaatio palauttaa versio 1 mukaisen hakukohteen")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  public ResultV1RDTO<HakukohdeV1RDTO> findByOid(
      @Parameter(name = "Hakukohteen oid", required = true) @PathParam("oid") String oid,
      @Parameter(
              name =
                  "Lisää hakukohteelle koulutuksen tutkintoonjohtavuuden, alkamiskauden ja -vuoden")
          @QueryParam("populateAdditionalKomotoFields")
          boolean populate);

  @GET
  @Path("/{oid}/valintaperusteet")
  @Operation(
      summary = "Palauttaa hakukohteen valintaperusteet",
      description = "Operaatio palauttaa hakukohteen valintaperusteet")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  public ResultV1RDTO<HakukohdeValintaperusteetV1RDTO> findValintaperusteetByOid(
      @Parameter(name = "Hakukohteen oid", required = true) @PathParam("oid") String oid);

  @GET
  @Path("/{tarjoajaOid}/{ulkoinenTunniste}")
  @Operation(summary = "Palauttaa hakukohteen ulkoisella tunnisteella")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  public ResultV1RDTO<HakukohdeV1RDTO> findByUlkoinenTunniste(
      @Parameter(name = "Tarjoajan oid", required = true) @PathParam("tarjoajaOid")
          String tarjoajaOid,
      @Parameter(name = "Ulkoinen tunniste", required = true) @PathParam("ulkoinenTunniste")
          String ulkoinenTunniste);

  @POST
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Operation(
      summary = "Luo uuden hakukohteen",
      description = "Operaatio luo uuden hakukohteen")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Operation successful"),
        @ApiResponse(responseCode = "400", description = "Invalid request payload"),
        @ApiResponse(responseCode = "401", description = "Unauthorized request"),
        @ApiResponse(responseCode = "403", description = "Permission denied")
      })
  public Response postHakukohde(
      @Parameter(name = "Luotava hakukohde", required = true) HakukohdeV1RDTO hakukohde,
      @Context HttpServletRequest request);

  @PUT
  @Path("/{oid}")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Operation(
      summary = "Päivittää hakukohteen",
      description = "Operaatio päivittää hakukohteen")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Operation successful"),
        @ApiResponse(responseCode = "400", description = "Invalid request payload"),
        @ApiResponse(responseCode = "401", description = "Unauthorized request"),
        @ApiResponse(responseCode = "403", description = "Permission denied")
      })
  public Response updateHakukohde(
      @Parameter(name = "Päivitettävän hakukohteen oid", required = true) @PathParam("oid")
          String hakukohdeOid,
      @Parameter(name = "Päivitetty hakukohde", required = true) HakukohdeV1RDTO hakukohde,
      @Context HttpServletRequest request);

  @DELETE
  @Path("/{oid}")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Operation(summary = "Poistaa hakukohteen", description = "Operaatio poistaa hakukohteen")
  public ResultV1RDTO<Boolean> deleteHakukohde(
      @Parameter(name = "Poistettavan hakukohteen oid", required = true) @PathParam("oid")
          String oid,
      @Context HttpServletRequest request);

  @GET
  @Path("/{oid}/valintakoe")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Operation(
      summary = "Listaa hakukohteen valintakokeet",
      description = "Operaatio listaa hakukohteen valintakokeet, parametrina annetaan hakukohteen oid")
  public ResultV1RDTO<List<ValintakoeV1RDTO>> findHakukohdeValintakoes(
      @Parameter(name = "Hakukohteen oid", required = true) @PathParam("oid") String hakukohdeOid);

  @POST
  @Path("/{oid}/valintakoe")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Operation(
      summary = "Lisää hakukohteelle valintakokeen",
      description =
          "Lisätään hakukohteelle valintakoe, parametrina annetaan hakukohteen oid ja payloadina valintakoe")
  public ResultV1RDTO<ValintakoeV1RDTO> insertValintakoe(
      @Parameter(name = "Hakukohteen oid", required = true) @PathParam("oid") String hakukohdeOid,
      @Parameter(name = "Hakukohteelle lisättävä valintakoe", required = true)
          ValintakoeV1RDTO valintakoeV1RDTO);

  @PUT
  @Path("/{oid}/valintakoe")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Operation(
      summary = "Päivittää hakukohteelle valintakokeen",
      description =
          "Päivittää hakukohteen valintakokeen, parametrina annetaan hakukohteen oid ja payloadina valintakoe")
  public ResultV1RDTO<ValintakoeV1RDTO> updateValintakoe(
      @Parameter(name = "Hakukohteen oid", required = true) @PathParam("oid") String hakukohdeOid,
      @Parameter(name = "Päivitettävä valintakoe", required = true)
          ValintakoeV1RDTO valintakoeV1RDTO);

  @DELETE
  @Path("/{oid}/valintakoe/{valintakoeId}")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Operation(
      summary = "Poistaa hakukohteelta valintakokeen",
      description = "Poistaa valintakokeen annetulta hakukohteelta.")
  public ResultV1RDTO<Boolean> removeValintakoe(
      @Parameter(name = "Hakukohteen oid", required = true) @PathParam("oid") String hakukohdeOid,
      @Parameter(name = "Poistettavan valintakokeen oid", required = true)
          @PathParam("valintakoeId")
          String valintakoeOid);

  @GET
  @Path("/{oid}/liite")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Operation(
      summary = "Listaa hakukohteen liitteet",
      description = "Operaatio listaa hakukohteen liitteet, parametrinä annetaan hakukohteen oid")
  public ResultV1RDTO<List<HakukohdeLiiteV1RDTO>> findHakukohdeLiites(
      @Parameter(name = "Hakukohteen oid", required = true) @PathParam("oid") String hakukohdeOid);

  @POST
  @Path("/{oid}/liite")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Operation(
      summary = "Lisää hakukohteelle liitteen",
      description =
          "Lisätään hakukohteelle liite, parametrina annetaan hakukohteen oid ja payloadina liite.")
  public ResultV1RDTO<HakukohdeLiiteV1RDTO> insertHakukohdeLiite(
      @Parameter(name = "Hakukohteen oid", required = true) @PathParam("oid") String hakukohdeOid,
      @Parameter(name = "Lisättävä hakukohteen liite", required = true)
          HakukohdeLiiteV1RDTO liiteV1RDTO);

  @PUT
  @Path("/{oid}/liite")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Operation(
      summary = "Päivittää hakukohteen liitteen",
      description = "Operaatio päivittää hakukohteen liitteen.")
  public ResultV1RDTO<HakukohdeLiiteV1RDTO> updateHakukohdeLiite(
      @Parameter(name = "Hakukohteen oid", required = true) @PathParam("oid") String hakukohdeOid,
      @Parameter(name = "Päivitettävä hakukohteen liite", required = true)
          HakukohdeLiiteV1RDTO liiteV1RDTO);

  @DELETE
  @Path("/{oid}/liite/{liiteId}")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Operation(
      summary = "Päivittää hakukohteen liitteen",
      description =
          "Päivitetään hakukohteelle liite, parametrina annetaan hakukohteen oid ja payloadina liite.")
  public ResultV1RDTO<Boolean> deleteHakukohdeLiite(
      @Parameter(name = "Hakukohteen oid", required = true) @PathParam("oid") String hakukohdeOid,
      @Parameter(name = "Poistettava hakukohteen liite", required = true) @PathParam("liiteId")
          String liiteId);

  @GET
  @Path("/{oid}/valintaperustekuvaus")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Operation(
      summary = "Hakukohteen valintaperusteet",
      description = "Listaa hakukohteen valintaperusteet, parametrinä annetaan hakukohteen oid.")
  public ResultV1RDTO<HashMap<String, String>> findHakukohdeValintaperusteet(
      @Parameter(name = "Hakukohteen oid", required = true) @PathParam("oid") String hakukohdeOid);

  @POST
  @Path("/{oid}/valintaperustekuvaus")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Operation(
      summary = "Lisää hakukohteelle valintaperusteet",
      description = "Lisää hakukohteelle valintaperusteet, poistaa mahdolliset vanhat.")
  public ResultV1RDTO<HashMap<String, String>> insertHakukohdeValintaPerusteet(
      @Parameter(name = "Hakukohteen oid", required = true) @PathParam("oid") String hakukohdeOid,
      @Parameter(name = "Lisättävät hakukohteen valintaperusteet", required = true)
          HashMap<String, String> valintaPerusteet);

  @GET
  @Path("/{oid}/sora")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Operation(
      summary = "Hakukohteen sora-kuvaukset",
      description = "Listaa hakukohteen sora-kuvaukset, parametrinä annetaan hakukohteen oid.")
  public ResultV1RDTO<HashMap<String, String>> findHakukohdeSoraKuvaukset(
      @Parameter(name = "Hakukohteen oid", required = true) @PathParam("oid") String hakukohdeOid);

  @POST
  @Path("/{oid}/sora")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Operation(
      summary = "Lisää hakukohteelle sora-kuvaukset",
      description = "Lisää hakukohteelle sora-kuvaukset, poistaa mahdolliset vanhat.")
  public ResultV1RDTO<HashMap<String, String>> insertHakukohdeSora(
      @Parameter(name = "Hakukohteen oid", required = true) @PathParam("oid") String hakukohdeOid,
      @Parameter(name = "Hakukohteelle lisättävät sora-kuvaukset", required = true)
          HashMap<String, String> sorat);

  @GET
  @Path("/search")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Operation(
      summary = "Palauttaa listan hakukohteista annetuilla parametreilla",
      description = "Palauttaa listan hakukohteista annetuilla parametreilla.")
  public ResultV1RDTO<HakutuloksetV1RDTO<HakukohdeHakutulosV1RDTO>> search(
      @Parameter(name = "Hakutermit", required = false) @QueryParam("searchTerms")
          String searchTerms,
      @Parameter(name = "Koodiston hakukohteet koodi URI", required = false)
          @QueryParam("hakukohteenNimiUri")
          String hakukohteenNimiUri,
      @Parameter(name = "Lista organisaatioiden oid:tä", required = false)
          @QueryParam("organisationOid")
          List<String> organisationOids,
      @Parameter(name = "Lista hakukohteen tiloja", required = false) @QueryParam("tila")
          List<String> hakukohdeTilas,
      @Parameter(name = "Alkamiskausi", required = false) @QueryParam("alkamisKausi")
          String alkamisKausi,
      @Parameter(name = "Alkamisvuosi", required = false) @QueryParam("alkamisVuosi")
          Integer alkamisVuosi,
      @Parameter(name = "Hakukohteen oid", required = false) @QueryParam("hakukohdeOid")
          String hakukohdeOid,
      @Parameter(name = "Lista koulutusasteen tyyppejä", required = false)
          @QueryParam("koulutusastetyyppi")
          List<KoulutusasteTyyppi> koulutusastetyyppi,
      @Parameter(name = "Haun oid", required = false) @QueryParam("hakuOid") String hakuOid,
      @Parameter(name = "Lista hakukohderyhmiä", required = false)
          @QueryParam("organisaatioRyhmaOid")
          List<String> organisaatioRyhmaOid,
      @Parameter(name = "Lista toteutustyyppejä", required = false) @QueryParam("koulutustyyppi")
          List<ToteutustyyppiEnum> koulutustyypit,
      @Parameter(name = "Lista koulutusmoduuli tyyppejä", required = false)
          @QueryParam("koulutusmoduuliTyyppi")
          List<KoulutusmoduuliTyyppi> koulutusmoduulityypit,
      @QueryParam("defaultTarjoaja") String defaultTarjoaja,
      @QueryParam("hakutapa") String hakutapa,
      @QueryParam("hakutyyppi") String hakutyyppi,
      @QueryParam("koulutuslaji") String koulutuslaji,
      @QueryParam("kohdejoukko") String kohdejoukko,
      @QueryParam("oppilaitostyyppi") String oppilaitostyyppi,
      @QueryParam("kunta") String kunta,
      @QueryParam("opetuskielet") List<String> opetuskielet,
      @QueryParam("koulutusOid") List<String> koulutusOids,
      @QueryParam("offset") Integer offset,
      @QueryParam("limit") Integer limit,
      @Context HttpServletRequest request);

  @GET
  @Path("/{oid}/koulutukset")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Operation(
      summary = "Palauttaa hakukohteen koulutukset",
      description = "Operaatio palauttaa listan hakukohteen koulutuksia.")
  public ResultV1RDTO<List<NimiJaOidRDTO>> getKoulutukset(
      @Parameter(name = "Hakukohteen oid", required = true) @PathParam("oid") String oid);

  @POST
  @Path("/{oid}/koulutukset")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Operation(
      summary = "Poistaa annetun hakukohteen ja koulutusten välisen relaation",
      description =
          "Poistaa annetun hakukohteen ja koulutusten välisen relaation, huom. mikäli hakukohteelle ei jää yhtään koulutusrelaatiota se poistetaan")
  public ResultV1RDTO<List<String>> removeKoulutuksesFromHakukohde(
      @Parameter(name = "Hakukohteen oid", required = true) @PathParam("oid") String hakukohdeOid,
      @Parameter(name = "Lista hakukohteelta poistettavista koulutuksista")
          List<KoulutusTarjoajaV1RDTO> koulutukses,
      @Context HttpServletRequest request);

  @POST
  @Path("/{oid}/koulutukset/lisaa")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Operation(summary = "Liittää annetut koulutukset hakukohteelle")
  public ResultV1RDTO<List<String>> lisaaKoulutuksesToHakukohde(
      @Parameter(name = "Hakukohteen oid jolle koulutukset liitetään", required = true)
          @PathParam("oid")
          String hakukohdeOid,
      @Parameter(name = "Koulutusten tiedot jotka liitetään hakukohteelle", required = true)
          List<KoulutusTarjoajaV1RDTO> koulutukses,
      HttpServletRequest request);

  @GET
  @Path("/{oid}/stateChangeCheck")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Operation(summary = "Tutkii onko esitetty tilamuutos mahdollinen")
  public ResultV1RDTO<Boolean> isStateChangePossible(
      @PathParam("oid") String oid, @QueryParam("state") TarjontaTila tila);

  @POST
  @Path("/ryhmat/operate")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Operation(
      summary = "Liittää/poistaa annetut ryhmat hakukohteelle, ei muuta olemassaolevia liitoksia")
  public ResultV1RDTO<Boolean> lisaaRyhmatHakukohteille(
      @Parameter(name = "Lista hakukohteiden liittamis/poistamis toimintoja", required = true)
          List<HakukohdeRyhmaV1RDTO> data,
      @Context HttpServletRequest request);

  @GET
  @Path("/komotoSelectedCheck")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Operation(
      summary = "Tutkii onko valituista koulutuksista mahdollista luoda uusi hakukohde")
  public ResultV1RDTO<ValitutKoulutuksetV1RDTO> isValidKoulutusSelection(
      @QueryParam("oid") List<String> oids);

  @POST
  @Path("/updateValintakokeetToNewStructure")
  public void updateValintakokeetToNewStructure();

  @POST
  @Path("/updateLiitteetToNewStructure")
  public void updateLiitteetToNewStructure();

  @GET
  @Path("/findHakukohdesByKuvausId/{id}")
  @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
  @Operation(
      summary = "Palauttaa hakukohteet, jotka on liittetty annettuun valintaperuste/SORA -kuvaukseen")
  public ResultV1RDTO<List<HakukohdeV1RDTO>> findHakukohdesByKuvausId(@PathParam("id") Long id);
}
