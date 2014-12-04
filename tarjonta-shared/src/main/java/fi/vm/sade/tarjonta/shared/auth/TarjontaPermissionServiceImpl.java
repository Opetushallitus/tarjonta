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

    public static final String VALINTAPERUSTEKUVAUSTENHALLINTA = "VALINTAPERUSTEKUVAUSTENHALLINTA";
    public static final String VALINTAPERUSTEKUVAUSTENHALLINTA_KK = "VALINTAPERUSTEKUVAUSTENHALLINTA_KK";
    public static final String HAKUJENHALLINTA = "HAKUJENHALLINTA";

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

    @Component
    public static class VPermissionService extends AbstractPermissionService {

        public VPermissionService() {
            super (VALINTAPERUSTEKUVAUSTENHALLINTA);
        }
    }

    @Component
    public static class ValintaperustekuvaustenhallintaKKPermissionService extends AbstractPermissionService {

        public ValintaperustekuvaustenhallintaKKPermissionService() {
            super (VALINTAPERUSTEKUVAUSTENHALLINTA_KK);
        }
    }

    @Component
    public static class HakujenHallintaPermissionService extends AbstractPermissionService {

        public HakujenHallintaPermissionService() {
            super(HAKUJENHALLINTA);
        }
    }

    @Autowired
    TPermissionService wrapped;

    @Autowired
    VPermissionService vWrapped;

    @Autowired
    ValintaperustekuvaustenhallintaKKPermissionService valintaperustekuvaustenhallintaKKWrapped;

    @Autowired
    HakujenHallintaPermissionService hakujenHallintaPermissionServiceWrapped;

    @Override
    public void afterPropertiesSet() throws Exception {
        Preconditions.checkNotNull(wrapped, "The permissionService is not set");
    }

    /**
     * Checks if user can cancel koulutus publishment. Takes into account hakuaika.
     *
     * @param org
     * @return
     */
    public boolean userCanCancelKoulutusPublish(final OrganisaatioContext context, boolean hakuStarted) {
       return genericCanChangeTarjonta(context, hakuStarted, wrapped.ROLE_CRUD);
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

    public boolean userCanDeleteHakukohde(final OrganisaatioContext context, boolean hakuStarted) {
        return genericCanChangeTarjonta(context, hakuStarted, wrapped.ROLE_CRUD);
    }

    /**
     * Checks if user can delete koulutus.
     *
     * @return
     */
    public boolean userCanDeleteKoulutus(final OrganisaatioContext context) {
        return wrapped.checkAccess(context.ooid, wrapped.ROLE_CRUD);
    }

    public boolean userCanCreateValintaperuste() {
        return vWrapped.userCanCreateReadUpdateAndDelete();
    }

    public boolean userCanCreateValintaperusteKK() {
        return valintaperustekuvaustenhallintaKKWrapped.userCanCreateReadUpdateAndDelete();
    }

    public boolean userCanUpdateValinteperuste() {
        return vWrapped.userCanReadAndUpdate();
    }

    public boolean userCanUpdateValinteperusteKK() {
        return valintaperustekuvaustenhallintaKKWrapped.userCanReadAndUpdate();
    }

    public boolean userCanDeleteValintaperuste() {
        return vWrapped.userCanCreateReadUpdateAndDelete();
    }

    public boolean userCanDeleteValintaperusteKK() {
        return valintaperustekuvaustenhallintaKKWrapped.userCanCreateReadUpdateAndDelete();
    }

    /**
     * Checks if user can delete koulutus, takes into account hakuaika.
     *
     * @return
     */
    public boolean userCanDeleteKoulutus(final OrganisaatioContext context, boolean hakuStarted) {
        return genericCanChangeTarjonta(context, hakuStarted, wrapped.ROLE_CRUD);
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
     * Checks if user can publish koulutus.
     *
     * @param org
     * @return
     */
    public boolean userCanPublishKoulutus(final OrganisaatioContext context, boolean hakuStarted) {
        return genericCanChangeTarjonta(context, hakuStarted, wrapped.ROLE_CRUD);
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
        return genericCanChangeTarjonta(context, hakuStarted, wrapped.ROLE_CRUD, wrapped.ROLE_RU);
    }

    /**
     * Checks if user can update koulutus, takes into account hakuStarted.
     *
     * @return
     */
    public boolean userCanUpdateKoulutus(final OrganisaatioContext context, boolean hakuStarted) {
        return genericCanChangeTarjonta(context, hakuStarted, wrapped.ROLE_RU, wrapped.ROLE_CRUD);
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
     * Checks if user can delete hakukohde from koulutus, takes into account hakuaika.
     *
     * @param context
     * @return
     */
    public boolean userCanDeleteHakukohdeFromKoulutus(OrganisaatioContext context, boolean hakuStarted) {
        return genericCanChangeTarjonta(context, hakuStarted, wrapped.ROLE_CRUD);
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
     * This is true if user has role HAKUJENHALLINTA_CRUD for root oid OR that role.
     *
     * TODO
     * - YHTEISHAKU - only root oid user / OPH
     * - others - only root oid user / OPH AND if that user belongs to creators organisation.
     * @deprecated use {@link #userCanDeleteHakuWithOrgs(String...)}
     *
     * @param hakuOid haku to delete
     * @return
     */
    @Deprecated
    public boolean userCanDeleteHaku(String hakuOid) {
        // First check Tarojonta OPH user
        boolean userCanDeleteHalku = wrapped.checkAccess(rootOrgOid, wrapped.ROLE_CRUD);

        // Or hakujen hallinta root user?
        userCanDeleteHalku = userCanDeleteHalku || hakujenHallintaPermissionServiceWrapped.checkAccess(rootOrgOid, hakujenHallintaPermissionServiceWrapped.ROLE_CRUD);

        // Or hakujen hallinta crud then?
        // TODO add organisation check for hakujenhallinta org!
        userCanDeleteHalku = userCanDeleteHalku || hakujenHallintaPermissionServiceWrapped.userCanCreateReadUpdateAndDelete();

        LOGGER.info("userCanDeleteHaku:" + userCanDeleteHalku);
        return userCanDeleteHalku;
        // return wrapped.checkAccess(rootOrgOid, wrapped.ROLE_CRUD);
    }

    /**
     * Check if user can create/delete haku.
     *
     * This is true if user has role HAKUJENHALLINTA_CRUD for root oid OR that role.
     *
     * TODO
     * - YHTEISHAKU - only root oid user / OPH
     * - others - only root oid user / OPH AND if that user belongs to creators organisation.
     * @deprecated use {@link #userCanCreateHakuWithOrgs(String...)} instead
     * @return
     */
    @Deprecated
    public boolean userCanCreateHaku() {
        // First check Tarojonta OPH user
        boolean userCanCreateHalku = wrapped.checkAccess(rootOrgOid, wrapped.ROLE_CRUD);

        // Or hakujen hallinta root user?
        userCanCreateHalku = userCanCreateHalku || hakujenHallintaPermissionServiceWrapped.checkAccess(rootOrgOid, hakujenHallintaPermissionServiceWrapped.ROLE_CRUD);

        // Or hakujen hallinta crud then?
        // TODO add organisation check for hakujenhallinta org!
        userCanCreateHalku = userCanCreateHalku || hakujenHallintaPermissionServiceWrapped.userCanCreateReadUpdateAndDelete();

        LOGGER.info("userCanCreateHaku:" + userCanCreateHalku);
        return userCanCreateHalku;
        // return wrapped.checkAccess(rootOrgOid, wrapped.ROLE_CRUD);
    }

    /**
     * Check if user can edit haku.
     *
     * TODO
     * - YHTEISHAKU - onlu "oph" user
     * - other hakus - has CRUD + haku belongs to that organisation
     *
     * @param hakuOid haku to edit
     * @deprecated älä käytä tätä, vaan {@link #userCanUpdateHakuWithOrgs(String...)}
     * @return
     */
    @Deprecated
    public boolean userCanUpdateHaku(String hakuOid) {
        // First check Tarojonta OPH user
        boolean userCanUpdateHalku = wrapped.checkAccess(rootOrgOid, wrapped.ROLE_CRUD);

        // Or hakujen hallinta root user?
        userCanUpdateHalku = userCanUpdateHalku || hakujenHallintaPermissionServiceWrapped.checkAccess(rootOrgOid, hakujenHallintaPermissionServiceWrapped.ROLE_CRUD);

        // Or hakujen hallinta crud then?
        // TODO add organisation check for hakujenhallinta org!
        userCanUpdateHalku = userCanUpdateHalku || hakujenHallintaPermissionServiceWrapped.checkAccess(hakujenHallintaPermissionServiceWrapped.ROLE_CRUD);

        // Or hakujen hallinta Update access?
        // TODO add organisation check for hakujenhallinta org!
        userCanUpdateHalku = userCanUpdateHalku || hakujenHallintaPermissionServiceWrapped.userCanReadAndUpdate();

        LOGGER.info("userCanUpdateHaku:" + userCanUpdateHalku);
        return userCanUpdateHalku;

//        boolean userCanEditHaku = wrapped.checkAccess(rootOrgOid, wrapped.ROLE_RU, wrapped.ROLE_CRUD);
//        LOGGER.debug("userCanEditHaku:" + userCanEditHaku);
//        return userCanEditHaku;
    }

    /**
     * Check if user can edit haku based on organisations.
     *
     *
     * @param hakuOrgs organisaatiot jotka saa muokata hakua
     * @return
     */
    public boolean userCanUpdateHakuWithOrgs(String... hakuOrgs) {
        
        for(String org: hakuOrgs) {
            final boolean isAllowed = hakujenHallintaPermissionServiceWrapped.checkAccess(org, hakujenHallintaPermissionServiceWrapped.ROLE_RU, hakujenHallintaPermissionServiceWrapped.ROLE_CRUD);
            if(isAllowed) { //update/crud organisaatioon
                return true;
            }
        }
        return false;
    }

    /**
     * Check if user create haku based on organisations.
     *
     *
     * @param hakuOrgs organisaatiot jotka saa muokata hakua
     * @return
     */
    public boolean userCanCreateHakuWithOrgs(String... hakuOrgs) {
        
        for(String org: hakuOrgs) {
            final boolean isAllowed = hakujenHallintaPermissionServiceWrapped.checkAccess(org, hakujenHallintaPermissionServiceWrapped.ROLE_CRUD);
            if(isAllowed) { //update/crud organisaatioon
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if user create haku based on organisations.
     *
     *
     * @param hakuOrgs organisaatiot jotka saa muokata hakua
     * @return
     */
    public boolean userCanDeleteHakuWithOrgs(String... hakuOrgs) {
        return userCanCreateHakuWithOrgs(hakuOrgs); //c & d are combined!
    }


    /**
     * Check if user can publish haku.
     *
     * TODO
     * - YHTEISHAKU - onlu "oph" user
     * - other hakus - has CRUD + haku belongs to that organisation
     *
     * @deprecated user {@link #userCanUpdateHakuWithOrgs(String...)} instead...
     * @param hakuOid haku to publish
     * @return
     */
    @Deprecated
    public boolean userCanPublishHaku(String hakuOid) {
        // First check Tarojonta OPH user
        boolean userCanPublishHalku = wrapped.checkAccess(rootOrgOid, wrapped.ROLE_CRUD);

        // Or hakujen hallinta root user?
        userCanPublishHalku = userCanPublishHalku || hakujenHallintaPermissionServiceWrapped.checkAccess(rootOrgOid, hakujenHallintaPermissionServiceWrapped.ROLE_CRUD);

        // Or hakujen hallinta crud then?
        // TODO add organisation check for hakujenhallinta org!
        userCanPublishHalku = userCanPublishHalku || hakujenHallintaPermissionServiceWrapped.checkAccess(hakujenHallintaPermissionServiceWrapped.ROLE_CRUD);

        LOGGER.info("userCanUpdateHaku:" + userCanPublishHalku);
        return userCanPublishHalku;

        // return wrapped.checkAccess(rootOrgOid, wrapped.ROLE_RU, wrapped.ROLE_CRUD);
    }

    /**
     * Check if user can Cancel haku publishment.
     * @deprecated use {@link #userCanUpdateHakuWithOrgs(String...)} instead.
     * @return
     */
    @Deprecated
    public boolean userCanCancelHakuPublish(String hakuOid) {
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

    @Deprecated
    public boolean userIsOphCrud() {
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

    private boolean genericCanChangeTarjonta(final OrganisaatioContext context, boolean hakuStarted, String... roles) {
        boolean result = wrapped.checkAccess(context.ooid, roles) && !hakuStarted;
        if (!result) {
            result = wrapped.checkAccess(rootOrgOid, roles);
        }
        return result;
    }

}
