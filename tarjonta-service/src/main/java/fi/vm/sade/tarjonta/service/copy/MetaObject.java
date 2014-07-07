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
    private String komoOid;

    public MetaObject() {
    }

    public void addKomotoOid(String oid) {
        if (komotoOids == null) {
            komotoOids = Sets.<String>newHashSet();
        }

        komotoOids.add(oid);
    }

    public void addHakukohdeOid(String oid) {
        if (hakukohdeOids == null) {
            hakukohdeOids = Sets.<String>newHashSet();
        }

        hakukohdeOids.add(oid);
    }

    /**
     * @return the komoOid
     */
    public String getKomoOid() {
        return komoOid;
    }

    /**
     * @param komoOid the komoOid to set
     */
    public void setKomoOid(String komoOid) {
        this.komoOid = komoOid;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
