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

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.ui.model.KoulutusLinkkiDTO;
import fi.vm.sade.tarjonta.ui.poc.helper.I18NHelper;
import fi.vm.sade.vaadin.oph.helper.UiBuilder;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;

/**
 * An editor for KoulutusLinkkiDTO objecst.
 *
 * Fires internal events on actions:
 * <ul>
 * <li>SaveEvent</li>
 * <li>CancelEvent</li>
 * <li>DeleteEvent</li>
 * </ul>
 *
 * Use "addListener" to catch these.
 *
 * @author mlyly
 * @see KoulutusLinkkiDTO
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
@Configurable(preConstruction = true)
public class EditKoulutusPerustiedotLinkkiView extends VerticalLayout {

    @PropertyId("linkkityyppi")
    private Select _sLinkkityyppi;
    @PropertyId("url")
    private TextField _tfUrl;
    @PropertyId("kielet")
    private KoodistoComponent _kcKielet;

    private I18NHelper i18n = new I18NHelper(this);

    @Value("${koodisto-uris.kieli:http://kieli}")
    private String _koodistoUriKieli;

    public EditKoulutusPerustiedotLinkkiView() {
        this.setSpacing(true);

        _sLinkkityyppi = UiBuilder.newComboBox(null, KoulutusLinkkiDTO.LINKKI_TYYPIT, null);
        _sLinkkityyppi.setWidth("100%");
        this.addComponent(_sLinkkityyppi);

        _tfUrl = UiBuilder.newTextField("", i18n.getMessage("Linkki.prompt"), false);
        _tfUrl.setRequired(true);
        _tfUrl.setRequiredError(i18n.getMessage("Linkki.tyhja"));
        _tfUrl.setWidth("100%");
        this.addComponent(_tfUrl);

        _kcKielet = UiBuilder.newKoodistoTwinColSelect(_koodistoUriKieli, null, null, this);


        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        this.addComponent(hl);

        UiBuilder.newButtonSmallSecodary(i18n.getMessage("Tallenna"), hl,  new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                fireEvent(new SaveEvent(EditKoulutusPerustiedotLinkkiView.this));
            }
        });

        UiBuilder.newButtonSmallSecodary(i18n.getMessage("Peruuta"), hl, new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                fireEvent(new CancelEvent(EditKoulutusPerustiedotLinkkiView.this));
            }
        });

       UiBuilder.newButtonSmallSecodary(i18n.getMessage("Poista"), hl, new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                fireEvent(new DeleteEvent(EditKoulutusPerustiedotLinkkiView.this));
            }
        });
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
