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
package fi.vm.sade.tarjonta.publication.enricher.organisaatio;

import fi.vm.sade.tarjoaja.service.types.KoulutustarjoajaTyyppi;

/**
 * Data lookup contract used by content enrichment handlers to enrich
 * LearningOpportunityProvider data.
 *
 * TODO: do we want to use this return type directly...?
 *
 * @author Jukka Raanamo
 */
public interface KoulutustarjoajaLookupService {

    /**
     *
     * @param oid
     * @return
     */
    public KoulutustarjoajaTyyppi lookupKoulutustarjoajaByOrganisaatioOid(String oid)
        throws Exception;

}

