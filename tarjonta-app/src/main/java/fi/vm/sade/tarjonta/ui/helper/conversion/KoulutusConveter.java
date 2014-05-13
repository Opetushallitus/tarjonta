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

import static fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper.splitKoodiURIAllowNull;

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

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.organisaatio.api.model.OrganisaatioService;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.organisaatio.service.search.OrganisaatioSearchService;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HenkiloTyyppi;
import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.LueKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.WebLinkkiTyyppi;
import fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.ui.helper.OidCreationException;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusLinkkiViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusKoodistoModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusohjelmaModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.MonikielinenTekstiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.KoulutusRelaatioModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.LukiolinjaModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.YhteyshenkiloModel;
import fi.vm.sade.tarjonta.ui.model.org.OrganisationOidNamePair;

/**
 *
 * @author Jani Wilén
 */
public class KoulutusConveter {

    private static final Logger LOG = LoggerFactory.getLogger(KoulutusConveter.class);
    public static final String INVALID_DATA = "Invalid data exception - ";
    private static final String FALLBACK_LANG_CODE = "fi";
    @Autowired(required = true)
    protected OrganisaatioSearchService organisaatioSearchService;
    @Autowired(required = true)
    private OrganisaatioService organisaatioService;
    @Autowired(required = true)
    protected KoulutusKoodistoConverter koulutusKoodisto;
    @Autowired(required = true)
    protected TarjontaKoodistoHelper helper;

    public OrganisationOidNamePair searchOrganisationByOid(final String organisaatioOid) {
        Preconditions.checkNotNull(organisaatioOid, "Organisation OID cannot be null.");
        OrganisaatioPerustieto dto = this.organisaatioSearchService.findByOidSet(Sets.newHashSet(organisaatioOid)).get(0);

        if (dto == null || dto.getOid() == null) {
            throw new RuntimeException("No organisation found by OID : " + organisaatioOid);
        }
        OrganisationOidNamePair pair = new OrganisationOidNamePair();
        pair.dtoToModel(dto);

        return pair;
    }

    public OrganisaatioPerustieto searchOrganisationByOid(final String organisaatioOid, final OrganisationOidNamePair model) {
        Preconditions.checkNotNull(organisaatioOid, "Organisation OID cannot be null.");
        Preconditions.checkNotNull(model, "TarjoajaModel object cannot be null.");
        Preconditions.checkNotNull(organisaatioSearchService, "Organisaation service cannot be null.");
        OrganisaatioPerustieto perus = this.organisaatioSearchService.findByOidSet(Sets.newHashSet(organisaatioOid)).get(0);

        if (perus == null || perus.getOid() == null) {
            throw new RuntimeException("No organisation found by OID : " + organisaatioOid);
        }

        model.dtoToModel(perus);

        return perus;
    }

    public String getUserLangUri() {
        return helper.convertKielikoodiToKieliUri(I18N.getLocale().getLanguage());
    }

    public static String getUserLangUri(KoulutusRelaatioModel baseModel) {
        Preconditions.checkNotNull(baseModel.getUserKoodiLangUri(), "User koodi URI language code cannot be null.");
        Preconditions.checkArgument(!baseModel.getUserKoodiLangUri().isEmpty(), "User koodi URI language code cannot be an empty string.");

        return baseModel.getUserKoodiLangUri();
    }

    public static String checkWwwOsoite(String linkki) {
        return (!isValidUrl(linkki)) ? "http://" + linkki : linkki;
    }

    public static WebLinkkiTyyppi mapOpetussuunnitelmaLinkkiToTyyppi(String linkki) {
        WebLinkkiTyyppi webLink = new WebLinkkiTyyppi();
        webLink.setKieli(I18N.getLocale().getLanguage());
        webLink.setUri(checkWwwOsoite(linkki));
        webLink.setTyyppi(KoulutusLinkkiViewModel.LINKKI_TYYPIT[1]);
        return webLink;
    }

    public static boolean isValidUrl(String givenUrl) {
        return givenUrl == null
                || givenUrl.isEmpty()
                || givenUrl.startsWith("http://")
                || givenUrl.startsWith("https://")
                || givenUrl.startsWith("ftp://")
                || givenUrl.startsWith("file://");
    }

    public static YhteyshenkiloTyyppi mapYhteyshenkiloToTyyppi(KoulutusToisenAsteenPerustiedotViewModel model) {
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
        yhteyshenkilo.setHenkiloTyyppi(HenkiloTyyppi.YHTEYSHENKILO);
        return yhteyshenkilo;
    }

