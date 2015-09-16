package fi.vm.sade.tarjonta.dao;

import com.google.common.collect.Lists;
import fi.vm.sade.tarjonta.TestUtilityBase;
import fi.vm.sade.tarjonta.model.BinaryData;
import fi.vm.sade.tarjonta.model.KoulutusOwner;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.search.IndexDataUtils;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;


@Transactional
public class KoulutusmoduuliToteutusDAOTest extends TestUtilityBase {

    private static final String URI_EN = "kieli_en";
    private static final String URI_FI = "kieli_fi";
    private static final String FILENAME1 = "name1";
    private static final String FILENAME2 = "name2";
    private static final String MIME1 = "type1";
    private static final String MIME2 = "type2";

    private void insertFutureKomoto(TarjontaTila tila, Integer alkamisvuosi, ToteutustyyppiEnum tyyppi) {
        Koulutusmoduuli komo = fixtures.createTutkintoOhjelma();
        komo = this.koulutusmoduuliDAO.insert(komo);

        if (alkamisvuosi == null) {
            alkamisvuosi = IndexDataUtils.parseYearInt(new Date());
        }

        if (tyyppi == null) {
            tyyppi = ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO;
        }

        KoulutusmoduuliToteutus komoto = fixtures.createTutkintoOhjelmaToteutus();
        komoto.setTarjoaja("1.2.3");
        komoto.setToteutustyyppi(tyyppi);
        komoto.setKoulutusmoduuli(komo);
        komoto.setTila(tila);
        komoto.setAlkamisVuosi(alkamisvuosi);
        komoto.setAlkamiskausiUri("kausi_s#1");
        this.koulutusmoduuliToteutusDAO.insert(komoto);
    }

    @Test
    public void testThatFutureKoulutuksesReturnsOnlyKomotosWithValidTila() {
        insertFutureKomoto(TarjontaTila.JULKAISTU, null, null);
        insertFutureKomoto(TarjontaTila.POISTETTU, null, null);
        insertFutureKomoto(TarjontaTila.LUONNOS, null, null);
        insertFutureKomoto(TarjontaTila.PERUTTU, null, null);
        insertFutureKomoto(TarjontaTila.KOPIOITU, null, null);

        List<KoulutusmoduuliToteutus> komotos = this.koulutusmoduuliToteutusDAO.findFutureKoulutukset(
                Lists.newArrayList(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO), 0, 100
        );

        assertEquals(3, komotos.size());
    }

    @Test
    public void testThatFutureKoulutuksesDoesNotReturnKomotosFromPast() {
        insertFutureKomoto(TarjontaTila.JULKAISTU, 2010, null);
        insertFutureKomoto(TarjontaTila.POISTETTU, 2010, null);
        insertFutureKomoto(TarjontaTila.LUONNOS, 2010, null);
        insertFutureKomoto(TarjontaTila.PERUTTU, 2010, null);
        insertFutureKomoto(TarjontaTila.KOPIOITU, 2010, null);

        List<KoulutusmoduuliToteutus> komotos = this.koulutusmoduuliToteutusDAO.findFutureKoulutukset(
                Lists.newArrayList(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO), 0, 100
        );

        assertEquals(0, komotos.size());
    }

    @Test
    public void testThatFutureKoulutuksesOnlyReturnsSpecifiedToteutustyyppis() {
        insertFutureKomoto(TarjontaTila.JULKAISTU, null, ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO);
        insertFutureKomoto(TarjontaTila.JULKAISTU, null, ToteutustyyppiEnum.KORKEAKOULUOPINTO);
        insertFutureKomoto(TarjontaTila.JULKAISTU, null, ToteutustyyppiEnum.LUKIOKOULUTUS);

        List<KoulutusmoduuliToteutus> komotos = this.koulutusmoduuliToteutusDAO.findFutureKoulutukset(
                Lists.newArrayList(ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO), 0, 100
        );

        assertEquals(1, komotos.size());
    }

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

}
