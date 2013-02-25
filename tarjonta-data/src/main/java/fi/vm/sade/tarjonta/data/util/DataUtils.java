package fi.vm.sade.tarjonta.data.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fi.vm.sade.generic.common.DateHelper;
import fi.vm.sade.koodisto.service.types.CreateKoodiDataType;
import fi.vm.sade.koodisto.service.types.CreateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodistoMetadataType;
import fi.vm.sade.koodisto.service.types.common.TilaType;
import fi.vm.sade.tarjonta.data.CommonKoodiData;
import fi.vm.sade.tarjonta.data.dto.Koodi;

public final class DataUtils {

    private static final String DATE_PATTERN = "dd.MM.yyyy";

    public static CreateKoodistoDataType createCreateKoodistoDataType(String koodistoUri, String omistaja,
            String organisaatioOid, Date voimassaAlkuPvm, Date voimassaLoppuPvm, String nimi) {
        CreateKoodistoDataType type = new CreateKoodistoDataType();
        type.setKoodistoUri(koodistoUri);
        type.setOmistaja(omistaja);
        type.setOrganisaatioOid(organisaatioOid);
        type.setVoimassaAlkuPvm(voimassaAlkuPvm != null ? DateHelper.DateToXmlCal(voimassaAlkuPvm) : null);
        type.setVoimassaLoppuPvm(voimassaLoppuPvm != null ? DateHelper.DateToXmlCal(voimassaLoppuPvm) : null);

        for (KieliType k : KieliType.values()) {
            KoodistoMetadataType m = new KoodistoMetadataType();
            m.setNimi(nimi);
            m.setKieli(k);
            m.setKuvaus(nimi);
            type.getMetadataList().add(m);
        }

        return type;
    }

    public static String createKoodiUriFromName(String koodistoNimi) {
        koodistoNimi =  koodistoNimi.replaceAll("\\s","");
        koodistoNimi = koodistoNimi.toUpperCase().replace('Ý', 'Y');
        koodistoNimi = koodistoNimi.toUpperCase().replaceAll("Ù | Ú | Û | Ü", "U");
        koodistoNimi = koodistoNimi.toUpperCase().replaceAll("Ò | Ó | Ô | Õ | Ö", "O");
        koodistoNimi = koodistoNimi.toUpperCase().replaceAll("Ì | Í | Î | Ï", "I");
        koodistoNimi = koodistoNimi.toUpperCase().replaceAll("È | É | Ê | Ë", "E");
        koodistoNimi = koodistoNimi.toUpperCase().replace('Ç', 'C');
        koodistoNimi = koodistoNimi.toUpperCase().replaceAll("À | Á | Â | Ã | Ä | Å | Æ", "A");
        return koodistoNimi;
    }

    public static CreateKoodiDataType createCreateKoodiDataType(Koodi koodiData, TilaType tila) {
        CreateKoodiDataType koodiDataType = new CreateKoodiDataType();
        koodiDataType.setKoodiArvo(koodiData.getKoodiArvo());
        koodiDataType.setKoodiUri(createKoodiUriFromName(koodiData.getKoodiNimiFi()));
        koodiDataType.setTila(tila);

        if (koodiData.getKoodiNimiFi() != null) {
            KoodiMetadataType metadataType = new KoodiMetadataType();
            metadataType.setNimi(koodiData.getKoodiNimiFi());
            metadataType.setLyhytNimi(koodiData.getKoodiLyhytNimiFi() != null ? koodiData.getKoodiLyhytNimiFi() : "");
            metadataType.setKuvaus(koodiData.getKoodiKuvausFi() != null ? koodiData.getKoodiKuvausFi() : "");
            metadataType.setKieli(KieliType.FI);
            koodiDataType.getMetadata().add(metadataType);
        }

        if (koodiData.getKoodiNimiSv() != null) {
            KoodiMetadataType metadataType = new KoodiMetadataType();
            metadataType.setNimi(koodiData.getKoodiNimiSv());
            metadataType.setLyhytNimi(koodiData.getKoodiLyhytNimiSv() != null ? koodiData.getKoodiLyhytNimiSv() : "");
            metadataType.setKuvaus(koodiData.getKoodiKuvausSv() != null ? koodiData.getKoodiKuvausSv() : "");
            metadataType.setKieli(KieliType.SV);
            koodiDataType.getMetadata().add(metadataType);
        }

        if (koodiData.getKoodiNimiEn() != null) {
            KoodiMetadataType metadataType = new KoodiMetadataType();
            metadataType.setNimi(koodiData.getKoodiNimiEn());
            metadataType.setLyhytNimi(koodiData.getKoodiLyhytNimiEn() != null ? koodiData.getKoodiLyhytNimiEn() : "");
            metadataType.setKuvaus(koodiData.getKoodiKuvausEn() != null ? koodiData.getKoodiKuvausEn() : "");
            metadataType.setKieli(KieliType.EN);
            koodiDataType.getMetadata().add(metadataType);
        }
        koodiDataType.setVoimassaAlkuPvm(koodiData.getAlkuPvm() != null ? DateHelper.DateToXmlCal(tryGetDate(koodiData.getAlkuPvm())) : DateHelper.DateToXmlCal(new Date()));
        koodiDataType.setVoimassaLoppuPvm(koodiData.getLoppuPvm() != null ? DateHelper.DateToXmlCal(tryGetDate(koodiData.getLoppuPvm())): null);

        return koodiDataType;
    }

    private static Date tryGetDate(String dateToParse) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        try {
            return sdf.parse(dateToParse);
        } catch (ParseException e) {
            return null;
        }
    }

    public static CreateKoodiDataType createCreateKoodiDataType(String koodiUri, String koodiArvo, TilaType tila,
            Date voimassaAlkuPvm, Date voimassaLoppuPvm, String nimi) {
        CreateKoodiDataType koodiDataType = new CreateKoodiDataType();
        koodiDataType.setKoodiArvo(koodiArvo);
        koodiDataType.setKoodiUri(koodiUri);
        koodiDataType.setTila(tila);
        koodiDataType.setVoimassaAlkuPvm(voimassaAlkuPvm != null ? DateHelper.DateToXmlCal(voimassaAlkuPvm) : null);
        koodiDataType.setVoimassaLoppuPvm(voimassaLoppuPvm != null ? DateHelper.DateToXmlCal(voimassaLoppuPvm) : null);
        for (KieliType k : KieliType.values()) {
            KoodiMetadataType metadataType = new KoodiMetadataType();
            metadataType.setNimi(nimi);
            metadataType.setLyhytNimi(nimi);
            metadataType.setKuvaus(nimi);
            metadataType.setKieli(k);
            koodiDataType.getMetadata().add(metadataType);
        }

        return koodiDataType;
    }
}
