package fi.vm.sade.tarjonta.data.util;

import fi.vm.sade.generic.common.DateHelper;
import fi.vm.sade.koodisto.service.types.CreateKoodiDataType;
import fi.vm.sade.koodisto.service.types.CreateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodistoMetadataType;
import fi.vm.sade.tarjonta.data.dto.Koodi;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class DataUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataUtils.class);
    private static final String DATE_PATTERN = "dd.MM.yyyy";
    private static final String ISO_DATE_PATTERN = "yyyy-MM-dd";
    private static final String REGEX_ISO_DATE = "\\d{4}-\\d{2}-\\d{2}";

    private DataUtils() {

    }

    public static CreateKoodistoDataType createCreateKoodistoDataType(final String omistaja,
                                                                      final String organisaatioOid,
                                                                      final Date voimassaAlkuPvm,
                                                                      final Date voimassaLoppuPvm,
                                                                      final String nimi) {
        final CreateKoodistoDataType type = new CreateKoodistoDataType();
        type.setOmistaja(omistaja);
        type.setOrganisaatioOid(organisaatioOid);
        type.setVoimassaAlkuPvm(voimassaAlkuPvm != null ? DateHelper.DateToXmlCal(voimassaAlkuPvm) : null);
        type.setVoimassaLoppuPvm(voimassaLoppuPvm != null ? DateHelper.DateToXmlCal(voimassaLoppuPvm) : null);

        for (final KieliType k : KieliType.values()) {
            final KoodistoMetadataType m = new KoodistoMetadataType();
            m.setNimi(nimi);
            m.setKieli(k);
            m.setKuvaus(nimi);
            type.getMetadataList().add(m);
        }

        return type;
    }

    public static String createKoodistoUriFromName(final String koodistoNimi) {
        if (koodistoNimi == null) {
            return StringUtils.EMPTY;
        }

        return transliterate(koodistoNimi);
    }

    private static final Map<Character, Character> transliteration = new HashMap<Character, Character>();

    static {
        Character[][] specialCharacters = new Character[][]{{'å', 'o'}, {'ä', 'a'}, {'ö', 'o'}};
        Character[] allowedCharacters = new Character[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
                'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
                '6', '7', '8', '9'};
        for (Character c : allowedCharacters) {
            transliteration.put(c, c);
        }
        for (Character[] c : specialCharacters) {
            transliteration.put(c[0], c[1]);
        }
    }

    private static String transliterate(final String value) {
        StringBuilder b = new StringBuilder();

        final String localValue = value.toLowerCase();
        for (int i = 0; i < localValue.length(); ++i) {
            char c = localValue.charAt(i);

            if (transliteration.containsKey(c)) {
                b.append(transliteration.get(c));
            }
        }

        return b.toString();
    }

    public static CreateKoodiDataType createCreateKoodiDataType(final Koodi koodiData) {
        final CreateKoodiDataType koodiDataType = new CreateKoodiDataType();
        koodiDataType.setKoodiArvo(koodiData.getKoodiArvo());

        final String koodiNimiFi = StringUtils.defaultIfBlank(koodiData.getKoodiNimiFi(), koodiData.getKoodiArvo());
        if (koodiNimiFi != null) {
            final KoodiMetadataType metadataType = new KoodiMetadataType();
            metadataType.setNimi(koodiNimiFi);
            metadataType.setLyhytNimi(koodiData.getKoodiLyhytNimiFi() != null
                    && koodiData.getKoodiLyhytNimiFi().trim().length() > 0 ? koodiData.getKoodiLyhytNimiFi() : koodiNimiFi);
            metadataType.setKuvaus(koodiData.getKoodiKuvausFi() != null
                    && koodiData.getKoodiKuvausFi().trim().length() > 0 ? koodiData.getKoodiKuvausFi() : koodiNimiFi);
            metadataType.setKieli(KieliType.FI);
            koodiDataType.getMetadata().add(metadataType);
        }

        if (koodiData.getKoodiNimiSv() != null) {
            final KoodiMetadataType metadataType = new KoodiMetadataType();
            metadataType.setNimi(koodiData.getKoodiNimiSv());
            metadataType.setLyhytNimi(koodiData.getKoodiLyhytNimiSv() != null
                    && koodiData.getKoodiLyhytNimiSv().trim().length() > 0 ? koodiData.getKoodiLyhytNimiSv() : koodiData.getKoodiNimiSv());
            metadataType.setKuvaus(koodiData.getKoodiKuvausSv() != null
                    && koodiData.getKoodiKuvausSv().trim().length() > 0 ? koodiData.getKoodiKuvausSv() : koodiData.getKoodiNimiSv());
            metadataType.setKieli(KieliType.SV);
            koodiDataType.getMetadata().add(metadataType);
        }

        if (koodiData.getKoodiNimiEn() != null) {
            final KoodiMetadataType metadataType = new KoodiMetadataType();
            metadataType.setNimi(koodiData.getKoodiNimiEn());
            metadataType.setLyhytNimi(koodiData.getKoodiLyhytNimiEn() != null
                    && koodiData.getKoodiLyhytNimiFi().trim().length() > 0 ? koodiData.getKoodiLyhytNimiEn() : koodiData.getKoodiNimiEn());
            metadataType.setKuvaus(koodiData.getKoodiKuvausEn() != null
                    && koodiData.getKoodiKuvausEn().trim().length() > 0 ? koodiData.getKoodiKuvausEn() : koodiData.getKoodiNimiEn());
            metadataType.setKieli(KieliType.EN);
            koodiDataType.getMetadata().add(metadataType);
        }
        koodiDataType.setVoimassaAlkuPvm(DateHelper.DateToXmlCal(tryGetDate(koodiData.getAlkuPvm())));
        koodiDataType.setVoimassaLoppuPvm(koodiData.getLoppuPvm() != null
                ? DateHelper.DateToXmlCal(tryGetDate(koodiData.getLoppuPvm())) : null);

        return koodiDataType;
    }

    private static Date tryGetDate(final String dateToParse) {
        if (StringUtils.isNotBlank(dateToParse)) {
            if (dateToParse.matches(REGEX_ISO_DATE)) {
                final SimpleDateFormat sdf = new SimpleDateFormat(ISO_DATE_PATTERN);
                try {
                    return sdf.parse(dateToParse);
                } catch (final ParseException e) {
                    LOGGER.warn("Could not parse date [{}]", dateToParse);
                }
            } else {
                final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
                try {
                    return sdf.parse(dateToParse);
                } catch (final ParseException e) {
                    LOGGER.warn("Could not parse date [{}]", dateToParse);
                }

            }
        }
        return new Date();
    }
}
