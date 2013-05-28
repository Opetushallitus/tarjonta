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
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 *
 * @author Jani Wilén
 */
public class KoulutusohjelmanKuvauksetRow {

    public static final String FILENAME = "Kouutusohjelman_kuvaukset_KONVERSIO";
    public static final Column[] COLUMNS = {
        new Column("koulutusohjelmaKoodiarvo", "KOUOLUTUSOHJELMA", InputColumnType.INTEGER),
        new Column("koulutusohjelmanSeliteTeksti", "KOK_SSELITE", InputColumnType.STRING),
        new Column("koulutusohjelmanTavoiteFiTeksti", "KOULUTUSOHJELMAN TAVOITE FI", InputColumnType.STRING),
        new Column("koulutusohjelmanTavoiteSvTeksti", "KOULUTUSOHJELMAN TAVOITE SV", InputColumnType.STRING)};
    private String koulutusohjelmaKoodiarvo;
    private String koulutusohjelmanSeliteTeksti;
    private String koulutusohjelmanTavoiteFiTeksti;
    private String koulutusohjelmanTavoiteSvTeksti;

    /**
     * @return the koulutusohjelmaKoodiarvo
     */
    public String getKoulutusohjelmaKoodiarvo() {
        return koulutusohjelmaKoodiarvo;
    }

    /**
     * @param koulutusohjelmaKoodiarvo the koulutusohjelmaKoodiarvo to set
     */
    public void setKoulutusohjelmaKoodiarvo(String koulutusohjelmaKoodiarvo) {
        this.koulutusohjelmaKoodiarvo = koulutusohjelmaKoodiarvo;
    }

    /**
     * @return the koulutusohjelmanSeliteTeksti
     */
    public String getKoulutusohjelmanSeliteTeksti() {
        return koulutusohjelmanSeliteTeksti;
    }

    /**
     * @param koulutusohjelmanSeliteTeksti the koulutusohjelmanSeliteTeksti to
     * set
     */
    public void setKoulutusohjelmanSeliteTeksti(String koulutusohjelmanSeliteTeksti) {
        this.koulutusohjelmanSeliteTeksti = koulutusohjelmanSeliteTeksti;
    }

    /**
     * @return the koulutusohjelmanTavoiteFiTeksti
     */
    public String getKoulutusohjelmanTavoiteFiTeksti() {
        return koulutusohjelmanTavoiteFiTeksti;
    }

    /**
     * @param koulutusohjelmanTavoiteFiTeksti the
     * koulutusohjelmanTavoiteFiTeksti to set
     */
    public void setKoulutusohjelmanTavoiteFiTeksti(String koulutusohjelmanTavoiteFiTeksti) {
        this.koulutusohjelmanTavoiteFiTeksti = koulutusohjelmanTavoiteFiTeksti;
    }

    /**
     * @return the koulutusohjelmanTavoiteSvTeksti
     */
    public String getKoulutusohjelmanTavoiteSvTeksti() {
        return koulutusohjelmanTavoiteSvTeksti;
    }

    /**
     * @param koulutusohjelmanTavoiteSvTeksti the
     * koulutusohjelmanTavoiteSvTeksti to set
     */
    public void setKoulutusohjelmanTavoiteSvTeksti(String koulutusohjelmanTavoiteSvTeksti) {
        this.koulutusohjelmanTavoiteSvTeksti = koulutusohjelmanTavoiteSvTeksti;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
