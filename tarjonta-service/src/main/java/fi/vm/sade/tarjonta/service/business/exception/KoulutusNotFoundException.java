package fi.vm.sade.tarjonta.service.business.exception;

import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusIdentification;

public class KoulutusNotFoundException extends Exception {

    private KoulutusIdentification komotoId;

    public KoulutusNotFoundException(KoulutusIdentification komotoId) {
        super();
        this.komotoId = komotoId;
    }

    public KoulutusIdentification getKomotoId() {
        return komotoId;
    }

}
