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
package fi.vm.sade.tarjonta.shared.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import fi.vm.sade.generic.service.AbstractPermissionService;
import fi.vm.sade.generic.service.PermissionService;

/**
 * This class encapsulates permission service so that changes the actual
 * {@link PermissionService} do not have so much effect on this app.
 *
 * @author Jani Wilén
 */
@Configurable
@Component
public class TarjontaPermissionServiceImpl implements InitializingBean {

    public static final String TARJONTA = "TARJONTA";
    //OPH oid
    @Value("${root.organisaatio.oid}")
    String rootOrgOid;

    public TarjontaPermissionServiceImpl() {
        // TODO Auto-generated constructor stub
    }
    private static final Logger LOGGER = LoggerFactory.getLogger(TarjontaPermissionServiceImpl.class);

    @Component
    public static class TPermissionService extends AbstractPermissionService {

        public TPermissionService() {
            super(TARJONTA);
        }
    }
    @Autowired
    TPermissionService wrapped;

    @Override
    public void afterPropertiesSet() throws Exception {
        Preconditions.checkNotNull(wrapped, "The permissionService is not set");
    }

    /**
     * Checks if user can cancel koulutus publishment.
     *
     * @param org
     * @return
     */
    public boolean userCanCancelKoulutusPublish(final OrganisaatioContext context) {
        return wrapped.checkAccess(context.ooid, wrapped.ROLE_CRUD);
    }

    public boolean userCanCreateHakukohde(final OrganisaatioContext context) {
        return wrapped.checkAccess(context.ooid, wrapped.ROLE_CRUD);
    }

    /**
     * Checks if user is allowed to create koulutus.
     *
     * @param context
     * @return
     */
    public boolean userCanCreateKoulutus(final OrganisaatioContext context) {
        return wrapped.checkAccess(context.ooid, wrapped.ROLE_CRUD);
    }

    public boolean userCanDeleteHakukohde(final OrganisaatioContext context) {
        return wrapped.checkAccess(context.ooid, wrapped.ROLE_CRUD);
    }

    /**
     * Checks if user can delete koulutus.
     *
     * @return
     */
    public boolean userCanDeleteKoulutus(final OrganisaatioContext context) {
        return wrapped.checkAccess(context.ooid, wrapped.ROLE_CRUD);
    }

    /**
     * Checks if user can publish koulutus.
     *
     * @param org
     * @return
     */
    public boolean userCanPublishKoulutus(final OrganisaatioContext context) {
        return wrapped.checkAccess(context.ooid, wrapped.ROLE_CRUD);
    }

    /**
     * Checks if user can update hakukohde.
     *
     * @param context
     * @return
     */
    public boolean userCanUpdateHakukohde(final OrganisaatioContext context) {
        final boolean result = wrapped.checkAccess(context.ooid, wrapped.ROLE_CRUD, wrapped.ROLE_RU);
        LOGGER.debug("userCanUpdateHakukohde({}):{}", context, result);
        return result;
    }

    /**
     * Checks if user can update hakukohde when haku started status is known
     *
     * @param context
     * @return
     */
    public boolean userCanUpdateHakukohde(final OrganisaatioContext context, final boolean hakuStarted) {
        boolean result = wrapped.checkAccess(context.ooid, wrapped.ROLE_CRUD, wrapped.ROLE_RU) && !hakuStarted;

        //OPH user can edit even if haku has started
        if (!result) {
            result = wrapped.checkAccess(rootOrgOid, wrapped.ROLE_CRUD, wrapped.ROLE_RU);
        }
        LOGGER.debug("userCanUpdateHakukohde({}):{}", context, result);
        return result;
    }
    
    /**
     * Checks if user can update koulutus.
     *
     * @return
     */
    public boolean userCanUpdateKoulutus(final OrganisaatioContext context, boolean hakuStarted) {
        boolean result = wrapped.checkAccess(context.ooid, wrapped.ROLE_RU, wrapped.ROLE_CRUD);
        
        if (!result) {
            result = wrapped.checkAccess(rootOrgOid, wrapped.ROLE_CRUD, wrapped.ROLE_RU);
        }
        
        LOGGER.debug("userCanUpdateKoulutus({}):{}", context, result);
        return result;
    }

