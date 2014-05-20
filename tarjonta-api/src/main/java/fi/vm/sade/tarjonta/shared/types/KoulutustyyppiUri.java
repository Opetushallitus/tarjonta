package fi.vm.sade.tarjonta.shared.types;

public enum KoulutustyyppiUri {

    VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS("koulutustyyppi_5"),
    ERIKOISAMMATTITUTKINTO("koulutustyyppi_12"),
    VAPAAN_SIVISTYSTYON_KOULUTUS("koulutustyyppi_10"),
    AMMATTITUTKINTO("koulutustyyppi_11"),
    LUKIOKOULUTUS("koulutustyyppi_2"),
    AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA("koulutustyyppi_13"),
    LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA("koulutustyyppi_14"),
    AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS("koulutustyyppi_7"),
    AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA("koulutustyyppi_4"),
    AMMATILLINEN_PERUSTUTKINTO("koulutustyyppi_1"),
    MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS("koulutustyyppi_8"),
    KORKEAKOULUTUS("koulutustyyppi_3"),
    MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS("koulutustyyppi_9"),
    PERUSOPETUKSEN_LISAOPETUS("koulutustyyppi_6");

    final private String koulutustyyppiUri;

    private KoulutustyyppiUri(String koulutustyyppiUri) {
        this.koulutustyyppiUri = koulutustyyppiUri;
    }

    public String uri() {
        return this.koulutustyyppiUri;
    }

    public static KoulutustyyppiUri fromString(String v) {
        for (KoulutustyyppiUri c : KoulutustyyppiUri.values()) {
            if (c.koulutustyyppiUri.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Koulutustyyppi URI not found. URI : " + v);
    }
}
