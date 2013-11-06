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

import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import static fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS;
import fi.vm.sade.tarjonta.ui.loader.xls.helper.RelaatioMap;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.GenericRow;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.KoulutusRelaatioRow;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.KoulutusohjelmanKuvauksetRow;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.LukionKoulutusModuulitRow;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.OppilaitostyyppiRow;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.ExcelMigrationDTO;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.TutkinnonKuvauksetNuoretRow;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.TutkintonimikeRow;
import fi.vm.sade.tarjonta.ui.loader.xls.helper.KoulutusRelaatioMap;
import fi.vm.sade.tarjonta.ui.loader.xls.helper.KoulutusohjelmanKuvauksetMap;
import fi.vm.sade.tarjonta.ui.loader.xls.helper.LukionModuulitMap;
import fi.vm.sade.tarjonta.ui.loader.xls.helper.OppilaitostyyppiMap;
import fi.vm.sade.tarjonta.ui.loader.xls.helper.TutkinnonKuvauksetMap;
import fi.vm.sade.tarjonta.ui.loader.xls.helper.TutkintonimikeMap;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Read and convert all excel data rows to DTOs. This class only work as
 * container for the imported data. If you need the logic that uses the data,
 * then please look more information from TarjontaKomoData createKomo.
 *
 * @author Jani Wilén
 */
public class DataReader {

    private static final Logger log = LoggerFactory.getLogger(DataReader.class);
    private static final int DEFAULT_READ_LIMIT_ROWS_MIN = 500;
    private static final int DEFAULT_READ_LIMIT_ROWS_HUGE = 3000;
    /*
     * container for pre-processed data from excel files.
     */
    private Set<ExcelMigrationDTO> mergedData;
    /*
     * container for pre-processed data from excel files.
     */
    private Set<ExcelMigrationDTO> valmentavaData;

    /**
     *
     * @throws IOException
     */
    public DataReader() throws IOException {
        //merge the Excel files together by using koulutuskoodi as the base key value
        mergedData = new HashSet<ExcelMigrationDTO>();
        valmentavaData = new HashSet<ExcelMigrationDTO>();

        /* 
         * LUKIO : data conversion to BASE DTO 
         */
        convertLukioData();
        /* 
         * AMMATILLINEN : data conversion to BASE DTO
         */
        convertAmmatillinenData();

        /*
         * LUKIO & AMMATILLINEN
         * 
         * Some of the relations might need the base values from above 
         * methods, or it's just more easier to handle them at pre-process.
         */
        convertMiscData();
    }

    private void addKoulutusohjelmaRelatedData(Map<String, GenericRow> mapBasicData, TutkintonimikeMap mapTutkintonimikes, KoulutusohjelmanKuvauksetMap koulutusohjelmanKuvaukset) {
        for (Map.Entry<String, GenericRow> e : mapBasicData.entrySet()) {
            final GenericRow basic = e.getValue();

            ExcelMigrationDTO row = new ExcelMigrationDTO();
            row.setEqf(basic.getEqfKoodiarvo());

            row.setKoulutusateTyyppi(basic.getKoulutusasteTyyppiEnum());
            final String koulutusohjelmaKoodi = basic.getRelaatioKoodiarvo();

            //values
            row.setKoulutusasteenKoodiarvo(basic.getKoulutusasteKoodiarvo());
            row.setKoulutuskoodiKoodiarvo(basic.getKoulutuskoodiKoodiarvo());

            row.setLaajuus(basic.getLaajuusKoodiarvo());
            row.setLaajuusyksikko(basic.getLaajuusyksikkoKoodiarvo());

            switch (basic.getKoulutusasteTyyppiEnum()) {
                case AMMATILLINEN_PERUSKOULUTUS:
                    row.setKoulutusohjelmanKuvaukset(koulutusohjelmanKuvaukset.get(koulutusohjelmaKoodi));
                    row.setKoulutusohjelmanKoodiarvo(koulutusohjelmaKoodi);
                    checkTutkintonimike(row, mapTutkintonimikes, koulutusohjelmaKoodi);
                    break;
                case LUKIOKOULUTUS:
                    /*
                     * KOULUTUSOHJELMA KOODI => LUKIOLINJA KOODI!
                     * DO NOT TRY TO CHANGE THIS!
                     */
                    row.setLukiolinjaKoodiarvo(koulutusohjelmaKoodi);
                    checkTutkintonimike(row, mapTutkintonimikes, koulutusohjelmaKoodi);
                    break;
                case MAAHANM_AMM_VALMISTAVA_KOULUTUS:
                case MAAHANM_LUKIO_VALMISTAVA_KOULUTUS:
                case VAPAAN_SIVISTYSTYON_KOULUTUS:
                case PERUSOPETUKSEN_LISAOPETUS:
                case AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS:
                case VALMENTAVA_JA_KUNTOUTTAVA_OPETUS:
                    row.setKoulutusohjelmanKoodiarvo(koulutusohjelmaKoodi);
                    break;
                default:
                    throw new RuntimeException("An unsupported type " + basic.getKoulutusasteTyyppiEnum());
            }
            mergedData.add(row);
        }
    }

