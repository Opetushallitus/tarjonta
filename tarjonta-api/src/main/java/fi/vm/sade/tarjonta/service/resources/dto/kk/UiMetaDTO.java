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
package fi.vm.sade.tarjonta.service.resources.dto.kk;

import fi.vm.sade.tarjonta.service.resources.dto.kk.UiDTO;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jani Wilén
 */
public class UiMetaDTO extends UiDTO {

    private static final long serialVersionUID = 1L;
    private Map<String, UiDTO> meta;

    public UiMetaDTO() {
    }

    /**
     * @return the tekstis
     */
    public Map<String, UiDTO> getMeta() {
        if (meta == null) {
            meta = new HashMap<String, UiDTO>();
        }
        return meta;
    }

    /**
     * @param tekstis the tekstis to set
     */
    public void setMeta(Map<String, UiDTO> meta) {
        this.meta = meta;
    }
}
