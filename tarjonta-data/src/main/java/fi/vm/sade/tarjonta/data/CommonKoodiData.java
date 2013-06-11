package fi.vm.sade.tarjonta.data;/*
 *
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

import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.tarjonta.data.dto.Koodi;
import fi.vm.sade.tarjonta.data.loader.xls.Column;
import fi.vm.sade.tarjonta.data.loader.xls.ExcelReader;
import fi.vm.sade.tarjonta.data.loader.xls.InputColumnType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

/**
 * @author: Tuomas Katva
 * Date: 13.2.2013
 */
public class CommonKoodiData {

    private final Logger log = LoggerFactory.getLogger(CommonKoodiData.class);
    private String fileUri = null;

    private Set<Koodi> loadedKoodis;

    public static final Column[] COMMON_KOODI_COLUMNS = {
            new Column("koodiArvo", "KOODIARVO", InputColumnType.STRING),
            new Column("koodiArvo", "ARVO", InputColumnType.STRING),
            new Column("koodiNimiFi", "NIMI_FI", InputColumnType.STRING),
            new Column("koodiNimiSv", "NIMI_SV", InputColumnType.STRING),
            new Column("koodiNimiEn", "NIMI_EN", InputColumnType.STRING),
            new Column("koodiKuvausFi", "KUVAUS_FI", InputColumnType.STRING),
            new Column("koodiLyhytNimiFi", "LYHYTNIMI_FI", InputColumnType.STRING),
            new Column("koodiLyhytNimiFi", "NIMILYHENNE_FI", InputColumnType.STRING),
            new Column("koodiLyhytNimiFi", "NIMILYHENNE", InputColumnType.STRING),
            new Column("koodiKuvausSv", "KUVAUS_SV", InputColumnType.STRING),
            new Column("koodiLyhytNimiSv", "LYHYTNIMI_SV", InputColumnType.STRING),
            new Column("koodiLyhytNimiSv", "NIMILYHENNE_SV", InputColumnType.STRING),
            new Column("koodiKuvausEn", "KUVAUS_EN", InputColumnType.STRING),
            new Column("koodiLyhytNimiEn", "LYHYTNIMI_EN", InputColumnType.STRING),
            new Column("koodiLyhytNimiEn", "NIMILYHENNE_EN", InputColumnType.STRING),
            new Column("alkuPvm", "ALKUPVM", InputColumnType.STRING),
            new Column("alkuPvm", "ALKU_PVM", InputColumnType.STRING),
            new Column("alkuPvm", "OTY_ALKUPVM", InputColumnType.STRING),
            new Column("loppuPvm", "LOPPUPVM", InputColumnType.STRING),
            new Column("loppuPvm", "LOPPU_PVM", InputColumnType.STRING),
            new Column("loppuPvm", "LAKKAUTUSPVM", InputColumnType.STRING)
    };

    public CommonKoodiData(final String filepath) throws IOException, ExceptionMessage {
        fileUri = filepath;
        if (fileUri != null && fileUri.length() > 1) {
            final ExcelReader<Koodi> koodiReader = new ExcelReader<Koodi>(Koodi.class, COMMON_KOODI_COLUMNS, Integer.MAX_VALUE);
            loadedKoodis = koodiReader.read(fileUri, true);
        } else {
            log.error("Invalid file path : {}", fileUri);
        }
    }

    public Set<Koodi> getLoadedKoodis() {
        return loadedKoodis;
    }

}
