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

import com.google.common.base.Preconditions;

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
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTulos;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenTekstiTyyppi.Teksti;
import fi.vm.sade.tarjonta.service.types.PaivitaTilaTyyppi;
import fi.vm.sade.tarjonta.service.types.SisaltoTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.types.KomoTeksti;
import fi.vm.sade.tarjonta.ui.helper.conversion.ConversionUtils;
import fi.vm.sade.tarjonta.ui.helper.conversion.SearchWordUtil;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.ExcelMigrationDTO;

/**
 * Alustaa tarjontaan esimerkki dataa. Poistetaan kun Koodistosta saadaan
 * tarvittava koodien relaatioihin perustuvat data.
 */
@Component
public class TarjontaKomoData {

    private static final Logger log = LoggerFactory.getLogger(TarjontaKomoData.class);
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
    private DataReader dataReader;
    private Map<String, KoodiType> map = new HashMap<String, KoodiType>();

    public void preLoadAllKoodistot() {
        String[] koodistot = new String[]{KoodistoURI.KOODISTO_TUTKINTO_URI,
            KoodistoURI.KOODISTO_KOULUTUSOHJELMA_URI,
            KoodistoURI.KOODISTO_KOULUTUSASTE_URI,
            KoodistoURI.KOODISTO_KOULUTUSALA_URI,
            KoodistoURI.KOODISTO_TUTKINTONIMIKE_URI,
            KoodistoURI.KOODISTO_OPINTOALA_URI,
            KoodistoURI.KOODISTO_OPINTOJEN_LAAJUUSYKSIKKO_URI,
            KoodistoURI.KOODISTO_LUKIOLINJA_URI,
            KoodistoURI.KOODISTO_OPPILAITOSTYYPPI_URI
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
        if (dataReader == null) {
            dataReader = new DataReader();
        }
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
            LOS komos = createKomos(dto);
            final KoulutusasteTyyppi koulutusTyyppi = dto.getKoulutusTyyppi();
            KoulutusmoduuliTulos searchChildKomo = searchKomo(koulutusTyyppi, komos.getChildren().getKoulutuskoodiUri(), komos.getChildren());

            if (saveChanges && searchChildKomo != null) {
                String parentKomoOid = searchChildKomo.getKoulutusmoduuli().getParentOid();
                final String childKomoOid = searchChildKomo.getKoulutusmoduuli().getOid();
                log.info("Update : {} {}", komos.getChildren().getKoulutuskoodiUri(), childKomoOid);
    
                if (komos.getParent().getKoulutusmoduuliTyyppi().equals(KoulutusmoduuliTyyppi.TUTKINTO)) {
                    //update KOMO parent
                    komos.getParent().setOid(parentKomoOid);
                    tarjontaAdminService.paivitaKoulutusmoduuli(komos.getParent());
                } else {
                    throw new RuntimeException("An invalid data exception - KOMO tutkinto type expected, but was " + komos.getParent().getKoulutusmoduuliTyyppi() + ".");
                }

                if (komos.getChildren().getKoulutusmoduuliTyyppi().equals(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA)) {
                    //update KOMO children
                    komos.getChildren().setOid(childKomoOid);
                    tarjontaAdminService.paivitaKoulutusmoduuli(komos.getChildren());
                } else {
                    throw new RuntimeException("An invalid data exception - KOMO tutkinto_ohjelma type expected, but was " + komos.getParent().getKoulutusmoduuliTyyppi() + ".");
                }

            } else if (saveChanges) {
                //persist new KOMO (child and parent) to database 
                komos.getParent();

                Preconditions.checkNotNull(komos.getParent().getKoulutuskoodiUri(), "Parent KOMO koulutuskoodi cannot be null.");
                KoulutusmoduuliKoosteTyyppi tKomo = tarjontaAdminService.lisaaKoulutusmoduuli(komos.getParent());
                Preconditions.checkNotNull(tKomo.getKoulutuskoodiUri(), "Parent KOMO koulutuskoodi cannot be null.");

                if (tKomo.getOid() != null) {
                    //create komo and update oid to map
                    getKoodiToKomoOid(koulutusTyyppi).put(tKomo.getKoulutuskoodiUri(), tKomo.getOid());
                } else {
                    //NO OID! I guess the KOMO was created earlier and this was a batch update...
                    final KoulutusmoduuliTulos resultTutkintoKomo = searchKomo(koulutusTyyppi, komos.getChildren().getKoulutuskoodiUri(), null);
                    final String parentOid = resultTutkintoKomo.getKoulutusmoduuli().getOid();
                    final String koulutuskoodiUri = resultTutkintoKomo.getKoulutusmoduuli().getKoulutuskoodiUri();

                    log.info("Load parent OID : {}, {}", parentOid, koulutuskoodiUri);
                    getKoodiToKomoOid(koulutusTyyppi).put(koulutuskoodiUri, parentOid);
                }

                final String mapParentOid = getKoodiToKomoOid(koulutusTyyppi).get(komos.getParent().getKoulutuskoodiUri());
                Preconditions.checkNotNull(mapParentOid, "Parent KOMO OID cannot be null.");
                log.info("Map parent OID : {}", mapParentOid);
                komos.getChildren().setParentOid(mapParentOid);
                tarjontaAdminService.lisaaKoulutusmoduuli(komos.getChildren());
                log.info("Add : {} {}", komos.getParent(), tKomo.getOid());
            }
            //add oids to list, the oids will be used for change udate status to published
            komoOids.add(komos.getParent().getOid());
            komoOids.add(komos.getChildren().getOid());
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
        Preconditions.checkNotNull(koodiArvo, "Koodi value cannot be null in koodisto '" + koodisto + "'");
        Preconditions.checkNotNull(koodisto, "Koodisto cannot be null!");

        final KoodiType koodiType = getKoodiType(koodiArvo, koodisto, fallbackKey);
        //search and create the real koodi uri  

        if (koodiType != null) {
            return koodiType.getMetadata();
        }

        return null;
    }

    public static String createUniqueKey(final String value, final String koodisto) {
        Preconditions.checkNotNull(koodisto, "Koodisto uri cannot be null! Koodi value was " + value + ".");
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
    private LOS createKomos(final ExcelMigrationDTO dto) throws ExceptionMessage {
        //base values
        Preconditions.checkNotNull(dto.getKoulutuskoodiKoodiarvo(), "Import data error - koulutuskoodi value cannot be null!");
        final String koulutuskoodiUri = getUriWithVersion(dto.getKoulutuskoodiKoodiarvo(), KoodistoURI.KOODISTO_TUTKINTO_URI);

        KoulutusmoduuliKoosteTyyppi tutkintoParentKomo = new KoulutusmoduuliKoosteTyyppi();
        tutkintoParentKomo.setOid(this.oidService.newOid(NodeClassCode.TEKN_5));
        tutkintoParentKomo.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO);

        //search Uris from Koodisto for komo
        tutkintoParentKomo.setKoulutuskoodiUri(koulutuskoodiUri);
        tutkintoParentKomo.setOpintoalaUri(getUriWithVersion(dto.getOpintoalaKoodi(), KoodistoURI.KOODISTO_OPINTOALA_URI));  //Automaalari
        tutkintoParentKomo.setKoulutusalaUri(getUriWithVersion(dto.getKoulutusalaKoodi(), KoodistoURI.KOODISTO_KOULUTUSALA_URI));
        tutkintoParentKomo.setKoulutusasteUri(getUriWithVersion(dto.getKoulutusasteenKoodiarvo(), KoodistoURI.KOODISTO_KOULUTUSASTE_URI));
        tutkintoParentKomo.setLaajuusyksikkoUri(getUriWithVersion(dto.getLaajuusyksikko(), KoodistoURI.KOODISTO_OPINTOJEN_LAAJUUSYKSIKKO_URI)); //OV,OP           
        tutkintoParentKomo.setLaajuusarvoUri(dto.getLaajuusUri()); //120, not a koodisto value
        tutkintoParentKomo.setKoulutustyyppi(dto.getKoulutusTyyppi());
        /*
         * Description data for tutkinto (nothing to do with the Koodisto service)
         * LUKIO and AMMATILLINEN koulutus
         */
        ConversionUtils.setTeksti(tutkintoParentKomo.getTekstit(), KomoTeksti.KOULUTUKSEN_RAKENNE, dto.getTutkinnonKuvaukset().getKoulutuksenRakenneTeksti());
        tutkintoParentKomo.setTutkinnonTavoitteet(dto.getTutkinnonKuvaukset().getTavoiteTeksti());
        ConversionUtils.setTeksti(tutkintoParentKomo.getTekstit(), KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET, dto.getTutkinnonKuvaukset().getJatkoOpintomahdollisuudetTeksti());

        /*
         * Oppilaitostyyppi
         */
        tutkintoParentKomo.getOppilaitostyyppi().clear();
        for (String codeValue : dto.getOppilaitostyyppis()) {
            tutkintoParentKomo.getOppilaitostyyppi().add(getUriWithVersion(codeValue, KoodistoURI.KOODISTO_OPPILAITOSTYYPPI_URI));
        }
        //create search words from Koodisto meta data 
        List<KoodiMetadataType> koulutuskoodiMeta = getKoodiMetadataTypes(dto.getKoulutuskoodiKoodiarvo(), KoodistoURI.KOODISTO_TUTKINTO_URI);

        KoulutusmoduuliKoosteTyyppi koChildKomo = new KoulutusmoduuliKoosteTyyppi();
        switch (dto.getKoulutusTyyppi()) {
            case AMMATILLINEN_PERUSKOULUTUS:
                final String koulutusohjelmanKoodiarvo = dto.getKoulutusohjelmanKoodiarvo();
                Preconditions.checkNotNull(koulutusohjelmanKoodiarvo, "Koulutusohjelma koodi uri cannot be null.");

                final String fallbackValue = koulutusohjelmanKoodiarvo.substring(0, 4);
                List<KoodiMetadataType> koulutusohjelmaMeta = getKoodiMetadataTypes(koulutusohjelmanKoodiarvo, KoodistoURI.KOODISTO_KOULUTUSOHJELMA_URI, fallbackValue);

                koChildKomo.setKoulutusmoduulinNimi(SearchWordUtil.createSearchKeywords(koulutuskoodiMeta, koulutusohjelmaMeta, tarjontaKoodistoHelper));
                koChildKomo.setKoulutusohjelmakoodiUri(getUriWithVersion(dto.getKoulutusohjelmanKoodiarvo(), KoodistoURI.KOODISTO_KOULUTUSOHJELMA_URI, fallbackValue));

                /*
                 * Description data for koulutusohjelma (nothing to do with the Koodisto service)
                 */
                ConversionUtils.setTeksti(koChildKomo.getTekstit(), KomoTeksti.TAVOITTEET, dto.getKoulutusohjelmanKuvaukset().getTavoiteTeksti());
                break;
            case LUKIOKOULUTUS:
                Preconditions.checkNotNull(dto.getLukiolinjaKoodiarvo(), "Lukiolinja koodi uri cannot be null.");

                List<KoodiMetadataType> lukiolinjaMeta = getKoodiMetadataTypes(dto.getLukiolinjaKoodiarvo(), KoodistoURI.KOODISTO_LUKIOLINJA_URI);
                koChildKomo.setKoulutusmoduulinNimi(SearchWordUtil.createSearchKeywords(koulutuskoodiMeta, lukiolinjaMeta, tarjontaKoodistoHelper));
                koChildKomo.setLukiolinjakoodiUri(getUriWithVersion(dto.getLukiolinjaKoodiarvo(), KoodistoURI.KOODISTO_LUKIOLINJA_URI));
                break;
        }

        koChildKomo.setKoulutuskoodiUri(koulutuskoodiUri);
        koChildKomo.setOid(this.oidService.newOid(NodeClassCode.TEKN_5));
        koChildKomo.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);

        Preconditions.checkNotNull(dto.getTutkintonimikkeenKoodiarvo(), "Tutkintonimike koodi uri cannot be null. Obj : " + dto);
        koChildKomo.setTutkintonimikeUri(getUriWithVersion(dto.getTutkintonimikkeenKoodiarvo(), KoodistoURI.KOODISTO_TUTKINTONIMIKE_URI, "00000")); //00000 -> empty line
        koChildKomo.setParentOid(tutkintoParentKomo.getOid());
        koChildKomo.setKoulutustyyppi(dto.getKoulutusTyyppi());
        koChildKomo.setEqfLuokitus(dto.getEqfUri());

        return new LOS(tutkintoParentKomo, koChildKomo);
    }

