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

import fi.vm.sade.generic.model.BaseEntity;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.apache.commons.lang.StringUtils;

/**
 * One KoulutusmoduuliToteutus may be offered by several Organisaatio. This is a cross-service link to that Organisaatio,
 * an OID basically.
 * 
 * @author Jukka Raanamo
 */
@Entity
@Table(name=KoulutusmoduuliToteutusTarjoaja.TABLE_NAME)
public class KoulutusmoduuliToteutusTarjoaja extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -6249676108088247159L;

    private String organisaatioOID;
    
    public static final String TABLE_NAME = "koulutusmoduuli_toteutus_tarjoja";

    /**
     * JPA only
     */
    protected KoulutusmoduuliToteutusTarjoaja() {
    }

    public KoulutusmoduuliToteutusTarjoaja(String organisaatioOID) {
        assert StringUtils.isNotEmpty(organisaatioOID) : "organisaatio OID cannot be empty";
        this.organisaatioOID = organisaatioOID;
    }

    public String getOrganisaatioOID() {
        return organisaatioOID;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final KoulutusmoduuliToteutusTarjoaja other = (KoulutusmoduuliToteutusTarjoaja) obj;
        if ((this.organisaatioOID == null) ? (other.organisaatioOID != null) : !this.organisaatioOID.equals(other.organisaatioOID)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (this.organisaatioOID != null ? this.organisaatioOID.hashCode() : 0);
        return hash;
    }

}

