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
package fi.vm.sade.tarjonta.service.auth;

import com.google.common.base.Preconditions;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.vm.sade.tarjonta.dao.impl.HakukohdeDAOImpl;
import fi.vm.sade.tarjonta.dao.impl.KoulutusmoduuliToteutusDAOImpl;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.HakukohdeLiite;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.Valintakoe;
import fi.vm.sade.tarjonta.service.types.GeneerinenTilaTyyppi;
import fi.vm.sade.tarjonta.service.types.HakukohdeTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaTilaTyyppi;
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

    /**
     *
     * @param organisaatioOids
     */
    public void checkCopyKoulutus(List<String> organisaatioOids) {
        for (String orgOid : organisaatioOids) {
            checkPermission(permissionService
                    .userCanCopyKoulutusAsNew(OrganisaatioContext
                            .getContext(orgOid)));
        }
    }

    public void checkCopyKoulutusTarjoaja(final String tarjoajaOid) {
        checkUpdateKoulutusByTarjoajaOid(tarjoajaOid);
    }

    private void checkPermission(boolean result) {
        if (!result) {
            throw new NotAuthorizedException("no.permission");
        }
    }

    /**
     * @deprecated use {@link #checkHakuUpdateWithOrgs(String...)}
     * @param hakuOid
     */
    public void checkHakuUpdate(String hakuOid) {
        checkPermission(permissionService.userCanUpdateHaku(hakuOid));
    }

    public void checkHakuUpdateWithOrgs(String... orgs) {
        checkPermission(permissionService.userCanUpdateHakuWithOrgs(orgs));
    }

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
        Valintakoe valintakoe = hakukohdeDaoImpl.findValintaKoeById(valintakoeTunniste);
        Hakukohde hakukohde = hakukohdeDaoImpl.read(valintakoe.getHakukohde().getId());
        Set<KoulutusmoduuliToteutus> komot = hakukohde.getKoulutusmoduuliToteutuses();
        if (komot.size() > 0) {
            checkPermission(permissionService.userCanUpdateHakukohde(OrganisaatioContext.getContext(komot.iterator().next().getTarjoaja())));
        } // hakukohde must always have komoto? -> YES
    }

    public void checkCreateHakukohde(HakukohdeTyyppi hakukohde) {
        List<KoulutusKoosteTyyppi> komot = hakukohde.getHakukohdeKoulutukses();
        if (komot.size() > 0) {
            checkPermission(permissionService
                    .userCanUpdateHakukohde(OrganisaatioContext
                            .getContext(komot.iterator().next().getTarjoaja())));
        } // hakukohde must always have komoto? -> YES
    }

    public void checkCreateHakukohde(List<String> komotoOids) {

        List<KoulutusmoduuliToteutus> komot = new ArrayList<KoulutusmoduuliToteutus>();
        for (String komotoOid : komotoOids) {
            KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAOImpl.findByOid(komotoOid);
            Preconditions.checkArgument(komoto != null, "No such komoto: %s", komotoOid);
            komot.add(komoto);
        }

        if (komot.size() > 0) {
            checkPermission(permissionService.userCanUpdateHakukohde(OrganisaatioContext.getContext(komot.iterator().next().getTarjoaja())));
        }
    }

    public void checkRemoveHakukohde(String hakukohdeOid) {
        Hakukohde hakukohde = hakukohdeDaoImpl.findHakukohdeByOid(hakukohdeOid);
        Set<KoulutusmoduuliToteutus> komot = hakukohde
                .getKoulutusmoduuliToteutuses();

        System.out.println(" HAKUKOHDE KOMOTOS SIZE :  " + komot.size());
        if (komot.size() > 0) {
            checkPermission(permissionService
                    .userCanDeleteHakukohde(OrganisaatioContext
                            .getContext(komot.iterator().next().getTarjoaja())));
        } // hakukohde must always have komoto?
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
