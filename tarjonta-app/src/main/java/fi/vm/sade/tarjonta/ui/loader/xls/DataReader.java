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
import fi.vm.sade.tarjonta.ui.loader.xls.Column;
import fi.vm.sade.tarjonta.ui.loader.xls.InputColumnType;
import fi.vm.sade.tarjonta.ui.loader.xls.KomoExcelReader;
import fi.vm.sade.tarjonta.ui.loader.xls.helper.RelaatioMap;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.GenericRow;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.KoulutusRelaatioRow;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.Relaatiot5RowDTO;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.TutkintonimikeRow;
import fi.vm.sade.tarjonta.ui.loader.xls.helper.KoulutuskoodiMap;
import fi.vm.sade.tarjonta.ui.loader.xls.helper.TutkintonimikeMap;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
    public static final Column[] GENERIC_TUTKINTONIMIKE_LUKIO = {
        new Column("relaatioKoodiarvo", "LUKIOLNJA", InputColumnType.INTEGER),
        new Column("tutkintonimikeKoodiarvo", "TUTKINTONIMIKE", InputColumnType.INTEGER)
    };
    public static final Column[] GENERIC_TUTKINTONIMIKE_AMMATILLINEN = {
        new Column("relaatioKoodiarvo", "KOULUTUSOHJELMA", InputColumnType.INTEGER),
        new Column("tutkintonimikeKoodiarvo", "TUTKINTONIMIKE", InputColumnType.INTEGER)
    };
    public static final Column[] GENERIC_AMMATILLINEN = {
        new Column("koulutuskoodiKoodiarvo", "KOULUTUS", InputColumnType.INTEGER),
        new Column("relaatioKoodiarvo", "KOULUTUOSOHJELMA", InputColumnType.INTEGER),
        new Column("koulutusasteTyyppi", "Koulutusaste", InputColumnType.INTEGER),
        new Column("koulutusasteKoodiarvo", "Koulutusasteen", InputColumnType.INTEGER),
        new Column("laajuusKoodiarvo", "Laajuus", InputColumnType.INTEGER),
        new Column("laajuusyksikkoKoodiarvo", "Laajuusyksikko", InputColumnType.INTEGER),
        new Column("eqfKoodiarvo", "EQF", InputColumnType.INTEGER)
    };
    public static final Column[] GENERIC_LUKIO = {
        new Column("koulutuskoodiKoodiarvo", "KOULUTUS", InputColumnType.INTEGER),
        new Column("relaatioKoodiarvo", "LUKIOLINJA", InputColumnType.STRING),
        new Column("koulutusasteTyyppi", "Koulutusaste", InputColumnType.INTEGER),
        new Column("koulutusasteKoodiarvo", "Koulutusasteen", InputColumnType.INTEGER),
        new Column("laajuusKoodiarvo", "Laajuus", InputColumnType.INTEGER),
        new Column("laajuusyksikkoKoodiarvo", "Laajuusyksikko", InputColumnType.INTEGER),
        new Column("eqfKoodiarvo", "EQF", InputColumnType.INTEGER)
    };
    public static final Column[] KOULUTUS_RELAATIOT = {
        new Column("koulutuskoodiKoodiarvo", "KOULUTUS", InputColumnType.INTEGER),
        new Column("koulutusluokituksenTasoKoodiarvo", "KOULUTUSLUOKITUKSEN TASO", InputColumnType.INTEGER),
        new Column("koulutusalaOph2002Koodiarvo", "KOULUTUSALAOPH2002", InputColumnType.INTEGER),
        new Column("opintoalaOph2002Koodiarvo", "OPINTOALAOPH2002", InputColumnType.INTEGER),
        new Column("koulutusasteOph2002Koodiarvo", "KOULUTUSASTEOPH2002", InputColumnType.INTEGER),
        new Column("koulutusalaOph1995Koodiarvo", "KOULUTUSALAOPH1995", InputColumnType.INTEGER),
        new Column("opintoala1995Koodiarvo", "OPINTOALAOPH1995", InputColumnType.INTEGER),
        new Column("koulutusasteOph1997Koodiarvo", "KOULUTUSASTEOPH1995", InputColumnType.INTEGER),
        new Column("koulutusasteIsced1997Koodiarvo", "KOULUTUSASTE ISCED1997", InputColumnType.INTEGER),
        new Column("koulutusalaIsced1997Koodiarvo", "KOULUTUSALA ISCED1997", InputColumnType.INTEGER)
    };
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
         * MISC 
         */
        final KomoExcelReader<KoulutusRelaatioRow> readerForKoulutusRelations = new KomoExcelReader<KoulutusRelaatioRow>(KoulutusRelaatioRow.class, KOULUTUS_RELAATIOT, 3000);
        KoulutuskoodiMap mapRelations = new KoulutuskoodiMap(readerForKoulutusRelations.read(getFilePath("KOULUTUS_RELAATIOT"), false));

        for (Relaatiot5RowDTO r : mergedData) {
            final String keyKoulutuskoodi = r.getKoulutuskoodi();

            if (mapRelations.containsKey(keyKoulutuskoodi)) {
                final KoulutusRelaatioRow rowKr = mapRelations.get(keyKoulutuskoodi);
                r.setKoulutusalaKoodi(rowKr.getKoulutusalaOph2002Koodiarvo());
                r.setOpintoalaKoodi(rowKr.getOpintoalaOph2002Koodiarvo());
                r.setKoulutusasteenKoodiarvo(rowKr.getKoulutusasteOph2002Koodiarvo());
            } else {
                log.warn("Required koulutuskoodi " + r.getKoulutuskoodi() + " relation not found.");
            }
        }
    }

    private void addImportedData(Map<String, GenericRow> mapBasicData, TutkintonimikeMap mapTutkintonimikes) {
        for (Map.Entry<String, GenericRow> e : mapBasicData.entrySet()) {
            final GenericRow value = e.getValue();

            Relaatiot5RowDTO row = new Relaatiot5RowDTO();
            row.setEqf(value.getEqfKoodiarvo());

            row.setTyyppi(value.getKoulutusasteTyyppi());
            //text data
            row.setJatkoOpinto("");
            row.setKoulutuksenRakenne("");
            row.setTavoitteet("");

            final String relaatioKoodiarvo = value.getRelaatioKoodiarvo();

            //values
            row.setKoulutusasteenKoodiarvo(value.getKoulutusasteKoodiarvo());
            row.setKoulutuskoodi(value.getKoulutuskoodiKoodiarvo());

            row.setLaajuus(value.getLaajuusKoodiarvo());
            row.setLaajuusyksikko(value.getLaajuusyksikkoKoodiarvo());

            switch (KoulutusasteTyyppi.fromValue(value.getKoulutusasteTyyppi())) {
                case AMMATILLINEN_PERUSKOULUTUS:
                    row.setKoulutusohjelmanKoodiarvo(relaatioKoodiarvo);
                    break;
                case LUKIOKOULUTUS:
                    row.setLukiolinjaKoodiarvo(relaatioKoodiarvo);
                    break;
                default:
                    throw new RuntimeException("An unsupported type.");
            }

            if (mapTutkintonimikes.containsKey(relaatioKoodiarvo)) {
                TutkintonimikeRow tutkintonimikeRow = mapTutkintonimikes.get(relaatioKoodiarvo);
                row.setTutkintonimikkeenKoodiarvo(tutkintonimikeRow.getTutkintonimikeKoodiarvo());
            } else {
                log.error("Require tutkintonimike koodi for value {}. Obj : {}", relaatioKoodiarvo, row);
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
        final URL urlAmmTutkintonimike = this.getClass().getResource("/" + resourceFilename + ".xls");

        return urlAmmTutkintonimike.getPath();
    }

    private void convertLukioData() throws IOException {

        final KomoExcelReader<GenericRow> readerForLukio = new KomoExcelReader<GenericRow>(GenericRow.class, GENERIC_LUKIO, 100);
        RelaatioMap mapLukio = new RelaatioMap(readerForLukio.read(getFilePath("KOULUTUS_LUKIOLINJAT_relaatio"), true));

        final KomoExcelReader<TutkintonimikeRow> readerForLukioNimike = new KomoExcelReader<TutkintonimikeRow>(TutkintonimikeRow.class, GENERIC_TUTKINTONIMIKE_LUKIO, 200);
        TutkintonimikeMap mapLukioNimikkeet = new TutkintonimikeMap(readerForLukioNimike.read(getFilePath("LUKIOLINJA_TUTKINTONIMIKE_relaatio"), true));
        addImportedData(mapLukio, mapLukioNimikkeet);
    }

    private void convertAmmatillinenData() throws IOException {
        final KomoExcelReader<GenericRow> readerForAmmatillinen = new KomoExcelReader<GenericRow>(GenericRow.class, GENERIC_AMMATILLINEN, 200);
        RelaatioMap mapAmm = new RelaatioMap(readerForAmmatillinen.read(getFilePath("KOULUTUS_KOULUTUSOHJELMA_RELAATIO"), false));
        final KomoExcelReader<TutkintonimikeRow> readerForAmmNimike = new KomoExcelReader<TutkintonimikeRow>(TutkintonimikeRow.class, GENERIC_TUTKINTONIMIKE_AMMATILLINEN, 200);
        TutkintonimikeMap mapAmmTukintonimike = new TutkintonimikeMap(readerForAmmNimike.read(getFilePath("TUTKINTONIMIKKEET_koulutusohjelmat_relaatio"), true));

        addImportedData(mapAmm, mapAmmTukintonimike);
    }
}
