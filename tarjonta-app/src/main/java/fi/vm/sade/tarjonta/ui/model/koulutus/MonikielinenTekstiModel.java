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
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

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

    /**
     * @param kielikaannos the kielikaannos to set
     */
    public void setKielikaannos(final String userKieliUri, final String fallbackKieliUri, final Set<KielikaannosViewModel> kielikaannos) {
        Preconditions.checkNotNull(kielikaannos, "KielikaannosViewModel object cannot be null.");

        //build index for easier access
        for (KielikaannosViewModel model : kielikaannos) {
            Preconditions.checkNotNull(model.getKielikoodi(), ERROR_NULL);

            kaannokset.put(model.getKielikoodi(), model);
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
        String value = getTextByLangCode(userLangCode);

        if (value != null) {
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
}
