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

public class HakuTest {
    
 
    @Test
    public void testSetNimi() {
        
        Haku h = new Haku();
        h.setNimiFi("name-fi");        
        assertEquals("name-fi", h.getNimiFi());
        
    }
    
    @Test
    public void testOverwriteNimi() {
        
        Haku h = new Haku();
        h.setNimiFi("name1");
        h.setNimiFi("name2");
        
        assertEquals(1, h.getNimi().getTekstiKaannos().size());
        assertEquals("name2", h.getNimiFi());
        
    }
    
    @Test
    public void testRemoveNimiKaannos() {
        
        Haku h = new Haku();
        h.setNimiFi("nimi");
        h.setNimiEn("name");
        
        assertEquals(2, h.getNimi().getTekstiKaannos().size());
        
        h.getNimi().removeKaannos("fi");
        
        assertEquals(1, h.getNimi().getTekstiKaannos().size());        
        assertEquals("name", h.getNimiEn());        
        
    }
    
    
}

