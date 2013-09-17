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
package fi.vm.sade.tarjonta.ui.presenter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.eventbus.EventBus;
import com.vaadin.ui.Window;

import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.service.search.OrganisaatioSearchService;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.types.ListHakuVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.ListaaHakuTyyppi;
import fi.vm.sade.tarjonta.service.types.SisaltoTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.auth.TarjontaPermissionServiceImpl;
import fi.vm.sade.tarjonta.ui.enums.UserNotification;
import fi.vm.sade.tarjonta.ui.model.BaseUIViewModel;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import fi.vm.sade.tarjonta.ui.service.PublishingService;

/**
 *
 * @author jani
 */
public abstract class CommonPresenter<MODEL extends BaseUIViewModel> {
    
    private static Logger LOG = LoggerFactory.getLogger(CommonPresenter.class);

    /**
     * Rekisteröi uusi event listeneri.
     */
    public void registerEventListener(Object o) {
        LOG.debug("registering new listener:" + o.getClass());
        eventBus.register(o);        
    }

    /**
     * Poista listenerin rekisteröinti.
     */
    public void unregisterEventListener(Object o) {
        LOG.debug("inregistering listener:" + o.getClass());
        eventBus.unregister(o);        
    }
    
    /**
     * Lähetä eventti.
     */
    public void sendEvent(Object o) {
        LOG.info("sending event:" + o.getClass());
        eventBus.post(o);
    }

    @Autowired(required=true)
    protected EventBus eventBus;
    @Autowired
    protected OIDService oidService;
    @Autowired(required = true)
    protected KoodiService koodiService;
    @Autowired(required = true)
    protected TarjontaPublicService tarjontaPublicService;
    @Autowired(required = true)
    protected TarjontaAdminService tarjontaAdminService;
    @Autowired(required = true)
    protected PublishingService publishingService;
    @Autowired(required = true)
    protected TarjontaPermissionServiceImpl tarjontaPermissionService;
    @Autowired(required = true)
    protected OrganisaatioSearchService organisaatioSearchService;
    @Autowired(required = true)
    protected OrganisaatioService organisaatioService;
    @Autowired(required = true)
    protected TarjontaPermissionServiceImpl tarjontaPermissionServiceImpl;

    public abstract boolean isSaveButtonEnabled(final String oid, final SisaltoTyyppi sisalto, final TarjontaTila... requiredState);

    public void showNotification(final UserNotification msg){
        LOG.info("Show user notification - type {}, value {}", msg, msg != null ? msg.getInfo() : null);
        if (msg != null && getRootView() != null) {
            getRootView().showNotification(msg.getInfo(), msg.getNotifiaction());
        } else {
            LOG.error("Application error - an unknown problem with UI notification. Value : {}", msg);
        }
 
    }

    public abstract Window getRootView();

    public abstract void showMainDefaultView();

    public abstract void changeStateToCancelled(final String oid, final SisaltoTyyppi sisalto);

    public abstract void changeStateToPublished(final String oid, final SisaltoTyyppi sisalto);

    public abstract MODEL getModel();
    
    public TarjontaPermissionServiceImpl getPermission() {
        return tarjontaPermissionService;
    }

    public HakuViewModel findHakuByOid(String oid) {
        ListaaHakuTyyppi hakuehdot = new ListaaHakuTyyppi();
        hakuehdot.setHakuOid(oid);
        ListHakuVastausTyyppi vastaus = tarjontaPublicService.listHaku(hakuehdot);
        return new HakuViewModel(vastaus.getResponse().get(0));
    }
    

}
