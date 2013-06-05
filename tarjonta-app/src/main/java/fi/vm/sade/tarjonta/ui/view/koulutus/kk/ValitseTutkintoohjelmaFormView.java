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
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import static com.vaadin.terminal.Sizeable.UNITS_PERCENTAGE;
import static com.vaadin.terminal.Sizeable.UNITS_PIXELS;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.ui.validation.JSR303FieldValidator;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusKoodistoModel;
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
public class ValitseTutkintoohjelmaFormView extends AbstractVerticalLayout {

    private static final long serialVersionUID = 6245718709351863230L;
    private static transient final Logger LOG = LoggerFactory.getLogger(ValitseTutkintoohjelmaFormView.class);
    private TarjontaKorkeakouluPresenter presenter;
    private UiBuilder uiBuilder;
    @NotNull(message = "{validation.Koulutus.tutkintoohjelma.notNull}")
    @PropertyId("tutkintoohjelma")
    private ComboBox cbTutkintoohjelma;
    private OptionGroup valitseMuokkaaLisaa;
    private BeanItemContainer<TutkintoohjelmaModel> bic;
    private ValitseTutkintoohjelmaDialog dialog;
    private Button btNext, btPrev;

    @Override
    protected void buildLayout() {
    
    }

    private enum RadioItemType {

        SELECT, EDIT, ADD
    };

    public ValitseTutkintoohjelmaFormView(TarjontaKorkeakouluPresenter presenter, UiBuilder uiBuilder, ValitseTutkintoohjelmaDialog dialog) {
        this.presenter = presenter;
        this.uiBuilder = uiBuilder;
        this.dialog = dialog;

        setSizeFull();
        addInfoText();
        addRadio();
        addTutkintoohjelmaRow("tutkinto-ohjelma");
        addNavigationButtonLayout();
        JSR303FieldValidator.addValidatorsBasedOnAnnotations(this);
    }

    private void addInfoText() {
        Label label = UiUtil.label(null, T("yleisohje"));
        label.setContentMode(Label.CONTENT_TEXT);
        label.setWidth(100, UNITS_PERCENTAGE);
        label.setStyleName(Oph.LABEL_SMALL);
        addHL(label);
    }

    private void addRadio() {
        RadioItem radioItem = new RadioItem(RadioItemType.SELECT, T("radioValitseNimi"));

        valitseMuokkaaLisaa = new OptionGroup();
        valitseMuokkaaLisaa.addItem(radioItem);
        valitseMuokkaaLisaa.addItem(new RadioItem(RadioItemType.EDIT, T("radioMuokkaa")));
        valitseMuokkaaLisaa.addItem(new RadioItem(RadioItemType.ADD, T("radioLisaa")));
        valitseMuokkaaLisaa.setImmediate(true);
        valitseMuokkaaLisaa.setValue(radioItem);
        valitseMuokkaaLisaa.addListener(new ValueChangeListener() {
            private static final long serialVersionUID = -382717228031608542L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (event != null && event.getProperty() != null) {
                    final RadioItem ri = (RadioItem) event.getProperty().getValue();
                    btNext.setEnabled(true);
                    enableNextDialogButton(ri.getType());
                }
            }
        });

        addHL(valitseMuokkaaLisaa);
    }

    private void addTutkintoohjelmaRow(final String propertyKey) {
        bic = new BeanItemContainer<TutkintoohjelmaModel>(TutkintoohjelmaModel.class);

        cbTutkintoohjelma = new ComboBox();
        cbTutkintoohjelma.setContainerDataSource(bic);
        cbTutkintoohjelma.setInputPrompt(T(propertyKey + ".prompt"));
        cbTutkintoohjelma.setWidth(300, UNITS_PIXELS);
        cbTutkintoohjelma.setNullSelectionAllowed(false);
        cbTutkintoohjelma.setImmediate(true);
        cbTutkintoohjelma.setItemCaptionMode(ComboBox.ITEM_CAPTION_MODE_PROPERTY);
        cbTutkintoohjelma.setItemCaptionPropertyId(KoulutusKoodistoModel.MODEL_NAME_PROPERY);
        cbTutkintoohjelma.addListener(new Property.ValueChangeListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                LOG.debug("Selected: {}, {}" + cbTutkintoohjelma.getValue(), event);

                if (event != null) {
                    presenter.getPerustiedotModel().setTutkintoohjelma((TutkintoohjelmaModel) cbTutkintoohjelma.getValue());
                    enableNextDialogButton(RadioItemType.SELECT);
                } else {
                    LOG.debug("Null event object");
                }
            }
        });

        addHL(cbTutkintoohjelma);
    }

    private void addNavigationButtonLayout() {
        final HorizontalLayout hl = addHL();
        btPrev = UiUtil.buttonSmallSecodary(hl, I18N.getMessage("peruuta"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                dialog.windowClose();
                presenter.showValitseKoulutusDialog();
            }
        });

        btNext = UiUtil.buttonSmallSecodary(hl, I18N.getMessage("jatka"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                dialog.windowClose();
                RadioItem ri = (RadioItem) valitseMuokkaaLisaa.getValue();

                switch (ri.getType()) {
                    case ADD:
                        presenter.showMuokkaaTutkintoohjelmaDialog(false);
                        break;
                    case SELECT:
                        presenter.showKorkeakouluKoulutusEditView();
                        break;
                    case EDIT:
                        presenter.showMuokkaaTutkintoohjelmaDialog(true);
                        break;
                }


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

    public void reload() {
        clearAllDataItems();
        List<TutkintoohjelmaModel> ds = presenter.getSearchPresenter().searchKorkeakouluTutkintoohjelmas();

        bic.addAll(ds);
    }

    /**
     * Clear all data items from a tree component.
     */
    public void clearAllDataItems() {
        cbTutkintoohjelma.removeAllItems();
    }

    private class RadioItem {

        private RadioItemType type;
        private String name;

        private RadioItem(RadioItemType t, String name) {
            type = t;
            this.name = name;
        }

        /**
         * @return the type
         */
        public RadioItemType getType() {
            return type;
        }

        /**
         * @param type the type to set
         */
        public void setType(RadioItemType type) {
            this.type = type;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name the name to set
         */
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;

        }
    }

    private void enableNextDialogButton(RadioItemType type) {
        RadioItem ri = (RadioItem) valitseMuokkaaLisaa.getValue();
        TutkintoohjelmaModel m = (TutkintoohjelmaModel) cbTutkintoohjelma.getValue();
        switch (type) {
            case ADD:
                btNext.setEnabled(ri != null);
                cbTutkintoohjelma.setEnabled(false);
                cbTutkintoohjelma.setValue(null);
                break;
            case SELECT:
            case EDIT:
                boolean enable = ri != null && m != null;
                btNext.setEnabled(enable);
                cbTutkintoohjelma.setEnabled(true);
                break;
        }
    }
}
