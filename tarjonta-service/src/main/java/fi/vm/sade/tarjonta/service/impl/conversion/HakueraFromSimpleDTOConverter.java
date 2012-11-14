package fi.vm.sade.tarjonta.service.impl.conversion;

import fi.vm.sade.generic.service.conversion.AbstractToDomainConverter;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.service.types.HakueraSimpleTyyppi;

/**
 * @author Antti Salonen
 */
public class HakueraFromSimpleDTOConverter extends AbstractToDomainConverter<HakueraSimpleTyyppi, Haku> {

    @Override
    public Haku convert(HakueraSimpleTyyppi hakueraSimpleDTO) {
        throw new RuntimeException("not impl, not needed?");
    }

}
