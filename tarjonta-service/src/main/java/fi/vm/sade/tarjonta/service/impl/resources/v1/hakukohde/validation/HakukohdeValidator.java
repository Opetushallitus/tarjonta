package fi.vm.sade.tarjonta.service.impl.resources.v1.hakukohde.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeLiiteRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeAjankohtaRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ErrorV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeLiiteV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.HakukohdeV1RDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.ValintakoeV1RDTO;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;

/*
* @author: Tuomas Katva 15/11/13
*/
public class HakukohdeValidator {


    private static List<HakukohdeValidationMessages> validateCommonProperties(HakukohdeV1RDTO hakukohdeRDTO) {

        List<HakukohdeValidationMessages> validationMessages = new ArrayList<HakukohdeValidationMessages>();

        if (hakukohdeRDTO.getHakukohdeKoulutusOids() == null || hakukohdeRDTO.getHakukohdeKoulutusOids().size() < 1) {
            validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_KOULUTUS_MISSING);
        }

        if (hakukohdeRDTO.getHakuOid() == null) {
            validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_HAKU_MISSING);
        }

        if(hakukohdeRDTO.getTila()==null) {
            validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_TILA_MISSING);
            return validationMessages;
        }

        TarjontaTila hakukohdeTila = TarjontaTila.valueOf(hakukohdeRDTO.getTila());

        if (hakukohdeRDTO.getOid() == null && hakukohdeTila.equals(TarjontaTila.JULKAISTU) ||  hakukohdeRDTO.getOid() == null && hakukohdeTila.equals(TarjontaTila.PERUTTU))  {
            validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_TILA_WRONG);
        }


        return validationMessages;
    }

    public static List<HakukohdeValidationMessages> validateAikuLukioHakukohde(HakukohdeV1RDTO hakukohdeRDTO) {

        List<HakukohdeValidationMessages> validationMessages = new ArrayList<HakukohdeValidationMessages>();

        validationMessages.addAll(validateCommonProperties(hakukohdeRDTO));

        if (hakukohdeRDTO.getHakukohteenNimiUri() == null || hakukohdeRDTO.getHakukohteenNimiUri().trim().length() < 1) {
            validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_NIMI_MISSING);
        }

        return validationMessages;
    }

    public static List<HakukohdeValidationMessages> validateHakukohde(HakukohdeV1RDTO hakukohdeRDTO) {
        List<HakukohdeValidationMessages> validationMessages = new ArrayList<HakukohdeValidationMessages>();


        validationMessages.addAll(validateCommonProperties(hakukohdeRDTO));

        if (hakukohdeRDTO.getHakukohteenNimet() == null || hakukohdeRDTO.getHakukohteenNimet().size() < 1) {
            validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_NIMI_MISSING);
        }

        if (hakukohdeRDTO.getValintakokeet() != null && hakukohdeRDTO.getValintakokeet().size() > 0) {
            validationMessages.addAll(validateValintakokees(hakukohdeRDTO.getValintakokeet()));
        }

        if (hakukohdeRDTO.getHakukohteenLiitteet() != null && hakukohdeRDTO.getHakukohteenLiitteet().size() >0) {
            for (HakukohdeLiiteV1RDTO liite : hakukohdeRDTO.getHakukohteenLiitteet()) {
                validationMessages.addAll(validateLiite(liite));
            }
        }

        return validationMessages;
    }
    
    /**
     * Tarkista että kaikilla koulutuksilla sama vuosi/kausi ja että niiden tila ei ole peruttu, poistettu
     * @param komotot
     */
    public static List<HakukohdeValidationMessages> checkKoulutukset(Collection<KoulutusmoduuliToteutus> komotot) {
        String kausi = null;
        Integer vuosi = null;
        
//        boolean tilaOk = false;
        if(komotot.size()==0) {
            return Lists.newArrayList(HakukohdeValidationMessages.HAKUKOHDE_KOULUTUS_MISSING);
        }
        
        for (KoulutusmoduuliToteutus komoto : komotot) {
            if (kausi == null) {
                kausi = komoto.getAlkamiskausiUri();
                vuosi = komoto.getAlkamisVuosi();
            } else {
               if(!(Objects.equal(kausi, komoto.getAlkamiskausiUri()) && Objects.equal(vuosi, komoto.getAlkamisVuosi()))) {
                   return Lists.newArrayList(HakukohdeValidationMessages.HAKUKOHDE_KOULUTUS_VUOSI_KAUSI_INVALID);
               } 
            }
            
//            if(komoto.getTila()!=TarjontaTila.PERUTTU && komoto.getTila()!=TarjontaTila.POISTETTU) {
//            	tilaOk = true;
//            }

        }
        
//        if (!tilaOk) {
//            return Lists.newArrayList(HakukohdeValidationMessages.HAKUKOHDE_KOULUTUS_TILA_INVALID);
//        }
        
        return Collections.EMPTY_LIST;
    }
    
    
    public static List<HakukohdeValidationMessages> validateLiite(HakukohdeLiiteV1RDTO liite) {

        Set<HakukohdeValidationMessages> liiteValidationMsgs = new HashSet<HakukohdeValidationMessages>();

        if (liite.getKieliUri() == null || liite.getKieliUri().length() < 1) {
            liiteValidationMsgs.add(HakukohdeValidationMessages.HAKUKOHDE_LIITE_KIELI_MISSING);
        }

        if (liite.getLiitteenNimi() == null || liite.getLiitteenNimi().length() < 1) {
            liiteValidationMsgs.add(HakukohdeValidationMessages.HAKUKOHDE_LIITE_NIMI_MISSING);
        }

        if (liite.getLiitteenToimitusOsoite() == null) {
            liiteValidationMsgs.add(HakukohdeValidationMessages.HAKUKOHDE_LIITE_OSOITE_MISSING);
        }

        if (liite.getToimitettavaMennessa() == null) {

            liiteValidationMsgs.add(HakukohdeValidationMessages.HAKUKOHDE_LIITE_TOIMITETTAVA_MENNESSA_MISSING);

        }

        return new ArrayList<HakukohdeValidationMessages>(liiteValidationMsgs);
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

    //public static List<HakukohdeValidationMessages> validateValintakoe()

    public static List<HakukohdeValidationMessages> validateValintakokees(List<ValintakoeV1RDTO> valintakoeV1RDTOs) {
        Set<HakukohdeValidationMessages> validationMessages = new HashSet<HakukohdeValidationMessages>();
        
        for (Iterator<ValintakoeV1RDTO> i = valintakoeV1RDTOs.iterator(); i.hasNext();) {
        	ValintakoeV1RDTO valintakoeV1RDTO = i.next();

        	// jos nimi on tyhjä eikä ajankohtia -> automaattisesti luotu ranka jonka voi hylätä
        	if (Strings.isNullOrEmpty(valintakoeV1RDTO.getValintakoeNimi())
        			&& (valintakoeV1RDTO.getValintakoeAjankohtas() == null || valintakoeV1RDTO.getValintakoeAjankohtas().isEmpty())) {
        		i.remove();
        		continue;
        	}
        	
            if (Strings.isNullOrEmpty(valintakoeV1RDTO.getValintakoeNimi()) && Strings.isNullOrEmpty(valintakoeV1RDTO.getValintakoetyyppi())) {
                validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_NIMI_MISSING);
            }            
            if (Strings.isNullOrEmpty(valintakoeV1RDTO.getKieliUri())){
                validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_KIELI_MISSING);
            }
            for (ValintakoeAjankohtaRDTO ajankohta: valintakoeV1RDTO.getValintakoeAjankohtas()){
                if (ajankohta.getLoppuu().before(ajankohta.getAlkaa())){
                    validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_START_DATE_BEFORE_END_DATE);
                }
                if (ajankohta.getOsoite() == null) {
                    validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_OSOITE_MISSING);
                }
            }

        }

        return new ArrayList<HakukohdeValidationMessages>(validationMessages);
    }
  

}
