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

import com.google.common.base.Preconditions;
import com.vaadin.data.Property;
import com.vaadin.data.util.PropertyFormatter;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.data.validator.CompositeValidator;
import com.vaadin.data.validator.IntegerValidator;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import fi.vm.sade.generic.ui.component.CaptionFormatter;
import fi.vm.sade.generic.ui.component.FieldValueFormatter;
import fi.vm.sade.generic.ui.component.OphTokenField;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.widget.DefaultKoodiCaptionFormatter;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.koodisto.widget.WidgetFactory;
import fi.vm.sade.vaadin.util.UiBaseUtil;
import fi.vm.sade.vaadin.util.UiUtil;
import static fi.vm.sade.vaadin.util.UiUtil.textField;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Helper class to make creating of styled components easier.
 *
 * @author jani
 * @author mlyly
 */
@Configurable
public class UiBuilder extends UiUtil {

    private static final String TWO_DIGIT_FORMAT = "#0.##";
    private static final char FALLBACK_DECIMAL_CHAR = '.';
    private static final String FALLBACK_DECIMAL_MARK = FALLBACK_DECIMAL_CHAR + "";
    @Autowired
    private transient WidgetFactory bean;
    public static final String LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
            + "Donec molestie neque non enim lobortis sed scelerisque ante lacinia. Donec ut "
            + "justo non lectus ultricies porttitor. Praesent ultrices eros eget nulla "
            + "scelerisque sodales. Donec aliquet libero vel risus posuere porta. "
            + "Donec sit amet fermentum tellus. Aenean eget commodo sem. Sed enim mauris, "
            + "rutrum eget dignissim eu, vehicula sit amet purus. Nulla tincidunt volutpat "
            + "urna, sit amet lobortis metus dictum vel. Pellentesque semper felis a lacus "
            + "dictum commodo. Donec vitae feugiat mi. Aliquam facilisis nisl ut magna "
            + "suscipit faucibus. Sed consequat neque non augue tincidunt sed venenatis"
            + " turpis tincidunt. Quisque interdum pharetra mauris nec tincidunt.";
    private static final Logger LOG = LoggerFactory.getLogger(UiBuilder.class);
    /**
     * Default field value as uri formatter for koodisto components - since we
     * store uri's in Tarjonta.
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
     * Field value formatter that always stores also the koodi version to the
     * value, separated with "#".
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

    public KoodistoComponent koodistoComboBox(AbstractLayout layout, final String koodistoUri) {
        return koodistoComboBox(layout, koodistoUri, (PropertysetItem) null, null, null, (ComboBox) null, true);
    }

    public KoodistoComponent koodistoComboBox(AbstractLayout layout, final String koodistoUri, boolean uriWithVersion) {
        return koodistoComboBox(layout, koodistoUri, (PropertysetItem) null, null, null, (ComboBox) null, uriWithVersion);
    }

    public KoodistoComponent koodistoComboBox(AbstractLayout layout, final String koodistoUri, String prompt) {
        return koodistoComboBox(layout, koodistoUri, (PropertysetItem) null, null, prompt, (ComboBox) null, true);
    }

    public KoodistoComponent koodistoComboBox(AbstractLayout layout, final String koodistoUri, String prompt, boolean uriWithVersion) {
        return koodistoComboBox(layout, koodistoUri, (PropertysetItem) null, null, prompt, (ComboBox) null, uriWithVersion);
    }

    public KoodistoComponent koodistoComboBox(AbstractLayout layout, final String koodistoUri, String prompt, ComboBox cb) {
        return koodistoComboBox(layout, koodistoUri, (PropertysetItem) null, null, prompt, cb, true);
    }

    public KoodistoComponent koodistoComboBox(AbstractLayout layout, final String koodistoUri, String prompt, ComboBox cb, boolean uriWithVersion) {
        return koodistoComboBox(layout, koodistoUri, (PropertysetItem) null, null, prompt, cb, uriWithVersion);
    }

    /**
     * Create new KoodistoComponent with ComboBox. Sets combobox's filtering
     * mode to "CONTAINS".
     *
     * Note: not in immediate mode by default. Note: null selection not allowed.
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
    public KoodistoComponent koodistoComboBox(AbstractLayout layout, final String koodistoUri, PropertysetItem psi, String expression, String prompt, boolean uriWithVersion) {
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
    public KoodistoComponent koodistoComboBox(AbstractLayout layout, final String koodistoUri, PropertysetItem psi, String expression, String prompt) {
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
    public KoodistoComponent koodistoComboBox(AbstractLayout layout, final String koodistoUri, PropertysetItem psi, String expression, String prompt, ComboBox cb) {
        return koodistoComboBox(layout, koodistoUri, psi, expression, prompt, cb, true);
    }

    /**
     * Create new KoodistoComponent with ComboBox. Sets compobox's filtering
     * mode to "CONTAINS".
     *
     * Note: not in immediate mode by default. Note: null selection not allowed.
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
    public KoodistoComponent koodistoComboBox(AbstractLayout layout, final String koodistoUri, PropertysetItem psi, String expression, String prompt, ComboBox cb, boolean uriWithVersion) {
        // Koodisto displayed in ComboBox

        ComboBox combo = (cb == null) ? comboBox(null, null, null) : cb;

        combo.setFilteringMode(AbstractSelect.Filtering.FILTERINGMODE_CONTAINS);
        if (prompt != null) {
            combo.setInputPrompt(prompt);
        }
        //Tuomas Katva 27.2.2014 -> OVT-6744 ei haeta passiivisia koodeja koodistosta.
        KoodistoComponent c = bean.createComponent(koodistoUri,true);

        // Wire koodisto to combobox
        c.setField(combo);

        // BOUND value as uri, with or without version information
        if (uriWithVersion) {
            c.setFieldValueFormatter(DEFAULT_URI_AND_VERSION_FIELD_VALUE_FORMATTER);
        } else {
            c.setFieldValueFormatter(DEFAULT_URI_FIELD_VALUE_FORMATTER);
        }

        // Selected data bound here if wanted
        bindFieldToAProperty(psi, expression, c);

        UiBaseUtil.handleAddComponent(layout, c);

        return c;
    }

    /**
     * Creates a koodisto-bound TwinColSelect component with given koodistoUri.
     * Created component is in "multiselect"-mode. Created component is in
     * immediate mode.
     *
     * Note: a Vaadin bug requires that twincol selects "value" has to be a
     * <code>Set</code>. ie. if you want to bind the component to bean item the
     * item has to be of type "Set".
     *
     * Note: to change how the data is shown in the compoent you must set the
     * <code>CaptionFormatter</code> for the component. Similarily if you want
     * to change the actual values returned bt the component (by default koodi
     * uri's) you can set the
     * <code>FieldValueFormatter</code> for the component.
     *
     * @param layout if given created component will be added there
     * @param koodistoUri koodisto uri to bind component to
     * @param uriWithVersion if true also version information stored to the
     * value
     * @return created component
     */
    public KoodistoComponent koodistoTwinColSelectUri(AbstractLayout layout, final String koodistoUri, boolean uriWithVersion) {

        // Koodisto displayed in TwinColSelect
        TwinColSelect c = twinColSelect();

        // Only multiple (Set<String>) values allowed!
        c.setMultiSelect(true);

        final KoodistoComponent kc = bean.createComponent(koodistoUri);

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
    public KoodistoComponent koodistoTwinColSelectUri(AbstractLayout layout, final String koodistoUri) {
        return koodistoTwinColSelectUri(layout, koodistoUri, true);
    }

    /**
     * Similar to koodistoTwinColSelectUri() but also possible to bind to a
     * property.
     *
     * @param layout
     * @param koodistoUri
     * @param psi
     * @param expression
     * @param uriWithVersion if true also version information stored to the
     * value
     * @return
     */
    public KoodistoComponent koodistoTwinColSelect(AbstractLayout layout, final String koodistoUri, PropertysetItem psi, String expression, boolean uriWithVersion) {

        final KoodistoComponent kc = koodistoTwinColSelectUri(layout, koodistoUri, uriWithVersion);

        // Selected data bound here if wanted
        bindFieldToAProperty(psi, expression, kc);

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
    public KoodistoComponent koodistoTwinColSelect(AbstractLayout layout, final String koodistoUri, PropertysetItem psi, String expression) {
        return koodistoTwinColSelect(layout, koodistoUri, psi, expression, true);
    }

    /**
     * Create token field selector with koodisto linked combobox.
     *
     * @param layout
     * @param koodistoUri
     * @param psi
     * @param expression
     * @return
     */
    public OphTokenField koodistoTokenField(AbstractLayout layout, final String koodistoUri, PropertysetItem psi, String expression) {

        KoodistoComponent kc = koodistoComboBox(null, koodistoUri);
        kc.setImmediate(true);
        kc.setCaptionFormatter(new DefaultKoodiCaptionFormatter());


        OphTokenField f = new OphTokenField();
        f.setSelectionComponent(kc);

        // Otherwise not rendered at all?
        kc.setSizeUndefined();

        // Selected data bound here if wanted
        bindFieldToAProperty(psi, expression, f);

        UiBaseUtil.handleAddComponent(layout, f);

        return f;
    }

    /**
     * Bind field "f" to a data source.
     *
     * @param psi
     * @param expression
     * @param f
     */
    private static void bindFieldToAProperty(PropertysetItem psi, String expression, Field f) {
        if (psi != null && expression != null) {
            Property p = psi.getItemProperty(expression);
            if (p == null) {
                throw new RuntimeException("Cannot bind to null property: expression = '" + expression + "', available: " + psi.getItemPropertyIds());
            }
            f.setPropertyDataSource(p);
        }
    }

    public static TextField integerField(final AbstractComponentContainer layout,
            final PropertysetItem psi,
            final String expression,
            final String caption,
            final String prompt,
            final int minValue,
            final int maxValue,
            final String errorMessage) {

        TextField tf = textField(layout, psi, expression, caption, prompt);

        CompositeValidator cv = new CompositeValidator();
        if (errorMessage != null) {
            cv.setErrorMessage(errorMessage);
        }

        cv.addValidator(new IntegerValidator(errorMessage));
        cv.addValidator(new AbstractValidator(errorMessage) {
            private static final long serialVersionUID = -7750387119487308450L;

            @Override
            public boolean isValid(Object value) {
                Integer v;
                if (value instanceof Integer) {
                    v = (Integer) value;
                } else if (value instanceof String) {
                    v = Integer.parseInt((String) value);
                } else {
                    return false;
                }

                return (v >= minValue && v <= maxValue);
            }
        });

        tf.addValidator(cv);

        return tf;
    }

    public static TextField doubleField(final AbstractComponentContainer layout,
            final PropertysetItem psi,
            final String expression,
            final String caption,
            final String prompt,
            final double minValue,
            final double maxValue,
            final String errorMessage,
            final Locale locale) {
        Preconditions.checkNotNull(expression, "PropertysetItem object cannot be null.");
        Preconditions.checkNotNull(expression, "Expression (property field name) cannot be null.");
        Preconditions.checkNotNull(errorMessage, "Error message cannot be null.");
        Preconditions.checkNotNull(locale, "Locale object cannot be null.");

        final DecimalFormat df = new DecimalFormat(TWO_DIGIT_FORMAT, new DecimalFormatSymbols(locale));

        final DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator(FALLBACK_DECIMAL_CHAR);
        final DecimalFormat dfFallback = new DecimalFormat(TWO_DIGIT_FORMAT);
        dfFallback.setDecimalFormatSymbols(dfs);

        TextField tf = textField(layout);
        CompositeValidator cv = new CompositeValidator();
        if (errorMessage != null) {
            cv.setErrorMessage(errorMessage);
        }

        cv.addValidator(new AbstractValidator(errorMessage) {
            private static final long serialVersionUID = -7750387119487308450L;

            @Override
            public boolean isValid(Object value) {
                Double v;
                if (value instanceof Double) {
                    v = (Double) value;
                } else if (value instanceof String) {
                    final String strValue = (String) value;
                    try {
                        v = df.parse(strValue).doubleValue();
                        if (tooManyDigits(strValue)) {
                            return false;
                        }
                    } catch (Exception e) {
                        LOG.debug("Validation failed for an object {}", strValue, e);
                        return false;
                    }
                } else {
                    return false;
                }

                return (Double.compare(minValue, v) <= 0 && Double.compare(v, maxValue) <= 0);
            }
        });
        tf.addValidator(cv);

        tf.setPropertyDataSource(new PropertyFormatter(psi.getItemProperty(expression)) {
            private static final long serialVersionUID = -1024644297269795524L;

            @Override
            public String format(Object value) {
                if (value == null) {
                    return "";
                } else {
                    return df.format(value);
                }
            }

            @Override
            public Object parse(String formattedValue) throws Exception {
                if (formattedValue != null && formattedValue.trim().length() != 0) {

                    if (formattedValue.contains(FALLBACK_DECIMAL_MARK)) {
                        return (Double) dfFallback.parse(formattedValue).doubleValue();
                    } else {
                        return (Double) df.parse(formattedValue).doubleValue();
                    }
                }
                return null;
            }
        });

        return tf;
    }
    
    private static boolean tooManyDigits(String strVal) {
        int index = strVal.indexOf(',');
        if (index < 0) {
            index = strVal.indexOf('.');
        }
        return index < strVal.length() - 3;
        
    }
}
