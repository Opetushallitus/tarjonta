package fi.vm.sade.tarjonta.service.impl.resources.v1;

import com.google.common.collect.Lists;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.tarjonta.TestUtilityBase;
import fi.vm.sade.tarjonta.matchers.KoodistoCriteriaMatcher;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeAjankohtaRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.*;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.auth.OrganisaatioContext;
import fi.vm.sade.tarjonta.shared.auth.TarjontaPermissionServiceImpl;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;

import static fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO.ErrorCode.VALIDATION;
import static fi.vm.sade.tarjonta.service.resources.v1.dto.ResultV1RDTO.ResultStatus.OK;
import static org.junit.Assert.*;


@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class})
@ActiveProfiles("embedded-solr")
@Transactional()
public class HakukohdeResourceImplV1TestOld extends TestUtilityBase {

    private TarjontaPermissionServiceImpl permissionService = Mockito.mock(TarjontaPermissionServiceImpl.class);
    private TarjontaPermissionServiceImpl originalPermissionService;

    @Autowired
    private TarjontaKoodistoHelper koodistoHelper;

    @Before
    public void setup() throws Exception {
        originalPermissionService = Whitebox.getInternalState(permissionChecker, "permissionService");
        Whitebox.setInternalState(permissionChecker, "permissionService", permissionService);
        Mockito.stub(oidService.get(TarjontaOidType.HAKUKOHDE)).toReturn("1.2.3.4.5");
        stubKoodi("kieli_fi", "suomi");

        Mockito.when(koodistoHelper.getKoodiByUri(Matchers.anyString())).thenReturn(
                new KoodiType(){{
                    setKoodiArvo("arvo");
                    setKoodiUri("uri");
                    setVersio(1);
                }}
        );
        Mockito.when(koodistoHelper.convertKielikoodiToKieliUri(Matchers.anyString())).thenReturn("kieli_fi");

        //mock permission service to return true for all requests
        Mockito.stub(permissionService.userCanUpdateHakukohde(Mockito.any(OrganisaatioContext.class))).toReturn(true);
        Mockito.stub(permissionService.userCanCreateHakukohde(Mockito.any(OrganisaatioContext.class))).toReturn(true);
        Mockito.stub(permissionService.userCanDeleteHakukohde(Mockito.any(OrganisaatioContext.class))).toReturn(true);
    }

    @After
    public void after() {
        if(originalPermissionService != null) {
            Whitebox.setInternalState(permissionChecker, "permissionService", originalPermissionService);
        }
    }

    @Test
    public void testBlockDuplicatingHakukohdeThatMatchesHakuAndKomoto() {
        HakukohdeV1RDTO hakukohde = mkHakukohde(canAddHakukohde(mkRandomHaku()), mkRandomKomoto());

        assertEquals(200, hakukohdeResource.postHakukohde(hakukohde).getStatus());

        ResultV1RDTO<HakukohdeV1RDTO> result = (ResultV1RDTO<HakukohdeV1RDTO>) hakukohdeResource.postHakukohde(hakukohde).getEntity();

        assertEquals(ResultV1RDTO.ResultStatus.VALIDATION, result.getStatus());

        ErrorV1RDTO error = result.getErrors().get(0);
        assertEquals(VALIDATION, error.getErrorCode());
        assertEquals("HAKUKOHDE_DUPLIKAATTI", error.getErrorMessageKey());
    }

    @Test
    public void testThatHakukohdeInheritsHaunLomakeUrl() {
        Haku haku = mkRandomHaku();
        haku.setHakulomakeUrl("http://haunUrl.com");
        HakukohdeV1RDTO hakukohde = mkHakukohde(canAddHakukohde(haku), mkRandomKomoto());

        ResultV1RDTO<HakukohdeV1RDTO> result = (ResultV1RDTO<HakukohdeV1RDTO>) hakukohdeResource.postHakukohde(hakukohde).getEntity();
        assertEquals(OK, result.getStatus());
        HakukohdeV1RDTO hakukohdeRes = result.getResult();
        assertEquals("http://haunUrl.com", hakukohdeRes.getHakulomakeUrl());
        assertFalse(hakukohdeRes.getOverridesHaunHakulomakeUrl());
    }

    @Test
    public void testThatHakukohdeOverridesUkoinenLomakeUrl() {
        Haku haku = mkRandomHaku();
        haku.setHakulomakeUrl("http://haunUrl.com");
        HakukohdeV1RDTO hakukohde = mkHakukohde(canAddHakukohde(haku), mkRandomKomoto());
        hakukohde.setHakulomakeUrl("http://hakukohdekohtainen.com");
        hakukohde.setOverridesHaunHakulomakeUrl(true);

        ResultV1RDTO<HakukohdeV1RDTO> result = (ResultV1RDTO<HakukohdeV1RDTO>) hakukohdeResource.postHakukohde(hakukohde).getEntity();
        assertEquals(OK, result.getStatus());
        HakukohdeV1RDTO hakukohdeRes = result.getResult();
        assertEquals("http://hakukohdekohtainen.com", hakukohdeRes.getHakulomakeUrl());
        assertTrue(hakukohdeRes.getOverridesHaunHakulomakeUrl());
    }

