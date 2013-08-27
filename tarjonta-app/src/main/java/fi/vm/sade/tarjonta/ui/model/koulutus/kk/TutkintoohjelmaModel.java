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
package fi.vm.sade.tarjonta.ui.model.koulutus.kk;

import fi.vm.sade.tarjonta.ui.model.koulutus.*;

/**
 *
 * @author Jani Wilén
 */
public class TutkintoohjelmaModel extends MonikielinenTekstiModel {
    private static final long serialVersionUID = -5947159028438495028L;
    private String komoOid;
    private String komoParentOid;
    
    public TutkintoohjelmaModel() {
    }

    /**
     * @return the komoOid
     */
    public String getKomoOid() {
        return komoOid;
    }

    /**
     * @param komoOid the komoOid to set
     */
    public void setKomoOid(String komoOid) {
        this.komoOid = komoOid;
    }

    /**
     * @return the komoParentOid
     */
    public String getKomoParentOid() {
        return komoParentOid;
    }

    /**
     * @param komoParentOid the komoParentOid to set
     */
    public void setKomoParentOid(String komoParentOid) {
        this.komoParentOid = komoParentOid;
    }
}