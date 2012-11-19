package fi.vm.sade.tarjonta.ui.helper.conversion;

import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
import fi.vm.sade.tarjonta.ui.model.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.NimiModel;
import java.util.Locale;
import java.util.Map;
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
    private KoulutuskoodiModel KoulutuskoodiModel;
    
    @Before
    public void setUp() {
        koodistoTyyppi1 = new KoodistoKoodiTyyppi();
        koodistoTyyppi1.setArvo(ARVO);
        koodistoTyyppi1.setUri(URI);
        koodistoTyyppi1.setVersio(VERSION);
        
        KoulutuskoodiModel = new KoulutuskoodiModel();
        KoulutuskoodiModel.setNimi(NIMI);
        KoulutuskoodiModel.setKoodistoUri(URI);
        KoulutuskoodiModel.setKoodistoUriVersio(URI_VERSION);
        KoulutuskoodiModel.setKoodistoVersio(VERSION);
        KoulutuskoodiModel.setKoodi(KOODI);
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
        KoodistoKoodiTyyppi result = KoulutusViewModelToDTOConverter.mapToValidKoodistoKoodiTyyppi(false, KoulutuskoodiModel);
        assertNotNull("KoodistoKoodiTyyppi obj cannot be null", result);
        assertEquals(NIMI, result.getArvo());
        assertEquals(0, result.getNimi().size());
        assertEquals(URI_VERSION, result.getUri());
        assertEquals(null, result.getVersio());
    }
    
    @Test
    public void testMultilanguageKomotoName() {
        NimiModel nm = new NimiModel();
        nm.setNimi("nimi");
        nm.setType(KieliType.FI);
        
        KoulutusToisenAsteenPerustiedotViewModel model = new KoulutusToisenAsteenPerustiedotViewModel(DocumentStatus.NEW);
        KoulutuskoodiModel kkm = new KoulutuskoodiModel();
        kkm.getKielet().add(nm);
        model.setKoulutuskoodiModel(kkm);
        Map<KieliType, StringBuilder> multilanguageKomotoName = KoulutusViewModelToDTOConverter.multilanguageKomotoName(model);
        
        assertNotNull(multilanguageKomotoName.get(KieliType.FI));
    }
}
