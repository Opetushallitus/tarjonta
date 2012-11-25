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
package fi.vm.sade.tarjonta.ui.view.haku;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Container;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;

import fi.vm.sade.tarjonta.ui.model.HakuaikaViewModel;
import fi.vm.sade.tarjonta.ui.model.HakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.view.HakuPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalInfoLayout;
import fi.vm.sade.tarjonta.ui.view.common.CategoryTreeView;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.constants.StyleEnum;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.dto.PageNavigationDTO;
import fi.vm.sade.vaadin.util.UiUtil;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Component for viewing Haku objects.
 *
 * @author markus
 *
 */
@Configurable(preConstruction = true)
public class ShowHakuViewImpl extends AbstractVerticalInfoLayout implements ShowHakuView {

    private static final Logger LOG = LoggerFactory.getLogger(ShowHakuViewImpl.class);

    @Autowired(required=true)
    private HakuPresenter hakuPresenter;

    @Autowired(required=true)
    private TarjontaUIHelper _tarjontaUIHelper;

    public ShowHakuViewImpl(String pageTitle, String message, PageNavigationDTO dto) {
        super(VerticalLayout.class, pageTitle, message, dto);
    }

    @Override
    public void buildLayout(VerticalLayout layout) {
        addNavigationButton("", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                backFired();
            }
        }, StyleEnum.STYLE_BUTTON_BACK);

        addNavigationButton(T("Poista"), new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                hakuPresenter.removeHaku(hakuPresenter.getHakuModel());
                getWindow().showNotification(T("HakuPoistettu"));
                backFired();
            }
        });

        addLayoutSplit();
        buildLayoutMiddleTop(layout);
        addLayoutSplit();
        buildLayoutMiddleMid2(layout);
        addLayoutSplit();
        buildLayoutMiddleBottom(layout);
        addLayoutSplit();
    }

    private void buildLayoutMiddleTop(VerticalLayout layout) {
        layout.addComponent(buildHeaderLayout(T("HaunTiedot"), T("Muokkaa")));

        HakuViewModel model = hakuPresenter.getHakuModel();

        GridLayout grid = new GridLayout(2, 9);
        grid.setHeight("100%");
        grid.setWidth("800px");

        LOG.info("Building lalbels");
        grid.addComponent(UiUtil.label(null, T("Hakutyyppi") + ": "), 0, 0);
        grid.addComponent(UiUtil.label(null, T("HakukausiJaVuosi") + ": "), 0, 1);
        grid.addComponent(UiUtil.label(null, T("KoulutuksenAlkamiskausi") + ": "), 0, 2);
        grid.addComponent(UiUtil.label(null, T("HakuKohdejoukko") + ": "), 0, 3);
        grid.addComponent(UiUtil.label(null, T("Hakutapa") + ": "), 0, 4);
        grid.addComponent(UiUtil.label(null, T("HaunTunniste") + ": "), 0, 5);
        grid.addComponent(UiUtil.label(null, T("Hakuaika") + ": "), 0, 6);
        grid.addComponent(UiUtil.label(null, T("Hakulomake") + ": "), 0, 7);


        LOG.info("building content labels");
        grid.addComponent(UiUtil.label(null, _tarjontaUIHelper.getKoodiNimi(model.getHakutyyppi())), 1, 0);


        grid.addComponent(UiUtil.label(null, _tarjontaUIHelper.getKoodiNimi(model.getHakukausi()) + " " + model.getHakuvuosi()), 1, 1);
        grid.addComponent(UiUtil.label(null, _tarjontaUIHelper.getKoodiNimi(model.getKoulutuksenAlkamisKausi()) + " " + model.getKoulutuksenAlkamisvuosi()), 1, 2);
        grid.addComponent(UiUtil.label(null, _tarjontaUIHelper.getKoodiNimi(model.getHaunKohdejoukko())), 1, 3);
        grid.addComponent(UiUtil.label(null, _tarjontaUIHelper.getKoodiNimi(model.getHakutapa())), 1, 4);
        grid.addComponent(UiUtil.label(null, model.getHaunTunniste()), 1, 5);
        grid.addComponent(UiUtil.label(null,
                _tarjontaUIHelper.formatDate(model.getAlkamisPvm()) + " - " + _tarjontaUIHelper.formatDate(model.getPaattymisPvm())),
                1, 6);
        String hakulomakeStr = hakuPresenter.getHakuModel().isKaytetaanJarjestelmanHakulomaketta()
                ? T("KaytetaanJarjestelmanHakulomaketta")
                : hakuPresenter.getHakuModel().getHakuLomakeUrl();
        grid.addComponent(UiUtil.label(null, hakulomakeStr), 1, 7);

        grid.setColumnExpandRatio(0, 1);
        grid.setColumnExpandRatio(1, 2);

        for (int row = 0; row < grid.getRows(); row++) {
            //alignment code not working?
            Component c = grid.getComponent(0, row);
            grid.setComponentAlignment(c, Alignment.TOP_RIGHT);
        }

        layout.addComponent(grid);
        layout.setExpandRatio(grid, 1f);
        LOG.info("Middle part done.");
    }

    private void buildLayoutMiddleMid2(VerticalLayout layout) {
        layout.addComponent(buildHeaderLayout(T("Sisaisethaut"), T("Muokkaa")));

        CategoryTreeView categoryTree = new CategoryTreeView();
        categoryTree.setHeight("100px");
        categoryTree.setContainerDataSource(createHakuaikaTreeDataSource(hakuPresenter.getSisaisetHautSource()));
        layout.addComponent(categoryTree);
    }



    private void buildLayoutMiddleBottom(VerticalLayout layout) {
        layout.addComponent(buildBottomHeaderLayout(T("Hakukohteet"), T("LuoHakukohde")));

        CategoryTreeView categoryTree = new CategoryTreeView();
        categoryTree.setHeight("100px");
        categoryTree.setContainerDataSource(createHakukohteetTreeDataSource(hakuPresenter.getHakukohteet()));
        layout.addComponent(categoryTree);
    }



    private HorizontalLayout buildHeaderLayout(String title, String btnCaption) {
        HorizontalLayout headerLayout = UiUtil.horizontalLayout(true, UiMarginEnum.NONE);
        Label titleLabel = UiUtil.label(headerLayout, title);
        titleLabel.setStyleName(Oph.LABEL_H2);

        if (btnCaption != null) {
            headerLayout.addComponent(titleLabel);
            Button btn = UiUtil.buttonSmallSecodary(headerLayout, btnCaption, new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                   editFired();
                }
            });

            headerLayout.setExpandRatio(btn, 1f);
            headerLayout.setComponentAlignment(btn, Alignment.TOP_RIGHT);
        }
        return headerLayout;
    }

    private HorizontalLayout buildBottomHeaderLayout(String title, String btnCaption) {
        HorizontalLayout headerLayout = UiUtil.horizontalLayout(true, UiMarginEnum.NONE);
        Label titleLabel = UiUtil.label(headerLayout, title);
        titleLabel.setStyleName(Oph.LABEL_H2);

        if (btnCaption != null) {
            headerLayout.addComponent(titleLabel);
            Button btn = UiUtil.buttonSmallSecodary(headerLayout, btnCaption, new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                   LOG.info("Luo hakukohde to be implemented");
                }
            });

            headerLayout.setExpandRatio(btn, 1f);
            headerLayout.setComponentAlignment(btn, Alignment.TOP_RIGHT);
        }
        return headerLayout;
    }

    private Container createHakuaikaTreeDataSource(
            List<HakuaikaViewModel> sisaisetHautSource) {
        HierarchicalContainer hc = new HierarchicalContainer();
        return hc;
    }

    private Container createHakukohteetTreeDataSource(
            List<HakukohdeViewModel> hakukohteet) {
        HierarchicalContainer hc = new HierarchicalContainer();
        return hc;
    }

    private void backFired() {
        fireEvent(new BackEvent(this));
    }

    private void editFired() {
        fireEvent(new EditEvent(this));
    }

    /**
     * Fired when Back is pressed.
     */
    public class BackEvent extends Component.Event {

        public BackEvent(Component source) {
            super(source);

        }
    }

    /**
     * Fired when Edit is pressed.
     */
    public class EditEvent extends Component.Event {

        public EditEvent(Component source) {
            super(source);

        }
    }

}
