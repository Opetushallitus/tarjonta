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
package fi.vm.sade.tarjonta.data;

import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.tarjonta.data.dto.YhteishakuKooditDTO;
import fi.vm.sade.tarjonta.data.loader.xls.Column;
import fi.vm.sade.tarjonta.data.loader.xls.ExcelReader;
import fi.vm.sade.tarjonta.data.loader.xls.InputColumnType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

public class HakukohdeData {
    private static final Logger log = LoggerFactory.getLogger(HakukohdeData.class);
    private static final String FILE_URI = "/YHTEISHAKUKOODIT_TOINEN_ASTE_Relaatiot2.xls";
    private Set<YhteishakuKooditDTO> loadedData;
    public static final Column[] YHTEISHAKUKOODIT_TOINEN_ASTE = {
            //property, title desc, type= conversion type
            new Column("koulutusohjelmanNimi", "KOULUTUSOHJELMAN NIMI", InputColumnType.STRING),
            new Column("koulutusohjelmanKoodiarvo", "KOULUTUSOHJELMAN KOODIARVO", InputColumnType.INTEGER),
            new Column("tutkintonimike", "TUTKINTONIMIKE", InputColumnType.STRING),
            new Column("tutkintonimikkeenKoodiarvo", "TUTKINTONIMIKKEEN KOODIARVO", InputColumnType.INTEGER),
            new Column("tutkinnonNimi", "TUTKINNON NIMI", InputColumnType.STRING),
            new Column("koulutuskoodi", "KOULUTUSKOODI(TUTKINTOKOODI)", InputColumnType.INTEGER),
            new Column("hakukohdeKoodiArvo", "HAKUKOHDEKOODIARVO", InputColumnType.STRING),
            new Column("hakukohteenNimi", "HAKUKOHTEEN NIMI", InputColumnType.STRING)
    };

    public HakukohdeData() throws IOException, ExceptionMessage {
        log.info("Load a file : {}", FILE_URI);
        final URL relaatiot5 = this.getClass().getResource(FILE_URI);

        final ExcelReader<YhteishakuKooditDTO> readerForRelaatiot5 = new ExcelReader<YhteishakuKooditDTO>(YhteishakuKooditDTO.class, YHTEISHAKUKOODIT_TOINEN_ASTE, 800);
        loadedData = readerForRelaatiot5.read(relaatiot5.getPath(), false);
    }

    public Set<YhteishakuKooditDTO> getLoadedData() {
        return loadedData;
    }
}
