/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.tarjonta.ui.model;

import com.google.common.base.Preconditions;

import fi.vm.sade.tarjonta.service.search.HakukohdePerustieto;
import fi.vm.sade.tarjonta.service.search.KoulutusPerustieto;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusLisatiedotModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusToisenAsteenPerustiedotViewModel;

import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.KorkeakouluKuvailevatTiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.KorkeakouluPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.ValitseKoulutusModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.KoulutusLukioKuvailevatTiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.KoulutusLukioPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.org.NavigationModel;
import fi.vm.sade.tarjonta.ui.model.org.TarjoajaModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains the data and state of the Tarjonta UI.
 * 
* @author mlyly
 */
public class TarjontaModel extends BaseUIViewModel {

    private static final long serialVersionUID = 6216606779350260527L;
    // Show label that shows last modification
    private String _identifier;
    private String rootOrganisaatioOid;//OPH's root oid.
    private NavigationModel navigationModel; //data instance of selected organisation
    private TarjoajaModel tarjoajaModel; //data instace of exam provider
    private KoulutusSearchSpesificationViewModel _searchSpec = new KoulutusSearchSpesificationViewModel();
    /*
     * 2-aste ammattikoulut
     */
    private KoulutusToisenAsteenPerustiedotViewModel _koulutusPerustiedotModel;
    private KoulutusLisatiedotModel _koulutusLisatiedotModel;
    /*
     * 2-aste lukiokoulutus
     */
    private KoulutusLukioPerustiedotViewModel koulutusLukioPerustiedot;
    /*
     * University
     */
    private KorkeakouluPerustiedotViewModel korkeakouluPerustiedot;
    private KorkeakouluKuvailevatTiedotViewModel korkeakouluKuvailevatTiedot;
    /*
     * Koulutuskoodi filter dialog model
     */
    private ValitseKoulutusModel valitseKoulutusModel;
    /*
     * Hakutulos
     */
    private List<HakukohdePerustieto> _selectedhakukohteet;
    private List<KoulutusPerustieto> _koulutukset;
    private List<KoulutusPerustieto> _selectedKoulutukset;
    private HakukohdeViewModel hakukohde;
    private HakukohdeLiiteViewModel selectedLiite;
    private ValintakoeViewModel selectedValintaKoe;
    private ValintakoeAikaViewModel selectedValintakoeAika;
    private String selectedKoulutusOid;
    public KoulutusLukioKuvailevatTiedotViewModel koulutusLukioKuvailevatTiedot;
    private List<KoulutusOidNameViewModel> hakukohdeTitleKoulutukses;

    public String getSelectedKoulutusOid() {
        return selectedKoulutusOid;
    }

    public void setSelectedKoulutusOid(String selectedKoulutusOid) {
        this.selectedKoulutusOid = selectedKoulutusOid;
    }

    /**
     * @return the koulutusLukioPerustiedot
     */
    public KoulutusLukioPerustiedotViewModel getKoulutusLukioPerustiedot() {
        if (koulutusLukioPerustiedot == null) {
            koulutusLukioPerustiedot = new KoulutusLukioPerustiedotViewModel();
        }

        return koulutusLukioPerustiedot;
    }

    /**
     * @return the koulutusLukioKuvailevatTiedot
     */
    public KoulutusLukioKuvailevatTiedotViewModel getKoulutusLukioKuvailevatTiedot() {
        //TODO have a map!
        if (koulutusLukioKuvailevatTiedot == null) {
            setKoulutusLukioKuvailevatTiedot(new KoulutusLukioKuvailevatTiedotViewModel());
        }

        return koulutusLukioKuvailevatTiedot;
    }

    /**
     * @param koulutusLukioPerustiedot the koulutusLukioPerustiedot to set
     */
    public void setKoulutusLukioPerustiedot(KoulutusLukioPerustiedotViewModel koulutusLukioPerustiedot) {
        this.koulutusLukioPerustiedot = koulutusLukioPerustiedot;
    }

    /**
     * @param koulutusLukioKuvailevatTiedot the koulutusLukioKuvailevatTiedot to
     * set
     */
    public void setKoulutusLukioKuvailevatTiedot(KoulutusLukioKuvailevatTiedotViewModel koulutusLukioKuvailevatTiedot) {
        this.koulutusLukioKuvailevatTiedot = koulutusLukioKuvailevatTiedot;
    }

