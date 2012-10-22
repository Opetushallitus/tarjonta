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

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodistoItemType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.oid.service.types.NodeClassCode;
import fi.vm.sade.tarjonta.ui.model.HakukohdeViewModel;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetVastausTyyppi.HakukohdeTulos;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetVastausTyyppi.KoulutusTulos;
import fi.vm.sade.tarjonta.service.types.ListHakuVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.ListaaHakuTyyppi;
import fi.vm.sade.tarjonta.service.types.LueHakukohdeKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.HakukohdeTyyppi;
import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
import fi.vm.sade.tarjonta.ui.enums.UserNotification;
import fi.vm.sade.tarjonta.ui.model.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.TarjontaModel;
import fi.vm.sade.tarjonta.ui.view.common.OrganisaatiohakuView;
import fi.vm.sade.tarjonta.ui.view.hakukohde.EditHakukohdeView;
import fi.vm.sade.tarjonta.ui.view.hakukohde.ListHakukohdeView;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.PerustiedotView;
import fi.vm.sade.tarjonta.ui.view.koulutus.EditKoulutusPerustiedotToinenAsteView;
import fi.vm.sade.tarjonta.ui.view.koulutus.ListKoulutusView;
import fi.vm.sade.tarjonta.ui.view.koulutus.ShowKoulutusView;
import fi.vm.sade.vaadin.util.UiUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import fi.vm.sade.tarjonta.ui.helper.conversion.HakukohdeViewModelToDTOConverter;
import fi.vm.sade.tarjonta.ui.model.KoulutusohjelmaModel;

/**
 * This class is used to control the "tarjonta" UI.
 *
 * @author mlyly
 */
@Component
@Configurable(preConstruction = false)
public class TarjontaPresenter {

    private static final Logger LOG = LoggerFactory.getLogger(TarjontaPresenter.class);
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
    private TarjontaModel _model;
    @Autowired(required = true)
    HakukohdeViewModelToDTOConverter hakukohdeToDTOConverter;
    // Views this presenter can control
    private TarjontaRootView _rootView;
    private ListHakukohdeView _hakukohdeListView;
    private ListKoulutusView koulutusListView;
    private PerustiedotView hakuKohdePerustiedotView;

    public void saveHakuKohde() {
        saveHakuKohdePerustiedot();
    }

    public void saveHakuKohdePerustiedot() {
        LOG.info("Form saved");
        getModel().getHakukohde().getLisatiedot().addAll(hakuKohdePerustiedotView.getLisatiedot());
        tarjontaAdminService.lisaaHakukohde(hakukohdeToDTOConverter.convertHakukohdeViewModelToDTO(getModel().getHakukohde()));

    }

    public void setTunnisteKoodi(String hakukohdeNimiUri) {
        //TODO add search from koodisto


        List<KoodiType> koodit = koodiService.searchKoodis(KoodiServiceSearchCriteriaBuilder.latestAcceptedKoodiByUri(hakukohdeNimiUri));
        KoodiType foundKoodi = null;
        for (KoodiType koodi : koodit) {
            foundKoodi = koodi;
        }
        if (foundKoodi != null) {
            String koodi = foundKoodi.getMetadata().get(0).getNimi();
            hakuKohdePerustiedotView.setTunnisteKoodi(koodi);
        }
    }

    public void initHakukohdeForm(HakukohdeViewModel model, PerustiedotView hakuKohdePerustiedotView) {
        this.hakuKohdePerustiedotView = hakuKohdePerustiedotView;
        if (model == null) {
            getModel().setHakukohde(new HakukohdeViewModel());

        } else {
            getModel().setHakukohde(model);
            setTunnisteKoodi(getModel().getHakukohde().getHakukohdeNimi());

        }
        ListHakuVastausTyyppi haut = tarjontaPublicService.listHaku(new ListaaHakuTyyppi());

        this.hakuKohdePerustiedotView.initForm(getModel().getHakukohde());
        this.hakuKohdePerustiedotView.addItemsToHakuCombobox(haut.getResponse());

    }

    /**
     * Show main default view
     *
     * TODO REMOVE UI CODE FROM PRESENTER!
     */
    public void showMainDefaultView() {
        LOG.info("showMainDefaultView()");

        OrganisaatiohakuView organisaatiohakuView = new OrganisaatiohakuView(null);

        _rootView.getAppRootLayout().removeAllComponents();
        _rootView.getAppRootLayout().addComponent(organisaatiohakuView);

        VerticalLayout vrightLayout = UiUtil.verticalLayout();
        vrightLayout.setHeight(-1, VerticalLayout.UNITS_PIXELS);
        vrightLayout.addComponent(_rootView.getBreadcrumbsView());
        vrightLayout.addComponent(_rootView.getSearchSpesificationView());
        vrightLayout.addComponent(_rootView.getSearchResultsView());
        organisaatiohakuView.addComponent(vrightLayout);
        organisaatiohakuView.setExpandRatio(vrightLayout, 1f);
    }

