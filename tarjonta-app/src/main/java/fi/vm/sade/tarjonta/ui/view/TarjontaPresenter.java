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
import fi.vm.sade.tarjonta.ui.model.HakukohdeViewModel;

import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetVastausTyyppi.HakukohdeTulos;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetVastausTyyppi.KoulutusTulos;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.ListHakuVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.ListaaHakuTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.HakukohdeKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.TarjontaModel;
import fi.vm.sade.tarjonta.ui.view.common.OrganisaatiohakuView;
import fi.vm.sade.tarjonta.ui.view.hakukohde.ListHakukohdeView;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.PerustiedotView;
import fi.vm.sade.tarjonta.ui.view.koulutus.ListKoulutusView;
import fi.vm.sade.tarjonta.ui.view.koulutus.ShowKoulutusView;
import fi.vm.sade.vaadin.util.UiUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.xml.ws.soap.SOAPFaultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

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
    private TarjontaAdminService tarjontaAdminService;

    @Autowired(required = true)
    private TarjontaPublicService tarjontaPublicService;

    // UI model
    @Autowired(required = true)
    private TarjontaModel _model;

    // Views this presenter can control
    private TarjontaRootView _rootView;
    private ListHakukohdeView _hakukohdeListView;
    private ListKoulutusView koulutusListView;
    private PerustiedotView hakuKohdePerustiedotView;

    private HakukohdeViewModel hakuKohde;
    private List<HakukohdeViewModel> hakukohteet = new ArrayList<HakukohdeViewModel>();
    private List<HakukohdeViewModel> selectedhakukohteet = new ArrayList<HakukohdeViewModel>();

    @PostConstruct
    public void initialize() {
        LOG.info("initialize(): model={}", getModel());

        // TODO remove me pretty soon please
        if (getModel().getHakukohteet().isEmpty()) {
            createInitialTemporaryDemoDataDForTestingPurposes();
        }
    }

    public void saveHakuKohde() {
        saveHakuKohdePerustiedot();
    }

    public void saveHakuKohdePerustiedot() {
        LOG.info("Form saved");
        hakuKohde.getLisatiedot().addAll(hakuKohdePerustiedotView.getLisatiedot());
        for (KielikaannosViewModel kieli : hakuKohde.getLisatiedot()) {
            LOG.info("KIELI : " + kieli.getKielikoodi() + " TEKSTI : " + kieli.getNimi());
        }

    }

    public void initHakukohdeForm(HakukohdeViewModel model, PerustiedotView hakuKohdePerustiedotView) {
        this.hakuKohdePerustiedotView = hakuKohdePerustiedotView;
        if (model == null) {
            hakuKohde = new HakukohdeViewModel();
        } else {
            hakuKohde = model;
        }
        ListHakuVastausTyyppi haut = tarjontaPublicService.listHaku(new ListaaHakuTyyppi());

        this.hakuKohdePerustiedotView.initForm(hakuKohde);
        this.hakuKohdePerustiedotView.addItemsToHakuCombobox(haut.getResponse());
    }

    private void createInitialTemporaryDemoDataDForTestingPurposes() {
        LOG.error("createInitalData() - DEMO DATA CREATED TO UI! I so hope we are not in production :)");
        /*getModel().getHakukohteet().add(new HakukohdeViewModel("Testi1", "Organisaatio1"));
         geModel().getHakukohteet().add(new HakukohdeViewModel("Testi2", "Organisaatio2"));*/
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
    public void showShowKoulutusView() {
        LOG.info("showShowKoulutusView()");
        ShowKoulutusView view = new ShowKoulutusView("", null);
        _rootView.getAppRootLayout().removeAllComponents();
        _rootView.getAppRootLayout().addComponent(view);
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
            //this.tarjontaService.poistaHakukohde(curHakukohde);
        }
        getModel().getSelectedhakukohteet().clear();

        // Force UI update.
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
        for (KoulutusTulos curHakukohde : getModel().getSelectedKoulutukset()) {
            //this.tarjontaService.poistaHakukohde(curHakukohde);
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
     * Saves haku as ready.
     */
    public void saveKoulutusValmiina() {
        LOG.info("Koulutus tallennettu valmiina");
        LOG.info(getModel().getKoulutusPerustiedotModel().toString());
        LisaaKoulutusTyyppi lisaaKoulutusTyyppi = new LisaaKoulutusTyyppi();
        lisaaKoulutusTyyppi.setKoulutusKoodi(createKoodi("321101"));
        lisaaKoulutusTyyppi.setKoulutusohjelmaKoodi(createKoodi("1603"));
        lisaaKoulutusTyyppi.setOpetusmuoto(createKoodi("opetusmuoto"));
        try {
            tarjontaAdminService.lisaaKoulutus(lisaaKoulutusTyyppi);
        } catch (SOAPFaultException e) {
            LOG.error("Application error - koulutus data persist failed {}", lisaaKoulutusTyyppi, e);
        }
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

    public ListKoulutusView getKoulutusListView() {
        return koulutusListView;
    }

    public void setKoulutusListView(ListKoulutusView listKoulutusView) {
        this.koulutusListView = listKoulutusView;
    }

    /**
     * Helper method that wraps uri string into KoodistoKoodiTyypi. No other attribute populated.
     *
     * @param uri
     * @return
     */
    private static KoodistoKoodiTyyppi createKoodi(String uri) {
        final KoodistoKoodiTyyppi koodi = new KoodistoKoodiTyyppi();
        koodi.setUri(uri);
        return koodi;
    }

    //TODO tähän kutsu koulutusten listaukseen kunhan palvelu on toteutettu
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


}

