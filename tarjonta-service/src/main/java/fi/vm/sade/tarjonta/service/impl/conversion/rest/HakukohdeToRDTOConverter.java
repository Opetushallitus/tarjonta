package fi.vm.sade.tarjonta.service.impl.conversion.rest;

import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.impl.conversion.BaseRDTOConverter;
import fi.vm.sade.tarjonta.service.impl.conversion.CommonToDTOConverter;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeLiiteRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.TekstiRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeAjankohtaRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ValintakoeV1RDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/*
* @author: Tuomas Katva 10/11/13
*/
public class HakukohdeToRDTOConverter  extends BaseRDTOConverter<Hakukohde,HakukohdeV1RDTO> {


    private static final Logger LOG = LoggerFactory.getLogger(HakukohdeToRDTOConverter.class);
    @Override
    public HakukohdeV1RDTO convert(Hakukohde hakukohde)  {
        HakukohdeV1RDTO hakukohdeRDTO = new HakukohdeV1RDTO();



        hakukohdeRDTO.setHakukohteenNimiUri(checkAndRemoveForEmbeddedVersionInUri(hakukohde.getHakukohdeNimi()));
        hakukohdeRDTO.setVersion(hakukohde.getVersion());
        hakukohdeRDTO.setOid(hakukohde.getOid());
        hakukohdeRDTO.setAloituspaikatLkm(hakukohde.getAloituspaikatLkm());

        for (KoulutusmoduuliToteutus komoto:hakukohde.getKoulutusmoduuliToteutuses()) {
            hakukohdeRDTO.getHakukohdeKoulutusOids().add(komoto.getOid());

            hakukohdeRDTO.getTarjoajaOids().add(komoto.getTarjoaja());


        }

        if (hakukohde.getHakukohdeMonikielinenNimi() != null) {
            hakukohdeRDTO.setHakukohteenNimet(convertMonikielinenTekstiToHashMap(hakukohde.getHakukohdeMonikielinenNimi()));
            //hakukohdeRDTO.setHakukohteenNimet(convertMonikielinenTekstiToHashMap(hakukohde.getHakukohdeMonikielinenNimi()));
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

        hakukohdeRDTO.setValintojenAloituspaikatLkm(hakukohde.getValintojenAloituspaikatLkm());

        if (hakukohde.getYlinValintaPistemaara() != null) {
            hakukohdeRDTO.setYlinValintapistemaara(hakukohde.getYlinValintaPistemaara());
        }

        if (hakukohde.getHakuaikaAlkuPvm() != null) {
            hakukohdeRDTO.setHakuaikaAlkuPvm(hakukohde.getHakuaikaAlkuPvm());
        }

        if (hakukohde.getHakuaikaLoppuPvm() != null) {
            hakukohdeRDTO.setHakuaikaLoppuPvm(hakukohde.getHakuaikaLoppuPvm());
        }

        hakukohdeRDTO.setSahkoinenToimitusOsoite(hakukohde.getSahkoinenToimitusOsoite());
        hakukohdeRDTO.setSoraKuvausKoodiUri(checkAndRemoveForEmbeddedVersionInUri(hakukohde.getSoraKuvausKoodiUri()));
        hakukohdeRDTO.setTila(hakukohde.getTila().name());
        hakukohdeRDTO.setValintaperustekuvausKoodiUri(checkAndRemoveForEmbeddedVersionInUri(hakukohde.getValintaperustekuvausKoodiUri()));
        hakukohdeRDTO.setLiitteidenToimitusPvm(hakukohde.getLiitteidenToimitusPvm());
        hakukohdeRDTO.setLisatiedot(convertMonikielinenTekstiToHashMap(hakukohde.getLisatiedot()));
        if (hakukohde.getValintaperusteKuvaus() != null) {
        hakukohdeRDTO.setValintaperusteKuvaukset(convertMonikielinenTekstiToHashMap(hakukohde.getValintaperusteKuvaus()));
        }
        if (hakukohde.getSoraKuvaus() != null) {
            hakukohdeRDTO.setSoraKuvaukset(convertMonikielinenTekstiToHashMap(hakukohde.getSoraKuvaus()));
        }
        hakukohdeRDTO.setKaytetaanJarjestelmanValintaPalvelua(hakukohde.isKaytetaanJarjestelmanValintapalvelua());
        hakukohdeRDTO.setKaytetaanHaunPaattymisenAikaa(hakukohde.isKaytetaanHaunPaattymisenAikaa());
        hakukohdeRDTO.setLiitteidenToimitusOsoite(CommonToDTOConverter.convertOsoiteToOsoiteDTO(hakukohde.getLiitteidenToimitusOsoite()));
        LOG.debug("HAKUKOHDE LISATIEDOT : {} " , hakukohdeRDTO.getLisatiedot() != null ? hakukohdeRDTO.getLisatiedot().size() : "IS EMPTY" );


        if (hakukohde.getValintakoes() != null) {
            hakukohdeRDTO.setValintakokeet(convertValintakokeet(hakukohde.getValintakoes()));
        }
        
        hakukohdeRDTO.setOrganisaatioRyhmaOids(hakukohde.getOrganisaatioRyhmaOids());

        return hakukohdeRDTO;
    }

    private HashMap<String,String> convertMonikielinenTekstiToHashMap(MonikielinenTeksti teksti) {
        HashMap<String,String> returnValue = new HashMap<String, String>();

        for (TekstiKaannos tekstiKaannos : teksti.getKaannoksetAsList()) {
            LOG.debug("TEKSTIKAANNOS KIELI : {} ARVO : {} ", tekstiKaannos.getKieliKoodi(),tekstiKaannos.getArvo());
            returnValue.put(tekstiKaannos.getKieliKoodi(),tekstiKaannos.getArvo());
        }

        return returnValue;
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
                    tekstiRDTO.setArvo(koodiType.getKoodiArvo());
                    tekstiRDTO.setVersio(koodiType.getVersio());
                    if (koodiType.getMetadata() != null) {
                        for (KoodiMetadataType meta:koodiType.getMetadata()) {
                            //By default set default name finnish
                            if (meta.getKieli().equals(KieliType.FI)) {
                                tekstiRDTO.setNimi(meta.getNimi());
                            }
                            tekstiRDTO.addKieliAndNimi(meta.getKieli().value(),meta.getNimi());
                        }
                    }


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

    private List<ValintakoeV1RDTO> convertValintakokeet(Set<Valintakoe> valintakoes) {
        List<ValintakoeV1RDTO> result = new ArrayList<ValintakoeV1RDTO>();

        for (Valintakoe valintakoe : valintakoes) {
            result.add(convertValintakoeToValintakoeV1RDTO(valintakoe));
        }

        return result.isEmpty() ? null : result;
    }

    private ValintakoeV1RDTO convertValintakoeToValintakoeV1RDTO(Valintakoe valintakoe) {
        ValintakoeV1RDTO valintakoeV1RDTO = new ValintakoeV1RDTO();

        valintakoeV1RDTO.setKieliUri(valintakoe.getKieli());
        valintakoeV1RDTO.setValintakoeNimi(valintakoe.getValintakoeNimi());
        List<TekstiRDTO> lisatiedot = convertMonikielinenTekstiToTekstiDTOs(valintakoe.getKuvaus());
        if (lisatiedot != null && lisatiedot.size() > 0) {
            valintakoeV1RDTO.setValintakokeenKuvaus(lisatiedot.get(0));
        }

        if (valintakoe.getAjankohtas() != null) {
            for (ValintakoeAjankohta valintakoeAjankohta : valintakoe.getAjankohtas()) {
                valintakoeV1RDTO.getValintakoeAjankohtas().add(convertValintakoeAjankohtaToValintakoeAjankohtaRDTO(valintakoeAjankohta));
            }
        }

        return valintakoeV1RDTO;
    }


    private ValintakoeAjankohtaRDTO convertValintakoeAjankohtaToValintakoeAjankohtaRDTO(ValintakoeAjankohta valintakoeAjankohta) {

        ValintakoeAjankohtaRDTO valintakoeAjankohtaRDTO = new ValintakoeAjankohtaRDTO();

        valintakoeAjankohtaRDTO.setOid(valintakoeAjankohta.getId().toString());
        valintakoeAjankohtaRDTO.setAlkaa(valintakoeAjankohta.getAlkamisaika());
        valintakoeAjankohtaRDTO.setLoppuu(valintakoeAjankohta.getPaattymisaika());
        valintakoeAjankohtaRDTO.setLisatiedot(valintakoeAjankohta.getLisatietoja());
        valintakoeAjankohtaRDTO.setOsoite(CommonToDTOConverter.convertOsoiteToOsoiteDTO(valintakoeAjankohta.getAjankohdanOsoite()));

        return valintakoeAjankohtaRDTO;

    };


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
