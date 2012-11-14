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
import static org.junit.Assert.*;

/**
 *
 * @author jani
 */
public class SearchWordUtilTest {

    private static final String NIMI1 = "nimi1";
    private static final String NIMI2 = "nimi2";
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
        nimi1.setValue(NIMI1);
        koodistoKoodiTyyppi1.getNimi().add(nimi1);
        tyyppi1.setKoulutusKoodi(koodistoKoodiTyyppi1);

        KoodistoKoodiTyyppi koodistoKoodiTyyppi2 = new KoodistoKoodiTyyppi();
        koodistoKoodiTyyppi2.setArvo(NIMI2);
        Nimi nimi2 = new KoodistoKoodiTyyppi.Nimi();
        nimi2.setValue(NIMI3);
        koodistoKoodiTyyppi2.getNimi().add(nimi2);
        tyyppi1.setKoulutusaste(koodistoKoodiTyyppi2);

        //TYYPPI3
        tyyppi2 = new KoulutusTyyppi();
        KoodistoKoodiTyyppi koodistoKoodiTyyppi3 = new KoodistoKoodiTyyppi();
        koodistoKoodiTyyppi3.setArvo(NIMI_TOO_LONG);
        tyyppi2.setKoulutusohjelmaKoodi(koodistoKoodiTyyppi3);
    }

    /**
     * Test of createSearchKeywords method, of class SearchWordUtil.
     */
    @Test
    public void testCreateSearchKeywords() {
        String result = SearchWordUtil.createSearchKeywords(tyyppi1);
        assertEquals("nimi1, nimi3, ", result);
    }

    /**
     * Test of appendTyyppi method, of class SearchWordUtil.
     */
    @Test
    public void testAppendTyyppi() {
         String result = SearchWordUtil.createSearchKeywords(tyyppi2);
        assertEquals(255, result.length());
    }
}
