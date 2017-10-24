package fi.vm.sade.tarjonta.dao;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import fi.vm.sade.tarjonta.TestUtilityBase;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.impl.resources.v1.OidServiceMock;
import fi.vm.sade.tarjonta.service.search.IndexDataUtils;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;


@Transactional
public class KoulutusmoduuliToteutusDAOTest extends TestUtilityBase {

    private static final String URI_EN = "kieli_en";
    private static final String URI_FI = "kieli_fi";
    private static final String FILENAME1 = "name1";
    private static final String FILENAME2 = "name2";
    private static final String MIME1 = "type1";
    private static final String MIME2 = "type2";

    @Autowired
    OidServiceMock oidServiceMock;

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
         * LINK_KOULUTUS TEST : KOMOTO A TO KOMOTO B
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
    public void testFindSameKoulutusWhenKomotoIsSame() {
        insertKomoto(getKomo(), "tarjoaja1", Lists.newArrayList("kieli_fi"), Lists.newArrayList("koulutuslaji_n"),
                "pk_n", "koulutuskoodi_1", "koulutusohjelma_1", TarjontaTila.LUONNOS);
        List<KoulutusmoduuliToteutus> matches = koulutusmoduuliToteutusDAO.findSameKoulutus("tarjoaja1", "pk_n", "koulutuskoodi_1", "koulutusohjelma_1",
                Lists.newArrayList("kieli_fi"), Lists.newArrayList("koulutuslaji_n"));
        assertEquals(1, matches.size());
    }

    @Test
    public void testFindSameKoulutusIgnoresDeleted() {
        insertKomoto(getKomo(), "tarjoaja1", Lists.newArrayList("kieli_fi"), Lists.newArrayList("koulutuslaji_n"),
                "pk_n", "koulutuskoodi_1", "koulutusohjelma_1", TarjontaTila.POISTETTU);
        List<KoulutusmoduuliToteutus> matches = koulutusmoduuliToteutusDAO.findSameKoulutus("tarjoaja1", "pk_n", "koulutuskoodi_1", "koulutusohjelma_1",
                Lists.newArrayList("kieli_fi"), Lists.newArrayList("koulutuslaji_n"));
        assertEquals(0, matches.size());
    }

    @Test
    public void testFindSameKoulutusIgnoresPeruttu() {
        insertKomoto(getKomo(), "tarjoaja1", Lists.newArrayList("kieli_fi"), Lists.newArrayList("koulutuslaji_n"),
                "pk_n", "koulutuskoodi_1", "koulutusohjelma_1", TarjontaTila.PERUTTU);
        List<KoulutusmoduuliToteutus> matches = koulutusmoduuliToteutusDAO.findSameKoulutus("tarjoaja1", "pk_n", "koulutuskoodi_1", "koulutusohjelma_1",
                Lists.newArrayList("kieli_fi"), Lists.newArrayList("koulutuslaji_n"));
        assertEquals(0, matches.size());
    }

    @Test
    public void testFindSameKoulutusDoesNotMatchWhenOpetuskielisDiffer() {
        insertKomoto(getKomo(), "tarjoaja1", Lists.newArrayList("kieli_fi"), Lists.newArrayList("koulutuslaji_n"),
                "pk_n", "koulutuskoodi_1", "koulutusohjelma_1", TarjontaTila.LUONNOS);
        insertKomoto(getKomo(), "otherTarjoaja", Lists.newArrayList("kieli_different"), Lists.newArrayList("koulutuslaji_n"),
                "pk_n", "koulutuskoodi_1", "koulutusohjelma_1", TarjontaTila.LUONNOS);
        List<KoulutusmoduuliToteutus> matches = koulutusmoduuliToteutusDAO.findSameKoulutus("tarjoaja1", "pk_n", "koulutuskoodi_1", "koulutusohjelma_1",
                Lists.newArrayList("kieli_different"), Lists.newArrayList("koulutuslaji_n"));
        assertEquals(0, matches.size());
    }

    @Test
    public void testFindSameKoulutusDoesNotMatchWhenKoulutuslajisDiffer() {
        insertKomoto(getKomo(), "tarjoaja1", Lists.newArrayList("kieli_fi"), Lists.newArrayList("koulutuslaji_n"),
                "pk_n", "koulutuskoodi_1", "koulutusohjelma_1", TarjontaTila.LUONNOS);
        insertKomoto(getKomo(), "otherTarjoaja", Lists.newArrayList("kieli_fi"), Lists.newArrayList("koulutuslaji_different"),
                "pk_n", "koulutuskoodi_1", "koulutusohjelma_1", TarjontaTila.LUONNOS);
        List<KoulutusmoduuliToteutus> matches = koulutusmoduuliToteutusDAO.findSameKoulutus("tarjoaja1", "pk_n", "koulutuskoodi_1", "koulutusohjelma_1",
                Lists.newArrayList("kieli_fi"), Lists.newArrayList("koulutuslaji_different"));
        assertEquals(0, matches.size());
    }

    @Test
    public void testFindSameKoulutusDoesNotMatchWhenKoulutuskoodiDiffers() {
        insertKomoto(getKomo(), "tarjoaja1", Lists.newArrayList("kieli_fi"), Lists.newArrayList("koulutuslaji_n"),
                "pk_n", "koulutuskoodi_1", "koulutusohjelma_1", TarjontaTila.LUONNOS);
        List<KoulutusmoduuliToteutus> matches = koulutusmoduuliToteutusDAO.findSameKoulutus("tarjoaja1", "pk_n", "koulutuskoodi_differ", "koulutusohjelma_1",
                Lists.newArrayList("kieli_fi"), Lists.newArrayList("koulutuslaji_n"));
        assertEquals(0, matches.size());
    }

