/*
 * Copyright (c) 2014 The Finnish Board of Education - Opetushallitus
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
package fi.vm.sade.tarjonta.service.auth;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.impl.HakukohdeDAOImpl;
import fi.vm.sade.tarjonta.dao.impl.KoulutusmoduuliToteutusDAOImpl;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.HakukohdeLiite;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.Valintakoe;
import fi.vm.sade.tarjonta.service.types.GeneerinenTilaTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaTilaTyyppi;
import fi.vm.sade.tarjonta.shared.ParameterServices;
import fi.vm.sade.tarjonta.shared.auth.OrganisaatioContext;
import fi.vm.sade.tarjonta.shared.auth.TarjontaPermissionServiceImpl;

@Component
public class PermissionChecker {

    @Autowired
    TarjontaPermissionServiceImpl permissionService;
    @Autowired
    HakukohdeDAOImpl hakukohdeDaoImpl;
    @Autowired
    KoulutusmoduuliToteutusDAOImpl koulutusmoduuliToteutusDAOImpl;
    @Autowired
    KoulutusmoduuliDAO koulutusmoduuliDAOImpl;
    @Autowired
    ParameterServices parameterServices;

    /**
     * Saako koulutuksen kopioida.
     * @param organisaatioOids
     */
    public void checkCopyKoulutus(final List<String> organisaatioOids) {
        for (String orgOid : organisaatioOids) {
            checkPermission(permissionService
                    .userCanCopyKoulutusAsNew(OrganisaatioContext
                            .getContext(orgOid)));
        }
    }

    private void checkPermission(final boolean result, final String... message) {
        final String msg = message.length==1?message[0]:"no.permission"; 
        if (!result) {
            throw new NotAuthorizedException(msg);
        }
    }

    /**
     * @deprecated use {@link #checkHakuUpdateWithOrgs(String...)}
     * @param hakuOid
     */
    public void checkHakuUpdate(final String hakuOid) {
        checkPermission(permissionService.userCanUpdateHaku(hakuOid));
    }

    public void checkHakuUpdateWithOrgs(String... orgs) {
        checkPermission(permissionService.userCanUpdateHakuWithOrgs(orgs));
    }

    /**
     * Saako käyttäjä muokata hakukohdetta, huom tämä ei ota huomioon parametreja!!!!!!
     * @param hakukohdeOid
     */
    public void checkUpdateHakukohde(String hakukohdeOid) {
        Hakukohde hakukohde = hakukohdeDaoImpl.findHakukohdeByOid(hakukohdeOid);
        Set<KoulutusmoduuliToteutus> komot = hakukohde
                .getKoulutusmoduuliToteutuses();
        if (komot.size() > 0) {
            checkPermission(permissionService
                    .userCanUpdateHakukohde(OrganisaatioContext
                            .getContext(komot.iterator().next().getTarjoaja())));
        } // hakukohde must always have komoto?
    }

    
    /**
     * Saako käyttäjä muokata hakukohdetta, ottaa huomioon hakuparametrit, tarkistaa koulutus lisäykset poistot
     * XXX HJVO-55 suodata pois haut joihin ei saa koskea (permissiot!) älä anna lisätä/poistaa koulutuksia jos parametri estäää
     * 
     * @param hakukohdeOid hakukohteen oid jota ollaan muokkaamassa
     * @param targetHakuOid hakukohteen oid (muutettavissa rajapinann kautta)
     * @param newKoulutusOids lista koulutusoideja jotka ilmoitettu rajapinnassa
     */
    public void checkUpdateHakukohde(String hakukohdeOid, String targetHakuOid, Collection<String> newKoulutusOids) {

        Preconditions.checkArgument(newKoulutusOids.size()>0, "hakukohde without komotos");


        final Hakukohde currentHakukohde = hakukohdeDaoImpl.findHakukohdeByOid(hakukohdeOid);
        final Haku currentHaku = currentHakukohde.getHaku();

        // 1. rajapinnassa hakukohde voidaan "siirtää" hausta toiseen, jolloin pitää tarkistaa uusi haku + vanha haku
        
        //jos "kohde" haku lukittu voidaan failata heti TODO: jos ei oph
        if(!currentHaku.getOid().equals(targetHakuOid)){
            checkPermission(parameterServices.parameterCanAddHakukohdeToHaku(targetHakuOid));
        } 
        
        final Set<KoulutusmoduuliToteutus> komot = currentHakukohde
                .getKoulutusmoduuliToteutuses();

        
        if(!parameterServices.parameterCanAddHakukohdeToHaku(targetHakuOid) && !parameterServices.parameterCanRemoveHakukohdeFromHaku(targetHakuOid)){
            Set<String> komotoOids = Sets.newHashSet();
            for(KoulutusmoduuliToteutus komoto: komot) {
                komotoOids.add(komoto.getOid());
            }
            
            //tarkista että hakukohteen koulutussetti ei ole muuttunut
            checkPermission(newKoulutusOids.size()==komotoOids.size() && Sets.intersection(komotoOids, Sets.newHashSet(newKoulutusOids)).size()==newKoulutusOids.size());
        }
        
        
        if (komot.size() > 0) {
            checkPermission(permissionService
                    .userCanUpdateHakukohde(OrganisaatioContext
                            .getContext(komot.iterator().next().getTarjoaja())));
        } else {
            throw new RuntimeException("hakukohde without komos" + hakukohdeOid);
        }
    }
    
    public void checkCreateHakukohde(String hakuOid, List<String> komotoOids) {
        //tarkista että koulutuksia on
        Preconditions.checkArgument(komotoOids.size()>0, "hakukohde without komotos");

        //saako hakuun liittää hakukohteita
        checkPermission(parameterServices.parameterCanAddHakukohdeToHaku(hakuOid));
        
        for(String koulutusOid: komotoOids) {
            final KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAOImpl.findByOid(koulutusOid);
            checkPermission(permissionService.userCanCreateHakukohde(OrganisaatioContext.getContext(komoto.getTarjoaja())));
        }
    }
    
    public void checkUpdateHakukohdeByHakukohdeliiteTunniste(
            String hakukohdeLiiteTunniste) {
        HakukohdeLiite liite = hakukohdeDaoImpl
                .findHakuKohdeLiiteById(hakukohdeLiiteTunniste);
        Set<KoulutusmoduuliToteutus> komot = liite.getHakukohde()
                .getKoulutusmoduuliToteutuses();
        if (komot.size() > 0) {
            checkPermission(permissionService
                    .userCanUpdateHakukohde(OrganisaatioContext
                            .getContext(komot.iterator().next().getTarjoaja())));
        } // hakukohde must always have komoto?
    }

    public void checkUpdateHakukohdeByValintakoeTunniste(String valintakoeTunniste) {
        Preconditions.checkNotNull(valintakoeTunniste, "Valintakoe tunniste cannot be null.");

        final Valintakoe valintakoe = hakukohdeDaoImpl.findValintaKoeById(valintakoeTunniste);
        final Hakukohde hakukohde = hakukohdeDaoImpl.read(valintakoe.getHakukohde().getId());
        final Set<KoulutusmoduuliToteutus> komot = hakukohde.getKoulutusmoduuliToteutuses();
        Preconditions.checkArgument(komot.size()>0, "Hakukohde cannot exist without koulutus");
        //XXX why are we checking only first org???
        checkPermission(permissionService.userCanUpdateHakukohde(OrganisaatioContext.getContext(komot.iterator().next().getTarjoaja())));
    }

    public void checkRemoveHakukohde(String hakukohdeOid) {
        final Hakukohde hakukohde = hakukohdeDaoImpl.findHakukohdeByOid(hakukohdeOid);

        
        final Set<KoulutusmoduuliToteutus> komot = hakukohde
                .getKoulutusmoduuliToteutuses();
        Preconditions.checkArgument(komot.size()>0, "Hakukohde cannot exist without koulutus");
        //XXX why are we checking only first org???
        checkPermission(permissionService.userCanDeleteHakukohde(OrganisaatioContext.getContext(komot.iterator().next().getTarjoaja())));
        
        checkPermission(parameterServices.parameterCanRemoveHakukohdeFromHaku(hakukohde.getHaku().getOid()), "error.parameters.deny.removal");
    }

    /**
     * @deprecated use {@link #checkCreateHakuWithOrgs(String...)} 
     */
    public void checkCreateHaku() {
        checkPermission(permissionService.userCanCreateHaku());
    }

    public void checkCreateHakuWithOrgs(String... hakuOrgs) {
        checkPermission(permissionService.userCanCreateHakuWithOrgs(hakuOrgs));
    }

    public void checkCreateValintaPeruste() {

        checkPermission(permissionService.userCanCreateValintaperuste());
    }

    /**
     * @deprecated use {@link #checkRemoveHakuWithOrgs(String...)}
     * @param hakuOid
     */
    public void checkRemoveHaku(String hakuOid) {
        checkPermission(permissionService.userCanDeleteHaku(hakuOid));
    }

    public void checkRemoveHakuWithOrgs(String... orgs) {
        checkPermission(permissionService.userCanDeleteHakuWithOrgs(orgs));
    }

    public void checkRemoveValintaPeruste() {
        checkPermission(permissionService.userCanDeleteValintaperuste());
    }

    public void checkCreateKoulutus(String tarjoajaOid) {
        checkPermission(permissionService
                .userCanCreateKoulutus(OrganisaatioContext
                        .getContext(tarjoajaOid)));
    }

    public void checkAddKoulutusKuva(String tarjoajaOid) {
        checkPermission(permissionService
                .userCanCreateKoulutus(OrganisaatioContext
                        .getContext(tarjoajaOid)));
    }

    public void checkUpdateKoulutusByTarjoajaOid(String tarjoajaOid) {
        checkPermission(permissionService
                .userCanUpdateKoulutus(OrganisaatioContext
                        .getContext(tarjoajaOid)));
    }

    public void checkRemoveKoulutus(String koulutusOid) {
        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAOImpl.findByOid(koulutusOid);

        checkPermission(permissionService
                .userCanDeleteKoulutus(OrganisaatioContext.getContext(komoto
                                .getTarjoaja())));
    }

    public void checkRemoveKoulutusByTarjoaja(final String tarjoajaOid) {
        checkPermission(permissionService
                .userCanDeleteKoulutus(OrganisaatioContext.getContext(tarjoajaOid)));
    }

    public void checkRemoveKoulutusKuva(String koulutusOid) {
        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAOImpl.findByOid(koulutusOid);
        checkPermission(permissionService
                .userCanDeleteKoulutus(OrganisaatioContext.getContext(komoto
                                .getTarjoaja())));
    }

    public void checkCreateKoulutusmoduuli() {
        checkPermission(permissionService.userCanCreateKoulutusmoduuli());
    }

    public void checkUpdateKoulutusmoduuli() {
        checkPermission(permissionService.userCanCreateKoulutusmoduuli());
    }

    public void checkCreateKoulutusmoduuli(String komoOid) {
        Koulutusmoduuli komo = koulutusmoduuliDAOImpl.findByOid(komoOid);

        checkPermission(permissionService
                .userCanDeleteKoulutus(OrganisaatioContext.getContext(komo.getOmistajaOrganisaatioOid())));
    }

    /**
     * @deprecated used only in vaadin
     * @param tarjontatiedonTila
     */
    public void checkTilaUpdate(PaivitaTilaTyyppi tarjontatiedonTila) {
        for (GeneerinenTilaTyyppi tyyppi : tarjontatiedonTila.getTilaOids()) {
            switch (tyyppi.getSisalto()) {

                case HAKU:
                    checkHakuUpdate(tyyppi.getOid());
                    break;
                case HAKUKOHDE:
                    checkUpdateHakukohde(tyyppi.getOid());
                    break;
                case KOMO:
                    break; // TODO XXX currently no permission check for this
                case KOMOTO:
                    checkUpdateKoulutusByKoulutusOid(tyyppi.getOid());
                    break;
            }
        }
    }

    public void checkUpdateKoulutusByKoulutusOid(String oid) {
        final KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAOImpl.findByOid(oid);
        checkUpdateKoulutusByTarjoajaOid(komoto.getTarjoaja());
    }

    public void checkUpdateValintaperustekuvaus() {
        checkPermission(permissionService.userCanUpdateValinteperuste());
    }

    /**
     * 
     * @param orgOids
     */
    public void checkUpdateHaku(String... orgOids) {
        checkPermission(permissionService.userCanUpdateHakuWithOrgs(orgOids));
    }
    
}
