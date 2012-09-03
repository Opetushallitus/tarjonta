package fi.vm.sade.tarjonta.ui.poc;

import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import fi.oph.Oph;
import java.text.MessageFormat;

/**
 *
 * @author jani
 */
public class UI {

    public static final String PCT100 = "100.0%";


    private enum Layout {

        REINDEER, OPH;
    }

    private static final Layout THEME = Layout.REINDEER;
    
    public static final String format(String format, Object... args) {
        return MessageFormat.format(format, args);
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
    
    public static NativeSelect newCompobox(final String name, final String[] items, final AbstractOrderedLayout layout) {
        NativeSelect ns = new NativeSelect(name);
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

    public static Button newButton(final String name, final AbstractOrderedLayout layout) {
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

    public static Panel newPanel(final String width, final String height, AbstractOrderedLayout customLayout) {
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
