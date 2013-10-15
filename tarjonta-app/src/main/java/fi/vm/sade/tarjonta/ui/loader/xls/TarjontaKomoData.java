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
import com.google.common.collect.Lists;
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
import static fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS;
import static fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi.LUKIOKOULUTUS;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTulos;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import static fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.TUTKINTO;
import static fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA;
import fi.vm.sade.tarjonta.service.types.LueKoulutusmoduuliKyselyTyyppi;
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
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
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
    private static String SEPARATOR = "#";
    private static String SEPARATOR_UNDERLINE = "_";
    private List<String> newKomoOids; //updated, inserted
    //a tempate for lukio or ammatillinen koulutus
    private DataReader dataReader;
    private Map<String, KoodiType> mapKoodistos = new HashMap<String, KoodiType>();
    private Map<String, KoulutusmoduuliKoosteTyyppi> parentKomosReadyForUpdate;
    private Map<String, List<KoulutusmoduuliKoosteTyyppi>> childKomosReadyForUpdate;
    private Map<String, String> dbParentKomos;//<koulutuskoodi, parent>
    private Map<String, KoulutusmoduuliKoosteTyyppi> dbChildKomos;//<lukiolinja/koulutuohjelma, child>

    public void preLoadAllKoodistot() {
        String[] koodistot = new String[]{KoodistoURI.KOODISTO_TUTKINTO_URI,
            KoodistoURI.KOODISTO_KOULUTUSOHJELMA_URI,
            KoodistoURI.KOODISTO_KOULUTUSASTE_URI,
            KoodistoURI.KOODISTO_KOULUTUSALA_URI,
            KoodistoURI.KOODISTO_TUTKINTONIMIKE_URI,
            KoodistoURI.KOODISTO_OPINTOALA_URI,
            KoodistoURI.KOODISTO_OPINTOJEN_LAAJUUSYKSIKKO_URI,
            KoodistoURI.KOODISTO_LUKIOLINJA_URI,
            KoodistoURI.KOODISTO_OPPILAITOSTYYPPI_URI,
            KoodistoURI.KOODISTO_EQF_LUOKITUS_URI
        };

        for (String koodisto : koodistot) {
            log.info("-------------------------------------------------");
            log.info("Loading koodisto : '" + koodisto + "'");

            List<KoodiType> result = koodiService.searchKoodisByKoodisto(KoodiServiceSearchCriteriaBuilder.koodisByKoodistoUri(koodisto));

            for (KoodiType type : result) {
                final String createKey = createUniqueKey(type.getKoodiArvo(), koodisto);

                if (mapKoodistos.containsKey(createKey)) {
                    log.trace("Already contains koodi : '" + type.getKoodiArvo() + "', uri:'" + type.getKoodiUri() + "', koodisto : '" + koodisto + "'");
                } else {
                    //make unique key by koodisto and koodi
                    //log.debug("Add koodi : '" + type.getKoodiArvo() + "', uri:'" + type.getKoodiUri() + "', koodisto : '" + koodisto + "'");
                    log.info("Loading koodisto key : {}", createKey);
                    mapKoodistos.put(createKey, type);
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

        specialExcelKomos();

//        newKomoOids = Lists.<String>newArrayList(); //updated, inserted
//        parentKomosReadyForUpdate = new HashMap<String, KoulutusmoduuliKoosteTyyppi>();
//        childKomosReadyForUpdate = new HashMap<String, List<KoulutusmoduuliKoosteTyyppi>>();
//        dbParentKomos = new HashMap<String, String>();//<koulutuskoodi, parent>
//        dbChildKomos = new HashMap<String, KoulutusmoduuliKoosteTyyppi>();//<lukiolinja/koulutuohjelma, child>
//
//        //separate imported flat data to child and parent objects

//        separateExcelKomos();
//        separateDbKomos();
//
//        //insert or update imported data
//        updateParentKomos(); //must be run before child data block
//        updateChildKomos();
//        publishKomos(); //update status for new objects, if any
//        log.info("Process ended.");
//        log.info("-------------------------------------------------");
    }

    private void updateRelations(KoulutusmoduuliKoosteTyyppi child, final String oid, final String parentOid, final String exceptionInfo) {
        Preconditions.checkNotNull(child, "A child object cannot be null, URI :" + exceptionInfo);
        Preconditions.checkNotNull(oid, "A child object OID cannot be null, URI :" + exceptionInfo);
        Preconditions.checkNotNull(parentOid, "A parent OID cannot be null, URI :" + exceptionInfo);
        child.setOid(oid);
        child.setParentOid(parentOid);
    }

    private KoodiType getKoodiType(final String koodiArvo, final String koodisto, final String fallbackKey) {
        final String searchKey = createUniqueKey(koodiArvo, koodisto);
        if (mapKoodistos.containsKey(searchKey)) {
            return mapKoodistos.get(searchKey);
        } else if (fallbackKey != null) {
            final String createUniqueKey = createUniqueKey(fallbackKey, koodisto);

            log.error("Using fallback key, real value not found by : '" + koodiArvo + "' -> '" + fallbackKey + "'," + koodisto);
            final KoodiType koodiType = mapKoodistos.get(createUniqueKey);
            log.error("fallback koodi object : '" + createUniqueKey + "' | '" + fallbackKey + "', " + koodiType);
            return koodiType;
        } else {
            log.error("Koodi not found by : '" + koodiArvo + "'," + koodisto);
            throw new RuntimeException("Koodi not found by : '" + koodiArvo + "'," + koodisto + ", " + searchKey);
        }
    }

    private String getUriWithVersion(final String koodiArvo, final String koodisto, final boolean skipEmpty) {
        if (skipEmpty && (koodiArvo == null || koodiArvo.isEmpty())) {
            return "";
        } else {
            return getUriWithVersion(koodiArvo, koodisto);
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
        return (new StringBuffer(koodisto)).append(SEPARATOR_UNDERLINE).append(value).toString();
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

    /**
     * Create the KOMO child DTO.
     *
     * @param dto
     * @return
     * @throws ExceptionMessage
     */
    private KoulutusmoduuliKoosteTyyppi createChildKomo(final ExcelMigrationDTO dto) throws ExceptionMessage {
        Preconditions.checkNotNull(dto.getKoulutuskoodiKoodiarvo(), "Import data error - koulutuskoodi value cannot be null!");
        final String koulutuskoodiUri = getUriWithVersion(dto.getKoulutuskoodiKoodiarvo(), KoodistoURI.KOODISTO_TUTKINTO_URI);
        //base values
        final KoulutusmoduuliKoosteTyyppi tutkintoParentKomo = createParentKomo(dto, koulutuskoodiUri);
        //create search words from Koodisto meta data 
        List<KoodiMetadataType> koulutuskoodiMeta = getKoodiMetadataTypes(dto.getKoulutuskoodiKoodiarvo(), KoodistoURI.KOODISTO_TUTKINTO_URI);

        KoulutusmoduuliKoosteTyyppi koChildKomo = new KoulutusmoduuliKoosteTyyppi();
        switch (dto.getKoulutusateTyyppi()) {
            case AMMATILLINEN_PERUSKOULUTUS:
                final String koulutusohjelmanKoodiarvo = dto.getKoulutusohjelmanKoodiarvo();
                Preconditions.checkNotNull(koulutusohjelmanKoodiarvo, "Koulutusohjelma koodi uri cannot be null.");

                final String fallbackValue = koulutusohjelmanKoodiarvo.substring(0, 4);
                List<KoodiMetadataType> koulutusohjelmaMeta = getKoodiMetadataTypes(koulutusohjelmanKoodiarvo, KoodistoURI.KOODISTO_KOULUTUSOHJELMA_URI, fallbackValue);

                koChildKomo.setKoulutusmoduulinNimi(SearchWordUtil.createSearchKeywords(koulutuskoodiMeta, koulutusohjelmaMeta, tarjontaKoodistoHelper));
                koChildKomo.setKoulutusohjelmakoodiUri(getUriWithVersion(dto.getKoulutusohjelmanKoodiarvo(), KoodistoURI.KOODISTO_KOULUTUSOHJELMA_URI, fallbackValue));

                /*
                 * Description data for koulutusohjelma (nothing to do with the Koodisto service)
                 * Koulutusohjelman koulutukselliset ja ammatilliset tavoitteet:
                 * -------------------------------------------------------------
                 * Kokin koulutusohjelman tai osaamisalan suorittanut kokki toimii ruoanvalmistustehtavissa...
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
        koChildKomo.setKoulutustyyppi(dto.getKoulutusateTyyppi());
        koChildKomo.setEqfLuokitus(getUriWithVersion(dto.getEqfUri(), KoodistoURI.KOODISTO_EQF_LUOKITUS_URI, true));

        //update child to parent relation
        updateRelations(koChildKomo, koChildKomo.getOid(), tutkintoParentKomo.getOid(), koulutuskoodiUri);

        return koChildKomo;
    }

    private KoulutusmoduuliKoosteTyyppi createTutkinto(final ExcelMigrationDTO dto, KoulutusmoduuliKoosteTyyppi tutkinto, final String koulutuskoodiUri) throws ExceptionMessage {
        tutkinto.setOid(this.oidService.newOid(NodeClassCode.TEKN_5));
        tutkinto.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO);

        //search Uris from Koodisto for komo
        tutkinto.setKoulutuskoodiUri(koulutuskoodiUri);
        tutkinto.setOpintoalaUri(getUriWithVersion(dto.getOpintoalaKoodi(), KoodistoURI.KOODISTO_OPINTOALA_URI));  //Automaalari
        tutkinto.setKoulutusalaUri(getUriWithVersion(dto.getKoulutusalaKoodi(), KoodistoURI.KOODISTO_KOULUTUSALA_URI));
        tutkinto.setKoulutusasteUri(getUriWithVersion(dto.getKoulutusasteenKoodiarvo(), KoodistoURI.KOODISTO_KOULUTUSASTE_URI));
        tutkinto.setLaajuusyksikkoUri(getUriWithVersion(dto.getLaajuusyksikko(), KoodistoURI.KOODISTO_OPINTOJEN_LAAJUUSYKSIKKO_URI)); //OV,OP           
        tutkinto.setLaajuusarvoUri(dto.getLaajuusUri()); //120, not a koodisto value
        tutkinto.setKoulutustyyppi(dto.getKoulutusateTyyppi());
        /*
         * Description data for tutkinto (nothing to do with the Koodisto service)
         * Tutkinnon koulutukselliset ja ammatilliset tavoitteet:
         * ------------------------------------------------------
         * Hotelli-, ravintola- ja catering-alan perustutkinnon suorittanut on kielitaitoinen sekä myynti- ja asiakaspalveluhenkinen....
         */
        ConversionUtils.setTeksti(tutkinto.getTekstit(), KomoTeksti.KOULUTUKSEN_RAKENNE, dto.getTutkinnonKuvaukset().getKoulutuksenRakenneTeksti());
        tutkinto.setTutkinnonTavoitteet(dto.getTutkinnonKuvaukset().getTavoiteTeksti());
        ConversionUtils.setTeksti(tutkinto.getTekstit(), KomoTeksti.JATKOOPINTO_MAHDOLLISUUDET, dto.getTutkinnonKuvaukset().getJatkoOpintomahdollisuudetTeksti());
        /*
         * Oppilaitostyyppi
         */
        tutkinto.getOppilaitostyyppi().clear();
        for (String codeValue : dto.getOppilaitostyyppis()) {
            tutkinto.getOppilaitostyyppi().add(getUriWithVersion(codeValue, KoodistoURI.KOODISTO_OPPILAITOSTYYPPI_URI));
        }

        return tutkinto;
    }

    /**
     * Create the KOMO parent DTO.
     *
     * @param dto
     * @param koulutuskoodiUri
     * @return
     * @throws ExceptionMessage
     */
    private KoulutusmoduuliKoosteTyyppi createParentKomo(final ExcelMigrationDTO dto, final String koulutuskoodiUri) throws ExceptionMessage {
        Preconditions.checkNotNull(koulutuskoodiUri, "Import data error - koulutuskoodi value cannot be null!");
        KoulutusmoduuliKoosteTyyppi tutkintoParentKomo = new KoulutusmoduuliKoosteTyyppi();
        if (parentKomosReadyForUpdate.containsKey(koulutuskoodiUri)) {
            tutkintoParentKomo = parentKomosReadyForUpdate.get(koulutuskoodiUri);
        } else {
            parentKomosReadyForUpdate.put(koulutuskoodiUri, tutkintoParentKomo);
        }

        return createTutkinto(dto, tutkintoParentKomo, koulutuskoodiUri);
    }

    private void updateParentKomos() {
        log.info("Update parent KOMO data to database.");
        int count = 1;
        if (parentKomosReadyForUpdate != null && !parentKomosReadyForUpdate.isEmpty()) {
            Set<Entry<String, KoulutusmoduuliKoosteTyyppi>> excelParents = parentKomosReadyForUpdate.entrySet();

            for (Entry<String, KoulutusmoduuliKoosteTyyppi> excelParentEntry : excelParents) {
                if (count % 10 == 1) {
                    log.info("Processing KOMO parents... {}, {}", excelParentEntry.getKey(), count);
                }

                KoulutusmoduuliKoosteTyyppi excelParentKomo = excelParentEntry.getValue();
                final String koulutuskoodiUri = excelParentKomo.getKoulutuskoodiUri();
                List<KoulutusmoduuliKoosteTyyppi> excelKomoChilds = childKomosReadyForUpdate.get(koulutuskoodiUri);
                String dbParentKomoOid = dbParentKomos.get(koulutuskoodiUri);

                //the parent komo must have at least one children
                if (dbParentKomoOid != null && excelKomoChilds != null && !excelKomoChilds.isEmpty()) {
                    //overwrite previously created komo
                    Preconditions.checkNotNull(dbParentKomoOid, "Parent OID cannot be null.");
                    excelParentKomo.setOid(dbParentKomoOid);
                    tarjontaAdminService.paivitaKoulutusmoduuli(excelParentKomo);
                } else if (excelKomoChilds != null && !excelKomoChilds.isEmpty()) {
                    //add new parent komo
                    newKomoOids.add(excelParentKomo.getOid());
                    tarjontaAdminService.lisaaKoulutusmoduuli(excelParentKomo);
                } else {
                    log.warn("Skipped KOMO parent, URI : {}, OID : {}", excelParentKomo.getKoulutuskoodiUri(), excelParentKomo.getOid());
                }
                count++;
            }
        }
    }

    private void updateChildKomos() {
        log.info("Update child KOMO data to database.");
        int count = 1;
        if (parentKomosReadyForUpdate != null && !parentKomosReadyForUpdate.isEmpty()) {
            for (Entry<String, KoulutusmoduuliKoosteTyyppi> parentKomoEntry : parentKomosReadyForUpdate.entrySet()) {
                final String koulutuskoodiUri = parentKomoEntry.getKey();

                if (count % 10 == 1) {
                    log.info("Processing KOMO child... {}, {}", parentKomoEntry.getKey(), count);
                }
                List<KoulutusmoduuliKoosteTyyppi> excelChildKomos = childKomosReadyForUpdate.get(koulutuskoodiUri);
                log.info("Update child KOMO data to database. {}", excelChildKomos);
                for (KoulutusmoduuliKoosteTyyppi excelChildKomo : excelChildKomos) {
                    if (excelChildKomo == null) {
                        throw new RuntimeException("A child KOMO cannot be null!");
                    }

                    switch (excelChildKomo.getKoulutusmoduuliTyyppi()) {
                        case TUTKINTO_OHJELMA:
                            final String koulutusohjelmakoodiUri = excelChildKomo.getKoulutusohjelmakoodiUri();
                            final String lukiolinjaUri = excelChildKomo.getLukiolinjakoodiUri();
                            String dbParentOid = dbParentKomos.get(koulutuskoodiUri);
                            if (dbParentOid == null) {
                                dbParentOid = parentKomosReadyForUpdate.get(koulutuskoodiUri).getOid();
                            }
                            Preconditions.checkNotNull(dbParentOid, "Parent OID cannot be null.");
                            //update childrend
                            switch (excelChildKomo.getKoulutustyyppi()) {
                                case AMMATILLINEN_PERUSKOULUTUS:
                                    if (dbParentOid != null && dbChildKomos.containsKey(koulutusohjelmakoodiUri)) {
                                        //overwrite and add target to parent
                                        updateRelations(excelChildKomo, dbChildKomos.get(koulutusohjelmakoodiUri).getOid(), dbParentOid, koulutuskoodiUri + "/" + koulutusohjelmakoodiUri);
                                        tarjontaAdminService.paivitaKoulutusmoduuli(excelChildKomo);
                                    } else if (dbParentOid != null) {
                                        updateRelations(excelChildKomo, excelChildKomo.getOid(), dbParentOid, koulutuskoodiUri + "/" + koulutusohjelmakoodiUri);
                                        tarjontaAdminService.lisaaKoulutusmoduuli(excelChildKomo);
                                        newKomoOids.add(excelChildKomo.getOid());
                                    }
                                    break;
                                case LUKIOKOULUTUS:
                                    //log.debug("LUKIOKOULUTUS {}", lukiolinjaUri);

                                    if (dbParentOid != null && dbChildKomos.containsKey(lukiolinjaUri)) {
                                        //overwrite and add target to parent
                                        updateRelations(excelChildKomo, dbChildKomos.get(lukiolinjaUri).getOid(), dbParentOid, koulutuskoodiUri + "/" + lukiolinjaUri);
                                        tarjontaAdminService.paivitaKoulutusmoduuli(excelChildKomo);
                                    } else if (dbParentOid != null) {
                                        updateRelations(excelChildKomo, excelChildKomo.getOid(), dbParentOid, koulutuskoodiUri + "/" + lukiolinjaUri);
                                        tarjontaAdminService.lisaaKoulutusmoduuli(excelChildKomo);
                                        newKomoOids.add(excelChildKomo.getOid());
                                    }
                                    break;
                            }

                            if (excelChildKomo.getKoulutusmoduuliTyyppi() != KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA || excelChildKomo.getParentOid() == null) {
                                throw new RuntimeException("An invalid child KOMO object." + ReflectionToStringBuilder.toString(excelChildKomo));
                            }
                    }
                }

                count++;
            }
        }
    }

    private void publishKomos() {
        PaivitaTilaTyyppi paivitaTilaTyyppi = new PaivitaTilaTyyppi();
        log.info("New KOMOs {}", newKomoOids);
        log.info("Publish KOMOs");
        for (final String oid : newKomoOids) {
            GeneerinenTilaTyyppi geneerinenTilaTyyppi = new GeneerinenTilaTyyppi();
            geneerinenTilaTyyppi.setOid(oid);
            geneerinenTilaTyyppi.setSisalto(SisaltoTyyppi.KOMO);
            geneerinenTilaTyyppi.setTila(TarjontaTila.JULKAISTU);
            paivitaTilaTyyppi.getTilaOids().add(geneerinenTilaTyyppi);
        }
        tarjontaAdminService.paivitaTilat(paivitaTilaTyyppi);
    }

    private void publishKomo(final String oid) {
        PaivitaTilaTyyppi paivitaTilaTyyppi = new PaivitaTilaTyyppi();
        GeneerinenTilaTyyppi geneerinenTilaTyyppi = new GeneerinenTilaTyyppi();
        geneerinenTilaTyyppi.setOid(oid);
        geneerinenTilaTyyppi.setSisalto(SisaltoTyyppi.KOMO);
        geneerinenTilaTyyppi.setTila(TarjontaTila.JULKAISTU);
        paivitaTilaTyyppi.getTilaOids().add(geneerinenTilaTyyppi);
        tarjontaAdminService.paivitaTilat(paivitaTilaTyyppi);
    }

    private void separateDbKomos() {
        int count = 1;

        HaeKoulutusmoduulitVastausTyyppi allKomos = tarjontaPublicService.haeKoulutusmoduulit(new HaeKoulutusmoduulitKyselyTyyppi());
        if (allKomos != null && !allKomos.getKoulutusmoduuliTulos().isEmpty()) {
            List<KoulutusmoduuliTulos> allModules = allKomos.getKoulutusmoduuliTulos();
            for (KoulutusmoduuliTulos t : allModules) {
                if (count % 10 == 1) {
                    log.info("Processing DB KOMOs... {}", count);
                }
                KoulutusmoduuliKoosteTyyppi kkt = t.getKoulutusmoduuli();
                final String koulutuskoodiUri = kkt.getKoulutuskoodiUri();
                switch (kkt.getKoulutusmoduuliTyyppi()) {
                    case TUTKINTO:
                        if (!dbParentKomos.containsKey(koulutuskoodiUri)) {
                            //KOMO parent data sanity check, remove an invalid parent relations etc.
                            if (kkt.getOid().equals("1.2.246.562.5.2013061010184237347962")
                                    || kkt.getOid().equals("1.2.246.562.5.2013061010184880727629")) {
                                //A quick hack, remove this code block after the rows are removed from dd.
                                continue;
                            }

                            if (kkt.getKoulutusmoduuliTyyppi() != null && kkt.getParentOid() == null) {
                                dbParentKomos.put(koulutuskoodiUri, kkt.getOid());
                                log.debug("added parent OID : {}, URI : {}", kkt.getOid(), koulutuskoodiUri);
                                break;
                            }
                        }
                        break;
                    case TUTKINTO_OHJELMA:
                        switch (kkt.getKoulutustyyppi()) {
                            case AMMATILLINEN_PERUSKOULUTUS:
                                final String koulutusOhjelmaUri = kkt.getKoulutusohjelmakoodiUri();
                                dbChildKomos.put(koulutusOhjelmaUri, kkt);
                                break;
                            case LUKIOKOULUTUS:
                                final String lukiolinjaUri = kkt.getLukiolinjakoodiUri();
                                dbChildKomos.put(lukiolinjaUri, kkt);
                                break;
                        }
                        break;
                }

                count++;
            }
        }
    }

    private void specialExcelKomos() throws ExceptionMessage {
        /*
         * TUTKINTO DATA, NO CHILD RELATIONS
         */
        Set<ExcelMigrationDTO> loadedData = dataReader.getValmentavaData();
        log.info("Excel files merged, now try to create all KOMOs.");
        int count = 1;
        for (ExcelMigrationDTO dto : loadedData) {
            if (count % 10 == 1) {
                log.info("Processing special case komos... {}", count);
                final String koulutuskoodiUri = getUriWithVersion(dto.getKoulutuskoodiKoodiarvo(), KoodistoURI.KOODISTO_TUTKINTO_URI);
                //base values

                log.error("Processing special case komos... {}", koulutuskoodiUri);
                final KoulutusmoduuliKoosteTyyppi kooste = createTutkinto(dto, new KoulutusmoduuliKoosteTyyppi(), koulutuskoodiUri);

                HaeKoulutusmoduulitKyselyTyyppi hae = new HaeKoulutusmoduulitKyselyTyyppi();
                hae.setKoulutustyyppi(dto.getKoulutusateTyyppi());
                hae.setKoulutuskoodiUri(dto.getKoulutuskoodiKoodiarvo());
                HaeKoulutusmoduulitVastausTyyppi resultKomos = tarjontaPublicService.haeKoulutusmoduulit(hae);

                if (resultKomos == null || resultKomos.getKoulutusmoduuliTulos().isEmpty()) {
                    KoulutusmoduuliKoosteTyyppi lisaaKoulutusmoduuli = tarjontaAdminService.lisaaKoulutusmoduuli(kooste);
                    publishKomo(lisaaKoulutusmoduuli.getOid());
                } else {
                    String oid = resultKomos.getKoulutusmoduuliTulos().get(0).getKoulutusmoduuli().getOid();
                    kooste.setOid(oid);
                    tarjontaAdminService.paivitaKoulutusmoduuli(kooste);
                    log.error("kooste : {}", kooste);
                }
            }
        }
        log.info("Total count of the imported KOMOs : {}", count);
    }

    private void separateExcelKomos() throws ExceptionMessage {
        /*
         * DATA WITH PARENT CHILD RELATION
         */
        Set<ExcelMigrationDTO> loadedData = dataReader.getData();
        int count = 1;
        for (ExcelMigrationDTO dto : loadedData) {
            if (count % 10 == 1) {
                log.info("Processing normal cases komos... {}", count);
            }

            /*
             * Create new parent and child KOMOs, next we need to check
             * have we already created this kind of KOMOs to database.
             */
            KoulutusmoduuliKoosteTyyppi komoChild = createChildKomo(dto);
            final String koulutuskoodiUri = komoChild.getKoulutuskoodiUri();
            if (childKomosReadyForUpdate.containsKey(koulutuskoodiUri)) {
                childKomosReadyForUpdate.get(koulutuskoodiUri).add(komoChild);
            } else {
                List<KoulutusmoduuliKoosteTyyppi> childKomos = Lists.<KoulutusmoduuliKoosteTyyppi>newArrayList();
                childKomos.add(komoChild);
                childKomosReadyForUpdate.put(koulutuskoodiUri, childKomos);
            }
            count++;
        }

        log.info("Total count of the imported KOMOs : {}", count);
    }
}
