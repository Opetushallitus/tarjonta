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
package fi.vm.sade.tarjonta.ui.presenter;

import fi.vm.sade.authentication.service.UserService;
import fi.vm.sade.authentication.service.types.HenkiloPagingObjectType;
import fi.vm.sade.authentication.service.types.HenkiloSearchObjectType;
import fi.vm.sade.authentication.service.types.dto.HenkiloType;
import fi.vm.sade.authentication.service.types.dto.SearchConnectiveType;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.*;
import fi.vm.sade.organisaatio.helper.OrganisaatioDisplayHelper;
import fi.vm.sade.tarjonta.service.types.*;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.ui.helper.conversion.*;
import fi.vm.sade.tarjonta.ui.model.*;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetVastausTyyppi.HakukohdeTulos;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetVastausTyyppi.KoulutusTulos;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi.Teksti;
import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
import fi.vm.sade.tarjonta.ui.enums.KoulutusActiveTab;
import fi.vm.sade.tarjonta.ui.enums.KoulutusasteType;
import fi.vm.sade.tarjonta.ui.enums.SaveButtonState;
import fi.vm.sade.tarjonta.ui.enums.UserNotification;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.view.hakukohde.CreationDialog;
import fi.vm.sade.tarjonta.ui.view.hakukohde.EditHakukohdeView;
import fi.vm.sade.tarjonta.ui.view.hakukohde.ListHakukohdeView;
import fi.vm.sade.tarjonta.ui.view.hakukohde.ShowHakukohdeViewImpl;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.PerustiedotView;
import fi.vm.sade.tarjonta.ui.view.koulutus.EditKoulutusLisatiedotToinenAsteView;
import fi.vm.sade.tarjonta.ui.view.koulutus.ShowKoulutusView;

import java.text.SimpleDateFormat;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusohjelmaModel;
import fi.vm.sade.tarjonta.ui.service.PublishingService;
import fi.vm.sade.tarjonta.ui.service.TarjontaPermissionServiceImpl;
import fi.vm.sade.tarjonta.ui.service.UserContext;
import fi.vm.sade.tarjonta.ui.view.SearchResultsView;
import fi.vm.sade.tarjonta.ui.view.TarjontaRootView;
import fi.vm.sade.tarjonta.ui.view.koulutus.EditKoulutusView;

import org.apache.commons.beanutils.BeanComparator;

/**
 * This class is used to control the "tarjonta" UI.
 *
 * @author mlyly
 */
public class TarjontaPresenter implements CommonPresenter<TarjontaModel> {

    private static final Logger LOG = LoggerFactory.getLogger(TarjontaPresenter.class);
    private static final String LIITE_DATE_PATTERNS = "dd.MM.yyyy hh:mm";
    private static final String NAME_OPH = "OPH";
    @Autowired(required = true)
    private UserService userService;
    @Autowired(required = true)
    private TarjontaPermissionServiceImpl tarjontaPermissionService; //initialized in spring config
    @Autowired(required = true)
    private UserContext userContext;
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
    @Autowired(required = true)
    private transient TarjontaUIHelper uiHelper;
    private transient I18NHelper i18n = new I18NHelper(this);
    // Views this presenter can control
    private TarjontaModel _model;
    private TarjontaRootView _rootView;
    private ListHakukohdeView _hakukohdeListView;
    private PerustiedotView hakuKohdePerustiedotView;
    private ShowHakukohdeViewImpl hakukohdeView;
    private ShowKoulutusView showKoulutusView;
    private EditKoulutusView editKoulutusView;
    private SearchResultsView searchResultsView;
    private EditHakukohdeView editHakukohdeView;
    @Autowired(required = true)
    private PublishingService publishingService;
    private EditKoulutusLisatiedotToinenAsteView lisatiedotView;

    public TarjontaPresenter() {
    }

    public void saveHakuKohde(SaveButtonState tila) {
        HakukohdeViewModel hakukohde = getModel().getHakukohde();
        hakukohde.setTila(tila.toTarjontaTila(getModel().getHakukohde().getTila()));
        hakukohde.setHakukohdeKoodistoNimi(resolveHakukohdeKoodistonimiFields() + " " + tila);

        saveHakuKohdePerustiedot();
        editHakukohdeView.enableLiitteetTab();
        editHakukohdeView.enableValintakokeetTab();
    }

    public void saveHakuKohdePerustiedot() {
        LOG.info("Form saved");
        //checkHakuLiitetoimitusPvm();

        if (getModel().getHakukohde().getOid() == null) {

            LOG.debug(getModel().getHakukohde().getHakukohdeNimi() + ", " + getModel().getHakukohde().getHakukohdeKoodistoNimi());

            HakukohdeTyyppi hakukohdeTyyppi = hakukohdeToDTOConverter.convertHakukohdeViewModelToDTO(getModel().getHakukohde());
            getModel().getHakukohde().setOid(hakukohdeTyyppi.getOid());

            KoodiUriAndVersioType uriType = TarjontaUIHelper.getKoodiUriAndVersioTypeByKoodiUriAndVersion(getModel().getHakukohde().getHakukohdeNimi());
            List<KoodiType> listKoodiByRelation = koodiService.listKoodiByRelation(uriType, true, SuhteenTyyppiType.SISALTYY);

            for (KoodiType koodi : listKoodiByRelation) {
                final String koodistoUri = koodi.getKoodisto().getKoodistoUri();
                if (KoodistoURIHelper.KOODISTO_VALINTAPERUSTEKUVAUSRYHMA_URI.equals(koodistoUri)) {
                    hakukohdeTyyppi.setValintaperustekuvausKoodiUri(TarjontaUIHelper.createVersionUri(koodi.getKoodiUri(), koodi.getVersio()));
                }

                if (KoodistoURIHelper.KOODISTO_SORA_KUVAUSRYHMA_URI.equals(koodistoUri)) {
                    hakukohdeTyyppi.setSoraKuvausKoodiUri(TarjontaUIHelper.createVersionUri(koodi.getKoodiUri(), koodi.getVersio()));
                }
            }

            tarjontaAdminService.lisaaHakukohde(hakukohdeTyyppi);

        } else {
            tarjontaAdminService.paivitaHakukohde(hakukohdeToDTOConverter.convertHakukohdeViewModelToDTO(getModel().getHakukohde()));
        }
    }

    private void checkHakuLiitetoimitusPvm() {
        if (getModel().getHakukohde().isKaytaHaunPaattymisenAikaa()) {
            if (getModel().getHakukohde().getHakuOid() != null && getModel().getHakukohde().getHakuOid().getPaattymisPvm() != null) {
                getModel().getHakukohde().setLiitteidenToimitusPvm(getModel().getHakukohde().getHakuOid().getPaattymisPvm());
            }
        }
        if (!getModel().getHakukohde().isSahkoinenToimitusSallittu()) {
            getModel().getHakukohde().setLiitteidenSahkoinenToimitusOsoite(null);
        }

    }

