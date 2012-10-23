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
package fi.vm.sade.tarjonta.ui.view.koulutus;

import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.view.common.DataTableEvent;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;

/**
 * An editor to edit KoulutusYhteyshenkiloDTO.
 *
 * Fires events: <ul> <li>DataTableEvent.SaveEvent</li>
 * <li>DataTableEvent.CancelEvent</li> <li>DataTableEvent.DeleteEvent</li> </ul>
 *
 * Use "addListener" to catch these.
 *
 * @author mlyly
 * @see KoulutusYhteyshenkiloDTO
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
@Configurable(preConstruction = true)
public class EditKoulutusPerustiedotYhteystietoView extends VerticalLayout implements Component {

    private static final Logger LOG = LoggerFactory.getLogger(EditKoulutusPerustiedotYhteystietoView.class);
    @PropertyId("etunimet")
    private TextField _tfEtunimet;
    @PropertyId("sukunimi")
    private TextField _tfSukunimi;
    @PropertyId("titteli")
    private TextField _tfTitteli;
    @PropertyId("email")
    private TextField _tfEmail;
    @PropertyId("puhelin")
    private TextField _tfPuhelin;
    @PropertyId("kielet")
    KoodistoComponent _kcKielet;
    private I18NHelper i18n = new I18NHelper(this);

    public EditKoulutusPerustiedotYhteystietoView() {
        this.setSpacing(true);

        _tfEtunimet = UiUtil.textField(null, "", i18n.getMessage("Etunimet.prompt"), true);
        _tfEtunimet.setRequired(true);
        _tfEtunimet.setRequiredError(i18n.getMessage("Etunimet.tyhja"));
        this.addComponent(_tfEtunimet);

        _tfSukunimi = UiUtil.textField(null, "", i18n.getMessage("Sukunimi.prompt"), true);
        _tfSukunimi.setRequired(true);
        _tfSukunimi.setRequiredError(i18n.getMessage("Sukunimi.tyhja"));
        this.addComponent(_tfSukunimi);

        _tfTitteli = UiUtil.textField(null, "", i18n.getMessage("Titteli.prompt"), true);
        this.addComponent(_tfTitteli);

        _tfEmail = UiUtil.textField(null, "", i18n.getMessage("Email.prompt"), true);
        _tfEmail.setRequired(true);
        _tfEmail.setRequiredError(i18n.getMessage("Email.tyhja"));
        this.addComponent(_tfEmail);

        _tfPuhelin = UiUtil.textField(null, "", i18n.getMessage("Puhelin.prompt"), true);
        _tfPuhelin.setRequired(true);
        _tfPuhelin.setRequiredError(i18n.getMessage("Puhelin.tyhja"));
        _tfPuhelin.addValidator(new RegexpValidator("^(\\s+|\\d+)*", i18n.getMessage("Puhelin.muoto")));
        this.addComponent(_tfPuhelin);

        this.addComponent(UiUtil.label(null, i18n.getMessage("YhteyshenkiloKielissa")));

        _kcKielet = UiBuilder.koodistoTwinColSelectUri(this, KoodistoURIHelper.KOODISTO_KIELI_URI);
        
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        this.addComponent(hl);

        UiUtil.buttonSmallSecodary(hl, i18n.getMessage("Tallenna"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                LOG.info("fire : SaveEvent");

                fireEvent(new DataTableEvent.SaveEvent(EditKoulutusPerustiedotYhteystietoView.this));
            }
        });

        UiUtil.buttonSmallSecodary(hl, i18n.getMessage("Peruuta"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                fireEvent(new DataTableEvent.CancelEvent(EditKoulutusPerustiedotYhteystietoView.this));
            }
        });

//        UiUtil.buttonSmallSecodary(hl, i18n.getMessage("Poista"), new Button.ClickListener() {
//            @Override
//            public void buttonClick(Button.ClickEvent event) {
//                fireEvent(new DataTableEvent.DeleteEvent(EditKoulutusPerustiedotYhteystietoView.this));
//            }
//        });
    }
}
