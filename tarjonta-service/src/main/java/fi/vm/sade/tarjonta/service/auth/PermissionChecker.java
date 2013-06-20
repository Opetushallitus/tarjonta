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

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.vm.sade.tarjonta.dao.impl.HakukohdeDAOImpl;
import fi.vm.sade.tarjonta.dao.impl.KoulutusmoduuliToteutusDAOImpl;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.HakukohdeLiite;
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

    private void checkPermission(boolean result) {
        if (!result) {
            throw new NotAuthorizedException("no.permission");
        }
    }

    public void checkHakuUpdate() {
        checkPermission(permissionService.userCanUpdateHaku());
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

    public void checkUpdateHakukohdeByValintakoeTunniste(
            String valintakoeTunniste) {
        Valintakoe valintakoe = hakukohdeDaoImpl
                .findValintaKoeById(valintakoeTunniste);
        Hakukohde hakukohde = hakukohdeDaoImpl
                .read(valintakoe.getHakukohdeId());
        Set<KoulutusmoduuliToteutus> komot = hakukohde
                .getKoulutusmoduuliToteutuses();
        if (komot.size() > 0) {
            checkPermission(permissionService
                    .userCanUpdateHakukohde(OrganisaatioContext
                            .getContext(komot.iterator().next().getTarjoaja())));
        } // hakukohde must always have komoto?
    }

    public void checkCreateHakukohde(HakukohdeTyyppi hakukohde) {
        List<KoulutusKoosteTyyppi> komot = hakukohde.getHakukohdeKoulutukses();
        if (komot.size() > 0) {
            checkPermission(permissionService
                    .userCanUpdateHakukohde(OrganisaatioContext
                            .getContext(komot.iterator().next().getTarjoaja())));
        } // hakukohde must always have komoto?
    }

    public void checkRemoveHakukohde(String hakukohdeOid) {
        Hakukohde hakukohde = hakukohdeDaoImpl.findHakukohdeByOid(hakukohdeOid);
        Set<KoulutusmoduuliToteutus> komot = hakukohde
                .getKoulutusmoduuliToteutuses();
        if (komot.size() > 0) {
            checkPermission(permissionService
                    .userCanDeleteHakukohde(OrganisaatioContext
                            .getContext(komot.iterator().next().getTarjoaja())));
        } // hakukohde must always have komoto?
    }

    public void checkCreateHaku() {
        checkPermission(permissionService.userCanCreateHaku());
    }

    public void checkRemoveHaku() {
        checkPermission(permissionService.userCanDeleteHaku());
    }

    public void checkCreateKoulutus(String tarjoajaOid) {
        checkPermission(permissionService
                .userCanCreateKoulutus(OrganisaatioContext
                        .getContext(tarjoajaOid)));
    }

    public void checkUpdateKoulutus(String tarjoajaOid) {
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

    public void checkCreateKoulutusmoduuli() {
        checkPermission(permissionService.userCanCreateKoulutusmoduuli());
    }

    public void checkUpdateKoulutusmoduuli() {
        checkPermission(permissionService.userCanCreateKoulutusmoduuli());
    }

    public void checkTilaUpdate(PaivitaTilaTyyppi tarjontatiedonTila) {
        for (GeneerinenTilaTyyppi tyyppi : tarjontatiedonTila.getTilaOids()) {
            switch (tyyppi.getSisalto()) {

            case HAKU:
                checkHakuUpdate();
                break;
            case HAKUKOHDE:
                checkUpdateHakukohde(tyyppi.getOid());
                break;
            case KOMO:
                break; // XXX currently no permission check for this
            case KOMOTO:
                checkUpdateKoulutus(tyyppi.getOid());
                break;
            }
        }
    }

    public void checkUpdateValintaperustekuvaus() {
        checkPermission(permissionService.userCanEditValintaperustekuvaus());
    }

}