/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.tarjonta.ui.helper.conversion;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.oid.service.types.NodeClassCode;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioDTO;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.WebLinkkiTyyppi;
import fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import static fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper.splitKoodiURIAllowNull;
import static fi.vm.sade.tarjonta.ui.helper.conversion.Koulutus2asteConverter.INVALID_DATA;
import static fi.vm.sade.tarjonta.ui.helper.conversion.Koulutus2asteConverter.mapToKoulutusLinkkiViewModel;
import static fi.vm.sade.tarjonta.ui.helper.conversion.Koulutus2asteConverter.mapToWebLinkkiTyyppiDto;
import static fi.vm.sade.tarjonta.ui.helper.conversion.Koulutus2asteConverter.mapToYhteyshenkiloTyyppiDto;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusLinkkiViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusYhteyshenkiloViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusKoodistoModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusohjelmaModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.MonikielinenTekstiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.YhteyshenkiloModel;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Jani Wilén
 */
public class KoulutusConveter {

    private static final Logger LOG = LoggerFactory.getLogger(Koulutus2asteConverter.class);
    public static final String INVALID_DATA = "Invalid data exception - ";
    @Autowired(required = true)
    private OrganisaatioService organisaatioService;
    @Autowired(required = true)
    private KoulutusKoodistoConverter koulutusKoodisto;
    @Autowired(required = true)
    private OIDService oidService;

    public OrganisaatioDTO searchOrganisatioByOid(final String organisaatioOid) {
        if (organisaatioOid == null) {
            throw new RuntimeException(INVALID_DATA + "organisation OID cannot be null.");
        }
        OrganisaatioDTO dto = this.organisaatioService.findByOid(organisaatioOid);

        if (dto == null || dto.getOid() == null) {
            throw new RuntimeException("No organisation found by OID : " + organisaatioOid);
        }

        return dto;
    }

    public String checkWwwOsoite(String linkki) {
        return (!isValidUrl(linkki)) ? "http://" + linkki : linkki;
    }

    public WebLinkkiTyyppi mapOpetussuunnitelmaLinkkiToTyyppi(String linkki) {
        WebLinkkiTyyppi webLink = new WebLinkkiTyyppi();
        webLink.setKieli(I18N.getLocale().getLanguage());
        webLink.setUri(checkWwwOsoite(linkki));
        webLink.setTyyppi(KoulutusLinkkiViewModel.LINKKI_TYYPIT[1]);
        return webLink;
    }

    public boolean isValidUrl(String givenUrl) {
        return givenUrl == null
                || givenUrl.isEmpty()
                || givenUrl.startsWith("http://")
                || givenUrl.startsWith("https://")
                || givenUrl.startsWith("ftp://")
                || givenUrl.startsWith("file://");
    }

    public YhteyshenkiloTyyppi mapYhteyshenkiloToTyyppi(KoulutusToisenAsteenPerustiedotViewModel model) {
        YhteyshenkiloTyyppi yhteyshenkilo = new YhteyshenkiloTyyppi();
        String kokoNimi = model.getYhtHenkKokoNimi();
        String[] nimet = kokoNimi.split(" ");
        if (nimet.length > 1) {
            yhteyshenkilo.setEtunimet(kokoNimi.substring(0, kokoNimi.lastIndexOf(' ')));
            yhteyshenkilo.setSukunimi(nimet[nimet.length - 1]);
        } else {
            yhteyshenkilo.setEtunimet(kokoNimi);
        }
        yhteyshenkilo.setPuhelin(model.getYhtHenkPuhelin());
        yhteyshenkilo.setSahkoposti(model.getYhtHenkEmail());
        yhteyshenkilo.setTitteli(model.getYhtHenkTitteli());
        yhteyshenkilo.setHenkiloOid(model.getYhtHenkiloOid());
        return yhteyshenkilo;
    }

    public YhteyshenkiloTyyppi mapYhteyshenkiloToTyyppi(YhteyshenkiloModel model) {
        YhteyshenkiloTyyppi yhteyshenkilo = new YhteyshenkiloTyyppi();
        yhteyshenkilo.setEtunimet(model.getYhtHenkKokoNimi());
        yhteyshenkilo.setPuhelin(model.getYhtHenkPuhelin());
        yhteyshenkilo.setSahkoposti(model.getYhtHenkEmail());
        yhteyshenkilo.setTitteli(model.getYhtHenkTitteli());
        yhteyshenkilo.setHenkiloOid(model.getYhtHenkiloOid());
        return yhteyshenkilo;
    }

