package fi.vm.sade.tarjonta.ui.helper;

import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import fi.vm.sade.generic.ui.component.CaptionFormatter;
import fi.vm.sade.generic.ui.component.FieldValueFormatter;
import fi.vm.sade.koodisto.service.types.dto.KoodiDTO;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.koodisto.widget.factory.WidgetFactory;
import fi.vm.sade.vaadin.Oph;
import java.text.MessageFormat;

/**
 *
 * @author jani
 */
public class UiBuilder extends ComponentUtil {

    public static String format(String format, Object... args) {
        return MessageFormat.format(format, args);
    }

    public static DateField newDate() {
        return UiBuilder.newDate(null, null, null);
    }

    public static DateField newDate(String caption, String dateFormat, AbstractLayout layout) {
        DateField df = new DateField();
        if (caption != null) {
            df.setCaption(caption);
        }
        df.setDateFormat(dateFormat != null ? dateFormat : "dd.MM.yyyy");
        handleAddComponent(layout, df);
        return df;
    }

    public static DateField newDateField(String caption, String dateFormat, PropertysetItem psi, String expression, AbstractOrderedLayout layout) {
        DateField c = newDate(caption, dateFormat, layout);

        if (psi != null && expression != null) {
            c.setPropertyDataSource(psi.getItemProperty(expression));
        }

        return c;
    }

    public static Link newLink(String caption, AbstractLayout layout) {
        Link link = new Link();
        link.setCaption(caption);
        link.setImmediate(false);

        handleAddComponent(layout, link);

        return link;
    }

    public static TextField newTextField(String nullRepresentation, String inputPrompt, boolean immediate) {
        TextField tf = new TextField();
        if (nullRepresentation != null) {
            tf.setNullRepresentation(nullRepresentation);
        }
        if (inputPrompt != null) {
            tf.setInputPrompt(inputPrompt);
        }
        tf.setImmediate(immediate);
        return tf;
    }

    /**
     * Add property bound text field.
     *
     * @param psi
     * @param expression
     * @param caption
     * @param prompt
     * @param layout
     * @return
     */
    public static TextField newTextField(PropertysetItem psi, String expression, String caption, String prompt, AbstractOrderedLayout layout) {
        TextField c = newTextField("", prompt, true);

        if (caption != null) {
            c.setCaption(caption);
        }

        if (psi != null && expression != null) {
            c.setPropertyDataSource(psi.getItemProperty(expression));
        }

        handleAddComponent(layout, c);

        return c;
    }

    public static Label newLabel(final String format, final Object... args) {
        Label l = new Label(UiBuilder.format(format, args));
        return l;
    }

    public static Label newLabel(final String format, final AbstractLayout layout, final Object... args) {
        Label l = UiBuilder.newLabel(format, args);
        handleAddComponent(layout, l);
        return l;
    }

//    public static Label newLabel(final LabelDTO label, final AbstractLayout layout) {
//        return newLabel(label.getFormat(), layout, LabelStyle.TEXT, label.getStyle(), label.getFormatArgs());
//    }
//
//    public static Label newLabel(final String format, final AbstractLayout layout, final LabelStyle style, final Object... args) {
//        Label l = UiBuilder.newLabel(format, args);
//        switch (style) {
//            case H1:
//                l.addStyleName(Oph.LABEL_H1);
//                break;
//            case H2:
//                l.addStyleName(Oph.LABEL_H2);
//                break;
//            case TEXT:
//                l.addStyleName(Oph.LABEL_SMALL);
//                break;
//
//        }
//        handleAddComponent(layout, l);
//        return l;
//    }

    /**
     * Create propertu bound label. Default style is "Oph.LABEL_SMALL".
     *
     * @param psi
     * @param expression
     * @param layout
     * @return
     */
    public static Label newLabel(PropertysetItem psi, String expression, AbstractOrderedLayout layout) {
        Label c = new Label();

        c.addStyleName(Oph.LABEL_SMALL);

        if (psi != null && expression != null) {
            c.setPropertyDataSource(psi.getItemProperty(expression));
        }

        handleAddComponent(layout, c);

        return c;
    }

    public static CheckBox newCheckbox(final String name, final AbstractLayout layout) {
        CheckBox checkBox;
        if (name != null) {
            checkBox = new CheckBox(name);
        } else {
            checkBox = new CheckBox();
        }

        checkBox.setImmediate(false);
        handleWidth(checkBox, DEFAULT_REALTIVE_SIZE);
        handleHeight(checkBox, DEFAULT_REALTIVE_SIZE);

        handleAddComponent(layout, checkBox);

        return checkBox;
    }

