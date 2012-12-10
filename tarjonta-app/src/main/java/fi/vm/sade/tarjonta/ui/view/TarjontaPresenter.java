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
package fi.vm.sade.tarjonta.ui.view;

import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.helper.OrganisaatioDisplayHelper;
import fi.vm.sade.tarjonta.service.types.*;
import fi.vm.sade.tarjonta.ui.model.*;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioOidListType;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioOidType;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioSearchOidType;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetVastausTyyppi.HakukohdeTulos;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetVastausTyyppi.KoulutusTulos;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitVastausTyyppi.KoulutusmoduuliTulos;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi.Teksti;
import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
import fi.vm.sade.tarjonta.ui.enums.KoulutusasteType;
import fi.vm.sade.tarjonta.ui.enums.UserNotification;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.view.hakukohde.EditHakukohdeView;
import fi.vm.sade.tarjonta.ui.view.hakukohde.HakukohdeCreationDialog;
import fi.vm.sade.tarjonta.ui.view.hakukohde.ListHakukohdeView;
import fi.vm.sade.tarjonta.ui.view.hakukohde.ShowHakukohdeViewImpl;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.PerustiedotView;
import fi.vm.sade.tarjonta.ui.view.koulutus.ShowKoulutusView;
import fi.vm.sade.vaadin.util.UiUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import fi.vm.sade.tarjonta.ui.helper.conversion.HakukohdeViewModelToDTOConverter;
import fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusSearchSpecificationViewModelToDTOConverter;
import fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusConverter;
import fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusKoodistoConverter;

import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusohjelmaModel;
import fi.vm.sade.tarjonta.ui.service.TarjontaPermissionService;
import fi.vm.sade.tarjonta.ui.view.koulutus.EditKoulutusView;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.beanutils.BeanComparator;

/**
 * This class is used to control the "tarjonta" UI.
 *
 * @author mlyly
 */
public class TarjontaPresenter {
    
    private static final Logger LOG = LoggerFactory.getLogger(TarjontaPresenter.class);
    @Autowired
    private TarjontaPermissionService tarjontaPermissionService;
    @Autowired(required = true)
    private TarjontaUIHelper uiHelper;
    // Services used
    @Autowired(required = true)
    protected OIDService oidService;
    @Autowired(required = true)
    private KoodiService koodiService;
    @Autowired(required = true)
    private TarjontaAdminService tarjontaAdminService;
    @Autowired(required = true)
    private TarjontaPublicService tarjontaPublicService;
    @Autowired(required = true)
    private OrganisaatioService organisaatioService;
    @Autowired(required = true)
    private HakukohdeViewModelToDTOConverter hakukohdeToDTOConverter;
    @Autowired(required = true)
    private KoulutusConverter koulutusToDTOConverter;
    @Autowired(required = true)
    private KoulutusSearchSpecificationViewModelToDTOConverter koulutusSearchSpecToDTOConverter;
    @Autowired(required = true)
    private KoulutusKoodistoConverter kolutusKoodistoConverter;
    // Views this presenter can control
    private TarjontaModel _model;
    private TarjontaRootView _rootView;
    private ListHakukohdeView _hakukohdeListView;
    private PerustiedotView hakuKohdePerustiedotView;
    private HakukohdeCreationDialog hakukohdeCreationDialog;
    
    public TarjontaPresenter() {
    }
    
    public void saveHakuKohde(String tila) {
        getModel().getHakukohde().setHakukohdeTila(tila);
        //getModel().getHakukohde().setHakukohdeKoodistoNimi(tryGetHakukohdeNimi(getModel().getHakukohde().getHakukohdeNimi()));
        saveHakuKohdePerustiedot();
    }
    
    public void commitHakukohdeForm(String tila) {
        hakuKohdePerustiedotView.commitForm(tila);
    }

//    private String tryGetHakukohdeNimi(String hakukohdeNimiUri) {
//        List<KoodiType> koodit = koodiService.searchKoodis(KoodiServiceSearchCriteriaBuilder.latestAcceptedKoodiByUri(hakukohdeNimiUri));
//        if (koodit != null && koodit.size() > 0) {
//            return koodit.get(0).getMetadata().get(0).getNimi();
//        } else {
//            return "";
//        }
//    }
    public void saveHakuKohdePerustiedot() {
        LOG.info("Form saved");
        getModel().getHakukohde().getLisatiedot().addAll(hakuKohdePerustiedotView.getLisatiedot());
        if (getModel().getHakukohde().getOid() == null) {
            tarjontaAdminService.lisaaHakukohde(hakukohdeToDTOConverter.convertHakukohdeViewModelToDTO(getModel().getHakukohde()));
        } else {
            tarjontaAdminService.paivitaHakukohde(hakukohdeToDTOConverter.convertHakukohdeViewModelToDTO(getModel().getHakukohde()));
        }
    }
    
