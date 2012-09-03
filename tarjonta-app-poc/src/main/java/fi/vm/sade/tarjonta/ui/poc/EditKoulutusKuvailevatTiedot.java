/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.vm.sade.tarjonta.ui.poc;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mlyly
 */
public class EditKoulutusKuvailevatTiedot extends VerticalLayout {
    
    private static final Logger LOG = LoggerFactory.getLogger(EditKoulutusKuvailevatTiedot.class);
    
    public EditKoulutusKuvailevatTiedot() {
        super();
        initialize();
    }
    
    private void initialize() {
        LOG.info("initialize()");
        
        // Top control buttons
        HorizontalLayout hlControlButtonsTop = UI.newHorizontalLayout(null, null);
        UI.newButton("Peruuta", hlControlButtonsTop);
        UI.newButton("Tallenna luonnoksena", hlControlButtonsTop);
        UI.newButton("Tallenna valmiina", hlControlButtonsTop);
        UI.newButton("Jatka", hlControlButtonsTop);
        addComponent(hlControlButtonsTop);
        
        Panel kuvailevatTiedotPanel = new Panel("Koulutuksen kuvailevat tiedot");
        kuvailevatTiedotPanel.addComponent(loremIpsumLabel());
        addComponent(kuvailevatTiedotPanel);
        
        addDummyLisatiedotPanel("Tutkinno rakenne");
        addDummyLisatiedotPanel("koulutukselliset ja ammatilliset tavoitteet");
        addDummyLisatiedotPanel("Koulutuksen sisältö");
        addDummyLisatiedotPanel("Jatko-opintomahdollisuudet");
        addDummyLisatiedotPanel("Sijoittuminen työelämään");
        addDummyLisatiedotPanel("Kansainvälistyminen");
        addDummyLisatiedotPanel("Yhteistyö muiden toimijoiden kanssa");
        addDummyLisatiedotPanel("Lisätietoja");
        
        // Bottom control buttons
        HorizontalLayout hlControlButtonsBottom = UI.newHorizontalLayout(null, null);
        UI.newButton("Peruuta", hlControlButtonsBottom);
        UI.newButton("Tallenna luonnoksena", hlControlButtonsBottom);
        UI.newButton("Tallenna valmiina", hlControlButtonsBottom);
        UI.newButton("Jatka", hlControlButtonsBottom);
        addComponent(hlControlButtonsBottom);
    }
    
    private Label loremIpsumLabel() {
        return new Label("Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet. "
                + "Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet. Lorem ipsum"
                + " dolor sit amet. Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet."
                + " Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet. ");
    }

    private void addDummyLisatiedotPanel(String caption) {
        Panel p = new Panel(caption);
        p.addComponent(loremIpsumLabel());
        
        TabSheet tab = new TabSheet();
        String[] langs = {"Suomi", "Ruotsi", "Englanti"};
        for (String lang : langs) {
            RichTextArea rta = new RichTextArea();
            rta.setWidth("100%");
            tab.addTab(rta, lang);
        }
        p.addComponent(tab);
        
        addComponent(p);
    }
    
    
}
