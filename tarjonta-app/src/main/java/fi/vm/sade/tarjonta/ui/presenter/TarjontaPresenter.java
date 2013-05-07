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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.beanutils.BeanComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Window;

import fi.vm.sade.authentication.service.UserService;
import fi.vm.sade.authentication.service.types.HenkiloPagingObjectType;
import fi.vm.sade.authentication.service.types.HenkiloSearchObjectType;
import fi.vm.sade.authentication.service.types.dto.HenkiloType;
import fi.vm.sade.authentication.service.types.dto.SearchConnectiveType;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.generic.ui.feature.UserFeature;
import fi.vm.sade.generic.ui.portlet.security.User;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.*;
import fi.vm.sade.organisaatio.helper.OrganisaatioDisplayHelper;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteenLiitteetKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteenLiitteetVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteenValintakokeetHakukohteenTunnisteellaKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteenValintakokeetHakukohteenTunnisteellaVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetVastausTyyppi.HakukohdeTulos;
import fi.vm.sade.tarjonta.service.types.HaeKaikkiKoulutusmoduulitKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKaikkiKoulutusmoduulitVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetVastausTyyppi.KoulutusTulos;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.HakuTyyppi;
import fi.vm.sade.tarjonta.service.types.HakukohdeKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.HakukohdeLiiteTyyppi;
import fi.vm.sade.tarjonta.service.types.HakukohdeListausTyyppi;
import fi.vm.sade.tarjonta.service.types.HakukohdeTyyppi;
import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTulos;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusHakukohteelleTyyppi;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.ListHakuVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.ListaaHakuTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohdeKoulutuksineenKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohdeKoulutuksineenVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohdeKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohdeVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohteenLiiteTunnisteellaKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohteenLiiteTunnisteellaVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohteenValintakoeTunnisteellaKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohteenValintakoeTunnisteellaVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi.Teksti;
import fi.vm.sade.tarjonta.service.types.PaivitaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.SisaisetHakuAjat;
import fi.vm.sade.tarjonta.service.types.SisaltoTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.service.types.TarkistaKoulutusKopiointiTyyppi;
import fi.vm.sade.tarjonta.service.types.ValintakoeTyyppi;
import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
import fi.vm.sade.tarjonta.ui.enums.KoulutusActiveTab;
import fi.vm.sade.tarjonta.ui.enums.KoulutusasteType;
import fi.vm.sade.tarjonta.ui.enums.SaveButtonState;
import fi.vm.sade.tarjonta.ui.enums.UserNotification;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.conversion.HakukohdeLiiteTyyppiToViewModelConverter;
import fi.vm.sade.tarjonta.ui.helper.conversion.HakukohdeLiiteViewModelToDtoConverter;
import fi.vm.sade.tarjonta.ui.helper.conversion.HakukohdeViewModelToDTOConverter;
import fi.vm.sade.tarjonta.ui.helper.conversion.Koulutus2asteConverter;
import fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusKoodistoConverter;
import fi.vm.sade.tarjonta.ui.helper.conversion.KoulutusSearchSpecificationViewModelToDTOConverter;
import fi.vm.sade.tarjonta.ui.helper.conversion.ValintakoeConverter;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import fi.vm.sade.tarjonta.ui.model.HakukohdeLiiteViewModel;
import fi.vm.sade.tarjonta.ui.model.HakukohdeNameUriModel;
import fi.vm.sade.tarjonta.ui.model.HakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusOidNameViewModel;
import fi.vm.sade.tarjonta.ui.model.SimpleHakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.model.TarjontaModel;
import fi.vm.sade.tarjonta.ui.model.ValintakoeAikaViewModel;
import fi.vm.sade.tarjonta.ui.model.ValintakoeViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusohjelmaModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusLisatiedotModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.org.NavigationModel;
import fi.vm.sade.tarjonta.ui.model.org.OrganisationOidNamePair;
import fi.vm.sade.tarjonta.ui.model.org.TarjoajaModel;
import fi.vm.sade.tarjonta.ui.service.OrganisaatioContext;
import fi.vm.sade.tarjonta.ui.service.PublishingService;
import fi.vm.sade.tarjonta.ui.service.TarjontaPermissionServiceImpl;
import fi.vm.sade.tarjonta.ui.service.UserContext;
import fi.vm.sade.tarjonta.ui.view.SearchResultsView;
import fi.vm.sade.tarjonta.ui.view.TarjontaRootView;
import fi.vm.sade.tarjonta.ui.view.hakukohde.CreationDialog;
import fi.vm.sade.tarjonta.ui.view.hakukohde.EditHakukohdeView;
import fi.vm.sade.tarjonta.ui.view.hakukohde.ListHakukohdeView;
import fi.vm.sade.tarjonta.ui.view.hakukohde.ShowHakukohdeViewImpl;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.PerustiedotView;
import fi.vm.sade.tarjonta.ui.view.koulutus.ShowKoulutusView;
import fi.vm.sade.tarjonta.ui.view.koulutus.aste2.EditKoulutusLisatiedotToinenAsteView;
import fi.vm.sade.tarjonta.ui.view.koulutus.aste2.EditKoulutusView;

