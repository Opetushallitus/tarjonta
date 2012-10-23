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
 * This class represents one "contact person" for a given set of languages.
 *
 * @author mlyly
 */
public class KoulutusYhteyshenkiloViewModel extends BaseUIViewModel {
    private String _titteli;
    private String _email;
    private String _puhelin;
    private String etunimet;
    private String sukunimi;

    // Koodisto: kieli
    private Set<String> _kielet = new HashSet<String>();

    public String getTitteli() {
        return _titteli;
    }

    public void setTitteli(String titteli) {
        this._titteli = titteli;
    }

    public String getEmail() {
        return _email;
    }

    public void setEmail(String email) {
        this._email = email;
    }

    public String getPuhelin() {
        return _puhelin;
    }

    public void setPuhelin(String puhelin) {
        this._puhelin = puhelin;
    }

    public Set<String> getKielet() {
        return _kielet;
    }

    public void setKielet(Set<String> kielet) {
        this._kielet = kielet;
    }

    /**
     * @return the etunimet
     */
    public String getEtunimet() {
        return etunimet;
    }

    /**
     * @param etunimet the etunimet to set
     */
    public void setEtunimet(String etunimet) {
        this.etunimet = etunimet;
    }

    /**
     * @return the sukunimi
     */
    public String getSukunimi() {
        return sukunimi;
    }

    /**
     * @param sukunimi the sukunimi to set
     */
    public void setSukunimi(String sukunimi) {
        this.sukunimi = sukunimi;
    }
}
