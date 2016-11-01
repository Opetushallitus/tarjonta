package fi.vm.sade.tarjonta.service.impl.resources.v1;

import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.tarjonta.TestMockBase;
import fi.vm.sade.tarjonta.helpers.KoodistoHelper;
import fi.vm.sade.tarjonta.matchers.KoodistoCriteriaMatcher;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.impl.resources.v1.util.YhdenPaikanSaantoBuilder;
import fi.vm.sade.tarjonta.service.resources.v1.HakuV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.AtaruLomakeHakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.AtaruLomakkeetV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuaikaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.junit.internal.matchers.StringContains.containsString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HakuResourceImplV1Test extends TestMockBase {

    private KoodistoHelper koodistoHelper = new KoodistoHelper();
    private ConverterV1 realConverter = new ConverterV1();

    @InjectMocks
    private YhdenPaikanSaantoBuilder yhdenPaikanSaantoBuilder;

    private TarjontaKoodistoHelper tarjontaKoodistoHelper = mock(TarjontaKoodistoHelper.class);

    @InjectMocks
    private HakuV1Resource hakuResource = new HakuResourceImplV1();

    @Rule
    public ExpectedException nonUniqueKoulutuksenAlkamiskaudet = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        when(oidService.get(TarjontaOidType.HAKU)).thenReturn("1.2.3.4.5");

        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("kieli_fi"))).thenReturn(koodistoHelper.getKoodiTypes("FI"));
        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("kausi_k"))).thenReturn(koodistoHelper.getKoodiTypes("K"));
        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("hakutapa_01"))).thenReturn(koodistoHelper.getKoodiTypes("01"));
        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("hakutyyppi_01"))).thenReturn(koodistoHelper.getKoodiTypes("01"));
        when(koodiService.searchKoodis(createKoodistoCriteriaMatcher("haunkohdejoukko_12"))).thenReturn(koodistoHelper.getKoodiTypes("12"));

        Haku haku = new Haku();
        haku.setOid("haku1");
        when(converterV1.convertHakuV1DRDTOToHaku(any(HakuV1RDTO.class), any(Haku.class))).thenReturn(haku);

        KoodistoURI.KOODISTO_TUTKINTOON_JOHTAVA_KOULUTUS_URI = "tutkintoonjohtava";
        KoodistoURI.KOODI_ON_TUTKINTO_URI = "tutkintoonjohtava_1";
        when(tarjontaKoodistoHelper.getUniqueKoodistoRelation("koulutusUri1", KoodistoURI.KOODISTO_TUTKINTOON_JOHTAVA_KOULUTUS_URI, SuhteenTyyppiType.SISALTYY, false))
                .thenReturn(KoodistoURI.KOODI_ON_TUTKINTO_URI);
        when(tarjontaKoodistoHelper.getUniqueKoodistoRelation("koulutusUri2", KoodistoURI.KOODISTO_TUTKINTOON_JOHTAVA_KOULUTUS_URI, SuhteenTyyppiType.SISALTYY, false))
                .thenReturn(KoodistoURI.KOODI_ON_TUTKINTO_URI);
        when(tarjontaKoodistoHelper.getUniqueKoodistoRelation("koulutusUri3", KoodistoURI.KOODISTO_TUTKINTOON_JOHTAVA_KOULUTUS_URI, SuhteenTyyppiType.SISALTYY, false))
                .thenReturn("jotain-muuta");
        Whitebox.setInternalState(yhdenPaikanSaantoBuilder, "tarjontaKoodistoHelper", tarjontaKoodistoHelper);
    }

    @Test(expected = NullPointerException.class)
    public void thatEmptyHakuIsNotCreated() {
        hakuResource.createHaku(null);
    }


    @Test
    public void thatHakuWithOidIsNotCreated() {
        HakuV1RDTO hakuDTO = new HakuV1RDTO();
        hakuDTO.setOid("1.2.3");

        ResultV1RDTO<HakuV1RDTO> result = hakuResource.createHaku(hakuDTO);

        assertTrue(result.hasErrors());
        assertEquals(ResultV1RDTO.ResultStatus.ERROR, result.getStatus());
    }

    @Test
    public void thatHakuIsCreated() {
        HakuV1RDTO hakuDTO = new HakuV1RDTO();
        ResultV1RDTO<HakuV1RDTO> result = hakuResource.createHaku(hakuDTO);
        assertNotNull(result);
        assertNotNull(result.getStatus());
        assertEquals(ResultV1RDTO.ResultStatus.ERROR, result.getStatus());

        hakuDTO = new HakuV1RDTO();
        hakuDTO.setHakukausiUri("kausi_k");
        hakuDTO.setHakutapaUri("hakutapa_01");
        hakuDTO.setHakutyyppiUri("hakutyyppi_01");
        hakuDTO.setKohdejoukkoUri("haunkohdejoukko_12");
        hakuDTO.setKoulutuksenAlkamiskausiUri("kausi_k");
        hakuDTO.setMaxHakukohdes(42);
        hakuDTO.getNimi().put("kieli_fi", "Nimi suomi");
        hakuDTO.getHakuaikas().add(createHakuaika(new Date(), new Date()));

        result = hakuResource.createHaku(hakuDTO);

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

        haku.setKohdejoukkoUri("haunkohdejoukko_10#1");
        assertEquals(false, yhdenPaikanSaantoBuilder.from(hakukohde).isVoimassa());
        assertEquals("Ei korkeakouluhaku ja hakukohde ei kuulu jatkuvaan korkeakouluhakuun, jonka kohdejoukon tarkenne kuuluu joukkoon [haunkohdejoukontarkenne_3#] tai sitä ei ole", yhdenPaikanSaantoBuilder.from(hakukohde).getSyy());

        haku.setKohdejoukkoUri("haunkohdejoukko_12#1");
        haku.setKohdejoukonTarkenne("");
        assertEquals(true, yhdenPaikanSaantoBuilder.from(hakukohde).isVoimassa());
        assertEquals("Korkeakouluhaku ilman kohdejoukon tarkennetta", yhdenPaikanSaantoBuilder.from(hakukohde).getSyy());

        haku.setKohdejoukonTarkenne("haunkohdejoukontarkenne_3#1");
        assertEquals(true, yhdenPaikanSaantoBuilder.from(hakukohde).isVoimassa());
        assertThat(yhdenPaikanSaantoBuilder.from(hakukohde).getSyy(), containsString("Haun kohdejoukon tarkenne on"));

        haku.setKohdejoukonTarkenne("haunkohdejoukontarkenne_4#1");
        assertEquals(false, yhdenPaikanSaantoBuilder.from(hakukohde).isVoimassa());
        assertThat(yhdenPaikanSaantoBuilder.from(hakukohde).getSyy(), containsString("Haulla on kohdejoukon tarkenne, joka ei ole"));

        haku.setKoulutuksenAlkamisVuosi(2016);
        haku.setKoulutuksenAlkamiskausiUri("kausi_k#1");
        assertEquals(false, yhdenPaikanSaantoBuilder.from(hakukohde).isVoimassa());
        assertThat(yhdenPaikanSaantoBuilder.from(hakukohde).getSyy(), containsString("Haun koulutuksen alkamiskausi on ennen syksyä 2016"));

        haku.setKoulutuksenAlkamisVuosi(2015);
        haku.setKoulutuksenAlkamiskausiUri("kausi_s#1");
        assertEquals(false, yhdenPaikanSaantoBuilder.from(hakukohde).isVoimassa());
        assertThat(yhdenPaikanSaantoBuilder.from(hakukohde).getSyy(), containsString("Haun koulutuksen alkamiskausi on ennen syksyä 2016"));

        haku.setHakutapaUri("hakutapa_03");
        haku.setKohdejoukonTarkenne("haunkohdejoukontarkenne_3#1");
        assertEquals(false, yhdenPaikanSaantoBuilder.from(hakukohde).isVoimassa());
        assertThat(yhdenPaikanSaantoBuilder.from(hakukohde).getSyy(), containsString("hakukohteen koulutus ei ole tutkintoon johtavaa"));

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
        hakukohde.setKoulutusmoduuliToteutuses(new HashSet<>(Arrays.asList(komoto1,komoto2)));

        hakukohde.setKoulutusmoduuliToteutuses(new HashSet<>(Arrays.asList(komoto2)));
        assertEquals(false, yhdenPaikanSaantoBuilder.from(hakukohde).isVoimassa());
        assertThat(yhdenPaikanSaantoBuilder.from(hakukohde).getSyy(), containsString("hakukohteen koulutuksen alkamiskausi on ennen syksyä 2016"));

        KoulutusmoduuliToteutus komoto3 = new KoulutusmoduuliToteutus();
        komoto3.setTila(TarjontaTila.JULKAISTU);
        komoto3.setAlkamisVuosi(2016);
        komoto3.setAlkamiskausiUri("kausi_s");
        komoto3.setKoulutusUri("koulutusUri3");
        hakukohde.setKoulutusmoduuliToteutuses(new HashSet<>(Arrays.asList(komoto3)));

        when(tarjontaKoodistoHelper.getUniqueKoodistoRelation("koulutusUri3", KoodistoURI.KOODISTO_TUTKINTOON_JOHTAVA_KOULUTUS_URI, SuhteenTyyppiType.SISALTYY, false))
                .thenReturn(KoodistoURI.KOODI_ON_TUTKINTO_URI);

        assertEquals(true, yhdenPaikanSaantoBuilder.from(hakukohde).isVoimassa());
        assertThat(yhdenPaikanSaantoBuilder.from(hakukohde).getSyy(), containsString("Jatkuvan haun hakukohteen alkamiskausi ja vuosi on jälkeen kevään 2016"));

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
        assertThat(yhdenPaikanSaantoBuilder.from(hakukohde).getSyy(), containsString("hakukohteen koulutus ei ole tutkintoon johtavaa"));

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
    public void thatAtaruFormsInUseAreFound() {
        String oid1 = "ataru1";
        String oid2 = "ataru2";
        String oid3 = "ataru3";
        String name1 = "name1";
        String name2 = "name2";
        String name3 = "name3";
        String key1 = "aaaa-aaaa-aaaa-aaaa-aaaa";
        String key2 = "bbbb-bbbb-bbbb-bbbb-bbbb";

        List<Haku> hakus = new ArrayList<>();
        Haku haku1 = new Haku();
        Haku haku2 = new Haku();
        Haku haku3 = new Haku();
        haku1.setOid(oid1);
        haku2.setOid(oid2);
        haku3.setOid(oid3);
        haku1.setNimiFi(name1);
        haku2.setNimiFi(name2);
        haku3.setNimiFi(name3);
        haku1.setAtaruLomakeAvain(key1); // haku1 uses atarulomake1
        haku2.setAtaruLomakeAvain(key1); // haku2 uses atarulomake1
        haku3.setAtaruLomakeAvain(key2); // haku3 uses atarulomake2
        hakus.add(haku1);
        hakus.add(haku2);
        hakus.add(haku3);

        when(hakuDAO.findHakusWithAtaruFormKeys()).thenReturn(hakus);

        AtaruLomakeHakuV1RDTO item1 = new AtaruLomakeHakuV1RDTO();
        AtaruLomakeHakuV1RDTO item2 = new AtaruLomakeHakuV1RDTO();
        AtaruLomakeHakuV1RDTO item3 = new AtaruLomakeHakuV1RDTO();
        AtaruLomakkeetV1RDTO expectedUsage1 = new AtaruLomakkeetV1RDTO();
        AtaruLomakkeetV1RDTO expectedUsage2 = new AtaruLomakkeetV1RDTO();
        List<AtaruLomakeHakuV1RDTO> expectedItems1 = new ArrayList<>();
        List<AtaruLomakeHakuV1RDTO> expectedItems2 = new ArrayList<>();
        item1.setOid(oid1);
        item2.setOid(oid2);
        item3.setOid(oid3);
        item1.setNimi(name1);
        item2.setNimi(name2);
        item3.setNimi(name3);
        expectedItems1.add(item1);
        expectedItems1.add(item2);
        expectedItems2.add(item3);
        expectedUsage1.setAvain(key1);
        expectedUsage2.setAvain(key2);
        expectedUsage1.setHaut(expectedItems1); // expected [haku1, haku2] with atarulomake1
        expectedUsage2.setHaut(expectedItems2); // expected [haku3] with atarulomake2

        ResultV1RDTO<List<AtaruLomakkeetV1RDTO>> result = hakuResource.findAtaruFormUsage();
        assertNotNull(result);
        assertNotNull(result.getStatus());
        assertEquals(ResultV1RDTO.ResultStatus.OK, result.getStatus());

        List<AtaruLomakkeetV1RDTO> ataruResult = result.getResult();
        assertEquals(2, ataruResult.size());

        AtaruLomakkeetV1RDTO usage1 = ataruResult.get(0);
        AtaruLomakkeetV1RDTO usage2 = ataruResult.get(1);
        List<AtaruLomakeHakuV1RDTO> items1 = usage1.getHaut();
        List<AtaruLomakeHakuV1RDTO> items2 = usage2.getHaut();
        assertEquals(expectedUsage1.getAvain(), usage1.getAvain());
        assertEquals(expectedUsage2.getAvain(), usage2.getAvain());
        assertEquals(expectedItems1.size(), items1.size());
        assertEquals(expectedItems2.size(), items2.size());
        assertEquals(expectedItems1.get(0).getOid(), items1.get(0).getOid());
        assertEquals(expectedItems1.get(0).getNimi(), items1.get(0).getNimi());
        assertEquals(expectedItems1.get(1).getOid(), items1.get(1).getOid());
        assertEquals(expectedItems1.get(1).getNimi(), items1.get(1).getNimi());
        assertEquals(expectedItems2.get(0).getOid(), items2.get(0).getOid());
        assertEquals(expectedItems2.get(0).getNimi(), items2.get(0).getNimi());
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

        ResultV1RDTO<HakuV1RDTO> result = hakuResource.createHaku(hakuDTO);
        return result;
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
