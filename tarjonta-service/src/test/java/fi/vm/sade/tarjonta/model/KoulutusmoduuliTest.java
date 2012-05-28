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

import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliTila;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Jukka Raanamo
 */
public class KoulutusmoduuliTest {

    @Test
    public void testGetTilaAfterCreate() {
        Koulutusmoduuli moduuli = newModuuli();
        assertEquals(KoulutusmoduuliTila.SUUNNITELUSSA, moduuli.getTila());
    }

    @Test
    public void testAddChild() throws Exception {

        Koulutusmoduuli parent = newModuuli();
        Koulutusmoduuli child = newModuuli();

        assertFalse(parent.hasAsChild(child));
        assertTrue(parent.addChild(child, false));
        assertTrue(parent.hasAsChild(child));

        // the child is already added and cannot be added again 
        assertFalse(parent.addChild(child, false));

    }

    @Test
    public void testAddAsParent() throws Exception {

        Koulutusmoduuli parent = newModuuli();
        Koulutusmoduuli child = newModuuli();

        assertTrue(child.addParent(parent, false));

        assertEquals(1, parent.getChildren().
            size());
        assertEquals(1, child.getParents().
            size());

        assertTrue(parent.hasAsChild(child));

    }

    @Test
    public void testEquals() {

        Koulutusmoduuli k1 = newModuuli();
        Koulutusmoduuli k2 = newModuuli();

        assertFalse(k1.equals(k2));

        // equality is based on db id's
        k1.setId(0L);
        k2.setId(0L);

        assertTrue(k1.equals(k2));

    }

    @Test
    public void testRemoveChild() throws Exception {

        Koulutusmoduuli parent = newModuuli();
        Koulutusmoduuli child = newModuuli();

        parent.addChild(child, true);

        // before remove
        assertEquals(1, parent.getChildren().
            size());
        assertEquals(1, child.getParents().
            size());

        // was removed
        assertTrue(parent.removeChild(child));

        // after remove
        assertEquals(0, parent.getChildren().
            size());
        assertEquals(0, child.getParents().
            size());

    }

    @Test(expected = CyclicReferenceException.class)
    public void testCannotAddChildThatIsAlreadyOurDirectParent() throws Exception {

        Koulutusmoduuli parent = newModuuli();
        Koulutusmoduuli child = newModuuli();

        parent.addChild(child, true);
        child.addChild(parent, true);

    }

    @Test(expected = CyclicReferenceException.class)
    public void testCannotAddChildThatIsAlreadyOurIndirectParent() throws Exception {

        Koulutusmoduuli a1 = newModuuli();
        Koulutusmoduuli a2 = newModuuli();
        Koulutusmoduuli b = newModuuli();
        Koulutusmoduuli c = newModuuli();

        a1.addChild(b, true);
        a2.addChild(b, true);
        b.addChild(c, true);

        c.addChild(a2, true);

    }

    private Koulutusmoduuli newModuuli() {
        return new TutkintoOhjelma();
    }

}

