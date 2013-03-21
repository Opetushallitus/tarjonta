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
package fi.vm.sade.tarjonta.ui.model.koulutus.lukio;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
import fi.vm.sade.tarjonta.ui.model.BaseUIViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusLisatietoModel;

public class KoulutusLukioKuvailevatTiedotViewModel extends BaseUIViewModel {

    private static final long serialVersionUID = 1L;

    private TarjontaTila tila;

    public TarjontaTila getTila() {
        return tila;
    }

    public void setTila(TarjontaTila tila) {
        this.tila = tila;
    }

    // diplomit
    private List<String> diplomit = Lists.newArrayList();

    // kielet
    private List<String> kieliA = Lists.newArrayList();
    private List<String> kieliB1 = Lists.newArrayList();
    private List<String> kieliB2 = Lists.newArrayList();
    private List<String> kieliB3 = Lists.newArrayList();
    private List<String> kieletMuu = Lists.newArrayList();

    // tekstikentät
    private Map<String, KoulutusLisatietoModel> tekstit = Maps.newHashMap();

    private String oid;

    public Map<String, KoulutusLisatietoModel> getTekstikentat() {
        return tekstit;
    }

    public void setLisatiedot(Map<String, KoulutusLisatietoModel> lisatiedot) {
        this.tekstit = lisatiedot;
    }

    public KoulutusLukioKuvailevatTiedotViewModel(DocumentStatus status) {
        super();
    }

    public List<String> getDiplomit() {
        return diplomit;
    }

    public Collection<String> getKielet() {
        return tekstit.keySet();
    }

    public List<String> getKieletMuu() {
        return kieletMuu;
    }

    public List<String> getKieliA() {
        return kieliA;
    }

    public List<String> getKieliB1() {
        return kieliB1;
    }

    public List<String> getKieliB2() {
        return kieliB2;
    }

    public List<String> getKieliB3() {
        return kieliB3;
    }

    public void setDiplomit(List<String> diplomit) {
        this.diplomit = diplomit;
    }

    public void setKieletMuu(List<String> kieletMuu) {
        this.kieletMuu = kieletMuu;
    }

    public void setKieliA(List<String> kieliA) {
        this.kieliA = kieliA;
    }

    public void setKieliB1(List<String> kieliB1) {
        this.kieliB1 = kieliB1;
    }

    public void setKieliB2(List<String> kieliB2) {
        this.kieliB2 = kieliB2;
    }

    public void setKieliB3(List<String> kieliB3) {
        this.kieliB3 = kieliB3;
    }

    /**
     * Reset model state
     */
    public void clearModel(DocumentStatus status) {
        diplomit.clear();
        kieliA.clear();
        kieliB1.clear();
        kieliB2.clear();
        kieliB3.clear();
        kieletMuu.clear();
        tekstit.clear();
    }

    /**
     * Is model loaded?
     */
    public boolean isLoaded() {
        // TODO Auto-generated method stub
        return false;
    }

    public String getOid() {
        return oid;
    }

    public KoulutusLisatietoModel getLisatiedot(String kieliKoodi) {
        if (!tekstit.containsKey(kieliKoodi)) {
            tekstit.put(kieliKoodi, new KoulutusLisatietoModel());
        }
        return tekstit.get(kieliKoodi);
    }

}
