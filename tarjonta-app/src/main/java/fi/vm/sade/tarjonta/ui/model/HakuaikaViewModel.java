/*
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

package fi.vm.sade.tarjonta.ui.model;

import java.text.DateFormat;
import java.util.Date;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.service.types.SisaisetHakuAjat;

/**
 *
 * @author Tuomas Katva
 */
public class HakuaikaViewModel {

	private final SisaisetHakuAjat hakuaikaDto;

    public HakuaikaViewModel(SisaisetHakuAjat hakuaikaDto) {
        this.hakuaikaDto = hakuaikaDto;
    }

    public HakuaikaViewModel() {
    	this(new SisaisetHakuAjat());
    }
    
    @Override
    public String toString() {
    	final DateFormat fmt = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, I18N.getLocale());
    	return getHakuajanKuvaus()+" "+fmt.format(getAlkamisPvm())+" - "+fmt.format(getPaattymisPvm());
    }


    /**
     * @return the alkamisPvm
     */
    public Date getAlkamisPvm() {
        return hakuaikaDto.getSisaisenHaunAlkamisPvm();
    }

    /**
     * @param alkamisPvm the alkamisPvm to set
     */
    public void setAlkamisPvm(Date alkamisPvm) {
        hakuaikaDto.setSisaisenHaunAlkamisPvm(alkamisPvm);
    }

    /**
     * @return the paattymisPvm
     */
    public Date getPaattymisPvm() {
        return hakuaikaDto.getSisaisenHaunPaattymisPvm();
    }


    /**
     * @return the hakuajanKuvaus
     */
    public String getHakuajanKuvaus() {
        return hakuaikaDto.getHakuajanKuvaus();
    }

    /**
     * @param hakuajanKuvaus the hakuajanKuvaus to set
     */
    public void setHakuajanKuvaus(String hakuajanKuvaus) {
        hakuaikaDto.setHakuajanKuvaus(hakuajanKuvaus);
    }

    /**
     * @param paattymisPvm the paattymisPvm to set
     */
    public void setPaattymisPvm(Date paattymisPvm) {
        hakuaikaDto.setSisaisenHaunPaattymisPvm(paattymisPvm);
    }


    /**
     * @return the hakuaikaDto
     */
    public SisaisetHakuAjat getHakuaikaDto() {
        return hakuaikaDto;
    }


}
