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
import static com.vaadin.terminal.Sizeable.UNITS_PERCENTAGE;
import static com.vaadin.terminal.Sizeable.UNITS_PIXELS;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.ui.component.CaptionFormatter;
import fi.vm.sade.generic.ui.validation.JSR303FieldValidator;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusKoodistoModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.KoulutuskoodiRowModel;
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
public class ValitseKoulutusFormView extends AbstractVerticalLayout {

    private static final long serialVersionUID = 6245718709351863230L;
    private static transient final Logger LOG = LoggerFactory.getLogger(ValitseKoulutusFormView.class);
    private static final Object[] VISIBLE_COLUMNS = new Object[]{KoulutusKoodistoModel.MODEL_VALUE_PROPERY, KoulutusKoodistoModel.MODEL_NAME_PROPERY};
    private TarjontaKorkeakouluPresenter presenter;
    private UiBuilder uiBuilder;
    @PropertyId("koulutusala")
    private KoodistoComponent kcKoulutusalas;
    @NotNull(message = "{validation.Koulutus.searchField.notNull}")
    @PropertyId("searchWord")
    private TextField tfSearchField;
    private Table table;
    private BeanItemContainer<KoulutuskoodiRowModel> bic;
    private ValitseKoulutusDialog dialog;
    private Button btClear, btSearch, info, btNext, btPrev;

    public ValitseKoulutusFormView(TarjontaKorkeakouluPresenter presenter, UiBuilder uiBuilder, ValitseKoulutusDialog dialog) {
        this.presenter = presenter;
        this.uiBuilder = uiBuilder;
        this.dialog = dialog;

        setSizeFull();
        addInfoText();
        addComponentKoulutusala();
        addComponentSearchField();
        addSearchButtons();
        addComponentKoulutuskoodiTable();
        addNavigationButtonLayout();
        JSR303FieldValidator.addValidatorsBasedOnAnnotations(this);
    }

    @Override
    protected void buildLayout() {
        reload();
    }

    private void addInfoText() {
        Label label = UiUtil.label(null, T("yleisohje"));
        label.setContentMode(Label.CONTENT_TEXT);
        label.setWidth(100, UNITS_PERCENTAGE);
        label.setStyleName(Oph.LABEL_SMALL);

        info = UiUtil.buttonSmallInfo(null, new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                LOG.debug("info event");
            }
        });

        HorizontalLayout hl = addHL();
        hl.addComponent(label);
        hl.addComponent(info);

        hl.setExpandRatio(label, 1l);
        hl.setExpandRatio(info, 0l);
    }

    private void addComponentKoulutusala() {
        ComboBox comboBox = new ComboBox();
        comboBox.setNullSelectionAllowed(false);
        comboBox.setCaption(T("koulutusala.caption"));

        kcKoulutusalas = uiBuilder.koodistoComboBox(null, KoodistoURIHelper.KOODISTO_KOULUTUSALA_URI, T("koulutusala.prompt"), comboBox, true);
        kcKoulutusalas.setImmediate(true);
        kcKoulutusalas.setCaptionFormatter(koodiNimiFormatter);
        addHL(kcKoulutusalas);
    }

    private void addComponentSearchField() {
        tfSearchField = UiUtil.textFieldSmallSearch(null);
        tfSearchField.setImmediate(true);
        tfSearchField.setWidth(100, UNITS_PERCENTAGE);
        tfSearchField.setCaption(T("search.caption"));
        tfSearchField.addListener(new Property.ValueChangeListener() {
            private static final long serialVersionUID = -382717228031608542L;

            @Override
            public void valueChange(ValueChangeEvent event) {

                LOG.debug("valueChange {} {}", event, presenter.getPerustiedotModel().getValitseKoulutus());

            }
        });

        addHL(tfSearchField);
    }

    private void addSearchButtons() {
        final HorizontalLayout hl = addHL();

        btClear = UiUtil.buttonSmallSecodary(hl, T("button.clear"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                LOG.debug("clear {}", event);
                presenter.getPerustiedotModel().getValitseKoulutus().clear();
                tfSearchField.setValue("");
                kcKoulutusalas.getField().setValue(null);
                reload();
            }
        });

        btSearch = UiUtil.buttonSmallSecodary(hl, T("button.search"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                LOG.debug("buttonClick  {} {}", presenter.getPerustiedotModel().getValitseKoulutus(), event);
                reload();
            }
        });

        hl.setExpandRatio(btSearch, 1f);
        hl.setComponentAlignment(btClear, Alignment.MIDDLE_LEFT);
        hl.setComponentAlignment(btSearch, Alignment.MIDDLE_LEFT);

    }

    private void addComponentKoulutuskoodiTable() {
        table = new Table();

        //Bean 
        bic = new BeanItemContainer<KoulutuskoodiRowModel>(KoulutuskoodiRowModel.class);
        bic.addNestedContainerProperty(KoulutusKoodistoModel.MODEL_VALUE_PROPERY);
        bic.addNestedContainerProperty(KoulutusKoodistoModel.MODEL_NAME_PROPERY);
        table.setContainerDataSource(bic);

        //Layout
        table.setWidth(100, UNITS_PERCENTAGE);
        table.setHeight(250, UNITS_PIXELS);
        table.setImmediate(true);
        table.setSelectable(true);
        table.setVisibleColumns(VISIBLE_COLUMNS);
        table.setColumnHeader(KoulutusKoodistoModel.MODEL_VALUE_PROPERY, T("column.koulutuskoodi"));
        table.setColumnHeader(KoulutusKoodistoModel.MODEL_NAME_PROPERY, T("column.kuvaus"));
        table.setColumnExpandRatio(KoulutusKoodistoModel.MODEL_VALUE_PROPERY, 0.3f);
        table.setColumnExpandRatio(KoulutusKoodistoModel.MODEL_NAME_PROPERY, 0.7f);

        HorizontalLayout hlTable = addHL(table);
        hlTable.setExpandRatio(table, 1f);

        //Data sorting
        table.setSortContainerPropertyId(KoulutusKoodistoModel.MODEL_VALUE_PROPERY);

        //Listener
        table.addListener(new Property.ValueChangeListener() {
            private static final long serialVersionUID = -382717228031608542L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                LOG.debug("Selected: {}, {}" + table.getValue(), event);

                if (event != null) {
                    presenter.getPerustiedotModel().getValitseKoulutus().setKoulutuskoodiRow((KoulutuskoodiRowModel) table.getValue());
                    btNext.setEnabled(true);
                } else {
                    LOG.debug("Null event object");
                }
            }
        });
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
                dialog.windowClose();
                presenter.showValitseTutkintoohjelmaDialog();
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
    private CaptionFormatter koodiNimiFormatter = new CaptionFormatter<KoodiType>() {
        @Override
        public String formatCaption(KoodiType dto) {
            if (dto == null) {
                return "";
            }

            return TarjontaUIHelper.getKoodiMetadataForLanguage(dto, I18N.getLocale()).getNimi();
        }
    };

    /**
     * Reloads the data to the Hakukohde list.
     */
    public void reload() {
        reload(null);
    }

    public void reload(final String searchParam) {
        clearAllDataItems();
        List<KoulutuskoodiRowModel> ds = presenter.filterKoulutuskoodis();

        bic.addAll(ds);
        table.setPageLength(ds.size());
        table.sort();
    }

    /**
     * Clear all data items from a tree component.
     */
    public void clearAllDataItems() {
        table.removeAllItems();
    }
}
