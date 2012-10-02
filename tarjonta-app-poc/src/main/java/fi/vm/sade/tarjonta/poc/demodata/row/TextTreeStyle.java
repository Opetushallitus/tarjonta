package fi.vm.sade.tarjonta.poc.demodata.row;

import fi.vm.sade.tarjonta.poc.demodata.ITableRowFormat;

/**
 *
 * @author jani
 */
public class TextTreeStyle implements ITableRowFormat<String> {

    @Override
    public String format(String text) {
        return text;
    }
}
