/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.tarjonta.poc.ui.view.common;

import fi.vm.sade.tarjonta.poc.ui.enums.TarjontaStyles;
import fi.vm.sade.vaadin.ui.OphAbstractTreeTable;

public class CategoryTreeView extends OphAbstractTreeTable {

    public CategoryTreeView() {
        super();
        addStyleName(TarjontaStyles.CATEGORY_TREE.getStyleName());
        setColumnHeaderMode(COLUMN_HEADER_MODE_HIDDEN);
        setSizeFull();
    }
}
