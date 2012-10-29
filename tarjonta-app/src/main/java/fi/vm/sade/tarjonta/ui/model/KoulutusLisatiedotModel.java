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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Contains all the additional information for all given languages for a stydy.
 *
 * @author mlyly
 */
public class KoulutusLisatiedotModel extends BaseUIViewModel {

    private Set<String> _kielet;
    private Set<String> _ammattinimikeet;

    /**
     * Language specific information for a study.
     */
    private Map<String, KoulutusLisatietoModel> _lisatiedot;

    /**
     * A set of URIs from koodisto with list of languages used for studies.
     *
     * @return
     */
    public Set<String> getKielet() {
        if (_kielet == null) {
            _kielet = new HashSet<String>();
        }
        return _kielet;
    }

    public void setKielet(Set<String> kielet) {
        _kielet = kielet;
    }


    /**
     * Get additional info with given language.
     * If it's not found, create empty KoulutusLisatietoModel.
     *
     * @param uri
     * @return
     */
    public KoulutusLisatietoModel getLisatiedot(String uri) {
        KoulutusLisatietoModel result = getLisatiedot().get(uri);
        if (result == null) {
            result = new KoulutusLisatietoModel();
            getLisatiedot().put(uri, result);

            // Update language settings too
            getKielet().add(uri);
        }

        return result;
    }

    /**
     * Map of additional information keyed by language URI in "getKielet".
     *
     * @return
     */
    public Map<String, KoulutusLisatietoModel> getLisatiedot() {
        if (_lisatiedot == null) {
            _lisatiedot = new HashMap<String, KoulutusLisatietoModel>();
        }
        return _lisatiedot;
    }

    public void setLisatiedot(Map<String, KoulutusLisatietoModel> map) {
        _lisatiedot = map;
    }

    /**
     * TODO A set of URIs from koodisto XXX.
     *
     * @return
     */
    public Set<String> getAmmattinimikeet() {
        if (_ammattinimikeet == null) {
            _ammattinimikeet = new HashSet<String>();
        }
        return _ammattinimikeet;
    }

    public void setAmmattinimikeet(Set<String> set) {
        _ammattinimikeet = set;
    }

}
