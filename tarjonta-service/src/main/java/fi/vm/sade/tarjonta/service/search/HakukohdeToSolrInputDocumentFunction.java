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
import java.util.List;
import java.util.Locale;

import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.KoodistoService;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi.Teksti;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.*;

/**
 * Convert "Koulutus" to {@link SolrInputDocument} so that it can be
 * indexed.
 */
@Configurable
public class HakukohdeToSolrInputDocumentFunction implements
        Function<Hakukohde, List<SolrInputDocument>> {
	
	@Autowired
	 private OrganisaatioService organisaatioService;
	 
	 @Autowired
	 private KoodistoService koodistoPublicService;
	 
	 @Autowired
	 private KoodiService koodiService;
	
    @Override
    public List<SolrInputDocument> apply(final Hakukohde hakukohde) {
        Preconditions.checkNotNull(hakukohde);
        List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
        final SolrInputDocument hakukohdeDoc = new SolrInputDocument();
        add(hakukohdeDoc, OID, hakukohde.getOid());
        addOrganisaatioTiedot(hakukohdeDoc, docs, hakukohde);
        addKausikoodiTiedot(hakukohdeDoc, hakukohde.getHaku().getHakukausiUri());
        add(hakukohdeDoc, VUOSI_KOODI, hakukohde.getHaku().getHakukausiVuosi());
        addHakutapaTiedot(hakukohdeDoc, hakukohde.getHaku().getHakutapaUri());
        add(hakukohdeDoc, ALOITUSPAIKAT, hakukohde.getAloituspaikatLkm());
        add(hakukohdeDoc, TILA, hakukohde.getTila());
        addNimitiedot(hakukohdeDoc, hakukohde.getHakukohdeNimi());
        docs.add(hakukohdeDoc);
        return docs;
    }
    
    private void addNimitiedot(SolrInputDocument doc,
            String hakukohdeNimi) {
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
        }
        
    }

    private void addHakutapaTiedot(SolrInputDocument doc,
            String hakutapaUri) {
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
        }
        
    }

    private void addKausikoodiTiedot(SolrInputDocument doc,
            String kausikoodi) {
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
            add(doc, KAUSI_KOODI, koodi.getKoodiUri() + IndexingUtils.KOODI_URI_AND_VERSION_SEPARATOR + koodi.getVersio());
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
    
    private void addOrganisaatioTiedot(SolrInputDocument hakukohdeDoc, List<SolrInputDocument> docs, Hakukohde hakukohde) {
    	for (KoulutusmoduuliToteutus curKoulutus : hakukohde.getKoulutusmoduuliToteutuses()) {
    		handleOrganisaatio(curKoulutus, hakukohdeDoc, docs);
    	}
    }
    
    private void handleOrganisaatio(KoulutusmoduuliToteutus komoto, SolrInputDocument hakukohdeDoc, List<SolrInputDocument> docs) {
        final SolrInputDocument orgDoc = new SolrInputDocument();
        OrganisaatioDTO org = organisaatioService.findByOid(komoto.getTarjoaja());
        if (org == null) {
            return;
        }
        add(orgDoc, OID, org.getOid());
        add(hakukohdeDoc, ORG_OID, org.getOid());
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
        docs.add(orgDoc);
    }
}
