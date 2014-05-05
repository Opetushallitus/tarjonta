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
import com.vaadin.ui.Component;
import fi.vm.sade.generic.ui.app.AbstractSadeApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.wolfie.refresher.Refresher;
import com.github.wolfie.refresher.Refresher.RefreshListener;

/**
 * The Application's "main" class for servlet and portlet implementation.
 *
 * @author jani
 */
public abstract class AbstractWebApplication extends AbstractSadeApplication {

    private static final long serialVersionUID = 4058508673680251653L;
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

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


    /**
     * Helper to create simple refresher component to keep session alive.
     *
     * @return
     */
    public static Component createRefersh(final String id) {
        LOG.info("createRefresh() - id = {}", id);
        final Refresher refresher = new Refresher();
        refresher.setRefreshInterval(1000L * 60L * 5L);
        refresher.addListener(new RefreshListener() {
            public void refresh(Refresher source) {
                LOG.debug("refresher() - id = {}", id);
            }
        });

        return refresher;
    }

}
