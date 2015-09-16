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
package fi.vm.sade.tarjonta.model.util;

import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi.Nimi;
import fi.vm.sade.tarjonta.service.types.KoulutusTyyppi;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author jani
 */
public class SearchWordUtilTest {

    private static final String LANG_CODE_EN = "EN";
    private static final String LANG_CODE_FI = "FI";
    private static final String NIMI_FI_1 = "nimi1";
    private static final String NIMI_EN_1 = "name1";
    private static final String NIMI_FI_2 = "nimi2";
    private static final String NIMI_EN_2 = "name2";
    private static final String NIMI3 = "nimi3";
    private static final String NIMI_TOO_LONG = "123456789012345678901234567890"
            + "123456789012345678901234567890"
            + "123456789012345678901234567890"
            + "123456789012345678901234567890"
            + "123456789012345678901234567890"
            + "123456789012345678901234567890"
            + "123456789012345678901234567890"
            + "123456789012345678901234567890"
            + "123456789012345678901234567890"
            + "123456789012345678901234567890"
            + "123456789012345678901234567890"
            + "123456789012345678901234567890"
            + "123456789012345678901234567890"
            + "123456789012345678901234567890"
            + "123456789012345678901234567890";
    private static KoulutusTyyppi tyyppi1;
    private static KoulutusTyyppi tyyppi2;

    @Before
    public void setUp() {
        //TYYPPI1
        tyyppi1 = new KoulutusTyyppi();
        KoodistoKoodiTyyppi koodistoKoodiTyyppi1 = new KoodistoKoodiTyyppi();
        Nimi nimi1 = new KoodistoKoodiTyyppi.Nimi();
        nimi1.setKieli(LANG_CODE_FI);
        nimi1.setValue(NIMI_FI_1);

        Nimi nimi2 = new KoodistoKoodiTyyppi.Nimi();
        nimi2.setKieli(LANG_CODE_EN);
        nimi2.setValue(NIMI_EN_1);

        koodistoKoodiTyyppi1.getNimi().add(nimi1);
        koodistoKoodiTyyppi1.getNimi().add(nimi2);
        tyyppi1.setKoulutusKoodi(koodistoKoodiTyyppi1);

        KoodistoKoodiTyyppi koodistoKoodiTyyppi2 = new KoodistoKoodiTyyppi();
        koodistoKoodiTyyppi2.setArvo(NIMI_FI_2);
        Nimi nimi3 = new KoodistoKoodiTyyppi.Nimi();
        nimi3.setKieli(LANG_CODE_FI);
        nimi3.setValue(NIMI_FI_2);

        Nimi nimi4 = new KoodistoKoodiTyyppi.Nimi();
        nimi4.setKieli(LANG_CODE_EN);
        nimi4.setValue(NIMI_EN_2);

        koodistoKoodiTyyppi2.getNimi().add(nimi3);
        koodistoKoodiTyyppi2.getNimi().add(nimi4);
        //  tyyppi1.setKoulutusaste(koodistoKoodiTyyppi2);
    }

    /**
     * Test of createSearchKeywords method, of class SearchWordUtil.
     */
    @Test
    public void testCreateSearchKeywords() {
        Map<String, String> createSearchKeywords = SearchWordUtil.createSearchKeywords(tyyppi1);
        assertEquals(2, createSearchKeywords.size());

//        assertEquals("nimi1, nimi2", createSearchKeywords.get(LANG_CODE_FI));
//        assertEquals("name1, name2", createSearchKeywords.get(LANG_CODE_EN));

        assertEquals("nimi1", createSearchKeywords.get(LANG_CODE_FI));
        assertEquals("name1", createSearchKeywords.get(LANG_CODE_EN));
    }
}
