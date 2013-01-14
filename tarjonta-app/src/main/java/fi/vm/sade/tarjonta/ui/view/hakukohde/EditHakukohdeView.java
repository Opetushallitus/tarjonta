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
package fi.vm.sade.tarjonta.ui.view.hakukohde;

import com.vaadin.ui.Button;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalNavigationLayout;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.PerustiedotViewImpl;
import fi.vm.sade.vaadin.constants.StyleEnum;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author Tuomas Katva
 */
@Configurable
public class EditHakukohdeView extends AbstractVerticalNavigationLayout {
    private static final long serialVersionUID = 8806220426371090907L;

    @Autowired
    private TarjontaPresenter _presenter;
    @Autowired(required = true)
    private transient UiBuilder uiBuilder;
    private TabSheet tabs;

    public EditHakukohdeView() {
        super();
        setHeight(-1, UNITS_PIXELS);

    }

    @Override
    protected void buildLayout(VerticalLayout t) {
        tabs = new TabSheet();
        tabs.setHeight(-1, UNITS_PIXELS);
        t.addComponent(tabs);
        PerustiedotViewImpl perustiedot = new PerustiedotViewImpl(_presenter, uiBuilder);

        tabs.addTab(perustiedot, T("tabNimi"));

        createButtons();
    }

    private void createButtons() {
        addNavigationButton("", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                _presenter.showMainDefaultView();
                _presenter.getHakukohdeListView().reload();
            }
        }, StyleEnum.STYLE_BUTTON_BACK);

        addNavigationButton(T("tallennaLuonnoksena"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                _presenter.commitHakukohdeForm("LUONNOS");
                //getWindow().showNotification();
            }
        });

        addNavigationButton(T("tallennaValmiina"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                _presenter.commitHakukohdeForm("VALMIS");

            }
        });
        /*  for future use
         addNavigationButton(T("jatka"), new Button.ClickListener() {
         @Override
         public void buttonClick(Button.ClickEvent event) {
         //                getPresenter().showShowHakukohdeView();
         }
         });
         */
    }
}
