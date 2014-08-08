package fi.vm.sade.tarjonta.service.impl.resources.v1.util;

import fi.vm.sade.tarjonta.model.PainotettavaOppiaine;
import fi.vm.sade.tarjonta.model.Valintakoe;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeRDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by kjsaila on 06/08/14.
 */
public class ValintaperusteetUtil {
    public static final String PAINOKERROIN_POSTFIX = "_painokerroin";

    public static final String AIDINKIELI_JA_KIRJALLISUUS1 = "AI";
    public static final String AIDINKIELI_JA_KIRJALLISUUS2 = "AI2";

    // A1-kieliä voi olla kolme
    public static final String A11KIELI = "A1";
    public static final String A12KIELI = "A12";
    public static final String A13KIELI = "A13";

    // A2-kieliä voi olla kolme
    public static final String A21KIELI = "A2";
    public static final String A22KIELI = "A22";
    public static final String A23KIELI = "A23";

    // B1-kieliä voi olla yksi
    public static final String B1KIELI = "B1";

    // B2-kieliä voi olla kolme
    public static final String B21KIELI = "B2";
    public static final String B22KIELI = "B22";
    public static final String B23KIELI = "B23";

    // B3-kieliä voi olla kolme
    public static final String B31KIELI = "B3";
    public static final String B32KIELI = "B32";
    public static final String B33KIELI = "B33";

    public static final String USKONTO = "KT";
    public static final String HISTORIA = "HI";
    public static final String YHTEISKUNTAOPPI = "YH";
    public static final String MATEMATIIKKA = "MA";
    public static final String FYSIIKKA = "FY";
    public static final String KEMIA = "KE";
    public static final String BIOLOGIA = "BI";
    public static final String TERVEYSTIETO = "TE";
    public static final String MAANTIETO = "GE";

    public static final String LIIKUNTA = "LI";
    public static final String KASITYO = "KS";
    public static final String KOTITALOUS = "KO";
    public static final String MUSIIKKI = "MU";
    public static final String KUVATAIDE = "KU";

    public static final String SAKSA = "DE";
    public static final String KREIKKA = "EL";
    public static final String ENGLANTI = "EN";
    public static final String ESPANJA = "ES";
    public static final String EESTI = "ET";
    public static final String SUOMI = "FI";
    public static final String RANSKA = "FR";
    public static final String ITALIA = "IT";
    public static final String JAPANI = "JA";
    public static final String LATINA = "LA";
    public static final String LIETTUA = "LT";
    public static final String LATVIA = "LV";
    public static final String PORTUGALI = "PT";
    public static final String VENAJA = "RU";
    public static final String SAAME = "SE";
    public static final String RUOTSI = "SV";
    public static final String VIITTOMAKIELI = "VK";
    public static final String KIINA = "ZH";


    public static final String[] LUKUAINEET = {
            AIDINKIELI_JA_KIRJALLISUUS1,
            AIDINKIELI_JA_KIRJALLISUUS2,
            USKONTO,
            HISTORIA,
            YHTEISKUNTAOPPI,
            MATEMATIIKKA,
            FYSIIKKA,
            KEMIA,
            BIOLOGIA,
            TERVEYSTIETO,
            MAANTIETO
    };

    public static final String[] KIELET = {
            A11KIELI,
            A12KIELI,
            A13KIELI,
            A21KIELI,
            A22KIELI,
            A23KIELI,
            B1KIELI,
            B21KIELI,
            B22KIELI,
            B23KIELI,
            B31KIELI,
            B32KIELI,
            B33KIELI
    };

    public static final String[] KIELIKOODIT = {
            SAKSA,
            KREIKKA,
            ENGLANTI,
            ESPANJA,
            EESTI,
            SUOMI,
            RANSKA,
            ITALIA,
            JAPANI,
            LATINA,
            LIETTUA,
            LATVIA,
            PORTUGALI,
            VENAJA,
            SAAME,
            RUOTSI,
            VIITTOMAKIELI,
            KIINA
    };

    public static final String[] TAITO_JA_TAIDEAINEET = {
            LIIKUNTA,
            KASITYO,
            KOTITALOUS,
            MUSIIKKI,
            KUVATAIDE
    };

    public static final String PAASY_JA_SOVELTUVUUSKOE = "valintakokeentyyppi_1";
    public static final String LISANAYTTO = "valintakokeentyyppi_2";
    public static final String LISAPISTE = "valintakokeentyyppi_5";

    public static final String KIELIURI_PREFIX = "kieli_";
    public static final String KIELIURI_POSTFIX_REGEX = "#[0-9]*";

    public static String sanitizeOpetuskieliUri(String koodiUri) {
        return koodiUri.replace(KIELIURI_PREFIX, "").replaceAll(KIELIURI_POSTFIX_REGEX, "");
    }

    public static Map<String, String> convertPainotettavatOppianeet(Set<PainotettavaOppiaine> s) {
        Map<String, String> result = defaultPainotukset();

        for (PainotettavaOppiaine painotettavaOppiaine : s) {
            String koodijaversio = painotettavaOppiaine.getOppiaine().split("_")[1];
            String koodijanimi = koodijaversio.split("#")[0];
            String nimi = koodijanimi.substring(0, 2).toUpperCase();
            String koodi = "";

            if (koodijanimi.length() > 2) {
                koodi = "_" + koodijanimi.substring(2, 4).toUpperCase();
            }
            String avain = nimi + koodi + PAINOKERROIN_POSTFIX;
            result.put(avain, String.valueOf(painotettavaOppiaine.getPainokerroin()));
            if (nimi.equals(A11KIELI) || nimi.equals(A21KIELI) || nimi.equals(B21KIELI) || nimi.equals(B31KIELI)) {
                String varaavain = nimi + "2" + koodi + PAINOKERROIN_POSTFIX;
                result.put(varaavain, String.valueOf(painotettavaOppiaine.getPainokerroin()));
                varaavain = nimi + "3" + koodi + PAINOKERROIN_POSTFIX;
                result.put(varaavain, String.valueOf(painotettavaOppiaine.getPainokerroin()));
            }


        }

        return result;
    }

    public static Map<String, String> defaultPainotukset() {
        Map<String, String> result = new HashMap<String, String>();
        for (String aine : LUKUAINEET) {
            result.put(aine + PAINOKERROIN_POSTFIX, "1.0");
        }
        for (String aine : TAITO_JA_TAIDEAINEET) {
            result.put(aine + PAINOKERROIN_POSTFIX, "0.0");
        }
        for (String kieli : KIELET) {
            for (String kielikoodi : KIELIKOODIT) {
                result.put(kieli + "_" + kielikoodi + PAINOKERROIN_POSTFIX, "1.0");
            }

        }
        return result;
    }

}
