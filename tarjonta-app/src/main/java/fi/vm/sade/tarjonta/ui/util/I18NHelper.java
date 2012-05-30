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
package fi.vm.sade.tarjonta.ui.util;

import fi.vm.sade.generic.common.I18N;
import sun.tools.tree.OrExpression;

/**
 *
 * @author Jukka Raanamo
 */
public class I18NHelper {

    private StringBuilder keyBuilder = new StringBuilder();

    private int prefixLength;

    public I18NHelper(String prefix) {
        keyBuilder.append(prefix);
        prefixLength = keyBuilder.length();
    }

    public I18NHelper(Class from) {
        this(from.getSimpleName() + ".");
    }

    public I18NHelper(Object from) {
        this(from.getClass());
    }

    String makeKey(String key) {
        keyBuilder.setLength(prefixLength);
        keyBuilder.append(key);
        return keyBuilder.toString();
    }

    public String getMessage(String key) {
        return I18N.getMessage(makeKey(key));
    }

}

