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
package fi.vm.sade.tarjonta.ui.enums;

import fi.vm.sade.tarjonta.shared.KoodistoURI;

/**
 *
 * @author jani
 */
public enum Koulutustyyppi {

    UNAVAILABLE(null),
    TOINEN_ASTE_LUKIO("koulutustyyppi_2"),
    TOINEN_ASTE_AMMATILLINEN_KOULUTUS("koulutustyyppi_1"),
    KORKEAKOULU("koulutustyyppi_3"),
    TOINEN_ASTE_AMMATILLINEN_ERITYISKOULUTUS("koulutustyyppi_4"),
    TOINEN_ASTE_VALMENTAVA_KOULUTUS("koulutustyyppi_5"),
    PERUSOPETUKSEN_LISAOPETUS("koulutustyyppi_6"),
    AMMATILLISEEN_OHJAAVA_KOULUTUS("koulutustyyppi_7"),
    MAMU_AMMATILLISEEN_OHJAAVA_KOULUTUS("koulutustyyppi_8"),
    MAMU_LUKIOON_OHJAAVA_KOULUTUS("koulutustyyppi_9"),
    VAPAAN_SIVISTYSTYON_KOULUTUS("koulutustyyppi_10");
    
    private String koulutustyyppiUri;

    Koulutustyyppi(String koulutustyyppiUri) {
        this.koulutustyyppiUri = koulutustyyppiUri;
    }

    public String getKoulutustyyppiUri() {
        return koulutustyyppiUri;
    }
    
    public static Koulutustyyppi getByKoodistoUri(String uri){
        
        
        if(uri == null) {
            return null;
        }
        
        for(Koulutustyyppi tyyppi:Koulutustyyppi.values()){
            if(KoodistoURI.compareKoodi(uri, tyyppi.getKoulutustyyppiUri() )) {
              return tyyppi;
            }
        }
        
        return null;
    }

    @Override
    public String toString() {
        return this.koulutustyyppiUri;
    }
}