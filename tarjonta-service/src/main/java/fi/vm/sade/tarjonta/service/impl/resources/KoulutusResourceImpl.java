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
package fi.vm.sade.tarjonta.service.impl.resources;

import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.jaxrs.cors.CrossOriginResourceSharing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;

import fi.vm.sade.tarjonta.dao.KoulutusmoduuliDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.koodisto.KoulutuskoodiRelations;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.business.exception.TarjontaBusinessException;
import fi.vm.sade.tarjonta.service.resources.KoulutusResource;
import fi.vm.sade.tarjonta.service.resources.dto.HakutuloksetRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.kk.KorkeakouluDTO;
import fi.vm.sade.tarjonta.service.resources.dto.KoulutusHakutulosRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.kk.ToteutusDTO;
import fi.vm.sade.tarjonta.service.search.IndexerResource;
import fi.vm.sade.tarjonta.service.search.KoulutuksetKysely;
import fi.vm.sade.tarjonta.service.search.KoulutuksetVastaus;
import fi.vm.sade.tarjonta.service.search.TarjontaSearchService;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import java.util.Locale;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 *
 * @author Jani Wilén
 */
@Transactional(readOnly = true)
@CrossOriginResourceSharing(allowAllOrigins = true)
public class KoulutusResourceImpl implements KoulutusResource {

    private static final Logger LOG = LoggerFactory.getLogger(KoulutusResourceImpl.class);
    @Autowired
    private KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;
    @Autowired
    private KoulutusmoduuliDAO koulutusmoduuliDAO;
    @Autowired
    private ConversionService conversionService;
    @Autowired
    private TarjontaSearchService tarjontaSearchService;
    @Autowired
    private IndexerResource solrIndexer;
    @Autowired(required = true)
    private KoulutuskoodiRelations koulutuskoodiRelations;
    @Autowired(required = true)
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;
    @Autowired(required = true)
    private KoodiService koodiService;
    @Autowired(required = true)
    private OrganisaatioService organisaatioService;

    @Override
    public String help() {
        return "/help (this REST resource help text)\n"
                + "/toteutus/<KOMO-OID>/\n"
                + "/tekstis?lang=<koodi-kieli-URI>\n"
                + "/tekstis/<KOMO-OID>\n";
    }

