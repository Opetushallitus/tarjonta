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
package fi.vm.sade.tarjonta.poc.ui.view.koulutus;

import fi.vm.sade.vaadin.util.UiUtil;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.CloseEvent;
import fi.vm.sade.vaadin.constants.UiConstant;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.tarjonta.poc.demodata.DataSource;
import fi.vm.sade.tarjonta.poc.demodata.row.CheckBoxTableStyle;
import fi.vm.sade.vaadin.ui.OphAbstractDialogWindow;
import fi.vm.sade.vaadin.util.UiBaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jani
 */
public class CreateKoulutusView extends OphAbstractDialogWindow {

    private static final Logger LOG = LoggerFactory.getLogger(CreateKoulutusView.class);
    private static final String TEKSTI = "Koulutusta ei ole vielä liitetty mihinkään organisaatioon.";
    private static final String TITLE_FORMAT = "Olet luomassa {0} {1}";
    private CreateKoulutusTreeView createKoulutusTreeView;

    public CreateKoulutusView(String label) {
        super(
                label,
                UiBaseUtil.format(TITLE_FORMAT, "tutkintoon johtavaa", "koulutusta"),
                DataSource.LOREM_IPSUM_SHORT);

        setWidth("700px");
        setHeight("500px");
    }

    @Override
    public void buildLayout(VerticalLayout layout) {

        /* YOUR LAYOUT BETWEEN TOPIC AND BUTTONS */

        HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        splitPanel.setSplitPosition(50); // percent
        VerticalLayout vLeft = UiUtil.verticalLayout();
        VerticalLayout vRight = UiUtil.verticalLayout(false, UiMarginEnum.LEFT);

        splitPanel.addComponent(vLeft);
        splitPanel.addComponent(vRight);

        createKoulutusTreeView = new CreateKoulutusTreeView();
        createKoulutusTreeView.setSizeFull();
        createKoulutusTreeView.setContainerDataSource(DataSource.treeTableData(new CheckBoxTableStyle(), DataSource.ORGANISAATIOT));
        vLeft.addComponent(createKoulutusTreeView);

        HorizontalLayout middleLayout = UiUtil.horizontalLayout();
        Panel newTextPanel = UiUtil.textPanel(TEKSTI, null, UiConstant.DEFAULT_RELATIVE_SIZE, middleLayout);
        newTextPanel.setHeight(UiConstant.DEFAULT_RELATIVE_SIZE);
        vRight.addComponent(newTextPanel);

       // layout.addComponent(UiBuilder.newComboBox("Tyyppi", new String[]{"Tutkintoon johtava koulutus", "Opintokokonaisuus", "Opinto"}, layout));
        layout.addComponent(splitPanel);
        layout.setExpandRatio(splitPanel, 1f);
    }

    /**
     * In case the window is closed otherwise.
     */
    @Override
    public void windowClose(CloseEvent e) {
        //DEBUGSAWAY:LOG.debug("In windowClose - close event");
        removeDialogButtons();
        createKoulutusTreeView = null;
    }
}
