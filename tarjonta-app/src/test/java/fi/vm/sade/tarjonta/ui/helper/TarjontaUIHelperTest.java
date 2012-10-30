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
package fi.vm.sade.tarjonta.ui.helper;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * UI helper tests.
 *
 * @author mlyly
 */
public class TarjontaUIHelperTest {

    /**
     * Test of getKoodiURI method, of class TarjontaUIHelper.
     */
    @Test
    public void testUiHelper() {

        TarjontaUIHelper h = new TarjontaUIHelper();

        String koodiUri = "uri: xyxxy 837483";
        int version = 123;

        assertEquals(h.getKoodiURI(koodiUri + TarjontaUIHelper.KOODI_URI_AND_VERSION_SEPARATOR + version), koodiUri);
        assertEquals(h.getKoodiVersion(koodiUri + TarjontaUIHelper.KOODI_URI_AND_VERSION_SEPARATOR + version), version);

        assertEquals(h.getKoodiURI(koodiUri), koodiUri);
        assertEquals(h.getKoodiVersion(koodiUri), -1);

        Calendar cal = GregorianCalendar.getInstance();
        cal.set(2012, 9, 30, 23, 59, 59);
        Date d = cal.getTime();

        assertEquals("30.10.2012", h.formatDate(d));
        assertEquals("30.10.2012 23:59", h.formatDateTime(d));
        assertEquals("23:59", h.formatTime(d));
    }

}