    /**
     * Create a checkbox with a caption and possibly bind to a property.
     *
     * @param caption
     * @param psi
     * @param expression
     * @param layout
     * @return
     */
    public static CheckBox addCheckBox(String caption, PropertysetItem psi, String expression, AbstractLayout layout) {
        CheckBox c = newCheckbox(caption, layout);

        // Bind
        if (psi != null && expression != null) {
            c.setPropertyDataSource(psi.getItemProperty(expression));
        }

        return c;
    }

    public static Label newLabel(final String name, final AbstractLayout layout) {
        Label label = new Label(name);

        label.setImmediate(false);
        handleWidth(label, DEFAULT_REALTIVE_SIZE);
        handleHeight(label, DEFAULT_REALTIVE_SIZE);

        handleAddComponent(layout, label);

        return label;
    }

    public static ComboBox newComboBox(final String name, final String[] items, final AbstractLayout layout) {
        ComboBox comboBox = new ComboBox();

        if (name != null) {
            comboBox.setCaption(name);
        }

        if (items != null) {
            for (String item : items) {
                comboBox.addItem(item);
            }
            comboBox.setValue(items[0]);
        }

        comboBox.setImmediate(false);
        comboBox.setNullSelectionAllowed(false);

        handleWidth(comboBox, DEFAULT_REALTIVE_SIZE);
        handleHeight(comboBox, DEFAULT_REALTIVE_SIZE);
        handleAddComponent(layout, comboBox);

        return comboBox;
    }

    public static TwinColSelect newTwinColSelect(final String name, final String[] items, final AbstractLayout layout) {
        TwinColSelect c = new TwinColSelect();
        if (name != null) {
            c.setCaption(name);
        }

        if (items != null) {
            for (String item : items) {
                c.addItem(item);
            }
        }

        c.setImmediate(false);

        handleWidth(c, DEFAULT_REALTIVE_SIZE);
        handleHeight(c, DEFAULT_REALTIVE_SIZE);
        handleAddComponent(layout, c);

        return c;
    }

    public static Button newButton(final String name, final AbstractLayout layout) {
        Button btn = new Button();
        btn.setCaption(name);
        btn.setImmediate(true);
        handleWidth(btn, DEFAULT_REALTIVE_SIZE);
        handleHeight(btn, DEFAULT_REALTIVE_SIZE);
        handleAddComponent(layout, btn);

        return btn;
    }

    public static Button newButtonSmallPrimary(final String name, final AbstractLayout layout) {
        return newButtonSmallPrimary(name, layout, null);
    }

    public static Button newButtonSmallPrimary(final String name, final AbstractLayout layout, ClickListener listener) {
        Button btn = listener == null ? new Button(name) : new Button(name, listener);
        btn.setImmediate(true);
        handleWidth(btn, DEFAULT_REALTIVE_SIZE);
        handleHeight(btn, DEFAULT_REALTIVE_SIZE);
        handleTheme(btn, Oph.BUTTON_DEFAULT);
        handleTheme(btn, Oph.BUTTON_SMALL);
        handleAddComponent(layout, btn);

        return btn;
    }

    public static Button newButtonSmallSecodary(final String name, final AbstractLayout layout) {
        Button btn = new Button();
        btn.setCaption(name);
        btn.setImmediate(true);
        handleWidth(btn, DEFAULT_REALTIVE_SIZE);
        handleHeight(btn, DEFAULT_REALTIVE_SIZE);
        handleTheme(btn, Oph.CONTAINER_SECONDARY);
        handleTheme(btn, Oph.BUTTON_SMALL);
        handleAddComponent(layout, btn);

        return btn;
    }

    public static Button newButtonSmallSecodary(final String name, final AbstractLayout layout, ClickListener listener) {
        Button btn = new Button(name, listener);
        btn.setImmediate(true);
        handleWidth(btn, DEFAULT_REALTIVE_SIZE);
        handleHeight(btn, DEFAULT_REALTIVE_SIZE);
        handleTheme(btn, Oph.CONTAINER_SECONDARY);
        handleTheme(btn, Oph.BUTTON_SMALL);
        handleAddComponent(layout, btn);

        return btn;
    }

    public static Panel newTextPanel(final String text, final String width, final String height, AbstractLayout layout) {
        Panel panel = newPanel(width, height, null, layout);
        panel.addComponent(new Label(text));

        return panel;
    }

//    public static Panel newTextPanel(final LabelDTO label, final String width, final String height, AbstractLayout layout) {
//        Panel panel = newPanel(width, height, null, layout);
//        panel.addComponent(new Label(label.getFormat()));
//
//        return panel;
//    }