    public void setTunnisteKoodi(String hakukohdeNimiUri) {
        // TODO add search from koodisto

        List<KoodiType> koodit = koodiService.searchKoodis(KoodiServiceSearchCriteriaBuilder
                .latestAcceptedKoodiByUri(hakukohdeNimiUri));
        KoodiType foundKoodi = null;
        for (KoodiType koodi : koodit) {
            foundKoodi = koodi;
        }
        if (foundKoodi != null) {
            String koodi = foundKoodi.getMetadata().get(0).getNimi();
            hakuKohdePerustiedotView.setTunnisteKoodi(koodi);
        }
    }
    
    public void initHakukohdeForm(PerustiedotView hakuKohdePerustiedotView) {
        this.hakuKohdePerustiedotView = hakuKohdePerustiedotView;
        if (getModel().getHakukohde().getHakukohdeNimi() != null) {
            setTunnisteKoodi(getModel().getHakukohde().getHakukohdeNimi());
        }
        
        ListHakuVastausTyyppi haut = tarjontaPublicService.listHaku(new ListaaHakuTyyppi());
        
        this.hakuKohdePerustiedotView.initForm(getModel().getHakukohde());
        HakuViewModel hakuView = null;
        if (getModel().getHakukohde() != null && getModel().getHakukohde().getHakuOid() != null) {
            hakuView = getModel().getHakukohde().getHakuOid();
        }
        List<HakuViewModel> foundHaut = new ArrayList<HakuViewModel>();
        for (HakuTyyppi foundHaku : haut.getResponse()) {
            HakuViewModel haku = new HakuViewModel(foundHaku);
            haku.getHakuOid();
            haku.getNimiFi();
            
            foundHaut.add(haku);
        }
        
        this.hakuKohdePerustiedotView.addItemsToHakuCombobox(foundHaut);
        
        if (hakuView != null) {
            getModel().getHakukohde().setHakuOid(hakuView);
            ListaaHakuTyyppi hakuKysely = new ListaaHakuTyyppi();
            hakuKysely.setHakuOid(getModel().getHakukohde().getHakuOid().getHakuOid());
            ListHakuVastausTyyppi hakuVastaus = tarjontaPublicService.listHaku(hakuKysely);
            HakuViewModel hakuModel = new HakuViewModel(hakuVastaus.getResponse().get(0));
            hakuModel.getHakuOid();
            hakuModel.getNimiFi();
            getModel().getHakukohde().setHakuOid(hakuModel);
        }
    }

    /**
     * Show main default view
     */
    public void showMainDefaultView() {
        LOG.info("showMainDefaultView()");
        getRootView().showMainView();
    }
    
    public void loadKoulutusToteutusDialogWithOids(List<String> komotoOids) {
        HaeKoulutuksetKyselyTyyppi kysely = new HaeKoulutuksetKyselyTyyppi();
        kysely.getKoulutusOids().addAll(komotoOids);
        HaeKoulutuksetVastausTyyppi vastaus = tarjontaPublicService.haeKoulutukset(kysely);
        List<KoulutusOidNameViewModel> koulutusModel = convertKoulutusToNameOidViewModel(vastaus.getKoulutusTulos());
        hakukohdeCreationDialog.buildLayout(koulutusModel);
        
    }
    
    private List<KoulutusOidNameViewModel> convertKoulutusToNameOidViewModel(List<KoulutusTulos> tulokset) {
        List<KoulutusOidNameViewModel> result = new ArrayList<KoulutusOidNameViewModel>();
        
        for (KoulutusTulos tulos : tulokset) {
            
            KoulutusOidNameViewModel nimiOid = new KoulutusOidNameViewModel();
            
            nimiOid.setKoulutusOid(tulos.getKoulutus().getKomotoOid());
            LOG.info("convertKoulutusToNameOidViewModel tulos size : " + tulokset.size());
            String nimi = "";
            if (tulos.getKoulutus().getNimi() != null) {
                for (Teksti teksti : tulos.getKoulutus().getNimi().getTeksti()) {
                    if (teksti.getKieliKoodi().trim().equalsIgnoreCase(I18N.getLocale().getLanguage())) {
                        nimi = teksti.getValue();
                    }
                }
            }
            nimiOid.setKoulutusNimi(nimi);
            result.add(nimiOid);
            
        }
        
        return result;
    }
    
    public void cancelHakukohdeCreationDialog() {
        getRootView().getListKoulutusView().closeHakukohdeCreationDialog();
    }

    /*
     * Show hakukohde overview view
     */

