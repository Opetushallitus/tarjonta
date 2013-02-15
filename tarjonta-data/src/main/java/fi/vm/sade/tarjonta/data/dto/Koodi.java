package fi.vm.sade.tarjonta.data.dto;/*
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
 * Date: 13.2.2013
 */

/*
* This bean is populated with values from Excel-sheet containing koodisto data
 */
public class Koodi {

    private String koodiArvo;

    private String koodiNimiFi;
    private String koodiNimiSv;
    private String koodiNimiEn;

    private String koodiKuvausFi;
    private String koodiKuvausSv;
    private String koodiKuvausEn;

    private String koodiLyhytNimiFi;
    private String koodiLyhytNimiSv;
    private String koodiLyhytNimiEn;


    public String getKoodiArvo() {
        return koodiArvo;
    }

    public void setKoodiArvo(String koodiArvo) {
        this.koodiArvo = koodiArvo;
    }

    public String getKoodiNimiFi() {
        return koodiNimiFi;
    }

    public void setKoodiNimiFi(String koodiNimiFi) {
        this.koodiNimiFi = koodiNimiFi;
    }

    public String getKoodiNimiSv() {
        return koodiNimiSv;
    }

    public void setKoodiNimiSv(String koodiNimiSv) {
        this.koodiNimiSv = koodiNimiSv;
    }

    public String getKoodiNimiEn() {
        return koodiNimiEn;
    }

    public void setKoodiNimiEn(String koodiNimiEn) {
        this.koodiNimiEn = koodiNimiEn;
    }

    public String getKoodiKuvausFi() {
        return koodiKuvausFi;
    }

    public void setKoodiKuvausFi(String koodiKuvausFi) {
        this.koodiKuvausFi = koodiKuvausFi;
    }

    public String getKoodiKuvausSv() {
        return koodiKuvausSv;
    }

    public void setKoodiKuvausSv(String koodiKuvausSv) {
        this.koodiKuvausSv = koodiKuvausSv;
    }

    public String getKoodiKuvausEn() {
        return koodiKuvausEn;
    }

    public void setKoodiKuvausEn(String koodiKuvausEn) {
        this.koodiKuvausEn = koodiKuvausEn;
    }

    public String getKoodiLyhytNimiFi() {
        return koodiLyhytNimiFi;
    }

    public void setKoodiLyhytNimiFi(String koodiLyhytNimiFi) {
        this.koodiLyhytNimiFi = koodiLyhytNimiFi;
    }

    public String getKoodiLyhytNimiSv() {
        return koodiLyhytNimiSv;
    }

    public void setKoodiLyhytNimiSv(String koodiLyhytNimiSv) {
        this.koodiLyhytNimiSv = koodiLyhytNimiSv;
    }

    public String getKoodiLyhytNimiEn() {
        return koodiLyhytNimiEn;
    }

    public void setKoodiLyhytNimiEn(String koodiLyhytNimiEn) {
        this.koodiLyhytNimiEn = koodiLyhytNimiEn;
    }
}
