package fi.vm.sade.tarjonta.service.impl.resources.v1;

import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.dao.*;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.OIDCreationException;
import fi.vm.sade.tarjonta.service.OidService;
import fi.vm.sade.tarjonta.service.impl.resources.v1.process.MassCommitProcess;
import fi.vm.sade.tarjonta.service.impl.resources.v1.process.MassCopyProcess;
import fi.vm.sade.tarjonta.service.impl.resources.v1.process.MassPepareProcess;
import fi.vm.sade.tarjonta.service.resources.v1.HakuV1Resource;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ProcessV1RDTO;
import fi.vm.sade.tarjonta.shared.types.ModuulityyppiEnum;
import fi.vm.sade.tarjonta.shared.types.TarjontaOidType;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@TestExecutionListeners(listeners = {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class MassCopyTest {

    @Autowired
    private TarjontaFixtures fixtures;

    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;

    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    @Autowired
    private HakukohdeDAO hakukohdeDAO;

    @Autowired
    private HakuDAO hakuDAO;

    @Autowired
    @Spy
    private OrganisaatioService organisaatioService;

    @Autowired
    private KoulutusSisaltyvyysDAO koulutusSisaltyvyysDAO;

    @Autowired
    private KoodiService koodiService;

    @Autowired
    private OidService oidService;

    @Autowired
    @InjectMocks
    HakuV1Resource hakuV1Resource;

    @Value("${root.organisaatio.oid}")
    protected String ophOid;

    @Autowired
    MassPepareProcess prepareProcess;

    @Autowired
    MassCommitProcess commitProcess;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);

        try {
            Mockito.when(oidService.get(TarjontaOidType.KOMO))
                    .thenReturn("new-komo-oid-1")
                    .thenReturn("new-komo-oid-2")
                    .thenReturn("new-komo-oid-3")
                    .thenReturn("new-komo-oid-4")
                    .thenReturn("new-komo-oid-5")
                    .thenReturn("new-komo-oid-6")
                    .thenReturn("new-komo-oid-7")
                    .thenReturn("new-komo-oid-8");

            Mockito.when(oidService.get(TarjontaOidType.KOMOTO))
                    .thenReturn("new-komoto-oid-1")
                    .thenReturn("new-komoto-oid-2")
                    .thenReturn("new-komoto-oid-3")
                    .thenReturn("new-komoto-oid-4")
                    .thenReturn("new-komoto-oid-5")
                    .thenReturn("new-komoto-oid-6")
                    .thenReturn("new-komoto-oid-7")
                    .thenReturn("new-komoto-oid-8");

            Mockito.when(oidService.get(TarjontaOidType.HAKU))
                    .thenReturn("new-haku-oid-1")
                    .thenReturn("new-haku-oid-2")
                    .thenReturn("new-haku-oid-3")
                    .thenReturn("new-haku-oid-4");

            Mockito.when(oidService.get(TarjontaOidType.HAKUKOHDE))
                    .thenReturn("new-hakukohde-oid-1")
                    .thenReturn("new-hakukohde-oid-2")
                    .thenReturn("new-hakukohde-oid-3")
                    .thenReturn("new-hakukohde-oid-4");
        } catch (OIDCreationException e1) {
            e1.printStackTrace();
        }

        KoulutusResourceImplV1CopyTest.stubKorkeakoulutusConvertKoodis(koodiService);
    }

    private Koulutusmoduuli getKomo(String oid) {
        Koulutusmoduuli komoFromDb = koulutusmoduuliDAO.findByOid(oid);
        if (komoFromDb != null) {
            return komoFromDb;
        }

        Koulutusmoduuli komo = KoulutusResourceImplV1CopyTest.getKorkeakoulutusKomo(fixtures);
        komo.setOid(oid);
        komo.setKoulutustyyppiEnum(ModuulityyppiEnum.KORKEAKOULUTUS);
        return koulutusmoduuliDAO.insert(komo);
    }

    private KoulutusmoduuliToteutus getKomoto(Koulutusmoduuli komo, String oid) {
        KoulutusmoduuliToteutus komotoFromDb = koulutusmoduuliToteutusDAO.findKomotoByOid(oid);
        if (komotoFromDb != null) {
            return komotoFromDb;
        }

        KoulutusmoduuliToteutus komoto = KoulutusResourceImplV1CopyTest.getKorkeakoulutusKomoto(fixtures, komo);
        komoto.setTila(TarjontaTila.JULKAISTU);
        komoto.setOid(oid);
        komoto.setToteutustyyppi(ToteutustyyppiEnum.KORKEAKOULUTUS);
        komoto.setKoulutusmoduuli(komo);
        return koulutusmoduuliToteutusDAO.insert(komoto);
    }

    private Hakukohde getHakukohde(String oid, Haku haku) {
        Hakukohde hakukohde = fixtures.createHakukohde();
        hakukohde.setTila(TarjontaTila.JULKAISTU);
        hakukohde.setOid(oid + "-" + haku.getOid());
        hakukohde.setHaku(haku);
        haku.addHakukohde(hakukohde);

        return hakukohdeDAO.insert(hakukohde);
    }

    private void addAlamoduuli(Koulutusmoduuli ylamoduuli, Koulutusmoduuli alamoduuli) {
        KoulutusSisaltyvyys sisaltyvyys = new KoulutusSisaltyvyys(
                ylamoduuli, alamoduuli, KoulutusSisaltyvyys.ValintaTyyppi.ALL_OFF
        );
        koulutusSisaltyvyysDAO.insert(sisaltyvyys);
    }

    private void connectHakukohdeWithKomoto(Hakukohde hakukohde, KoulutusmoduuliToteutus komoto) {
        hakukohde.addKoulutusmoduuliToteutus(komoto);
        komoto.addHakukohde(hakukohde);
    }
    
    public Haku createKorkeakoulutusHaku(String hakuOid) {
        Haku haku = fixtures.createHaku();
        haku.setOid(hakuOid);
        haku.setTarjoajaOids(new String[]{ophOid});
        haku.setTila(TarjontaTila.JULKAISTU);
        hakuDAO.insert(haku);

        Koulutusmoduuli komo = getKomo("komo-1");
        Koulutusmoduuli komo2 = getKomo("komo-2");
        Koulutusmoduuli komo3 = getKomo("komo-3");

        addAlamoduuli(komo2, komo);
        addAlamoduuli(komo2, komo3);

        KoulutusmoduuliToteutus komoto = getKomoto(komo, "komoto-1");
        KoulutusmoduuliToteutus komoto2 = getKomoto(komo, "komoto-2");

        KoulutusmoduuliToteutus komoto3 = getKomoto(komo2, "komoto-3");
        KoulutusmoduuliToteutus komoto4 = getKomoto(komo2, "komoto-4");

        // This should not be copied (and neither it's komo or sisaltyvyys information)
        KoulutusmoduuliToteutus komotoThatIsNotInHaku = getKomoto(komo3, "komoto-5");

        KoulutusmoduuliToteutus komotoThatIsNotPublished = getKomoto(komo3, "komoto-6");
        komotoThatIsNotPublished.setTila(TarjontaTila.LUONNOS);

        KoulutusmoduuliToteutus komotoThatHasNoPublishedHakukohde = getKomoto(komo3, "komoto-7");

        Hakukohde hakukohdeThatShouldBeCopied = getHakukohde("hakukohde-1", haku);
        connectHakukohdeWithKomoto(hakukohdeThatShouldBeCopied, komoto);
        connectHakukohdeWithKomoto(hakukohdeThatShouldBeCopied, komoto2);
        connectHakukohdeWithKomoto(hakukohdeThatShouldBeCopied, komoto3);
        connectHakukohdeWithKomoto(hakukohdeThatShouldBeCopied, komoto4);
        addRyhmaliitos(hakukohdeThatShouldBeCopied, "testiryhma");

        Hakukohde anotherHakukohdeThatShouldBeCopied = getHakukohde("another-hakukohde-that-should-be-copied", haku);
        connectHakukohdeWithKomoto(anotherHakukohdeThatShouldBeCopied, komoto);

        Hakukohde hakukohde2 = getHakukohde("hakukohde-2", haku);
        connectHakukohdeWithKomoto(hakukohde2, komotoThatIsNotPublished);

        Hakukohde hakukohde3 = getHakukohde("hakukohde-3", haku);
        hakukohde3.setTila(TarjontaTila.LUONNOS); // this should not be copied
        connectHakukohdeWithKomoto(hakukohde3, komotoThatHasNoPublishedHakukohde);

        return haku;
    }

    public void addRyhmaliitos(Hakukohde hakukohde, String ryhmaOid) {
        Ryhmaliitos ryhmaliitos = new Ryhmaliitos();
        ryhmaliitos.setHakukohde(hakukohde);
        ryhmaliitos.setRyhmaOid(ryhmaOid);
        hakukohde.addRyhmaliitos(ryhmaliitos);
    }

    public String copyHaku(String hakuOid) {
        ProcessV1RDTO state = ProcessV1RDTO.generate();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(MassCopyProcess.SELECTED_HAKU_OID, hakuOid);
        state.setParameters(params);

        prepareProcess.setState(state);
        prepareProcess.run();

        assertEquals(true, prepareProcess.isCompleted());

        commitProcess.setState(state);
        commitProcess.run();

        return state.getId();
    }

    @Test
    public void testKorkeakoulutusCopy() {
        Haku haku1 = createKorkeakoulutusHaku("haku-1");
        Haku haku2 = createKorkeakoulutusHaku("haku-2");

        Koulutusmoduuli komo = getKomo("komo-1");
        KoulutusmoduuliToteutus komotoOnlyInHaku2 = getKomoto(komo, "komoto-only-in-haku2");
        Hakukohde hakukohdeInHaku2 = hakukohdeDAO.findHakukohdeByOid("hakukohde-1-haku-2");
        connectHakukohdeWithKomoto(hakukohdeInHaku2, komotoOnlyInHaku2);
        
        int komoCountBeforeCopy = koulutusmoduuliDAO.findAllKomos().size();
        int komotoCountBeforeCopy = koulutusmoduuliToteutusDAO.findAll().size();
        int hakukohdeCountBeforeCopy = hakukohdeDAO.findAll().size();
        int hakuCountBeforeCopy = hakuDAO.findAll().size();

        String processId = copyHaku("haku-1");
        String processId2 = copyHaku("haku-2");

        int komoCountAfterCopy = koulutusmoduuliDAO.findAllKomos().size();
        int komotoCountAfterCopy = koulutusmoduuliToteutusDAO.findAll().size();
        int hakukohdeCountAfterCopy = hakukohdeDAO.findAll().size();
        int hakuCountAfterCopy = hakuDAO.findAll().size();

        // Jos komoto kuuluu useampaan hakuun, se kopioidaan kuitenkin vain kerran! BUG-328
        assertEquals(komoCountBeforeCopy + 5, komoCountAfterCopy);
        assertEquals(komotoCountBeforeCopy + 5, komotoCountAfterCopy);

        assertEquals(hakukohdeCountBeforeCopy + 4, hakukohdeCountAfterCopy);
        assertEquals(hakuCountBeforeCopy + 2, hakuCountAfterCopy);

        Iterator<Koulutusmoduuli> tmpKomos = koulutusmoduuliDAO.findByKoulutuksenTunnisteOid("komo-2").iterator();
        Koulutusmoduuli newKomo2;
        do {
            newKomo2 = tmpKomos.next();
        }
        while (newKomo2.getOid().equals("komo-2"));

        assertNotSame("komo-2", newKomo2.getOid());

        Set<Koulutusmoduuli> alamoduulit = newKomo2.getAlamoduuliList();
        assertEquals(1, alamoduulit.size());

        for (Koulutusmoduuli alamoduuli :alamoduulit) {
            assertNotSame("komo-1", alamoduuli.getOid());
            assertEquals("komo-1", alamoduuli.getKoulutuksenTunnisteOid());
        }

        KoulutusmoduuliToteutus newKomoto = newKomo2.getKoulutusmoduuliToteutusList().iterator().next();
        assertEquals(processId, newKomoto.getUlkoinenTunniste());

        Set<String> newHakukohdeOids = new HashSet<String>();
        for (Hakukohde hakukohde : newKomoto.getHakukohdes()) {
            newHakukohdeOids.add(hakukohde.getOid());
        }
        assertEquals(2, newHakukohdeOids.size());

        Haku newHaku1 = hakuDAO.findByOid("new-haku-oid-1");
        Haku newHaku2 = hakuDAO.findByOid("new-haku-oid-1");

        Hakukohde newHakukohdeFromHaku1 = newHaku1.getHakukohdes().iterator().next();
        assertEquals("new-haku-oid-1", newHakukohdeFromHaku1.getHaku().getOid());

        Hakukohde newHakukohdeFromHaku2 = newHaku1.getHakukohdes().iterator().next();
        assertEquals("new-haku-oid-1", newHakukohdeFromHaku2.getHaku().getOid());

        Hakukohde originalHakukohdeHaku1 = hakukohdeDAO.findHakukohdeByOid("hakukohde-1-haku-1");
        Ryhmaliitos originalLiitos = originalHakukohdeHaku1.getRyhmaliitokset().iterator().next();
        assertEquals(originalHakukohdeHaku1, originalLiitos.getHakukohde());
        assertEquals("testiryhma", originalLiitos.getRyhmaOid());

        Ryhmaliitos newRyhmaliitos = null;
        for (Hakukohde hakukohde : haku1.getHakukohdes()) {
            if (hakukohde.getRyhmaliitokset() != null && !hakukohde.getRyhmaliitokset().isEmpty()) {
                newRyhmaliitos = hakukohde.getRyhmaliitokset().iterator().next();
                assertEquals(hakukohde, newRyhmaliitos.getHakukohde());
                break;
            }
        }
        assertNotNull(newRyhmaliitos);
        assertEquals("testiryhma", newRyhmaliitos.getRyhmaOid());
    }
}