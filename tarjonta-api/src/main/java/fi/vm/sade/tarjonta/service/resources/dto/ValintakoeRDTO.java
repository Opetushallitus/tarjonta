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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Valintakoe to REST API conversion.
 *
 * @author mlyly
 */
public class ValintakoeRDTO extends BaseRDTO {

    private String valintakoeId;
    private String _tyyppiUri;
    private Map<String, String> _lisanaytot;
    private Map<String, String> _kuvaus;
    private List<ValintakoeAjankohtaRDTO> _valintakoeAjankohtas = new ArrayList<ValintakoeAjankohtaRDTO>();
    private List<ValintakoePisterajaRDTO> _valintakoePisterajas = new ArrayList<ValintakoePisterajaRDTO>();

    public String getTyyppiUri() {
        return _tyyppiUri;
    }

    public void setTyyppiUri(String _tyyppiUri) {
        this._tyyppiUri = _tyyppiUri;
    }

    public Map<String, String> getLisanaytot() {
        return _lisanaytot;
    }

    public void setLisanaytot(Map<String, String> _lisanaytot) {
        this._lisanaytot = _lisanaytot;
    }

    public Map<String, String> getKuvaus() {
        return _kuvaus;
    }

    public void setKuvaus(Map<String, String> _kuvaus) {
        this._kuvaus = _kuvaus;
    }

    public List<ValintakoeAjankohtaRDTO> getValintakoeAjankohtas() {
        return _valintakoeAjankohtas;
    }

    public void setValintakoeAjankohtas(List<ValintakoeAjankohtaRDTO> _valintakoeAjankohtas) {
        this._valintakoeAjankohtas = _valintakoeAjankohtas;
    }

    public List<ValintakoePisterajaRDTO> getValintakoePisterajas() {
        return _valintakoePisterajas;
    }

    public void setValintakoePisterajas(List<ValintakoePisterajaRDTO> _valintakoePisterajas) {
        this._valintakoePisterajas = _valintakoePisterajas;
    }

    public String getValintakoeId() {
        return valintakoeId;
    }

    public void setValintakoeId(String valintakoeId) {
        this.valintakoeId = valintakoeId;
    }
}
