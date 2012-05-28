package fi.vm.sade.tarjonta.service.impl.conversion;

import fi.vm.sade.generic.service.conversion.AbstractToDomainConverter;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliDTO;
import fi.vm.sade.tarjonta.model.dto.TutkintoOhjelmaDTO;

/**
 * Converter that delegates converting to the converter that knows how to handle
 * exact Koulutusmoduuli implementation.
 * 
 * @author Jukka Raanamo
 */
public class KoulutusmoduuliDTOToKoulutusmoduuliConverter extends AbstractToDomainConverter<KoulutusmoduuliDTO, Koulutusmoduuli> {

    @Override
    public Koulutusmoduuli convert(KoulutusmoduuliDTO source) {

        if (source instanceof TutkintoOhjelmaDTO) {
            return new TutkintoOhjelmaDTOToTutkintoOhjelmaConverter().convert((TutkintoOhjelmaDTO) source);
        } else {
            throw new IllegalArgumentException("dont know how to convert: " + source);
        }
    }

}