    @Test
    public void testBlockDuplicatingHakukohdeThatMatchesHakuButNotKomoto() {
        Haku haku = canAddHakukohde(mkRandomHaku());

        assertEquals(200, hakukohdeResource.postHakukohde(mkHakukohde(haku, mkRandomKomoto())).getStatus());

        ResultV1RDTO<HakukohdeV1RDTO> result = (ResultV1RDTO<HakukohdeV1RDTO>) hakukohdeResource.postHakukohde(mkHakukohde(haku, mkRandomKomoto())).getEntity();

        assertEquals(ResultV1RDTO.ResultStatus.VALIDATION, result.getStatus());

        ErrorV1RDTO error = result.getErrors().get(0);
        assertEquals(VALIDATION, error.getErrorCode());
        assertEquals("HAKUKOHDE_DUPLIKAATTI_SAMALLA_NIMELLA", error.getErrorMessageKey());
    }

    private KoodiMetadataType getKoodiMeta(String arvo, KieliType kieli) {
        KoodiMetadataType type = new KoodiMetadataType();
        type.setKieli(kieli);
        type.setNimi(arvo + "-nimi-" + kieli.toString());
        return type;
    }

    private KoodiType getKoodiType(String uri, String arvo) {
        KoodiType kt = new KoodiType();
        kt.setKoodiArvo(arvo);
        kt.setKoodiUri(uri);
        kt.getMetadata().addAll(Lists.newArrayList(
                getKoodiMeta(arvo, KieliType.FI),
                getKoodiMeta(arvo, KieliType.SV),
                getKoodiMeta(arvo, KieliType.EN)));
        return kt;
    }

    private void stubKoodi(String uri, String arvo) {
        Mockito
                .stub(koodiService.searchKoodis(Matchers.argThat(new KoodistoCriteriaMatcher(uri))))
                .toReturn(Lists.newArrayList(getKoodiType(uri, arvo)));
    }

    private Haku canAddHakukohde(Haku haku) {
        Mockito.stub(parameterServices.parameterCanAddHakukohdeToHaku(haku.getOid())).toReturn(true);
        return haku;
    }

    private static HakukohdeV1RDTO mkHakukohde(final Haku haku, final KoulutusmoduuliToteutus komoto) {
        final OsoiteRDTO osoite = new OsoiteRDTO() {{
            setOsoiterivi1("Juna Korsoon");
            setPostinumero("posti_12345");
            setPostitoimipaikka("KORSO");
            setPostinumeroArvo("12345");
        }};

        final HakukohdeLiiteV1RDTO liite = new HakukohdeLiiteV1RDTO() {{
            setKieliUri("kieli_fi");
            setLiitteenKuvaukset(Collections.singletonMap("kieli_fi", "Looremin ipsumi."));
            setLiitteenNimi("Liite123");
            setSahkoinenToimitusOsoite("www-osoite");
            setToimitettavaMennessa(new Date());
            setLiitteenToimitusOsoite(osoite);
        }};

        final ValintakoeAjankohtaRDTO ajankohta = new ValintakoeAjankohtaRDTO() {{
            setLisatiedot("Lisa Tieto");
            setOsoite(osoite);
            setAlkaa(new Date());
            setLoppuu(new Date());
        }};

        final ValintakoeV1RDTO valintakoe = new ValintakoeV1RDTO() {{
            setKieliUri("kieli_fi");
            setValintakoeNimi("Pudotuspeli");
            getValintakoeAjankohtas().add(ajankohta);
        }};

        HakukohdeV1RDTO dto = HakukohdeV1RDTO.defaultDto();
        dto.setHakukohteenNimiUri("hakukohteet_255#2");
        dto.setToteutusTyyppi(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO);
        dto.setTila(TarjontaTila.LUONNOS);
        dto.setHakukohteenNimet(Collections.singletonMap("kieli_fi", "Hakukohde #1"));
        dto.setTarjoajaOids(Collections.singleton(komoto.getTarjoaja()));
        dto.setTarjoajaNimet(Collections.singletonMap(komoto.getTarjoaja(), "Organisaatio ABC"));
        dto.setHakuOid(haku.getOid());
        dto.setHakukohdeKoulutusOids(Collections.singletonList(komoto.getOid()));
        dto.getHakukohteenLiitteet().add(liite);
        dto.getValintakokeet().add(valintakoe);

        return dto;
    }

    private Koulutusmoduuli mkRandomKomo() {
        return koulutusmoduuliDAO.insert(tarjontaFixtures.createTutkintoOhjelma());
    }

    private KoulutusmoduuliToteutus mkRandomKomoto() {
        KoulutusmoduuliToteutus komoto = tarjontaFixtures.createTutkintoOhjelmaToteutusWithTarjoajaOid("tarjoajaOid");
        komoto.setKoulutusmoduuli(mkRandomKomo());
        return koulutusmoduuliToteutusDAO.insert(komoto);
    }

    private Haku mkRandomHaku() {
        return hakuDAO.insert(tarjontaFixtures.createHaku());
    }
}
