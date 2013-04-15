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
package fi.vm.sade.tarjonta.data.util;

import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
import java.util.Date;

/**
 *
 * @author Jani Wilén
 */
public class KoodistoUtil {

    private static final String KOODI_VERSION = "#1";
    private static final String KOODI_PREFIX = "_";

    public static String toKoodiUri(final String koodisto, final String value) {
        //URI data example : "koulutusohjelma_1603#1"
        return new StringBuilder(koodisto).append(KOODI_PREFIX).append(value).append(KOODI_VERSION).toString();
    }

    public static KoodistoKoodiTyyppi toKoodistoTyyppi(String uri, String arvo) {
        KoodistoKoodiTyyppi koodi = new KoodistoKoodiTyyppi();
        koodi.setUri(toKoodiUri(uri, arvo));
        koodi.setArvo(arvo);

        return koodi;
    }

    
}
