package fi.vm.sade.tarjonta.service.impl.conversion.rest;

import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.Osoite;
import fi.vm.sade.tarjonta.model.TekstiKaannos;
import fi.vm.sade.tarjonta.model.Yhteystiedot;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.YhteystiedotV1RDTO;

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

    public static Yhteystiedot convertYhteystiedotV1RDTOToYhteystiedot(YhteystiedotV1RDTO yhteystiedotV1RDTO) {
        if (yhteystiedotV1RDTO != null) {
            Yhteystiedot yh = new Yhteystiedot();

            yh.setLang(yhteystiedotV1RDTO.getLang());
            yh.setOsoiterivi1(yhteystiedotV1RDTO.getOsoiterivi1());
            yh.setOsoiterivi2(yhteystiedotV1RDTO.getOsoiterivi2());
            yh.setPostinumero(yhteystiedotV1RDTO.getPostinumero());
            yh.setPostitoimipaikka(yhteystiedotV1RDTO.getPostitoimipaikka());

            return yh;
        } else {
            return null;
        }
    }
}
