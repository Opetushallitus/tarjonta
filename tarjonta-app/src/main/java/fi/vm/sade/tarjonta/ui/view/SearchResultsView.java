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

import com.vaadin.ui.Accordion;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.tarjonta.ui.helper.I18NHelper;
import fi.vm.sade.tarjonta.ui.view.haku.ListHakuViewImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author mlyly
 */
@Configurable
public class SearchResultsView extends VerticalLayout{

    @Autowired
    private TarjontaPresenter _presenter;

    private I18NHelper _i18n = new I18NHelper(this);

    public SearchResultsView() {
        super();
    }

    @Override
    public void attach() {
        super.attach();

        TabSheet tabs = new TabSheet();
        addComponent(tabs);

        tabs.addTab(new ListHakuViewImpl(), T("haut"));
        tabs.addTab(new Label("koulutukset"), T("koulutukset"));
        tabs.addTab(new Label("hakuryhmat"), T("hakuryhmat"));
    }

    private String T(String key) {
        return _i18n.getMessage(key);
    }

}
