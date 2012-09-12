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
package fi.vm.sade.tarjonta.ui.model.view;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.tarjonta.ui.model.KoulutusYhteyshenkiloDTO;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addon.customfield.CustomField;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;

/**
 *
 * @author mlyly
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
@Configurable(preConstruction = false)
public class EditKoulutusPerustiedotYhteystietoView extends CustomField {

    private VerticalLayout _layout;

    @PropertyId("nimi")
    private TextField _nimi;
    @PropertyId("titteli")
    private TextField _titteli;
    @PropertyId("email")
    private TextField _email;
    @PropertyId("puhelin")
    private TextField _puhelin;
    @PropertyId("kielet")
    private TwinColSelect _kielet;

    private KoulutusYhteyshenkiloDTO _model;
    private BeanItem<KoulutusYhteyshenkiloDTO> _bi;

    public EditKoulutusPerustiedotYhteystietoView(KoulutusYhteyshenkiloDTO dto) {
        _layout = new VerticalLayout();
        setCompositionRoot(_layout);
    }

    @Override
    public Class<?> getType() {
        return KoulutusYhteyshenkiloDTO.class;
    }

}
