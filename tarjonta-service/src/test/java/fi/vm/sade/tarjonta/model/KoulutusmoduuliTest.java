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

import java.util.Date;
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
        assertEquals(KoodistoContract.TarjontaTilat.SUUNNITTELUSSA, moduuli.getTila());
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
    public void testRemoveChild() throws Exception {

        Koulutusmoduuli parent = newModuuli();
        Koulutusmoduuli child = newModuuli();

        parent.addChild(child, true);

        // before remove
        assertEquals(1, parent.getChildren().size());
        
        // was removed
        assertTrue(parent.removeChild(child));

        // after remove
        assertEquals(0, parent.getChildren().size());        

    }


    public void testMoveChild() throws Exception {

        Koulutusmoduuli firstParent = newModuuli();
        Koulutusmoduuli secondParent = newModuuli();

        Koulutusmoduuli child = newModuuli();

        firstParent.addChild(child, true);

        // todo: what are the rules of adding a child
        //firstParent.moveChild(child, secondParent);

    }

    @Test
    public void testUpdateTimestampIsSetAtInsert() {
        
        Koulutusmoduuli m = newModuuli();
        m.beforePersist();
        assertNotNull(m.getUpdated());
        
    }
    
    @Test
    public void testUpdateTimestampIsUpdatesAtUpdate() throws Exception {
        
        Koulutusmoduuli m = newModuuli();
        m.beforePersist();
        Date before = m.getUpdated();
        
        Thread.sleep(50L);
        
        m.beforeUpdate();
        Date after = m.getUpdated();
        
        assertEquals(1, after.compareTo(before));
        
    }
    
    
    
    private Koulutusmoduuli newModuuli() {
        return new TutkintoOhjelma();
    }

}

