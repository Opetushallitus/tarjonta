/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.vm.sade.tarjonta.poc.ui.helper;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.MethodProperty;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Property that accesses given maps data by given key.
 * 
 * These properties will be stored to the "MapItem" (BeanItem-like) datasource.
 *
 * @author mlyly
 * @see BeanItem
 * @see MethodProperty
 */
public class MapProperty implements Property {

    private static final Logger LOG = LoggerFactory.getLogger(MapProperty.class);

    private Map _map;
    private Object _key;
    private boolean _readonly = false;

    public MapProperty(Map map, Object key) {
        _map = map;
        _key = key;
    }
    
    @Override
    public Object getValue() {
        //DEBUGSAWAY:LOG.debug("getValue(): key={}, value={}", _key, _map.get(_key));
        return _map.get(_key);
    }

    @Override
    public void setValue(Object newValue) throws Property.ReadOnlyException, Property.ConversionException {
        //DEBUGSAWAY:LOG.debug("setValue(): key={}, newValue={}", _key, newValue);
        _map.put(_key, newValue);
    }

    @Override
    public Class<?> getType() {
        Class result;
        
        Object value = _map.get(_key);
        if (value != null) {
            result = value.getClass();
        } else {
            result = String.class;
        }

        //DEBUGSAWAY:LOG.debug("getType() --> {}", result);
        return result;
    }

    @Override
    public boolean isReadOnly() {
        return _readonly;
    }

    @Override
    public void setReadOnly(boolean newStatus) {
        _readonly = newStatus;
    }
    
    @Override
    public String toString() {
        return getValue() == null ? null : "" + getValue();
    }
}