    public void showHakukohdeViewImpl(String hakukohdeOid) {
        if (hakukohdeOid != null) {
            LueHakukohdeKyselyTyyppi kysely = new LueHakukohdeKyselyTyyppi();
            kysely.setOid(hakukohdeOid);
             LueHakukohdeVastausTyyppi vastaus = tarjontaPublicService.lueHakukohde(kysely);
            if (vastaus.getHakukohde() != null) {
                getModel().setHakukohde(hakukohdeToDTOConverter.convertDTOToHakukohdeViewMode(vastaus.getHakukohde()));
                ShowHakukohdeViewImpl view = new ShowHakukohdeViewImpl(getModel().getHakukohde().getHakukohdeNimi(),null,null);
                getRootView().changeView(view);

            }
        }
    }

    /**
     * Show koulutus overview view.
     */
    public void showShowKoulutusView() {
        KoulutusToisenAsteenPerustiedotViewModel model = getModel().getKoulutusPerustiedotModel();
        String title = "";
        final KoulutusasteType koulutusaste = model.getSelectedKoulutusasteType();
        switch (koulutusaste) {
            case TOINEN_ASTE_AMMATILLINEN_KOULUTUS:
                title = model.getKoulutuskoodiModel().getNimi()
                        + ", "
                        + model.getKoulutusohjelmaModel().getNimi();
                break;
            case TOINEN_ASTE_LUKIO:
                title = model.getKoulutuskoodiModel().getNimi();
                break;
        }
        
        ShowKoulutusView view = new ShowKoulutusView(title, null);
        getRootView().changeView(view);
    }
    
    public void showShowKoulutusView(String koulutusOid) {
        LOG.info("showShowKoulutusView()");

        // If oid of koulutus is provided the koulutus is read from database
        // before opening the ShowKoulutusView
        if (koulutusOid != null) {
            try {
                LueKoulutusKyselyTyyppi koulutusKysely = new LueKoulutusKyselyTyyppi();
                koulutusKysely.setOid(koulutusOid);
                LueKoulutusVastausTyyppi tyyppi = this.tarjontaPublicService.lueKoulutus(koulutusKysely);
                KoulutusToisenAsteenPerustiedotViewModel model = koulutusToDTOConverter
                        .createKoulutusPerustiedotViewModel(tyyppi, DocumentStatus.LOADED, I18N.getLocale());
                getModel().setKoulutusPerustiedotModel(model);
                getModel().setKoulutusLisatiedotModel(koulutusToDTOConverter.createKoulutusLisatiedotViewModel(tyyppi));
            } catch (ExceptionMessage ex) {
                LOG.error("Service call failed.", ex);
            }
        } else {
            throw new RuntimeException("Application error - missing OID, cannot open ShowKoulutusView.");
        }
        
        showShowKoulutusView();
    }
    
    public void setKomotoOids(List<String> komotoOids) {
        getModel().getHakukohde().setKomotoOids(komotoOids);
    }
    
    @SuppressWarnings("empty-statement")
    public void showKoulutusPerustiedotEditView(final String koulutusOid) {
        // If oid of koulutus is provided the koulutus is read from database
        // before opening the KoulutusEditView
        if (koulutusOid != null) {
            LueKoulutusVastausTyyppi lueKoulutus = this.getKoulutusByOid(koulutusOid);
            try {
                KoulutusToisenAsteenPerustiedotViewModel koulutus;
                koulutus = koulutusToDTOConverter.createKoulutusPerustiedotViewModel(lueKoulutus, DocumentStatus.LOADED, I18N.getLocale());
                getModel().setKoulutusPerustiedotModel(koulutus);
                getModel().setKoulutusLisatiedotModel(koulutusToDTOConverter.createKoulutusLisatiedotViewModel(lueKoulutus));

                //Empty previous Koodisto data from the comboboxes.
                koulutus.getKoulutusohjelmat().clear();
                koulutus.getKoulutuskoodit().clear();

                //Add selected data to the comboboxes.
                if (koulutus.getKoulutusohjelmaModel() != null && koulutus.getKoulutusohjelmaModel().getKoodistoUri() != null) {
                    koulutus.getKoulutusohjelmat().add(koulutus.getKoulutusohjelmaModel());
                }
                koulutus.getKoulutuskoodit().add(koulutus.getKoulutuskoodiModel());
            } catch (ExceptionMessage ex) {
                LOG.error("Service call failed.", ex);
                showMainDefaultView();
            }
        } else {
            if (getModel().getOrganisaatioOid() == null) {
                throw new RuntimeException("Application error - missing organisation OID.");
            }
            
            getModel().getKoulutusPerustiedotModel().clearModel(DocumentStatus.NEW);
        }
        
        getRootView().changeView(new EditKoulutusView());
        
    }

