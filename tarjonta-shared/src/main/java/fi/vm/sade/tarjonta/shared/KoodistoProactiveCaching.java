package fi.vm.sade.tarjonta.shared;

import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.CachingKoodistoClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KoodistoProactiveCaching {

    CachingKoodistoClient cachingKoodistoClient;

    public KoodistoProactiveCaching() {
        this.cachingKoodistoClient = new CachingKoodistoClient();
        this.clearCache();
    }

    Map<String, KoodiType> koodiMap;

    public void clearCache() {
        koodiMap = new HashMap<String, KoodiType>();
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
        return koodiMap.get(koodi);
    }

}
