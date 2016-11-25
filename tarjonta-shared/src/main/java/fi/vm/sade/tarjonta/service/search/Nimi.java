package fi.vm.sade.tarjonta.service.search;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Kielistetty nimi, sallitut localet: fi, en, sv
 */
public class Nimi extends HashMap<String, String> {

    public static String FI = "fi";
    public static String SV = "sv";
    public static String EN = "en";

    private List<String> locales = Arrays.asList(FI, SV, EN);
    private boolean limitLocales;

    public Nimi() {
        limitLocales = true;
    }

    private static final long serialVersionUID = 1L;

    /**
     * Asettaa nimen jos se ei ole null
     * 
     * @param locale
     *            (kieli_fi, kieli_sv, kieli_en)
     * @param value
     */
    @Override
    public String put(String locale, String value) {
        if (value != null) {
            return super.put(locale, value);
        } else {
            throw new IllegalArgumentException("null arvo ei ole sallittu!");
        }
    }

    public String get(String locale) {
        return super.get(locale);
    }
}
