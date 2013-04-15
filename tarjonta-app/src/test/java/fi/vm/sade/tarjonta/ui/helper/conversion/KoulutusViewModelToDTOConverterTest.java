/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.tarjonta.ui.helper.conversion;

import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
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
        Koulutus2asteConverter instance = new Koulutus2asteConverter();
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
        final String result = Koulutus2asteConverter.mapToVersionUri(uri, 10);
        assertEquals(uri + "#10", result);
    }
    
    @Test
    public void mapToKoodistoKoodiTyyppi() {
        KoodistoKoodiTyyppi result = Koulutus2asteConverter.mapToValidKoodistoKoodiTyyppi(false, KoulutuskoodiModel);
        assertNotNull("KoodistoKoodiTyyppi obj cannot be null", result);
        assertEquals(NIMI, result.getArvo());
        assertEquals(0, result.getNimi().size());
        assertEquals(URI_VERSION, result.getUri());
        assertEquals(null, result.getVersio());
    }
   
}
