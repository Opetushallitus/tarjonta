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

import java.util.HashSet;
import java.util.Set;

/**
 * This class defines categories of links to outside of this system.
 *
 * Link contains the category, language(s) the given URL link is used for.
 *
 * @author mlyly
 */
public class KoulutusLinkkiViewModel extends BaseUIViewModel {

    /**
     * Defines possible types for links.
     */
    public static final String[] LINKKI_TYYPIT = new String[]{
        "OPPILAITOS",
        "KOULUTUSOHJELMA",
        "SOSIAALINENMEDIA",
        "MULTIMEDIA",
        "MAKSULLISUUS",
        "STIPENDIMAHDOLLISUUS",
    };

    private String linkkityyppi;
    // Koodisto: kieli
    private String kieli;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLinkkityyppi() {
        if (linkkityyppi == null) {
            linkkityyppi = LINKKI_TYYPIT[0];
        }
        return linkkityyppi;
    }

    public void setLinkkityyppi(String linkkityyppi) {
        // Make sure value is valid
        boolean valid = false;
        for (String tyyppi : LINKKI_TYYPIT) {
            if (linkkityyppi != null && tyyppi.equals(linkkityyppi)) {
                valid = true;
                break;
            }
        }

        if (!valid) {
            // Default type is "OPPILAITOS" if invalid value given
            this.linkkityyppi = LINKKI_TYYPIT[0];
        } else {
            this.linkkityyppi = linkkityyppi;
        }
    }

    /**
     * @return the kieli
     */
    public String getKieli() {
        return kieli;
    }

    /**
     * @param kieli the kieli to set
     */
    public void setKieli(String kieli) {
        this.kieli = kieli;
    }
}
