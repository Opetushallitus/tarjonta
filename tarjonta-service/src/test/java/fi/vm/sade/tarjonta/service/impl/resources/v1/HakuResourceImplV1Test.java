package fi.vm.sade.tarjonta.service.impl.resources.v1;

import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.tarjonta.TestMockBase;
import fi.vm.sade.tarjonta.helpers.KoodistoHelper;
import fi.vm.sade.tarjonta.matchers.KoodistoCriteriaMatcher;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.service.resources.v1.HakuV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuaikaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;

import java.util.Date;

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
    public void thatHakuSingleStudyPlaceIsResolved() {
        HakuV1RDTO hakuDTO = new HakuV1RDTO();

        hakuDTO.setKohdejoukkoUri("haunkohdejoukko_10#1");
        assertEquals(false, hakuDTO.getYhdenPaikanSaanto().isVoimassa());
        assertEquals("Ei korkeakouluhaku", hakuDTO.getYhdenPaikanSaanto().getSyy());

        hakuDTO.setKohdejoukkoUri("haunkohdejoukko_12#1");
        hakuDTO.setKohdejoukonTarkenne("");
        assertEquals(true, hakuDTO.getYhdenPaikanSaanto().isVoimassa());
        assertEquals("Korkeakouluhaku ilman kohdejoukon tarkennetta", hakuDTO.getYhdenPaikanSaanto().getSyy());

        hakuDTO.setKohdejoukonTarkenne("haunkohdejoukontarkenne_3#1");
        assertEquals(true, hakuDTO.getYhdenPaikanSaanto().isVoimassa());
        assertThat(hakuDTO.getYhdenPaikanSaanto().getSyy(), containsString("Kohdejoukon tarkenne"));

        hakuDTO.setKohdejoukonTarkenne("haunkohdejoukontarkenne_4#1");
        assertEquals(false, hakuDTO.getYhdenPaikanSaanto().isVoimassa());
        assertThat(hakuDTO.getYhdenPaikanSaanto().getSyy(), containsString("Kohdejoukon tarkenne"));
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
