package fi.vm.sade.tarjonta.shared;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Predicates;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.FluentIterable;
import fi.vm.sade.organisaatio.api.search.OrganisaatioHakutulos;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.dto.v2.OrganisaatioHakutulosSuppeaDTOV2;
import fi.vm.sade.organisaatio.dto.v3.OrganisaatioRDTOV3;
import fi.vm.sade.organisaatio.resource.dto.OrganisaatioRDTO;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.OrganisaatioRDTOV3ToOrganisaatioPerustietoConverter;
import fi.vm.sade.tarjonta.shared.organisaatio.OrganisaatioResultDTO;
import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrganisaatioService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectMapper ignoreFieldsObjectMapper = createIgnoreFieldsObjectMapper();
    private final Long cacheRefreshInterval;

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

    public OrganisaatioCache getOrganisaatioPerustietoCache() {
        return organisaatioPerustietoCache;
    }

    // Nollataan ennen indeksointia. Elinkaareksi on tarkoitettu indeksoinnin kesto.
    private final OrganisaatioCache organisaatioPerustietoCache;

    @Autowired
    public OrganisaatioService(TarjontaKoodistoHelper tarjontaKoodistoHelper,
                               UrlConfiguration urlConfiguration,
                               @Value("${tarjonta-service.organisaatiocache.refresh-interval-seconds: 60}") Long refreshInterval) {
        this.tarjontaKoodistoHelper = tarjontaKoodistoHelper;
        this.urlConfiguration = urlConfiguration;
        this.organisaatioPerustietoCache = new OrganisaatioCache();
        this.cacheRefreshInterval = refreshInterval;
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

    public synchronized long refreshCacheIfNeeded(OrganisaatioCache organisaatioCache) {
        if (ChronoUnit.SECONDS.between(organisaatioCache.getLastUpdated(), LocalDateTime.now()) > this.cacheRefreshInterval) {
            // Add organisations to cache (active, incoming and passive)
            List<OrganisaatioPerustieto> organisaatiosWithoutRootOrg = this.fetchAllOrganisationWithHaeAPI().getOrganisaatiot();
            organisaatioCache.populateOrganisaatioCache(new OrganisaatioPerustieto(), organisaatiosWithoutRootOrg);
            LOG.info("Organisation client cache refreshed with {} organisations", organisaatioCache.getCacheCount());
        }
        return organisaatioCache.getCacheCount();
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
            return ignoreFieldsObjectMapper.readValue(new URL(urlConfiguration.url("organisaatio-service.organisaatio.v2.hae-with-oid", oid)), OrganisaatioResultDTO.class);
        } catch (Exception e) {
            final String msg = "Could not fetch organization with oid " + oid;
            LOG.error(msg);
            throw new RuntimeException(msg);
        }
    }

    private OrganisaatioHakutulos fetchAllOrganisationWithHaeAPI() {
        try {
            return ignoreFieldsObjectMapper.readValue(new URL(urlConfiguration.url("organisaatio-service.organisaatio.v2.hae")), OrganisaatioHakutulos.class);
        } catch (Exception e) {
            final String msg = "Could not fetch all organizations";
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

    /**
     * Hakukohde organisaatio cachea hyödyntävä wrapperi. Hakee löytymättömät organisaatiotiedot organisaatiopalvelusta
     * @param organisaatioOids Setti organisaatio oideja
     * @return Lista organisaatioiden tietoja
     */
    public List<OrganisaatioPerustieto> findByUsingOrganisaatioCache(Set<String> organisaatioOids) {
        return this.findByUsingCache(organisaatioOids, this.organisaatioPerustietoCache);
    }

    /**
     * Hakee annetusta organisaatio cachesta löytymättömät organisaatiotiedot organisaatiopalvelusta
     * @param organisaatioOids Setti organisaatio oideja
     * @param cache Organisaatiocache
     * @return Lista organisaatioiden tietoja
     */
    public List<OrganisaatioPerustieto> findByUsingCache(Set<String> organisaatioOids, OrganisaatioCache cache) {
        this.refreshCacheIfNeeded(this.organisaatioPerustietoCache);
        Set<String> uncachedOrganisaatioOids = organisaatioOids.stream()
                .filter(organisaatioOid -> !cache.getByOid(organisaatioOid).isPresent())
                .collect(Collectors.toSet());
        this.findByOidSet(uncachedOrganisaatioOids)
                .forEach(cache::add);
        return organisaatioOids.stream()
                .map(cache::getByOid)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Hakee haluttujen organisaatioiden tiedot
     * organisaatiopalvelua.
     * @param organisaatioOids Organisaatioiden oidit
     * @return Organisaatioiden tiedot
     */
    public List<OrganisaatioPerustieto> findByOidSet(Set<String> organisaatioOids) {
        final AtomicInteger counter = new AtomicInteger(0);

        return organisaatioOids.stream()
                .distinct()
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / 100, Collectors.toSet()))
                .values().stream()
                .map(this::findByOidSetAtChunks)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<OrganisaatioPerustieto> findByOidSetAtChunks(Set<String> organisaatioOids) {
        if (organisaatioOids.isEmpty()) {
            return Collections.emptyList();
        }
        if (organisaatioOids.size() > 1000) {
            throw new IllegalStateException("Tried to fetch more than 1000 organisations at time.");
        }
        HttpURLConnection connection = null;
        try {
            JSONArray oids = new JSONArray();
            for (String oid : organisaatioOids) {
                oids.put(oid);
            }
            URL url = new URL(urlConfiguration.url("organisaatio-service.findByOids"));

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("clientSubSystemCode", "1.2.246.562.10.00000000001.tarjonta");
            connection.setRequestProperty("content-type", "application/json;charset=UTF-8");
            connection.setDoOutput(true);
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(10000);

            OutputStreamWriter wr= new OutputStreamWriter(connection.getOutputStream());
            wr.write(oids.toString());
            wr.flush();
            wr.close();

            List<OrganisaatioRDTOV3> results = objectMapper.readValue(IOUtils.toString(connection.getInputStream()),
                    new TypeReference<List<OrganisaatioRDTOV3>>() {}
            );

            List<OrganisaatioPerustieto> convertedResults = new ArrayList<>();
            OrganisaatioRDTOV3ToOrganisaatioPerustietoConverter converter = new OrganisaatioRDTOV3ToOrganisaatioPerustietoConverter();
            for (OrganisaatioRDTOV3 dto : results) {
                convertedResults.add(converter.convert(dto));
            }
            return convertedResults;
        } catch (SocketTimeoutException e) {
            final String msg = "Could not fetch organization with oid set " + organisaatioOids + " - connection timed out.";
            LOG.error(msg);
            throw new RuntimeException(msg);
        } catch (IOException e) {
            final String msg = "Could not fetch organization with oid set " + organisaatioOids;
            LOG.error(msg);
            throw new RuntimeException(msg);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

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
