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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 *
 */
@Entity
@Table(name = "monikielinen_teksti")
public class MonikielinenTeksti extends BaseEntity {

    private static final long serialVersionUID = -8996615595354088586L;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "teksti", orphanRemoval = true)
    private Set<TekstiKaannos> tekstis = new HashSet<TekstiKaannos>();

    public Set<TekstiKaannos> getTekstis() {
        return Collections.unmodifiableSet(tekstis);
    }

    public void addTekstiKaannos(String kieliKoodi, String teksti) {
        tekstis.add(new TekstiKaannos(this, kieliKoodi, teksti));
    }

    public void setTekstiKaannos(String kieliKoodi, String teksti) {

        final TekstiKaannos kaannos = new TekstiKaannos(this, kieliKoodi, teksti);
        tekstis.remove(kaannos);
        tekstis.add(new TekstiKaannos(this, kieliKoodi, teksti));

    }

    /**
     * Convenience method that looks up translation for given language code.
     * Note that translated texts are by default lazy loaded.
     *
     * @param kieliKoodi
     * @return
     */
    public String getTekstiForKieliKoodi(String kieliKoodi) {
        final TekstiKaannos kaannos = findKaannos(kieliKoodi);
        return (kaannos != null ? kaannos.getArvo() : null);
    }

    /**
     * Clears all existing translations and inserts new values from given object.
     *
     * @param otherTeksti
     */
    public void updateFrom(MonikielinenTeksti otherTeksti) {

        tekstis.clear();

        for (TekstiKaannos t : otherTeksti.getTekstis()) {
            tekstis.add(new TekstiKaannos(this, t));
        }

    }

    private TekstiKaannos findKaannos(String kieliKoodi) {
        final String koodi = TekstiKaannos.formatKieliKoodi(kieliKoodi);
        for (TekstiKaannos t : tekstis) {
            if (t.getKieliKoodi().equals(koodi)) {
                return t;
            }
        }
        return null;
    }

    public boolean removeKaannos(String kieliKoodi) {
        return tekstis.remove(new TekstiKaannos(this, kieliKoodi, kieliKoodi));
    }

}