    /**
     * Show hakukohde edit view.
     *
     * @param koulutusOids
     * @param hakukohdeOid
     */
    public void showHakukohdeEditView(List<String> koulutusOids, String hakukohdeOid) {
        LOG.info("showHakukohdeEditView()");
        for (String oid : koulutusOids) {
            LOG.info("OID : {} ", oid);
        }
        //After the data has been initialized the form is created
        EditHakukohdeView editHakukohdeView = new EditHakukohdeView();
        if (hakukohdeOid == null) {
            getModel().setHakukohde(new HakukohdeViewModel());
        }

        //If a list of koulutusOids is provided they are set in the model
        //These koulutus objects will be published in the created hakukohde
        if (koulutusOids != null) {
            setKomotoOids(koulutusOids);
            
        }
        //if a hakukohdeOid is provided the hakukohde is read from the database
        if (hakukohdeOid != null) {
            LueHakukohdeKyselyTyyppi kysely = new LueHakukohdeKyselyTyyppi();
            kysely.setOid(hakukohdeOid);
            getModel().setHakukohde(this.hakukohdeToDTOConverter.convertDTOToHakukohdeViewMode(tarjontaPublicService
                    .lueHakukohde(kysely).getHakukohde()));
            setKomotoOids(getModel().getHakukohde().getKomotoOids());
        }
        
        getRootView().changeView(editHakukohdeView);
        
    }
    
    public String resolveHakukohdeKoodistonimiFields() {
        return uiHelper.getHakukohdeHakukentta(getModel().getHakukohde().getHakuOid().getHakuOid(), I18N.getLocale(), getModel().getHakukohde().getHakukohdeNimi());
    }
    
    public void doSearch() {
        LOG.info("doSearch(): searchSpec={}", getModel().getSearchSpec());
    }
    
    public ListHakukohdeView getHakukohdeListView() {
        return _hakukohdeListView;
    }
    
    public void setHakukohdeListView(ListHakukohdeView hakukohdeListView) {
        this._hakukohdeListView = hakukohdeListView;
    }
    
    public Map<String, List<HakukohdeTulos>> getHakukohdeDataSource() {
        Map<String, List<HakukohdeTulos>> map = new HashMap<String, List<HakukohdeTulos>>();
        try {
            // Fetching komotos matching currently specified criteria (currently
            // selected organisaatio and written text in search box)
            HaeHakukohteetKyselyTyyppi kysely = this.koulutusSearchSpecToDTOConverter
                    .convertViewModelToHakukohdeDTO(getModel().getSearchSpec());
            getModel().setHakukohteet(tarjontaPublicService.haeHakukohteet(kysely).getHakukohdeTulos());
        } catch (Exception ex) {
            LOG.error("Error in finding hakukokohteet: {}", ex.getMessage());
            getModel().setHakukohteet(new ArrayList<HakukohdeTulos>());
        }
        for (HakukohdeTulos curHk : getModel().getHakukohteet()) {
            String hkKey = this.getOrganisaatioNimiByOid(curHk.getKoulutus().getTarjoaja());
            if (!map.containsKey(hkKey)) {
                LOG.info("Adding a new key to the map: " + hkKey);
                List<HakukohdeTulos> hakukohteetM = new ArrayList<HakukohdeTulos>();
                hakukohteetM.add(curHk);
                map.put(hkKey, hakukohteetM);
            } else {
                map.get(hkKey).add(curHk);
            }
        }
        TreeMap<String, List<HakukohdeTulos>> sortedMap = new TreeMap<String, List<HakukohdeTulos>>(map);
        
        return sortedMap;
    }

    /**
     * Gets the currently selected hakukohde objects.
     *
     * @return
     */
    public List<HakukohdeTulos> getSelectedhakukohteet() {
        return getModel().getSelectedhakukohteet();
    }

    /**
     * Removes the selected hakukohde objects from the database.
     */
    public void removeSelectedHakukohteet() {
        try {
            for (HakukohdeTulos curHakukohde : getModel().getSelectedhakukohteet()) {
                HakukohdeTyyppi hakukohde = new HakukohdeTyyppi();
                hakukohde.setOid(curHakukohde.getHakukohde().getOid());
                tarjontaAdminService.poistaHakukohde(hakukohde);
            }
            getModel().getSelectedhakukohteet().clear();

            // Force UI update.
            getHakukohdeListView().reload();
        } catch (Exception exp) {
            if (exp.getMessage().contains("fi.vm.sade.tarjonta.service.business.exception.HakukohdeUsedException")) {
                getHakukohdeListView().showErrorMessage(I18N.getMessage("notification.error.hakukohde.used"));
            } else {
                showNotification(UserNotification.SAVE_FAILED);
            }
        }
    }
    
