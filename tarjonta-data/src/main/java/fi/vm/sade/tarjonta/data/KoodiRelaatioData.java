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
import fi.vm.sade.tarjonta.data.dto.KoodiRelaatio;
import fi.vm.sade.tarjonta.data.loader.xls.Column;
import fi.vm.sade.tarjonta.data.loader.xls.ExcelReader;
import fi.vm.sade.tarjonta.data.loader.xls.InputColumnType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

/**
 * @author: Tuomas Katva
 * Date: 13.2.2013
 */
public class KoodiRelaatioData {

    private final Logger log = LoggerFactory.getLogger(CommonKoodiData.class);
    private String fileUri = null;

    private Set<KoodiRelaatio> koodiRelaatios;

    public static final Column[] COMMON_KOODI_RELATIONS = {
        new Column("koodiYlaArvo","YLA_KOODI_ARVO", InputColumnType.STRING),
        new Column("koodiAlaArvo","ALA_KOODI_ARVO",InputColumnType.STRING)
    };

    public KoodiRelaatioData(String filepath) throws IOException, ExceptionMessage {
        fileUri = filepath;
        if (fileUri != null && fileUri.length() > 1) {
//            final URL koodisFileURL = this.getClass().getResource(fileUri);

            final ExcelReader<KoodiRelaatio> koodiReader = new ExcelReader<KoodiRelaatio>(KoodiRelaatio.class, COMMON_KOODI_RELATIONS, Integer.MAX_VALUE);
            koodiRelaatios = koodiReader.read(fileUri,true);


        } else {
            log.error("Invalid file path : {}", fileUri);
        }

    }

    public Set<KoodiRelaatio> getKoodiRelaatios() {
        return koodiRelaatios;
    }

}
