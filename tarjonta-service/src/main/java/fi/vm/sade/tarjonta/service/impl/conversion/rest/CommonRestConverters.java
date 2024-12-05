package fi.vm.sade.tarjonta.service.impl.conversion.rest;

import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.Osoite;
import fi.vm.sade.tarjonta.model.TekstiKaannos;
import fi.vm.sade.tarjonta.model.Yhteystiedot;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.YhteystiedotV1RDTO;
import fi.vm.sade.tarjonta.shared.types.Osoitemuoto;
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

  public static Yhteystiedot convertYhteystiedotV1RDTOToYhteystiedot(
      YhteystiedotV1RDTO yhteystiedotV1RDTO) {
    if (yhteystiedotV1RDTO != null) {
      Yhteystiedot yh = new Yhteystiedot();

      yh.setLang(yhteystiedotV1RDTO.getLang());

      yh.setOsoitemuoto(yhteystiedotV1RDTO.getOsoitemuoto());

      if (Osoitemuoto.KANSAINVALINEN.equals(yh.getOsoitemuoto())) {
        yh.setKansainvalinenOsoite(yhteystiedotV1RDTO.getKansainvalinenOsoite());
      } else {
        yh.setOsoiterivi1(yhteystiedotV1RDTO.getOsoiterivi1());
        yh.setOsoiterivi2(yhteystiedotV1RDTO.getOsoiterivi2());
        yh.setPostinumero(yhteystiedotV1RDTO.getPostinumero());
        yh.setPostitoimipaikka(yhteystiedotV1RDTO.getPostitoimipaikka());
      }
      yh.setHakutoimistonNimi(yhteystiedotV1RDTO.getHakutoimistonNimi());
      yh.setPuhelinnumero(yhteystiedotV1RDTO.getPuhelinnumero());
      yh.setSahkopostiosoite(yhteystiedotV1RDTO.getSahkopostiosoite());
      yh.setWwwOsoite(yhteystiedotV1RDTO.getWwwOsoite());

      YhteystiedotV1RDTO kayntisoiteV1RDTO = yhteystiedotV1RDTO.getKayntiosoite();
      if (kayntisoiteV1RDTO != null) {
        if (Osoitemuoto.KANSAINVALINEN.equals(yh.getOsoitemuoto())) {
          yh.setKansainvalinenKayntiOsoite(kayntisoiteV1RDTO.getKansainvalinenOsoite());
        } else {
          yh.setKayntiosoiteOsoiterivi1(kayntisoiteV1RDTO.getOsoiterivi1());
          yh.setKayntiosoitePostinumero(kayntisoiteV1RDTO.getPostinumero());
          yh.setKayntiosoitePostitoimipaikka(kayntisoiteV1RDTO.getPostitoimipaikka());
        }
      }

      return yh;
    } else {
      return null;
    }
  }
}
