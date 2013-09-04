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
package fi.vm.sade.tarjonta.ui.view.koulutus.kk;

import com.google.common.base.Preconditions;
import com.vaadin.data.Property;
import static com.vaadin.terminal.Sizeable.UNITS_PERCENTAGE;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.ui.component.CaptionFormatter;
import fi.vm.sade.generic.ui.validation.JSR303FieldValidator;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.TutkintoohjelmaModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaKorkeakouluPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalLayout;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.util.UiUtil;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;

/**
 *
 * @author Jani Wilén
 */
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
public class LisaaNimiFormView extends AbstractVerticalLayout {

    private static final long serialVersionUID = 6245718709351863230L;
    private static transient final Logger LOG = LoggerFactory.getLogger(LisaaNimiFormView.class);
    private TarjontaKorkeakouluPresenter presenter;
    private LisaaNimiDialog dialog;
    private Button btNext, btCancel;
    @NotNull(message = "{validation.Koulutus.tutkintoohjelma.notNull}")
    @PropertyId("nimi")
    private TextField tftutkintoohjelmanNimi;
    @PropertyId("kielikoodi")
    private KoodistoComponent kieli;
    private boolean mode;

    public LisaaNimiFormView(TarjontaKorkeakouluPresenter presenter, TarjontaUIHelper uiHelper, UiBuilder uiBuilder, LisaaNimiDialog dialog, boolean mode) {
        Preconditions.checkNotNull(presenter, "TarjontaKorkeakouluPresenter object cannot be null.");
        Preconditions.checkNotNull(uiHelper, "TarjontaUIHelper object cannot be null.");
        Preconditions.checkNotNull(dialog, "LisaaNimiDialog object cannot be null.");
        this.presenter = presenter;
        this.dialog = dialog;
        this.mode = mode;
        setSizeFull();

        addInfoText();
        addKoodistoKieli(uiBuilder);
        addTextField("tutkinto-ohjelma");
        addNavigationButtonLayout();
        JSR303FieldValidator.addValidatorsBasedOnAnnotations(this);
    }

    @Override
    protected void buildLayout() {
    }

    private void addKoodistoKieli(UiBuilder uiBuilder) {
        kieli = uiBuilder.koodistoComboBox(null, KoodistoURI.KOODISTO_KIELI_URI, true);
        kieli.setCaptionFormatter(koodiNimiFormatter);
        kieli.setImmediate(true);
        addHL(kieli);
    }

    private void addInfoText() {
        Label label = UiUtil.label(null, T(mode() + "yleisohje"));
        label.setContentMode(Label.CONTENT_TEXT);
        label.setWidth(100, UNITS_PERCENTAGE);
        label.setStyleName(Oph.LABEL_SMALL);
        addHL(label);
    }

    private void addTextField(String propertyKey) {
        tftutkintoohjelmanNimi = UiUtil.textField(null, null, null, null, T(mode() + propertyKey + ".prompt"));
        tftutkintoohjelmanNimi.setRequired(true);
        tftutkintoohjelmanNimi.setImmediate(true);
        tftutkintoohjelmanNimi.setValidationVisible(true);
        tftutkintoohjelmanNimi.setWidth(400, UNITS_PIXELS);
        tftutkintoohjelmanNimi.addListener(new Property.ValueChangeListener() {
            private static final long serialVersionUID = -382717228031608542L;

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (event != null) {
                    String value = (String) event.getProperty().getValue();
                    btNext.setEnabled(value != null ? !value.isEmpty() : false);
                }
            }
        });

        addHL(tftutkintoohjelmanNimi);
    }

    private void addNavigationButtonLayout() {
        final HorizontalLayout hl = addHL();
        btCancel = UiUtil.buttonSmallSecodary(hl, I18N.getMessage("peruuta"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                dialog.windowClose();
            }
        });

        btNext = UiUtil.buttonSmallPrimary(hl, I18N.getMessage("jatka"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                LOG.debug("Add new kielikoodi : {} , text: {}", dialog.getKielikaannosViewModel().getKielikoodi(), dialog.getKielikaannosViewModel().getNimi());

                TutkintoohjelmaModel tutkintoohjelma = presenter.getPerustiedotModel().getTutkintoohjelma();
                tutkintoohjelma.addKielikaannos(dialog.getKielikaannosViewModel());
                presenter.getPerustiedotView().rebuildLanguageTextFields();

                LOG.debug("set of langs : {}", tutkintoohjelma.getKielikaannos());
                dialog.windowClose();
            }
        });
        btNext.setEnabled(false); //no item selected -> disable 

        hl.setExpandRatio(btNext, 1f);
        hl.setComponentAlignment(btCancel, Alignment.MIDDLE_LEFT);
        hl.setComponentAlignment(btNext, Alignment.MIDDLE_RIGHT);
    }

    private HorizontalLayout addHL() {
        return addHL(null);
    }

    private HorizontalLayout addHL(Component component) {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setWidth(100, UNITS_PERCENTAGE);
        if (component != null) {
            hl.addComponent(component);
        }
        hl.setMargin(false, false, true, true);
        addComponent(hl);
        return hl;
    }

    private String mode() {
        return this.mode ? "edit." : "add.";
    }
    private CaptionFormatter<KoodiType> koodiNimiFormatter = new CaptionFormatter<KoodiType>() {
        @Override
        public String formatCaption(KoodiType dto) {
            if (dto == null) {
                return "";
            }

            return TarjontaUIHelper.getKoodiMetadataForLanguage(dto, I18N.getLocale()).getNimi();
        }
    };
}
