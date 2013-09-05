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
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import static com.vaadin.terminal.Sizeable.UNITS_PERCENTAGE;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.ui.validation.JSR303FieldValidator;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.TutkintoohjelmaModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaKorkeakouluPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalLayout;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.util.UiUtil;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;

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
    private BeanItemContainer<TutkintoohjelmaModel> bic;
    private ValitseTutkintoohjelmaDialog dialog;
    private Button btNext, btPrev;
    private CollapsibleTutkintoohjelmaTable tbTutkintoohjelma;
    TarjontaKoodistoHelper tkHelper;

    @Override
    protected void buildLayout() {
    }

    private enum RadioItemType {

        SELECT, EDIT, ADD
    };

    public ValitseTutkintoohjelmaFormView(TarjontaKorkeakouluPresenter presenter, TarjontaKoodistoHelper tkHelper, UiBuilder uiBuilder, ValitseTutkintoohjelmaDialog dialog) {
        this.presenter = presenter;
        this.uiBuilder = uiBuilder;
        this.dialog = dialog;
        this.tkHelper = tkHelper;

        setSizeFull();
        addInfoText();
        addHeaderText("tutkinto-ohjelma");
        addTutkintoohjelmaRow();
        addNavigationButtonLayout();
        JSR303FieldValidator.addValidatorsBasedOnAnnotations(this);
    }

    private void addHeaderText(final String propertyKey) {
        Label label = UiUtil.label(null, T(propertyKey));
        label.setContentMode(Label.CONTENT_TEXT);
        label.setWidth(100, UNITS_PERCENTAGE);
        addHL(label);
    }

    private void addInfoText() {
        Label label = UiUtil.label(null, T("yleisohje"));
        label.setContentMode(Label.CONTENT_TEXT);
        label.setWidth(100, UNITS_PERCENTAGE);
        label.setStyleName(Oph.LABEL_SMALL);
        addHL(label);
    }

    private void addTutkintoohjelmaRow() {
        bic = new BeanItemContainer<TutkintoohjelmaModel>(TutkintoohjelmaModel.class);

        tbTutkintoohjelma = new CollapsibleTutkintoohjelmaTable(tkHelper);
        tbTutkintoohjelma.setSizeFull();
        tbTutkintoohjelma.setHeight("280px");
        tbTutkintoohjelma.setNullSelectionAllowed(false);
        tbTutkintoohjelma.setImmediate(true);
        tbTutkintoohjelma.addListener(new Property.ValueChangeListener() {
            private static final long serialVersionUID = -382717228031608542L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                LOG.debug("Selected: {}, {}" + tbTutkintoohjelma.getSelectedRows(), event);

                if (event != null && !tbTutkintoohjelma.getSelectedRows().isEmpty()) {

                    btNext.setEnabled(true);
                } else {
                    btNext.setEnabled(false);
                }
            }
        });


        addHL(tbTutkintoohjelma);
    }

    private void addNavigationButtonLayout() {
        final HorizontalLayout hl = addHL();
        btPrev = UiUtil.buttonSmallSecodary(hl, I18N.getMessage("peruuta"), new Button.ClickListener() {
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
                for (KielikaannosViewModel kieli : tbTutkintoohjelma.getSelectedRows()) {
                    presenter.getPerustiedotModel().getTutkintoohjelma().addKielikaannos(kieli);
                }
                presenter.getPerustiedotView().rebuildLanguageTextFields();
                dialog.windowClose();
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
        tbTutkintoohjelma.addDataToContainer(ds);
    }

    /**
     * Clear all data items from a tree component.
     */
    public void clearAllDataItems() {
        tbTutkintoohjelma.removeAllItems();
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
}
