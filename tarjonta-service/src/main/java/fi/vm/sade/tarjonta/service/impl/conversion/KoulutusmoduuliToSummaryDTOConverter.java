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
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliSummaryDTO;

/**
 * 
 * @author Jukka Raanamo
 */
public class KoulutusmoduuliToSummaryDTOConverter extends AbstractFromDomainConverter<Koulutusmoduuli, KoulutusmoduuliSummaryDTO> {

    @Override
    public KoulutusmoduuliSummaryDTO convert(Koulutusmoduuli source) {

        KoulutusmoduuliSummaryDTO summary = new KoulutusmoduuliSummaryDTO();
        summary.setKoulutusmoduuliOID(source.getOid());
        summary.setNimi(source.getNimi());
        
        return summary;

    }
}

