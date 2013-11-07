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

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.oid.service.types.NodeClassCode;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitKyselyTyyppi;
import fi.vm.sade.tarjonta.service.types.HaeKoulutusmoduulitVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliKoosteTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTulos;
import fi.vm.sade.tarjonta.service.types.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaTilaTyyppi;
import fi.vm.sade.tarjonta.service.types.PaivitaTilaVastausTyyppi;
import fi.vm.sade.tarjonta.service.types.SisaltoTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.ExcelMigrationDTO;
import fi.vm.sade.tarjonta.ui.loader.xls.helper.RelaatioMap;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.GenericRow;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.KoulutusohjelmanKuvauksetRow;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.LukionKoulutusModuulitRow;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.OppilaitostyyppiRow;
import fi.vm.sade.tarjonta.ui.loader.xls.dto.TutkinnonKuvauksetNuoretRow;
import fi.vm.sade.tarjonta.ui.loader.xls.helper.OppilaitostyyppiMap;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.easymock.Capture;
import static org.easymock.EasyMock.*;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

/**
 *
 * @author jani
 */
public class TarjontaKomoDataTest {

    private static final KoulutusmoduuliKoosteTyyppi NOT_IMPLEMENTED = null;
    private TarjontaKomoData instance;
    private TarjontaKoodistoHelper tarjontaKoodistoHelperMock;
    private KoodiService koodiServiceMock;
    private TarjontaAdminService tarjontaAdminServiceMock;
    private TarjontaPublicService tarjontaPublicServiceMock;
    private OIDService oidServiceMock;
    private DataReader dataReader;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        dataReader = new DataReader();
        ExcelMigrationDTO find = Iterables.find(dataReader.getData(), searchMigrationObject);
        assertNotNull("Data in the excel files might have changed.", find);

        dataReader.getData().clear(); //remove all items
        dataReader.getData().add(find); //add only item

        KoodistoURI.KOODISTO_TUTKINTO_URI = "uri_tutkinto";
        KoodistoURI.KOODISTO_KOULUTUSOHJELMA_URI = "uri_koulutusohjelma";
        KoodistoURI.KOODISTO_KOULUTUSASTE_URI = "uri_koulutusaste";
        KoodistoURI.KOODISTO_KOULUTUSALA_URI = "uri_koulutusala";
        KoodistoURI.KOODISTO_TUTKINTONIMIKE_URI = "uri_tutkintonimike";
        KoodistoURI.KOODISTO_OPINTOALA_URI = "uri_opintoala";
        KoodistoURI.KOODISTO_OPINTOJEN_LAAJUUSYKSIKKO_URI = "uri_laajuusyksikko";
        KoodistoURI.KOODISTO_LUKIOLINJA_URI = "uri_lukiolinja";
        KoodistoURI.KOODISTO_OPPILAITOSTYYPPI_URI = "uri_oppilaitostyyppi";
        KoodistoURI.KOODISTO_EQF_LUOKITUS_URI = "uri_eqf";

        List<String> readLines = FileUtils.readLines(FileUtils.getFile("src", "test", "resources", "test_resource_koodis.csv"), "UTF8");
        Map<String, KoodiType> map = new HashMap<String, KoodiType>();

        for (String line : readLines) {
            String[] split = line.split(",");
            KoodiType k = new KoodiType();
            k.setKoodiArvo(split[0].trim());
            k.setKoodiUri(split[1].trim() + "_" + split[0].trim());
            k.setVersio(1);
            map.put(TarjontaKomoData.createUniqueKey(k.getKoodiArvo(), split[1].trim()), k);
        }
        instance = new TarjontaKomoData();

        tarjontaKoodistoHelperMock = createMock(TarjontaKoodistoHelper.class);
        koodiServiceMock = createMock(KoodiService.class);
        tarjontaAdminServiceMock = createMock(TarjontaAdminService.class);
        tarjontaPublicServiceMock = createMock(TarjontaPublicService.class);
        oidServiceMock = createMock(OIDService.class);

