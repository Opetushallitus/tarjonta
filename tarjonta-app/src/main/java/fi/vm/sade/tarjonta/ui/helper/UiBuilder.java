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
package fi.vm.sade.tarjonta.ui.helper;

import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TwinColSelect;
import fi.vm.sade.generic.ui.component.CaptionFormatter;
import fi.vm.sade.generic.ui.component.FieldValueFormatter;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.koodisto.widget.factory.WidgetFactory;
import fi.vm.sade.vaadin.util.UiBaseUtil;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 * Helper class to make creating of styled components easier.
 *
 * @author jani
 * @author mlyly
 */
public class UiBuilder extends UiUtil {

    /**
     * Default field value as uri formatter for koodisto components - since we store uri's in Tarjonta.
     */
    public static final FieldValueFormatter DEFAULT_URI_FIELD_VALUE_FORMATTER = new FieldValueFormatter() {

        @Override
        public Object formatFieldValue(Object dto) {
            if (dto == null) {
                return null;
            }

            if (dto instanceof KoodiType) {
                KoodiType kdto = (KoodiType) dto;
                return kdto.getKoodiUri();
            } else {
                return "" + dto;
            }
        }
    };

    /**
     * Default field value as uri and versio formatter for koodisto components
     * NOTE: Value is object of type "KoodiUriAndVersioType".
     */
    public static final FieldValueFormatter DEFAULT_KOODISTO_URI_AND_VERSION_OBJECT_FIELD_VALUE_FORMATTER = new FieldValueFormatter() {

        @Override
        public Object formatFieldValue(Object dto) {
            if (dto == null) {
                return null;
            }

            if (dto instanceof KoodiType) {
                KoodiType kdto = (KoodiType) dto;
                KoodiUriAndVersioType kvt = new KoodiUriAndVersioType();
                kvt.setKoodiUri(kdto.getKoodiUri());
                kvt.setVersio(kdto.getVersio());
                return kvt;
            } else {
                return "" + dto;
            }
        }
    };

    /**
     * Field value formatter that always stores also the koodi version to the value, separated with "#".
     */
    public static final FieldValueFormatter DEFAULT_URI_AND_VERSION_FIELD_VALUE_FORMATTER = new FieldValueFormatter() {

        @Override
        public Object formatFieldValue(Object dto) {
            if (dto == null) {
                return null;
            }

            if (dto instanceof KoodiType) {
                KoodiType kdto = (KoodiType) dto;
                return kdto.getKoodiUri() + TarjontaUIHelper.KOODI_URI_AND_VERSION_SEPARATOR + kdto.getVersio();
            } else {
                return "" + dto;
            }
        }
    };


    /**
     * Default caption formatter that shows the koodi value (arvo).
     */
    public static final CaptionFormatter DEFAULT_ARVO_CAPTION_FORMATTER = new CaptionFormatter<KoodiType>() {

        @Override
        public String formatCaption(KoodiType dto) {
            if (dto == null) {
                return "";
            }

            return dto.getKoodiArvo();
        }
    };

    /**
     * Default caption formatter that shows the koodi URI as caption.
     */
    public static final CaptionFormatter DEFAULT_URI_CAPTION_FORMATTER = new CaptionFormatter<KoodiType>() {

        @Override
        public String formatCaption(KoodiType dto) {
            if (dto == null) {
                return "";
            }

            return dto.getKoodiUri();
        }
    };

//    private static final ThemeResource TAB_ICON_PLUS = new ThemeResource(UiConstant.RESOURCE_URL_OPH_IMG + "icon-add-black.png");

    public static KoodistoComponent koodistoComboBox(AbstractLayout layout, final String koodistoUri) {
        return koodistoComboBox(layout, koodistoUri, (PropertysetItem) null, null, null, (ComboBox) null, true);
    }
    public static KoodistoComponent koodistoComboBox(AbstractLayout layout, final String koodistoUri, boolean uriWithVersion) {
        return koodistoComboBox(layout, koodistoUri, (PropertysetItem) null, null, null, (ComboBox) null, uriWithVersion);
    }

