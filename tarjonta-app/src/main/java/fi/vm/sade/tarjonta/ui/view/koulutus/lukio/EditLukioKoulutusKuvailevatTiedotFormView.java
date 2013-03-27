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
package fi.vm.sade.tarjonta.ui.view.koulutus.lukio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;

import com.google.common.base.Preconditions;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.generic.ui.component.OphTokenField;
import fi.vm.sade.generic.ui.validation.JSR303FieldValidator;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.KoulutusLukioKuvailevatTiedotViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.vaadin.constants.LabelStyleEnum;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;

@Configurable(preConstruction = true)
@FormView(matchFieldsBy = FormFieldMatch.ANNOTATION)
public class EditLukioKoulutusKuvailevatTiedotFormView extends VerticalLayout {

    @Autowired
    private transient TarjontaUIHelper uiHelper;
    private static transient final Logger LOG = LoggerFactory
            .getLogger(EditLukioKoulutusKuvailevatTiedotFormView.class);
    private static transient final long serialVersionUID = 1L;
    private transient I18NHelper i18n;
    private transient TarjontaPresenter presenter;
    private transient UiBuilder uiBuilder;
    private EditLukioKoulutusKuvailevatTiedotTekstikentatTabSheet tekstit;
    private OphTokenField[] tokenFields = new OphTokenField[6];
    private int tokenFieldIndex = 0;

    private GridLayout buildKielivalinnat() {
        GridLayout kielivalinnat = new GridLayout(2, 1);
        buildSpacingGridRow(kielivalinnat);
        kielivalinnat.setSizeFull();
        //SIGH, how to set the right column to occupy all available space?
        kielivalinnat.setColumnExpandRatio(0, 0.15f);
        kielivalinnat.setColumnExpandRatio(1, 0.85f);
        addRow(kielivalinnat, "kieliA");
        addRow(kielivalinnat, "kieliB1");
        addRow(kielivalinnat, "kieliB2");
        addRow(kielivalinnat, "kieliB3");
        addRow(kielivalinnat, "kieletMuu");
        buildSpacingGridRow(kielivalinnat);
        return kielivalinnat;
    }

    /**
     * Add new row to the kielet section
     */
    private void addRow(final GridLayout kielivalinnat, final String name) {
        buildLabel(kielivalinnat, name);
        final VerticalLayout vl = new VerticalLayout();
        buildTokenField(name, vl);

        kielivalinnat.addComponent(vl, 1, kielivalinnat.getCursorY() - 1);
        Label help = UiBuilder.label(this, T(name + ".help"), LabelStyleEnum.TEXT);
        help.setSizeFull();
        vl.addComponent(help);
    }

    private void buildTokenField(final String name, final VerticalLayout vl) {
        final PropertysetItem psi = new BeanItem(presenter.getModel().getKoulutusLukioKuvailevatTiedot());
        final OphTokenField tokenField = uiBuilder.koodistoTokenField(vl,
                KoodistoURIHelper.KOODISTO_AMMATTINIMIKKEET_URI, psi, name);
        tokenField.setFormatter(new OphTokenField.SelectedTokenToTextFormatter() {
            @Override
            public String formatToken(Object selectedToken) {
                return uiHelper.getKoodiNimi((String) selectedToken);
            }
        });

        tokenFields[tokenFieldIndex++] = tokenField;
    }

    public EditLukioKoulutusKuvailevatTiedotFormView() {
    }

    public EditLukioKoulutusKuvailevatTiedotFormView(final TarjontaPresenter presenter, final UiBuilder uiBuilder,
            final KoulutusLukioKuvailevatTiedotViewModel model) {
        this.uiBuilder = uiBuilder;
        this.presenter = presenter;
        initializeLayout();
    }

    private void initializeLayout() {
        UiBuilder.label(this, T("kielivalikoima.label"), LabelStyleEnum.H2);
        UiBuilder.label(this, T("kielivalikoima.help"), LabelStyleEnum.TEXT);

        AbstractLayout kielivalinnat = buildKielivalinnat();
        addComponent(kielivalinnat);
        setComponentAlignment(kielivalinnat, Alignment.TOP_LEFT);

        addComponent(buildLukioDiplomit());

        tekstit = new EditLukioKoulutusKuvailevatTiedotTekstikentatTabSheet(presenter.getModel(), uiHelper,
                uiBuilder);
        addComponent(tekstit);

        // activate all property annotation validations
        JSR303FieldValidator.addValidatorsBasedOnAnnotations(this);
    }

    private Component buildLukioDiplomit() {
        VerticalLayout kielivalinnat = new VerticalLayout();
        UiBuilder.label(kielivalinnat, T("luokiodiplomit.label"), LabelStyleEnum.H2);
        UiBuilder.label(kielivalinnat, T("luokiodiplomit.help"), LabelStyleEnum.TEXT);
        buildTokenField("diplomit", kielivalinnat);
        return kielivalinnat;
    }

    private Label buildLabel(GridLayout grid, final String propertyKey) {
        Preconditions.checkNotNull(propertyKey, "label caption was null!");
        gridLabel(grid, propertyKey);
        final Label label = new Label();
        grid.addComponent(label);
        grid.newLine();
        buildSpacingGridRow(grid);
        return label;
    }

    /*
     * PRIVATE HELPER METHODS:
     */
    private void buildSpacingGridRow(GridLayout grid) {
        gridLabel(grid, null);
        final CssLayout cssLayout = new CssLayout();
        cssLayout.setHeight(4, UNITS_PIXELS);
        grid.addComponent(cssLayout);
        grid.newLine();
    }

    private AbstractLayout gridLabel(GridLayout grid, final String propertyKey) {
        HorizontalLayout hl = UiUtil.horizontalLayout(false, UiMarginEnum.RIGHT);
        hl.setSizeFull();

        if (propertyKey != null) {
            Label labelValue = UiUtil.label(hl, T(propertyKey));
            hl.setComponentAlignment(labelValue, Alignment.TOP_RIGHT);
            labelValue.setSizeUndefined();
            grid.addComponent(hl);
            grid.setComponentAlignment(hl, Alignment.TOP_RIGHT);

        }
        return hl;
    }

    // Generic translation helpers
    private String T(String key) {
        return getI18n().getMessage(key);
    }

    private I18NHelper getI18n() {
        if (i18n == null) {
            i18n = new I18NHelper(this);
        }
        return i18n;
    }

    /*
     * Reload data from UI model
     */
    public void reload() {
        LOG.info("reload()");
    }

    public void reBuildTabsheet() {
        if (tekstit != null) {
            tekstit.reload();
        }
    }

    @Override
    public void attach() {
        super.attach();

        for (OphTokenField t : tokenFields) {
            t.getSelectionLayout().setWidth("900px");
        }
    }
}
