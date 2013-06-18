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
package fi.vm.sade.tarjonta.ui.loader.xls;

import com.google.common.base.Preconditions;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.ExcelMigrationDTO;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.oid.service.types.NodeClassCode;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.types.GeneerinenTilaTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import static fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS;
import static fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi.LUKIOKOULUTUS;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTulos;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi.Teksti;
import fi.vm.sade.tarjonta.service.types.PaivitaTilaTyyppi;
import fi.vm.sade.tarjonta.service.types.SisaltoTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.conversion.SearchWordUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Alustaa tarjontaan esimerkki dataa. Poistetaan kun Koodistosta saadaan
 * tarvittava koodien relaatioihin perustuvat data.
 */
@Component
public class TarjontaKomoData {

    private static final Logger log = LoggerFactory.getLogger(TarjontaKomoData.class);
    private static final boolean USE_UPDATE = true; //unready feature
    private Map<KoulutusasteTyyppi, Map<String, String>> kKoodiToKomoOid;
    @Autowired
    private TarjontaKoodistoHelper tarjontaKoodistoHelper;
    @Autowired
    private KoodiService koodiService;
    @Autowired(required = true)
    private OIDService oidService;
    @Autowired(required = true)
    private TarjontaAdminService tarjontaAdminService;
    @Autowired(required = true)
    private TarjontaPublicService tarjontaPublicService;
    private Set<ExcelMigrationDTO> loadedData;
    private static String SEPARATOR = "#";
    private List<String> komoOids; //updated, inserted
    //a tempate for lukio or ammatillinen koulutus
    private Map<String, KoodiType> map = new HashMap<String, KoodiType>();

    public void preLoadAllKoodistot() {
        String[] koodistot = new String[]{KoodistoURIHelper.KOODISTO_TUTKINTO_URI,
            KoodistoURIHelper.KOODISTO_KOULUTUSOHJELMA_URI,
            KoodistoURIHelper.KOODISTO_KOULUTUSASTE_URI,
            KoodistoURIHelper.KOODISTO_KOULUTUSALA_URI,
            KoodistoURIHelper.KOODISTO_TUTKINTONIMIKE_URI,
            KoodistoURIHelper.KOODISTO_OPINTOALA_URI,
            KoodistoURIHelper.KOODISTO_OPINTOJEN_LAAJUUSYKSIKKO_URI,
            KoodistoURIHelper.KOODISTO_LUKIOLINJA_URI,
            KoodistoURIHelper.KOODISTO_OPPILAITOSTYYPPI_URI
        };

        for (String koodisto : koodistot) {
            log.info("-------------------------------------------------");
            log.info("Loading koodisto : '" + koodisto + "'");

            List<KoodiType> result = koodiService.searchKoodisByKoodisto(KoodiServiceSearchCriteriaBuilder.koodisByKoodistoUri(koodisto));

            for (KoodiType type : result) {
                final String createKey = createUniqueKey(type.getKoodiArvo(), koodisto);

                if (map.containsKey(createKey)) {
                    log.debug("Already contains koodi : '" + type.getKoodiArvo() + "', uri:'" + type.getKoodiUri() + "', koodisto : '" + koodisto + "'");
                } else {
                    //make unique key by koodisto and koodi
                    //log.debug("Add koodi : '" + type.getKoodiArvo() + "', uri:'" + type.getKoodiUri() + "', koodisto : '" + koodisto + "'");

                    map.put(createKey, type);
                }
            }
            log.info("Loaded koodis :" + result.size());
            log.info("-------------------------------------------------");
        }
    }

