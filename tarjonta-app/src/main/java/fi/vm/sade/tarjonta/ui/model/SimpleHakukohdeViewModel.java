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
package fi.vm.sade.tarjonta.ui.model;

import java.util.Date;

/*
* Author: Tuomas Katva
*/
public class SimpleHakukohdeViewModel {

    private String hakukohdeOid;

    private String hakukohdeNimiKoodi;

    private String hakukohdeNimi;

    private String hakukohdeTila;
    
    private boolean hakuStarted;

    public String getHakukohdeOid() {
        return hakukohdeOid;
    }

    public void setHakukohdeOid(String hakukohdeOid) {
        this.hakukohdeOid = hakukohdeOid;
    }

    public String getHakukohdeNimiKoodi() {
        return hakukohdeNimiKoodi;
    }

    public void setHakukohdeNimiKoodi(String hakukohdeNimiKoodi) {
        this.hakukohdeNimiKoodi = hakukohdeNimiKoodi;
    }

    public String getHakukohdeNimi() {
        return hakukohdeNimi;
    }

    public void setHakukohdeNimi(String hakukohdeNimi) {
        this.hakukohdeNimi = hakukohdeNimi;
    }

    public String getHakukohdeTila() {
        return hakukohdeTila;
    }

    public void setHakukohdeTila(String hakukohdeTila) {
        this.hakukohdeTila = hakukohdeTila;
    }

    public void setHakuStarted(Date hakuAlkamisPvm) {
        Date today = new Date();
        hakuStarted = (hakuAlkamisPvm != null) && today.after(hakuAlkamisPvm);
    }

    public boolean isHakuStarted() {
        return hakuStarted;
    }

    public void setHakuStarted(boolean hakuStarted) {
        this.hakuStarted = hakuStarted;
    }
}
