package fi.vm.sade.tarjonta.service.impl.conversion.rest;

import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.Osoite;
import fi.vm.sade.tarjonta.model.Valintakoe;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.TekstiRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeRDTO;

import java.util.List;
import java.util.Map;

/*
* @author: Tuomas Katva 10/3/13
*/
public class CommonRestConverters {

    public static Osoite convertOsoiteRDTOToOsoite(OsoiteRDTO osoiteRDTO) {
        Osoite osoite = new Osoite();

        osoite.setOsoiterivi1(osoiteRDTO.getOsoiterivi1());
        osoite.setOsoiterivi2(osoiteRDTO.getOsoiterivi2());
        osoite.setPostinumero(osoiteRDTO.getPostinumero());
        osoite.setPostitoimipaikka(osoiteRDTO.getPostitoimipaikka());



        return osoite;
    }




    public static MonikielinenTeksti convertMapToMonikielinenTeksti(Map<String,String> map) {

        if (map != null) {
            MonikielinenTeksti monikielinenTeksti = new MonikielinenTeksti();

            for (String key : map.keySet()) {
                monikielinenTeksti.addTekstiKaannos(key,map.get(key));
            }

            return monikielinenTeksti;
        } else {
            return null;
        }

    }

}
