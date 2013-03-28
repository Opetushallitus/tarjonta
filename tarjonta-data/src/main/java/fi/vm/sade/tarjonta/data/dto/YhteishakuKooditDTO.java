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
package fi.vm.sade.tarjonta.data.dto;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author Jani Wilén
 */
public class YhteishakuKooditDTO extends AbstractReadableRow {
    private String koulutusohjelmanKoodiarvo;
    private String tutkintonimike;
    private String tutkintonimikkeenKoodiarvo;
    private String tutkinnonNimi;
    private String koulutuskoodi;
    private String hakukohdeKoodiArvo;
    private String hakukohteenNimi;

    /**
     * @return the koulutusohjelmanKoodiarvo
     */
    public String getKoulutusohjelmanKoodiarvo() {
        return koulutusohjelmanKoodiarvo;
    }

    /**
     * @param koulutusohjelmanKoodiarvo the koulutusohjelmanKoodiarvo to set
     */
    public void setKoulutusohjelmanKoodiarvo(final String koulutusohjelmanKoodiarvo) {
        this.koulutusohjelmanKoodiarvo = koulutusohjelmanKoodiarvo;
    }

    /**
     * @return the tutkintonimike
     */
    public String getTutkintonimike() {
        return tutkintonimike;
    }

    /**
     * @param tutkintonimike the tutkintonimike to set
     */
    public void setTutkintonimike(final String tutkintonimike) {
        this.tutkintonimike = tutkintonimike;
    }

    /**
     * @return the tutkintonimikkeenKoodiarvo
     */
    public String getTutkintonimikkeenKoodiarvo() {
        return tutkintonimikkeenKoodiarvo;
    }

    /**
     * @param tutkintonimikkeenKoodiarvo the tutkintonimikkeenKoodiarvo to set
     */
    public void setTutkintonimikkeenKoodiarvo(final String tutkintonimikkeenKoodiarvo) {
        this.tutkintonimikkeenKoodiarvo = tutkintonimikkeenKoodiarvo;
    }

    /**
     * @return the tutkinnonNimi
     */
    public String getTutkinnonNimi() {
        return tutkinnonNimi;
    }

    /**
     * @param tutkinnonNimi the tutkinnonNimi to set
     */
    public void setTutkinnonNimi(final String tutkinnonNimi) {
        this.tutkinnonNimi = tutkinnonNimi;
    }

    /**
     * @return the koulutuskoodi
     */
    public String getKoulutuskoodi() {
        return koulutuskoodi;
    }

    /**
     * @param koulutuskoodi the koulutuskoodi to set
     */
    public void setKoulutuskoodi(final String koulutuskoodi) {
        this.koulutuskoodi = koulutuskoodi;
    }

    //Toisen_asteen_tutkinnot/321101
    private static final String SEPARATOR = "/";
    private static final String VERSION = "#1";

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        final YhteishakuKooditDTO other = (YhteishakuKooditDTO) obj;
        final EqualsBuilder builder = new EqualsBuilder();
        builder.append(getKoulutuskoodi(), other.getKoulutuskoodi()).
                append(koulutusohjelmanKoodiarvo, other.koulutusohjelmanKoodiarvo);

        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(getKoulutuskoodi()).append(koulutusohjelmanKoodiarvo).toHashCode();
    }

    /**
     * @return the hakukohdeKoodiArvo
     */
    public String getHakukohdeKoodiArvo() {
        return hakukohdeKoodiArvo;
    }

    /**
     * @param hakukohdeKoodiArvo the hakukohdeKoodiArvo to set
     */
    public void setHakukohdeKoodiArvo(final String hakukohdeKoodiArvo) {
        this.hakukohdeKoodiArvo = hakukohdeKoodiArvo;
    }

    /**
     * @return the hakukohteenNimi
     */
    public String getHakukohteenNimi() {
        return hakukohteenNimi;
    }

    /**
     * @param hakukohteenNimi the hakukohteenNimi to set
     */
    public void setHakukohteenNimi(final String hakukohteenNimi) {
        this.hakukohteenNimi = hakukohteenNimi;
    }

    @Override
    public boolean isEmpty() {
        return StringUtils.isBlank(koulutusohjelmanKoodiarvo) && StringUtils.isBlank(tutkintonimike)
                && StringUtils.isBlank(tutkintonimikkeenKoodiarvo) && StringUtils.isBlank(tutkinnonNimi)
                && StringUtils.isBlank(koulutuskoodi) && StringUtils.isBlank(hakukohdeKoodiArvo)
                && StringUtils.isBlank(hakukohteenNimi);
    }
}
