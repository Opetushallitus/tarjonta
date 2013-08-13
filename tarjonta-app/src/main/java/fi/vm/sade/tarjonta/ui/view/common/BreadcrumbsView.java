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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickEvent;

import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.service.UserContext;
import fi.vm.sade.vaadin.constants.LabelStyleEnum;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 * Simple "breadcrumbs" view.
 *
 * TODO push, pop, clear, and click functionality
 *
 * @author mlyly
 */
@Configurable
public class BreadcrumbsView extends AbstractVerticalLayout {

    private static final long serialVersionUID = 2254224099223350768L;
    private Label organisaatioNimi;
    private Button poistaValintaB;
    @Autowired
    UserContext userContext;
    TarjontaPresenter presenter;

    public BreadcrumbsView(TarjontaPresenter presenter) {
        super();
        this.setMargin(false, false, false, true);
        this.setSizeUndefined();
        this.presenter = presenter;
    }

    @Override
    protected void buildLayout() {
        HorizontalLayout hl = UiUtil.horizontalLayout();
        hl.setSpacing(true);
        organisaatioNimi = UiUtil.label(hl, "OPH", LabelStyleEnum.H2);
        organisaatioNimi.setSizeUndefined();
        hl.setExpandRatio(organisaatioNimi, 0.99f);
        
        organisaatioNimi.setContentMode(Label.CONTENT_XHTML);
        poistaValintaB = UiUtil.buttonLink(hl, T("poistaOrganisaatioValinta"));
        poistaValintaB.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(ClickEvent event) {
                ///XXX how to restore use restriction??
                userContext.setUseRestriction(false);
                presenter.unSelectOrganisaatio();
            }
        });
        hl.setComponentAlignment(organisaatioNimi, Alignment.MIDDLE_LEFT);
        hl.setComponentAlignment(poistaValintaB, Alignment.MIDDLE_RIGHT);
        poistaValintaB.setVisible(isNavigationOrganisationSelected());
        addComponent(hl);

        hl.setSizeFull();
        hl.setExpandRatio(poistaValintaB, 1f);
    }

    /**
     * @param organisaatioNimi the organisaatio to set
     */
    public void setOrganisaatio(String organisaatioNimi) {
    	// OVT-4891 span-haxorointi koska vaadin ei osaa rivittää teksti(kentti)ä jonka leveyttä ei tiedetä etukäteen.
        this.organisaatioNimi.setValue("<span style=\"white-space: normal;\">"+organisaatioNimi+"</span>");
        poistaValintaB.setVisible(isNavigationOrganisationSelected());
    }

    private boolean isNavigationOrganisationSelected() {
        return presenter.getNavigationOrganisation().isOrganisationSelected();
    }
}
