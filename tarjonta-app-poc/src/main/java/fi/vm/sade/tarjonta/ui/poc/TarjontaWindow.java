package fi.vm.sade.tarjonta.ui.poc;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import fi.vm.sade.tarjonta.ui.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.model.view.CreateKoulutusView;
import fi.vm.sade.tarjonta.ui.model.view.MainSplitPanelView;
import fi.vm.sade.tarjonta.ui.poc.helper.UI;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author jani
 */
@Configurable(preConstruction=true)
public class TarjontaWindow extends Window {
    
    private static final Logger LOG = LoggerFactory.getLogger(TarjontaWindow.class);
    
    @Autowired
    private TarjontaPresenter _presenter;
    
    private MainSplitPanelView main;

    private ClickListener btnClickListener = new Button.ClickListener() {
        @Override
        public void buttonClick(ClickEvent event) {
            new CreateKoulutusView("Valitse organisaatio", main.getMainRightLayout(), getWindow());
        }
    };

    public TarjontaWindow() {
        super();
        LOG.info("TarjontaWindow(): {}", _presenter);
        
        VerticalLayout layout = UI.newVerticalLayout(null, null);
        setContent(layout); //window käyttää laypottia pohjana

        main = new MainSplitPanelView();
        main.setBtnLuoUusiKoulutus(btnClickListener);
        addComponent(main);        
    }
    
    @PostConstruct
    private void initialize() {
        LOG.info("initialize(): presenter={}", _presenter);
    }
    
}
