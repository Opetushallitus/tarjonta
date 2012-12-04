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

import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.easymock.EasyMock.*;
import org.powermock.reflect.Whitebox;

/**
 * UI helper tests.
 *
 * @author mlyly
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(KoodiService.class)
public class TarjontaUIHelperTest {

    private static final String TEXT_EN = "text";
    private static final String TEXT_FI = "teksti";

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

    /**
     * Test of getKoodiNimi method, of class TarjontaUIHelper.
     */
    @Test
    public void testGetKoodiNimi_String() {
        // Mock only the modifyData method
        KoodiService createMock = createMock(KoodiService.class);
        I18NHelper createMock1 = createMock(I18NHelper.class);

        TarjontaUIHelper instance = new TarjontaUIHelper();
        Set<String> uris = new HashSet<String>();
        uris.add("URI : aaa1#1");
        uris.add("URI : bbb1#2");
        uris.add("URI : ccc1#122");

        List<KoodiType> list = new ArrayList<KoodiType>();
        KoodiType koodiType1 = new KoodiType();
        koodiType1.setKoodiArvo("arvi1");
        koodiType1.setKoodiUri("uri1");
        //new
        KoodiMetadataType koodiMetadataType1 = new KoodiMetadataType();
        koodiMetadataType1.setKieli(KieliType.FI);
        koodiMetadataType1.setNimi(TEXT_FI);

        KoodiMetadataType koodiMetadataType2 = new KoodiMetadataType();
        koodiMetadataType2.setKieli(KieliType.EN);
        koodiMetadataType2.setNimi(TEXT_EN);

        koodiType1.getMetadata().add(koodiMetadataType1);
        koodiType1.getMetadata().add(koodiMetadataType2);

        KoodiType koodiType2 = new KoodiType();
        koodiType2.setKoodiArvo("arvo2");
        koodiType2.setKoodiUri("uri2");
        koodiType2.getMetadata().add(koodiMetadataType1);
        koodiType2.getMetadata().add(koodiMetadataType2);

        list.add(koodiType2);

        Whitebox.setInternalState(instance, "_i18n", createMock1);
        Whitebox.setInternalState(instance, "_koodiService", createMock);
        expect(createMock.searchKoodis(isA(SearchKoodisCriteriaType.class))).andReturn(list).times(3);
        expect(createMock1.getMessage(isA(String.class))).andReturn("lang_key_property").anyTimes();

        replay(createMock);
        replay(createMock1);
        String result = instance.getKoodiNimi(uris, new Locale("FI"));
        verify(createMock);
        verify(createMock1);

        assertEquals("teksti, teksti, teksti", result);
    }
}
