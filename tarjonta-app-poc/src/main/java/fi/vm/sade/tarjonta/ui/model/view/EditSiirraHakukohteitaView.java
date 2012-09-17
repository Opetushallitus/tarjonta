package fi.vm.sade.tarjonta.ui.model.view;

import fi.vm.sade.vaadin.oph.helper.UiBuilder;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.CloseEvent;
import fi.vm.sade.vaadin.oph.demodata.DataSource;
import fi.vm.sade.vaadin.oph.layout.AbstractDialogWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jani
 */
public class EditSiirraHakukohteitaView extends AbstractDialogWindow {

    private static final Logger LOG = LoggerFactory.getLogger(EditSiirraHakukohteitaView.class);

    public EditSiirraHakukohteitaView(String label) {
        super(
                label,
                UiBuilder.format("Olet lisäämässä hakuun {0} {1}", "7", "kohdetta"),
                DataSource.LOREM_IPSUM_SHORT);

        setWidth("600px");
        setHeight("500px");
    }

    @Override
    public void buildLayout(VerticalLayout layout) {

        UiBuilder.newComboBox("Lisää hakuun", new String[]{"Täydennyshakuun"}, layout);

    }

    /**
     * In case the window is closed otherwise.
     */
    @Override
    public void windowClose(CloseEvent e) {
        removeDialogButtons();
    }
}
