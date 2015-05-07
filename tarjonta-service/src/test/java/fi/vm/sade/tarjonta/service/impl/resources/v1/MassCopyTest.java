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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

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
                    .thenReturn("new-komo-oid-4");

            Mockito.when(oidService.get(TarjontaOidType.KOMOTO))
                    .thenReturn("new-komoto-oid-1")
                    .thenReturn("new-komoto-oid-2")
                    .thenReturn("new-komoto-oid-3")
                    .thenReturn("new-komoto-oid-4");

            Mockito.when(oidService.get(TarjontaOidType.HAKU))
                    .thenReturn("new-haku-oid-1")
                    .thenReturn("new-haku-oid-2");

            Mockito.when(oidService.get(TarjontaOidType.HAKUKOHDE))
                    .thenReturn("new-hakukohde-oid-1")
                    .thenReturn("new-hakukohde-oid-2");
        } catch (OIDCreationException e1) {
            e1.printStackTrace();
        }

        KoulutusResourceImplV1CopyTest.stubKorkeakoulutusConvertKoodis(koodiService);
    }

    private Koulutusmoduuli getKomo(String oid) {
        Koulutusmoduuli komo = KoulutusResourceImplV1CopyTest.getKorkeakoulutusKomo(fixtures);
        komo.setOid(oid);
        komo.setKoulutustyyppiEnum(ModuulityyppiEnum.KORKEAKOULUTUS);
        return koulutusmoduuliDAO.insert(komo);
    }

    private KoulutusmoduuliToteutus getKomoto(Koulutusmoduuli komo, String oid) {
        KoulutusmoduuliToteutus komoto = KoulutusResourceImplV1CopyTest.getKorkeakoulutusKomoto(fixtures, komo);
        komoto.setTila(TarjontaTila.JULKAISTU);
        komoto.setOid(oid);
        komoto.setToteutustyyppi(ToteutustyyppiEnum.KORKEAKOULUTUS);
        komoto.setKoulutusmoduuli(komo);
        return koulutusmoduuliToteutusDAO.insert(komoto);
    }

    private void addAlamoduuli(Koulutusmoduuli ylamoduuli, Koulutusmoduuli alamoduuli) {
        KoulutusSisaltyvyys sisaltyvyys = new KoulutusSisaltyvyys(
                ylamoduuli, alamoduuli, KoulutusSisaltyvyys.ValintaTyyppi.ALL_OFF
        );
        koulutusSisaltyvyysDAO.insert(sisaltyvyys);
    }
    
    public void createKorkeakoulutusHaku() {
        Haku haku = fixtures.createHaku();
        haku.setOid("haku-1");
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

        Hakukohde hakukohde = fixtures.createHakukohde();
        hakukohde.setTila(TarjontaTila.JULKAISTU);
        hakukohde.setOid("hakukohde-1");
        hakukohde.addKoulutusmoduuliToteutus(komoto);
        hakukohde.addKoulutusmoduuliToteutus(komoto2);
        hakukohde.addKoulutusmoduuliToteutus(komoto3);
        hakukohde.addKoulutusmoduuliToteutus(komoto4);
        hakukohde.setHaku(haku);
        hakukohdeDAO.insert(hakukohde);

        addRyhmaliitos(hakukohde, "testiryhma");

        Hakukohde hakukohde2 = fixtures.createHakukohde();
        hakukohde2.setTila(TarjontaTila.JULKAISTU);
        hakukohde2.setOid("hakukohde-2");
        hakukohde2.addKoulutusmoduuliToteutus(komotoThatIsNotPublished);
        hakukohde2.setHaku(haku);
        hakukohdeDAO.insert(hakukohde2);

        Hakukohde hakukohde3 = fixtures.createHakukohde();
        hakukohde3.setTila(TarjontaTila.LUONNOS); // this should not be copied
        hakukohde3.setOid("hakukohde-3");
        hakukohde3.addKoulutusmoduuliToteutus(komotoThatHasNoPublishedHakukohde);
        hakukohde3.setHaku(haku);
        hakukohdeDAO.insert(hakukohde3);

        komoto.addHakukohde(hakukohde);
        komoto2.addHakukohde(hakukohde);
        komoto3.addHakukohde(hakukohde);
        komoto4.addHakukohde(hakukohde);
        komotoThatIsNotPublished.addHakukohde(hakukohde2);
        komotoThatHasNoPublishedHakukohde.addHakukohde(hakukohde3);
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
        createKorkeakoulutusHaku();
        
        int komoCountBeforeCopy = koulutusmoduuliDAO.findAllKomos().size();
        int komotoCountBeforeCopy = koulutusmoduuliToteutusDAO.findAll().size();
        int hakukohdeCountBeforeCopy = hakukohdeDAO.findAll().size();
        int hakuCountBeforeCopy = hakuDAO.findAll().size();

        String processId = copyHaku("haku-1");

        int komoCountAfterCopy = koulutusmoduuliDAO.findAllKomos().size();
        int komotoCountAfterCopy = koulutusmoduuliToteutusDAO.findAll().size();
        int hakukohdeCountAfterCopy = hakukohdeDAO.findAll().size();
        int hakuCountAfterCopy = hakuDAO.findAll().size();

        assertEquals(komoCountBeforeCopy + 4, komoCountAfterCopy);
        assertEquals(komotoCountBeforeCopy + 4, komotoCountAfterCopy);
        assertEquals(hakukohdeCountBeforeCopy + 1, hakukohdeCountAfterCopy);
        assertEquals(hakuCountBeforeCopy + 1, hakuCountAfterCopy);

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

        assertEquals(1, newKomoto.getHakukohdes().size());

        Hakukohde originalHakukohde = hakukohdeDAO.findHakukohdeByOid("hakukohde-1");
        Ryhmaliitos originalLiitos = originalHakukohde.getRyhmaliitokset().iterator().next();
        assertEquals(originalHakukohde, originalLiitos.getHakukohde());
        assertEquals("testiryhma", originalLiitos.getRyhmaOid());

        Hakukohde newHakukohde = newKomoto.getHakukohdes().iterator().next();
        Ryhmaliitos newRyhmaliitos = newHakukohde.getRyhmaliitokset().iterator().next();
        assertEquals(newHakukohde, newRyhmaliitos.getHakukohde());
        assertEquals("testiryhma", newRyhmaliitos.getRyhmaOid());

        assertEquals("new-haku-oid-1", newKomoto.getHakukohdes().iterator().next().getHaku().getOid());
    }
}
