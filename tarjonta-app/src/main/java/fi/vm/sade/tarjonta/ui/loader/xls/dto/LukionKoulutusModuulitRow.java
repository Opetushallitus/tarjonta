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

    public static final String FILENAME = "LUKION_MODUULIT_konversio";
    public static final Column[] COLUMNS = {
        new Column(KOULUTUSKOODI_PROPERTY, "koulutus", InputColumnType.INTEGER),
        new Column("koulutuksellisetTeksti", "Koulutukselliset tavoitteet", InputColumnType.STRING),
        new Column("koulutuksenRakenneTeksti", "Koulutuksen rakenne", InputColumnType.STRING),
        new Column("jatkoOpintomahdollisuudetTeksti", "Jatko-opintomahdollisuudet", InputColumnType.STRING)};
    private String koulutuksellisetTeksti;
    private String koulutuksenRakenneTeksti;
    private String jatkoOpintomahdollisuudetTeksti;

    /**
     * @return the koulutuksellisetTeksti
     */
    public String getKoulutuksellisetTeksti() {
        return koulutuksellisetTeksti;
    }

    /**
     * @param koulutuksellisetTeksti the koulutuksellisetTeksti to set
     */
    public void setKoulutuksellisetTeksti(String koulutuksellisetTeksti) {
        this.koulutuksellisetTeksti = koulutuksellisetTeksti;
    }

    /**
     * @return the koulutuksenRakenneTeksti
     */
    public String getKoulutuksenRakenneTeksti() {
        return koulutuksenRakenneTeksti;
    }

    /**
     * @param koulutuksenRakenneTeksti the koulutuksenRakenneTeksti to set
     */
    public void setKoulutuksenRakenneTeksti(String koulutuksenRakenneTeksti) {
        this.koulutuksenRakenneTeksti = koulutuksenRakenneTeksti;
    }

    /**
     * @return the jatkoOpintomahdollisuudetTeksti
     */
    public String getJatkoOpintomahdollisuudetTeksti() {
        return jatkoOpintomahdollisuudetTeksti;
    }

    /**
     * @param jatkoOpintomahdollisuudetTeksti the
     * jatkoOpintomahdollisuudetTeksti to set
     */
    public void setJatkoOpintomahdollisuudetTeksti(String jatkoOpintomahdollisuudetTeksti) {
        this.jatkoOpintomahdollisuudetTeksti = jatkoOpintomahdollisuudetTeksti;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
