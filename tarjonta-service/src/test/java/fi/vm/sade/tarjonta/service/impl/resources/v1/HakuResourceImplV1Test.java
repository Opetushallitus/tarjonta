package fi.vm.sade.tarjonta.service.impl.resources.v1;

import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.tarjonta.TestMockBase;
import fi.vm.sade.tarjonta.helpers.KoodistoHelper;
import fi.vm.sade.tarjonta.matchers.KoodistoCriteriaMatcher;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.impl.resources.v1.util.YhdenPaikanSaantoBuilder;
import fi.vm.sade.tarjonta.service.resources.v1.HakuV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuaikaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import static org.junit.Assert.*;
import static org.junit.internal.matchers.StringContains.containsString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class HakuResourceImplV1Test extends TestMockBase {

    private KoodistoHelper koodistoHelper = new KoodistoHelper();

    @InjectMocks
    private HakuV1Resource hakuResource = new HakuResourceImplV1();

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
    public void thatHakuAndHakukohdeSingleStudyPlaceIsResolved() {
        Haku haku = new Haku();
        Hakukohde hakukohde = new Hakukohde();
        hakukohde.setHaku(haku);
        haku.setKoulutuksenAlkamisVuosi(2016);
        haku.setKoulutuksenAlkamiskausiUri("kausi_s#1");

        haku.setKohdejoukkoUri("haunkohdejoukko_10#1");
        assertEquals(false, YhdenPaikanSaantoBuilder.from(hakukohde).isVoimassa());
        assertEquals("Ei korkeakouluhaku", YhdenPaikanSaantoBuilder.from(hakukohde).getSyy());

        haku.setKohdejoukkoUri("haunkohdejoukko_12#1");
        haku.setKohdejoukonTarkenne("");
        assertEquals(true, YhdenPaikanSaantoBuilder.from(hakukohde).isVoimassa());
        assertEquals("Korkeakouluhaku ilman kohdejoukon tarkennetta", YhdenPaikanSaantoBuilder.from(hakukohde).getSyy());

        haku.setKohdejoukonTarkenne("haunkohdejoukontarkenne_3#1");
        assertEquals(true, YhdenPaikanSaantoBuilder.from(hakukohde).isVoimassa());
        assertThat(YhdenPaikanSaantoBuilder.from(hakukohde).getSyy(), containsString("Kohdejoukon tarkenne"));

        haku.setKohdejoukonTarkenne("haunkohdejoukontarkenne_4#1");
        assertEquals(false, YhdenPaikanSaantoBuilder.from(hakukohde).isVoimassa());
        assertThat(YhdenPaikanSaantoBuilder.from(hakukohde).getSyy(), containsString("Kohdejoukon tarkenne"));

        haku.setKoulutuksenAlkamisVuosi(2016);
        haku.setKoulutuksenAlkamiskausiUri("kausi_k#1");
        assertEquals(false, YhdenPaikanSaantoBuilder.from(hakukohde).isVoimassa());
        assertThat(YhdenPaikanSaantoBuilder.from(hakukohde).getSyy(), containsString("Koulutuksen alkamiskausi ennen syksyä 2016"));

        haku.setKoulutuksenAlkamisVuosi(2015);
        haku.setKoulutuksenAlkamiskausiUri("kausi_s#1");
        assertEquals(false, YhdenPaikanSaantoBuilder.from(hakukohde).isVoimassa());
        assertThat(YhdenPaikanSaantoBuilder.from(hakukohde).getSyy(), containsString("Koulutuksen alkamiskausi ennen syksyä 2016"));

        haku.setHakutapaUri("hakutapa_03");
        assertEquals(false, YhdenPaikanSaantoBuilder.from(hakukohde).isVoimassa());
        assertThat(YhdenPaikanSaantoBuilder.from(hakukohde).getSyy(), containsString("hakukohteella ei ole oikean tilaista koulutusmoduulia"));

        KoulutusmoduuliToteutus komoto1 = new KoulutusmoduuliToteutus();
        komoto1.setTila(TarjontaTila.JULKAISTU);
        komoto1.setAlkamisVuosi(2010);
        komoto1.setAlkamiskausiUri("kausi_k");
        KoulutusmoduuliToteutus komoto2 = new KoulutusmoduuliToteutus();
        komoto2.setTila(TarjontaTila.JULKAISTU);
        komoto2.setAlkamisVuosi(2016);
        komoto2.setAlkamiskausiUri("kausi_k");
        hakukohde.setKoulutusmoduuliToteutuses(new HashSet<>(Arrays.asList(komoto1,komoto2)));
        assertEquals(false, YhdenPaikanSaantoBuilder.from(hakukohde).isVoimassa());
        assertThat(YhdenPaikanSaantoBuilder.from(hakukohde).getSyy(), containsString("hakukohteella on liian monta oikean tilaista koulutusmoduulia"));

        hakukohde.setKoulutusmoduuliToteutuses(new HashSet<>(Arrays.asList(komoto2)));
        assertEquals(false, YhdenPaikanSaantoBuilder.from(hakukohde).isVoimassa());
        assertThat(YhdenPaikanSaantoBuilder.from(hakukohde).getSyy(), containsString("hakukohteen alkamiskausi ja vuosi on ennen syksyä 2016"));

        KoulutusmoduuliToteutus komoto3 = new KoulutusmoduuliToteutus();
        komoto3.setTila(TarjontaTila.JULKAISTU);
        komoto3.setAlkamisVuosi(2016);
        komoto3.setAlkamiskausiUri("kausi_s");
        hakukohde.setKoulutusmoduuliToteutuses(new HashSet<>(Arrays.asList(komoto3)));
        assertEquals(true, YhdenPaikanSaantoBuilder.from(hakukohde).isVoimassa());
        assertThat(YhdenPaikanSaantoBuilder.from(hakukohde).getSyy(), containsString("Jatkuvan haun hakukohteen alkamiskausi ja vuosi on jälkeen kevään 2016"));
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
