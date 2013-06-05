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

/**
 *
 * @author mlyly
 */
public class ValintakoePisterajaRDTO extends BaseRDTO {

    private int _alinHyvaksyttyPistemaara;
    private int _alinPistemaara;
    private int _ylinPistemaara;
    private String _tyyppi;

    public int getAlinHyvaksyttyPistemaara() {
        return _alinHyvaksyttyPistemaara;
    }

    public void setAlinHyvaksyttyPistemaara(int _alinHyvaksyttyPistemaara) {
        this._alinHyvaksyttyPistemaara = _alinHyvaksyttyPistemaara;
    }

    public int getAlinPistemaara() {
        return _alinPistemaara;
    }

    public void setAlinPistemaara(int _alinPistemaara) {
        this._alinPistemaara = _alinPistemaara;
    }

    public int getYlinPistemaara() {
        return _ylinPistemaara;
    }

    public void setYlinPistemaara(int _ylinPistemaara) {
        this._ylinPistemaara = _ylinPistemaara;
    }

    public String getTyyppi() {
        return _tyyppi;
    }

    public void setTyyppi(String tyyppi) {
        this._tyyppi = tyyppi;
    }

}
