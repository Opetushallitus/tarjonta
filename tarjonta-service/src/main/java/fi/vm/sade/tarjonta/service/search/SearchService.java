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

import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde;
import fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus;
import fi.vm.sade.tarjonta.service.search.SolrFields.Organisaatio;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetVastausTyyppi;

@Component
public class SearchService {

    @Value("${root.organisaatio.oid}")
    private String rootOrganisaatioOid;

    private final SolrDocumentToKoulutusPerustietoTypeFunction koulutusConverter = new SolrDocumentToKoulutusPerustietoTypeFunction();
    private final SolrDocumentToHakukohdetulosFunction hakukohdeConverter = new SolrDocumentToHakukohdetulosFunction();

    private final SolrServer koulutusSolr;
    private final SolrServer hakukohdeSolr;
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    public SearchService(SolrServerFactory factory) {
        this.koulutusSolr = factory.getSolrServer("koulutukset");
        this.hakukohdeSolr = factory.getSolrServer("hakukohteet");
    }

    public HaeHakukohteetVastausTyyppi haeHakukohteet(
            final HaeHakukohteetKyselyTyyppi kysely) {

        HaeHakukohteetVastausTyyppi response = new HaeHakukohteetVastausTyyppi();

        String nimi = kysely.getNimi();
        final String kausi = kysely.getKoulutuksenAlkamiskausi();
        final String vuosi = Integer.toString(kysely
                .getKoulutuksenAlkamisvuosi());
        final List<String> oids = kysely.getTarjoajaOids();
        final String nimikoodiURI = kysely.getNimiKoodiUri(); // what for?

        final List<String> queryParts = Lists.newArrayList();

        final SolrQuery q = new SolrQuery("*:*");

        nimi = escape(nimi);

        // nimihaku
        if (nimi != null && nimi.length() > 0) {
            addQuery(nimi, queryParts, "%s:*%s*",
                    Hakukohde.HAKUKOHTEEN_NIMI_FI, nimi);
            addQuery(nimi, queryParts, "%s:*%s*",
                    Hakukohde.HAKUKOHTEEN_NIMI_SV, nimi);
            addQuery(nimi, queryParts, "%s:*%s*",
                    Hakukohde.HAKUKOHTEEN_NIMI_EN, nimi);
            q.addFilterQuery(Joiner.on(" ").join(queryParts));
            queryParts.clear();
        }

        // vuosi & kausi
        addFilterForVuosiKausi(kausi, vuosi, queryParts, q);

        // restrict by org
        addFilterForOrgs(oids, queryParts, q);

        //filter out orgs
        filterOutOrgs(q);
        try {
            // query solr
            q.setRows(Integer.MAX_VALUE);
            QueryResponse hakukohdeResponse = hakukohdeSolr.query(q);
            
            //now we have the hakukohteet, fetch orgs
            Set<String> orgOids = Sets.newHashSet();
            
            for(SolrDocument doc: hakukohdeResponse.getResults()){
                orgOids.add((String)doc.getFieldValue(Hakukohde.ORG_OID));
            }
            
            if(orgOids.size()>0) {
                QueryResponse orgResponse = searchOrgs(orgOids, hakukohdeSolr);
                SolrDocumentToHakukohdeConverter converter = new SolrDocumentToHakukohdeConverter();
                response = converter.convertSolrToHakukohteetVastaus(hakukohdeResponse.getResults(), orgResponse.getResults());
            } else {
                //empty result
                response = new HaeHakukohteetVastausTyyppi();
            }
            
        } catch (SolrServerException e) {
            throw new RuntimeException("haku.error", e);
        }

        return response;
    }

    private QueryResponse searchOrgs(Set<String> orgOids, SolrServer solr) throws SolrServerException {
        SolrQuery orgQ = new SolrQuery();
        
        String orgQuery = String.format("%s:(%s)", Organisaatio.OID, Joiner.on(" ").join(orgOids));
        orgQ.setQuery(orgQuery);
        orgQ.setRows(Integer.MAX_VALUE);

        QueryResponse orgResponse = solr.query(orgQ);
        return orgResponse;
    }

    private void addFilterForOrgs(final List<String> oids,
            final List<String> queryParts, SolrQuery q) {
        if (oids.size() > 0) {
            addQuery("", queryParts, "%s:(%s)", Hakukohde.ORG_PATH,
                    Joiner.on(" ").join(oids));
            q.addFilterQuery(Joiner.on(" ").join(queryParts));
        }
    }

