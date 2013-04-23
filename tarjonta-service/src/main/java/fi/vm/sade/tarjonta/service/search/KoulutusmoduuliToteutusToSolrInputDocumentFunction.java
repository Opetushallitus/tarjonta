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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;

import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.KoodistoService;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.koodisto.util.KoodistoHelper;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi.Teksti;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.search.SolrFields.Organisaatio;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.ORG_PATH;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.*;

/**
 * Convert "Koulutus" to {@link SolrInputDocument} so that it can be
 * indexed.
 */
@Configurable
public class KoulutusmoduuliToteutusToSolrInputDocumentFunction implements
Function<KoulutusmoduuliToteutus, List<SolrInputDocument>> {

    @Autowired
    private OrganisaatioService organisaatioService;

    @Autowired
    private KoodistoService koodistoPublicService;

    @Autowired
    private KoodiService koodiService;

   

    @Override
    public List<SolrInputDocument> apply(final KoulutusmoduuliToteutus komoto) {
        Preconditions.checkNotNull(komoto);
        //If the komoto is not a koulutusohjelma or lukiolinja komoto it is not index, becuse they are not
        //shown in search
        List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
        if (komoto.getKoulutusmoduuli().getLukiolinja() == null
                && komoto.getKoulutusmoduuli().getKoulutusohjelmaKoodi() == null) {
            return docs;
        }
       
        final SolrInputDocument komotoDoc = new SolrInputDocument();
        add(komotoDoc, OID, komoto.getOid());
        OrganisaatioDTO org = organisaatioService.findByOid(komoto.getTarjoaja());
        addOrganisaatioTiedot(komotoDoc, org, docs);
        
        for(String path: Splitter.on("|").omitEmptyStrings().split(org.getParentOidPath())) {
            add(komotoDoc, ORG_PATH, path);
        }


        
        addKoulutusohjelmaTiedot(komotoDoc, komoto.getKoulutusmoduuli().getKoulutustyyppi().equals(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS.value()) 
                ? komoto.getKoulutusmoduuli().getKoulutusohjelmaKoodi() : komoto.getKoulutusmoduuli().getLukiolinja());
        addKoulutuskoodiTiedot(komotoDoc, komoto.getKoulutusmoduuli().getKoulutusKoodi());
        addTutkintonimikeTiedot(komotoDoc, komoto.getKoulutusmoduuli().getTutkintonimike());
        add(komotoDoc, KAUSI_KOODI, IndexingUtils.parseKausi(komoto.getKoulutuksenAlkamisPvm()));
        add(komotoDoc, VUOSI_KOODI, IndexingUtils.parseYear(komoto.getKoulutuksenAlkamisPvm()));
        add(komotoDoc, TILA_EN, komoto.getTila());
        add(komotoDoc, KOULUTUSMODUULI_OID, komoto.getKoulutusmoduuli().getOid());
        add(komotoDoc, KOULUTUSTYYPPI, komoto.getKoulutusmoduuli().getKoulutustyyppi());
        add(komotoDoc, POHJAKOULUTUSVAATIMUS_URI, komoto.getPohjakoulutusvaatimus());
        docs.add(komotoDoc);
        return docs;
    }

    private void addTutkintonimikeTiedot(SolrInputDocument doc,
            String tutkintonimike) {
        if (tutkintonimike == null) {
            return;
        }

        KoodiType koodi = IndexingUtils.getKoodiByUriWithVersion(tutkintonimike, koodiService); 

        if (koodi != null) {
            KoodiMetadataType metadata = IndexingUtils.getKoodiMetadataForLanguage(koodi, new Locale("fi"));
            add(doc, TUTKINTONIMIKE_FI, metadata.getNimi());
            metadata = IndexingUtils.getKoodiMetadataForLanguage(koodi, new Locale("sv"));
            add(doc, TUTKINTONIMIKE_SV, metadata.getNimi());
            metadata = IndexingUtils.getKoodiMetadataForLanguage(koodi, new Locale("en"));
            add(doc, TUTKINTONIMIKE_EN, metadata.getNimi());
            add(doc, TUTKINTONIMIKE_URI, tutkintonimike);
        }
    }



    private void addKoulutuskoodiTiedot(SolrInputDocument doc,
            String koulutusKoodi) {
        if (koulutusKoodi == null) {
            return;
        }

        KoodiType koodi = IndexingUtils.getKoodiByUriWithVersion(koulutusKoodi, koodiService); 

        if (koodi != null) {
            KoodiMetadataType metadata = IndexingUtils.getKoodiMetadataForLanguage(koodi, new Locale("fi"));
            add(doc, KOULUTUSKOODI_FI, metadata.getNimi());
            metadata = IndexingUtils.getKoodiMetadataForLanguage(koodi, new Locale("sv"));
            add(doc, KOULUTUSKOODI_SV, metadata.getNimi());
            metadata = IndexingUtils.getKoodiMetadataForLanguage(koodi, new Locale("en"));
            add(doc, KOULUTUSKOODI_EN, metadata.getNimi());
            add(doc, KOULUTUSKOODI_URI, koulutusKoodi);
        }
    }



    private void addKoulutusohjelmaTiedot(SolrInputDocument doc, String koulutusohjelmaKoodi) {
        if (koulutusohjelmaKoodi == null) {
            return;
        }

        KoodiType koodi = IndexingUtils.getKoodiByUriWithVersion(koulutusohjelmaKoodi, koodiService); 

        if (koodi != null) {
            KoodiMetadataType metadata = IndexingUtils.getKoodiMetadataForLanguage(koodi, new Locale("fi"));
            add(doc, KOULUTUSOHJELMA_FI, metadata.getNimi());
            metadata = IndexingUtils.getKoodiMetadataForLanguage(koodi, new Locale("sv"));
            add(doc, KOULUTUSOHJELMA_SV, metadata.getNimi());
            metadata = IndexingUtils.getKoodiMetadataForLanguage(koodi, new Locale("en"));
            add(doc, KOULUTUSOHJELMA_EN, metadata.getNimi());
            add(doc, KOULUTUSOHJELMA_URI, koulutusohjelmaKoodi);
        }
    }

    private void addOrganisaatioTiedot(SolrInputDocument doc, OrganisaatioDTO org, List<SolrInputDocument> docs) {
        if (org == null) {
            return;
        }
        final SolrInputDocument orgDoc = new SolrInputDocument();
        add(orgDoc, Organisaatio.TYPE, "ORG");

        add(doc, ORG_OID, org.getOid());
        for (Teksti curTeksti : org.getNimi().getTeksti()) {
            String kielikoodi = curTeksti.getKieliKoodi();//.equals("fi");
            if (kielikoodi.equals("fi")) {
                add(orgDoc, ORG_NAME_FI, curTeksti.getValue());
            } else if (kielikoodi.equals("sv")) {
                add(orgDoc, ORG_NAME_SV, curTeksti.getValue());
            } else if (kielikoodi.equals("en")) {
                add(orgDoc, ORG_NAME_EN, curTeksti.getValue());
            }
        }
        add(orgDoc, OID, org.getOid());
        
        docs.add(orgDoc);
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
