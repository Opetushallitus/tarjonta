package fi.vm.sade.tarjonta.data.util;

import java.util.Date;

import fi.vm.sade.generic.common.DateHelper;
import fi.vm.sade.koodisto.service.types.CreateKoodiDataType;
import fi.vm.sade.koodisto.service.types.CreateKoodistoDataType;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodistoMetadataType;
import fi.vm.sade.koodisto.service.types.common.TilaType;

public final class DataUtils {

    public static CreateKoodistoDataType createCreateKoodistoDataType(String koodistoUri, String omistaja,
            String organisaatioOid, Date voimassaAlkuPvm, Date voimassaLoppuPvm, String nimi) {
        CreateKoodistoDataType type = new CreateKoodistoDataType();
        type.setKoodistoUri(koodistoUri);
        type.setOmistaja(omistaja);
        type.setOrganisaatioOid(organisaatioOid);
        type.setVoimassaAlkuPvm(voimassaAlkuPvm != null ? DateHelper.DateToXmlCal(voimassaAlkuPvm) : null);
        type.setVoimassaAlkuPvm(voimassaLoppuPvm != null ? DateHelper.DateToXmlCal(voimassaLoppuPvm) : null);

        for (KieliType k : KieliType.values()) {
            KoodistoMetadataType m = new KoodistoMetadataType();
            m.setNimi(nimi);
            m.setKieli(k);
            m.setKuvaus(nimi);
            type.getMetadataList().add(m);
        }

        return type;
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
