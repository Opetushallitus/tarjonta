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
package fi.vm.sade.tarjonta.ui.model.koulutus.lukio;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *
 * @author Jani Wilén
 */
public class YhteyshenkiloModel {
    /*
     * Yhteyshenkilo fields
     */

    protected String yhtHenkKokoNimi;
    protected String yhtHenkTitteli;
    protected String yhtHenkEmail;
    protected String yhtHenkPuhelin;
    protected String YhtHenkiloOid;

    public YhteyshenkiloModel() {
    }

    public void clear() {
        setYhtHenkEmail(null); //optional
        setYhtHenkKokoNimi(null); //optional
        setYhtHenkPuhelin(null); //optional
        setYhtHenkTitteli(null); //optional
        setYhtHenkiloOid(null); //optional
    }

    /**
     * @return the yhtHenkKokoNimi
     */
    public String getYhtHenkKokoNimi() {
        return yhtHenkKokoNimi;
    }

    /**
     * @param yhtHenkKokoNimi the yhtHenkKokoNimi to set
     */
    public void setYhtHenkKokoNimi(String yhtHenkKokoNimi) {
        this.yhtHenkKokoNimi = yhtHenkKokoNimi;
    }

    /**
     * @return the yhtHenkTitteli
     */
    public String getYhtHenkTitteli() {
        return yhtHenkTitteli;
    }

    /**
     * @param yhtHenkTitteli the yhtHenkTitteli to set
     */
    public void setYhtHenkTitteli(String yhtHenkTitteli) {
        this.yhtHenkTitteli = yhtHenkTitteli;
    }

    /**
     * @return the yhtHenkEmail
     */
    public String getYhtHenkEmail() {
        return yhtHenkEmail;
    }

    /**
     * @param yhtHenkEmail the yhtHenkEmail to set
     */
    public void setYhtHenkEmail(String yhtHenkEmail) {
        this.yhtHenkEmail = yhtHenkEmail;
    }

    /**
     * @return the yhtHenkPuhelin
     */
    public String getYhtHenkPuhelin() {
        return yhtHenkPuhelin;
    }

    /**
     * @param yhtHenkPuhelin the yhtHenkPuhelin to set
     */
    public void setYhtHenkPuhelin(String yhtHenkPuhelin) {
        this.yhtHenkPuhelin = yhtHenkPuhelin;
    }

    /**
     * @return the YhtHenkiloOid
     */
    public String getYhtHenkiloOid() {
        return YhtHenkiloOid;
    }

    /**
     * @param YhtHenkiloOid the YhtHenkiloOid to set
     */
    public void setYhtHenkiloOid(String YhtHenkiloOid) {
        this.YhtHenkiloOid = YhtHenkiloOid;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        YhteyshenkiloModel other = (YhteyshenkiloModel) obj;

        EqualsBuilder builder = new EqualsBuilder();

        builder.append(YhtHenkiloOid, other.YhtHenkiloOid);
        builder.append(yhtHenkKokoNimi, other.yhtHenkKokoNimi);
        builder.append(yhtHenkTitteli, other.yhtHenkTitteli);
        builder.append(yhtHenkEmail, other.yhtHenkEmail);
        builder.append(yhtHenkPuhelin, other.yhtHenkPuhelin);
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(YhtHenkiloOid)
                .append(yhtHenkKokoNimi)
                .append(yhtHenkTitteli)
                .append(yhtHenkEmail)
                .append(yhtHenkPuhelin)
                .toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
