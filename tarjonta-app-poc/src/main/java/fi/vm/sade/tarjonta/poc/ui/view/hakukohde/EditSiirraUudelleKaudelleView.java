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
package fi.vm.sade.tarjonta.poc.ui.view.hakukohde;

import fi.vm.sade.vaadin.util.UiUtil;
import com.vaadin.ui.DateField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.CloseEvent;
import fi.vm.sade.tarjonta.poc.demodata.DataSource;
import fi.vm.sade.vaadin.ui.OphAbstractDialogWindow;
import fi.vm.sade.vaadin.util.UiBaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jani
 */
public class EditSiirraUudelleKaudelleView extends OphAbstractDialogWindow {

    private static final Logger LOG = LoggerFactory.getLogger(EditSiirraUudelleKaudelleView.class);
    private static final String TITLE_FORMAT = "Olet muokkaamassa {0} {1}";

    public EditSiirraUudelleKaudelleView(String label) {
        super(
                label,
                UiBaseUtil.format(TITLE_FORMAT, "28", "koulutusta"),
                DataSource.LOREM_IPSUM_SHORT);
        setWidth("600px");
        setHeight("500px");
    }

    @Override
    public void buildLayout(VerticalLayout layout) {
        UiUtil.comboBox(layout, "Aseta kaikille sama koulutusten alkamiskausi*", new String[]{"Syksy 2012", "Talvi 2013"});
        DateField newDate = UiUtil.dateField();
        newDate.setCaption("Aseta kaikille sama koulutusten alkamispäivä");
        layout.addComponent(newDate);

        UiUtil.checkbox(layout, "Siirrä myös liittyvät hakukohteet (35)");
    }

    /**
     * In case the window is closed otherwise.
     */
    @Override
    public void windowClose(CloseEvent e) {
        removeDialogButtons();
    }
}
