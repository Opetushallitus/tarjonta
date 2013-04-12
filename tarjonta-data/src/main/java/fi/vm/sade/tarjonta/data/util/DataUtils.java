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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

        String localNimi = koodistoNimi.toUpperCase();
        localNimi = localNimi.replaceAll("\\s", "");
        localNimi = localNimi.replace('Ý', 'Y');
        localNimi = localNimi.replaceAll("Ù|Ú|Û|Ü", "U");
        localNimi = localNimi.replaceAll("Ò|Ó|Ô|Õ|Ö", "O");
        localNimi = localNimi.replaceAll("Ì|Í|Î|Ï", "I");
        localNimi = localNimi.replaceAll("È|É|Ê|Ë", "E");
        localNimi = localNimi.replace('Ç', 'C');
        localNimi = localNimi.replaceAll("À|Á|Â|Ã|Ä|Æ", "A");
        localNimi = localNimi.replaceAll("Å", "O");
        localNimi = localNimi.replaceAll("_", "");
        localNimi = localNimi.replaceAll("-", "");

        return localNimi.toLowerCase();
    }

    public static CreateKoodiDataType createCreateKoodiDataType(final Koodi koodiData) {
        final CreateKoodiDataType koodiDataType = new CreateKoodiDataType();
        koodiDataType.setKoodiArvo(koodiData.getKoodiArvo());

        if (koodiData.getKoodiNimiFi() != null) {
            final KoodiMetadataType metadataType = new KoodiMetadataType();
            metadataType.setNimi(koodiData.getKoodiNimiFi());
            metadataType.setLyhytNimi(koodiData.getKoodiLyhytNimiFi() != null
                    && koodiData.getKoodiLyhytNimiFi().trim().length() > 0 ? koodiData.getKoodiLyhytNimiFi() : koodiData.getKoodiNimiFi());
            metadataType.setKuvaus(koodiData.getKoodiKuvausFi() != null
                    && koodiData.getKoodiKuvausFi().trim().length() > 0 ? koodiData.getKoodiKuvausFi() : koodiData.getKoodiNimiFi());
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
