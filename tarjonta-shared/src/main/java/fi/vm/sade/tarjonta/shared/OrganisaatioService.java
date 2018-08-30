package fi.vm.sade.tarjonta.shared;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.dto.v2.OrganisaatioHakutulosSuppeaDTOV2;
import fi.vm.sade.organisaatio.dto.v2.OrganisaatioPerustietoSuppea;
import fi.vm.sade.organisaatio.dto.v3.OrganisaatioRDTOV3;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
;


import fi.vm.sade.tarjonta.service.impl.conversion.rest.OrganisaatioRDTOV3ToOrganisaatioPerustietoConverter;
import fi.vm.sade.tarjonta.shared.organisaatio.OrganisaatioResultDTO;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class OrganisaatioService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectMapper ignoreFieldsObjectMapper = createIgnoreFieldsObjectMapper();
    private static ObjectMapper createIgnoreFieldsObjectMapper() {
        ObjectMapper m = new ObjectMapper();
        m.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return m;
    }
    private static final Logger LOG = LoggerFactory.getLogger(OrganisaatioService.class);

    private final TarjontaKoodistoHelper tarjontaKoodistoHelper;

    private final UrlConfiguration urlConfiguration;

    private final LoadingCache<String, OrganisaatioRDTO> orgCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build(new CacheLoader<String, OrganisaatioRDTO>() {
                public OrganisaatioRDTO load(String oid) {
                    return fetchOrganisation(oid);
                }
            });

    private final LoadingCache<String, OrganisaatioResultDTO> orgHaeCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build(new CacheLoader<String, OrganisaatioResultDTO>() {
                public OrganisaatioResultDTO load(String oid) {
                    return fetchOrganisationWithHaeAPI(oid);
                }
            });

    private final LoadingCache<String, String> koulutustoimijaCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build(new CacheLoader<String, String>() {
                public String load(String oid) {
                    return fetchKoulutustoimija(oid);
                }
            });

    @Autowired
    public OrganisaatioService(TarjontaKoodistoHelper tarjontaKoodistoHelper, UrlConfiguration urlConfiguration) {
        this.tarjontaKoodistoHelper = tarjontaKoodistoHelper;
        this.urlConfiguration = urlConfiguration;
    }

    /**
     * Enum declaration is only available in Organisation SOAP/WSDL description,
     * which is being deprecated. Therefore the enum is "duplicated" here,
     * until a similar Enum is provided by the organisation API.
     */
    public enum OrganisaatioTyyppi {
        KOULUTUSTOIMIJA("Koulutustoimija"),
        OPPILAITOS("Oppilaitos"),
        TOIMIPISTE("Toimipiste"),
        OPPISOPIMUSTOIMIPISTE("Oppisopimustoimipiste"),
        MUU_ORGANISAATIO("Muu organisaatio"),
        RYHMA("Ryhma"),
        VARHAISKASVATUKSEN_JARJESTAJA("Varhaiskasvatuksen jarjestaja"),
        VARHAISKASVATUKSEN_TOIMIPAIKKA("Varhaiskasvatuksen toimipaikka"),
        TYOELAMAJARJESTO("Tyoelamajarjesto");

        private final String value;

        OrganisaatioTyyppi(String v) {
            value = v;
        }

        public String value() {
            return value;
        }

        public static OrganisaatioTyyppi fromValue(String v) {
            for (OrganisaatioTyyppi c: OrganisaatioTyyppi.values()) {
                if (c.value.equals(v)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(v);
        }
    }

    public OrganisaatioRDTO findByOid(String oid) {
        try {
            return orgCache.get(oid);
        } catch (ExecutionException e) {
            final String msg = "Getting organization from Guava cache failed. Org oid: " + oid;
            LOG.error(msg);
            throw new RuntimeException(msg);
        }
    }
    public OrganisaatioResultDTO findByOidWithHaeAPI(String oid) {
        try {
            return orgHaeCache.get(oid);
        } catch (ExecutionException e) {
            final String msg = "Getting organization from Guava cache failed for hae API. Org oid: " + oid;
            LOG.error(msg);
            throw new RuntimeException(msg);
        }
    }

    public String findKoulutustoimijaForOrganisation(String oid){
        try {
            return koulutustoimijaCache.get(oid);
        } catch (ExecutionException e) {
            final String msg = "Getting organization from Guava cache failed for hae API. Org oid: " + oid;
            LOG.error(msg);
            throw new RuntimeException(msg);
        }
    }
    private OrganisaatioResultDTO fetchOrganisationWithHaeAPI(String oid) {
        try {
            return ignoreFieldsObjectMapper.readValue(new URL(urlConfiguration.url("organisaatio-service.organisaatio.hae", oid)), OrganisaatioResultDTO.class);
        } catch (Exception e) {
            final String msg = "Could not fetch organization with oid " + oid;
            LOG.error(msg);
            throw new RuntimeException(msg);
        }
    }

    private OrganisaatioRDTO fetchOrganisation(String oid) {
        try {
            return objectMapper.readValue(new URL(urlConfiguration.url("organisaatio-service.fetchOrganisation", oid)), OrganisaatioRDTO.class);
        } catch (Exception e) {
            final String msg = "Could not fetch organization with oid " + oid;
            LOG.error(msg);
            throw new RuntimeException(msg);
        }
    }

    private String fetchKoulutustoimija(String oid) {
        try {
            OrganisaatioResultDTO org = ignoreFieldsObjectMapper.readValue(new URL(urlConfiguration.url("organisaatio-service.organisaatio.hierarkia", oid)), OrganisaatioResultDTO.class);
            return org.getOrganisaatiot().get(0).getOid();
        } catch (Exception e) {
            final String msg = "Could not fetch organization with oid " + oid;
            LOG.error(msg);
            throw new RuntimeException(msg);
        }
    }

    public Set<String> findChildrenOidsByOid(String oid) {
        try {
            OrganisaatioHakutulosSuppeaDTOV2 result = objectMapper.readValue(
                    new URL(urlConfiguration.url("organisaatio-service.findChildrenOidsByOid", oid)), OrganisaatioHakutulosSuppeaDTOV2.class);
            return FluentIterable
                    .from(result.getOrganisaatiot())
                    .transform(org -> org.getOid())
                    .filter(Predicates.not(Predicates.equalTo(oid)))
                    .toSet();
        } catch (Exception e) {
            final String msg = "Could not fetch child oids for organization with oid " + oid;
            LOG.error(msg);
            throw new RuntimeException(msg);
        }
    }

    public List<OrganisaatioPerustieto> findByOidSet(Set<String> organisaatioOids) {
        if (organisaatioOids.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        JSONArray oids = new JSONArray();
        for (String oid: organisaatioOids) {
            oids.put(oid);
        }
        OrganisaatioRDTOV3ToOrganisaatioPerustietoConverter converter = new OrganisaatioRDTOV3ToOrganisaatioPerustietoConverter();
        try {

            List<OrganisaatioRDTOV3> results = (List<OrganisaatioRDTOV3>) objectMapper.readValue(new URL(urlConfiguration.url("organisaatio-service.findByOids", oids)), OrganisaatioRDTOV3.class);

            List<OrganisaatioPerustieto> convertedResults = new ArrayList<>();
            for (OrganisaatioRDTOV3 dto : results) {
                convertedResults.add(converter.convert(dto));
            }
            return convertedResults;
        } catch (Exception e) {
            final String msg = "Could not fetch organization with oid set " + organisaatioOids;
            LOG.error(msg);
            throw new RuntimeException(msg);
        }
    }
/*
    public List<OrganisaatioPerustieto> findByOidSet(Set<String> organisationOids) {
        SolrQuery q = new SolrQuery(String.format(SolrOrgFields.OID + ":(%s)", Joiner.on(" ").join(organisationOids)));
        q.setRows(organisationOids.size());

        final SolrDocumentToOrganisaatioPerustietoTypeFunction converter = new SolrDocumentToOrganisaatioPerustietoTypeFunction(null);

        try {
            List<OrganisaatioPerustieto> result = Lists.newArrayList(
            Lists.transform(
            solr.query(q, SolrRequest.METHOD.POST).getResults(),
                    converter));
            return result;
        } catch (SolrServerException e) {
            throw new RuntimeException(e);
        }

    }
*/



    public Map<String, String> getTarjoajaNimiMap(String orgOid) {
        final OrganisaatioRDTO org = findByOid(orgOid);
        if (org != null) {
            final Map<String, String> map = new HashMap<>();
            org.getNimi().forEach((key, value) ->
                    map.put(tarjontaKoodistoHelper.convertKielikoodiToKieliUri(key), value)
            );
            return map;
        }
        return new HashMap<>();
    }
}
