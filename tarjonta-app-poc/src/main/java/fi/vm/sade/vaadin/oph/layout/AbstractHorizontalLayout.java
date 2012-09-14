package fi.vm.sade.vaadin.oph.layout;

import com.vaadin.ui.HorizontalLayout;
import fi.vm.sade.vaadin.oph.enums.UiMarginEnum;
import fi.vm.sade.vaadin.oph.helper.ComponentUtil;

/**
 *
 * @author jani
 */
public abstract class AbstractHorizontalLayout extends HorizontalLayout {

    public AbstractHorizontalLayout() {
        super();

        init(false, UiMarginEnum.NONE, null, ComponentUtil.DEFAULT_REALTIVE_SIZE);
    }

    public AbstractHorizontalLayout(boolean spacing, UiMarginEnum margin) {
        super();

        init(spacing, margin, null, ComponentUtil.DEFAULT_REALTIVE_SIZE);
    }

    public AbstractHorizontalLayout(boolean spacing, UiMarginEnum margin, String width, String height) {
        super();

        init(spacing, margin, width, height);
    }

    protected void init(boolean spacing, UiMarginEnum margin, String width, String height) {
        this.setSpacing(spacing);
        ComponentUtil.handleWidth(this, width);
        ComponentUtil.handleHeight(this, height);
        ComponentUtil.handleMarginParam(this, margin != null ? margin.getSelectedValue() : null);
    }
}
