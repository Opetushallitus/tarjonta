/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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
 */
package fi.vm.sade.tarjonta.shared;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jani
 */
public class TarjontaKoodistoHelperTest {

    @Before
    public void setUp() {
    }

    /**
     * Test of convertKieliUriToKielikoodi method, of class
     * TarjontaKoodistoHelper.
     */
    @Test
    public void testConvertKieliUriToKielikoodi() {
        assertEquals("en", TarjontaKoodistoHelper.convertKieliUriToKielikoodi("kieli_en"));
        assertEquals("fi", TarjontaKoodistoHelper.convertKieliUriToKielikoodi("kieli_fi"));
        assertEquals("sv", TarjontaKoodistoHelper.convertKieliUriToKielikoodi("kieli_sv"));
        assertEquals("sv", TarjontaKoodistoHelper.convertKieliUriToKielikoodi("KIELI_SV"));
        assertEquals("ee", TarjontaKoodistoHelper.convertKieliUriToKielikoodi("KIELI_EE"));
    }

    @Test
    public void testConvertKieliUriToKielikoodiWithVersion() {
        assertEquals("en", TarjontaKoodistoHelper.convertKieliUriToKielikoodi("kieli_en#1"));
        assertEquals("fi", TarjontaKoodistoHelper.convertKieliUriToKielikoodi("kieli_fi#123"));
        assertEquals("ee", TarjontaKoodistoHelper.convertKieliUriToKielikoodi("KIELI_EE#123"));
    }

    @Test(expected = RuntimeException.class)
    public void testConvertKieliUriToKielikoodiException1() {
        TarjontaKoodistoHelper.convertKieliUriToKielikoodi("kieli_e");

        fail("Missing RuntimeException!");
    }

    @Test(expected = RuntimeException.class)
    public void testConvertKieliUriToKielikoodiException2() {
        TarjontaKoodistoHelper.convertKieliUriToKielikoodi("ILEIK_SV");

        fail("Missing RuntimeException!");
    }

    @Test(expected = RuntimeException.class)
    public void testConvertKieliUriToKielikoodiException3() {
        TarjontaKoodistoHelper.convertKieliUriToKielikoodi("ILEIK_SV#");

        fail("Missing RuntimeException!");
    }
    
    
    @Test
    public void testKoodistoUri_splitKoodi_and_hasVersion() {

        String source = null;
        String target = null;

        // Test null split
        assertEquals("Null should split to '' and ''.", "", KoodistoURI.splitKoodiToKoodiAndVersion(source)[0]);
        assertEquals("Null should split to '' and ''.", "", KoodistoURI.splitKoodiToKoodiAndVersion(source)[1]);

        // Test no version split
        source = "kieli_fi";
        assertFalse("Has no version", KoodistoURI.koodiHasVersion(source));
        assertEquals("No version - koodi", "kieli_fi", KoodistoURI.splitKoodiToKoodiAndVersion(source)[0]);
        assertEquals("No version - version", "", KoodistoURI.splitKoodiToKoodiAndVersion(source)[1]);
        
        // Test version split
        source = "kieli_fi#123";
        assertTrue("Has version", KoodistoURI.koodiHasVersion(source));
        assertEquals("Version - koodi", "kieli_fi", KoodistoURI.splitKoodiToKoodiAndVersion(source)[0]);
        assertEquals("Version - version", "123", KoodistoURI.splitKoodiToKoodiAndVersion(source)[1]);
    }

    @Test
    public void testKoodistoUri_compare_versions() {

        Object[][] testData = {
            {null, null, true, true},
            {"", null, false, false},
            {null, "", false, false},

            {null, "kieli_fi", false, false},
            {null, "kieli_fi#1", false, false},
            {null, "kieli_fi#2", false, false},

            {"kieli_fi", null, false, false},
            {"kieli_fi#1", null, false, false},
            {"kieli_fi#2", null, false, false},

            {"kieli_fi", "kieli_fi", true, true},
            {"kieli_fi", "kieli_fi#1", true, true},
            {"kieli_fi", "kieli_fi#1234", true, true},
            {"kieli_fi", "hakutapa_03", false, false},
            {"kieli_fi", "hakutapa_03#1", false, false},

            {"kieli_fi#1", "kieli_fi", false, true},
            {"kieli_fi#1", "kieli_fi#1", true, true},
            {"kieli_fi#1", "kieli_fi#1234", false, true},

            {"kieli_fi", "kieli_sv", false, false},
            {"kieli_fi", "kieli_sv#1", false, false},
            {"kieli_fi", "hakutapa_03", false, false},
            {"kieli_fi", "hakutapa_03#1", false, false},
            {"kieli_fi#1", "kieli_sv", false, false},
            {"kieli_fi#1", "kieli_sv#1", false, false},
            {"kieli_fi#1", "hakutapa_03", false, false},
            {"kieli_fi#1", "hakutapa_03#112", false, false},
            
        };
        
        for (Object[] testRow : testData) {
            String source = (String) testRow[0];
            String target = (String) testRow[1];
            boolean expectedResultVersions = (Boolean) testRow[2];
            boolean expectedResultNoVersions = (Boolean) testRow[3];

            if (expectedResultVersions) {
                assertTrue(source + " == " + target, KoodistoURI.compareKoodi(source, target));
            } else {
                assertFalse(source + " != " + target, KoodistoURI.compareKoodi(source, target));
            }

            if (expectedResultNoVersions) {
                assertTrue(source + " == " + target + " (ignore versions)", KoodistoURI.compareKoodi(source, target, true));
            } else {
                assertFalse(source + " != " + target + " (ignore versions)", KoodistoURI.compareKoodi(source, target, true));
            }
        }
    }
}