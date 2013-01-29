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
package fi.vm.sade.tarjonta.ui.service;

import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.types.GeneerinenTilaTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaTilaTyyppi;
import fi.vm.sade.tarjonta.service.types.SisaltoTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Jani Wilén
 */
@Service
public class PublishingService {
private static final Logger LOG = LoggerFactory.getLogger(PublishingService.class);
    
    @Autowired(required = true)
    private TarjontaAdminService tarjontaAdminService;

    /**
     * Publish single tarjonta model by OID and data model type.
     *
     * @param oid
     * @param sisalto
     * @return true when no errors
     */
    public boolean changeState(final String oid, final TarjontaTila toTila, final SisaltoTyyppi sisalto) {
        GeneerinenTilaTyyppi tyyppi = new GeneerinenTilaTyyppi();
        tyyppi.setOid(oid);
        tyyppi.setSisalto(sisalto);
        tyyppi.setTila(toTila);
        if (tarjontaAdminService.testaaTilasiirtyma(tyyppi)) {
            PaivitaTilaTyyppi tila = new PaivitaTilaTyyppi();
            tila.getTilaOids().add(tyyppi);
            tarjontaAdminService.paivitaTilat(tila);
            return true;
        } 
        return false;
    }


    /**
     * To determine if the tested process (draft, ready etc.) step is allowed.
     *
     * @param oid
     * @param sisalto
     * @param requiredState
     * @return
     */
    public boolean isStateStepAllowed(final String oid, final SisaltoTyyppi sisalto, final TarjontaTila... requiredState) {
        if (sisalto == null) {
            throw new RuntimeException("SisaltoTyyppi object cannot be null.");
        }

        if (requiredState == null && requiredState.length > 0) {
            throw new RuntimeException("TarjontaTila object cannot be null or empty.");
        }

        if (oid == null) {
            //OID is null, when user tryes to add new data.
            LOG.debug("Buttons enabled for {} {}", sisalto, requiredState);
            return true;
        }

        GeneerinenTilaTyyppi tyyppi = new GeneerinenTilaTyyppi();
        tyyppi.setOid(oid);
        tyyppi.setSisalto(sisalto);

        for (TarjontaTila tila : requiredState) {
            tyyppi.setTila(tila);

            if (tarjontaAdminService.testaaTilasiirtyma(tyyppi)) {
                return true;
            }
        }

        return false;
    }
}
