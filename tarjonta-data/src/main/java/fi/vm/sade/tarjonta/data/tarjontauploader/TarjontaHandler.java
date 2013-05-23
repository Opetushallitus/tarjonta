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

    public void addKoulutus(final Koulutus koulutus) {
        logger.info("Lisätään koulutus");
        //tarjontaAdminService.lisaaKoulutus(getLisaaKoulutusTyyppi(koulutus));
    }

    public void addHakukohde(final Hakukohde hakukohde, final String hakuOid) {
        logger.info("Lisätään hakukohde");

        final HakukohdeTyyppi hakukohdeTyyppi = getHakukohdeTyyppi(hakukohde, hakuOid);
        tarjontaAdminService.lisaaHakukohde(getHakukohdeTyyppi(hakukohde, hakuOid));

        if (StringUtils.equalsIgnoreCase(hakukohde.getValintakoe(), "T")) {
            tarjontaAdminService.tallennaValintakokeitaHakukohteelle(hakukohdeTyyppi.getOid(), Collections.singletonList(getEmptyValintakoeTyyppi()));
        }
    }

    private LisaaKoulutusTyyppi getLisaaKoulutusTyyppi(final Koulutus koulutus) {
        final LisaaKoulutusTyyppi lisaaKoulutusTyyppi = new LisaaKoulutusTyyppi();

        // TODO lisää kaikki tarvittavat tiedot

        return lisaaKoulutusTyyppi;
    }

    private HakukohdeTyyppi getHakukohdeTyyppi(final Hakukohde hakukohde, final String hakuOid) {
        final HakukohdeTyyppi hakukohdeTyyppi = new HakukohdeTyyppi();

        hakukohdeTyyppi.setHakukohteenHakuOid(hakuOid);
        hakukohdeTyyppi.setOid(getHakukohdeOid(hakukohde.getOppilaitosnumero(), hakukohde.getToimipisteJno(), hakukohde.getHakukohdekoodi(), hakukohde.getYhkoulu()));
        hakukohdeTyyppi.setValinnanAloituspaikat(hakukohde.getValinnanAloituspaikka());
        hakukohdeTyyppi.setAloituspaikat(hakukohde.getAloituspaikka());
        hakukohdeTyyppi.setHakukohteenTila(TarjontaTila.LUONNOS);
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
        organisaatioSearch.setSearchStr(StringUtils.trim(hakukohde.getOppilaitosnumero()));
        final List<OrganisaatioPerustietoType> organisaatiot = organisaatioService.searchBasicOrganisaatios(organisaatioSearch);
        if (CollectionUtils.isNotEmpty(organisaatiot)) {
            final OrganisaatioPerustietoType oppilaitos = organisaatiot.get(0);
            final List<OrganisaatioDTO> toimipisteet = organisaatioService.findChildrenTo(oppilaitos.getOid());
            if (CollectionUtils.isNotEmpty(toimipisteet)) {
                for (final OrganisaatioDTO toimipiste : toimipisteet) {
                    if (StringUtils.equalsIgnoreCase(toimipiste.getOpetuspisteenJarjNro(), StringUtils.trim(hakukohde.getToimipisteJno()))) {
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
                    }
                }
            }
            throw new RuntimeException("toimipistettä ei löytynyt");
        }
        throw new RuntimeException("oppilaitosta ei löytynyt");
    }

    private String getHakukohdeOid(final String oppilaitoskoodi, final String juoksevaNumero, final String hakukohdekoodi, final String yhkoulu) {
        return String.format("%s.%s.%s_%s_%s_%s", OID_PREFIX, OID_TEKN_5, oppilaitoskoodi, juoksevaNumero, hakukohdekoodi, yhkoulu);
    }

    private ValintakoeTyyppi getEmptyValintakoeTyyppi() {
        final ValintakoeTyyppi valintakoeTyyppi = new ValintakoeTyyppi();

        valintakoeTyyppi.setKuvaukset(getMonikielinenTekstiTyyppi(null));

        return valintakoeTyyppi;
    }

    private MonikielinenTekstiTyyppi getMonikielinenTekstiTyyppi(final String value) {
        final MonikielinenTekstiTyyppi tekstiTyyppi = new MonikielinenTekstiTyyppi();

        final MonikielinenTekstiTyyppi.Teksti teksti = new MonikielinenTekstiTyyppi.Teksti();
        teksti.setKieliKoodi("fi");
        teksti.setValue(value);

        tekstiTyyppi.getTeksti().add(teksti);

        return tekstiTyyppi;
    }
}