    protected void mapYhteyshenkiloToViewModel(KoulutusToisenAsteenPerustiedotViewModel model2Aste, LueKoulutusVastausTyyppi tyyppi) {
        if (tyyppi.getYhteyshenkilo().isEmpty()) {
            return;
        }

        YhteyshenkiloTyyppi yhtHenk = tyyppi.getYhteyshenkilo().get(0);
        model2Aste.setYhtHenkKokoNimi(yhtHenk.getEtunimet() + " " + yhtHenk.getSukunimi());
        model2Aste.setYhtHenkEmail(yhtHenk.getSahkoposti());
        model2Aste.setYhtHenkPuhelin(yhtHenk.getPuhelin());
        model2Aste.setYhtHenkTitteli(yhtHenk.getTitteli());
        model2Aste.setYhtHenkiloOid(yhtHenk.getHenkiloOid());
    }

    protected void addToYhteyshenkiloTyyppiList(final Collection<KoulutusYhteyshenkiloViewModel> fromModel, List<YhteyshenkiloTyyppi> toTyyppi) throws
            ExceptionMessage {
        if (toTyyppi == null) {
            throw new RuntimeException(INVALID_DATA + "list of YhteyshenkiloTyyppi objects cannot be null.");
        }

        if (fromModel != null && !fromModel.isEmpty()) {
            for (KoulutusYhteyshenkiloViewModel yhteyshenkiloModel : fromModel) {
                if (yhteyshenkiloModel.getHenkiloOid() == null) {
                    //generate OID to new yhteyshenkilo.
                    //back-end not not accept null OIDs.
                    yhteyshenkiloModel.setHenkiloOid(oidService.newOid(NodeClassCode.TEKN_5));
                }

                toTyyppi.add(mapToYhteyshenkiloTyyppiDto(yhteyshenkiloModel));
            }
        }
    }

    public static KoulutusLinkkiViewModel mapToKoulutusLinkkiViewModel(WebLinkkiTyyppi type) {
        KoulutusLinkkiViewModel koulutusLinkkiViewModel = new KoulutusLinkkiViewModel();
        koulutusLinkkiViewModel.setKieli(type.getKieli());
        koulutusLinkkiViewModel.setLinkkityyppi(type.getTyyppi());
        koulutusLinkkiViewModel.setUrl(type.getUri());

        return koulutusLinkkiViewModel;
    }

    public static void addToKoulutusLinkkiViewModel(List<WebLinkkiTyyppi> linkki, List<KoulutusLinkkiViewModel> listLinkkiModel) {
        if (listLinkkiModel == null) {
            throw new RuntimeException(INVALID_DATA + "list of KoulutusLinkkiViewModel objects cannot be null.");
        }

        if (linkki != null && !linkki.isEmpty()) {
            for (WebLinkkiTyyppi type : linkki) {
                listLinkkiModel.add(mapToKoulutusLinkkiViewModel(type));
            }
        }
    }

    public static YhteyshenkiloTyyppi mapToYhteyshenkiloTyyppiDto(final KoulutusYhteyshenkiloViewModel model) throws ExceptionMessage {
        YhteyshenkiloTyyppi yhteyshenkiloTyyppi = new YhteyshenkiloTyyppi();

        yhteyshenkiloTyyppi.setHenkiloOid(model.getHenkiloOid());
        yhteyshenkiloTyyppi.setEtunimet(model.getEtunimet());
        yhteyshenkiloTyyppi.setSukunimi(model.getSukunimi());
        yhteyshenkiloTyyppi.setSahkoposti(model.getEmail());
        yhteyshenkiloTyyppi.setTitteli(model.getTitteli());
        yhteyshenkiloTyyppi.setPuhelin(model.getPuhelin());

        if (model.getKielet() != null && !model.getKielet().isEmpty()) {
            for (String kieliUri : model.getKielet()) {
                yhteyshenkiloTyyppi.getKielet().add(kieliUri);
            }
        }

        return yhteyshenkiloTyyppi;
    }

    public static WebLinkkiTyyppi mapToWebLinkkiTyyppiDto(final KoulutusLinkkiViewModel model) throws ExceptionMessage {
        WebLinkkiTyyppi web = new WebLinkkiTyyppi();
        web.setKieli(model.getKieli());
        web.setTyyppi(model.getLinkkityyppi());
        web.setUri(model.getUrl());

        return web;
    }