    public static Panel newPanel() {
        return newPanel(DEFAULT_REALTIVE_SIZE, DEFAULT_REALTIVE_SIZE, null, null);
    }

    public static Panel newPanel(final String width, final String height, AbstractLayout panelContent, AbstractLayout addtoLayout) {
        Panel panel = new Panel();
        handleTheme(panel, Oph.CONTAINER_SECONDARY);
        handleWidth(panel, width);
        handleHeight(panel, height);

        if (panelContent != null) {
            //when layout param is null, the Panel uses vetical layout
            panel.setContent(panelContent);
        }

        handleAddComponent(addtoLayout, panel);

        return panel;
    }

    private static VerticalLayout newVerticalLayout(final String width, final String height, boolean margin) {
        VerticalLayout vlayout = new VerticalLayout();
        handleWidth(vlayout, width);
        handleHeight(vlayout, height);
        handleMarginParam(vlayout, new Boolean[]{margin});

        return vlayout;
    }

    public static VerticalLayout newVerticalLayout(final String width, final String height) {
        return newVerticalLayout(width, height, false);
    }

    public static VerticalLayout newVerticalLayout() {
        return newVerticalLayout(false, new Boolean[]{false});
    }

    public static CssLayout newCssLayout(final Boolean[] margin) {
        CssLayout layout = new CssLayout();
        //SET SPACING NOT AVAILABLE IN CSSLAYOUT CLASS
        handleWidth(layout, null);
        handleHeight(layout, null);
        handleMarginParam(layout, margin); //set padding size (18px in Raideer theme) , if needed.

        return layout;
    }

    public static CssLayout newCssLayout(final UiMarginEnum margin) {
        return newCssLayout(margin != null ? margin.getSelectedValue() : null);
    }

    public static VerticalLayout newVerticalLayout(boolean spacing, final Boolean[] margin) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(spacing); //set component spacing (12px in Raideer theme)
        handleWidth(layout, null);
        handleHeight(layout, null);
        handleMarginParam(layout, margin); //set padding size (18px in Raideer theme) , if needed.

