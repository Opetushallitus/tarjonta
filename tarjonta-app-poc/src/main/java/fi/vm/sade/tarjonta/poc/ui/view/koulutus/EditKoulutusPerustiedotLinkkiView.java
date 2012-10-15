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

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.poc.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.poc.ui.model.KoulutusLinkkiDTO;
import fi.vm.sade.tarjonta.poc.ui.view.common.AutoSizeVerticalLayout;
import fi.vm.sade.vaadin.util.UiUtil;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;

/**
 * An editor for KoulutusLinkkiDTO objecst.
 *
 * Fires internal events on actions: <ul> <li>SaveEvent</li>
 * <li>CancelEvent</li> <li>DeleteEvent</li> </ul>
 *
 * Use "addListener" to catch these.
 *
 * @author mlyly
 * @see KoulutusLinkkiDTO
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
@Configurable(preConstruction = true)
public class EditKoulutusPerustiedotLinkkiView extends AutoSizeVerticalLayout {

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
        super(Type.PCT_100, Type.AUTOSIZE);
        this.setSpacing(true);

        _sLinkkityyppi = UiUtil.comboBox(this, null, KoulutusLinkkiDTO.LINKKI_TYYPIT);
        _sLinkkityyppi.setWidth("100%");
        this.addComponent(_sLinkkityyppi);

        _tfUrl = UiUtil.textField(this, "", i18n.getMessage("Linkki.prompt"), false);
        _tfUrl.setRequired(true);
        _tfUrl.setRequiredError(i18n.getMessage("Linkki.tyhja"));
        _tfUrl.setWidth("100%");
        this.addComponent(_tfUrl);

        _kcKielet = UiBuilder.koodistoTwinColSelect(this, _koodistoUriKieli, null, null, null);


        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        this.addComponent(hl);

        UiUtil.buttonSmallSecodary(hl, i18n.getMessage("Tallenna"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                fireEvent(new SaveEvent(EditKoulutusPerustiedotLinkkiView.this));
            }
        });

        UiUtil.buttonSmallSecodary(hl, i18n.getMessage("Peruuta"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                fireEvent(new CancelEvent(EditKoulutusPerustiedotLinkkiView.this));
            }
        });

        UiUtil.buttonSmallSecodary(hl, i18n.getMessage("Poista"), new Button.ClickListener() {
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
