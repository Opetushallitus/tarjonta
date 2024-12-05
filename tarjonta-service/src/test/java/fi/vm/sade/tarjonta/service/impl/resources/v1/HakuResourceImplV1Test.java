package fi.vm.sade.tarjonta.service.impl.resources.v1;

import static fi.vm.sade.tarjonta.service.impl.resources.v1.HakuResourceImplV1.getCriteriaListFromParams;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.TestMockBase;
import fi.vm.sade.tarjonta.helpers.KoodistoHelper;
import fi.vm.sade.tarjonta.matchers.KoodistoCriteriaMatcher;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.service.auditlog.AuditHelper;
import fi.vm.sade.tarjonta.service.impl.resources.v1.util.AutoRefreshableCache;
import fi.vm.sade.tarjonta.service.impl.resources.v1.util.YhdenPaikanSaantoBuilder;
import fi.vm.sade.tarjonta.service.resources.v1.HakuSearchCriteria;
import fi.vm.sade.tarjonta.service.resources.v1.dto.AtaruLomakeHakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.AtaruLomakkeetV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuSearchParamsV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuaikaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeTulosV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.search.HakukohdePerustieto;
import fi.vm.sade.tarjonta.service.search.HakukohdeSearchService;
import fi.vm.sade.tarjonta.service.search.HakukohteetKysely;
import fi.vm.sade.tarjonta.service.search.HakukohteetVastaus;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

public class HakuResourceImplV1Test extends TestMockBase {

  private KoodistoHelper koodistoHelper = new KoodistoHelper();
  private ConverterV1 realConverter = new ConverterV1();

  @InjectMocks private YhdenPaikanSaantoBuilder yhdenPaikanSaantoBuilder;

  private TarjontaKoodistoHelper tarjontaKoodistoHelper = mock(TarjontaKoodistoHelper.class);

  @InjectMocks private HakuResourceImplV1 hakuResource = new HakuResourceImplV1();

  @Mock private HakukohdeSearchService hakukohdeSearchService;

  @Rule public ExpectedException nonUniqueKoulutuksenAlkamiskaudet = ExpectedException.none();

  @Mock private UriInfo uriInfo;
  private MultivaluedMap<String, String> queryParams = new MultivaluedHashMap();
  private AuditHelper audithelper = mock(AuditHelper.class);
  private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

