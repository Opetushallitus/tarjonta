package fi.vm.sade.tarjonta.shared.types;

import java.util.HashSet;
import java.util.Set;

public enum ToteutustyyppiEnum {

    AMMATILLINEN_PERUSTUTKINTO("koulutustyyppi_1"),
    LUKIOKOULUTUS("koulutustyyppi_2"),
    KORKEAKOULUTUS("koulutustyyppi_3"),
    AMMATILLINEN_PERUSKOULUTUS_ERITYISOPETUKSENA("koulutustyyppi_4"),
    VALMENTAVA_JA_KUNTOUTTAVA_OPETUS_JA_OHJAUS("koulutustyyppi_5"),
    PERUSOPETUKSEN_LISAOPETUS("koulutustyyppi_6"),
    KORKEAKOULUOPINTO("koulutustyyppi_3"),  // opintokokonaisuus or opintojakso
    AMMATILLISEEN_PERUSKOULUTUKSEEN_OHJAAVA_JA_VALMISTAVA_KOULUTUS("koulutustyyppi_7"),
    AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA("koulutustyyppi_18"),
    AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMENTAVA_ER("koulutustyyppi_19"),
    MAAHANMUUTTAJIEN_AMMATILLISEEN_PERUSKOULUTUKSEEN_VALMISTAVA_KOULUTUS("koulutustyyppi_8"),
    MAAHANMUUTTAJIEN_JA_VIERASKIELISTEN_LUKIOKOULUTUKSEEN_VALMISTAVA_KOULUTUS("koulutustyyppi_9"),
    VAPAAN_SIVISTYSTYON_KOULUTUS("koulutustyyppi_10"),
    AMMATTITUTKINTO("koulutustyyppi_11"),
    ERIKOISAMMATTITUTKINTO("koulutustyyppi_12"),
    AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA("koulutustyyppi_13"),
    LUKIOKOULUTUS_AIKUISTEN_OPPIMAARA("koulutustyyppi_14"),
    EB_RP_ISH("koulutustyyppi_21"),
    ESIOPETUS("koulutustyyppi_15"),
    PERUSOPETUS("koulutustyyppi_16"),
    AIKUISTEN_PERUSOPETUS("koulutustyyppi_17"),
    ERIKOISAMMATTITUTKINTO_VALMISTAVA(null),
    AMMATTITUTKINTO_VALMISTAVA(null),
    AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA_VALMISTAVA(null);

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

    public static boolean isValmistavaToteutustyyppi(ToteutustyyppiEnum tyyppi) {
        return getValmistavatToteutustyypit().contains(tyyppi);
    }

    public static Set<ToteutustyyppiEnum> getValmistavatToteutustyypit() {
        Set<ToteutustyyppiEnum> valmistavatToteutustyypit = new HashSet<>();

        valmistavatToteutustyypit.add(ERIKOISAMMATTITUTKINTO_VALMISTAVA);
        valmistavatToteutustyypit.add(AMMATTITUTKINTO_VALMISTAVA);
        valmistavatToteutustyypit.add(AMMATILLINEN_PERUSTUTKINTO_NAYTTOTUTKINTONA_VALMISTAVA);

        return valmistavatToteutustyypit;
    }

}
