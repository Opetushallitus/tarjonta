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
import fi.vm.sade.tarjonta.ui.loader.xls.dto.KoulutusohjelmanKuvauksetRow;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.LukionKoulutusModuulitRow;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.OppilaitostyyppiRow;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.TutkinnonKuvauksetNuoretRow;
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
        final URL resource = filenameToURL("KOULUTUS_KOULUTUSOHJELMA_RELAATIO");
        boolean verbose = true;
        KomoExcelReader instance = new KomoExcelReader(GenericRow.class, GenericRow.COLUMNS_AMMATILLINEN, 2);
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
        final URL resource = filenameToURL("KOULUTUS_LUKIOLINJAT_relaatio");
        boolean verbose = true;
        KomoExcelReader instance = new KomoExcelReader(GenericRow.class, GenericRow.COLUMNS_LUKIO, 100);
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
        final URL resource = filenameToURL(OppilaitostyyppiRow.FILENAME);
        boolean verbose = true;
        KomoExcelReader instance = new KomoExcelReader(OppilaitostyyppiRow.class, OppilaitostyyppiRow.COLUMNS, 100);
        Set<OppilaitostyyppiRow> result = instance.read(resource.getPath(), verbose);
        OppilaitostyyppiMap excelDataMap = new OppilaitostyyppiMap(result);

        assertEquals(26, result.size());
        List<String> next = excelDataMap.get("31");

        assertNotNull("result koulutusaste list cannot be null", next);
        assertEquals(3, next.size());
    }

    @Test
    public void testReadExcelKoulutusohjelmanKuvaukset() throws Exception {
        final URL resource = filenameToURL(KoulutusohjelmanKuvauksetRow.FILENAME);
        boolean verbose = true;
        KomoExcelReader instance = new KomoExcelReader(KoulutusohjelmanKuvauksetRow.class, KoulutusohjelmanKuvauksetRow.COLUMNS, 2);
        Set<KoulutusohjelmanKuvauksetRow> result = instance.read(resource.getPath(), verbose);

        assertEquals(1, result.size());
        KoulutusohjelmanKuvauksetRow next = result.iterator().next();

        assertEquals("1501", next.getKoulutusohjelmaKoodiarvo());
        assertNotNull("KoulutusohjelmanSeliteTeksti", next.getKoulutusohjelmanSeliteTeksti());
        assertNotNull("KoulutusohjelmanTavoiteFiTeksti", next.getKoulutusohjelmanTavoiteFiTeksti());
        assertNotNull("getKoulutusohjelmanTavoiteSvTeksti", next.getKoulutusohjelmanTavoiteSvTeksti());
    }

    @Test
    public void testReadExcelLukionModuulit() throws Exception {
        final URL resource = filenameToURL(LukionKoulutusModuulitRow.FILENAME);
        boolean verbose = true;
        KomoExcelReader instance = new KomoExcelReader(LukionKoulutusModuulitRow.class, LukionKoulutusModuulitRow.COLUMNS, 2);
        Set<LukionKoulutusModuulitRow> result = instance.read(resource.getPath(), verbose);

        assertEquals(1, result.size());
        LukionKoulutusModuulitRow next = result.iterator().next();

        assertEquals("301101", next.getKoulutuskoodiKoodiarvo());
        assertNotNull("JatkoOpintomahdollisuudetTeksti", next.getJatkoOpintomahdollisuudetTeksti());
        assertNotNull("KoulutuksellisetTeksti", next.getKoulutuksellisetTeksti());
        assertNotNull("KoulutuksenRakenneTeksti", next.getKoulutuksenRakenneTeksti());
    }

    @Test
    public void testReadExcelTutkinnonKuvaukset() throws Exception {
        final URL resource = filenameToURL(TutkinnonKuvauksetNuoretRow.FILENAME);
        boolean verbose = true;
        KomoExcelReader instance = new KomoExcelReader(TutkinnonKuvauksetNuoretRow.class, TutkinnonKuvauksetNuoretRow.COLUMNS, 2);
        Set<TutkinnonKuvauksetNuoretRow> result = instance.read(resource.getPath(), verbose);

        assertEquals(1, result.size());
        TutkinnonKuvauksetNuoretRow next = result.iterator().next();

        assertEquals("321602", next.getKoulutuskoodiKoodiarvo());
        assertNotNull("KoulutuksenRakenneSvTeksti", next.getKoulutuksenRakenneSvTeksti());
        assertEquals(null, next.getJatkoOpintomahdollisuudetEnTeksti());
        assertNotNull("JatkoOpintomahdollisuudetFiTeksti", next.getJatkoOpintomahdollisuudetFiTeksti());
        assertNotNull("JatkoOpintomahdollisuudetSvTeksti", next.getJatkoOpintomahdollisuudetSvTeksti());
        assertEquals("NULL", next.getKoulutuksellisetJaAmmatillisetTavoitteetEnTeksti());
        assertNotNull("KoulutuksellisetJaAmmatillisetTavoitteetFiTeksti", next.getKoulutuksellisetJaAmmatillisetTavoitteetFiTeksti());
        assertNotNull("KoulutuksellisetJaAmmatillisetTavoitteetSvTeksti", next.getKoulutuksellisetJaAmmatillisetTavoitteetSvTeksti());
        assertEquals(null, next.getKoulutuksenRakenneEnTeksti());
        assertNotNull("KoulutuksenRakenneFiTeksti", next.getKoulutuksenRakenneFiTeksti());
        assertNotNull("TukinnonNimiFiTeksti", next.getTukinnonNimiFiTeksti());
        assertNotNull("TukinnonNimiSvTeksti", next.getTukinnonNimiSvTeksti());
    }

    private URL filenameToURL(final String filename) {
        return this.getClass().getResource("/" + filename + ".xls");
    }
}
