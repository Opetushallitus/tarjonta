package fi.vm.sade.tarjonta.service.copy;

import java.lang.reflect.InvocationTargetException;

import com.google.common.base.Throwables;
import org.apache.commons.beanutils.BeanUtilsBean;

/**
 * See: http://stackoverflow.com/questions/1301697/helper-in-order-to-copy-non-null-properties-from-object-to-another-java
 */
public class NullAwareBeanUtilsBean extends BeanUtilsBean{

    @Override
    public void copyProperty(Object dest, String name, Object value)
            throws IllegalAccessException, InvocationTargetException {
        if (value == null) {
            return;
        }
        super.copyProperty(dest, name, value);
    }

}