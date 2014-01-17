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
import org.springframework.stereotype.Component;

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
import fi.vm.sade.tarjonta.service.types.PaivitaTilaVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.SisaltoTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.auth.TarjontaPermissionServiceImpl;
import fi.vm.sade.tarjonta.ui.enums.UserNotification;
import fi.vm.sade.tarjonta.ui.model.BaseUIViewModel;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import fi.vm.sade.tarjonta.ui.service.PublishingService;
import fi.vm.sade.tarjonta.ui.view.hakukohde.HakukohdeContainerEvent;
import fi.vm.sade.tarjonta.ui.view.koulutus.KoulutusContainerEvent;

/**
 *
 * @author jani
 */
@Component
public abstract class CommonPresenter<MODEL extends BaseUIViewModel> {
    
    private static Logger LOG = LoggerFactory.getLogger(CommonPresenter.class);

    /**
     * Rekisteröi uusi event listeneri.
     */
    public void registerEventListener(Object o) {
        LOG.debug("registering new listener:" + o.getClass() + ", eventbus=" + eventBus);
        eventBus.register(o);        
    }

    /**
     * Poista listenerin rekisteröinti.
     */
    public void unregisterEventListener(Object o) {
        LOG.debug("unregistering listener:" + o.getClass() + ", eventbus=" + eventBus);
        eventBus.unregister(o);        
    }
    
    /**
     * Lähetä eventti.
     */
    public void sendEvent(Object o) {
        LOG.debug("sending event:" + o.getClass() + ", eventbus=" + eventBus);
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
    protected TarjontaPermissionServiceImpl tarjontaPermissionService;
    @Autowired(required = true)
    protected OrganisaatioSearchService organisaatioSearchService;
    @Autowired(required = true)
    protected OrganisaatioService organisaatioService;
    @Autowired(required = true)
    protected TarjontaPermissionServiceImpl tarjontaPermissionServiceImpl;

    public abstract boolean isSaveButtonEnabled(final String oid, final SisaltoTyyppi sisalto, final TarjontaTila... requiredState);

    /**
     * Näyttää käyttäjälle notifikaation.
     */
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

    public abstract MODEL getModel();
    
    public TarjontaPermissionServiceImpl getPermission() {
        return tarjontaPermissionService;
    }

    /**
     * Hakee haun hakuoidilla. jos ei löydy, palauttaa nullin.
     */
    public HakuViewModel findHakuByOid(String oid) {
        final ListaaHakuTyyppi hakuehdot = new ListaaHakuTyyppi();
        hakuehdot.setHakuOid(oid);
        final ListHakuVastausTyyppi vastaus = tarjontaPublicService.listHaku(hakuehdot);
        if(vastaus.getResponse().size()>0) {
            return new HakuViewModel(vastaus.getResponse().get(0));
        } else {
            return null;        
        }
    }
    
    
    /**
     * Palauttaa true jos tilamuutos meni ok.
     * @param oid
     * @param toState
     * @param sisalto
     * @return
     */
    private void publish(final String oid, final TarjontaTila toState, final SisaltoTyyppi sisalto) {
        PublishingService publishingService = getPublishingService();
        PaivitaTilaVastausTyyppi vastaus = publishingService.changeState(oid, toState, sisalto);
        if (vastaus!=null) {
            for(String hakukohdeOid:vastaus.getHakukohdeOidit()){
                LOG.debug("event for hakukohde " + hakukohdeOid);
                sendEvent(HakukohdeContainerEvent.update(hakukohdeOid));
            }
            for(String komotoOid:vastaus.getKomotoOidit()){
                LOG.debug("event for komoto " + komotoOid);
                sendEvent(KoulutusContainerEvent.update(komotoOid));
            }
            
            showNotification(UserNotification.GENERIC_SUCCESS);
        } else {
            showNotification(UserNotification.GENERIC_ERROR);
        }
    }

    abstract PublishingService getPublishingService();

    /**
     * Cancel single tarjonta model by OID and data model type.
     *     
* @param oid
     * @param sisalto
     */
    public final void changeStateToCancelled(String oid, SisaltoTyyppi sisalto) {
        publish(oid, TarjontaTila.PERUTTU, sisalto);
    }

    public final void changeStateToPublished(String oid, SisaltoTyyppi sisalto) {
        publish(oid, TarjontaTila.JULKAISTU, sisalto);
    }
}
