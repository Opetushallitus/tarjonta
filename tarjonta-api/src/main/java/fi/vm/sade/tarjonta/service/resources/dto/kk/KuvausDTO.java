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

import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author Jani Wilén
 */
public class KuvausDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Map<KomoTeksti, MonikielinenTekstiTyyppi> tekstis;

    /**
     * @return the tekstis
     */
    public Map<KomoTeksti, MonikielinenTekstiTyyppi> getTekstis() {
        return tekstis;
    }

    /**
     * @param tekstis the tekstis to set
     */
    public void setTekstis(Map<KomoTeksti, MonikielinenTekstiTyyppi> tekstis) {
        this.tekstis = tekstis;
    }
}
