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
package fi.vm.sade.tarjonta.service.mock;

import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliDTO;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Data storage used by KoulutusmoduuliAdminServiceMock (at least) that may be manipulated or replaced from junit tests.
 *
 * @author Jukka Raanamo
 */
public class KoulutusmoduuliStorage {

    private Map<Long, KoulutusmoduuliDTO> dataMap = new HashMap<Long, KoulutusmoduuliDTO>();

    private AtomicLong idCounter = new AtomicLong(0);

    /**
     * Adds new item to storage. Id is generated and injected if missing.
     * 
     * @param koulutusmoduuli
     */
    public void add(KoulutusmoduuliDTO koulutusmoduuli) {
        if (koulutusmoduuli.getId() == null) {
            koulutusmoduuli.setId(idCounter.incrementAndGet());
        }
        dataMap.put(koulutusmoduuli.getId(), koulutusmoduuli);
    }

    /**
     * Returns all items in storage.
     * 
     * @return
     */
    public Collection<KoulutusmoduuliDTO> getAll() {
        return dataMap.values();
    }

    /**
     * Returns number of koulutusmoduuli's in storage.
     * 
     * @return
     */
    public int size() {
        return dataMap.size();
    }
    
    
    /**
     * Removes all items from storage.
     */
    public void clear() {
        dataMap.clear();
    }
    
    
    

}

