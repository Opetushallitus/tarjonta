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
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 *
 * @author Jani Wilén
 */
public class OppilaitostyyppiRow {

    public static final Column[] OPPILAITOSTYYPPI_RELAATIOT = {
        new Column("oppilaitostyyppiKoodiarvo", "OPPILAITOSTYYPPI", InputColumnType.INTEGER),
        new Column("koulutusasteKoodiarvo1", "KOULUTUSASTE_OPH2002", InputColumnType.INTEGER),
        new Column("koulutusasteKoodiarvo2", "KOULUTUSASTE_OPH2002", InputColumnType.INTEGER),
        new Column("koulutusasteKoodiarvo3", "KOULUTUSASTE_OPH2002", InputColumnType.INTEGER),
        new Column("koulutusasteKoodiarvo4", "KOULUTUSASTE_OPH2002", InputColumnType.INTEGER),
        new Column("koulutusasteKoodiarvo5", "KOULUTUSASTE_OPH2002", InputColumnType.INTEGER),
        new Column("koulutusasteKoodiarvo6", "KOULUTUSASTE_OPH2002", InputColumnType.INTEGER),
        new Column("koulutusasteKoodiarvo7", "KOULUTUSASTE_OPH2002", InputColumnType.INTEGER),
        new Column("koulutusasteKoodiarvo8", "KOULUTUSASTE_OPH2002", InputColumnType.INTEGER)
    };
    private String oppilaitostyyppiKoodiarvo;
    private String koulutusasteKoodiarvo1;
    private String koulutusasteKoodiarvo2;
    private String koulutusasteKoodiarvo3;
    private String koulutusasteKoodiarvo4;
    private String koulutusasteKoodiarvo5;
    private String koulutusasteKoodiarvo6;
    private String koulutusasteKoodiarvo7;
    private String koulutusasteKoodiarvo8;

    public List<String> getKoulutusastes() {
        List<String> list = new LinkedList<String>(Arrays.asList(
                koulutusasteKoodiarvo1,
                koulutusasteKoodiarvo2,
                koulutusasteKoodiarvo3,
                koulutusasteKoodiarvo4,
                koulutusasteKoodiarvo5,
                koulutusasteKoodiarvo6,
                koulutusasteKoodiarvo7,
                koulutusasteKoodiarvo8));

        //remove nulls
        list.removeAll(Collections.singleton(null));

        return list;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    /**
     * @return the oppilaitostyyppiKoodiarvo
     */
    public String getOppilaitostyyppiKoodiarvo() {
        return oppilaitostyyppiKoodiarvo;
    }

    /**
     * @param oppilaitostyyppiKoodiarvo the oppilaitostyyppiKoodiarvo to set
     */
    public void setOppilaitostyyppiKoodiarvo(String oppilaitostyyppiKoodiarvo) {
        this.oppilaitostyyppiKoodiarvo = oppilaitostyyppiKoodiarvo;
    }

    /**
     * @return the koulutusasteKoodiarvo1
     */
    public String getKoulutusasteKoodiarvo1() {
        return koulutusasteKoodiarvo1;
    }

    /**
     * @param koulutusasteKoodiarvo1 the koulutusasteKoodiarvo1 to set
     */
    public void setKoulutusasteKoodiarvo1(String koulutusasteKoodiarvo1) {
        this.koulutusasteKoodiarvo1 = koulutusasteKoodiarvo1;
    }

    /**
     * @return the koulutusasteKoodiarvo2
     */
    public String getKoulutusasteKoodiarvo2() {
        return koulutusasteKoodiarvo2;
    }

    /**
     * @param koulutusasteKoodiarvo2 the koulutusasteKoodiarvo2 to set
     */
    public void setKoulutusasteKoodiarvo2(String koulutusasteKoodiarvo2) {
        this.koulutusasteKoodiarvo2 = koulutusasteKoodiarvo2;
    }

    /**
     * @return the koulutusasteKoodiarvo3
     */
    public String getKoulutusasteKoodiarvo3() {
        return koulutusasteKoodiarvo3;
    }

    /**
     * @param koulutusasteKoodiarvo3 the koulutusasteKoodiarvo3 to set
     */
    public void setKoulutusasteKoodiarvo3(String koulutusasteKoodiarvo3) {
        this.koulutusasteKoodiarvo3 = koulutusasteKoodiarvo3;
    }

    /**
     * @return the koulutusasteKoodiarvo4
     */
    public String getKoulutusasteKoodiarvo4() {
        return koulutusasteKoodiarvo4;
    }

    /**
     * @param koulutusasteKoodiarvo4 the koulutusasteKoodiarvo4 to set
     */
    public void setKoulutusasteKoodiarvo4(String koulutusasteKoodiarvo4) {
        this.koulutusasteKoodiarvo4 = koulutusasteKoodiarvo4;
    }

    /**
     * @return the koulutusasteKoodiarvo5
     */
    public String getKoulutusasteKoodiarvo5() {
        return koulutusasteKoodiarvo5;
    }

    /**
     * @param koulutusasteKoodiarvo5 the koulutusasteKoodiarvo5 to set
     */
    public void setKoulutusasteKoodiarvo5(String koulutusasteKoodiarvo5) {
        this.koulutusasteKoodiarvo5 = koulutusasteKoodiarvo5;
    }

    /**
     * @return the koulutusasteKoodiarvo6
     */
    public String getKoulutusasteKoodiarvo6() {
        return koulutusasteKoodiarvo6;
    }

    /**
     * @param koulutusasteKoodiarvo6 the koulutusasteKoodiarvo6 to set
     */
    public void setKoulutusasteKoodiarvo6(String koulutusasteKoodiarvo6) {
        this.koulutusasteKoodiarvo6 = koulutusasteKoodiarvo6;
    }

    /**
     * @return the koulutusasteKoodiarvo7
     */
    public String getKoulutusasteKoodiarvo7() {
        return koulutusasteKoodiarvo7;
    }

    /**
     * @param koulutusasteKoodiarvo7 the koulutusasteKoodiarvo7 to set
     */
    public void setKoulutusasteKoodiarvo7(String koulutusasteKoodiarvo7) {
        this.koulutusasteKoodiarvo7 = koulutusasteKoodiarvo7;
    }

    /**
     * @return the koulutusasteKoodiarvo8
     */
    public String getKoulutusasteKoodiarvo8() {
        return koulutusasteKoodiarvo8;
    }

    /**
     * @param koulutusasteKoodiarvo8 the koulutusasteKoodiarvo8 to set
     */
    public void setKoulutusasteKoodiarvo8(String koulutusasteKoodiarvo8) {
        this.koulutusasteKoodiarvo8 = koulutusasteKoodiarvo8;
    }
}