  @Before
  public void setUp() throws Exception {
    hakuResource.hakuCache = mock(AutoRefreshableCache.class);
    when(oidService.get(TarjontaOidType.HAKU)).thenReturn("1.2.3.4.5");

    when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("kieli_fi")))
        .thenReturn(koodistoHelper.getKoodiTypes("FI"));
    when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("kausi_k")))
        .thenReturn(koodistoHelper.getKoodiTypes("K"));
    when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("hakutapa_01")))
        .thenReturn(koodistoHelper.getKoodiTypes("01"));
    when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("hakutyyppi_01")))
        .thenReturn(koodistoHelper.getKoodiTypes("01"));
    when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("haunkohdejoukko_12")))
        .thenReturn(koodistoHelper.getKoodiTypes("12"));

    Haku haku = new Haku();
    haku.setOid("haku1");
    when(converterV1.convertHakuV1DRDTOToHaku(any(HakuV1RDTO.class), any(Haku.class)))
        .thenReturn(haku);
    HakuV1RDTO hakuV1RDTO = new HakuV1RDTO();
    hakuV1RDTO.setOid("haku1");
    when(audithelper.getHakuAsDto(any(Haku.class))).thenReturn(hakuV1RDTO);
    Whitebox.setInternalState(hakuResource, "auditHelper", audithelper);

    KoodistoURI.KOODISTO_TUTKINTOON_JOHTAVA_KOULUTUS_URI = "tutkintoonjohtava";
    KoodistoURI.KOODI_ON_TUTKINTO_URI = "tutkintoonjohtava_1";
    KoodistoURI.KOODI_VARSINAINEN_HAKU_URI = "hakutyyppi_01#1";
    KoodistoURI.KOODI_ERILLISHAKU_URI = "hakutapa_02#1";
    KoodistoURI.KOODI_KOHDEJOUKKO_AMMATILLINEN_LUKIO_URI = "haunkohdejoukko_11#1";
    KoodistoURI.KOODI_KOHDEJOUKKO_VALMISTAVA_URI = "haunkohdejoukko_17#1";
    KoodistoURI.KOODI_KOHDEJOUKKO_ERITYISOPETUKSENA_URI = "haunkohdejoukko_20#1";
    when(tarjontaKoodistoHelper.getUniqueKoodistoRelation(
            "koulutusUri1",
            KoodistoURI.KOODISTO_TUTKINTOON_JOHTAVA_KOULUTUS_URI,
            SuhteenTyyppiType.SISALTYY,
            false))
        .thenReturn(KoodistoURI.KOODI_ON_TUTKINTO_URI);
    when(tarjontaKoodistoHelper.getUniqueKoodistoRelation(
            "koulutusUri2",
            KoodistoURI.KOODISTO_TUTKINTOON_JOHTAVA_KOULUTUS_URI,
            SuhteenTyyppiType.SISALTYY,
            false))
        .thenReturn(KoodistoURI.KOODI_ON_TUTKINTO_URI);
    when(tarjontaKoodistoHelper.getUniqueKoodistoRelation(
            "koulutusUri3",
            KoodistoURI.KOODISTO_TUTKINTOON_JOHTAVA_KOULUTUS_URI,
            SuhteenTyyppiType.SISALTYY,
            false))
        .thenReturn("jotain-muuta");
    Whitebox.setInternalState(
        yhdenPaikanSaantoBuilder, "tarjontaKoodistoHelper", tarjontaKoodistoHelper);

    when(uriInfo.getQueryParameters()).thenReturn(queryParams);
    when(uriInfo.getQueryParameters(anyBoolean())).thenReturn(queryParams);
  }

  @Test(expected = NullPointerException.class)
  public void thatEmptyHakuIsNotCreated() {
    hakuResource.createHaku(null, request);
  }

  @Test
  public void thatHakuWithOidIsNotCreated() {
    HakuV1RDTO hakuDTO = new HakuV1RDTO();
    hakuDTO.setOid("1.2.3");

    ResultV1RDTO<HakuV1RDTO> result = hakuResource.createHaku(hakuDTO, request);

    assertTrue(result.hasErrors());
    assertEquals(ResultV1RDTO.ResultStatus.ERROR, result.getStatus());
  }

  @Test
  public void thatHakuIsCreated() {
    HakuV1RDTO hakuDTO = new HakuV1RDTO();
    ResultV1RDTO<HakuV1RDTO> result = hakuResource.createHaku(hakuDTO, request);
    assertNotNull(result);
    assertNotNull(result.getStatus());
    assertEquals(ResultV1RDTO.ResultStatus.ERROR, result.getStatus());

    hakuDTO = new HakuV1RDTO();
    hakuDTO.setHakukausiUri("kausi_k");
    hakuDTO.setHakutapaUri("hakutapa_01#1");
    hakuDTO.setHakutyyppiUri("hakutyyppi_01#1");
    hakuDTO.setKohdejoukkoUri("haunkohdejoukko_12#1");
    hakuDTO.setKoulutuksenAlkamiskausiUri("kausi_k");
    hakuDTO.setMaxHakukohdes(42);
    hakuDTO.getNimi().put("kieli_fi", "Nimi suomi");
    hakuDTO.getHakuaikas().add(createHakuaika(new Date(), new Date()));
    hakuDTO.setCanSubmitMultipleApplications(false);

    result = hakuResource.createHaku(hakuDTO, request);

    assertNotNull(result);
    assertNotNull(result.getStatus());
    assertEquals(ResultV1RDTO.ResultStatus.OK, result.getStatus());
  }

  @Test
  public void testHakuMaksumuuriAndTunnistus() {
    boolean maksumuuri = false;
    boolean tunnistus = false;

    Haku haku = new Haku();
    haku.setOid("1.2.3.4.5");
    haku.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO);
    haku.setKohdejoukkoUri("haunkohdejoukko_12#");
    haku.setKohdejoukonTarkenne("");
    haku.setHakutyyppiUri("");
    haku.setTila(TarjontaTila.VALMIS);
    haku.setHakukausiVuosi(2019);
    haku.setHakukausiUri("s#1");
    haku.setTunnistusKaytossa(true);

    haku.setKoulutuksenAlkamisVuosi(2016);
    haku.setKoulutuksenAlkamiskausiUri("kausi_k#1");
    HakuV1RDTO hakuV1RDTO = realConverter.fromHakuToHakuRDTO(haku, false);
    maksumuuri = hakuV1RDTO.isMaksumuuriKaytossa();
    tunnistus = hakuV1RDTO.isTunnistusKaytossa();
    assertTrue(tunnistus);
    assertFalse(maksumuuri);

    haku.setKoulutuksenAlkamisVuosi(2016);
    haku.setKoulutuksenAlkamiskausiUri("kausi_s#1");
    hakuV1RDTO = realConverter.fromHakuToHakuRDTO(haku, false);
    maksumuuri = hakuV1RDTO.isMaksumuuriKaytossa();
    assertTrue(maksumuuri);

    haku.setKoulutuksenAlkamisVuosi(2017);
    haku.setKoulutuksenAlkamiskausiUri("kausi_k#1");
    haku.setTunnistusKaytossa(false);
    hakuV1RDTO = realConverter.fromHakuToHakuRDTO(haku, false);
    maksumuuri = hakuV1RDTO.isMaksumuuriKaytossa();
    tunnistus = hakuV1RDTO.isTunnistusKaytossa();
    assertFalse(tunnistus);
    assertTrue(maksumuuri);

    haku.setKoulutuksenAlkamisVuosi(2017);
    haku.setKoulutuksenAlkamiskausiUri("kausi_k#1");
    haku.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.OPINTOJAKSO);
    hakuV1RDTO = realConverter.fromHakuToHakuRDTO(haku, false);
    maksumuuri = hakuV1RDTO.isMaksumuuriKaytossa();
    assertFalse(maksumuuri);

    haku.setKoulutuksenAlkamisVuosi(2017);
    haku.setKoulutuksenAlkamiskausiUri("kausi_s#1");
    hakuV1RDTO = realConverter.fromHakuToHakuRDTO(haku, false);
    maksumuuri = hakuV1RDTO.isMaksumuuriKaytossa();
    assertFalse(maksumuuri);
  }

  @Test
  public void thatHakuAndHakukohdeSingleStudyPlaceIsResolved() {
    Haku haku = new Haku();
    Hakukohde hakukohde = new Hakukohde();
    hakukohde.setOid("hakukohdeOid");
    hakukohde.setHaku(haku);
    haku.setKoulutuksenAlkamisVuosi(2016);
    haku.setKoulutuksenAlkamiskausiUri("kausi_s#1");
    haku.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO);

    haku.setKohdejoukkoUri("haunkohdejoukko_10#1");
    assertEquals(false, yhdenPaikanSaantoBuilder.from(hakukohde).isVoimassa());
    assertEquals(
        "Ei korkeakouluhaku ja hakukohde ei kuulu jatkuvaan korkeakouluhakuun, jonka kohdejoukon tarkenne kuuluu joukkoon [haunkohdejoukontarkenne_3#] tai sitä ei ole",
        yhdenPaikanSaantoBuilder.from(hakukohde).getSyy());

    haku.setKohdejoukkoUri("haunkohdejoukko_12#1");
    haku.setKohdejoukonTarkenne("");
    assertEquals(true, yhdenPaikanSaantoBuilder.from(hakukohde).isVoimassa());
    assertEquals(
        "Korkeakouluhaku ilman kohdejoukon tarkennetta",
        yhdenPaikanSaantoBuilder.from(hakukohde).getSyy());

    haku.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.OPINTOKOKONAISUUS);
    assertEquals(false, yhdenPaikanSaantoBuilder.from(hakukohde).isVoimassa());
    assertEquals(
        "Haun koulutukset eivät ole tutkintoon johtavaa ja hakukohde ei kuulu jatkuvaan korkeakouluhakuun, jonka kohdejoukon tarkenne kuuluu joukkoon [haunkohdejoukontarkenne_3#] tai sitä ei ole",
        yhdenPaikanSaantoBuilder.from(hakukohde).getSyy());
    haku.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO);

    haku.setKohdejoukonTarkenne("haunkohdejoukontarkenne_3#1");
    assertEquals(true, yhdenPaikanSaantoBuilder.from(hakukohde).isVoimassa());
    assertThat(
        yhdenPaikanSaantoBuilder.from(hakukohde).getSyy(),
        containsString("Haun kohdejoukon tarkenne on"));

    haku.setKohdejoukonTarkenne("haunkohdejoukontarkenne_4#1");
    assertEquals(false, yhdenPaikanSaantoBuilder.from(hakukohde).isVoimassa());
    assertThat(
        yhdenPaikanSaantoBuilder.from(hakukohde).getSyy(),
        containsString("Haulla on kohdejoukon tarkenne, joka ei ole"));

    haku.setKoulutuksenAlkamisVuosi(2016);
    haku.setKoulutuksenAlkamiskausiUri("kausi_k#1");
    assertEquals(false, yhdenPaikanSaantoBuilder.from(hakukohde).isVoimassa());
    assertThat(
        yhdenPaikanSaantoBuilder.from(hakukohde).getSyy(),
        containsString("Haun koulutuksen alkamiskausi on ennen syksyä 2016"));

    haku.setKoulutuksenAlkamisVuosi(2015);
    haku.setKoulutuksenAlkamiskausiUri("kausi_s#1");
    assertEquals(false, yhdenPaikanSaantoBuilder.from(hakukohde).isVoimassa());
    assertThat(
        yhdenPaikanSaantoBuilder.from(hakukohde).getSyy(),
        containsString("Haun koulutuksen alkamiskausi on ennen syksyä 2016"));

    haku.setHakutapaUri("hakutapa_03");
    haku.setKohdejoukonTarkenne("haunkohdejoukontarkenne_3#1");
    assertEquals(false, yhdenPaikanSaantoBuilder.from(hakukohde).isVoimassa());
    assertThat(
        yhdenPaikanSaantoBuilder.from(hakukohde).getSyy(),
        containsString("hakukohteen koulutus ei ole tutkintoon johtavaa"));

    KoulutusmoduuliToteutus komoto1 = new KoulutusmoduuliToteutus();
    komoto1.setOid("komotoOid1");
    komoto1.setTila(TarjontaTila.JULKAISTU);
    komoto1.setAlkamisVuosi(2010);
    komoto1.setAlkamiskausiUri("kausi_k");
    komoto1.setKoulutusUri("koulutusUri1");
    KoulutusmoduuliToteutus komoto2 = new KoulutusmoduuliToteutus();
    komoto2.setOid("komotoOid2");
    komoto2.setTila(TarjontaTila.JULKAISTU);
    komoto2.setAlkamisVuosi(2016);
    komoto2.setAlkamiskausiUri("kausi_k");
    komoto2.setKoulutusUri("koulutusUri2");
    hakukohde.setKoulutusmoduuliToteutuses(new HashSet<>(Arrays.asList(komoto1, komoto2)));

    hakukohde.setKoulutusmoduuliToteutuses(new HashSet<>(Arrays.asList(komoto2)));
    assertEquals(false, yhdenPaikanSaantoBuilder.from(hakukohde).isVoimassa());
    assertThat(
        yhdenPaikanSaantoBuilder.from(hakukohde).getSyy(),
        containsString("hakukohteen koulutuksen alkamiskausi on ennen syksyä 2016"));

    KoulutusmoduuliToteutus komoto3 = new KoulutusmoduuliToteutus();
    komoto3.setTila(TarjontaTila.JULKAISTU);
    komoto3.setAlkamisVuosi(2016);
    komoto3.setAlkamiskausiUri("kausi_s");
    komoto3.setKoulutusUri("koulutusUri3");
    hakukohde.setKoulutusmoduuliToteutuses(new HashSet<>(Arrays.asList(komoto3)));

    when(tarjontaKoodistoHelper.getUniqueKoodistoRelation(
            "koulutusUri3",
            KoodistoURI.KOODISTO_TUTKINTOON_JOHTAVA_KOULUTUS_URI,
            SuhteenTyyppiType.SISALTYY,
            false))
        .thenReturn(KoodistoURI.KOODI_ON_TUTKINTO_URI);

    assertEquals(true, yhdenPaikanSaantoBuilder.from(hakukohde).isVoimassa());
    assertThat(
        yhdenPaikanSaantoBuilder.from(hakukohde).getSyy(),
        containsString("Jatkuvan haun hakukohteen alkamiskausi ja vuosi on jälkeen kevään 2016"));

    haku.setKohdejoukonTarkenne("haunkohdejoukontarkenne_4#1");
    assertEquals(false, yhdenPaikanSaantoBuilder.from(hakukohde).isVoimassa());
  }

  @Test
  public void thatKoulutustenAlkamiskaudetMustBeUnique() {
    Haku haku = new Haku();
    Hakukohde hakukohde = new Hakukohde();
    hakukohde.setOid("hakukohdeOid");
    hakukohde.setHaku(haku);
    haku.setKoulutuksenAlkamisVuosi(2015);
    haku.setKoulutuksenAlkamiskausiUri("kausi_s#1");
    haku.setHakutapaUri("hakutapa_03");
    haku.setKohdejoukkoUri("haunkohdejoukko_12#1");
    haku.setKohdejoukonTarkenne("haunkohdejoukontarkenne_3#1");
    KoulutusmoduuliToteutus komoto1 = new KoulutusmoduuliToteutus();
    komoto1.setOid("komotoOid1");
    komoto1.setTila(TarjontaTila.JULKAISTU);
    komoto1.setAlkamisVuosi(2010);
    komoto1.setAlkamiskausiUri("kausi_k");
    komoto1.setKoulutusUri("koulutusUri1");
    KoulutusmoduuliToteutus komoto2 = new KoulutusmoduuliToteutus();
    komoto2.setOid("komotoOid2");
    komoto2.setTila(TarjontaTila.JULKAISTU);
    komoto2.setAlkamisVuosi(2016);
    komoto2.setAlkamiskausiUri("kausi_k");
    komoto2.setKoulutusUri("koulutusUri2");
    hakukohde.setKoulutusmoduuliToteutuses(new HashSet<>(Arrays.asList(komoto1, komoto2)));

    nonUniqueKoulutuksenAlkamiskaudet.expect(IllegalStateException.class);
    yhdenPaikanSaantoBuilder.from(hakukohde);
  }

  @Test
  public void thatOneTutkintoonJohtavaKoulutusMakesHakukohdeTutkintoonJohtava() {
    Haku haku = new Haku();
    Hakukohde hakukohde = new Hakukohde();
    hakukohde.setOid("hakukohdeOid");
    hakukohde.setHaku(haku);
    haku.setKoulutuksenAlkamisVuosi(2015);
    haku.setKoulutuksenAlkamiskausiUri("kausi_s#1");
    haku.setHakutapaUri("hakutapa_03");
    haku.setKohdejoukkoUri("haunkohdejoukko_12#1");
    haku.setKohdejoukonTarkenne("haunkohdejoukontarkenne_3#1");
    KoulutusmoduuliToteutus komoto1 = new KoulutusmoduuliToteutus();
    komoto1.setOid("komotoOid1");
    komoto1.setTila(TarjontaTila.JULKAISTU);
    komoto1.setAlkamisVuosi(2017);
    komoto1.setAlkamiskausiUri("kausi_k");
    komoto1.setKoulutusUri("koulutusUri3");
    KoulutusmoduuliToteutus komoto2 = new KoulutusmoduuliToteutus();
    komoto2.setOid("komotoOid2");
    komoto2.setTila(TarjontaTila.JULKAISTU);
    komoto2.setAlkamisVuosi(2017);
    komoto2.setAlkamiskausiUri("kausi_k");
    komoto2.setKoulutusUri("koulutusUri3");
    hakukohde.setKoulutusmoduuliToteutuses(new HashSet<>(Arrays.asList(komoto1, komoto2)));

    assertFalse(yhdenPaikanSaantoBuilder.from(hakukohde).isVoimassa());
    assertThat(
        yhdenPaikanSaantoBuilder.from(hakukohde).getSyy(),
        containsString("hakukohteen koulutus ei ole tutkintoon johtavaa"));

    komoto1.setKoulutusUri("koulutusUri1");
    assertTrue(yhdenPaikanSaantoBuilder.from(hakukohde).isVoimassa());
  }

  @Test
  public void thatHakuIsCreatedWithValidAtaruLomakeAvain() {
    List<String> validAtaruLomakeAvainList = new ArrayList<>();
    validAtaruLomakeAvainList.add("01234567-89ab-cdef-0123-4567890abcdef");

    for (String validAtaruLomakeAvain : validAtaruLomakeAvainList) {
      ResultV1RDTO<HakuV1RDTO> result = createHakuWithAtaruLomakeAvain(validAtaruLomakeAvain);

      assertNotNull(result);
      assertNotNull(result.getStatus());
      assertEquals(ResultV1RDTO.ResultStatus.OK, result.getStatus());
    }
  }

  @Test
  public void thatHakuIsNotCreatedWithInValidAtaruLomakeAvain() {
    List<String> invalidAtaruLomakeAvainList = new ArrayList<>();
    invalidAtaruLomakeAvainList.add("01234567");

    for (String inValidAtaruLomakeAvain : invalidAtaruLomakeAvainList) {
      ResultV1RDTO<HakuV1RDTO> result = createHakuWithAtaruLomakeAvain(inValidAtaruLomakeAvain);

      assertNotNull(result);
      assertNotNull(result.getStatus());
      assertEquals(ResultV1RDTO.ResultStatus.ERROR, result.getStatus());
    }
  }

  @Test
  public void thatHakusWithoutAtaruFormsReturnsEmpty() {
    List<Haku> empty = new ArrayList<>();
    when(hakuDAO.findHakusWithAtaruFormKeys()).thenReturn(empty);

    ResultV1RDTO<List<AtaruLomakkeetV1RDTO>> result =
        hakuResource.findAtaruFormUsage(Collections.EMPTY_LIST);

    assertNotNull(result);
    assertNotNull(result.getStatus());
    assertEquals(ResultV1RDTO.ResultStatus.OK, result.getStatus());
    assertEquals(0, result.getResult().size());
  }

  @Test
  public void thatHakusWithAtaruFormsAreGrouped() {
    String oid1 = "ataru1";
    String oid2 = "ataru2";
    String oid3 = "ataru3";
    String lomakeKey1 = "aaaa-aaaa-aaaa-aaaa-aaaa";
    String lomakeKey2 = "bbbb-bbbb-bbbb-bbbb-bbbb";
    MonikielinenTeksti nimi1 = TarjontaFixtures.createText("nimi1_fi", "nimi1_sv", "nimi1_en");
    MonikielinenTeksti nimi2 = TarjontaFixtures.createText("nimi2_fi", "nimi2_sv", "nimi2_en");
    MonikielinenTeksti nimi3 = TarjontaFixtures.createText("nimi3_fi", "nimi3_sv", "nimi3_en");

    Haku mockHaku1 = createHakuWithName(oid1, nimi1, lomakeKey1);
    Haku mockHaku2 = createHakuWithName(oid2, nimi2, lomakeKey1);
    Haku mockHaku3 = createHakuWithName(oid3, nimi3, lomakeKey2);
    List<Haku> mockHaut = new ArrayList<>(Arrays.asList(mockHaku1, mockHaku2, mockHaku3));
    when(hakuDAO.findHakusWithAtaruFormKeys()).thenReturn(mockHaut);

    AtaruLomakeHakuV1RDTO mockItem1 = createAtaruLomakeItem(oid1, nimi1);
    AtaruLomakeHakuV1RDTO mockItem2 = createAtaruLomakeItem(oid2, nimi2);
    AtaruLomakeHakuV1RDTO mockItem3 = createAtaruLomakeItem(oid3, nimi3);
    when(converterV1.fromHakuToAtaruLomakeHakuRDTO(mockHaku1)).thenReturn(mockItem1);
    when(converterV1.fromHakuToAtaruLomakeHakuRDTO(mockHaku2)).thenReturn(mockItem2);
    when(converterV1.fromHakuToAtaruLomakeHakuRDTO(mockHaku3)).thenReturn(mockItem3);

    AtaruLomakkeetV1RDTO expectedForLomake1 =
        createAtaruLomakkeet(lomakeKey1, new ArrayList<>(Arrays.asList(mockItem1, mockItem2)));
    AtaruLomakkeetV1RDTO expectedForLomake2 =
        createAtaruLomakkeet(lomakeKey2, new ArrayList<>(Arrays.asList(mockItem3)));

    ResultV1RDTO<List<AtaruLomakkeetV1RDTO>> result =
        hakuResource.findAtaruFormUsage(Collections.EMPTY_LIST);

    assertNotNull(result);
    assertNotNull(result.getStatus());
    assertEquals(ResultV1RDTO.ResultStatus.OK, result.getStatus());
    assertEquals(2, result.getResult().size());

    List<AtaruLomakkeetV1RDTO> ataruResult = result.getResult();
    AtaruLomakkeetV1RDTO resultForLomake1 = ataruResult.get(0);
    AtaruLomakkeetV1RDTO resultForLomake2 = ataruResult.get(1);

    assertEquals(expectedForLomake1.getAvain(), resultForLomake1.getAvain());
    assertEquals(expectedForLomake2.getAvain(), resultForLomake2.getAvain());
    assertEquals(expectedForLomake1.getHaut().size(), resultForLomake1.getHaut().size());
    assertEquals(expectedForLomake2.getHaut().size(), resultForLomake2.getHaut().size());
    assertEquals(
        expectedForLomake1.getHaut().get(0).getOid(), resultForLomake1.getHaut().get(0).getOid());
    assertEquals(
        expectedForLomake1.getHaut().get(0).getNimi(), resultForLomake1.getHaut().get(0).getNimi());
    assertEquals(
        expectedForLomake1.getHaut().get(1).getOid(), resultForLomake1.getHaut().get(1).getOid());
    assertEquals(
        expectedForLomake1.getHaut().get(1).getNimi(), resultForLomake1.getHaut().get(1).getNimi());
    assertEquals(
        expectedForLomake2.getHaut().get(0).getOid(), resultForLomake2.getHaut().get(0).getOid());
    assertEquals(
        expectedForLomake2.getHaut().get(0).getNimi(), resultForLomake2.getHaut().get(0).getNimi());
  }

  @Test
  public void expandsVirkailijaTyyppiToCriterionWithExpectedKohdejoukkoIds() throws Exception {
    // enable limiting logic
    queryParams.putSingle("virkailijaTyyppi", null);
    HakuSearchParamsV1RDTO params = new HakuSearchParamsV1RDTO();

    params.virkailijaTyyppi = null;
    assertKohdejoukot(
        params,
        uriInfo,
        "haunkohdejoukko_11,haunkohdejoukko_17,haunkohdejoukko_20,haunkohdejoukko_12");

    params.virkailijaTyyppi = "";
    assertKohdejoukot(
        params,
        uriInfo,
        "haunkohdejoukko_11,haunkohdejoukko_17,haunkohdejoukko_20,haunkohdejoukko_12");

    params.virkailijaTyyppi = HakuResourceImplV1.KORKEAKOULUVIRKAILIJA;
    assertKohdejoukot(params, uriInfo, "haunkohdejoukko_12");

    params.virkailijaTyyppi = HakuResourceImplV1.TOISEN_ASTEEN_VIRKAILIJA;
    assertKohdejoukot(params, uriInfo, "haunkohdejoukko_11,haunkohdejoukko_17,haunkohdejoukko_20");

    params.virkailijaTyyppi = "all";
    assertKohdejoukot(
        params,
        uriInfo,
        "haunkohdejoukko_11,haunkohdejoukko_17,haunkohdejoukko_20,haunkohdejoukko_12");

    // disable limiting logic
    queryParams.remove("virkailijaTyyppi");
    assertTrue(
        "non-existent virkailijaTyyppi should not produce any criteria",
        getCriteriaListFromParams(params, uriInfo).isEmpty());
  }

  @Test
  public void findWillUseCacheAndMarkTheEntryAsKeeper() {
    HakuSearchParamsV1RDTO params = new HakuSearchParamsV1RDTO();
    params.virkailijaTyyppi = "all";
    params.cache = true;
    queryParams.putSingle("virkailijaTyyppi", "all");
    when(hakuResource.hakuCache.get(anyString(), any(), anyBoolean())).thenReturn(null);

    hakuResource.find(params, uriInfo);

    verify(hakuResource.hakuCache, times(1)).get(anyString(), any(), eq(true));
  }

  @Test
  public void findAllFetchesDataFromBackedWhenCacheUpdatefails() {
    when(hakuResource.hakuCache.get(anyString(), any(), anyBoolean()))
        .thenThrow(new RuntimeException("Boom"));
    when(hakuDAO.findAll()).thenReturn(Arrays.asList(createHaku("1.1.1"), createHaku("2.2.2")));

    assertThat(hakuResource.findAllHakus().getResult(), hasSize(2));
  }

  @Test
  public void findFetchesDataFromBackedWhenCacheUpdatefails() {
    HakuSearchParamsV1RDTO params = new HakuSearchParamsV1RDTO();
    params.setModifiedAfter(1539939112896L);
    params.virkailijaTyyppi = "all";
    params.cache = true;
    queryParams.putSingle("virkailijaTyyppi", "all");

    when(hakuResource.hakuCache.get(anyString(), any(), anyBoolean()))
        .thenThrow(new RuntimeException("Boom"));
    when(hakuDAO.findHakuByCriteria(anyInt(), anyInt(), any(ArrayList.class)))
        .thenReturn(Arrays.asList(createHaku("1.1.1"), createHaku("2.2.2")));

    assertThat(hakuResource.find(params, uriInfo).getResult(), hasSize(2));
  }

  @Test
  public void organisationGroupOidsCauseHakukohdeSearchBothWithTarjoajaAndGroupOids() {
    String hakuOid = "1.2.246.562.29.676633696010";
    String searchTerms = "";
    String organisationOid1 = "1.2.246.562.10.38382525541";
    String organisationOid2 = "1.2.246.562.10.21540239577";
    String organisationOidsStr = organisationOid1 + "," + organisationOid2;
    String groupOid1 = "1.2.246.562.28.64488491917";
    String groupOid2 = "1.2.246.562.28.48294892489";
    String groupOidsStr = groupOid1 + "," + groupOid2;

    when(hakukohdeSearchService.haeHakukohteet(
            argThat(
                new ArgumentMatcher<HakukohteetKysely>() {
                  @Override
                  public void describeTo(Description description) {
                    description.appendText("organisationOids");
                  }

                  @Override
                  public boolean matches(Object item) {
                    if (!(item instanceof HakukohteetKysely)) {
                      return false;
                    }
                    HakukohteetKysely kysely = (HakukohteetKysely) item;
                    return kysely.getTarjoajaOids().size() == 2
                        && kysely.getTarjoajaOids().contains(organisationOid1)
                        && kysely.getTarjoajaOids().contains(organisationOid2);
                  }
                })))
        .thenReturn(createHakukohteetVastaus(1, 0));
    when(hakukohdeSearchService.haeHakukohteet(
            argThat(
                new ArgumentMatcher<HakukohteetKysely>() {
                  @Override
                  public void describeTo(Description description) {
                    description.appendText("groupOids");
                  }

                  @Override
                  public boolean matches(Object item) {
                    if (!(item instanceof HakukohteetKysely)) {
                      return false;
                    }
                    HakukohteetKysely kysely = (HakukohteetKysely) item;
                    return kysely.getOrganisaatioRyhmaOid().size() == 2
                        && kysely.getOrganisaatioRyhmaOid().contains(groupOid1)
                        && kysely.getOrganisaatioRyhmaOid().contains(groupOid2);
                  }
                })))
        .thenReturn(createHakukohteetVastaus(2, 1000));

    HakukohdeTulosV1RDTO result =
        hakuResource.getHakukohdeTulos(
            hakuOid,
            searchTerms,
            15,
            0,
            null,
            null,
            organisationOidsStr,
            groupOidsStr,
            "JULKAISTU",
            2018,
            null);
    assertThat(result.getTulokset(), hasSize(3));
  }

  @Test
  public void hakukohdeIsNotReturnedTwiceIfItIsFoundBothByOrganisationAndGroupOid() {
    String hakuOid = "1.2.246.562.29.676633696010";
    String searchTerms = "";
    String organisationOid1 = "1.2.246.562.10.38382525541";
    String groupOid1 = "1.2.246.562.28.64488491917";

    HakukohteetVastaus hakukohteetVastaus1 = createHakukohteetVastaus(7, 0);
    HakukohteetVastaus hakukohteetVastaus2 = createHakukohteetVastaus(7, 0);

    List<String> oids1 =
        hakukohteetVastaus1.getHakukohteet().stream()
            .map(HakukohdePerustieto::getOid)
            .collect(Collectors.toList());
    List<String> oids2 =
        hakukohteetVastaus2.getHakukohteet().stream()
            .map(HakukohdePerustieto::getOid)
            .collect(Collectors.toList());
    assertEquals(oids1, oids2);

    when(hakukohdeSearchService.haeHakukohteet(
            argThat(
                new ArgumentMatcher<HakukohteetKysely>() {
                  @Override
                  public void describeTo(Description description) {
                    description.appendText("organisationOids");
                  }

                  @Override
                  public boolean matches(Object item) {
                    if (!(item instanceof HakukohteetKysely)) {
                      return false;
                    }
                    HakukohteetKysely kysely = (HakukohteetKysely) item;
                    return kysely.getTarjoajaOids().size() == 1
                        && kysely.getTarjoajaOids().contains(organisationOid1);
                  }
                })))
        .thenReturn(hakukohteetVastaus1);
    when(hakukohdeSearchService.haeHakukohteet(
            argThat(
                new ArgumentMatcher<HakukohteetKysely>() {
                  @Override
                  public void describeTo(Description description) {
                    description.appendText("groupOids");
                  }

                  @Override
                  public boolean matches(Object item) {
                    if (!(item instanceof HakukohteetKysely)) {
                      return false;
                    }
                    HakukohteetKysely kysely = (HakukohteetKysely) item;
                    return kysely.getOrganisaatioRyhmaOid().size() == 1
                        && kysely.getOrganisaatioRyhmaOid().contains(groupOid1);
                  }
                })))
        .thenReturn(hakukohteetVastaus2);

    HakukohdeTulosV1RDTO result =
        hakuResource.getHakukohdeTulos(
            hakuOid,
            searchTerms,
            15,
            0,
            null,
            null,
            organisationOid1,
            groupOid1,
            "JULKAISTU",
            2018,
            null);
    assertThat(result.getTulokset(), hasSize(7));
  }

  private HakukohteetVastaus createHakukohteetVastaus(int numberToReturn) {
    return createHakukohteetVastaus(numberToReturn, 0);
  }

  private HakukohteetVastaus createHakukohteetVastaus(int numberToReturn, int oidStartIndex) {
    HakukohteetVastaus vastaus = new HakukohteetVastaus();
    vastaus.setHitCount(numberToReturn);
    List<HakukohdePerustieto> hakukohteet = new LinkedList<>();
    for (int i = 0; i < numberToReturn; i++) {
      HakukohdePerustieto hakukohde = new HakukohdePerustieto();
      hakukohde.setOid("oid" + (oidStartIndex + i));
      hakukohteet.add(hakukohde);
    }
    vastaus.setHakukohteet(hakukohteet);
    return vastaus;
  }

  private static void assertKohdejoukot(
      HakuSearchParamsV1RDTO params, UriInfo uriInfo, String expectedValue) {
    List<HakuSearchCriteria> criteria = getCriteriaListFromParams(params, uriInfo);
    assertEquals(
        "Well-defined virkailijaTyyppi should result in exactly one criterion", 1, criteria.size());
    HakuSearchCriteria virkailijaCriterion = criteria.get(0);

    assertEquals(
        "Valid match value is comma separated list; LIKE_OR is required",
        HakuSearchCriteria.Match.LIKE_OR,
        virkailijaCriterion.getMatch());
    assertEquals(expectedValue, virkailijaCriterion.getValue());
  }

  private ResultV1RDTO<HakuV1RDTO> createHakuWithAtaruLomakeAvain(String ataruLomakeAvain) {
    HakuV1RDTO hakuDTO = new HakuV1RDTO();
    hakuDTO.setHakukausiUri("kausi_k");
    hakuDTO.setHakutapaUri("hakutapa_01");
    hakuDTO.setHakutyyppiUri("hakutyyppi_01");
    hakuDTO.setKohdejoukkoUri("haunkohdejoukko_12");
    hakuDTO.setKoulutuksenAlkamiskausiUri("kausi_k");
    hakuDTO.setMaxHakukohdes(42);
    hakuDTO.getNimi().put("kieli_fi", "Nimi suomi");
    hakuDTO.getHakuaikas().add(createHakuaika(new Date(), new Date()));
    hakuDTO.setAtaruLomakeAvain(ataruLomakeAvain);

    ResultV1RDTO<HakuV1RDTO> result = hakuResource.createHaku(hakuDTO, request);
    return result;
  }

  private Haku createHaku(String oid) {
    Haku haku = new Haku();
    haku.setOid(oid);
    return haku;
  }

  private Haku createHakuWithName(String oid, MonikielinenTeksti nimi, String key) {
    Haku haku = createHaku(oid);
    haku.setNimi(nimi);
    haku.setAtaruLomakeAvain(key);
    return haku;
  }

  private AtaruLomakeHakuV1RDTO createAtaruLomakeItem(String oid, MonikielinenTeksti nimi) {
    AtaruLomakeHakuV1RDTO item = new AtaruLomakeHakuV1RDTO();
    item.setOid(oid);
    item.setNimi(nimi.asMap());
    return item;
  }

  private AtaruLomakkeetV1RDTO createAtaruLomakkeet(String key, List<AtaruLomakeHakuV1RDTO> items) {
    AtaruLomakkeetV1RDTO usage = new AtaruLomakkeetV1RDTO();
    usage.setAvain(key);
    usage.setHaut(items);
    return usage;
  }

  private HakuaikaV1RDTO createHakuaika(Date start, Date end) {
    HakuaikaV1RDTO dto = new HakuaikaV1RDTO();

    dto.setAlkuPvm(start);
    dto.setLoppuPvm(end);

    return dto;
  }

  private SearchKoodisCriteriaType createKoodistoCriteriaMatcher(String uri) {
    return Matchers.argThat(new KoodistoCriteriaMatcher(uri));
  }
}