    private KoulutusmoduuliTulos searchKomo(final KoulutusasteTyyppi koulutusasteTyyppi, final String koulutuskoodi, final KoulutusmoduuliKoosteTyyppi komoChild) {
        HaeKoulutusmoduulitKyselyTyyppi kysely = new HaeKoulutusmoduulitKyselyTyyppi();
        kysely.setKoulutustyyppi(koulutusasteTyyppi);
        kysely.setKoulutuskoodiUri(koulutuskoodi);

        String check = null;

        if (komoChild != null) {
            switch (koulutusasteTyyppi) {
                case AMMATILLINEN_PERUSKOULUTUS:
                    check = komoChild.getKoulutusohjelmakoodiUri();
                    kysely.setKoulutusohjelmakoodiUri(komoChild.getKoulutusohjelmakoodiUri());
                    break;
                case LUKIOKOULUTUS:
                    check = komoChild.getLukiolinjakoodiUri();
                    kysely.setLukiolinjakoodiUri(komoChild.getLukiolinjakoodiUri());
                    break;
            }
        }
        log.info(koulutusasteTyyppi + " - search KOMO by '{}' and '{}'", kysely.getKoulutuskoodiUri(), check);

        final HaeKoulutusmoduulitVastausTyyppi result = tarjontaPublicService.haeKoulutusmoduulit(kysely);
        final List<KoulutusmoduuliTulos> tulos = result.getKoulutusmoduuliTulos();

        if (tulos != null && !tulos.isEmpty()) {
            if (tulos.size() > 1) {
                //SEARCH PARENT KOMO
                for (KoulutusmoduuliTulos t : tulos) {
                    //TODO: add TutktintoTyyppi param to HaeKoulutusmoduulitKyselyTyyppi.
                    //a quick hack: as there is other way to filter the result to parent 'TUTKINTO' -type of komos.
                    Preconditions.checkNotNull(t.getKoulutusmoduuli().getKoulutusmoduuliTyyppi(), "Koulutustyyppi object cannot be null.");

                    if (t.getKoulutusmoduuli().getKoulutusmoduuliTyyppi().equals(KoulutusmoduuliTyyppi.TUTKINTO)) {
                        //program have tried to search parent TUTKINTO-type of komo, not a child komo.

                        if (check != null) {
                            throw new RuntimeException("Tried to search a child KOMO, but result was parent?");
                        }

                        return t;
                    }
                }
                throw new RuntimeException("Parent KOMO not found, result size of komos was " + tulos.size() + ".");
            } else {
                //SEARCH CHILD KOMO
                final KoulutusmoduuliTulos childKomo = tulos.get(0);
                Preconditions.checkNotNull(childKomo.getKoulutusmoduuli().getOid(), "Koulutustyyppi OID cannot be null.");
                Preconditions.checkNotNull(childKomo.getKoulutusmoduuli().getKoulutuskoodiUri(), "Koulutuskoodi URI cannot be null.");
                Preconditions.checkNotNull(childKomo.getKoulutusmoduuli().getKoulutusmoduuliTyyppi(), "Koulutustyyppi object cannot be null.");
                Preconditions.checkNotNull(check, "Tried to search a parent KOMO, but result was child?");

                if (childKomo.getKoulutusmoduuli().getKoulutusmoduuliTyyppi().equals(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA)) {
                    return childKomo;
                } else {
                    throw new RuntimeException("Not a child KOMO, OID : " + childKomo.getKoulutusmoduuli().getOid() + ".");
                }
            }
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