    public static YhteyshenkiloTyyppi mapYhteyshenkiloToTyyppi(YhteyshenkiloModel model, HenkiloTyyppi tyyppiEnum) {
        YhteyshenkiloTyyppi yhteyshenkilo = new YhteyshenkiloTyyppi();
        yhteyshenkilo.setEtunimet(model.getYhtHenkKokoNimi());
        yhteyshenkilo.setPuhelin(model.getYhtHenkPuhelin());
        yhteyshenkilo.setSahkoposti(model.getYhtHenkEmail());
        yhteyshenkilo.setTitteli(model.getYhtHenkTitteli());
        yhteyshenkilo.setHenkiloOid(model.getYhtHenkiloOid());
        yhteyshenkilo.setHenkiloTyyppi(tyyppiEnum);
        return yhteyshenkilo;
    }

    protected void mapYhteyshenkiloToViewModel(KoulutusToisenAsteenPerustiedotViewModel model2Aste, LueKoulutusVastausTyyppi tyyppi) {
        if (tyyppi.getYhteyshenkiloTyyppi().isEmpty()) {
            return;
        }

        YhteyshenkiloTyyppi yhtHenk = tyyppi.getYhteyshenkiloTyyppi().get(0);
        model2Aste.setYhtHenkKokoNimi(yhtHenk.getEtunimet() + " " + yhtHenk.getSukunimi());
        model2Aste.setYhtHenkEmail(yhtHenk.getSahkoposti());
        model2Aste.setYhtHenkPuhelin(yhtHenk.getPuhelin());
        model2Aste.setYhtHenkTitteli(yhtHenk.getTitteli());
        model2Aste.setYhtHenkiloOid(yhtHenk.getHenkiloOid());
    }

