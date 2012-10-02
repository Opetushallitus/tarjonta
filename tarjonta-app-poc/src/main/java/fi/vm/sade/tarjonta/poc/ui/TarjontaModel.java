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
package fi.vm.sade.tarjonta.poc.ui;

import fi.vm.sade.tarjonta.poc.ui.model.KoulutusPerustiedotDTO;
import fi.vm.sade.tarjonta.poc.ui.model.KoulutusSearchSpesificationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author mlyly
 */
@Configurable(preConstruction=false)
public class TarjontaModel {

    private static final Logger LOG = LoggerFactory.getLogger(TarjontaModel.class);

    public TarjontaModel() {
        LOG.info("TarjontaModel()");
    }

    // Show label that shows last modification
    @Value("${common.showAppIdentifier:true}")
    private Boolean _showIdentifier;
    @Value("${tarjonta-app.identifier:APPLICATION IDENTIFIER NOT AVAILABLE}")
    private String _identifier;

    private KoulutusSearchSpesificationDTO searchSpesification;
    private KoulutusPerustiedotDTO _koulutusPerustiedot;

    /**
     * Search spesification for Koulutus offerings.
     *
     * @return
     */
    public KoulutusSearchSpesificationDTO getSearchSpesification() {
        if (searchSpesification == null) {
            searchSpesification = new KoulutusSearchSpesificationDTO();
        }
        return searchSpesification;
    }

    /**
     * True if app identifier should be shown.
     *
     * @return
     */
    public Boolean getShowIdentifier() {
        return _showIdentifier;
    }

    /**
     * Get APP identifier.
     *
     * @return
     */
    public String getIdentifier() {
        return _identifier;
    }

    public KoulutusPerustiedotDTO getKoulutusPerustiedot() {
        if (_koulutusPerustiedot == null) {
            _koulutusPerustiedot = new KoulutusPerustiedotDTO();
        }
        return _koulutusPerustiedot;
    }

    public void setKoulutusPerustiedot(KoulutusPerustiedotDTO _koulutusPerustiedot) {
        this._koulutusPerustiedot = _koulutusPerustiedot;
    }

}
