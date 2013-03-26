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

    private List<KielikaannosViewModel> sanallisetKuvaukset;

    private List<ValintakoeAikaViewModel> valintakoeAjat;

    private String pkAlinPM;
    private String pkYlinPM;
    private String pkAlinHyvaksyttyPM;
    private String lpAlinPM;
    private String lpYlinPM;
    private String lpAlinHyvaksyttyPM;
    private String kpAlinHyvaksyttyPM;
    
    private List<KielikaannosViewModel> lisanayttoKuvaukset;

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
    
    public List<KielikaannosViewModel> getLisanayttoKuvaukset() {
        if (lisanayttoKuvaukset == null) {
            lisanayttoKuvaukset = new ArrayList<KielikaannosViewModel>();
        }
        return lisanayttoKuvaukset;
    }

    public void setLisanayttoKuvaukset(List<KielikaannosViewModel> lisanayttoKuvaukset) {
        this.lisanayttoKuvaukset = lisanayttoKuvaukset;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValintakoeViewModel that = (ValintakoeViewModel) o;

        if (valintakoeAjat != null ? !valintakoeAjat.equals(that.valintakoeAjat) : that.valintakoeAjat != null)
            return false;
        if (valintakoeTunniste != null ? !valintakoeTunniste.equals(that.valintakoeTunniste) : that.valintakoeTunniste != null)
            return false;
        if (valintakoeTyyppi != null ? !valintakoeTyyppi.equals(that.valintakoeTyyppi) : that.valintakoeTyyppi != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = valintakoeTunniste != null ? valintakoeTunniste.hashCode() : 0;
        result = 31 * result + (valintakoeTyyppi != null ? valintakoeTyyppi.hashCode() : 0);
        result = 31 * result + (valintakoeAjat != null ? valintakoeAjat.hashCode() : 0);
        return result;
    }
    
    public String getPkAlinPM() {
        return pkAlinPM;
    }

    public void setPkAlinPM(String pkAlinPM) {
        this.pkAlinPM = pkAlinPM;
    }

    public String getPkYlinPM() {
        return pkYlinPM;
    }

    public void setPkYlinPM(String pkYlinPM) {
        this.pkYlinPM = pkYlinPM;
    }

    public String getPkAlinHyvaksyttyPM() {
        return pkAlinHyvaksyttyPM;
    }

    public void setPkAlinHyvaksyttyPM(String pkAlinHyvaksyttyPM) {
        this.pkAlinHyvaksyttyPM = pkAlinHyvaksyttyPM;
    }

    public String getLpAlinPM() {
        return lpAlinPM;
    }

    public void setLpAlinPM(String lpAlinPM) {
        this.lpAlinPM = lpAlinPM;
    }

    public String getLpYlinPM() {
        return lpYlinPM;
    }

    public void setLpYlinPM(String lpYlinPM) {
        this.lpYlinPM = lpYlinPM;
    }

    public String getLpAlinHyvaksyttyPM() {
        return lpAlinHyvaksyttyPM;
    }

    public void setLpAlinHyvaksyttyPM(String lpAlinHyvaksyttyPM) {
        this.lpAlinHyvaksyttyPM = lpAlinHyvaksyttyPM;
    }

    public String getKpAlinHyvaksyttyPM() {
        return kpAlinHyvaksyttyPM;
    }

    public void setKpAlinHyvaksyttyPM(String aliHyvaksyttyPM) {
        this.kpAlinHyvaksyttyPM = aliHyvaksyttyPM;
    }
}
