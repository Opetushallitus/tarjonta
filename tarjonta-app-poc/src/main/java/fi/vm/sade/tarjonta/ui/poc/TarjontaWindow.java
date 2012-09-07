package fi.vm.sade.tarjonta.ui.poc;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import fi.vm.sade.tarjonta.ui.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.model.view.CategoryTreeView;
import fi.vm.sade.tarjonta.ui.model.view.CreateKoulutusView;
import fi.vm.sade.tarjonta.ui.model.view.MainResultView;
import fi.vm.sade.tarjonta.ui.model.view.MainSearchView;
import fi.vm.sade.tarjonta.ui.model.view.MainSplitPanelView;
import fi.vm.sade.vaadin.oph.helper.UiBuilder;
import javax.annotation.PostConstruct;
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
    @Autowired
    private TarjontaPresenter _presenter;
    private MainSplitPanelView main;
    private MainSearchView mainSearch;
    private MainResultView mainResult;
    private CreateKoulutusView mainDialog;
    private ClickListener btnClickListener = new Button.ClickListener() {
        @Override
        public void buttonClick(ClickEvent event) {
            mainDialog = new CreateKoulutusView("Valitse organisaatio", main.getMainRightLayout(), getWindow());
        }
    };

    public TarjontaWindow() {
        super();
        LOG.info("TarjontaWindow(): {}", _presenter);

        VerticalLayout layout = UiBuilder.newVerticalLayout();
        setContent(layout); //window käyttää layouttia pohjana

        mainSearch = new MainSearchView();
        mainResult = new MainResultView();
        mainResult.setBtnLuoUusiKoulutus(btnClickListener);

        main = new MainSplitPanelView();
        main.getMainRightLayout().addComponent(mainSearch);
        main.getMainRightLayout().addComponent(mainResult);        
    
        addComponent(main);

    }
}
