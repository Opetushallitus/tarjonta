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

import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.oid.service.types.NodeClassCode;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
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
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.conversion.SearchWordUtil;
import java.io.IOException;
import java.net.URL;
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
    private static final int MAX_READ_ROWS = 800;
    private static final boolean USE_UPDATE = false; //unready feature
    @Autowired
    private KoodiService koodiService;
    @Autowired(required = true)
    private OIDService oidService;
    @Autowired(required = true)
    private TarjontaAdminService tarjontaAdminService;
    @Autowired(required = true)
    private TarjontaPublicService tarjontaPublicService;
    private Set<Relaatiot5RowDTO> loadedData;
    private static String SEPARATOR = "#";
    public static final Column[] COLUMNS_RELAATIO5 = {
        //property, title desc, type= conversion type
        new Column("koulutuksenRakenne", "KOULUTUKSEN RAKENNE", InputColumnType.STRING),
        new Column("tavoitteet", "KOULUTUKSELLISET JA AMMATILLISET TAVOITTEET", InputColumnType.STRING),
        new Column("jatkoOpinto", "JATKO-OPINTOMAHDOLLISUUDET", InputColumnType.STRING),
        new Column("koulutusohjelmanNimi", "KOULUTUSOHJELMAN NIMI", InputColumnType.STRING),
        new Column("koulutusohjelmanKoodiarvo", "KOULUTUSOHJELMAN KOODIARVO", InputColumnType.INTEGER),
        new Column("tutkintonimike", "TUTKINTONIMIKE", InputColumnType.STRING),
        new Column("tutkintonimikkeenKoodiarvo", "TUTKINTONIMIKKEEN KOODIARVO", InputColumnType.INTEGER),
        new Column("tutkinnonNimi", "TUTKINNON NIMI", InputColumnType.STRING),
        new Column("koulutuskoodi", "KOULUTUSKOODI(TUTKINTOKOODI)", InputColumnType.INTEGER),
        new Column("koulutusaste", "Koulutusaste", InputColumnType.STRING),
        new Column("koulutusasteenKoodiarvo", "Koulutusasteen koodiarvo", InputColumnType.INTEGER),
        new Column("laajuus", "Laajuus", InputColumnType.INTEGER),
        new Column("laajuusyksikko", "Laajuusyksikkö", InputColumnType.STRING),
        new Column("eqf", "EQF", InputColumnType.INTEGER),
        new Column("koulutusohjelmanTavoitteet", "KOULUTUSOHJELMAN TAVOITTEET", InputColumnType.STRING),
        new Column("lukiolinjaNimi", "LUKIOLINJAN NIMI", InputColumnType.STRING),
        new Column("lukiolinjaKoodiarvo", "LUKIOLINJAN KOODIARVO", InputColumnType.INTEGER)
    };
    public static final Column[] COLUMNS_KOULUTUSLUOKITUS = {
        //property, title desc, type= conversion type
        new Column(null, "tilv", null),
        new Column("koulutuskoodi", "koulk", InputColumnType.INTEGER),
        new Column(null, "taso", null),
        new Column(null, "snimi", null),
        new Column(null, "slnimi", null),
        new Column(null, "rnimi", null),
        new Column(null, "rlnimi", null),
        new Column(null, "enimi", null),
        new Column(null, "elnimi", null),
        new Column(null, "olo", null),
        new Column(null, "lakkv", null),
        new Column(null, "korvkoulk", null),
        new Column(null, "syntv", null),
        new Column(null, "nimvuosi", null),
        new Column("koulutusalaKoodi", "kala", InputColumnType.INTEGER),
        new Column("koulutusalaNimi", "kalanimi", InputColumnType.STRING),
        new Column(null, "kalalnimi", null),
        new Column(null, "kalanimi_r", null),
        new Column(null, "kalalnimi_r", null),
        new Column(null, "kaste", null),
        new Column(null, "kastenimi", null),
        new Column(null, "kastenimi_r", null),
        new Column(null, "opmala", null),
        new Column(null, "opmalani", null),
        new Column(null, "opmalalni", null),
        new Column("opintoalaKoodi", "opmopa", InputColumnType.INTEGER),
        new Column("opintoalaNimi", "opmopani", InputColumnType.STRING)
    };
    private Map<String, KoodiType> map = new HashMap<String, KoodiType>();
    
    public void preLoadAllKoodistot() {
        String[] koodistot = new String[]{KoodistoURIHelper.KOODISTO_TUTKINTO_URI,
            KoodistoURIHelper.KOODISTO_KOULUTUSOHJELMA_URI,
            KoodistoURIHelper.KOODISTO_KOULUTUSASTE_URI,
            KoodistoURIHelper.KOODISTO_KOULUTUSALA_URI,
            KoodistoURIHelper.KOODISTO_TUTKINTONIMIKE_URI,
            KoodistoURIHelper.KOODISTO_OPINTOALA_URI,
            //KoodistoURIHelper.KOODISTO_OPINTOJEN_LAAJUUS_URI,
            KoodistoURIHelper.KOODISTO_OPINTOJEN_LAAJUUSYKSIKKO_URI,
            KoodistoURIHelper.KOODISTO_LUKIOLINJA_URI
        };
        
        for (String koodisto : koodistot) {
            log.info("-------------------------------------------------");
            log.info("Loading koodisto : '" + koodisto + "'");
            
            List<KoodiType> result = koodiService.searchKoodisByKoodisto(KoodiServiceSearchCriteriaBuilder.koodisByKoodistoUri(koodisto));
            
            for (KoodiType type : result) {
                final String createKey = createUniqueKey(koodisto, type.getKoodiArvo());
                
                if (map.containsKey(createKey)) {
                    log.debug("Koodi : '" + type.getKoodiArvo() + "', uri:'" + type.getKoodiUri() + "', koodisto : '" + koodisto + "'");
                } else {
                    //make unique key by koodisto and koodi
                    map.put(createKey, type);
                }
            }
            log.info("Loaded koodis :" + result.size());
            log.info("-------------------------------------------------");
        }
    }
    
    public void createData(boolean saveChanges) throws IOException, ExceptionMessage {
        log.info("Starting to load KOMOs from Excel file");
        mergeExcels();
        log.info("Excel files merged, now try to create all KOMOs.");
        HashMap<String, String> kKoodiToKomoOid = new HashMap<String, String>();
        int count = 1;
        
        for (Relaatiot5RowDTO dto : loadedData) {
            if (count % 10 == 1) {
                log.info("Processing... {}", count);
            }
            LOS komo = createKomo(dto);
            
            KoulutusmoduuliTulos searchChildKomo = null;
            
            switch (dto.getKoulutusTyyppi()) {
                case AMMATILLINEN_PERUSKOULUTUS:
                    searchChildKomo = searchKomo(dto.getKoulutusTyyppi(), komo.getParent().getKoulutuskoodiUri(), komo.getChildren().getKoulutusohjelmakoodiUri());
                    break;
                case LUKIOKOULUTUS:
                    searchChildKomo = searchKomo(dto.getKoulutusTyyppi(), komo.getParent().getKoulutuskoodiUri(), komo.getChildren().getLukiolinjakoodiUri());
                    break;
            }
            
            if (USE_UPDATE && saveChanges && searchChildKomo != null) {
                log.debug("Update");
                //update parent
                komo.getParent().setOid(searchChildKomo.getKoulutusmoduuli().getParentOid());
                tarjontaAdminService.paivitaKoulutusmoduuli(komo.getParent());

                //update children
                komo.getChildren().setOid(searchChildKomo.getKoulutusmoduuli().getOid());
                tarjontaAdminService.paivitaKoulutusmoduuli(komo.getChildren());
            } else if (saveChanges) {
                log.debug("Create");
                KoulutusmoduuliKoosteTyyppi tKomo = tarjontaAdminService.lisaaKoulutusmoduuli(komo.getParent());
                if (tKomo.getOid() != null) {
                    kKoodiToKomoOid.put(tKomo.getKoulutuskoodiUri(), tKomo.getOid());
                }
                
                komo.getChildren().setParentOid(kKoodiToKomoOid.get(komo.getParent().getKoulutuskoodiUri()));
                tarjontaAdminService.lisaaKoulutusmoduuli(komo.getChildren());
            }
            
            count++;
        }
        log.info("Total count of the imported KOMOs : {}", count);
        log.info("Process ended.");
    }
    
    private KoodiType getKoodiType(final String koodiArvo, final String koodisto) {
        final String searchKey = createUniqueKey(koodisto, koodiArvo);
        
        if (map.containsKey(searchKey)) {
            return map.get(searchKey);
        } else {
            log.error("Koodi not found by : '" + koodiArvo + "'," + koodisto);
            throw new RuntimeException("Koodi not found by : '" + koodiArvo + "'," + koodisto);
        }
    }
    
    private String getUriWithVersion(final String koodiArvo, final String koodisto) {
        final KoodiType koodiType = getKoodiType(koodiArvo, koodisto);
        //search and create the real koodi uri  

        if (koodiType != null) {
            return koodiType.getKoodiUri() + SEPARATOR + koodiType.getVersio();
        }
        
        return null;
    }
    
    private List<KoodiMetadataType> getKoodiMetadataTypes(final String koodiArvo, final String koodisto) {
        if (koodiArvo == null) {
            throw new IllegalArgumentException("Koodi value cannot be null in koodisto '" + koodisto + "'");
        }
        
        if (koodisto == null) {
            throw new IllegalArgumentException("Koodisto cannot be null!");
        }
        
        final KoodiType koodiType = getKoodiType(koodiArvo, koodisto);
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
    
    private static MonikielinenTekstiTyyppi createTeksti(String fiTeksti, String svTeskti, String enTeksti) {
        
        MonikielinenTekstiTyyppi t = new MonikielinenTekstiTyyppi();
        if (fiTeksti != null) {
            Teksti teksti = new MonikielinenTekstiTyyppi.Teksti();
            teksti.setKieliKoodi("fi");
            teksti.setValue(fiTeksti);
            t.getTeksti().add(teksti);
        }
        if (enTeksti != null) {
            Teksti teksti = new MonikielinenTekstiTyyppi.Teksti();
            teksti.setKieliKoodi("en");
            teksti.setValue(fiTeksti);
            t.getTeksti().add(teksti);
        }
        if (svTeskti != null) {
            Teksti teksti = new MonikielinenTekstiTyyppi.Teksti();
            teksti.setKieliKoodi("sv");
            teksti.setValue(fiTeksti);
            t.getTeksti().add(teksti);
        }
        return t;
    }
    
    public Set<Relaatiot5RowDTO> getLoadedData() {
        return loadedData;
    }

    /**
     * Create full KoulutusmoduuliKoosteTyyppi object.
     *
     * @param dto
     * @return
     * @throws ExceptionMessage
     */
    private LOS createKomo(final Relaatiot5RowDTO dto) throws ExceptionMessage {
        //base values
        if (dto.getKoulutuskoodi() == null) {
            throw new IllegalArgumentException("Import data error - koulutuskoodi value cannot be null!");
        }
        
        KoulutusmoduuliKoosteTyyppi tutkintoKomo = new KoulutusmoduuliKoosteTyyppi();
        tutkintoKomo.setOid(this.oidService.newOid(NodeClassCode.TEKN_5));
        tutkintoKomo.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO);

        //search Uris from Koodisto for komo
        tutkintoKomo.setKoulutuskoodiUri(getUriWithVersion(dto.getKoulutuskoodi(), KoodistoURIHelper.KOODISTO_TUTKINTO_URI));
        
        tutkintoKomo.setOpintoalaUri(getUriWithVersion(dto.getOpintoalaKoodi(), KoodistoURIHelper.KOODISTO_OPINTOALA_URI));  //Automaalari
        tutkintoKomo.setKoulutusalaUri(getUriWithVersion(dto.getKoulutusalaKoodi(), KoodistoURIHelper.KOODISTO_KOULUTUSALA_URI));
        tutkintoKomo.setKoulutusasteUri(getUriWithVersion(dto.getKoulutusasteenKoodiarvo(), KoodistoURIHelper.KOODISTO_KOULUTUSASTE_URI));
        tutkintoKomo.setLaajuusyksikkoUri(getUriWithVersion(dto.getLaajuusyksikko(), KoodistoURIHelper.KOODISTO_OPINTOJEN_LAAJUUSYKSIKKO_URI)); //OV,OP           

        //T2_koodistot custom values (only finnish text)
        tutkintoKomo.setLaajuusarvoUri(dto.getLaajuusUri()); //120
        tutkintoKomo.setKoulutuksenRakenne(createTeksti(dto.getKoulutuksenRakenne(), null, null));
        tutkintoKomo.setTavoitteet(createTeksti(dto.getTavoitteet(), null, null));
        tutkintoKomo.setJatkoOpintoMahdollisuudet(createTeksti(dto.getJatkoOpinto(), null, null));
        tutkintoKomo.setKoulutustyyppi(dto.getKoulutusTyyppi());

        //create search words from Koodisto meta data 
        List<KoodiMetadataType> koulutuskoodiMeta = getKoodiMetadataTypes(dto.getKoulutuskoodi(), KoodistoURIHelper.KOODISTO_TUTKINTO_URI);
        
        KoulutusmoduuliKoosteTyyppi koKomo = new KoulutusmoduuliKoosteTyyppi();
        switch (dto.getKoulutusTyyppi()) {
            case AMMATILLINEN_PERUSKOULUTUS:
                List<KoodiMetadataType> koulutusohjelmaMeta = getKoodiMetadataTypes(dto.getKoulutusohjelmanKoodiarvo(), KoodistoURIHelper.KOODISTO_KOULUTUSOHJELMA_URI);
                koKomo.setKoulutusmoduulinNimi(SearchWordUtil.createSearchKeywords(koulutuskoodiMeta, koulutusohjelmaMeta));
                koKomo.setKoulutusohjelmakoodiUri(getUriWithVersion(dto.getKoulutusohjelmanKoodiarvo(), KoodistoURIHelper.KOODISTO_KOULUTUSOHJELMA_URI));
                break;
            case LUKIOKOULUTUS:
                List<KoodiMetadataType> lukiolinjaMeta = getKoodiMetadataTypes(dto.getLukiolinjaKoodiarvo(), KoodistoURIHelper.KOODISTO_LUKIOLINJA_URI);
                koKomo.setKoulutusmoduulinNimi(SearchWordUtil.createSearchKeywords(koulutuskoodiMeta, lukiolinjaMeta));
                koKomo.setLukiolinjakoodiUri(getUriWithVersion(dto.getLukiolinjaKoodiarvo(), KoodistoURIHelper.KOODISTO_LUKIOLINJA_URI));
                break;
        }
        
        koKomo.setOid(this.oidService.newOid(NodeClassCode.TEKN_5));
        koKomo.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
        koKomo.setTutkintonimikeUri(getUriWithVersion(dto.getTutkintonimikkeenKoodiarvo(), KoodistoURIHelper.KOODISTO_TUTKINTONIMIKE_URI));
        koKomo.setTavoitteet(createTeksti(dto.getKoulutusohjelmanTavoitteet(), null, null));
        koKomo.setParentOid(tutkintoKomo.getOid());
        koKomo.setKoulutustyyppi(dto.getKoulutusTyyppi());

        //TODO:  komo.setEqfLuokitus(dto.getEqfUri());

        return new LOS(tutkintoKomo, koKomo);
    }
    
    private void mergeExcels() throws IOException {
        final URL relaatiot5 = this.getClass().getResource("/Moduulit_TOINEN_ASTE_Relaatiot5.xls");
        final URL koulutusluokitus = this.getClass().getResource("/Koulutusluokitus_2011.xls");
        
        final KomoExcelReader<Relaatiot5RowDTO> readerForRelaatiot5 = new KomoExcelReader<Relaatiot5RowDTO>(Relaatiot5RowDTO.class, COLUMNS_RELAATIO5, MAX_READ_ROWS);
        loadedData = readerForRelaatiot5.read(relaatiot5.getPath(), false);
        
        final KomoExcelReader<KoulutusluokitusRowDTO> readerForKoulutusluokitus = new KomoExcelReader<KoulutusluokitusRowDTO>(KoulutusluokitusRowDTO.class, COLUMNS_KOULUTUSLUOKITUS, 3000);
        final Set<KoulutusluokitusRowDTO> koulutusluokitusDtos = readerForKoulutusluokitus.read(koulutusluokitus.getPath(), false);

        //merge the Excel files together by using koulutuskoodi as the key value
        for (Relaatiot5RowDTO r : loadedData) {
            boolean found = false;
            
            for (KoulutusluokitusRowDTO k : koulutusluokitusDtos) {
                if (r.getKoulutuskoodi().equals(k.getKoulutuskoodi())) {
                    r.setKoulutusalaNimi(k.getKoulutusalaNimi());
                    r.setKoulutusalaKoodi(k.getKoulutusalaKoodi());
                    r.setOpintoalaKoodi(k.getOpintoalaKoodi());
                    r.setOpintoalaNimi(k.getOpintoalaNimi());
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                log.warn("Required koulutuskoodi " + r.getKoulutuskoodi() + " relation not found.");
            }
        }
    }
    
    private KoulutusmoduuliTulos searchKomo(final KoulutusasteTyyppi koulutusasteTyyppi, final String tutkintoKoodi, final String koodi) {
        HaeKoulutusmoduulitKyselyTyyppi kysely = new HaeKoulutusmoduulitKyselyTyyppi();
       // kysely.setKoulutuskoodiUri(tutkintoKoodi);
        
        switch (koulutusasteTyyppi) {
            case AMMATILLINEN_PERUSKOULUTUS:
                kysely.setKoulutusohjelmakoodiUri(koodi);
                kysely.setKoulutustyyppi(AMMATILLINEN_PERUSKOULUTUS);
                break;
            case LUKIOKOULUTUS:
                kysely.setLukiolinjakoodiUri(koodi);
                kysely.setKoulutustyyppi(LUKIOKOULUTUS);
                break;
        }
        log.debug(koulutusasteTyyppi + " - search KOMO  by {} and {}", kysely.getKoulutuskoodiUri(), koodi);
        
        
        HaeKoulutusmoduulitVastausTyyppi result = tarjontaPublicService.haeKoulutusmoduulit(kysely);
        List<KoulutusmoduuliTulos> koulutusmoduuliTulos = result.getKoulutusmoduuliTulos();
        
        if (koulutusmoduuliTulos != null && !koulutusmoduuliTulos.isEmpty()) {
            if (koulutusmoduuliTulos.size() > 1) {
                for (KoulutusmoduuliTulos t : koulutusmoduuliTulos) {
                    log.debug("KoulutusmoduuliTulos : ", t.getKoulutusmoduuli().getKoulutuskoodiUri(), t.getKoulutusmoduuli().getKoulutusohjelmakoodiUri());
                }
                
                throw new RuntimeException("Search found too many KOMOs - single KOMO was expected. Result size : " + koulutusmoduuliTulos.size());
            }
            
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
                throw new IllegalArgumentException("KoulutusmoduuliKoosteTyyppi parent cannot be null!");
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
}
