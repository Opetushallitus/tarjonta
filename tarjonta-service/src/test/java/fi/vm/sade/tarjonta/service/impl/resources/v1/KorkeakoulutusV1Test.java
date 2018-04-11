package fi.vm.sade.tarjonta.service.impl.resources.v1;

import com.google.common.collect.ImmutableMap;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.tarjonta.shared.OrganisaatioService;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.KoulutusValidationMessages;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.KoulutusValidator;
import fi.vm.sade.tarjonta.service.resources.v1.KoulutusV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoodiV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.NimiV1RDTO;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static fi.vm.sade.tarjonta.service.impl.resources.v1.V1TestHelper.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@Service
@ActiveProfiles("embedded-solr")
public class KorkeakoulutusV1Test {

    @Autowired
    OrganisaatioService organisaatioService;

    @Autowired
    PermissionChecker permissionChecker;

    @Autowired
    OidService oidService;

    @Autowired
    OidServiceMock oidServiceMock;

    @Autowired
    KoulutusV1Resource koulutusResourceV1;

    @Autowired
    V1TestHelper helper;

    @Autowired
    KoodiService koodiService;

    private static final String KOULUTUSKOODI = "koulutus_testikoodi";
    private HttpTestHelper httpTestHelper = new HttpTestHelper();
    private HttpServletRequest request = httpTestHelper.request;


    @Before
    public void init() {
        helper.init();

        List<KoodiType> mockedCodes = new ArrayList<KoodiType>();
        mockedCodes.add(mockKoodi(KoulutusImplicitDataPopulator.KOULUTUSALAOPH2002));
        mockedCodes.add(mockKoodi(KoulutusImplicitDataPopulator.KOULUTUSASTEOPH2002));
        mockedCodes.add(mockKoodi(KoulutusImplicitDataPopulator.OPINTOALAOPH2002));
        mockedCodes.add(mockKoodi(KoulutusImplicitDataPopulator.EQF));
        mockedCodes.add(mockKoodi(KoulutusImplicitDataPopulator.TUTKINTO));
        mockedCodes.add(mockKoodi(KoulutusImplicitDataPopulator.TUTKINTONIMIKEKK));
        when(koodiService.listKoodiByRelation(
                new KoodiUriAndVersioType() {{
                    this.setKoodiUri(KOULUTUSKOODI);
                    this.setVersio(1);
                }},
                false,
                SuhteenTyyppiType.SISALTYY
        )).thenReturn(mockedCodes);
    }

    @Test
    public void testCreatePuutteellinen() throws OIDCreationException {
        when(oidService.get(TarjontaOidType.KOMOTO)).thenReturn(oidServiceMock.getOid());
        when(oidService.get(TarjontaOidType.KOMO)).thenReturn(oidServiceMock.getOid());
        KoulutusKorkeakouluV1RDTO dto = baseDto();
        dto.setTila(TarjontaTila.PUUTTEELLINEN);

        ResultV1RDTO<KoulutusKorkeakouluV1RDTO> result = (ResultV1RDTO<KoulutusKorkeakouluV1RDTO>) koulutusResourceV1.postKoulutus(dto, request).getEntity();
        assertEquals(ResultV1RDTO.ResultStatus.OK, result.getStatus());
    }

    @Test
    public void testCreatePuuttellinenFailsWhenMissingRequiredFields() throws OIDCreationException {
        KoulutusKorkeakouluV1RDTO dto = new KoulutusKorkeakouluV1RDTO();
        dto.setTila(TarjontaTila.PUUTTEELLINEN);
        dto.setOrganisaatio(new OrganisaatioV1RDTO(TARJOAJA1));

        ResultV1RDTO<KoulutusKorkeakouluV1RDTO> result = (ResultV1RDTO<KoulutusKorkeakouluV1RDTO>) koulutusResourceV1.postKoulutus(dto, request).getEntity();
        assertEquals(ResultV1RDTO.ResultStatus.VALIDATION, result.getStatus());
        assertEquals(3, result.getErrors().size());
        assertTrue(containsError(result.getErrors(), KoulutusValidator.KOULUTUSOHJELMA));
        assertTrue(containsError(result.getErrors(), KoulutusValidator.KOULUTUKSEN_ALKAMISPVMS));
        assertTrue(containsError(result.getErrors(), KoulutusValidationMessages.KOULUTUS_KOULUTUSKOODI_MISSING.getFieldName()));
    }

    private static KoulutusKorkeakouluV1RDTO baseDto() {
        KoulutusKorkeakouluV1RDTO dto = new KoulutusKorkeakouluV1RDTO();
        dto.setOrganisaatio(new OrganisaatioV1RDTO(TARJOAJA1));
        dto.setTila(TarjontaTila.LUONNOS);
        dto.setKoulutusohjelma(new NimiV1RDTO(ImmutableMap.of("kieli_fi", "Nimi")));
        dto.setKoulutuksenAlkamisvuosi(2015);
        dto.setKoulutuksenAlkamiskausi(new KoodiV1RDTO("kausi_k", 1, "Kevät"));
        dto.setKoulutuskoodi(new KoodiV1RDTO(KOULUTUSKOODI, 1, ""));

        return dto;
    }

}