    public void removeHakukohde(HakukohdeTulos curHakukohde) {
        HakukohdeTyyppi hakukohde = new HakukohdeTyyppi();
        hakukohde.setOid(curHakukohde.getHakukohde().getOid());
        try {
            tarjontaAdminService.poistaHakukohde(hakukohde);
            getHakukohdeListView().reload();
            showNotification(UserNotification.DELETE_SUCCESS);
        } catch (Exception exp) {
            if (exp.getMessage().contains("fi.vm.sade.tarjonta.service.business.exception.HakukohdeUsedException")) {
                getHakukohdeListView().showErrorMessage(I18N.getMessage("notification.error.hakukohde.used"));
            } else {
                showNotification(UserNotification.SAVE_FAILED);
            }
        }
    }

    /**
     * Gets the currently selected koulutus objects.
     *
     * @return
     */
    public List<KoulutusTulos> getSelectedKoulutukset() {
        return getModel().getSelectedKoulutukset();
    }

    /**
     * Removes the selected koulutus objects from the database.
     */
    public void removeSelectedKoulutukset() {
        for (KoulutusTulos curKoulutus : getModel().getSelectedKoulutukset()) {
            tarjontaAdminService.poistaKoulutus(curKoulutus.getKoulutus().getKoulutusmoduuliToteutus());
        }
        getModel().getSelectedKoulutukset().clear();

        // Force UI update.
        getReloadKoulutusListData();
    }

    /**
     * Saves koulutus.
     */
    public void saveKoulutus(TarjontaTila tila) throws ExceptionMessage {
        KoulutusToisenAsteenPerustiedotViewModel koulutusModel = getModel().getKoulutusPerustiedotModel();
        
        if (koulutusModel.isLoaded()) {
            //update KOMOTO
            PaivitaKoulutusTyyppi paivita = koulutusToDTOConverter.createPaivitaKoulutusTyyppi(getModel(), koulutusModel.getOid());
            paivita.setTila(tila);
            koulutusToDTOConverter.validateSaveData(paivita, koulutusModel);
            tarjontaAdminService.paivitaKoulutus(paivita);
        } else {
            //persist new KOMO and KOMOTO
            koulutusModel.setOrganisaatioOid(getModel().getOrganisaatioOid());
            koulutusModel.setOrganisaatioName(getModel().getOrganisaatioName());
            
            LisaaKoulutusTyyppi lisaa = koulutusToDTOConverter.createLisaaKoulutusTyyppi(getModel(), getModel().getOrganisaatioOid());
            lisaa.setTila(tila);
            koulutusToDTOConverter.validateSaveData(lisaa, koulutusModel);
            checkKoulutusmoduuli();
            tarjontaAdminService.lisaaKoulutus(lisaa);
            koulutusModel.setOid(lisaa.getOid());
            
        }
        koulutusModel.setDocumentStatus(DocumentStatus.SAVED);
    }

    /**
     * Get UI model.
     *
     * @return
     */
    public TarjontaModel getModel() {
        if (_model == null) {
            _model = new TarjontaModel();
        }
        return _model;
    }
    
    public void setRootView(TarjontaRootView rootView) {
        _rootView = rootView;
    }
    
    public TarjontaRootView getRootView() {
        return _rootView;
    }

    /**
     * If true (read from model, value set from application property
     * "common.showAppIdentifier") UI should show app identifier so that testers
     * know what version was deployed.
     */
    public boolean isShowIdentifier() {
        return getModel().isShowIdentifier();
    }

    /**
     * @return application identifier.
     */
    public String getIdentifier() {
        return getModel().getIdentifier();
    }
    
    public void getReloadKoulutusListData() {
        getRootView().getListKoulutusView().reload();
    }

    /**
     * Retrieves the koulutus objects for ListKoulutusView.
     *
     * @return the koulutus objects
     */
    public Map<String, List<KoulutusTulos>> getKoulutusDataSource() {
        Map<String, List<KoulutusTulos>> map = new HashMap<String, List<KoulutusTulos>>();
        try {
            // Fetching komotos matching currently specified criteria (currently
            // selected organisaatio and written text in search box)
            HaeKoulutuksetKyselyTyyppi kysely = this.koulutusSearchSpecToDTOConverter
                    .convertViewModelToKoulutusDTO(getModel().getSearchSpec());
            
            getModel().setKoulutukset(this.tarjontaPublicService.haeKoulutukset(kysely).getKoulutusTulos());
            
        } catch (Exception ex) {
            LOG.error("Error in finding koulutukset: {}", ex.getMessage());
            getModel().setKoulutukset(new ArrayList<KoulutusTulos>());
        }

        // Creating the datasource model
        for (KoulutusTulos curKoulutus : getModel().getKoulutukset()) {
            String koulutusKey = this.getOrganisaatioNimiByOid(curKoulutus.getKoulutus().getTarjoaja());
            if (!map.containsKey(koulutusKey)) {
                LOG.info("Adding a new key to the map: " + koulutusKey);
                List<KoulutusTulos> koulutuksetM = new ArrayList<KoulutusTulos>();
                koulutuksetM.add(curKoulutus);
                map.put(koulutusKey, koulutuksetM);
            } else {
                map.get(koulutusKey).add(curKoulutus);
            }
        }
        
        TreeMap<String, List<KoulutusTulos>> sortedMap = new TreeMap<String, List<KoulutusTulos>>(map);
        
        return sortedMap;
    }

