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

import java.util.HashSet;
import java.util.Set;

import fi.vm.sade.tarjonta.service.types.LueKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
import fi.vm.sade.tarjonta.ui.enums.KoulutusFormType;
import java.util.ArrayList;
import java.util.Date;

/**
 * Model holding basic information data for Koulutus.
 *
 * @author mlyly
 * @author mholi
 * @author Jani Wilén
 */
public class KoulutusToisenAsteenPerustiedotViewModel extends KoulutusPerustiedotViewModel {
    private static final String NO_DATA_AVAILABLE = "Tietoa ei saatavilla";
    private Set<KoulutusohjelmaModel> koodistoKoulutusohjelma;
    private KoulutusFormType koulutusFormType = KoulutusFormType.SHOW_ALL; //default value

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

    /**
     * True if data was loaded from database.
     *
     * @return Boolean
     */
    public boolean isLoaded(){
        return getOid() != null;
    }
}
