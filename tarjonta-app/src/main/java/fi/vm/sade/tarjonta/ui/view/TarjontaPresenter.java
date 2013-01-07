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


import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.helper.OrganisaatioDisplayHelper;
import fi.vm.sade.tarjonta.service.types.*;
import fi.vm.sade.tarjonta.ui.model.*;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.koodisto.widget.WidgetFactory;
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
import fi.vm.sade.tarjonta.ui.view.hakukohde.CreationDialog;
import fi.vm.sade.tarjonta.ui.view.hakukohde.EditHakukohdeView;
import fi.vm.sade.tarjonta.ui.view.hakukohde.ListHakukohdeView;
import fi.vm.sade.tarjonta.ui.view.hakukohde.ShowHakukohdeViewImpl;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.PerustiedotView;
import fi.vm.sade.tarjonta.ui.view.koulutus.ShowKoulutusView;

import java.util.*;

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
    private ShowHakukohdeViewImpl hakukohdeView;
    private ShowKoulutusView showKoulutusView;
    private SearchResultsView searchResultsView;
    private I18NHelper i18n = new I18NHelper(this);
    @Autowired(required = true)
    private WidgetFactory koodistoWidget;

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

    public void saveHakuKohdePerustiedot() {
        LOG.info("Form saved");
        //checkHakuLiitetoimitusPvm();
        getModel().getHakukohde().getLisatiedot().addAll(hakuKohdePerustiedotView.getLisatiedot());
        if (getModel().getHakukohde().getOid() == null) {
            tarjontaAdminService.lisaaHakukohde(hakukohdeToDTOConverter.convertHakukohdeViewModelToDTO(getModel().getHakukohde()));
        } else {
            tarjontaAdminService.paivitaHakukohde(hakukohdeToDTOConverter.convertHakukohdeViewModelToDTO(getModel().getHakukohde()));
        }
    }

    private void checkHakuLiitetoimitusPvm() {
        if (getModel().getHakukohde().isKaytaHaunPaattymisenAikaa()) {
            if(getModel().getHakukohde().getHakuOid() != null && getModel().getHakukohde().getHakuOid().getPaattymisPvm() != null) {
                getModel().getHakukohde().setLiitteidenToimitusPvm(getModel().getHakukohde().getHakuOid().getPaattymisPvm());
            }
        }
        if(!getModel().getHakukohde().isSahkoinenToimitusSallittu()) {
            getModel().getHakukohde().setLiitteidenSahkoinenToimitusOsoite(null);
        }

    }

    public void setTunnisteKoodi(String hakukohdeNimiUri) {
        // TODO add search from koodisto
        String koodiUri = TarjontaUIHelper.splitKoodiURI(hakukohdeNimiUri)[0];
        List<KoodiType> koodit = koodiService.searchKoodis(KoodiServiceSearchCriteriaBuilder
                .latestAcceptedKoodiByUri(koodiUri));

        KoodiType foundKoodi = null;
        for (KoodiType koodi : koodit) {
            foundKoodi = koodi;
        }
        if (foundKoodi != null) {
            String koodi = foundKoodi.getKoodiArvo();

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
            this.hakuKohdePerustiedotView.setSelectedHaku(hakuView);

        }
        if (getModel().getHakukohde() != null && getModel().getHakukohde().getHakukohdeNimi() != null) {
            setTunnisteKoodi(getModel().getHakukohde().getHakukohdeNimi());
        }
    }

    /**
     * Show main default view
     */
    public void showMainDefaultView() {
        LOG.info("showMainDefaultView()");
        getRootView().showMainView();
    }

    public void reloadAndShowMainDefaultView() {
        this.getHakukohdeListView().reload();
        getReloadKoulutusListData();
        getRootView().showMainView();
    }

    //Tuomas Katva : two following methods break the presenter pattern consider moving everything except service call to view
  public CreationDialog<KoulutusOidNameViewModel> createHakukohdeCreationDialogWithKomotoOids(List<String> komotoOids) {
        HaeKoulutuksetKyselyTyyppi kysely = new HaeKoulutuksetKyselyTyyppi();
        kysely.getKoulutusOids().addAll(komotoOids);
        HaeKoulutuksetVastausTyyppi vastaus = tarjontaPublicService.haeKoulutukset(kysely);
        List<KoulutusOidNameViewModel> koulutusModel = convertKoulutusToNameOidViewModel(vastaus.getKoulutusTulos());
        CreationDialog<KoulutusOidNameViewModel> dialog = new CreationDialog<KoulutusOidNameViewModel>(koulutusModel,KoulutusOidNameViewModel.class,"HakukohdeCreationDialog.title","HakukohdeCreationDialog.valitutKoulutuksetOptionGroup");
        return dialog;
    }
    
    public CreationDialog<KoulutusOidNameViewModel> createHakukohdeCreationDialogWithSelectedTarjoaja() {
        HaeKoulutuksetKyselyTyyppi kysely = new HaeKoulutuksetKyselyTyyppi();
        kysely.getTarjoajaOids().add(getModel().getOrganisaatioOid());
        HaeKoulutuksetVastausTyyppi vastaus = tarjontaPublicService.haeKoulutukset(kysely);
        List<KoulutusOidNameViewModel> filtedredKoulutukses = removeSelectedKoulutukses(convertKoulutusToNameOidViewModel(vastaus.getKoulutusTulos()));
        CreationDialog<KoulutusOidNameViewModel> dialog = new CreationDialog<KoulutusOidNameViewModel>(filtedredKoulutukses,KoulutusOidNameViewModel.class,"ShowHakukohdeViewImpl.liitaUusiKoulutusDialogSecondaryTitle","HakukohdeCreationDialog.valitutKoulutuksetOptionGroup");
        return dialog;

    }

    private List<KoulutusOidNameViewModel> removeSelectedKoulutukses(List<KoulutusOidNameViewModel> koulutukses) {
        List<KoulutusOidNameViewModel> filteredKoulutukses = new ArrayList<KoulutusOidNameViewModel>();
        for( KoulutusOidNameViewModel koulutus:koulutukses) {
            boolean koulutusFound = false;
            for (String oid:getModel().getHakukohde().getKomotoOids()) {
                 if (koulutus.getKoulutusOid().trim().equalsIgnoreCase(oid)) {
                     koulutusFound = true;
                 }
            }
            if (!koulutusFound) {
                filteredKoulutukses.add(koulutus);
            }
        }
        return filteredKoulutukses;
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

    public void removeHakukohdeFromKoulutus(String hakukohdeOid) {

        LisaaKoulutusHakukohteelleTyyppi req = new LisaaKoulutusHakukohteelleTyyppi();
        req.setLisaa(false);
        req.setHakukohdeOid(hakukohdeOid);
        req.getKoulutusOids().add(getModel().getKoulutusPerustiedotModel().getOid());
        tarjontaAdminService.lisaaTaiPoistaKoulutuksiaHakukohteelle(req);
        showShowKoulutusView(getModel().getKoulutusPerustiedotModel().getOid());
    }

    public void removeKoulutusFromHakukohde(KoulutusOidNameViewModel koulutus) {
        int hakukohdeKoulutusCount = getModel().getHakukohde().getKoulukses().size();
        List<String> poistettavatKoulutukses = new ArrayList<String>();
        poistettavatKoulutukses.add(koulutus.getKoulutusOid());
        LisaaKoulutusHakukohteelleTyyppi req = new LisaaKoulutusHakukohteelleTyyppi();
        req.setHakukohdeOid(getModel().getHakukohde().getOid());
        req.getKoulutusOids().addAll(poistettavatKoulutukses);
        req.setLisaa(false);
        tarjontaAdminService.lisaaTaiPoistaKoulutuksiaHakukohteelle(req);
        //If removing last koulutus from hakukohde then hakukohde is not valid
        //anymore, show main view instead
        if (hakukohdeKoulutusCount > 1) {
            showHakukohdeViewImpl(getModel().getHakukohde().getOid());
        } else {
            showMainDefaultView();
        }
    }

    public void addKoulutuksesToHakukohde(Collection<KoulutusOidNameViewModel> koulutukses) {

        List<String> koulutusOids = new ArrayList<String>();
        for (KoulutusOidNameViewModel koulutus:koulutukses) {
            koulutusOids.add(koulutus.getKoulutusOid());
        }
        koulutusOids.addAll(getModel().getHakukohde().getKomotoOids());
        LisaaKoulutusHakukohteelleTyyppi req = new LisaaKoulutusHakukohteelleTyyppi();
        req.setHakukohdeOid(getModel().getHakukohde().getOid());
        req.getKoulutusOids().addAll(koulutusOids);
        req.setLisaa(true);
        tarjontaAdminService.lisaaTaiPoistaKoulutuksiaHakukohteelle(req);
        showHakukohdeViewImpl(getModel().getHakukohde().getOid());
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
                getModel().getHakukohde().setKoulukses(getHakukohdeKoulutukses(getModel().getHakukohde()));
                hakukohdeView = new ShowHakukohdeViewImpl(getModel().getHakukohde().getHakukohdeKoodistoNimi(), null, null);

                getRootView().changeView(hakukohdeView);

            }
        }
    }

    private List<KoulutusOidNameViewModel> getHakukohdeKoulutukses(HakukohdeViewModel hakukohdeViewModel) {
        List<KoulutusOidNameViewModel> koulutukses = new ArrayList<KoulutusOidNameViewModel>();

        LueHakukohdeKoulutuksineenKyselyTyyppi kysely = new LueHakukohdeKoulutuksineenKyselyTyyppi();
        kysely.setHakukohdeOid(hakukohdeViewModel.getOid());
        LueHakukohdeKoulutuksineenVastausTyyppi vastaus = tarjontaPublicService.lueHakukohdeKoulutuksineen(kysely);
        if (vastaus.getHakukohde() != null && vastaus.getHakukohde().getHakukohdeKoulutukses() != null) {

            List<KoulutusKoosteTyyppi> koulutusKoostes = vastaus.getHakukohde().getHakukohdeKoulutukses();
            for (KoulutusKoosteTyyppi koulutusKooste : koulutusKoostes) {
                KoulutusOidNameViewModel koulutus = new KoulutusOidNameViewModel();
                koulutus.setKoulutusNimi(buildKoulutusCaption(koulutusKooste));
                koulutus.setKoulutusOid(koulutusKooste.getKomotoOid());
                koulutukses.add(koulutus);
            }
        }

        return koulutukses;
    }

    private String buildKoulutusCaption(KoulutusKoosteTyyppi curKoulutus) {
        String caption = getKoulutusNimi(curKoulutus);
        caption += ", " + curKoulutus.getTila();
        return caption;
    }

    private String getKoulutusNimi(KoulutusKoosteTyyppi curKoulutus) {
        String nimi = getKoodiNimi(curKoulutus.getKoulutuskoodi());
        if (curKoulutus.getKoulutusohjelmakoodi() != null) {
            nimi += ", " + getKoodiNimi(curKoulutus.getKoulutusohjelmakoodi());
        }
        nimi += ", " + curKoulutus.getAjankohta();
        return nimi;
    }

    private String getTilaStr(String tilaUri) {
        String[] parts = tilaUri.split("\\/");
        return i18n.getMessage(parts[parts.length - 1]);
    }

    private String getKoodiNimi(String hakukohdeUri) {
        String nimi = this.getUiHelper().getKoodiNimi(hakukohdeUri, I18N.getLocale());
        if ("".equals(nimi)) {
            nimi = hakukohdeUri;
        }
        return nimi;
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

        showKoulutusView = new ShowKoulutusView(title, null);
        getRootView().changeView(showKoulutusView);
    }

    public void showShowKoulutusView(String koulutusOid) {
        LOG.info("showShowKoulutusView()");

        // If oid of koulutus is provided the koulutus is read from database
        // before opening the ShowKoulutusView
        if (koulutusOid != null) {

              readKoulutusToModel(koulutusOid);

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
            readKoulutusToModel(koulutusOid);
        } else {
            if (getModel().getOrganisaatioOid() == null) {
                throw new RuntimeException("Application error - missing organisation OID.");
            }

            getModel().getKoulutusPerustiedotModel().clearModel(DocumentStatus.NEW);
        }

        getRootView().changeView(new EditKoulutusView());

    }

    private void readKoulutusToModel(final String koulutusOid) {
        LueKoulutusVastausTyyppi lueKoulutus = this.getKoulutusByOid(koulutusOid);
        try {
            KoulutusToisenAsteenPerustiedotViewModel koulutus;
            koulutus = koulutusToDTOConverter.createKoulutusPerustiedotViewModel(lueKoulutus, DocumentStatus.LOADED, I18N.getLocale());
            getModel().setKoulutusPerustiedotModel(koulutus);
            getModel().setKoulutusLisatiedotModel(koulutusToDTOConverter.createKoulutusLisatiedotViewModel(lueKoulutus));

            //Empty previous Koodisto data from the comboboxes.
            koulutus.getKoulutusohjelmat().clear();
            koulutus.getKoulutuskoodit().clear();
            if (lueKoulutus.getHakukohteet() != null) {
               koulutus.getKoulutuksenHakukohteet().clear();
               for (HakukohdeKoosteTyyppi hakukohdeKoosteTyyppi: lueKoulutus.getHakukohteet()) {
                   SimpleHakukohdeViewModel hakukohdeViewModel = new SimpleHakukohdeViewModel();
                   hakukohdeViewModel.setHakukohdeNimi(hakukohdeKoosteTyyppi.getKoodistoNimi());
                   hakukohdeViewModel.setHakukohdeNimiKoodi(hakukohdeKoosteTyyppi.getNimi());
                   hakukohdeViewModel.setHakukohdeOid(hakukohdeKoosteTyyppi.getOid());
                   hakukohdeViewModel.setHakukohdeTila(hakukohdeKoosteTyyppi.getTila().value());
                   koulutus.getKoulutuksenHakukohteet().add(hakukohdeViewModel);
               }
            }

            //Add selected data to the comboboxes.
            if (koulutus.getKoulutusohjelmaModel() != null && koulutus.getKoulutusohjelmaModel().getKoodistoUri() != null) {
                koulutus.getKoulutusohjelmat().add(koulutus.getKoulutusohjelmaModel());
            }
            koulutus.getKoulutuskoodit().add(koulutus.getKoulutuskoodiModel());
        } catch (ExceptionMessage ex) {
            LOG.error("Service call failed.", ex);
            showMainDefaultView();
        }
    }

    /**
     * Show hakukohde edit view.
     *
     * @param koulutusOids
     * @param hakukohdeOid
     */
    public void showHakukohdeEditView(List<String> koulutusOids, String hakukohdeOid) {
        LOG.info("showHakukohdeEditView()");
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
        this.searchResultsView.setResultSizeForHakukohdeTab(getModel().getHakukohteet().size());
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

    public void loadHakukohdeHakuPvm() {
        ListaaHakuTyyppi haku = new ListaaHakuTyyppi();
        haku.setHakuOid(getModel().getHakukohde().getHakuOid().getHakuOid());
        ListHakuVastausTyyppi vastaus = tarjontaPublicService.listHaku(haku);
        if (vastaus != null && vastaus.getResponse() != null) {
            HakuTyyppi hakuTyyppi = vastaus.getResponse().get(0);
            SisaisetHakuAjat hakuaika = hakuTyyppi.getSisaisetHakuajat().get(0);
            getModel().getHakukohde().getHakuOid().setAlkamisPvm(hakuaika.getSisaisenHaunAlkamisPvm());
            getModel().getHakukohde().getHakuOid().setPaattymisPvm(hakuaika.getSisaisenHaunPaattymisPvm());
        }
    }

    public void removeSelectedHakukohde() {
        getModel().getSelectedhakukohteet().clear();
        HakukohdeTulos tmp = new HakukohdeTulos();
        HakukohdeKoosteTyyppi wtf = new HakukohdeKoosteTyyppi();
        wtf.setOid(getModel().getHakukohde().getOid());
        tmp.setHakukohde(wtf);
        getModel().getSelectedhakukohteet().add(tmp);
        removeSelectedHakukohteet();
        getRootView().showMainView();

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

    public void showRemoveKoulutusFromHakukohdeDialog(KoulutusOidNameViewModel koulutusOidNameViewModel) {

        hakukohdeView.showKoulutusRemovalDialog(koulutusOidNameViewModel);
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

        this.searchResultsView.setResultSizeForKoulutusTab(getModel().getKoulutukset().size());
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

    public void showRemoveHakukohdeFromKoulutusDialog(String hakukohdeOid, String hakukohdeNimi) {
        showKoulutusView.showHakukohdeRemovalDialog(hakukohdeOid,hakukohdeNimi);
    }

    /**
     * Removal of a komoto object.
     *
     * @param koulutus
     */
    public boolean removeKoulutus(KoulutusTulos koulutus) {
        boolean removeSuccess = false;
        try {
            tarjontaAdminService.poistaKoulutus(koulutus.getKoulutus().getKoulutusmoduuliToteutus());
            getRootView().getListKoulutusView().reload();
            showNotification(UserNotification.DELETE_SUCCESS);
            removeSuccess = true;
        } catch (Exception ex) {
            if (ex.getMessage().contains("fi.vm.sade.tarjonta.service.business.exception.KoulutusUsedException")) {
                showNotification(UserNotification.KOULUTUS_REMOVAL_FAILED);
            } else {
                showNotification(UserNotification.SAVE_FAILED);
            }
        }
        return removeSuccess;
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

    public void setSearchResultsView(SearchResultsView searchResultsView) {
        this.searchResultsView = searchResultsView;
    }

    /**
     * @return the koodistoWidget
     */
    public WidgetFactory getKoodistoWidget() {
        return koodistoWidget;
    }

    /**
     * @param koodistoWidget the koodistoWidget to set
     */
    public void setKoodistoWidget(WidgetFactory koodistoWidget) {
        this.koodistoWidget = koodistoWidget;
    }
}
