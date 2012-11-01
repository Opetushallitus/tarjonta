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

import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.SearchKoodisByKoodistoCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.tarjonta.service.TarjontaKoodistoService;
import fi.vm.sade.tarjonta.service.types.koodisto.KoodiHakuTyyppi;
import fi.vm.sade.tarjonta.service.types.koodisto.KoulutusHakuTyyppi;
import fi.vm.sade.tarjonta.service.types.koodisto.KoulutusasteVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.koodisto.KoulutuskoodiVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.koodisto.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.koodisto.KoulutuskoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.koodisto.KoulutusohjelmaTyyppi;
import fi.vm.sade.tarjonta.service.types.koodisto.KoulutusohjelmaVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.koodisto.Nimi;
import fi.vm.sade.tarjonta.ui.enums.KoulutusType;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Jani Wilén
 */
@Component
public class KoulutusKoodistoConverter implements TarjontaKoodistoService {

    private final String LUKIO = KoulutusType.TOINEN_ASTE_LUKIO.getKoulutusaste();
    private final String AMMATILLINEN = KoulutusType.TOINEN_ASTE_AMMATILLINEN_KOULUTUS.getKoulutusaste();
    private static final String URI_KOULUTUSKOODI = "Koulutusluokitus";
    private static final String URI_KOULUTUSOHJELMA = "YO-koulutusohjelma";
    @Autowired(required = true)
    private KoodiService koodiPublicService;

    public KoulutusKoodistoConverter() {
        super();
    }

    @Override
    public KoulutusasteVastausTyyppi listaaKoulutusasteet(KoulutusHakuTyyppi parameters) {
        KoulutusasteVastausTyyppi response = new KoulutusasteVastausTyyppi();
        List<KoodiType> koodistoData = getKoodistoData("opm02_koulutusaste");
        response.getKoulutusaste().addAll(mapKoulutusaste(parameters.getKieliKoodi(), koodistoData));
        return response;
    }

    public KoulutusasteTyyppi listaaKoulutusaste(KoodiHakuTyyppi parameters) {
        List<KoodiType> koodistoData = null;
        final int version = parameters.getKoodistoVersio();

        if (parameters.getKoodistoUri() != null) {
            koodistoData = getKoodistoData(parameters.getKoodistoUri(), version);
        } else if (parameters.getKoodistoArvo() != null) {
            koodistoData = getKoodistoData(URI_KOULUTUSKOODI, parameters.getKoodistoArvo(), version);
        }

        if (koodistoData != null && !koodistoData.isEmpty()) {
            List<KoulutusasteTyyppi> list = mapKoulutusaste(parameters.getKieliKoodi(), koodistoData);

            if (!list.isEmpty() && list.size() > 0) {
                return list.get(0);
            }
        }

        return null;
    }

    @Override
    public KoulutuskoodiTyyppi listaaKoulutuskoodi(KoodiHakuTyyppi parameters) {
        List<KoodiType> koodistoData = null;
        final int version = parameters.getKoodistoVersio();

        if (parameters.getKoodistoUri() != null) {
            koodistoData = getKoodistoData(parameters.getKoodistoUri(), version);
        } else if (parameters.getKoodistoArvo() != null) {
            koodistoData = getKoodistoData(URI_KOULUTUSKOODI, parameters.getKoodistoArvo(), version);
        }

        if (koodistoData != null && !koodistoData.isEmpty()) {
            List<KoulutuskoodiTyyppi> list = mapKoulutuskoodi(parameters.getKieliKoodi(), null, koodistoData);

            if (!list.isEmpty() && list.size() > 0) {
                return list.get(0);
            }
        }

        return null;
    }