    public void createData(boolean saveChanges) throws IOException, ExceptionMessage {
        log.info("Starting to load KOMOs from Excel file");
        DataReader dataReader = new DataReader();
        loadedData = dataReader.getData();
        komoOids = new ArrayList<String>();

        log.info("Excel files merged, now try to create all KOMOs.");

        //init tutkinto hashmap
        kKoodiToKomoOid = new EnumMap<KoulutusasteTyyppi, Map<String, String>>(KoulutusasteTyyppi.class);
        kKoodiToKomoOid.put(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS, new HashMap<String, String>());
        kKoodiToKomoOid.put(KoulutusasteTyyppi.LUKIOKOULUTUS, new HashMap<String, String>());

        int count = 1;

        for (ExcelMigrationDTO dto : loadedData) {
            if (count % 10 == 1) {
                log.info("Processing... {}", count);
            }
            LOS komo = createKomo(dto);

            KoulutusmoduuliTulos searchChildKomo = null;
            final KoulutusasteTyyppi koulutusTyyppi = dto.getKoulutusTyyppi();

            switch (koulutusTyyppi) {
                case AMMATILLINEN_PERUSKOULUTUS:
                    searchChildKomo = searchKomo(koulutusTyyppi, komo.getChildren().getKoulutuskoodiUri(), komo.getChildren().getKoulutusohjelmakoodiUri());
                    log.info("Search : {} {}", komo.getChildren().getKoulutuskoodiUri(), komo.getChildren().getKoulutusohjelmakoodiUri());
                    break;
                case LUKIOKOULUTUS:
                    searchChildKomo = searchKomo(koulutusTyyppi, komo.getChildren().getKoulutuskoodiUri(), komo.getChildren().getLukiolinjakoodiUri());
                    break;
            }

            if (USE_UPDATE && saveChanges && searchChildKomo != null) {
                log.info("Update : {} {}", komo.getChildren().getKoulutuskoodiUri(), searchChildKomo.getKoulutusmoduuli().getOid());
                //update parent
                komo.getParent().setOid(searchChildKomo.getKoulutusmoduuli().getParentOid());
                tarjontaAdminService.paivitaKoulutusmoduuli(komo.getParent());

                //update children
                komo.getChildren().setOid(searchChildKomo.getKoulutusmoduuli().getOid());
                tarjontaAdminService.paivitaKoulutusmoduuli(komo.getChildren());
            } else if (saveChanges) {


                KoulutusmoduuliKoosteTyyppi tKomo = tarjontaAdminService.lisaaKoulutusmoduuli(komo.getParent());
                if (tKomo.getOid() != null) {
                    //create komo and update oid to map
                    getKoodiToKomoOid(koulutusTyyppi).put(tKomo.getKoulutuskoodiUri(), tKomo.getOid());
                } else {
                    //NO OID! I guess the KOMO was created earlier and this was a batch update...
                    log.info("koulutusTyyppi : {}", koulutusTyyppi);

                    final KoulutusmoduuliTulos resultTutkintoKomo = searchKomo(koulutusTyyppi, komo.getChildren().getKoulutuskoodiUri(), null);
                    final String parentOid = resultTutkintoKomo.getKoulutusmoduuli().getOid();
                    final String koulutuskoodiUri = resultTutkintoKomo.getKoulutusmoduuli().getKoulutuskoodiUri();

                    log.info("Load parent OID : {}, {}", parentOid, koulutuskoodiUri);
                    getKoodiToKomoOid(koulutusTyyppi).put(koulutuskoodiUri, parentOid);
                }

                final String mapParentOid = getKoodiToKomoOid(koulutusTyyppi).get(komo.getParent().getKoulutuskoodiUri());
                log.info("Map parent OID : {}", mapParentOid);
                komo.getChildren().setParentOid(mapParentOid);
                tarjontaAdminService.lisaaKoulutusmoduuli(komo.getChildren());
                log.info("Add : {} {}", komo.getParent(), tKomo.getOid());
            }
            //add oids to list, the oids will be used for change udate status to published
            komoOids.add(komo.getParent().getOid());
            komoOids.add(komo.getChildren().getOid());
            count++;
        }
        log.info("Total count of the imported KOMOs : {}", count);

        log.info("Publish KOMOs");
        PaivitaTilaTyyppi paivitaTilaTyyppi = new PaivitaTilaTyyppi();

        for (String oid : komoOids) {
            GeneerinenTilaTyyppi geneerinenTilaTyyppi = new GeneerinenTilaTyyppi();
            geneerinenTilaTyyppi.setOid(oid);
            geneerinenTilaTyyppi.setSisalto(SisaltoTyyppi.KOMO);
            geneerinenTilaTyyppi.setTila(TarjontaTila.JULKAISTU);
            paivitaTilaTyyppi.getTilaOids().add(geneerinenTilaTyyppi);
        }

        tarjontaAdminService.paivitaTilat(paivitaTilaTyyppi);

        log.info("Process ended.");
    }

    private KoodiType getKoodiType(final String koodiArvo, final String koodisto, final String fallbackKey) {
        final String searchKey = createUniqueKey(koodiArvo, koodisto);

        if (map.containsKey(searchKey)) {
            return map.get(searchKey);
        } else if (fallbackKey != null) {
            final String createUniqueKey = createUniqueKey(fallbackKey, koodisto);

            log.error("Using fallback key, real value not found by : '" + koodiArvo + "' -> '" + fallbackKey + "'," + koodisto);
            final KoodiType koodiType = map.get(createUniqueKey);
            log.error("fallback koodi object : '" + createUniqueKey + "' | '" + fallbackKey + "', " + koodiType);
            return koodiType;
        } else {
            log.error("Koodi not found by : '" + koodiArvo + "'," + koodisto);
            throw new RuntimeException("Koodi not found by : '" + koodiArvo + "'," + koodisto);
        }
    }

