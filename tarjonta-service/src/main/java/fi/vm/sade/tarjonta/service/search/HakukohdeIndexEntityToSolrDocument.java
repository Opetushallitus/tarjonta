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

import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi.Teksti;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.tarjonta.dao.IndexerDAO;
import fi.vm.sade.tarjonta.model.index.HakuAikaIndexEntity;
import fi.vm.sade.tarjonta.model.index.HakukohdeIndexEntity;
import fi.vm.sade.tarjonta.model.index.KoulutusIndexEntity;
import fi.vm.sade.tarjonta.service.search.SolrFields.Organisaatio;

/**
 * Convert "Hakukohde" to {@link SolrInputDocument} so that it can be indexed.
 */
@Component
public class HakukohdeIndexEntityToSolrDocument implements Function<HakukohdeIndexEntity, List<SolrInputDocument>> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrganisaatioService organisaatioService;

    @Autowired
    private KoodiService koodiService;

    @Autowired
    private IndexerDAO indexerDao;

    @Override
    public List<SolrInputDocument> apply(final HakukohdeIndexEntity hakukohde) {
        Preconditions.checkNotNull(hakukohde);
        List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
        final SolrInputDocument hakukohdeDoc = new SolrInputDocument();
        add(hakukohdeDoc, OID, hakukohde.getOid());
        addKausikoodiTiedot(hakukohdeDoc, hakukohde.getHakukausiUri());
        add(hakukohdeDoc, VUOSI_KOODI, hakukohde.getHakukausiVuosi());
        addHakutapaTiedot(hakukohdeDoc, hakukohde.getHakutapaUri());
        add(hakukohdeDoc, ALOITUSPAIKAT, hakukohde.getAloituspaikatLkm());
        add(hakukohdeDoc, TILA, hakukohde.getTila());
        addNimitiedot(hakukohdeDoc, hakukohde.getHakukohdeNimi());
        addHakuajat(hakukohdeDoc, getHakuajat(hakukohde.getHakuId()));
        addTekstihaku(hakukohdeDoc);
        List<KoulutusIndexEntity> koulutuses = indexerDao.findKoulutusmoduuliToteutusesByHakukohdeId(hakukohde.getId());

        addKomotoOids(hakukohdeDoc, koulutuses);
        addKoulutuslajit(hakukohdeDoc, koulutuses);

        docs.add(hakukohdeDoc);

        if(koulutuses.size()>0) {
            final String tarjoaja = koulutuses.get(0).getTarjoaja();
            boolean orgFound = addOrganisaatioTiedot(hakukohdeDoc, docs,
            tarjoaja);
            
             if(!orgFound) {
             logger.warn("Skipping hakukohde:" + hakukohde.getOid() +
             " no orgnisation found with oid " + tarjoaja);
             return Lists.newArrayList();
             }
        } else {
            logger.warn("No koulutuses found, this should not be possible!");
        }

        
        return docs;
    }

    private List<HakuAikaIndexEntity> getHakuajat(Long hakuId) {
        return indexerDao.findHakuajatForHaku(hakuId);
    }

    private void addKomotoOids(SolrInputDocument hakukohdeDoc, List<KoulutusIndexEntity> koulutuses) {
        if (koulutuses == null) {
            return;
        }
        for (KoulutusIndexEntity komoto : koulutuses) {
            add(hakukohdeDoc, KOULUTUS_OIDS, komoto.getOid());
        }
    }

    private void addTekstihaku(SolrInputDocument hakukohdeDoc) {
        add(hakukohdeDoc, TEKSTIHAKU, String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                hakukohdeDoc.getFieldValue(HAKUKOHTEEN_NIMI_FI), hakukohdeDoc.getFieldValue(HAKUKOHTEEN_NIMI_SV),
                hakukohdeDoc.getFieldValue(HAKUKOHTEEN_NIMI_EN), hakukohdeDoc.getFieldValue(KAUSI_FI),
                hakukohdeDoc.getFieldValue(KAUSI_SV), hakukohdeDoc.getFieldValue(KAUSI_EN),
                hakukohdeDoc.getFieldValue(VUOSI_KOODI), hakukohdeDoc.getFieldValue(HAKUTAPA_FI),
                hakukohdeDoc.getFieldValue(HAKUTAPA_SV), hakukohdeDoc.getFieldValue(HAKUTAPA_EN)));
    }

    private void addHakuajat(SolrInputDocument hakukohdeDoc, List<HakuAikaIndexEntity> hakuajat) {
        add(hakukohdeDoc, HAUN_ALKAMISPVM, getStartDateStr(hakuajat));
        add(hakukohdeDoc, HAUN_PAATTYMISPVM, getEndDateStr(hakuajat));
    }

    private void addNimitiedot(SolrInputDocument doc, String hakukohdeNimi) {
        if (hakukohdeNimi == null) {
            return;
        }
        KoodiType koodi = IndexingUtils.getKoodiByUriWithVersion(hakukohdeNimi, koodiService);

        if (koodi != null) {
            KoodiMetadataType metadata = IndexingUtils.getKoodiMetadataForLanguage(koodi, new Locale("fi"));
            add(doc, HAKUKOHTEEN_NIMI_FI, metadata.getNimi());
            metadata = IndexingUtils.getKoodiMetadataForLanguage(koodi, new Locale("sv"));
            add(doc, HAKUKOHTEEN_NIMI_SV, metadata.getNimi());
            metadata = IndexingUtils.getKoodiMetadataForLanguage(koodi, new Locale("en"));
            add(doc, HAKUKOHTEEN_NIMI_EN, metadata.getNimi());
            add(doc, HAKUKOHTEEN_NIMI_URI, hakukohdeNimi);
        }

    }

    private void addHakutapaTiedot(SolrInputDocument doc, String hakutapaUri) {
        if (hakutapaUri == null) {
            return;
        }
        KoodiType koodi = IndexingUtils.getKoodiByUriWithVersion(hakutapaUri, koodiService);

        if (koodi != null) {
            KoodiMetadataType metadata = IndexingUtils.getKoodiMetadataForLanguage(koodi, new Locale("fi"));
            add(doc, HAKUTAPA_FI, metadata.getNimi());
            metadata = IndexingUtils.getKoodiMetadataForLanguage(koodi, new Locale("sv"));
            add(doc, HAKUTAPA_SV, metadata.getNimi());
            metadata = IndexingUtils.getKoodiMetadataForLanguage(koodi, new Locale("en"));
            add(doc, HAKUTAPA_EN, metadata.getNimi());
            add(doc, HAKUTAPA_URI, hakutapaUri);
        }

    }

    private void addKausikoodiTiedot(SolrInputDocument doc, String kausikoodi) {
        if (kausikoodi == null) {
            return;
        }

        KoodiType koodi = IndexingUtils.getKoodiByUriWithVersion(kausikoodi, koodiService);

        if (koodi != null) {
            KoodiMetadataType metadata = IndexingUtils.getKoodiMetadataForLanguage(koodi, new Locale("fi"));
            add(doc, KAUSI_FI, metadata.getNimi());
            metadata = IndexingUtils.getKoodiMetadataForLanguage(koodi, new Locale("sv"));
            add(doc, KAUSI_SV, metadata.getNimi());
            metadata = IndexingUtils.getKoodiMetadataForLanguage(koodi, new Locale("en"));
            add(doc, KAUSI_EN, metadata.getNimi());
            add(doc, KAUSI_KOODI,
                    koodi.getKoodiUri() + IndexingUtils.KOODI_URI_AND_VERSION_SEPARATOR + koodi.getVersio());
        }
    }

    private void addKoulutuslajit(SolrInputDocument doc, List<KoulutusIndexEntity> koulutuses) {
        if (koulutuses == null || koulutuses.size()==0) {
            return;
        }
        
        
        KoulutusIndexEntity koulutus = koulutuses.get(0);
        if(koulutus.getKoulutuslaji()==null) {
            return;
        }
        

        KoodiType koodi = IndexingUtils.getKoodiByUriWithVersion(koulutus.getKoulutuslaji(),  koodiService);

        if (koodi != null) {
            KoodiMetadataType metadata = IndexingUtils.getKoodiMetadataForLanguage(koodi, new Locale("fi"));
            add(doc, KOULUTUSLAJI_FI, metadata.getNimi());
            metadata = IndexingUtils.getKoodiMetadataForLanguage(koodi, new Locale("sv"));
            add(doc, KOULUTUSLAJI_SV, metadata.getNimi());
            metadata = IndexingUtils.getKoodiMetadataForLanguage(koodi, new Locale("en"));
            add(doc, KOULUTUSLAJI_EN, metadata.getNimi());
        }
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

    private boolean addOrganisaatioTiedot(SolrInputDocument hakukohdeDoc, List<SolrInputDocument> docs,
            String tarjoaja) {
        boolean orgFound = false;
        orgFound = handleOrganisaatio(tarjoaja, hakukohdeDoc, docs);
        return orgFound;
    }

    private boolean handleOrganisaatio(String tarjoaja, SolrInputDocument hakukohdeDoc,
            List<SolrInputDocument> docs) {
        //final SolrInputDocument orgDoc = new SolrInputDocument();

        OrganisaatioDTO org = organisaatioService.findByOid(tarjoaja);
        if (org == null) {
            return false;
        }
//        add(orgDoc, OID, org.getOid());
//        add(orgDoc, Organisaatio.TYPE, "ORG");

        add(hakukohdeDoc, ORG_OID, org.getOid());

        for (String path : Splitter.on("|").omitEmptyStrings().split(org.getParentOidPath())) {
            add(hakukohdeDoc, ORG_PATH, path);
        }
        add(hakukohdeDoc, ORG_PATH, org.getOid());

//        for (Teksti curTeksti : org.getNimi().getTeksti()) {
//            String kielikoodi = curTeksti.getKieliKoodi();// .equals("fi");
//            if (kielikoodi.equals("fi")) {
//               add(orgDoc, ORG_NAME_FI, curTeksti.getValue());
//            } else if (kielikoodi.equals("sv")) {
//                add(orgDoc, ORG_NAME_SV, curTeksti.getValue());
//            } else if (kielikoodi.equals("en")) {
//                add(orgDoc, ORG_NAME_EN, curTeksti.getValue());
//            }
//        }
//        docs.add(orgDoc);
        return true;
    }

    private Date getStartDate(List<HakuAikaIndexEntity> hakuaikas) {
        Date startDate = null;
        for (HakuAikaIndexEntity aika : hakuaikas) {
            if (startDate == null) {
                startDate = aika.getAlkamisPvm();
            } else if (aika.getAlkamisPvm().before(startDate)) {
                startDate = aika.getAlkamisPvm();
            }
        }
        return startDate;
    }

    private String getStartDateStr(List<HakuAikaIndexEntity> hakuaikas) {
        Date startDate = getStartDate(hakuaikas);
        if (startDate != null) {
            DateFormat df = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
            return df.format(startDate);
        }
        return "";
    }

    private Date getEndDate(List<HakuAikaIndexEntity> hakuaikas) {
        Date endDate = null;
        for (HakuAikaIndexEntity aika : hakuaikas) {
            if (endDate == null) {
                endDate = aika.getPaattymisPvm();
            } else if (aika.getPaattymisPvm().after(endDate)) {
                endDate = aika.getPaattymisPvm();
            }
        }
        return endDate;
    }

    private String getEndDateStr(List<HakuAikaIndexEntity> hakuaikas) {
        Date endDate = getEndDate(hakuaikas);
        if (endDate != null) {
            DateFormat df = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
            return df.format(endDate);
        }
        return "";
    }
}
