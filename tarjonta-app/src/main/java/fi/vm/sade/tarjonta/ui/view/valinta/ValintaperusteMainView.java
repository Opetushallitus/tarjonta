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
package fi.vm.sade.tarjonta.ui.view.valinta;

import com.vaadin.ui.TabSheet;
import fi.vm.sade.tarjonta.ui.enums.MetaCategory;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.presenter.ValintaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalLayout;

/**
 * Valintaperuste main page.
 *
 * @author Jani Wilén
 */
public class ValintaperusteMainView extends AbstractVerticalLayout {

    private transient UiBuilder uiBuilder;
    private ValintaPresenter presenter;

    public ValintaperusteMainView(ValintaPresenter presenter, UiBuilder uiBuilder) {
        super();

        this.presenter = presenter;
        this.uiBuilder = uiBuilder;
    }

    @Override
    protected void buildLayout() {
        this.setMargin(true);
        TabSheet tabs = UiBuilder.tabSheet(this);

        /*
         * Generic implementation of valintaperustekuvaus tab
         */
        EditValintakuvausView valintaperustekuvausView = new EditValintakuvausView(MetaCategory.VALINTAPERUSTEKUVAUS, presenter, uiBuilder);
        tabs.addTab(valintaperustekuvausView, T("valintaperustekuvaus"));
        /*
         * Generic implementation of SORA tab
         */
        EditValintakuvausView soraView = new EditValintakuvausView(MetaCategory.SORA_KUVAUS, presenter, uiBuilder);
        tabs.addTab(soraView, T("sorakuvaus"));
    }
}
