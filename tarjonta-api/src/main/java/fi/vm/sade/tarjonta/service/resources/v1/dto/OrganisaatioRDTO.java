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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mlyly
 */
public class OrganisaatioRDTO extends BaseRDTO {

    private List<LokalisointiRDTO> _nimet;

    public void addNimi(LokalisointiRDTO lokalisointi) {
        getNimet().add(lokalisointi);
    }

    public void addNimi(String kieli, String kieliUri, String arvo) {
        getNimet().add(new LokalisointiRDTO(kieli, kieliUri, arvo));
    }

    public List<LokalisointiRDTO> getNimet() {
        if (_nimet == null) {
            _nimet = new ArrayList<LokalisointiRDTO>();
        }
        return _nimet;
    }

    public void setNimet(List<LokalisointiRDTO> _nimet) {
        this._nimet = _nimet;
    }
}