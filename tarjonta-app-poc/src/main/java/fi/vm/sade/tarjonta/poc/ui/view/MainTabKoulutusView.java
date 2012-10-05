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
package fi.vm.sade.tarjonta.poc.ui.view;

import com.vaadin.data.Container;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import fi.vm.sade.tarjonta.poc.ui.TarjontaPresenter;
import fi.vm.sade.tarjonta.poc.ui.helper.I18NHelper;
import fi.vm.sade.vaadin.constants.StyleEnum;
import fi.vm.sade.tarjonta.poc.demodata.DataSource;
import fi.vm.sade.tarjonta.poc.ui.view.common.CategoryTreeView;
import fi.vm.sade.tarjonta.poc.ui.view.koulutus.CreateKoulutusView;
import fi.vm.sade.tarjonta.poc.ui.view.common.AutoSizeVerticalLayout;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author jani
 */
@Configurable(preConstruction = true)
public class MainTabKoulutusView extends AutoSizeVerticalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(MainTabKoulutusView.class);
    private Button btnMuokkaa;
    private Button btnPoista;
    private Button btnLuoUusiHakukohde;
    private Button btnLuoUusiKoulutus;
    private Button btnInfo;
    private ComboBox cbJarjestys;
    private CategoryTreeView categoryTree;
    private I18NHelper i18n = new I18NHelper(this);
    @Autowired(required = true)
    private TarjontaPresenter _presenter;

    public MainTabKoulutusView() {
        super(Type.PCT_100, Type.AUTOSIZE);
        HorizontalLayout buildMiddleResultLayout = buildMiddleResultLayout();
        addComponent(buildMiddleResultLayout);

        CssLayout wrapper = UiUtil.cssLayout(UiMarginEnum.BOTTOM);
        wrapper.addComponent(new CheckBox(i18n.getMessage("ValitseKaikki")));
        addComponent(wrapper);

        categoryTree = new CategoryTreeView();
        addComponent(categoryTree);
        setExpandRatio(wrapper, 0.07f);
        setExpandRatio(categoryTree, 0.93f);
        setMargin(true);

        categoryTree.setContainerDataSource(_presenter.getTreeDataSource());
    }

    private HorizontalLayout buildMiddleResultLayout() {
        HorizontalLayout layout = UiUtil.horizontalLayout(true, UiMarginEnum.BOTTOM);

        btnMuokkaa = UiUtil.buttonSmallPrimary(layout, i18n.getMessage("Muokkaa"));
        btnMuokkaa.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                //TODO: how this should work?
            }
        });

        btnLuoUusiHakukohde = UiUtil.buttonSmallPrimary(layout, i18n.getMessage("LuoUusiHakukohde"));
        btnLuoUusiHakukohde.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                //Add all selected checkboxes to the luo hakukohde selection.
                _presenter.showCreateHakukohdeView();
            }
        });

        btnPoista = UiUtil.buttonSmallSecodary(layout, i18n.getMessage("Poista"));
        btnPoista.setEnabled(false);

        btnLuoUusiKoulutus = UiUtil.buttonSmallPrimary(layout, i18n.getMessage("LuoUusiKoulutus"));
        btnLuoUusiKoulutus.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {

                final CreateKoulutusView modal = new CreateKoulutusView(i18n.getMessage("LuoUusiKoulutus"));
                getWindow().addWindow(modal);

                modal.addNavigationButton("Peruuta", new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        LOG.debug("buttonClick() - peruuta click...");
                        // Stay in same view
                        getWindow().removeWindow(modal);
                        modal.removeDialogButtons();
                    }
                }, StyleEnum.STYLE_BUTTON_SECONDARY);

                modal.addNavigationButton("Jatka", new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        LOG.debug("buttonClick() - luo uusi koulutus click...");

                        getWindow().removeWindow(modal);
                        modal.removeDialogButtons();
                        LOG.debug("presenter : " + _presenter);
                        _presenter.showEditKolutusView();
                    }
                }, StyleEnum.STYLE_BUTTON_PRIMARY);

                modal.buildDialogButtons();
            }
        });

        cbJarjestys = UiUtil.comboBox(layout, null, DataSource.ORDER_BY);
        cbJarjestys.setWidth("300px");

        layout.setExpandRatio(btnLuoUusiKoulutus, 1f);
        layout.setComponentAlignment(btnLuoUusiKoulutus, Alignment.TOP_RIGHT);

        btnInfo = UiUtil.buttonSmallInfo(layout);

        return layout;
    }

    /**
     * @param btnLuoUusiKoulutus the btnLuoUusiKoulutus to set
     */
    public void setBtnListenerLuoUusiKoulutus(Button.ClickListener btnLuoUusiKoulutus) {
        this.btnLuoUusiKoulutus.addListener(btnLuoUusiKoulutus);
    }

    /**
     * @param btnListenerMuokkaa( the btnLuoUusiKoulutus to set
     */
    public void setBtnListenerMuokkaa(Button.ClickListener btnKopioiUudelleKaudelle) {
        this.btnMuokkaa.addListener(btnKopioiUudelleKaudelle);
    }

    public void setCategoryDataSource(Container dataSource) {
        categoryTree.setContainerDataSource(dataSource);
    }
}
