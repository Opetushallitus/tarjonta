package fi.vm.sade.tarjonta.service.impl.resources.v1;

import javax.ws.rs.core.MultivaluedMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Minimal clone of {@link com.sun.jersey.core.util.MultivaluedMapImpl} which is inaccessible due to being in special
 * package (com.sun.)
 */
public class MultivaluedHashMap extends HashMap<String, List<String>> implements MultivaluedMap<String, String> {

    public MultivaluedHashMap() { }

    @Override
    public final void putSingle(String key, String value) {
        List<String> l = getList(key);

        l.clear();
        if (value != null)
            l.add(value);
        else
            l.add("");
    }

    @Override
    public final void add(String key, String value) {
        List<String> l = getList(key);

        if (value != null)
            l.add(value);
        else
            l.add("");
    }

    @Override
    public String getFirst(String key) {
        List<String> values = get(key);
        if (values != null && values.size() > 0)
            return values.get(0);
        else
            return null;
    }

    private List<String> getList(String key) {
        List<String> l = get(key);
        if (l == null) {
            l = new LinkedList<>();
            put(key, l);
        }
        return l;
    }
}
