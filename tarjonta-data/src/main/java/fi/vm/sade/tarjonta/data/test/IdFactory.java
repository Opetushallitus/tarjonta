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
package fi.vm.sade.tarjonta.data.test;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jani Wilén
 */
public class IdFactory {

    private static boolean initialized = false;
    private static IdFactory oid;
    private Map<String, Long> map = new HashMap<String, Long>();

    public IdFactory() {
    }

    protected long getNextId(final String type) {
        Long index = 1l;
        if (map.containsKey(type)) {
            index = map.get(type) + 1;
        }
        map.put(type, index);
        return index.longValue();
    }

    public synchronized static long geNextIdByType(final String type) {
        Preconditions.checkNotNull(type, "ID type cannot be null.");

        if (!initialized) {
            initialized = true;
            oid = new IdFactory();
        }

        return oid.getNextId(type);
    }
}
