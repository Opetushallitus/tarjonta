/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.vm.sade.tarjonta.ui.poc;

import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mlyly
 */
public class EditKoulutusPerustiedot extends VerticalLayout {
    
    private static final Logger LOG = LoggerFactory.getLogger(EditKoulutusPerustiedot.class);
    
    public EditKoulutusPerustiedot() {
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
        
        //
        // Perustiedot
        //
        GridLayout perustiedotGrid = new GridLayout(3, 30);
        Panel perustiedotPanel = new Panel("Koulutuksen perustiedot", perustiedotGrid);
        addComponent(perustiedotPanel);
        
        
        // Bottom control buttons
        HorizontalLayout hlControlButtonsBottom = UI.newHorizontalLayout(null, null);
        UI.newButton("Peruuta", hlControlButtonsBottom);
        UI.newButton("Tallenna luonnoksena", hlControlButtonsBottom);
        UI.newButton("Tallenna valmiina", hlControlButtonsBottom);
        UI.newButton("Jatka", hlControlButtonsBottom);
        addComponent(hlControlButtonsBottom);
    }
    
}
