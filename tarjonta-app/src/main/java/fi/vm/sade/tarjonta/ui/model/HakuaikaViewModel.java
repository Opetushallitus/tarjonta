/*
 *
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

package fi.vm.sade.tarjonta.ui.model;

import java.util.Date;




/**
 *
 * @author Tuomas Katva
 */
public class HakuaikaViewModel {

    private Date alkamisPvm;
    
    private Date paattymisPvm;

    /**
     * @return the alkamisPvm
     */
    public Date getAlkamisPvm() {
        return alkamisPvm;
    }

    /**
     * @param alkamisPvm the alkamisPvm to set
     */
    public void setAlkamisPvm(Date alkamisPvm) {
        this.alkamisPvm = alkamisPvm;
    }

    /**
     * @return the paattymisPvm
     */
    public Date getPaattymisPvm() {
        return paattymisPvm;
    }

    /**
     * @param paattymisPvm the paattymisPvm to set
     */
    public void setPaattymisPvm(Date paattymisPvm) {
        this.paattymisPvm = paattymisPvm;
    }
    
    
}