    private void checkTutkintonimike(ExcelMigrationDTO row, TutkintonimikeMap mapTutkintonimikes, String koulutusohjelmaKoodi) {
        if (mapTutkintonimikes.containsKey(koulutusohjelmaKoodi)) {
            TutkintonimikeRow tutkintonimikeRow = mapTutkintonimikes.get(koulutusohjelmaKoodi);
            row.setTutkintonimikkeenKoodiarvo(tutkintonimikeRow.getTutkintonimikeKoodiarvo());
        } else {
            log.error("Require tutkintonimike koodi for value {}. Obj : {}", koulutusohjelmaKoodi, row);
            throw new RuntimeException("A required relation to tutkintonimike not found.");
        }
    }

    /**
     * @return the mergedData
     */
    public Set<ExcelMigrationDTO> getData() {
        return mergedData;
    }

    /**
     * @return the mergedData
     */
    public Set<ExcelMigrationDTO> getValmentavaData() {
        return valmentavaData;
    }

    private String getFilePath(String resourceFilename) {
        final String filename = "/" + resourceFilename + ".xls";
        log.info("Trying to load resource {} ...", filename);
        final URL url = this.getClass().getResource(filename);

        if (url == null) {
            throw new RuntimeException("Unable to load resource with filename '" + filename + "'.");
        }

        return url.getPath();
    }

    private void convertLukioData() throws IOException {

        final KomoExcelReader<GenericRow> readerForLukio = new KomoExcelReader<GenericRow>(GenericRow.class, GenericRow.COLUMNS_LUKIO, DEFAULT_READ_LIMIT_ROWS_MIN);
        RelaatioMap mapLukio = new RelaatioMap(readerForLukio.read(getFilePath(GenericRow.FILENAME_LUKIO), true), true);

        final KomoExcelReader<TutkintonimikeRow> readerForLukioNimike = new KomoExcelReader<TutkintonimikeRow>(TutkintonimikeRow.class, TutkintonimikeRow.COLUMNS_LUKIO, DEFAULT_READ_LIMIT_ROWS_MIN);
        TutkintonimikeMap mapLukioNimikkeet = new TutkintonimikeMap(readerForLukioNimike.read(getFilePath(TutkintonimikeRow.FILENAME_LUKIO), true));

        addKoulutusohjelmaRelatedData(mapLukio, mapLukioNimikkeet, null);
    }

    private void convertAmmatillinenData() throws IOException {
        final KomoExcelReader<GenericRow> readerForAmmatillinen = new KomoExcelReader<GenericRow>(GenericRow.class, GenericRow.COLUMNS_AMMATILLINEN, DEFAULT_READ_LIMIT_ROWS_MIN);
        RelaatioMap mapAmm = new RelaatioMap(readerForAmmatillinen.read(getFilePath(GenericRow.FILENAME_AMMATILLINEN), false), true);
        final KomoExcelReader<TutkintonimikeRow> readerForAmmNimike = new KomoExcelReader<TutkintonimikeRow>(TutkintonimikeRow.class, TutkintonimikeRow.COLUMNS_AMMATILLINEN, DEFAULT_READ_LIMIT_ROWS_MIN);
        TutkintonimikeMap mapAmmTukintonimike = new TutkintonimikeMap(readerForAmmNimike.read(getFilePath(TutkintonimikeRow.FILENAME_AMMATILLINEN), true));

        KoulutusohjelmanKuvauksetMap koulutusohjelmanKuvaukset = createAmmKoulutusohjelmanKuvauksetMap();
        addKoulutusohjelmaRelatedData(mapAmm, mapAmmTukintonimike, koulutusohjelmanKuvaukset);
    }

//for future use... 
//    private void convertValamentavaJaKuntouttava() throws IOException {
//        final KomoExcelReader<GenericRow> readerForValmentava = new KomoExcelReader<GenericRow>(GenericRow.class, GenericRow.COLUMNS_VALMENTAVA, DEFAULT_READ_LIMIT_ROWS_MIN);
//        RelaatioMap mapVal = new RelaatioMap(readerForValmentava.read(getFilePath(GenericRow.VALMENTAVA_JA_KUNTOUTTAVA), true), false);
//        addKoulutusohjelmaRelatedData(mapVal, null, null);
//    }
    private void convertMiscData() throws IOException {
        /*
         * Oppilaitostyyppi <-> koulutusaste relations (not needed?)
         */
        OppilaitostyyppiMap oppilaitosTyyppiMap = createOppilaitosTyyppiMap();

        /*
         * MISC  maps
         */
        final KomoExcelReader<KoulutusRelaatioRow> readerForKoulutusRelations = new KomoExcelReader<KoulutusRelaatioRow>(KoulutusRelaatioRow.class, KoulutusRelaatioRow.COLUMNS, DEFAULT_READ_LIMIT_ROWS_HUGE);
        KoulutusRelaatioMap mapRelations = new KoulutusRelaatioMap(readerForKoulutusRelations.read(getFilePath(KoulutusRelaatioRow.KOULUTUS_RELAATIOT), false));

        TutkinnonKuvauksetMap ammTutkinnonKuvauksetMap = createAmmTutkinnonKuvauksetMap();
        LukionModuulitMap lukionModuulitMap = createLukionModuulitMap();

        /*
         * Override the base data object with misc excel data.
         */
        for (ExcelMigrationDTO dto : valmentavaData) {
            final String keyKoulutuskoodi = dto.getKoulutuskoodiKoodiarvo();

            if (mapRelations.containsKey(keyKoulutuskoodi)) {
                convert(dto, oppilaitosTyyppiMap, keyKoulutuskoodi, mapRelations);

                if (ammTutkinnonKuvauksetMap.containsKey(keyKoulutuskoodi)) {
                    //DESC DATA FOR AMMATILLINEN KOULUTUS
                    dto.setTutkinnonKuvaukset(ammTutkinnonKuvauksetMap.get(keyKoulutuskoodi));
                }
            }
        }

        for (ExcelMigrationDTO dto : mergedData) {
            final String keyKoulutuskoodi = dto.getKoulutuskoodiKoodiarvo();

            if (mapRelations.containsKey(keyKoulutuskoodi)) {
                convert(dto, oppilaitosTyyppiMap, keyKoulutuskoodi, mapRelations);

                if (ammTutkinnonKuvauksetMap.containsKey(keyKoulutuskoodi)) {
                    //DESC DATA FOR AMMATILLINEN KOULUTUS
                    dto.setTutkinnonKuvaukset(ammTutkinnonKuvauksetMap.get(keyKoulutuskoodi));
                } else if (lukionModuulitMap.containsKey(keyKoulutuskoodi)) {
                    //DESC DATA FOR LUKIOKOULUTUS
                    dto.setTutkinnonKuvaukset(lukionModuulitMap.get(keyKoulutuskoodi));
                }
            } else {
                log.warn("Required koulutuskoodi " + dto.getKoulutuskoodiKoodiarvo() + " relation not found.");
            }
        }
    }

