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
package fi.vm.sade.tarjonta.service.impl.conversion;

import fi.vm.sade.generic.service.conversion.SadeConversionService;
import fi.vm.sade.tarjonta.model.Koulutusmoduuli;
import fi.vm.sade.tarjonta.model.KoulutusmoduuliTyyppi;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliDTO;
import fi.vm.sade.tarjonta.model.dto.TutkintoOhjelmaDTO;
import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 */
@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TutkintoOhjelmaToDTOConverterTest {

    @Autowired
    private SadeConversionService conversionService;

    /**
     * Test that conversion from concrete type works
     *
     * HUOM. koulutusmoduleiden konversio toistaiseksi deprekoitu, saa poistaa heti kun on todettu josko conversiota
     * ei kayteta ollenkaan.
     *
     */
    @Test
    @Ignore
    public void testConvertTutkintoOhjelmaModelToDTO() {

        Koulutusmoduuli model = new Koulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
        TutkintoOhjelmaDTO dto = conversionService.convert(model, TutkintoOhjelmaDTO.class);
        assertNotNull(dto);
    }

    /**
     * Test that conversion from abstract base class works.
     */
    @Test
    @Ignore
    public void testConvertTutkintoOhjelmaModelToDTOUsingBaseClass() {

        Koulutusmoduuli model = new Koulutusmoduuli(KoulutusmoduuliTyyppi.TUTKINTO_OHJELMA);
        KoulutusmoduuliDTO dto = conversionService.convert(model, KoulutusmoduuliDTO.class);

        assertNotNull(dto);
        assertTrue(dto instanceof TutkintoOhjelmaDTO);

    }

}

