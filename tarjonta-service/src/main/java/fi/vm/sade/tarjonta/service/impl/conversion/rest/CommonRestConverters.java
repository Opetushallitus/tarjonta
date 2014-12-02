package fi.vm.sade.tarjonta.service.impl.conversion.rest;

import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.Osoite;
import fi.vm.sade.tarjonta.model.TekstiKaannos;
import fi.vm.sade.tarjonta.model.Yhteystiedot;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.YhteystiedotRDTO;

import java.util.HashMap;
import java.util.Map;

public class CommonRestConverters {

    public static Osoite toOsoite(OsoiteRDTO osoiteRDTO) {
        Osoite osoite = new Osoite();

        osoite.setOsoiterivi1(osoiteRDTO.getOsoiterivi1());
        osoite.setOsoiterivi2(osoiteRDTO.getOsoiterivi2());
        osoite.setPostinumero(osoiteRDTO.getPostinumero());
        osoite.setPostitoimipaikka(osoiteRDTO.getPostitoimipaikka());

        return osoite;
    }

    public static MonikielinenTeksti toMonikielinenTeksti(Map<String, String> map) {

        if (map != null) {
            MonikielinenTeksti monikielinenTeksti = new MonikielinenTeksti();

            for (String key : map.keySet()) {
                monikielinenTeksti.addTekstiKaannos(key, map.get(key));
            }

            return monikielinenTeksti;
        } else {
            return null;
        }

    }

    public static Map<String, String> toStringMap(MonikielinenTeksti monikielinenTeksti) {
        Map<String, String> map = new HashMap<String, String>();

        for (TekstiKaannos tekstiKaannos : monikielinenTeksti.getKaannoksetAsList()) {
            map.put(tekstiKaannos.getKieliKoodi(), tekstiKaannos.getArvo());
        }

        return map;
    }

    public static Yhteystiedot convertYhteystiedotRDTOToYhteystiedot(YhteystiedotRDTO yhteystiedotRDTO) {
        if (yhteystiedotRDTO != null) {
            Yhteystiedot yh = new Yhteystiedot();

            yh.setOsoiterivi1(yhteystiedotRDTO.getOsoiterivi1());
            yh.setOsoiterivi2(yhteystiedotRDTO.getOsoiterivi2());
            yh.setPostinumero(yhteystiedotRDTO.getPostinumero());
            yh.setPostitoimipaikka(yhteystiedotRDTO.getPostitoimipaikka());

            return yh;
        } else {
            return null;
        }
    }
}