    /**
     * Creating komoto search criteria according to currently selected
     * organisaatio If no organisaatio selected, criteria is empty
     *
     * @return
     */
    private HaeKoulutuksetKyselyTyyppi generateKomotoSearchCriteria() {
        HaeKoulutuksetKyselyTyyppi kysely = new HaeKoulutuksetKyselyTyyppi();
        if (getModel().getOrganisaatioOid() != null) {
            // Find all descendant organisation oids
            kysely.getTarjoajaOids().addAll(findAllChilrenOidsByParentOid(getModel().getOrganisaatioOid()));
            kysely.getTarjoajaOids().add(getModel().getOrganisaatioOid());

//            List<OrganisaatioDTO> childOrgs = this.organisaatioService.findAllChildrenWithOid(getModel().getOrganisaatioOid());
//            LOG.debug("childOrgs: " + childOrgs.size());
//            for (OrganisaatioDTO org : childOrgs) {
//                LOG.debug("Current organisaatio: " + OrganisaatioDisplayHelper.getClosest(I18N.getLocale(), org) + ", " + org.getOid());
//                kysely.getTarjoajaOids().add(org.getOid());
//            }
        }
        return kysely;
    }
    
    private List<String> findAllChilrenOidsByParentOid(String parentOid) {
        List<String> oids = new ArrayList<String>();

        // Find all descendant organisation oids
        OrganisaatioSearchOidType sot = new OrganisaatioSearchOidType();
        sot.setSearchOid(parentOid);
        OrganisaatioOidListType olt = organisaatioService.findChildrenOidsByOid(sot);
        
        for (OrganisaatioOidType childOidType : olt.getOrganisaatioOidList()) {
            oids.add(childOidType.getOrganisaatioOid());
        }
        
        return oids;
    }
    
    public String getOrganisaatioNimiByOid(String organisaatioOid) {
        String vastaus = organisaatioOid;
        try {
            vastaus = OrganisaatioDisplayHelper.getClosest(I18N.getLocale(), this.organisaatioService.findByOid(organisaatioOid));
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
        }
        return vastaus;
    }

    /**
     * Gets the oids of the selectd koulutuses.
     *
     * @return the oids
     */
    public List<String> getSelectedKoulutusOids() {
        List<String> kOids = new ArrayList<String>();
        
        if (getModel().getSelectedKoulutukset() != null || !getModel().getSelectedKoulutukset().isEmpty()) {
            for (KoulutusTulos curKoul : getModel().getSelectedKoulutukset()) {
                if (curKoul != null && curKoul.getKoulutus() != null) {
                    kOids.add(curKoul.getKoulutus().getKoulutusmoduuliToteutus());
                } 
            }
        }
        return kOids;
    }

    /**
     * Removal of a komoto object.
     *
     * @param koulutus
     */
    public void removeKoulutus(KoulutusTulos koulutus) {
        try {
            tarjontaAdminService.poistaKoulutus(koulutus.getKoulutus().getKoulutusmoduuliToteutus());
            getRootView().getListKoulutusView().reload();
            showNotification(UserNotification.DELETE_SUCCESS);
        } catch (Exception ex) {
            if (ex.getMessage().contains("fi.vm.sade.tarjonta.service.business.exception.KoulutusUsedException")) {
                showNotification(UserNotification.KOULUTUS_REMOVAL_FAILED);
            } else {
                showNotification(UserNotification.SAVE_FAILED);
            }
        }
    }

    /*
     * A simple notification helper method.
     */
    public void showNotification(final UserNotification msg) {
        LOG.info("Show user notification - type {}, value {}", msg, msg.getInfo());
        if (msg != null && getRootView() != null) {
            getRootView().showNotification(msg.getInfo(), msg.getNotifiaction());
        } else {
            LOG.error("Application error - an unknown problem with UI notification. Value : {}", msg);
        }
    }

    /**
     * Navigate to the ShowHakukohdeView for the hakukohde with oid given as
     * parameter.
     *
     * @param oid the oid given
     */
    public void showShowHakukohdeView(String oid) {
        // TODO Auto-generated method stub
    }

    /**
     * Shows the koulutus objects for a hakukohde in the ListHakukohdeView.
     *
     * @param oid
     */
    public void showKoulutuksetForHakukohde(String oid) {
        LueHakukohdeKyselyTyyppi kysely = new LueHakukohdeKyselyTyyppi();
        kysely.setOid(oid);
        HakukohdeViewModel hakukohde = this.hakukohdeToDTOConverter
                .convertDTOToHakukohdeViewMode(this.tarjontaPublicService.lueHakukohde(kysely).getHakukohde());
        this._hakukohdeListView.appendKoulutuksetToList(hakukohde);
        
    }

