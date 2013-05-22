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

import com.google.common.collect.Lists;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.KoulutuskoodiRowModel;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jani
 */
public class RegexModelFilterTest {

    private static final String STR_PART_A = "Elementary,";
    private static final String STR_PART_B = " my dear Watson.";
    private static final String STR_CASE = "SeArch";
    private static final String STR_NOCASE = "search param";
    private static final String KOODI_VALUE1 = "221";
    private static final String KOODI_VALUE2 = "12345678";
    private static final String KOODI_VALUE3 = "1234";
    private static final String NOT_IN_SEARCH_WORDS = "hauki_on_kala";

    public RegexModelFilterTest() {
    }

    @Before
    public void setUp() {
    }

    @Test
    public void testBuildRegexString() {
        Set<String> results = RegexModelFilter.buildRegexString("12345 321 abc", RegexModelFilter.PATTERN_NUMBER);
        assertEquals(2, results.size());

        results = RegexModelFilter.buildRegexString("12345 321", RegexModelFilter.PATTERN_NUMBER);
        assertEquals(2, results.size());

        results = RegexModelFilter.buildRegexString("abc 1234 yo y", RegexModelFilter.PATTERN_ALPHA);
        assertEquals(3, results.size());

        results = RegexModelFilter.buildRegexString("abc yo y", RegexModelFilter.PATTERN_ALPHA);
        assertEquals(3, results.size());
    }

    @Test
    public void testFilterByParams() {
        List<KoulutuskoodiRowModel> codes = Lists.<KoulutuskoodiRowModel>newArrayList();

        RegexModelFilter<KoulutuskoodiRowModel> instance = new RegexModelFilter<KoulutuskoodiRowModel>();
        KoulutuskoodiRowModel r1 = new KoulutuskoodiRowModel();
        r1.setKoodi(KOODI_VALUE1);
        r1.setNimi(STR_PART_A + STR_PART_B);
        codes.add(r1);

        assertEquals("#1.1 start object count", 1, codes.size());
        List<KoulutuskoodiRowModel> result = instance.filterByParams(codes, KOODI_VALUE1 + " " + STR_PART_A + "      " + STR_PART_B);
        assertEquals("#1.1 end object count", 1, result.size());
        result = instance.filterByParams(codes, KOODI_VALUE1 + " " + STR_PART_A + " " + NOT_IN_SEARCH_WORDS);
        assertEquals("#1.2 end object count", 0, result.size());

        KoulutuskoodiRowModel r2 = new KoulutuskoodiRowModel();
        r2.setKoodi(KOODI_VALUE2);
        r2.setNimi(STR_CASE);
        codes.add(r2);

        assertEquals("#2 start object count", 2, codes.size());
        instance.setStrictKoodiarvoFilter(KOODI_VALUE2);
        result = instance.filterByParams(codes, "");
        assertEquals("#2 end object count", 1, result.size());

        KoulutuskoodiRowModel r3 = new KoulutuskoodiRowModel();
        r3.setKoodi(KOODI_VALUE3);
        r3.setNimi(STR_NOCASE);
        codes.add(r3);

        assertEquals("#3 start object count", 3, codes.size());
        instance.setStrictKoodiarvoFilter(KOODI_VALUE3);
        result = instance.filterByParams(codes, STR_CASE);
        assertEquals("#3 end object count", 1, result.size());

        KoulutuskoodiRowModel mismatch = new KoulutuskoodiRowModel();
        mismatch.setKoodi(KOODI_VALUE2);
        mismatch.setNimi(STR_NOCASE);
        codes.add(mismatch);

        assertEquals("#4 start object count", 4, codes.size());
        instance.setStrictKoodiarvoFilter(KOODI_VALUE3);
        result = instance.filterByParams(codes, STR_CASE);
        assertEquals("#4 end object count", 1, result.size());
    }
}