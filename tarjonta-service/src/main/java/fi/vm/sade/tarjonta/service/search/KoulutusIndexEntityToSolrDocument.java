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
package fi.vm.sade.tarjonta.service.search;

import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.ORG_PATH;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.HAKUKOHDE_OIDS;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.KAUSI_KOODI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.KAUSI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.KOULUTUSKOODI_EN;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.KOULUTUSKOODI_FI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.KOULUTUSKOODI_SV;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.KOULUTUSKOODI_URI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.KOULUTUSLAJI_URIS;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.KOULUTUSMODUULI_OID;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.KOULUTUSOHJELMA_EN;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.KOULUTUSOHJELMA_FI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.KOULUTUSOHJELMA_SV;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.KOULUTUSOHJELMA_URI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.KOULUTUSTYYPPI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.OID;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.ORG_OID;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.POHJAKOULUTUSVAATIMUS_URI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.TEKSTIHAKU;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.TILA_EN;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.TUTKINTONIMIKE_EN;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.TUTKINTONIMIKE_FI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.TUTKINTONIMIKE_SV;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.TUTKINTONIMIKE_URI;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.VUOSI_KOODI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.KoodistoService;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.service.search.OrganisaatioSearchService;
import fi.vm.sade.tarjonta.dao.IndexerDAO;
import fi.vm.sade.tarjonta.model.index.HakukohdeIndexEntity;
import fi.vm.sade.tarjonta.model.index.KoulutusIndexEntity;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;

/**
 * Convert "Koulutus" to {@link SolrInputDocument} so that it can be
 * indexed.
 */
