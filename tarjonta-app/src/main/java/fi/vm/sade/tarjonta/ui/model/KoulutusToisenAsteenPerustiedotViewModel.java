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

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fi.vm.sade.tarjonta.service.types.LueKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.KoodistoKoodiTyyppi;

/**
 *
 * @author mlyly
 */
public class KoulutusToisenAsteenPerustiedotViewModel extends KoulutusPerustiedotViewModel {

    private String koulutusohjelma;
    private String koulutuksenTyyppi;
    
    public KoulutusToisenAsteenPerustiedotViewModel(LueKoulutusVastausTyyppi koulutus) {
    	super();
    	setKoulutusohjelma(koulutus.getKoulutusKoodi() != null ? koulutus.getKoulutusohjelmaKoodi().getUri() : null);
    	setKoulutuksenAlkamisPvm(koulutus.getKoulutuksenAlkamisPaiva() != null ?  koulutus.getKoulutuksenAlkamisPaiva().toGregorianCalendar().getTime() : null);
    	setOpetuskielet(convertOpetuskielet(koulutus.getOpetuskieli()));
    	setKoulutus((koulutus.getKoulutusKoodi() != null) ? koulutus.getKoulutusKoodi().getUri() : null);
    	setKoulutuslaji(koulutus.getKoulutuslaji().isEmpty() ? null : koulutus.getKoulutuslaji().get(0).getUri());
    	setOpetusmuoto(koulutus.getOpetusmuoto() != null ? koulutus.getOpetusmuoto().getUri() : null);
    }

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
    
    private Set<String> convertOpetuskielet(List<KoodistoKoodiTyyppi> opetuskieliKoodit) {
    	Set<String> opetuskielet = new HashSet<String>();
    	for (KoodistoKoodiTyyppi curKoodi : opetuskieliKoodit) {
    		opetuskielet.add(curKoodi.getUri());
    	}
    	return opetuskielet;
    }
}