    protected void mapYhteyshenkiloToViewModel(YhteyshenkiloModel yhteyshenkiloModel, LueKoulutusVastausTyyppi tyyppi, HenkiloTyyppi tyyppiEnum) {
        Preconditions.checkNotNull(tyyppiEnum, "HenkiloTyyppi enum cannot be null.");
        if (tyyppi.getYhteyshenkiloTyyppi().isEmpty()) {
            return;
        }

        for (YhteyshenkiloTyyppi henk : tyyppi.getYhteyshenkiloTyyppi()) {
            HenkiloTyyppi henkiloTyyppi = henk.getHenkiloTyyppi();
            if (henkiloTyyppi == null) {
                henkiloTyyppi = HenkiloTyyppi.YHTEYSHENKILO;
            }

            if (tyyppiEnum.equals(henkiloTyyppi)) {
                yhteyshenkiloModel.setYhtHenkKokoNimi(henk.getEtunimet() + " " + henk.getSukunimi());
                yhteyshenkiloModel.setYhtHenkEmail(henk.getSahkoposti());
                yhteyshenkiloModel.setYhtHenkPuhelin(henk.getPuhelin());
                yhteyshenkiloModel.setYhtHenkTitteli(henk.getTitteli());
                yhteyshenkiloModel.setYhtHenkiloOid(henk.getHenkiloOid());
                yhteyshenkiloModel.setHenkiloTyyppi(henk.getHenkiloTyyppi());
                break;
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

    public static WebLinkkiTyyppi mapToWebLinkkiTyyppiDto(final KoulutusLinkkiViewModel model) throws OidCreationException {
        WebLinkkiTyyppi web = new WebLinkkiTyyppi();
        web.setKieli(model.getKieli());
        web.setTyyppi(model.getLinkkityyppi());
        web.setUri(model.getUrl());

        return web;
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
            //allow null object
            return null;
        }

        if (model != null) {
            throw new RuntimeException("KoulutusKoodistoModel cannot be null!");
        } else {
            throw new RuntimeException("KoulutusKoodistoModel koodisto URI cannot be null!");
        }
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
        kysely.setKoulutuskoodiUri(koulutuskoodi.getKoodistoUri());

        String childTutkintoUri = null;

        if (model != null && model.getKoodistoUri() != null) {
            childTutkintoUri = model.getKoodistoUri();
        }

        switch (aste) {
            case AMMATILLINEN_PERUSKOULUTUS:
                kysely.setKoulutusohjelmakoodiUri(childTutkintoUri);
                break;
            case LUKIOKOULUTUS:
                kysely.setLukiolinjakoodiUri(childTutkintoUri);
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

    public static Set<String> convertListToSet(final List<KoodistoKoodiTyyppi> opetuskieliKoodit) {
        Set<String> set = Sets.<String>newHashSet();
        for (KoodistoKoodiTyyppi curKoodi : opetuskieliKoodit) {
            set.add(curKoodi.getUri());
        }
        return set;
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
    public static MonikielinenTekstiTyyppi.Teksti convertToMonikielinenTekstiTyyppi(final String languageUri, final String teksti) {
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
            tyyppi.getTeksti().add(convertToMonikielinenTekstiTyyppi(nimi.getKielikoodi(), nimi.getNimi()));
        }

        return tyyppi;
    }

    public static MonikielinenTekstiTyyppi mapToMonikielinenTekstiTyyppi(final String languageUri, final String teksti) {
        MonikielinenTekstiTyyppi tyyppi = new MonikielinenTekstiTyyppi();
        tyyppi.getTeksti().add(convertToMonikielinenTekstiTyyppi(languageUri, teksti));
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

    public static Set<KielikaannosViewModel> convertToKielikaannosViewModel(final MonikielinenTekstiTyyppi tyyppi) {
        Set<KielikaannosViewModel> model = new HashSet<KielikaannosViewModel>();

        for (MonikielinenTekstiTyyppi.Teksti teksti : tyyppi.getTeksti()) {
            model.add(new KielikaannosViewModel(teksti.getKieliKoodi(), teksti.getValue()));
        }

        return model;
    }

    public static Map<String, List<KoulutusmoduuliKoosteTyyppi>> komoCacheMapByKoulutuskoodi(Collection<KoulutusmoduuliKoosteTyyppi> komos) {
        Map<String, List<KoulutusmoduuliKoosteTyyppi>> map = new HashMap<String, List<KoulutusmoduuliKoosteTyyppi>>();

        for (KoulutusmoduuliKoosteTyyppi komo : komos) {
            //remove version hashtag from koulutus uris
            final String uri = TarjontaKoodistoHelper.getKoodiURIFromVersionedUri(komo.getKoulutuskoodiUri());

            if (map.containsKey(uri)) {
                map.get(uri).add(komo);
            } else {
                List<KoulutusmoduuliKoosteTyyppi> l = new ArrayList<KoulutusmoduuliKoosteTyyppi>();
                l.add(komo);
                map.put(uri, l);
            }
        }
        return map;

    }

    /**
     * Data in KoulutusmoduuliKoosteTyyppi object will be store in komoto -table
     * in DB.
     */
    public static void convertCommonModelToKoulutusmoduuliKoosteTyyppi(KoulutuskoodiModel km, MonikielinenTekstiModel mtk, KoulutusTyyppi toTyyppi) {
        Preconditions.checkNotNull(km, "KoulutuskoodiModel object cannot be null.");
        Preconditions.checkNotNull(toTyyppi, "KoulutusTyyppi object cannot be null.");
        KoulutusmoduuliKoosteTyyppi kooste = toTyyppi.getKoulutusmoduuli();

        if (kooste == null) {
            kooste = new KoulutusmoduuliKoosteTyyppi();
            toTyyppi.setKoulutusmoduuli(kooste);
        }

        /*
         * KoulutuskoodiModel to KoulutusmoduuliKoosteTyyppi
         */
        if (mtk != null) {
            if (mtk instanceof LukiolinjaModel) {
                final LukiolinjaModel lm = (LukiolinjaModel) mtk;

                if (lm.getKoodistoUriVersio() != null) {
                    kooste.setLukiolinjakoodiUri(lm.getKoodistoUriVersio());
                }

                if (lm.getKoulutuslaji() != null) {
                    //TODO : add field name Koulutuslaji to KoulutusmoduuliKoosteTyyppi 
                }

            } else if (mtk instanceof KoulutusohjelmaModel) {
                final KoulutusohjelmaModel ko = (KoulutusohjelmaModel) mtk;

                if (ko.getKoodistoUriVersio() != null) {
                    kooste.setKoulutusohjelmakoodiUri(ko.getKoodistoUriVersio());
                }

                if (ko.getTutkintonimike() != null && ko.getTutkintonimike().getKoodistoUriVersio() != null) {
                    kooste.setTutkintonimikeUri(ko.getTutkintonimike().getKoodistoUriVersio());
                }
            }
        }

        if (km != null) {

            if (km.getKoodistoUriVersio() != null) {
                kooste.setKoulutuskoodiUri(km.getKoodistoUriVersio());
            }

            if (km.getKoulutusala() != null) {
                kooste.setKoulutusalaUri(km.getKoulutusala().getKoodistoUriVersio());
            }

            if (km.getOpintoala() != null) {
                kooste.setOpintoalaUri(km.getOpintoala().getKoodistoUriVersio());
            }

            if (km.getTutkintonimike() != null) {
                kooste.setTutkintonimikeUri(km.getTutkintonimike().getKoodistoUriVersio());
            }

            if (km.getOpintojenLaajuusyksikko() != null) {
                kooste.setLaajuusyksikkoUri(km.getOpintojenLaajuusyksikko().getKoodistoUriVersio());
            }

            if (km.getOpintojenLaajuus() != null) {
                kooste.setLaajuusarvoUri(km.getOpintojenLaajuus().getKoodistoUriVersio());
            }

            if (km.getKoulutusaste() != null) {
                kooste.setKoulutusasteUri(km.getKoulutusaste().getKoodistoUriVersio());
            }
        }

    }
}
