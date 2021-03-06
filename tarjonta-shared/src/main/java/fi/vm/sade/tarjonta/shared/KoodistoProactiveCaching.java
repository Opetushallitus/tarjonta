package fi.vm.sade.tarjonta.shared;

import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.CachingKoodistoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KoodistoProactiveCaching {

    private CachingKoodistoClient cachingKoodistoClient;

    @Autowired
    public KoodistoProactiveCaching(UrlConfiguration urlConfiguration) {
        this.cachingKoodistoClient = new CachingKoodistoClient(urlConfiguration.url("koodisto-service.base"));
        this.cachingKoodistoClient.setCallerId(HttpClientConfiguration.CALLER_ID);
        this.clearCache();
    }

    private Map<String, KoodiType> koodiMap;

    public void clearCache() {
        koodiMap = new HashMap<>();
    }

    public void cacheKoodisto(String koodisto) {
        cacheKoodisto(koodisto, null);
    }

    public void cacheKoodisto(String koodisto, Integer version) {
        List<KoodiType> koodiTypes = cachingKoodistoClient.getKoodisForKoodisto(koodisto, version);

        for (KoodiType koodi : koodiTypes) {
            koodiMap.put(koodi.getKoodiUri() + "#" + koodi.getVersio(), koodi);

            // Also direct access without version (use latest code version)
            if (version == null) {
                koodiMap.put(koodi.getKoodiUri(), koodi);
            }
        }
    }

    public KoodiType getKoodi(String koodi) {
        String[] koodiAndVersion = TarjontaKoodistoHelper.splitKoodiURIWithVersion(koodi);
        if("-1".equals(koodiAndVersion[1])) {
            return koodiMap.get(koodiAndVersion[0]);
        }
        return koodiMap.get(koodi);
    }

}
