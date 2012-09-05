package fi.vm.sade.tarjonta.ui.poc;

import fi.vm.sade.tarjonta.ui.model.view.CreateKoulutusView;
import fi.vm.sade.tarjonta.ui.poc.helper.UI;
import fi.vm.sade.tarjonta.ui.model.view.MainSplitPanelView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 *
 * @author jani
 */
public class TarjontaWindow extends Window {

    private MainSplitPanelView main;
    private ClickListener btnClickListener = new Button.ClickListener() {
        @Override
        public void buttonClick(ClickEvent event) {
            new CreateKoulutusView("Valitse organisaatio", main.getMainRightLayout(), getWindow());
        }
    };

    public TarjontaWindow() {
        super();
        VerticalLayout layout = UI.newVerticalLayout(null, null);
        setContent(layout); //window käyttää laypottia pohjana

        main = new MainSplitPanelView();
        main.setBtnLuoUusiKoulutus(btnClickListener);
        addComponent(main);
    }
}
