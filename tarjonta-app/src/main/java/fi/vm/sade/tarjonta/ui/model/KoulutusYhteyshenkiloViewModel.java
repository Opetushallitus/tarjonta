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

    private String _nimi;
    private String _titteli;
    private String _email;
    private String _puhelin;

    // Koodisto: kieli
    private Set<String> _kielet = new HashSet<String>();

    public String getNimi() {
        return _nimi;
    }

    public void setNimi(String nimi) {
        this._nimi = nimi;
    }

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
}
