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

/**
 *
 * @author Jani Wilén
 */
public class TutkinnonKuvauksetNuoretRow extends AbstractKoulutuskoodiField {

    public static final String FILENAME = "Tutkinnon_kuvaukset_nuoret_KONVERTOINTI_taydennetty_html_final";
    public static final Column[] COLUMNS = {
        new Column(KOULUTUSKOODI_PROPERTY, "TUT_KOUKOODI", InputColumnType.INTEGER),
        new Column("tukinnonNimiFiTeksti", "TUTN_NIMI", InputColumnType.STRING),
        new Column("tukinnonNimiSvTeksti", "TUTN_NIMI_1", InputColumnType.STRING),
        new Column("koulutuksellisetJaAmmatillisetTavoitteetFiTeksti", "KOULUTUKSELLISET JA AMMATILLISET TAVOITTEET FI", InputColumnType.STRING),
        new Column("koulutuksenRakenneFiTeksti", "KOULUTUKSEN RAKENNE FI", InputColumnType.STRING),
        new Column("jatkoOpintomahdollisuudetFiTeksti", "JATKO-OPINTOMAHDOLLISUUDET FI", InputColumnType.STRING),
        new Column("koulutuksellisetJaAmmatillisetTavoitteetSvTeksti", "KOULUTUKSELLISET JA AMMATILLISET TAVOITTEET SV", InputColumnType.STRING),
        new Column("koulutuksenRakenneSvTeksti", "KOULUTUKSEN RAKENNE SV", InputColumnType.STRING),
        new Column("jatkoOpintomahdollisuudetSvTeksti", "JATKO-OPINTOMAHDOLLISUUDET SV", InputColumnType.STRING),
        new Column("koulutuksellisetJaAmmatillisetTavoitteetEnTeksti", "KOULUTUKSELLISET JA AMMATILLISET TAVOITTEET EN", InputColumnType.STRING),
        new Column("koulutuksenRakenneEnTeksti", "KOULUTUKSEN RAKENNE EN", InputColumnType.STRING),
        new Column("jatkoOpintomahdollisuudetEnTeksti", "JATKO-OPINTOMAHDOLLISUUDET EN", InputColumnType.STRING)
    };
    private String tukinnonNimiFiTeksti;
    private String tukinnonNimiSvTeksti;
    private String koulutuksellisetJaAmmatillisetTavoitteetFiTeksti;
    private String koulutuksenRakenneFiTeksti;
    private String jatkoOpintomahdollisuudetFiTeksti;
    private String koulutuksellisetJaAmmatillisetTavoitteetSvTeksti;
    private String koulutuksenRakenneSvTeksti;
    private String jatkoOpintomahdollisuudetSvTeksti;
    private String koulutuksellisetJaAmmatillisetTavoitteetEnTeksti;
    private String koulutuksenRakenneEnTeksti;
    private String jatkoOpintomahdollisuudetEnTeksti;

    /**
     * @return the tukinnonNimiFiTeksti
     */
    public String getTukinnonNimiFiTeksti() {
        return tukinnonNimiFiTeksti;
    }

    /**
     * @param tukinnonNimiFiTeksti the tukinnonNimiFiTeksti to set
     */
    public void setTukinnonNimiFiTeksti(String tukinnonNimiFiTeksti) {
        this.tukinnonNimiFiTeksti = tukinnonNimiFiTeksti;
    }

    /**
     * @return the tukinnonNimiSvTeksti
     */
    public String getTukinnonNimiSvTeksti() {
        return tukinnonNimiSvTeksti;
    }

    /**
     * @param tukinnonNimiSvTeksti the tukinnonNimiSvTeksti to set
     */
    public void setTukinnonNimiSvTeksti(String tukinnonNimiSvTeksti) {
        this.tukinnonNimiSvTeksti = tukinnonNimiSvTeksti;
    }

    /**
     * @return the koulutuksenRakenneFiTeksti
     */
    public String getKoulutuksenRakenneFiTeksti() {
        return koulutuksenRakenneFiTeksti;
    }

    /**
     * @param koulutuksenRakenneFiTeksti the koulutuksenRakenneFiTeksti to set
     */
    public void setKoulutuksenRakenneFiTeksti(String koulutuksenRakenneFiTeksti) {
        this.koulutuksenRakenneFiTeksti = koulutuksenRakenneFiTeksti;
    }

    /**
     * @return the jatkoOpintomahdollisuudetFiTeksti
     */
    public String getJatkoOpintomahdollisuudetFiTeksti() {
        return jatkoOpintomahdollisuudetFiTeksti;
    }

    /**
     * @param jatkoOpintomahdollisuudetFiTeksti the
     * jatkoOpintomahdollisuudetFiTeksti to set
     */
    public void setJatkoOpintomahdollisuudetFiTeksti(String jatkoOpintomahdollisuudetFiTeksti) {
        this.jatkoOpintomahdollisuudetFiTeksti = jatkoOpintomahdollisuudetFiTeksti;
    }

