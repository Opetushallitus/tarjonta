package fi.vm.sade.tarjonta.data.tarjontauploader;

import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.*;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.*;
import fi.vm.sade.organisaatio.api.model.types.OsoiteTyyppi;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.types.*;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

@Service
public class TarjontaHandler {
    private final Logger logger = LoggerFactory.getLogger(TarjontaHandler.class);

    private static final String OID_PREFIX = "1.2.246.562";
    private static final String OID_TEKN_5 = "5";

    private TarjontaPublicService tarjontaPublicService;
    private TarjontaAdminService tarjontaAdminService;
    private KoodiService koodiService;
    private OrganisaatioService organisaatioService;

    @Autowired
    public TarjontaHandler(final TarjontaPublicService tarjontaPublicService, final TarjontaAdminService tarjontaAdminService,
                           final KoodiService koodiService, final OrganisaatioService organisaatioService) {
        this.tarjontaPublicService = tarjontaPublicService;
        this.tarjontaAdminService = tarjontaAdminService;
        this.koodiService = koodiService;
        this.organisaatioService = organisaatioService;
    }

    public void addKoulutus(final Koulutus koulutus, final String koulutusastetyyppi) {
        logger.info("Lisätään koulutus");

        final LisaaKoulutusTyyppi koulutusTyyppi = getLisaaKoulutusTyyppi(koulutus, koulutusastetyyppi);
        tarjontaAdminService.lisaaKoulutus(koulutusTyyppi);

        tarjontaAdminService.lisaaTaiPoistaKoulutuksiaHakukohteelle(getLisaaKoulutusHakukohteelleTyyppi(koulutus, koulutusTyyppi.getOid()));
    }

    private LisaaKoulutusHakukohteelleTyyppi getLisaaKoulutusHakukohteelleTyyppi(final Koulutus koulutus, final String koulutusOid) {
        final LisaaKoulutusHakukohteelleTyyppi lisaaKoulutusHakukohteelle = new LisaaKoulutusHakukohteelleTyyppi();
        lisaaKoulutusHakukohteelle.getKoulutusOids().add(koulutusOid);
        final String hakukohdeOid = getHakukohdeOid(koulutus.getOppilaitosnumero(), koulutus.getToimipisteJno(),
                koulutus.getHakukohdekoodi(), koulutus.getYhkoodi());
        lisaaKoulutusHakukohteelle.setHakukohdeOid(hakukohdeOid);
        lisaaKoulutusHakukohteelle.setLisaa(true);
        logger.info("Lisätään koulutus [{}] hakukohteelle [{}]", koulutusOid, hakukohdeOid);
        return lisaaKoulutusHakukohteelle;
    }

    public void addHakukohde(final Hakukohde hakukohde, final String hakuOid) {
        logger.info("Lisätään hakukohde");

        final HakukohdeTyyppi hakukohdeTyyppi = getHakukohdeTyyppi(hakukohde, hakuOid);
        tarjontaAdminService.lisaaHakukohde(hakukohdeTyyppi);

        if (StringUtils.equalsIgnoreCase(hakukohde.getValintakoe(), "T")) {
            tarjontaAdminService.tallennaValintakokeitaHakukohteelle(hakukohdeTyyppi.getOid(), Collections.singletonList(getEmptyValintakoeTyyppi()));
        }
    }

