package fi.vm.sade.tarjonta.service.impl.resources.v1;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.KoodistoItemType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.tarjonta.dao.KoulutusPermissionDAO;
import fi.vm.sade.tarjonta.dao.KoulutusSisaltyvyysDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.model.KoulutusPermission;
import fi.vm.sade.tarjonta.model.KoulutusPermissionType;
import fi.vm.sade.tarjonta.model.KoulutusSisaltyvyys;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.resources.v1.KomoV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.KoulutusV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.*;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.OnrService;
import fi.vm.sade.tarjonta.shared.OrganisaatioService;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static fi.vm.sade.tarjonta.service.impl.resources.v1.V1TestHelper.TARJOAJA1;
import static fi.vm.sade.tarjonta.service.impl.resources.v1.V1TestHelper.mockKoodi;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@Service
@ActiveProfiles("embedded-solr")
public class AmmatillinenPerustutkintoV1Test {

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
    KomoV1Resource komoV1Resource;

    @Autowired
    KoulutusmoduuliDAO koulutusmoduuliDAO;

    @Autowired
    protected KoulutusPermissionDAO koulutusPermissionDAO;

    @Autowired
    KoodiService koodiService;

    @Autowired
    TarjontaKoodistoHelper tarjontaKoodistoHelper;

    @Autowired
    KoulutusSisaltyvyysDAO koulutusSisaltyvyysDAO;

    @Autowired
    OnrService onrService;

    private static final String KOULUTUSKOODI = "koulutus_x";
    private static final String OSAAMISALA = "osaamisala_x";
    private static final String KOMO_OID = "ammKomoOid";
    private static final String TUTKINTONIMIKE = "tutkintonimike_x";
    private HttpTestHelper httpTestHelper = new HttpTestHelper(true);
    private HttpServletRequest request = httpTestHelper.request;

