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

/**
 * @author: Tuomas Katva
 * Date: 18.2.2013
 */
public class HakukohdeNameUriModel {

    private String hakukohdeNimi;

    private String hakukohdeUri;

    private String hakukohdeArvo;


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
}
