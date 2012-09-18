package fi.vm.sade.tarjonta.ui.model.view;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.tarjonta.ui.TarjontaPresenter;
import fi.vm.sade.vaadin.oph.helper.UiBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable(preConstruction = true)
public class MainSplitPanelView extends VerticalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(MainSplitPanelView.class);
    private static final int DEFAULT_SPLIT_PCT = 0;
    @Autowired(required = true)
    private TarjontaPresenter _presenter;
    private HorizontalLayout mainLeftLayout;
    private VerticalLayout mainRightLayout;

    /**
     * The constructor should first build the main layout, set the composition
     * root and then do any custom initialization.
     */
    public MainSplitPanelView() {
        LOG.info("In MainSplitPanelView");
        buildMainLayout();
        mainRightLayout.setHeight(-1, UNITS_PIXELS);
    }

    private void buildMainLayout() {
        //INIT SPLIT PANEL
        //setSplitPosition(DEFAULT_SPLIT_PCT); // percent
        mainLeftLayout = UiBuilder.newHorizontalLayout(); //split panel right
        mainRightLayout = UiBuilder.newVerticalLayout(); //Split panel left
        mainRightLayout.setHeight(-1, UNITS_PIXELS);
        this.addComponent(mainLeftLayout);
        this.addComponent(mainRightLayout);

        //LEFT LAYOUT IN SPLIT PANEL
       // UiBuilder.newLabel("Organisaation valinta tähän", mainLeftLayout);
    }

    /**
     * @return the mainRightLayout
     */
    public VerticalLayout getMainRightLayout() {
        return mainRightLayout;
    }

    /**
     * @param mainRightLayout the mainRightLayout to set
     */
    public void setMainRightLayout(VerticalLayout mainRightLayout) {
        this.mainRightLayout = mainRightLayout;
    }
}
