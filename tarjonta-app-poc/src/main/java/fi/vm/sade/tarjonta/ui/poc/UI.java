package fi.vm.sade.tarjonta.ui.poc;

import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import fi.oph.Oph;

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

        layout.addComponent(ns);

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

        layout.addComponent(btn);

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

    public static VerticalLayout newVerticalLayout(final String width, final String height) {
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
        vlayout.setMargin(false);

        return vlayout;
    }

    public static HorizontalLayout newHorizontalLayout(final String width, final String height) {
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
        hlayout.setMargin(false);

        return hlayout;
    }
    
    public static boolean isThemeOPH(){
        return THEME.equals(Layout.OPH);
    }
}
