package fi.vm.sade.tarjonta.service;

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
    private KoulutusmoduuliToteutusAdminService adminService;

    private KoulutusmoduuliToteutusDTO emptyToteutus;

    private static final String COMPLETE_NAME = "Koulutusmoduuli toteutus 1";

    /*
     * Koulutusmoduuli toteutus with most if not all attributes populated.
     */
    private TutkintoOhjelmaToteutusDTO completeToteutus;

    private Date today = new Date();

    @Before
    public void setUp() {
        emptyToteutus = new TutkintoOhjelmaToteutusDTO();
        completeToteutus = createCompleteToteutus();
    }

    @Test
    public void testSavedKoulutusmoduuliHasOid() {

        KoulutusmoduuliToteutusDTO toteutusDTO = adminService.save(emptyToteutus);
        assertNotNull(toteutusDTO.getOid());

    }

    @Test
    public void testFindByOid() {
    }

    private TutkintoOhjelmaToteutusDTO createCompleteToteutus() {

        TutkintoOhjelmaToteutusDTO toteutus = new TutkintoOhjelmaToteutusDTO();

        toteutus.setNimi(COMPLETE_NAME);
        toteutus.setKoulutuksenAlkamisPvm(today);
        toteutus.setKoulutuslajiUri("http://koulutuslaji/aikuis");
        toteutus.setTarjoajat(Arrays.asList("http://organisaatio/1", "http://organisaatio/2"));
        
        KoulutusmoduuliPerustiedotDTO perustiedot = new KoulutusmoduuliPerustiedotDTO();
        perustiedot.setKoulutusKoodiUri("http://koulutusmooduuri");
        perustiedot.setOpetuskielis(Arrays.asList("http://opentuskieli/fi", "http://opetuskieli/en"));
        perustiedot.setOpetusmuotos(Arrays.asList("http://opetusmuoto/luokka", "http://opetusmuoto/eta"));
        perustiedot.setAsiasanoituses(Arrays.asList("http://asiasana/talous", "http://asiasana/suojelu"));

        toteutus.setPerustiedot(perustiedot);



        return toteutus;

    }

}