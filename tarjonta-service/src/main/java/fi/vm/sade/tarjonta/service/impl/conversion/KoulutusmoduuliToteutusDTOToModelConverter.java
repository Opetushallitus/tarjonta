package fi.vm.sade.tarjonta.service.impl.conversion;

import fi.vm.sade.generic.service.conversion.AbstractToDomainConverter;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliToteutusDTO;
import fi.vm.sade.tarjonta.model.dto.TutkintoOhjelmaToteutusDTO;

/**
 * Converter that delegates converting to the converter that knows how to handle
 * exact Koulutusmoduuli implementation.
 * 
 * @author Jukka Raanamo
 */
public class KoulutusmoduuliToteutusDTOToModelConverter extends AbstractToDomainConverter<KoulutusmoduuliToteutusDTO, KoulutusmoduuliToteutus> {

    @Override
    public KoulutusmoduuliToteutus convert(KoulutusmoduuliToteutusDTO source) {

        if (source instanceof TutkintoOhjelmaToteutusDTO) {
            return new TutkintoOhjelmaToteutusDTOToModelConverter().convert((TutkintoOhjelmaToteutusDTO) source);
        } else {
            throw new IllegalArgumentException("dont know how to convert: " + source);
        }
    }

}

