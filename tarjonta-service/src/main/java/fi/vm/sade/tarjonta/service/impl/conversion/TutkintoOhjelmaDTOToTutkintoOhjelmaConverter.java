package fi.vm.sade.tarjonta.service.impl.conversion;

import fi.vm.sade.generic.service.conversion.AbstractToDomainConverter;
import fi.vm.sade.tarjonta.model.TutkintoOhjelma;
import fi.vm.sade.tarjonta.model.dto.TutkintoOhjelmaDTO;

public class TutkintoOhjelmaDTOToTutkintoOhjelmaConverter extends AbstractToDomainConverter<TutkintoOhjelmaDTO, TutkintoOhjelma> {

    @Override
    public TutkintoOhjelma convert(TutkintoOhjelmaDTO source) {
        
        TutkintoOhjelma model = new TutkintoOhjelma();
        return model;
    }

}
