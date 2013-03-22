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

import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;

/*
* Author: Tuomas Katva
*/
public class KoulutusOidNameViewModel {

    private String koulutusOid;

    private String koulutusNimi;
    
    private KoulutusasteTyyppi koulutustyyppi;


    public String getKoulutusOid() {
        return koulutusOid;
    }

    public void setKoulutusOid(String koulutusOid) {
        this.koulutusOid = koulutusOid;
    }

    public String getKoulutusNimi() {
        return koulutusNimi;
    }

    public void setKoulutusNimi(String koulutusNimi) {
        this.koulutusNimi = koulutusNimi;
    }

    @Override
    public String toString() {
        return koulutusNimi;
    }

    public KoulutusasteTyyppi getKoulutustyyppi() {
        return koulutustyyppi;
    }

    public void setKoulutustyyppi(KoulutusasteTyyppi koulutustyyppi) {
        this.koulutustyyppi = koulutustyyppi;
    }
}
