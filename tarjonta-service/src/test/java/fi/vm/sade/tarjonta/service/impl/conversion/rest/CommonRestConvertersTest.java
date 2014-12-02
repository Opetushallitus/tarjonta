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
package fi.vm.sade.tarjonta.service.impl.conversion.rest;


import fi.vm.sade.tarjonta.model.MonikielinenTeksti;
import fi.vm.sade.tarjonta.model.Osoite;
import fi.vm.sade.tarjonta.service.resources.dto.OsoiteRDTO;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class CommonRestConvertersTest {

    @Test
    public void toMonikielinenTeksti() {
        Map<String, String> input = new HashMap<String, String>();

        input.put("kieli_fi", "suomi");
        input.put("kieli_en", "english");

        MonikielinenTeksti monikielinenTeksti = CommonRestConverters.toMonikielinenTeksti(input);

        assertEquals("suomi", monikielinenTeksti.getTekstiForKieliKoodi("kieli_fi"));
        assertEquals("english", monikielinenTeksti.getTekstiForKieliKoodi("kieli_en"));
        assertTrue(monikielinenTeksti.getKaannoksetAsList().size() == 2);
    }

    @Test
    public void toStringMap() {
        MonikielinenTeksti monikielinenTeksti = new MonikielinenTeksti();
        monikielinenTeksti.addTekstiKaannos("kieli_fi", "suomi");
        monikielinenTeksti.addTekstiKaannos("kieli_en", "english");

        Map<String, String> stringMap = CommonRestConverters.toStringMap(monikielinenTeksti);

        assertEquals("suomi", stringMap.get("kieli_fi"));
        assertEquals("english", stringMap.get("kieli_en"));
        assertTrue(stringMap.size() == 2);
    }

    @Test
    public void toOsoite() {
        OsoiteRDTO dto = new OsoiteRDTO();
        dto.setOsoiterivi1("osoite 1");
        dto.setOsoiterivi2("osoite 2");
        dto.setPostinumero("12345");
        dto.setPostitoimipaikka("postitoimipaikka");

        Osoite osoite = CommonRestConverters.toOsoite(dto);

        assertEquals("osoite 1", osoite.getOsoiterivi1());
        assertEquals("osoite 2", osoite.getOsoiterivi2());
        assertEquals("12345", osoite.getPostinumero());
        assertEquals("postitoimipaikka", osoite.getPostitoimipaikka());
    }
}