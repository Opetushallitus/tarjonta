/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.vm.sade.tarjonta.ui.model.view;

import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.tarjonta.ui.TarjontaPresenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author mlyly
 */
@Configurable
public class EditKoulutusView extends VerticalLayout {
    
    private static final Logger LOG = LoggerFactory.getLogger(EditKoulutusView.class);
    
    @Autowired(required=true)
    private TarjontaPresenter _presenter;
    
    public EditKoulutusView() {
        super();
        LOG.info("EditKoulutusView()");

        setSizeUndefined();
        setWidth("100%");
        setMargin(false, false, false, true);
        setSpacing(true);

        addComponent(new Label("Olet luomassa [tutkintoon johtavaa] koulutusta organisaatioon [informaatiotekniikan tiedekunta]"));
        
        TabSheet tabs = new TabSheet();
        tabs.addTab(new EditKoulutusPerustiedotView(), "Koulutuksen perustiedot (status)");
        tabs.addTab(new EditKoulutusKuvailevattiedotView(), "Koulutuksen kuvailevat tiedot (status)");

        addComponent(tabs);
    }
    
}
