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
package fi.vm.sade.tarjonta.ui.model;

import java.io.Serializable;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author mlyly
 */
@Configurable
public class TarjontaModel implements Serializable {

    // Show label that shows last modification
    @Value("${common.showAppIdentifier:true}")
    private boolean _showIdentifier;
    @Value("${tarjonta-app.identifier:APPLICATION IDENTIFIER NOT AVAILABLE}")
    private String _identifier;

    public String getIdentifier() {
        return _identifier;
    }

    public void setIdentifier(String _identifier) {
        this._identifier = _identifier;
    }

    public boolean isShowIdentifier() {
        return _showIdentifier;
    }

    public void setShowIdentifier(boolean _showIdentifier) {
        this._showIdentifier = _showIdentifier;
    }


}
