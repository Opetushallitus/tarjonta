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
package fi.vm.sade.tarjonta.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Typed, localized url to a an external resource.
 */
@Embeddable
@JsonIgnoreProperties({"id", "version", "tyyppi"})
public class WebLinkki implements Serializable {

    private static final long serialVersionUID = -9139969267734067600L;

    /**
     * Constant to represent that kieli is null. This is used since kieli column
     * cannot be nullable or it is not added to unique constraint.
     */
    private static final String NULL_KIELI = "00";

    @Column(name = "linkki_tyyppi", nullable = false)
    private String linkkiTyyppi;

    @Column(name = "kieli", nullable = false)
    private String kieli;

    @Column(name = "url", nullable = false)
    private String url;

    /**
     * JPA constructor.
     */
    protected WebLinkki() {
    }

    /**
     *
     * @param linkkiTyyppi
     * @param kieli language for which this link applies or null
     * @param url
     */
    public WebLinkki(String linkkiTyyppi, String kieli, String url) {
        this.linkkiTyyppi = linkkiTyyppi;
        this.url = url;
        setKieliInternal(kieli);
    }

    /**
     * Create a new WebLinkki using a known type
     *
     * @param tyyppi
     * @param kieli
     * @param url
     */
    public WebLinkki(LinkkiTyyppi tyyppi, String kieli, String url) {
        this(tyyppi.name(), kieli, url);
    }

    public String getKieli() {
        return (NULL_KIELI.equals(kieli) ? null : kieli);
    }

    private void setKieliInternal(String kieliKoodi) {
        kieli = (StringUtils.isEmpty(kieliKoodi) ? NULL_KIELI : kieliKoodi.trim());
    }

    public String getLinkkiTyyppi() {
        return linkkiTyyppi;
    }

    public void setLinkkiTyyppi(String linkkiTyyppi) {
        this.linkkiTyyppi = linkkiTyyppi;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + this.linkkiTyyppi.hashCode();
        hash = 29 * hash + this.kieli.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WebLinkki other = (WebLinkki) obj;
        if ((this.linkkiTyyppi == null) ? (other.linkkiTyyppi != null) : !this.linkkiTyyppi.equals(other.linkkiTyyppi)) {
            return false;
        }
        if ((this.kieli == null) ? (other.kieli != null) : !this.kieli.equals(other.kieli)) {
            return false;
        }
        return true;
    }

    public enum LinkkiTyyppi {

        OPETUSSUUNNITELMA,
        OPPILAITOS,
        SOME,
        MULTIMEDIA,
        MAKSULLISUUS,
        STIPENDI
    }

}
