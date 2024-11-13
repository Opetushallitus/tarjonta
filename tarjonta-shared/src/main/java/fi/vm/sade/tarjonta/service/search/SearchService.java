package fi.vm.sade.tarjonta.service.search;

import static fi.vm.sade.tarjonta.service.search.SolrFields.Koulutus.*;
import static fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper.getKoodiURIFromVersionedUri;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.tarjonta.shared.OrganisaatioService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class SearchService {

  protected static final String QUERY_ALL = "*:*";
  protected static final String TEKSTIHAKU_TEMPLATE = "%s:*%s*";

  private OrganisaatioService organisaatioService;

  @Autowired
  public SearchService(OrganisaatioService organisaatioService) {
    this.organisaatioService = organisaatioService;
  }

  protected Map<String, OrganisaatioPerustieto> searchOrgs(Set<String> orgOids)
      throws SolrServerException {
    Map<String, OrganisaatioPerustieto> oidIndex = Maps.newHashMap();
    List<OrganisaatioPerustieto> orgVastaus =
        organisaatioService.findByUsingOrganisaatioCache(orgOids);
    for (OrganisaatioPerustieto org : orgVastaus) {
      oidIndex.put(org.getOid(), org);
    }
    return oidIndex;
  }

  protected String escape(String searchStr) {
    searchStr = ClientUtils.escapeQueryChars(searchStr);
    return searchStr;
  }

  protected void addQuery(
      final String param, final List<String> queryParts, String template, Object... params) {
    if (param != null) {
      queryParts.add(String.format(template, params));
    }
  }

  protected void addFilterForVuosiKausi(
      final String kausi, final Integer vuosi, final List<String> queryParts, SolrQuery q) {
    if (vuosi != null) {
      String qVuosi = vuosi <= 0 ? null : Integer.toString(vuosi);
      addQuery(qVuosi, queryParts, "%s:%s", VUOSI_KOODI, qVuosi);
      q.addFilterQuery(Joiner.on(" ").join(queryParts));
      queryParts.clear();
    }

    if (kausi != null) {
      addQuery(kausi, queryParts, getFilterQueryForUri(KAUSI_URI, kausi));
    }

    q.addFilterQuery(Joiner.on(" ").join(queryParts));
    queryParts.clear();
  }

  protected void addFilterForOrgs(
      final List<String> oids, final List<String> queryParts, SolrQuery q, String path) {
    if (oids.size() > 0) {
      addQuery("", queryParts, matchFull(), path, Joiner.on(" ").join(oids));
      q.addFilterQuery(Joiner.on(" ").join(queryParts));
    }
  }

  protected void addFilterForTarjoaja(
      final List<String> oids, final List<String> queryParts, SolrQuery q) {
    addFilterForOrgs(oids, queryParts, q, ORG_PATH);
  }

  protected void addFilterForJarjestaja(
      final List<String> oids, final List<String> queryParts, SolrQuery q) {
    addFilterForOrgs(oids, queryParts, q, JARJESTAJA_PATH);
  }

  protected String matchFull() {
    return "%s:(%s)";
  }

  private String matchUriWithUnknownVersion() {
    return "%s:(%s#*) %s:(%s)";
  }

  private String matchUriWithKnownVersion() {
    return "%s:(%s) %s:(%s)";
  }

  protected String getFilterQueryForUri(String fieldName, String fieldValue) {
    if (fieldValue.contains("#")) {
      return String.format(
          matchUriWithKnownVersion(),
          fieldName,
          getKoodiURIFromVersionedUri(fieldValue),
          fieldName,
          fieldValue);
    } else {
      return String.format(
          matchUriWithUnknownVersion(),
          fieldName,
          getKoodiURIFromVersionedUri(fieldValue),
          fieldName,
          fieldValue);
    }
  }

  protected String noMatch() {
    return "!%s:(%s)";
  }
}
