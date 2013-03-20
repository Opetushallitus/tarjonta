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

import com.google.common.collect.Maps;

import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusLisatietoModel;

public class KoulutusLukioKuvailevatTiedotViewModel extends KoulutusRelaatioModel {

    private static final long serialVersionUID = 1L;

    // diplomi
    private List<String> diplomit;

    // kielet
    private List<String> kieliA;
    @Override
    public String toString() {
        return "KoulutusLukioKuvailevatTiedotViewModel [diplomit=" + diplomit + ", kieliA=" + kieliA + ", kieliB1="
                + kieliB1 + ", kieliB2=" + kieliB2 + ", kieliB3=" + kieliB3 + ", kieletMuu=" + kieletMuu + ", tekstit="
                + tekstit + "]";
    }

    private List<String> kieliB1;
    private List<String> kieliB2;
    private List<String> kieliB3;
    private List<String> kieletMuu;

    // tekstikentät
    private Map<String, KoulutusLisatietoModel> tekstit = Maps.newHashMap();

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

}
