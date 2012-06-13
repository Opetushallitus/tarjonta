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
import fi.vm.sade.tarjonta.model.TutkintoOhjelmaToteutus;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliTila;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliToteutusDTO;
import fi.vm.sade.tarjonta.model.dto.TutkintoOhjelmaToteutusDTO;

/**
 * 
 * @param <T> 
 * @author Jukka Raanamo
 */
public class TutkintoOhjelmaToteutusToDTOConverter<T extends KoulutusmoduuliToteutusDTO> extends AbstractFromDomainConverter<TutkintoOhjelmaToteutus, T> {

    @Override
    public T convert(TutkintoOhjelmaToteutus source) {

        TutkintoOhjelmaToteutusDTO dto = new TutkintoOhjelmaToteutusDTO();
        dto.setTila(source.getTila());
        dto.setOid(source.getOid());
        dto.setNimi(source.getNimi());
        dto.setPerustiedot(CommonConverter.convert(source.getPerustiedot()));
        
        return (T) dto;

    }

    
    
    
}

