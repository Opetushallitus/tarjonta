package fi.vm.sade.tarjonta.service;

import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliDTO;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliSummaryDTO;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliTila;
import fi.vm.sade.tarjonta.service.NoSuchOIDException;
import fi.vm.sade.tarjonta.service.KoulutusmoduuliAdminService;
import java.util.List;
import java.util.UUID;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class KoulutusmoduuliAdminServiceTest {

    @Autowired
    private KoulutusmoduuliAdminService adminService;

    @Test
    public void testNewKoulutusmoduuliIsInEditState() {

        KoulutusmoduuliDTO koulutusModuuli = adminService.createTutkintoOhjelma(null);
        assertEquals(KoulutusmoduuliTila.SUUNNITTELUSSA.name(), koulutusModuuli.getTila());
    }

    @Test
    public void testSavedKoulutusmoduuliHasOid() {

        KoulutusmoduuliDTO koulutusmoduuliDTO = adminService.createTutkintoOhjelma(null);
        assertNull(koulutusmoduuliDTO.getOid());

        koulutusmoduuliDTO = adminService.save(koulutusmoduuliDTO);
        assertNotNull(koulutusmoduuliDTO.getOid());

        koulutusmoduuliDTO = adminService.find(koulutusmoduuliDTO.getOid());
        assertNotNull(koulutusmoduuliDTO);
        assertNotNull(koulutusmoduuliDTO.getOid());
    }

    @Test
    public void testGetParentKoulutusmoduulit() {

        KoulutusmoduuliDTO koulutusmoduuliDTO = adminService.createTutkintoOhjelma(null);
        koulutusmoduuliDTO = adminService.save(koulutusmoduuliDTO);

        List<KoulutusmoduuliSummaryDTO> parents = adminService.getParentModuulis(koulutusmoduuliDTO.getOid());
        assertEquals(0, parents.size());


    }

    @Test(expected = NoSuchOIDException.class)
    public void testUnknownOIDThrowsException() {

        adminService.getParentModuulis(UUID.randomUUID().toString());

    }
    
    
    @Test
    public void testUpdate() {
        
        KoulutusmoduuliDTO koulutusmoduuli = adminService.createTutkintoOhjelma(null);
        koulutusmoduuli.setNimi("name");
        
        koulutusmoduuli = adminService.save(koulutusmoduuli);
        assertEquals("name", koulutusmoduuli.getNimi());
       
        
        
    }

}