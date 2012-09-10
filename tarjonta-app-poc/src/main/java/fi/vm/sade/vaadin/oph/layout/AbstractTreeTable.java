package fi.vm.sade.vaadin.oph.layout;

import fi.vm.sade.tarjonta.ui.model.view.*;

import com.vaadin.ui.TreeTable;

import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.oph.helper.ComponentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTreeTable extends TreeTable {

    private static final Logger LOG = LoggerFactory.getLogger(CategoryTreeView.class);

    public AbstractTreeTable() {
        super();

        init(null, null);
    }

    public AbstractTreeTable(String width, String height) {
        super();

        init(width, height);
    }

    private void init(String width, String height) {
        addStyleName(Oph.TABLE_BORDERLESS); //TODO: TEEMATKAA!
    
        // OTHER
        setSelectable(false);
        setImmediate(false);

        if (width != null || height != null) {
            ComponentUtil.handleWidth(this, width);
            ComponentUtil.handleHeight(this, height);
        } else {
            setSizeFull();
        }
    }
}