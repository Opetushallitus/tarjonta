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
package fi.vm.sade.tarjonta.ui.view.koulutus;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalInfoLayout;
import fi.vm.sade.vaadin.constants.StyleEnum;
import fi.vm.sade.vaadin.dto.PageNavigationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Show collected terse information about koulutus.
 *
 * @author mlyly
 */
@Configurable
public class ShowKoulutusView extends AbstractVerticalInfoLayout {

    private static final Logger LOG = LoggerFactory.getLogger(ShowKoulutusView.class);

    public ShowKoulutusView(String pageTitle, PageNavigationDTO pageNavigationDTO) {
        super(VerticalLayout.class, pageTitle, null, pageNavigationDTO);

        addNavigationButton("", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getPresenter().showMainDefaultView();
            }
        }, StyleEnum.STYLE_BUTTON_BACK);

        addNavigationButton("Poista", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getWindow().showNotification(T("poistoOnnistui"));
                getPresenter().showMainDefaultView();
            }
        });

        addNavigationButton("Kopioi uudeksi", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getWindow().showNotification(T("kopiointiUudeksiOnnistui"));
                getPresenter().showMainDefaultView();
            }
        });
    }

    @Override
    protected void buildLayout(VerticalLayout layout) {
        LOG.info("buildLayout()");

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
        layout.addComponent(new Label("MIDDLE TOP"));
    }

    private void buildLayoutMiddleMid1(VerticalLayout layout) {
        layout.addComponent(new Label("MIDDLE MID1"));
    }

    private void buildLayoutMiddleMid2(VerticalLayout layout) {
        layout.addComponent(new Label("MIDDLE MID2"));
    }

    private void buildLayoutMiddleBottom(VerticalLayout layout) {
        layout.addComponent(new Label("MIDDLE BOTTOM"));
    }
}
