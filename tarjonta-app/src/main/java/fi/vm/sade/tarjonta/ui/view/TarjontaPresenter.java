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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import fi.vm.sade.tarjonta.ui.model.KoulutusToisenAsteenPerustiedotViewModel;

import fi.vm.sade.tarjonta.ui.model.HakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusYhteyshenkiloViewModel;
import fi.vm.sade.tarjonta.ui.model.TarjontaModel;
import fi.vm.sade.tarjonta.ui.view.common.OrganisaatiohakuView;
import fi.vm.sade.tarjonta.ui.view.hakukohde.ListHakukohdeView;
import fi.vm.sade.tarjonta.ui.view.koulutus.EditKoulutusPerustiedotToinenAsteView;

import fi.vm.sade.vaadin.util.UiUtil;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

/**
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
    private ListHakukohdeView hakukohdeListView;
    private KoulutusToisenAsteenPerustiedotViewModel koulutusYhteistietoModel;
    private EditKoulutusPerustiedotToinenAsteView koulutusPerustiedot;
    private List<HakukohdeViewModel> hakukohteet = new ArrayList<HakukohdeViewModel>();
    private List<HakukohdeViewModel> selectedhakukohteet = new ArrayList<HakukohdeViewModel>();

    @PostConstruct
    public void initialize() {
        LOG.info("initialize(): model={}", _model);
        if (hakukohteet.size() == 0) {
            createInitialData();
        }
    }

    private void createInitialData() {
        hakukohteet.add(new HakukohdeViewModel("Testi1", "Organisaatio1"));
        hakukohteet.add(new HakukohdeViewModel("Testi2", "Organisaatio2"));
    }

    /**
     * Show main default view
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
        LOG.info("doSearch(): searchSpec={}", _model.getSearchSpec());
    }

    public ListHakukohdeView getHakukohdeListView() {
        return hakukohdeListView;
    }

    public void showKoulutusPerustiedotToinenAsteView() {
        _rootView.getSearchResultsView();
    }

    public void initKoulutusYhteystietoModel() {
        koulutusYhteistietoModel = new KoulutusToisenAsteenPerustiedotViewModel();
    }

    public void setHakukohdeListView(ListHakukohdeView hakukohdeListView) {
        this.hakukohdeListView = hakukohdeListView;
    }

    public Map<String, List<HakukohdeViewModel>> getHakukohdeDataSource() {
        Map<String, List<HakukohdeViewModel>> map = new HashMap<String, List<HakukohdeViewModel>>();
        for (HakukohdeViewModel curHk : hakukohteet) {
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

        return selectedhakukohteet;
    }

    /**
     * Removes the selected hakukohde objects from the database.
     */
    public void removeSelectedHakukohteet() {
        for (HakukohdeViewModel curHakukohde : selectedhakukohteet) {
            //this.tarjontaService.poistaHakukohde(curHakukohde);
        }
        selectedhakukohteet.clear();
        this.hakukohdeListView.reload();
    }

    /**
     * @return the koulutusYhteistietoModel
     */
    public KoulutusToisenAsteenPerustiedotViewModel getKoulutusToisenAsteenPerustiedotViewModel() {
        return koulutusYhteistietoModel;
    }

    public TarjontaModel getModel() {
        return _model;
    }

    public void setTarjontaWindow(TarjontaRootView rootView) {
        _rootView = rootView;
    }

    public TarjontaRootView getRootView() {
        return _rootView;
    }

    public boolean isShowIdentifier() {
        return _model.isShowIdentifier();
    }

    public String getIdentifier() {
        return _model.getIdentifier();
    }
}