    /**
     * Selects the organisaatio in tarjonta, by setting the organisaatio name in
     * breadcrumb and setting the organisaatioOid and organisaatioNimi in
     * tarjonta model. Enables the create koulutus button in koulutus list view.
     *
     * @param organisaatioOid - the organisaatio oid to select
     * @param organisaatioName - the organisaatio name to select
     */
    public void selectOrganisaatio(String organisaatioOid, String organisaatioName) {
        getModel().setOrganisaatioOid(organisaatioOid);
        getModel().setOrganisaatioName(organisaatioName);
        
        getRootView().getBreadcrumbsView().setOrganisaatio(organisaatioName);

        // Descendant organisation oids to limit the search
        getModel().getSearchSpec().getOrganisaatioOids().clear();
        getModel().getSearchSpec().getOrganisaatioOids().addAll(findAllChilrenOidsByParentOid(organisaatioOid));
        getModel().getSearchSpec().getOrganisaatioOids().add(organisaatioOid);

//        List<OrganisaatioDTO> childOrgs = this.organisaatioService.findAllChildrenWithOid(organisaatioOid);
//
//        List<String> orgOids = new ArrayList<String>();
//        orgOids.add(organisaatioOid);
//        for (OrganisaatioDTO org : childOrgs) {
//
//            orgOids.add(org.getOid());
//        }
//        getModel().getSearchSpec().setOrganisaatioOids(orgOids);

        //Clearing the selected hakukohde and koulutus objects
        getModel().getSelectedhakukohteet().clear();
        getModel().getSelectedKoulutukset().clear();

        // Updating koulutuslista to show only komotos with tarjoaja matching
        // the selected org or one of its descendants

        getReloadKoulutusListData();
        this.getHakukohdeListView().reload();
        this.getRootView().getListKoulutusView().toggleCreateKoulutusB(true);
    }
    
    public void unSelectOrganisaatio() {
        getModel().setOrganisaatioOid(null);
        getModel().setOrganisaatioName(null);
        
        getRootView().getBreadcrumbsView().setOrganisaatio("-");
        getModel().setOrganisaatioOid(null);
        getModel().setOrganisaatioName(null);
        getRootView().getBreadcrumbsView().setOrganisaatio("OPH");
        
        getRootView().getOrganisaatiohakuView().clearTreeSelection();

        //Clearing the selected hakukohde and koulutus objects
        getModel().getSelectedhakukohteet().clear();
        getModel().getSelectedKoulutukset().clear();
        
        getModel().getSearchSpec().setOrganisaatioOids(new ArrayList<String>());
        getReloadKoulutusListData();
        this.getHakukohdeListView().reload();
        this.getRootView().getListKoulutusView().toggleCreateKoulutusB(false);
        this.getRootView().getListKoulutusView().toggleCreateHakukohdeB(false);
    }

    /**
     * Gets koulutus by its oid.
     *
     * @param komotoOid - the koulutus oid for which the name is returned
     * @return the koulutus
     */
    public LueKoulutusVastausTyyppi getKoulutusByOid(String komotoOid) {
        LueKoulutusKyselyTyyppi kysely = new LueKoulutusKyselyTyyppi();
        kysely.setOid(komotoOid);
        LueKoulutusVastausTyyppi vastaus = this.tarjontaPublicService.lueKoulutus(kysely);
        return vastaus;
    }

    /**
     *
     *
     */
    public void checkKoulutusmoduuli() {
        KoulutusToisenAsteenPerustiedotViewModel model = getModel().getKoulutusPerustiedotModel();
        
        HaeKoulutusmoduulitKyselyTyyppi kysely =
                KoulutusConverter.mapToHaeKoulutusmoduulitKyselyTyyppi(model);
        HaeKoulutusmoduulitVastausTyyppi vastaus = this.tarjontaPublicService.haeKoulutusmoduulit(kysely);
        
        if (vastaus.getKoulutusmoduuliTulos().isEmpty()) {
            //No KOMO, insert new KOMO
            LOG.error("Tarjonta do not have requested komo! "
                    + "tutkinto : '" + kysely.getKoulutuskoodiUri()
                    + "', koulutusohjelma : '" + kysely.getKoulutusohjelmakoodiUri() + "'");
        } else {
            //KOMO found
            model.setKoulutusmoduuliOid(vastaus.getKoulutusmoduuliTulos().get(0).getKoulutusmoduuli().getOid());
        }
    }