    private String getUriWithVersion(final String koodiArvo, final String koodisto) {
        final KoodiType koodiType = getKoodiType(koodiArvo, koodisto, null);
        //search and create the real koodi uri  

        if (koodiType != null) {
            return koodiType.getKoodiUri() + SEPARATOR + koodiType.getVersio();
        }

        return null;
    }

    private String getUriWithVersion(final String koodiArvo, final String koodisto, final String fallbackKey) {
        final KoodiType koodiType = getKoodiType(koodiArvo, koodisto, fallbackKey);
        //search and create the real koodi uri  

        if (koodiType != null) {
            return koodiType.getKoodiUri() + SEPARATOR + koodiType.getVersio();
        }

        return null;
    }

    private List<KoodiMetadataType> getKoodiMetadataTypes(final String koodiArvo, final String koodisto) {
        return getKoodiMetadataTypes(koodiArvo, koodisto, null);
    }

    private List<KoodiMetadataType> getKoodiMetadataTypes(final String koodiArvo, final String koodisto, final String fallbackKey) {


        if (koodiArvo == null) {
            throw new IllegalArgumentException("Koodi value cannot be null in koodisto '" + koodisto + "'");
        }

        if (koodisto == null) {
            throw new IllegalArgumentException("Koodisto cannot be null!");
        }

        final KoodiType koodiType = getKoodiType(koodiArvo, koodisto, fallbackKey);
        //search and create the real koodi uri  

        if (koodiType != null) {
            return koodiType.getMetadata();
        }

        return null;
    }

    private static String createUniqueKey(final String value, final String koodisto) {
        if (koodisto == null) {
            throw new IllegalArgumentException("Koodisto uri cannot be null! Koodi value was " + value + ".");
        }

        return (new StringBuffer(koodisto)).append(SEPARATOR).append(value).toString();
    }

    public static MonikielinenTekstiTyyppi createTeksti(String fiTeksti, String svTeksti, String enTeksti) {
        MonikielinenTekstiTyyppi t = new MonikielinenTekstiTyyppi();
        addLang("kieli_fi", fiTeksti, t);
        addLang("kieli_en", enTeksti, t);
        addLang("kieli_sv", svTeksti, t);
        return t;
    }

    private static void addLang(final String lang, final String text, MonikielinenTekstiTyyppi t) {
        if (text != null) {
            Teksti teksti = new MonikielinenTekstiTyyppi.Teksti();
            teksti.setKieliKoodi(lang);
            teksti.setValue(text);
            t.getTeksti().add(teksti);
        }
    }

    public Set<ExcelMigrationDTO> getLoadedData() {
        return loadedData;
    }

    /**
     * Create full KoulutusmoduuliKoosteTyyppi object.
     *
     * @param dto
     * @return
     * @throws ExceptionMessage
     */
    private LOS createKomo(final ExcelMigrationDTO dto) throws ExceptionMessage {
        //base values
        Preconditions.checkNotNull(dto.getKoulutuskoodiKoodiarvo(), "Import data error - koulutuskoodi value cannot be null!");
        final String koulutuskoodiUri = getUriWithVersion(dto.getKoulutuskoodiKoodiarvo(), KoodistoURIHelper.KOODISTO_TUTKINTO_URI);

        KoulutusmoduuliKoosteTyyppi tutkintoKomo = new KoulutusmoduuliKoosteTyyppi();
        tutkintoKomo.setOid(this.oidService.newOid(NodeClassCode.TEKN_5));
        tutkintoKomo.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO);

