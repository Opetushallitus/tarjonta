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
 * One named applying time (hakuaika) for HakuDTO.
 *
 * @author mlyly
 */
public class HakuaikaRDTO extends BaseRDTO {

    String _nimi;
    Date _alkuPvm;
    Date _loppuPvm;

    public String getNimi() {
        return _nimi;
    }

    public void setNimi(String name) {
        this._nimi = name;
    }

    public Date getAlkuPvm() {
        return _alkuPvm;
    }

    public void setAlkuPvm(Date alkuPvm) {
        this._alkuPvm = alkuPvm;
    }

    public Date getLoppuPvm() {
        return _loppuPvm;
    }

    public void setLoppuPvm(Date loppuPvm) {
        this._loppuPvm = loppuPvm;
    }
}
