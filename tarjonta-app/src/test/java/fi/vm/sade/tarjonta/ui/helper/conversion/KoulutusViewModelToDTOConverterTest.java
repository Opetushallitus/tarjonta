package fi.vm.sade.tarjonta.ui.helper.conversion;

import fi.vm.sade.tarjonta.service.types.koodisto.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.koodisto.KoulutuskoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusasteModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jani
 */
public class KoulutusViewModelToDTOConverterTest {

    private static final String KOODI = "123456";
    private static final String ARVO = "value";
    private static final String NIMI = "nimi";
    private static final String URI = "URI";
    private static final int VERSION = 999;
    private static final String URI_VERSION = URI + "#" + VERSION;
    private KoodistoKoodiTyyppi koodistoTyyppi1;
    private KoulutusasteModel koulutuasteModel1;
    private KoulutuskoodiModel koulutuskoodiModel1;

    @Before
    public void setUp() {
        koodistoTyyppi1 = new KoodistoKoodiTyyppi();
        koodistoTyyppi1.setArvo(ARVO);
        koodistoTyyppi1.setUri(URI);
        koodistoTyyppi1.setVersio(VERSION);

        koulutuasteModel1 = new KoulutusasteModel();
        koulutuasteModel1.setNimi(NIMI);
        koulutuasteModel1.setKoodistoUri(URI);
        koulutuasteModel1.setKoodistoUriVersio(URI_VERSION);
        koulutuasteModel1.setKoodistoVersio(VERSION);
        koulutuasteModel1.setKoodi(KOODI);
    }

    /**
     * Test of mapToKoulutuskoodiTyyppi method, of class
     * KoulutusViewModelToDTOConverter.
     */
    //@Test
    public void testMapToKoulutuskoodiTyyppi_KoodistoKoodiTyyppi() {
        System.out.println("mapToKoulutuskoodiTyyppi");
        KoulutusViewModelToDTOConverter instance = new KoulutusViewModelToDTOConverter();
        KoulutuskoodiModel result = instance.mapToKoulutuskoodiModel(koodistoTyyppi1, new Locale("fi"));
        assertEquals(URI, result.getKoodistoUri());
        assertEquals(URI_VERSION, result.getKoodistoUriVersio());
        assertEquals(VERSION + "", result.getKoodistoVersio());
    }

    /**
     * Test of mapToKoulutusasteTyyppi method, of class
     * KoulutusViewModelToDTOConverter.
     */
    //@Test
    public void testMapToKoulutusasteTyyppi_KoodistoKoodiTyyppi() {
        KoulutusViewModelToDTOConverter instance = new KoulutusViewModelToDTOConverter();
        KoulutusasteModel result = instance.mapToKoulutusasteModel(koodistoTyyppi1, new Locale("fi"));
        assertEquals(URI, result.getKoodistoUri());
        assertEquals(URI_VERSION, result.getKoodistoUriVersio());
        assertEquals(VERSION + "", result.getKoodistoVersio());
    }

    /**
     * Test of mapToVersionUri method, of class KoulutusViewModelToDTOConverter.
     */
    @Test
    public void testMapToVersionUri() {
        final String uri = "uri: abc1234567";
        final String result = KoulutusViewModelToDTOConverter.mapToVersionUri(uri, 10);
        assertEquals(uri + "#10", result);
    }

    @Test
    public void mapToKoodistoKoodiTyyppi() {
        KoodistoKoodiTyyppi result = KoulutusViewModelToDTOConverter.mapToValidKoodistoKoodiTyyppi(false, koulutuasteModel1);
        assertNotNull("KoodistoKoodiTyyppi obj cannot be null", result);
        assertEquals(NIMI, result.getArvo());
        assertEquals(0, result.getNimi().size());
        assertEquals(URI_VERSION, result.getUri());
        assertEquals(null, result.getVersio());
    }
}
