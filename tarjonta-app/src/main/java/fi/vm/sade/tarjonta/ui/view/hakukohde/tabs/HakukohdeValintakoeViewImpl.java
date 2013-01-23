package fi.vm.sade.tarjonta.ui.view.hakukohde.tabs;/*
 *
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

import com.vaadin.ui.*;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.vaadin.util.UiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;

/**
 * Created by: Tuomas Katva
 * Date: 23.1.2013
 */

@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
@Configurable
public class HakukohdeValintakoeViewImpl extends CustomComponent {

    private ErrorMessage errorView;

    @Autowired
    private TarjontaUIHelper tarjontaUIHelper;

    @Autowired
    private TarjontaPresenter presenter;

    @PropertyId("valintakoeTyyppi")
    private KoodistoComponent valintakoeTyyppi;

    private ValintakoeKuvausTabSheet valintaKoeKuvaus;

    @PropertyId("currentAika.osoiteRivi")
    private TextField osoiteRiviTxt;

    @PropertyId("currentAika.postinumero")
    private KoodistoComponent postinumeroCombo;

    @PropertyId("currentAika.postitoimiPaikka")
    private TextField postitoimiPaikka;

    @PropertyId("currentAika.alkamisAika")
    private DateField alkupvm;

    @PropertyId("currentAika.paattymisAika")
    private DateField loppuPvm;

    @PropertyId("currentAika.valintakoeAikaTiedot")
    private TextField lisatietoja;

    private Table valintakoeAikasTable;

    private HorizontalLayout buildErrorLayout() {
        HorizontalLayout topErrorArea = UiUtil.horizontalLayout();
        HorizontalLayout padding = UiUtil.horizontalLayout();
        padding.setWidth(30, UNITS_PERCENTAGE);
        errorView = new ErrorMessage();
        errorView.setSizeUndefined();

        topErrorArea.addComponent(padding);
        topErrorArea.addComponent(errorView);

        return topErrorArea;
    }
}
