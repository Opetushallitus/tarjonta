/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.vm.sade.tarjonta.ui.poc;

import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * TODO
 * - give koulutus as a parameter and initialize form
 *
 * @author mlyly
 */
public class EditKoulutusForm extends VerticalLayout {
    
    private static final Logger log = LoggerFactory.getLogger(EditKoulutusForm.class);
    
    public EditKoulutusForm() {
        super();
        log.info("EditKoulutusForm()");
        
        initialize();
    }
    
    private void initialize() {
        log.info("initilize()");
        
        // Info
        UI.newLabel("Olet luomassa {0} koulutusta organisaatioon {1}.", this, "tutkintoon johtavaa", "informaatiotekniikan tiedekunta");
        
        // Tabs
        TabSheet tabs = new TabSheet();
        addComponent(tabs);
        
        //
        // Perustiedot Tab
        //
        VerticalLayout perustiedotLayout = UI.newVerticalLayout(null, null);
        tabs.addTab(perustiedotLayout, "Koulutuksen perustiedot (status)");
        
        // Top control buttons
        HorizontalLayout hlControlButtonsTop = UI.newHorizontalLayout(null, null);
        UI.newButton("Peruuta", hlControlButtonsTop);
        UI.newButton("Tallenna luonnoksena", hlControlButtonsTop);
        UI.newButton("Tallenna valmiina", hlControlButtonsTop);
        UI.newButton("Jatka", hlControlButtonsTop);
        perustiedotLayout.addComponent(hlControlButtonsTop);
        
        //
        // Perustiedot
        //
        GridLayout perustiedotGrid = new GridLayout(3, 30);
        Panel perustiedotPanel = new Panel("Koulutuksen perustiedot", perustiedotGrid);
        perustiedotLayout.addComponent(perustiedotPanel);

        
        // Bottom control buttons
        HorizontalLayout hlControlButtonsBottom = UI.newHorizontalLayout(null, null);
        UI.newButton("Peruuta", hlControlButtonsBottom);
        UI.newButton("Tallenna luonnoksena", hlControlButtonsBottom);
        UI.newButton("Tallenna valmiina", hlControlButtonsBottom);
        UI.newButton("Jatka", hlControlButtonsBottom);
        perustiedotLayout.addComponent(hlControlButtonsBottom);
        
        //
        // Lisätiedot tab
        //
        VerticalLayout lisatiedotLaoyout = UI.newVerticalLayout(null, null);
        tabs.addTab(lisatiedotLaoyout, "Koulutuksen kuvailevat tiedot (status)");
    }
    
    
    
    
}
