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

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.shared.OrganisaatioService;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.ORGANISAATIORYHMAOID;
import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.*;

@Component
public class KoulutusSearchService extends SearchService {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private final SolrServer koulutusSolr;

    @Autowired
    public KoulutusSearchService(SolrServerFactory factory,
                                 OrganisaatioService organisaatioService) {
        super(organisaatioService);
        this.koulutusSolr = factory.getSolrServer("koulutukset");
    }

    public KoulutuksetVastaus haeKoulutukset(final KoulutuksetKysely kysely) {
        return haeKoulutukset(kysely, null);
    }

    public KoulutuksetVastaus haeKoulutukset(final KoulutuksetKysely kysely, String defaultTarjoaja) {

        KoulutuksetVastaus response;

        final SolrQuery q = createKoulutusQuery(kysely);

        try {
            // query solr
            q.setRows(Integer.MAX_VALUE);
            QueryResponse koulutusResponse = koulutusSolr.query(q);

            //now we have the hakukohteet, fetch orgs
            Set<String> orgOids = Sets.newHashSet();

            for (SolrDocument doc : koulutusResponse.getResults()) {
                if (doc.getFieldValue(ORG_OID) != null) {
                    orgOids.addAll((ArrayList) doc.getFieldValue(ORG_OID));
                }
                // KJOH-778 fallback
                else if (doc.get("orgoid_s") != null) {
                    orgOids.add((String) doc.getFieldValue("orgoid_s"));
                }
            }

            if (orgOids.size() > 0) {
                Map<String, OrganisaatioPerustieto> orgs = searchOrgs(orgOids);
                SolrDocumentToKoulutusConverter converter = new SolrDocumentToKoulutusConverter();
                response = converter.convertSolrToKoulutuksetVastaus(koulutusResponse.getResults(), orgs, defaultTarjoaja);
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
        final List<String> jarjestajaOids = kysely.getJarjestajaOids();
        final List<String> koulutusOids = kysely.getKoulutusOids();
        final List<String> hakukohdeOids = kysely.getHakukohdeOids();

        final SolrQuery q = new SolrQuery(QUERY_ALL);
        final List<String> queryParts = Lists.newArrayList();

        addFilterForNimi(nimi, q, queryParts);
        if (!kysely.showAllKoulutukset()) {
            addFilterForTila(koulutuksenTila, q);
        }
        addFilterForKoulutuskoodi(kysely, q);
        addFilterForKoulutuslaji(kysely, q);
        addFilterForKoulutusOid(kysely, q);
        addFilterForKomoOid(kysely, q);
        addFilterForVuosiKausi(kausi, vuosi, queryParts, q);
        addFilterForTarjoaja(tarjoajaOids, queryParts, q);
        addFilterForJarjestaja(jarjestajaOids, queryParts, q);
        addFilterForHakutapa(kysely.getHakutapa(), q);
        addFilterForHakutyyppi(kysely, q);
        addFilterForHakukohteet(hakukohdeOids, queryParts, q);
        addFilterForHaut(kysely.getHakuOids(), queryParts, q);
        addFilterForKoulutustyypit(kysely, q);
        addFilterForKoulutusasteTyypit(kysely, q);
        addFilterForToteutustyypit(kysely, q);
        addFilterForOids(koulutusOids, q);
        addFilterForKohdejoukko(kysely, q);
        addFilterForOppilaitostyyppi(kysely, q);
        addFilterForKunta(kysely, q);
        addFilterForOpetuskielet(kysely, q);
        addFilterForKoulutusmoduuliTyyppi(kysely.getKoulutusmoduuliTyyppi(), q);
        addFilterForHakukohderyhma(kysely, q);
        addFilterForKoodis(kysely, q);

        // Älä palauta valmistavia koulutuksia. Nämä on aina "liitetty" johonkin toiseen koulutukseen, eikä niitä
        // listata hakutuloksissa siitä syystä
        excludeValmistavatKoulutukset(q);

        return q;
    }

    private void addFilterForKoodis(KoulutuksetKysely kysely, SolrQuery q) {
        addWildCardFilter(q, KOULUTUSKOODI_URI, kysely.getKoulutuskoodis());
        addWildCardFilter(q, OPINTOALA_URI, kysely.getOpintoalakoodis());
        addWildCardFilter(q, KOULUTUSALA_URI, kysely.getKoulutusalakoodis());
    }

    private void addWildCardFilter(SolrQuery q, String field, List<String> values) {
        if (!values.isEmpty()) {
            List<String> wildCardValues = new ArrayList<String>();
            for (String val : values) {
                wildCardValues.add(val + '*');
            }

            q.addFilterQuery(
                String.format(matchFull(), field, Joiner.on(" ").join(
                    wildCardValues
                ))
            );
        }
    }

    private void addFilterForHakukohderyhma(KoulutuksetKysely kysely, SolrQuery q) {
        if (kysely.getHakukohderyhma() != null) {
            q.addFilterQuery(String.format(matchFull(), ORGANISAATIORYHMAOID, kysely.getHakukohderyhma()));
        }
    }

    private void addFilterForOpetuskielet(KoulutuksetKysely kysely, SolrQuery q) {
        if (!kysely.getOpetuskielet().isEmpty()) {
            q.addFilterQuery(String.format(matchFull(), OPETUSKIELI_URIS, Joiner.on(" ").join(kysely.getOpetuskielet())));
        }
    }


    private void addFilterForKunta(KoulutuksetKysely kysely, SolrQuery q) {
        if (kysely.getKunta() != null) {
            q.addFilterQuery(getFilterQueryForUri(KUNTA_URIS, kysely.getKunta()));
        }
    }

    private void addFilterForOppilaitostyyppi(KoulutuksetKysely kysely, SolrQuery q) {
        if (kysely.getOppilaitostyyppi() != null) {
            q.addFilterQuery(getFilterQueryForUri(OPPILAITOSTYYPPI_URIS, kysely.getOppilaitostyyppi()));
        }
    }


    private void addFilterForKohdejoukko(KoulutuksetKysely kysely, SolrQuery q) {
        if (kysely.getKohdejoukko() != null) {
            q.addFilterQuery(getFilterQueryForUri(KOHDEJOUKKO_URIS, kysely.getKohdejoukko()));
        }
    }


    private void excludeValmistavatKoulutukset(SolrQuery q) {
        q.addFilterQuery(String.format(noMatch(), TOTEUTUSTYYPPI_ENUM, "*_VALMISTAVA"));
    }

    private void addFilterForToteutustyypit(KoulutuksetKysely kysely, SolrQuery q) {
        if (kysely.getTotetustyyppi().size() > 0) {
            final ArrayList<String> tyypit = Lists.newArrayList(Iterables.transform(kysely.getTotetustyyppi(), src -> src.name()));
            q.addFilterQuery(String.format(matchFull(), TOTEUTUSTYYPPI_ENUM, Joiner.on(" ").join(tyypit)));
        }
    }

    private void addFilterForKoulutusasteTyypit(KoulutuksetKysely kysely, SolrQuery q) {
        if (kysely.getKoulutusasteTyypit().size() > 0) {
            final ArrayList<String> tyypit = Lists.newArrayList(Iterables.transform(kysely.getKoulutusasteTyypit(), src -> src.value()));
            q.addFilterQuery(String.format(matchFull(), KOULUTUSASTETYYPPI_ENUM, Joiner.on(" ").join(tyypit)));
        }
    }

    private void addFilterForKoulutustyypit(KoulutuksetKysely kysely, SolrQuery q) {
        if (kysely.getKoulutustyyppi().size() > 0) {
            final ArrayList<String> tyypit = Lists.newArrayList(Iterables.transform(kysely.getKoulutustyyppi(), src -> src));
            q.addFilterQuery(String.format(matchFull(), KOULUTUSTYYPPI_URI, Joiner.on(" ").join(tyypit)));
        }
    }

    private void addFilterForNimi(String nimi, SolrQuery q, List<String> queryParts) {
        if (nimi != null && nimi.length() > 0) {
            nimi = escape(nimi);
            addQuery(nimi, queryParts, TEKSTIHAKU_TEMPLATE, TEKSTIHAKU,
                    nimi);
            q.addFilterQuery(Joiner.on(" ").join(queryParts));
            queryParts.clear();
        }
    }

    private void addFilterForTila(String koulutuksenTila, SolrQuery q) {
        if (koulutuksenTila != null) {
            q.addFilterQuery(String.format(matchFull(), TILA, koulutuksenTila));
        } else {
            //when an empty search, do not show koulutus status of deleted
            q.addFilterQuery(String.format(matchFull(), "-" + TILA, TarjontaTila.POISTETTU));
        }
    }

    private void addFilterForKoulutuskoodi(KoulutuksetKysely kysely, SolrQuery q) {
        if (kysely.getKoulutusKoodi() != null) {
            q.addFilterQuery(getFilterQueryForUri(KOULUTUSKOODI_URI, kysely.getKoulutusKoodi()));
        }
    }

    private void addFilterForKoulutuslaji(KoulutuksetKysely kysely, SolrQuery q) {
        if (kysely.getKoulutuslaji() != null) {
            q.addFilterQuery(getFilterQueryForUri(KOULUTUSLAJI_URIS, kysely.getKoulutuslaji()));
        }
    }

    private void addFilterForKoulutusOid(KoulutuksetKysely kysely, SolrQuery q) {
        if (kysely.getKoulutusOid() != null) {
            q.addFilterQuery(String.format(matchFull(), OID, kysely.getKoulutusOid()));
        }
    }

    private void addFilterForKomoOid(KoulutuksetKysely kysely, SolrQuery q) {
        if (kysely.getKomoOid() != null) {
            q.addFilterQuery(String.format(matchFull(), KOULUTUSMODUULI_OID, kysely.getKomoOid()));
        }
    }

    private void addFilterForOids(List<String> oids, SolrQuery q) {
        if (oids.size() > 0) {
            q.addFilterQuery(String.format(matchFull(), OID, Joiner.on(" ").join(oids)));
        }
    }

    private void addFilterForHakutapa(String hakutapa, SolrQuery q) {
        if (hakutapa != null) {
            q.addFilterQuery(getFilterQueryForUri(HAKUTAPA_URIS, hakutapa));
        }
    }

    private void addFilterForHakutyyppi(KoulutuksetKysely kysely, SolrQuery q) {
        if (kysely.getHakutyyppi() != null) {
            q.addFilterQuery(getFilterQueryForUri(HAKUTYYPPI_URIS, kysely.getHakutyyppi()));
        }
    }

    private void addFilterForHakukohteet(final List<String> oids,
                                         final List<String> queryParts, SolrQuery q) {
        if (oids.size() > 0) {
            addQuery("", queryParts, matchFull(), HAKUKOHDE_OIDS,
                    Joiner.on(" ").join(oids));
            q.addFilterQuery(Joiner.on(" ").join(queryParts));
        }
    }

    private void addFilterForHaut(final List<String> oids, final List<String> queryParts, SolrQuery q) {
        if (oids.size() > 0) {
            addQuery("", queryParts, matchFull(), HAKU_OIDS, Joiner.on(" ").join(oids));
            q.addFilterQuery(Joiner.on(" ").join(queryParts));
        }
    }

    private void addFilterForKoulutusmoduuliTyyppi(List<KoulutusmoduuliTyyppi> tyypit, SolrQuery q) {
        if (tyypit.size() > 0) {
            final ArrayList<String> strings = Lists.newArrayList(Iterables.transform(tyypit, src -> src.name()));
            q.addFilterQuery(String.format("%s:(%s)", KOULUTUSMODUULITYYPPI_ENUM, Joiner.on(" ").join(strings)));
        }
    }
}
