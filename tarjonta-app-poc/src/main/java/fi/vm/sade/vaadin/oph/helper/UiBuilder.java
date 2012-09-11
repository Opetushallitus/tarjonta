package fi.vm.sade.vaadin.oph.helper;

import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.vaadin.Oph;
import java.text.MessageFormat;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TabSheet;
import fi.vm.sade.vaadin.oph.enums.UiMarginEnum;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.ui.model.view.EditKoulutusKuvailevattiedotView;
import fi.vm.sade.vaadin.oph.dto.LabelDTO;
import fi.vm.sade.vaadin.oph.enums.LabelStyle;

/**
 *
 * @author jani
 */
public class UiBuilder extends ComponentUtil {

    public static final String format(String format, Object... args) {
        return MessageFormat.format(format, args);
    }

    public static DateField newDate() {
        return UiBuilder.newDate(null, null, null);
    }

    public static DateField newDate(String caption, String dateFormat, AbstractLayout layout) {
        DateField df = new DateField();
        df.setDateFormat(dateFormat != null ? dateFormat : "dd.MM.yyyy");
        if (layout != null) {
            layout.addComponent(df);
        }
        return df;
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

    public static Label newLabel(final String format, final Object... args) {
        Label l = new Label(UiBuilder.format(format, args));
        return l;
    }

    public static Label newLabel(final String format, final AbstractLayout layout, final Object... args) {
        Label l = UiBuilder.newLabel(format, args);
        handleAddComponent(layout, l);
        return l;
    }

    public static Label newLabel(final LabelDTO label, final AbstractLayout layout) {
        return newLabel(label.getFormat(), layout, LabelStyle.TEXT, label.getStyle(), label.getFormatArgs());
    }

    public static Label newLabel(final String format, final AbstractLayout layout, final LabelStyle style, final Object... args) {
        Label l = UiBuilder.newLabel(format, args);
        switch (style) {
            case H1:
                l.addStyleName(Oph.LABEL_H1);
                break;
            case H2:
                l.addStyleName(Oph.LABEL_H2);
                break;
            case TEXT:
                l.addStyleName(Oph.LABEL_SMALL);
                break;

        }
        handleAddComponent(layout, l);
        return l;
    }

    public static CheckBox newCheckbox(final String name, final AbstractLayout layout) {
        CheckBox checkBox = new CheckBox(name);

        checkBox.setImmediate(false);
        handleWidth(checkBox, DEFAULT_REALTIVE_SIZE);
        handleHeight(checkBox, DEFAULT_REALTIVE_SIZE);

        handleAddComponent(layout, checkBox);

        return checkBox;
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
        ComboBox comboBox = new ComboBox(name);
        for (String item : items) {
            comboBox.addItem(item);
        }
        comboBox.setImmediate(false);
        comboBox.setNullSelectionAllowed(false);
        comboBox.setValue(items[0]);

        handleWidth(comboBox, DEFAULT_REALTIVE_SIZE);
        handleHeight(comboBox, DEFAULT_REALTIVE_SIZE);
        handleAddComponent(layout, comboBox);

        return comboBox;
    }

    public static Button newButton(final String name, final AbstractLayout layout) {
        Button btn = new Button();
        btn.setCaption(name);
        btn.setImmediate(true);
        handleWidth(btn, DEFAULT_REALTIVE_SIZE);
        handleHeight(btn, DEFAULT_REALTIVE_SIZE);
        handleTheme(btn, Oph.BUTTON_DEFAULT);
        handleAddComponent(layout, btn);

        return btn;
    }

    public static Panel newTextPanel(final String text, final String width, final String height, AbstractLayout layout) {
        Panel panel = newPanel(width, height, null, layout);
        panel.addComponent(new Label(text));

        return panel;
    }

    public static Panel newTextPanel(final LabelDTO label, final String width, final String height, AbstractLayout layout) {
        Panel panel = newPanel(width, height, null, layout);
        panel.addComponent(new Label(label.getFormat()));

        return panel;
    }

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

        if (layout != null) {
            layout.addComponent(tabs);
        }

        return tabs;
    }

    /**
     * Create RichTextArea.
     *
     * @param psi
     * @param expression
     * @param layout
     * @return
     */
    public static RichTextArea newRichTextArea(PropertysetItem psi, String expression, AbstractOrderedLayout layout) {

        RichTextArea rta = new RichTextArea();

        // Bind to model
        if (psi != null && expression != null) {
            rta.setPropertyDataSource(psi.getItemProperty(expression));
        }

        if (layout != null) {
            layout.addComponent(rta);
        }

        return rta;
    }
}
