/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
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
 */
package fi.vm.sade.tarjonta.service.resources.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Collection of language dependant values.
 *
 * @author mlyly
 */
public class MonikielinenTekstisDTO implements Serializable {

    private List<MonikielinenTekstiDTO> _arvot = new ArrayList<MonikielinenTekstiDTO>();

    public List<MonikielinenTekstiDTO> getArvot() {
        if (_arvot == null) {
            _arvot = new ArrayList<MonikielinenTekstiDTO>();
        }
        return _arvot;
    }

    public void setArvot(List<MonikielinenTekstiDTO> _arvot) {
        this._arvot = _arvot;
    }


    /**
     * Add language text.
     *
     * @param kieliUri
     * @param arvo
     */
    public void addKieli(String kieliUri, String arvo) {
        MonikielinenTekstiDTO t = new MonikielinenTekstiDTO();
        t.setArvo(arvo);
        t.setKieliUri(kieliUri);
        addKieli(t);
    }


    /**
     * Add value with given language.
     *
     * @param mkt
     */
    public void addKieli(MonikielinenTekstiDTO mkt) {
        for (MonikielinenTekstiDTO monikielinenTeksti : getArvot()) {
            // Already there?
            if (monikielinenTeksti.getKieliUri().equals(mkt.getKieliUri())) {
                // Yes, already saved, just update content
                monikielinenTeksti.setArvo(mkt.getArvo());
                return;
            }
        }
        // No, just insert new
        getArvot().add(mkt);
    }

    /**
     * Get value for given language.
     *
     * @param kieliUri
     * @return
     */
    public MonikielinenTekstiDTO getKieli(String kieliUri) {
        for (MonikielinenTekstiDTO monikielinenTeksti : getArvot()) {
            if (monikielinenTeksti.getKieliUri().equals(kieliUri)) {
                return monikielinenTeksti;
            }
        }
        return null;
    }


}
