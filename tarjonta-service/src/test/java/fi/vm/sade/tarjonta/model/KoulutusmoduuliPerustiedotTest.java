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
package fi.vm.sade.tarjonta.model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Jukka Raanamo
 */
public class KoulutusmoduuliPerustiedotTest {

    private static final String OPETUSKIELI_FI_URI = "http://kieli/fi";

    private static final String OPETUSKIELI_EN_URI = "http://kieli/en";

    private static final String OPETUSMUOTO_LAHI_URI = "http://opetusmuoto/lahi";

    private static final String OPETUSMUOTO_LUOKKA_URI = "http://opetusmuoto/luokka";

    private static final String ASIASANOITUS_SOSIAALI_URI = "http://asiasanoitus/sosiaali";

    private static final String ASIASANOITUS_KIELET_URI = "http://asiasanoitus/kielet";

    private KoulutusmoduuliPerustiedot perustiedot;

    private KoulutusmoduuliPerustiedot perustiedotWith2Opetuskieli;

    private KoulutusmoduuliPerustiedot perustiedotWith2Koulutusmuoto;

    @Before
    public void setUp() {

        perustiedot = new KoulutusmoduuliPerustiedot();

        perustiedotWith2Opetuskieli = new KoulutusmoduuliPerustiedot();
        perustiedotWith2Opetuskieli.addOpetuskieli(OPETUSKIELI_FI_URI);
        perustiedotWith2Opetuskieli.addOpetuskieli(OPETUSKIELI_EN_URI);

        perustiedotWith2Koulutusmuoto = new KoulutusmoduuliPerustiedot();
        perustiedotWith2Koulutusmuoto.addOpetusmuoto(OPETUSMUOTO_LAHI_URI);
        perustiedotWith2Koulutusmuoto.addOpetusmuoto(OPETUSMUOTO_LUOKKA_URI);

    }

    @Test
    public void testAddOpetuskieli() {
        assertTrue(perustiedot.addOpetuskieli(OPETUSKIELI_FI_URI));
        assertTrue(perustiedot.addOpetuskieli(OPETUSKIELI_EN_URI));
        assertFalse(perustiedot.addOpetuskieli(OPETUSKIELI_EN_URI));
        assertEquals(2, perustiedot.getOpetuskieletkielis().size());
    }

    @Test
    public void testRemoveOpetuskieli() {
        assertTrue(perustiedotWith2Opetuskieli.removeOpetuskieli(OPETUSKIELI_FI_URI));
        assertTrue(perustiedotWith2Opetuskieli.removeOpetuskieli(OPETUSKIELI_EN_URI));
        assertFalse(perustiedotWith2Opetuskieli.removeOpetuskieli(OPETUSKIELI_EN_URI));
        assertEquals(0, perustiedotWith2Opetuskieli.getOpetuskieletkielis().size());
    }

    @Test
    public void testAddOpetusmuoto() {
        assertTrue(perustiedot.addOpetusmuoto(OPETUSMUOTO_LAHI_URI));
        assertTrue(perustiedot.addOpetusmuoto(OPETUSMUOTO_LUOKKA_URI));
        assertFalse(perustiedot.addOpetusmuoto(OPETUSMUOTO_LAHI_URI));
        assertEquals(2, perustiedot.getOpetusmuotos().size());
    }

    @Test
    public void testRemoveOpetusmuoto() {
        assertTrue(perustiedotWith2Koulutusmuoto.removeOpetusmuoto(OPETUSMUOTO_LAHI_URI));
        assertTrue(perustiedotWith2Koulutusmuoto.removeOpetusmuoto(OPETUSMUOTO_LUOKKA_URI));
        assertFalse(perustiedotWith2Koulutusmuoto.removeOpetusmuoto(OPETUSMUOTO_LUOKKA_URI));
        assertEquals(0, perustiedotWith2Koulutusmuoto.getOpetusmuotos().size());
    }

    @Test
    public void testAddAsiasanoitus() {
        assertTrue(perustiedot.addAsiasanoitus(ASIASANOITUS_KIELET_URI));
        assertTrue(perustiedot.addAsiasanoitus(ASIASANOITUS_SOSIAALI_URI));
        assertFalse(perustiedot.addAsiasanoitus(ASIASANOITUS_SOSIAALI_URI));
        assertEquals(2, perustiedot.getAsiasanoituses().size());
    }

}

