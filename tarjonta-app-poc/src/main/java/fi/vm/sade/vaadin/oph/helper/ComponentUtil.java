package fi.vm.sade.vaadin.oph.helper;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractLayout;
import fi.vm.sade.vaadin.Oph;

/**
 *
 * @author jani
 */
public class ComponentUtil {

    private static final Layout THEME = Layout.OPH;
    public static final String PCT100 = "100%";
    public static final String DEFAULT_REALTIVE_SIZE = "-1px";
    public static final String UNDEFINED_TEXT = "UNDEFINED";
    public static final String RESOURCE_URL_OPH = "../../themes/oph/";
    public static final String[] STYLE_BUTTON_SECONDARY = new String[]{Oph.CONTAINER_SECONDARY, Oph.BUTTON_SMALL};
    public static final String[] STYLE_BUTTON_PRIMARY = new String[]{Oph.CONTAINER_SECONDARY, Oph.BUTTON_SMALL};

    private enum Layout {

        REINDEER, OPH;
    }

    //OTHER METHODS
    public static boolean isThemeOPH() {
        return THEME.equals(Layout.OPH);
    }

    public static AbstractLayout handleMarginParam(AbstractLayout layout, final Boolean[] margin) {
        if (margin != null && margin.length == 1) {
            layout.setMargin(margin[0]);
        } else if (margin != null && margin.length >= 4) {
            layout.setMargin(margin[0], margin[1], margin[2], margin[3]);
        } else {
            layout.setMargin(false);
        }

        return layout;
    }

    public static AbstractComponent handleHeight(AbstractComponent component, final String height) {
        if (height != null && height.equals(DEFAULT_REALTIVE_SIZE)) {
            component.setHeight(-1, Sizeable.UNITS_PIXELS);
        } else if (height != null) {
            //set CSS value
            component.setHeight(height);
        } else {
            component.setHeight(100, Sizeable.UNITS_PERCENTAGE);
        }

        return component;
    }

    public static AbstractComponent handleWidth(AbstractComponent component, final String width) {

        if (width != null && width.equals(DEFAULT_REALTIVE_SIZE)) {
            component.setWidth(-1, Sizeable.UNITS_PIXELS);
        } else if (width != null) {
            //set CSS value
            component.setWidth(width);
        } else {
            component.setWidth(100, Sizeable.UNITS_PERCENTAGE);
        }

        return component;
    }

    public static void handleTheme(AbstractComponent layout, final String styleName) {
        if (isThemeOPH()) {
            layout.addStyleName(styleName);
        }
    }

    public static void handleAddComponent(AbstractLayout layout, final AbstractComponent component) {
        if (layout != null) {
            layout.addComponent(component);
        }
    }
}
