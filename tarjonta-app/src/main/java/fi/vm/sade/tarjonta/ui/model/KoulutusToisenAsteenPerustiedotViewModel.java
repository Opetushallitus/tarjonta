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

import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fi.vm.sade.tarjonta.service.types.LueKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.KoulutuksenKestoTyyppi;
import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
import fi.vm.sade.tarjonta.ui.enums.KoulutusFormType;

/**
 *
 * @author mlyly
 */
public class KoulutusToisenAsteenPerustiedotViewModel extends KoulutusPerustiedotViewModel {

    private static final String NO_DATA_AVAILABLE = "Tietoa ei saatavilla";
    private Set<KoulutusohjelmaModel> koodistoKoulutusohjelma;
    private KoulutusFormType koulutusFormType = KoulutusFormType.TOINEN_ASTE_LUKIO; //default value

    public KoulutusToisenAsteenPerustiedotViewModel(DocumentStatus status, LueKoulutusVastausTyyppi koulutus) {
        super(status);

        setKoulutusKoodi((koulutus.getKoulutusKoodi() != null) ? koulutus.getKoulutusKoodi().getUri() : null);

        String koodiUri = koulutus.getKoulutusohjelmaKoodi() != null ? koulutus.getKoulutusohjelmaKoodi().getUri() : null;
        KoulutusohjelmaModel koulutusohjelmaModel = new KoulutusohjelmaModel(koodiUri, null, null);
        setKoulutusohjema(koulutusohjelmaModel);

        setKoulutuksenAlkamisPvm(koulutus.getKoulutuksenAlkamisPaiva() != null ? koulutus.getKoulutuksenAlkamisPaiva().toGregorianCalendar().getTime() : null);
        setOpetuskielet(convertOpetuskielet(koulutus.getOpetuskieli()));
        setKoulutuslaji(koulutus.getKoulutuslaji().isEmpty() ? null : koulutus.getKoulutuslaji().get(0).getUri());
        setOpetusmuoto(koulutus.getOpetusmuoto() != null ? koulutus.getOpetusmuoto().getUri() : null);
    }

    public LisaaKoulutusTyyppi mapToLisaaKoulutusTyyppi(String oid) {
       
        this.getDocumentStatus();  //TODO: status
        this.getOrganisaatioName(); //TODO: organisaatio
        
        LisaaKoulutusTyyppi lisaaKoulutusTyyppi = new LisaaKoulutusTyyppi();
        lisaaKoulutusTyyppi.setOid(oid);

        lisaaKoulutusTyyppi.setKoulutusKoodi(createKoodi(this.getKoulutusKoodi()));
        KoulutusohjelmaModel ko = getKoulutusohjema();
        //URI data example : "koulutusohjelma/1603"
        lisaaKoulutusTyyppi.setKoulutusohjelmaKoodi(createKoodi(ko.getKoodiUri(), ko.getFullName()));
        lisaaKoulutusTyyppi.setKoulutuksenAlkamisPaiva(this.getKoulutuksenAlkamisPvm());
        KoulutuksenKestoTyyppi koulutuksenKestoTyyppi = new KoulutuksenKestoTyyppi();
        koulutuksenKestoTyyppi.setArvo(this.getSuunniteltuKesto());
        koulutuksenKestoTyyppi.setYksikko(this.getSuunniteltuKestoTyyppi());
        lisaaKoulutusTyyppi.setKesto(koulutuksenKestoTyyppi);
        lisaaKoulutusTyyppi.setOpetusmuoto(createKoodi(this.getOpetusmuoto()));

        for (String koodi : this.getOpetuskielet()) {
            lisaaKoulutusTyyppi.getOpetuskieli().add(createKoodi(koodi));
        }

        return lisaaKoulutusTyyppi;
    }

    public KoulutusToisenAsteenPerustiedotViewModel() {
        super();
        setKoulutusala(NO_DATA_AVAILABLE); //Tekniikan ja liikenteen ala
        setTutkinto(NO_DATA_AVAILABLE); //Autoalan perustutkinto
        setTutkintonimike(NO_DATA_AVAILABLE); //Automaalari
        setOpintojenLaajuusyksikko(NO_DATA_AVAILABLE); //Opintoviikot
        setOpintojenLaajuus(NO_DATA_AVAILABLE); //120 ov
        setOpintoala(NO_DATA_AVAILABLE); //Opintoala ei tiedossa
        setKoulutuksenTyyppi(NO_DATA_AVAILABLE); //Ei valintaa
    }

    private Set<String> convertOpetuskielet(List<KoodistoKoodiTyyppi> opetuskieliKoodit) {
        Set<String> opetuskielet = new HashSet<String>();
        for (KoodistoKoodiTyyppi curKoodi : opetuskieliKoodit) {
            opetuskielet.add(curKoodi.getUri());
        }
        return opetuskielet;
    }

    /**
     * @return the koodistoKoulutusohjelma
     */
    public Set<KoulutusohjelmaModel> getKoodistoKoulutusohjelma() {
        return koodistoKoulutusohjelma;
    }

    /**
     * @param koodistoKoulutusohjelma the koodistoKoulutusohjelma to set
     */
    public void setKoodistoKoulutusohjelma(Set<KoulutusohjelmaModel> koodistoKoulutusohjelma) {
        this.koodistoKoulutusohjelma = koodistoKoulutusohjelma;
    }

    /**
     * @return the koulutusFormType
     */
    public KoulutusFormType getKoulutusFormType() {
        return koulutusFormType;
    }

    /**
     * @param koulutusFormType the koulutusFormType to set
     */
    public void setKoulutusFormType(KoulutusFormType koulutusFormType) {
        this.koulutusFormType = koulutusFormType;
    }

    /**
     * Helper method that wraps uri string into KoodistoKoodiTyypi. No other
     * attribute populated.
     *
     * @param uri
     * @return
     */
    private static KoodistoKoodiTyyppi createKoodi(String uri) {
        final KoodistoKoodiTyyppi koodi = new KoodistoKoodiTyyppi();
        koodi.setUri(uri);
        return koodi;
    }

    private static KoodistoKoodiTyyppi createKoodi(String uri, String name) {
        final KoodistoKoodiTyyppi koodi = new KoodistoKoodiTyyppi();
        koodi.setUri(uri);
        koodi.setArvo(name);
        return koodi;
    }
}
