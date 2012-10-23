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
import fi.vm.sade.tarjonta.service.types.tarjonta.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
import fi.vm.sade.tarjonta.ui.enums.KoulutusFormType;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author mlyly
 */
public class KoulutusToisenAsteenPerustiedotViewModel extends KoulutusPerustiedotViewModel {

    private static final String NO_DATA_AVAILABLE = "Tietoa ei saatavilla";
    private Set<KoulutusohjelmaModel> koodistoKoulutusohjelma;
    private KoulutusFormType koulutusFormType = KoulutusFormType.SHOW_ALL; //default value

    public KoulutusToisenAsteenPerustiedotViewModel(DocumentStatus status, LueKoulutusVastausTyyppi koulutus) {
        super();
        clearModel(status);

        setOid(koulutus.getOid());
        
        setKoulutusKoodi((koulutus.getKoulutusKoodi() != null) ? koulutus.getKoulutusKoodi().getUri() : null);
        final String koodiUri = koulutus.getKoulutusohjelmaKoodi() != null ? koulutus.getKoulutusohjelmaKoodi().getUri() : null;
        setKoulutusohjema(new KoulutusohjelmaModel(koodiUri, null, null));

        setKoulutuksenAlkamisPvm(koulutus.getKoulutuksenAlkamisPaiva() != null ? koulutus.getKoulutuksenAlkamisPaiva().toGregorianCalendar().getTime() : null);
        setOpetuskielet(convertOpetuskielet(koulutus.getOpetuskieli()));

        //addAvainsanat(); TODO
        addKoulutuslajit(koulutus.getKoulutuslaji());

        if (koulutus.getKesto() != null) {
            setSuunniteltuKesto(koulutus.getKesto().getArvo());
            setSuunniteltuKestoTyyppi(koulutus.getKesto().getYksikko());
        }
  
        addOpetusmuoto(koulutus.getOpetusmuoto());
    }

    public LisaaKoulutusTyyppi mapToLisaaKoulutusTyyppi(String oid) {

        this.getDocumentStatus();  //TODO: status
        this.getOrganisaatioName(); //TODO: organisaatio

        LisaaKoulutusTyyppi lisaaKoulutusTyyppi = new LisaaKoulutusTyyppi();
        lisaaKoulutusTyyppi.setOid(oid);

        //TODO: fix the test data
        lisaaKoulutusTyyppi.setKoulutusKoodi(createKoodi(this.getKoulutusKoodi()));
        KoulutusohjelmaModel ko = getKoulutusohjema();
        //URI data example : "koulutusohjelma/1603"
        lisaaKoulutusTyyppi.setKoulutusohjelmaKoodi(createKoodi(ko.getKoodiUri(), ko.getFullName()));
        lisaaKoulutusTyyppi.setKoulutuksenAlkamisPaiva(this.getKoulutuksenAlkamisPvm());
        KoulutuksenKestoTyyppi koulutuksenKestoTyyppi = new KoulutuksenKestoTyyppi();
        koulutuksenKestoTyyppi.setArvo(this.getSuunniteltuKesto());
        koulutuksenKestoTyyppi.setYksikko(this.getSuunniteltuKestoTyyppi());
        lisaaKoulutusTyyppi.setKesto(koulutuksenKestoTyyppi);

        for (String opetusmuoto : this.getOpetusmuoto()) {
            lisaaKoulutusTyyppi.getOpetusmuoto().add(createKoodi(opetusmuoto));
        }

        for (String opetuskielet : this.getOpetuskielet()) {
            lisaaKoulutusTyyppi.getOpetuskieli().add(createKoodi(opetuskielet));
        }

        return lisaaKoulutusTyyppi;
    }

    public KoulutusToisenAsteenPerustiedotViewModel(DocumentStatus status) {
        super();
        clearModel(status);
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

    private void addKoulutuslajit(final List<KoodistoKoodiTyyppi> koulutuslaji) {
        if (koulutuslaji != null && !koulutuslaji.isEmpty()) {
            for (KoodistoKoodiTyyppi type : koulutuslaji) {
                getKoulutuslaji().add(type.getUri());
            }
        }
    }

    private void addAvainsanat(final List<KoodistoKoodiTyyppi> avainsanat) {
        if (avainsanat != null && !avainsanat.isEmpty()) {
            for (KoodistoKoodiTyyppi type : avainsanat) {
                getAvainsanat().add(type.getUri());
            }
        }
    }

    private void addOpetusmuoto(final List<KoodistoKoodiTyyppi> opetusmuoto) {
        if (opetusmuoto != null && !opetusmuoto.isEmpty()) {
            for (KoodistoKoodiTyyppi type : opetusmuoto) {
                getOpetusmuoto().add(type.getUri());
            }
        }
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

    /**
     * Initialize model with all default values.
     *
     * @param status of koulutus document
     */
    private void clearModel(final DocumentStatus status) {
        setKoulutusKoodi(null);
        setKoulutusohjema(null);
        setKoulutusala(null);
        setTutkinto(null);
        setTutkintonimike(null);
        setOpintojenLaajuusyksikko(null);
        setOpintojenLaajuus(null);
        setOpintoala(null);
        setKoulutuksenAlkamisPvm(new Date());
        setSuunniteltuKesto(null);
        setSuunniteltuKestoTyyppi(null);
        setOpetusmuoto(null);
        setKoulutuksenTyyppi(null);
        setDocumentStatus(status);
        setOrganisaatioName(null);
        setOrganisaatioOid(null);
        setUserFrienlyDocumentStatus(null);

        setOpetuskielet(new HashSet<String>(1)); //one required
        setKoulutuslaji(new HashSet<String>(1));//one required
        setOpetusmuoto(new HashSet<String>(1));//one required
        setAvainsanat(new HashSet<String>(0));//optional
        setKoulutusLinkit(new ArrayList<KoulutusLinkkiViewModel>(0)); //optional
        setYhteyshenkilot(new ArrayList<KoulutusYhteyshenkiloViewModel>(0)); //optional
    }
}
