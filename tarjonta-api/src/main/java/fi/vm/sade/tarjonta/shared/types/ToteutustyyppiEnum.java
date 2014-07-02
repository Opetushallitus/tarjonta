package fi.vm.sade.tarjonta.shared.types;

public enum ToteutustyyppiEnum {

    VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS("koulutustyyppi_5"),
    ERIKOISAMMATTITUTKINTO("koulutustyyppi_12"),
    ERIKOISAMMATTITUTKINTO_VALMISTAVA(null),
    VAPAAN_SIVISTYSTYON_KOULUTUS("koulutustyyppi_10"),
    AMMATTITUTKINTO("koulutustyyppi_11"),
    AMMATTITUTKINTO_VALMISTAVA(null),
    LUKIOKOULUTUS("koulutustyyppi_2"),
    AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA("koulutustyyppi_13"),
    AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA_VALMISTAVA(null),
    LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA("koulutustyyppi_14"),
    AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS("koulutustyyppi_7"),
    AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA("koulutustyyppi_4"),
    AMMATILLINEN_PERUSTUTKINTO("koulutustyyppi_1"),
    MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS("koulutustyyppi_8"),
    KORKEAKOULUTUS("koulutustyyppi_3"),
    MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS("koulutustyyppi_9"),
    PERUSOPETUKSEN_LISAOPETUS("koulutustyyppi_6");

    final private String koulutustyyppiUri;

    private ToteutustyyppiEnum(String koulutustyyppiUri) {
        this.koulutustyyppiUri = koulutustyyppiUri;
    }

    public String uri() {
        return this.koulutustyyppiUri;
    }

    /**
     * Convert string name or value to enumeration.
     *
     * @param strValue
     * @return
     */
    public static ToteutustyyppiEnum fromString(String strValue) {
        for (ToteutustyyppiEnum e : ToteutustyyppiEnum.values()) {
            if (e.koulutustyyppiUri != null && (e.koulutustyyppiUri.equals(strValue) || e.name().equals(strValue))) {
                return e;
            }
        }
        throw new IllegalArgumentException("Toteutustyyppi enum not found by value : '" + strValue + "'");
    }

    public static ToteutustyyppiEnum convertToValmistava(ToteutustyyppiEnum toteutustyyppi) {
        if (toteutustyyppi != null) {
            for (ToteutustyyppiEnum e : ToteutustyyppiEnum.values()) {
                if (e.koulutustyyppiUri == null && e.name().contains(toteutustyyppi.name())) {
                    return e;
                }
            }
        }
        throw new IllegalArgumentException("Valmistava toteutustyyppi enum not found by : '" + toteutustyyppi + "'");
    }

}
