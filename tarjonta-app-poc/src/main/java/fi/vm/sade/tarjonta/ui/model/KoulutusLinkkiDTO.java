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
import java.lang.reflect.Field;

/**
 *
 * @author mlyly
 */
public class KoulutusLinkkiDTO implements Serializable {

    // Koodisto: kieli
    String _kieli;
    String _url;

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
                Object v = field.get(this);
                if (v == null) {
                    sb.append("NULL");
                } else {
                    sb.append(v.toString());
                }
            } catch (Throwable ex) {
                sb.append("FAILED TO GET VALUE");
            }

            isFirstField = false;
        }

        sb.append("]");
        return sb.toString();
    }

    public String getKieli() {
        return _kieli;
    }

    public void setKieli(String _kieli) {
        this._kieli = _kieli;
    }

    public String getUrl() {
        return _url;
    }

    public void setUrl(String _url) {
        this._url = _url;
    }
}
