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
package fi.vm.sade.tarjonta.ui;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.ui.hakuera.HakuView;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.KoulutusmoduuliEditView;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.toteutus.KoulutusmoduuliToteutusListView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jukka Raanamo
 */
public class MainWindow extends Window {

    private static final Logger LOG = LoggerFactory.getLogger(MainWindow.class);
    private KoulutusmoduuliEditView koulutusmoduuliEditView;
    private HakuView hakuView;
    private KoulutusmoduuliToteutusListView toteutusListView;
    
    public MainWindow() {
        super(I18N.getMessage("tarjonta.title"));
        init();
    }

    private void init() {
        
        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.addComponent(createTabs());
        setContent(mainLayout);

    }

    private TabSheet createTabs() {

        final TabSheet tabs = new TabSheet();
        tabs.setWidth(100, UNITS_PERCENTAGE);
        koulutusmoduuliEditView = new KoulutusmoduuliEditView();
        tabs.addTab(koulutusmoduuliEditView, I18N.getMessage("tarjonta.tabs.koulutusmoduulit"));
        hakuView = new HakuView();
        tabs.addTab(hakuView, I18N.getMessage("tarjonta.tabs.haut"));
        toteutusListView = new KoulutusmoduuliToteutusListView();
        tabs.addTab(toteutusListView, I18N.getMessage("tarjonta.tabs.toteutusList"));
        return tabs;
    }

    public KoulutusmoduuliEditView getKoulutusmoduuliEditView() {
        return koulutusmoduuliEditView;
    }

    public HakuView getHakuView() {
        return hakuView;
    }
}

