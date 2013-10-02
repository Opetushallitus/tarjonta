package fi.vm.sade.tarjonta.service.impl.conversion.rest;

import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.Osoite;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeDTO;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;

/*
* @author: Tuomas Katva 10/1/13
*/
public class HakukohdeRDTOToHakukohdeConverter implements Converter<HakukohdeDTO,Hakukohde> {

    @Override
    public Hakukohde convert(HakukohdeDTO hakukohdeDTO) {
        Hakukohde hakukohde = new Hakukohde();

        if (hakukohdeDTO.getOid() != null) {
            hakukohde.setOid(hakukohdeDTO.getOid());
        }

        hakukohde.setAloituspaikatLkm(hakukohdeDTO.getAloituspaikatLkm());
        if (hakukohdeDTO.getHakukohteenNimi() != null) {
            hakukohde.setHakukohdeNimi(hakukohdeDTO.getHakukohteenNimi());
        }  else if (hakukohdeDTO.getHakukohdeNimiUri() != null) {
            hakukohde.setHakukohdeNimi(hakukohdeDTO.getHakukohdeNimiUri());
        }

        hakukohde.setHakukohdeKoodistoNimi(hakukohdeDTO.getHakukohdeKoodistoNimi());
        hakukohde.setOid(hakukohdeDTO.getOid());
        hakukohde.setHakuaikaAlkuPvm(hakukohdeDTO.getHakuaikaAlkuPvm());
        hakukohde.setHakuaikaLoppuPvm(hakukohdeDTO.getHakuaikaLoppuPvm());
        hakukohde.setTila(TarjontaTila.valueOf(hakukohdeDTO.getTila()));
        hakukohde.setLisatiedot(convertMapToMonikielinenTeksti(hakukohdeDTO.getLisatiedot()));
        hakukohde.setValintojenAloituspaikatLkm(hakukohdeDTO.getValintojenAloituspaikatLkm());
        hakukohde.setLiitteidenToimitusPvm(hakukohdeDTO.getLiitteidenToimitusPvm());
        hakukohde.setSahkoinenToimitusOsoite(hakukohdeDTO.getSahkoinenToimitusOsoite());
        hakukohde.setKaytetaanHaunPaattymisenAikaa(hakukohdeDTO.isKaytetaanHaunPaattymisenAikaa());
        hakukohde.setKaytetaanJarjestelmanValintapalvelua(hakukohdeDTO.isKaytetaanJarjestelmanValintaPalvelua());
        hakukohde.setSoraKuvausKoodiUri(hakukohdeDTO.getSoraKuvausKoodiUri());
        hakukohde.setSoraKuvaus(hakukohdeDTO.getSoraKuvausKoodiUri()!=null ? null :
               convertMapToMonikielinenTeksti(hakukohdeDTO.getSorakuvaus()));

        hakukohde.setValintaperustekuvausKoodiUri(hakukohdeDTO.getValintaperustekuvausKoodiUri());
        hakukohde.setValintaperusteKuvaus(hakukohdeDTO.getValintaperustekuvausKoodiUri() != null  ? null : convertMapToMonikielinenTeksti(hakukohdeDTO.getValintaperustekuvaus()));
        hakukohde.setAlinHyvaksyttavaKeskiarvo(hakukohdeDTO.getAlinHyvaksyttavaKeskiarvo());
        hakukohde.setAlinValintaPistemaara(hakukohdeDTO.getAlinValintaPistemaara());
        hakukohde.setYlinValintaPistemaara(hakukohdeDTO.getYlinValintapistemaara());
        hakukohde.setAloituspaikatLkm(hakukohdeDTO.getAloituspaikatLkm());

        hakukohde.setLiitteidenToimitusOsoite(convertRDTOOsoiteToOsoite(hakukohdeDTO.getLiitteidenToimitusosoite()));

        hakukohde.setHakukelpoisuusVaatimukset(new HashSet<String>(hakukohdeDTO.getHakukelpoisuusvaatimusUris()));

        hakukohde.setLastUpdateDate(new Date());



        return hakukohde;
    }



    private Osoite convertRDTOOsoiteToOsoite(OsoiteRDTO osoiteRDTO) {

        if (osoiteRDTO != null) {
            Osoite osoite = new Osoite();

            osoite.setOsoiterivi1(osoiteRDTO.getOsoiterivi1());
            osoite.setOsoiterivi2(osoiteRDTO.getOsoiterivi2());
            osoite.setPostinumero(osoiteRDTO.getPostinumero());
            osoite.setPostitoimipaikka(osoiteRDTO.getPostitoimipaikka());

            return osoite;
        } else return null;

    }


    private MonikielinenTeksti convertMapToMonikielinenTeksti(Map<String,String> map) {
        MonikielinenTeksti monikielinenTeksti = new MonikielinenTeksti();

        for (String key : map.keySet()) {
            monikielinenTeksti.addTekstiKaannos(key,map.get(key));
        }

        return monikielinenTeksti;
    }
}
