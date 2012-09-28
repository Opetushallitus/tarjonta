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

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import fi.vm.sade.tarjonta.poc.ui.enums.Notification;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.constants.StyleEnum;
import fi.vm.sade.tarjonta.poc.demodata.DataSource;
import fi.vm.sade.tarjonta.poc.demodata.row.MultiActionTableStyleNoCBox;
import fi.vm.sade.tarjonta.poc.ui.view.common.AbstractVerticalInfoLayout;
import fi.vm.sade.vaadin.dto.PageNavigationDTO;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author mlyly
 */
@Configurable(preConstruction = true)
public class ShowHakukohdeView extends AbstractVerticalInfoLayout {

    private static final Logger LOG = LoggerFactory.getLogger(ShowHakukohdeView.class);

    public ShowHakukohdeView(String pageTitle, String message, PageNavigationDTO dto) {
        super(VerticalLayout.class, pageTitle, message, dto);

        addNavigationButton("", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                getPresenter().showMainKoulutusView();
            }
        }, StyleEnum.STYLE_BUTTON_BACK);

        addNavigationButton("Poista", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                getPresenter().showMainKoulutusView();
                getPresenter().demoInformation(Notification.DELETE);
            }
        });

        addNavigationButton("Kopioi uudeksi", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                getPresenter().demoInformation(Notification.COPY);

            }
        });
    }

    @Override
    protected void buildLayout(VerticalLayout layout) {
        buildLayoutMiddleTop(layout);
        buildLayoutMiddleBottom(layout);
    }

    private void buildLayoutMiddleTop(VerticalLayout layout) {

        VerticalSplitPanel panel1 = new VerticalSplitPanel();
        panel1.setImmediate(false);
        panel1.setWidth("100%");
        panel1.setHeight("2px");
        panel1.setLocked(true);
        layout.addComponent(panel1);

        layout.addComponent(buildHeaderLayout("Hakukohteen tiedot", "Muokkaa"));

        GridLayout lorem1 = new GridLayout(2, 6);
        lorem1.setHeight("100%");
        lorem1.setWidth("800px");
        lorem1.addComponent(UiUtil.label(null, DataSource.randomLorem(1, 15)), 0, 0);
        lorem1.addComponent(UiUtil.label(null, DataSource.randomLorem(1, 35)), 0, 1);
        lorem1.addComponent(UiUtil.label(null, DataSource.randomLorem(1, 15)), 0, 2);
        lorem1.addComponent(UiUtil.label(null, DataSource.randomLorem(1, 35)), 0, 3);
        lorem1.addComponent(UiUtil.label(null, DataSource.randomLorem(1, 25)), 0, 4);
        lorem1.addComponent(UiUtil.label(null, DataSource.randomLorem(1, 25)), 0, 5);

        lorem1.addComponent(UiUtil.label(null, DataSource.randomLorem(1, 255)), 1, 0);
        lorem1.addComponent(UiUtil.label(null, DataSource.randomLorem(1, 65)), 1, 1);
        lorem1.addComponent(UiUtil.label(null, DataSource.randomLorem(1, 45)), 1, 2);
        lorem1.addComponent(UiUtil.label(null, DataSource.randomLorem(1, 85)), 1, 3);
        lorem1.addComponent(UiUtil.label(null, DataSource.randomLorem(1, 15)), 1, 4);
        lorem1.addComponent(UiUtil.label(null, DataSource.randomLorem(1, 1000)), 1, 5);

        lorem1.setColumnExpandRatio(0, 1);
        lorem1.setColumnExpandRatio(1, 5);

        for (int row = 0; row < lorem1.getRows(); row++) {
            //alignment code not working?
            Component c = lorem1.getComponent(0, row);
            lorem1.setComponentAlignment(c, Alignment.TOP_RIGHT);
        }


        layout.addComponent(lorem1);
        VerticalSplitPanel panel2 = new VerticalSplitPanel();
        panel2.setImmediate(false);
        panel2.setWidth("100%");
        panel2.setHeight("2px");
        panel2.setLocked(true);
        layout.addComponent(panel2);

        layout.setExpandRatio(lorem1, 1f);
    }

    private void buildLayoutMiddleBottom(VerticalLayout layout) {
        layout.addComponent(buildHeaderLayout("Hakukohteeseen sisältyvät koulutukset", "Liitä uusi koulutus"));

        CategoryTreeView categoryTree = new CategoryTreeView();
        categoryTree.setHeight("200px");
        categoryTree.setContainerDataSource(DataSource.treeTableData(new MultiActionTableStyleNoCBox()));
        layout.addComponent(categoryTree);
    }

    private HorizontalLayout buildHeaderLayout(String title, String btnCaption) {
        HorizontalLayout headerLayout = UiUtil.horizontalLayout(true, UiMarginEnum.TOP_BOTTOM);
        Label titleLabel = UiUtil.label(headerLayout, title);
        titleLabel.setStyleName(Oph.LABEL_H2);

        if (btnCaption != null) {
            headerLayout.addComponent(titleLabel);
            Button btn = UiUtil.buttonSmallSecodary(headerLayout, btnCaption, new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    getPresenter().demoInformation(Notification.GENERIC_ERROR);
                }
            });

            headerLayout.setExpandRatio(btn, 1f);

            headerLayout.setComponentAlignment(btn, Alignment.TOP_RIGHT);
        }
        return headerLayout;
    }
}
