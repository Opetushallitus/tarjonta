package fi.vm.sade.tarjonta.ui.poc;

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

    private ClickListener btnClickListener;

    public TarjontaWindow() {
        super();
        VerticalLayout layout = UI.newVerticalLayout(null, null);
        setContent(layout); //window käyttää laypottia pohjana

        MainSplitPanel main = new MainSplitPanel();
        main.setBtnLuoUusiKoulutus(btnClickListener);
        addComponent(main);
        
        btnClickListener = new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {

                new WindowOpener("Valitse organisaatio", null, getWindow());
            }
        };
    }
}
