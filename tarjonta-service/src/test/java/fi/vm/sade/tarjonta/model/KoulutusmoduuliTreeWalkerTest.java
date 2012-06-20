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

import fi.vm.sade.tarjonta.model.KoulutusmoduuliTreeWalker.WalkTester;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliTyyppi;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

/**
 * Our test tree is:
 * <pre>
 *               R
 *              / \
 *             A   B
 *            /
 *           C
 * </pre>
 *
 * @author Jukka Raanamo
 */
public class KoulutusmoduuliTreeWalkerTest {

    private static Koulutusmoduuli root = newNode();

    private static Koulutusmoduuli level_1_child_0 = newNode();

    private static Koulutusmoduuli level_1_child_1 = newNode();

    private static Koulutusmoduuli level_2_child_0 = newNode();

    private static AtomicInteger counter = new AtomicInteger(0);

    private static KoulutusmoduuliTreeWalker.WalkTester tester = new WalkTester() {

        @Override
        public boolean continueWalking(Koulutusmoduuli moduuli) {
            counter.incrementAndGet();
            return true;
        }

    };

    private static KoulutusmoduuliTreeWalker walker = KoulutusmoduuliTreeWalker.createWalker(-1, tester);

    @BeforeClass
    public static void setUpTree() throws Exception {

        root.addChild(level_1_child_0, false);
        root.addChild(level_1_child_1, false);
        level_1_child_0.addChild(level_2_child_0, false);

    }

    private static Koulutusmoduuli newNode() {
        return new TutkintoOhjelma();
    }

    /**
     * Test of createWalker method, of class KoulutusmoduuliTreeWalker.
     */
    @Test
    public void testWalkDown() {

        // walk down from root node
        counter.set(0);
        walker.walkDown(root);
        assertEquals(4, counter.intValue());


    }

    @Test
    public void testWalkUp() {

        // walk up: C-A-R
        counter.set(0);
        walker.walkUp(level_2_child_0);
        assertEquals(3, counter.intValue());

    }

}

