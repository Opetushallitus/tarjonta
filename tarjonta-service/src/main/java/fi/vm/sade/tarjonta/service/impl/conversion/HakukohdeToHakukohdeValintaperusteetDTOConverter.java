package fi.vm.sade.tarjonta.service.impl.conversion;

import fi.vm.sade.tarjonta.shared.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.tarjonta.dao.MonikielinenMetadataDAO;
import fi.vm.sade.tarjonta.model.*;
import fi.vm.sade.tarjonta.service.resources.dto.HakukohdeValintaperusteetDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoePisterajaRDTO;
import fi.vm.sade.tarjonta.service.resources.dto.ValintakoeRDTO;
import fi.vm.sade.tarjonta.service.types.ValinnanPisterajaTyyppi;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kjsaila
 * Date: 02/12/13
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class HakukohdeToHakukohdeValintaperusteetDTOConverter extends BaseRDTOConverter<Hakukohde, HakukohdeValintaperusteetDTO> {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(HakukohdeToHakukohdeValintaperusteetDTOConverter.class);

    @Autowired
    private MonikielinenMetadataDAO monikielinenMetadataDAO;

    @Autowired
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;

    @Autowired
    private OrganisaatioService organisaatioService;

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

    private String sanitizeOpetuskieliUri(String koodiUri) {
        return koodiUri.replace(KIELIURI_PREFIX, "").replaceAll(KIELIURI_POSTFIX_REGEX, "");
    }


    @Override
    public HakukohdeValintaperusteetDTO convert(Hakukohde s) {
        HakukohdeValintaperusteetDTO t = new HakukohdeValintaperusteetDTO();

        t.setOid(s.getOid());
        t.setVersion(s.getVersion() != null ? s.getVersion().intValue() : -1);

        // tarjoajaOid, tarjoajaNimi
        for (KoulutusmoduuliToteutus koulutusmoduuliToteutus : s.getKoulutusmoduuliToteutuses()) {
            if (koulutusmoduuliToteutus.getTarjoaja() != null) {
                // Assumes that only one provider for koulutus - is this true?
                String organisaatioOid = koulutusmoduuliToteutus.getTarjoaja();
                t.setTarjoajaOid(organisaatioOid);
                t.setTarjoajaNimi(organisaatioService.getTarjoajaNimiMap(organisaatioOid));
                break;
            }
        }

        BigDecimal nolla = new BigDecimal("0.0");
        // hakukohdeNimi
        t.setHakukohdeNimi(tarjontaKoodistoHelper.getKoodiMetadataNimi(s.getHakukohdeNimi()));
        // Painotetun keskiarvon arvoväli
        t.setPainotettuKeskiarvoHylkaysMax(s.getAlinHyvaksyttavaKeskiarvo() != null ? new BigDecimal(String.valueOf(s.getAlinHyvaksyttavaKeskiarvo())) : nolla);
        t.setPainotettuKeskiarvoHylkaysMin(nolla);

        // Kokonaishylkäyksen arvoväli
        t.setHylkaysMax(s.getAlinValintaPistemaara() != null ? new BigDecimal(String.valueOf(s.getAlinValintaPistemaara())) : nolla);
        t.setHylkaysMin(nolla);

        // Valintakokeiden arvovälit
        t.setLisanayttoMax(nolla);
        t.setLisanayttoMin(nolla);
        t.setLisapisteMax(nolla);
        t.setLisapisteMin(nolla);
        t.setPaasykoeMax(nolla);
        t.setPaasykoeMin(nolla);

        t.setLisanayttoHylkaysMax(nolla);
        t.setLisanayttoHylkaysMin(nolla);
        t.setLisapisteHylkaysMax(nolla);
        t.setLisapisteHylkaysMin(nolla);
        t.setPaasykoeHylkaysMax(nolla);
        t.setPaasykoeHylkaysMin(nolla);

        // Muutetaan yhdistetyt valintakokeet erillisiksi kokeiksi
        Set<Valintakoe> result = new HashSet<Valintakoe>();
        for (Valintakoe koe : s.getValintakoes()) {
            Valintakoe vk = null;
            Valintakoe lt = null;

            Set<Pisteraja> addToBothVKs = new HashSet<Pisteraja>();

            for (Pisteraja pisteraja : koe.getPisterajat()) {

                if (ValinnanPisterajaTyyppi.PAASYKOE.value().equals(pisteraja.getValinnanPisterajaTyyppi())) {
                    vk = (vk == null) ? new Valintakoe() : vk;
                    if (vk.getPisterajat() == null) {
                        vk.setPisterajat(new HashSet<Pisteraja>());
                    }
                    vk.getPisterajat().add(pisteraja);
                } else if (ValinnanPisterajaTyyppi.LISAPISTEET.value().equals(pisteraja.getValinnanPisterajaTyyppi())) {
                    lt = (lt == null) ? new Valintakoe() : lt;
                    if (lt.getPisterajat() == null) {
                        lt.setPisterajat(new HashSet<Pisteraja>());
                    }
                    lt.getPisterajat().add(pisteraja);
                } else {
                    addToBothVKs.add(pisteraja);
                }
            }

            if (vk != null) {
                vk.setKuvaus(koe.getKuvaus());
                vk.setTyyppiUri(PAASY_JA_SOVELTUVUUSKOE);
                vk.getPisterajat().addAll(addToBothVKs);
                result.add(vk);
            }

            if (lt != null) {

                lt.setKuvaus(koe.getLisanaytot());
                if(koe.getTyyppiUri() != null &&
                        (koe.getTyyppiUri().split("#")[0].equals(LISANAYTTO) || koe.getTyyppiUri().split("#")[0].equals(LISAPISTE))) {
                    lt.setTyyppiUri(koe.getTyyppiUri());
                } else {
                    lt.setTyyppiUri(LISANAYTTO);
                }


                lt.getPisterajat().addAll(addToBothVKs);

                result.add(lt);
            }

            // Jos valintakokeella ei ole pisterajoja
            if(lt == null && vk == null) {
                result.add(koe);
            }
        }

        for (Valintakoe koe : result) {
//            if (koe.getTyyppiUri() == null) {
//                for (Pisteraja p : koe.getPisterajat()) {
//                    if (p.getValinnanPisterajaTyyppi().equals(ValinnanPisterajaTyyppi.PAASYKOE.value())) {
//                        koe.setTyyppiUri(PAASY_JA_SOVELTUVUUSKOE);
//                    }
//                    if (p.getValinnanPisterajaTyyppi().equals(ValinnanPisterajaTyyppi.LISAPISTEET.value())) {
//                        koe.setTyyppiUri(LISANAYTTO);
//                    }
//                }
//            }
            if (koe.getTyyppiUri() != null) {
                if (koe.getTyyppiUri().split("#")[0].equals(PAASY_JA_SOVELTUVUUSKOE)) {
                    for (Pisteraja p : koe.getPisterajat()) {
                        if (p.getValinnanPisterajaTyyppi().equals(ValinnanPisterajaTyyppi.PAASYKOE.value())) {
                            t.setPaasykoeMax(p.getYlinPistemaara());
                            t.setPaasykoeMin(p.getAlinPistemaara());
                            t.setPaasykoeHylkaysMax(p.getAlinHyvaksyttyPistemaara());
                        }
                    }
                }
                if (koe.getTyyppiUri().split("#")[0].equals(LISANAYTTO)) {
                    for (Pisteraja p : koe.getPisterajat()) {
                        if (p.getValinnanPisterajaTyyppi().equals(ValinnanPisterajaTyyppi.LISAPISTEET.value())) {
                            t.setLisanayttoMax(p.getYlinPistemaara());
                            t.setLisanayttoMin(p.getAlinPistemaara());
                            t.setLisanayttoHylkaysMax(p.getAlinHyvaksyttyPistemaara());
                        }
                    }
                }
                if (koe.getTyyppiUri().split("#")[0].equals(LISAPISTE)) {
                    for (Pisteraja p : koe.getPisterajat()) {
                        if (p.getValinnanPisterajaTyyppi().equals(ValinnanPisterajaTyyppi.LISAPISTEET.value())) {
                            t.setLisapisteMax(p.getYlinPistemaara());
                            t.setLisapisteMin(p.getAlinPistemaara());
                            t.setLisapisteHylkaysMax(p.getAlinHyvaksyttyPistemaara());
                        }
                    }
                }
            }

        }

        // Alimman valintapistemäärän asettaminen ei ole pakollista, jos kohteella on vain pääsykoe
        if (t.getHylkaysMax().equals(nolla) && !t.getPaasykoeHylkaysMax().equals(nolla)) {
            t.setHylkaysMax(t.getPaasykoeHylkaysMax());
        }

        t.setValintakokeet(convertValintakokeet(result));


        t.setHakuOid(s.getHaku() != null ? s.getHaku().getOid() : null);

        t.setHakukohdeNimiUri(s.getHakukohdeNimi());
        t.setTila(s.getTila() != null ? s.getTila().name() : null);

        t.setModified(s.getLastUpdateDate());
        t.setModifiedBy(s.getLastUpdatedByOid());

        t.setValintojenAloituspaikatLkm(s.getValintojenAloituspaikatLkm());


        // Opetuskielet
        Set<String> opetuskielis = new HashSet<String>();
        for (KoulutusmoduuliToteutus koulutusmoduuliToteutus : s.getKoulutusmoduuliToteutuses()) {
            for (KoodistoUri koodistoUri : koulutusmoduuliToteutus.getOpetuskielis()) {
                opetuskielis.add(sanitizeOpetuskieliUri(koodistoUri.getKoodiUri()));
            }
        }
        t.setOpetuskielet(new ArrayList<String>(opetuskielis));

        t.setPainokertoimet(convertPainotettavatOppianeet(s.getPainotettavatOppiaineet()));

        Haku haku = s.getHaku();
        if (haku != null) {
            t.setHakuVuosi(haku.getHakukausiVuosi());
            t.setHakuKausi(tarjontaKoodistoHelper.getKoodiMetadataNimi(haku.getHakukausiUri()));
        } else {
            t.setHakuVuosi(-1);
            t.setHakuKausi(null);
        }

        return t;
    }

    private List<ValintakoeRDTO> convertValintakokeet(Set<Valintakoe> valintakoes) {
        List<ValintakoeRDTO> result = new ArrayList<ValintakoeRDTO>();

        for (Valintakoe valintakoe : valintakoes) {
            result.add(getConversionService().convert(valintakoe, ValintakoeRDTO.class));
        }

        return result.isEmpty() ? null : result;
    }


    /**
     * Convert PainotettavaOppiaine to list of [ [ "oppiaine", "9.7"], ... ]
     *
     * @param s
     * @return
     */
    private Map<String, String> convertPainotettavatOppianeet(Set<PainotettavaOppiaine> s) {
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

    private Map<String, String> defaultPainotukset() {
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
