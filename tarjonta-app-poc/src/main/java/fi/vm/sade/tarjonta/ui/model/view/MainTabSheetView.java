package fi.vm.sade.tarjonta.ui.model.view;

import com.vaadin.ui.TabSheet;
import fi.vm.sade.tarjonta.ui.poc.helper.I18NHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author jani
 */
@Configurable(preConstruction = true)
public class MainTabSheetView extends TabSheet {

    private static final Logger LOG = LoggerFactory.getLogger(MainTabSheetView.class);
    private MainTabKoulutusView tabKoulutukset;
    private MainTabHakuView tabHaut;
    private MainTabHakukohteetView tabHakukohteet;
    private I18NHelper i18n = new I18NHelper(this);

    public MainTabSheetView() {
        setSizeFull();
        buildLayout();
    }

    private void buildLayout() {
        setImmediate(true);
        setSizeFull();

        tabHaut = new MainTabHakuView();
        addTab(tabHaut, "Haut (2 kpl)", null);

        tabKoulutukset = new MainTabKoulutusView();
        addTab(tabKoulutukset, "Koulutukset (28 kpl)", null);

        tabHakukohteet = new MainTabHakukohteetView();
        addTab(tabHakukohteet, "Hakukohteet (35 kpl)", null);

        //SET SELECTED TAB!
        setSelectedTab(tabKoulutukset);
    }
}
