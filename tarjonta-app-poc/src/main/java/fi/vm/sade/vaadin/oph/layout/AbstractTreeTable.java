package fi.vm.sade.vaadin.oph.layout;

import com.vaadin.data.util.HierarchicalContainer;
import fi.vm.sade.tarjonta.ui.model.view.*;
import fi.vm.sade.vaadin.oph.helper.UiBuilder;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import com.vaadin.ui.TreeTable;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import fi.vm.sade.tarjonta.ui.enums.TarjontaStyles;
import fi.vm.sade.tarjonta.ui.poc.RowMenuBar;
import fi.vm.sade.vaadin.Oph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTreeTable extends TreeTable {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTreeTable.class);
    
    public AbstractTreeTable() {
        super();

        init();
        addStyleName(TarjontaStyles.CATEGORY_TREE.getStyleName());
    }

    private void init() {
        this.addStyleName(Oph.TABLE_BORDERLESS); //TODO: TEEMATKAA!
        this.setColumnHeaderMode(COLUMN_HEADER_MODE_HIDDEN);

        // OTHER
        this.setSelectable(false);
        this.setImmediate(false);
        this.setSizeFull();
    }
}