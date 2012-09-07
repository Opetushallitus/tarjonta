/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.vm.sade.tarjonta.ui.poc;

import fi.vm.sade.vaadin.oph.helper.UiBuilder;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
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
    private ComboBox cbHakukausi;
    private ComboBox cbKoulutuksenAlkamiskausi;
    private ComboBox cbHakutapa;
    private ComboBox cbHakutyyppi;
    private ComboBox cbHaunKohdejoukko;
    private Button btnTyhjenna;
    
    public SearchKoulutus() {
        super();
        setSizeUndefined();
        initialize();
    }
    
    private void initialize() {
        LOG.info("initialize()");
        
        layout = UiBuilder.newHorizontalLayout();
        addComponent(layout);
        
        // Search field
        tfSearch = UiBuilder.newTextField("", "Hae koulutuksia", true);
        layout.addComponent(tfSearch);

        tfSearch.addListener(new Property.ValueChangeListener() {
            
            @Override
            public void valueChange(ValueChangeEvent event) {
                LOG.error("valueChange() - do the search: {}", event.getProperty().getValue());
            }
        });
        
        
        cbHakukausi = UiBuilder.newComboBox("Hakukausi", new String[]{"Kevätkausi"}, layout);
        cbKoulutuksenAlkamiskausi = UiBuilder.newComboBox("Koulutuksen alkamiskausi", new String[]{"Syksy 2012"}, layout);
        cbHakutapa = UiBuilder.newComboBox("Hakutapa", new String[]{"Kaikki"}, layout);
        cbHakutyyppi = UiBuilder.newComboBox("Hakutyyppi", new String[]{"Kaikki"}, layout);
        cbHaunKohdejoukko = UiBuilder.newComboBox("Kohdejoukko", new String[]{"Kaikki"}, layout);
        btnTyhjenna = UiBuilder.newButton("Tyhjennä", layout);
    }
    
}