        return layout;
    }

    public static VerticalLayout newVerticalLayout(boolean spacing, final UiMarginEnum margin) {
        return newVerticalLayout(spacing, margin != null ? margin.getSelectedValue() : null);
    }

    /*
     *
     * HORIZONTAL LAYOUT HELPER METHODS
     *
     */
    /**
     * Create new instance of HorizontalLayout. No spacing and not margin.
     *
     * @return HorizontalLayout instance
     */
    public static HorizontalLayout newHorizontalLayout() {
        return newHorizontalLayout(false, new Boolean[]{false});
    }

    /**
     * Create new instance of HorizontalLayout. Optional spacing and margin
     * parameters.
     *
     * @param boolean spacing
     * @param UiMarginEnum
     * @return HorizontalLayout instance
     */
    public static HorizontalLayout newHorizontalLayout(boolean spacing, final UiMarginEnum margin) {
        return newHorizontalLayout(spacing, margin != null ? margin.getSelectedValue() : null);
    }

    /**
     * Create new instance of HorizontalLayout. Optional spacing, margin, width
     * and height parameters.
     *
     * @param spacing
     * @param margin
     * @param String width
     * @param String height
     * @return HorizontalLayout instance
     */
    public static HorizontalLayout newHorizontalLayout(boolean spacing, final UiMarginEnum margin, final String width, final String height) {
        return newHorizontalLayout(spacing, margin != null ? margin.getSelectedValue() : null, width, height);
    }

    /**
     *
     *
     * @param String width, when set to null it uses a default value.
     * @param String height, when set to null it uses a default value.
     * @param Boolean Array, you can enable the margins only for specific sides
     * by array index: top[0], right[1], bottom[2], and left[3] margin.
     * @return New instance of HorizontalLayout.
     */
    private static HorizontalLayout newHorizontalLayout(boolean spacing, final Boolean[] margin, final String width, final String height) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setImmediate(false);
        layout.setSpacing(spacing); //set component spacing (12px in Raideer theme)
        handleWidth(layout, width);
        handleHeight(layout, height);
        handleMarginParam(layout, margin); //set padding size (18px in Raideer theme) , if needed.

        return layout;
    }

    private static HorizontalLayout newHorizontalLayout(boolean spacing, final Boolean[] margin) {
        //height is set to use relative size.
        return newHorizontalLayout(spacing, margin, null, DEFAULT_REALTIVE_SIZE);
    }

    /**
     * Create new tabsheet.
     *
     * @param layout
     * @return
     */
    public static TabSheet newTabSheet(AbstractOrderedLayout layout) {
        TabSheet tabs = new TabSheet();
        handleAddComponent(layout, tabs);
        return tabs;
    }

    /**
     * Create RichTextArea. Possibly bind to property.
     *
     * @param psi
     * @param expression
     * @param layout
     * @return
     */
    public static RichTextArea newRichTextArea(PropertysetItem psi, String expression, AbstractOrderedLayout layout) {

        RichTextArea rta = new RichTextArea();
        rta.setWidth(PCT100);

        // Bind to model
        if (psi != null && expression != null) {
            rta.setPropertyDataSource(psi.getItemProperty(expression));
        }

        handleAddComponent(layout, rta);

        return rta;
    }

    /**
     * Create new KoodistoComponent with ComboBox. Possible bind to a property.
     *
     * @param koodistoUri
     * @param psi
     * @param expression
     * @param prompt
     * @param layout
     * @return
     */
    public static KoodistoComponent newKoodistoComboBox(final String koodistoUri, PropertysetItem psi, String expression, String prompt, AbstractOrderedLayout layout) {

        // Koodisto displayed in ComboBox
        ComboBox combo = newComboBox(null, null, null);

        combo.setFilteringMode(AbstractSelect.Filtering.FILTERINGMODE_CONTAINS);
        if (prompt != null) {
            combo.setInputPrompt(prompt);
        }

        final KoodistoComponent c = WidgetFactory.create(koodistoUri);

        // Wire koodisto to combobox
        c.setField(combo);

        // Set to be immediate
        // c.setImmediate(true);

        // DISPLAYED text
        c.setCaptionFormatter(new CaptionFormatter() {
            @Override
            public String formatCaption(Object dto) {
                if (dto instanceof KoodiDTO) {
                    KoodiDTO kdto = (KoodiDTO) dto;
                    return kdto.getKoodiArvo();
                } else {
                    return "!KoodiDTO - Don't know how to format this: " + dto;
                }
            }
        });

        // BOUND value
        c.setFieldValueFormatter(new FieldValueFormatter() {
            @Override
            public Object formatFieldValue(Object dto) {
                if (dto instanceof KoodiDTO) {
                    KoodiDTO kdto = (KoodiDTO) dto;
                    return kdto.getKoodiUri();
                } else {
                    return "" + dto;
                }
            }
        });

        // Selected data bound there
        if (psi != null && expression != null) {
            c.setPropertyDataSource(psi.getItemProperty(expression));
        }

        handleAddComponent(layout, c);

        return c;
    }

    public static KoodistoComponent newKoodistoTwinColSelect(final String koodistoUri, PropertysetItem psi, String expression, AbstractOrderedLayout layout) {

        // Koodisto displayed in TwinColSelect
        TwinColSelect c = newTwinColSelect(null, null, null);

        // Only multiple (Set<String>) values allowed!
        c.setMultiSelect(true);

        final KoodistoComponent kc = WidgetFactory.create(koodistoUri);

        // Wire koodisto to combobox
        kc.setField(c);

        // Set to be immediate
        // kc.setImmediate(true);

        // DISPLAYED text
        kc.setCaptionFormatter(new CaptionFormatter() {
            @Override
            public String formatCaption(Object dto) {
                if (dto instanceof KoodiDTO) {
                    KoodiDTO kdto = (KoodiDTO) dto;
                    return kdto.getKoodiArvo();
                } else {
                    return "!KoodiDTO?: " + dto;
                }
            }
        });

        // BOUND value
        kc.setFieldValueFormatter(new FieldValueFormatter() {
            @Override
            public Object formatFieldValue(Object dto) {
                if (dto instanceof KoodiDTO) {
                    KoodiDTO kdto = (KoodiDTO) dto;
                    return kdto.getKoodiUri();
                } else {
                    return "" + dto;
                }
            }
        });

        // Selected data bound there
        if (psi != null && expression != null) {
            kc.setPropertyDataSource(psi.getItemProperty(expression));
        }

        handleAddComponent(layout, kc);

        return kc;
    }

    public static VerticalSplitPanel newHR(VerticalLayout _layout) {
        VerticalSplitPanel sp = new VerticalSplitPanel();
        sp.setLocked(true);
        sp.setHeight("2px");

        handleAddComponent(_layout, sp);

        return sp;
    }
}
