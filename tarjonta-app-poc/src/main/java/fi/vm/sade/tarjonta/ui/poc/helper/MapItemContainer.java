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
package fi.vm.sade.tarjonta.ui.poc.helper;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractInMemoryContainer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author mlyly
 */
public class MapItemContainer extends AbstractInMemoryContainer<Object, String, MapItem> {

    private Map<String, Class> _propertyTypes = new HashMap<String, Class>();

    @Override
    protected MapItem getUnfilteredItem(Object itemId) {
        return super.getItem(itemId);
    }

    @Override
    public Collection<?> getContainerPropertyIds() {
        Set set = new HashSet();

        for (Object itemId : super.getAllItemIds()) {
            MapItem mi = getItem(itemId);
            for (Object pId : mi.getItemPropertyIds()) {
                set.add(pId);

                _propertyTypes.put((String) pId, mi.getItemProperty(pId).getType());
            }
        }

        return set;
    }

    @Override
    public Property getContainerProperty(Object itemId, Object propertyId) {
        MapItem item = getItem(itemId);
        if (item != null) {
            return item.getItemProperty(propertyId);
        } else {
            return null;
        }
    }

    @Override
    public Class<?> getType(Object propertyId) {
        if (_propertyTypes.isEmpty()) {
            getContainerPropertyIds();
        }

        return _propertyTypes.get(propertyId);
    }

}
