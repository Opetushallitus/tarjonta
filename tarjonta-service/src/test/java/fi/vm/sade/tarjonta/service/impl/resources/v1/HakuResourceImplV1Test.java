package fi.vm.sade.tarjonta.service.impl.resources.v1;

import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.helpers.KoodistoHelper;
import fi.vm.sade.tarjonta.matchers.KoodistoCriteriaMatcher;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.impl.resources.v1.util.KoodistoValidator;
import fi.vm.sade.tarjonta.service.resources.v1.HakuV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuaikaV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HakuResourceImplV1Test {

    private KoodistoHelper koodistoHelper = new KoodistoHelper();

    @Mock
    private OidService oidService;

    @Mock
    private KoodiService koodiService;

    @Mock
    private KoodistoValidator koodistoValidator;

    @Mock
    private PermissionChecker permissionChecker;

    @Mock
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;

    @Mock
    private ConverterV1 converterV1;

    @Mock
    private HakuDAO hakuDAO;

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