    private void addFilterForVuosiKausi(final String kausi, final String vuosi,
            final List<String> queryParts, SolrQuery q) {
        // vuosi kausi
        if(vuosi!=null) {
            String qVuosi = Integer.parseInt(vuosi) < 0 ? "*" : vuosi;
            addQuery(qVuosi, queryParts, "%s:%s", Hakukohde.VUOSI_KOODI, qVuosi);
        }
        addQuery(kausi, queryParts, "%s:%s", Hakukohde.KAUSI_KOODI, kausi);
        q.addFilterQuery(Joiner.on(" ").join(queryParts));
        queryParts.clear();
    }

    // TODO
    public HaeKoulutuksetVastausTyyppi haeKoulutukset(
            final HaeKoulutuksetKyselyTyyppi kysely) {

        HaeKoulutuksetVastausTyyppi response = new HaeKoulutuksetVastausTyyppi();

        String nimi = kysely.getNimi();
        final String kausi = kysely.getKoulutuksenAlkamiskausi();
        final String vuosi = kysely.getKoulutuksenAlkamisvuosi()!=null?Integer.toString(kysely
                .getKoulutuksenAlkamisvuosi()):null;
        final List<String> tarjoajaOids = kysely.getTarjoajaOids();
        final List<String> koulutusOids = kysely.getKoulutusOids();

        nimi = escape(nimi);
        
        
        

        final SolrQuery q = new SolrQuery("*:*");
        final List<String> queryParts = Lists.newArrayList();

        // nimihaku
        if (nimi != null && nimi.length() > 0) {
            addQuery(nimi, queryParts, "%s:*%s*", Koulutus.KOULUTUSKOODI_FI,
                    nimi);
            addQuery(nimi, queryParts, "%s:*%s*", Koulutus.KOULUTUSKOODI_SV,
                    nimi);
            addQuery(nimi, queryParts, "%s:*%s*", Koulutus.KOULUTUSKOODI_EN,
                    nimi);
            q.addFilterQuery(Joiner.on(" ").join(queryParts));
            queryParts.clear();
        }

        // vuosi & kausi
        addFilterForVuosiKausi(kausi, vuosi, queryParts, q);

        // restrict by org
        addFilterForOrgs(tarjoajaOids, queryParts, q);

        //restrict by koulutus
        if (koulutusOids.size() > 0) {
            addFilterForKOulutus(koulutusOids, q);
        }

        //filter out orgs
        filterOutOrgs(q);
        try {
            // query solr
            q.setRows(Integer.MAX_VALUE);
            QueryResponse koulutusResponse = koulutusSolr.query(q);
            
            //now we have the hakukohteet, fetch orgs
            Set<String> orgOids = Sets.newHashSet();

            
            for(SolrDocument doc: koulutusResponse.getResults()){
                orgOids.add((String)doc.getFieldValue(Hakukohde.ORG_OID));
            }

            if (orgOids.size() > 0) {
                QueryResponse orgResponse = searchOrgs(orgOids, koulutusSolr);

                SolrDocumentToKoulutusmoduuliToteutusConverter converter = new SolrDocumentToKoulutusmoduuliToteutusConverter();

                response = converter
                        .convertSolrToKoulutuksetVastaus(
                                koulutusResponse.getResults(),
                                orgResponse.getResults());
                
            } else {
                response = new HaeKoulutuksetVastausTyyppi();
            }

        } catch (SolrServerException e) {
            throw new RuntimeException("haku.error", e);
        }
        return response;
    }
    
    private void addFilterForKOulutus(List<String> tarjoajaOids, SolrQuery q) {
        q.addFilterQuery(String.format("%s:(%s)", Koulutus.OID, Joiner.on(" ").join(tarjoajaOids)));
    }

    private void filterOutOrgs(SolrQuery query){
        query.addFilterQuery("-" + Organisaatio.TYPE + ":ORG");
    }

    private void addQuery(final String param, final List<String> queryParts,
            String template, Object... params) {
        if (param != null) {
            queryParts.add(String.format(template, params));
        }
    }

    private String escape(String searchStr) {
        if (searchStr == null) {
            return null;
        }

        searchStr = searchStr.replaceAll("\"", "\\\\\"");
        return searchStr;
    }

}
