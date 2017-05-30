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
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.Hibernate;

/**
 * Translatable texts with modest "metadata" properties.
 */

@JsonIgnoreProperties({"kaannoksetAsList", "tekstiKaannos", "id", "version", "hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "monikielinen_teksti")
public class MonikielinenTeksti extends TarjontaBaseEntity {

    private static final long serialVersionUID = -8996615595354088586L;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "teksti", fetch = FetchType.LAZY, orphanRemoval = true)
    @MapKey(name = "kieliKoodi")
    private Map<String, TekstiKaannos> tekstis = new HashMap<String, TekstiKaannos>();

    public void setTekstis(Map<String, TekstiKaannos> tekstis){
        this.tekstis.clear();
        for(TekstiKaannos k:tekstis.values()){
            addTekstiKaannos(k.getKieliKoodi(), k.getArvo());
        }
    }

    public Collection<TekstiKaannos> getTekstiKaannos() {
        return Collections.unmodifiableCollection(lataaKaannostekstit());
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

    public MonikielinenTeksti() {
    }

    /**
     * Construct Monikielinen teksti from lang, value pairs. for every pair n
     * the nth lang is at data[n*2], the nth value is at data[n*2+1].
     *
     * @param data
     */
    public MonikielinenTeksti(String... data) {
        for (int i = 0; i < data.length / 2; i++) {
            addTekstiKaannos(data[i * 2], data[i * 2 + 1]);
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
        return kaannos != null ? kaannos.getArvo() : null;
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
     * for (TekstiKaannos t : otherTeksti.getTekstiKaannos()) {
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

        Hibernate.initialize(old.tekstis);
        for (Iterator<String> ki = old.tekstis.keySet().iterator(); ki.hasNext();) {
            if (!uus.tekstis.containsKey(ki.next())) {
                ki.remove();
            }
        }

        for (TekstiKaannos tk : uus.lataaKaannostekstit()) {
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
        } catch (Throwable t) {
            return super.toString();
        }
    }

    public List<TekstiKaannos> getKaannoksetAsList() {
        return Lists.newArrayList(lataaKaannostekstit());
    }

    public Map<String, String> asMap() {
        Map<String, String> tekstit = Maps.newHashMap();
        for (TekstiKaannos tk : lataaKaannostekstit()) {
            tekstit.put(tk.getKieliKoodi(), tk.getArvo());
        }
        return tekstit;
    }

    public Map<String, String> asMapWithoutVersions() {
        Map<String, String> tekstit = Maps.newHashMap();
        for (TekstiKaannos tk : lataaKaannostekstit()) {
            tekstit.put(tk.getKieliKoodi().split("#")[0], tk.getArvo());
        }
        return tekstit;
    }

    public String getFirstNonEmptyKaannos() {
        // Prefer finnish
        if (tekstis.get("kieli_fi") != null && !tekstis.get("kieli_fi").getArvo().isEmpty()) {
            return tekstis.get("kieli_fi").getArvo();
        }
        for (TekstiKaannos tmpKaannos : getTekstiKaannos()) {
            if (!tmpKaannos.getArvo().isEmpty()) {
                return tmpKaannos.getArvo();
            }
        }
        return null;
    }

    private Collection<TekstiKaannos> lataaKaannostekstit() {
        Hibernate.initialize(tekstis);
        return tekstis.values();
    }

}
