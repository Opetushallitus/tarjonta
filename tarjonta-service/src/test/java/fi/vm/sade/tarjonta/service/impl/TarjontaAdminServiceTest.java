package fi.vm.sade.tarjonta.service.impl;

import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliDTO;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliTila;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
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
public class TarjontaAdminServiceTest {

    @Autowired
    private TarjontaAdminService adminService;
    
    @Test
    public void testNewKoulutusmoduuliIsInEditState() {
        
        KoulutusmoduuliDTO koulutusModuuli = adminService.createTutkintoOhjelma(null);
        assertEquals(KoulutusmoduuliTila.SUUNNITELUSSA.name(), koulutusModuuli.getTila());
        
    }
    
    
    @Test
    public void testSavedKoulutusmoduuliHasOid() {
        
        KoulutusmoduuliDTO koulutusmoduuliDTO = adminService.createTutkintoOhjelma(null);
        assertNull(koulutusmoduuliDTO.getOid());
        
        koulutusmoduuliDTO = adminService.save(koulutusmoduuliDTO);        
        assertNotNull(koulutusmoduuliDTO.getOid());
        
    }

}