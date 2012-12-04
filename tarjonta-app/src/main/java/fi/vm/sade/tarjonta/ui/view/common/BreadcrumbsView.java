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
package fi.vm.sade.tarjonta.ui.view.common;

import com.vaadin.ui.Label;
import fi.vm.sade.vaadin.constants.LabelStyleEnum;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 * Simple "breadcrumbs" view.
 *
 * TODO push, pop, clear, and click functionality
 *
 * @author mlyly
 */
public class BreadcrumbsView extends AbstractVerticalLayout {

    private static final long serialVersionUID = 2254224099223350768L;
    private Label organisaatio;

    public BreadcrumbsView() {
        super();
        this.setMargin(false, false, false, true);
        this.setSizeUndefined();
    }

    @Override
    protected void buildLayout() {
        organisaatio = UiUtil.label(this, "-", LabelStyleEnum.H2);
        organisaatio.setSizeUndefined();
    }

    /**
     * @param organisaatio the organisaatio to set
     */
    public void setOrganisaatio(String organisaatio) {
        this.organisaatio.setValue(organisaatio);
    }
}