    @Override
    public KoulutuskoodiVastausTyyppi listaaKoulutuskoodit(KoulutusHakuTyyppi parameters) {
        final String koulutusasteKoodi = parameters.getKoulutusasteKoodi();
        KoulutusType type = null;
        if (koulutusasteKoodi != null) {
            //TODO: better koodisto data filter.
            //A simple and ugly filter, fix it after koodisto has better data.
            for (KoulutusType t : KoulutusType.values()) {
                if (t.getKoulutusaste().equals(koulutusasteKoodi)) {
                    type = t;
                    break;
                }
            }

            if (type == null) {
                throw new RuntimeException("Koulutus type is required.");
            }
        }

        List<KoodiType> koodistoData = getKoodistoData(URI_KOULUTUSKOODI);
        KoulutuskoodiVastausTyyppi response = new KoulutuskoodiVastausTyyppi();
        final String kieliKoodi = parameters.getKieliKoodi();
        response.getKoulutuskoodi().addAll(mapKoulutuskoodi(kieliKoodi, type, koodistoData));

        return response;
    }

    @Override
    public KoulutusohjelmaTyyppi listaaKoulutusohjelma(KoodiHakuTyyppi parameters) {
        List<KoodiType> koodistoData = null;
        int version = parameters.getKoodistoVersio();

        if (parameters.getKoodistoUri() != null) {
            koodistoData = getKoodistoData(parameters.getKoodistoUri(), version);
        } else if (parameters.getKoodistoArvo() != null) {
            koodistoData = getKoodistoData(URI_KOULUTUSOHJELMA, parameters.getKoodistoArvo(), version);
        }

        if (koodistoData != null && !koodistoData.isEmpty()) {
            List<KoulutusohjelmaTyyppi> list = mapKoulutusohjelma(parameters.getKieliKoodi(), koodistoData);

            if (!list.isEmpty() && list.size() > 0) {
                return list.get(0);
            }
        }

        return null;
    }

    @Override
    public KoulutusohjelmaVastausTyyppi listaaKoulutusohjelmat(KoulutusHakuTyyppi parameters) {
        List<KoodiType> koodistoData = getKoodistoData(URI_KOULUTUSOHJELMA);
        KoulutusohjelmaVastausTyyppi response = new KoulutusohjelmaVastausTyyppi();
        response.getKoulutusohjelma().addAll(mapKoulutusohjelma(parameters.getKieliKoodi(), koodistoData));
        return response;
    }

    /*
     * 
     * HELPER METHODS:
     * 
     */
    private List<KoodiType> getKoodistoData(String uri, String arvo, int version) {
        SearchKoodisByKoodistoCriteriaType criteria = KoodiServiceSearchCriteriaBuilder.koodisByArvoAndKoodistoUriAndKoodistoVersio(arvo, uri, version);
        return koodiPublicService.searchKoodisByKoodisto(criteria);
    }

    private List<KoodiType> getKoodistoData(String uri, int version) {
        SearchKoodisByKoodistoCriteriaType criteria = KoodiServiceSearchCriteriaBuilder.koodisByKoodistoUriAndKoodistoVersio(uri, version);
        return koodiPublicService.searchKoodisByKoodisto(criteria);
    }

    private List<KoodiType> getKoodistoData(String uri) {
        SearchKoodisByKoodistoCriteriaType criteriUri = KoodiServiceSearchCriteriaBuilder.koodisByKoodistoUri(uri);
        return koodiPublicService.searchKoodisByKoodisto(criteriUri);
    }

    private List<Nimi> kieli(final List<KoodiMetadataType> languageMetaData) {
        List<Nimi> nimet = new ArrayList<Nimi>();

        for (KoodiMetadataType meta : languageMetaData) {
            final KieliType kieli = meta.getKieli();
            Nimi nimi = new Nimi();

            if (kieli != null) {
                nimi.setKieli(kieli.value() != null ? kieli.value().toLowerCase() : null);
                nimi.setValue(meta.getNimi());
                nimet.add(nimi);
            }
        }

        return nimet;
    }

    private String kieli(final List<KoodiMetadataType> languageMetaData, final String kieliKoodi) {
        for (KoodiMetadataType koodiMeta : languageMetaData) {
            final KieliType kieli = koodiMeta.getKieli();
            if (kieli != null && kieliKoodi.toLowerCase().equals(kieli.value().toLowerCase())) {
                return koodiMeta.getNimi();
            }
        }

        return null;
    }

