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

import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;

/**
 *
 * @author Jani Wilén
 */
public class KuvausDTO extends AbstractKoulutuskoodiField {

    /*
     * For all data object
     */
    private MonikielinenTekstiTyyppi tavoiteTeksti;
    private MonikielinenTekstiTyyppi koulutuksenRakenneTeksti;
    private MonikielinenTekstiTyyppi jatkoOpintomahdollisuudetTeksti;
    
    /**
     * @return the koulutuksellisetTeksti
     */
    public MonikielinenTekstiTyyppi getTavoiteTeksti() {
        if (tavoiteTeksti == null) {
            tavoiteTeksti = new MonikielinenTekstiTyyppi();
        }

        return tavoiteTeksti;
    }

    /**
     * @param koulutuksellisetTeksti the koulutuksellisetTeksti to set
     */
    public void setTavoiteTeksti(MonikielinenTekstiTyyppi koulutuksellisetTeksti) {
        this.tavoiteTeksti = koulutuksellisetTeksti;
    }

    /**
     * @return the koulutuksenRakenneTeksti
     */
    public MonikielinenTekstiTyyppi getKoulutuksenRakenneTeksti() {
        if (koulutuksenRakenneTeksti == null) {
            koulutuksenRakenneTeksti = new MonikielinenTekstiTyyppi();
        }

        return koulutuksenRakenneTeksti;
    }

    /**
     * @param koulutuksenRakenneTeksti the koulutuksenRakenneTeksti to set
     */
    public void setKoulutuksenRakenneTeksti(MonikielinenTekstiTyyppi koulutuksenRakenneTeksti) {
        this.koulutuksenRakenneTeksti = koulutuksenRakenneTeksti;
    }

    /**
     * @return the jatkoOpintomahdollisuudetTeksti
     */
    public MonikielinenTekstiTyyppi getJatkoOpintomahdollisuudetTeksti() {
        if (jatkoOpintomahdollisuudetTeksti == null) {
            jatkoOpintomahdollisuudetTeksti = new MonikielinenTekstiTyyppi();
        }

        return jatkoOpintomahdollisuudetTeksti;
    }

    /**
     * @param jatkoOpintomahdollisuudetTeksti the
     * jatkoOpintomahdollisuudetTeksti to set
     */
    public void setJatkoOpintomahdollisuudetTeksti(MonikielinenTekstiTyyppi jatkoOpintomahdollisuudetTeksti) {
        this.jatkoOpintomahdollisuudetTeksti = jatkoOpintomahdollisuudetTeksti;
    }
}