    @Test
    public void testFindSameKoulutusDoesNotMatchWhenKoulutusohjelmaIsDifferent() {
        insertKomoto(getKomo(), "tarjoaja1", Lists.newArrayList("kieli_fi"), Lists.newArrayList("koulutuslaji_n"),
                "pk_n", "koulutuskoodi_1", "koulutusohjelma_1", TarjontaTila.LUONNOS);
        List<KoulutusmoduuliToteutus> matches = koulutusmoduuliToteutusDAO.findSameKoulutus("tarjoaja1", "pk_n", "koulutuskoodi_1", "koulutusohjelma_differ",
                Lists.newArrayList("kieli_fi"), Lists.newArrayList("koulutuslaji_n"));
        assertEquals(0, matches.size());
    }

    @Test
    public void testFindSameKoulutusDoesNotMatchWhenTarjoajaIsDifferent() {
        insertKomoto(getKomo(), "tarjoaja1", Lists.newArrayList("kieli_fi"), Lists.newArrayList("koulutuslaji_n"),
                "pk_n", "koulutuskoodi_1", "koulutusohjelma_1", TarjontaTila.LUONNOS);
        List<KoulutusmoduuliToteutus> matches = koulutusmoduuliToteutusDAO.findSameKoulutus("tarjoaja_differ", "pk_n", "koulutuskoodi_1", "koulutusohjelma_1",
                Lists.newArrayList("kieli_fi"), Lists.newArrayList("koulutuslaji_n"));
        assertEquals(0, matches.size());
    }

    @Test
    public void testFindSameKoulutusDoesNotMatchWhenPkVaatimusIsDifferent() {
        insertKomoto(getKomo(), "tarjoaja1", Lists.newArrayList("kieli_fi"), Lists.newArrayList("koulutuslaji_n"),
                "pk_n", "koulutuskoodi_1", "koulutusohjelma_1", TarjontaTila.LUONNOS);
        List<KoulutusmoduuliToteutus> matches = koulutusmoduuliToteutusDAO.findSameKoulutus("tarjoaja1", "pk_differ", "koulutuskoodi_1", "koulutusohjelma_1",
                Lists.newArrayList("kieli_fi"), Lists.newArrayList("koulutuslaji_n"));
        assertEquals(0, matches.size());
    }

    @Test
    public void testFindSameKoulutusWhenNoKoulutusohjelma() {
        insertKomoto(getKomo(), "tarjoaja1", Lists.newArrayList("kieli_fi"), Lists.newArrayList("koulutuslaji_n"),
                "pk_n", "koulutuskoodi_1", null, TarjontaTila.LUONNOS);
        List<KoulutusmoduuliToteutus> matches = koulutusmoduuliToteutusDAO.findSameKoulutus("tarjoaja1", "pk_n", "koulutuskoodi_1", null,
                Lists.newArrayList("kieli_fi"), Lists.newArrayList("koulutuslaji_n"));
        assertEquals(1, matches.size());
    }

    @Test
    public void testFindSameKoulutusWhenKomotoIsSameButKoodiversionDiffer() {
        insertKomoto(getKomo(), "tarjoaja1", Lists.newArrayList("kieli_fi#1"), Lists.newArrayList("koulutuslaji_n#1"),
                "pk_n#1", "koulutuskoodi_1#1", "koulutusohjelma_1#1", TarjontaTila.LUONNOS);
        List<KoulutusmoduuliToteutus> matches = koulutusmoduuliToteutusDAO.findSameKoulutus("tarjoaja1", "pk_n#2", "koulutuskoodi_1#2", "koulutusohjelma_1#2",
                Lists.newArrayList("kieli_fi#2"), Lists.newArrayList("koulutuslaji_n#2"));
        assertEquals(1, matches.size());
    }

    private Koulutusmoduuli getKomo() {
        String KOMO_OID = "testKomoOid";
        Koulutusmoduuli komo = koulutusmoduuliDAO.findByOid(KOMO_OID);
        if (komo == null) {
            komo = new Koulutusmoduuli();
            komo.setOid(KOMO_OID);
            return koulutusmoduuliDAO.insert(komo);
        } else {
            return komo;
        }
    }

    private void insertKomoto(Koulutusmoduuli komo, String tarjoaja, List<String> opetuskielis,
                              List<String> koulutuslajis, String pohjakoulutusvaatimus, String koulutuskoodi,
                              String koulutusohjelma, TarjontaTila tila) {
        KoulutusmoduuliToteutus komoto = new KoulutusmoduuliToteutus();
        komoto.setOid(oidServiceMock.getOid());
        komoto.setTarjoaja(tarjoaja);
        komoto.setKoulutusmoduuli(komo);
        komoto.setOpetuskieli(toKoodistoUri(opetuskielis));
        komoto.setKoulutuslajis(toKoodistoUri(koulutuslajis));
        komoto.setPohjakoulutusvaatimusUri(pohjakoulutusvaatimus);
        komoto.setKoulutusUri(koulutuskoodi);
        komoto.setOsaamisalaUri(koulutusohjelma);
        komoto.setTila(tila == null ? TarjontaTila.LUONNOS : tila);

        koulutusmoduuliToteutusDAO.insert(komoto);
    }

    private Set<KoodistoUri> toKoodistoUri(List<String> koodis) {
        return FluentIterable.from(koodis).transform(new Function<String, KoodistoUri>() {
            @Override
            public KoodistoUri apply(String input) {
                return new KoodistoUri(input);
            }
        }).toSet();
    }

}
