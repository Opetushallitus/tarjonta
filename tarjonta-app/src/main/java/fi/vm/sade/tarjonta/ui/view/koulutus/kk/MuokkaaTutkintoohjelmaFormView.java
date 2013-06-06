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

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import static com.vaadin.terminal.Sizeable.UNITS_PERCENTAGE;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.ui.validation.JSR303FieldValidator;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.TutkintoohjelmaModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaKorkeakouluPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalLayout;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.util.UiUtil;
import java.util.List;
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
public class MuokkaaTutkintoohjelmaFormView extends AbstractVerticalLayout {

    private static final long serialVersionUID = 6245718709351863230L;
    private static transient final Logger LOG = LoggerFactory.getLogger(MuokkaaTutkintoohjelmaFormView.class);
    private TarjontaKorkeakouluPresenter presenter;
    private MuokkaaTutkintoohjelmaDialog dialog;
    private Button btNext, btPrev;
    @NotNull(message = "{validation.Koulutus.tutkintoohjelma.notNull}")
    @PropertyId("tutkintoohjelmaNimi")
    private TextField tftutkintoohjelmanNimi;
    private boolean mode;

    public MuokkaaTutkintoohjelmaFormView(TarjontaKorkeakouluPresenter presenter, MuokkaaTutkintoohjelmaDialog dialog, boolean mode) {
        this.presenter = presenter;
        this.dialog = dialog;
        this.mode = mode;

        setSizeFull();
        addInfoText();
        addTextField("tutkinto-ohjelma");
        addNavigationButtonLayout();
        JSR303FieldValidator.addValidatorsBasedOnAnnotations(this);
    }

    @Override
    protected void buildLayout() {
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
        btPrev = UiUtil.buttonSmallSecodary(hl, I18N.getMessage("peruuta"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                dialog.windowClose();
                presenter.showValitseTutkintoohjelmaDialog();
            }
        });

        btNext = UiUtil.buttonSmallPrimary(hl, I18N.getMessage("jatka"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                TutkintoohjelmaModel tutkintoohjelma = presenter.getPerustiedotModel().getTutkintoohjelma();

                LOG.debug("tutkintoohjelma : {}", tutkintoohjelma);
                /*
                 * mode == true => edit komo name => next page => create tutkinto
                 * mode == false => add new name => next page => create new komo + tutkinto
                 */
                if (mode && tutkintoohjelma != null && tutkintoohjelma.getKomoOid() != null) {
                    //update the changed name to database
                    presenter.updateSelectedKOMOName(tutkintoohjelma.getKomoOid());
                }

                LOG.debug("tutkintoohjelmaNimi : {}", presenter.getPerustiedotModel().getTutkintoohjelmaNimi());
                dialog.windowClose();
                presenter.showKorkeakouluKoulutusEditView();

            }
        });
        btNext.setEnabled(false); //no item selected -> disable 

        hl.setExpandRatio(btNext, 1f);
        hl.setComponentAlignment(btPrev, Alignment.MIDDLE_LEFT);
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
}
