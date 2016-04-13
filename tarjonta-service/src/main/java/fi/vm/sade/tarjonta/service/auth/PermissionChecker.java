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

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.impl.HakukohdeDAOImpl;
import fi.vm.sade.tarjonta.dao.impl.KoulutusmoduuliToteutusDAOImpl;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusIdentification;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.types.GeneerinenTilaTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaTilaTyyppi;
import fi.vm.sade.tarjonta.shared.ParameterServices;
import fi.vm.sade.tarjonta.shared.auth.OrganisaatioContext;
import fi.vm.sade.tarjonta.shared.auth.TarjontaPermissionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    @Deprecated
    public void checkHakuUpdate(final String hakuOid) {
        checkPermission(permissionService.userCanUpdateHaku(hakuOid));
    }

    public void checkHakuUpdateWithOrgs(String... orgs) {
        checkPermission(permissionService.userCanUpdateHakuWithOrgs(orgs));
    }


    // KJOH-778 monta tarjoajaa
    public boolean canEditHakukohdeMultipleOwners(Hakukohde hakukohde) {
        Map<String, KoulutusmoduuliToteutusTarjoajatiedot> tarjoajatiedot = hakukohde.getKoulutusmoduuliToteutusTarjoajatiedot();

        if (!tarjoajatiedot.isEmpty()) {
            for(KoulutusmoduuliToteutusTarjoajatiedot value : tarjoajatiedot.values()) {
                for(String tarjoajaOid : value.getTarjoajaOids()) {
                    try {
                        checkPermission(permissionService.userCanUpdateHakukohde(OrganisaatioContext.getContext(tarjoajaOid)));
                        return true;
                    }
                    catch (NotAuthorizedException e) {
                        // Do nothing
                    }
                }
            }
        }

        return false;
    }

    /**
     * Saako käyttäjä muokata hakukohdetta, huom tämä ei ota huomioon parametreja!!!!!!
     * Saa käyttää ainoastaan silloin kun koulutuksia/haun tietoja ei muuteta
     * @param hakukohdeOid
     */
    public void checkUpdateHakukohdeAndIgnoreParametersWhileChecking(String hakukohdeOid) {
        Hakukohde hakukohde = hakukohdeDaoImpl.findHakukohdeByOid(hakukohdeOid);

        if ( canEditHakukohdeMultipleOwners(hakukohde)) {
            return;
        }

        Set<KoulutusmoduuliToteutus> komot = hakukohde.getKoulutusmoduuliToteutuses();
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

        if(permissionService.userIsOphCrud()) {
            return;
        }
        Preconditions.checkArgument(newKoulutusOids.size()>0, "hakukohde without komotos");


        final Hakukohde currentHakukohde = hakukohdeDaoImpl.findHakukohdeByOid(hakukohdeOid);
        final Haku currentHaku = currentHakukohde.getHaku();

        // 1. rajapinnassa hakukohde voidaan "siirtää" hausta toiseen, jolloin pitää tarkistaa uusi haku + vanha haku

        //jos "kohde" haku lukittu voidaan failata heti
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

        if (canEditHakukohdeMultipleOwners(currentHakukohde)) {
            return;
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

        if(permissionService.userIsOphCrud()) {
            return;
        }

        //tarkista että koulutuksia on
        Preconditions.checkArgument(komotoOids.size() > 0, "hakukohde without komotos");

        //saako hakuun liittää hakukohteita
        checkPermission(parameterServices.parameterCanAddHakukohdeToHaku(hakuOid));

        for(String koulutusOid: komotoOids) {
            final KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAOImpl.findByOid(koulutusOid);
            checkPermission(permissionService.userCanCreateHakukohde(OrganisaatioContext.getContext(komoto.getTarjoaja())));
        }
    }

    public void checkCreateHakukohde(String hakuOid, String tarjoajaOid) {
        if(permissionService.userIsOphCrud()) {
            return;
        }

        //saako hakuun liittää hakukohteita
        checkPermission(parameterServices.parameterCanAddHakukohdeToHaku(hakuOid));

        checkPermission(permissionService.userCanCreateHakukohde(OrganisaatioContext.getContext(tarjoajaOid)));
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

    /**
     * Voiko hakukohteen poistaa, ottaa huomioon parametrit
     * @param hakukohdeOid
     */
    public void checkRemoveHakukohde(String hakukohdeOid) {
        if (permissionService.userIsOphCrud()) {
            return;
        }

        final Hakukohde hakukohde = hakukohdeDaoImpl.findHakukohdeByOid(hakukohdeOid);

        final Set<KoulutusmoduuliToteutus> komot = hakukohde
                .getKoulutusmoduuliToteutuses();
        Preconditions.checkArgument(komot.size() > 0, "Hakukohde cannot exist without koulutus");
        //XXX why are we checking only first org???
        checkPermission(permissionService.userCanDeleteHakukohde(OrganisaatioContext.getContext(komot.iterator().next().getTarjoaja())));

        checkPermission(parameterServices.parameterCanRemoveHakukohdeFromHaku(hakukohde.getHaku().getOid()), "error.parameters.deny.removal");
    }

    public void checkCreateHakuWithOrgs(String... hakuOrgs) {
        checkPermission(permissionService.userCanCreateHakuWithOrgs(hakuOrgs));
    }

    public void checkCreateValintaPeruste() {
        checkPermission(permissionService.userCanCreateValintaperuste());
    }

    public void checkCreateValintaPerusteKK() {
        checkPermission(permissionService.userCanCreateValintaperusteKK());
    }

    /**
     * @deprecated use {@link #checkRemoveHakuWithOrgs(String...)}
     * @param hakuOid
     */
    @Deprecated
    public void checkRemoveHaku(String hakuOid) {
        checkPermission(permissionService.userCanDeleteHaku(hakuOid));
    }

    public void checkRemoveHakuWithOrgs(String... orgs) {
        checkPermission(permissionService.userCanDeleteHakuWithOrgs(orgs));
    }

    public void checkRemoveValintaPeruste() {
        checkPermission(permissionService.userCanDeleteValintaperuste());
    }

    public void checkRemoveValintaPerusteKK() {
        checkPermission(permissionService.userCanDeleteValintaperusteKK());
    }

    public void checkUpsertKoulutus(final KoulutusV1RDTO dto) {
        KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAOImpl.findKomotoByKoulutusId(
                new KoulutusIdentification(dto.getOid(), dto.getUniqueExternalId())
        );

        if (komoto == null) {
            checkCreateKoulutus(dto.getOrganisaatio().getOid());
        } else {
            checkUpdateKoulutusByTarjoajaOid(komoto.getTarjoaja());
        }
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

    /**
     * Tarkistaa voiko koulutusta poistaa, ottaa huomioon parametrit
     * @param koulutusOid
     */
    public void checkRemoveKoulutus(final String koulutusOid) {

        if (permissionService.userIsOphCrud()) {
            return;
        }

        final KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAOImpl.findByOid(koulutusOid);
        //käyttöoikeudet
        checkPermission(permissionService
                .userCanDeleteKoulutus(OrganisaatioContext.getContext(komoto
                                .getTarjoaja())));

        //parametrit
        for(Hakukohde hk: komoto.getHakukohdes()){
            final String hakuOid = hk.getHaku().getOid();
            checkPermission(parameterServices.parameterCanEditHakukohdeLimited(hakuOid), "error.parameters.deny.removal");
        }

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
    @Deprecated
    public void checkTilaUpdate(PaivitaTilaTyyppi tarjontatiedonTila) {
        for (GeneerinenTilaTyyppi tyyppi : tarjontatiedonTila.getTilaOids()) {
            switch (tyyppi.getSisalto()) {

                case HAKU:
                    checkHakuUpdate(tyyppi.getOid());
                    break;
                case HAKUKOHDE:
                    checkUpdateHakukohdeAndIgnoreParametersWhileChecking(tyyppi.getOid());
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

    public void checkPublishOrUnpublishKomoto(String komotoOid) {
        if (isOphCrud()) {
            return;
        }
        for (Hakukohde h : hakukohdeDaoImpl.findByKoulutusOid(komotoOid)) {
            Haku haku = h.getHaku();
            if (!parameterServices.parameterCanRemoveHakukohdeFromHaku(haku.getOid())
                || !parameterServices.parameterCanAddHakukohdeToHaku(haku.getOid())) {
                throw new NotAuthorizedException("haun.parametrit.estaa.julkaisun.perumisen");
            }
        }
    }

    public void checkPublishOrUnpublishHakukohde(String hakukohdeOid) {
        if (isOphCrud()) {
            return;
        }
        Hakukohde hakukohde = hakukohdeDaoImpl.findHakukohdeByOid(hakukohdeOid);
        Haku haku = hakukohde.getHaku();
        if (!parameterServices.parameterCanRemoveHakukohdeFromHaku(haku.getOid())
                || !parameterServices.parameterCanAddHakukohdeToHaku(haku.getOid())) {
            throw new NotAuthorizedException("haun.parametrit.estaa.julkaisun.perumisen");
        }
    }

    public void checkUpdateValintaperustekuvaus() {
        checkPermission(permissionService.userCanUpdateValinteperuste());
    }

    public void checkUpdateValintaperustekuvausKK() {
        checkPermission(permissionService.userCanUpdateValinteperusteKK());
    }

    /**
     *
     * @param orgOids
     */
    public void checkUpdateHaku(String... orgOids) {
        checkPermission(permissionService.userCanUpdateHakuWithOrgs(orgOids));
    }

    /**
     * Returns true if user is OPH CRUD user.
     *
     * @return
     */
    public boolean isOphCrud() {
        return permissionService.userIsOphCrud();
    }
}
