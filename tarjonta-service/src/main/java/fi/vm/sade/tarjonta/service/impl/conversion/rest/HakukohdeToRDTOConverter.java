package fi.vm.sade.tarjonta.service.impl.conversion.rest;

import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.impl.conversion.BaseRDTOConverter;
import fi.vm.sade.tarjonta.service.impl.conversion.CommonToDTOConverter;
import fi.vm.sade.tarjonta.service.resources.dto.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/*
* @author: Tuomas Katva 10/11/13
*/
public class HakukohdeToRDTOConverter  extends BaseRDTOConverter<Hakukohde,HakukohdeRDTO> {

    @Override
    public HakukohdeRDTO convert(Hakukohde hakukohde)  {
        HakukohdeRDTO hakukohdeRDTO = new HakukohdeRDTO();

        hakukohdeRDTO.setHakukohteenNimiUri(checkAndRemoveForEmbeddedVersionInUri(hakukohde.getHakukohdeNimi()));
        hakukohdeRDTO.setVersion(hakukohde.getVersion().intValue());
        hakukohdeRDTO.setOid(hakukohde.getOid());
        hakukohdeRDTO.setAloituspaikatLkm(hakukohde.getAloituspaikatLkm());

        for (KoulutusmoduuliToteutus komoto:hakukohde.getKoulutusmoduuliToteutuses()) {
            hakukohdeRDTO.getHakukohdeKoulutusOids().add(komoto.getOid());

            hakukohdeRDTO.getTarjoajaOids().add(komoto.getTarjoaja());


        }

        for (String hakukelpoisuusVaatimus:hakukohde.getHakukelpoisuusVaatimukset()) {
            hakukohdeRDTO.getHakukelpoisuusvaatimusUris().add(checkAndRemoveForEmbeddedVersionInUri(hakukelpoisuusVaatimus));
        }

        hakukohdeRDTO.setHakuOid(hakukohde.getHaku().getOid());
        if (hakukohde.getAlinHyvaksyttavaKeskiarvo() != null) {
            hakukohdeRDTO.setAlinHyvaksyttavaKeskiarvo(hakukohde.getAlinHyvaksyttavaKeskiarvo());
        }
        if (hakukohde.getAlinValintaPistemaara() != null) {
            hakukohdeRDTO.setAlinValintaPistemaara(hakukohde.getAlinValintaPistemaara());
        }

        if (hakukohde.getValintojenAloituspaikatLkm() != null) {
            hakukohdeRDTO.setValintojenAloituspaikatLkm(hakukohde.getValintojenAloituspaikatLkm());
        }
        if (hakukohde.getYlinValintaPistemaara() != null) {
            hakukohdeRDTO.setYlinValintapistemaara(hakukohde.getYlinValintaPistemaara());
        }
        hakukohdeRDTO.setSahkoinenToimitusOsoite(hakukohde.getSahkoinenToimitusOsoite());
        hakukohdeRDTO.setSoraKuvausKoodiUri(checkAndRemoveForEmbeddedVersionInUri(hakukohde.getSoraKuvausKoodiUri()));
        hakukohdeRDTO.setTila(hakukohde.getTila().name());
        hakukohdeRDTO.setValintaperustekuvausKoodiUri(checkAndRemoveForEmbeddedVersionInUri(hakukohde.getValintaperustekuvausKoodiUri()));
        hakukohdeRDTO.setLiitteidenToimitusPvm(hakukohde.getLiitteidenToimitusPvm());
        hakukohdeRDTO.setLisatiedot(convertMonikielinenTekstiToTekstiDTOs(hakukohde.getLisatiedot()));
        hakukohdeRDTO.setValintaperusteKuvaukset(CommonToDTOConverter.convertMonikielinenTekstiToTekstiRDOT(hakukohde.getValintaperusteKuvaus()));
        hakukohdeRDTO.setKaytetaanJarjestelmanValintaPalvelua(hakukohde.isKaytetaanJarjestelmanValintapalvelua());
        hakukohdeRDTO.setKaytetaanHaunPaattymisenAikaa(hakukohde.isKaytetaanHaunPaattymisenAikaa());
        hakukohdeRDTO.setLiitteidenToimitusOsoite(CommonToDTOConverter.convertOsoiteToOsoiteDTO(hakukohde.getLiitteidenToimitusOsoite()));

        if (hakukohde.getLiites() != null) {
            hakukohdeRDTO.setHakukohteenLiitteet(convertLiitteet(hakukohde.getLiites()));

        }

        if (hakukohde.getValintakoes() != null) {
            hakukohdeRDTO.setValintakokeet(convertValintakokeet(hakukohde.getValintakoes()));
        }


        return hakukohdeRDTO;
    }