    /**
     * Checks if user can update koulutus.
     *
     * @return
     */
    public boolean userCanUpdateKoulutus(final OrganisaatioContext context) {
        final boolean result = wrapped.checkAccess(context.ooid, wrapped.ROLE_RU, wrapped.ROLE_CRUD);
        LOGGER.debug("userCanUpdateKoulutus({}):{}", context, result);
        return result;
    }

    /**
     * Checks if user can copy koulutus as new.
     *
     * @param context
     * @return
     */
    public boolean userCanCopyKoulutusAsNew(OrganisaatioContext context) {
        return wrapped.checkAccess(context.ooid, wrapped.ROLE_CRUD);
    }

    /**
     * Checks if user can move koulutus
     *
     * @param context
     * @return
     */
    public boolean userCanMoveKoulutus(OrganisaatioContext context) {
        return wrapped.checkAccess(context.ooid, wrapped.ROLE_CRUD);
    }

    /**
     * Checks if user can add new "koulutus instance"
     *
     * @param context
     * @return
     */
    public boolean userCanAddKoulutusInstanceToKoulutus(OrganisaatioContext context) {
        return wrapped.checkAccess(context.ooid, wrapped.ROLE_CRUD);
    }

    /**
     * Checks if user can copy hakukohde as new.
     */
    public boolean userCanCopyHakukohdAsNew(OrganisaatioContext context) {
        return wrapped.checkAccess(context.ooid, wrapped.ROLE_CRUD);
    }

    /**
     * Checks if user can delete hakukohde from koulutus.
     *
     * @param context
     * @return
     */
    public boolean userCanDeleteHakukohdeFromKoulutus(OrganisaatioContext context) {
        return wrapped.checkAccess(context.ooid, wrapped.ROLE_CRUD);
    }

    /**
     * Check if user can add koulutus to hakukohde.
     *
     * @param context
     * @return
     */
    public boolean userCanAddKoulutusToHakukohde(OrganisaatioContext context) {
        return wrapped.checkAccess(context.ooid, wrapped.ROLE_CRUD);
    }

    public boolean userCanPublishCancelledKoulutus() {
        return wrapped.checkAccess(rootOrgOid, wrapped.ROLE_RU, wrapped.ROLE_CRUD);
    }

    /**
     * Check if user can delete haku.
     *
     * @return
     */
    public boolean userCanDeleteHaku() {
        return wrapped.checkAccess(rootOrgOid, wrapped.ROLE_CRUD);
    }

    /**
     * Check if user can delete haku.
     *
     * @return
     */
    public boolean userCanCreateHaku() {
        boolean userCanCreateHalku = wrapped.checkAccess(rootOrgOid, wrapped.ROLE_CRUD);
        LOGGER.debug("userCanCreateHaku:" + userCanCreateHalku);
        return userCanCreateHalku;
    }

    /**
     * Check if user can edit haku.
     *
     * @return
     */
    public boolean userCanUpdateHaku() {
        boolean userCanEditHaku = wrapped.checkAccess(rootOrgOid, wrapped.ROLE_RU, wrapped.ROLE_CRUD);
        LOGGER.debug("userCanEditHaku:" + userCanEditHaku);
        return userCanEditHaku;
    }

    /**
     * Check if user can publish haku.
     *
     * @return
     */
    public boolean userCanPublishHaku() {
        return wrapped.checkAccess(rootOrgOid, wrapped.ROLE_RU, wrapped.ROLE_CRUD);
    }

    /**
     * Check if user can Cancel haku publishment.
     *
     * @return
     */
    public boolean userCanCancelHakuPublish() {
        return wrapped.checkAccess(rootOrgOid, wrapped.ROLE_RU, wrapped.ROLE_CRUD);
    }

    /**
     * Check if user can edit valintaperustekuvaus/sorakuvaus
     *
     * @return
     */
    public boolean userCanEditValintaperustekuvaus() {
        return wrapped.checkAccess(rootOrgOid, wrapped.ROLE_CRUD);
    }

    /**
     * This is used by the "luo koulutusmoduulit" button in the ui.
     *
     * @return
     */
    public boolean userCanCreateKoulutusmoduuli() {
        return wrapped.checkAccess(rootOrgOid, wrapped.ROLE_CRUD);
    }

     /**
     * Check if user can see unfinished development features.
     *
     * @return
     */
    public boolean underConstruction() {
        return wrapped.checkAccess(rootOrgOid, wrapped.ROLE_CRUD);
    }
}
