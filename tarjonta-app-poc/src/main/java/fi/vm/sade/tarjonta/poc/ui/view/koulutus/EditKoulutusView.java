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
package fi.vm.sade.tarjonta.poc.ui.view.koulutus;

import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.tarjonta.poc.ui.view.common.AbstractVerticalNavigationLayout;
import fi.vm.sade.vaadin.constants.LabelStyleEnum;
import fi.vm.sade.vaadin.constants.StyleEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author mlyly
 */
@Configurable(preConstruction = true)
public class EditKoulutusView extends AbstractVerticalNavigationLayout {

    private static final Logger LOG = LoggerFactory.getLogger(EditKoulutusView.class);
    private static final String TITLE_FORMAT = "Olet luomassa {0} koulutusta organisaatioon {1}";

    public EditKoulutusView() {
        super(EditKoulutusView.class);
        LOG.info("EditKoulutusView()");

        addNavigationButton("", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getPresenter().showMainKoulutusView();
            }
        }, StyleEnum.STYLE_BUTTON_BACK);

        addNavigationButton(T("tallennaLuonnoksena"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });

        addNavigationButton(T("tallennaValmiina"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });

        addNavigationButton(T("jatka"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getPresenter().showShowKoulutusView();
            }
        });

        setSizeFull();


    }

    @Override
    protected void buildLayout(VerticalLayout layout) {
        UiUtil.label(this, TITLE_FORMAT, LabelStyleEnum.H2, "tutkintoon johtavaa", "Informaatiotekniikan tiedekunta");

        TabSheet tabs = new TabSheet();
        tabs.setSizeFull();
        tabs.addTab(new EditKoulutusPerustiedotToinenAsteView(), "Koulutuksen perustiedot (status)");
        tabs.addTab(new EditKoulutusKuvailevattiedotView(), "Koulutuksen kuvailevat tiedot (status)");

        layout.addComponent(tabs);
    }
}