    public static KoulutusYhteyshenkiloViewModel mapToKoulutusYhteyshenkiloViewModel(final YhteyshenkiloTyyppi tyyppi) {
        KoulutusYhteyshenkiloViewModel yhteyshenkiloModel = new KoulutusYhteyshenkiloViewModel();
        yhteyshenkiloModel.setHenkiloOid(tyyppi.getHenkiloOid());
        yhteyshenkiloModel.setEmail(tyyppi.getSahkoposti());
        yhteyshenkiloModel.setEtunimet(tyyppi.getEtunimet());
        yhteyshenkiloModel.setPuhelin(tyyppi.getPuhelin());
        yhteyshenkiloModel.setSukunimi(tyyppi.getSukunimi());
        yhteyshenkiloModel.setTitteli(tyyppi.getTitteli());
        final List<String> kielet = tyyppi.getKielet();
        if (kielet != null && !kielet.isEmpty()) {
            for (String kieliUri : kielet) {
                yhteyshenkiloModel.getKielet().add(kieliUri);
            }
        }
        return yhteyshenkiloModel;
    }

    public KoulutuskoodiModel mapToKoulutuskoodiModel(final KoodistoKoodiTyyppi koulutusKoodi, final Locale locale) {
        if (koulutusKoodi != null && koulutusKoodi.getUri() != null) {
            KoulutuskoodiModel model = koulutusKoodisto.listaaKoulutuskoodi(koulutusKoodi, locale);

            if (model == null) {
                /**
                 * Serious external system error has occured: Koodisto koodi
                 * search has returned a null data object. It's very likely that
                 * someone has changed Koodisto service koodi uris or the
                 * service is having other an internal problem.
                 *
                 * Let just show koodi uris in all UI data fields, as there is
                 * no way to get correct language data from the Koodisto
                 * service.
                 */
                model = new KoulutuskoodiModel();
                model.setNimi(koulutusKoodi.getUri());
                model.setKuvaus(koulutusKoodi.getUri());
                model.setKoodi(koulutusKoodi.getUri());
                final String[] koodiUriAndVersion = splitKoodiURIAllowNull(koulutusKoodi.getUri());

                model.setKoodistoUri(koodiUriAndVersion[0]);
                model.setKoodistoVersio(Integer.parseInt(koodiUriAndVersion[1]));
                model.getKielikaannos().add(new KielikaannosViewModel("FI", koulutusKoodi.getUri()));
                model.setKielikoodi("FI");

                LOG.error("Data conversion error - Koodisto service do not have a koodi with URI '{}' and version '{}'.", koodiUriAndVersion[0], koodiUriAndVersion[1]);
            }

            return model;
        }

        LOG.error("Data conversion error - koulutus Koodisto service koodi URI was null.");

        return null;
    }

    /**
     * Return KoodistoKoodiTyyppi search data object.
     *
     * @param model
     * @return
     */
    public static KoodistoKoodiTyyppi mapToValidKoodistoKoodiTyyppi(final boolean allowNull, final KoulutusKoodistoModel model) {
        if (model != null && model.getKoodistoUri() != null) {
            return mapToKoodistoKoodiTyyppi(model);
        } else if (allowNull) {
            //KoulutusohjelmaModel obejct can have null uri
            return null;
        }

        throw new RuntimeException("KoulutusasteModel or KoulutuskoodiModel cannot be null! Object " + model);
    }

    public static KoodistoKoodiTyyppi mapToKoodistoKoodiTyyppi(KoulutusKoodistoModel model) {
        KoodistoKoodiTyyppi koodit = createKoodiVersionUri(
                model.getKoodistoUri(),
                model.getKoodistoVersio(),
                model.getNimi());

        if (model instanceof MonikielinenTekstiModel) {
            //Some UI objects do not need multilanguage data.
            MonikielinenTekstiModel o = (MonikielinenTekstiModel) model;
            if (o.getKielikaannos() != null && !o.getKielikaannos().isEmpty()) {
                for (KielikaannosViewModel nimiModel : o.getKielikaannos()) {
                    KoodistoKoodiTyyppi.Nimi tyyppi = new KoodistoKoodiTyyppi.Nimi();
                    tyyppi.setKieli(nimiModel.getKielikoodi());
                    tyyppi.setValue(nimiModel.getNimi());
                    koodit.getNimi().add(tyyppi);
                }
            }
        }
        return koodit;
    }

