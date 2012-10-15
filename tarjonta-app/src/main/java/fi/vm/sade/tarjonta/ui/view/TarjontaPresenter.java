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
import fi.vm.sade.tarjonta.ui.model.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.TarjontaModel;
import fi.vm.sade.tarjonta.ui.view.common.OrganisaatiohakuView;
import fi.vm.sade.tarjonta.ui.view.hakukohde.ListHakukohdeView;
import fi.vm.sade.tarjonta.ui.view.koulutus.EditKoulutusPerustiedotToinenAsteView;
import fi.vm.sade.vaadin.util.UiUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
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

    @Autowired(required = true)
    private TarjontaModel _model;

    private TarjontaRootView _rootView;
    private ListHakukohdeView _hakukohdeListView;
    // private EditKoulutusPerustiedotToinenAsteView _koulutusPerustiedotView;

    @PostConstruct
    public void initialize() {
        LOG.info("initialize(): model={}", getModel());

        // TODO remove me pretty soon please
        if (getModel().getHakukohteet().isEmpty()) {
            createInitialTemporaryDemoDataDForTestingPurposes();
        }
    }

    private void createInitialTemporaryDemoDataDForTestingPurposes() {
        LOG.error("createInitalData() - DEMO DATA CREATED TO UI! I so hope we are not in production :)");
        getModel().getHakukohteet().add(new HakukohdeViewModel("Testi1", "Organisaatio1"));
        getModel().getHakukohteet().add(new HakukohdeViewModel("Testi2", "Organisaatio2"));
    }

    /**
     * Show main default view
     *
     * TODO REMOVE UI CODE FROM PRESENTER!
     */
    public void showMainDefaultView() {
        LOG.info("showMainDefaultView()");

        OrganisaatiohakuView organisaatiohakuView = new OrganisaatiohakuView(null);
        _rootView.getAppRootLayout().addComponent(organisaatiohakuView);
        VerticalLayout vl = UiUtil.verticalLayout();
        vl.setHeight(-1, VerticalLayout.UNITS_PIXELS);
        vl.addComponent(_rootView.getBreadcrumbsView());
        vl.addComponent(_rootView.getSearchSpesificationView());
        vl.addComponent(_rootView.getSearchResultsView());
        organisaatiohakuView.addComponent(vl);
        organisaatiohakuView.setExpandRatio(vl, 1f);
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

    public Map<String, List<HakukohdeViewModel>> getHakukohdeDataSource() {
        Map<String, List<HakukohdeViewModel>> map = new HashMap<String, List<HakukohdeViewModel>>();
        for (HakukohdeViewModel curHk : getModel().getHakukohteet()) {
            String hkKey = curHk.getOrganisaatioOid();
            if (!map.containsKey(hkKey)) {
                LOG.info("Adding a new key to the map: " + hkKey);
                List<HakukohdeViewModel> hakukohteetM = new ArrayList<HakukohdeViewModel>();
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
    public List<HakukohdeViewModel> getSelectedhakukohteet() {
        return getModel().getSelectedhakukohteet();
    }

    /**
     * Removes the selected hakukohde objects from the database.
     */
    public void removeSelectedHakukohteet() {
        for (HakukohdeViewModel curHakukohde : getModel().getSelectedhakukohteet()) {
            //this.tarjontaService.poistaHakukohde(curHakukohde);
        }
        getModel().getSelectedhakukohteet().clear();

        // Force UI update.
        getHakukohdeListView().reload();
    }

    /**
     * @return the koulutusYhteistietoModel
     */
    public KoulutusToisenAsteenPerustiedotViewModel getKoulutusToisenAsteenPerustiedotViewModel() {
        return getModel().getKoulutusYhteistietoModel();
    }

    /**
     * Get UI model.
     * TarjontaModel is initialized and injected by Spring.
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
     * If true (read from model, value set from application property "common.showAppIdentifier")
     * UI should show app identifier so that testers know what version was deployed.
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
}
