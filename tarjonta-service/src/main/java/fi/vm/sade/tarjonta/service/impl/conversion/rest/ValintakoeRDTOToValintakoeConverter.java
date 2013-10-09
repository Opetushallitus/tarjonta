package fi.vm.sade.tarjonta.service.impl.conversion.rest;

import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.Pisteraja;
import fi.vm.sade.tarjonta.model.Valintakoe;
import fi.vm.sade.tarjonta.model.ValintakoeAjankohta;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeAjankohtaRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoePisterajaRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeRDTO;
import org.springframework.core.convert.converter.Converter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
* @author: Tuomas Katva 10/3/13
*/
public class ValintakoeRDTOToValintakoeConverter implements Converter<ValintakoeRDTO,Valintakoe> {


    @Override
    public Valintakoe convert(ValintakoeRDTO valintakoeRDTO) {
        Valintakoe valintakoe = new Valintakoe();

        valintakoe.setKuvaus(CommonRestConverters.convertMapToMonikielinenTeksti(valintakoeRDTO.getKuvaus()));
        valintakoe.setLisanaytot(CommonRestConverters.convertMapToMonikielinenTeksti(valintakoeRDTO.getLisanaytot()));
        valintakoe.setTyyppiUri(valintakoeRDTO.getTyyppiUri());
        valintakoe.setPisterajat(convertPisterajaRDTOToPisteraja(valintakoeRDTO.getValintakoePisterajas()));
        valintakoe.setAjankohtas(convertAjankohtaRDTOToAjankohta(valintakoeRDTO.getValintakoeAjankohtas()));
        valintakoe.setLastUpdateDate(valintakoeRDTO.getModified());
        valintakoe.setLastUpdatedByOid(valintakoeRDTO.getCreatedBy());


        return valintakoe;
    }


    private Set<ValintakoeAjankohta> convertAjankohtaRDTOToAjankohta(List<ValintakoeAjankohtaRDTO> valintakoeAjankohtaRDTOs) {
        if (valintakoeAjankohtaRDTOs != null) {
            Set<ValintakoeAjankohta> valintakoeAjankohtas = new HashSet<ValintakoeAjankohta>();

            for (ValintakoeAjankohtaRDTO ajankohtaRDTO: valintakoeAjankohtaRDTOs) {
               ValintakoeAjankohta ajankohta = new ValintakoeAjankohta();
               ajankohta.setAjankohdanOsoite(CommonRestConverters.convertOsoiteRDTOToOsoite(ajankohtaRDTO.getOsoite()));
               ajankohta.setAlkamisaika(ajankohtaRDTO.getAlkaa());
               ajankohta.setPaattymisaika(ajankohtaRDTO.getLoppuu());
               ajankohta.setLisatietoja(ajankohtaRDTO.getLisatiedot());
               valintakoeAjankohtas.add(ajankohta);


            }

            return valintakoeAjankohtas;
        } else {

            return null;
        }

    }



    private Set<Pisteraja> convertPisterajaRDTOToPisteraja(List<ValintakoePisterajaRDTO> pisterajaRDTOs) {
        if (pisterajaRDTOs != null) {
            Set<Pisteraja> pisterajat = new HashSet<Pisteraja>();

            for (ValintakoePisterajaRDTO valintakoePisterajaRDTO:pisterajaRDTOs) {
                Pisteraja pisteraja = new Pisteraja();
                pisteraja.setAlinHyvaksyttyPistemaara(new BigDecimal(valintakoePisterajaRDTO.getAlinHyvaksyttyPistemaara()));
                pisteraja.setAlinPistemaara(new BigDecimal(valintakoePisterajaRDTO.getAlinPistemaara()));
                pisteraja.setValinnanPisterajaTyyppi(valintakoePisterajaRDTO.getTyyppi());
                pisteraja.setYlinPistemaara(new BigDecimal(valintakoePisterajaRDTO.getYlinPistemaara()));
                pisterajat.add(pisteraja);


            }

            return pisterajat;
        } else {
            return null;
        }

    }


}
