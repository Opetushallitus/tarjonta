package fi.vm.sade.tarjonta.shared;

import fi.vm.sade.generic.rest.CachingRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OnrService {
    private static final Logger LOG = LoggerFactory.getLogger(OnrService.class);

    private final CachingRestClient cachingRestClient;
    private final UrlConfiguration urlConfiguration;

    @Autowired
    public OnrService(UrlConfiguration urlConfiguration,
                      @Value("${cas.service.oppijanumerorekisteri-service}") String targetService,
                      @Value("${tarjonta.oppijanumerorekisteri.username}") String clientAppUser,
                      @Value("${tarjonta.oppijanumerorekisteri.password}") String clientAppPass) {
        this.urlConfiguration = urlConfiguration;

        cachingRestClient = new CachingRestClient();
        cachingRestClient.setWebCasUrl(urlConfiguration.url("cas.url"));
        cachingRestClient.setCasService(targetService);
        cachingRestClient.setUsername(clientAppUser);
        cachingRestClient.setPassword(clientAppPass);
    }

    public String findUserAsiointikieli(String oid) {
        try {
            String url = urlConfiguration.url("oppijanumerorekisteri.henkilo.asiointiKieli", oid);
            return cachingRestClient.getAsString(url);
        } catch (Exception e) {
            LOG.error("Getting virkailija asiointikieli from ONR failed", e);
            throw new RuntimeException(e);
        }
    }

}
