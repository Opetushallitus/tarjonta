package fi.vm.sade.tarjonta.service.impl.resources.v1;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.KoodistoItemType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioTyyppi;
import fi.vm.sade.tarjonta.SecurityAwareTestBase;
import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.KoodistoUri;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.search.it.TarjontaSearchServiceTest;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class KoulutusResourceImplV1CopyTest extends SecurityAwareTestBase {

    @Autowired
    private TarjontaFixtures fixtures;

    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;

    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    @Autowired
    private KoulutusUtilService koulutusUtilService;

    @Autowired
    @Spy
    private OrganisaatioService organisaatioService;

    @Autowired
    private KoodiService koodiService;

    @Autowired
    private OidService oidService;

    @Before
    @Override
    public void before() {
        MockitoAnnotations.initMocks(this);

        try {
            Mockito.stub(oidService.get(TarjontaOidType.KOMO)).toReturn("new-komo-oid");
            Mockito.stub(oidService.get(TarjontaOidType.KOMOTO)).toReturn("new-komoto-oid");
        } catch (OIDCreationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        OrganisaatioDTO organisaatioDTO = new OrganisaatioDTO();
        organisaatioDTO.setOid("1.2.3.4");
        organisaatioDTO.setOppilaitosTyyppi(OrganisaatioTyyppi.OPPILAITOS.value());
        organisaatioDTO.getTyypit().add(OrganisaatioTyyppi.OPPILAITOS);

        Mockito.doReturn(organisaatioDTO).when(organisaatioService).findByOid(Matchers.anyString());

        stubKorkeakoulutusConvertKoodis(koodiService);

        super.before();
    }

    public static void stubKorkeakoulutusConvertKoodis(KoodiService koodiService) {
        stubKoodi(koodiService, "Oppilaitos", "FI");
        stubKoodi(koodiService, "koulutusaste-uri", "FI");
        stubKoodi(koodiService, "koulutustyyppi_3", "FI");
        stubKoodi(koodiService, "koulutusala", "FI");
        stubKoodi(koodiService, "koulutus", "FI");
        stubKoodi(koodiService, "opintoala", "FI");
        stubKoodi(koodiService, "tutkintonimike", "FI");
        stubKoodi(koodiService, "opetuskieli", "FI");
        stubKoodi(koodiService, "http://opetusmuodot/lahiopetus", "FI");

        KoodiType koodiType = new KoodiType();
        koodiType.setKoodiUri("koulutusaste");
        koodiType.setVersio(0);
        koodiType.setKoodiArvo("koulutusaste");
        KoodistoItemType koodistoType = new KoodistoItemType();
        koodistoType.setKoodistoUri("${koodisto-uris.koulutusaste}");
        koodiType.setKoodisto(koodistoType);
        Mockito.stub(koodiService.listKoodiByRelation(Matchers.any(KoodiUriAndVersioType.class), Matchers.anyBoolean(), Matchers.any(SuhteenTyyppiType.class)))
                .toReturn(Lists.newArrayList(koodiType));
    }

    public static void stubKoodi(KoodiService koodiService, String uri, String arvo) {
        List<KoodiType> vastaus = Lists.newArrayList(TarjontaSearchServiceTest.getKoodiType(uri, arvo));
        Mockito.stub(
                koodiService.searchKoodis(
                        Matchers.argThat(new TarjontaSearchServiceTest.KoodistoCriteriaMatcher(uri))))
                .toReturn(vastaus);
    }

    public static Koulutusmoduuli getKorkeakoulutusKomo(TarjontaFixtures fixtures) {
        Koulutusmoduuli komo = fixtures.createKoulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO);
        komo.setKoulutusasteUri("koulutusaste");
        komo.setTila(TarjontaTila.LUONNOS);
        komo.setKoulutusalaUri("koulutusala");
        komo.setKoulutusUri("koulutus");
        komo.setOpintoalaUri("opintoala");
        komo.setTutkintonimikeUri("tutkintonimike");
        return komo;
    }

    public static KoulutusmoduuliToteutus getKorkeakoulutusKomoto(TarjontaFixtures fixtures, Koulutusmoduuli komo) {
        KoulutusmoduuliToteutus komoto = fixtures.createTutkintoOhjelmaToteutus("1.2.3");
        komoto.setTarjoaja("TEST_TARJOAJA");
        komoto.setKoulutusmoduuli(komo);
        komoto.setToteutustyyppi(ToteutustyyppiEnum.KORKEAKOULUTUS);
        KoodistoUri opetuskieli = new KoodistoUri("opetuskieli");
        komoto.setOpetuskieli(Sets.newHashSet(opetuskieli));
        return komoto;
    }

    @Test
    public void thatKorkeakoulutusIsCopied() {
        Koulutusmoduuli originalKomo = koulutusmoduuliDAO.insert(getKorkeakoulutusKomo(fixtures));
        KoulutusmoduuliToteutus originalKomoto = koulutusmoduuliToteutusDAO.insert(getKorkeakoulutusKomoto(fixtures, originalKomo));

        List<Koulutusmoduuli> komos = koulutusmoduuliDAO.findAllKomos();
        List<KoulutusmoduuliToteutus> komotos = koulutusmoduuliToteutusDAO.findAll();
        int komoCountBeforeCopy = komos.size();
        int komotoCountBeforeCopy = komotos.size();

        KoulutusmoduuliToteutus newKomoto = koulutusUtilService.copyKorkeakoulutus(originalKomoto, originalKomoto.getTarjoaja(), null, null, false);

        komos = koulutusmoduuliDAO.findAllKomos();
        komotos = koulutusmoduuliToteutusDAO.findAll();

        assertEquals(komoCountBeforeCopy + 1, komos.size());
        assertEquals(komotoCountBeforeCopy + 1, komotos.size());

        assertEquals("new-komoto-oid", newKomoto.getOid());
        assertEquals("new-komo-oid", newKomoto.getKoulutusmoduuli().getOid());

        assertEquals(originalKomo.getKoulutuksenTunnisteOid(), newKomoto.getKoulutusmoduuli().getKoulutuksenTunnisteOid());
    }

}
