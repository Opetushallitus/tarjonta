package fi.vm.sade.tarjonta.shared.types;

import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;

/*
 * TODO: remove the KoulutusasteTyyppi xml type from all APIs as soon as you can.
 */
public enum ModuulityyppiEnum {

    AMMATILLINEN_PERUSKOULUTUS(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS,
            //subtypes of ammatillinen komo
            ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA,
            ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO,
            ToteutustyyppiEnum.AMMATILLINEN_PERUSTUTKINTO_ALK_2018,
            ToteutustyyppiEnum.AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA),

    LUKIOKOULUTUS(KoulutusasteTyyppi.LUKIOKOULUTUS,
            //subtypes of lukio komo
            ToteutustyyppiEnum.LUKIOKOULUTUS,
            ToteutustyyppiEnum.LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA
    ),
    KORKEAKOULUTUS(KoulutusasteTyyppi.KORKEAKOULUTUS,
            //only one subtype for kk
            ToteutustyyppiEnum.KORKEAKOULUTUS
    ),
    PERUSOPETUKSEN_LISAOPETUS(KoulutusasteTyyppi.PERUSOPETUKSEN_LISAOPETUS),
    VALMENTAVA_JA_KUNTOUTTAVA_OPETUS(KoulutusasteTyyppi.VALMENTAVA_JA_KUNTOUTTAVA_OPETUS),
    AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS(KoulutusasteTyyppi.AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS),
    MAAHANM_LUKIO_VALMISTAVA_KOULUTUS(KoulutusasteTyyppi.MAAHANM_LUKIO_VALMISTAVA_KOULUTUS),
    MAAHANM_AMM_VALMISTAVA_KOULUTUS(KoulutusasteTyyppi.MAAHANM_AMM_VALMISTAVA_KOULUTUS),
    VAPAAN_SIVISTYSTYON_KOULUTUS(KoulutusasteTyyppi.VAPAAN_SIVISTYSTYON_KOULUTUS),
    PERUSOPETUS(KoulutusasteTyyppi.PERUSOPETUS),
    PERUSOPETUS_ULKOMAINEN(KoulutusasteTyyppi.PERUSOPETUS_ULKOMAINEN),
    AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA(KoulutusasteTyyppi.AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA),
    ERIKOISAMMATTITUTKINTO(KoulutusasteTyyppi.ERIKOISAMMATTITUTKINTO),
    AMMATTITUTKINTO(KoulutusasteTyyppi.AMMATTITUTKINTO),
    TUNTEMATON(KoulutusasteTyyppi.TUNTEMATON); //generic unknown type for missing data

    //Mostly deprecated API type, please remove it from alla APIs
    final private KoulutusasteTyyppi koulutusasteTyyppi;

    //Detailed education types of the ModuulityyppiEnum
    final private ToteutustyyppiEnum[] subKoulutustyyppiEnums;

    private ModuulityyppiEnum(KoulutusasteTyyppi koulutusasteTyyppi, ToteutustyyppiEnum... subKoulutustyyppiEnums) {
        this.koulutusasteTyyppi = koulutusasteTyyppi;
        this.subKoulutustyyppiEnums = subKoulutustyyppiEnums;
    }

    /**
     * @return the koulutusasteTyyppi
     */
    public KoulutusasteTyyppi getKoulutusasteTyyppi() {
        return koulutusasteTyyppi;
    }

    public static ModuulityyppiEnum fromEnum(KoulutusasteTyyppi v) {
        for (ModuulityyppiEnum c : ModuulityyppiEnum.values()) {
            if (c.koulutusasteTyyppi.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v != null ? v.name() : "KoulutusasteTyyppi enum was null.");
    }

    /**
     * Search base 'koulutustyyppi' enum by detailed education type enum. When
     * an array of ToteutustyyppiEnums is null, sub type is not yet implemented!
     *
     * @param subTypeEnum
     * @return
     */
    public static ModuulityyppiEnum fromEnum(ToteutustyyppiEnum subTypeEnum) {
        for (ModuulityyppiEnum c : ModuulityyppiEnum.values()) {
            if (c.subKoulutustyyppiEnums != null) {
                for (ToteutustyyppiEnum e : c.subKoulutustyyppiEnums) {
                    if (e.equals(subTypeEnum)) {
                        return c;
                    }
                }
            }
        }
        throw new IllegalArgumentException(subTypeEnum != null ? subTypeEnum.name() : "SubKoulutustyyppi not found.");
    }
}
