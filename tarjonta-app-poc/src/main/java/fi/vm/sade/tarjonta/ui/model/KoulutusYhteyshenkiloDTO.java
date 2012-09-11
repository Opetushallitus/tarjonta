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

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mlyly
 */
public class KoulutusYhteyshenkiloDTO implements Serializable {

    private String _nimi;
    private String _titteli;
    private String _email;
    private String _puhelin;
    // Koodisto: kieli
    private List<String> _kielet = new ArrayList<String>();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append("[");

        Field[] fields = this.getClass().getDeclaredFields();

        boolean isFirstField = true;

        for (Field field : fields) {
            if (!isFirstField) {
                sb.append(", ");
            }

            sb.append(field.getName());
            sb.append("=");
            try {
                sb.append("" + field.get(this));
            } catch (Throwable ex) {
                sb.append("FAILED TO GET VALUE");
            }

            isFirstField = false;
        }

        sb.append("]");
        return sb.toString();
    }

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

    public List<String> getKielet() {
        return _kielet;
    }

    public void setKielet(List<String> kielet) {
        this._kielet = kielet;
    }
}
