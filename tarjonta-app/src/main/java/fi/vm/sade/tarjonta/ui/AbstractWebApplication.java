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

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext;
import fi.vm.sade.generic.ui.app.AbstractSadeApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Application's "main" class for servlet and portlet implementation.
 *
 * @author jani
 */
public abstract class AbstractWebApplication extends AbstractSadeApplication implements ApplicationContext.TransactionListener {

    private static final long serialVersionUID = 4058508673680251653L;
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);
    private static ThreadLocal<Application> tl = new ThreadLocal<Application>();

    public AbstractWebApplication() {
        super();
        LOG.info("WebApplication()");
    }

    @Override
    public synchronized void init() {
        super.init();
        this.transactionStart(this, null);

        initApplication();
    }

    protected abstract void initApplication();

    @Override
    public void transactionStart(Application application, Object transactionData) {
        super.transactionStart(application, transactionData);

        if (application == this) {
            tl.set(this);
        }
    }

    @Override
    public void transactionEnd(Application application, Object transactionData) {
        if (application == this) {
            tl.remove();
        }

        super.transactionEnd(application, transactionData);
    }

    public static Application getInstance() {
        return tl.get();
    }
}
