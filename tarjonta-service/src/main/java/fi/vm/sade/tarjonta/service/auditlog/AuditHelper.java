package fi.vm.sade.tarjonta.service.auditlog;

import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.publication.model.RestParam;
import fi.vm.sade.tarjonta.service.business.ContextDataService;
import fi.vm.sade.tarjonta.service.impl.conversion.rest.EntityConverterToRDTO;
import fi.vm.sade.tarjonta.service.impl.resources.v1.ConverterV1;
import fi.vm.sade.tarjonta.service.impl.resources.v1.KoulutusResourceImplV1;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakuV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class contains helper functions needed in different classes related to audit log. This is meant to be more
 * easily mocked than private helper methos inside target class.
 */


@Component
public class AuditHelper {

    private final ContextDataService contextDataService;

    private final EntityConverterToRDTO converterToRDTO;

    private final ConverterV1 converterV1;

    @Autowired
    public AuditHelper(ContextDataService contextDataService, EntityConverterToRDTO converterToRDTO, ConverterV1 converterV1) {
        this.contextDataService = contextDataService;
        this.converterToRDTO = converterToRDTO;
        this.converterV1 = converterV1;
    }


    public KoulutusV1RDTO getKomotoAsDto(KoulutusmoduuliToteutus komoto) {
        final RestParam param = RestParam.noImageAndShowMeta(contextDataService.getCurrentUserLang());
        return KoulutusResourceImplV1.convert(converterToRDTO, komoto, param);
    }

    public HakuV1RDTO getHakuAsDto(Haku haku) {
        return converterV1.fromHakuToHakuRDTO(haku, false);
    }



}