    /**
     * Show koulutus overview view.
     */
    public void showShowKoulutusView(String koulutusOid) {
        LOG.info("showShowKoulutusView()");

        //If oid of koulutus is provided the koulutus is read from database before opening the ShowKoulutusView
        if (koulutusOid != null) {
            LueKoulutusKyselyTyyppi koulutusKysely = new LueKoulutusKyselyTyyppi();
            koulutusKysely.setOid(koulutusOid);
            getModel().setKoulutusPerustiedotModel(new KoulutusToisenAsteenPerustiedotViewModel(DocumentStatus.LOADED,this.tarjontaPublicService.lueKoulutus(koulutusKysely)));//new  this.tarjontaPublicService.lueKoulutus(koulutusKysely));
        }
        ShowKoulutusView view = new ShowKoulutusView("", null);
        _rootView.getAppRootLayout().removeAllComponents();
        _rootView.getAppRootLayout().addComponent(view);
    }

    public void setKomotoOids(List<String> komotoOids) {
        _model.getHakukohde().setKomotoOids(komotoOids);
    }

    /**
     * Show koulutus edit view.
     */
    public void showKoulutusEditView(String koulutusOid) {
        LOG.info("showKoulutusEditView()");

        //If oid of koulutus is provided the koulutus is read from database before opening the KoulutusEditView
        if (koulutusOid != null) {
            LueKoulutusKyselyTyyppi koulutusKysely = new LueKoulutusKyselyTyyppi();
            koulutusKysely.setOid(koulutusOid);
            getModel().setKoulutusPerustiedotModel(new KoulutusToisenAsteenPerustiedotViewModel(DocumentStatus.LOADED,this.tarjontaPublicService.lueKoulutus(koulutusKysely)));
        }

        //Clearing the layout from previos content
        this._rootView.getAppRootLayout().removeAllComponents();

        //Adding the form
        VerticalLayout vl = UiUtil.verticalLayout();
        vl.setHeight(-1, VerticalLayout.UNITS_PIXELS);
        vl.addComponent(_rootView.getBreadcrumbsView());
        vl.addComponent(new EditKoulutusPerustiedotToinenAsteView());
        _rootView.getAppRootLayout().addComponent(vl);
        _rootView.getAppRootLayout().setExpandRatio(vl, 1f);
    }

    /**
     * Show hakukohde edit view.
     *
     * @param koulutusOids
     * @param hakukohdeOid
     */
    public void showHakukohdeEditView(List<String> koulutusOids, String hakukohdeOid) {
        LOG.info("showHakukohdeEditView()");

        //If a list of koulutusOids is provided they are set in the model
        //These koulutus objects will be published in the created hakukohde
        if (koulutusOids != null) {
            setKomotoOids(koulutusOids);

        } //if a hakukohdeOid is provided the hakukohde is read from the database
        else if (hakukohdeOid != null) {
            LueHakukohdeKyselyTyyppi kysely = new LueHakukohdeKyselyTyyppi();
            kysely.setOid(hakukohdeOid);
            _model.setHakukohde(this.hakukohdeToDTOConverter.convertDTOToHakukohdeViewMode(tarjontaPublicService.lueHakukohde(kysely).getHakukohde()));
            setKomotoOids(_model.getHakukohde().getKomotoOids());
        }

        //After the data has been initialized the form is created
        EditHakukohdeView editHakukohdeView = new EditHakukohdeView();

        //Clearing the layout from previos content
        this._rootView.getAppRootLayout().removeAllComponents();

        //Adding the form
        VerticalLayout vl = UiUtil.verticalLayout();
        vl.setHeight(-1, VerticalLayout.UNITS_PIXELS);
        vl.addComponent(_rootView.getBreadcrumbsView());
        vl.addComponent(editHakukohdeView);
        _rootView.getAppRootLayout().addComponent(vl);
        _rootView.getAppRootLayout().setExpandRatio(vl, 1f);

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
        getModel().setHakukohteet(tarjontaPublicService.haeHakukohteet(new HaeHakukohteetKyselyTyyppi()).getHakukohdeTulos());
        for (HakukohdeTulos curHk : getModel().getHakukohteet()) {
            String hkKey = curHk.getKoulutus().getTarjoaja();
            if (!map.containsKey(hkKey)) {
                LOG.info("Adding a new key to the map: " + hkKey);
                List<HakukohdeTulos> hakukohteetM = new ArrayList<HakukohdeTulos>();
                hakukohteetM.add(curHk);
                map.put(hkKey, hakukohteetM);
            } else {
                map.get(hkKey).add(curHk);
            }
        }

        return map;
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
        for (HakukohdeTulos curHakukohde : getModel().getSelectedhakukohteet()) {
            HakukohdeTyyppi hakukohde = new HakukohdeTyyppi();
            hakukohde.setOid(curHakukohde.getHakukohde().getOid());
            tarjontaAdminService.poistaHakukohde(hakukohde);
        }
        getModel().getSelectedhakukohteet().clear();

        // Force UI update.
        getHakukohdeListView().reload();
    }