    public static KoodistoComponent koodistoComboBox(AbstractLayout layout, final String koodistoUri, String prompt) {
        return koodistoComboBox(layout, koodistoUri, (PropertysetItem) null, null, prompt, (ComboBox) null, true);
    }
    public static KoodistoComponent koodistoComboBox(AbstractLayout layout, final String koodistoUri, String prompt, boolean uriWithVersion) {
        return koodistoComboBox(layout, koodistoUri, (PropertysetItem) null, null, prompt, (ComboBox) null, uriWithVersion);
    }

    public static KoodistoComponent koodistoComboBox(AbstractLayout layout, final String koodistoUri, String prompt, ComboBox cb) {
        return koodistoComboBox(layout, koodistoUri, (PropertysetItem) null, null, prompt, cb, true);
    }
    public static KoodistoComponent koodistoComboBox(AbstractLayout layout, final String koodistoUri, String prompt, ComboBox cb, boolean uriWithVersion) {
        return koodistoComboBox(layout, koodistoUri, (PropertysetItem) null, null, prompt, cb, uriWithVersion);
    }

    /**
     * Create new KoodistoComponent with ComboBox. Sets combobox's filtering mode to "CONTAINS".
     *
     * Note: not in immediate mode by default.
     * Note: null selection not allowed.
     *
     * Possible bind to a property.
     *
     * @param layout
     * @param koodistoUri
     * @param psi
     * @param expression
     * @param prompt
     * @param uriWithVersion
     * @return
     */
    public static KoodistoComponent koodistoComboBox(AbstractLayout layout, final String koodistoUri, PropertysetItem psi, String expression, String prompt, boolean uriWithVersion) {
        return koodistoComboBox(layout, koodistoUri, psi, expression, prompt, (ComboBox) null, uriWithVersion);
    }

    /**
     * Default storage mode uri with version.
     *
     * @param layout
     * @param koodistoUri
     * @param psi
     * @param expression
     * @param prompt
     * @return
     */
    public static KoodistoComponent koodistoComboBox(AbstractLayout layout, final String koodistoUri, PropertysetItem psi, String expression, String prompt) {
        return koodistoComboBox(layout, koodistoUri, psi, expression, prompt, (ComboBox) null, true);
    }

    /**
     * Default storage mode uri with version.
     *
     * @param layout
     * @param koodistoUri
     * @param psi
     * @param expression
     * @param prompt
     * @param cb
     * @return
     */
    public static KoodistoComponent koodistoComboBox(AbstractLayout layout, final String koodistoUri, PropertysetItem psi, String expression, String prompt, ComboBox cb) {
        return koodistoComboBox(layout, koodistoUri, psi, expression, prompt, cb, true);
    }

    /**
     * Create new KoodistoComponent with ComboBox. Sets compobox's filtering mode to "CONTAINS".
     *
     * Note: not in immediate mode by default.
     * Note: null selection not allowed.
     *
     * Possible bind to a property.
     *
     * @param layout
     * @param koodistoUri
     * @param psi
     * @param expression
     * @param prompt
     * @param cb optional
     * @param uriWithVersion
     * @return
     */
    public static KoodistoComponent koodistoComboBox(AbstractLayout layout, final String koodistoUri, PropertysetItem psi, String expression, String prompt, ComboBox cb, boolean uriWithVersion) {
        // Koodisto displayed in ComboBox

        ComboBox combo =  (cb == null) ? comboBox(null, null, null) : cb;

        combo.setFilteringMode(AbstractSelect.Filtering.FILTERINGMODE_CONTAINS);
        if (prompt != null) {
            combo.setInputPrompt(prompt);
        }

        final KoodistoComponent c = WidgetFactory.create(koodistoUri);

        // Wire koodisto to combobox
        c.setField(combo);

        // BOUND value as uri, with or without version information
        if (uriWithVersion) {
            c.setFieldValueFormatter(DEFAULT_URI_AND_VERSION_FIELD_VALUE_FORMATTER);
        } else {
            c.setFieldValueFormatter(DEFAULT_URI_FIELD_VALUE_FORMATTER);
        }

        // Selected data bound there
        if (psi != null && expression != null) {
            c.setPropertyDataSource(psi.getItemProperty(expression));
        }

        UiBaseUtil.handleAddComponent(layout, c);

        return c;
    }