    @Before
    public void init() {
        helper.init();

        Koulutusmoduuli komo = koulutusmoduuliDAO.findByOid(KOMO_OID);
        if (komo == null) {
            Koulutusmoduuli parentKomo = new Koulutusmoduuli();
            parentKomo.setOid("parentAmmatillinenKomoOid");
            parentKomo.setModuuliTyyppi(fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi.TUTKINTO);
            parentKomo.setKoulutustyyppiEnum(ModuulityyppiEnum.AMMATILLINEN_PERUSKOULUTUS);
            koulutusmoduuliDAO.insert(parentKomo);

            komo = new Koulutusmoduuli();
            komo.setOid(KOMO_OID);
            komo.setModuuliTyyppi(fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
            komo.setKoulutustyyppiEnum(ModuulityyppiEnum.AMMATILLINEN_PERUSKOULUTUS);
            komo.setTutkintonimikeUri(TUTKINTONIMIKE);
            koulutusmoduuliDAO.insert(komo);

            KoulutusSisaltyvyys sisaltyvyys = new KoulutusSisaltyvyys(
                    parentKomo, komo, KoulutusSisaltyvyys.ValintaTyyppi.ALL_OFF
            );
            koulutusSisaltyvyysDAO.insert(sisaltyvyys);
        }

        List<ModuuliTuloksetV1RDTO> komos = new ArrayList<>();
        komos.add(new ModuuliTuloksetV1RDTO(komo.getOid(), null, null, null, null, null, null));
        when(komoV1Resource.searchModule(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO, KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA, KOULUTUSKOODI, OSAAMISALA, null)).thenReturn(
                new ResultV1RDTO<>(komos)
        );

        when(koodiService.searchKoodis(new SearchKoodisCriteriaType(){{
            getKoodiUris().add(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO.uri());
        }})).thenReturn(
                Lists.newArrayList(new KoodiType(){{
                    setKoodiUri(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO.uri());
                    setVersio(1);
                }})
        );

        when(tarjontaKoodistoHelper.getKoodi(OSAAMISALA, 1)).thenReturn(
                new KoodiType(){{
                    setKoodiArvo(OSAAMISALA);
                    setKoodiUri(OSAAMISALA);
                    setVersio(1);
                    setKoodisto(new KoodistoItemType(){{
                        setKoodistoUri(KoodistoURI.KOODISTO_OSAAMISALA_URI);
                    }});
                }}
        );

        when(tarjontaKoodistoHelper.getKoodiByUri(TUTKINTONIMIKE)).thenReturn(
                new KoodiType(){{
                    setKoodiArvo(TUTKINTONIMIKE);
                    setKoodiUri(TUTKINTONIMIKE);
                    setVersio(1);
                }}
        );

        when(koodiService.listKoodiByRelation(
                new KoodiUriAndVersioType() {{
                    this.setKoodiUri(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO.uri());
                    this.setVersio(1);
                }},
                false,
                SuhteenTyyppiType.SISALTYY
        )).thenReturn(Lists.newArrayList(
                mockKoodi(KoulutusImplicitDataPopulator.KOULUTUSLAJI),
                mockKoodi(KoulutusImplicitDataPopulator.POHJAKOULUTUSVAATIMUS_TOINEN_ASTE)
        ));

        List<KoodiType> mockedCodes = new ArrayList<>();
        mockedCodes.add(mockKoodi(KoulutusImplicitDataPopulator.KOULUTUSALAOPH2002));
        mockedCodes.add(mockKoodi(KoulutusImplicitDataPopulator.KOULUTUSASTEOPH2002));
        mockedCodes.add(mockKoodi(KoulutusImplicitDataPopulator.OPINTOALAOPH2002));
        mockedCodes.add(mockKoodi(KoulutusImplicitDataPopulator.EQF));
        mockedCodes.add(mockKoodi(KoulutusImplicitDataPopulator.TUTKINTO));
        mockedCodes.add(mockKoodi(KoulutusImplicitDataPopulator.TUTKINTONIMIKKEET));
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
    @Transactional
    public void testCreateFailsWhenDuplicateKomotoExists() throws OIDCreationException {
        KoulutusPermission koulutusPermission = new KoulutusPermission(TARJOAJA1, null, "koulutus", "koulutus_x", null, null, KoulutusPermissionType.OIKEUS);
        koulutusPermissionDAO.insert(koulutusPermission);
        KoulutusPermission kieliPermission = new KoulutusPermission(TARJOAJA1, null, "kieli", "kieli_fi", null, null, KoulutusPermissionType.VELVOITE);
        koulutusPermissionDAO.insert(kieliPermission);

        final String komoto1Oid = oidServiceMock.getOid();
        when(oidService.get(TarjontaOidType.KOMOTO))
                .thenReturn(komoto1Oid)
                .thenReturn(oidServiceMock.getOid());

        ResultV1RDTO<KoulutusLukioV1RDTO> result1 = (ResultV1RDTO<KoulutusLukioV1RDTO>) koulutusResourceV1.postKoulutus(dtoForDuplicateTest(), request).getEntity();
        assertEquals(ResultV1RDTO.ResultStatus.OK, result1.getStatus());

        ResultV1RDTO<KoulutusLukioV1RDTO> result2 = (ResultV1RDTO<KoulutusLukioV1RDTO>) koulutusResourceV1.postKoulutus(dtoForDuplicateTest(), request).getEntity();
        assertEquals(ResultV1RDTO.ResultStatus.VALIDATION, result2.getStatus());
        List<ErrorV1RDTO> errors = result2.getErrors();

        assertTrue(errors.size() > 0);

        ErrorV1RDTO duplicateError = Iterables.find(errors, error ->
                error.getErrorMessageKey().contains("koulutusOnJoOlemassa")
        );

        assertTrue(duplicateError.getErrorMessageParameters().get(0).contains(komoto1Oid));
    }

    private KoulutusAmmatillinenPerustutkintoV1RDTO dtoForDuplicateTest() {
        KoulutusAmmatillinenPerustutkintoV1RDTO dto = baseDto();
        dto.setKoulutuksenAlkamisvuosi(2010);
        dto.setTila(TarjontaTila.PUUTTEELLINEN);
        return dto;
    }

    private static KoulutusAmmatillinenPerustutkintoV1RDTO baseDto() {
        KoulutusAmmatillinenPerustutkintoV1RDTO dto = new KoulutusAmmatillinenPerustutkintoV1RDTO();
        dto.setOrganisaatio(new OrganisaatioV1RDTO(TARJOAJA1));
        dto.setTila(TarjontaTila.LUONNOS);
        dto.setKoulutusohjelma(new NimiV1RDTO(){{
            setUri(OSAAMISALA);
            setVersio(1);
            setArvo("lukionlinja");
        }});
        dto.setOpetuskielis(new KoodiUrisV1RDTO(ImmutableMap.of("kieli_fi", 1)));
        dto.setKoulutuksenAlkamisvuosi(2015);
        dto.setKoulutuksenAlkamiskausi(new KoodiV1RDTO("kausi_k", 1, "Kevät"));
        dto.setKoulutuskoodi(new KoodiV1RDTO(KOULUTUSKOODI, 1, ""));

        return dto;
    }

}