@Configurable
@Component
public class KoulutusIndexEntityToSolrDocument implements
Function<KoulutusIndexEntity, List<SolrInputDocument>> {

    @Autowired
    private OrganisaatioSearchService organisaatioSearchService;

    @Autowired
    private KoodistoService koodistoPublicService;

    @Autowired
    private KoodiService koodiService;
    
    @Autowired
    private IndexerDAO indexerDao;

    Logger logger = LoggerFactory.getLogger(getClass());
   

    @Override
    public List<SolrInputDocument> apply(final KoulutusIndexEntity koulutus) {
        Preconditions.checkNotNull(koulutus);
        List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
       
        final SolrInputDocument komotoDoc = new SolrInputDocument();
        add(komotoDoc, OID, koulutus.getOid());
        final List<OrganisaatioPerustieto> orgs = organisaatioSearchService.findByOidSet(Sets.newHashSet(koulutus.getTarjoaja()));
        
        if (orgs.size() == 0) {
            logger.warn("No org found for komoto: " + koulutus.getOid());
            return Lists.newArrayList();
        }    
    
        final OrganisaatioPerustieto org = orgs.get(0);
        addOrganisaatioTiedot(komotoDoc, org, docs);
        
        if (org.getParentOidPath() != null) {
            ArrayList<String> oidPath = Lists.newArrayList();
            
            Iterables.addAll(oidPath, Splitter.on("/").omitEmptyStrings().split(org.getParentOidPath()));
            Collections.reverse(oidPath);
            
            for (String path : oidPath) {
                add(komotoDoc, ORG_PATH, path);
            }
        }
        
        addKoulutusohjelmaTiedot(komotoDoc, koulutus.getKoulutusTyyppi().equals(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS.value()) 
                ? koulutus.getKoulutusohjelmaKoodi() : koulutus.getLukiolinja());
        addKoulutuskoodiTiedot(komotoDoc, koulutus.getKoulutusKoodi());
        addTutkintonimikeTiedot(komotoDoc, koulutus.getTutkintonimike());
        add(komotoDoc, KAUSI_KOODI, IndexDataUtils.parseKausiKoodi(koulutus.getKoulutuksenAlkamisPvm()));
        add(komotoDoc, KAUSI, IndexDataUtils.parseKausi(koulutus.getKoulutuksenAlkamisPvm()));
        add(komotoDoc, VUOSI_KOODI, IndexDataUtils.parseYear(koulutus.getKoulutuksenAlkamisPvm()));
        add(komotoDoc, TILA_EN, koulutus.getTila());
        add(komotoDoc, KOULUTUSMODUULI_OID, koulutus.getKoulutusmoduuliOid());
        add(komotoDoc, KOULUTUSTYYPPI, koulutus.getKoulutustyyppi());
        add(komotoDoc, POHJAKOULUTUSVAATIMUS_URI, koulutus.getPohjakoulutusvaatimus());
        
        //XXX in DAO find koulutuslajiuris for koulutusmoduulitoteutus
        addKoulutuslajiUris(komotoDoc, indexerDao.findKoulutusLajisForKoulutus(koulutus.getKoulutusId()));
        
        addHakukohdeOids(komotoDoc, indexerDao.findhakukohteetByKoulutusmoduuliToteutusId(koulutus.getKoulutusId()));
        
        addTekstihaku(komotoDoc);
        docs.add(komotoDoc);
        return docs;
    }

    private void addKoulutuslajiUris(SolrInputDocument doc,List<String> koodistoUris) {
          if (koodistoUris == null) {
              return;
          }

        for (String uri: koodistoUris) {
            add(doc,KOULUTUSLAJI_URIS,uri);
        }
    }
    
    private void addHakukohdeOids(SolrInputDocument komotoDoc,
            List<HakukohdeIndexEntity> hakukohdes) {
        if (hakukohdes == null) {
            return;
        }
        
        for (HakukohdeIndexEntity curHakukohde : hakukohdes) {
            add(komotoDoc, HAKUKOHDE_OIDS, curHakukohde.getOid());
        }
        
    }

    private void addTekstihaku(SolrInputDocument komotoDoc) {
        add(komotoDoc, TEKSTIHAKU, String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s", 
                 komotoDoc.getFieldValue(KOULUTUSKOODI_FI), 
                 komotoDoc.getFieldValue(KOULUTUSKOODI_SV), 
                 komotoDoc.getFieldValue(KOULUTUSKOODI_EN),
                 komotoDoc.getFieldValue(KAUSI_KOODI),
                 komotoDoc.getFieldValue(VUOSI_KOODI),
                 komotoDoc.getFieldValue(KOULUTUSOHJELMA_FI),
                 komotoDoc.getFieldValue(KOULUTUSOHJELMA_SV),
                 komotoDoc.getFieldValue(KOULUTUSOHJELMA_EN),
                 komotoDoc.getFieldValue(TUTKINTONIMIKE_FI),
                 komotoDoc.getFieldValue(TUTKINTONIMIKE_SV),
                 komotoDoc.getFieldValue(TUTKINTONIMIKE_EN)));
     }

    private void addTutkintonimikeTiedot(SolrInputDocument doc,
            String tutkintonimike) {
        if (tutkintonimike == null) {
            return;
        }

        KoodiType koodi = IndexDataUtils.getKoodiByUriWithVersion(tutkintonimike, koodiService); 

        if (koodi != null) {
            KoodiMetadataType metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("fi"));
            add(doc, TUTKINTONIMIKE_FI, metadata.getNimi());
            metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("sv"));
            add(doc, TUTKINTONIMIKE_SV, metadata.getNimi());
            metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("en"));
            add(doc, TUTKINTONIMIKE_EN, metadata.getNimi());
            add(doc, TUTKINTONIMIKE_URI, tutkintonimike);
        }
    }



    private void addKoulutuskoodiTiedot(SolrInputDocument doc,
            String koulutusKoodi) {
        if (koulutusKoodi == null) {
            return;
        }

        KoodiType koodi = IndexDataUtils.getKoodiByUriWithVersion(koulutusKoodi, koodiService); 

        if (koodi != null) {
            KoodiMetadataType metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("fi"));
            add(doc, KOULUTUSKOODI_FI, metadata.getNimi());
            metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("sv"));
            add(doc, KOULUTUSKOODI_SV, metadata.getNimi());
            metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("en"));
            add(doc, KOULUTUSKOODI_EN, metadata.getNimi());
            add(doc, KOULUTUSKOODI_URI, koulutusKoodi);
        }
    }



    private void addKoulutusohjelmaTiedot(SolrInputDocument doc, String koulutusohjelmaKoodi) {
        if (koulutusohjelmaKoodi == null) {
            return;
        }

        KoodiType koodi = IndexDataUtils.getKoodiByUriWithVersion(koulutusohjelmaKoodi, koodiService); 

        if (koodi != null) {
            KoodiMetadataType metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("fi"));
            add(doc, KOULUTUSOHJELMA_FI, metadata.getNimi());
            metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("sv"));
            add(doc, KOULUTUSOHJELMA_SV, metadata.getNimi());
            metadata = IndexDataUtils.getKoodiMetadataForLanguage(koodi, new Locale("en"));
            add(doc, KOULUTUSOHJELMA_EN, metadata.getNimi());
            add(doc, KOULUTUSOHJELMA_URI, koulutusohjelmaKoodi);
        }
    }

    private void addOrganisaatioTiedot(SolrInputDocument doc, OrganisaatioPerustieto org, List<SolrInputDocument> docs) {
        if (org == null) {
            return;
        }
        add(doc, ORG_OID, org.getOid());
    }

    /**
     * Add field if value is not null
     * 
     * @param doc
     * @param nimifi
     * @param string
     */
    private void add(final SolrInputDocument doc, final String fieldName, final Object value) {
        if (value != null) {
            doc.addField(fieldName, value);
        }
    }

    
}
