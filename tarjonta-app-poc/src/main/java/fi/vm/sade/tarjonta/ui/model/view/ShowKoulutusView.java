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
package fi.vm.sade.tarjonta.ui.model.view;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.oph.demodata.DataSource;
import fi.vm.sade.vaadin.oph.demodata.row.MultiActionTableStyleNoCBox;
import fi.vm.sade.vaadin.oph.demodata.row.TextTableStyle;
import fi.vm.sade.vaadin.oph.dto.PageNavigationDTO;
import fi.vm.sade.vaadin.oph.enums.UiMarginEnum;
import fi.vm.sade.vaadin.oph.helper.UiBuilder;
import fi.vm.sade.vaadin.oph.layout.AbstractInfoLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author mlyly
 */
@Configurable(preConstruction = true)
public class ShowKoulutusView extends AbstractInfoLayout<VerticalLayout> {

    private static final Logger LOG = LoggerFactory.getLogger(ShowKoulutusView.class);

    public ShowKoulutusView(String pageTitle, String message, PageNavigationDTO dto) {
        super(VerticalLayout.class, pageTitle, message, dto);

        addNavigationButton("", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                _presenter.showMainKoulutusView();
            }
        }, Oph.BUTTON_BACK);

        addNavigationButton("Poista", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }, Oph.BUTTON_SMALL);

        addNavigationButton("Kopioi uudeksi", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }, Oph.BUTTON_SMALL);
    }

    @Override
    protected void buildLayout(VerticalLayout layout) {
        addLayoutSplit();
        buildLayoutMiddleTop(layout);
        addLayoutSplit();
        buildLayoutMiddleMid1(layout);
        addLayoutSplit();
        buildLayoutMiddleMid2(layout);
        addLayoutSplit();
        buildLayoutMiddleBottom(layout);
        addLayoutSplit();
    }

    private void buildLayoutMiddleTop(VerticalLayout layout) {
        layout.addComponent(buildHeaderLayout("Koulutuksen perustiedot", "Muokkaa"));

        String[] arr = DataSource.KOULUTUKSEN_PERUSTIEDOT_HEADERS;

        GridLayout grid = new GridLayout(2, arr.length + 1);
        grid.setHeight("100%");
        grid.setWidth("800px");

        LOG.info("Grid result length : " + arr.length);
        for (int i = 0; i < arr.length; i++) {
            grid.addComponent(UiBuilder.newLabel(arr[i]), 0, i);
        }

        arr = DataSource.KOULUTUKSEN_PERUSTIEDOT_DATA;

        for (int i = 0; i < arr.length; i++) {
            grid.addComponent(UiBuilder.newLabel(arr[i] + " "), 1, i);
        }

        grid.setColumnExpandRatio(0, 1);
        grid.setColumnExpandRatio(1, 2);

        for (int row = 0; row < grid.getRows(); row++) {
            //alignment code not working?
            Component c = grid.getComponent(0, row);
            grid.setComponentAlignment(c, Alignment.TOP_RIGHT);
        }

        layout.addComponent(grid);
        layout.setExpandRatio(grid, 1f);
    }

    private void buildLayoutMiddleMid1(VerticalLayout layout) {
        layout.addComponent(buildHeaderLayout("Koulutuksen kuvailevat tiedot", "Muokkaa"));

        String[] arr = DataSource.KOULUTUKSEN_KUVAILEVAT_TIEDOT_HEADERS;

        GridLayout grid = new GridLayout(2, arr.length + 1);
        grid.setHeight("100%");
        grid.setWidth("800px");

        for (int i = 0; i < arr.length; i++) {
            grid.addComponent(UiBuilder.newLabel(arr[i]), 0, i);
        }

        for (int i = 0; i < arr.length; i++) {
            grid.addComponent(UiBuilder.newLabel(" - "), 1, i);
        }

        grid.setColumnExpandRatio(0, 1);
        grid.setColumnExpandRatio(1, 2);

        for (int row = 0; row < grid.getRows(); row++) {
            //alignment code not working?
            Component c = grid.getComponent(0, row);
            grid.setComponentAlignment(c, Alignment.TOP_RIGHT);
        }

        layout.addComponent(grid);
        layout.setExpandRatio(grid, 1f);
    }

    private void buildLayoutMiddleMid2(VerticalLayout layout) {
        layout.addComponent(buildHeaderLayout("Sisältyvät opintokokonaisuudet (3 kpl)", "Muokkaa"));

        CategoryTreeView categoryTree = new CategoryTreeView();
        categoryTree.setHeight("100px");
        categoryTree.setContainerDataSource(DataSource.treeTableData(new TextTableStyle()));
        layout.addComponent(categoryTree);
    }

    private void buildLayoutMiddleBottom(VerticalLayout layout) {
        layout.addComponent(buildHeaderLayout("Hakukohteet (1 kpl)", "Luo uusi hakukohde"));

        CategoryTreeView categoryTree = new CategoryTreeView();
        categoryTree.setHeight("100px");
        categoryTree.setContainerDataSource(DataSource.treeTableData(new MultiActionTableStyleNoCBox()));
        layout.addComponent(categoryTree);
    }

    private HorizontalLayout buildHeaderLayout(String title, String btnCaption) {
        HorizontalLayout headerLayout = UiBuilder.newHorizontalLayout(true, UiMarginEnum.NONE);
        Label titleLabel = UiBuilder.newLabel(title, headerLayout);
        titleLabel.setStyleName(Oph.LABEL_H2);

        if (btnCaption != null) {
            headerLayout.addComponent(titleLabel);
            Button btn = UiBuilder.newButton(btnCaption, headerLayout);
            btn.setStyleName(Oph.CONTAINER_SECONDARY);
            btn.addStyleName(Oph.BUTTON_SMALL);

            headerLayout.setExpandRatio(btn, 1f);

            headerLayout.setComponentAlignment(btn, Alignment.TOP_RIGHT);
        }
        return headerLayout;
    }
}
