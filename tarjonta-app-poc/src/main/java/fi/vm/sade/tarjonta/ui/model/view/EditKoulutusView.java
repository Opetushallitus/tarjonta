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
import fi.vm.sade.vaadin.oph.enums.LabelStyle;
import fi.vm.sade.vaadin.oph.helper.UiBuilder;
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
    private static final String TITLE_FORMAT = "Olet muokkaamassa {0} koulutusta organisaatioon {1}";

    public EditKoulutusView() {
        super();
        LOG.info("EditKoulutusView()");

        setSizeFull();
        setScrollable(true);

        addComponent(UiBuilder.newLabel(TITLE_FORMAT, null, LabelStyle.H2, "tutkintoon johtavaa", "Informaatiotekniikan tiedekunta"));

        TabSheet tabs = new TabSheet();
        tabs.setSizeFull();
        tabs.addTab(new EditKoulutusPerustiedotView(), "Koulutuksen perustiedot (status)");
        tabs.addTab(new EditKoulutusKuvailevattiedotView(), "Koulutuksen kuvailevat tiedot (status)");

        addComponent(tabs);
    }
}
