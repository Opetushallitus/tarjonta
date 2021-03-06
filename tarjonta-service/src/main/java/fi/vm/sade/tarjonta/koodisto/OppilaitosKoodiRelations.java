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
import com.google.common.collect.Sets;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.OrganisaatioService;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Jani Wilén
 */
@Component
public class OppilaitosKoodiRelations {

    private static final Logger LOG = LoggerFactory.getLogger(OppilaitosKoodiRelations.class);

    @Autowired
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;

    @Autowired
    private OrganisaatioService organisaatioService;

    @Value("${root.organisaatio.oid:}")
    private String rootOphOid;

    /**
     * Check if the given organization is allowed to create new education.
     *
     *
     * @param organisaatioOid
     * @param koulutusaste Koodisto service uri
     * @return
     */
    public boolean isKoulutusAllowedForOrganisation(final String organisaatioOid, final String koulutusaste) {
        Preconditions.checkNotNull(koulutusaste, "Koulutusaste URI cannot be null");
        Preconditions.checkNotNull(organisaatioOid, "Organisaatio OID cannot be null");
        final OrganisaatioRDTO orgDto = organisaatioService.findByOid(organisaatioOid);

        if (orgDto == null) {
            LOG.info("org not found: {}", organisaatioOid);
            return false;
        }

        List<String> oids = splitOrganisationPath(orgDto.getParentOidPath());
        oids.remove(rootOphOid); //remove OPH OID from the list

        if (orgDto.getTyypit().contains(OrganisaatioService.OrganisaatioTyyppi.OPPILAITOS.value())) {
            if (isCorrectOppilaitostyyppis(organisaatioOid, koulutusaste)) {
                return true;
            } else {
                return false;
            }
        }

        if (orgDto.getTyypit().contains(OrganisaatioService.OrganisaatioTyyppi.KOULUTUSTOIMIJA.value())) {
            //search all children organisations, one of them must be OPPILAITOS
            final Set<String> childrenOids = organisaatioService.findChildrenOidsByOid(organisaatioOid);
            List<String> oppilaitostyyppiUris = Lists.newArrayList();
            for (String oid : childrenOids) {
                oppilaitostyyppiUris.addAll(getAllOppilaitosTyyppisByOrganisaatioOid(oid));
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

        if (orgDto.getTyypit().contains(OrganisaatioService.OrganisaatioTyyppi.TOIMIPISTE.value())) {
            String oid = oids.get(1); // oppilaitos (oph/kt/ol/op)
            final OrganisaatioRDTO pathOrgDto = organisaatioService.findByOid(oid);
            if (pathOrgDto == null) {
                LOG.error("Data error - organisation with OID {} not found!", oid);
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

    /**
     * Search all valid koulutustyyppi uris by organisation and koodisto
     * realations.
     *
     * @param organisaatioOid
     * @return
     */
    public Set<String> getKoulutustyyppiUris(final String organisaatioOid) {
        Preconditions.checkNotNull(organisaatioOid, "Organisaatio OID cannot be null");
        final OrganisaatioRDTO orgDto = organisaatioService.findByOid(organisaatioOid);

        if (orgDto == null) {
            LOG.info("org not found: {}", organisaatioOid);
            return Sets.<String>newHashSet();
        }

        List<String> oids = splitOrganisationPath(orgDto.getParentOidPath());
        oids.remove(rootOphOid); //remove OPH OID from the list

        if (orgDto.getTyypit().contains(OrganisaatioService.OrganisaatioTyyppi.OPPILAITOS.value())) {
            return getKoodistoKoulutustyyppiUrisByOppilaitostyyppiUris(getAllOppilaitosTyyppisByOrganisaatioOid(organisaatioOid));
        }

        if (orgDto.getTyypit().contains(OrganisaatioService.OrganisaatioTyyppi.KOULUTUSTOIMIJA.value())) {
            //search all children organisations, one of them must be OPPILAITOS
            final Set<String> childrenOids = organisaatioService.findChildrenOidsByOid(organisaatioOid);
            List<String> oppilaitostyyppiUris = Lists.newArrayList();
            for (String oid : childrenOids) {
                oppilaitostyyppiUris.addAll(getAllOppilaitosTyyppisByOrganisaatioOid(oid));
            }
            return getKoodistoKoulutustyyppiUrisByOppilaitostyyppiUris(oppilaitostyyppiUris);
        }

        if (orgDto.getTyypit().contains(OrganisaatioService.OrganisaatioTyyppi.TOIMIPISTE.value())) {
            String oid = oids.get(1); // oppilaitos (oph/kt/ol/op)
            final OrganisaatioRDTO pathOrgDto = organisaatioService.findByOid(oid);
            if (pathOrgDto == null) {
                LOG.error("Data error - organisation with OID {} not found!", oid);
            } else {
                // search one organisation
                return getKoodistoKoulutustyyppiUrisByOppilaitostyyppiUris(getAllOppilaitosTyyppisByOrganisaatioOid(pathOrgDto));
            }
        }

        return Sets.<String>newHashSet();
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
        if (parentOidPath == null) {
            return Lists.<String>newArrayList();
        }

        final List<String> list = Lists.<String>newArrayList(parentOidPath.split("[|]"));
        list.removeAll(Arrays.asList("")); //remove empty strings
        return list;
    }

    private List<String> getOppilaitosTyyppis(OrganisaatioRDTO org) {
        List<String> oppilaitostyyppis = Lists.newArrayList();

        for (String organisaatioTyyppi : org.getTyypit()) {
            OrganisaatioService.OrganisaatioTyyppi tyyppi = OrganisaatioService.OrganisaatioTyyppi.fromValue(organisaatioTyyppi);
            switch (tyyppi) {
                case TOIMIPISTE:
                    //- we are on the leaf (or one of them)
                    break;
                case KOULUTUSTOIMIJA:
                    //- we are on the root (not oph?)
                    break;
                case OPPILAITOS:
                    //success : end of the line
                    oppilaitostyyppis.add(org.getOppilaitosTyyppiUri());
                    break;
            }
        }

        return oppilaitostyyppis;
    }

    private List<String> getAllOppilaitosTyyppisByOrganisaatioOid(String oid) {
        return getOppilaitosTyyppis(organisaatioService.findByOid(oid));
    }

    private List<String> getAllOppilaitosTyyppisByOrganisaatioOid(OrganisaatioRDTO dto) {
        return getOppilaitosTyyppis(dto);
    }

    private boolean oppilaitostyyppiMatchesKoulutusaste(final String koulutusaste, final String oppilaitosTyyppiUri) {
        Preconditions.checkNotNull(koulutusaste, "Koulutusaste cannot be null");
        Preconditions.checkNotNull(oppilaitosTyyppiUri, "Oppilaitostyyppi cannot be null");

        final Collection<KoodiType> koulutusastees = searchKoulutusasteFromKoodisto(oppilaitosTyyppiUri);

        for (KoodiType koulutusasteKoodi : koulutusastees) {
            if (KoodistoURI.compareKoodi(koulutusasteKoodi.getKoodiUri(), koulutusaste)) {
                return true;
            }
        }

        return false;
    }

    private Set<String> getKoodistoKoulutustyyppiUrisByOppilaitostyyppiUris(final List<String> oppilaitosTyyppiUris) {
        Preconditions.checkNotNull(oppilaitosTyyppiUris, "Oppilaitostyyppi list of oppilaitostyyppi objects cannot be null");
        Set<String> koulutustyyppiUris = Sets.<String>newHashSet();

        for (String oppilaitostyyppiUri : oppilaitosTyyppiUris) {

            Collection<KoodiType> koodistoRelations = tarjontaKoodistoHelper.getKoodistoRelations(oppilaitostyyppiUri, KoodistoURI.KOODISTO_TARJONTA_KOULUTUSTYYPPI, SuhteenTyyppiType.SISALTYY, false);
            for (KoodiType type : koodistoRelations) {
                LOG.info("URI " + type.getKoodiUri());
                koulutustyyppiUris.add(type.getKoodiUri());
            }

        }

        return koulutustyyppiUris;
    }

    private Collection<KoodiType> searchKoulutusasteFromKoodisto(final String oppilaitostyyppiUri) {
        Preconditions.checkNotNull(oppilaitostyyppiUri, "Oppilaitostyyppi URI cannot be null");
        final Collection<KoodiType> koodistoRelations = tarjontaKoodistoHelper.getKoodistoRelations(oppilaitostyyppiUri, KoodistoURI.KOODISTO_KOULUTUSASTE_URI, SuhteenTyyppiType.SISALTYY, false);
        return koodistoRelations;
    }

}
