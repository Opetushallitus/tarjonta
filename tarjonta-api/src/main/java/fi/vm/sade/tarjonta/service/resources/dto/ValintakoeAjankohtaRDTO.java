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

import java.util.Date;

/**
 * REST API dto for Valintakoe Ajankohta.
 *
 * @author mlyly
 */
public class ValintakoeAjankohtaRDTO extends BaseRDTO {

    private OsoiteRDTO _osoite;
    private Date _alkaa;
    private Date _loppuu;
    private String _lisatiedot;
    private boolean kellonaikaKaytossa = true;

    public OsoiteRDTO getOsoite() {
    	if (_osoite==null) {
    		_osoite = new OsoiteRDTO();
    	}
        return _osoite;
    }

    public void setOsoite(OsoiteRDTO _osoite) {
        this._osoite = _osoite;
    }

    public Date getAlkaa() {
        return _alkaa;
    }

    public void setAlkaa(Date _alkaa) {
        this._alkaa = _alkaa;
    }

    public Date getLoppuu() {
        return _loppuu;
    }

    public void setLoppuu(Date _loppuu) {
        this._loppuu = _loppuu;
    }

    public String getLisatiedot() {
        return _lisatiedot;
    }

    public void setLisatiedot(String _lisatiedot) {
        this._lisatiedot = _lisatiedot;
    }

    public boolean isKellonaikaKaytossa() {
        return kellonaikaKaytossa;
    }

    public void setKellonaikaKaytossa(boolean kellonaikaKaytossa) {
        this.kellonaikaKaytossa = kellonaikaKaytossa;
    }
}
