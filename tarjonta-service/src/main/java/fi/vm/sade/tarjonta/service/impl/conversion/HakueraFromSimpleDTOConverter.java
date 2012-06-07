package fi.vm.sade.tarjonta.service.impl.conversion;

import fi.vm.sade.generic.service.conversion.AbstractToDomainConverter;
import fi.vm.sade.tarjonta.model.Hakuera;
import fi.vm.sade.tarjonta.service.types.dto.HakueraSimpleDTO;

/**
 * @author Antti Salonen
 */
public class HakueraFromSimpleDTOConverter extends AbstractToDomainConverter<HakueraSimpleDTO, Hakuera> {

    @Override
    public Hakuera convert(HakueraSimpleDTO hakueraSimpleDTO) {
        throw new RuntimeException("not impl, not needed?");
    }

}
