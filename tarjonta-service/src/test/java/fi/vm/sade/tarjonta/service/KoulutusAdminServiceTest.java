package fi.vm.sade.tarjonta.service;

import fi.vm.sade.tarjonta.KoodistoContract;
import fi.vm.sade.tarjonta.service.impl.KoulutusAdminServiceImpl;
import fi.vm.sade.tarjonta.service.tarjonta.koulutus._2012._09._04.KoulutusTyyppi;
import fi.vm.sade.tarjonta.service.tarjonta.koulutus._2012._09._04.KoulutusmoduuliTyyppi;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class KoulutusAdminServiceTest {

    @Autowired
    private KoulutusAdminService adminService;
    
    private KoulutusTyyppi koulutusWithNoInstance;
    
    @Before
    public void setUp() {
        
        koulutusWithNoInstance = new KoulutusTyyppi();
        
        KoulutusmoduuliTyyppi moduuli = new KoulutusmoduuliTyyppi();
        
        // moduulinTyyppi is a koodisto uri, use "known" values
        moduuli.setModuulinTyyppi(KoodistoContract.ModuuliTyypit.TUTKINTO_OHJELMA);
        // mandatory 
        moduuli.setOid("http://koulutus/123456");
        moduuli.setKoulutusLuokitusKoodi("12345");
        moduuli.setTutkintoOhjelmanNimi("Masters Degree in JUnit testing");
        
        
        koulutusWithNoInstance.setKoulutusModuuli(moduuli);
    }

    @Test
    public void testRekisterinpitajaCreatesNewKoulutusmoduuli() {

        setUserIsRekisterinpitaja(true);

        KoulutusTyyppi created = adminService.createKoulutus(koulutusWithNoInstance);

        assertNotNull(created);

    }

    /**
     * Forces admin service to detect user as rekisterinpitaja or virkailija.
     *
     * @param isRekisterinpitaja
     */
    private void setUserIsRekisterinpitaja(final boolean isRekisterinpitaja) {


        try {
            
            // get the actual impl
            KoulutusAdminServiceImpl impl = (KoulutusAdminServiceImpl) ((Advised) adminService).getTargetSource().getTarget();
            
            // overwrite role resolver
            impl.setRoleResolver(new KoulutusAdminServiceImpl.UserRoleResolver() {

                @Override
                public boolean isUserRekisterinpitaja() {
                    return isRekisterinpitaja;
                }

                @Override
                public boolean isUserVirkailija() {
                    return !isRekisterinpitaja;
                }

            });

        } catch (Exception e) {
            throw new RuntimeException("getting impl failed", e);
        }


    }

}