    private LisaaKoulutusTyyppi getLisaaKoulutusTyyppi(final Koulutus koulutus, final String koulutusastetyyppi) {
        final LisaaKoulutusTyyppi lisaaKoulutusTyyppi = new LisaaKoulutusTyyppi();

        // koulutusastetyyppi
        if (StringUtils.isBlank(koulutusastetyyppi)) {
            throw new IllegalArgumentException("koulutusastetyyppi on tyhjä");
        }
        lisaaKoulutusTyyppi.setKoulutustyyppi(KoulutusasteTyyppi.valueOf(koulutusastetyyppi.toUpperCase()));

        // pohjakoulutusvaatimus
        if (StringUtils.isBlank(koulutus.getPohjakoulutusvaatimus())) {
            throw new IllegalArgumentException("pohjakoulutusvaatimus on tyhjä");
        }
        final KoodistoKoodiTyyppi pohjakoulutusvaatimus = new KoodistoKoodiTyyppi();
        pohjakoulutusvaatimus.setArvo(koulutus.getPohjakoulutusvaatimus());
        pohjakoulutusvaatimus.setUri(String.format("pohjakoulutusvaatimustoinenaste_%s#1", koulutus.getPohjakoulutusvaatimus().toLowerCase().trim()));
        pohjakoulutusvaatimus.setVersio(1);
        lisaaKoulutusTyyppi.setPohjakoulutusvaatimus(pohjakoulutusvaatimus);

        // koulutuslaji
        if (StringUtils.isBlank(koulutus.getKoulutuslaji())) {
            throw new IllegalArgumentException("koulutuslaji on tyhjä");
        }
        final KoodistoKoodiTyyppi koulutuslaji = new KoodistoKoodiTyyppi();
        koulutuslaji.setArvo(koulutus.getKoulutuslaji());
        koulutuslaji.setUri(String.format("koulutuslaji_%s#1", koulutus.getKoulutuslaji().toLowerCase().trim()));
        koulutuslaji.setVersio(1);
        lisaaKoulutusTyyppi.getKoulutuslaji().add(koulutuslaji);

        // opetuskieli
        if (StringUtils.isBlank(koulutus.getOpetuskieli())) {
            throw new IllegalArgumentException("opetuskieli on tyhjä");
        }
        final KoodistoKoodiTyyppi opetuskieli = new KoodistoKoodiTyyppi();
        opetuskieli.setArvo(koulutus.getOpetuskieli());
        opetuskieli.setUri(String.format("kieli_%s#1", koulutus.getOpetuskieli().toLowerCase().trim()));
        opetuskieli.setVersio(1);
        lisaaKoulutusTyyppi.getOpetuskieli().add(opetuskieli);

        // opetusmuoto
        if (StringUtils.isBlank(koulutus.getOpetusmuoto())) {
            throw new IllegalArgumentException("opetusmuoto");
        }
        final KoodistoKoodiTyyppi opetusmuoto = new KoodistoKoodiTyyppi();
        opetusmuoto.setArvo(koulutus.getOpetusmuoto());
        opetusmuoto.setUri(String.format("opetusmuoto_%s#1", koulutus.getOpetusmuoto().toLowerCase().trim()));
        opetusmuoto.setVersio(1);
        lisaaKoulutusTyyppi.getOpetusmuoto().add(opetusmuoto);

        // koulutus
        if (StringUtils.isBlank(koulutus.getKoulutus())) {
            throw new IllegalArgumentException("koulutus on tyhjä");
        }
        final KoodistoKoodiTyyppi koulutusTyyppi = new KoodistoKoodiTyyppi();
        koulutusTyyppi.setArvo(koulutus.getKoulutus());
        koulutusTyyppi.setUri(String.format("koulutus_%s#1", StringUtils.leftPad(koulutus.getKoulutus().toLowerCase().trim(), 6, '0')));
        koulutusTyyppi.setVersio(1);
        lisaaKoulutusTyyppi.setKoulutusKoodi(koulutusTyyppi);

        // koulutusohjelma
        if (StringUtils.isBlank(koulutus.getKoulutusohjelma())) {
            throw new IllegalArgumentException("koulutusohjelma on tyhjä");
        }
        final KoodistoKoodiTyyppi koulutusohjelma = new KoodistoKoodiTyyppi();
        koulutusohjelma.setArvo(koulutus.getKoulutusohjelma());
        koulutusohjelma.setUri(String.format("koulutusohjelmaamm_%s#1", StringUtils.leftPad(koulutus.getKoulutusohjelma().toLowerCase().trim(), 4, '0')));
        koulutusohjelma.setVersio(1);
        lisaaKoulutusTyyppi.setKoulutusohjelmaKoodi(koulutusohjelma);

        // koulutusaste
        final HaeKaikkiKoulutusmoduulitKyselyTyyppi haeKoulutusmoduuli = new HaeKaikkiKoulutusmoduulitKyselyTyyppi();
        haeKoulutusmoduuli.setKoulutuskoodiUri(koulutusTyyppi.getUri());
        haeKoulutusmoduuli.setKoulutustyyppi(lisaaKoulutusTyyppi.getKoulutustyyppi());
        final HaeKaikkiKoulutusmoduulitVastausTyyppi koulutusmoduuliVastaus = tarjontaPublicService.haeKaikkiKoulutusmoduulit(haeKoulutusmoduuli);
        final List<KoulutusmoduuliTulos> koulutusmoduulit = koulutusmoduuliVastaus.getKoulutusmoduuliTulos();
        if (CollectionUtils.isNotEmpty(koulutusmoduulit)) {
            logger.info("Löytyi {} vastaavaa koulutusmoduulia", koulutusmoduulit.size());
            koulutusmoduulit: for (final KoulutusmoduuliTulos koulutusmoduuli : koulutusmoduulit) {
                final KoulutusmoduuliKoosteTyyppi koosteTyyppi = koulutusmoduuli.getKoulutusmoduuli();
                if (StringUtils.isNotBlank(koosteTyyppi.getKoulutusasteUri())) {
                    final KoodistoKoodiTyyppi koulutusaste = new KoodistoKoodiTyyppi();
                    koulutusaste.setUri(koosteTyyppi.getKoulutusasteUri());
                    lisaaKoulutusTyyppi.setKoulutusaste(koulutusaste);
                    logger.info("Asetettiin koulutusasteUri [{}]", koosteTyyppi.getKoulutusasteUri());
                    break koulutusmoduulit;
                }
            }
        }
        if (lisaaKoulutusTyyppi.getKoulutusaste() == null) {
            final KoodistoKoodiTyyppi koulutusaste = new KoodistoKoodiTyyppi();
            koulutusaste.setUri("koulutusasteoph2002_32#1");
            lisaaKoulutusTyyppi.setKoulutusaste(koulutusaste);

            logger.warn("Koulutusastetta ei löytynyt koulutusmoduulilta, asetettiin oletusarvo [koulutusasteoph2002_32#1]");
        }

        // suunniteltu kesto
        final KoulutuksenKestoTyyppi kesto = new KoulutuksenKestoTyyppi();
        kesto.setArvo(koulutus.getSuunniteltuKesto().toString());
        kesto.setYksikko("suunniteltukesto_01#1");
        lisaaKoulutusTyyppi.setKesto(kesto);

        // tarjoaja (organisaatio oid)
        lisaaKoulutusTyyppi.setTarjoaja(getOrganisaatioOid(koulutus.getOppilaitosnumero(), koulutus.getToimipisteJno()));

        // oid
        lisaaKoulutusTyyppi.setOid(getKoulutusOid(koulutus.getOppilaitosnumero(), koulutus.getToimipisteJno(),
                koulutus.getHakukohdekoodi(), koulutus.getYhkoodi(), koulutus.getKoulutusohjelma()));

        // tila
        lisaaKoulutusTyyppi.setTila(TarjontaTila.LUONNOS);

        // painotus
        lisaaKoulutusTyyppi.setPainotus(getMonikielinenTekstiTyyppi(koulutus.getPainotus()));

        // koulutuksen alkamispäivä
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            lisaaKoulutusTyyppi.setKoulutuksenAlkamisPaiva(sdf.parse("2014-01-01")); // oletusarvo
        } catch (final ParseException e) {
            throw new RuntimeException("päivämäärää ei voitu parsia", e);
        }

