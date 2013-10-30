/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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
 */
package fi.vm.sade.tarjonta.service.resources.v1.dto;

import java.io.Serializable;

/**
 *
 * @author mlyly
 */
public class LokalisointiV1RDTO implements Serializable {

    private String _kieli;
    private String _kieliUri;
    private String _arvo;

    public LokalisointiV1RDTO() {
    }

    public LokalisointiV1RDTO(String kieli, String kieliUri, String arvo) {
        this._kieli = kieli;
        this._kieliUri = kieliUri;
        this._arvo = arvo;
    }

    public String getKieli() {
        return _kieli;
    }

    public void setKieli(String _kieli) {
        this._kieli = _kieli;
    }

    public String getKieliUri() {
        return _kieliUri;
    }

    public void setKieliUri(String _kieliUri) {
        this._kieliUri = _kieliUri;
    }

    public String getArvo() {
        return _arvo;
    }

    public void setArvo(String _arvo) {
        this._arvo = _arvo;
    }

}
