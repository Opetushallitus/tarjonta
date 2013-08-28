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
import org.apache.solr.client.solrj.SolrRequest.METHOD;
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

@Component
public class TarjontaSearchService {

    private static final String QUERY_ALL = "*:*";
    private static final String TEKSTIHAKU_TEMPLATE = "{!lucene q.op=AND df=%s}%s";
    @Value("${root.organisaatio.oid}")
    private String rootOrganisaatioOid;
    private final SolrServer koulutusSolr;
    private final SolrServer hakukohdeSolr;
    private final SolrServer organisaatioSolr;
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    public TarjontaSearchService(SolrServerFactory factory) {
        this.koulutusSolr = factory.getSolrServer("koulutukset");
        this.hakukohdeSolr = factory.getSolrServer("hakukohteet");
        this.organisaatioSolr = factory.getOrganisaatioSolrServer();
    }

    public HakukohteetVastaus haeHakukohteet(
            final HakukohteetKysely kysely) {

        HakukohteetVastaus response = new HakukohteetVastaus();

        String nimi = kysely.getNimi();
        final String kausi = kysely.getKoulutuksenAlkamiskausi();
        final Integer vuosi = kysely.getKoulutuksenAlkamisvuosi();
        final List<String> oids = kysely.getTarjoajaOids();
        final List<String> queryParts = Lists.newArrayList();
        final String tila = kysely.getTilat() != null ? kysely.getTilat().name() : null;
        final SolrQuery q = new SolrQuery(QUERY_ALL);

        nimi = escape(nimi);

        // nimihaku
        if (nimi != null && nimi.length() > 0) {
            addQuery(nimi, queryParts, TEKSTIHAKU_TEMPLATE,
                    Hakukohde.TEKSTIHAKU, nimi);
            q.addFilterQuery(Joiner.on(" ").join(queryParts));
            queryParts.clear();
        }

        if (tila != null ) {

            q.addFilterQuery(String.format("%s:%s", Hakukohde.TILA, tila));
        }

        
        if(kysely.getHakuOid()!=null) {
            addFilterForHakuOid(kysely.getHakuOid(), q);
        }
        
        // vuosi & kausi
        addFilterForVuosiKausi(kausi, vuosi, queryParts, q);

        // restrict by org
        addFilterForOrgs(oids, queryParts, q);

        addFilterForKoulutukset(kysely.getKoulutusOids(), queryParts, q);

        //filter out orgs
        filterOutOrgs(q);
        try {
            // query solr
            q.setRows(Integer.MAX_VALUE);
            QueryResponse hakukohdeResponse = hakukohdeSolr.query(q);

            //now we have the hakukohteet, fetch orgs
            Set<String> orgOids = Sets.newHashSet();

            for (SolrDocument doc : hakukohdeResponse.getResults()) {
                if (doc.getFieldValue(Hakukohde.ORG_OID) != null) {
                    orgOids.add((String) doc.getFieldValue(Hakukohde.ORG_OID));
                }
            }

            if (orgOids.size() > 0) {
                QueryResponse orgResponse = searchOrgs(orgOids, hakukohdeSolr);
                SolrDocumentToHakukohdeConverter converter = new SolrDocumentToHakukohdeConverter();
                response = converter.convertSolrToHakukohteetVastaus(hakukohdeResponse.getResults(), orgResponse.getResults());
            } else {
                //empty result
                response = new HakukohteetVastaus();
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
        //TODO limit fields
        QueryResponse orgResponse = organisaatioSolr.query(orgQ, METHOD.POST);
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

    private void addFilterForKoulutukset(final List<String> oids,
            final List<String> queryParts, SolrQuery q) {
        if (oids.size() > 0) {
            addQuery("", queryParts, "%s:(%s)", Hakukohde.KOULUTUS_OIDS,
                    Joiner.on(" ").join(oids));
            q.addFilterQuery(Joiner.on(" ").join(queryParts));
        }
    }

    private void addFilterForHakukohdes(final List<String> oids,
            final List<String> queryParts, SolrQuery q) {
        if (oids.size() > 0) {
            addQuery("", queryParts, "%s:(%s)", Koulutus.HAKUKOHDE_OIDS,
                    Joiner.on(" ").join(oids));
            q.addFilterQuery(Joiner.on(" ").join(queryParts));
        }
    }

    private void addFilterForVuosiKausi(final String kausi, final Integer vuosi,
            final List<String> queryParts, SolrQuery q) {
        // vuosi kausi
        if (vuosi != null) {
            String qVuosi = vuosi <= 0 ? null : Integer.toString(vuosi);
            addQuery(qVuosi, queryParts, "%s:%s", Hakukohde.VUOSI_KOODI, qVuosi);
            q.addFilterQuery(Joiner.on(" ").join(queryParts));
            queryParts.clear();
        }
        addQuery(kausi, queryParts, "%s:%s", Hakukohde.KAUSI_KOODI, kausi);
        q.addFilterQuery(Joiner.on(" ").join(queryParts));
        queryParts.clear();
    }

    public KoulutuksetVastaus haeKoulutukset(
            final KoulutuksetKysely kysely) {

        KoulutuksetVastaus response = new KoulutuksetVastaus();

        String nimi = kysely.getNimi();
        final String kausi = kysely.getKoulutuksenAlkamiskausi();
        final Integer vuosi = kysely.getKoulutuksenAlkamisvuosi();

        final String koulutuksenTila = kysely.getKoulutuksenTila() != null ? kysely.getKoulutuksenTila().value() : null;
        final List<String> tarjoajaOids = kysely.getTarjoajaOids();
        final List<String> koulutusOids = kysely.getKoulutusOids();
        final List<String> hakukohdeOids = kysely.getHakukohdeOids();
        nimi = escape(nimi);

        final SolrQuery q = new SolrQuery(QUERY_ALL);
        final List<String> queryParts = Lists.newArrayList();

        // nimihaku
        if (nimi != null && nimi.length() > 0) {
            addQuery(nimi, queryParts, TEKSTIHAKU_TEMPLATE, Koulutus.TEKSTIHAKU,
                    nimi);
            q.addFilterQuery(Joiner.on(" ").join(queryParts));
            queryParts.clear();
        }

        if (koulutuksenTila != null) {
            q.addFilterQuery(String.format("%s:%s", Koulutus.TILA_EN, koulutuksenTila));
        }

        if (kysely.getKoulutusKoodi() != null) {
            q.addFilterQuery(String.format("%s:%s", Koulutus.KOULUTUSKOODI_URI, kysely.getKoulutusKoodi()));
        }

        // vuosi & kausi
        addFilterForVuosiKausi(kausi, vuosi, queryParts, q);

        // restrict by org
        addFilterForOrgs(tarjoajaOids, queryParts, q);


        //restrict with hakukohde oids
        if (hakukohdeOids != null && hakukohdeOids.size() > 0) {
            addFilterForHakukohdes(hakukohdeOids, queryParts, q);
        }

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

            for (SolrDocument doc : koulutusResponse.getResults()) {
                if (doc.getFieldValue(Hakukohde.ORG_OID) != null) {
                    orgOids.add((String) doc.getFieldValue(Hakukohde.ORG_OID));
                }
            }

            if (orgOids.size() > 0) {
                QueryResponse orgResponse = searchOrgs(orgOids, koulutusSolr);

                SolrDocumentToKoulutusmoduuliToteutusConverter converter = new SolrDocumentToKoulutusmoduuliToteutusConverter();

                response = converter
                        .convertSolrToKoulutuksetVastaus(
                        koulutusResponse.getResults(),
                        orgResponse.getResults());

            } else {
                response = new KoulutuksetVastaus();
            }

        } catch (SolrServerException e) {
            System.out.println("haku.error : " + e.toString());
            LOG.error("haku.error : " + e.toString());
            throw new RuntimeException("haku.error", e);
        }
        return response;
    }

    private void addFilterForKOulutus(List<String> tarjoajaOids, SolrQuery q) {
        q.addFilterQuery(String.format("%s:(%s)", Koulutus.OID, Joiner.on(" ").join(tarjoajaOids)));
    }
    
    private void addFilterForHakuOid(String haunOid, SolrQuery q) {
        q.addFilterQuery(String.format("%s:(%s)", Hakukohde.HAUN_OID, haunOid));
    }

    private void filterOutOrgs(SolrQuery query) {
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
