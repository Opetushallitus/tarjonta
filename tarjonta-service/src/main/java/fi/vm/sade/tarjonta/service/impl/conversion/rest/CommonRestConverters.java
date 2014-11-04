package fi.vm.sade.tarjonta.service.impl.conversion.rest;

import com.google.common.base.Preconditions;

import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.Osoite;
import fi.vm.sade.tarjonta.model.Yhteystiedot;
import fi.vm.sade.tarjonta.service.business.impl.EntityUtils;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.FieldNames;
import fi.vm.sade.tarjonta.service.impl.resources.v1.koulutus.validation.KoulutusValidationMessages;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.YhteystiedotRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusKorkeakouluV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusV1RDTO;
import fi.vm.sade.tarjonta.service.search.IndexDataUtils;

import java.util.Date;
import java.util.Map;
import java.util.Set;

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

    public static MonikielinenTeksti convertMapToMonikielinenTeksti(Map<String, String> map) {

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

    public static Yhteystiedot convertYhteystiedotRDTOToYhteystiedot(YhteystiedotRDTO yhteystiedotRDTO) {
        if (yhteystiedotRDTO != null) {
            Yhteystiedot yh = new Yhteystiedot();
            
            yh.setOsoiterivi1(yhteystiedotRDTO.getOsoiterivi1());
            yh.setOsoiterivi2(yhteystiedotRDTO.getOsoiterivi2());
            yh.setPostinumero(yhteystiedotRDTO.getPostinumero());
            yh.setPostitoimipaikka(yhteystiedotRDTO.getPostitoimipaikka());
            
            return yh;
        }
        else {
            return null;
        }
    }
}
