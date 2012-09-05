package fi.vm.sade.tarjonta.ui.model.view;

import fi.vm.sade.tarjonta.ui.poc.helper.UI;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import fi.vm.sade.tarjonta.ui.poc.EditKoulutus;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jani
 */
/**
 * Component contains a button that allows opening a window.
 */
public class CreateKoulutusView extends VerticalLayout
        implements Window.CloseListener {

    private static final Logger LOG = LoggerFactory.getLogger(CreateKoulutusView.class);
    private static final String TEKSTI = "Koulutusta ei ole vielä liitetty mihinkään organisaatioon.";
    private static final String COLUMN_A = "Kategoriat";
    
   
    private Map<String, String[]> map = new HashMap<String, String[]>();
    private Window mainwindow;  // Reference to main window
    private Window win;    // The window to be opened
    private Button closebutton; // A button in the window
    private VerticalLayout rightMainLayout;

    public CreateKoulutusView(String label, VerticalLayout rightMainLayout, Window main) {
        this.rightMainLayout = rightMainLayout;
        mainwindow = main;
        win = new Window(label);
        win.setWidth("50%");
        win.setHeight("50%");
        win.center();
        this.setSizeFull();

        mainwindow.addWindow(win);

        //win.addComponent(new Label("A text label in the window."));
        closebutton = new Button("Close");

        closebutton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                mainwindow.removeWindow(win);
            }
        });

        win.addComponent(closebutton);
        win.addComponent(buildSearchLayout());
    }

    private VerticalLayout buildSearchLayout() {
        VerticalLayout newVerticalLayout = UI.newVerticalLayout(null, null);
        HorizontalLayout newHorizontalLayout = UI.newHorizontalLayout(null, null);

        newVerticalLayout.addComponent(new Label(UI.LOREM_IPSUM_SHORT));
        newVerticalLayout.addComponent(newHorizontalLayout);

        Tree tree = new Tree("Hardware Inventory");
        // Set multiselect mode
        tree.setMultiSelect(true);
        tree.setImmediate(true);
        tree.addListener(new ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                Tree t = (Tree) event.getProperty();
                // enable if something is selected, returns a set
            }
        });

        populateTree(tree);

        newHorizontalLayout.addComponent(tree);
        newHorizontalLayout.addComponent(new Label(TEKSTI));

        Button button = new Button("Jatka");
        button.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                LOG.info("buttonClick() - luo uusi koulutus click...");
                mainwindow.removeWindow(win);

                EditKoulutus f = new EditKoulutus();
                rightMainLayout.removeAllComponents();
                rightMainLayout.addComponent(f);
            }
        });

        newHorizontalLayout.addComponent(button);

        return newVerticalLayout;
    }

    private void populateTree(Tree tree) {
        map.put("Kulttuuriala (3kpl)", UI.KULTTURIALA);
        map.put("Tekniikan ja liikentee ala (16kpl)", UI.TEKNIIIKAN_JA_LIIKENTEEN_ALA);

        Set<Map.Entry<String, String[]>> set = map.entrySet();
        for (Map.Entry<String, String[]> e : set) {
            tree.addContainerProperty(COLUMN_A, String.class, e.getKey());

            Object rootItem = tree.addItem();
            tree.getContainerProperty(rootItem, COLUMN_A).setValue(e.getKey());
            // tree.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);

//            for (String arr : e.getValue()) {
//                Object subItem = tree.addItem();
//
//                tree.setParent(subItem, rootItem);
//                tree.getContainerProperty(subItem, COLUMN_A).setValue(arr);
//                tree.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
//            }
        }
    }

    /**
     * In case the window is closed otherwise.
     */
    @Override
    public void windowClose(CloseEvent e) {
    }
}
