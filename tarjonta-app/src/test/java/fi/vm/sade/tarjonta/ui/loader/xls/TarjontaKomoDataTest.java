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
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import java.net.URL;
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
        assertEquals(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS.value(), next.getKoulutusasteTyyppi());
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
        assertEquals("3", next.getEqfKoodiarvo());
        assertEquals(KoulutusasteTyyppi.LUKIOKOULUTUS.value(), next.getKoulutusasteTyyppi());

        next = excelDataMap.get("0014");
        assertEquals("301104", next.getKoulutuskoodiKoodiarvo());

        next = excelDataMap.get("0086");
        assertEquals("301101", next.getKoulutuskoodiKoodiarvo());
    }
}
