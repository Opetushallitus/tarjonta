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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import fi.vm.sade.generic.model.BaseEntity;

/**
 * Translatable texts with modest "metadata" properties.
 */
@Entity
@Table(name = "monikielinen_teksti")
public class MonikielinenTeksti extends TarjontaBaseEntity {

    private static final long serialVersionUID = -8996615595354088586L;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "teksti", fetch = FetchType.LAZY, orphanRemoval = true)
    @MapKey(name = "kieliKoodi")
    private Map<String, TekstiKaannos> tekstis = new HashMap<String, TekstiKaannos>();

    public Collection<TekstiKaannos> getTekstis() {
        return Collections.unmodifiableCollection(tekstis.values());
    }

    public void addTekstiKaannos(String kieliKoodi, String teksti) {
        tekstis.put(kieliKoodi, new TekstiKaannos(this, kieliKoodi, teksti));
    }

    public void addTekstiKaannos(TekstiKaannos tekstiKaannos) {
        tekstis.put(tekstiKaannos.getKieliKoodi(), tekstiKaannos);
    }

    public void setTekstiKaannos(String kieliKoodi, String teksti) {
        TekstiKaannos kaannos = tekstis.get(kieliKoodi);
        if (kaannos == null) {
            addTekstiKaannos(kieliKoodi, teksti);
        } else {
            kaannos.setArvo(teksti);
            //kaannos.setVersion(null);
        }
    }

    /**
     * Convenience method that looks up translation for given language code.
     *
     * @param kieliKoodi
     * @return
     */
    public String getTekstiForKieliKoodi(String kieliKoodi) {
        final TekstiKaannos kaannos = findKaannos(kieliKoodi);
        return (kaannos != null ? kaannos.getArvo() : null);
    }

    /**
     * Clears all existing translations and inserts new values from given
     * object.
     *
     * @param otherTeksti / public void updateFrom(MonikielinenTeksti
     * otherTeksti) {
     *
     * tekstis.clear();
     *
     * for (TekstiKaannos t : otherTeksti.getTekstis()) {
     * addTekstiKaannos(t.getKieliKoodi(), t.getArvo()); }
     *
     * }
     */
    private TekstiKaannos findKaannos(String kieliKoodi) {
        final String koodi = TekstiKaannos.formatKieliKoodi(kieliKoodi);
        return tekstis.get(koodi);
    }

    public boolean removeKaannos(String kieliKoodi) {
        return tekstis.remove(kieliKoodi) != null;
    }

    /**
     * Apumetodi monikielisten tekstien settereille; esim.:
     *
     * <pre>
     * void setFoo(MonikielinenTeksti foo) {
     *     this.foo = MonikielinenTeksti.merge(this.foo, foo);
     * }
     * </pre>
     */
    public static MonikielinenTeksti merge(MonikielinenTeksti old, MonikielinenTeksti uus) {
        if (old == null) {
            return uus;
        }
        if (uus == null) {
            return null;
        }

        // retainAll ei toiminut hibernaten lazy-collectionin kanssa - hibernaten bugi?
        for (Iterator<String> ki = old.tekstis.keySet().iterator(); ki.hasNext();) {
            if (!uus.tekstis.containsKey(ki.next())) {
                ki.remove();
            }
        }

        for (TekstiKaannos tk : uus.tekstis.values()) {
            old.setTekstiKaannos(tk.getKieliKoodi(), tk.getArvo());
        }

        return old;
    }

    public static <T> void merge(Map<T, MonikielinenTeksti> dst, T key, MonikielinenTeksti uus) {
        if (uus == null) {
            dst.remove(key);
        } else {
            dst.put(key, merge(dst.get(key), uus));
        }
    }

    @Override
    public String toString() {
        try {
        return "MonikielinenTeksti [tekstis=" + tekstis + "]";
        } catch(Throwable t) {
            return super.toString();
        }
        
    }

}
