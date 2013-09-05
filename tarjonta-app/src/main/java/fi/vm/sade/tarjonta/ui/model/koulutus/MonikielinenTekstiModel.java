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
package fi.vm.sade.tarjonta.ui.model.koulutus;

import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;

import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

/**
 *
 * @author Jani Wilén
 */
public class MonikielinenTekstiModel extends KoulutusKoodistoModel implements Comparable<MonikielinenTekstiModel> {

    private static final long serialVersionUID = -3004063117090257469L;
    private static final String ERROR_NULL = "Language code cannot be null.";
    private Map<String, KielikaannosViewModel> kaannokset = Maps.<String, KielikaannosViewModel>newHashMap();

    /**
     * @return the kielikaannos
     */
    public Set<KielikaannosViewModel> getKielikaannos() {
        Set<KielikaannosViewModel> model = Sets.<KielikaannosViewModel>newHashSet();
        model.addAll(kaannokset.values());
        return model;
    }

    public void addKielikaannos(KielikaannosViewModel model) {
        Preconditions.checkNotNull(model, "KielikaannosViewModel object cannot be null.");
        Preconditions.checkNotNull(model.getKielikoodi(), "Kielikoodi cannot be null.");
        kaannokset.put(model.getKielikoodi(), model);
    }

    public KielikaannosViewModel removeKielikaannos(String langCode) {
        Preconditions.checkNotNull(langCode, "Language code cannot be null.");
        return kaannokset.remove(langCode);
    }

    /**
     * @param kielikaannos the kielikaannos to set
     */
    public void setKielikaannos(final String userKieliUri, final String fallbackKieliUri, final Set<KielikaannosViewModel> kielikaannos) {
        Preconditions.checkNotNull(kielikaannos, "KielikaannosViewModel object cannot be null.");

        //build index for easier access
        for (KielikaannosViewModel model : kielikaannos) {
            addKielikaannos(model);
        }

        //store text to the field
        closestKieliKaannos(userKieliUri, fallbackKieliUri);
    }

    public String getTextByLangCode(final String langCode) {
        return kaannokset.get(langCode) != null ? kaannokset.get(langCode).getNimi() : null;
    }

    /**
     * Return closest transalation, 1th user's transalation, 2nd fallback
     * transalation and the last fallback transalation.
     *
     * @param userLangCode
     * @param fallbackLangCode
     * @param lang code
     * @return
     */
    private String closestKieliKaannos(final String userLangCode, final String fallbackLangCode) {
        String value = getTextByLangCode(userLangCode.toLowerCase());

        if (value != null && !value.isEmpty()) {
            setSelectedLanguage(userLangCode, value);
            return userLangCode;
        }
        
        //There seems to be some confusion whether lang is lower or upper case so trying both
        value = getTextByLangCode(userLangCode.toUpperCase());
        if (value != null && !value.isEmpty()) {
            setSelectedLanguage(userLangCode, value);
            return userLangCode;
        }

        value = getTextByLangCode(fallbackLangCode);
        if (value != null) {
            setSelectedLanguage(fallbackLangCode, value);
            return fallbackLangCode;
        } else if (getKielikaannos().size() > 0) {
            final KielikaannosViewModel m = getKielikaannos().iterator().next();
            setSelectedLanguage(m.getKielikoodi(), m.getNimi());
            return m.getKielikoodi();
        }

        return null;
    }

    private void setSelectedLanguage(final String langCode, final String text) {
        setKielikoodi(langCode);
        setNimi(text);
    }

    @Override
    public int compareTo(MonikielinenTekstiModel other) {
        final String t1 = this.nimi;
        final String t2 = other != null ? other.nimi : null;
        if (t1 != null && t2 != null) {
            return t1.compareTo(t2);
        }
        if (t1 != null) {
            return 1;
        }
        return -1;
    }

    /**
     * Get KielikaannosViewModel objects by given order. Null param not allowed.
     *
     * @param orderByLangCode
     * @return
     */
    public LinkedList<KielikaannosViewModel> getLangByOrder(String[] orderByLangCode) {
        Preconditions.checkNotNull(orderByLangCode, "Array cannot be null.");
        final Set<Entry<String, KielikaannosViewModel>> entrySet = kaannokset.entrySet();
        LinkedList<KielikaannosViewModel> order = Lists.<KielikaannosViewModel>newLinkedList();

        for (String e : orderByLangCode) {
            if (kaannokset.containsKey(e)) {
                order.add(kaannokset.get(e));
            } else {
                order.addLast(new KielikaannosViewModel(e, ""));
            }
        }

        for (Entry<String, KielikaannosViewModel> e : entrySet) {
            if (!order.contains(e.getValue())) {
                order.addLast(e.getValue());
            }
        }
        return order;
    }

    public Set<String> getLanguages() {
        return kaannokset.keySet();
    }
}
