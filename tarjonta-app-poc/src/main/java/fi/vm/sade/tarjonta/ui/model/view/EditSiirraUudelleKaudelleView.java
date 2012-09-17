package fi.vm.sade.tarjonta.ui.model.view;

import com.vaadin.ui.AbstractLayout;
import fi.vm.sade.vaadin.oph.helper.UiBuilder;
import com.vaadin.ui.DateField;
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
public class EditSiirraUudelleKaudelleView extends AbstractDialogWindow {

    private static final Logger LOG = LoggerFactory.getLogger(EditSiirraUudelleKaudelleView.class);

    public EditSiirraUudelleKaudelleView(String label) {
        super(
                label,
                UiBuilder.format("Olet luomassa {0} {1}", "28", "koulutusta"),
                DataSource.LOREM_IPSUM_SHORT);
        setWidth("600px");
        setHeight("500px");
    }

    @Override
    public void buildLayout(VerticalLayout layout) {
        UiBuilder.newComboBox("Aseta kaikille sama koulutusten alkamiskausi*", new String[]{"Syksy 2012", "Talvi 2013"}, layout);
        DateField newDate = UiBuilder.newDate();
        newDate.setCaption("Aseta kaikille sama koulutusten alkamispäivä");
        layout.addComponent(newDate);

        UiBuilder.newCheckbox("Siirrä myös liittyvät hakukohteet (35)", layout);
    }

    /**
     * In case the window is closed otherwise.
     */
    @Override
    public void windowClose(CloseEvent e) {
        removeDialogButtons();
    }
}
