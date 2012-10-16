
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

package fi.vm.sade.tarjonta.ui.helper;


import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author Tuomas Katva
 * 
 */

@Configurable
public class KoodistoURIHelper {

    public static String KOODISTO_KIELI_URI = "KIELI";
    
    public static String KOODISTO_HAKUKELPOISUUS_VAATIMUKSET_URI = "KOULUTUSRYHMÄ";
    
    public static String KOODISTO_HAKUKOHDENIMI_URI = "KOULUTUSRYHMÄ";
    
    public KoodistoURIHelper() {
        
    } 
    
    public KoodistoURIHelper(String kieliUri, String hakukelpoisuusVaatimusUri) {
        setKieliUri(kieliUri);
        setKoodistoHakukelpoisuusVaatimuksetUri(hakukelpoisuusVaatimusUri);
    }
    
    public void setKieliUri(String kieliUri) {
        if (kieliUri != null && kieliUri.length() > 0) {
            KOODISTO_KIELI_URI = kieliUri;
        }
    }
    
    public void setKoodistoHakukelpoisuusVaatimuksetUri(String koodistoHakukelpoisuusVaatimuksetUri) {
        if (koodistoHakukelpoisuusVaatimuksetUri != null && koodistoHakukelpoisuusVaatimuksetUri.length() > 0) {
        KOODISTO_HAKUKELPOISUUS_VAATIMUKSET_URI = koodistoHakukelpoisuusVaatimuksetUri;
        }
    }
    
    public void setKoodistoHakukohdeNimiUri(String hakukohdeNimiUri) {
        if (hakukohdeNimiUri != null && hakukohdeNimiUri.length() > 0) {
           KOODISTO_HAKUKOHDENIMI_URI = hakukohdeNimiUri;
        }
    }
    
}