    public void removeHakukohde(HakukohdeTulos curHakukohde) {
        HakukohdeTyyppi hakukohde = new HakukohdeTyyppi();
        hakukohde.setOid(curHakukohde.getHakukohde().getOid());
        tarjontaAdminService.poistaHakukohde(hakukohde);
        getHakukohdeListView().reload();
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
        getKoulutusListView().reload();
    }

    public void saveKoulutusLuonnoksenaModel() {
        LOG.info("Koulutus tallennettu luonnoksena");
        LOG.info(getModel().getKoulutusPerustiedotModel().toString());
    }

    /**
     * Saves koulutus as ready.
     */
    public void saveKoulutusValmiina() throws ExceptionMessage {
        KoulutusToisenAsteenPerustiedotViewModel model = getModel().getKoulutusPerustiedotModel();
        LOG.debug("Try to persist KoulutusTyyppi - "
                + "koulutuskoodi : '{}', "
                + "koulutusohjelmakoodi : '{}'",
                model.getKoulutusKoodi(),
                model.getKoulutusohjema().getKoodiUri());
        
        //Requested new id form Oid Service.
        final String newOid = oidService.newOid(NodeClassCode.TEKN_5);
        LOG.debug("Requested new OID : {}", newOid);
        LOG.debug("Output of data model : {}", model);
        
        tarjontaAdminService.lisaaKoulutus(model.mapToLisaaKoulutusTyyppi(newOid));
    }

    /**
     * @return the koulutusYhteistietoModel
     */
    public KoulutusToisenAsteenPerustiedotViewModel getKoulutusToisenAsteenPerustiedotViewModel() {
        return getModel().getKoulutusPerustiedotModel();
    }

