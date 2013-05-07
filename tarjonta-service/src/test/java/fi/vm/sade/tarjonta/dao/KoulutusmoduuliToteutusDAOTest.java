package fi.vm.sade.tarjonta.dao;


import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.sade.tarjonta.TarjontaFixtures;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;

/**
 * Tests for KoulutusmoduuliTotetusDAO.
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class KoulutusmoduuliToteutusDAOTest {
    
    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
    
    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;
    
    @Autowired
    private TarjontaFixtures fixtures;

    
    @Test
    public void testFindKomotosByKomoTarjoajaPohjakoulutus() {
        String TARJOAJA_OID = "jokin.tarjoaja.oid";
        String KOMOTO_OID = "jokin.KOMOTO.oid.1.1.12.2." + System.currentTimeMillis();
        String POHJAKOULUTUS = "http://jokin.pohjakoulutus/yo";
        Koulutusmoduuli komo = fixtures.createTutkintoOhjelma();
        komo = this.koulutusmoduuliDAO.insert(komo);
        
        KoulutusmoduuliToteutus komoto = fixtures.createTutkintoOhjelmaToteutus();
        komoto.setOid(KOMOTO_OID);
        komoto.setTarjoaja(TARJOAJA_OID);
        komoto.setPohjakoulutusvaatimus(POHJAKOULUTUS);
        komoto.setKoulutusmoduuli(komo);
        komoto = this.koulutusmoduuliToteutusDAO.insert(komoto);
        
        KoulutusmoduuliToteutus komoto1 = fixtures.createTutkintoOhjelmaToteutus();
        komoto1.setTarjoaja(TARJOAJA_OID + "xxx");
        komoto1.setKoulutusmoduuli(komo);
        komoto1 = this.koulutusmoduuliToteutusDAO.insert(komoto1);
        
        KoulutusmoduuliToteutus result = this.koulutusmoduuliToteutusDAO.findKomotosByKomoTarjoajaPohjakoulutus(komo, TARJOAJA_OID, POHJAKOULUTUS).get(0);
        assertTrue(result.getOid().equals(KOMOTO_OID));        
        
    }
    
    @Test
    public void testReadKomoto() {
        String TARJOAJA_OID = "jokin.tarjoaja.oid";
        String KOMOTO_OID = "jokin.KOMOTO.oid.1.1.12.2." + System.currentTimeMillis();
        Koulutusmoduuli komo = fixtures.createTutkintoOhjelma();
        komo = this.koulutusmoduuliDAO.insert(komo);
        
        KoulutusmoduuliToteutus komoto = fixtures.createTutkintoOhjelmaToteutus();
        komoto.setOid(KOMOTO_OID);
        komoto.setTarjoaja(TARJOAJA_OID);
        komoto.setKoulutusmoduuli(komo);
        komoto = this.koulutusmoduuliToteutusDAO.insert(komoto);
        
        KoulutusmoduuliToteutus komotoRes = this.koulutusmoduuliToteutusDAO.findKomotoByOid(KOMOTO_OID);
        assertTrue(komotoRes.getOid().equals(KOMOTO_OID));     
    }

}