    public static HaeKoulutusmoduulitKyselyTyyppi mapToHaeKoulutusmoduulitKyselyTyyppi(KoulutusasteTyyppi aste, KoulutuskoodiModel koulutuskoodi, KoulutusKoodistoModel model) {
        HaeKoulutusmoduulitKyselyTyyppi kysely = new HaeKoulutusmoduulitKyselyTyyppi();
        kysely.setKoulutuskoodiUri(koulutuskoodi.getKoodistoUriVersio());

        switch (aste) {
            case AMMATILLINEN_PERUSKOULUTUS:
                if (model != null && model.getKoodistoUri() != null) {
                    kysely.setKoulutusohjelmakoodiUri(model.getKoodistoUri());
                    LOG.debug("Koulutuskoodi URI '{}' Koulutusohjelma URI : '{}'",
                            kysely.getKoulutuskoodiUri(),
                            kysely.getKoulutusohjelmakoodiUri());
                }
                break;
            case LUKIOKOULUTUS:
                if (model != null && model.getKoodistoUri() != null) {
                    kysely.setLukiolinjakoodiUri(model.getKoodistoUri());
                    LOG.debug("Koulutuskoodi URI '{}' Lukiolinja URI : '{}'",
                            kysely.getKoulutuskoodiUri(),
                            kysely.getLukiolinjakoodiUri());
                }
                break;
        }


        return kysely;
    }

    public static String mapToVersionUri(String uri, int version) {
        if (uri != null) {
            return TarjontaUIHelper.createVersionUri(uri, version);
        }

        throw new RuntimeException(INVALID_DATA + "URI cannot be null.");
    }

    public static KoodistoKoodiTyyppi createKoodi(final String uri, final boolean noNullValues, final String errorInField) {
        if (noNullValues && uri == null) {
            throw new RuntimeException("URI cannot be null in field name " + errorInField);
        }

        return toKoodistoKoodiTyypi.apply(uri);
    }

    public static KoodistoKoodiTyyppi createKoodiVersionUri(final String uri, final int version, final String arvo) {
        final KoodistoKoodiTyyppi koodi = new KoodistoKoodiTyyppi();
        final String uriVersion = mapToVersionUri(uri, version);
        koodi.setUri(uriVersion);
        koodi.setArvo(arvo);

        return koodi;
    }

    public static String getUri(final KoodistoKoodiTyyppi type) {
        return type.getUri();
    }

    public static Set<String> convertOpetuskielet(final List<KoodistoKoodiTyyppi> opetuskieliKoodit) {
        Set<String> opetuskielet = new HashSet<String>();
        for (KoodistoKoodiTyyppi curKoodi : opetuskieliKoodit) {
            opetuskielet.add(curKoodi.getUri());
        }
        return opetuskielet;
    }
    /**
     * Converter KoodistoKoodiTyyppi -> String
     */
    public static final Function<KoodistoKoodiTyyppi, String> fromKoodistoKoodiTyyppi = new Function<KoodistoKoodiTyyppi, String>() {
        @Override
        public String apply(KoodistoKoodiTyyppi koodi) {
            return koodi.getUri();
        }
    };
    /**
     * Converter String -> KoodistoKoodiTyyppi
     */
    public static final Function<String, KoodistoKoodiTyyppi> toKoodistoKoodiTyypi = new Function<String, KoodistoKoodiTyyppi>() {
        @Override
        public KoodistoKoodiTyyppi apply(String uri) {
            final KoodistoKoodiTyyppi tyyppi = new KoodistoKoodiTyyppi();
            tyyppi.setUri(uri);
            return tyyppi;
        }
    };

    public static Set<String> mapToKoodistoKoodis(List<KoodistoKoodiTyyppi> ammattinimikkeet) {
        return Sets.newHashSet(Iterables.transform(ammattinimikkeet, fromKoodistoKoodiTyyppi));
    }

    protected void clear(MonikielinenTekstiTyyppi tekstis) {
        if (tekstis != null) {
            tekstis.getTeksti().clear();
        }
    }

    /**
     * Convert language uri and value to MonikielinenTekstiTyyppi.
     *
     * @param languageUri
     * @param teksti
     * @return
     */
    public MonikielinenTekstiTyyppi.Teksti convertToMonikielinenTekstiTyyppi(final String languageUri, final String teksti) {
        MonikielinenTekstiTyyppi.Teksti mktt = new MonikielinenTekstiTyyppi.Teksti();
        mktt.setValue(teksti);

        if (languageUri == null) {
            throw new RuntimeException("Language URI cannot be null.");
        }
        mktt.setKieliKoodi(languageUri);
        return mktt;
    }