        Whitebox.setInternalState(instance, "dataReader", dataReader);
        Whitebox.setInternalState(instance, "mapKoodistos", map);
        Whitebox.setInternalState(instance, "oidService", oidServiceMock);
        Whitebox.setInternalState(instance, "tarjontaKoodistoHelper", tarjontaKoodistoHelperMock);
        Whitebox.setInternalState(instance, "koodiService", koodiServiceMock);
        Whitebox.setInternalState(instance, "tarjontaAdminService", tarjontaAdminServiceMock);
        Whitebox.setInternalState(instance, "tarjontaPublicService", tarjontaPublicServiceMock);
    }

    @After
    public void tearDown() throws Exception {
    }
    private int oid = 1;

//    @Test
//    public void readValmentava() throws IOException {
//        final KomoExcelReader<GenericRow> readerForValmentava = new KomoExcelReader<GenericRow>(GenericRow.class, GenericRow.COLUMNS_VALMENTAVA, 10);
//        RelaatioMap relaatioMap = new RelaatioMap(readerForValmentava.read(url.getPath(), true), false);
//        assertEquals(1, relaatioMap.size());
//    }
    @Test
    public void testReadExcelAmm() throws Exception {
        final URL resource = filenameToURL("KOULUTUS_KOULUTUSOHJELMA_RELAATIO");
        boolean verbose = true;
        KomoExcelReader instance = new KomoExcelReader(GenericRow.class, GenericRow.COLUMNS_AMMATILLINEN, 2);
        Set<GenericRow> result = instance.read(resource.getPath(), verbose);

        assertEquals(1, result.size());
        GenericRow next = result.iterator().next();

        assertEquals(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS, next.getKoulutusasteTyyppiEnum());
        assertEquals("1603", next.getRelaatioKoodiarvo());
        assertEquals("321101", next.getKoulutuskoodiKoodiarvo());
        assertEquals("32", next.getKoulutusasteKoodiarvo());
        assertEquals("120", next.getLaajuusKoodiarvo());
        assertEquals("4", next.getEqfKoodiarvo());
        assertEquals("1", next.getLaajuusyksikkoKoodiarvo());

        instance = new KomoExcelReader(GenericRow.class, GenericRow.COLUMNS_AMMATILLINEN, 300);
        result = instance.read(resource.getPath(), verbose);
        int founded = 0;
        for (GenericRow g : result) {
            if (g.getKoulutuskoodiKoodiarvo().equals("039999")) { //special case
                assertEquals(KoulutusasteTyyppi.VALMENTAVA_JA_KUNTOUTTAVA_OPETUS, g.getKoulutusasteTyyppiEnum());
                assertEquals("0003", g.getRelaatioKoodiarvo());
                assertEquals("32", g.getKoulutusasteKoodiarvo());
                assertEquals(null, g.getLaajuusKoodiarvo());
                assertEquals("3", g.getEqfKoodiarvo());
                assertEquals(null, g.getLaajuusyksikkoKoodiarvo());
                founded++;
            } else if (g.getKoulutuskoodiKoodiarvo().equals("039996")) { //special case
                assertEquals(KoulutusasteTyyppi.AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS, g.getKoulutusasteTyyppiEnum());
                assertEquals("0005", g.getRelaatioKoodiarvo());
                assertEquals("32", g.getKoulutusasteKoodiarvo());
                assertEquals(null, g.getLaajuusKoodiarvo());
                assertEquals(null, g.getEqfKoodiarvo());
                assertEquals(null, g.getLaajuusyksikkoKoodiarvo());
                founded++;
            } else if (g.getKoulutuskoodiKoodiarvo().equals("039997")) { //special case
                assertEquals(KoulutusasteTyyppi.MAAHANM_LUKIO_VALMISTAVA_KOULUTUS, g.getKoulutusasteTyyppiEnum());
                assertEquals("0090", g.getRelaatioKoodiarvo());
                assertEquals("31", g.getKoulutusasteKoodiarvo());
                assertEquals(null, g.getLaajuusKoodiarvo());
                assertEquals(null, g.getEqfKoodiarvo());
                assertEquals(null, g.getLaajuusyksikkoKoodiarvo());
                founded++;
            } else if (g.getKoulutuskoodiKoodiarvo().equals("039998")) { //special case
                assertEquals(KoulutusasteTyyppi.MAAHANM_AMM_VALMISTAVA_KOULUTUS, g.getKoulutusasteTyyppiEnum());
                assertEquals("0006", g.getRelaatioKoodiarvo());
                assertEquals("32", g.getKoulutusasteKoodiarvo());
                assertEquals(null, g.getLaajuusKoodiarvo());
                assertEquals(null, g.getEqfKoodiarvo());
                assertEquals(null, g.getLaajuusyksikkoKoodiarvo());
                founded++;
            } else if (g.getKoulutuskoodiKoodiarvo().equals("099999")) { //special case
                assertEquals(KoulutusasteTyyppi.VAPAAN_SIVISTYSTYON_KOULUTUS, g.getKoulutusasteTyyppiEnum());
                assertEquals("0007", g.getRelaatioKoodiarvo());
                assertEquals("90", g.getKoulutusasteKoodiarvo());
                assertEquals(null, g.getLaajuusKoodiarvo());
                assertEquals(null, g.getEqfKoodiarvo());
                assertEquals(null, g.getLaajuusyksikkoKoodiarvo());
                founded++;
            } else if (g.getKoulutuskoodiKoodiarvo().equals("020075")) { //special case
                assertEquals(KoulutusasteTyyppi.PERUSOPETUKSEN_LISAOPETUS, g.getKoulutusasteTyyppiEnum());
                assertEquals("0004", g.getRelaatioKoodiarvo());
                assertEquals("22", g.getKoulutusasteKoodiarvo());
                assertEquals(null, g.getLaajuusKoodiarvo());
                assertEquals(null, g.getEqfKoodiarvo());
                assertEquals(null, g.getLaajuusyksikkoKoodiarvo());
                founded++;
            }
        }
        assertEquals("One or many special cases are missing", 6, founded);
        assertEquals("1", next.getLaajuusyksikkoKoodiarvo());
    }

    @Test
    public void testReadExcelLukio() throws Exception {
        final URL resource = filenameToURL("KOULUTUS_LUKIOLINJAT_relaatio");
        boolean verbose = true;
        KomoExcelReader instance = new KomoExcelReader(GenericRow.class, GenericRow.COLUMNS_LUKIO, 100);
        Set<GenericRow> result = instance.read(resource.getPath(), verbose);
        RelaatioMap excelDataMap = new RelaatioMap(result, true);

        assertEquals("count of excel rows", 87, result.size());
        GenericRow next = excelDataMap.get("0000");

        assertEquals(KoulutusasteTyyppi.LUKIOKOULUTUS, next.getKoulutusasteTyyppiEnum());
        assertEquals("301101", next.getKoulutuskoodiKoodiarvo());
        assertEquals("31", next.getKoulutusasteKoodiarvo());
        assertEquals("75", next.getLaajuusKoodiarvo());
        assertEquals("4", next.getLaajuusyksikkoKoodiarvo());
        assertEquals("3", next.getEqfKoodiarvo());

        next = excelDataMap.get("0014");
        assertEquals("301104", next.getKoulutuskoodiKoodiarvo());

        next = excelDataMap.get("0086");
        assertEquals("301101", next.getKoulutuskoodiKoodiarvo());

        next = excelDataMap.get("0089");
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
        assertNotNull("JatkoOpintomahdollisuudetTeksti fi", next.getJatkoOpintomahdollisuudetTekstiFi());
        assertNotNull("KoulutuksellisetTeksti fi", next.getKoulutuksellisetTekstiFi());
        assertNotNull("KoulutuksenRakenneTeksti fi", next.getKoulutuksenRakenneTekstiFi());

        assertNotNull("JatkoOpintomahdollisuudetTeksti sv", next.getJatkoOpintomahdollisuudetTekstiSv());
        assertNotNull("KoulutuksellisetTeksti sv ", next.getKoulutuksellisetTekstiSv());
        assertNotNull("KoulutuksenRakenneTeksti sv", next.getKoulutuksenRakenneTekstiSv());
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
        assertEquals(null, next.getKoulutuksellisetJaAmmatillisetTavoitteetEnTeksti());
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

    /**
     * Test of getLoadedData method, of class TarjontaKomoData.
     *
     * The test uses the real KOMO excel data!
     */
    @Test
    public void testGetLoadedDataUpdate() throws IOException, ExceptionMessage {

        final Capture<KoulutusmoduuliKoosteTyyppi> komoParent = new Capture<KoulutusmoduuliKoosteTyyppi>();
        final Capture<KoulutusmoduuliKoosteTyyppi> komoChild = new Capture<KoulutusmoduuliKoosteTyyppi>();
        expect(oidServiceMock.newOid(NodeClassCode.TEKN_5)).andReturn("random_oid_" + createNeOid()).anyTimes();
        expect(tarjontaAdminServiceMock.paivitaKoulutusmoduuli(capture(komoParent))).andReturn(NOT_IMPLEMENTED);
        expect(tarjontaAdminServiceMock.paivitaKoulutusmoduuli(capture(komoChild))).andReturn(NOT_IMPLEMENTED);

        HaeKoulutusmoduulitVastausTyyppi result = new HaeKoulutusmoduulitVastausTyyppi();

        /*
         * KOMO PARENT
         */
        KoulutusmoduuliTulos parentTulos = new KoulutusmoduuliTulos();
        KoulutusmoduuliKoosteTyyppi parentKomo = new KoulutusmoduuliKoosteTyyppi();
        parentKomo.setKoulutustyyppi(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS);
        parentKomo.setKoulutuskoodiUri("uri_tutkinto_371101#1");
        parentKomo.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO);
        parentKomo.setOid("komo_parent_oid_" + createNeOid());
        parentTulos.setKoulutusmoduuli(parentKomo);

        /*
         * KOMO CHILD
         */
        KoulutusmoduuliTulos childTulos = new KoulutusmoduuliTulos();
        KoulutusmoduuliKoosteTyyppi parentChild = new KoulutusmoduuliKoosteTyyppi();
        parentChild.setParentOid(parentKomo.getOid());
        parentChild.setOid("komo_child_oid_" + createNeOid());
        parentChild.setKoulutuskoodiUri("uri_tutkinto_371101#1");
        parentChild.setKoulutustyyppi(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS);
        parentChild.setKoulutusmoduuliTyyppi(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
        parentChild.setKoulutusohjelmakoodiUri("uri_koulutusohjelma_1511#1");
        childTulos.setKoulutusmoduuli(parentChild);

        result.getKoulutusmoduuliTulos().add(childTulos);
        result.getKoulutusmoduuliTulos().add(parentTulos);

        expect(tarjontaPublicServiceMock.haeKoulutusmoduulit(isA(HaeKoulutusmoduulitKyselyTyyppi.class))).andReturn(result);

        /*
         * replays
         */
        replay(oidServiceMock);
        replay(tarjontaAdminServiceMock);
        replay(tarjontaPublicServiceMock);
        replay(koodiServiceMock);
        replay(tarjontaKoodistoHelperMock);

        /*
         * Presenter method call
         */
        instance.createData(true);

        /*
         * verify
         */
        verify(oidServiceMock);
        verify(tarjontaAdminServiceMock);
        verify(tarjontaPublicServiceMock);
        verify(koodiServiceMock);
        verify(tarjontaKoodistoHelperMock);

        assertEquals(null, komoParent.getValue().getParentOid());
        assertEquals("uri_tutkinto_371101#1", komoParent.getValue().getKoulutuskoodiUri());
        assertEquals(null, komoParent.getValue().getKoulutusohjelmakoodiUri());
        assertEquals(KoulutusmoduuliTyyppi.TUTKINTO, komoParent.getValue().getKoulutusmoduuliTyyppi());

        assertNotNull(komoChild.getValue().getParentOid());
        assertEquals("uri_tutkinto_371101#1", komoChild.getValue().getKoulutuskoodiUri());
        assertEquals("uri_koulutusohjelma_1511#1", komoChild.getValue().getKoulutusohjelmakoodiUri());
        assertEquals(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA, komoChild.getValue().getKoulutusmoduuliTyyppi());
    }

    /**
     * The test uses the real KOMO excel data!
     *
     * @throws IOException
     * @throws ExceptionMessage
     */
    @Test
    public void testGetLoadedDataInsert() throws IOException, ExceptionMessage {
        final Capture<KoulutusmoduuliKoosteTyyppi> komoParent = new Capture<KoulutusmoduuliKoosteTyyppi>();
        final Capture<KoulutusmoduuliKoosteTyyppi> komoChild = new Capture<KoulutusmoduuliKoosteTyyppi>();

        expect(oidServiceMock.newOid(NodeClassCode.TEKN_5)).andReturn("random_oid_" + createNeOid()).anyTimes();
        expect(tarjontaAdminServiceMock.lisaaKoulutusmoduuli(capture(komoParent))).andReturn(expectKoulutusmoduuliKoosteTyyppi());
        expect(tarjontaAdminServiceMock.lisaaKoulutusmoduuli(capture(komoChild))).andReturn(expectKoulutusmoduuliKoosteTyyppi());

        expect(tarjontaAdminServiceMock.paivitaTilat(isA(PaivitaTilaTyyppi.class))).andReturn(new PaivitaTilaVastausTyyppi());
        final Capture<HaeKoulutusmoduulitKyselyTyyppi> kysely = new Capture<HaeKoulutusmoduulitKyselyTyyppi>();

        expect(tarjontaPublicServiceMock.haeKoulutusmoduulit(capture(kysely))).andAnswer(
                new IAnswer<HaeKoulutusmoduulitVastausTyyppi>() {
                    @Override
                    public HaeKoulutusmoduulitVastausTyyppi answer() throws Throwable {
                        HaeKoulutusmoduulitVastausTyyppi v = new HaeKoulutusmoduulitVastausTyyppi();
                        v.getKoulutusmoduuliTulos();

                        return v;
                    }
                });

        /*
         * replay
         */
        replay(oidServiceMock);
        replay(tarjontaAdminServiceMock);
        replay(tarjontaPublicServiceMock);
        replay(koodiServiceMock);
        replay(tarjontaKoodistoHelperMock);

        /*
         * Presenter method call
         */
        instance.createData(true);

        /*
         * verify
         */
        verify(oidServiceMock);
        verify(tarjontaAdminServiceMock);
        verify(tarjontaPublicServiceMock);
        verify(koodiServiceMock);
        verify(tarjontaKoodistoHelperMock);

        assertNotNull(komoParent.getValue().getOid());
        assertNull(komoParent.getValue().getParentOid());
        assertEquals("uri_tutkinto_371101#1", komoParent.getValue().getKoulutuskoodiUri());
        assertEquals(null, komoParent.getValue().getKoulutusohjelmakoodiUri());
        assertEquals(KoulutusmoduuliTyyppi.TUTKINTO, komoParent.getValue().getKoulutusmoduuliTyyppi());

        assertNotNull(komoChild.getValue().getOid());
        assertNotNull(komoChild.getValue().getParentOid());
        assertEquals("uri_tutkinto_371101#1", komoChild.getValue().getKoulutuskoodiUri());
        assertEquals("uri_koulutusohjelma_1511#1", komoChild.getValue().getKoulutusohjelmakoodiUri());
        assertEquals(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA, komoChild.getValue().getKoulutusmoduuliTyyppi());
    }

    private int createNeOid() {
        return oid++;
    }
    private Predicate<ExcelMigrationDTO> searchMigrationObject = new Predicate<ExcelMigrationDTO>() {
        ExcelMigrationDTO excelMigrationDTO;

        private ExcelMigrationDTO getSearchObject() {
            if (excelMigrationDTO == null) {
                excelMigrationDTO = new ExcelMigrationDTO();
                excelMigrationDTO.setKoulutuskoodiKoodiarvo("371101");
                excelMigrationDTO.setKoulutusohjelmanKoodiarvo("1511");
            }
            return excelMigrationDTO;
        }

        @Override
        public boolean apply(ExcelMigrationDTO t) {
            return getSearchObject().equals(t);
        }
    };

    private KoulutusmoduuliKoosteTyyppi expectKoulutusmoduuliKoosteTyyppi() {
        KoulutusmoduuliKoosteTyyppi tyyppi = new KoulutusmoduuliKoosteTyyppi();
        tyyppi.setOid("komo_" + createNeOid());
        tyyppi.setKoulutuskoodiUri("uri_tutkinto_371101#1");
        return tyyppi;
    }
}
