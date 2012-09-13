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
import static org.junit.Assert.*;

/**
 *
 * @author Jukka Raanamo
 */
public class KoulutusTest {

    @Test
    public void testKoulutusCanHoldMultipleChildren() {

        Koulutus parent = new TutkintoOhjelma();

        TutkinnonOsa child1 = new TutkinnonOsa();
        TutkinnonOsa child2 = new TutkinnonOsa();

        parent.addChild(child1, false);
        parent.addChild(child2, false);

        assertEquals(2, parent.getChildren().size());

    }

    @Test
    public void testSameChildCanOnlyAddedOnce() {

        Koulutus parent = new TutkintoOhjelma();

        TutkinnonOsa child = new TutkinnonOsa();

        parent.addChild(child, false);
        assertFalse(parent.addChild(child, false));

        assertEquals(1, parent.getChildren().size());

    }

    @Test(expected = KoulutusTreeException.class)
    public void testParentCannotBeAddedAsChild() {

        Koulutus parent = new TutkintoOhjelma();
        parent.addChild(parent, true);

    }

    @Test
    public void testNodeCanBeShared() {

        Koulutus tree1 = new TutkintoOhjelma();
        Koulutus tree2 = new TutkintoOhjelma();

        TutkinnonOsa sharedChild = new TutkinnonOsa();

        tree1.addChild(sharedChild, true);
        tree2.addChild(sharedChild, false);

        assertEquals(1, tree1.getChildren().size());
        assertEquals(1, tree2.getChildren().size());

    }

}

