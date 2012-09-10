package fi.vm.sade.tarjonta.ui.model.view;

import fi.vm.sade.vaadin.oph.helper.UiBuilder;
import fi.vm.sade.vaadin.oph.layout.AbstractTreeTable;

public class CreateKoulutusTreeView extends AbstractTreeTable {

    public CreateKoulutusTreeView() {
        super(null, UiBuilder.DEFAULT_REALTIVE_SIZE);
        setColumnHeaderMode(COLUMN_HEADER_MODE_HIDDEN);
    }
}
