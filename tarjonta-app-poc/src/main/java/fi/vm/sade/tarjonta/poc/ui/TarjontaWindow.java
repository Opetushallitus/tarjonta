package fi.vm.sade.tarjonta.poc.ui;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import fi.vm.sade.tarjonta.poc.ui.view.MainSplitPanelView;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author jani
 */
@Configurable(preConstruction = true)
public class TarjontaWindow extends Window {

    private static final Logger LOG = LoggerFactory.getLogger(TarjontaWindow.class);
    @Autowired(required = true)
    private TarjontaPresenter _presenter;
    private MainSplitPanelView mainSplitPanel;

    public TarjontaWindow() {
        super();
        LOG.info("TarjontaWindow(): {}", _presenter);

        _presenter.setTarjontaWindow(this);

        VerticalLayout layout = UiUtil.verticalLayout();
        layout.setHeight(-1,UNITS_PIXELS);
        setContent(layout); //window käyttää layouttia pohjana
        layout.addStyleName(Oph.CONTAINER_MAIN);

        mainSplitPanel = new MainSplitPanelView();
        mainSplitPanel.getMainRightLayout().addComponent(new Label("NOT INITIALIZED"));

        layout.addComponent(mainSplitPanel);

        if (_presenter != null && _presenter.showIdentifier()) {
            layout.addComponent(new Label("ID=" + _presenter.getIdentifier()));
        }

       // layout.setExpandRatio(mainSplitPanel, 1f);

        _presenter.showMainKoulutusView();
    }

    public MainSplitPanelView getMainSplitPanel() {
        return mainSplitPanel;
    }
}
