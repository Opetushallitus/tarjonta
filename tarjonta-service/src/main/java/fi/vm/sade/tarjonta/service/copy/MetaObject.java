/*
 * Copyright (c) 2014 The Finnish Board of Education - Opetushallitus
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
package fi.vm.sade.tarjonta.service.copy;

import com.google.common.collect.Sets;
import java.io.Serializable;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *
 * @author jani
 */
public class MetaObject implements Serializable {

    private Set<String> komotoOids;
    private Set<String> hakukohdeOids;
    private String newKomotoOid;

    private String newKomoOid;
    private String newHakukohdeOid;

    private String originalKomoOid;
    private String originalKomotoOid;
    private String originalHakuOid;

    public MetaObject() {
    }

    public void addKomotoOid(String oid) {
        if (getKomotoOids() == null) {
            setKomotoOids(Sets.<String>newHashSet());
        }

        if (oid != null) {
            getKomotoOids().add(oid);
        }
    }

    public void addHakukohdeOid(String oid) {
        if (getHakukohdeOids() == null) {
            setHakukohdeOids(Sets.<String>newHashSet());
        }

        if (oid != null) {
            getHakukohdeOids().add(oid);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * @return the hakukohdeOids
     */
    public Set<String> getHakukohdeOids() {
        return hakukohdeOids;
    }

    /**
     * @param hakukohdeOids the hakukohdeOids to set
     */
    public void setHakukohdeOids(Set<String> hakukohdeOids) {
        this.hakukohdeOids = hakukohdeOids;
    }

    /**
     * @return the komotoOids
     */
    public Set<String> getKomotoOids() {
        return komotoOids;
    }

    /**
     * @param komotoOids the komotoOids to set
     */
    public void setKomotoOids(Set<String> komotoOids) {
        this.komotoOids = komotoOids;
    }

    public Boolean hasNoKomotos() {
        return getKomotoOids() == null || getKomotoOids().isEmpty();
    }

    /**
     * @return the newKomotoOid
     */
    public String getNewKomotoOid() {
        return newKomotoOid;
    }

    /**
     * @param newKomotoOid the newKomotoOid to set
     */
    public void setNewKomotoOid(String newKomotoOid) {
        this.newKomotoOid = newKomotoOid;
    }

    /**
     * @return the originalKomoOid
     */
    public String getOriginalKomoOid() {
        return originalKomoOid;
    }

    /**
     * @param originalKomoOid the originalKomoOid to set
     */
    public void setOriginalKomoOid(String originalKomoOid) {
        this.originalKomoOid = originalKomoOid;
    }

    /**
     * @return the originalHakuOid
     */
    public String getOriginalHakuOid() {
        return originalHakuOid;
    }

    /**
     * @param originalHakuOid the originalHakuOid to set
     */
    public void setOriginalHakuOid(String originalHakuOid) {
        this.originalHakuOid = originalHakuOid;
    }

    /**
     * @return the originalKomotoOid
     */
    public String getOriginalKomotoOid() {
        return originalKomotoOid;
    }

    /**
     * @param originalKomotoOid the originalKomotoOid to set
     */
    public void setOriginalKomotoOid(String originalKomotoOid) {
        this.originalKomotoOid = originalKomotoOid;
    }

    /**
     * @return the newHakukohdeOid
     */
    public String getNewHakukohdeOid() {
        return newHakukohdeOid;
    }

    /**
     * @param newHakukohdeOid the newHakukohdeOid to set
     */
    public void setNewHakukohdeOid(String newHakukohdeOid) {
        this.newHakukohdeOid = newHakukohdeOid;
    }

    public void setNewKomoOid(String newKomoOid) {
        this.newKomoOid = newKomoOid;
    }

    public String getNewKomoOid() {
        return newKomoOid;
    }
}
