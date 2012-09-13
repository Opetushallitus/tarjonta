package fi.vm.sade.tarjonta.service.impl.conversion;

import fi.vm.sade.generic.service.conversion.AbstractFromDomainConverter;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.service.types.dto.HakueraSimpleDTO;

/**
 * @author Antti Salonen
 */
public class HakueraToSimpleDTOConverter extends AbstractFromDomainConverter<Haku, HakueraSimpleDTO> {

    @Override
    public HakueraSimpleDTO convert(Haku hakuera) {
        HakueraSimpleDTO dto = new HakueraSimpleDTO();
        dto.setOid(hakuera.getOid());
        dto.setNimiFi(hakuera.getNimiFi());
        dto.setNimiSv(hakuera.getNimiSv());
        dto.setNimiEn(hakuera.getNimiEn());
        return dto;
    }
}
