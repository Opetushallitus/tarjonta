package fi.vm.sade.tarjonta.service.impl.resources.v1.hakukohde.validation;

import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeLiiteRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeAjankohtaRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ValintakoeV1RDTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
* @author: Tuomas Katva 15/11/13
*/
public class HakukohdeValidator {

    public static List<HakukohdeValidationMessages> validateHakukohde(HakukohdeV1RDTO hakukohdeRDTO) {
        List<HakukohdeValidationMessages> validationMessages = new ArrayList<HakukohdeValidationMessages>();

        if (hakukohdeRDTO.getHakukohdeKoulutusOids() == null || hakukohdeRDTO.getHakukohdeKoulutusOids().size() < 1) {
            validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_KOULUTUS_MISSING);
        }

        if (hakukohdeRDTO.getHakuOid() == null) {
            validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_HAKU_MISSING);
        }

        if (hakukohdeRDTO.getTarjoajaOids() == null || hakukohdeRDTO.getTarjoajaOids().size() < 1) {
            validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_TARJOAJA_MISSING);
        }

        if (hakukohdeRDTO.getHakukohteenNimet() == null || hakukohdeRDTO.getHakukohteenNimet().size() < 1) {
            validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_NIMI_MISSING);
        }

        if (hakukohdeRDTO.getTila() != null) {
            validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_TILA_MISSING);
        }

        if (hakukohdeRDTO.getValintakokeet() != null && hakukohdeRDTO.getValintakokeet().size() > 0) {
            validationMessages.addAll(validateValintakokees(hakukohdeRDTO.getValintakokeet()));
        }

        return validationMessages;
    }

    public static List<HakukohdeValidationMessages> validateLiites(List<HakukohdeLiiteRDTO> liites) {

        Set<HakukohdeValidationMessages> liiteValidationMsgs = new HashSet<HakukohdeValidationMessages>();

        if (liites != null && liites.size() > 0 ) {

            for (HakukohdeLiiteRDTO liite : liites) {

                 if (liite.getLiiteKieli() == null || liite.getLiiteKieli().length() < 1) {
                     liiteValidationMsgs.add(HakukohdeValidationMessages.HAKUKOHDE_LIITE_KIELI_MISSING);
                }



            }

        }

        return new ArrayList<HakukohdeValidationMessages>(liiteValidationMsgs);

    }

    public static List<HakukohdeValidationMessages> validateValintakokees(List<ValintakoeV1RDTO> valintakoeV1RDTOs) {
        Set<HakukohdeValidationMessages> validationMessages = new HashSet<HakukohdeValidationMessages>();

        for (ValintakoeV1RDTO valintakoeV1RDTO : valintakoeV1RDTOs) {

            if (valintakoeV1RDTO.getKieliUri() == null){
                validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_KIELI_MISSING);
            }
            if (valintakoeV1RDTO.getValintakoeAjankohtas() == null || valintakoeV1RDTO.getValintakoeAjankohtas().size() < 1) {
                validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_AIKAS_MISSING);
            }  else {
                if (valintakoeV1RDTO.getValintakoeAjankohtas() == null || valintakoeV1RDTO.getValintakoeAjankohtas().size() < 1) {
                   validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_MISSING);
                } else {
                for (ValintakoeAjankohtaRDTO ajankohta: valintakoeV1RDTO.getValintakoeAjankohtas()){
                    if (ajankohta.getLoppuu().before(ajankohta.getAlkaa())){
                        validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_START_DATE_BEFORE_END_DATE);
                    }
                    if (ajankohta.getOsoite() == null) {
                        validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_OSOITE_MISSING);
                    }
                }
                }
            }

        }

        return new ArrayList<HakukohdeValidationMessages>(validationMessages);
    }

}