/**
 * This class is used to control the "tarjonta" UI.
 *
 * @author jwilen
 * @author tkatva
 * @author mholi
 * @author mlyly
 * @author Timo Santasalo / Teknokala Ky
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
    public OIDService oidService;
    @Autowired(required = true)
    public KoodiService koodiService;
    @Autowired(required = true)
    public TarjontaAdminService tarjontaAdminService;
    @Autowired(required = true)
    public TarjontaPublicService tarjontaPublicService;
    @Autowired(required = true)
    public OrganisaatioService organisaatioService;
    @Autowired(required = true)
    private HakukohdeViewModelToDTOConverter hakukohdeToDTOConverter;
    @Autowired(required = true)
    private Koulutus2asteConverter koulutusToDTOConverter;
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
    @Autowired(required = true)
    private TarjontaLukioPresenter lukioPresenter;
    private LueKoulutusVastausTyyppi rawKoulutus;
    
    public LueKoulutusVastausTyyppi getRawKoulutus() {
        return rawKoulutus;
    }

    public static final String VALINTAKOE_TAB_SELECT = "valintakokeet";
    public static final String LIITTEET_TAB_SELECT = "liitteet";

    public TarjontaPresenter() {
    }

    public void saveHakuKohde(SaveButtonState tila) {
        HakukohdeViewModel hakukohde = getModel().getHakukohde();
        hakukohde.setTila(tila.toTarjontaTila(getModel().getHakukohde().getTila()));
        hakukohde.setHakukohdeKoodistoNimi(resolveHakukohdeKoodistonimiFields() + " " + tilaToLangStr(hakukohde.getTila()));

        saveHakuKohdePerustiedot();
        editHakukohdeView.enableLiitteetTab();
        editHakukohdeView.enableValintakokeetTab();
    }

    public void saveHakuKohdePerustiedot() {
        LOG.info("Form saved");
        //checkHakuLiitetoimitusPvm();
        User usr = UserFeature.get();
        if (getModel().getHakukohde().getOid() == null) {

            LOG.debug(getModel().getHakukohde().getHakukohdeNimi() + ", " + getModel().getHakukohde().getHakukohdeKoodistoNimi());

            HakukohdeTyyppi hakukohdeTyyppi = hakukohdeToDTOConverter.convertHakukohdeViewModelToDTO(getModel().getHakukohde());
            hakukohdeTyyppi.setViimeisinPaivittajaOid(usr.getOid());
            getModel().getHakukohde().setOid(hakukohdeTyyppi.getOid());


            KoodiUriAndVersioType uriType = TarjontaUIHelper.getKoodiUriAndVersioTypeByKoodiUriAndVersion(getModel().getHakukohde().getHakukohdeNimi());
            List<KoodiType> listKoodiByRelation = getKoodiService().listKoodiByRelation(uriType, true, SuhteenTyyppiType.SISALTYY);

            for (KoodiType koodi : listKoodiByRelation) {
                final String koodistoUri = koodi.getKoodisto().getKoodistoUri();
                if (KoodistoURIHelper.KOODISTO_VALINTAPERUSTEKUVAUSRYHMA_URI.equals(koodistoUri)) {
                    hakukohdeTyyppi.setValintaperustekuvausKoodiUri(TarjontaUIHelper.createVersionUri(koodi.getKoodiUri(), koodi.getVersio()));
                }

                if (KoodistoURIHelper.KOODISTO_SORA_KUVAUSRYHMA_URI.equals(koodistoUri)) {
                    hakukohdeTyyppi.setSoraKuvausKoodiUri(TarjontaUIHelper.createVersionUri(koodi.getKoodiUri(), koodi.getVersio()));
                }
            }
            HakukohdeTyyppi fresh = getTarjontaAdminService().lisaaHakukohde(hakukohdeTyyppi);
            //TODO copy this fresh dto to model;
        } else {

            HakukohdeTyyppi fresh = getTarjontaAdminService().paivitaHakukohde(hakukohdeToDTOConverter.convertHakukohdeViewModelToDTO(getModel().getHakukohde()));
            //TODO copy this fresh dto to model;
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

        getTarjontaAdminService().tallennaLiitteitaHakukohteelle(getModel().getHakukohde().getOid(), liitteet);
        getModel().setSelectedLiite(null);
    }

    public void removeLiiteFromHakukohde(HakukohdeLiiteViewModel liite) {
        getTarjontaAdminService().poistaHakukohdeLiite(liite.getHakukohdeLiiteId());
        editHakukohdeView.loadLiiteTableWithData();
    }

    public void removeValintakoeFromHakukohde(ValintakoeViewModel valintakoe) {
        getTarjontaAdminService().poistaValintakoe(valintakoe.getValintakoeTunniste());
        editHakukohdeView.loadValintakokees();
    }

    public OrganisaatioDTO getSelectOrganisaatioModel() {
        OrganisaatioDTO organisaatioDTO = organisaatioService.findByOid(getNavigationOrganisation().getOrganisationOid());
        return organisaatioDTO;
    }

    public void saveHakukohdeValintakoe(List<KielikaannosViewModel> kuvaukset) {
        getModel().getSelectedValintaKoe().setSanallisetKuvaukset(kuvaukset);
        getModel().getHakukohde().getValintaKokees().add(getModel().getSelectedValintaKoe());
        List<ValintakoeTyyppi> valintakokeet = new ArrayList<ValintakoeTyyppi>();
        for (ValintakoeViewModel valintakoeViewModel : getModel().getHakukohde().getValintaKokees()) {
            valintakokeet.add(ValintakoeConverter.mapKieliKaannosToValintakoeTyyppi(valintakoeViewModel));
        }

        getTarjontaAdminService().tallennaValintakokeitaHakukohteelle(getModel().getHakukohde().getOid(), valintakokeet);

        getModel().setSelectedValintaKoe(new ValintakoeViewModel());
        editHakukohdeView.loadValintakokees();
        editHakukohdeView.closeValintakoeEditWindow();
    }

    public void closeCancelHakukohteenEditView() {
        editHakukohdeView.closeHakukohdeLiiteEditWindow();
    }

    public void setHakukohteenOletusOsoiteToEmpty() {
        if (getModel().getHakukohde() != null) {
            getModel().getHakukohde().setPostinumero(null);
            getModel().getHakukohde().setPostitoimipaikka("");
            getModel().getHakukohde().setOsoiteRivi1("");
            getModel().getHakukohde().setOsoiteRivi2("");

        }
    }

    public void emptySelectedLiiteOsoite() {
        getSelectedHakuliite().setOsoiteRivi1("");
        getSelectedHakuliite().setOsoiteRivi2("");
        getSelectedHakuliite().setPostinumero(null);
        getSelectedHakuliite().setPostitoimiPaikka("");
    }

    public void setDefaultSelectedLiiteToimitusOsoite() {
        getSelectedHakuliite().setOsoiteRivi1(getModel().getHakukohde().getOsoiteRivi1());
        getSelectedHakuliite().setOsoiteRivi2(getModel().getHakukohde().getOsoiteRivi2());
        getSelectedHakuliite().setPostinumero(getModel().getHakukohde().getPostinumero());
        getSelectedHakuliite().setPostitoimiPaikka(getModel().getHakukohde().getPostitoimipaikka());
    }

    public void saveHakukohteenEditView() {
        saveHakukohdeLiite();
        editHakukohdeView.loadLiiteTableWithData();
        editHakukohdeView.closeHakukohdeLiiteEditWindow();
    }

    public void initHakukohdeForm(PerustiedotView hakuKohdePerustiedotView) {
        this.hakuKohdePerustiedotView = hakuKohdePerustiedotView;

        ListHakuVastausTyyppi haut = getTarjontaPublicService().listHaku(new ListaaHakuTyyppi());

        this.hakuKohdePerustiedotView.initForm(getModel().getHakukohde());
        HakuViewModel hakuView = null;
        if (getModel().getHakukohde() != null && getModel().getHakukohde().getHakuOid() != null) {
            hakuView = getModel().getHakukohde().getHakuOid();
        }
        List<HakuViewModel> foundHaut = new ArrayList<HakuViewModel>();
        for (HakuTyyppi foundHaku : haut.getResponse()) {
            foundHaut.add(new HakuViewModel(foundHaku));
        }

        this.hakuKohdePerustiedotView.addItemsToHakuCombobox(foundHaut);

        if (hakuView != null) {
            getModel().getHakukohde().setHakuOid(hakuView);
            ListaaHakuTyyppi hakuKysely = new ListaaHakuTyyppi();
            hakuKysely.setHakuOid(getModel().getHakukohde().getHakuOid().getHakuOid());
            ListHakuVastausTyyppi hakuVastaus = getTarjontaPublicService().listHaku(hakuKysely);
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
    @Override
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
        //HaeKoulutuksetVastausTyyppi vastaus = getTarjontaPublicService().haeKoulutukset(kysely);

        List<KoulutusOidNameViewModel> koulutusModel = convertKoulutusToNameOidViewModel(getSelectedKoulutukset());//vastaus.getKoulutusTulos());


        CreationDialog<KoulutusOidNameViewModel> dialog = new CreationDialog<KoulutusOidNameViewModel>(koulutusModel, KoulutusOidNameViewModel.class, "HakukohdeCreationDialog.title", "HakukohdeCreationDialog.valitutKoulutuksetOptionGroup");
        List<String> validationMessages = validateKoulutukses(getSelectedKoulutukset());//vastaus.getKoulutusTulos());
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
        HaeKoulutuksetVastausTyyppi vastaus = getTarjontaPublicService().haeKoulutukset(kysely);
        System.out.println("vastaus koko: " + vastaus.getKoulutusTulos().size());
        return validateKoulutukses(vastaus.getKoulutusTulos());
    }

    private List<String> validateKoulutukses(List<KoulutusTulos> koulutukses) {

        List<String> returnVal = new ArrayList<String>();
        List<String> koulutusKoodis = new ArrayList<String>();
        List<String> pohjakoulutukses = new ArrayList<String>();
        for (KoulutusTulos koulutusModel : koulutukses) {
            koulutusKoodis.add(koulutusModel.getKoulutus().getKoulutuskoodi().getUri());
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
            System.out.println("koodi: " + strs[i]);
            if (!strs[0].equals(strs[i])) {
                return false;
            }
        }
        return true;
    }

    public CreationDialog<KoulutusOidNameViewModel> createHakukohdeCreationDialogWithSelectedTarjoaja() {
        HaeKoulutuksetKyselyTyyppi kysely = new HaeKoulutuksetKyselyTyyppi();
        kysely.getTarjoajaOids().add(getNavigationOrganisation().getOrganisationOid());
        HaeKoulutuksetVastausTyyppi vastaus = getTarjontaPublicService().haeKoulutukset(kysely);
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
            nimiOid.setKoulutustyyppi(tulos.getKoulutus().getKoulutustyyppi());
            nimiOid.setKoulutustyyppi(tulos.getKoulutus().getKoulutustyyppi());
            result.add(nimiOid);

        }

        return result;
    }

    public void removeHakukohdeFromKoulutus(String hakukohdeOid) {

        LisaaKoulutusHakukohteelleTyyppi req = new LisaaKoulutusHakukohteelleTyyppi();
        req.setLisaa(false);
        req.setHakukohdeOid(hakukohdeOid);
        req.getKoulutusOids().add(getModel().getKoulutusPerustiedotModel().getOid());
        getTarjontaAdminService().lisaaTaiPoistaKoulutuksiaHakukohteelle(req);
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
        getTarjontaAdminService().lisaaTaiPoistaKoulutuksiaHakukohteelle(req);
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
        getTarjontaAdminService().lisaaTaiPoistaKoulutuksiaHakukohteelle(req);

        showHakukohdeViewImpl(getModel().getHakukohde().getOid());
    }

    /*
     * Show hakukohde overview view
     */
    public void showHakukohdeViewImpl(final String hakukohdeOid) {
        if (hakukohdeOid != null) {
            LueHakukohdeKyselyTyyppi kysely = new LueHakukohdeKyselyTyyppi();
            kysely.setOid(hakukohdeOid);
            LueHakukohdeVastausTyyppi vastaus = getTarjontaPublicService().lueHakukohde(kysely);
            if (vastaus.getHakukohde() != null) {
                //create name string
                getModel().setHakukohde(hakukohdeToDTOConverter.convertDTOToHakukohdeViewMode(vastaus.getHakukohde()));
                final String hakukohdenimi = resolveHakukohdeKoodistonimiFields() + ", " + tilaToLangStr(getModel().getHakukohde().getTila());

                getModel().getHakukohde().setKoulukses(getHakukohdeKoulutukses(getModel().getHakukohde()));
                getModel().getHakukohde().setHakukohdeKoodistoNimi(hakukohdenimi);
                hakukohdeView = new ShowHakukohdeViewImpl(hakukohdenimi, null, null);
                getRootView().changeView(hakukohdeView);

            }
        }
    }

    public List<OrganisaatioPerustietoType> fetchChildOrganisaatios(List<String> organisaatioOids) {

        OrganisaatioSearchCriteriaDTO criteriaDTO = new OrganisaatioSearchCriteriaDTO();

        criteriaDTO.getOidResctrictionList().addAll(organisaatioOids);
        criteriaDTO.setMaxResults(400);

        return getOrganisaatioService().searchBasicOrganisaatios(criteriaDTO);

    }

    private List<KoulutusOidNameViewModel> getHakukohdeKoulutukses(HakukohdeViewModel hakukohdeViewModel) {
        List<KoulutusOidNameViewModel> koulutukses = new ArrayList<KoulutusOidNameViewModel>();

        LueHakukohdeKoulutuksineenKyselyTyyppi kysely = new LueHakukohdeKoulutuksineenKyselyTyyppi();
        kysely.setHakukohdeOid(hakukohdeViewModel.getOid());
        LueHakukohdeKoulutuksineenVastausTyyppi vastaus = getTarjontaPublicService().lueHakukohdeKoulutuksineen(kysely);
        if (vastaus.getHakukohde() != null && vastaus.getHakukohde().getHakukohdeKoulutukses() != null) {

            List<KoulutusKoosteTyyppi> koulutusKoostes = vastaus.getHakukohde().getHakukohdeKoulutukses();
            for (KoulutusKoosteTyyppi koulutusKooste : koulutusKoostes) {
                KoulutusOidNameViewModel koulutus = new KoulutusOidNameViewModel();
                koulutus.setKoulutusNimi(buildKoulutusCaption(koulutusKooste));
                koulutus.setKoulutusOid(koulutusKooste.getKomotoOid());
                koulutus.setKoulutustyyppi(koulutusKooste.getKoulutustyyppi());
                koulutukses.add(koulutus);
            }
        }

        return koulutukses;
    }

    private String buildKoulutusCaption(KoulutusKoosteTyyppi curKoulutus) {
        String caption = getKoulutusNimi(curKoulutus);
        caption += ", " + this.tilaToLangStr(curKoulutus.getTila());
        return caption;
    }

    public void setAllSelectedOrganisaatios(Collection<OrganisaatioPerustietoType> orgs) {
        getTarjoaja().addSelectedOrganisations(orgs);
    }

    public void showKoulutusEditView(Collection<OrganisaatioPerustietoType> orgs, String pohjakoulutusvaatimusUri) {
        getTarjoaja().addSelectedOrganisations(orgs);
        
        //showKoulutustEditView(null, KoulutusActiveTab.PERUSTIEDOT);
        Preconditions.checkNotNull(getTarjoaja().getSelectedOrganisationOid(), "Missing organisation OID.");
        getTarjoaja().setSelectedResultRowOrganisationOid(null);
        getModel().getKoulutusPerustiedotModel().clearModel(DocumentStatus.NEW);
        this.getModel().getKoulutusPerustiedotModel().setPohjakoulutusvaatimus(pohjakoulutusvaatimusUri);
        getModel().setKoulutusLisatiedotModel(new KoulutusLisatiedotModel());
        readNavigationOrgTreeToTarjoaja();
        showEditKoulutusView(null, KoulutusActiveTab.PERUSTIEDOT);
    }
    
    public void showNewKoulutusEditView(final KoulutusActiveTab tab) {

    }
    

    public void copyKoulutusToOrganizations(Collection<OrganisaatioPerustietoType> orgs) {


        getTarjoaja().addSelectedOrganisations(orgs);
        showCopyKoulutusPerustiedotEditView(getModel().getSelectedKoulutusOid(),orgs);
        getModel().getSelectedKoulutukset().clear();
    }

    public void copyLukioKoulutusToOrganization(Collection<OrganisaatioPerustietoType> orgs)  {


        lukioPresenter.showCopyKoulutusView(getModel().getSelectedKoulutusOid(),KoulutusActiveTab.PERUSTIEDOT,orgs);

        getModel().getSelectedKoulutukset().clear();
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

    public String getKoodiNimi(String hakukohdeUri) {
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

    public void showCopyKoulutusPerustiedotEditView(final String koulutusOid, Collection<OrganisaatioPerustietoType> orgs) {


        // If oid of koulutus is provided the koulutus is read from database
        // before opening the KoulutusEditView
        if (koulutusOid != null) {
            copyKoulutusToModel(koulutusOid);

            if (orgs != null && orgs.size() > 0) {

                getModel().getTarjoajaModel().getOrganisationOidNamePairs().clear();
                for (OrganisaatioPerustietoType org:orgs) {
                    OrganisationOidNamePair oidNamePair = new OrganisationOidNamePair();
                    oidNamePair.setOrganisation(org.getOid(),org.getNimiFi());
                    getModel().getTarjoajaModel().getOrganisationOidNamePairs().add(oidNamePair);
                }
            }

            getModel().getKoulutusPerustiedotModel().setTila(TarjontaTila.LUONNOS);

            showEditKoulutusView(koulutusOid, KoulutusActiveTab.PERUSTIEDOT);
            getModel().getKoulutusPerustiedotModel().setOid(null);
        }
    }

    public void showLukioCopyKoulutusPerustiedotView(final String koulutusOid) {
        if (koulutusOid != null) {

        }
    }

    @SuppressWarnings("empty-statement")
    public void showKoulutustEditView(final String koulutusOid, final KoulutusActiveTab tab) {
        // If oid of koulutus is provided the koulutus is read from database
        // before opening the KoulutusEditView
        if (koulutusOid != null) {
            readKoulutusToModel(koulutusOid);
        } else {
            Preconditions.checkNotNull(getTarjoaja().getSelectedOrganisationOid(), "Missing organisation OID.");
            getTarjoaja().setSelectedResultRowOrganisationOid(null);
            getModel().getKoulutusPerustiedotModel().clearModel(DocumentStatus.NEW);
            getModel().setKoulutusLisatiedotModel(new KoulutusLisatiedotModel());
        }
        readNavigationOrgTreeToTarjoaja();
        showEditKoulutusView(koulutusOid, tab);
    }

    public void showLisaaRinnakkainenToteutusEditView(final String koulutusOid) {
        if (koulutusOid != null) {
            readKoulutusToModel(koulutusOid);
            readNavigationOrgTreeToTarjoaja();


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

    public void readNavigationOrgTreeToTarjoaja() {
        readOrgTreeToTarjoaja(getTarjoaja().getSingleSelectRowResultOrganisationOid());
    }

    /*
     * Retrieves the oids of organisaatios that belong to the organisaatio tree of the organisaatio the oid of which is
     * given as a parameter to this method.
     * The retrieved oid list is used when querying for potential yhteyshenkilos of a koulutus object.
     */
    public void readOrgTreeToTarjoaja(String organisaatioOid) {
        Preconditions.checkNotNull(getTarjoaja().getSelectedOrganisationOid(), "Organisation OID cannot be null.");

        LOG.info("getting org oid tree");
        OrganisaatioSearchCriteriaDTO dto = new OrganisaatioSearchCriteriaDTO();
        dto.getOidResctrictionList().add(organisaatioOid);
        try {
        List<OrganisaatioPerustietoType> orgs = getOrganisaatioService().searchBasicOrganisaatios(dto);
        List<String> organisaatioOidTree = new ArrayList<String>();
        for(OrganisaatioPerustietoType perus: orgs) {
            if(perus!=null) {
                organisaatioOidTree.add(perus.getOid());
            }
        }
        getTarjoaja().setOrganisaatioOidTree(organisaatioOidTree);

        } catch (Exception ex) {
            LOG.error("Problem fetching organisaatio oid tree: {}", ex.getMessage());
        }
        LOG.info("getting org oid tree, done.");
//        
//        organisaatioOidTree.add(organisaatioOid);
//        try {
//            List<OrganisaatioDTO> parentOrganisaatios = getOrganisaatioService().findParentsTo(organisaatioOid);
//            for (OrganisaatioDTO curOrg : parentOrganisaatios) {
//                organisaatioOidTree.add(curOrg.getOid());
//            }
//            OrganisaatioSearchOidType childKysely = new OrganisaatioSearchOidType();
//            childKysely.setSearchOid(organisaatioOid);
//            OrganisaatioOidListType childVastaus = getOrganisaatioService().findChildrenOidsByOid(childKysely);
//            for (OrganisaatioOidType curOid : childVastaus.getOrganisaatioOidList()) {
//                organisaatioOidTree.add(curOid.getOrganisaatioOid());
//            }
//        } catch (Exception ex) {
//            LOG.error("Problem fetching organisaatio oid tree: {}", ex.getMessage());
//        }
//
    }

    private void copyKoulutusToModel(final String koulutusOid) {
        LueKoulutusVastausTyyppi lueKoulutus = this.getKoulutusByOid(koulutusOid);
        try {
            KoulutusToisenAsteenPerustiedotViewModel koulutus;


            koulutus = koulutusToDTOConverter.createKoulutusPerustiedotViewModel(getModel(), lueKoulutus, I18N.getLocale());
            readNavigationOrgTreeToTarjoaja();
            getModel().setKoulutusPerustiedotModel(koulutus);
            getModel().setKoulutusLisatiedotModel(koulutusToDTOConverter.createKoulutusLisatiedotViewModel(lueKoulutus));

            //Empty previous Koodisto data from the comboboxes.
            koulutus.getKoulutusohjelmat().clear();
            koulutus.getKoulutuskoodit().clear();
            koulutus.getKoulutuksenHakukohteet().clear();

            //Add selected data to the comboboxes.
            if (koulutus.getKoulutusohjelmaModel() != null && koulutus.getKoulutusohjelmaModel().getKoodistoUri() != null) {

                getModel().getKoulutusPerustiedotModel().getKoulutusohjelmat().add(koulutus.getKoulutusohjelmaModel());
            }
            getModel().getKoulutusPerustiedotModel().setKoulutuslaji(koulutus.getKoulutuslaji());
            getModel().getKoulutusPerustiedotModel().setPohjakoulutusvaatimus(koulutus.getPohjakoulutusvaatimus());

            getModel().getKoulutusPerustiedotModel().setSuunniteltuKesto(koulutus.getSuunniteltuKesto());
            getModel().getKoulutusPerustiedotModel().setSuunniteltuKestoTyyppi(koulutus.getSuunniteltuKestoTyyppi());
            koulutus.getKoulutuskoodit().add(koulutus.getKoulutuskoodiModel());
        } catch (ExceptionMessage ex) {
            LOG.error("Service call failed.", ex);
            showMainDefaultView();
        }
    }

    private void readKoulutusToModel(final String koulutusOid) {
        rawKoulutus = this.getKoulutusByOid(koulutusOid);
        try {
            KoulutusToisenAsteenPerustiedotViewModel koulutus;
            koulutus = koulutusToDTOConverter.createKoulutusPerustiedotViewModel(getModel(), rawKoulutus, I18N.getLocale());

            getModel().setKoulutusPerustiedotModel(koulutus);
            getModel().setKoulutusLisatiedotModel(koulutusToDTOConverter.createKoulutusLisatiedotViewModel(rawKoulutus));

            //Empty previous Koodisto data from the comboboxes.
            koulutus.getKoulutusohjelmat().clear();
            koulutus.getKoulutuskoodit().clear();
            if (rawKoulutus.getHakukohteet() != null) {
                koulutus.getKoulutuksenHakukohteet().clear();
                for (HakukohdeKoosteTyyppi hakukohdeKoosteTyyppi : rawKoulutus.getHakukohteet()) {
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
            HaeHakukohteenValintakokeetHakukohteenTunnisteellaVastausTyyppi vastaus = getTarjontaPublicService().haeHakukohteenValintakokeetHakukohteenTunnisteella(kysely);
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
            HaeHakukohteenLiitteetVastausTyyppi vastaus = getTarjontaPublicService().lueHakukohteenLiitteet(kysely);

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
        LueHakukohteenLiiteTunnisteellaVastausTyyppi vastaus = getTarjontaPublicService().lueHakukohteenLiiteTunnisteella(kysely);
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
        LueHakukohteenValintakoeTunnisteellaVastausTyyppi vastaus = getTarjontaPublicService().lueHakukohteenValintakoeTunnisteella(kysely);
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

    public HakukohdeNameUriModel hakukohdeNameUriModelFromKoodi(KoodiType koodiType) {
        HakukohdeNameUriModel hakukohdeNameUriModel = new HakukohdeNameUriModel();
        hakukohdeNameUriModel.setUriVersio(koodiType.getVersio());
        hakukohdeNameUriModel.setHakukohdeUri(koodiType.getKoodiUri());
        hakukohdeNameUriModel.setHakukohdeArvo(koodiType.getKoodiArvo());

        KoodiMetadataType meta = TarjontaUIHelper.getKoodiMetadataForLanguage(koodiType, I18N.getLocale());
        if (meta != null) {
            hakukohdeNameUriModel.setHakukohdeNimi(meta.getNimi());
        } else {
            //no text found for any language, so only way to show something is to show a koodiuri.
            hakukohdeNameUriModel.setHakukohdeNimi(koodiType.getKoodiUri() + "#" + koodiType.getVersio());
        }
        
        return hakukohdeNameUriModel;
    }

    /**
     * Show hakukohde edit view.
     *
     * @param koulutusOids
     * @param hakukohdeOid
     */
    public void showHakukohdeEditView(List<String> koulutusOids, String hakukohdeOid, List<KoulutusOidNameViewModel> koulutusOidNameViewModels, String selectedTab) {
        LOG.info("showHakukohdeEditView()");
        //After the data has been initialized the form is created
        editHakukohdeView = new EditHakukohdeView();
        if (hakukohdeOid == null) {
            getModel().setHakukohde(HakukohdeViewModel.create());

            if (koulutusOidNameViewModels != null) {
                addKomotoOidsToModel(koulutusOidNameViewModels);
                getModel().getHakukohde().getKoulukses().addAll(koulutusOidNameViewModels);
            }

            if (getModel().getSelectedKoulutukset() != null && !getModel().getSelectedKoulutukset().isEmpty()) {  
                getTarjoaja().setSelectedResultRowOrganisationOid(getModel().getSelectedKoulutukset().get(0).getKoulutus().getKomotoOid());
            } else if (koulutusOids != null && !koulutusOids.isEmpty()) {
                getTarjoaja().setSelectedResultRowOrganisationOid(koulutusOids.get(0));
            }
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
            getModel().setHakukohde(this.hakukohdeToDTOConverter.convertDTOToHakukohdeViewMode(getTarjontaPublicService()
                    .lueHakukohde(kysely).getHakukohde()));
            setKomotoOids(getModel().getHakukohde().getKomotoOids());

            if (getModel().getHakukohde().getHakukohdeNimi() != null) {
                List<KoodiType> koodis = uiHelper.getKoodis(getModel().getHakukohde().getHakukohdeNimi());
                if (koodis != null && koodis.size() > 0) {
                    getModel().getHakukohde().setSelectedHakukohdeNimi(hakukohdeNameUriModelFromKoodi(koodis.get(0)));
                }
            }
            if (getModel().getHakukohde().getKoulukses() == null || getModel().getHakukohde().getKoulukses().size() == 0) {
                getModel().getHakukohde().setKoulukses(getHakukohdeKoulutukses(getModel().getHakukohde()));
            }
        }


        getRootView().changeView(editHakukohdeView);
        //If selected tab is given set it to selected
        if (selectedTab != null && selectedTab.trim().equalsIgnoreCase(TarjontaPresenter.VALINTAKOE_TAB_SELECT)) {
            editHakukohdeView.setValintakokeetTabSelected();
        } else if (selectedTab != null && selectedTab.trim().equalsIgnoreCase(TarjontaPresenter.LIITTEET_TAB_SELECT)) {
            editHakukohdeView.setLiitteetTabSelected();
        }
    }

    private void addKomotoOidsToModel(List<KoulutusOidNameViewModel> koulutukses) {
        for (KoulutusOidNameViewModel koulutus : koulutukses) {
            getModel().getHakukohde().getKomotoOids().add(koulutus.getKoulutusOid());
        }
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
            getModel().setHakukohteet(getTarjontaPublicService().haeHakukohteet(kysely).getHakukohdeTulos());
        } catch (Exception ex) {
            LOG.error("Error in finding hakukokohteet: {}", ex.getMessage());
            getModel().setHakukohteet(new ArrayList<HakukohdeTulos>());
        }
        this.searchResultsView.setResultSizeForHakukohdeTab(getModel().getHakukohteet().size());
        for (HakukohdeTulos curHk : getModel().getHakukohteet()) {
            String hkKey = TarjontaUIHelper.getClosestMonikielinenTekstiTyyppiName(I18N.getLocale(), curHk.getHakukohde().getTarjoaja().getNimi()).getValue();
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
        ListHakuVastausTyyppi vastaus = getTarjontaPublicService().listHaku(haku);
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
        HakukohdeListausTyyppi wtf = new HakukohdeListausTyyppi();
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
    	
    	int removalLaskuri = 0;
        String errorNotes = "";
        for (HakukohdeTulos curHakukohde : getModel().getSelectedhakukohteet()) {
        	String hakukohdeNimi = TarjontaUIHelper.getClosestMonikielinenTekstiTyyppiName(I18N.getLocale(), curHakukohde.getHakukohde().getNimi()).getValue();
        	try {
            	final OrganisaatioContext context = OrganisaatioContext.getContext(curHakukohde.getHakukohde().getTarjoaja().getTarjoajaOid());
    			TarjontaTila tila = curHakukohde.getHakukohde().getTila();
    			
    	        if ((tila.equals(TarjontaTila.VALMIS) || tila.equals(TarjontaTila.LUONNOS)) 
    	        		&& getPermission().userCanDeleteHakukohde(context)) {
    	        	HakukohdeTyyppi hakukohde = new HakukohdeTyyppi();
                    hakukohde.setOid(curHakukohde.getHakukohde().getOid());
    	        	getTarjontaAdminService().poistaHakukohde(hakukohde);
    	        	++removalLaskuri;
    	        } else {
    	        	errorNotes += I18N.getMessage("notification.error.hakukohde.notRemovable", hakukohdeNimi) + "<br/>";
    	        }
            } catch (Throwable e) {
            	
            	if (e.getMessage().contains("fi.vm.sade.tarjonta.service.business.exception.HakukohdeUsedException")) {
                    errorNotes += I18N.getMessage("notification.error.hakukohde.used.multiple", hakukohdeNimi) + "<br/>";
                } else {
                    LOG.error(e.getMessage());
                }
            }
        }

        String notificationMessage = "<br />" + I18N.getMessage("notification.deleted.hakukohteet", removalLaskuri) + "<br />" + errorNotes;
        getModel().getSelectedhakukohteet().clear();

        getHakukohdeListView().reload();
        
        getRootView().getSearchResultsView().getHakukohdeList().getWindow().showNotification(I18N.getMessage("notification.deleted.hakukohteet.title"),
                notificationMessage,
                Window.Notification.TYPE_HUMANIZED_MESSAGE);
        getRootView().getSearchResultsView().getHakukohdeList().closeRemoveDialog();
    }

    public void removeHakukohde(HakukohdeTulos curHakukohde) {
        HakukohdeTyyppi hakukohde = new HakukohdeTyyppi();
        hakukohde.setOid(curHakukohde.getHakukohde().getOid());
        try {
            getTarjontaAdminService().poistaHakukohde(hakukohde);
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
    	
    	int removalLaskuri = 0;
        String errorNotes = "";
        for (KoulutusTulos curKoulutus : getModel().getSelectedKoulutukset()) {
        	String koulutusNimiUri = curKoulutus.getKoulutus().getKoulutustyyppi().equals(KoulutusasteTyyppi.LUKIOKOULUTUS) ?
            		curKoulutus.getKoulutus().getKoulutuskoodi().getUri() 
            		: curKoulutus.getKoulutus().getKoulutusohjelmakoodi().getUri();
        	try {
            	final OrganisaatioContext context = OrganisaatioContext.getContext(curKoulutus.getKoulutus().getTarjoaja().getTarjoajaOid());
    			TarjontaTila tila = curKoulutus.getKoulutus().getTila();
    			
    	        if ((tila.equals(TarjontaTila.VALMIS) || tila.equals(TarjontaTila.LUONNOS)) 
    	        		&& getPermission().userCanDeleteKoulutus(context)) {
    	        	getTarjontaAdminService().poistaKoulutus(curKoulutus.getKoulutus().getKoulutusmoduuliToteutus());
    	        	++removalLaskuri;
    	        } else {
    	        	errorNotes += I18N.getMessage("notification.error.koulutus.notRemovable", uiHelper.getKoodiNimi(koulutusNimiUri)) + "<br/>";
    	        }
            } catch (Throwable e) {
            	
            	if (e.getMessage().contains("fi.vm.sade.tarjonta.service.business.exception.KoulutusUsedException")) {
                    errorNotes += I18N.getMessage("notification.error.koulutus.used.multiple", uiHelper.getKoodiNimi(koulutusNimiUri)) + "<br/>";
                } else {
                    LOG.error(e.getMessage());
                }
            }
        }

        String notificationMessage = "<br />" + I18N.getMessage("notification.deleted.koulutukset", removalLaskuri) + "<br />" + errorNotes;
        getModel().getSelectedKoulutukset().clear();

        // Force UI update.
        getReloadKoulutusListData();
        
        getRootView().getListKoulutusView().getWindow().showNotification(I18N.getMessage("notification.deleted.koulutukset.title"),
                notificationMessage,
                Window.Notification.TYPE_HUMANIZED_MESSAGE);
        getRootView().getListKoulutusView().closeKoulutusDialog();
    }

    /**
     * Saves koulutus/tukinto, other synonyms: LOI, KOMOTO.
     *
     * @param tila (save state)
     * @throws ExceptionMessage
     */
    public void saveKoulutus(SaveButtonState tila) throws ExceptionMessage {
        KoulutusToisenAsteenPerustiedotViewModel koulutusModel = getModel().getKoulutusPerustiedotModel();
        koulutusModel.setViimeisinPaivittajaOid(UserFeature.get().getOid());
        if (koulutusModel.isLoaded()) {
            //update KOMOTO
            OrganisationOidNamePair selectedOrganisation = getModel().getTarjoajaModel().getSelectedOrganisation();
            PaivitaKoulutusTyyppi paivita = koulutusToDTOConverter.createPaivitaKoulutusTyyppi(getModel(), selectedOrganisation, koulutusModel.getOid());
            paivita.setTila(tila.toTarjontaTila(koulutusModel.getTila()));

            koulutusToDTOConverter.validateSaveData(paivita, koulutusModel);
            getTarjontaAdminService().paivitaKoulutus(paivita);
        } else {
            for (OrganisationOidNamePair pair : getTarjoaja().getOrganisationOidNamePairs()) {
                persistKoulutus(koulutusModel, pair, tila);
            }
        }

        this.editKoulutusView.enableLisatiedotTab();
        this.lisatiedotView.getEditKoulutusLisatiedotForm().reBuildTabsheet();
    }

    private void persistKoulutus(KoulutusToisenAsteenPerustiedotViewModel koulutusModel, OrganisationOidNamePair pair, SaveButtonState tila) throws ExceptionMessage {
        //persist new KOMO and KOMOTO
        LisaaKoulutusTyyppi lisaa = koulutusToDTOConverter.createLisaaKoulutusTyyppi(getModel(), pair);
        lisaa.setTila(tila.toTarjontaTila(koulutusModel.getTila()));
        koulutusToDTOConverter.validateSaveData(lisaa, koulutusModel);
        checkKoulutusmoduuli();
        if (checkExistingKomoto(lisaa)) {
            getTarjontaAdminService().lisaaKoulutus(lisaa);
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

        return getTarjontaAdminService().tarkistaKoulutuksenKopiointi(kysely);

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

        if (forceReload || (getNavigationOrganisation().getOrganisationOid() != null && !getModel().isSelectedRootOrganisaatio())) {
            LOG.debug("not root, main view reloaded {} {}", getNavigationOrganisation().getOrganisationOid(), getModel().isSelectedRootOrganisaatio());
            getReloadKoulutusListData();
            getHakukohdeListView().reload();
        } else {
            getRootView().getListKoulutusView().clearAllDataItems();
            getHakukohdeListView().clearAllDataItems();
            this.searchResultsView.setResultSizeForKoulutusTab(0);
            this.searchResultsView.setResultSizeForHakukohdeTab(0);
        }
        getModel().getSelectedKoulutukset().clear();
        getModel().getSelectedhakukohteet().clear();
        this.searchResultsView.getKoulutusList().toggleCreateHakukohdeB(null, false);
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

            getModel().setKoulutukset(this.getTarjontaPublicService().haeKoulutukset(kysely).getKoulutusTulos());

        } catch (Exception ex) {
            LOG.error("Error in finding koulutukset: {}", ex.getMessage());
            getModel().setKoulutukset(new ArrayList<KoulutusTulos>());
        }

        this.searchResultsView.setResultSizeForKoulutusTab(getModel().getKoulutukset().size());
        // Creating the datasource model
        for (KoulutusTulos curKoulutus : getModel().getKoulutukset()) {
            String koulutusKey = TarjontaUIHelper.getClosestMonikielinenTekstiTyyppiName(I18N.getLocale(),curKoulutus.getKoulutus().getTarjoaja().getNimi()).getValue();
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

    public String getOrganisaatioNimiByOid(String organisaatioOid) {
        String vastaus = organisaatioOid;
        try {
            vastaus = OrganisaatioDisplayHelper.getClosest(I18N.getLocale(), this.getOrganisaatioService().findByOid(organisaatioOid));
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

    public List<KoulutusOidNameViewModel> getSelectedKoulutusOidNameViewModels() {
        List<KoulutusOidNameViewModel> koulutukses = new ArrayList<KoulutusOidNameViewModel>();

        if (getModel().getSelectedKoulutukset() != null || !getModel().getSelectedKoulutukset().isEmpty()) {
            koulutukses = convertKoulutusToNameOidViewModel(getModel().getSelectedKoulutukset());
        }

        return koulutukses;
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
            getTarjontaAdminService().poistaKoulutus(koulutus.getKoulutus().getKoulutusmoduuliToteutus());
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
       
        HaeKoulutuksetKyselyTyyppi kysely = new HaeKoulutuksetKyselyTyyppi();
        kysely.getHakukohdeOids().add(oid);
        
        HaeKoulutuksetVastausTyyppi vastaus =  this.getTarjontaPublicService().haeKoulutukset(kysely);
        
        
        /*LueHakukohdeKyselyTyyppi kysely = new LueHakukohdeKyselyTyyppi();
        kysely.setOid(oid);
        
        HakukohdeViewModel hakukohde = this.hakukohdeToDTOConverter
                .convertDTOToHakukohdeViewMode(this.getTarjontaPublicService().lueHakukohde(kysely).getHakukohde());*/
        this._hakukohdeListView.showKoulutuksetForHakukohde(vastaus.getKoulutusTulos());//appendKoulutuksetToList(hakukohde);
    }

    private void addOrganisaatioNameValuePair(String oid, String name) {
        getTarjoaja().getOrganisationOidNamePairs().clear();
        getTarjoaja().setSelectedOrganisation(new OrganisationOidNamePair(oid, name));
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
        NavigationModel navigaatioModel = getNavigationOrganisation();
        navigaatioModel.setOrganisation(organisaatioOid, organisaatioName);

        addOrganisaatioNameValuePair(organisaatioOid, organisaatioName);
        getRootView().getBreadcrumbsView().setOrganisaatio(organisaatioName);

        // Descendant organisation oids to limit the search
        getModel().getSearchSpec().getOrganisaatioOids().clear();
        //getModel().getSearchSpec().getOrganisaatioOids().addAll(findAllChilrenOidsByParentOid(organisaatioOid));
        getModel().getSearchSpec().getOrganisaatioOids().add(organisaatioOid);

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
        getNavigationOrganisation().setOrganisation(getModel().getRootOrganisaatioOid(), NAME_OPH);
        getRootView().getBreadcrumbsView().setOrganisaatio(NAME_OPH);

        getRootView().getOrganisaatiohakuView().clearTreeSelection();

        //Clearing the selected hakukohde and koulutus objects
        getModel().getSelectedhakukohteet().clear();
        getModel().getSelectedKoulutukset().clear();

        getModel().getSearchSpec().setOrganisaatioOids(new ArrayList<String>());

        reloadMainView();
        this.getRootView().getListKoulutusView().toggleCreateKoulutusB(getModel().getRootOrganisaatioOid(), false);
        this.getRootView().getListKoulutusView().toggleCreateHakukohdeB(getModel().getRootOrganisaatioOid(), false);
    }

    /**
     * Gets koulutus by its oid.
     *
     * @param komotoOid - the koulutus oid for which the name is returned
     * @return the koulutus
     */
    public LueKoulutusVastausTyyppi getKoulutusByOid(String komotoOid) {
        Preconditions.checkNotNull(komotoOid, "KOMOTO OID cannot be null.");
        LueKoulutusKyselyTyyppi kysely = new LueKoulutusKyselyTyyppi();
        kysely.setOid(komotoOid);
        LOG.info("getKoulutusByOId");
        LueKoulutusVastausTyyppi vastaus = this.getTarjontaPublicService().lueKoulutus(kysely);
        LOG.info("getKoulutusByOId, done.");

        return vastaus;
    }

    /**
     *
     *
     */
    public void checkKoulutusmoduuli() {
        KoulutusToisenAsteenPerustiedotViewModel model = getModel().getKoulutusPerustiedotModel();

        HaeKoulutusmoduulitKyselyTyyppi kysely =
                Koulutus2asteConverter.mapToHaeKoulutusmoduulitKyselyTyyppi(
                KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS,
                model.getKoulutuskoodiModel(),
                model.getKoulutusohjelmaModel());

        HaeKoulutusmoduulitVastausTyyppi vastaus = this.getTarjontaPublicService().haeKoulutusmoduulit(kysely);

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
        HaeKaikkiKoulutusmoduulitKyselyTyyppi kysely = new HaeKaikkiKoulutusmoduulitKyselyTyyppi();
        kysely.setKoulutustyyppi(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS);
        //TODO: fix this
        //kysely.getOppilaitostyyppiUris().addAll(getOppilaitostyyppiUris());
        HaeKaikkiKoulutusmoduulitVastausTyyppi haeKaikkiKoulutusmoduulit = getTarjontaPublicService().haeKaikkiKoulutusmoduulit(kysely);
        List<KoulutusmoduuliTulos> koulutusmoduuliTulos = haeKaikkiKoulutusmoduulit.getKoulutusmoduuliTulos();

        Set<String> uris = new HashSet<String>();
        List<KoulutusmoduuliKoosteTyyppi> komos = new ArrayList<KoulutusmoduuliKoosteTyyppi>();

        for (KoulutusmoduuliTulos tulos : koulutusmoduuliTulos) {
            komos.add(tulos.getKoulutusmoduuli());
            uris.add(tulos.getKoulutusmoduuli().getKoulutuskoodiUri());
        }
        LOG.debug("KOMOs found {}", komos.size());

        KoulutusToisenAsteenPerustiedotViewModel model = getModel().getKoulutusPerustiedotModel();
        model.setKomos(komos);
        model.createCacheKomos(); //cache komos to map object


        //koodisto service search result remapped to UI model objects.
        List<KoulutuskoodiModel> listaaKoulutuskoodit = kolutusKoodistoConverter.listaaKoulutukses(uris, I18N.getLocale());
        Collections.sort(listaaKoulutuskoodit, new BeanComparator("nimi"));

        model.getKoulutuskoodit().clear();
        model.getKoulutuskoodit().addAll(listaaKoulutuskoodit);
    }


    public List<String> getOppilaitostyyppiUris(String orgOid) {
        final String organisaatioOid = orgOid;
        OrganisaatioDTO selectedOrg = this.getOrganisaatioService().findByOid(organisaatioOid);

        if (selectedOrg == null) {
            throw new RuntimeException("No organisation found by OID " + organisaatioOid + ".");
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

        LOG.debug("TyyppiUris : {}", olTyyppiUris);
        LOG.debug("olTyyppiUris size: {}", olTyyppiUris.size());
        return olTyyppiUris;
    }
    /*
     * Retrieves the list of (koodisto) oppilaitostyyppi uri's matching the currently selected organisaatio.
     */
    public List<String> getOppilaitostyyppiUris() {
        final String organisaatioOid = this.getNavigationOrganisation().getOrganisationOid();
        OrganisaatioDTO selectedOrg = this.getOrganisaatioService().findByOid(organisaatioOid);

        if (selectedOrg == null) {
            throw new RuntimeException("No organisation found by OID " + organisaatioOid + ".");
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

        LOG.debug("TyyppiUris : {}", olTyyppiUris);
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
        OrganisaatioDTO organisaatio = this.getOrganisaatioService().findByOid(oid);
        return organisaatio.getOppilaitosTyyppi();
    }

    /*
     * Gets the list of oppilaitostyyppi uris that match the children of the organisaatio given as parameter.
     */
    private List<String> getChildOrgOlTyyppis(OrganisaatioDTO selectedOrg) {
        List<String> childOlTyyppis = new ArrayList<String>();
        OrganisaatioSearchCriteriaDTO criteria = new OrganisaatioSearchCriteriaDTO();
        criteria.getOidResctrictionList().add(selectedOrg.getOid());
        criteria.setMaxResults(1000);
        List<OrganisaatioPerustietoType> childOrgs = this.getOrganisaatioService().searchBasicOrganisaatios(criteria);
        if (childOrgs != null) {
            for (OrganisaatioPerustietoType curChild : childOrgs) {
                if (curChild.getTyypit().contains(OrganisaatioTyyppi.OPPILAITOS)
                        && !childOlTyyppis.contains(curChild.getOppilaitostyyppi())) {
                    childOlTyyppis.add(curChild.getOppilaitostyyppi());
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
            final String koulutuskoodiUri = model.getKoulutuskoodiModel().getKoodistoUriVersio();

            LOG.debug("Find koulutusohjelma by koulutuskoodi uri : '{}'", koulutuskoodiUri);
            List<KoulutusmoduuliKoosteTyyppi> tyyppis = model.getQuickKomosByKoulutuskoodiUri(koulutuskoodiUri);
            List<KoulutusohjelmaModel> listaaKoulutusohjelmat = kolutusKoodistoConverter.listaaKoulutusohjelmas(tyyppis, I18N.getLocale());

            Collections.sort(listaaKoulutusohjelmat, new BeanComparator("nimi"));
            model.getKoulutusohjelmat().addAll(listaaKoulutusohjelmat);

            //Loading data from the parent tutkinto komo (startDate and koulutusohjelmanValinta).
            loadKoulutusohjelmaLisatiedotData(model.getKoulutuskoodiModel().getKoodistoUriVersio(), model.getPohjakoulutusvaatimus());
        }
    }

    //Prefills the tutkinto komoto (koulutuksenAlkamisPvm, koulutusohjelmanValinta) fields if a tutkinto komoto exists
    private void loadKoulutusohjelmaLisatiedotData(final String koulutuskoodi, String pohjakoulutusvaatimus) {
        LOG.debug("loadtutkintoData, koulutuskoodi: {}, tarjoaja: {}", koulutuskoodi, getTarjoaja());
        HaeKoulutuksetKyselyTyyppi kysely = new HaeKoulutuksetKyselyTyyppi();
        kysely.setKoulutusKoodi(koulutuskoodi);

        /*
         * When use has selected many organisations(example koulutus copy),
         * an organisation OID is taken from the selected result row item, if
         * use has selected only one organisation on dialog, then the OID is
         * taken from the selected organisation.
         */
        kysely.getTarjoajaOids().add(getTarjoaja().getSingleSelectRowResultOrganisationOid());
        HaeKoulutuksetVastausTyyppi vastaus = this.getTarjontaPublicService().haeKoulutukset(kysely);

        if (vastaus.getKoulutusTulos() != null && !vastaus.getKoulutusTulos().isEmpty()) {
            for (KoulutusTulos curTulos : vastaus.getKoulutusTulos()) {
                if (pohjakoulutusvaatimus == null 
                        || (pohjakoulutusvaatimus != null 
                            && pohjakoulutusvaatimus.equals(curTulos.getKoulutus().getPohjakoulutusVaatimus()))) {
                    LueKoulutusKyselyTyyppi lueKysely = new LueKoulutusKyselyTyyppi();
                    lueKysely.setOid(curTulos.getKoulutus().getKomotoOid());
                    LueKoulutusVastausTyyppi lueVastaus = getTarjontaPublicService().lueKoulutus(lueKysely);
                    Date koulutuksenAlkuPvm = lueVastaus.getKoulutuksenAlkamisPaiva() != null ? lueVastaus.getKoulutuksenAlkamisPaiva().toGregorianCalendar().getTime() : null;

                    getModel().getKoulutusPerustiedotModel().setKoulutuksenAlkamisPvm(koulutuksenAlkuPvm);
                    getModel().setKoulutusLisatiedotModel(new KoulutusLisatiedotModel());

                    if (lueVastaus.getKoulutusohjelmanValinta() != null) {
                        for (MonikielinenTekstiTyyppi.Teksti mkt : lueVastaus.getKoulutusohjelmanValinta().getTeksti()) {
                            getModel().getKoulutusLisatiedotModel().getLisatiedot(mkt.getKieliKoodi()).setKoulutusohjelmanValinta(mkt.getValue());
                        }
                    }
                    LOG.debug("going to reload tabsheet");
                }
            }
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

            if (tyyppi == null) {
                LOG.error("No tutkinto & koulutusohjelma, result was null. Search by '{}'" + " and '{}'", koulutuskoodi.getKoodistoUriVersio(), ohjelma.getKoodistoUriVersio());
            }

            kolutusKoodistoConverter.listaa2asteSisalto(koulutuskoodi, ohjelma, tyyppi, I18N.getLocale());
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
        this.getRootView().getListKoulutusView().toggleCreateHakukohdeB(this.getNavigationOrganisation().getOrganisationOid(), !this._model.getSelectedKoulutukset().isEmpty());
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
        List organisaatioOids = getTarjoaja().getOrganisaatioOidTree();

        //If given string is null or empty returning an empty list, i.e. not doing an empty search.
        Preconditions.checkNotNull(organisaatioOids, "A list of organisaatio OIDs cannot be null.");

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
        searchType.getOrganisaatioOids().addAll(organisaatioOids);
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
        HaeKaikkiKoulutusmoduulitKyselyTyyppi kysely = new HaeKaikkiKoulutusmoduulitKyselyTyyppi();
        kysely.getOppilaitostyyppiUris().addAll(oppilaitostyyppiUris);
        return !this.tarjontaPublicService.haeKaikkiKoulutusmoduulit(kysely).getKoulutusmoduuliTulos().isEmpty();
    }

    /**
     * FIXME shouldn't this check that the (given) koulutus is allowed for ALL
     * of the given organisations?
     *
     * Only used from KoulutusKopiointiDialog, safe to modify. Maybe rename to
     * "checkKoulutusCanBeAddedToOrganisations(String koulutusKoodiUri,
     * Collection<> orgs)" ?
     *
     * @param orgs
     * @return
     */
    public boolean checkOrganisaatiosKoulutukses(Collection<OrganisaatioPerustietoType> orgs) {
        for (OrganisaatioPerustietoType org : orgs) {
            List<String> oppilaitosTyyppis = new ArrayList<String>(getOppilaitosTyyppiUrisForOrg(org));
            boolean isEmpty = this.uiHelper.getOlRelatedKoulutuskoodit(oppilaitosTyyppis).isEmpty();
            if (isEmpty) {
                return false;
            }
        }
        return true;
    }

    /**
     * Used only from UusiKoulutusDialog.
     *
     * Make sure selected Organisaatios have at least one common organisation
     * type.
     *
     * @param orgs
     * @return true if there is at leas one common organisaatio type in selected
     * organisation types
     */
    public boolean checkOrganisaatioOppilaitosTyyppimatches(Collection<OrganisaatioPerustietoType> orgs) {

        // Load the list containing the SET of all OppilaitosTyyppi uris.
        List<Set<String>> listOfOppilaitostyyppisLists = new ArrayList<Set<String>>();
        for (OrganisaatioPerustietoType org : orgs) {
            listOfOppilaitostyyppisLists.add(getOppilaitosTyyppiUrisForOrg(org));
        }

        // Initialize intersection with the first set in the list (if any)
        Set<String> intersectionSet = new HashSet<String>();
        if (!listOfOppilaitostyyppisLists.isEmpty()) {
            intersectionSet.addAll(listOfOppilaitostyyppisLists.get(0));
        }

        // Make intersection with all the sets
        for (Set<String> set : listOfOppilaitostyyppisLists) {
            intersectionSet.retainAll(set);
        }

        // If we have any common elements in the set we conlucde that there is a match and reation can proceed.
        return !intersectionSet.isEmpty();
    }

    /**
     * Returns a set of OppilaitosTyyppi's for a given organisation. If org is
     * of type "OPPILAITOS" then we use that orgs type AND (If org is of type
     * "KOULUTUSTOIMIJA" then we use the types for the child organisations. OR
     * If org is of type "OPETUSPISTE" then we use the parents OppilaitosTyyppi
     * uris also)
     *
     * @param org
     * @return
     */
    private Set<String> getOppilaitosTyyppiUrisForOrg(OrganisaatioPerustietoType org) {
        Set<String> oppilaitosTyyppis = new HashSet<String>();

        OrganisaatioDTO selectedOrg = this.getOrganisaatioService().findByOid(org.getOid());

        if (selectedOrg == null) {
            throw new RuntimeException("No organisation found by OID " + this.getNavigationOrganisation().getOrganisationOid() + ".");
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
            oppilaitosTyyppis.addAll(olTyyppis);
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

    private String tilaToLangStr(TarjontaTila tila) {
        return i18n.getMessage(tila.value());
    }

    /**
     * @return the lukioPresenter
     */
    public TarjontaLukioPresenter getLukioPresenter() {
        return lukioPresenter;
    }

    /**
     * @param lukioPresenter the lukioPresenter to set
     */
    public void setLukioPresenter(TarjontaLukioPresenter lukioPresenter) {
        this.lukioPresenter = lukioPresenter;
    }

    /**
     * @return the oidService
     */
    public OIDService getOidService() {
        return oidService;
    }

    /**
     * @return the koodiService
     */
    public KoodiService getKoodiService() {
        return koodiService;
    }

    /**
     * @return the tarjontaAdminService
     */
    public TarjontaAdminService getTarjontaAdminService() {
        return tarjontaAdminService;
    }

    /**
     * @return the tarjontaPublicService
     */
    public TarjontaPublicService getTarjontaPublicService() {
        return tarjontaPublicService;
    }

    /**
     * @return the organisaatioService
     */
    public OrganisaatioService getOrganisaatioService() {
        return organisaatioService;
    }

    /**
     * Get koulutustarjoaja.
     *
     * @return
     */
    public TarjoajaModel getTarjoaja() {
        return getModel().getTarjoajaModel();
    }

    /**
     * Get organisation data for navigation component.
     *
     * @return
     */
    public NavigationModel getNavigationOrganisation() {
        return getModel().getNavigationModel();
    }

    /*
	public void togglePoistaKoulutusB() {
		boolean showPoista = false;
		for (KoulutusTulos curKoul : getSelectedKoulutukset()) {
			final OrganisaatioContext context = OrganisaatioContext.getContext(curKoul.getKoulutus().getTarjoaja().getTarjoajaOid());
			TarjontaTila tila = curKoul.getKoulutus().getTila();
	        if ((tila.equals(TarjontaTila.VALMIS) || tila.equals(TarjontaTila.LUONNOS)) 
	        		&& getPermission().userCanDeleteKoulutus(context)) {
	            showPoista = true;
	        }
		}
		this.getRootView().getListKoulutusView().togglePoistaB(showPoista);
	}
	
	public void togglePoistaHakukohdeB() {
		boolean showPoista = false;
		for (HakukohdeTulos curHakukohde : getSelectedhakukohteet()) {
			final OrganisaatioContext context = OrganisaatioContext.getContext(curHakukohde.getHakukohde().getTarjoaja().getTarjoajaOid());
			TarjontaTila tila = curHakukohde.getHakukohde().getTila();
	        if ((tila.equals(TarjontaTila.VALMIS) || tila.equals(TarjontaTila.LUONNOS)) 
	        		&& getPermission().userCanDeleteHakukohde(context)) {
	            showPoista = true;
	        }
		}
		this.getRootView().getSearchResultsView().getHakukohdeList().togglePoistaB(showPoista);
	}*/

	public void closeKoulutusRemovalDialog() {
		getRootView().getListKoulutusView().closeKoulutusDialog();
	}

	public void closeHakukohdeRemovalDialog() {
		getRootView().getSearchResultsView().getHakukohdeList().closeRemoveDialog();
	}
}
