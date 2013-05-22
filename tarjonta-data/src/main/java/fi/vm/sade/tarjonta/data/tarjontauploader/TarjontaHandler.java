package fi.vm.sade.tarjonta.data.tarjontauploader;

import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.types.HakukohdeTyyppi;
import fi.vm.sade.tarjonta.service.types.LisaaKoulutusTyyppi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TarjontaHandler {
    private final Logger logger = LoggerFactory.getLogger(TarjontaHandler.class);

    private TarjontaPublicService tarjontaPublicService;
    private TarjontaAdminService tarjontaAdminService;

    @Autowired
    public TarjontaHandler(final TarjontaPublicService tarjontaPublicService, final TarjontaAdminService tarjontaAdminService) {
        this.tarjontaPublicService = tarjontaPublicService;
        this.tarjontaAdminService = tarjontaAdminService;
    }

    public void addKoulutus(final Koulutus koulutus) {
        logger.info("Lisätään koulutus");
        //tarjontaAdminService.lisaaKoulutus(getLisaaKoulutusTyyppi(koulutus));
    }

    public void addHakukohde(final Hakukohde hakukohde, final String hakuOid) {
        logger.info("Lisätään hakukohde");
        //tarjontaAdminService.lisaaHakukohde(getHakukohdeTyyppi(hakukohde, hakuOid));
    }

    private LisaaKoulutusTyyppi getLisaaKoulutusTyyppi(final Koulutus koulutus) {
        final LisaaKoulutusTyyppi lisaaKoulutusTyyppi = new LisaaKoulutusTyyppi();

        // TODO lisää kaikki tarvittavat tiedot

        return lisaaKoulutusTyyppi;
    }

    private HakukohdeTyyppi getHakukohdeTyyppi(final Hakukohde hakukohde, final String hakuOid) {
        final HakukohdeTyyppi hakukohdeTyyppi = new HakukohdeTyyppi();

        hakukohdeTyyppi.setHakukohteenHakuOid(hakuOid);

        // TODO lisää kaikki tarvittavat tiedot
        hakukohdeTyyppi.setOid(null); // TODO hae uusi oid

        return hakukohdeTyyppi;
    }
}
