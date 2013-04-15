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

/**
 *
 * @author Jani Wilén
 */
public class KoulutusluokitusRowDTO extends AbstractKoulutuskoodiField {

    protected String koulutusalaNimi;
    protected String koulutusalaKoodi;
    protected String opintoalaNimi;
    protected String opintoalaKoodi;

    /**
     * @return the koulutusalaNimi
     */
    public String getKoulutusalaNimi() {
        return koulutusalaNimi;
    }

    /**
     * @param koulutusalaNimi the koulutusalaNimi to set
     */
    public void setKoulutusalaNimi(String koulutusalaNimi) {
        this.koulutusalaNimi = koulutusalaNimi;
    }

    /**
     * @return the koulutusalaKoodi
     */
    public String getKoulutusalaKoodi() {
        return koulutusalaKoodi;
    }

    /**
     * @param koulutusalaKoodi the koulutusalaKoodi to set
     */
    public void setKoulutusalaKoodi(String koulutusalaKoodi) {
        this.koulutusalaKoodi = koulutusalaKoodi;
    }

    /**
     * @return the opintoalaNimi
     */
    public String getOpintoalaNimi() {
        return opintoalaNimi;
    }

    /**
     * @param opintoalaNimi the opintoalaNimi to set
     */
    public void setOpintoalaNimi(String opintoalaNimi) {
        this.opintoalaNimi = opintoalaNimi;
    }

    /**
     * @return the opintoalaKoodi
     */
    public String getOpintoalaKoodi() {
        return opintoalaKoodi;
    }

    /**
     * @param opintoalaKoodi the opintoalaKoodi to set
     */
    public void setOpintoalaKoodi(String opintoalaKoodi) {
        this.opintoalaKoodi = opintoalaKoodi;
    }
}
