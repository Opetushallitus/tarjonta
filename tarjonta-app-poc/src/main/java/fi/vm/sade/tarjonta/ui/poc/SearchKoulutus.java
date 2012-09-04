/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.vm.sade.tarjonta.ui.poc;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Search component to search for Koulutus info.
 *
 * @author mlyly
 */
public class SearchKoulutus extends VerticalLayout {
    
    private static final Logger LOG = LoggerFactory.getLogger(SearchKoulutus.class);
    
    private HorizontalLayout layout;
    private TextField tfSearch;
    private NativeSelect cbHakukausi;
    private NativeSelect cbKoulutuksenAlkamiskausi;
    private NativeSelect cbHakutapa;
    private NativeSelect cbHakutyyppi;
    private NativeSelect cbHaunKohdejoukko;
    private Button btnTyhjenna;
    
    public SearchKoulutus() {
        super();
        setSizeUndefined();
        initialize();
    }
    
    private void initialize() {
        LOG.info("initialize()");
        
        layout = UI.newHorizontalLayout(null, null);
        addComponent(layout);
        
        // Search field
        tfSearch = UI.newTextField("", "Hae koulutuksia", true);
        layout.addComponent(tfSearch);

        tfSearch.addListener(new Property.ValueChangeListener() {
            
            @Override
            public void valueChange(ValueChangeEvent event) {
                LOG.error("valueChange() - do the search: {}", event.getProperty().getValue());
            }
        });
        
        
        cbHakukausi = UI.newCompobox("Hakukausi", new String[]{"Kevätkausi"}, layout);
        cbKoulutuksenAlkamiskausi = UI.newCompobox("Koulutuksen alkamiskausi", new String[]{"Syksy 2012"}, layout);
        cbHakutapa = UI.newCompobox("Hakutapa", new String[]{"Kaikki"}, layout);
        cbHakutyyppi = UI.newCompobox("Hakutyyppi", new String[]{"Kaikki"}, layout);
        cbHaunKohdejoukko = UI.newCompobox("Kohdejoukko", new String[]{"Kaikki"}, layout);
        btnTyhjenna = UI.newButton("Tyhjennä", layout);
    }
    
}
