package fi.vm.sade.tarjonta.dao;

import fi.vm.sade.tarjonta.TestUtilityBase;
import fi.vm.sade.tarjonta.model.MonikielinenMetadata;
import fi.vm.sade.tarjonta.service.enums.MetaCategory;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;


@Transactional
public class MonikielinenMetadataDAOTest extends TestUtilityBase {

    private static final Logger LOG = LoggerFactory.getLogger(MonikielinenMetadataDAOTest.class);
    public static final String AVAIN_0 = "avain0";
    public static final String AVAIN_1 = "avain1";
    public static final String AVAIN_2 = "avain2";
    public static final String AVAIN_3 = "avain3";
    public static final String CAT_A = MetaCategory.SORA_KUVAUS.toString();
    public static final String CAT_B = MetaCategory.VALINTAPERUSTEKUVAUS.toString();

    @Test
    public void testMonikielinenMetadata() {
        LOG.info("testMonikielinenMetadata()...");

        createMKMD(AVAIN_0, null, null, 1000);
        createMKMD(AVAIN_1, null, null, 2000);
        createMKMD(AVAIN_2, null, null, 4000);
        createMKMD(AVAIN_3, null, null, 8000);

        Assert.assertTrue("Must have 4 items in db", monikielinenMetadataDAO.findAll().size() == 4);
        printMD(monikielinenMetadataDAO.findAll());

        Assert.assertTrue(monikielinenMetadataDAO.findByAvain(AVAIN_0).size() == 1);
        Assert.assertTrue(monikielinenMetadataDAO.findByAvain(AVAIN_1).size() == 1);
        Assert.assertTrue(monikielinenMetadataDAO.findByAvain(AVAIN_2).size() == 1);
        Assert.assertTrue(monikielinenMetadataDAO.findByAvain(AVAIN_3).size() == 1);

        createMKMD(AVAIN_0, CAT_A, null, 16000);
        createMKMD(AVAIN_1, CAT_A, null, 32000);
        createMKMD(AVAIN_2, CAT_B, null, 64000);
        createMKMD(AVAIN_3, CAT_B, null, 128000);

        Assert.assertTrue("Must have 8 items in db", monikielinenMetadataDAO.findAll().size() == 8);
        // printMD(monikielinenMetadataDAO.findAll());

        Assert.assertTrue(monikielinenMetadataDAO.findByAvain(AVAIN_0).size() == 2);
        Assert.assertTrue(monikielinenMetadataDAO.findByAvain(AVAIN_1).size() == 2);
        Assert.assertTrue(monikielinenMetadataDAO.findByAvain(AVAIN_2).size() == 2);
        Assert.assertTrue(monikielinenMetadataDAO.findByAvain(AVAIN_3).size() == 2);

        Assert.assertTrue(monikielinenMetadataDAO.findByAvainAndKategoria(AVAIN_0, CAT_A).size() == 1);
        Assert.assertTrue(monikielinenMetadataDAO.findByAvainAndKategoria(AVAIN_1, CAT_A).size() == 1);
        Assert.assertTrue(monikielinenMetadataDAO.findByAvainAndKategoria(AVAIN_2, CAT_A).size() == 0);
        Assert.assertTrue(monikielinenMetadataDAO.findByAvainAndKategoria(AVAIN_3, CAT_A).size() == 0);

        Assert.assertTrue(monikielinenMetadataDAO.findByAvainAndKategoria(AVAIN_0, CAT_B).size() == 0);
        Assert.assertTrue(monikielinenMetadataDAO.findByAvainAndKategoria(AVAIN_1, CAT_B).size() == 0);
        Assert.assertTrue(monikielinenMetadataDAO.findByAvainAndKategoria(AVAIN_2, CAT_B).size() == 1);
        Assert.assertTrue(monikielinenMetadataDAO.findByAvainAndKategoria(AVAIN_3, CAT_B).size() == 1);


        // Create language dependant values
        MonikielinenMetadata mdFI = monikielinenMetadataDAO.createOrUpdate(AVAIN_0, CAT_A, "FI", createRandomData(100));
        MonikielinenMetadata mdEN = monikielinenMetadataDAO.createOrUpdate(AVAIN_0, CAT_A, "EN", createRandomData(100));
        MonikielinenMetadata mdSV = monikielinenMetadataDAO.createOrUpdate(AVAIN_0, CAT_A, "SV", createRandomData(100));
        Assert.assertTrue(monikielinenMetadataDAO.findByAvainAndKategoria(AVAIN_0, CAT_A).size() == 4);

        // Save version information
        Long fiOldVersion = mdFI.getVersion();
        Long enOldVersion = mdEN.getVersion();
        Long svOldVersion = mdSV.getVersion();

        // Update existing values
        MonikielinenMetadata mdFI2 = monikielinenMetadataDAO.createOrUpdate(AVAIN_0, CAT_A, "FI", createRandomData(100));
        MonikielinenMetadata mdEN2 = monikielinenMetadataDAO.createOrUpdate(AVAIN_0, CAT_A, "EN", createRandomData(100));
        MonikielinenMetadata mdSV2 = monikielinenMetadataDAO.createOrUpdate(AVAIN_0, CAT_A, "SV", createRandomData(100));
        Assert.assertTrue(monikielinenMetadataDAO.findByAvainAndKategoria(AVAIN_0, CAT_A).size() == 4);

        // The version number of updated entries should have changed
        Assert.assertTrue(fiOldVersion != mdFI2.getVersion());
        Assert.assertTrue(enOldVersion != mdEN2.getVersion());
        Assert.assertTrue(svOldVersion != mdSV2.getVersion());

        LOG.info("testMonikielinenMetadata()... done.");
    }

    private void printMD(List<MonikielinenMetadata> mds) {
        if (mds == null) {
            return;
        }

        LOG.info("Metadatas: size = {}", mds.size());
        for (MonikielinenMetadata md : mds) {
            LOG.info("  MD: avain={}, kategoria={}, kieli={}", md.getAvain(), md.getKategoria(), md.getKieli());
        }
    }

    private MonikielinenMetadata createMKMD(String avain, String kategoria, String kieli, int pituus) {
        LOG.debug("createMKMD({}, {}, {}, {})...", avain, kategoria, kieli, pituus);

        String arvo = createRandomData(pituus);

        MonikielinenMetadata md = monikielinenMetadataDAO.createOrUpdate(avain, kategoria, kieli, arvo);

        LOG.debug(" id = {}", md.getId());

        // Make sure everything was saved
        MonikielinenMetadata md2 = monikielinenMetadataDAO.read(md.getId());
        Assert.assertEquals(md.getArvo(), md2.getArvo());
        Assert.assertEquals(md.getKategoria(), md2.getKategoria());
        Assert.assertEquals(md.getKieli(), md2.getKieli());
        Assert.assertEquals(md.getAvain(), md2.getAvain());

        return md;
    }


    private String createRandomData(int pituus) {
        final String[] words = new String[]{
                "Tämä", "on", "testi", "suomi", "maailma", "Laphroaig", "Kolme leijonaa", "Koskenkorva", "paljon", "lauantai",
                "kalja", "kahvi", "pulla", "donitsi", "kuppi"
        };

        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();

        while (sb.length() < pituus) {
            sb.append(words[rnd.nextInt(words.length)]);
            sb.append(" ");
        }

        return sb.toString();
    }

}
