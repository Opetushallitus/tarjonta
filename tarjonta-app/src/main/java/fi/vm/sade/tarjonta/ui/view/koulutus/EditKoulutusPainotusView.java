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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class EditKoulutusPainotusView extends VerticalLayout implements Component {

    private static final Logger LOG = LoggerFactory.getLogger(EditKoulutusPainotusView.class);
    @PropertyId("nimi")
    private TextField tfNimi;
    @PropertyId("kielikoodi")
    private KoodistoComponent kcKieli;
    private I18NHelper i18n = new I18NHelper(this);
    @Autowired(required = true)
    private UiBuilder uiBuilder;

    public EditKoulutusPainotusView() {
        this.setSpacing(true);
        this.addComponent(UiUtil.label(null, i18n.getMessage("Painotus")));

        tfNimi = UiUtil.textField(this, "", i18n.getMessage("painotus.prompt"), true);
        tfNimi.setWidth(400, UNITS_PIXELS);
        tfNimi.setRequired(true);
        tfNimi.setRequiredError(i18n.getMessage("painotus.tyhja"));

        UiUtil.label(this, i18n.getMessage("Kielelle"));
        kcKieli = uiBuilder.koodistoComboBox(this, KoodistoURIHelper.KOODISTO_KIELI_URI, true);
        kcKieli.getField().setRequired(true);
        kcKieli.getField().setNullSelectionAllowed(false);
        kcKieli.getField().setRequiredError(i18n.getMessage("kieli.tyhja"));
        
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        this.addComponent(hl);

        UiUtil.buttonSmallSecodary(hl, i18n.getMessage("Tallenna"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                LOG.debug("fire : SaveEvent");
                fireEvent(new DataTableEvent.SaveEvent(EditKoulutusPainotusView.this));
            }
        });

        UiUtil.buttonSmallSecodary(hl, i18n.getMessage("Peruuta"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                fireEvent(new DataTableEvent.CancelEvent(EditKoulutusPainotusView.this));
            }
        });


    }
}
