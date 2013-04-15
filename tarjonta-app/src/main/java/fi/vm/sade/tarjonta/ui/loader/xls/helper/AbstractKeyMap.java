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
package fi.vm.sade.tarjonta.ui.loader.xls.helper;

import com.google.common.base.Preconditions;
import java.util.HashMap;

/**
 *
 * @author Jani Wilén
 */
public abstract class AbstractKeyMap<ROW> extends HashMap<String, ROW> {

    private static final long serialVersionUID = -4077482965611646038L;

    protected void checkKey(String key, Object obj, String text, int rowIndex) {
        Preconditions.checkNotNull(key, text + " code value cannot be null! Row number : " + rowIndex + ", object : " + obj);
        Preconditions.checkArgument(!key.isEmpty(), text + " code value cannot be empty  Row number : " + rowIndex + ", object : " + obj);
        Preconditions.checkArgument(!key.contains("."), "An invalid character was found in relation key : '" + key + "'");
    }
}
