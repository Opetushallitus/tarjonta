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
package fi.vm.sade.tarjonta.ui.model.koulutus.aste2;

import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.ui.model.BaseUIViewModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Contains all the additional information for all given languages for a study.
 *
 * Note that this class contains references to "KoulutusLisatietoMmodel" (note
 * missing -t :) that contains language specific textual data.
 *
 * @author mlyly
 */
public class KoulutusLisatiedotModel extends BaseUIViewModel {
    private static final long serialVersionUID = 7745705875364869396L;

    private TarjontaTila tila;
    private Set<String> _kielet;
    private Collection<String> _ammattinimikkeet;
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
     * Get additional info with given language. If it's not found, create empty
     * KoulutusLisatietoModel.
     *
     * @param uri
     * @return
     */
    public KoulutusLisatietoModel getLisatiedot(String uri) {

        if (uri == null) {
            throw new IllegalArgumentException("An invalid language URI - the URI value cannot be null.");
        }

        KoulutusLisatietoModel result = getLisatiedot().get(uri);
        if (result == null) {
            result = new KoulutusLisatietoModel();
            result.setLanguageUri(uri);
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
    public Collection<String> getAmmattinimikkeet() {
        if (_ammattinimikkeet == null) {
            _ammattinimikkeet = new ArrayList<String>();
        }
        return _ammattinimikkeet;
    }

    public void setAmmattinimikkeet(Collection<String> set) {
        _ammattinimikkeet = set;
    }

    /**
     * @return the tila
     */
    public TarjontaTila getTila() {
        return tila;
    }

    /**
     * @param tila the tila to set
     */
    public void setTila(TarjontaTila tila) {
        this.tila = tila;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        KoulutusLisatiedotModel other = (KoulutusLisatiedotModel) obj;

        EqualsBuilder builder = new EqualsBuilder();
        builder.append(this._ammattinimikkeet, other._ammattinimikkeet);
        builder.append(this._kielet, other._kielet);
        builder.append(this._lisatiedot, other._lisatiedot);
        builder.append(this.tila, other.tila);
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(_ammattinimikkeet).
                append(_kielet).
                append(_lisatiedot).toHashCode();
    }
}
