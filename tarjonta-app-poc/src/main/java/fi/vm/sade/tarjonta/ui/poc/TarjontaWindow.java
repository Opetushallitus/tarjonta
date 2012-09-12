package fi.vm.sade.tarjonta.ui.poc;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import fi.vm.sade.tarjonta.ui.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.model.KoulutusPerustiedotDTO;
import fi.vm.sade.tarjonta.ui.model.view.CreateKoulutusView;
import fi.vm.sade.tarjonta.ui.model.view.EditKoulutusView;
import fi.vm.sade.tarjonta.ui.model.view.EditSiirraUudelleKaudelleView;
import fi.vm.sade.tarjonta.ui.model.view.MainKoulutusView;
import fi.vm.sade.tarjonta.ui.model.view.MainResultView;
import fi.vm.sade.tarjonta.ui.model.view.MainSearchView;
import fi.vm.sade.tarjonta.ui.model.view.MainSplitPanelView;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.oph.demodata.DataSource;
import fi.vm.sade.vaadin.oph.demodata.row.MultiActionTableStyle;
import fi.vm.sade.vaadin.oph.helper.UiBuilder;
import fi.vm.sade.vaadin.oph.layout.AbstractDialogWindow;
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

    @Autowired(required=true)
    private TarjontaPresenter _presenter;

    private MainSplitPanelView mainSplitPanel;
    private MainSearchView mainSearch;
    private MainResultView mainResult;
    private AbstractDialogWindow mainModalWindow;

    private MainKoulutusView mainKoulutusView;

    public TarjontaWindow() {
        super();
        LOG.info("TarjontaWindow(): {}", _presenter);

        _presenter.setTarjontaWindow(this);

        VerticalLayout layout = UiBuilder.newVerticalLayout();
        layout.setSizeFull();
        setContent(layout); //window käyttää layouttia pohjana
        layout.addStyleName(Oph.CONTAINER_MAIN);

        mainSplitPanel = new MainSplitPanelView();
        mainSplitPanel.getMainRightLayout().addComponent(new Label("NOT INITIALIZED"));

        layout.addComponent(mainSplitPanel);

        if (_presenter != null && _presenter.showIdentifier()) {
            layout.addComponent(new Label("ID=" + _presenter.getIdentifier()));
        }

        layout.setExpandRatio(mainSplitPanel, 1f);

        _presenter.showMainSearchView();
    }

    public MainSplitPanelView getMainSplitPanel() {
        return mainSplitPanel;
    }


}
