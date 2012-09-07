/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.vm.sade.tarjonta.ui.poc.helper;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.PropertysetItem;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Just like BeanItem but for Maps.
 *
 * For quick prototyping.
 *
 * @author mlyly
 * @see BeanItem
 */
public class MapItem extends PropertysetItem {

    private static final Logger LOG = LoggerFactory.getLogger(MapItem.class);
    Map _map;

    public MapItem(Map map) {
        super();
        _map = map;

        for (Object object : _map.keySet()) {
            LOG.debug("  adding property: {}", object);
            addItemProperty(object, new MapProperty(_map, object));
        }
    }

    @Override
    public Property getItemProperty(Object id) {
        LOG.info("getItemProperty({})", id);
        
        if (!_map.containsKey(id)) {
            LOG.debug("  add nonexisting property: {}", id);
            super.addItemProperty(id, new MapProperty(_map, id));
        }
        return super.getItemProperty(id);
    }
}
