
package fi.vm.sade.tarjonta.poc.demodata.row;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import fi.vm.sade.tarjonta.poc.demodata.ITableRowFormat;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 *
 * @author jani
 */
public class CheckBoxTableStyle implements ITableRowFormat<CheckBox>{

    @Override
    public CheckBox format(String text) {
        return UiUtil.checkbox(null, text);
    }
}
