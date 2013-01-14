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

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.KoulutusLinkkiViewModel;
import fi.vm.sade.tarjonta.ui.view.common.DataTableEvent;
import fi.vm.sade.vaadin.util.UiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;

/**
 * An editor for KoulutusLinkkiDTO objects.
 *
 * Fires internal events on actions:
 * <ul>
 *   <li>DataTableEvent.SaveEvent when save is pressed</li>
 *   <li>DataTableEvent.CancelEvent when cancel is pressed</li>
 * </ul>
 *
 * Use "addListener" to catch these events where ever this component is used.
 *
 * @author mlyly
 * @see KoulutusLinkkiDTO
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
@Configurable(preConstruction = true)
public class EditKoulutusPerustiedotLinkkiView extends VerticalLayout implements Component  {
    private static final long serialVersionUID = -7283666973111838791L;

    @PropertyId("linkkityyppi")
    private Select _sLinkkityyppi;
    @PropertyId("url")
    private TextField _tfUrl;
    @PropertyId("kieli")
    private KoodistoComponent kcKieli;
    @Autowired(required = true)
    private transient UiBuilder uiBuilder;

    private transient I18NHelper i18n;

    public EditKoulutusPerustiedotLinkkiView() {
        this.setSpacing(true);

        _sLinkkityyppi = UiUtil.comboBox(this, null, KoulutusLinkkiViewModel.LINKKI_TYYPIT);
        _sLinkkityyppi.setWidth("100%");
        this.addComponent(_sLinkkityyppi);

        _tfUrl = UiUtil.textField(this, "", T("Linkki.prompt"), false);
        _tfUrl.setRequired(true);
        _tfUrl.setRequiredError(T("Linkki.tyhja"));
        _tfUrl.setWidth("100%");
        this.addComponent(_tfUrl);

        kcKieli = uiBuilder.koodistoComboBox(this, KoodistoURIHelper.KOODISTO_KIELI_URI);

        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        this.addComponent(hl);

        UiUtil.buttonSmallSecodary(hl, T("Tallenna"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;
            @Override
            public void buttonClick(Button.ClickEvent event) {
                fireEvent(new DataTableEvent.SaveEvent(EditKoulutusPerustiedotLinkkiView.this));
            }
        });

        UiUtil.buttonSmallSecodary(hl, T("Peruuta"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;
            @Override
            public void buttonClick(Button.ClickEvent event) {
                fireEvent(new DataTableEvent.CancelEvent(EditKoulutusPerustiedotLinkkiView.this));
            }
        });
    }

    /**
     * Translator helper.
     *
     * @param key
     * @return
     */
    private String T(String key) {
        if (i18n == null) {
            i18n = new I18NHelper(this);
        }
        return i18n.getMessage(key);
    }
}
