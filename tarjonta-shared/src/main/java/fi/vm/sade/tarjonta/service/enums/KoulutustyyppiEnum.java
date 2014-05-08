package fi.vm.sade.tarjonta.service.enums;

import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;

public enum KoulutustyyppiEnum {

    AMMATILLINEN_PERUSKOULUTUS(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS),
    LUKIOKOULUTUS(KoulutusasteTyyppi.LUKIOKOULUTUS),
    KORKEAKOULUTUS(KoulutusasteTyyppi.KORKEAKOULUTUS),
    AMMATTIKORKEAKOULUTUS(KoulutusasteTyyppi.AMMATTIKORKEAKOULUTUS),
    YLIOPISTOKOULUTUS(KoulutusasteTyyppi.YLIOPISTOKOULUTUS),
    PERUSOPETUKSEN_LISAOPETUS(KoulutusasteTyyppi.PERUSOPETUKSEN_LISAOPETUS),
    VALMENTAVA_JA_KUNTOUTTAVA_OPETUS(KoulutusasteTyyppi.VALMENTAVA_JA_KUNTOUTTAVA_OPETUS),
    AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS(KoulutusasteTyyppi.AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS),
    MAAHANM_LUKIO_VALMISTAVA_KOULUTUS(KoulutusasteTyyppi.MAAHANM_LUKIO_VALMISTAVA_KOULUTUS),
    MAAHANM_AMM_VALMISTAVA_KOULUTUS(KoulutusasteTyyppi.MAAHANM_AMM_VALMISTAVA_KOULUTUS),
    VAPAAN_SIVISTYSTYON_KOULUTUS(KoulutusasteTyyppi.VAPAAN_SIVISTYSTYON_KOULUTUS);

    final private KoulutusasteTyyppi koulutusasteTyyppi;

    private KoulutustyyppiEnum(KoulutusasteTyyppi koulutusasteTyyppi) {
        this.koulutusasteTyyppi = koulutusasteTyyppi;
    }

    /**
     * @return the koulutusasteTyyppi
     */
    public KoulutusasteTyyppi getKoulutusasteTyyppi() {
        return koulutusasteTyyppi;
    }

    public static KoulutustyyppiEnum fromEnum(KoulutusasteTyyppi v) {
        for (KoulutustyyppiEnum c : KoulutustyyppiEnum.values()) {
            if (c.koulutusasteTyyppi.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v != null ? v.name() : "KoulutusasteTyyppi enum was null.");
    }
}
