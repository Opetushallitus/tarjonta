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

import org.junit.Test;

import javax.validation.constraints.AssertTrue;

import static org.junit.Assert.*;

/**
 *
 */
public class HakukohdeTest {

    @Test
    public void testRemoveValintakoe() {

        Hakukohde h = new Hakukohde();
        Valintakoe v = new Valintakoe();

        h.addValintakoe(v);
        assertEquals(1, h.getValintakoes().size());

        Valintakoe v2 = h.getValintakoes().iterator().next();
        h.removeValintakoe(v2);

        assertEquals(0, h.getValintakoes().size());

    }

    @Test
    public void thatReturnsEmptyRyhmaOids() {
        Hakukohde h = new Hakukohde();

        assertTrue(h.getOrganisaatioRyhmaOids().length == 0);

        h.setOrganisaatioRyhmaOids(new String[]{""});

        assertTrue(h.getOrganisaatioRyhmaOids().length == 0);
    }

    @Test
    public void thatReturnsRyhmaOids() {
        Hakukohde h = new Hakukohde();
        h.setOrganisaatioRyhmaOids(new String[]{"0.1.2", "3.4.5"});

        assertTrue(h.getOrganisaatioRyhmaOids().length == 2);
        assertEquals("0.1.2", h.getOrganisaatioRyhmaOids()[0]);
        assertEquals("3.4.5", h.getOrganisaatioRyhmaOids()[1]);
    }
}

