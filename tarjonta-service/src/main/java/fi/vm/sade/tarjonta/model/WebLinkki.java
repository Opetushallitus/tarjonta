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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * A link to an external resource. Used anywhere?
 */
@Embeddable
public class WebLinkki implements Serializable {

    private static final long serialVersionUID = -9139969267734067600L;

    @Enumerated(EnumType.STRING)
    private LinkkiTyyppi tyyppi;

    @Column(name = "kieli", length = 2)
    private String kieli;

    @Column(name = "url", nullable = false)
    private String url;

    /**
     * JPA constructor.
     */
    protected WebLinkki() {
    }

    public WebLinkki(LinkkiTyyppi tyyppi, String kieli, String url) {
        this.tyyppi = tyyppi;
        this.kieli = kieli;
        this.url = url;
    }

    public String getKieli() {
        return kieli;
    }

    public LinkkiTyyppi getTyyppi() {
        return tyyppi;
    }

    public String getUrl() {
        return url;
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

