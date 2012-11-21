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
package fi.vm.sade.tarjonta.ui.model.koulutus;

import java.util.Set;

/**
 *
 * @author Jani Wilén
 */
public class KoulutusohjelmaModel extends MonikielinenTekstiModel {

    private KoodiModel tutkintonimike;
    
    public KoulutusohjelmaModel() {
    }

    /**
     * @return the tutkintonimike
     */
    public KoodiModel getTutkintonimike() {
        return tutkintonimike;
    }

    /**
     * @param tutkintonimike the tutkintonimike to set
     */
    public void setTutkintonimike(KoodiModel tutkintonimike) {
        this.tutkintonimike = tutkintonimike;
    }
}
