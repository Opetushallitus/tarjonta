package fi.vm.sade.tarjonta.ui.model.view;

import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.AbstractLayout;
import fi.vm.sade.vaadin.oph.helper.UiBuilder;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.CloseEvent;
import fi.vm.sade.vaadin.oph.enums.UiMarginEnum;
import fi.vm.sade.vaadin.oph.demodata.DataSource;
import fi.vm.sade.vaadin.oph.demodata.row.CheckBoxTableStyle;
import fi.vm.sade.vaadin.oph.layout.AbstractDialogWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jani
 */
public class CreateKoulutusView extends AbstractDialogWindow<VerticalLayout> {

    private static final Logger LOG = LoggerFactory.getLogger(CreateKoulutusView.class);
    private static final String TEKSTI = "Koulutusta ei ole vielä liitetty mihinkään organisaatioon.";
    private CreateKoulutusTreeView createKoulutusTreeView;

    public CreateKoulutusView(String label) {
        super(
                label,
                UiBuilder.format("Olet luomassa {0} {1}", "tutkintoon johtavaa", "koulutusta"),
                DataSource.LOREM_IPSUM_SHORT);

    }

    @Override
    public void buildLayout(AbstractLayout layout) {

        /* YOUR LAYOUT BETWEEN TOPIC AND BUTTONS */

        HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        splitPanel.setSplitPosition(50); // percent

        VerticalLayout vLeft = UiBuilder.newVerticalLayout();
        VerticalLayout vRight = UiBuilder.newVerticalLayout(false, UiMarginEnum.LEFT);

        splitPanel.addComponent(vLeft);
        splitPanel.addComponent(vRight);

        createKoulutusTreeView = new CreateKoulutusTreeView();
        createKoulutusTreeView.setContainerDataSource(DataSource.treeTableData(new CheckBoxTableStyle()));
        vLeft.addComponent(createKoulutusTreeView);

        HorizontalLayout middleLayout = UiBuilder.newHorizontalLayout();
        Panel newTextPanel = UiBuilder.newTextPanel(TEKSTI, null, UiBuilder.DEFAULT_REALTIVE_SIZE, middleLayout);
        newTextPanel.setSizeFull();
        vRight.addComponent(newTextPanel);
        
        layout.addComponent(splitPanel);
    }

    /**
     * In case the window is closed otherwise.
     */
    @Override
    public void windowClose(CloseEvent e) {
        LOG.debug("In windowClose - close event");
        removeDialogButtons();
        createKoulutusTreeView = null;
    }
}
