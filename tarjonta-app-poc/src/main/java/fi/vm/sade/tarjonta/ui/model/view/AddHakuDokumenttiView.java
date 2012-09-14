package fi.vm.sade.tarjonta.ui.model.view;

import com.vaadin.ui.Form;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.vaadin.oph.helper.UiBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addon.formbinder.ViewBoundForm;

/**
 *
 * @author jani
 */
@Configurable
public class AddHakuDokumenttiView extends VerticalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(AddHakuDokumenttiView.class);

    public AddHakuDokumenttiView() {
        Panel p = new Panel();
        addComponent(p);

        VerticalLayout vl = UiBuilder.newVerticalLayout();
        vl.setSpacing(true);

        EditorHakuView editor = new EditorHakuView();
        final Form f = new ViewBoundForm(editor);
        f.setWriteThrough(false);
        // f.setEnabled(false);
        // f.setItemDataSource(mi);

        vl.addComponent(f);
        p.setContent(vl);


        editor.addListener(new Listener() {

            @Override
            public void componentEvent(Event event) {
                LOG.info("event: {}", event);
                if (event instanceof EditorHakuView.CancelEvent) {
                    f.discard();
                }
                if (event instanceof EditorHakuView.SaveEvent) {
                    f.commit();
                }
                if (event instanceof EditorHakuView.ContinueEvent) {
                    //
                }
            }
        });


    }

}
