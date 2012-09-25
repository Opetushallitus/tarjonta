package fi.vm.sade.tarjonta.ui.view.common;

import fi.vm.sade.tarjonta.ui.enums.TarjontaStyles;
import fi.vm.sade.vaadin.ui.OphAbstractTreeTable;

public class CategoryTreeView extends OphAbstractTreeTable {

    public CategoryTreeView() {
        super();
        addStyleName(TarjontaStyles.CATEGORY_TREE.getStyleName());
        setColumnHeaderMode(COLUMN_HEADER_MODE_HIDDEN);
        
        
    }
    
    
}
