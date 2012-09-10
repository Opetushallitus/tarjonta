package fi.vm.sade.tarjonta.ui.model.view;

import fi.vm.sade.tarjonta.ui.enums.TarjontaStyles;
import fi.vm.sade.vaadin.oph.layout.AbstractTreeTable;

public class CategoryTreeView extends AbstractTreeTable {

    public CategoryTreeView() {
        super();
        addStyleName(TarjontaStyles.CATEGORY_TREE.getStyleName());
        setColumnHeaderMode(COLUMN_HEADER_MODE_HIDDEN);
    }
}
