package fi.vm.sade.tarjonta.service.search;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fi.vm.sade.tarjonta.service.search.SolrFields.Hakukohde.*;
import static fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper.getKoodiURIFromVersionedUri;

@Component
public class HakukohdeSearchService extends SearchService {

    private final SolrServer hakukohdeSolr;

    @Autowired
    public HakukohdeSearchService(SolrServerFactory factory) {
        this.hakukohdeSolr = factory.getSolrServer("hakukohteet");
    }

    public HakukohteetVastaus haeHakukohteet(final HakukohteetKysely kysely) {
        return haeHakukohteet(kysely, null);
    }

    public HakukohteetVastaus haeHakukohteet(final HakukohteetKysely kysely, String defaultTarjoaja) {

        HakukohteetVastaus response;

        final SolrQuery q = createHakukohdeQuery(kysely);

        try {
            QueryResponse hakukohdeResponse = hakukohdeSolr.query(q);

            Set<String> orgOids = Sets.newHashSet();

            for (SolrDocument doc : hakukohdeResponse.getResults()) {
                // KJOH-778 fallback
                String fallBackField = "orgoid_s";

                if (doc.getFieldValue(ORG_OID) != null) {
                    for (String tmpOrgOid : (ArrayList<String>) doc.getFieldValue(ORG_OID)) {
                        orgOids.add(tmpOrgOid);
                    }
                } else if (doc.getFieldValue(fallBackField) != null) {
                    orgOids.add((String) doc.getFieldValue(fallBackField));
                }
            }

            if (orgOids.size() > 0) {
                Map<String, OrganisaatioPerustieto> orgResponse = searchOrgs(orgOids);
                SolrDocumentToHakukohdeConverter converter = new SolrDocumentToHakukohdeConverter();
                response = converter.convertSolrToHakukohteetVastaus(hakukohdeResponse.getResults(), orgResponse, defaultTarjoaja);
            } else {
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
        final List<String> tilat = Lists.newArrayList(Iterables.transform(kysely.getTilat(), new Function<TarjontaTila, String>() {
            public String apply(TarjontaTila tila) {
                return tila != null ? tila.name() : null;
            }
        }));

        final SolrQuery q = new SolrQuery(QUERY_ALL);

        addFilterForHakukohdeNimi(nimi, queryParts, q);
        addFilterForHakukohdeTila(tilat, q);
        addFilterForHakuOid(kysely, q);
        addFilterForVuosiKausi(kausi, vuosi, queryParts, q);
        addFilterForOrgs(oids, queryParts, q);
        addFilterForHakutapa(kysely, q);
        addFilterForHakutyyppi(kysely, q);
        addFilterForHakukohdeOid(kysely, q);
        addFilterForOrganisaatioRyhmaOid(kysely, q);
        addFilterForKoulutus(kysely, queryParts, q);
        addFilterForKoulutusastetyypit(kysely, q);
        addFilterForKoulutustyypit(kysely, q);
        addFilterForKoulutuslaji(kysely, q);
        addFilterForKohdejoukko(kysely, q);
        addFilterForOppilaitostyyppi(kysely, q);
        addFilterForKunta(kysely, q);
        addFilterForOpetuskielet(kysely, q);
        addFilterForKoulutusmoduuliTyyppi(kysely.getKoulutusmoduuliTyyppi(), q);

        q.setRows(Integer.MAX_VALUE);
        return q;
    }

    private void addFilterForKohdejoukko(HakukohteetKysely kysely, SolrQuery q) {
        if (kysely.getKohdejoukko() != null) {
            q.addFilterQuery(String.format(matchUri(), KOHDEJOUKKO_URI, getKoodiURIFromVersionedUri(kysely.getKohdejoukko())));
        }
    }

    private void addFilterForKoulutustyypit(HakukohteetKysely kysely, SolrQuery q) {
        if (kysely.getKoulutustyyppi().size() > 0) {
            q.addFilterQuery(String.format(matchUri(), KOULUTUSTYYPPI_URI, Joiner.on(" ").join(kysely.getKoulutustyyppi())));
        }
    }

    private void addFilterForKoulutusastetyypit(HakukohteetKysely kysely, SolrQuery q) {
        if (kysely.getKoulutusasteTyypit().size() > 0) {
            final ArrayList<String> tyypit = Lists.newArrayList(Iterables.transform(kysely.getKoulutusasteTyypit(), new Function<KoulutusasteTyyppi, String>() {
                public String apply(KoulutusasteTyyppi src) {
                    return src.value();
                }
            }));
            q.addFilterQuery(String.format(matchUri(), KOULUTUSASTETYYPPI, Joiner.on(" ").join(tyypit)));
        }
    }

    private void addFilterForOrganisaatioRyhmaOid(HakukohteetKysely kysely, SolrQuery q) {
        if (kysely.getOrganisaatioRyhmaOid() != null) {
            q.addFilterQuery(String.format(matchFull(), ORGANISAATIORYHMAOID, kysely.getOrganisaatioRyhmaOid()));
        }
    }

    private void addFilterForHakukohdeOid(HakukohteetKysely kysely, SolrQuery q) {
        if (kysely.getHakukohdeOid() != null) {
            q.addFilterQuery(String.format(matchFull(), OID, kysely.getHakukohdeOid()));
        }
    }

    private void addFilterForHakukohdeTila(List<String> tilat, SolrQuery q) {
        if (tilat.size() > 0) {
            q.addFilterQuery(String.format(matchFull(), TILA, Joiner.on(' ').skipNulls().join(tilat)));
        } else {
            //when an empty search, do not show koulutus status of deleted
            q.addFilterQuery(String.format(matchFull(), "-" + TILA, TarjontaTila.POISTETTU));
        }
    }

    private void addFilterForHakukohdeNimi(String nimi, List<String> queryParts, SolrQuery q) {
        if (nimi != null && nimi.length() > 0) {
            nimi = escape(nimi);
            queryParts.clear();
            addQuery(nimi, queryParts, TEKSTIHAKU_TEMPLATE, TEKSTIHAKU, nimi);
            q.addFilterQuery(Joiner.on(" ").join(queryParts));
            queryParts.clear();
        }
    }

    private void addFilterForKoulutus(final HakukohteetKysely kysely,
                                      final List<String> queryParts, SolrQuery q) {
        if (kysely.getKoulutusOids().size() > 0) {
            addQuery("", queryParts, matchFull(), KOULUTUS_OIDS, Joiner.on(" ").join(kysely.getKoulutusOids()));
            q.addFilterQuery(Joiner.on(" ").join(queryParts));
        }
    }

    private void addFilterForHakuOid(HakukohteetKysely kysely, SolrQuery q) {
        if (kysely.getHakuOid() != null) {
            q.addFilterQuery(String.format(matchFull(), HAUN_OID, kysely.getHakuOid()));
        }
    }

    private void addFilterForHakutapa(HakukohteetKysely kysely, SolrQuery q) {
        if (kysely.getHakutapa() != null) {
            q.addFilterQuery(String.format(matchUri(), HAKUTAPA_URI, getKoodiURIFromVersionedUri(kysely.getHakutapa())));
        }
    }

    private void addFilterForHakutyyppi(HakukohteetKysely kysely, SolrQuery q) {
        if (kysely.getHakutyyppi() != null) {
            q.addFilterQuery(String.format(matchUri(), HAKUTYYPPI_URI, getKoodiURIFromVersionedUri(kysely.getHakutyyppi())));
        }
    }

    private void addFilterForKunta(HakukohteetKysely kysely, SolrQuery q) {
        if (kysely.getKunta() != null) {
            q.addFilterQuery(String.format(matchUri(), KUNTA_URIS, getKoodiURIFromVersionedUri(kysely.getKunta())));
        }
    }

    private void addFilterForOpetuskielet(HakukohteetKysely kysely, SolrQuery q) {
        if (!kysely.getOpetuskielet().isEmpty()) {
            q.addFilterQuery(String.format(matchFull(), OPETUSKIELI_URIS, Joiner.on(" ").join(kysely.getOpetuskielet())));
        }
    }

    private void addFilterForOppilaitostyyppi(HakukohteetKysely kysely, SolrQuery q) {
        if (kysely.getOppilaitostyyppi() != null) {
            q.addFilterQuery(String.format(matchUri(), OPPILAITOSTYYPPI_URIS, getKoodiURIFromVersionedUri(kysely.getOppilaitostyyppi())));
        }
    }

    private void addFilterForKoulutuslaji(HakukohteetKysely kysely, SolrQuery q) {
        if (kysely.getKoulutuslaji() != null) {
            q.addFilterQuery(String.format(matchUri(), KOULUTUSLAJI_URIS, getKoodiURIFromVersionedUri(kysely.getKoulutuslaji())));
        }
    }

    private void addFilterForKoulutusmoduuliTyyppi(List<KoulutusmoduuliTyyppi> tyypit, SolrQuery q) {
        if (tyypit.size() > 0) {
            final ArrayList<String> strings = Lists.newArrayList(Iterables.transform(tyypit, new Function<KoulutusmoduuliTyyppi, String>() {
                @Override
                public String apply(KoulutusmoduuliTyyppi src) {
                    return src.name();
                }
            }));
            q.addFilterQuery(String.format("%s:(%s)", KOULUTUSMODUULITYYPPI_ENUM, Joiner.on(" ").join(strings)));
        }
    }
}
