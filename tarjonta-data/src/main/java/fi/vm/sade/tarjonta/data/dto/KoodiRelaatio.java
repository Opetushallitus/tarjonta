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

import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import org.apache.commons.lang.StringUtils;

/**
 * @author: Tuomas Katva
 * Date: 13.2.2013
 */
public class KoodiRelaatio extends AbstractReadableRow {
    private String ylaArvoKoodisto;
    private String alaArvoKoodisto;
    private String koodiYlaArvo;
    private String koodiAlaArvo;
    private SuhteenTyyppiType suhteenTyyppi;

    public String getKoodiYlaArvo() {
        return koodiYlaArvo;
    }

    public void setKoodiYlaArvo(final String koodiYlaArvo) {
        this.koodiYlaArvo = koodiYlaArvo;
    }

    public String getKoodiAlaArvo() {
        return koodiAlaArvo;
    }

    public void setKoodiAlaArvo(final String koodiAlaArvo) {
        this.koodiAlaArvo = koodiAlaArvo;
    }

    public String getYlaArvoKoodisto() {
        return ylaArvoKoodisto;
    }

    public void setYlaArvoKoodisto(final String ylaArvoKoodisto) {
        this.ylaArvoKoodisto = ylaArvoKoodisto;
    }

    public String getAlaArvoKoodisto() {
        return alaArvoKoodisto;
    }

    public void setAlaArvoKoodisto(final String alaArvoKoodisto) {
        this.alaArvoKoodisto = alaArvoKoodisto;
    }

    public SuhteenTyyppiType getSuhteenTyyppi() {
        return suhteenTyyppi;
    }

    public void setSuhteenTyyppi(final SuhteenTyyppiType suhteenTyyppi) {
        this.suhteenTyyppi = suhteenTyyppi;
    }

    @Override
    public boolean isEmpty() {
        return StringUtils.isBlank(ylaArvoKoodisto) && StringUtils.isBlank(alaArvoKoodisto)
                && StringUtils.isBlank(koodiYlaArvo) && StringUtils.isBlank(koodiAlaArvo);
    }
}
