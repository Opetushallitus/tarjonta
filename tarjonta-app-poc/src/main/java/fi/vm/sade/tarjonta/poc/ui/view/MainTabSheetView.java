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

import com.vaadin.ui.TabSheet;
import fi.vm.sade.tarjonta.poc.ui.helper.I18NHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author jani
 */
@Configurable(preConstruction = true)
public class MainTabSheetView extends TabSheet {

    private static final Logger LOG = LoggerFactory.getLogger(MainTabSheetView.class);
    private MainTabKoulutusView tabKoulutukset;
    private MainTabHakuView tabHaut;
    private MainTabHakukohteetView tabHakukohteet;
    private I18NHelper i18n = new I18NHelper(this);

    public MainTabSheetView() {
        setImmediate(true);
        setSizeFull();
        buildLayout();
    }

    private void buildLayout() {
        tabHaut = new MainTabHakuView();
        //addTab(tabHaut, "Haut (2 kpl)", null);

        tabKoulutukset = new MainTabKoulutusView();
        addTab(tabKoulutukset, "Koulutukset (28 kpl)", null);

        tabHakukohteet = new MainTabHakukohteetView();
        addTab(tabHakukohteet, "Hakukohteet (35 kpl)", null);

        //SET SELECTED TAB!
        setSelectedTab(tabKoulutukset);
    }
}
