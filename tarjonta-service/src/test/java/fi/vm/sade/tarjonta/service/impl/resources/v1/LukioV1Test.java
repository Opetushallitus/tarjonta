package fi.vm.sade.tarjonta.service.impl.resources.v1;

import com.google.common.collect.Lists;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.tarjonta.dao.KoulutusSisaltyvyysDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.model.KoulutusSisaltyvyys;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.resources.v1.KomoV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.KoulutusV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.OrganisaatioV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.*;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
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

import java.util.ArrayList;
import java.util.List;

import static fi.vm.sade.tarjonta.service.impl.resources.v1.V1TestHelper.TARJOAJA1;
import static fi.vm.sade.tarjonta.service.impl.resources.v1.V1TestHelper.mockKoodi;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@Service
@ActiveProfiles("embedded-solr")
public class LukioV1Test {

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
    KoodiService koodiService;

    @Autowired
    KoulutusSisaltyvyysDAO koulutusSisaltyvyysDAO;

    private static final String KOULUTUSKOODI = "koulutus_yo";
    private static final String LUKIOLINJA = "lukionlinja_x";
    private static final String KOMO_OID = "lukioKomoOid";

    @Before
    public void init() {
        helper.init();

        Koulutusmoduuli komo = koulutusmoduuliDAO.findByOid(KOMO_OID);
        if (komo == null) {
            Koulutusmoduuli parentKomo = new Koulutusmoduuli();
            parentKomo.setOid("parentLukioKomoOid");
            parentKomo.setModuuliTyyppi(fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi.TUTKINTO);
            parentKomo.setKoulutustyyppiEnum(ModuulityyppiEnum.LUKIOKOULUTUS);
            koulutusmoduuliDAO.insert(parentKomo);

            komo = new Koulutusmoduuli();
            komo.setOid("lukioKomoOid");
            komo.setModuuliTyyppi(fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
            komo.setKoulutustyyppiEnum(ModuulityyppiEnum.LUKIOKOULUTUS);
            koulutusmoduuliDAO.insert(komo);

            KoulutusSisaltyvyys sisaltyvyys = new KoulutusSisaltyvyys(
                    parentKomo, komo, KoulutusSisaltyvyys.ValintaTyyppi.ALL_OFF
            );
            koulutusSisaltyvyysDAO.insert(sisaltyvyys);
        }

        List<ModuuliTuloksetV1RDTO> komos = new ArrayList<ModuuliTuloksetV1RDTO>();
        komos.add(new ModuuliTuloksetV1RDTO(komo.getOid(), null, null, null, null, null, null));
        when(komoV1Resource.searchModule(ToteutustyyppiEnum.LUKIOKOULUTUS, KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA, KOULUTUSKOODI, LUKIOLINJA, null)).thenReturn(
                new ResultV1RDTO<List<ModuuliTuloksetV1RDTO>>(komos)
        );

        when(koodiService.searchKoodis(new SearchKoodisCriteriaType(){{
            getKoodiUris().add(ToteutustyyppiEnum.LUKIOKOULUTUS.uri());
        }})).thenReturn(
                Lists.<KoodiType>newArrayList(new KoodiType(){{
                    setKoodiUri(ToteutustyyppiEnum.LUKIOKOULUTUS.uri());
                    setVersio(1);
                }})
        );

        when(koodiService.listKoodiByRelation(
                new KoodiUriAndVersioType() {{
                    this.setKoodiUri(ToteutustyyppiEnum.LUKIOKOULUTUS.uri());
                    this.setVersio(1);
                }},
                false,
                SuhteenTyyppiType.SISALTYY
        )).thenReturn(Lists.newArrayList(
                mockKoodi(KoulutusImplicitDataPopulator.KOULUTUSLAJI),
                mockKoodi(KoulutusImplicitDataPopulator.POHJAKOULUTUSVAATIMUS_TOINEN_ASTE)
        ));

        List<KoodiType> mockedCodes = new ArrayList<KoodiType>();
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
    public void testCreatePuutteellinen() throws OIDCreationException {
        when(oidService.get(TarjontaOidType.KOMOTO)).thenReturn(oidServiceMock.getOid());
        when(oidService.get(TarjontaOidType.KOMO)).thenReturn(oidServiceMock.getOid());
        KoulutusLukioV1RDTO dto = baseDto();
        dto.setTila(TarjontaTila.PUUTTEELLINEN);

        ResultV1RDTO<KoulutusKorkeakouluV1RDTO> result = (ResultV1RDTO<KoulutusKorkeakouluV1RDTO>) koulutusResourceV1.postKoulutus(dto).getEntity();
        assertEquals(ResultV1RDTO.ResultStatus.OK, result.getStatus());
    }

    private static KoulutusLukioV1RDTO baseDto() {
        KoulutusLukioV1RDTO dto = new KoulutusLukioV1RDTO();
        dto.setOrganisaatio(new OrganisaatioV1RDTO(TARJOAJA1));
        dto.setTila(TarjontaTila.LUONNOS);
        dto.setKoulutusohjelma(new NimiV1RDTO(){{
            setUri(LUKIOLINJA);
            setVersio(1);
            setArvo("lukionlinja");
        }});
        dto.setKoulutuksenAlkamisvuosi(2015);
        dto.setKoulutuksenAlkamiskausi(new KoodiV1RDTO("kausi_k", 1, "Kevät"));
        dto.setKoulutuskoodi(new KoodiV1RDTO(KOULUTUSKOODI, 1, ""));

        return dto;
    }

}
