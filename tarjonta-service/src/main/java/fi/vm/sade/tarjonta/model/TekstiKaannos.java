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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
@Entity
@Table(
	name = "teksti_kaannos",
	uniqueConstraints = @UniqueConstraint(columnNames={"kieli_koodi", "teksti_id"})
)
public class TekstiKaannos extends TarjontaBaseEntity {

    private static final Logger LOG = LoggerFactory.getLogger(TekstiKaannos.class);
    
    private static final long serialVersionUID = 8949181662473812771L;

    @Column(name = "kieli_koodi")
    private String kieliKoodi;

    @Column(name = "arvo")
    private String arvo;

    @ManyToOne(optional = false, fetch=FetchType.LAZY)
    private MonikielinenTeksti teksti;

    /**
     * Constructor for JPA.
     */
    protected TekstiKaannos() {}

    public TekstiKaannos(MonikielinenTeksti teksti, String kieliKoodi, String arvo) {
        
        if (arvo != null) {
            LOG.info("-------------> TekstiKaannos() arvo length = {}", arvo.length());
        }
        
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

    public void setArvo(String arvo) {
        if (arvo != null) {
            LOG.info("setArvo() len = {}", arvo.length());
        }
		this.arvo = arvo;
	}

    static String formatKieliKoodi(String value) {
        return value.trim();
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

