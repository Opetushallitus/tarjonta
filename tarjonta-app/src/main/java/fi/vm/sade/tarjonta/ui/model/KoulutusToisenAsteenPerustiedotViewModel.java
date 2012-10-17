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
package fi.vm.sade.tarjonta.ui.model;

/**
 *
 * @author mlyly
 */
public class KoulutusToisenAsteenPerustiedotViewModel extends KoulutusPerustiedotViewModel {

    private String koulutusohjelma;
    private String koulutuksenTyyppi;

    public KoulutusToisenAsteenPerustiedotViewModel() {
        super();

        // TODO demo data?
        setKoulutusala("Tekniikan ja liikenteen ala");
        setTutkinto("Autoalan perustutkinto");
        setTutkintonimike("Automaalari");
        setOpintojenLaajuusyksikko("Opintoviikot");
        setOpintojenLaajuus("120 ov");
        setOpintoala("Opintoala ei tiedossa");
        setKoulutuksenTyyppi("Ei valintaa");
    }

    public String getKoulutusohjelma() {
        return koulutusohjelma;
    }

    public void setKoulutusohjelma(String koulutusohjelma) {
        this.koulutusohjelma = koulutusohjelma;
    }

    public String getKoulutuksenTyyppi() {
        return koulutuksenTyyppi;
    }

    public void setKoulutuksenTyyppi(String koulutuksenTyyppi) {
        this.koulutuksenTyyppi = koulutuksenTyyppi;
    }
}
