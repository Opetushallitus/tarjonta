package fi.vm.sade.tarjonta.service;

import fi.vm.sade.tarjonta.model.TutkintoOhjelma;
import fi.vm.sade.tarjonta.model.dto.*;
import java.util.Arrays;
import java.util.Date;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class KoulutusmoduuliToteutusAdminServiceTest {

    @Autowired
    private KoulutusmoduuliToteutusAdminService toteutusService;

    @Autowired
    private KoulutusmoduuliAdminService moduuliService;

    /**
     * Newly instantiated toteutus with minimal attributes populated.
     */
    private KoulutusmoduuliToteutusDTO emptyToteutus;

    /*
     * Koulutusmoduuli toteutus with most if not all attributes populated.
     */
    private TutkintoOhjelmaToteutusDTO completeToteutus;

    /**
     * A known Koulutusmoduuli OID we can use when referring to which Koulutusmoduuli any KoulutusmoduuliToteutus is implementing.
     */
    private String koulutusmoduuliOid;

    private Date today = new Date();

    @Before
    public void setUp() {

        initKoulutusmoduuliOID();
        
        emptyToteutus = new TutkintoOhjelmaToteutusDTO();
        emptyToteutus.setToteutettavaKoulutusmoduuliOID(koulutusmoduuliOid);
        
        completeToteutus = createCompleteToteutus();        

    }

    private void initKoulutusmoduuliOID() {
        if (koulutusmoduuliOid == null) {
            TutkintoOhjelmaDTO tutkintoOhjelma = moduuliService.createTutkintoOhjelma(null);
            koulutusmoduuliOid = moduuliService.save(tutkintoOhjelma).getOid();            
        }
    }

    @Test
    public void testSavedKoulutusmoduuliHasOid() {

        
        KoulutusmoduuliToteutusDTO toteutusDTO = toteutusService.save(emptyToteutus);
        assertNotNull(toteutusDTO.getOid());

    }

    @Test
    public void testSaveAndLoadCompleteToteutus() {

        KoulutusmoduuliToteutusDTO saved = toteutusService.save(completeToteutus);
        assertNotNull(saved);

        KoulutusmoduuliToteutusDTO loaded = toteutusService.findByOID(saved.getOid());
        assertNotNull(loaded);

        assertTutkintoOhjelma((TutkintoOhjelmaToteutusDTO) completeToteutus, (TutkintoOhjelmaToteutusDTO) loaded);

    }

    private void assertTutkintoOhjelma(TutkintoOhjelmaToteutusDTO expected, TutkintoOhjelmaToteutusDTO actual) {

        assertEquals(expected.getNimi(), actual.getNimi());
        assertEquals(expected.getKoulutuksenAlkamisPvm(), actual.getKoulutuksenAlkamisPvm());
        assertEquals(expected.getKoulutuslajiUri(), actual.getKoulutuslajiUri());
        assertEquals(expected.getMaksullisuus(), actual.getMaksullisuus());
        assertEquals(expected.getTarjoajat(), actual.getTarjoajat());
        assertEquals(expected.getToteutettavaKoulutusmoduuliOID(), actual.getToteutettavaKoulutusmoduuliOID());

    }

    private TutkintoOhjelmaToteutusDTO createCompleteToteutus() {

        TutkintoOhjelmaToteutusDTO toteutus = new TutkintoOhjelmaToteutusDTO();

        toteutus.setNimi("Koulutusmoduuli toteutus");
        toteutus.setKoulutuksenAlkamisPvm(today);
        toteutus.setKoulutuslajiUri("http://koulutuslaji/aikuis");
        toteutus.setTarjoajat(Arrays.asList("http://organisaatio/1", "http://organisaatio/2"));
        toteutus.setToteutettavaKoulutusmoduuliOID(koulutusmoduuliOid);

        KoulutusmoduuliPerustiedotDTO perustiedot = new KoulutusmoduuliPerustiedotDTO();
        perustiedot.setKoulutusKoodiUri("http://koulutusmooduuri");
        perustiedot.setOpetuskielis(Arrays.asList("http://opentuskieli/fi", "http://opetuskieli/en"));
        perustiedot.setOpetusmuotos(Arrays.asList("http://opetusmuoto/luokka", "http://opetusmuoto/eta"));
        perustiedot.setAsiasanoituses(Arrays.asList("http://asiasana/talous", "http://asiasana/suojelu"));

        toteutus.setPerustiedot(perustiedot);

        return toteutus;

    }

}