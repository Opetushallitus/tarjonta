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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 */
@Entity
@Table(name = "teksti_kaannos")
public class TekstiKaannos extends BaseEntity {

    private static final long serialVersionUID = 8949181662473812771L;

    @Column(name = "kieli_koodi")
    private String kieliKoodi;

    @Column(name = "arvo")
    private String arvo;

    @ManyToOne(optional = false)
    private MonikielinenTeksti teksti;

    /**
     * Constructor for JPA.
     */
    protected TekstiKaannos() {
    }

    public TekstiKaannos(MonikielinenTeksti teksti, TekstiKaannos copyFrom) {
        this(teksti, copyFrom.getKieliKoodi(), copyFrom.getArvo());
    }

    public TekstiKaannos(MonikielinenTeksti teksti, String kieliKoodi, String arvo) {
        this.teksti = teksti;
        this.kieliKoodi = formatKieliKoodi(kieliKoodi);
        this.arvo = arvo;
    }

    public String getKieliKoodi() {
        return kieliKoodi;
    }

    public String getArvo() {
        return arvo;
    }

    static String formatKieliKoodi(String value) {
        return value.trim().toLowerCase();
    }

    @Override
    public String toString() {
        return kieliKoodi + ": " + arvo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TekstiKaannos other = (TekstiKaannos) obj;
        if ((this.kieliKoodi == null) ? (other.kieliKoodi != null) : !this.kieliKoodi.equals(other.kieliKoodi)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.kieliKoodi != null ? this.kieliKoodi.hashCode() : 0);
        return hash;
    }

}

