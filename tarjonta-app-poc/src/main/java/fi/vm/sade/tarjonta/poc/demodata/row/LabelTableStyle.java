package fi.vm.sade.tarjonta.poc.demodata.row;

import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Label;
import fi.vm.sade.tarjonta.poc.demodata.ITableRowFormat;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 *
 * @author jani
 */
public class LabelTableStyle implements ITableRowFormat<Label> {

    @Override
    public Label format(String text) {
        return UiUtil.label((AbstractLayout) null, text);
    }
}
