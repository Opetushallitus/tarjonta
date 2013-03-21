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

import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusLisatiedotModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioPerustietoType;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetVastausTyyppi.HakukohdeTulos;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetVastausTyyppi.KoulutusTulos;

import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.KoulutusLukioKuvailevatTiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.KoulutusLukioPerustiedotViewModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Contains the data and state of the Tarjonta UI.
 *
 * @author mlyly
 */
public class TarjontaModel extends BaseUIViewModel {

    private static final long serialVersionUID = 6216606779350260527L;
    // Show label that shows last modification
    private Boolean _showIdentifier;
    private String _identifier;
    private String rootOrganisaatioOid;//OPH's root oid.
    private String parentOrganisaatioOid; //portal user's parent organisation.
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
     * Hakutulos
     */
    private List<HakukohdeTulos> _hakukohteet;
    private List<HakukohdeTulos> _selectedhakukohteet;
    private List<KoulutusTulos> _koulutukset;
    private List<KoulutusTulos> _selectedKoulutukset;
    private HakukohdeViewModel hakukohde;
    private HakukohdeLiiteViewModel selectedLiite;
    private ValintakoeViewModel selectedValintaKoe;
    private ValintakoeAikaViewModel selectedValintakoeAika;
    private Collection<OrganisaatioOidNamePair> organisaatios;
    private String selectedKoulutusOid;
    public KoulutusLukioKuvailevatTiedotViewModel koulutusLukioKuvailevatTiedot;

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
            koulutusLukioPerustiedot = new KoulutusLukioPerustiedotViewModel(DocumentStatus.NEW);
        }

        return koulutusLukioPerustiedot;
    }

    /**
     * @return the koulutusLukioKuvailevatTiedot
     */
    public KoulutusLukioKuvailevatTiedotViewModel getKoulutusLukioKuvailevatTiedot() {
        //TODO have a map!
        if (koulutusLukioKuvailevatTiedot == null) {
            setKoulutusLukioKuvailevatTiedot(new KoulutusLukioKuvailevatTiedotViewModel(DocumentStatus.NEW));
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
     * @param koulutusLukioKuvailevatTiedot the koulutusLukioKuvailevatTiedot to set
     */
    public void setKoulutusLukioKuvailevatTiedot(KoulutusLukioKuvailevatTiedotViewModel koulutusLukioKuvailevatTiedot) {
        this.koulutusLukioKuvailevatTiedot = koulutusLukioKuvailevatTiedot;
    }

    public static class OrganisaatioOidNamePair {

        private String oid;
        private String name;

        public OrganisaatioOidNamePair(String oid, String name) {
            this.oid = oid;
            this.name = name;
        }

        public String getOid() {
            return oid;
        }

        public void setOid(String oid) {
            this.oid = oid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
    /*
     * Selected organisaatio data:
     */
    private String organisaatioName;
    private String organisaatioOid;

    public String getIdentifier() {
        return _identifier;
    }

    public void setIdentifier(String _identifier) {
        this._identifier = _identifier;
    }

    public boolean isShowIdentifier() {
        return _showIdentifier;
    }

    public void setShowIdentifier(boolean _showIdentifier) {
        this._showIdentifier = _showIdentifier;
    }

    public KoulutusSearchSpesificationViewModel getSearchSpec() {
        return _searchSpec;
    }

    public List<HakukohdeTulos> getHakukohteet() {
        if (_hakukohteet == null) {
            _hakukohteet = new ArrayList<HakukohdeTulos>();
        }
        return _hakukohteet;
    }

    public void setHakukohteet(List<HakukohdeTulos> hakukohteet) {
        this._hakukohteet = hakukohteet;
    }

    public List<HakukohdeTulos> getSelectedhakukohteet() {
        if (_selectedhakukohteet == null) {
            _selectedhakukohteet = new ArrayList<HakukohdeTulos>();
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
    public List<KoulutusTulos> getSelectedKoulutukset() {
        if (_selectedKoulutukset == null) {
            _selectedKoulutukset = new ArrayList<KoulutusTulos>();
        }
        return _selectedKoulutukset;
    }

    /**
     * Sets the koulutus objects that is the koulutus list used in
     * ListKoulutusView.
     *
     * @param koulutusTulos the koulutus objects to set
     */
    public void setKoulutukset(List<KoulutusTulos> koulutusTulos) {
        _koulutukset = koulutusTulos;
    }

    /**
     * Gets the koulutus objects that is the koulutus list used in
     * ListKoulutusView.
     *
     * @return
     */
    public List<KoulutusTulos> getKoulutukset() {
        if (_koulutukset == null) {
            _koulutukset = new ArrayList<KoulutusTulos>();
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

    /**
     * Gets the name of the selected organisaatio
     *
     * @return the organisaatio name
     */
    public String getOrganisaatioName() {
        return organisaatioName;
    }

    /**
     * Sets the name of the selected organisaatio
     *
     * @param organisaatioName - the organisaatio name to set
     */
    public void setOrganisaatioName(String organisaatioName) {
        this.organisaatioName = organisaatioName;
    }

    /**
     * Gets the oid of the selected organisaatio
     *
     * @return the organisaatio oid
     */
    public String getOrganisaatioOid() {
        return organisaatioOid;
    }

    /**
     * Sets the oid of the selected organisaatio
     *
     * @param organisaatioOid - the organisaatio oid to set
     */
    public void setOrganisaatioOid(String organisaatioOid) {
        this.organisaatioOid = organisaatioOid;
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
        if (rootOrganisaatioOid == null) {
            throw new RuntimeException("Application initialization error - organisation root OID cannot be null.");
        }

        return rootOrganisaatioOid;
    }

    /**
     * Set the root OID (OPH) of a organisation tree. Null OID not allowed.
     *
     * @param rootOrganisationOid the root organisation Oid to set
     */
    public void setRootOrganisaatioOid(String rootOrganisationOid) {
        if (rootOrganisationOid == null) {
            throw new IllegalArgumentException("Organisation root OID cannot be null.");
        }

        this.rootOrganisaatioOid = rootOrganisationOid;
    }

    /**
     * Is selected organisation same as the root organisation (OPH).
     *
     * @return boolean
     */
    public boolean isSelectedRootOrganisaatio() {
        if (getOrganisaatioOid() == null) {
            return false;
        }

        return getRootOrganisaatioOid().equals(getOrganisaatioOid());
    }

    /**
     * Set portal user's parent organisation.
     *
     * @return the parentOrganisaatioOid
     */
    public String getParentOrganisaatioOid() {
        if (parentOrganisaatioOid == null) {
            throw new IllegalArgumentException("Organisation parent OID cannot be null.");
        }

        return parentOrganisaatioOid;
    }

    public void addOneOrganisaatioNameOidPair(OrganisaatioOidNamePair pair) {
        getOrganisaatios().clear();
        organisaatios.add(pair);
    }

    /**
     * Get portal user's parent organisation, at least used in navigation tree.
     *
     * @param parentOrganisaatioOid the parentOrganisaatioOid to set
     */
    public void setParentOrganisaatioOid(String parentOrganisaatioOid) {
        if (parentOrganisaatioOid == null) {
            throw new IllegalArgumentException("Organisation parent OID cannot be null.");
        }

        this.parentOrganisaatioOid = parentOrganisaatioOid;
    }

    public Collection<OrganisaatioOidNamePair> getOrganisaatios() {
        if (organisaatios == null) {
            organisaatios = new ArrayList<OrganisaatioOidNamePair>();
        }
        return organisaatios;
    }

    public void setOrganisaatios(Collection<OrganisaatioOidNamePair> organisaatios) {
        this.organisaatios = organisaatios;
    }
}
