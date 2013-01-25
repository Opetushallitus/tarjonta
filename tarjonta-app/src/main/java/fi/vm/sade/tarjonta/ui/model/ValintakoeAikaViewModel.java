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
 * Date: 23.1.2013
 */
public class ValintakoeAikaViewModel {

    private String valintakoeAikaTiedot;

    private String osoiteRivi;
    private String postinumero;
    private String postitoimiPaikka;

    private Date alkamisAika;
    private Date paattymisAika;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValintakoeAikaViewModel that = (ValintakoeAikaViewModel) o;

        if (alkamisAika != null ? !alkamisAika.equals(that.alkamisAika) : that.alkamisAika != null) return false;
        if (osoiteRivi != null ? !osoiteRivi.equals(that.osoiteRivi) : that.osoiteRivi != null) return false;
        if (paattymisAika != null ? !paattymisAika.equals(that.paattymisAika) : that.paattymisAika != null)
            return false;
        if (postinumero != null ? !postinumero.equals(that.postinumero) : that.postinumero != null) return false;
        if (postitoimiPaikka != null ? !postitoimiPaikka.equals(that.postitoimiPaikka) : that.postitoimiPaikka != null)
            return false;
        if (valintakoeAikaTiedot != null ? !valintakoeAikaTiedot.equals(that.valintakoeAikaTiedot) : that.valintakoeAikaTiedot != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = valintakoeAikaTiedot != null ? valintakoeAikaTiedot.hashCode() : 0;
        result = 31 * result + (osoiteRivi != null ? osoiteRivi.hashCode() : 0);
        result = 31 * result + (postinumero != null ? postinumero.hashCode() : 0);
        result = 31 * result + (postitoimiPaikka != null ? postitoimiPaikka.hashCode() : 0);
        result = 31 * result + (alkamisAika != null ? alkamisAika.hashCode() : 0);
        result = 31 * result + (paattymisAika != null ? paattymisAika.hashCode() : 0);
        return result;
    }

    public String getValintakoeAikaTiedot() {
        return valintakoeAikaTiedot;
    }

    public void setValintakoeAikaTiedot(String valintakoeAikaTiedot) {
        this.valintakoeAikaTiedot = valintakoeAikaTiedot;
    }

    public String getOsoiteRivi() {
        return osoiteRivi;
    }

    public void setOsoiteRivi(String osoiteRivi) {
        this.osoiteRivi = osoiteRivi;
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

    public Date getAlkamisAika() {
        return alkamisAika;
    }

    public void setAlkamisAika(Date alkamisAika) {
        this.alkamisAika = alkamisAika;
    }

    public Date getPaattymisAika() {
        return paattymisAika;
    }

    public void setPaattymisAika(Date paattymisAika) {
        this.paattymisAika = paattymisAika;
    }
}
