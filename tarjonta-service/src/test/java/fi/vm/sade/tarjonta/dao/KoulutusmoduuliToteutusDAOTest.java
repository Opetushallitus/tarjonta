package fi.vm.sade.tarjonta.dao;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import fi.vm.sade.tarjonta.model.KoulutusOwner;
import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
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

import java.util.Calendar;
import java.util.Map;

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

        KoulutusmoduuliToteutus findByOid = koulutusmoduuliToteutusDAO.findByOid(komoto.getOid());
        assertNotNull("no komoto object found by findByOid?", findByOid);
        assertEquals(komoto.getOid(), findByOid.getOid());

        findByOid = koulutusmoduuliToteutusDAO.findKomotoByOid(komoto.getOid());
        assertNotNull("no komoto object found by findKomotoByOid?", findByOid);
        assertEquals(komoto.getOid(), findByOid.getOid());

        KoulutusmoduuliToteutus komoto1 = fixtures.createTutkintoOhjelmaToteutus();
        komoto1.setTarjoaja(TARJOAJA_OID + "xxx");
        komoto1.setKoulutusmoduuli(komo);
        komoto1 = this.koulutusmoduuliToteutusDAO.insert(komoto1);
        komoto1.setKuvaByUri(URI_EN, fixtures.createBinaryData(FILENAME1, MIME1));
        this.koulutusmoduuliToteutusDAO.update(komoto1);

        KoulutusmoduuliToteutus komoto2 = fixtures.createTutkintoOhjelmaToteutus();
        komoto2.setTarjoaja(TARJOAJA_OID + "xxx");
        komoto2.setKoulutusmoduuli(komo);
        komoto2 = this.koulutusmoduuliToteutusDAO.insert(komoto2);
        komoto2.setKuvaByUri(URI_FI, fixtures.createBinaryData(FILENAME2, MIME2));
        this.koulutusmoduuliToteutusDAO.update(komoto2);

        KoulutusmoduuliToteutus result = this.koulutusmoduuliToteutusDAO.findKomotosByKomoTarjoajaPohjakoulutus(komo, TARJOAJA_OID, POHJAKOULUTUS).get(0);
        assertTrue(result.getOid().equals(KOMOTO_OID));

//        KoulutusmoduuliToteutus a = this.koulutusmoduuliToteutusDAO.findByOid(komoto1.getOid());
//        assertEquals(1, a.getImageLangsUris().size());
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

        Map<String, BinaryData> map = koulutusmoduuliToteutusDAO.findAllImagesByKomotoOid(komoto1.getOid());
        assertNotNull(map);
        assertEquals("count of images", 1, map.size());
        assertNotNull(map.toString(), map.get(URI_EN));
        assertEquals(FILENAME1, map.get(URI_EN).getFilename());
        assertEquals(MIME1, map.get(URI_EN).getMimeType());
        assertEquals(map.get(URI_EN).getData()[0], 1);

        map = koulutusmoduuliToteutusDAO.findAllImagesByKomotoOid(komoto2.getOid());
        assertNotNull(map);
        assertEquals("count of images", 1, map.size());
        assertEquals(FILENAME2, map.get(URI_FI).getFilename());
        assertEquals(MIME2, map.get(URI_FI).getMimeType());
        assertEquals(map.get(URI_FI).getData()[0], 1);

        /*
         * LINK TEST : KOMOTO A TO KOMOTO B
         */
        KoulutusmoduuliToteutus linkedKomoto1 = koulutusmoduuliToteutusDAO.findKomotoByOid(komoto1.getOid());
        KoulutusmoduuliToteutus linkedKomoto2 = koulutusmoduuliToteutusDAO.findKomotoByOid(komoto2.getOid());

        linkedKomoto1.setValmistavaKoulutus(linkedKomoto2);
        this.koulutusmoduuliToteutusDAO.update(linkedKomoto1);

        linkedKomoto1 = koulutusmoduuliToteutusDAO.findByOid(linkedKomoto1.getOid());
        assertNotNull("no base link komoto object found by findByOid?", linkedKomoto1);
        assertNotNull("no linked komoto object found by findByOid?", linkedKomoto1.getValmistavaKoulutus());
        assertEquals(linkedKomoto2, linkedKomoto1.getValmistavaKoulutus());
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

        {
            KoulutusOwner owner = new KoulutusOwner();
            owner.setOwnerOid(TARJOAJA_OID);
            owner.setOwnerType("TEST1");
            komoto.getOwners().add(owner);
        }
        {
            KoulutusOwner owner = new KoulutusOwner();
            owner.setOwnerOid(TARJOAJA_OID);
            owner.setOwnerType("TEST2");
            komoto.getOwners().add(owner);
        }

        komoto = this.koulutusmoduuliToteutusDAO.insert(komoto);

        KoulutusmoduuliToteutus komotoRes = this.koulutusmoduuliToteutusDAO.findKomotoByOid(KOMOTO_OID);
        assertTrue(komotoRes.getOid().equals(KOMOTO_OID));
        assertEquals("There should be 2 'owners' for the komoto", 2, komotoRes.getOwners().size());

        for(KoulutusOwner owner : komotoRes.getOwners()) {
            assertEquals("Tarjoaja oid", TARJOAJA_OID, owner.getOwnerOid());
            assertTrue("Type starts with TEST", owner.getOwnerType().startsWith("TEST"));
        }
    }

    @Test
    public void thatMinAndMaxAlkamisPvmIsReturned() {
        KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();

        DateTime minDate = new DateTime();
        minDate.withYear(2013);
        DateTime maxDate = new DateTime();
        maxDate.withYear(2014);

        komoto.addKoulutuksenAlkamisPvms(minDate.toDate());
        komoto.addKoulutuksenAlkamisPvms(maxDate.toDate());

        assertEquals(DateUtils.truncate(minDate.toDate(), Calendar.DATE), komoto.getMinAlkamisPvm());
        assertEquals(DateUtils.truncate(maxDate.toDate(), Calendar.DATE), komoto.getMaxAlkamisPvm());

        komoto.getKoulutuksenAlkamisPvms().clear();

        assertNull(komoto.getMinAlkamisPvm());
        assertNull(komoto.getMaxAlkamisPvm());
    }
}