    @Override
    public ToteutusDTO getKoulutusRelation(String koulutuskoodi) {
        Preconditions.checkNotNull(koulutuskoodi, "Koulutuskoodi parameter cannot be null.");

        if (koulutuskoodi.contains("_")) {
            //simple paramter check if data is koodisto service koodi URI.
            return koulutuskoodiRelations.getKomoRelationByKoulutuskoodiUri(koulutuskoodi, new Locale("FI"));
        } else {
            SearchKoodisByKoodistoCriteriaType search = KoodiServiceSearchCriteriaBuilder.koodisByArvoAndKoodistoUri(koulutuskoodi, KoodistoURI.KOODISTO_TUTKINTO_URI);
            List<KoodiType> searchKoodisByKoodisto = koodiService.searchKoodisByKoodisto(search);
            return koulutuskoodiRelations.getKomoRelationByKoulutuskoodiUri(searchKoodisByKoodisto.get(0).getKoodiUri(), new Locale("FI"));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public HakutuloksetRDTO<KoulutusHakutulosRDTO> searchInfo(
            String searchTerms,
            List<String> organisationOids,
            String hakukohdeTila,
            String alkamisKausi,
            Integer alkamisVuosi) {

        try {
            organisationOids = organisationOids != null ? organisationOids : new ArrayList<String>();

            KoulutuksetKysely q = new KoulutuksetKysely();
            q.setNimi(searchTerms);
            q.setKoulutuksenAlkamiskausi(alkamisKausi);
            q.setKoulutuksenAlkamisvuosi(alkamisVuosi);
            q.getTarjoajaOids().addAll(organisationOids);
            q.setKoulutuksenTila(hakukohdeTila == null ? null : TarjontaTila.valueOf(hakukohdeTila));

            KoulutuksetVastaus r = tarjontaSearchService.haeKoulutukset(q);

            return (HakutuloksetRDTO<KoulutusHakutulosRDTO>) conversionService.convert(r, HakutuloksetRDTO.class);
        } catch (RuntimeException e) {
            e.printStackTrace(System.err);
            throw e;
        }
    }

    @Override
    public ToteutusDTO getToteutus(final String komotoOid) {
        Preconditions.checkNotNull(komotoOid, "KOMOTO OID cannot be null.");
        LOG.info("OID : {}", komotoOid);
        final KoulutusmoduuliToteutus komoto = this.koulutusmoduuliToteutusDAO.findKomotoByOid(komotoOid);


        return conversionService.convert(komoto, KorkeakouluDTO.class);
    }

//    private void addOtherLanguages(final KoodiUriListDTO koodiUriDto, List<KoodiMetadataType> metadata, final Locale locale) {
//        Preconditions.checkNotNull(koodiUriDto, "KoodiUriDTO object cannot be null.");
//        for (KoodiMetadataType meta : metadata) {
//            final String kieliUri = tarjontaKoodistoHelper.convertKielikoodiToKieliUri(meta.getKieli().value());
//            final KoodiType koodiByUri = tarjontaKoodistoHelper.getKoodiByUri(kieliUri);
//            koodiUriDto.getTekstis().add(toKoodiUriDTO(null, meta.getNimi(), koodiByUri, locale));
//        }
//    }
    @Override
    public void updateToteutus(KorkeakouluDTO dto) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void createToteutus(KorkeakouluDTO dto) {
        // permissionChecker.checkCreateKoulutus(koulutus.getTarjoaja());
        Preconditions.checkNotNull(dto, "An invalid data exception - KorkeakouluDTO object cannot be null.");

        if (dto.getOid() != null) {
            throw new TarjontaBusinessException("TARJONTA_REST_011", "An invalid data exception - external KOMOTO OIDs not allowed.");
        }

        if (dto.getKomoOid() != null) {
            throw new TarjontaBusinessException("TARJONTA_REST_012", "An invalid data exception - external KOMO OIDs not allowed.");
        }
        
        if (dto.getKoulutusasteTyyppi() == null) {
            throw new TarjontaBusinessException("TARJONTA_REST_013", "An invalid data exception - KoulutusasteTyyppi enum cannot be null.");
        }

        if (dto.getKoulutusmoduuliTyyppi() == null) {
            throw new TarjontaBusinessException("TARJONTA_REST_014", "An invalid data exception - KoulutusmoduuliTyyppi enum cannot be null.");
        }

        if (dto.getTila() == null) {
            throw new TarjontaBusinessException("TARJONTA_REST_015", "An invalid data exception - Tila enum cannot be null.");
        }

        if (dto.getOrganisaatio() == null || dto.getOrganisaatio().getOid() == null) {
            throw new TarjontaBusinessException("TARJONTA_REST_022", "An invalid data exception - organisation OID was missing.");
        }
        final OrganisaatioDTO org = organisaatioService.findByOid(dto.getOrganisaatio().getOid());

        if (org == null) {
            throw new TarjontaBusinessException("TARJONTA_REST_023", "An invalid data exception - no organisation found by OID '" + dto + "'.");
        }

        final KoulutusmoduuliToteutus newKomo = conversionService.convert(dto, KoulutusmoduuliToteutus.class);

        if (newKomo == null) {
            LOG.error(ReflectionToStringBuilder.toString(dto));
            throw new TarjontaBusinessException("TARJONTA_REST_033", "An invalid data exception - KOMOTO conversion to database object failed.");
        }

        if (newKomo.getKoulutusmoduuli() == null) {
            LOG.error(ReflectionToStringBuilder.toString(newKomo.getKoulutusmoduuli()));
            throw new TarjontaBusinessException("TARJONTA_REST_034", "An invalid data exception - KOMO conversion to database object failed.");
        }

        koulutusmoduuliDAO.insert(newKomo.getKoulutusmoduuli());
        final KoulutusmoduuliToteutus response = koulutusmoduuliToteutusDAO.insert(newKomo);
        solrIndexer.indexKoulutukset(Lists.newArrayList(response.getId()));
        // publication.sendEvent(response.getTila(), response.getOid(), PublicationDataService.DATA_TYPE_KOMOTO, PublicationDataService.ACTION_INSERT);
    }

    @Override
    public void deleteToteutus(String oid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String loadMonikielinenTekstis(String oid, String langUri) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveMonikielinenTeksti(String oid, String langUri) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteMonikielinenTeksti(String oid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveKuva(String oid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteKuva(String oid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