    public static MonikielinenTekstiTyyppi mapToMonikielinenTekstiTyyppi(final Collection<KielikaannosViewModel> kielet) {
        MonikielinenTekstiTyyppi tyyppi = new MonikielinenTekstiTyyppi();

        for (KielikaannosViewModel nimi : kielet) {
            MonikielinenTekstiTyyppi.Teksti teksti = new MonikielinenTekstiTyyppi.Teksti();
            teksti.setKieliKoodi(nimi.getKielikoodi());
            teksti.setValue(nimi.getNimi());
            tyyppi.getTeksti().add(teksti);
        }

        return tyyppi;
    }

    public static List<KielikaannosViewModel> mapToKielikaannosViewModel(final MonikielinenTekstiTyyppi kielet) {
        List<KielikaannosViewModel> models = new ArrayList<KielikaannosViewModel>();

        if (kielet == null) {
            return models;
        }

        for (MonikielinenTekstiTyyppi.Teksti nimi : kielet.getTeksti()) {
            models.add(new KielikaannosViewModel(nimi.getKieliKoodi(), nimi.getValue()));
        }

        return models;
    }

    public static Set<KielikaannosViewModel> mapToKoodiModel(final MonikielinenTekstiTyyppi tyyppi) {
        Set<KielikaannosViewModel> model = new HashSet<KielikaannosViewModel>();

        for (MonikielinenTekstiTyyppi.Teksti teksti : tyyppi.getTeksti()) {
            model.add(new KielikaannosViewModel(teksti.getKieliKoodi(), teksti.getValue()));
        }

        return model;
    }

    public static MonikielinenTekstiModel mapToMonikielinenTekstiModel(final MonikielinenTekstiTyyppi tyyppi, final Locale locale) {
        MonikielinenTekstiModel m = new MonikielinenTekstiModel();

        if (tyyppi == null) {
            LOG.warn("MonikielinenTekstiTyyppi object was null, the missing data cannot be show on UI.");
            return m;
        }

        if (locale != null) {
            final MonikielinenTekstiTyyppi.Teksti teksti = TarjontaUIHelper.searchTekstiTyyppiByLanguage(tyyppi.getTeksti(), locale);

            if (teksti != null) {
                m.setKielikoodi(teksti.getKieliKoodi());
                m.setNimi(teksti.getValue());

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Language code : " + teksti.getKieliKoodi());
                    LOG.debug("Text value : " + (teksti != null ? teksti.getValue() : teksti));
                }
            } else {
                LOG.debug("No text data found for locale " + locale.getLanguage());
            }
        }

        if (m.getNimi() == null || m.getNimi().isEmpty()) {
            //FI default fallback
            final Locale locale1 = new Locale("FI");
            final MonikielinenTekstiTyyppi.Teksti teksti = TarjontaUIHelper.searchTekstiTyyppiByLanguage(tyyppi.getTeksti(), locale1);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Language code fallback : " + locale1.getLanguage());
                LOG.debug("Text value : " + (teksti != null ? teksti.getValue() : teksti));
            }

            if (teksti != null) {
                m.setKielikoodi(teksti.getKieliKoodi());
                m.setNimi(teksti.getValue());
            } else {
                LOG.error("An invalid data error -´MonikielinenTekstiModel object was missing Finnish language data.");
            }
        }

        m.setKielikaannos(mapToKoodiModel(tyyppi));
        return m;
    }

    public static Map<String, List<KoulutusmoduuliKoosteTyyppi>> komoCacheMapByKoulutuskoodi(Collection<KoulutusmoduuliKoosteTyyppi> komos) {
        Map<String, List<KoulutusmoduuliKoosteTyyppi>> hashMap = new HashMap<String, List<KoulutusmoduuliKoosteTyyppi>>();

        for (KoulutusmoduuliKoosteTyyppi komo : komos) {
            final String uri = komo.getKoulutuskoodiUri();

            if (hashMap.containsKey(uri)) {
                hashMap.get(uri).add(komo);
            } else {
                List<KoulutusmoduuliKoosteTyyppi> l = new ArrayList<KoulutusmoduuliKoosteTyyppi>();
                l.add(komo);
                hashMap.put(uri, l);
            }

        }
        return hashMap;

    }
}
