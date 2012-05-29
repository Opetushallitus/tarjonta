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

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;
import fi.vm.sade.generic.common.I18N;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jukka Raanamo
 */
public class MainWindow extends Window {

    private static final Logger LOG = LoggerFactory.getLogger(MainWindow.class);

    public MainWindow() {
        super(I18N.getMessage("tarjonta.title"));
        init();
    }


    private void init() {
        LOG.info("init()");

        // main horizontal two column layout
        final HorizontalLayout mainLayout = new HorizontalLayout();
        addComponent(mainLayout);
    }
}

