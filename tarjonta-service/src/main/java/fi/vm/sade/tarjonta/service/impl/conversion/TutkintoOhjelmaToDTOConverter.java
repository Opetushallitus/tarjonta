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
package fi.vm.sade.tarjonta.service.impl.conversion;

import fi.vm.sade.generic.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliPerustiedot;
import fi.vm.sade.tarjonta.model.TutkintoOhjelma;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliDTO;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliPerustiedotDTO;
import fi.vm.sade.tarjonta.model.dto.TutkintoOhjelmaDTO;

/**
 * TODO: maybe inherit from KoulutusOhjelmaDTOConverter?
 * 
 * @param <T> 
 * @author Jukka Raanamo
 */
public class TutkintoOhjelmaToDTOConverter<T extends KoulutusmoduuliDTO> extends AbstractFromDomainConverter<TutkintoOhjelma, T> {

    @Override
    public T convert(TutkintoOhjelma source) {

        TutkintoOhjelmaDTO dto = new TutkintoOhjelmaDTO();
        dto.setTila(source.getTila().name());
        dto.setOid(source.getOid());
        dto.setUpdated(source.getUpdated());
        dto.setPerustiedot(convert(source.getPerustiedot()));
        
        return (T) dto;

    }

    
    private KoulutusmoduuliPerustiedotDTO convert(KoulutusmoduuliPerustiedot source) {
        
        if (source == null) {
            return null;
        }
        
        KoulutusmoduuliPerustiedotDTO dto = new KoulutusmoduuliPerustiedotDTO();
        dto.setOrganisaatioOid(source.getOrganisaatioOid());
        
        return dto;
        
    }
    
}

