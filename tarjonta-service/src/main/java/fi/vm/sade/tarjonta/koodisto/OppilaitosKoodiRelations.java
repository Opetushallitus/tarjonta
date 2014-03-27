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
package fi.vm.sade.tarjonta.koodisto;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioOidListType;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioOidType;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioSearchOidType;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioTyyppi;
import fi.vm.sade.tarjonta.service.impl.resources.v1.KoulutusResourceImplV1;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Jani Wilén
 */
@Component
public class OppilaitosKoodiRelations {

    private static final Logger LOG = LoggerFactory.getLogger(OppilaitosKoodiRelations.class);

    @Autowired(required = true)
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;

    @Autowired(required = true)
    private OrganisaatioService organisaatioService;

    @Value("${root.organisaatio.oid:}")
    private String rootOphOid;

    /**
     * Check if the given organization is allowed to create new education.
     *
     * @param tyyppi
     * @param organisaatioOid
     * @return
     */
    public boolean isKoulutusAllowedForOppilaitostyyppi(final String organisaatioOid, final KoulutusasteTyyppi tyyppi) {
        Preconditions.checkNotNull(tyyppi, "KoulutusasteTyyppi enum cannot be null");
        Preconditions.checkNotNull(organisaatioOid, "Organisaatio OID cannot be null");
        final OrganisaatioDTO orgDto = organisaatioService.findByOid(organisaatioOid);

        if (orgDto == null) {
            return false;
        }

        final String oidPath = orgDto.getParentOidPath();
        List<String> oids = splitOrganisationPath(oidPath);

        LOG.info("ORG PATH : {}", oidPath);

        if (rootOphOid != null && !rootOphOid.isEmpty() && orgDto.getOid() != null && rootOphOid.equals(orgDto.getOid())) {
            LOG.info("OPH ORGANISATION");
            //OPH root organisation : allow all oppilaitostyyppis
            return true;
        }

        //remove OPH OID from the list
        oids.remove(rootOphOid);

        //add selected to end of the list
        oids.add(orgDto.getOid());

        LOG.info("OIDS FOUND : {}", oids.size());

        if (oids.size() > 1) {
            //a quick way to get the type of OPPILAITOS
            if (isCorrectOppilaitostyyppis(oids.get(1), tyyppi)) {
                return true;
            }
        }

        //not found, then loop all oids
        for (String oid : oids) {
            final OrganisaatioDTO pathOrgDto = organisaatioService.findByOid(oid);
            if (pathOrgDto == null) {
                LOG.error("Data error - organisation with OID {} not found!", oid);
                continue;
            }

            if (isCorrectOrganisaatioTyyppi(pathOrgDto, OrganisaatioTyyppi.KOULUTUSTOIMIJA)) {
                //search all children organisations, one of them must be OPPILAITOS
                final OrganisaatioOidListType childrenOids = organisaatioService.findChildrenOidsByOid(new OrganisaatioSearchOidType(pathOrgDto.getOid()));
                List<String> oppilaitostyyppiUris = Lists.newArrayList();
                for (OrganisaatioOidType oidType : childrenOids.getOrganisaatioOidList()) {
                    oppilaitostyyppiUris.addAll(getAllOppilaitosTyyppisByOrganisaatioOid(oidType.getOrganisaatioOid()));
                }

                if (!oppilaitostyyppiUris.isEmpty()) {
                    for (String oppilaitostyyppiUri : oppilaitostyyppiUris) {
                        if (checkKoulutusAllowedForOppilaitostyyppi(tyyppi, oppilaitostyyppiUri)) {
                            LOG.info("Found : {} KOULUTUSTOIMIJA parent of OPPILAITOS with correct oppilaitostyyppiUri {}", oid, oppilaitostyyppiUri);
                            return true;
                        }
                    }
                }
            } else if (isCorrectOrganisaatioTyyppi(pathOrgDto, OrganisaatioTyyppi.OPPILAITOS)) {
                //search one organisation
                final List<String> oppilaitostyyppiUris = getAllOppilaitosTyyppisByOrganisaatioOid(pathOrgDto);

                if (!oppilaitostyyppiUris.isEmpty()) {
                    for (String oppilaitostyyppiUri : oppilaitostyyppiUris) {
                        if (checkKoulutusAllowedForOppilaitostyyppi(tyyppi, oppilaitostyyppiUri)) {
                            LOG.info("Found : {} OPPILAITOS with correct oppilaitostyyppi {}", oid, oppilaitostyyppiUri);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isCorrectOppilaitostyyppis(String orgOid, KoulutusasteTyyppi tyyppi) {
        List<String> oppilaitostyyppiUris = getAllOppilaitosTyyppisByOrganisaatioOid(orgOid);

        if (!oppilaitostyyppiUris.isEmpty()) {
            for (String oppilaitostyyppiUri : oppilaitostyyppiUris) {
                if (checkKoulutusAllowedForOppilaitostyyppi(tyyppi, oppilaitostyyppiUri)) {
                    LOG.info("Found (by path index) : {} OPPILAITOS with correct oppilaitostyyppi {}", orgOid, oppilaitostyyppiUri);
                    return true;
                }
            }
        }

        return false;
    }

    public static List splitOrganisationPath(String parentOidPath) {
        final List<String> list = Lists.<String>newArrayList(parentOidPath.split("[|]"));
        list.removeAll(Arrays.asList("")); //remove empty strings
        return list;
    }

    private boolean isCorrectOrganisaatioTyyppi(OrganisaatioDTO org, OrganisaatioTyyppi tyyppi) {
        for (OrganisaatioTyyppi organisaatioTyyppi : org.getTyypit()) {
            if (tyyppi.equals(organisaatioTyyppi)) {
                return true;
            }

        }
        return false;
    }

    private List<String> getOppilaitosTyyppis(OrganisaatioDTO org) {
        List<String> oppilaitostyyppis = Lists.<String>newArrayList();

        for (OrganisaatioTyyppi organisaatioTyyppi : org.getTyypit()) {
            switch (organisaatioTyyppi) {
                case OPETUSPISTE:
                    //- we are on the leaf (or one of them)
                    break;
                case KOULUTUSTOIMIJA:
                    //- we are on the root (not oph?)
                    break;
                case OPPILAITOS:
                    //success : end of the line
                    oppilaitostyyppis.add(org.getOppilaitosTyyppi());
                    break;
            }
        }

        return oppilaitostyyppis;
    }

    private List<String> getAllOppilaitosTyyppisByOrganisaatioOid(String oid) {
        return getOppilaitosTyyppis(organisaatioService.findByOid(oid));
    }

    private List<String> getAllOppilaitosTyyppisByOrganisaatioOid(OrganisaatioDTO dto) {
        return getOppilaitosTyyppis(dto);
    }

    private boolean checkKoulutusAllowedForOppilaitostyyppi(final KoulutusasteTyyppi tyyppi, final String oppilaitosTyyppiUri) {
        Preconditions.checkNotNull(tyyppi, "KoulutusasteTyyppi enum cannot be null");

        if (oppilaitosTyyppiUri == null) {
            return false;
        }

        Collection<KoodiType> koulutustyyppis = getKoulutustyyppi(oppilaitosTyyppiUri);
        for (KoodiType koulutustyyppi : koulutustyyppis) {

            switch (tyyppi) {
                case KORKEAKOULUTUS:
                    if (koulutustyyppi.getKoodiUri().equals("koulutustyyppi_3")) {
                        return true;
                    }
                    break;
            }
        }

        return false;
    }

    private Collection<KoodiType> getKoulutustyyppi(final String oppilaitostyyppiUri) {
        Preconditions.checkNotNull(oppilaitostyyppiUri, "Oppilaitostyyppi URI cannot be null");
        Collection<KoodiType> koodistoRelations = tarjontaKoodistoHelper.getKoodistoRelations(oppilaitostyyppiUri, KoodistoURI.KOODISTO_TARJONTA_KOULUTUSTYYPPI, SuhteenTyyppiType.SISALTYY, false);

        return koodistoRelations;
    }
}
