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
import fi.vm.sade.tarjonta.model.BinaryData;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;

/**
 * Tests for KoulutusmoduuliTotetusDAO.
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class KoulutusmoduuliToteutusDAOTest {

    private static final String URI_EN = "kieli_en";
    private static final String URI_FI = "kieli_fi";
    private static final String FILENAME1 = "name1";
    private static final String FILENAME2 = "name2";
    private static final String MIME1 = "type1";
    private static final String MIME2 = "type2";

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
        komoto.setPohjakoulutusvaatimusUri(POHJAKOULUTUS);
        komoto.setKoulutusmoduuli(komo);
        komoto = this.koulutusmoduuliToteutusDAO.insert(komoto);

        KoulutusmoduuliToteutus komoto1 = fixtures.createTutkintoOhjelmaToteutus();
        komoto1.setTarjoaja(TARJOAJA_OID + "xxx");
        komoto1.setKoulutusmoduuli(komo);
        komoto1 = this.koulutusmoduuliToteutusDAO.insert(komoto1);
        komoto1.setKuvaByUri(URI_EN, fixtures.createBinaryData(FILENAME1, MIME1));

        KoulutusmoduuliToteutus komoto2 = fixtures.createTutkintoOhjelmaToteutus();
        komoto2.setTarjoaja(TARJOAJA_OID + "xxx");
        komoto2.setKoulutusmoduuli(komo);
        komoto2 = this.koulutusmoduuliToteutusDAO.insert(komoto2);
        komoto2.setKuvaByUri(URI_FI, fixtures.createBinaryData(FILENAME2, MIME2));

        KoulutusmoduuliToteutus result = this.koulutusmoduuliToteutusDAO.findKomotosByKomoTarjoajaPohjakoulutus(komo, TARJOAJA_OID, POHJAKOULUTUS).get(0);
        assertTrue(result.getOid().equals(KOMOTO_OID));

        BinaryData binaryData = koulutusmoduuliToteutusDAO.findKuvaByKomotoOidAndKieliUri(komoto1.getOid(), "no_result_uri");
        assertNull("found data object?", binaryData);
        binaryData = koulutusmoduuliToteutusDAO.findKuvaByKomotoOidAndKieliUri(komoto1.getOid(), URI_EN);
        assertNotNull(binaryData);
        assertEquals(FILENAME1, binaryData.getFilename());
        assertEquals(MIME1, binaryData.getMimeType());
        assertEquals(binaryData.getData()[0], 1);

        binaryData = koulutusmoduuliToteutusDAO.findKuvaByKomotoOidAndKieliUri(komoto2.getOid(), URI_FI);
        assertNotNull(binaryData);
        assertEquals(FILENAME2, binaryData.getFilename());
        assertEquals(MIME2, binaryData.getMimeType());

        assertEquals(binaryData.getData()[0], 1);
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