        //search Uris from Koodisto for komo
        tutkintoKomo.setKoulutuskoodiUri(koulutuskoodiUri);
        tutkintoKomo.setOpintoalaUri(getUriWithVersion(dto.getOpintoalaKoodi(), KoodistoURIHelper.KOODISTO_OPINTOALA_URI));  //Automaalari
        tutkintoKomo.setKoulutusalaUri(getUriWithVersion(dto.getKoulutusalaKoodi(), KoodistoURIHelper.KOODISTO_KOULUTUSALA_URI));
        tutkintoKomo.setKoulutusasteUri(getUriWithVersion(dto.getKoulutusasteenKoodiarvo(), KoodistoURIHelper.KOODISTO_KOULUTUSASTE_URI));
        tutkintoKomo.setLaajuusyksikkoUri(getUriWithVersion(dto.getLaajuusyksikko(), KoodistoURIHelper.KOODISTO_OPINTOJEN_LAAJUUSYKSIKKO_URI)); //OV,OP           
        tutkintoKomo.setLaajuusarvoUri(dto.getLaajuusUri()); //120, not a koodisto value
        tutkintoKomo.setKoulutustyyppi(dto.getKoulutusTyyppi());
        /*
         * Description data for tutkinto (nothing to do with the Koodisto service)
         * LUKIO and AMMATILLINEN koulutus
         */
        tutkintoKomo.setKoulutuksenRakenne(dto.getTutkinnonKuvaukset().getKoulutuksenRakenneTeksti());
        tutkintoKomo.setTavoitteet(dto.getTutkinnonKuvaukset().getTavoiteTeksti());
        tutkintoKomo.setJatkoOpintoMahdollisuudet(dto.getTutkinnonKuvaukset().getJatkoOpintomahdollisuudetTeksti());

        /*
         * Oppilaitostyyppi
         */
        tutkintoKomo.getOppilaitostyyppi().clear();
        for (String codeValue : dto.getOppilaitostyyppis()) {
            tutkintoKomo.getOppilaitostyyppi().add(getUriWithVersion(codeValue, KoodistoURIHelper.KOODISTO_OPPILAITOSTYYPPI_URI));
        }
        //create search words from Koodisto meta data 
        List<KoodiMetadataType> koulutuskoodiMeta = getKoodiMetadataTypes(dto.getKoulutuskoodiKoodiarvo(), KoodistoURIHelper.KOODISTO_TUTKINTO_URI);

        KoulutusmoduuliKoosteTyyppi koKomo = new KoulutusmoduuliKoosteTyyppi();
        switch (dto.getKoulutusTyyppi()) {
            case AMMATILLINEN_PERUSKOULUTUS:
                final String koulutusohjelmanKoodiarvo = dto.getKoulutusohjelmanKoodiarvo();
                Preconditions.checkNotNull(koulutusohjelmanKoodiarvo, "Koulutusohjelma koodi uri cannot be null.");

                final String fallbackValue = koulutusohjelmanKoodiarvo.substring(0, 4);
                List<KoodiMetadataType> koulutusohjelmaMeta = getKoodiMetadataTypes(koulutusohjelmanKoodiarvo, KoodistoURIHelper.KOODISTO_KOULUTUSOHJELMA_URI, fallbackValue);

                koKomo.setKoulutusmoduulinNimi(SearchWordUtil.createSearchKeywords(koulutuskoodiMeta, koulutusohjelmaMeta, tarjontaKoodistoHelper));
                koKomo.setKoulutusohjelmakoodiUri(getUriWithVersion(dto.getKoulutusohjelmanKoodiarvo(), KoodistoURIHelper.KOODISTO_KOULUTUSOHJELMA_URI, fallbackValue));

                /*
                 * Description data for koulutusohjelma (nothing to do with the Koodisto service)
                 */
                koKomo.setTavoitteet(dto.getKoulutusohjelmanKuvaukset().getTavoiteTeksti());
                break;
            case LUKIOKOULUTUS:
                Preconditions.checkNotNull(dto.getLukiolinjaKoodiarvo(), "Lukiolinja koodi uri cannot be null.");

                List<KoodiMetadataType> lukiolinjaMeta = getKoodiMetadataTypes(dto.getLukiolinjaKoodiarvo(), KoodistoURIHelper.KOODISTO_LUKIOLINJA_URI);
                koKomo.setKoulutusmoduulinNimi(SearchWordUtil.createSearchKeywords(koulutuskoodiMeta, lukiolinjaMeta, tarjontaKoodistoHelper));
                koKomo.setLukiolinjakoodiUri(getUriWithVersion(dto.getLukiolinjaKoodiarvo(), KoodistoURIHelper.KOODISTO_LUKIOLINJA_URI));
                break;
        }

