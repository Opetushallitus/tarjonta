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
package fi.vm.sade.tarjonta.ui.koulutusmoduuli;

import com.vaadin.ui.*;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.ui.util.I18NHelper;

/**
 *
 *
 * @author Jukka Raanamo
 */
public class KoulutusmoduuliEditForm extends CustomComponent {

    private GridLayout mainLayout;

    private TextField organisaatioField;

    private TextField koulutusField;
    
    private static I18NHelper i18n = new I18NHelper(KoulutusmoduuliEditForm.class);

    public KoulutusmoduuliEditForm() {

        super();

        mainLayout = new GridLayout(2, 9);
        mainLayout.setSpacing(true);

        initFields();
        setCompositionRoot(mainLayout);

    }

    private void initFields() {

        organisaatioField = new TextField();
        koulutusField = new TextField();

        addField(mainLayout, new Label(i18n.getMessage("organisaatioLabel")), organisaatioField);
        addField(mainLayout, new Label(i18n.getMessage("koulutusLabel")), koulutusField);

    }

    private void addField(GridLayout grid, Label label, Component content) {

        grid.addComponent(label);
        grid.addComponent(content);
        grid.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);

    }

}

