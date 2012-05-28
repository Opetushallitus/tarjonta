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

import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliTyyppi;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Jukka Raanamo
 */
public class KoulutusmoduuliSisaltyvyysTest {
    
    /**
     * Test of equals method, of class KoulutusmoduuliSisaltyvyys.
     */
    @Test
    public void testEquals() {
     
        Koulutusmoduuli parent = newModuuli();
        Koulutusmoduuli child = newModuuli();
        
        KoulutusmoduuliSisaltyvyys r1 = new KoulutusmoduuliSisaltyvyys(parent, child, true);
        KoulutusmoduuliSisaltyvyys r2 = new KoulutusmoduuliSisaltyvyys(parent, child, true);
        
        assertTrue(r1.equals(r2));
        
        assertFalse(r1.equals(new KoulutusmoduuliSisaltyvyys(child, parent, true)));
        
    }
    
    private Koulutusmoduuli newModuuli() {
        return new TutkintoOhjelma();
    }

    
}

