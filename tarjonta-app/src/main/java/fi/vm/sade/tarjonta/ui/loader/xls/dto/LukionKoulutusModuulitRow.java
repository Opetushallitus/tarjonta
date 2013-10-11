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
package fi.vm.sade.tarjonta.ui.loader.xls.dto;

import fi.vm.sade.tarjonta.ui.loader.xls.Column;
import fi.vm.sade.tarjonta.ui.loader.xls.InputColumnType;
import static fi.vm.sade.tarjonta.ui.loader.xls.dto.AbstractKoulutuskoodiField.KOULUTUSKOODI_PROPERTY;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 *
 * @author Jani Wilén
 */
public class LukionKoulutusModuulitRow extends AbstractKoulutuskoodiField {

    public static final String FILENAME = "LUKION_MODUULIT_konversio_html_final";
    public static final Column[] COLUMNS = {
        new Column(KOULUTUSKOODI_PROPERTY, "koulutus", InputColumnType.INTEGER),
        new Column("koulutuksellisetTekstiFi", "Koulutukselliset tavoitteet", InputColumnType.STRING),
        new Column("koulutuksenRakenneTekstiFi", "Koulutuksen rakenne", InputColumnType.STRING),
        new Column("jatkoOpintomahdollisuudetTekstiFi", "Jatko-opintomahdollisuudet", InputColumnType.STRING),
        new Column("koulutuksellisetTekstiSv", "sv Koulutukselliset tavoitteet", InputColumnType.STRING),
        new Column("koulutuksenRakenneTekstiSv", "sv Koulutuksen rakenne", InputColumnType.STRING),
        new Column("jatkoOpintomahdollisuudetTekstiSv", "sv Jatko-opintomahdollisuudet", InputColumnType.STRING),
        new Column("koulutuksellisetTekstiEn", "en Koulutukselliset tavoitteet en", InputColumnType.STRING),
        new Column("koulutuksenRakenneTekstiEn", "en Koulutuksen rakenne", InputColumnType.STRING),
        new Column("jatkoOpintomahdollisuudetTekstiEn", "en Jatko-opintomahdollisuudet", InputColumnType.STRING)};
    //fi
    private String koulutuksellisetTekstiFi;
    private String koulutuksenRakenneTekstiFi;
    private String jatkoOpintomahdollisuudetTekstiFi;
    //sv
    private String koulutuksellisetTekstiSv;
    private String koulutuksenRakenneTekstiSv;
    private String jatkoOpintomahdollisuudetTekstiSv;
    //en
    private String koulutuksellisetTekstiEn;
    private String koulutuksenRakenneTekstiEn;
    private String jatkoOpintomahdollisuudetTekstiEn;

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    /**
     * @return the koulutuksellisetTekstiSv
     */
    public String getKoulutuksellisetTekstiSv() {
        return koulutuksellisetTekstiSv;
    }

    /**
     * @param koulutuksellisetTekstiSv the koulutuksellisetTekstiSv to set
     */
    public void setKoulutuksellisetTekstiSv(String koulutuksellisetTekstiSv) {
        this.koulutuksellisetTekstiSv = koulutuksellisetTekstiSv;
    }

    /**
     * @return the koulutuksenRakenneTekstiSv
     */
    public String getKoulutuksenRakenneTekstiSv() {
        return koulutuksenRakenneTekstiSv;
    }

    /**
     * @param koulutuksenRakenneTekstiSv the koulutuksenRakenneTekstiSv to set
     */
    public void setKoulutuksenRakenneTekstiSv(String koulutuksenRakenneTekstiSv) {
        this.koulutuksenRakenneTekstiSv = koulutuksenRakenneTekstiSv;
    }

    /**
     * @return the jatkoOpintomahdollisuudetTekstiSv
     */
    public String getJatkoOpintomahdollisuudetTekstiSv() {
        return jatkoOpintomahdollisuudetTekstiSv;
    }

    /**
     * @param jatkoOpintomahdollisuudetTekstiSv the
     * jatkoOpintomahdollisuudetTekstiSv to set
     */
    public void setJatkoOpintomahdollisuudetTekstiSv(String jatkoOpintomahdollisuudetTekstiSv) {
        this.jatkoOpintomahdollisuudetTekstiSv = jatkoOpintomahdollisuudetTekstiSv;
    }

    /**
     * @return the koulutuksellisetTekstiFi
     */
    public String getKoulutuksellisetTekstiFi() {
        return koulutuksellisetTekstiFi;
    }

    /**
     * @param koulutuksellisetTekstiFi the koulutuksellisetTekstiFi to set
     */
    public void setKoulutuksellisetTekstiFi(String koulutuksellisetTekstiFi) {
        this.koulutuksellisetTekstiFi = koulutuksellisetTekstiFi;
    }

    /**
     * @return the koulutuksenRakenneTekstiFi
     */
    public String getKoulutuksenRakenneTekstiFi() {
        return koulutuksenRakenneTekstiFi;
    }

    /**
     * @param koulutuksenRakenneTekstiFi the koulutuksenRakenneTekstiFi to set
     */
    public void setKoulutuksenRakenneTekstiFi(String koulutuksenRakenneTekstiFi) {
        this.koulutuksenRakenneTekstiFi = koulutuksenRakenneTekstiFi;
    }

    /**
     * @return the jatkoOpintomahdollisuudetTekstiFi
     */
    public String getJatkoOpintomahdollisuudetTekstiFi() {
        return jatkoOpintomahdollisuudetTekstiFi;
    }

    /**
     * @param jatkoOpintomahdollisuudetTekstiFi the
     * jatkoOpintomahdollisuudetTekstiFi to set
     */
    public void setJatkoOpintomahdollisuudetTekstiFi(String jatkoOpintomahdollisuudetTekstiFi) {
        this.jatkoOpintomahdollisuudetTekstiFi = jatkoOpintomahdollisuudetTekstiFi;
    }

    /**
     * @return the koulutuksellisetTekstiEn
     */
    public String getKoulutuksellisetTekstiEn() {
        return koulutuksellisetTekstiEn;
    }

    /**
     * @param koulutuksellisetTekstiEn the koulutuksellisetTekstiEn to set
     */
    public void setKoulutuksellisetTekstiEn(String koulutuksellisetTekstiEn) {
        this.koulutuksellisetTekstiEn = koulutuksellisetTekstiEn;
    }

    /**
     * @return the koulutuksenRakenneTekstiEn
     */
    public String getKoulutuksenRakenneTekstiEn() {
        return koulutuksenRakenneTekstiEn;
    }

    /**
     * @param koulutuksenRakenneTekstiEn the koulutuksenRakenneTekstiEn to set
     */
    public void setKoulutuksenRakenneTekstiEn(String koulutuksenRakenneTekstiEn) {
        this.koulutuksenRakenneTekstiEn = koulutuksenRakenneTekstiEn;
    }

    /**
     * @return the jatkoOpintomahdollisuudetTekstiEn
     */
    public String getJatkoOpintomahdollisuudetTekstiEn() {
        return jatkoOpintomahdollisuudetTekstiEn;
    }

    /**
     * @param jatkoOpintomahdollisuudetTekstiEn the
     * jatkoOpintomahdollisuudetTekstiEn to set
     */
    public void setJatkoOpintomahdollisuudetTekstiEn(String jatkoOpintomahdollisuudetTekstiEn) {
        this.jatkoOpintomahdollisuudetTekstiEn = jatkoOpintomahdollisuudetTekstiEn;
    }
}
