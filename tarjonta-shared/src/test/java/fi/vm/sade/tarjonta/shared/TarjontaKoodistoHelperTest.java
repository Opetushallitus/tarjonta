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
}