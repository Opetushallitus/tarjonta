package fi.vm.sade.tarjonta.ui.helper.conversion;

import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
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
        KoulutusConverter instance = new KoulutusConverter();
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
        final String result = KoulutusConverter.mapToVersionUri(uri, 10);
        assertEquals(uri + "#10", result);
    }
    
    @Test
    public void mapToKoodistoKoodiTyyppi() {
        KoodistoKoodiTyyppi result = KoulutusConverter.mapToValidKoodistoKoodiTyyppi(false, KoulutuskoodiModel);
        assertNotNull("KoodistoKoodiTyyppi obj cannot be null", result);
        assertEquals(NIMI, result.getArvo());
        assertEquals(0, result.getNimi().size());
        assertEquals(URI_VERSION, result.getUri());
        assertEquals(null, result.getVersio());
    }
    
    @Test
    public void testMultilanguageKomotoName() {   
        KielikaannosViewModel langModel = new KielikaannosViewModel();
        langModel.setNimi("nimi");
        langModel.setKielikoodi(KieliType.FI.name());
        Set<KielikaannosViewModel> languages = new HashSet<KielikaannosViewModel>(1);
        languages.add(langModel);
        
        KoulutusToisenAsteenPerustiedotViewModel model = new KoulutusToisenAsteenPerustiedotViewModel(DocumentStatus.NEW);
        KoulutuskoodiModel kkm = new KoulutuskoodiModel();
        kkm.setKielikaannos(languages);
        model.setKoulutuskoodiModel(kkm);
        Map<String, StringBuilder> multilanguageKomotoName = KoulutusConverter.multilanguageKomotoName(model);
        
        assertNotNull(multilanguageKomotoName.get(KieliType.FI.name()));
    }
}
