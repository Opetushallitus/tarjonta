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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 */
public class CollectionUtils {
    
    /**
     * Returns a collection of items from collection a that do not exist in collection b.
     * 
     * E.g:
     * <pre>
     * Collection a = Arrays.asList("a", "b", "c");
     * Collection b = Arrays.asList("a", "c");
     * </pre>
     * 
     * then 
     * 
     * <pre>
     * CollectionUtils.notIn(a, b);
     * </pre>
     * 
     * would return collection with one item, "b"
     * 
     * @param a
     * @param b
     * @return
     */
    public static Collection notIn(Collection a, Collection b) {
        
        final Collection result = new ArrayList();
        for (Object o : a) {
            if (!b.contains(o)) {
                result.add(o);
            }
        }
        return result;
        
    }
    
    
    /**
     * Returns last element from the collection or null if collection is empty.
     * 
     * @param collection
     * @return
     */
    public static Object last(Collection collection) {
        Object last = null;
        Iterator i = collection.iterator();
        while (i.hasNext()) {
            last = i.next();
        }
        return last;
    }
    
}

