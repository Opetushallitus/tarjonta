package fi.vm.sade.tarjonta.poc.demodata;

import com.vaadin.ui.HorizontalLayout;

/**
 *
 * @author jani
 */
public interface ITableRowFormat<E> {
    public E format(final String text);
 
}