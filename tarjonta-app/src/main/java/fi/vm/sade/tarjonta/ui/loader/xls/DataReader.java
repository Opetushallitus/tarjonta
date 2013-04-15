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
import fi.vm.sade.tarjonta.ui.loader.xls.helper.RelaatioMap;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.GenericRow;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.KoulutusRelaatioRow;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.KoulutusohjelmanKuvauksetRow;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.LukionKoulutusModuulitRow;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.OppilaitostyyppiRow;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.Relaatiot5RowDTO;
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
 * @author Jani Wilén
 */
public class DataReader {

    private static final Logger log = LoggerFactory.getLogger(DataReader.class);
    private Set<Relaatiot5RowDTO> mergedData;

    public DataReader() throws IOException {
        //merge the Excel files together by using koulutuskoodi as the base key value
        mergedData = new HashSet<Relaatiot5RowDTO>();
        /* 
         * LUKIO 
         */
        convertLukioData();
        /* 
         * AMMATILLINEN 
         */
        convertAmmatillinenData();
        /*
         * Oppilaitostyyppi <-> koulutusaste relations*
         */
        OppilaitostyyppiMap oppilaitosTyyppiMap = createOppilaitosTyyppiMap();

        /*
         * MISC 
         */
        final KomoExcelReader<KoulutusRelaatioRow> readerForKoulutusRelations = new KomoExcelReader<KoulutusRelaatioRow>(KoulutusRelaatioRow.class, KoulutusRelaatioRow.COLUMNS, 3000);
        KoulutusRelaatioMap mapRelations = new KoulutusRelaatioMap(readerForKoulutusRelations.read(getFilePath(KoulutusRelaatioRow.FILENAME), false));

        TutkinnonKuvauksetMap tutkinnonKuvauksetMap = createTutkinnonKuvauksetMap();
        LukionModuulitMap createLukionModuulitMap = createLukionModuulitMap();

        for (Relaatiot5RowDTO r : mergedData) {
            final String keyKoulutuskoodi = r.getKoulutuskoodiKoodiarvo();

            if (mapRelations.containsKey(keyKoulutuskoodi)) {
                final KoulutusRelaatioRow rowKr = mapRelations.get(keyKoulutuskoodi);
                r.setKoulutusalaKoodi(rowKr.getKoulutusalaOph2002Koodiarvo());
                r.setOpintoalaKoodi(rowKr.getOpintoalaOph2002Koodiarvo());
                r.setKoulutusasteenKoodiarvo(rowKr.getKoulutusasteOph2002Koodiarvo());

                r.setOppilaitostyyppis(oppilaitosTyyppiMap.get(rowKr.getKoulutusasteOph2002Koodiarvo()));

                if (tutkinnonKuvauksetMap.containsKey(keyKoulutuskoodi)) {
                    r.setTutkinnonKuvaukset(tutkinnonKuvauksetMap.get(keyKoulutuskoodi));
                } else if (tutkinnonKuvauksetMap.containsKey(keyKoulutuskoodi)) {
                    r.setTutkinnonKuvaukset(createLukionModuulitMap.get(keyKoulutuskoodi));
                }

            } else {
                log.warn("Required koulutuskoodi " + r.getKoulutuskoodiKoodiarvo() + " relation not found.");
            }

        }

    }

    private void addImportedData(KoulutusasteTyyppi type, Map<String, GenericRow> mapBasicData, TutkintonimikeMap mapTutkintonimikes, KoulutusohjelmanKuvauksetMap koulutusohjelmanKuvaukset) {
        for (Map.Entry<String, GenericRow> e : mapBasicData.entrySet()) {
            final GenericRow value = e.getValue();

            Relaatiot5RowDTO row = new Relaatiot5RowDTO();
            row.setEqf(value.getEqfKoodiarvo());

            row.setTyyppi(type.value());
            //text data
//            row.setJatkoOpinto("");
//            row.setKoulutuksenRakenne("");
//            row.setTavoitteet("");

            final String lukiolinjaTaiKoulutuohjelmaKoodiarvo = value.getRelaatioKoodiarvo();

            //values
            row.setKoulutusasteenKoodiarvo(value.getKoulutusasteKoodiarvo());
            row.setKoulutuskoodiKoodiarvo(value.getKoulutuskoodiKoodiarvo());

            row.setLaajuus(value.getLaajuusKoodiarvo());
            row.setLaajuusyksikko(value.getLaajuusyksikkoKoodiarvo());

            switch (type) {
                case AMMATILLINEN_PERUSKOULUTUS:
                    row.setKoulutusohjelmanKuvaukset(koulutusohjelmanKuvaukset.get(lukiolinjaTaiKoulutuohjelmaKoodiarvo));
                    row.setKoulutusohjelmanKoodiarvo(lukiolinjaTaiKoulutuohjelmaKoodiarvo);
                    break;
                case LUKIOKOULUTUS:
                    row.setLukiolinjaKoodiarvo(lukiolinjaTaiKoulutuohjelmaKoodiarvo);
                    break;
                default:
                    throw new RuntimeException("An unsupported type " + type);
            }

            if (mapTutkintonimikes.containsKey(lukiolinjaTaiKoulutuohjelmaKoodiarvo)) {
                TutkintonimikeRow tutkintonimikeRow = mapTutkintonimikes.get(lukiolinjaTaiKoulutuohjelmaKoodiarvo);
                row.setTutkintonimikkeenKoodiarvo(tutkintonimikeRow.getTutkintonimikeKoodiarvo());
            } else {
                log.error("Require tutkintonimike koodi for value {}. Obj : {}", lukiolinjaTaiKoulutuohjelmaKoodiarvo, row);
                throw new RuntimeException("A required relation to tutkintonimike not found.");

            }

            mergedData.add(row);
        }
    }

