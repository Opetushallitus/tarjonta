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
    public void testReadExcelRelaatiot5() throws Exception {
        URL resource = this.getClass().getResource("/Moduulit_TOINEN_ASTE_Relaatiot5.xls");
        boolean verbose = true;
        KomoExcelReader instance = new KomoExcelReader(Relaatiot5RowDTO.class, TarjontaKomoData.COLUMNS_RELAATIO5, 2);
        Set<Relaatiot5RowDTO> result = instance.read(resource.getPath(), verbose);

        assertEquals(1, result.size());
        Relaatiot5RowDTO next = result.iterator().next();

        assertEquals("1603", next.getKoulutusohjelmanKoodiarvo());
        assertEquals("10091", next.getTutkintonimikkeenKoodiarvo());
        assertEquals("32", next.getKoulutusasteenKoodiarvo());
        assertEquals("120", next.getLaajuus());
        assertEquals("4", next.getEqf());
        assertEquals(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS, next.getKoulutusTyyppi());
    }

    @Test
    public void testReadExcelKoulutusluokitus() throws Exception {
        URL resource = this.getClass().getResource("/Koulutusluokitus_2011.xls");
        boolean verbose = true;
        KomoExcelReader instance = new KomoExcelReader(KoulutusluokitusRowDTO.class, TarjontaKomoData.COLUMNS_KOULUTUSLUOKITUS, 2);
        Set<KoulutusluokitusRowDTO> result = instance.read(resource.getPath(), verbose);
        KoulutusluokitusRowDTO next = result.iterator().next();

        assertEquals(1, result.size());
        assertEquals("1000", next.getKoulutuskoodi());
        assertEquals("0", next.getKoulutusalaKoodi());
        assertEquals(null, next.getOpintoalaKoodi());
    }
    
//    @Test
//    public void testInit() throws Exception {
//        TarjontaKomoData instance = new TarjontaKomoData();
//
//        KoulutusmoduuliDAO koulutusModuuliDaoMock = createMock(KoulutusmoduuliDAO.class);
//        Whitebox.setInternalState(instance, "koulutusmoduuliDAO", koulutusModuuliDaoMock);
//        expect(koulutusModuuliDaoMock.insert(isA(Koulutusmoduuli.class))).andReturn(new Koulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA)).anyTimes();
//
//        replay(koulutusModuuliDaoMock);
//        instance.init();
//        verify(koulutusModuuliDaoMock);
//        Relaatiot5RowDTO firstRow = instance.getLoadedData().iterator().next();
//
//        assertEquals(120, instance.getLoadedData().size()); 
//
//        assertNotNull(firstRow.getKoulutusalaKoodi());
//        assertNotNull(firstRow.getOpintoalaKoodi());
//    }
}