    private List<TekstiRDTO> convertMonikielinenTekstiToTekstiDTOs(MonikielinenTeksti monikielinenTeksti) {

        if (monikielinenTeksti != null) {
            List<TekstiRDTO> tekstiRDTOs = new ArrayList<TekstiRDTO>();

            for(TekstiKaannos tekstiKaannos:monikielinenTeksti.getTekstis()) {
                TekstiRDTO tekstiRDTO = new TekstiRDTO();
                tekstiRDTO.setUri(checkAndRemoveForEmbeddedVersionInUri(tekstiKaannos.getKieliKoodi()));
                tekstiRDTO.setTeksti(tekstiKaannos.getArvo());
                try {
                    KoodiType koodiType = getTarjontaKoodistoHelper().getKoodiByUri(tekstiKaannos.getKieliKoodi());
                    //TODO: should it return nimi instead ? But with what language ?
                    tekstiRDTO.setNimi(koodiType.getKoodiArvo());

                } catch (Exception exp) {

                }
                 tekstiRDTOs.add(tekstiRDTO);

            }

            return tekstiRDTOs;
        }  else {
            return null;
        }

    }


    private String checkAndRemoveForEmbeddedVersionInUri(String uri) {
        if (uri != null) {
        if (uri.contains("#")) {
            StringTokenizer st = new StringTokenizer(uri,"#");
            return st.nextToken();
        } else {
            return uri;
        }
        } else {
            return null;
        }
    }

    private List<ValintakoeRDTO> convertValintakokeet(Set<Valintakoe> valintakoes) {
        List<ValintakoeRDTO> result = new ArrayList<ValintakoeRDTO>();

        for (Valintakoe valintakoe : valintakoes) {
            result.add(getConversionService().convert(valintakoe, ValintakoeRDTO.class));
        }

        return result.isEmpty() ? null : result;
    }



    private List<HakukohdeLiiteRDTO> convertLiitteet(Set<HakukohdeLiite> s) {
        List<HakukohdeLiiteRDTO> result = new ArrayList<HakukohdeLiiteRDTO>();

        for (HakukohdeLiite hakukohdeLiite : s) {
            HakukohdeLiiteRDTO hakukohdeLiiteRDTO = new HakukohdeLiiteRDTO();

            hakukohdeLiiteRDTO.setErapaiva(hakukohdeLiite.getErapaiva());
            hakukohdeLiiteRDTO.setLiitteenTyyppiUri(checkAndRemoveForEmbeddedVersionInUri(hakukohdeLiite.getLiitetyyppi()));
            hakukohdeLiiteRDTO.setLiitteenTyyppiKoodistoNimi(hakukohdeLiite.getLiitteenTyyppiKoodistoNimi());
            hakukohdeLiiteRDTO.setSahkoinenToimitusOsoite(hakukohdeLiite.getSahkoinenToimitusosoite());
            if (hakukohdeLiite.getToimitusosoite() != null) {
                hakukohdeLiiteRDTO.setToimitusOsoite(CommonToDTOConverter.convertOsoiteToOsoiteDTO(hakukohdeLiite.getToimitusosoite()));
            }
            hakukohdeLiiteRDTO.setKuvaus(convertMonikielinenTekstiToTekstiDTOs(hakukohdeLiite.getKuvaus()));


            result.add(hakukohdeLiiteRDTO);
        }

        return result.isEmpty() ? null : result;
    }
}