    /**
     * @return the mergedData
     */
    public Set<Relaatiot5RowDTO> getData() {
        return mergedData;
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

        final KomoExcelReader<GenericRow> readerForLukio = new KomoExcelReader<GenericRow>(GenericRow.class, GenericRow.COLUMNS_LUKIO, 100);
        RelaatioMap mapLukio = new RelaatioMap(readerForLukio.read(getFilePath(GenericRow.FILENAME_LUKIO), true));

        final KomoExcelReader<TutkintonimikeRow> readerForLukioNimike = new KomoExcelReader<TutkintonimikeRow>(TutkintonimikeRow.class, TutkintonimikeRow.COLUMNS_LUKIO, 200);
        TutkintonimikeMap mapLukioNimikkeet = new TutkintonimikeMap(readerForLukioNimike.read(getFilePath(TutkintonimikeRow.FILENAME_LUKIO), true));

        addImportedData(KoulutusasteTyyppi.LUKIOKOULUTUS, mapLukio, mapLukioNimikkeet, null);
    }

    private void convertAmmatillinenData() throws IOException {
        final KomoExcelReader<GenericRow> readerForAmmatillinen = new KomoExcelReader<GenericRow>(GenericRow.class, GenericRow.COLUMNS_AMMATILLINEN, 200);
        RelaatioMap mapAmm = new RelaatioMap(readerForAmmatillinen.read(getFilePath(GenericRow.FILENAME_AMMATILLINEN), false));
        final KomoExcelReader<TutkintonimikeRow> readerForAmmNimike = new KomoExcelReader<TutkintonimikeRow>(TutkintonimikeRow.class, TutkintonimikeRow.COLUMNS_AMMATILLINEN, 200);
        TutkintonimikeMap mapAmmTukintonimike = new TutkintonimikeMap(readerForAmmNimike.read(getFilePath(TutkintonimikeRow.FILENAME_AMMATILLINEN), true));

        KoulutusohjelmanKuvauksetMap koulutusohjelmanKuvaukset = createKoulutusohjelmanKuvauksetMap();

        addImportedData(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS, mapAmm, mapAmmTukintonimike, koulutusohjelmanKuvaukset);
    }

    private OppilaitostyyppiMap createOppilaitosTyyppiMap() throws IOException {
        final KomoExcelReader<OppilaitostyyppiRow> readerOppilaitostyyppi = new KomoExcelReader<OppilaitostyyppiRow>(OppilaitostyyppiRow.class, OppilaitostyyppiRow.COLUMNS, 50);
        return new OppilaitostyyppiMap(readerOppilaitostyyppi.read(getFilePath(OppilaitostyyppiRow.FILENAME), false));
    }

    private LukionModuulitMap createLukionModuulitMap() throws IOException {
        final KomoExcelReader<LukionKoulutusModuulitRow> readerOppilaitostyyppi = new KomoExcelReader<LukionKoulutusModuulitRow>(LukionKoulutusModuulitRow.class, LukionKoulutusModuulitRow.COLUMNS, 6);
        return new LukionModuulitMap(readerOppilaitostyyppi.read(getFilePath(LukionKoulutusModuulitRow.FILENAME), false));
    }

    private TutkinnonKuvauksetMap createTutkinnonKuvauksetMap() throws IOException {
        final KomoExcelReader<TutkinnonKuvauksetNuoretRow> readerOppilaitostyyppi = new KomoExcelReader<TutkinnonKuvauksetNuoretRow>(TutkinnonKuvauksetNuoretRow.class, TutkinnonKuvauksetNuoretRow.COLUMNS, 6);
        return new TutkinnonKuvauksetMap(readerOppilaitostyyppi.read(getFilePath(TutkinnonKuvauksetNuoretRow.FILENAME), false));
    }

    private KoulutusohjelmanKuvauksetMap createKoulutusohjelmanKuvauksetMap() throws IOException {
        final KomoExcelReader<KoulutusohjelmanKuvauksetRow> readerOppilaitostyyppi = new KomoExcelReader<KoulutusohjelmanKuvauksetRow>(KoulutusohjelmanKuvauksetRow.class, KoulutusohjelmanKuvauksetRow.COLUMNS, 6);
        return new KoulutusohjelmanKuvauksetMap(readerOppilaitostyyppi.read(getFilePath(KoulutusohjelmanKuvauksetRow.FILENAME), false));
    }
}
