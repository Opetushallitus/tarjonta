package fi.vm.sade.tarjonta.ui.model;/*
 *
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Tuomas Katva
 * Date: 23.1.2013
 */
public class ValintakoeViewModel {

    private String valintakoeTunniste;

    private String valintakoeTyyppi;

    private ValintakoeAikaViewModel currentAika;

    private List<KielikaannosViewModel> sanallisetKuvaukset;

    private List<ValintakoeAikaViewModel> valintakoeAjat;


    public String getValintakoeTyyppi() {
        return valintakoeTyyppi;
    }

    public void setValintakoeTyyppi(String valintakoeTyyppi) {
        this.valintakoeTyyppi = valintakoeTyyppi;
    }

    public List<KielikaannosViewModel> getSanallisetKuvaukset() {
        if (sanallisetKuvaukset == null) {
            sanallisetKuvaukset = new ArrayList<KielikaannosViewModel>();
        }
        return sanallisetKuvaukset;
    }

    public void setSanallisetKuvaukset(List<KielikaannosViewModel> sanallisetKuvaukset) {
        this.sanallisetKuvaukset = sanallisetKuvaukset;
    }

    public List<ValintakoeAikaViewModel> getValintakoeAjat() {
        if (valintakoeAjat == null) {
            valintakoeAjat = new ArrayList<ValintakoeAikaViewModel>();
        }
        return valintakoeAjat;
    }

    public void setValintakoeAjat(List<ValintakoeAikaViewModel> valintakoeAjat) {
        this.valintakoeAjat = valintakoeAjat;
    }

    public String getValintakoeTunniste() {
        return valintakoeTunniste;
    }

    public void setValintakoeTunniste(String valintakoeTunniste) {
        this.valintakoeTunniste = valintakoeTunniste;
    }

    public ValintakoeAikaViewModel getCurrentAika() {
        return currentAika;
    }

    public void setCurrentAika(ValintakoeAikaViewModel currentAika) {
        this.currentAika = currentAika;
    }
}