    /**
     * @return the navigationModel
     */
    public NavigationModel getNavigationModel() {
        if (navigationModel == null) {
            navigationModel = new NavigationModel();
        }

        return navigationModel;
    }

    /**
     * @param navigaatioModel the navigaatioModel to set
     */
    public void setNavigaatioModel(NavigationModel navigationModel) {
        this.navigationModel = navigationModel;
    }

    /**
     * @return the tarjoajaModel
     */
    public TarjoajaModel getTarjoajaModel() {
        if (tarjoajaModel == null) {
            tarjoajaModel = new TarjoajaModel();
        }

        return tarjoajaModel;
    }

    /**
     * @param tarjoajaModel the tarjoajaModel to set
     */
    public void setTarjoajaModel(TarjoajaModel tarjoajaModel) {
        this.tarjoajaModel = tarjoajaModel;
    }

    public String getIdentifier() {
        return _identifier;
    }

    public void setIdentifier(String _identifier) {
        this._identifier = _identifier;
    }

    public KoulutusSearchSpesificationViewModel getSearchSpec() {
        return _searchSpec;
    }

    public List<HakukohdePerustieto> getSelectedhakukohteet() {
        if (_selectedhakukohteet == null) {
            _selectedhakukohteet = new ArrayList<HakukohdePerustieto>();
        }
        return _selectedhakukohteet;
    }

    public KoulutusToisenAsteenPerustiedotViewModel getKoulutusPerustiedotModel() {
        if (_koulutusPerustiedotModel == null) {
            _koulutusPerustiedotModel = new KoulutusToisenAsteenPerustiedotViewModel(DocumentStatus.NEW);
        }
        return _koulutusPerustiedotModel;
    }

    public void setKoulutusPerustiedotModel(KoulutusToisenAsteenPerustiedotViewModel koulutusPerustiedotModel) {
        _koulutusPerustiedotModel = koulutusPerustiedotModel;
    }

    /**
     * Gets the currently selected (in ListKoulutusView) koulutus objects.
     *     
* @return the selected koulutukset
     */
    public List<KoulutusPerustieto> getSelectedKoulutukset() {
        if (_selectedKoulutukset == null) {
            _selectedKoulutukset = new ArrayList<KoulutusPerustieto>();
        }
        return _selectedKoulutukset;
    }

    /**
     * Sets the koulutus objects that is the koulutus list used in
     * ListKoulutusView.
     *     
* @param koulutusTulos the koulutus objects to set
     */
    public void setKoulutukset(List<KoulutusPerustieto> koulutusTulos) {
        _koulutukset = koulutusTulos;
    }

    /**
     * Gets the koulutus objects that is the koulutus list used in
     * ListKoulutusView.
     *     
* @return
     */
    public List<KoulutusPerustieto> getKoulutukset() {
        if (_koulutukset == null) {
            _koulutukset = new ArrayList<KoulutusPerustieto>();
        }
        return _koulutukset;
    }

    /**
     * @return the hakukohde
     */
    public HakukohdeViewModel getHakukohde() {
        if (hakukohde == null) {
            hakukohde = new HakukohdeViewModel();
        }

        return hakukohde;
    }

    /**
     * @param hakukohde the hakukohde to set
     */
    public void setHakukohde(HakukohdeViewModel hakukohde) {
        this.hakukohde = hakukohde;
    }

    public KoulutusLisatiedotModel getKoulutusLisatiedotModel() {
        if (_koulutusLisatiedotModel == null) {
            _koulutusLisatiedotModel = new KoulutusLisatiedotModel();
        }
        return _koulutusLisatiedotModel;
    }

    public void setKoulutusLisatiedotModel(KoulutusLisatiedotModel _koulutusLisatiedotModel) {
        this._koulutusLisatiedotModel = _koulutusLisatiedotModel;
    }

    public HakukohdeLiiteViewModel getSelectedLiite() {

        return selectedLiite;
    }

    public void setSelectedLiite(HakukohdeLiiteViewModel selectedLiite) {
        this.selectedLiite = selectedLiite;
    }