    public void saveHakukohdeLiite() {
        ArrayList<HakukohdeLiiteTyyppi> liitteet = new ArrayList<HakukohdeLiiteTyyppi>();
        HakukohdeLiiteViewModelToDtoConverter converter = new HakukohdeLiiteViewModelToDtoConverter();

        HakukohdeLiiteTyyppi hakukohdeLiite = converter.convertHakukohdeViewModelToHakukohdeLiiteTyyppi(getModel().getSelectedLiite());
        hakukohdeLiite.setLiitteenTyyppiKoodistoNimi(uiHelper.getKoodiNimi(hakukohdeLiite.getLiitteenTyyppi()));
        liitteet.add(hakukohdeLiite);

        for (HakukohdeLiiteViewModel hakuLiite : loadHakukohdeLiitteet()) {
            HakukohdeLiiteTyyppi liite = converter.convertHakukohdeViewModelToHakukohdeLiiteTyyppi(hakuLiite);
            liitteet.add(liite);
        }

        tarjontaAdminService.tallennaLiitteitaHakukohteelle(getModel().getHakukohde().getOid(), liitteet);
        getModel().setSelectedLiite(null);
    }

    public void removeLiiteFromHakukohde(HakukohdeLiiteViewModel liite) {
        tarjontaAdminService.poistaHakukohdeLiite(liite.getHakukohdeLiiteId());
        editHakukohdeView.loadLiiteTableWithData();
    }

    public void removeValintakoeFromHakukohde(ValintakoeViewModel valintakoe) {
        tarjontaAdminService.poistaValintakoe(valintakoe.getValintakoeTunniste());
        editHakukohdeView.loadValintakokees();
    }

    public void saveHakukohdeValintakoe(List<KielikaannosViewModel> kuvaukset) {
        getModel().getSelectedValintaKoe().setSanallisetKuvaukset(kuvaukset);
        getModel().getHakukohde().getValintaKokees().add(getModel().getSelectedValintaKoe());
        List<ValintakoeTyyppi> valintakokeet = new ArrayList<ValintakoeTyyppi>();
        for (ValintakoeViewModel valintakoeViewModel : getModel().getHakukohde().getValintaKokees()) {
            valintakokeet.add(ValintakoeConverter.mapKieliKaannosToValintakoeTyyppi(valintakoeViewModel));
        }

        tarjontaAdminService.tallennaValintakokeitaHakukohteelle(getModel().getHakukohde().getOid(), valintakokeet);

        getModel().setSelectedValintaKoe(new ValintakoeViewModel());
        editHakukohdeView.loadValintakokees();
        editHakukohdeView.closeValintakoeEditWindow();
    }

    public void closeCancelHakukohteenEditView() {
        editHakukohdeView.closeHakukohdeLiiteEditWindow();
    }

    public void saveHakukohteenEditView() {
        saveHakukohdeLiite();
        editHakukohdeView.loadLiiteTableWithData();
        editHakukohdeView.closeHakukohdeLiiteEditWindow();
    }

    public void initHakukohdeForm(PerustiedotView hakuKohdePerustiedotView) {
        this.hakuKohdePerustiedotView = hakuKohdePerustiedotView;

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


            //TODO: If hakukohde is not now initialize hakukohdeLiite form
            getModel().setSelectedLiite(new HakukohdeLiiteViewModel());
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
        reloadMainView();
        getRootView().showMainView();
    }

    public void closeValintakoeEditWindow() {
        editHakukohdeView.closeValintakoeEditWindow();
    }

    //Tuomas Katva : two following methods break the presenter pattern consider moving everything except service call to view
    public CreationDialog<KoulutusOidNameViewModel> createHakukohdeCreationDialogWithKomotoOids(List<String> komotoOids) {
        HaeKoulutuksetKyselyTyyppi kysely = new HaeKoulutuksetKyselyTyyppi();
        kysely.getKoulutusOids().addAll(komotoOids);
        HaeKoulutuksetVastausTyyppi vastaus = tarjontaPublicService.haeKoulutukset(kysely);

        List<KoulutusOidNameViewModel> koulutusModel = convertKoulutusToNameOidViewModel(vastaus.getKoulutusTulos());


        CreationDialog<KoulutusOidNameViewModel> dialog = new CreationDialog<KoulutusOidNameViewModel>(koulutusModel, KoulutusOidNameViewModel.class, "HakukohdeCreationDialog.title", "HakukohdeCreationDialog.valitutKoulutuksetOptionGroup");
        List<String> validationMessages = validateKoulutukses(vastaus.getKoulutusTulos());
        if (validationMessages != null && validationMessages.size() > 0) {
            for (String validationMessage : validationMessages) {
                dialog.addErrorMessage(validationMessage);
            }
        }

        return dialog;
    }

    public List<String> validateKoulutusOidNameViewModel(Collection<KoulutusOidNameViewModel> koulutukses) {
        List<String> selectedOids = new ArrayList<String>();
        for (KoulutusOidNameViewModel koulutusOidNameViewModel : koulutukses) {
            selectedOids.add(koulutusOidNameViewModel.getKoulutusOid());
        }
        HaeKoulutuksetKyselyTyyppi kysely = new HaeKoulutuksetKyselyTyyppi();
        kysely.getKoulutusOids().addAll(selectedOids);
        HaeKoulutuksetVastausTyyppi vastaus = tarjontaPublicService.haeKoulutukset(kysely);
        return validateKoulutukses(vastaus.getKoulutusTulos());
    }

    private List<String> validateKoulutukses(List<KoulutusTulos> koulutukses) {

        List<String> returnVal = new ArrayList<String>();
        List<String> koulutusKoodis = new ArrayList<String>();
        List<String> pohjakoulutukses = new ArrayList<String>();
        for (KoulutusTulos koulutusModel : koulutukses) {
            koulutusKoodis.add(koulutusModel.getKoulutus().getKoulutuskoodi());
            pohjakoulutukses.add(koulutusModel.getKoulutus().getPohjakoulutusVaatimus());
        }
        if (!doesEqual(koulutusKoodis.toArray(new String[koulutusKoodis.size()]))) {
            returnVal.add(I18N.getMessage("HakukohdeCreationDialog.wrongKoulutuskoodi"));
        }
        if (!doesEqual(pohjakoulutukses.toArray(new String[pohjakoulutukses.size()]))) {
            returnVal.add(I18N.getMessage("HakukohdeCreationDialog.wrongPohjakoulutus"));
        }
        return returnVal;
    }

