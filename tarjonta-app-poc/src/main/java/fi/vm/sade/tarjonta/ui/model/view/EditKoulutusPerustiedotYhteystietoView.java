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

import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.koodisto.widget.factory.WidgetFactory;
import fi.vm.sade.tarjonta.ui.poc.helper.I18NHelper;
import fi.vm.sade.vaadin.oph.helper.UiBuilder;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;

/**
 * Custom component to manage contact informations for Koulutus.
 *
 * Fires events:
 * <ul>
 * <li>SaveEvent</li>
 * <li>CancelEvent</li>
 * </ul>
 *
 * @author mlyly
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
@Configurable(preConstruction = true)
public class EditKoulutusPerustiedotYhteystietoView extends CustomComponent {

    private VerticalLayout _layout;

    @PropertyId("nimi")
    private TextField _tfNimi;
    @PropertyId("titteli")
    private TextField _tfTitteli;
    @PropertyId("email")
    private TextField _tfEmail;
    @PropertyId("puhelin")
    private TextField _tfPuhelin;
    @PropertyId("kielet")
    KoodistoComponent _kcKielet;

    private I18NHelper i18n = new I18NHelper(this);

    @Value("${koodisto-uris.kieli:http://kieli}")
    private String _koodistoUriKieli;

    public EditKoulutusPerustiedotYhteystietoView() {
        _layout = new VerticalLayout();
        _layout.setSpacing(true);

        _tfNimi = UiBuilder.newTextField("", i18n.getMessage("Nimi.prompt"), true);
        _tfNimi.setRequired(true);
        _tfNimi.setRequiredError(i18n.getMessage("Nimi.tyhja"));
        _layout.addComponent(_tfNimi);

        _tfTitteli = UiBuilder.newTextField("", i18n.getMessage("Titteli.prompt"), true);
        _layout.addComponent(_tfTitteli);

        _tfEmail = UiBuilder.newTextField("", i18n.getMessage("Email.prompt"), true);
        _tfEmail.setRequired(true);
        _tfEmail.setRequiredError(i18n.getMessage("Email.tyhja"));
        _layout.addComponent(_tfEmail);

        _tfPuhelin = UiBuilder.newTextField("", i18n.getMessage("Puhelin.prompt"), true);
        _tfPuhelin.setRequired(true);
        _tfPuhelin.setRequiredError(i18n.getMessage("Puhelin.tyhja"));
        _tfPuhelin.addValidator(new RegexpValidator("^(\\s+|\\d+)*", i18n.getMessage("Puhelin.muoto")));
        _layout.addComponent(_tfPuhelin);

        _layout.addComponent(UiBuilder.newLabel(i18n.getMessage("YhteyshenkiloKielissa"), (AbstractLayout) null));

        _kcKielet = UiBuilder.newKoodistoTwinColSelect(_koodistoUriKieli, null, null, _layout);

        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        _layout.addComponent(hl);

        Button btnSave = new Button(i18n.getMessage("Tallenna"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                fireEvent(new SaveEvent(EditKoulutusPerustiedotYhteystietoView.this));
            }
        });

        Button btnCancel = new Button(i18n.getMessage("Peruuta"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                fireEvent(new CancelEvent(EditKoulutusPerustiedotYhteystietoView.this));
            }
        });

        Button btnDelete = new Button(i18n.getMessage("Poista"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                fireEvent(new DeleteEvent(EditKoulutusPerustiedotYhteystietoView.this));
            }
        });

        hl.addComponent(btnDelete);
        hl.addComponent(btnCancel);
        hl.addComponent(btnSave);

        setCompositionRoot(_layout);
    }


    /**
     * Fired when save is pressed.
     */
    public class CancelEvent extends Component.Event {
        public CancelEvent(Component source) {
            super(source);
        }
    }

    /**
     * Fired when cancel is pressed.
     */
    public class SaveEvent extends Component.Event {
        public SaveEvent(Component source) {
            super(source);
        }
    }

    /**
     * Fired when delete is pressed.
     */
    public class DeleteEvent extends Component.Event {
        public DeleteEvent(Component source) {
            super(source);
        }
    }

}
