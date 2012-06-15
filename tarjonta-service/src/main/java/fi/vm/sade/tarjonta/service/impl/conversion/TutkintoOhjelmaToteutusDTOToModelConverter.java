package fi.vm.sade.tarjonta.service.impl.conversion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fi.vm.sade.generic.service.conversion.AbstractToDomainConverter;
import fi.vm.sade.tarjonta.model.KoodistoKoodi;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutusTarjoaja;
import fi.vm.sade.tarjonta.model.TutkintoOhjelma;
import fi.vm.sade.tarjonta.model.TutkintoOhjelmaToteutus;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.model.dto.TutkintoOhjelmaToteutusDTO;

public class TutkintoOhjelmaToteutusDTOToModelConverter extends AbstractToDomainConverter<TutkintoOhjelmaToteutusDTO, TutkintoOhjelmaToteutus> {

    @Override
    public TutkintoOhjelmaToteutus convert(TutkintoOhjelmaToteutusDTO source) {
        
        TutkintoOhjelmaToteutus model = new TutkintoOhjelmaToteutus();
        model.setTila(source.getTila());
        model.setOid(source.getOid());
        model.setNimi(source.getNimi());
        model.setPerustiedot(CommonConverter.convert(source.getPerustiedot()));
        model.setKoulutuksenAlkamisPvm(source.getKoulutuksenAlkamisPvm());
        model.setKoulutusLajiUri(source.getKoulutuslajiUri());
        model.setSuunniteltuKestoUri(source.getSuunniteltuKestoUri());
        convertTarjoajat(source, model);
        model.setTeemaUris(convertTeemaUris(source));
        model.setKoulutusmoduuli(convertKoulutusmoduuli(source));
        return model;
    }
    
    private void convertTarjoajat(TutkintoOhjelmaToteutusDTO source, TutkintoOhjelmaToteutus model) {
        if (source.getTarjoajat() != null) {
            for (String curTarjoaja : source.getTarjoajat()) {
                model.addTarjoaja(curTarjoaja);

            }
        }

    }
    
    private Set<KoodistoKoodi> convertTeemaUris(TutkintoOhjelmaToteutusDTO source) {
        if (source.getTeemaUris() == null) {
            return null;
        }
        Set<KoodistoKoodi> teemas = new HashSet<KoodistoKoodi>();
        for(String curTeema : source.getTeemaUris()) {
            teemas.add(new KoodistoKoodi(curTeema));
        }
        return teemas;
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
