package fi.vm.sade.tarjonta.service.impl.resources.v1.hakukohde.validation;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeLiiteRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeAjankohtaRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.*;
import fi.vm.sade.tarjonta.service.search.KoodistoKoodi;
import fi.vm.sade.tarjonta.service.search.KoulutuksetVastaus;
import fi.vm.sade.tarjonta.service.search.KoulutusPerustieto;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.*;

import static fi.vm.sade.tarjonta.shared.KoulutusasteResolver.isToisenAsteenKoulutus;

/*
 * @author: Tuomas Katva 15/11/13
 */
public class HakukohdeValidator {

    private static final int MAX_PRECISION = 2;

    private static List<HakukohdeValidationMessages> validateCommonProperties(HakukohdeV1RDTO hakukohdeRDTO) {

        List<HakukohdeValidationMessages> validationMessages = new ArrayList<HakukohdeValidationMessages>();

        if (hakukohdeRDTO.getHakukohdeKoulutusOids() == null || hakukohdeRDTO.getHakukohdeKoulutusOids().size() < 1) {
            validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_KOULUTUS_MISSING);
        }

        if (hakukohdeRDTO.getHakuOid() == null) {
            validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_HAKU_MISSING);
        }

        if (hakukohdeRDTO.getTila() == null) {
            validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_TILA_MISSING);
            return validationMessages;
        }

        TarjontaTila hakukohdeTila = TarjontaTila.valueOf(hakukohdeRDTO.getTila());

        if (hakukohdeRDTO.getOid() == null && hakukohdeTila.equals(TarjontaTila.JULKAISTU) || hakukohdeRDTO.getOid() == null && hakukohdeTila.equals(TarjontaTila.PERUTTU)) {
            validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_TILA_WRONG);
        }

        for (YhteystiedotV1RDTO yhteystietoDTO : hakukohdeRDTO.getYhteystiedot()) {
            if (StringUtils.isBlank(yhteystietoDTO.getOsoiterivi1()) || StringUtils.isBlank(yhteystietoDTO.getPostinumero())) {
                validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_YHTEYSTIEDOT_DATA_MISSING);
                break;
            }
        }

        if (hakukohdeRDTO.getValintakokeet() != null && hakukohdeRDTO.getValintakokeet().size() > 0) {
            validationMessages.addAll(validateValintakokees(hakukohdeRDTO.getValintakokeet()));
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

    public static List<HakukohdeValidationMessages> validateToisenAsteenHakukohde(HakukohdeV1RDTO hakukohdeRDTO) {

        List<HakukohdeValidationMessages> validationMessages = new ArrayList<HakukohdeValidationMessages>();

        validationMessages.addAll(validateCommonProperties(hakukohdeRDTO));

        if (Strings.isNullOrEmpty(hakukohdeRDTO.getHakukohteenNimiUri())) {
            validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_NIMI_MISSING);
        }

        if (hakukohdeRDTO.isLukioKoulutus()) {
            if (hakukohdeRDTO.getAlinHyvaksyttavaKeskiarvo() != 0) {
                if (hakukohdeRDTO.getAlinHyvaksyttavaKeskiarvo() < 4 || hakukohdeRDTO.getAlinHyvaksyttavaKeskiarvo() > 10) {
                    validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_ALIN_HYVAKSYTTY_KESKIARVO_RANGE);
                }
            }

            for (PainotettavaOppiaineV1RDTO painotettavaOppiaineV1RDTO : hakukohdeRDTO.getPainotettavatOppiaineet()) {
                if (painotettavaOppiaineV1RDTO.getPainokerroin() == null) {
                    validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_PAINOTETTAVA_OPPIAINE_PAINOKERROIN_MISSING);
                } else if (painokerroinTooLarge(painotettavaOppiaineV1RDTO) || painokerroinTooSmall(painotettavaOppiaineV1RDTO)) {
                    validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_PAINOTETTAVA_OPPIAINE_PAINOKERROIN_RANGE);
                } else if(painokerroinHasInvalidPrecision(painotettavaOppiaineV1RDTO)) {
                    validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_PAINOTETTAVA_OPPIAINE_PAINOKERROIN_RANGE);
                }

                if (StringUtils.isBlank(painotettavaOppiaineV1RDTO.getOppiaineUri())) {
                    validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_PAINOTETTAVA_OPPIAINE_OPPIAINE_MISSING);
                }
            }
        }

        return validationMessages;
    }

    private static boolean painokerroinHasInvalidPrecision(PainotettavaOppiaineV1RDTO painotettavaOppiaineV1RDTO) {
        return painotettavaOppiaineV1RDTO.getPainokerroin().scale() > MAX_PRECISION;
    }

    private static boolean painokerroinTooLarge(PainotettavaOppiaineV1RDTO painotettavaOppiaineV1RDTO) {
        return painotettavaOppiaineV1RDTO.getPainokerroin().compareTo(new BigDecimal(20)) > 0;
    }

    private static boolean painokerroinTooSmall(PainotettavaOppiaineV1RDTO painotettavaOppiaineV1RDTO) {
        return painotettavaOppiaineV1RDTO.getPainokerroin().compareTo(new BigDecimal(1)) < 0;
    }

    public static List<HakukohdeValidationMessages> validateHakukohde(HakukohdeV1RDTO hakukohdeRDTO) {
        List<HakukohdeValidationMessages> validationMessages = new ArrayList<HakukohdeValidationMessages>();

        validationMessages.addAll(validateCommonProperties(hakukohdeRDTO));

        if (hakukohdeRDTO.getHakukohteenNimet() == null || hakukohdeRDTO.getHakukohteenNimet().size() < 1) {
            validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_NIMI_MISSING);
        }

        if (hakukohdeRDTO.getHakukohteenLiitteet() != null && hakukohdeRDTO.getHakukohteenLiitteet().size() > 0) {
            for (HakukohdeLiiteV1RDTO liite : hakukohdeRDTO.getHakukohteenLiitteet()) {
                validationMessages.addAll(validateLiite(liite));
            }
        }

        for (String kuvaus : hakukohdeRDTO.getAloituspaikatKuvaukset().values()) {
            if (kuvaus.length() > 20) {
                validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_ALOITUSPAIKAT_KUVAUS_TOO_LONG);
                break;
            }
        }

        return validationMessages;
    }

    /**
     * Tarkista että kaikilla koulutuksilla sama vuosi/kausi ja että niiden tila
     * ei ole peruttu, poistettu
     *
     * @param komotot
     */
    public static List<HakukohdeValidationMessages> checkKoulutukset(Collection<KoulutusmoduuliToteutus> komotot) {
        String kausi = null;
        Integer vuosi = null;

//        boolean tilaOk = false;
        if (komotot.size() == 0) {
            return Lists.newArrayList(HakukohdeValidationMessages.HAKUKOHDE_KOULUTUS_MISSING);
        }

        for (KoulutusmoduuliToteutus komoto : komotot) {
            if (kausi == null) {
                kausi = komoto.getAlkamiskausiUri();
                vuosi = komoto.getAlkamisVuosi();
            } else {
                if (!(Objects.equal(kausi, komoto.getAlkamiskausiUri()) && Objects.equal(vuosi, komoto.getAlkamisVuosi()))) {
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

        if (liites != null && liites.size() > 0) {

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

        for (Iterator<ValintakoeV1RDTO> i = valintakoeV1RDTOs.iterator(); i.hasNext(); ) {
            ValintakoeV1RDTO valintakoeV1RDTO = i.next();

            if (isEmptyValintakoe(valintakoeV1RDTO)) {
                i.remove();
                continue;
            }

            validateNames(validationMessages, valintakoeV1RDTO);
            validateAjankohdat(validationMessages, valintakoeV1RDTO);
            validatePisterajat(validationMessages, valintakoeV1RDTO);
        }

        return new ArrayList<HakukohdeValidationMessages>(validationMessages);
    }

    private static boolean isEmptyValintakoe(ValintakoeV1RDTO valintakoeV1RDTO) {
        return Strings.isNullOrEmpty(valintakoeV1RDTO.getValintakoeNimi())
                && Strings.isNullOrEmpty(valintakoeV1RDTO.getValintakoetyyppi())
                && (valintakoeV1RDTO.getValintakokeenKuvaus() == null || Strings.isNullOrEmpty(valintakoeV1RDTO.getValintakokeenKuvaus().getTeksti()))
                && (valintakoeV1RDTO.getValintakoeAjankohtas() == null || valintakoeV1RDTO.getValintakoeAjankohtas().isEmpty())
                && !valintakoeV1RDTO.hasPisterajat();
    }

    private static void validateNames(Set<HakukohdeValidationMessages> validationMessages, ValintakoeV1RDTO valintakoeV1RDTO) {

        if (valintakoeV1RDTO.hasPisterajat()) {
            return;
        }

        if (Strings.isNullOrEmpty(valintakoeV1RDTO.getValintakoeNimi()) && Strings.isNullOrEmpty(valintakoeV1RDTO.getValintakoetyyppi())) {
            validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_NIMI_MISSING);
        }
        if (Strings.isNullOrEmpty(valintakoeV1RDTO.getKieliUri())) {
            validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_KIELI_MISSING);
        }
    }

    private static void validateAjankohdat(Set<HakukohdeValidationMessages> validationMessages, ValintakoeV1RDTO valintakoeV1RDTO) {
        for (ValintakoeAjankohtaRDTO ajankohta : valintakoeV1RDTO.getValintakoeAjankohtas()) {
            if (ajankohta.getLoppuu() == null || ajankohta.getAlkaa() == null) {
                validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_START_OR_END_DATE_MISSING);
            } else {
                if (ajankohta.getLoppuu().before(ajankohta.getAlkaa())) {
                    validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_START_DATE_BEFORE_END_DATE);
                }
            }
            if (ajankohta.getOsoite() == null || StringUtils.isBlank(ajankohta.getOsoite().getOsoiterivi1())) {
                validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_OSOITE_MISSING);
            }
        }
    }

    private static void validatePisterajat(Set<HakukohdeValidationMessages> validationMessages, ValintakoeV1RDTO valintakoeV1RDTO) {
        if (valintakoeV1RDTO.hasPisterajat()) {
            if (validPrecision(valintakoeV1RDTO)) {
                if (validRestictions(valintakoeV1RDTO)) {
                    if (!validKokonaispisteet(valintakoeV1RDTO)) {
                        validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_PISTERAJAT_KOKONAISPISTEET_NOT_VALID);
                    }
                } else {
                    validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_PISTERAJAT_NOT_VALID);
                }
            } else {
                validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_PISTERAJAT_NOT_VALID_TYPE);
            }
            if (!validKuvaus(valintakoeV1RDTO)) {
                validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_PISTERAJAT_PAASYKOE_DATA_MISSING);
            }
            if (!validLisanaytot(valintakoeV1RDTO)) {
                validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_VALINTAKOE_PISTERAJAT_LISANAYTOT_DATA_MISSING);
            }
        }
    }

    private static boolean validLisanaytot(ValintakoeV1RDTO valintakoeV1RDTO) {
        for (ValintakoePisterajaV1RDTO valintakoePisterajaV1RDTO : valintakoeV1RDTO.getPisterajat()) {
            if (valintakoePisterajaV1RDTO.isLisapisteet()) {
                Map<String, String> lisanaytot = valintakoeV1RDTO.getLisanaytot();
                if (allEmptyValues(lisanaytot)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean validKuvaus(ValintakoeV1RDTO valintakoeV1RDTO) {
        for (ValintakoePisterajaV1RDTO valintakoePisterajaV1RDTO : valintakoeV1RDTO.getPisterajat()) {
            if (valintakoePisterajaV1RDTO.isPaasykoe()) {
                Map<String, String> kuvaukset = valintakoeV1RDTO.getKuvaukset();
                if (allEmptyValues(kuvaukset)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean allEmptyValues(Map<String, String> kuvaukset) {
        boolean allEmpty = true;
        for (Map.Entry<String, String> entry : kuvaukset.entrySet()) {
            if (StringUtils.isNotBlank(entry.getValue())) {
                allEmpty = false;
            }
        }
        return allEmpty;
    }

    private static boolean validPrecision(ValintakoeV1RDTO valintakoeV1RDTO) {
        for (ValintakoePisterajaV1RDTO valintakoePisterajaV1RDTO : valintakoeV1RDTO.getPisterajat()) {

            BigDecimal alinHyvaksyttyPistemaara = valintakoePisterajaV1RDTO.getAlinHyvaksyttyPistemaara();
            BigDecimal alinPistemaara = valintakoePisterajaV1RDTO.getAlinPistemaara();
            BigDecimal ylinPistemaara = valintakoePisterajaV1RDTO.getYlinPistemaara();

            if (valintakoePisterajaV1RDTO.isKokonaispisteet()) {
                if (alinHyvaksyttyPistemaara == null || alinHyvaksyttyPistemaara.scale() > MAX_PRECISION) {
                    return false;
                }
            } else {
                if (alinPistemaara == null || ylinPistemaara == null) {
                    return false;
                }
                if (alinPistemaara.scale() > MAX_PRECISION ||
                        ylinPistemaara.scale() > MAX_PRECISION ||
                        alinHyvaksyttyPistemaara != null && alinHyvaksyttyPistemaara.scale() > MAX_PRECISION) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean validRestictions(ValintakoeV1RDTO valintakoeV1RDTO) {

        ValintakoePisterajaV1RDTO paasykoerajat = valintakoeV1RDTO.getValintakoePisteraja(ValintakoePisterajaV1RDTO.PAASYKOE);
        ValintakoePisterajaV1RDTO lisapisterajat = valintakoeV1RDTO.getValintakoePisteraja(ValintakoePisterajaV1RDTO.LISAPISTEET);

        double pkAlin = getAlinPistemaara(paasykoerajat);
        double pkYlin = getYlinPistemaara(paasykoerajat);
        double lpAlin = getAlinPistemaara(lisapisterajat);
        double lpYlin = getYlinPistemaara(lisapisterajat);

        if (isOutOfRange(pkAlin, pkYlin, lpAlin, lpYlin)) {
            return false;
        }

        if (sumsExceedMaximum(pkYlin, lpYlin)) {
            return false;
        }

        if (lisapisterajat != null && lisapisterajat.getAlinHyvaksyttyPistemaara() != null) {
            if (rowRestrictionsViolated(lpAlin, lpYlin, lisapisterajat.getAlinHyvaksyttyPistemaara().doubleValue())) {
                return false;
            }
        } else if (rowRestrictionsViolated(lpAlin, lpYlin)) {
            return false;
        }

        if (paasykoerajat != null && paasykoerajat.getAlinHyvaksyttyPistemaara() != null) {
            if (rowRestrictionsViolated(pkAlin, pkYlin, paasykoerajat.getAlinHyvaksyttyPistemaara().doubleValue())) {
                return false;
            }
        } else if (rowRestrictionsViolated(pkAlin, pkYlin)) {
            return false;
        }
        return true;
    }

    private static double getYlinPistemaara(ValintakoePisterajaV1RDTO pisteraja) {
        if (pisteraja != null) {
            return pisteraja.getYlinPistemaara().doubleValue();
        }
        return Double.parseDouble("0.0");
    }

    private static double getAlinPistemaara(ValintakoePisterajaV1RDTO pisteraja) {
        if (pisteraja != null) {
            return pisteraja.getAlinPistemaara().doubleValue();
        }
        return Double.parseDouble("0.0");
    }

    private static boolean rowRestrictionsViolated(double pkAlin, double pkYlin, double pkAlinHyvaksytty) {
        return pkAlin > pkYlin ||
                pkAlinHyvaksytty < pkAlin ||
                pkAlinHyvaksytty > pkYlin;
    }

    private static boolean rowRestrictionsViolated(double pkAlin, double pkYlin) {
        return pkAlin > pkYlin;
    }

    private static boolean isOutOfRange(double pkAlin,
                                        double pkYlin,
                                        double lpAlin,
                                        double lpYlin) {
        return pkAlin < 0 ||
                pkAlin > 10 ||
                pkYlin < 0 ||
                pkYlin > 10 ||
                lpAlin < 0 ||
                lpAlin > 10 ||
                lpYlin < 0 ||
                lpYlin > 10;
    }

    private static boolean sumsExceedMaximum(double pkYlin, double lpYlin) {
        return pkYlin + lpYlin > 10;
    }

    private static boolean validKokonaispisteet(ValintakoeV1RDTO valintakoeV1RDTO) {
        ValintakoePisterajaV1RDTO kokonaispisterajat = valintakoeV1RDTO.getValintakoePisteraja(ValintakoePisterajaV1RDTO.KOKONAISPISTEET);

        if (kokonaispisterajat == null) {
            return true;
        }

        ValintakoePisterajaV1RDTO paasykoerajat = valintakoeV1RDTO.getValintakoePisteraja(ValintakoePisterajaV1RDTO.PAASYKOE);
        ValintakoePisterajaV1RDTO lisapisterajat = valintakoeV1RDTO.getValintakoePisteraja(ValintakoePisterajaV1RDTO.LISAPISTEET);

        double pkAlin = getAlinPistemaara(paasykoerajat);
        double pkYlin = getYlinPistemaara(paasykoerajat);
        double lpAlin = getAlinPistemaara(lisapisterajat);
        double lpYlin = getYlinPistemaara(lisapisterajat);

        if (paasykoerajat != null && paasykoerajat.getAlinHyvaksyttyPistemaara() != null) {
            pkAlin = paasykoerajat.getAlinHyvaksyttyPistemaara().doubleValue();
        }

        if (lisapisterajat != null && lisapisterajat.getAlinHyvaksyttyPistemaara() != null) {
            lpAlin = lisapisterajat.getAlinHyvaksyttyPistemaara().doubleValue();
        }

        if (kokonaispisterajat.getAlinHyvaksyttyPistemaara() != null) {
            if (kpAlinHViolates(kokonaispisterajat.getAlinHyvaksyttyPistemaara().doubleValue(), pkAlin, pkYlin, lpAlin, lpYlin)) {
                return false;
            }
        }
        return true;
    }

    private static boolean kpAlinHViolates(double kpAlinH, double pkAlin, double pkYlin,
                                           double lpAlin, double lpYlin) {
        return kpAlinH < (pkAlin + lpAlin) || kpAlinH > (pkYlin + lpYlin);
    }

    public static ResultV1RDTO<ValitutKoulutuksetV1RDTO> getValidKomotoSelection(final KoulutuksetVastaus kv) {
        ResultV1RDTO<ValitutKoulutuksetV1RDTO> result = new ResultV1RDTO<ValitutKoulutuksetV1RDTO>();
        ValitutKoulutuksetV1RDTO dto = new ValitutKoulutuksetV1RDTO();
        Map<String, Set<String>> mapSelectedKomos = Maps.<String, Set<String>>newHashMap();

        Map<HakukohdeValidationMessages, Set<String>> map = Maps.<HakukohdeValidationMessages, Set<String>>newHashMap();

        for (KoulutusPerustieto kp : kv.getKoulutukset()) {

            mapSelectedKomos.put(kp.getKomotoOid(), Sets.<String>newHashSet());

            if (kp.getTila() == null || kp.getTila().equals(fi.vm.sade.tarjonta.service.types.TarjontaTila.POISTETTU)) {
                createError(kp.getKomotoOid(), kp.getKomotoOid(), mapSelectedKomos, map, HakukohdeValidationMessages.KOMOTO_TILA);
            } else if (kp.getToteutustyyppi() == null) {
                createError(kp.getKomotoOid(), kp.getKomotoOid(), mapSelectedKomos, map, HakukohdeValidationMessages.KOMOTO_KOULUTUSTYYPPI_URI);
            }

            for (KoulutusPerustieto o : kv.getKoulutukset()) {
                //search all invalid koulutus compinations
                if (kp.getKomotoOid().equals(o.getKomotoOid())) {
                    continue;
                }

                if (kp.getToteutustyyppi() != null && !kp.getToteutustyyppi().equals(o.getToteutustyyppi())) {
                    //toteutustyyppi enum must be same
                    createError(kp.getKomotoOid(), o.getKomotoOid(), mapSelectedKomos, map, HakukohdeValidationMessages.KOMOTO_KOULUTUSTYYPPI_URI);
                } else if (!kp.getToteutustyyppi().equals(ToteutustyyppiEnum.KORKEAKOULUTUS) && !isEqualKoodistoKoodiUri(kp.getKoulutusKoodi(), o.getKoulutusKoodi())) {
                    //koulutus koodi must be same
                    createError(kp.getKomotoOid(), o.getKomotoOid(), mapSelectedKomos, map, HakukohdeValidationMessages.KOMOTO_KOULUTUS_URI);
                } else if (!kp.getKoulutuksenAlkamisVuosi().equals(o.getKoulutuksenAlkamisVuosi())) {
                    //koulutus koodi must be same
                    createError(kp.getKomotoOid(), o.getKomotoOid(), mapSelectedKomos, map, HakukohdeValidationMessages.KOMOTO_VUOSI);
                } else if (!isEqualKoodistoKoodiUri(kp.getKoulutuksenAlkamiskausi(), o.getKoulutuksenAlkamiskausi())) {
                    //koulutus koodi must be same
                    createError(kp.getKomotoOid(), o.getKomotoOid(), mapSelectedKomos, map, HakukohdeValidationMessages.KOMOTO_KAUSI_URI);
                } else if (isToisenAsteenKoulutus(kp.getToteutustyyppi()) && !kp.getTarjoaja().getOid().equals(o.getTarjoaja().getOid())) {
                    createError(kp.getKomotoOid(), o.getKomotoOid(), mapSelectedKomos, map, HakukohdeValidationMessages.KOMOTO_ERI_TARJOAJAT);
                } else if (isToisenAsteenKoulutus(kp.getToteutustyyppi()) && !isEqualKoodistoKoodiUri(kp.getPohjakoulutusvaatimus(), o.getPohjakoulutusvaatimus())) {
                    createError(kp.getKomotoOid(), o.getKomotoOid(), mapSelectedKomos, map, HakukohdeValidationMessages.KOMOTO_ERI_POHJAKOULUTUSVAATIMUKSET);
                }
            }
        }
        for (Map.Entry<HakukohdeValidationMessages, Set<String>> e : map.entrySet()) {
            switch (e.getKey()) {
                case KOMOTO_KOULUTUS_URI:
                    result.addError(ErrorV1RDTO.createValidationError("koulutus", "hakukohde.luonti.virhe.koulutus",
                            e.getValue().toArray(new String[e.getValue().size()])));
                    break;
                case KOMOTO_KOULUTUSTYYPPI_URI:
                    result.addError(ErrorV1RDTO.createValidationError("koulutustyyppi", "hakukohde.luonti.virhe.tyyppi",
                            e.getValue().toArray(new String[e.getValue().size()])));
                    break;
                case KOMOTO_KAUSI_URI:
                    result.addError(ErrorV1RDTO.createValidationError("kausi", "hakukohde.luonti.virhe.kausi",
                            e.getValue().toArray(new String[e.getValue().size()])));
                    break;
                case KOMOTO_VUOSI:
                    result.addError(ErrorV1RDTO.createValidationError("vuosi", "hakukohde.luonti.virhe.vuosi",
                            e.getValue().toArray(new String[e.getValue().size()])));
                    break;
                case KOMOTO_TILA:
                    result.addError(ErrorV1RDTO.createValidationError("tila", "hakukohde.luonti.virhe.tila",
                            e.getValue().toArray(new String[e.getValue().size()])));
                    break;
                case KOMOTO_ERI_TARJOAJAT:
                    result.addError(ErrorV1RDTO.createValidationError("tarjoaja", "hakukohde.luonti.virhe.tarjoaja",
                            e.getValue().toArray(new String[e.getValue().size()])));
                    break;
                case KOMOTO_ERI_POHJAKOULUTUSVAATIMUKSET:
                    result.addError(ErrorV1RDTO.createValidationError("pohjakoulutusvaatimus", "hakukohde.luonti.virhe.pohjakoulutusvaatimus",
                            e.getValue().toArray(new String[e.getValue().size()])));
                    break;
            }
        }
        dto.setOidConflictingWithOids(mapSelectedKomos);
        result.setResult(dto);
        return result;
    }

    private static void createError(
            final String oid1,
            final String oid2,
            final Map<String, Set<String>> mapSelectedKomos,
            final Map<HakukohdeValidationMessages, Set<String>> map,
            final HakukohdeValidationMessages message) {

        if (!map.containsKey(message)) {
            map.put(message, Sets.<String>newHashSet());
        }

        map.get(message).add(oid2);
        mapSelectedKomos.get(oid1).add(oid2);
    }

    private static boolean isEqualKoodistoKoodiUri(KoodistoKoodi koodi1, KoodistoKoodi koodi2) {
        if (koodi1 == null
                || koodi1.getUri() == null
                || koodi2 == null
                || koodi2.getUri() == null) {
            return false;
        }

        return TarjontaKoodistoHelper.getKoodiURIFromVersionedUri(
                koodi1.getUri()).equals(TarjontaKoodistoHelper.getKoodiURIFromVersionedUri(
                koodi2.getUri()));
    }

}