    /**
     * @return the koulutuksenRakenneSvTeksti
     */
    public String getKoulutuksenRakenneSvTeksti() {
        return koulutuksenRakenneSvTeksti;
    }

    /**
     * @param koulutuksenRakenneSvTeksti the koulutuksenRakenneSvTeksti to set
     */
    public void setKoulutuksenRakenneSvTeksti(String koulutuksenRakenneSvTeksti) {
        this.koulutuksenRakenneSvTeksti = koulutuksenRakenneSvTeksti;
    }

    /**
     * @return the jatkoOpintomahdollisuudetSvTeksti
     */
    public String getJatkoOpintomahdollisuudetSvTeksti() {
        return jatkoOpintomahdollisuudetSvTeksti;
    }

    /**
     * @param jatkoOpintomahdollisuudetSvTeksti the
     * jatkoOpintomahdollisuudetSvTeksti to set
     */
    public void setJatkoOpintomahdollisuudetSvTeksti(String jatkoOpintomahdollisuudetSvTeksti) {
        this.jatkoOpintomahdollisuudetSvTeksti = jatkoOpintomahdollisuudetSvTeksti;
    }

    /**
     * @return the koulutuksenRakenneEnTeksti
     */
    public String getKoulutuksenRakenneEnTeksti() {
        return koulutuksenRakenneEnTeksti;
    }

    /**
     * @param koulutuksenRakenneEnTeksti the koulutuksenRakenneEnTeksti to set
     */
    public void setKoulutuksenRakenneEnTeksti(String koulutuksenRakenneEnTeksti) {
        this.koulutuksenRakenneEnTeksti = koulutuksenRakenneEnTeksti;
    }

    /**
     * @return the jatkoOpintomahdollisuudetEnTeksti
     */
    public String getJatkoOpintomahdollisuudetEnTeksti() {
        return jatkoOpintomahdollisuudetEnTeksti;
    }

    /**
     * @param jatkoOpintomahdollisuudetEnTeksti the
     * jatkoOpintomahdollisuudetEnTeksti to set
     */
    public void setJatkoOpintomahdollisuudetEnTeksti(String jatkoOpintomahdollisuudetEnTeksti) {
        this.jatkoOpintomahdollisuudetEnTeksti = jatkoOpintomahdollisuudetEnTeksti;
    }

    /**
     * @return the koulutuksellisetJaAmmatillisetTavoitteetFiTeksti
     */
    public String getKoulutuksellisetJaAmmatillisetTavoitteetFiTeksti() {
        return koulutuksellisetJaAmmatillisetTavoitteetFiTeksti;
    }

    /**
     * @param koulutuksellisetJaAmmatillisetTavoitteetFiTeksti the
     * koulutuksellisetJaAmmatillisetTavoitteetFiTeksti to set
     */
    public void setKoulutuksellisetJaAmmatillisetTavoitteetFiTeksti(String koulutuksellisetJaAmmatillisetTavoitteetFiTeksti) {
        this.koulutuksellisetJaAmmatillisetTavoitteetFiTeksti = koulutuksellisetJaAmmatillisetTavoitteetFiTeksti;
    }

    /**
     * @return the koulutuksellisetJaAmmatillisetTavoitteetSvTeksti
     */
    public String getKoulutuksellisetJaAmmatillisetTavoitteetSvTeksti() {
        return koulutuksellisetJaAmmatillisetTavoitteetSvTeksti;
    }

    /**
     * @param koulutuksellisetJaAmmatillisetTavoitteetSvTeksti the
     * koulutuksellisetJaAmmatillisetTavoitteetSvTeksti to set
     */
    public void setKoulutuksellisetJaAmmatillisetTavoitteetSvTeksti(String koulutuksellisetJaAmmatillisetTavoitteetSvTeksti) {
        this.koulutuksellisetJaAmmatillisetTavoitteetSvTeksti = koulutuksellisetJaAmmatillisetTavoitteetSvTeksti;
    }

    /**
     * @return the koulutuksellisetJaAmmatillisetTavoitteetEnTeksti
     */
    public String getKoulutuksellisetJaAmmatillisetTavoitteetEnTeksti() {
        return koulutuksellisetJaAmmatillisetTavoitteetEnTeksti;
    }

    /**
     * @param koulutuksellisetJaAmmatillisetTavoitteetEnTeksti the
     * koulutuksellisetJaAmmatillisetTavoitteetEnTeksti to set
     */
    public void setKoulutuksellisetJaAmmatillisetTavoitteetEnTeksti(String koulutuksellisetJaAmmatillisetTavoitteetEnTeksti) {
        this.koulutuksellisetJaAmmatillisetTavoitteetEnTeksti = koulutuksellisetJaAmmatillisetTavoitteetEnTeksti;
    }
}
