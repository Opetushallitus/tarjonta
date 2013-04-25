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
package fi.vm.sade.tarjonta.service.resources.dto;

import java.io.Serializable;

/**
 * Simple language dependant data.
 *
 * @author mlyly
 */
public class MonikielinenTeksti implements Serializable {

    String _kieliUri;
    String _arvo;

    public String getArvo() {
        return _arvo;
    }

    public void setArvo(String _arvo) {
        this._arvo = _arvo;
    }

    public String getKieliUri() {
        return _kieliUri;
    }

    public void setKieliUri(String _kieliUri) {
        this._kieliUri = _kieliUri;
    }
}
