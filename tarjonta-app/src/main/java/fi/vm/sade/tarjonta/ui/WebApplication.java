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

import com.github.wolfie.blackboard.Blackboard;
import com.vaadin.Application;
import fi.vm.sade.generic.ui.app.AbstractSadePortletApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Application's "main" class for servlet and portlet implementation.
 *
 * @author jani
 */
public class WebApplication extends AbstractSadePortletApplication {

    private static final long serialVersionUID = 4058508673680251653L;
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);
    private static ThreadLocal<Application> tl = new ThreadLocal<Application>();

    public WebApplication() {
        super();
        LOG.info("WebApplication()");
    }

    @Override
    protected void registerListeners(Blackboard blackboard) {
        LOG.info("registerListeners()");
    }

    @Override
    public synchronized void init() {
        super.init();
        this.transactionStart(this, null);

        initApplication();
    }

    protected void initApplication() {
        throw new RuntimeException("Uninitialized Vaadin window.");
    }

    @Override
    public void transactionStart(Application application, Object transactionData) {
        super.transactionStart(application, transactionData);
        if (application == this) {
            tl.set(this);
        }
    }

    @Override
    public void transactionEnd(Application application, Object transactionData) {
        super.transactionEnd(application, transactionData);

        if (application == this) {
            tl.remove();
        }
    }

    public static Application getInstance() {
        return tl.get();
    }
}
