package fi.vm.sade.vaadin.oph.demodata.row;

import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Label;
import fi.vm.sade.vaadin.oph.demodata.ITableRowFormat;
import fi.vm.sade.vaadin.oph.helper.UiBuilder;

/**
 *
 * @author jani
 */
public class TextTableStyle implements ITableRowFormat<Label> {

    @Override
    public Label format(String text) {
        return UiBuilder.newLabel(text, (AbstractLayout) null);
    }
}