    private boolean doesEqual(String[] strs) {
        for (int i = 0; i < strs.length; i++) {
            if (!strs[0].equals(strs[i])) {
                return false;
            }
        }
        return true;
    }

    public CreationDialog<KoulutusOidNameViewModel> createHakukohdeCreationDialogWithSelectedTarjoaja() {
        HaeKoulutuksetKyselyTyyppi kysely = new HaeKoulutuksetKyselyTyyppi();
        kysely.getTarjoajaOids().add(getModel().getOrganisaatioOid());
        HaeKoulutuksetVastausTyyppi vastaus = tarjontaPublicService.haeKoulutukset(kysely);
        List<KoulutusOidNameViewModel> filtedredKoulutukses = removeSelectedKoulutukses(convertKoulutusToNameOidViewModel(vastaus.getKoulutusTulos()));
        CreationDialog<KoulutusOidNameViewModel> dialog = new CreationDialog<KoulutusOidNameViewModel>(filtedredKoulutukses, KoulutusOidNameViewModel.class, "ShowHakukohdeViewImpl.liitaUusiKoulutusDialogSecondaryTitle", "HakukohdeCreationDialog.valitutKoulutuksetOptionGroup");
        return dialog;

    }

    private List<KoulutusOidNameViewModel> removeSelectedKoulutukses(List<KoulutusOidNameViewModel> koulutukses) {
        List<KoulutusOidNameViewModel> filteredKoulutukses = new ArrayList<KoulutusOidNameViewModel>();
        for (KoulutusOidNameViewModel koulutus : koulutukses) {
            boolean koulutusFound = false;
            for (String oid : getModel().getHakukohde().getKomotoOids()) {
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
            String nimi = "";

            if (tulos.getKoulutus().getNimi() != null) {
                Teksti name = TarjontaUIHelper.getClosestMonikielinenTekstiTyyppiName(I18N.getLocale(), tulos.getKoulutus().getNimi());

                if (name != null) {
                    nimi = name.getValue();
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
        for (KoulutusOidNameViewModel koulutus : koulutukses) {
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

    public List<OrganisaatioPerustietoType> fetchChildOrganisaatios(List<String> organisaatioOids) {

        OrganisaatioSearchCriteriaDTO criteriaDTO = new OrganisaatioSearchCriteriaDTO();

        criteriaDTO.getOidResctrictionList().addAll(organisaatioOids);
        criteriaDTO.setMaxResults(400);

        return organisaatioService.searchBasicOrganisaatios(criteriaDTO);

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
        caption += ", " + curKoulutus.getTila().value();
        return caption;
    }

    public void copyKoulutusToOrganizations(Collection<OrganisaatioPerustietoType> orgs) {
        getModel().setOrganisaatios(convertPerustietoToNameOidPair(orgs));

        showCopyKoulutusPerustiedotEditView(getModel().getSelectedKoulutusOid());
        getModel().getSelectedKoulutukset().clear();
    }

    private Collection<TarjontaModel.OrganisaatioOidNamePair> convertPerustietoToNameOidPair(Collection<OrganisaatioPerustietoType> orgs) {
        Collection<TarjontaModel.OrganisaatioOidNamePair> oidNamePairs = new ArrayList<TarjontaModel.OrganisaatioOidNamePair>();
        for (OrganisaatioPerustietoType org : orgs) {
            TarjontaModel.OrganisaatioOidNamePair organisaatioOidNamePair = new TarjontaModel.OrganisaatioOidNamePair(org.getOid(), org.getNimiFi());
            oidNamePairs.add(organisaatioOidNamePair);
        }
        return oidNamePairs;
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

    public void showCopyKoulutusPerustiedotEditView(final String koulutusOid) {
        // If oid of koulutus is provided the koulutus is read from database
        // before opening the KoulutusEditView
        if (koulutusOid != null) {
            copyKoulutusToModel(koulutusOid);

            getModel().getKoulutusPerustiedotModel().setTila(TarjontaTila.LUONNOS);

            showEditKoulutusView(koulutusOid, KoulutusActiveTab.PERUSTIEDOT);
            getModel().getKoulutusPerustiedotModel().setOid(null);
        }
    }

    @SuppressWarnings("empty-statement")
    public void showKoulutustEditView(final String koulutusOid, final KoulutusActiveTab tab) {
        // If oid of koulutus is provided the koulutus is read from database
        // before opening the KoulutusEditView
        if (koulutusOid != null) {
            readKoulutusToModel(koulutusOid);
        } else {
            if (getModel().getOrganisaatioOid() == null) {
                throw new RuntimeException("Application error - missing organisation OID.");
            }
            getModel().getKoulutusPerustiedotModel().clearModel(DocumentStatus.NEW);
            getModel().getKoulutusPerustiedotModel().setOrganisaatioOidTree(fetchOrganisaatioTree(getModel().getOrganisaatioOid()));
            getModel().setKoulutusLisatiedotModel(new KoulutusLisatiedotModel());
        }
        showEditKoulutusView(koulutusOid, tab);
    }

    public void showLisaaRinnakkainenToteutusEditView(final String koulutusOid) {
        if (koulutusOid != null) {
            readKoulutusToModel(koulutusOid);

            getModel().getKoulutusPerustiedotModel().getKoulutuksenHakukohteet().clear();
            getModel().getKoulutusPerustiedotModel().setOpetuskieli(null);
            getModel().getKoulutusPerustiedotModel().setOid(null);
            getModel().getKoulutusPerustiedotModel().setSuunniteltuKesto(null);
            getModel().getKoulutusPerustiedotModel().setKoulutuslaji(null);
            getModel().getKoulutusPerustiedotModel().setOpetusmuoto(null);
            getModel().getKoulutusPerustiedotModel().setPohjakoulutusvaatimus(null);
            showEditKoulutusView(koulutusOid, KoulutusActiveTab.PERUSTIEDOT);
        }
    }

    /*
     * Retrieves the oids of organisaatios that belong to the organisaatio tree of the organisaatio the oid of which is
     * given as a parameter to this method. 
     * The retrieved oid list is used when querying for potential yhteyshenkilos of a koulutus object.
     */
    private List<String> fetchOrganisaatioTree(String organisaatioOid) {
        List<String> organisaatioOidTree = new ArrayList<String>();
        organisaatioOidTree.add(organisaatioOid);
        try {
            List<OrganisaatioDTO> parentOrganisaatios = organisaatioService.findParentsTo(organisaatioOid);
            for (OrganisaatioDTO curOrg : parentOrganisaatios) {
                organisaatioOidTree.add(curOrg.getOid());
            }
            OrganisaatioSearchOidType childKysely = new OrganisaatioSearchOidType();
            childKysely.setSearchOid(organisaatioOid);
            OrganisaatioOidListType childVastaus = organisaatioService.findChildrenOidsByOid(childKysely);
            for (OrganisaatioOidType curOid : childVastaus.getOrganisaatioOidList()) {
                organisaatioOidTree.add(curOid.getOrganisaatioOid());
            }
        } catch (Exception ex) {
            LOG.error("Problem fetching organisaatio oid tree: {}", ex.getMessage());
        }
        return organisaatioOidTree;
    }

    private void copyKoulutusToModel(final String koulutusOid) {
        LueKoulutusVastausTyyppi lueKoulutus = this.getKoulutusByOid(koulutusOid);
        try {
            KoulutusToisenAsteenPerustiedotViewModel koulutus;
            koulutus = koulutusToDTOConverter.createKoulutusPerustiedotViewModel(lueKoulutus, DocumentStatus.NEW, I18N.getLocale());
            koulutus.setOrganisaatioOidTree(fetchOrganisaatioTree(koulutus.getOrganisaatioOid()));
            getModel().setKoulutusPerustiedotModel(koulutus);
            getModel().setKoulutusLisatiedotModel(koulutusToDTOConverter.createKoulutusLisatiedotViewModel(lueKoulutus));

            //Empty previous Koodisto data from the comboboxes.
            koulutus.getKoulutusohjelmat().clear();
            koulutus.getKoulutuskoodit().clear();
            koulutus.getKoulutuksenHakukohteet().clear();

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

    private void readKoulutusToModel(final String koulutusOid) {
        LueKoulutusVastausTyyppi lueKoulutus = this.getKoulutusByOid(koulutusOid);
        try {
            KoulutusToisenAsteenPerustiedotViewModel koulutus;
            koulutus = koulutusToDTOConverter.createKoulutusPerustiedotViewModel(lueKoulutus, DocumentStatus.LOADED, I18N.getLocale());
            koulutus.setOrganisaatioOidTree(fetchOrganisaatioTree(koulutus.getOrganisaatioOid()));
            getModel().setKoulutusPerustiedotModel(koulutus);
            getModel().setKoulutusLisatiedotModel(koulutusToDTOConverter.createKoulutusLisatiedotViewModel(lueKoulutus));

            //Empty previous Koodisto data from the comboboxes.
            koulutus.getKoulutusohjelmat().clear();
            koulutus.getKoulutuskoodit().clear();
            if (lueKoulutus.getHakukohteet() != null) {
                koulutus.getKoulutuksenHakukohteet().clear();
                for (HakukohdeKoosteTyyppi hakukohdeKoosteTyyppi : lueKoulutus.getHakukohteet()) {
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

    public List<ValintakoeViewModel> loadHakukohdeValintaKokees() {
        ArrayList<ValintakoeViewModel> valintaKokeet = new ArrayList<ValintakoeViewModel>();

        if (getModel().getHakukohde() != null && getModel().getHakukohde().getOid() != null) {
            HaeHakukohteenValintakokeetHakukohteenTunnisteellaKyselyTyyppi kysely = new HaeHakukohteenValintakokeetHakukohteenTunnisteellaKyselyTyyppi();
            kysely.setHakukohteenTunniste(getModel().getHakukohde().getOid());
            HaeHakukohteenValintakokeetHakukohteenTunnisteellaVastausTyyppi vastaus = tarjontaPublicService.haeHakukohteenValintakokeetHakukohteenTunnisteella(kysely);
            LOG.debug("haeHakukohteenValintakokeetHakukohteenTunnisteella size {}", vastaus.getHakukohteenValintaKokeet().size());
            if (vastaus != null && vastaus.getHakukohteenValintaKokeet() != null) {
                for (ValintakoeTyyppi valintakoeTyyppi : vastaus.getHakukohteenValintaKokeet()) {
                    ValintakoeViewModel valintakoeViewModel = ValintakoeConverter.mapDtoToValintakoeViewModel(valintakoeTyyppi);
                    valintaKokeet.add(valintakoeViewModel);
                }
                getModel().getHakukohde().getValintaKokees().clear();
                getModel().getHakukohde().getValintaKokees().addAll(valintaKokeet);
            }

        }

        return valintaKokeet;
    }

    public List<HakukohdeLiiteViewModel> loadHakukohdeLiitteet() {
        ArrayList<HakukohdeLiiteViewModel> liitteet = new ArrayList<HakukohdeLiiteViewModel>();
        if (getModel().getHakukohde() != null && getModel().getHakukohde().getOid() != null) {
            HaeHakukohteenLiitteetKyselyTyyppi kysely = new HaeHakukohteenLiitteetKyselyTyyppi();
            kysely.setHakukohdeOid(getModel().getHakukohde().getOid());
            HaeHakukohteenLiitteetVastausTyyppi vastaus = tarjontaPublicService.lueHakukohteenLiitteet(kysely);

            for (HakukohdeLiiteTyyppi liiteTyyppi : vastaus.getHakukohteenLiitteet()) {
                HakukohdeLiiteViewModel hakukohdeLiiteViewModel = HakukohdeLiiteTyyppiToViewModelConverter.convert(liiteTyyppi);

                liitteet.add(addTableFields(hakukohdeLiiteViewModel));
            }

        }
        return liitteet;
    }

    private HakukohdeLiiteViewModel addTableFields(HakukohdeLiiteViewModel hakukohdeLiiteViewModel) {

        for (KielikaannosViewModel teksti : hakukohdeLiiteViewModel.getLiitteenSanallinenKuvaus()) {
            if (teksti.getKielikoodi().trim().equalsIgnoreCase(I18N.getLocale().getLanguage().trim())) {
                hakukohdeLiiteViewModel.setLocalizedKuvaus(teksti.getNimi());
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat(LIITE_DATE_PATTERNS);
        if (hakukohdeLiiteViewModel.getToimitettavaMennessa() != null) {
            hakukohdeLiiteViewModel.setToimitusPvmTablePresentation(sdf.format(hakukohdeLiiteViewModel.getToimitettavaMennessa()));
        }

        StringBuilder sb = new StringBuilder();
        sb.append(hakukohdeLiiteViewModel.getOsoiteRivi1());
        sb.append("\n");
        sb.append(hakukohdeLiiteViewModel.getPostinumero());
        sb.append(" ");
        sb.append(hakukohdeLiiteViewModel.getPostitoimiPaikka());
        hakukohdeLiiteViewModel.setToimitusOsoiteConcat(sb.toString());
        return hakukohdeLiiteViewModel;
    }

    public void loadHakukohdeLiiteWithId(String liiteId) {
        LueHakukohteenLiiteTunnisteellaKyselyTyyppi kysely = new LueHakukohteenLiiteTunnisteellaKyselyTyyppi();
        kysely.setHakukohteenLiiteTunniste(liiteId);
        LueHakukohteenLiiteTunnisteellaVastausTyyppi vastaus = tarjontaPublicService.lueHakukohteenLiiteTunnisteella(kysely);
        getModel().setSelectedLiite(HakukohdeLiiteTyyppiToViewModelConverter.convert(vastaus.getHakukohteenLiite()));
    }

    public void showHakukohdeLiiteEditWindow(String liiteId) {
        editHakukohdeView.showHakukohdeEditWindow(liiteId);
    }

    public void showHakukohdeValintakoeEditView(String valintakoeId) {
        editHakukohdeView.showHakukohdeValintakoeEditView(valintakoeId);
    }

    public HakukohdeLiiteViewModel getSelectedHakuliite() {


        //Set default fields
        if (getModel().getHakukohde() != null) {

            getModel().getSelectedLiite().setOsoiteRivi1(getModel().getHakukohde().getOsoiteRivi1());
            getModel().getSelectedLiite().setOsoiteRivi2(getModel().getHakukohde().getOsoiteRivi2());
            getModel().getSelectedLiite().setPostinumero(getModel().getHakukohde().getPostinumero());
            getModel().getSelectedLiite().setPostitoimiPaikka(getModel().getHakukohde().getPostitoimipaikka());

        }

        return getModel().getSelectedLiite();
    }

    public void loadValintakoeWithId(String id) {
        LueHakukohteenValintakoeTunnisteellaKyselyTyyppi kysely = new LueHakukohteenValintakoeTunnisteellaKyselyTyyppi();
        kysely.setHakukohteenValintakoeTunniste(id);
        LueHakukohteenValintakoeTunnisteellaVastausTyyppi vastaus = tarjontaPublicService.lueHakukohteenValintakoeTunnisteella(kysely);
        getModel().setSelectedValintaKoe(ValintakoeConverter.mapDtoToValintakoeViewModel(vastaus.getHakukohdeValintakoe()));
    }

    public ValintakoeViewModel getSelectedValintakoe() {
        if (getModel().getSelectedValintaKoe() == null) {
            getModel().setSelectedValintaKoe(new ValintakoeViewModel());
        }


        return getModel().getSelectedValintaKoe();
    }

    public ValintakoeAikaViewModel getSelectedAikaView() {
        ValintakoeAikaViewModel aikaViewModel = new ValintakoeAikaViewModel();

        if (getModel().getSelectedValintakoeAika() != null) {
            aikaViewModel.setValintakoeAikaTiedot(getModel().getSelectedValintakoeAika().getValintakoeAikaTiedot());
            aikaViewModel.setOsoiteRivi(getModel().getSelectedValintakoeAika().getOsoiteRivi());
            aikaViewModel.setPostinumero(getModel().getSelectedValintakoeAika().getPostinumero());
            aikaViewModel.setPostitoimiPaikka(getModel().getSelectedValintakoeAika().getPostitoimiPaikka());
            aikaViewModel.setAlkamisAika(getModel().getSelectedValintakoeAika().getAlkamisAika());
            aikaViewModel.setPaattymisAika(getModel().getSelectedValintakoeAika().getPaattymisAika());

        }


        return aikaViewModel;
    }

    public void removeValintakoeAikaSelection(ValintakoeAikaViewModel valintakoeAikaViewModel) {
        List<ValintakoeAikaViewModel> ajat = new ArrayList<ValintakoeAikaViewModel>();
        if (getModel().getSelectedValintaKoe().getValintakoeAjat() != null) {
            for (ValintakoeAikaViewModel aika : getModel().getSelectedValintaKoe().getValintakoeAjat()) {
                if (!aika.equals(valintakoeAikaViewModel)) {
                    ajat.add(aika);
                }
            }
            getModel().getSelectedValintaKoe().setValintakoeAjat(ajat);
        }
    }

    private HakukohdeNameUriModel hakukohdeNameUriModelFromKoodi(KoodiType koodiType) {
        HakukohdeNameUriModel hakukohdeNameUriModel = new HakukohdeNameUriModel();
        hakukohdeNameUriModel.setUriVersio(koodiType.getVersio());
        hakukohdeNameUriModel.setHakukohdeUri(koodiType.getKoodiUri());
        hakukohdeNameUriModel.setHakukohdeArvo(koodiType.getKoodiArvo());
        if (koodiType.getMetadata() != null) {
            hakukohdeNameUriModel.setHakukohdeNimi(koodiType.getMetadata().get(0).getNimi());
        }
        return hakukohdeNameUriModel;
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
        editHakukohdeView = new EditHakukohdeView();
        if (hakukohdeOid == null) {
            getModel().setHakukohde(new HakukohdeViewModel());

        } else {

            editHakukohdeView.loadLiiteTableWithData();

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

            if (getModel().getHakukohde().getHakukohdeNimi() != null) {
                List<KoodiType> koodis = uiHelper.gethKoodis(getModel().getHakukohde().getHakukohdeNimi());
                if (koodis != null && koodis.size() > 0) {
                    getModel().getHakukohde().setSelectedHakukohdeNimi(hakukohdeNameUriModelFromKoodi(koodis.get(0)));
                }
            }
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
     * Saves koulutus/tukinto, other synonyms: LOI, KOMOTO.
     *
     * @param tila (save state)
     * @throws ExceptionMessage
     */
    public void saveKoulutus(SaveButtonState tila) throws ExceptionMessage {
        KoulutusToisenAsteenPerustiedotViewModel koulutusModel = getModel().getKoulutusPerustiedotModel();

        if (koulutusModel.isLoaded()) {
            //update KOMOTO
            PaivitaKoulutusTyyppi paivita = koulutusToDTOConverter.createPaivitaKoulutusTyyppi(getModel(), koulutusModel.getOid());
            paivita.setTila(tila.toTarjontaTila(koulutusModel.getTila()));
            koulutusToDTOConverter.validateSaveData(paivita, koulutusModel);
            tarjontaAdminService.paivitaKoulutus(paivita);
        } else {
            for (TarjontaModel.OrganisaatioOidNamePair pair : getModel().getOrganisaatios()) {
                getModel().setOrganisaatioName(pair.getName());
                getModel().setOrganisaatioOid(pair.getOid());
                persistKoulutus(koulutusModel, tila);
            }
        }

        this.editKoulutusView.enableLisatiedotTab();
        this.lisatiedotView.getEditKoulutusLisatiedotForm().reBuildTabsheet();
    }

    private void persistKoulutus(KoulutusToisenAsteenPerustiedotViewModel koulutusModel, SaveButtonState tila) throws ExceptionMessage {
        //persist new KOMO and KOMOTO
        koulutusModel.setOrganisaatioOid(getModel().getOrganisaatioOid());
        koulutusModel.setOrganisaatioName(getModel().getOrganisaatioName());

        LisaaKoulutusTyyppi lisaa = koulutusToDTOConverter.createLisaaKoulutusTyyppi(getModel(), getModel().getOrganisaatioOid());
        lisaa.setTila(tila.toTarjontaTila(koulutusModel.getTila()));
        koulutusToDTOConverter.validateSaveData(lisaa, koulutusModel);
        checkKoulutusmoduuli();
        if (checkExistingKomoto(lisaa)) {
            tarjontaAdminService.lisaaKoulutus(lisaa);
            koulutusModel.setDocumentStatus(DocumentStatus.SAVED);
            koulutusModel.setOid(lisaa.getOid());
        } else {

            LOG.debug("Unable to add koulutus because of the duplicate");
            throw new ExceptionMessage("EditKoulutusPerustiedotYhteystietoView.koulutusExistsMessage");
        }
    }

    private boolean checkExistingKomoto(LisaaKoulutusTyyppi lisaaTyyppi) {
        TarkistaKoulutusKopiointiTyyppi kysely = new TarkistaKoulutusKopiointiTyyppi();
        kysely.setKoulutusAlkamisPvm(lisaaTyyppi.getKoulutuksenAlkamisPaiva());
        kysely.setKoulutusLuokitusKoodi(lisaaTyyppi.getKoulutusKoodi().getUri());
        kysely.setKoulutusohjelmaKoodi(lisaaTyyppi.getKoulutusohjelmaKoodi().getUri());
        kysely.setPohjakoulutus(lisaaTyyppi.getPohjakoulutusvaatimus().getUri());
        kysely.getOpetuskielis().addAll(getUrisFromKoodistoTyyppis(lisaaTyyppi.getOpetuskieli()));
        kysely.getKoulutuslajis().addAll(getUrisFromKoodistoTyyppis(lisaaTyyppi.getKoulutuslaji()));
        kysely.setTarjoajaOid(lisaaTyyppi.getTarjoaja());

        return tarjontaAdminService.tarkistaKoulutuksenKopiointi(kysely);

    }

    private List<String> getUrisFromKoodistoTyyppis(List<KoodistoKoodiTyyppi> koodistoKoodis) {
        List<String> uris = new ArrayList<String>();

        for (KoodistoKoodiTyyppi koodi : koodistoKoodis) {
            uris.add(koodi.getUri());
        }

        return uris;
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
     *
     * Reload koulutus and hakukohde views when an user hasn't selected root
     * organization OID. If the root organization is selected, then all data
     * items are cleared from the views.
     *
     */
    public void reloadMainView() {
        reloadMainView(false);
    }

    /**
     *
     * Reload koulutus and hakukohde views when an user hasn't selected root
     * organization OID. If the root organization is selected, then all data
     * items are cleared from the views. There is also an option to force reload
     * to the views.
     *
     * @param forceReload
     */
    public void reloadMainView(final boolean forceReload) {
        //Main view will be reloaded only when an user has selected other than the root organisation
        if (forceReload || (getModel().getOrganisaatioOid() != null && !getModel().isSelectedRootOrganisaatio())) {
            LOG.debug("not root, main view reloaded {} {}", getModel().getOrganisaatioOid(), getModel().isSelectedRootOrganisaatio());
            getReloadKoulutusListData();
            getHakukohdeListView().reload();
        } else {
            getRootView().getListKoulutusView().clearAllDataItems();
            getHakukohdeListView().clearAllDataItems();
            this.searchResultsView.setResultSizeForKoulutusTab(0);
            this.searchResultsView.setResultSizeForHakukohdeTab(0);
        }
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
        showKoulutusView.showHakukohdeRemovalDialog(hakukohdeOid, hakukohdeNimi);
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
    @Override
    public void showNotification(final UserNotification msg) {
        LOG.info("Show user notification - type {}, value {}", msg, msg != null ? msg.getInfo() : null);
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

    private void addOrganisaatioNameValuePair(String oid, String name) {
        TarjontaModel.OrganisaatioOidNamePair pair = new TarjontaModel.OrganisaatioOidNamePair(oid, name);
        getModel().addOneOrganisaatioNameOidPair(pair);
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
        addOrganisaatioNameValuePair(organisaatioOid, organisaatioName);
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

        reloadMainView(false);
        this.getRootView().getListKoulutusView().toggleCreateKoulutusB(organisaatioOid, true);
    }

    public void unSelectOrganisaatio() {
        //TODO: there is no real breadcrumb, so the parent is always root level (OPH)...
        getModel().setOrganisaatioOid(getModel().getParentOrganisaatioOid());
        getModel().setOrganisaatioName(NAME_OPH);
        getRootView().getBreadcrumbsView().setOrganisaatio(NAME_OPH);

        getRootView().getOrganisaatiohakuView().clearTreeSelection();

        //Clearing the selected hakukohde and koulutus objects
        getModel().getSelectedhakukohteet().clear();
        getModel().getSelectedKoulutukset().clear();

        getModel().getSearchSpec().setOrganisaatioOids(new ArrayList<String>());

        reloadMainView();
        this.getRootView().getListKoulutusView().toggleCreateKoulutusB(getModel().getParentOrganisaatioOid(), false);
        this.getRootView().getListKoulutusView().toggleCreateHakukohdeB(getModel().getParentOrganisaatioOid(), false);
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
        HaeKaikkiKoulutusmoduulitVastausTyyppi haeKaikkiKoulutusmoduulit = tarjontaPublicService.haeKaikkiKoulutusmoduulit(new HaeKaikkiKoulutusmoduulitKyselyTyyppi());
        List<KoulutusmoduuliTulos> koulutusmoduuliTulos = haeKaikkiKoulutusmoduulit.getKoulutusmoduuliTulos();

        Set<String> uris = new HashSet<String>();
        List<KoulutusmoduuliKoosteTyyppi> komos = new ArrayList<KoulutusmoduuliKoosteTyyppi>();

        for (KoulutusmoduuliTulos tulos : koulutusmoduuliTulos) {
            komos.add(tulos.getKoulutusmoduuli());
            uris.add(tulos.getKoulutusmoduuli().getKoulutuskoodiUri());
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("KOMOs found {}", komos.size());
        }
        KoulutusToisenAsteenPerustiedotViewModel model = getModel().getKoulutusPerustiedotModel();
        model.setKomos(komos);
        model.createCacheKomos(); //cache komos to map object
        model.getKoulutuskoodit().clear();
        //koodisto service search result remapped to UI model objects.
        List<KoulutuskoodiModel> listaaKoulutuskoodit = kolutusKoodistoConverter.listaaKoulutukset(uris, I18N.getLocale());

        Collections.sort(listaaKoulutuskoodit, new BeanComparator("nimi"));
        model.getKoulutuskoodit().addAll(filterBasedOnOppilaitosTyyppi(listaaKoulutuskoodit));
    }

    /*
     * Filters list of KoulutuskoodiModel objects such that only the objects related to the
     * oppilaitostyyppi of the selected organisaatio are returned.
     */
    private List<KoulutuskoodiModel> filterBasedOnOppilaitosTyyppi(List<KoulutuskoodiModel> unfilteredKoodit) {
        LOG.debug("fitlerBasedOnOppilaitosTyyppi");

        //If an existing koulutus is being edited no filtering is done.
        if (getModel().getKoulutusPerustiedotModel() != null
                && getModel().getKoulutusPerustiedotModel().getOid() != null) {
            return unfilteredKoodit;
        }

        //Constructing the list of oppilaitostyyppis of the selected organisaatio
        List<String> olTyyppiUris = getOppilaitostyyppiUris();

        //Filtering the koulutuskoodit based on the oppilaitostyypit.
        return this.uiHelper.getKoulutusFilteredkooditRelatedToOlTyypit(olTyyppiUris, unfilteredKoodit);
    }

    /*
     * Retrieves the list of oppilaitostyyppis matching the selected organisaatio.
     */
    private List<String> getOppilaitostyyppiUris() {
        OrganisaatioDTO selectedOrg = this.organisaatioService.findByOid(this.getModel().getOrganisaatioOid());

        if (selectedOrg == null) {
            throw new RuntimeException("No organisation found by OID " + this.getModel().getOrganisaatioOid() + ".");
        }

        List<OrganisaatioTyyppi> tyypit = selectedOrg.getTyypit();
        List<String> olTyyppiUris = new ArrayList<String>();
        //If the types of the organisaatio contains oppilaitos, its oppilaitostyyppi is appended to the list of oppilaitostyyppiuris
        if (tyypit.contains(OrganisaatioTyyppi.OPPILAITOS)) {
            olTyyppiUris.add(selectedOrg.getOppilaitosTyyppi());
        }
        //If the types of the organisaatio contain koulutustoimija the oppilaitostyyppis of its children are appended to the
        //list of oppilaitostyyppiuris
        if (tyypit.contains(OrganisaatioTyyppi.KOULUTUSTOIMIJA)) {
            olTyyppiUris.addAll(getChildOrgOlTyyppis(selectedOrg));

            //If the types of the organisaatio contain opetuspiste the oppilaitostyyppi of its parent organisaatio is appended to the list of
            //oppilaitostyyppiuris
        } else if (tyypit.contains(OrganisaatioTyyppi.OPETUSPISTE)
                && selectedOrg.getParentOid() != null) {
            addParentOlTyyppi(selectedOrg, olTyyppiUris);
        }
        LOG.debug("olTyyppiUris size: {}", olTyyppiUris.size());
        return olTyyppiUris;
    }

    /*
     * Adds the oppilaitostyypi of the parent of the organisaatio given as first parameter
     * to the list of oppilaitostyyppis given as second parameters.
     */
    private void addParentOlTyyppi(OrganisaatioDTO selectedOrg, List<String> olTyyppiUris) {
        String olTyyppi = getOrganisaatioOlTyyppi(selectedOrg.getParentOid());
        if (olTyyppi != null) {
            olTyyppiUris.add(olTyyppi);
        }
    }

    /*
     * Gets the oppilaitostyyppi of the organisaatio the oid of which is given as parameters.
     */
    private String getOrganisaatioOlTyyppi(String oid) {
        OrganisaatioDTO organisaatio = this.organisaatioService.findByOid(oid);
        return organisaatio.getOppilaitosTyyppi();
    }

    /*
     * Gets the list of oppilaitostyyppi uris that match the children of the organisaatio given as parameter.
     */
    private List<String> getChildOrgOlTyyppis(OrganisaatioDTO selectedOrg) {
        List<String> childOlTyyppis = new ArrayList<String>();
        OrganisaatioSearchCriteriaDTO criteria = new OrganisaatioSearchCriteriaDTO();
        criteria.setOrganisaatioTyyppi(OrganisaatioTyyppi.OPPILAITOS.value());
        criteria.getOidResctrictionList().add(selectedOrg.getOid());
        List<OrganisaatioDTO> childOrgs = this.organisaatioService.findChildrenTo(selectedOrg.getOid());
        if (childOrgs != null) {
            for (OrganisaatioDTO curChild : childOrgs) {
                if (curChild.getTyypit().contains(OrganisaatioTyyppi.OPPILAITOS)
                        && !childOlTyyppis.contains(curChild.getOppilaitosTyyppi())) {
                    childOlTyyppis.add(curChild.getOppilaitosTyyppi());
                }
            }
        }
        return childOlTyyppis;
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
            String organisaatioOid = model.getOrganisaatioOid() != null ? model.getOrganisaatioOid() : getModel().getOrganisaatioOid();

            //Loading data from the parent tutkinto komo (startDate and koulutusohjelmanValinta).
            loadTutkintoData(model.getKoulutuskoodiModel().getKoodistoUriVersio(), organisaatioOid);
        }
    }

    //Prefills the tutkinto komoto (koulutuksenAlkamisPvm, koulutusohjelmanValinta) fields if a tutkinto komoto exists
    private void loadTutkintoData(String koulutuskoodi, String tarjoaja) {
        LOG.debug("loadtutkintoData, koulutuskoodi: {}, tarjoaja: {}", koulutuskoodi, tarjoaja);
        HaeKoulutuksetKyselyTyyppi kysely = new HaeKoulutuksetKyselyTyyppi();
        kysely.setKoulutusKoodi(koulutuskoodi);

        kysely.getTarjoajaOids().add(tarjoaja);
        HaeKoulutuksetVastausTyyppi vastaus = this.tarjontaPublicService.haeKoulutukset(kysely);

        if (vastaus.getKoulutusTulos() != null && !vastaus.getKoulutusTulos().isEmpty()) {
            KoulutusTulos hakutulos = vastaus.getKoulutusTulos().get(0);
            LueKoulutusKyselyTyyppi lueKysely = new LueKoulutusKyselyTyyppi();
            lueKysely.setOid(hakutulos.getKoulutus().getKomotoOid());
            LueKoulutusVastausTyyppi lueVastaus = tarjontaPublicService.lueKoulutus(lueKysely);
            Date koulutuksenAlkuPvm = lueVastaus.getKoulutuksenAlkamisPaiva() != null ? lueVastaus.getKoulutuksenAlkamisPaiva().toGregorianCalendar().getTime() : null;

            getModel().getKoulutusPerustiedotModel().setKoulutuksenAlkamisPvm(koulutuksenAlkuPvm);
            getModel().setKoulutusLisatiedotModel(new KoulutusLisatiedotModel());

            if (lueVastaus.getKoulutusohjelmanValinta() != null) {
                for (MonikielinenTekstiTyyppi.Teksti mkt : lueVastaus.getKoulutusohjelmanValinta().getTeksti()) {
                    getModel().getKoulutusLisatiedotModel().getLisatiedot(mkt.getKieliKoodi()).setKoulutusohjelmanValinta(mkt.getValue());
                }
            }
            LOG.debug("going to reload tabsheet");
            this.lisatiedotView.getEditKoulutusLisatiedotForm().reBuildTabsheet();
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
    @Override
    public TarjontaPermissionServiceImpl getPermission() {
        return tarjontaPermissionService;
    }

    /**
     * Enables or disables hakukohde button based on whether there are selected
     * koulutus objects in the list.
     */
    public void toggleCreateHakukohde() {
        this.getRootView().getListKoulutusView().toggleCreateHakukohdeB(this.getModel().getOrganisaatioOid(), !this._model.getSelectedKoulutukset().isEmpty());
    }

    public void setSearchResultsView(SearchResultsView searchResultsView) {
        this.searchResultsView = searchResultsView;
    }

    /**
     * Search yhteyshenkilo for a koulutus using user service based on on name
     * of the user and the organisation of the koulutus.
     *
     * @param value - the name or part of the name of the user to search for
     */
    public List<HenkiloType> searchYhteyshenkilo(String value) {
        //If given string is null or empty returning an empty list, i.e. not doing an empty search.
        if (value == null || value.isEmpty()) {
            return new ArrayList<HenkiloType>();
        }
        //Doing the search to UserService
        HenkiloSearchObjectType searchType = new HenkiloSearchObjectType();
        searchType.setConnective(SearchConnectiveType.AND);
        String[] nimetSplit = value.split(" ");
        if (nimetSplit.length > 1) {
            searchType.setSukunimi(nimetSplit[nimetSplit.length - 1]);
            searchType.setEtunimet(value.substring(0, value.lastIndexOf(' ')));
        } else {
            searchType.setEtunimet(value);
        }
        searchType.getOrganisaatioOids().addAll(_model.getKoulutusPerustiedotModel().getOrganisaatioOidTree());
        HenkiloPagingObjectType paging = new HenkiloPagingObjectType();
        List<HenkiloType> henkilos = new ArrayList<HenkiloType>();
        try {
            henkilos = this.userService.listHenkilos(searchType, paging);
        } catch (Exception ex) {
            LOG.error("Problem fetching henkilos: {}", ex.getMessage());
        }

        //Returning the list of found henkilos.
        return henkilos;
    }

    @Override
    public boolean isSaveButtonEnabled(String oid, SisaltoTyyppi sisalto, TarjontaTila... requiredState) {
        return publishingService.isStateStepAllowed(oid, sisalto, requiredState);
    }

    /**
     * Cancel single tarjonta model by OID and data model type.
     *
     * @param oid
     * @param sisalto
     */
    @Override
    public void changeStateToCancelled(String oid, SisaltoTyyppi sisalto) {
        publish(oid, TarjontaTila.PERUTTU, sisalto);
    }

    @Override
    public void changeStateToPublished(String oid, SisaltoTyyppi sisalto) {
        publish(oid, TarjontaTila.JULKAISTU, sisalto);
    }

    private void publish(final String oid, final TarjontaTila toState, final SisaltoTyyppi sisalto) {
        if (publishingService.changeState(oid, toState, sisalto)) {
            showNotification(UserNotification.GENERIC_SUCCESS);

            //reload result data in tables.
            reloadMainView();
        } else {
            showNotification(UserNotification.GENERIC_ERROR);
        }
    }

    public void setLisatiedotView(
            EditKoulutusLisatiedotToinenAsteView lisatiedotView) {
        this.lisatiedotView = lisatiedotView;
    }

    /**
     * Returns true if there are koulutuskoodis that are related to the
     * oppilaitostyyppis of the currently selected organisaatio.
     *
     * @return
     */
    public boolean availableKoulutus() {
        List<String> oppilaitostyyppiUris = getOppilaitostyyppiUris();
        return !this.uiHelper.getOlRelatedKoulutuskoodit(oppilaitostyyppiUris).isEmpty();
    }

    public boolean checkOrganisaatiosKoulutukses(Collection<OrganisaatioPerustietoType> orgs) {
        for (OrganisaatioPerustietoType org : orgs) {
            List<String> oppilaitosTyyppis = new ArrayList<String>(getOppilaitosUrisForOrg(org));
            boolean isEmpty = this.uiHelper.getOlRelatedKoulutuskoodit(oppilaitosTyyppis).isEmpty();
            if (isEmpty) {
                return false;
            }
        }
        return true;
    }

    private Set<String> getOppilaitosUrisForOrg(OrganisaatioPerustietoType org) {
        Set<String> oppilaitosTyyppis = new HashSet<String>();


        OrganisaatioDTO selectedOrg = this.organisaatioService.findByOid(org.getOid());

        if (selectedOrg == null) {
            throw new RuntimeException("No organisation found by OID " + this.getModel().getOrganisaatioOid() + ".");
        }

        List<OrganisaatioTyyppi> tyypit = selectedOrg.getTyypit();

        //If the types of the organisaatio contains oppilaitos, its oppilaitostyyppi is appended to the list of oppilaitostyyppiuris
        if (tyypit.contains(OrganisaatioTyyppi.OPPILAITOS)) {
            oppilaitosTyyppis.add(selectedOrg.getOppilaitosTyyppi());
        }
        //If the types of the organisaatio contain koulutustoimija the oppilaitostyyppis of its children are appended to the
        //list of oppilaitostyyppiuris
        if (tyypit.contains(OrganisaatioTyyppi.KOULUTUSTOIMIJA)) {
            oppilaitosTyyppis.addAll(getChildOrgOlTyyppis(selectedOrg));

            //If the types of the organisaatio contain opetuspiste the oppilaitostyyppi of its parent organisaatio is appended to the list of
            //oppilaitostyyppiuris
        } else if (tyypit.contains(OrganisaatioTyyppi.OPETUSPISTE)
                && selectedOrg.getParentOid() != null) {
            List<String> olTyyppis = new ArrayList<String>(oppilaitosTyyppis);
            addParentOlTyyppi(selectedOrg, olTyyppis);
            olTyyppis.addAll(olTyyppis);
        }
        LOG.debug("olTyyppiUris size: {}", oppilaitosTyyppis.size());

        return oppilaitosTyyppis;
    }

    /**
     * Open edit koulutus view.  
     * 
     * @param koulutusOid
     * @param tab 
     */
    private void showEditKoulutusView(final String koulutusOid, final KoulutusActiveTab tab) {
        editKoulutusView = new EditKoulutusView(koulutusOid, tab);
        getRootView().changeView(editKoulutusView);
    }
}
