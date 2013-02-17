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

import com.vaadin.ui.Button;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.types.MonikielinenMetadataTyyppi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.vm.sade.tarjonta.service.types.SisaltoTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.ui.enums.MetaCategory;
import fi.vm.sade.tarjonta.ui.enums.UserNotification;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.valinta.ValintaModel;
import fi.vm.sade.tarjonta.ui.model.valinta.ValintaperusteModel;
import fi.vm.sade.tarjonta.ui.service.AppPermissionService;
import fi.vm.sade.tarjonta.ui.service.TarjontaPermissionService;
import fi.vm.sade.tarjonta.ui.view.ValintaperustekuvausRootView;
import fi.vm.sade.tarjonta.ui.view.valinta.SaveDialogView;
import fi.vm.sade.tarjonta.ui.view.valinta.ValintaperusteMainView;
import fi.vm.sade.vaadin.constants.StyleEnum;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Presenter for searching, creating, editing, and viewing Haku objects.
 *
 * @author markus
 *
 */
public class ValintaPresenter implements CommonPresenter {

    private static final Logger LOG = LoggerFactory.getLogger(ValintaPresenter.class);
    private ValintaperustekuvausRootView rootView;
    @Autowired(required = true)
    private transient UiBuilder uiBuilder;
    private ValintaModel model;
    @Autowired(required = true)
    private TarjontaPermissionService permission;
    @Autowired(required = true)
    private TarjontaAdminService tarjontaAdminService;
    @Autowired(required = true)
    private TarjontaUIHelper tarjotaHelper;

    public ValintaPresenter() {
    }

    public void setRootView(ValintaperustekuvausRootView rootView) {
        this.rootView = rootView;
    }

    public ValintaperustekuvausRootView getRootView() {
        return rootView;
    }

    public void showMainView() {
        showValintaperustekuvaus();
    }

    public void showValintaperustekuvaus() {
        initValintaperusteModel(MetaCategory.SORA_KUVAUS);
        initValintaperusteModel(MetaCategory.VALINTAPERUSTEKUVAUS);

        ValintaperusteMainView view = new ValintaperusteMainView(this, uiBuilder);
        getRootView().addToWin(view);
    }

    @Override
    public boolean isSaveButtonEnabled(String oid, SisaltoTyyppi sisalto, TarjontaTila... requiredState) {
        return true;
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

    @Override
    public void showMainDefaultView() {
        getRootView();
    }

    @Override
    public AppPermissionService getPermission() {
        return permission;
    }

    @Override
    public void changeStateToCancelled(String oid, SisaltoTyyppi sisalto) {
        //Not needed, Leave method body empty.
    }

    @Override
    public void changeStateToPublished(String oid, SisaltoTyyppi sisalto) {
        //Not needed, Leave method body empty.
    }

    /**
     * @return the model
     */
    @Override
    public ValintaModel getModel() {
        if (model == null) {
            model = new ValintaModel();
        }

        return model;
    }

    public void initValintaperusteModel(final MetaCategory metaCategory) {
        initValintaperusteModel(metaCategory, null);
    }

    private void initValintaperusteModel(final MetaCategory metaCategory, final String uri) {
        getModel().getMap().put(metaCategory, new ValintaperusteModel(uri));
    }

    /**
     * Remove/delete a language by given koodi uri.
     *
     * @param metaCategory
     * @param selectedUri
     * @param removedLanguages
     */
    public void remove(final MetaCategory metaCategory, final String selectedUri, final Set<String> removedLanguages) {
        final String category = metaCategory.toString();

        for (String kieli : removedLanguages) {
            if (kieli != null) {
                LOG.debug("Remove language : {}", kieli);
                tarjontaAdminService.tallennaMetadata(selectedUri, category, kieli, null);
            }
        }
    }

    /**
     * Save form data.
     *
     * @param metaCategory
     */
    public void save(final MetaCategory metaCategory) {
        final ValintaperusteModel model = getValintaperustemodel(metaCategory);
        final String selectedUri = model.getSelectedUri();
        final String category = metaCategory.toString();

        if (selectedUri == null) {
            throw new RuntimeException("Meta data key is required!");
        }

        for (KielikaannosViewModel kieli : model.getKuvaus()) {
            tarjontaAdminService.tallennaMetadata(selectedUri, category, kieli.getKielikoodi(), kieli.getNimi());
        }
    }

    /**
     * Load selected metadata to valinta model.
     *
     * @param metaCategory
     * @param selectedUri
     */
    public void load(final MetaCategory metaCategory, String selectedUri) {
        if (selectedUri == null) {
            throw new RuntimeException("Meta data key is required!");
        }

        //init model
        initValintaperusteModel(metaCategory, selectedUri);
        final ValintaperusteModel valintaperusteModel = getValintaperustemodel(metaCategory);

        //load metadata to map object
        loadMetaDataToModel(metaCategory);
        Map<String, List<KielikaannosViewModel>> map = valintaperusteModel.getCategoryUris();

        if (map.containsKey(selectedUri)) {
            valintaperusteModel.setKuvaus(map.get(selectedUri));
        } else {
            valintaperusteModel.setKuvaus(new ArrayList<KielikaannosViewModel>(0));
        }

        LOG.debug("Loaded desc data : {}", valintaperusteModel.getKuvaus());

        valintaperusteModel.setLoaded(true);
    }

    /**
     * Select a model by given category. Description tabs can have its own data
     * model object.
     *
     * @param metaCategory
     * @return
     */
    public ValintaperusteModel getValintaperustemodel(final MetaCategory metaCategory) {
        return getModel().getKuvausModelByCategory(metaCategory);
    }

    /*
     * Create and open an user message dialog.
     */
    public SaveDialogView showSaveDialog() {
        final SaveDialogView modal = new SaveDialogView();
        getRootView().addWindow(modal);

        modal.addNavigationButton(I18N.getMessage("peruuta"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                // Stay in same view
                getRootView().removeWindow(modal);
                modal.removeDialogButtons();
            }
        }, StyleEnum.STYLE_BUTTON_SECONDARY);

        return modal;
    }

    /**
     * Load all metadata object from tarjonta service by given category.
     *
     * @param metaCategory
     */
    public void loadMetaDataToModel(final MetaCategory metaCategory) {
        final List<MonikielinenMetadataTyyppi> metaList = tarjontaAdminService.haeMetadata(null, metaCategory.toString());
        ValintaperusteModel valintaperustemodel = getValintaperustemodel(metaCategory);
        valintaperustemodel.setCategoryUris(mapKielidata(metaList));
    }

    /**
     * Convert raw tajonta service metadata to UI objects.
     *
     * @param metaCategory
     * @return
     */
    private Map<String, List<KielikaannosViewModel>> mapKielidata(final List<MonikielinenMetadataTyyppi> metaList) {
        Map<String, List<KielikaannosViewModel>> map = new HashMap<String, List<KielikaannosViewModel>>();

        for (MonikielinenMetadataTyyppi tyyppi : metaList) {
            final String uriKey = tyyppi.getAvain();
            final KielikaannosViewModel kieli = new KielikaannosViewModel(tyyppi.getKieli(), tyyppi.getArvo());
            if (map.containsKey(uriKey)) {
                map.get(uriKey).add(kieli);
            } else {
                List<KielikaannosViewModel> list = new ArrayList<KielikaannosViewModel>();
                list.add(kieli);
                map.put(uriKey, list);
            }
        }

        return map;
    }

    public KoodiType getKoodiByUri(String uri) {
        return tarjotaHelper.gethKoodis(uri).get(0);
    }
}
