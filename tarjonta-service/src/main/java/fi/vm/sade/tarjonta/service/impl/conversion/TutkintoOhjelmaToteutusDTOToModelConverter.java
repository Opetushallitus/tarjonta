package fi.vm.sade.tarjonta.service.impl.conversion;

import fi.vm.sade.generic.service.conversion.AbstractToDomainConverter;
import fi.vm.sade.tarjonta.model.TutkintoOhjelmaToteutus;
import fi.vm.sade.tarjonta.model.dto.TutkintoOhjelmaToteutusDTO;

public class TutkintoOhjelmaToteutusDTOToModelConverter extends AbstractToDomainConverter<TutkintoOhjelmaToteutusDTO, TutkintoOhjelmaToteutus> {

    @Override
    public TutkintoOhjelmaToteutus convert(TutkintoOhjelmaToteutusDTO source) {
        
        TutkintoOhjelmaToteutus model = new TutkintoOhjelmaToteutus();
        return model;
    }

}
