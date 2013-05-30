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

/**
 * Osoite (address) to REST API Osoite.
 *
 * @author mlyly
 */
public class OsoiteRDTO extends BaseRDTO {

    private String osoiterivi1;
    private String osoiterivi2;
    private String postinumero;
    private String postitoimipaikka;

    public String getOsoiterivi1() {
        return osoiterivi1;
    }

    public void setOsoiterivi1(String osoiterivi1) {
        this.osoiterivi1 = osoiterivi1;
    }

    public String getOsoiterivi2() {
        return osoiterivi2;
    }

    public void setOsoiterivi2(String osoiterivi2) {
        this.osoiterivi2 = osoiterivi2;
    }

    public String getPostinumero() {
        return postinumero;
    }

    public void setPostinumero(String postinumero) {
        this.postinumero = postinumero;
    }

    public String getPostitoimipaikka() {
        return postitoimipaikka;
    }

    public void setPostitoimipaikka(String postitoimipaikka) {
        this.postitoimipaikka = postitoimipaikka;
    }

}
