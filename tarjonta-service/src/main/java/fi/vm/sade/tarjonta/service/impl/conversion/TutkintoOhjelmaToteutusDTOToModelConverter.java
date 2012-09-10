package fi.vm.sade.tarjonta.service.impl.conversion;


import fi.vm.sade.generic.service.conversion.AbstractToDomainConverter;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.TutkintoOhjelma;
import fi.vm.sade.tarjonta.model.TutkintoOhjelmaToteutus;
import fi.vm.sade.tarjonta.model.dto.TutkintoOhjelmaToteutusDTO;

/**
 * NOTE: this class was created in the sprint that was interrupted due to changes in Tarjonta specs. Revalidate.
 */
public class TutkintoOhjelmaToteutusDTOToModelConverter extends AbstractToDomainConverter<TutkintoOhjelmaToteutusDTO, TutkintoOhjelmaToteutus> {

    @Override
    public TutkintoOhjelmaToteutus convert(TutkintoOhjelmaToteutusDTO source) {
        
        TutkintoOhjelmaToteutus model = new TutkintoOhjelmaToteutus();
        model.setTila(source.getTila());
        model.setOid(source.getOid());
        model.setNimi(source.getNimi());
        model.setKoulutuksenAlkamisPvm(source.getKoulutuksenAlkamisPvm());
        model.setKoulutusLaji(source.getKoulutuslajiUri());
        convertTarjoajat(source, model);
        
        return model;
    }
    
    private void convertTarjoajat(TutkintoOhjelmaToteutusDTO source, TutkintoOhjelmaToteutus model) {
        if (source.getTarjoajat() != null) {
            for (String curTarjoaja : source.getTarjoajat()) {
                model.addTarjoaja(curTarjoaja);

            }
        }

    }
    
    
    private Koulutusmoduuli convertKoulutusmoduuli(TutkintoOhjelmaToteutusDTO source) {
        if (source.getToteutettavaKoulutusmoduuliOID() != null) {
            Koulutusmoduuli km = new TutkintoOhjelma();
            km.setOid(source.getToteutettavaKoulutusmoduuliOID());
            return km;
        }
        return null;
    }
    
    

}
