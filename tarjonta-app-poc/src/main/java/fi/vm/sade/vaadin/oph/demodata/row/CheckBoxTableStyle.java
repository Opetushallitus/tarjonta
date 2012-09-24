
package fi.vm.sade.vaadin.oph.demodata.row;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import fi.vm.sade.vaadin.oph.demodata.ITableRowFormat;
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
