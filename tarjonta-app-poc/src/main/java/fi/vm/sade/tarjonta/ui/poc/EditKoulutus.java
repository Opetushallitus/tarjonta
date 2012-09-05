/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.vm.sade.tarjonta.ui.poc;

import fi.vm.sade.tarjonta.ui.poc.helper.UI;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO - give koulutus as a parameter and initialize
 *
 * @author mlyly
 */
public class EditKoulutus extends VerticalLayout {
    
    private static final Logger LOG = LoggerFactory.getLogger(EditKoulutus.class);
    
    public EditKoulutus() {
        super();
        LOG.info("EditKoulutus()");
        
        initialize();
    }
    
    private void initialize() {
        LOG.info("initialize()");
        
        // Info
        UI.newLabel("Olet luomassa {0} koulutusta organisaatioon {1}.", this, "tutkintoon johtavaa", "informaatiotekniikan tiedekunta");
        
        // Tabs
        TabSheet tabs = new TabSheet();
        addComponent(tabs);
        
        tabs.addTab(new EditKoulutusPerustiedot(), "Koulutuksen perustiedot (status)");
        tabs.addTab(new EditKoulutusKuvailevatTiedot(), "Koulutuksen kuvailevat tiedot (status)");
    }
    
}
