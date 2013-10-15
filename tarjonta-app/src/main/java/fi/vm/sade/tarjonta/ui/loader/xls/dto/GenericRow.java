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
public class GenericRow extends AbstractKoulutuskoodiField {
    public static final String VALMENTAVA_JA_KUNTOUTTAVA = "valmentava_ja_kuntouttava";
    public static final String FILENAME_AMMATILLINEN = "KOULUTUS_KOULUTUSOHJELMA_RELAATIO";
    public static final String FILENAME_LUKIO = "KOULUTUS_LUKIOLINJAT_relaatio";
    public static final Column[] COLUMNS_AMMATILLINEN = {
        new Column(KOULUTUSKOODI_PROPERTY, "KOULUTUS", InputColumnType.INTEGER),
        new Column("relaatioKoodiarvo", "KOULUTUOSOHJELMA", InputColumnType.INTEGER),
        new Column("eqfKoodiarvo", "EQF", InputColumnType.INTEGER),
        new Column("laajuusyksikkoKoodiarvo", "Laajuusyksikko", InputColumnType.INTEGER),
        new Column("laajuusKoodiarvo", "Laajuus", InputColumnType.INTEGER),
        new Column("koulutusasteKoodiarvo", "Koulutusasteen", InputColumnType.INTEGER)
    };
    public static final Column[] COLUMNS_LUKIO = {
        new Column(KOULUTUSKOODI_PROPERTY, "KOULUTUS", InputColumnType.INTEGER),
        new Column("relaatioKoodiarvo", "LUKIOLINJA", InputColumnType.STRING),
        new Column("eqfKoodiarvo", "EQF", InputColumnType.INTEGER),
        new Column("laajuusyksikkoKoodiarvo", "Laajuusyksikko", InputColumnType.INTEGER),
        new Column("laajuusKoodiarvo", "Laajuus", InputColumnType.INTEGER),
        new Column("koulutusasteKoodiarvo", "Koulutusasteen", InputColumnType.INTEGER)
    };
    public static final Column[] COLUMNS_VALMENTAVA = {
        new Column(KOULUTUSKOODI_PROPERTY, "KOULUTUS", InputColumnType.INTEGER),
        new Column("eqfKoodiarvo", "EQF", InputColumnType.INTEGER),
        new Column("laajuusyksikkoKoodiarvo", "Laajuusyksikko", InputColumnType.INTEGER),
        new Column("laajuusKoodiarvo", "Laajuus", InputColumnType.INTEGER),
        new Column("koulutusasteKoodiarvo", "Koulutusasteen", InputColumnType.INTEGER)
    };
    
    private String relaatioKoodiarvo;
    private String koulutusasteKoodiarvo;
    private String laajuusKoodiarvo;
    private String laajuusyksikkoKoodiarvo;
    private String eqfKoodiarvo;

    /**
     * @return the relaatioKoodiarvo
     */
    public String getRelaatioKoodiarvo() {
        return relaatioKoodiarvo;
    }

    /**
     * @param relaatioKoodiarvo the relaatioKoodiarvo to set
     */
    public void setRelaatioKoodiarvo(String relaatioKoodiarvo) {
        this.relaatioKoodiarvo = relaatioKoodiarvo;
    }

    /**
     * @return the koulutusasteKoodiarvo
     */
    public String getKoulutusasteKoodiarvo() {
        return koulutusasteKoodiarvo;
    }

    /**
     * @param koulutusasteKoodiarvo the koulutusasteKoodiarvo to set
     */
    public void setKoulutusasteKoodiarvo(String koulutusasteKoodiarvo) {
        this.koulutusasteKoodiarvo = koulutusasteKoodiarvo;
    }

    /**
     * @return the laajuusKoodiarvo
     */
    public String getLaajuusKoodiarvo() {
        return laajuusKoodiarvo;
    }

    /**
     * @param laajuusKoodiarvo the laajuusKoodiarvo to set
     */
    public void setLaajuusKoodiarvo(String laajuusKoodiarvo) {
        this.laajuusKoodiarvo = laajuusKoodiarvo;
    }

    /**
     * @return the laajuusyksikkoKoodiarvo
     */
    public String getLaajuusyksikkoKoodiarvo() {
        return laajuusyksikkoKoodiarvo;
    }

    /**
     * @param laajuusyksikkoKoodiarvo the laajuusyksikkoKoodiarvo to set
     */
    public void setLaajuusyksikkoKoodiarvo(String laajuusyksikkoKoodiarvo) {
        this.laajuusyksikkoKoodiarvo = laajuusyksikkoKoodiarvo;
    }

    /**
     * @return the eqfKoodiarvo
     */
    public String getEqfKoodiarvo() {
        return eqfKoodiarvo;
    }

    /**
     * @param eqfKoodiarvo the eqfKoodiarvo to set
     */
    public void setEqfKoodiarvo(String eqfKoodiarvo) {
        this.eqfKoodiarvo = eqfKoodiarvo;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
