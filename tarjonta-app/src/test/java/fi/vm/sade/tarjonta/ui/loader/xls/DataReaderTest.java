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

import fi.vm.sade.tarjonta.ui.loader.xls.dto.ExcelMigrationDTO;
import java.io.IOException;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jani
 */
public class DataReaderTest {

    private Set<ExcelMigrationDTO> parnetkomosWithChildsDtos;
    private Set<ExcelMigrationDTO> onlyParentKomoDtos;

    public DataReaderTest() throws IOException {
        final DataReader instance = new DataReader();
        parnetkomosWithChildsDtos = instance.getData();
        onlyParentKomoDtos = instance.getValmentavaData();
    }

    @Test
    public void testImportCount() throws IOException {
        assertEquals(233, parnetkomosWithChildsDtos.size()); //total count of KOMOs
       // assertEquals(1, onlyParentKomoDtos.size()); //total count of KOMOs
    }

//    @Test
//    public void testVamentavaJaOpastavaKoulutusKOMO() throws IOException {
//        ExcelMigrationDTO result = onlyParentKomoDtos.iterator().next();
//        assertNotNull("Object not found", result);
//
//        assertEquals("xxxxxx", result.getKoulutuskoodiKoodiarvo());
//        assertEquals("32", result.getKoulutusasteenKoodiarvo());
//
//        assertEquals(0, result.getKoulutusohjelmanKuvaukset().getJatkoOpintomahdollisuudetTeksti().getTeksti().size());
//        assertEquals(0, result.getKoulutusohjelmanKuvaukset().getKoulutuksenRakenneTeksti().getTeksti().size());
//        assertEquals(0, result.getKoulutusohjelmanKuvaukset().getTavoiteTeksti().getTeksti().size());
//
//        assertEquals(3, result.getTutkinnonKuvaukset().getJatkoOpintomahdollisuudetTeksti().getTeksti().size());
//        assertEquals(3, result.getTutkinnonKuvaukset().getKoulutuksenRakenneTeksti().getTeksti().size());
//        assertEquals(3, result.getTutkinnonKuvaukset().getTavoiteTeksti().getTeksti().size());
//    }

    @Test
    public void testLukioKOMO() throws IOException {
        //LUKIO
        ExcelMigrationDTO result = searchByKoulutuskoodi("301101", null, "0001");
        assertNotNull("Object not found", result);

        assertEquals("31", result.getKoulutusasteenKoodiarvo());
        assertEquals(0, result.getKoulutusohjelmanKuvaukset().getJatkoOpintomahdollisuudetTeksti().getTeksti().size());
        assertEquals(0, result.getKoulutusohjelmanKuvaukset().getKoulutuksenRakenneTeksti().getTeksti().size());
        assertEquals(0, result.getKoulutusohjelmanKuvaukset().getTavoiteTeksti().getTeksti().size());

        assertEquals(2, result.getTutkinnonKuvaukset().getJatkoOpintomahdollisuudetTeksti().getTeksti().size());
        assertEquals(2, result.getTutkinnonKuvaukset().getKoulutuksenRakenneTeksti().getTeksti().size());
        assertEquals(2, result.getTutkinnonKuvaukset().getTavoiteTeksti().getTeksti().size());
    }

    @Test
    public void testLukioKOMO2() throws IOException {
        //LUKIO
        ExcelMigrationDTO result = searchByKoulutuskoodi("301104", null, "0014");
        assertNotNull("Object not found", result);

        assertEquals(0, result.getKoulutusohjelmanKuvaukset().getJatkoOpintomahdollisuudetTeksti().getTeksti().size());
        assertEquals(0, result.getKoulutusohjelmanKuvaukset().getKoulutuksenRakenneTeksti().getTeksti().size());
        assertEquals(0, result.getKoulutusohjelmanKuvaukset().getTavoiteTeksti().getTeksti().size());

        assertEquals(1, result.getTutkinnonKuvaukset().getJatkoOpintomahdollisuudetTeksti().getTeksti().size());
        assertEquals(1, result.getTutkinnonKuvaukset().getKoulutuksenRakenneTeksti().getTeksti().size());
        assertEquals(1, result.getTutkinnonKuvaukset().getTavoiteTeksti().getTeksti().size());

        assertEquals("", result.getTutkinnonKuvaukset().getJatkoOpintomahdollisuudetTeksti().getTeksti().get(0).getValue());
        assertEquals("", result.getTutkinnonKuvaukset().getKoulutuksenRakenneTeksti().getTeksti().get(0).getValue());
        assertEquals("", result.getTutkinnonKuvaukset().getTavoiteTeksti().getTeksti().get(0).getValue());
    }

    @Test
    public void testAmmKOMO() throws IOException {
        //AMMATILLINEN	
        ExcelMigrationDTO result = searchByKoulutuskoodi("321101", "1624", null);
        assertNotNull("Object not found", result);
        assertEquals("32", result.getKoulutusasteenKoodiarvo());
        assertEquals(2, result.getKoulutusohjelmanKuvaukset().getTavoiteTeksti().getTeksti().size());
        assertEquals(0, result.getKoulutusohjelmanKuvaukset().getKoulutuksenRakenneTeksti().getTeksti().size());
        assertEquals(0, result.getKoulutusohjelmanKuvaukset().getJatkoOpintomahdollisuudetTeksti().getTeksti().size());

        assertEquals(3, result.getTutkinnonKuvaukset().getKoulutuksenRakenneTeksti().getTeksti().size());
        assertEquals(3, result.getTutkinnonKuvaukset().getTavoiteTeksti().getTeksti().size());
        assertEquals(2, result.getTutkinnonKuvaukset().getJatkoOpintomahdollisuudetTeksti().getTeksti().size());
    }

    private ExcelMigrationDTO searchByKoulutuskoodi(String koulutuskoodi, String koulutusohjelmakoodi, String lukiolinja) {
        for (ExcelMigrationDTO dto : parnetkomosWithChildsDtos) {
            if (koulutusohjelmakoodi != null && koulutuskoodi.equals(dto.getKoulutuskoodiKoodiarvo()) && koulutusohjelmakoodi.equals(dto.getKoulutusohjelmanKoodiarvo())) {
                return dto;
            } else if (lukiolinja != null && koulutuskoodi.equals(dto.getKoulutuskoodiKoodiarvo()) && lukiolinja.equals(dto.getLukiolinjaKoodiarvo())) {
                return dto;
            }
        }

        return null;
    }
}