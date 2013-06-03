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

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

/**
 *
 * @author Jani Wilén
 */
public class MonikielinenTekstiModel extends KoulutusKoodistoModel implements Comparable<MonikielinenTekstiModel> {

    private static final long serialVersionUID = -3004063117090257469L;
    private Set<KielikaannosViewModel> kielikaannos;
    private Map<String, String> kaannokset = Maps.newHashMap();

    /**
     * @return the kielikaannos
     */
    public Set<KielikaannosViewModel> getKielikaannos() {
        if (kielikaannos == null) {
            kielikaannos = new HashSet<KielikaannosViewModel>();
        }
        return kielikaannos;
    }

    /**
     * @param kielikaannos the kielikaannos to set
     */
    public void setKielikaannos(Set<KielikaannosViewModel> kielikaannos) {
        this.kielikaannos = kielikaannos;
        //build index for easier access
        for(KielikaannosViewModel model: kielikaannos) {
            Preconditions.checkNotNull(model.getKielikoodi(), "kielikoodi cannot be null");
            kaannokset.put(model.getKielikoodi().toLowerCase(), model.getNimi());
        }
    }
    
    public String getNimi(String kielikoodi) {
        Preconditions.checkNotNull(kielikoodi, "kielikoodi cannot be null");
        return kaannokset.get(kielikoodi.toLowerCase());
    }
    
    /**
     * Return closest translation for language, first try submitted locale, then "fi", the last fallback is the first translation in the set.
     * @param locale
     * @param model
     * @return
     */
    public String getClosestMonikielinenTeksti(Locale locale) {
        String value = getNimi(locale.getLanguage());
        if(value==null) {
            value = getNimi("fi");
        }
        if(value==null && getKielikaannos().size()>0) {
            value = getKielikaannos().iterator().next().getNimi();
        }
        return value;
    }


    @Override
    public int compareTo(MonikielinenTekstiModel other) {
        Locale locale = I18N.getLocale();
        final String t1 = this. getClosestMonikielinenTeksti(locale);
        final String t2 = other.getClosestMonikielinenTeksti(locale);
        if(t1!=null && t2!=null) return t1.compareTo(t2);
        if(t1!=null) return 1;
        return -1;
    }
    
    

    
}