    /**
     * Get UI model. TarjontaModel is initialized and injected by Spring.
     *
     * @return
     */
    public TarjontaModel getModel() {
        if (_model == null) {
            LOG.warn("NOW THIS SHOLD NEVER HAPPEN... TarjontaModel was null (should be autowired from session...) - creating empty model!");
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

    /**
     * Gets the list view of koulutus objects.
     * @return the koulutus list view
     */
    public ListKoulutusView getKoulutusListView() {
        return koulutusListView;
    }

    /**
     * Sets the list view of koulutus objects
     * @param listKoulutusView - the list view of koulutus objects to set
     */
    public void setKoulutusListView(ListKoulutusView listKoulutusView) {
        this.koulutusListView = listKoulutusView;
    }

    /**
     * Retrieves the koulutus objects for ListKoulutusView.
     *
     * @return the koulutus objects
     */
    public Map<String, List<KoulutusTulos>> getKoulutusDataSource() {
        Map<String, List<KoulutusTulos>> map = new HashMap<String, List<KoulutusTulos>>();
        getModel().setKoulutukset(tarjontaPublicService.haeKoulutukset(new HaeKoulutuksetKyselyTyyppi()).getKoulutusTulos());
        for (KoulutusTulos curKoulutus : getModel().getKoulutukset()) {
            String koulutusKey = curKoulutus.getKoulutus().getTarjoaja();
            if (!map.containsKey(koulutusKey)) {
                LOG.info("Adding a new key to the map: " + koulutusKey);
                List<KoulutusTulos> koulutuksetM = new ArrayList<KoulutusTulos>();
                koulutuksetM.add(curKoulutus);
                map.put(koulutusKey, koulutuksetM);
            } else {
                map.get(koulutusKey).add(curKoulutus);
            }
        }

        return map;
    }

    /**
     * Gets the oids of the selectd koulutuses.
     *
     * @return the oids
     */
    public List<String> getSelectedKoulutusOids() {
        List<String> kOids = new ArrayList<String>();
        for (KoulutusTulos curKoul : this._model.getSelectedKoulutukset()) {
            kOids.add(curKoul.getKoulutus().getKoulutusmoduuliToteutus());
        }
        return kOids;
    }

    /**
     * Removal of a komoto object.
     *
     * @param koulutus
     */
    public void removeKoulutus(KoulutusTulos koulutus) {
        tarjontaAdminService.poistaKoulutus(koulutus.getKoulutus().getKoulutusmoduuliToteutus());
        this.koulutusListView.reload();
    }

    public void searchKoulutusOhjelmakoodit(final String koodistoUri) {
        if (koodistoUri == null) {
            throw new RuntimeException("Application error - koulutus/tutkinto Koodisto URI cannot be null.");
        }

        final List<KoodiType> searchKoodis = koodiService.listKoodiByRelation(koodistoUri, true, SuhteenTyyppiType.SISALTYY);

        if (searchKoodis.size() == 0) {
            LOG.debug("koodi with URL " + koodistoUri + " was not found.");
        }

        Set<KoulutusohjelmaModel> koulutusohjemaResultSet = new HashSet<KoulutusohjelmaModel>();

        for (KoodiType type : searchKoodis) {
            String name = null;

            if (LOG.isDebugEnabled()) {
                LOG.debug("List basic data : {}, {}", type.getKoodiArvo(), type.getKoodiUri());
            }

            if (LOG.isDebugEnabled()) {
                for (KoodistoItemType t : type.getKoodistos()) {
                    LOG.debug("List included koodistos : {}, {}", t.getKoodistoUri(), t.getKoodistoVersio());
                }
            }

            for (KoodiMetadataType m : type.getMetadata()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("List included metadata : " + m.getKuvaus() + " ," + m.getLyhytNimi() + " ," + m.getKasite() + " ," + m.getSisaltaaKoodiston() + " ," + m.getSisaltaaMerkityksen() + " ," + m.getNimi() + " ," + m.getHuomioitavaKoodi());
                }
                if (m.getNimi() != null) {
                    name = m.getNimi();
                }
            }

            //filter all invalid koodisto items
            if (type.getKoodiUri() != null && name != null && type.getKoodiArvo() != null) {
                koulutusohjemaResultSet.add(new KoulutusohjelmaModel(type.getKoodiUri(), type.getKoodiArvo(), name));
            }

            LOG.debug("Result : {}", koulutusohjemaResultSet);


            getModel().getKoulutusPerustiedotModel().setKoodistoKoulutusohjelma(koulutusohjemaResultSet);
        }
    }

    /*
     * A simple notification helper method.
     */
    public void showNotification(final UserNotification msg) {
        LOG.info("Show user notification - type {}, value {}", msg, msg.getInfo());
        if (msg != null && _rootView != null) {
            _rootView.showNotification(msg.getInfo());
        } else {
            LOG.error("Application error - an unknown problem with UI notification. Value : {}", msg);
        }
    }

    /**
     * Navigate to the ShowHakukohdeView for the hakukohde with oid given as parameter.
     * @param oid the oid given
     */
	public void showShowHakukohdeView(String oid) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Shows the koulutus objects for a hakukohde in the ListHakukohdeView.
	 * @param oid
	 */
	public void showKoulutuksetForHakukohde(String oid) {
		LueHakukohdeKyselyTyyppi kysely = new LueHakukohdeKyselyTyyppi();
		kysely.setOid(oid);
		HakukohdeViewModel hakukohde = this.hakukohdeToDTOConverter.convertDTOToHakukohdeViewMode(this.tarjontaPublicService.lueHakukohde(kysely).getHakukohde());
		this._hakukohdeListView.appendKoulutuksetToList(hakukohde);
		
	}
	
	/**
	 * Selects the organisaatio in tarjonta, by setting the organisaatio name in breadcrumb
	 * and setting the organisaatioOid and organisaatioNimi in tarjonta model.
	 * Enables the create koulutus button in koulutus list view.
	 * 
	 * @param organisaatioOid - the organisaatio oid to select
	 * @param organisaatioName - the organisaatio name to select
	 */
	public void selectOrganisaatio(String organisaatioOid, String organisaatioName) {
		_rootView.getBreadcrumbsView().removeAllComponents();
		_rootView.getBreadcrumbsView().addComponent(new Label(organisaatioName));
		_model.setOrganisaatioOid(organisaatioOid);
		_model.setOrganisaatioName(organisaatioName);
		this.getKoulutusListView().toggleCreateKoulutusB(true);
	}


	/**
	 * Gets the name of koulutus by its oid.
	 * @param komotoOid - the koulutus oid for which the name is returned
	 * @return the name of the koulutus
	 */
	public String getKoulutusNimiByOid(String komotoOid) {
		for(KoulutusTulos curKoulutus : getModel().getKoulutukset()) {
			if (curKoulutus.getKoulutus().getKoulutusmoduuliToteutus().equals(komotoOid)) {
				return curKoulutus.getKoulutus().getNimi();
			}
		}
		return komotoOid;
	}
}
