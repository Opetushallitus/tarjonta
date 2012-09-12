/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.vm.sade.tarjonta.ui.model.view;

import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
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
public class EditKoulutusView extends Panel {

    private static final Logger LOG = LoggerFactory.getLogger(EditKoulutusView.class);
    @Autowired(required = true)
    private TarjontaPresenter _presenter;

    public EditKoulutusView() {
        super();
        LOG.info("EditKoulutusView()");

        addComponent(new Label("Olet luomassa [tutkintoon johtavaa] koulutusta organisaatioon [informaatiotekniikan tiedekunta]"));
        setSizeFull();
        setScrollable(true);

        TabSheet tabs = new TabSheet();
        tabs.setSizeFull();
        tabs.addTab(new EditKoulutusPerustiedotView(), "Koulutuksen perustiedot (status)");
        tabs.addTab(new EditKoulutusKuvailevattiedotView(), "Koulutuksen kuvailevat tiedot (status)");

        addComponent(tabs);
    }
}
