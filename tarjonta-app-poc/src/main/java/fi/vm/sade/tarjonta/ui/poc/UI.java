package fi.vm.sade.tarjonta.ui.poc;

import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.vaadin.Oph;
import java.text.MessageFormat;

/**
 *
 * @author jani
 */
public class UI {

    public static final String PCT100 = "100%";
    public static final String LOREM_IPSUM_SHORT = "Lorem ipsum dolor sit amet, consectetur "
            + "adipiscing elit. Ut ut massa eget erat dapibus sollicitudin. Vestibulum ante ipsum "
            + "primis in faucibus orci luctus et ultrices posuere cubilia Curae; Pellentesque a "
            + "augue. Praesent non elit. Duis sapien dolor, cursus eget, pulvinar eget, eleifend a, "
            + "est. Integer in nunc. Vivamus consequat ipsum id sapien. Duis eu elit vel libero "
            + "posuere luctus. Aliquam ac turpis. Aenean vitae justo in sem iaculis pulvinar. "
            + "Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus "
            + "mus. Aliquam sit amet mi. "
            + "<br/>"
            + "Aenean auctor, mi sit amet ultricies pulvinar, dui urna adipiscing odio, ut "
            + "faucibus odio mauris eget justo.";

    public static final String[] KULTTURIALA = {
        "Käsi- ja taideteollisuusalan perustutkinto - Artesaani",
        "Tuotteen suunnittelu ja valmistamisen koulutusohjelma",
        "Ympäristön suunnittelun ja rakentamisen koulutuohjelma"};
    public static final String[] TEKNIIIKAN_JA_LIIKENTEEN_ALA = {
        "Autoala perustutkinto - Parturi-kampaajan, syksy 2012",
        "Sähkä- ja automaatitekniikan perustutkinto",
        "Tieto- ja tietliikenneteksniikan perustutkinto",
        "Kone- ja metallialan perustutkinto - ICT-asentaja",
        "Kone- ja metallialan perustutkinto - Koneistaja"};
    
    private enum Layout {

        REINDEER, OPH;
    }
    private static final Layout THEME = Layout.OPH;

    public static final String format(String format, Object... args) {
        return MessageFormat.format(format, args);
    }

    public static DateField newDate() {
        return UI.newDate(null, null, null);
    }

    public static DateField newDate(String caption, String dateFormat, AbstractOrderedLayout layout) {
        DateField df = new DateField();
        df.setDateFormat(dateFormat != null ? dateFormat : "dd.MM.yyyy");
        if (layout != null) {
            layout.addComponent(df);
        }
        return df;
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
        Label l = new Label(UI.format(format, args));
        return l;
    }

    public static Label newLabel(final String format, final AbstractOrderedLayout layout, final Object... args) {
        Label l = UI.newLabel(format, args);
        if (layout != null) {
            layout.addComponent(l);
        }
        return l;
    }

    public static CheckBox newCheckbox(final String name, final AbstractLayout layout) {
        CheckBox ns = new CheckBox(name);

        ns.setImmediate(false);
        ns.setWidth("-1px");
        ns.setHeight("-1px");

        if (layout != null) {
            layout.addComponent(ns);
        }

        return ns;
    }

    public static Label newLabel(final String name, final AbstractLayout layout) {
        Label ns = new Label(name);

        ns.setImmediate(false);
        ns.setWidth("-1px");
        ns.setHeight("-1px");

        if (layout != null) {
            layout.addComponent(ns);
        }

        return ns;
    }

    public static ComboBox newCompobox(final String name, final String[] items, final AbstractLayout layout) {
        ComboBox ns = new ComboBox(name);
        for (String item : items) {
            ns.addItem(item);
        }
        ns.setImmediate(false);
        ns.setWidth("-1px");
        ns.setHeight("-1px");

        ns.setNullSelectionAllowed(false);
        ns.setValue(items[0]);

        if (layout != null) {
            layout.addComponent(ns);
        }

        return ns;
    }

    public static Button newButton(final String name, final AbstractLayout layout) {
        Button btn = new Button();
        btn.setCaption(name);
        btn.setImmediate(true);
        btn.setWidth("-1px");
        btn.setHeight("-1px");

        if (isThemeOPH()) {
            btn.addStyleName(Oph.BUTTON_DEFAULT);
        }

        if (layout != null) {
            layout.addComponent(btn);
        }

        return btn;
    }

    public static Panel newPanel(final String width, final String height, AbstractLayout customLayout) {
        Panel panel = new Panel();
        if (isThemeOPH()) {
            panel.addStyleName(Oph.CONTAINER_SECONDARY);
        }

        if (customLayout != null) {
            //when layout param is null, the Panel uses vetical layout
            panel.setContent(customLayout);
        }

        panel.setImmediate(false);
        if (width != null) {
            panel.setWidth(width);
        } else {
            panel.setWidth(PCT100);
        }

        if (height != null) {
            panel.setHeight(height);
        } else {
            panel.setHeight(PCT100);
        }

        return panel;
    }

    public static VerticalLayout newVerticalLayout(final String width, final String height, boolean margin) {
        VerticalLayout vlayout = new VerticalLayout();
        vlayout.setImmediate(false);
        if (width != null) {
            vlayout.setWidth(width);
        } else {
            vlayout.setWidth(PCT100);
        }

        if (height != null) {
            vlayout.setHeight(height);
        } else {
            vlayout.setHeight(PCT100);
        }
        vlayout.setMargin(margin);

        return vlayout;
    }

    public static VerticalLayout newVerticalLayout(final String width, final String height) {
        return newVerticalLayout(width, height, false);
    }

    public static HorizontalLayout newHorizontalLayout(final String width, final String height, Boolean margin) {
        return newHorizontalLayout(width, height, new Boolean[]{margin});
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
    public static HorizontalLayout newHorizontalLayout(final String width, final String height, Boolean[] margin) {
        HorizontalLayout hlayout = new HorizontalLayout();
        hlayout.setImmediate(false);
        if (width != null) {
            hlayout.setWidth(width);
        } else {
            hlayout.setWidth(PCT100);
        }

        if (height != null) {
            hlayout.setHeight(height);
        } else {
            hlayout.setHeight("-1px");
        }

        if (margin != null && margin.length == 1) {
            hlayout.setMargin(margin[0]);
        } else if (margin != null && margin.length >= 4) {
            hlayout.setMargin(margin[0], margin[1], margin[2], margin[3]);
        } else {
            hlayout.setMargin(false);
        }

        return hlayout;
    }

    public static HorizontalLayout newHorizontalLayout(final String width, final String height) {
        return newHorizontalLayout(width, height, false);
    }

    public static boolean isThemeOPH() {
        return THEME.equals(Layout.OPH);
    }
}
