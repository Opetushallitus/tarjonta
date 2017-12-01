package fi.vm.sade.tarjonta.service.impl.resources.v1.hakukohde.validation;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fi.vm.sade.tarjonta.dao.HakuDAO;
import fi.vm.sade.tarjonta.dao.HakukohdeDAO;
import fi.vm.sade.tarjonta.dao.KoulutusmoduuliToteutusDAO;
import fi.vm.sade.tarjonta.model.Haku;
import fi.vm.sade.tarjonta.model.Hakukohde;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliToteutus;
import fi.vm.sade.tarjonta.service.auth.NotAuthorizedException;
import fi.vm.sade.tarjonta.service.auth.PermissionChecker;
import fi.vm.sade.tarjonta.service.business.exception.KoulutusNotFoundException;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeAjankohtaRDTO;
import fi.vm.sade.tarjonta.service.resources.v1.dto.*;
import fi.vm.sade.tarjonta.service.resources.v1.dto.koulutus.KoulutusIdentification;
import fi.vm.sade.tarjonta.service.search.KoodistoKoodi;
import fi.vm.sade.tarjonta.service.search.KoulutuksetVastaus;
import fi.vm.sade.tarjonta.service.search.KoulutusPerustieto;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.types.ToteutustyyppiEnum;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class HakukohdeValidator {

    private static final int MAX_PRECISION = 2;

    private final KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO;

    private final HakukohdeDAO hakukohdeDAO;

    private final HakuDAO hakuDAO;

    private final PermissionChecker permissionChecker;

    @Autowired
    public HakukohdeValidator(KoulutusmoduuliToteutusDAO koulutusmoduuliToteutusDAO, HakukohdeDAO hakukohdeDAO, HakuDAO hakuDAO, PermissionChecker permissionChecker) {
        this.koulutusmoduuliToteutusDAO = koulutusmoduuliToteutusDAO;
        this.hakukohdeDAO = hakukohdeDAO;
        this.hakuDAO = hakuDAO;
        this.permissionChecker = permissionChecker;
    }

    public List<HakukohdeValidationMessages> validateCommonProperties(HakukohdeV1RDTO hakukohdeRDTO) {

        List<HakukohdeValidationMessages> validationMessages = new ArrayList<>();

        Set<String> komotoOids = new HashSet<>();
        komotoOids.addAll(hakukohdeRDTO.getHakukohdeKoulutusOids());
        try {
            komotoOids.addAll(getKomotoOidsFromKoulutusIds(hakukohdeRDTO));
        } catch (KoulutusNotFoundException e) {
            return Lists.newArrayList(HakukohdeValidationMessages.HAKUKOHDE_KOULUTUS_DOES_NOT_EXIST);
        }
        hakukohdeRDTO.setHakukohdeKoulutusOids(Lists.newArrayList(komotoOids));

        validationMessages.addAll(checkKoulutukset(hakukohdeRDTO.getHakukohdeKoulutusOids()));

        if (StringUtils.isBlank(hakukohdeRDTO.getHakuOid())) {
            validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_HAKU_MISSING);
        } else {
            Haku haku = hakuDAO.findByOid(hakukohdeRDTO.getHakuOid());
            if (haku == null || TarjontaTila.POISTETTU.equals(haku.getTila())) {
                validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_INVALID_HAKU_OID);
            }
        }

        if (hakukohdeRDTO.getTila() == null) {
            validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_TILA_MISSING);
            return validationMessages;
        }

        for (YhteystiedotV1RDTO yhteystietoDTO : hakukohdeRDTO.getYhteystiedot()) {
            if (StringUtils.isBlank(yhteystietoDTO.getHakutoimistonNimi())) {
                validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_YHTEYSTIEDOT_DATA_MISSING);
                break;
            }
        }

        if (hakukohdeRDTO.getValintakokeet() != null && hakukohdeRDTO.getValintakokeet().size() > 0) {
            validationMessages.addAll(validateValintakokees(hakukohdeRDTO.getValintakokeet()));
        }

        return validationMessages;
    }

    public List<HakukohdeValidationMessages> validateAikuLukioHakukohde(HakukohdeV1RDTO hakukohdeRDTO) {

        List<HakukohdeValidationMessages> validationMessages = new ArrayList<>();

        if (hakukohdeRDTO.getHakukohteenNimiUri() == null || hakukohdeRDTO.getHakukohteenNimiUri().trim().length() < 1) {
            validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_NIMI_MISSING);
        }

        return validationMessages;
    }

    public List<HakukohdeValidationMessages> validateToisenAsteenHakukohde(HakukohdeV1RDTO hakukohdeRDTO) {

        List<HakukohdeValidationMessages> validationMessages = new ArrayList<>();

        validationMessages.addAll(validateDuplicateHakukohteet(hakukohdeRDTO));

        if (Strings.isNullOrEmpty(hakukohdeRDTO.getHakukohteenNimiUri())) {
            validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_NIMI_MISSING);
        }

        if (hakukohdeRDTO.isLukioKoulutus()) {
            validatePainotettavatOppiaineet(hakukohdeRDTO, validationMessages);
        }

        return validationMessages;
    }

    private void validatePainotettavatOppiaineet(HakukohdeV1RDTO hakukohdeRDTO, List<HakukohdeValidationMessages> validationMessages) {
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
            } else if (painokerroinHasInvalidPrecision(painotettavaOppiaineV1RDTO)) {
                validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_PAINOTETTAVA_OPPIAINE_PAINOKERROIN_RANGE);
            }

            if (StringUtils.isBlank(painotettavaOppiaineV1RDTO.getOppiaineUri())) {
                validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_PAINOTETTAVA_OPPIAINE_OPPIAINE_MISSING);
            }
        }
    }

    private boolean painokerroinHasInvalidPrecision(PainotettavaOppiaineV1RDTO painotettavaOppiaineV1RDTO) {
        return painotettavaOppiaineV1RDTO.getPainokerroin().scale() > MAX_PRECISION;
    }

    private boolean painokerroinTooLarge(PainotettavaOppiaineV1RDTO painotettavaOppiaineV1RDTO) {
        return painotettavaOppiaineV1RDTO.getPainokerroin().compareTo(new BigDecimal(20)) > 0;
    }

    private boolean painokerroinTooSmall(PainotettavaOppiaineV1RDTO painotettavaOppiaineV1RDTO) {
        return painotettavaOppiaineV1RDTO.getPainokerroin().compareTo(new BigDecimal(1)) < 0;
    }

    public List<HakukohdeValidationMessages> validateHakukohde(HakukohdeV1RDTO hakukohdeRDTO) {
        List<HakukohdeValidationMessages> validationMessages = new ArrayList<>();

        if (hakukohdeRDTO.getHakukohteenNimet() == null || hakukohdeRDTO.getHakukohteenNimet().size() < 1) {
            validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_NIMI_MISSING);
        }

        if (hakukohdeRDTO.getHakukohteenLiitteet() != null && hakukohdeRDTO.getHakukohteenLiitteet().size() > 0) {
            for (HakukohdeLiiteV1RDTO liite : hakukohdeRDTO.getHakukohteenLiitteet()) {
                validationMessages.addAll(validateLiite(liite, true));
            }
        }

        if (hakukohdeRDTO.getAloituspaikatKuvaukset() != null) {
            for (String kuvaus : hakukohdeRDTO.getAloituspaikatKuvaukset().values()) {
                if (kuvaus.length() > 20) {
                    validationMessages.add(HakukohdeValidationMessages.HAKUKOHDE_ALOITUSPAIKAT_KUVAUS_TOO_LONG);
                    break;
                }
            }
        }

        return validationMessages;
    }

    private List<HakukohdeValidationMessages> validateDuplicateHakukohteet(HakukohdeV1RDTO hakukohdeRDTO) {
        List<HakukohdeValidationMessages> messages = new ArrayList<>();

        // Only validate when creating new hakukohde (oid doesn't exist yet)
        if (hakukohdeRDTO.getOid() != null) {
            return messages;
        }

        if (hakukohdeRDTO.isLukioKoulutus() || hakukohdeRDTO.isAmmatillinenPerustutkinto() ||
                (isYhteishaku(hakukohdeRDTO.getHakuOid()) && hakukohdeRDTO.isAmmatillinenPerustutkintoAlk2018()) ) {
            List<String> koulutusOids = hakukohdeRDTO.getHakukohdeKoulutusOids();
            for (String koulutusOid : koulutusOids) {
                KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findByOid(koulutusOid);
                if (isDuplicateHakukohdeForKomoto(hakukohdeRDTO, komoto)) {
                    messages.add(HakukohdeValidationMessages.HAKUKOHDE_DUPLIKAATTI);
                }
            }
            if (isDuplicateHakukohdeForHakuAndTarjoaja(hakukohdeRDTO)) {
                messages.add(HakukohdeValidationMessages.HAKUKOHDE_DUPLIKAATTI_SAMALLA_NIMELLA);
            }
        }
        return messages;
    }

    private boolean isYhteishaku(String hakuOid){
        Haku haku = hakuDAO.findByOid(hakuOid);
        return haku.isYhteishaku();
    }

    private boolean isDuplicateHakukohdeForHakuAndTarjoaja(HakukohdeV1RDTO hakukohde) {
        for (Hakukohde h : hakukohdeDAO.findByTarjoajaHakuAndNimiUri(hakukohde.getTarjoajaOids(), hakukohde.getHakuOid(), hakukohde.getHakukohteenNimiUri())) {
            // Self not a duplicate
            if (!h.getOid().equals(hakukohde.getOid())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isDuplicateHakukohdeForKomoto(HakukohdeV1RDTO hakukohdeDTO, KoulutusmoduuliToteutus komoto) {
        String hakuOid = hakukohdeDTO.getHakuOid();
        String nimiUri = hakukohdeDTO.getHakukohteenNimiUri();

        if (!includeInDuplicateCheck(hakukohdeDTO.getTila())) {
            return false;
        }

        for (Hakukohde hakukohde : komoto.getHakukohdes()) {
            if (includeInDuplicateCheck(hakukohde.getTila())
                    && hakukohde.getHaku().getOid().equals(hakuOid)
                    && hakukohde.getHakukohdeNimi().equals(nimiUri)
                    && !hakukohde.getOid().equals(hakukohdeDTO.getOid())) {
                return true;
            }
        }
        return false;
    }

    private static boolean includeInDuplicateCheck(TarjontaTila tila) {
        return !tila.equals(TarjontaTila.POISTETTU) && !tila.equals(TarjontaTila.PERUTTU);
    }

    private Collection<String> getKomotoOidsFromKoulutusIds(HakukohdeV1RDTO dto) throws KoulutusNotFoundException {
        Set<String> komotoOids = new HashSet<>();
        if (dto.getKoulutukset() != null) {
            for (KoulutusIdentification komotoId : dto.getKoulutukset()) {
                KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findKomotoByKoulutusId(komotoId);
                if (komoto != null) {
                    komotoOids.add(komoto.getOid());
                    dto.getTarjoajaOids().add(komoto.getTarjoaja());
                } else {
                    throw new KoulutusNotFoundException(komotoId);
                }
            }
        }
        return komotoOids;
    }

    /**
     * Tarkista että kaikilla koulutuksilla sama vuosi/kausi ja että niiden tila
     * ei ole poistettu
     *
     * @param komotoOids
     */
    public List<HakukohdeValidationMessages> checkKoulutukset(Collection<String> komotoOids) {
        String kausi = null;
        Integer vuosi = null;

        if (komotoOids == null || komotoOids.size() == 0) {
            return Lists.newArrayList(HakukohdeValidationMessages.HAKUKOHDE_KOULUTUS_MISSING);
        }

        for (String komotoOid : komotoOids) {
            KoulutusmoduuliToteutus komoto = koulutusmoduuliToteutusDAO.findKomotoByOid(komotoOid);

            if (komoto == null || TarjontaTila.POISTETTU.equals(komoto.getTila())) {
                return Lists.newArrayList(HakukohdeValidationMessages.HAKUKOHDE_KOULUTUS_DOES_NOT_EXIST);
            }

            try {
                permissionChecker.checkUpdateKoulutusByTarjoajaOid(komoto.getTarjoaja());
            } catch (NotAuthorizedException e) {
                return Lists.newArrayList(HakukohdeValidationMessages.HAKUKOHDE_PERMISSION_DENIED_FOR_KOULUTUS);
            }

            if (kausi == null) {
                kausi = komoto.getAlkamiskausiUri();
                vuosi = komoto.getAlkamisVuosi();
            } else {
                if (!(Objects.equal(kausi, komoto.getAlkamiskausiUri()) && Objects.equal(vuosi, komoto.getAlkamisVuosi()))) {
                    return Lists.newArrayList(HakukohdeValidationMessages.HAKUKOHDE_KOULUTUS_VUOSI_KAUSI_INVALID);
                }
            }
        }

        return Lists.newArrayList();
    }

    public List<HakukohdeValidationMessages> checkTarjoajat(Hakukohde hakukohde, Collection<KoulutusTarjoajaV1RDTO> koulutusTarjoajas) {
        // tarkistetaan että pyynnössä annetut tarjoajat löytyvät komotolta
        boolean tarjoajasFoundInKomotos = koulutusTarjoajas.stream().allMatch(komotoTarjoaja -> {
                Set<String> tarjoajaOidsInKomoto = koulutusmoduuliToteutusDAO.findKomotoByOid(komotoTarjoaja.getOid()).getTarjoajaOids();
                String tarjoajaOidFromParameter = komotoTarjoaja.getTarjoajaOid();
                return tarjoajaOidsInKomoto.contains(tarjoajaOidFromParameter);
        });

        // Tarkistetaan ettei useita eri tarjoajia. Verrataan uusien koulutusten tarjoajia toisiinsa, sekä olemassaolevien
        // koulutusten tarjoajiin. Ei verrata tässä olemassaolevia toisiinsa, vaan korjataan vanhat datavirheet kantaan.
        List<String> existingOids = hakukohde.getKoulutusmoduuliToteutuses().stream()
                .map(k -> k.getTarjoaja())
                .collect(Collectors.toList());
        Set<String> newOidsSet = koulutusTarjoajas.stream()
                .map(k -> k.getTarjoajaOid())
                .collect(Collectors.toSet());

        boolean newOidsMatchEachOther = newOidsSet.size() == 1;
        boolean newOidsMatchExisting = existingOids.isEmpty() || existingOids.contains(newOidsSet.iterator().next());

        if (!tarjoajasFoundInKomotos) {
            return Lists.newArrayList(HakukohdeValidationMessages.KOMOTO_VIRHEELLINEN_TARJOAJA);
        } else if (!newOidsMatchEachOther || !newOidsMatchExisting) {
            return Lists.newArrayList(HakukohdeValidationMessages.KOMOTO_ERI_TARJOAJAT);
        } else {
            return Lists.newArrayList();
        }
    }

    public List<HakukohdeValidationMessages> validateLiite(HakukohdeLiiteV1RDTO liite, boolean validateToimitettavaMennessa) {

        Set<HakukohdeValidationMessages> liiteValidationMsgs = new HashSet<>();

        if (liite.getKieliUri() == null || liite.getKieliUri().length() < 1) {
            liiteValidationMsgs.add(HakukohdeValidationMessages.HAKUKOHDE_LIITE_KIELI_MISSING);
        }

        if (liite.getLiitteenNimi() == null || liite.getLiitteenNimi().length() < 1) {
            liiteValidationMsgs.add(HakukohdeValidationMessages.HAKUKOHDE_LIITE_NIMI_MISSING);
        }

        if (liite.getLiitteenToimitusOsoite() == null) {
            liiteValidationMsgs.add(HakukohdeValidationMessages.HAKUKOHDE_LIITE_OSOITE_MISSING);
        }

        if (liite.getToimitettavaMennessa() == null && validateToimitettavaMennessa) {

            liiteValidationMsgs.add(HakukohdeValidationMessages.HAKUKOHDE_LIITE_TOIMITETTAVA_MENNESSA_MISSING);

        }

        return new ArrayList<>(liiteValidationMsgs);
    }

    public List<HakukohdeValidationMessages> validateValintakokees(List<ValintakoeV1RDTO> valintakoeV1RDTOs) {
        Set<HakukohdeValidationMessages> validationMessages = new HashSet<>();

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

        return new ArrayList<>(validationMessages);
    }

    private boolean isEmptyValintakoe(ValintakoeV1RDTO valintakoeV1RDTO) {
        return Strings.isNullOrEmpty(valintakoeV1RDTO.getValintakoeNimi())
                && Strings.isNullOrEmpty(valintakoeV1RDTO.getValintakoetyyppi())
                && (valintakoeV1RDTO.getValintakokeenKuvaus() == null || Strings.isNullOrEmpty(valintakoeV1RDTO.getValintakokeenKuvaus().getTeksti()))
                && (valintakoeV1RDTO.getValintakoeAjankohtas() == null || valintakoeV1RDTO.getValintakoeAjankohtas().isEmpty())
                && !valintakoeV1RDTO.hasPisterajat();
    }

    private void validateNames(Set<HakukohdeValidationMessages> validationMessages, ValintakoeV1RDTO valintakoeV1RDTO) {

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

    private void validateAjankohdat(Set<HakukohdeValidationMessages> validationMessages, ValintakoeV1RDTO valintakoeV1RDTO) {
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

    private void validatePisterajat(Set<HakukohdeValidationMessages> validationMessages, ValintakoeV1RDTO valintakoeV1RDTO) {
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

    private boolean validLisanaytot(ValintakoeV1RDTO valintakoeV1RDTO) {
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

    private boolean validKuvaus(ValintakoeV1RDTO valintakoeV1RDTO) {
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

    private boolean allEmptyValues(Map<String, String> kuvaukset) {
        boolean allEmpty = true;
        for (Map.Entry<String, String> entry : kuvaukset.entrySet()) {
            if (StringUtils.isNotBlank(entry.getValue())) {
                allEmpty = false;
            }
        }
        return allEmpty;
    }

    private boolean validPrecision(ValintakoeV1RDTO valintakoeV1RDTO) {
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

    private boolean validRestictions(ValintakoeV1RDTO valintakoeV1RDTO) {

        ValintakoePisterajaV1RDTO paasykoerajat = valintakoeV1RDTO.getValintakoePisteraja(ValintakoePisterajaV1RDTO.PAASYKOE);
        ValintakoePisterajaV1RDTO lisapisterajat = valintakoeV1RDTO.getValintakoePisteraja(ValintakoePisterajaV1RDTO.LISAPISTEET);

        double pkAlin = getAlinPistemaara(paasykoerajat);
        double pkYlin = getYlinPistemaara(paasykoerajat);
        double lpAlin = getAlinPistemaara(lisapisterajat);
        double lpYlin = getYlinPistemaara(lisapisterajat);

        return !isOutOfRange(pkAlin, pkYlin, lpAlin, lpYlin)
                && !sumsExceedMaximum(pkYlin, lpYlin)
                && pistemaaratRajojenPuitteissa(lisapisterajat, lpAlin, lpYlin)
                && pistemaaratRajojenPuitteissa(paasykoerajat, pkAlin, pkYlin);
    }

    private boolean pistemaaratRajojenPuitteissa(ValintakoePisterajaV1RDTO pisteraja, double lpAlin, double lpYlin) {
        if (pisteraja != null && pisteraja.getAlinHyvaksyttyPistemaara() != null) {
            if (rowRestrictionsViolated(lpAlin, lpYlin, pisteraja.getAlinHyvaksyttyPistemaara().doubleValue())) {
                return false;
            }
        } else if (rowRestrictionsViolated(lpAlin, lpYlin)) {
            return false;
        }
        return true;
    }

    private double getYlinPistemaara(ValintakoePisterajaV1RDTO pisteraja) {
        if (pisteraja != null) {
            return pisteraja.getYlinPistemaara().doubleValue();
        }
        return Double.parseDouble("0.0");
    }

    private double getAlinPistemaara(ValintakoePisterajaV1RDTO pisteraja) {
        if (pisteraja != null) {
            return pisteraja.getAlinPistemaara().doubleValue();
        }
        return Double.parseDouble("0.0");
    }

    private boolean rowRestrictionsViolated(double pkAlin, double pkYlin, double pkAlinHyvaksytty) {
        return pkAlin > pkYlin ||
                pkAlinHyvaksytty < pkAlin ||
                pkAlinHyvaksytty > pkYlin;
    }

    private boolean rowRestrictionsViolated(double pkAlin, double pkYlin) {
        return pkAlin > pkYlin;
    }

    private boolean isOutOfRange(double pkAlin,
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

    private boolean sumsExceedMaximum(double pkYlin, double lpYlin) {
        return pkYlin + lpYlin > 10;
    }

    private boolean validKokonaispisteet(ValintakoeV1RDTO valintakoeV1RDTO) {
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

    private boolean kpAlinHViolates(double kpAlinH, double pkAlin, double pkYlin,
                                    double lpAlin, double lpYlin) {
        return kpAlinH < (pkAlin + lpAlin) || kpAlinH > (pkYlin + lpYlin);
    }

    public ResultV1RDTO<ValitutKoulutuksetV1RDTO> getValidKomotoSelection(final KoulutuksetVastaus kv) {
        ResultV1RDTO<ValitutKoulutuksetV1RDTO> result = new ResultV1RDTO<>();
        ValitutKoulutuksetV1RDTO dto = new ValitutKoulutuksetV1RDTO();

        Map<String, Set<String>> oidToConflictinOidsMap = Maps.newHashMap();
        Map<HakukohdeValidationMessages, Set<String>> errorMessageToOidsMap = Maps.newHashMap();

        for (KoulutusPerustieto kp : kv.getKoulutukset()) {

            oidToConflictinOidsMap.put(kp.getKomotoOid(), Sets.newHashSet());

            if (kp.getTila() == null || kp.getTila().equals(fi.vm.sade.tarjonta.service.types.TarjontaTila.POISTETTU)) {
                createError(kp.getKomotoOid(), kp.getKomotoOid(), oidToConflictinOidsMap, errorMessageToOidsMap, HakukohdeValidationMessages.KOMOTO_TILA);
            } else if (kp.getToteutustyyppi() == null) {
                createError(kp.getKomotoOid(), kp.getKomotoOid(), oidToConflictinOidsMap, errorMessageToOidsMap, HakukohdeValidationMessages.KOMOTO_KOULUTUSTYYPPI_URI);
            }

            for (KoulutusPerustieto o : kv.getKoulutukset()) {
                //search all invalid koulutus compinations
                if (kp.getKomotoOid().equals(o.getKomotoOid())) {
                    continue;
                }

                if (kp.getToteutustyyppi() != null && !kp.getToteutustyyppi().equals(o.getToteutustyyppi())) {
                    //toteutustyyppi enum must be same
                    createError(kp.getKomotoOid(), o.getKomotoOid(), oidToConflictinOidsMap, errorMessageToOidsMap, HakukohdeValidationMessages.KOMOTO_KOULUTUSTYYPPI_URI);
                } else if (!ToteutustyyppiEnum.KORKEAKOULUTUS.equals(kp.getToteutustyyppi())
                        && !ToteutustyyppiEnum.KORKEAKOULUOPINTO.equals(kp.getToteutustyyppi())
                        && !isEqualKoodistoKoodiUri(kp.getKoulutusKoodi(), o.getKoulutusKoodi())) {
                    //koulutus koodi must be same
                    createError(kp.getKomotoOid(), o.getKomotoOid(), oidToConflictinOidsMap, errorMessageToOidsMap, HakukohdeValidationMessages.KOMOTO_KOULUTUS_URI);
                } else if (!kp.getKoulutuksenAlkamisVuosi().equals(o.getKoulutuksenAlkamisVuosi())) {
                    //koulutus koodi must be same
                    createError(kp.getKomotoOid(), o.getKomotoOid(), oidToConflictinOidsMap, errorMessageToOidsMap, HakukohdeValidationMessages.KOMOTO_VUOSI);
                } else if (!isEqualKoodistoKoodiUri(kp.getKoulutuksenAlkamiskausi(), o.getKoulutuksenAlkamiskausi())) {
                    //koulutus koodi must be same
                    createError(kp.getKomotoOid(), o.getKomotoOid(), oidToConflictinOidsMap, errorMessageToOidsMap, HakukohdeValidationMessages.KOMOTO_KAUSI_URI);
                } else if (kp.getToteutustyyppi().isToisenAsteenKoulutus() && !kp.getTarjoaja().getOid().equals(o.getTarjoaja().getOid())) {
                    createError(kp.getKomotoOid(), o.getKomotoOid(), oidToConflictinOidsMap, errorMessageToOidsMap, HakukohdeValidationMessages.KOMOTO_ERI_TARJOAJAT);
                } else if (kp.getToteutustyyppi().isToisenAsteenKoulutus() && !isEqualKoodistoKoodiUri(kp.getPohjakoulutusvaatimus(), o.getPohjakoulutusvaatimus())) {
                    createError(kp.getKomotoOid(), o.getKomotoOid(), oidToConflictinOidsMap, errorMessageToOidsMap, HakukohdeValidationMessages.KOMOTO_ERI_POHJAKOULUTUSVAATIMUKSET);
                }
            }
        }
        for (Map.Entry<HakukohdeValidationMessages, Set<String>> e : errorMessageToOidsMap.entrySet()) {
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
        dto.setOidConflictingWithOids(oidToConflictinOidsMap);
        result.setResult(dto);
        return result;
    }

    private void createError(
            final String oid1,
            final String oid2,
            final Map<String, Set<String>> mapSelectedKomos,
            final Map<HakukohdeValidationMessages, Set<String>> map,
            final HakukohdeValidationMessages message) {

        if (!map.containsKey(message)) {
            map.put(message, Sets.newHashSet());
        }

        map.get(message).add(oid2);
        mapSelectedKomos.get(oid1).add(oid2);
    }

    private boolean isEqualKoodistoKoodiUri(KoodistoKoodi koodi1, KoodistoKoodi koodi2) {
        return koodi1 != null
                && koodi1.getUri() != null
                && koodi2 != null
                && koodi2.getUri() != null
                && TarjontaKoodistoHelper.getKoodiURIFromVersionedUri(koodi1.getUri()).equals(TarjontaKoodistoHelper.getKoodiURIFromVersionedUri(koodi2.getUri()));

    }

}