    private List<KoulutusasteTyyppi> mapKoulutusaste(final String kieliKoodi, final List<KoodiType> koodit) {
        List<KoulutusasteTyyppi> outKoulutusaste = new ArrayList<KoulutusasteTyyppi>();

        KoulutusType.TOINEN_ASTE_AMMATILLINEN_KOULUTUS.getKoulutusaste();
        for (KoodiType koodiType : koodit) {

            //A very simple way to filter 'koulutusasteet'.
            if (koodiType.getKoodiArvo().equals(LUKIO)
                    || koodiType.getKoodiArvo().equals(AMMATILLINEN)) {

                KoulutusasteTyyppi outKoulutus = new KoulutusasteTyyppi();
                outKoulutus.setKoulutusasteKoodi(koodiType.getKoodiArvo());
                outKoulutus.setKoodistoUri(koodiType.getKoodiUri());
                outKoulutus.setKoodistoVersio(koodiType.getVersio());
                outKoulutusaste.add(outKoulutus);

                if (kieliKoodi != null) {
                    outKoulutus.setKoulutusasteNimi(kieli(koodiType.getMetadata(), kieliKoodi));
                } else {
                    outKoulutus.getNimi().addAll(kieli(koodiType.getMetadata()));
                }
            }
        }

        return outKoulutusaste;
    }

    private List<KoulutusohjelmaTyyppi> mapKoulutusohjelma(final String kieliKoodi, final List<KoodiType> koodit) {
        List<KoulutusohjelmaTyyppi> outKoulutusohjelma = new ArrayList<KoulutusohjelmaTyyppi>();

        for (KoodiType koodiType : koodit) {
            KoulutusohjelmaTyyppi outKoulutus = new KoulutusohjelmaTyyppi();
            outKoulutus.setKoulutusohjelmaKoodi(koodiType.getKoodiArvo());
            outKoulutus.setKoodistoUri(koodiType.getKoodiUri());
            outKoulutus.setKoodistoVersio(koodiType.getVersio());
            outKoulutusohjelma.add(outKoulutus);

            if (kieliKoodi != null) {
                outKoulutus.setKoulutusohjelmaNimi(kieli(koodiType.getMetadata(), kieliKoodi));
            } else {
                outKoulutus.getNimi().addAll(kieli(koodiType.getMetadata()));
            }
        }

        return outKoulutusohjelma;
    }

    private List<KoulutuskoodiTyyppi> mapKoulutuskoodi(final String kieliKoodi, final KoulutusType type, final List<KoodiType> koodit) {
        List<KoulutuskoodiTyyppi> outKoulutusaste = new ArrayList<KoulutuskoodiTyyppi>();

        for (KoodiType koodiType : koodit) {
            if (type == null) {
                KoulutuskoodiTyyppi outKoulutus = new KoulutuskoodiTyyppi();
                outKoulutus.setKoulutuskoodi(koodiType.getKoodiArvo());
                outKoulutus.setKoodistoUri(koodiType.getKoodiUri());
                outKoulutus.setKoodistoVersio(koodiType.getVersio());
                outKoulutusaste.add(outKoulutus);

                if (kieliKoodi != null) {
                    outKoulutus.setKoulutuskoodiNimi(kieli(koodiType.getMetadata(), kieliKoodi));
                } else {
                    outKoulutus.getNimi().addAll(kieli(koodiType.getMetadata()));
                }
            } else if (koodiType.getKoodiArvo().startsWith(type.getKoulutuskoodiFilter())) {
                //TODO: fix this after koodisto references are finalised. 
                //A bad way to filter koodisto data. 
                KoulutuskoodiTyyppi outKoulutus = new KoulutuskoodiTyyppi();
                outKoulutus.setKoulutuskoodi(koodiType.getKoodiArvo());
                outKoulutus.setKoodistoUri(koodiType.getKoodiUri());
                outKoulutus.setKoodistoVersio(koodiType.getVersio());
                outKoulutusaste.add(outKoulutus);

                if (kieliKoodi != null) {
                    outKoulutus.setKoulutuskoodiNimi(kieli(koodiType.getMetadata(), kieliKoodi));
                } else {
                    outKoulutus.getNimi().addAll(kieli(koodiType.getMetadata()));
                }
            }
        }

        return outKoulutusaste;
    }
}
