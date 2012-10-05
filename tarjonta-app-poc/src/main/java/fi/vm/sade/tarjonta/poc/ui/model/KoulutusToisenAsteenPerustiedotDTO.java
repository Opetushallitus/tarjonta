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
package fi.vm.sade.tarjonta.poc.ui.model;

import java.util.List;

/**
 *
 * @author mlyly
 */
public class KoulutusToisenAsteenPerustiedotDTO extends KoulutusPerustiedotDTO {
    
    private String koulutusohjelma;
    private String koulutuksenTyyppi;
    private List<AdditionalInfoDTO> info;
    
    public KoulutusToisenAsteenPerustiedotDTO() {
        setKoulutusala("Tekniikan ja liikenteen ala");
        setTutkinto("Autoalan perustutkinto");
        setTutkintonimike("Automaalari");
        setOpintojenlaajuusyksikko("Opintoviikot");
        setOpintojenlaajuus("120 ov");
        setOpintoala("Opintoala ei tiedossa");
        setKoulutuksenTyyppi("Ei valintaa");
    }

    /**
     * @return the koulutusohjelma
     */
    public String getKoulutusohjelma() {
        return koulutusohjelma;
    }

    /**
     * @param koulutusohjelma the koulutusohjelma to set
     */
    public void setKoulutusohjelma(String koulutusohjelma) {
        this.koulutusohjelma = koulutusohjelma;
    }

    /**
     * @return the koulutuksenTyyppi
     */
    public String getKoulutuksenTyyppi() {
        return koulutuksenTyyppi;
    }

    /**
     * @param koulutuksenTyyppi the koulutuksenTyyppi to set
     */
    public void setKoulutuksenTyyppi(String koulutuksenTyyppi) {
        this.koulutuksenTyyppi = koulutuksenTyyppi;
    }

    /**
     * @return the info
     */
    public List<AdditionalInfoDTO> getInfo() {
        return info;
    }

    /**
     * @param info the info to set
     */
    public void setInfo(List<AdditionalInfoDTO> info) {
        this.info = info;
    }
}