        koKomo.setKoulutuskoodiUri(koulutuskoodiUri);
        koKomo.setOid(this.oidService.newOid(NodeClassCode.TEKN_5));
        koKomo.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);

        Preconditions.checkNotNull(dto.getTutkintonimikkeenKoodiarvo(), "Tutkintonimike koodi uri cannot be null. Obj : " + dto);
        koKomo.setTutkintonimikeUri(getUriWithVersion(dto.getTutkintonimikkeenKoodiarvo(), KoodistoURIHelper.KOODISTO_TUTKINTONIMIKE_URI, "00000")); //00000 -> empty line
        koKomo.setParentOid(tutkintoKomo.getOid());
        koKomo.setKoulutustyyppi(dto.getKoulutusTyyppi());
        koKomo.setEqfLuokitus(dto.getEqfUri());

        return new LOS(tutkintoKomo, koKomo);
    }

    private KoulutusmoduuliTulos searchKomo(final KoulutusasteTyyppi koulutusasteTyyppi, final String koulutuskoodi, final String koodi) {
        HaeKoulutusmoduulitKyselyTyyppi kysely = new HaeKoulutusmoduulitKyselyTyyppi();
        kysely.setKoulutustyyppi(koulutusasteTyyppi);
        kysely.setKoulutuskoodiUri(koulutuskoodi);

        switch (koulutusasteTyyppi) {
            case AMMATILLINEN_PERUSKOULUTUS:
                kysely.setKoulutusohjelmakoodiUri(koodi);
                break;
            case LUKIOKOULUTUS:
                kysely.setLukiolinjakoodiUri(koodi);
                break;
        }
        log.info(koulutusasteTyyppi + " - search KOMO by '{}' and '{}'", kysely.getKoulutuskoodiUri(), koodi);


        HaeKoulutusmoduulitVastausTyyppi result = tarjontaPublicService.haeKoulutusmoduulit(kysely);
        List<KoulutusmoduuliTulos> koulutusmoduuliTulos = result.getKoulutusmoduuliTulos();

        if (koulutusmoduuliTulos != null && !koulutusmoduuliTulos.isEmpty()) {
            if (koulutusmoduuliTulos.size() > 1) {
                for (KoulutusmoduuliTulos t : koulutusmoduuliTulos) {
                    log.warn("KoulutusmoduuliTulos : {} {}", t.getKoulutusmoduuli().getKoulutuskoodiUri(), t.getKoulutusmoduuli().getKoulutusohjelmakoodiUri());

                    if (koodi == null && t.getKoulutusmoduuli().getKoulutusohjelmakoodiUri() == null) {
                        //a quick hack: as there is no way to search only 'TUTKINTO' -type of komos.
                        return t;
                    }
                }

                throw new RuntimeException("Search found too many KOMOs - single KOMO was expected. Result size : " + koulutusmoduuliTulos.size());
            }

            //log.info("Founded : {}, {}", koulutusmoduuliTulos.get(0).getKoulutusmoduuli().getKoulutuskoodiUri(), koulutusmoduuliTulos.get(0).getKoulutusmoduuli().getKoulutusohjelmakoodiUri());
            return koulutusmoduuliTulos.get(0);
        }


        return null;
    }

    /*
     * Helper class
     */
    private class LOS {

        private KoulutusmoduuliKoosteTyyppi parent;
        private KoulutusmoduuliKoosteTyyppi children;

        public LOS(KoulutusmoduuliKoosteTyyppi parent, KoulutusmoduuliKoosteTyyppi children) {
            if (parent == null) {
                throw new IllegalArgumentException("KoulutusmoduuliKoosteTyyppi parent 'tutkinto' cannot be null!");
            }

            if (children == null) {
                throw new IllegalArgumentException("KoulutusmoduuliKoosteTyyppi child cannot be null!");
            }

            this.parent = parent;
            this.children = children;
        }

        /**
         * Type : Tutkinto
         *
         * @return the parent
         */
        public KoulutusmoduuliKoosteTyyppi getParent() {
            return parent;
        }

        /**
         * Type : Koulutusohjelma
         *
         * @param parent the parent to set
         */
        public void setParent(KoulutusmoduuliKoosteTyyppi parent) {
            this.parent = parent;
        }

        /**
         * @return the children
         */
        public KoulutusmoduuliKoosteTyyppi getChildren() {
            return children;
        }

        /**
         * @param children the children to set
         */
        public void setChildren(KoulutusmoduuliKoosteTyyppi children) {
            this.children = children;
        }
    }

    private Map<String, String> getKoodiToKomoOid(final KoulutusasteTyyppi koulutusasteTyyppi) {
        if (koulutusasteTyyppi == null) {
            throw new RuntimeException("KoulutusasteTyyppi cannot be null.");
        }

        return kKoodiToKomoOid.get(koulutusasteTyyppi);
    }
}
