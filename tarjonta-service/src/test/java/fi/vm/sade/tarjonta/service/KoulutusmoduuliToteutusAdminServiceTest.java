package fi.vm.sade.tarjonta.service;

import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.tarjonta.model.dto.*;
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

    @Before
    public void setUp() {
        emptyToteutus = new TutkintoOhjelmaToteutusDTO();
        completeToteutus = createFullToteutus();
    }

    @Test
    public void testSavedKoulutusmoduuliHasOid() {

        KoulutusmoduuliToteutusDTO toteutusDTO = adminService.save(emptyToteutus);
        assertNotNull(toteutusDTO.getOid());

    }

    @Test
    public void testFindByOid() {
    }

    private TutkintoOhjelmaToteutusDTO createFullToteutus() {

        TutkintoOhjelmaToteutusDTO toteutus = new TutkintoOhjelmaToteutusDTO();
        toteutus.setNimi(COMPLETE_NAME);
        
        KoulutusmoduuliPerustiedotDTO perustiedot = new KoulutusmoduuliPerustiedotDTO();
        toteutus.setPerustiedot(perustiedot);
        
        return toteutus;

    }

}