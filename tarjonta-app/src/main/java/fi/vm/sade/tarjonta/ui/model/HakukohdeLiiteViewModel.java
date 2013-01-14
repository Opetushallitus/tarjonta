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

import java.util.Date;

/**
 * Created by: Tuomas Katva
 * Date: 14.1.2013
 */
public class HakukohdeLiiteViewModel {

    private String liitteenTyyppi;

    private String liitteenSanallinenKuvaus;

    private Date toimitettavaMennessa;

    private String osoiteRivi1;

    private String osoiteRivi2;

    private String postinumero;

    private String postitoimiPaikka;

    private String sahkoinenToimitusOsoite;


    public String getLiitteenTyyppi() {
        return liitteenTyyppi;
    }

    public void setLiitteenTyyppi(String liitteenTyyppi) {
        this.liitteenTyyppi = liitteenTyyppi;
    }

    public String getLiitteenSanallinenKuvaus() {
        return liitteenSanallinenKuvaus;
    }

    public void setLiitteenSanallinenKuvaus(String liitteenSanallinenKuvaus) {
        this.liitteenSanallinenKuvaus = liitteenSanallinenKuvaus;
    }

    public Date getToimitettavaMennessa() {
        return toimitettavaMennessa;
    }

    public void setToimitettavaMennessa(Date toimitettavaMennessa) {
        this.toimitettavaMennessa = toimitettavaMennessa;
    }

    public String getOsoiteRivi1() {
        return osoiteRivi1;
    }

    public void setOsoiteRivi1(String osoiteRivi1) {
        this.osoiteRivi1 = osoiteRivi1;
    }

    public String getOsoiteRivi2() {
        return osoiteRivi2;
    }

    public void setOsoiteRivi2(String osoiteRivi2) {
        this.osoiteRivi2 = osoiteRivi2;
    }

    public String getPostinumero() {
        return postinumero;
    }

    public void setPostinumero(String postinumero) {
        this.postinumero = postinumero;
    }

    public String getPostitoimiPaikka() {
        return postitoimiPaikka;
    }

    public void setPostitoimiPaikka(String postitoimiPaikka) {
        this.postitoimiPaikka = postitoimiPaikka;
    }

    public String getSahkoinenToimitusOsoite() {
        return sahkoinenToimitusOsoite;
    }

    public void setSahkoinenToimitusOsoite(String sahkoinenToimitusOsoite) {
        this.sahkoinenToimitusOsoite = sahkoinenToimitusOsoite;
    }
}
