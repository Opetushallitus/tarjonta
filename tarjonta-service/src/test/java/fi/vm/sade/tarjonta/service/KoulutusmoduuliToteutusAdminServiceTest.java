package fi.vm.sade.tarjonta.service;

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

    private KoulutusmoduuliToteutusDTO newToteutus;

    @Before
    public void setUp() {
        newToteutus = new TutkintoOhjelmaToteutusDTO();
    }

    @Test
    public void testSavedKoulutusmoduuliHasOid() {

        KoulutusmoduuliToteutusDTO toteutusDTO = adminService.save(newToteutus);
        assertNull(toteutusDTO.getOid());

    }

}