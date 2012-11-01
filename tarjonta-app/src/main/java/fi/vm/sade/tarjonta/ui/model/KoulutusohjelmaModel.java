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

import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import java.io.Serializable;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *
 * @author Jani Wilén
 */
public class KoulutusohjelmaModel implements Serializable {

    private int koodiVersio; //used as ID
    private String koodiUri; //used as ID
    private String code; //koodi code
    private String name;
    private String fullName; //combination of id and name fields

    public KoulutusohjelmaModel() {
    }

    public KoulutusohjelmaModel(String koodiUri, int koodiVersio, String code, String name) {
        this.koodiUri = koodiUri;
        this.koodiVersio = koodiVersio;
        this.code = code;
        this.name = name;
        this.fullName = name + " " + code;
    }

    /**
     * @return the koodiUri
     */
    public String getKoodiUri() {
        return koodiUri;
    }

    /**
     * @param koodiUri the koodiUri to set
     */
    public void setKoodiUri(String koodiUri) {
        this.koodiUri = koodiUri;
    }

    /**
     * @return the koodi
     */
    public String getCode() {
        return code;
    }

    /**
     * @param koodi the koodi to set
     */
    public void setCode(String koodi) {
        this.code = koodi;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * @param fullName the fullName to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public boolean equals(Object obj) {
        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }

        //Check only uri value as it's the ID of the class.
        final KoulutusohjelmaModel m = (KoulutusohjelmaModel) obj;

        if (this.getKoodiUri() == null || m.getKoodiUri() == null) {
            return false;
        }

        return this.getKoodiUri().equals(m.getKoodiUri());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).
                append(koodiUri).
                toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * @return the koodiVersio
     */
    public int getKoodiVersio() {
        return koodiVersio;
    }

    /**
     * @param koodiVersio the koodiVersio to set
     */
    public void setKoodiVersio(int koodiVersio) {
        this.koodiVersio = koodiVersio;
    }

    public String getVersionUri() {
        return TarjontaUIHelper.createVersionUri(koodiUri, koodiVersio);
    }
}
