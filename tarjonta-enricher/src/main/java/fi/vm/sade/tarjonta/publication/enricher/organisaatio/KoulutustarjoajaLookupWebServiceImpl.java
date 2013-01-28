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

import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.tarjoaja.service.types.FindByOrganizationOidRequestType;
import fi.vm.sade.tarjonta.publication.enricher.ext.KoodistoCodeValueEnricher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Lookup service implementation using Koulutustarjoaja SOAP service as data
 * source.
 *
 * @author Jukka Raanamo
 */
public class KoulutustarjoajaLookupWebServiceImpl implements KoulutustarjoajaLookupService {

    private static final Logger log = LoggerFactory.getLogger(KoodistoCodeValueEnricher.class);
    @Autowired(required = true)
    protected OrganisaatioService organisaatioService;

    @Override
    public OrganisaatioDTO lookupKoulutustarjoajaByOrganisaatioOid(String oid) throws Exception {

        FindByOrganizationOidRequestType request = new FindByOrganizationOidRequestType();
        request.setOid(oid);
        return organisaatioService.findByOid(oid);
    }
}