    /*
     * More detailed information of selected 'koulutusluokitus'.
     */
    public void loadKoulutuskoodit() {
        HaeKoulutusmoduulitVastausTyyppi haeKaikkiKoulutusmoduulit = tarjontaPublicService.haeKaikkiKoulutusmoduulit(new HaeKoulutusmoduulitKyselyTyyppi());
        List<KoulutusmoduuliTulos> koulutusmoduuliTulos = haeKaikkiKoulutusmoduulit.getKoulutusmoduuliTulos();
        
        Set<String> uris = new HashSet<String>();
        List<KoulutusmoduuliKoosteTyyppi> komos = new ArrayList<KoulutusmoduuliKoosteTyyppi>();
        
        for (KoulutusmoduuliTulos tulos : koulutusmoduuliTulos) {
            komos.add(tulos.getKoulutusmoduuli());
            KoulutusmoduuliKoosteTyyppi tyyppi = tulos.getKoulutusmoduuli();
            uris.add(tulos.getKoulutusmoduuli().getKoulutuskoodiUri());
        }
        
        LOG.debug("KOMOs found " + komos.size());
        KoulutusToisenAsteenPerustiedotViewModel model = getModel().getKoulutusPerustiedotModel();
        model.setKomos(komos);
        model.createCacheKomos(); //cache komos to map object
        model.getKoulutuskoodit().clear();
        //koodisto service search result remapped to UI model objects.
        List<KoulutuskoodiModel> listaaKoulutuskoodit = kolutusKoodistoConverter.listaaKoulutukset(uris, I18N.getLocale());
        
        Collections.sort(listaaKoulutuskoodit, new BeanComparator("nimi"));
        model.getKoulutuskoodit().addAll(listaaKoulutuskoodit);
    }
    
    public void loadKoulutusohjelmat() {
        KoulutusToisenAsteenPerustiedotViewModel model = getModel().getKoulutusPerustiedotModel();
        //Select 'koulutusohjelma' from pre-filtered koodisto data.
        if (model.getKoulutuskoodiModel() != null && model.getKoulutuskoodiModel().getKoodi() != null) {
            model.getKoulutusohjelmat().clear();
            List<KoulutusmoduuliKoosteTyyppi> tyyppis = model.getQuickKomosByKoulutuskoodiUri(model.getKoulutuskoodiModel().getKoodistoUriVersio());
            List<KoulutusohjelmaModel> listaaKoulutusohjelmat = kolutusKoodistoConverter.listaaKoulutusohjelmat(tyyppis, I18N.getLocale());
            
            Collections.sort(listaaKoulutusohjelmat, new BeanComparator("nimi"));
            model.getKoulutusohjelmat().addAll(listaaKoulutusohjelmat);
        }
    }
    
    public void loadSelectedKomoData() {
        KoulutusToisenAsteenPerustiedotViewModel model = getModel().getKoulutusPerustiedotModel();
        final KoulutuskoodiModel koulutuskoodi = model.getKoulutuskoodiModel();
        final KoulutusohjelmaModel ohjelma = model.getKoulutusohjelmaModel();
        
        if (koulutuskoodi != null && koulutuskoodi.getKoodi() != null && ohjelma != null && ohjelma.getKoodi() != null) {
            model.getKoulutusohjelmat().clear();
            KoulutusmoduuliKoosteTyyppi tyyppi = model.getQuickKomo(
                    koulutuskoodi.getKoodistoUriVersio(),
                    ohjelma.getKoodistoUriVersio());
            kolutusKoodistoConverter.listaaSisalto(koulutuskoodi, ohjelma, tyyppi, I18N.getLocale());
        }
    }
    
    public void showKoulutusPreview() {
        if (getRootView() != null) {
            getRootView().showNotification("NOT IMPLEMNTED");
        }
    }

    /**
     * @return the uiHelper
     */
    public TarjontaUIHelper getUiHelper() {
        return uiHelper;
    }

    /**
     * @param uiHelper the uiHelper to set
     */
    public void setUiHelper(TarjontaUIHelper uiHelper) {
        this.uiHelper = uiHelper;
    }

    /**
     * @return the tarjontaPermissionService
     */
    public TarjontaPermissionService getPermission() {
        LOG.debug("tarjontaPermissionService : " + tarjontaPermissionService);
        
        return tarjontaPermissionService;
    }

    /**
     * Enables or disables hakukohde button based on whether there are selected
     * koulutus objects in the list.
     */
    public void toggleCreateHakukohde() {
        this.getRootView().getListKoulutusView().toggleCreateHakukohdeB(!this._model.getSelectedKoulutukset().isEmpty());
    }
    
    public HakukohdeCreationDialog getHakukohdeCreationDialog() {
        return hakukohdeCreationDialog;
    }
    
    public void setHakukohdeCreationDialog(HakukohdeCreationDialog hakukohdeCreationDialog) {
        this.hakukohdeCreationDialog = hakukohdeCreationDialog;
    }
}
