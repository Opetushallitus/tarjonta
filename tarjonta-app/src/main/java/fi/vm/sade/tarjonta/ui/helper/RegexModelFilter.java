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

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusKoodistoModel;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Jani Wilén
 */
public class RegexModelFilter<MODEL extends KoulutusKoodistoModel> {

    public static final Pattern PATTERN_NUMBER = Pattern.compile("\\d+");
    public static final Pattern PATTERN_ALPHA = Pattern.compile("\\p{Alpha}+");
    private static final String REGEX_ALL = ".*";
    private String preFilterKoodiarvo;

    public RegexModelFilter() {
    }

    /**
     * An additional strict koodiarvo filter. Allows only exact matches.
     *
     * @param koodiarvo
     */
    public void setStrictKoodiarvoFilter(final String koodiarvo) {
        Preconditions.checkNotNull(koodiarvo, "Koodisto service koodiarvo cannot be null.");
        this.preFilterKoodiarvo = koodiarvo;
    }

    /**
     * Clear all additional filters.
     */
    public void clearAllFilters() {
        this.preFilterKoodiarvo = null;
    }

    public List<MODEL> filterByParams(final List<MODEL> models, final String searchWordParams) {
        Preconditions.checkNotNull(models, "List of models cannot be null.");

        Set<String> numbers = Sets.<String>newHashSet();
        Set<String> alphas = Sets.<String>newHashSet();
        if (!isKoodiarvoFilter() && !isSearchWords(searchWordParams)) {
            //No filter paramters, output all items.
            return models;
        } else if (isSearchWords(searchWordParams)) {
            //separate search word string to numbers and alphabets 
            numbers = buildRegexString(searchWordParams, PATTERN_NUMBER);
            alphas = buildRegexString(searchWordParams, PATTERN_ALPHA);
        }

        return matcher(models, numbers, alphas);
    }

    private List<MODEL> matcher(List<MODEL> list, Set<String> numbers, Set<String> alphas) {
        List<MODEL> matches = Lists.<MODEL>newArrayList();
        for (MODEL s : list) {
            if (isKoodiarvoFilter() && (s.getKoodi() == null || !s.getKoodi().equals(preFilterKoodiarvo))) {
                //skip all unneeded koodis
                continue;
            }

            if (mustMatchToAllPatterns(s.getKoodi(), numbers) && mustMatchToAllPatterns(s.getNimi(), alphas)) {
                //add a matched koodi
                matches.add(s);
            }
        }

        return matches;
    }

    /**
     * Given string must match to all regex patterns. If a set is empty, then
     * matching method will return true.
     *
     * @param str
     * @param regexs
     * @return
     */
    private static boolean mustMatchToAllPatterns(final String str, final Set<String> regexs) {
        if (regexs.isEmpty()) {
            //no filters
            return true;
        }

        for (String regex : regexs) {
            if (!match(str, regex)) {
                return false;
            }
        }
        return true;
    }

    private static boolean match(final String str, final String regex) {
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        return p.matcher(str).matches();
    }

    public static Set<String> buildRegexString(final String searchWord, final Pattern p) {
        Matcher m = p.matcher(searchWord);
        Set<String> output = Sets.<String>newHashSet();
        while (m.find()) {
            output.add(new StringBuilder()
                    .append(REGEX_ALL).
                    append(m.group()).
                    append(REGEX_ALL).toString());
        }

        return output;
    }

    private boolean isKoodiarvoFilter() {
        return preFilterKoodiarvo != null;
    }

    private boolean isSearchWords(final String searchWordParams) {
        return searchWordParams != null || !searchWordParams.isEmpty();
    }
}
