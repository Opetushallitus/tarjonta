package fi.vm.sade.tarjonta.ui.model;

import com.google.common.base.Preconditions;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;

/*
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
/**
 * @author: Tuomas Katva Date: 18.2.2013
 */
public class HakukohdeNameUriModel {

    private String hakukohdeNimi;
    private String hakukohdeUri;
    private String hakukohdeArvo;
    private Integer uriVersio;

    public String getHakukohdeNimi() {
        return hakukohdeNimi;
    }

    public void setHakukohdeNimi(String hakukohdeNimi) {
        this.hakukohdeNimi = hakukohdeNimi;
    }

    public String getHakukohdeUri() {
        return hakukohdeUri;
    }

    public void setHakukohdeUri(String hakukohdeUri) {
        this.hakukohdeUri = hakukohdeUri;
    }

    public String getHakukohdeArvo() {
        return hakukohdeArvo;
    }

    public void setHakukohdeArvo(String hakukohdeArvo) {
        this.hakukohdeArvo = hakukohdeArvo;
    }

    @Override
    public String toString() {
        return hakukohdeNimi;
    }

    public Integer getUriVersio() {
        return uriVersio;
    }

    public void setUriVersio(Integer uriVersio) {
        this.uriVersio = uriVersio;
    }

    public String getKoodiUriWithVersion() {
        Preconditions.checkNotNull(this.getHakukohdeUri(), "Hakukohde koodi URI cannot be null.");
        Preconditions.checkNotNull(this.getUriVersio(), "Hakukohde koodi version cannot be null.");
        return TarjontaUIHelper.createVersionUri(this.getHakukohdeUri(), this.getUriVersio());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        HakukohdeNameUriModel that = (HakukohdeNameUriModel) o;

        if (hakukohdeArvo != null ? !hakukohdeArvo.equals(that.hakukohdeArvo) : that.hakukohdeArvo != null) {
            return false;
        }
        if (hakukohdeNimi != null ? !hakukohdeNimi.equals(that.hakukohdeNimi) : that.hakukohdeNimi != null) {
            return false;
        }
        if (hakukohdeUri != null ? !hakukohdeUri.equals(that.hakukohdeUri) : that.hakukohdeUri != null) {
            return false;
        }
        if (uriVersio != null ? !uriVersio.equals(that.uriVersio) : that.uriVersio != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = hakukohdeNimi != null ? hakukohdeNimi.hashCode() : 0;
        result = 31 * result + (hakukohdeUri != null ? hakukohdeUri.hashCode() : 0);
        result = 31 * result + (hakukohdeArvo != null ? hakukohdeArvo.hashCode() : 0);
        result = 31 * result + (uriVersio != null ? uriVersio.hashCode() : 0);
        return result;
    }
}
