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

        return hakukohdeDAO.insert(hakukohde);
    }

    private void addAlamoduuli(Koulutusmoduuli ylamoduuli, Koulutusmoduuli alamoduuli) {
        KoulutusSisaltyvyys sisaltyvyys = new KoulutusSisaltyvyys(
                ylamoduuli, alamoduuli, KoulutusSisaltyvyys.ValintaTyyppi.ALL_OFF
        );
        koulutusSisaltyvyysDAO.insert(sisaltyvyys);
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

        Hakukohde hakukohde = getHakukohde("hakukohde-1", haku);
        hakukohde.addKoulutusmoduuliToteutus(komoto);
        hakukohde.addKoulutusmoduuliToteutus(komoto2);
        hakukohde.addKoulutusmoduuliToteutus(komoto3);
        hakukohde.addKoulutusmoduuliToteutus(komoto4);

        addRyhmaliitos(hakukohde, "testiryhma");

        Hakukohde hakukohde2 = getHakukohde("hakukohde-2", haku);
        hakukohde2.addKoulutusmoduuliToteutus(komotoThatIsNotPublished);

        Hakukohde hakukohde3 = getHakukohde("hakukohde-3", haku);
        hakukohde3.setTila(TarjontaTila.LUONNOS); // this should not be copied
        hakukohde3.addKoulutusmoduuliToteutus(komotoThatHasNoPublishedHakukohde);

        komoto.addHakukohde(hakukohde);
        komoto2.addHakukohde(hakukohde);
        komoto3.addHakukohde(hakukohde);
        komoto4.addHakukohde(hakukohde);
        komotoThatIsNotPublished.addHakukohde(hakukohde2);
        komotoThatHasNoPublishedHakukohde.addHakukohde(hakukohde3);

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
        hakukohdeInHaku2.addKoulutusmoduuliToteutus(komotoOnlyInHaku2);
        komotoOnlyInHaku2.addHakukohde(hakukohdeInHaku2);
        
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

        assertEquals(hakukohdeCountBeforeCopy + 2, hakukohdeCountAfterCopy);
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

        assertEquals(2, newKomoto.getHakukohdes().size());

        List<Hakukohde> newHakukohdes = new ArrayList(newKomoto.getHakukohdes());

        Collections.sort(newHakukohdes, new Comparator<Hakukohde>() {
            @Override
            public int compare(Hakukohde hakukohde1, Hakukohde hakukohde2) {
                if (hakukohde1.getId() < hakukohde2.getId()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        Hakukohde newHakukohdeFromHaku1 = newHakukohdes.get(0);
        assertEquals("new-hakukohde-oid-1", newHakukohdeFromHaku1.getOid());
        assertEquals("new-haku-oid-1", newHakukohdeFromHaku1.getHaku().getOid());

        Hakukohde newHakukohdeFromHaku2 = newHakukohdes.get(1);
        assertEquals("new-hakukohde-oid-2", newHakukohdeFromHaku2.getOid());
        assertEquals("new-haku-oid-2", newHakukohdeFromHaku2.getHaku().getOid());

        Hakukohde originalHakukohdeHaku1 = hakukohdeDAO.findHakukohdeByOid("hakukohde-1-haku-1");
        Ryhmaliitos originalLiitos = originalHakukohdeHaku1.getRyhmaliitokset().iterator().next();
        assertEquals(originalHakukohdeHaku1, originalLiitos.getHakukohde());
        assertEquals("testiryhma", originalLiitos.getRyhmaOid());

        Ryhmaliitos newRyhmaliitos = newHakukohdeFromHaku1.getRyhmaliitokset().iterator().next();
        assertEquals(newHakukohdeFromHaku1, newRyhmaliitos.getHakukohde());
        assertEquals("testiryhma", newRyhmaliitos.getRyhmaOid());
    }
}
