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

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.tarjonta.ui.TarjontaPresenter;
import fi.vm.sade.vaadin.oph.helper.UiBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author mlyly
 */
@Configurable(preConstruction=true)
public class ShowKoulutusView extends VerticalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(ShowKoulutusView.class);

    @Autowired
    private TarjontaPresenter _presenter;

    public ShowKoulutusView() {
        super();
        addComponent(new Label("NOT IMPLEMENTED"));

        Button btnGoBack = UiBuilder.newButton("<-- Hakutuloksiin", this);
        btnGoBack.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                _presenter.showMainKoulutusView();
            }
        });
    }

}