    private static void convert(ExcelMigrationDTO dto, final OppilaitostyyppiMap oppilaitosTyyppiMap, final String keyKoulutuskoodi, final KoulutusRelaatioMap mapRelations) {
        final KoulutusRelaatioRow rowKr = mapRelations.get(keyKoulutuskoodi);
        dto.setKoulutusalaKoodi(rowKr.getKoulutusalaOph2002Koodiarvo());
        dto.setOpintoalaKoodi(rowKr.getOpintoalaOph2002Koodiarvo());
        dto.setKoulutusasteenKoodiarvo(rowKr.getKoulutusasteOph2002Koodiarvo());
        dto.setOppilaitostyyppis(oppilaitosTyyppiMap.get(rowKr.getKoulutusasteOph2002Koodiarvo()));
    }

    private OppilaitostyyppiMap createOppilaitosTyyppiMap() throws IOException {
        final KomoExcelReader<OppilaitostyyppiRow> readerOppilaitostyyppi = new KomoExcelReader<OppilaitostyyppiRow>(OppilaitostyyppiRow.class, OppilaitostyyppiRow.COLUMNS, DEFAULT_READ_LIMIT_ROWS_MIN);
        return new OppilaitostyyppiMap(readerOppilaitostyyppi.read(getFilePath(OppilaitostyyppiRow.FILENAME), false));
    }

    private LukionModuulitMap createLukionModuulitMap() throws IOException {
        final KomoExcelReader<LukionKoulutusModuulitRow> readerOppilaitostyyppi = new KomoExcelReader<LukionKoulutusModuulitRow>(LukionKoulutusModuulitRow.class, LukionKoulutusModuulitRow.COLUMNS, DEFAULT_READ_LIMIT_ROWS_MIN);
        return new LukionModuulitMap(readerOppilaitostyyppi.read(getFilePath(LukionKoulutusModuulitRow.FILENAME), false));
    }

    private TutkinnonKuvauksetMap createAmmTutkinnonKuvauksetMap() throws IOException {
        final KomoExcelReader<TutkinnonKuvauksetNuoretRow> readerOppilaitostyyppi = new KomoExcelReader<TutkinnonKuvauksetNuoretRow>(TutkinnonKuvauksetNuoretRow.class, TutkinnonKuvauksetNuoretRow.COLUMNS, DEFAULT_READ_LIMIT_ROWS_HUGE);
        return new TutkinnonKuvauksetMap(readerOppilaitostyyppi.read(getFilePath(TutkinnonKuvauksetNuoretRow.FILENAME), false));
    }

    private KoulutusohjelmanKuvauksetMap createAmmKoulutusohjelmanKuvauksetMap() throws IOException {
        final KomoExcelReader<KoulutusohjelmanKuvauksetRow> readerOppilaitostyyppi = new KomoExcelReader<KoulutusohjelmanKuvauksetRow>(KoulutusohjelmanKuvauksetRow.class, KoulutusohjelmanKuvauksetRow.COLUMNS, DEFAULT_READ_LIMIT_ROWS_HUGE);
        return new KoulutusohjelmanKuvauksetMap(readerOppilaitostyyppi.read(getFilePath(KoulutusohjelmanKuvauksetRow.FILENAME), false));
    }
}
