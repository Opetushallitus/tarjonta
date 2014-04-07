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
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde;
import fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.helper.OrganisaatioDisplayHelper;
import fi.vm.sade.organisaatio.service.search.OrganisaatioSearchService;

@Component
public class TarjontaSearchService {

    private static final String QUERY_ALL = "*:*";
    private static final String TEKSTIHAKU_TEMPLATE = "%s:*%s*";
    private static final String TILAHAKU_TEMPLATE = "%s:*%s*";
    @Value("${root.organisaatio.oid}")
    private String rootOrganisaatioOid;
    private final SolrServer koulutusSolr;
    private final SolrServer hakukohdeSolr;
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrganisaatioSearchService organisaatioSearchService;

    @Autowired
    public TarjontaSearchService(SolrServerFactory factory) {
        this.koulutusSolr = factory.getSolrServer("koulutukset");
        this.hakukohdeSolr = factory.getSolrServer("hakukohteet");
    }

    /**
     * 1st step in hakukohdehaku, returns organisation name (with provided
     * locale), hit searchCount
     *
     * @param locale
     * @param kysely
     * @return
     */
    public List<OrganisaatioHakukohdeGroup> haeHakukohteetGroupedByOrganisaatio(Locale locale, HakukohteetKysely kysely) {
        List<OrganisaatioHakukohdeGroup> hakukohteet = Lists.newArrayList();
        SolrQuery q = createHakukohdeQuery(kysely);
        q.add("group", "true");
        q.add("group.field", Hakukohde.ORG_OID);
        q.add("group.limit", "0");
        try {
            QueryResponse response = hakukohdeSolr.query(q);
            List<NamedList<Object>> resultList = NamedListUtil.from(response.getResponse()).get("grouped").get(SolrFields.Hakukohde.ORG_OID).get("groups").value();
            Set<String> orgOids = Sets.newHashSet();
            for (NamedList<Object> group : resultList) {
                final String oid = NamedListUtil.getValue(group, "groupValue");
                final SolrDocumentList results = NamedListUtil.from(group).get("doclist").value();
                final Long count = results.getNumFound();
                final OrganisaatioHakukohdeGroup g = new OrganisaatioHakukohdeGroup(oid, count);
                hakukohteet.add(g);
                orgOids.add(oid);
            }

            if (orgOids.size() > 0) {
                List<OrganisaatioPerustieto> orgs = organisaatioSearchService.findByOidSet(orgOids);
                Map<String, OrganisaatioPerustieto> oidOrgIndex = Maps.newHashMap();
                for (OrganisaatioPerustieto perus : orgs) {
                    oidOrgIndex.put(perus.getOid(), perus);
                }

                for (OrganisaatioHakukohdeGroup group : hakukohteet) {
                    final OrganisaatioPerustieto perus = oidOrgIndex.get(group.getOrganisaatioOid());
                    if (perus != null) {
                        group.setOrganisaatioNimi(OrganisaatioDisplayHelper.getClosestBasic(locale, perus));
                    } else {
                        group.setOrganisaatioNimi(group.getOrganisaatioOid());
                    }
                }
            }

        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        return hakukohteet;
    }

    public HakukohteetVastaus haeHakukohteet(
            final HakukohteetKysely kysely) {

        HakukohteetVastaus response = new HakukohteetVastaus();

        final SolrQuery q = createHakukohdeQuery(kysely);
        try {
            // query solr
            QueryResponse hakukohdeResponse = hakukohdeSolr.query(q);

            //now we have the hakukohteet, fetch orgs
            Set<String> orgOids = Sets.newHashSet();

            for (SolrDocument doc : hakukohdeResponse.getResults()) {
                if (doc.getFieldValue(Hakukohde.ORG_OID) != null) {
                    orgOids.add((String) doc.getFieldValue(Hakukohde.ORG_OID));
                }
            }

            if (orgOids.size() > 0) {
                Map<String, OrganisaatioPerustieto> orgResponse = searchOrgs(orgOids);
                SolrDocumentToHakukohdeConverter converter = new SolrDocumentToHakukohdeConverter();
                response = converter.convertSolrToHakukohteetVastaus(hakukohdeResponse.getResults(), orgResponse);
            } else {
                //empty result
                response = new HakukohteetVastaus();
            }

        } catch (SolrServerException e) {
            throw new RuntimeException("haku.error", e);
        }

        return response;
    }

    private SolrQuery createHakukohdeQuery(final HakukohteetKysely kysely) {
        String nimi = kysely.getNimi();
        final String kausi = kysely.getKoulutuksenAlkamiskausi();
        final Integer vuosi = kysely.getKoulutuksenAlkamisvuosi();
        final List<String> oids = kysely.getTarjoajaOids();
        final List<String> queryParts = Lists.newArrayList();
        //final String tila = kysely.getTilat() != null ? kysely.getTilat().name() : null;

        final List<String> tilat = Lists.newArrayList(Iterables.transform(kysely.getTilat(), new Function<TarjontaTila, String>() {
            public String apply(TarjontaTila tila) {
                return tila != null ? tila.name() : null;
            }
        }));

        final SolrQuery q = new SolrQuery(QUERY_ALL);

        // nimihaku
        if (nimi != null && nimi.length() > 0) {
            nimi = escape(nimi);
            queryParts.clear();
            addQuery(nimi, queryParts, TEKSTIHAKU_TEMPLATE,
                    Hakukohde.TEKSTIHAKU, nimi);
            q.addFilterQuery(Joiner.on(" ").join(queryParts));
            queryParts.clear();
        }

        if (tilat.size() > 0) {
            q.addFilterQuery(String.format("%s:(%s)", Hakukohde.TILA, Joiner.on(' ').skipNulls().join(tilat)));
        }else {
            //when an empty search, do not show koulutus status of deleted
            q.addFilterQuery(String.format("%s:%s", "-" + Hakukohde.TILA, TarjontaTila.POISTETTU));
        }


        if (kysely.getHakuOid() != null) {
            addFilterForHakuOid(kysely.getHakuOid(), q);
        }

        // vuosi & kausi
        addFilterForVuosiKausi(kausi, vuosi, queryParts, q);

        // restrict by org
        addFilterForOrgs(oids, queryParts, q);

        // restrict by hakukohdeoid
        if (kysely.getHakukohdeOid() != null) {
            q.addFilterQuery(String.format("%s:%s", Hakukohde.OID, kysely.getHakukohdeOid()));
        }

        addFilterForKoulutukset(kysely.getKoulutusOids(), queryParts, q);

        //restrict with koulutusastetyyppi
        if (kysely.getKoulutusasteTyypit().size() > 0) {
            final ArrayList<String> tyypit = Lists.newArrayList(Iterables.transform(kysely.getKoulutusasteTyypit(), new Function<KoulutusasteTyyppi, String>() {
                public String apply(KoulutusasteTyyppi src) {
                    return src.value();
                }
            }));
            q.addFilterQuery(String.format("%s:(%s)", Hakukohde.KOULUTUSASTETYYPPI, Joiner.on(" ").join(tyypit)));
        }

        q.setRows(Integer.MAX_VALUE);
        return q;
    }

    private Map<String, OrganisaatioPerustieto> searchOrgs(Set<String> orgOids) throws SolrServerException {
        Map<String, OrganisaatioPerustieto> oidIndex = Maps.newHashMap();
        List<OrganisaatioPerustieto> orgVastaus = organisaatioSearchService.findByOidSet(orgOids);
        for (OrganisaatioPerustieto org : orgVastaus) {
            oidIndex.put(org.getOid(), org);
        }
        return oidIndex;
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
        addQuery(kausi, queryParts, "%s:%s", Hakukohde.KAUSI_URI, kausi);
        q.addFilterQuery(Joiner.on(" ").join(queryParts));
        queryParts.clear();
    }

    public KoulutuksetVastaus haeKoulutukset(
            final KoulutuksetKysely kysely) {

        KoulutuksetVastaus response = new KoulutuksetVastaus();

        final SolrQuery q = createKoulutusQuery(kysely);

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
                Map<String, OrganisaatioPerustieto> orgs = searchOrgs(orgOids);

                SolrDocumentToKoulutusConverter converter = new SolrDocumentToKoulutusConverter();

                response = converter.convertSolrToKoulutuksetVastaus(koulutusResponse.getResults(), orgs);

            } else {
                response = new KoulutuksetVastaus();
            }

        } catch (SolrServerException e) {
            LOG.error("haku.error : " + e.toString());
            throw new RuntimeException("haku.error", e);
        }
        return response;
    }

    private SolrQuery createKoulutusQuery(final KoulutuksetKysely kysely) {
        String nimi = kysely.getNimi();
        final String kausi = kysely.getKoulutuksenAlkamiskausi();
        final Integer vuosi = kysely.getKoulutuksenAlkamisvuosi();

        final String koulutuksenTila = kysely.getKoulutuksenTila() != null ? kysely.getKoulutuksenTila().value() : null;
        final List<String> tarjoajaOids = kysely.getTarjoajaOids();
        final List<String> koulutusOids = kysely.getKoulutusOids();
        final List<String> hakukohdeOids = kysely.getHakukohdeOids();

        final SolrQuery q = new SolrQuery(QUERY_ALL);
        final List<String> queryParts = Lists.newArrayList();

        // nimihaku
        if (nimi != null && nimi.length() > 0) {
            nimi = escape(nimi);
            addQuery(nimi, queryParts, TEKSTIHAKU_TEMPLATE, Koulutus.TEKSTIHAKU,
                    nimi);
            q.addFilterQuery(Joiner.on(" ").join(queryParts));
            queryParts.clear();
        }

        if (koulutuksenTila != null) {
            q.addFilterQuery(String.format("%s:%s", Koulutus.TILA, koulutuksenTila));
        } else {
            //when an empty search, do not show koulutus status of deleted
            q.addFilterQuery(String.format("%s:%s", "-" + Koulutus.TILA, TarjontaTila.POISTETTU));
        }

        if (kysely.getKoulutusKoodi() != null) {
            q.addFilterQuery(String.format("%s:%s", Koulutus.KOULUTUSKOODI_URI, kysely.getKoulutusKoodi()));
        }

        //koulutuksen oid
        if (kysely.getKoulutusOid() != null) {
            q.addFilterQuery(String.format("%s:%s", Koulutus.OID, kysely.getKoulutusOid()));
        }

        //komoOid
        if (kysely.getKomoOid() != null) {
            q.addFilterQuery(String.format("%s:%s", Koulutus.KOULUTUSMODUULI_OID, kysely.getKomoOid()));
        }

        // vuosi & kausi
        addFilterForVuosiKausi(kausi, vuosi, queryParts, q);

        // restrict by org
        addFilterForOrgs(tarjoajaOids, queryParts, q);

        //restrict with hakukohde oids
        if (hakukohdeOids != null && hakukohdeOids.size() > 0) {
            addFilterForHakukohdes(hakukohdeOids, queryParts, q);
        }

        //restrict with koulutusastetyyppi
        if (kysely.getKoulutusasteTyypit().size() > 0) {
            final ArrayList<String> tyypit = Lists.newArrayList(Iterables.transform(kysely.getKoulutusasteTyypit(), new Function<KoulutusasteTyyppi, String>() {
                public String apply(KoulutusasteTyyppi src) {
                    return src.value();

                }
            }));
            q.addFilterQuery(String.format("%s:(%s)", Koulutus.KOULUTUSTYYPPI, Joiner.on(" ").join(tyypit)));
        }

        //restrict by koulutus
        if (koulutusOids.size() > 0) {
            addFilterForKOulutus(koulutusOids, q);
        }
        return q;
    }

    private void addFilterForKOulutus(List<String> tarjoajaOids, SolrQuery q) {
        q.addFilterQuery(String.format("%s:(%s)", Koulutus.OID, Joiner.on(" ").join(tarjoajaOids)));
    }

    private void addFilterForHakuOid(String haunOid, SolrQuery q) {
        q.addFilterQuery(String.format("%s:(%s)", Hakukohde.HAUN_OID, haunOid));
    }

    private void addQuery(final String param, final List<String> queryParts,
            String template, Object... params) {
        if (param != null) {
            queryParts.add(String.format(template, params));
        }
    }

    private String escape(String searchStr) {
        searchStr = ClientUtils.escapeQueryChars(searchStr);
        return searchStr;
    }
}
