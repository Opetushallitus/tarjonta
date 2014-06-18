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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;

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
    public boolean isKoulutusAllowedForOrganisation(final String organisaatioOid, final String koulutusaste) {
        Preconditions.checkNotNull(koulutusaste, "Koulutusaste URI cannot be null");
        Preconditions.checkNotNull(organisaatioOid, "Organisaatio OID cannot be null");
        final OrganisaatioDTO orgDto = organisaatioService.findByOid(organisaatioOid);

        if (orgDto == null) {
            LOG.info("org not found: {}", organisaatioOid);
            return false;
        }

        List<String> oids = splitOrganisationPath(orgDto.getParentOidPath());
        oids.remove(rootOphOid); //remove OPH OID from the list

        if (orgDto.getTyypit().contains(OrganisaatioTyyppi.OPPILAITOS)) {
            if (isCorrectOppilaitostyyppis(organisaatioOid, koulutusaste)) {
                return true;
            } else {
                return false;
            }
        }

        if (orgDto.getTyypit().contains(OrganisaatioTyyppi.KOULUTUSTOIMIJA)) {
          //search all children organisations, one of them must be OPPILAITOS
            final OrganisaatioOidListType childrenOids = organisaatioService.findChildrenOidsByOid(new OrganisaatioSearchOidType(organisaatioOid));
            List<String> oppilaitostyyppiUris = Lists.newArrayList();
            for (OrganisaatioOidType oidType : childrenOids.getOrganisaatioOidList()) {
                oppilaitostyyppiUris.addAll(getAllOppilaitosTyyppisByOrganisaatioOid(oidType.getOrganisaatioOid()));
            }

            if (!oppilaitostyyppiUris.isEmpty()) {
                for (String oppilaitostyyppiUri : oppilaitostyyppiUris) {
                    if (oppilaitostyyppiMatchesKoulutusaste(koulutusaste, oppilaitostyyppiUri)) {
                        LOG.debug("Found : {} KOULUTUSTOIMIJA parent of OPPILAITOS with correct oppilaitostyyppiUri {}", organisaatioOid, oppilaitostyyppiUri);
                        return true;
                    }
                }
            }
            
            return false;
        }
        
        if (orgDto.getTyypit().contains(OrganisaatioTyyppi.TOIMIPISTE)) {
            String oid = oids.get(1); // oppilaitos (oph/kt/ol/op)
            final OrganisaatioDTO pathOrgDto = organisaatioService
                    .findByOid(oid);
            if (pathOrgDto == null) {
                LOG.error("Data error - organisation with OID {} not found!",
                        oid);
            } else {
                // search one organisation
                final List<String> oppilaitostyyppiUris = getAllOppilaitosTyyppisByOrganisaatioOid(pathOrgDto);

                if (!oppilaitostyyppiUris.isEmpty()) {
                    for (String oppilaitostyyppiUri : oppilaitostyyppiUris) {
                        if (oppilaitostyyppiMatchesKoulutusaste(koulutusaste, oppilaitostyyppiUri)) {
                            LOG.debug(
                                    "Found : {} OPPILAITOS with correct oppilaitostyyppi {}",
                                    oid, oppilaitostyyppiUri);
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private boolean isCorrectOppilaitostyyppis(final String orgOid, final String koulutusaste) {
        final List<String> oppilaitostyyppiUris = getAllOppilaitosTyyppisByOrganisaatioOid(orgOid);
        
        if (!oppilaitostyyppiUris.isEmpty()) {
            for (String oppilaitostyyppiUri : oppilaitostyyppiUris) {
                if (oppilaitostyyppiMatchesKoulutusaste(koulutusaste, oppilaitostyyppiUri)) {
                    LOG.debug("Found (by path index) : {} OPPILAITOS with correct oppilaitostyyppi {}", orgOid, oppilaitostyyppiUri);
                    return true;
                }
            }
        }

        return false;
    }

    public static List<String> splitOrganisationPath(String parentOidPath) {
        Preconditions.checkNotNull(parentOidPath, "Organisation OID path cannot be null");
        final List<String> list = Lists.<String>newArrayList(parentOidPath.split("[|]"));
        list.removeAll(Arrays.asList("")); //remove empty strings
        return list;
    }

    private List<String> getOppilaitosTyyppis(OrganisaatioDTO org) {
        List<String> oppilaitostyyppis = Lists.<String>newArrayList();

        for (OrganisaatioTyyppi organisaatioTyyppi : org.getTyypit()) {
            switch (organisaatioTyyppi) {
                case TOIMIPISTE:
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

    private boolean oppilaitostyyppiMatchesKoulutusaste(final String koulutusaste, final String oppilaitosTyyppiUri) {
        Preconditions.checkNotNull(koulutusaste, "Koulutusaste cannot be null");
        Preconditions.checkNotNull(oppilaitosTyyppiUri, "Oppilaitostyyppi cannot be null");

        final Collection<KoodiType> koulutusastees = searchKoulutusasteFromKoodisto(oppilaitosTyyppiUri);
        
        for (KoodiType koulutusasteKoodi : koulutusastees) {
            if(KoodistoURI.compareKoodi(koulutusasteKoodi.getKoodiUri(), koulutusaste)) {
                return true;
            }
        }

        return false;
    }

    private Collection<KoodiType> searchKoulutusasteFromKoodisto(final String oppilaitostyyppiUri) {
        Preconditions.checkNotNull(oppilaitostyyppiUri, "Oppilaitostyyppi URI cannot be null");
        final Collection<KoodiType> koodistoRelations = tarjontaKoodistoHelper.getKoodistoRelations(oppilaitostyyppiUri, KoodistoURI.KOODISTO_KOULUTUSASTE_URI, SuhteenTyyppiType.SISALTYY, false);
        return koodistoRelations;
    }
}