    public ValintakoeViewModel getSelectedValintaKoe() {
        if (selectedValintaKoe == null) {
            selectedValintaKoe = new ValintakoeViewModel();
        }

        return selectedValintaKoe;
    }

    public void setSelectedValintaKoe(ValintakoeViewModel selectedValintaKoe) {
        this.selectedValintaKoe = selectedValintaKoe;
    }

    public ValintakoeAikaViewModel getSelectedValintakoeAika() {
        if (selectedValintakoeAika == null) {
            selectedValintakoeAika = new ValintakoeAikaViewModel();
        }
        return selectedValintakoeAika;
    }

    public void setSelectedValintakoeAika(ValintakoeAikaViewModel selectedValintakoeAika) {
        this.selectedValintakoeAika = selectedValintakoeAika;
    }

    /**
     * Get the root OID (OPH) of a organisation tree. Throws an exception, if
     * OID is not set.
     *     
* @return the root organisation Oid
     */
    public String getRootOrganisaatioOid() {
        Preconditions.checkNotNull(rootOrganisaatioOid, "Application initialization error - organisation root OID cannot be null.");

        return rootOrganisaatioOid;
    }

    /**
     * Set the root OID (OPH) of a organisation tree. Null OID not allowed.
     *     
* @param rootOrganisationOid the root organisation Oid to set
     */
    public void setRootOrganisaatioOid(String rootOrganisationOid) {
        
        //Preconditions.checkNotNull(rootOrganisationOid, "Organisation root OID cannot be null.");

        this.rootOrganisaatioOid = "1.2.246.562.10.00000000001";
    }

    /**
     * Is selected organisation same as the root organisation (OPH).
     *     
* @return boolean
     */
    public boolean isSelectedRootOrganisaatio() {
        if (getNavigationModel().getOrganisationOid() == null) {
            return false;
        }

        return getRootOrganisaatioOid().equals(getNavigationModel().getOrganisationOid());
    }

    /**
     * @return the koulutusLukioPerustiedot
     */
    public KorkeakouluPerustiedotViewModel getKorkeakouluPerustiedot() {
        if (korkeakouluPerustiedot == null) {
            korkeakouluPerustiedot = new KorkeakouluPerustiedotViewModel();
        }

        return korkeakouluPerustiedot;
    }

    /**
     * @return the koulutusLukioKuvailevatTiedot
     */
    public KorkeakouluKuvailevatTiedotViewModel getKorkeakouluKuvailevatTiedot() {
        //TODO have a map!
        if (korkeakouluKuvailevatTiedot == null) {
            korkeakouluKuvailevatTiedot = new KorkeakouluKuvailevatTiedotViewModel();
        }

        return korkeakouluKuvailevatTiedot;
    }

    /**
     * @param korkeakouluKuvailevatTiedot the korkeakouluKuvailevatTiedot to set
     */
    public void setKorkeakouluKuvailevatTiedot(KorkeakouluKuvailevatTiedotViewModel korkeakouluKuvailevatTiedot) {
        this.korkeakouluKuvailevatTiedot = korkeakouluKuvailevatTiedot;
    }

    /**
     * @param korkeakouluPerustiedot the korkeakouluPerustiedot to set
     */
    public void setKorkeakouluPerustiedot(KorkeakouluPerustiedotViewModel korkeakouluPerustiedot) {
        this.korkeakouluPerustiedot = korkeakouluPerustiedot;
    }

    public List<KoulutusOidNameViewModel> getHakukohdeTitleKoulutukses() {
        return hakukohdeTitleKoulutukses;
    }

    public void setHakukohdeTitleKoulutukses(List<KoulutusOidNameViewModel> hakukohdeTitleKoulutukses) {
        this.hakukohdeTitleKoulutukses = hakukohdeTitleKoulutukses;
    }

    /**
     * @return the valitseKoulutusModel
     */
    public ValitseKoulutusModel getValitseKoulutusModel() {
        if (valitseKoulutusModel == null) {
            valitseKoulutusModel = new ValitseKoulutusModel();
        }
        return valitseKoulutusModel;
    }

    /**
     * @param valitseKoulutusModel the valitseKoulutusModel to set
     */
    public void setValitseKoulutusModel(ValitseKoulutusModel valitseKoulutusModel) {
        this.valitseKoulutusModel = valitseKoulutusModel;
    }
}
