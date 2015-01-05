package fi.vm.sade.tarjonta.service.search;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.service.search.OrganisaatioSearchService;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.*;

public class SearchService {

    protected static final String QUERY_ALL = "*:*";
    protected static final String TEKSTIHAKU_TEMPLATE = "%s:*%s*";

    @Autowired
    private OrganisaatioSearchService organisaatioSearchService;

    public SearchService() {
    }

    protected Map<String, OrganisaatioPerustieto> searchOrgs(Set<String> orgOids) throws SolrServerException {
        Map<String, OrganisaatioPerustieto> oidIndex = Maps.newHashMap();
        List<OrganisaatioPerustieto> orgVastaus = organisaatioSearchService.findByOidSet(orgOids);
        for (OrganisaatioPerustieto org : orgVastaus) {
            oidIndex.put(org.getOid(), org);
        }
        return oidIndex;
    }

    protected String escape(String searchStr) {
        searchStr = ClientUtils.escapeQueryChars(searchStr);
        return searchStr;
    }

    protected void addQuery(final String param, final List<String> queryParts,
                            String template, Object... params) {
        if (param != null) {
            queryParts.add(String.format(template, params));
        }
    }

    protected void addFilterForVuosiKausi(final String kausi, final Integer vuosi,
                                          final List<String> queryParts, SolrQuery q) {
        if (vuosi != null) {
            String qVuosi = vuosi <= 0 ? null : Integer.toString(vuosi);
            addQuery(qVuosi, queryParts, "%s:%s", VUOSI_KOODI, qVuosi);
            q.addFilterQuery(Joiner.on(" ").join(queryParts));
            queryParts.clear();
        }
        addQuery(kausi, queryParts, matchUri(), KAUSI_URI, kausi);
        q.addFilterQuery(Joiner.on(" ").join(queryParts));
        queryParts.clear();
    }

    protected void addFilterForOrgs(final List<String> oids,
                                    final List<String> queryParts, SolrQuery q) {
        if (oids.size() > 0) {
            addQuery("", queryParts, matchFull(), ORG_PATH,
                    Joiner.on(" ").join(oids));
            q.addFilterQuery(Joiner.on(" ").join(queryParts));
        }
    }

    protected String matchFull() {
        return "%s:(%s)";
    }

    protected String matchUri() {
        return "%s:(%s*)";
    }

    protected String noMatch() {
        return "!%s:(%s)";
    }

}