    /**
     * Creates a koodisto-bound TwinColSelect component with given koodistoUri.
     * Created component is in "multiselect"-mode.
     * Created component is in immediate mode.
     *
     * Note: a Vaadin bug requires that twincol selects "value" has to be a <code>Set</code>. ie. if you want to bind
     * the component to bean item the item has to be of type "Set".
     *
     * Note: to change how the data is shown in the compoent you must set the <code>CaptionFormatter</code> for the component.
     * Similarily if you want to change the actual values returned bt the component (by default koodi uri's) you can
     * set the <code>FieldValueFormatter</code> for the component.
     *
     * @param layout if given created component will be added there
     * @param koodistoUri koodisto uri to bind component to
     * @param uriWithVersion if true also version information stored to the value
     * @return created component
     */
    public static KoodistoComponent koodistoTwinColSelectUri(AbstractLayout layout, final String koodistoUri, boolean uriWithVersion) {

        // Koodisto displayed in TwinColSelect
        TwinColSelect c = twinColSelect();

        // Only multiple (Set<String>) values allowed!
        c.setMultiSelect(true);

        final KoodistoComponent kc = WidgetFactory.create(koodistoUri);

        // Wire koodisto to combobox
        kc.setField(c);

        // BOUND value as uri
        if (uriWithVersion) {
            kc.setFieldValueFormatter(DEFAULT_URI_AND_VERSION_FIELD_VALUE_FORMATTER);
        } else {
            kc.setFieldValueFormatter(DEFAULT_URI_FIELD_VALUE_FORMATTER);
        }

        UiBaseUtil.handleAddComponent(layout, kc);

        return kc;
    }

    /**
     * Default storage mode uri with version.
     *
     * @param layout
     * @param koodistoUri
     * @return
     */
    public static KoodistoComponent koodistoTwinColSelectUri(AbstractLayout layout, final String koodistoUri) {
        return koodistoTwinColSelectUri(layout, koodistoUri, true);
    }



    /**
     * Similar to koodistoTwinColSelectUri() but  also possible to bind to a property.
     *
     * @param layout
     * @param koodistoUri
     * @param psi
     * @param expression
     * @param uriWithVersion if true also version information stored to the value
     * @return
     */
    public static KoodistoComponent koodistoTwinColSelect(AbstractLayout layout, final String koodistoUri, PropertysetItem psi, String expression, boolean uriWithVersion) {

        final KoodistoComponent kc = koodistoTwinColSelectUri(layout, koodistoUri, uriWithVersion);

        // Selected data bound here if wanted
        if (psi != null && expression != null) {
            kc.setPropertyDataSource(psi.getItemProperty(expression));
        }

        return kc;
    }

    /**
     * Default storage mode is uri with version.
     *
     * @param layout
     * @param koodistoUri
     * @param psi
     * @param expression
     * @return
     */
    public static KoodistoComponent koodistoTwinColSelect(AbstractLayout layout, final String koodistoUri, PropertysetItem psi, String expression) {
        return koodistoTwinColSelect(layout, koodistoUri, psi, expression, true);
    }



//    public static TabSheet koodistoLanguageTabSheets(List<KoodiType> koodisto) {
//        TabSheet tab = new TabSheet();
//
//        if (koodisto != null) {
//            for (KoodiType k : koodisto) {
//                TextField textField = UiUtil.textField(null);
//                textField.setHeight("100px");
//                textField.setWidth(UiConstant.PCT100);
//
//                tab.addTab(textField, k.getKoodiArvo(), null);
//            }
//        }
//        VerticalLayout l3 = new VerticalLayout();
//        l3.setMargin(true);
//        tab.addTab(l3, "", TAB_ICON_PLUS);
//
//        return tab;
//    }

//    public static TabSheet koodistoLanguageTabSheets(String koodistoUri, Property.ValueChangeListener valueChangeListener) {
//        VerticalLayout vl = verticalLayout(true, UiMarginEnum.NONE);
//        KoodistoComponent kc = koodistoTwinColSelectUri(vl, koodistoUri, false); // no version for uris
//        if (valueChangeListener != null) {
//            kc.addListener(valueChangeListener);
//        }
//
//        TabSheet tab = new TabSheet();
//        tab.addTab(vl, "", TAB_ICON_PLUS);
//
//        return tab;
//    }
}
