package fi.vm.sade.tarjonta.service.tasks;

import fi.vm.sade.tarjonta.shared.KoodistoProactiveCaching;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
@Profile("default")
public class PreloadKoodisto {

    @Autowired
    KoodistoProactiveCaching koodistoProactiveCaching;

    // Kerran vuorokaudessa
    @Scheduled(fixedDelay=1000 * 60 * 60 * 24)
    public void loadKoodistot() {
        koodistoProactiveCaching.clearCache();

        koodistoProactiveCaching.cacheKoodisto("koulutus");
        koodistoProactiveCaching.cacheKoodisto("koulutus", 1);
        koodistoProactiveCaching.cacheKoodisto("koulutus", 2);
        koodistoProactiveCaching.cacheKoodisto("koulutus", 3);
        koodistoProactiveCaching.cacheKoodisto("koulutus", 4);
        koodistoProactiveCaching.cacheKoodisto("kieli");
        koodistoProactiveCaching.cacheKoodisto("kausi");
        koodistoProactiveCaching.cacheKoodisto("tutkintonimikkeet");
        koodistoProactiveCaching.cacheKoodisto("tutkintonimikkeet", 1);
        koodistoProactiveCaching.cacheKoodisto("tutkintonimikekk");
        koodistoProactiveCaching.cacheKoodisto("aiheet");
        koodistoProactiveCaching.cacheKoodisto("osaamisala");
        koodistoProactiveCaching.cacheKoodisto("osaamisala", 1);
        koodistoProactiveCaching.cacheKoodisto("osaamisala", 2);
        koodistoProactiveCaching.cacheKoodisto("koulutusohjelmaamm");
        koodistoProactiveCaching.cacheKoodisto("lukiolinjat");
        koodistoProactiveCaching.cacheKoodisto("pohjakoulutusvaatimustoinenaste");
        koodistoProactiveCaching.cacheKoodisto("koulutuslaji");
        koodistoProactiveCaching.cacheKoodisto("koulutustyyppi");
        koodistoProactiveCaching.cacheKoodisto("koulutustyyppi", 1);
        koodistoProactiveCaching.cacheKoodisto("koulutusasteoph2002");
        koodistoProactiveCaching.cacheKoodisto("koulutusalaoph2002");
        koodistoProactiveCaching.cacheKoodisto("opintoalaoph2002");
        koodistoProactiveCaching.cacheKoodisto("opintojenlaajuus");
        koodistoProactiveCaching.cacheKoodisto("opintojenlaajuusyksikko");
        koodistoProactiveCaching.cacheKoodisto("eqf");
        koodistoProactiveCaching.cacheKoodisto("suunniteltukesto");
        koodistoProactiveCaching.cacheKoodisto("tutkinto");
        koodistoProactiveCaching.cacheKoodisto("ammattiluokitus");
        koodistoProactiveCaching.cacheKoodisto("opetusmuoto");
        koodistoProactiveCaching.cacheKoodisto("opetusmuotokk");
        koodistoProactiveCaching.cacheKoodisto("opetusaikakk");
        koodistoProactiveCaching.cacheKoodisto("opetuspaikkakk");
        koodistoProactiveCaching.cacheKoodisto("lukiodiplomit");
        koodistoProactiveCaching.cacheKoodisto("posti");
        koodistoProactiveCaching.cacheKoodisto("oppilaitostyyppi");
        koodistoProactiveCaching.cacheKoodisto("hakukohteet");
        koodistoProactiveCaching.cacheKoodisto("hakukohteet", 1);
        koodistoProactiveCaching.cacheKoodisto("hakukohteet", 2);
        koodistoProactiveCaching.cacheKoodisto("aikuhakukohteet");
        koodistoProactiveCaching.cacheKoodisto("aikuhakukohteet", 1);
        koodistoProactiveCaching.cacheKoodisto("aikuhakukohteet", 2);
        koodistoProactiveCaching.cacheKoodisto("aikuhakukohteet", 3);
        koodistoProactiveCaching.cacheKoodisto("hakukelpoisuusvaatimusta");
    }

}
