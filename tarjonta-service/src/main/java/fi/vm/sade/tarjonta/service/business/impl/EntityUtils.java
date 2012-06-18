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
package fi.vm.sade.tarjonta.service.business.impl;

import fi.vm.sade.tarjonta.model.KoulutusmoduuliPerustiedot;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliTila;

/**
 *
 * @author Jukka Raanamo
 */
public final class EntityUtils {

    private EntityUtils() {
    }
    
    public static void copyFields(KoulutusmoduuliToteutus source, KoulutusmoduuliToteutus target) {
        
        target.setNimi(source.getNimi());
        target.setTila(source.getTila());
        target.setMaksullisuus(source.getMaksullisuus());
        
        copyFields(source.getPerustiedot(), target.getPerustiedot());
        
    }
    
    public static void copyFields(KoulutusmoduuliPerustiedot source, KoulutusmoduuliPerustiedot target) {
        
        target.setKoulutusKoodiUri(source.getKoulutusKoodiUri());
        
        
    }
    
    
    
}

