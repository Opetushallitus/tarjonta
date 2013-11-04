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
package fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus;

import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.UiMetaV1RDTO;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jani Wilén
 * @param <TYPE>
 */
public class TekstiV1RDTO<TYPE extends Enum> implements Serializable {

    private static final long serialVersionUID = 1L;
    private Map<TYPE, UiMetaV1RDTO> tekstis;

    public TekstiV1RDTO() {
    }

    /**
     * @return the tekstis
     */
    public Map<TYPE, UiMetaV1RDTO> getTekstis() {
        if (tekstis == null) {
            tekstis = new HashMap<TYPE, UiMetaV1RDTO>();
        }

        return tekstis;
    }

    /**
     * @param tekstis the tekstis to set
     */
    public void setTekstis(Map<TYPE, UiMetaV1RDTO> tekstis) {
        this.tekstis = tekstis;
    }

}
