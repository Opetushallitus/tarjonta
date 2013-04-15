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

import fi.vm.sade.tarjonta.ui.loader.xls.helper.RelaatioMap;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.GenericRow;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.OppilaitostyyppiRow;
import fi.vm.sade.tarjonta.ui.loader.xls.helper.OppilaitostyyppiMap;
import java.net.URL;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jani
 */
public class TarjontaKomoDataTest {

    @Test
    public void testReadExcelAmm() throws Exception {
        URL resource = this.getClass().getResource("/KOULUTUS_KOULUTUSOHJELMA_RELAATIO.xls");
        boolean verbose = true;
        KomoExcelReader instance = new KomoExcelReader(GenericRow.class, DataReader.GENERIC_AMMATILLINEN, 2);
        Set<GenericRow> result = instance.read(resource.getPath(), verbose);

        assertEquals(1, result.size());
        GenericRow next = result.iterator().next();

        assertEquals("16031", next.getRelaatioKoodiarvo());
        assertEquals("321101", next.getKoulutuskoodiKoodiarvo());
        assertEquals("32", next.getKoulutusasteKoodiarvo());
        assertEquals("120", next.getLaajuusKoodiarvo());
        assertEquals("4", next.getEqfKoodiarvo());
        assertEquals("1", next.getLaajuusyksikkoKoodiarvo());
    }

    @Test
    public void testReadExcelLukio() throws Exception {
        URL resource = this.getClass().getResource("/KOULUTUS_LUKIOLINJAT_relaatio.xls");
        boolean verbose = true;
        KomoExcelReader instance = new KomoExcelReader(GenericRow.class, DataReader.GENERIC_LUKIO, 100);
        Set<GenericRow> result = instance.read(resource.getPath(), verbose);
        RelaatioMap excelDataMap = new RelaatioMap(result);

        assertEquals(87, result.size());
        GenericRow next = excelDataMap.get("0000");

        assertEquals("301101", next.getKoulutuskoodiKoodiarvo());
        assertEquals("31", next.getKoulutusasteKoodiarvo());
        assertEquals("70", next.getLaajuusKoodiarvo());
        assertEquals("2", next.getLaajuusyksikkoKoodiarvo());
        assertEquals("3", next.getEqfKoodiarvo());

        next = excelDataMap.get("0014");
        assertEquals("301104", next.getKoulutuskoodiKoodiarvo());

        next = excelDataMap.get("0086");
        assertEquals("301101", next.getKoulutuskoodiKoodiarvo());
    }

    @Test
    public void testReadExcelOppilaitostyyppis() throws Exception {
        URL resource = this.getClass().getResource("/OPPILAITOSTYYPPI_relaatiot.xls");
        boolean verbose = true;
        KomoExcelReader instance = new KomoExcelReader(OppilaitostyyppiRow.class, OppilaitostyyppiRow.OPPILAITOSTYYPPI_RELAATIOT, 100);
        Set<OppilaitostyyppiRow> result = instance.read(resource.getPath(), verbose);
        OppilaitostyyppiMap excelDataMap = new OppilaitostyyppiMap(result);

        assertEquals(26, result.size());
        List<String> next = excelDataMap.get("31");

        assertNotNull("result koulutusaste list cannot be null", next);
        assertEquals(3, next.size());

    }
}