        //TODO pitää lisätä tarjontaan koulutukseen: alkamisvuosi, alkamiskausi

        return lisaaKoulutusTyyppi;
    }

    private HakukohdeTyyppi getHakukohdeTyyppi(final Hakukohde hakukohde, final String hakuOid) {
        final HakukohdeTyyppi hakukohdeTyyppi = new HakukohdeTyyppi();

        hakukohdeTyyppi.setHakukohteenHakuOid(hakuOid);
        hakukohdeTyyppi.setOid(getHakukohdeOid(hakukohde.getOppilaitosnumero(), hakukohde.getToimipisteJno(), hakukohde.getHakukohdekoodi(), hakukohde.getYhkoulu()));
        hakukohdeTyyppi.setValinnanAloituspaikat(hakukohde.getValinnanAloituspaikka());
        hakukohdeTyyppi.setAloituspaikat(hakukohde.getAloituspaikka());
        hakukohdeTyyppi.setHakukohteenTila(TarjontaTila.LUONNOS);
        hakukohdeTyyppi.setKaytetaanHaunPaattymisenAikaa(Boolean.FALSE);
        hakukohdeTyyppi.setLisatiedot(getMonikielinenTekstiTyyppi(null));

        fetchAndSetHakukohteenNimi(hakukohde, hakukohdeTyyppi);

        fetchAndSetLiitteidentoimitusosoite(hakukohde, hakukohdeTyyppi);

        return hakukohdeTyyppi;
    }

    private void fetchAndSetHakukohteenNimi(final Hakukohde hakukohde, final HakukohdeTyyppi hakukohdeTyyppi) {
        final SearchKoodisByKoodistoCriteriaType searchKoodi = new SearchKoodisByKoodistoCriteriaType();
        final KoodiBaseSearchCriteriaType koodi = new KoodiBaseSearchCriteriaType();
        koodi.setKoodiArvo(hakukohde.getHakukohdekoodi());
        searchKoodi.setKoodiSearchCriteria(koodi);
        searchKoodi.setKoodistoUri("hakukohteet");
        searchKoodi.setKoodistoVersioSelection(SearchKoodisByKoodistoVersioSelectionType.LATEST);
        final List<KoodiType> koodit = koodiService.searchKoodisByKoodisto(searchKoodi);
        if (CollectionUtils.isNotEmpty(koodit)) {
            final KoodiType koodiType = koodit.get(0);

            hakukohdeTyyppi.setHakukohdeNimi(String.format("%s#%d", koodiType.getKoodiUri(), koodiType.getVersio()));

            final StringBuilder hakukohdeNimi = new StringBuilder();
            for(final KoodiMetadataType meta : koodiType.getMetadata()) {
                hakukohdeNimi.append(meta.getNimi()).append(" ");
            }
            hakukohdeTyyppi.setHakukohdeKoodistoNimi(hakukohdeNimi.toString());

            return;
        }
        throw new RuntimeException("hakukohdetta ei löytynyt");
    }

    private void fetchAndSetLiitteidentoimitusosoite(final Hakukohde hakukohde, final HakukohdeTyyppi hakukohdeTyyppi) {
        //aseta liitteidentoimitusosoite organisaatiopalvelusta...
        final OrganisaatioSearchCriteriaDTO organisaatioSearch = new OrganisaatioSearchCriteriaDTO();
        organisaatioSearch.setOlKoodi(true);
        organisaatioSearch.setSearchStr(StringUtils.leftPad(StringUtils.trim(hakukohde.getOppilaitosnumero()), 5, '0'));
        final List<OrganisaatioDTO> organisaatiot = organisaatioService.searchOrganisaatios(organisaatioSearch);
        if (CollectionUtils.isNotEmpty(organisaatiot)) {
            final OrganisaatioDTO oppilaitos = organisaatiot.get(0);
            final List<OrganisaatioDTO> toimipisteet = organisaatioService.findChildrenTo(oppilaitos.getOid());
            if (CollectionUtils.isNotEmpty(toimipisteet)) {
                logger.info("löytyi {} toimipistettä", toimipisteet.size());
                for (final OrganisaatioDTO toimipiste : toimipisteet) {
                    if (StringUtils.equalsIgnoreCase(toimipiste.getOpetuspisteenJarjNro(), StringUtils.leftPad(StringUtils.trim(hakukohde.getToimipisteJno()), 2, '0'))) {
                        for (final YhteystietoDTO yhteystieto : toimipiste.getYhteystiedot()) {
                            if (yhteystieto instanceof OsoiteDTO) {
                                final OsoiteDTO osoite = (OsoiteDTO) yhteystieto;
                                if (osoite.getOsoiteTyyppi() == OsoiteTyyppi.POSTI) {
                                    final fi.vm.sade.tarjonta.service.types.OsoiteTyyppi osoiteTyyppi = new fi.vm.sade.tarjonta.service.types.OsoiteTyyppi();
                                    osoiteTyyppi.setOsoiteRivi(osoite.getOsoite());
                                    osoiteTyyppi.setLisaOsoiteRivi(osoite.getExtraRivi());
                                    osoiteTyyppi.setPostinumero(osoite.getPostinumero());
                                    osoiteTyyppi.setPostitoimipaikka(osoite.getPostitoimipaikka());

                                    hakukohdeTyyppi.setLiitteidenToimitusOsoite(osoiteTyyppi);

                                    return;
                                }
                            }
                        }
                        logger.warn("postiosoitetta ei löytynyt");
                        return; // OK, sopivaa osoitetta ei löytynyt
                    }
                }
            }
            throw new RuntimeException("toimipistettä ei löytynyt");
        }
        throw new RuntimeException("oppilaitosta ei löytynyt");
    }

    private String getOrganisaatioOid(final String oppilaitoskoodi, final String jno) {
        final OrganisaatioSearchCriteriaDTO organisaatioSearch = new OrganisaatioSearchCriteriaDTO();
        organisaatioSearch.setOlKoodi(true);
        organisaatioSearch.setSearchStr(StringUtils.leftPad(StringUtils.trim(oppilaitoskoodi), 5, '0'));
        final List<OrganisaatioDTO> oppilaitokset = organisaatioService.searchOrganisaatios(organisaatioSearch);
        if (CollectionUtils.isNotEmpty(oppilaitokset)) {
            final OrganisaatioDTO oppilaitos = oppilaitokset.get(0);
            final List<OrganisaatioDTO> toimipisteet = organisaatioService.findChildrenTo(oppilaitos.getOid());
            if (CollectionUtils.isNotEmpty(toimipisteet)) {
                for (final OrganisaatioDTO toimipiste : toimipisteet) {
                    if (StringUtils.equalsIgnoreCase(toimipiste.getOpetuspisteenJarjNro(), StringUtils.leftPad(jno, 2, '0'))) {
                        return toimipiste.getOid();
                    }
                }
            }
            throw new RuntimeException("toimipistettä ei löytynyt (koulutustarjoaja)");
        }
        throw new RuntimeException("oppilaitosta ei löytynyt (koulutustarjoaja)");
    }

    private String getHakukohdeOid(final String oppilaitoskoodi, final String juoksevaNumero, final String hakukohdekoodi, final String yhkoulu) {
        return String.format("%s.%s.%s_%s_%s_%s", OID_PREFIX, OID_TEKN_5, StringUtils.leftPad(StringUtils.trim(oppilaitoskoodi), 5, '0'),
                StringUtils.leftPad(StringUtils.trim(juoksevaNumero), 2, '0'), StringUtils.trim(hakukohdekoodi),
                StringUtils.leftPad(StringUtils.trim(yhkoulu), 4, '0'));
    }

    private String getKoulutusOid(final String oppilaitoskoodi, final String juoksevaNumero, final String hakukohdekoodi,
                                  final String yhkoulu, final String koulutusohjelma) {
        return String.format("%s.%s.%s_%s_%s_%s_%s", OID_PREFIX, OID_TEKN_5, StringUtils.leftPad(StringUtils.trim(oppilaitoskoodi), 5, '0'),
                StringUtils.leftPad(StringUtils.trim(juoksevaNumero), 2, '0'), StringUtils.trim(hakukohdekoodi),
                StringUtils.leftPad(StringUtils.trim(yhkoulu), 4, '0'), StringUtils.trim(koulutusohjelma));
    }

    private ValintakoeTyyppi getEmptyValintakoeTyyppi() {
        final ValintakoeTyyppi valintakoeTyyppi = new ValintakoeTyyppi();

        valintakoeTyyppi.setKuvaukset(getMonikielinenTekstiTyyppi(null));

        return valintakoeTyyppi;
    }

    private MonikielinenTekstiTyyppi getMonikielinenTekstiTyyppi(final String value) {
        final MonikielinenTekstiTyyppi tekstiTyyppi = new MonikielinenTekstiTyyppi();

        final MonikielinenTekstiTyyppi.Teksti teksti = new MonikielinenTekstiTyyppi.Teksti();
        teksti.setKieliKoodi("kieli_fi#1");
        teksti.setValue(value);

        tekstiTyyppi.getTeksti().add(teksti);

        return tekstiTyyppi;
    }
}
