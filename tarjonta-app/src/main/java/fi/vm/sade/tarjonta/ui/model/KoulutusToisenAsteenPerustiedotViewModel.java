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

import com.vaadin.data.util.BeanItemContainer;
import fi.vm.sade.tarjonta.service.types.koodisto.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.koodisto.KoulutuskoodiTyyppi;
import java.util.HashSet;
import java.util.Set;

import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
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
    private Set<KoulutusasteTyyppi> koulutusasteet;
    private Set<KoulutuskoodiTyyppi> koulutuskoodit;
    private Set<KoulutusohjelmaModel> koulutusohjelmat;
    private KoulutusasteTyyppi koulutusasteTyyppi;
    private KoulutuskoodiTyyppi koulutuskoodiTyyppi;
    private String organisaatioOid; //updated when loaded

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
     * Initialize model with all default values.
     *
     * @param status of koulutus document
     */
    public void clearModel(final DocumentStatus status) {
        //OIDs
        setOrganisaatioOid(null);
        setOid(null);

        //used in control logic 
        setDocumentStatus(status);
        setUserFrienlyDocumentStatus(null);
        setKoulutusasteTyyppi(null);
        setKoulutuskoodiTyyppi(null);
        setKoulutusohjelma(null);

        //koodisto data
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
        setOrganisaatioName(null);
        setOrganisaatioOid(null);
        setKoulutuslaji(null);

        setKoulutusasteet(new HashSet<KoulutusasteTyyppi>());
        setKoulutuskoodit(new HashSet<KoulutuskoodiTyyppi>());
        setKoulutusohjelmat(new HashSet<KoulutusohjelmaModel>());
        setOpetuskielet(new HashSet<String>(1)); //one required
        setOpetusmuoto(new HashSet<String>(1));//one required
        setAvainsanat(new HashSet<String>(0));//optional

        //Table data
        setKoulutusLinkit(new ArrayList<KoulutusLinkkiViewModel>(0)); //optional
        setYhteyshenkilot(new ArrayList<KoulutusYhteyshenkiloViewModel>(0)); //optional
    }

    /**
     * True if data was loaded from database.
     *
     * @return Boolean
     */
    public boolean isLoaded() {
        return getOid() != null && getDocumentStatus().equals(DocumentStatus.LOADED);
    }

    public boolean isEdited() {
        return getOid() != null && getDocumentStatus().equals(DocumentStatus.EDITED);
    }

    /**
     * @return the koulutusasteTyyppi
     */
    public KoulutusasteTyyppi getKoulutusasteTyyppi() {
        return koulutusasteTyyppi;
    }

    /**
     * @param koulutusasteTyyppi the koulutusasteTyyppi to set
     */
    public void setKoulutusasteTyyppi(KoulutusasteTyyppi koulutusasteTyyppi) {
        this.koulutusasteTyyppi = koulutusasteTyyppi;
    }

    public String getSelectedKoulutusasteKoodi() {
        if (getKoulutusasteTyyppi() != null && getKoulutusasteTyyppi().getKoulutusasteKoodi() != null) {
            return getKoulutusasteTyyppi().getKoulutusasteKoodi();
        }
        return null;
    }

    /**
     * @return the koulutuskoodiTyyppi
     */
    public KoulutuskoodiTyyppi getKoulutuskoodiTyyppi() {
        return koulutuskoodiTyyppi;
    }

    /**
     * @param koulutuskoodiTyyppi the koulutuskoodiTyyppi to set
     */
    public void setKoulutuskoodiTyyppi(KoulutuskoodiTyyppi koulutuskoodiTyyppi) {
        this.koulutuskoodiTyyppi = koulutuskoodiTyyppi;
    }

    /**
     * @return the koulutusasteet
     */
    public Set<KoulutusasteTyyppi> getKoulutusasteet() {
        return koulutusasteet;
    }

    /**
     * @param koulutusasteet the koulutusasteet to set
     */
    public void setKoulutusasteet(Set<KoulutusasteTyyppi> koulutusasteet) {
        this.koulutusasteet = koulutusasteet;
    }

    /**
     * @return the koulutuskoodit
     */
    public Set<KoulutuskoodiTyyppi> getKoulutuskoodit() {
        return koulutuskoodit;
    }

    /**
     * @param koulutuskoodit the koulutuskoodit to set
     */
    public void setKoulutuskoodit(Set<KoulutuskoodiTyyppi> koulutuskoodit) {
        this.koulutuskoodit = koulutuskoodit;
    }

    /**
     * @return the koulutusohjelmat
     */
    public Set<KoulutusohjelmaModel> getKoulutusohjelmat() {
        return koulutusohjelmat;
    }

    /**
     * @param koulutusohjelmat the koulutusohjelmat to set
     */
    public void setKoulutusohjelmat(Set<KoulutusohjelmaModel> koulutusohjelmat) {
        this.koulutusohjelmat = koulutusohjelmat;
    }

    /**
     * @return the organisaatioOid
     */
    public String getOrganisaatioOid() {
        return organisaatioOid;
    }

    /**
     * @param organisaatioOid the organisaatioOid to set
     */
    public void setOrganisaatioOid(String organisaatioOid) {
        this.organisaatioOid = organisaatioOid;
    }
}
