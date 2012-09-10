package fi.vm.sade.vaadin.oph.layout;

import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import fi.vm.sade.vaadin.oph.enums.UiMarginEnum;
import fi.vm.sade.vaadin.oph.helper.ComponentUtil;
import fi.vm.sade.vaadin.oph.helper.UiBuilder;

/**
 *
 * @author jani
 */
public abstract class AbstractWindow<T extends AbstractLayout> extends Window implements Window.CloseListener {

    private AbstractLayout windowLayout;

    public AbstractWindow(String label) {
        super(label);
        init("75%", ComponentUtil.DEFAULT_REALTIVE_SIZE, true, null, true);
    }
    
    public AbstractWindow(String label, boolean doLayout) {
        super(label);
       init("75%", ComponentUtil.DEFAULT_REALTIVE_SIZE, true, null, doLayout);
    }

    public AbstractWindow(String label, String width, String height, boolean modal) {
        super(label);
        init(width, height, modal, null, true);
    }

    public AbstractWindow(String label, String width, String height, boolean modal, T layout) {
        super(label);
        init(width, height, modal, layout, true);
    }

    protected void init(String width, String height, boolean modal, T layout, boolean doLayout) {
        ComponentUtil.handleWidth(this, width);
        ComponentUtil.handleHeight(this, height);
        center();
        setModal(true);

        if (layout == null) {
            windowLayout = UiBuilder.newVerticalLayout(true, UiMarginEnum.NONE);
        } else {
            windowLayout = (T) layout;
        }

        windowLayout.setSizeFull();
        this.addComponent(windowLayout);

        if (doLayout) {
            buildOrder(windowLayout);
        }
    }

    protected void buildOrder(AbstractLayout layout) {
        buildLayout(layout);
    }

    public abstract void buildLayout(AbstractLayout layout);

    @Override
    public void windowClose(CloseEvent e) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public void fullContentHeight() {
        getContent().setHeight("100%");
    }

    public AbstractLayout getLayout() {
        return windowLayout;
    }

    public void test() {
    }
}
