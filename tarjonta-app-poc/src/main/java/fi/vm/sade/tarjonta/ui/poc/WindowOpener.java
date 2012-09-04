package fi.vm.sade.tarjonta.ui.poc;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
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
public class WindowOpener extends VerticalLayout
        implements Window.CloseListener {

      private static final Logger LOG = LoggerFactory.getLogger(WindowOpener.class);
 
    
    public static final String lorem = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut ut massa eget erat dapibus sollicitudin. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Pellentesque a augue. Praesent non elit. Duis sapien dolor, cursus eget, pulvinar eget, eleifend a, est. Integer in nunc. Vivamus consequat ipsum id sapien. Duis eu elit vel libero posuere luctus. Aliquam ac turpis. Aenean vitae justo in sem iaculis pulvinar. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Aliquam sit amet mi. "
            + "<br/>"
            + "Aenean auctor, mi sit amet ultricies pulvinar, dui urna adipiscing odio, ut faucibus odio mauris eget justo.";
    private static final String TEKSTI = "Koulutusta ei ole vielä liitetty mihinkään organisaatioon.";
    private static final String COLUMN_A = "Kategoriat";
    private static final String COLUMN_C = "Toiminnot";
    private final String[] kultturiala = {
        "Käsi- ja taideteollisuusalan perustutkinto - Artesaani",
        "Tuotteen suunnittelu ja valmistamisen koulutusohjelma",
        "Ympäristön suunnittelun ja rakentamisen koulutuohjelma"};
    private final String[] tekniikanJaLiikenteenAla = {
        "Autoala perustutkinto - Parturi-kampaajan, syksy 2012",
        "Sähkä- ja automaatitekniikan perustutkinto",
        "Tieto- ja tietliikenneteksniikan perustutkinto",
        "Kone- ja metallialan perustutkinto - ICT-asentaja",
        "Kone- ja metallialan perustutkinto - Koneistaja"};
    private final Map<String, String[]> map = new HashMap<String, String[]>();
    Window mainwindow;  // Reference to main window
    Window win;    // The window to be opened
    Button closebutton; // A button in the window

    public WindowOpener(String label, Window main) {
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

        newVerticalLayout.addComponent(new Label(lorem));
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
                EditKoulutus f = new EditKoulutus();
                mainwindow.removeAllComponents();
                mainwindow.addComponent(f);
            }
        });
        
         newHorizontalLayout.addComponent(button);
        
        return newVerticalLayout;
    }

    private void populateTree(Tree tree) {
        map.put("Kulttuuriala (3kpl)", kultturiala);
        map.put("Tekniikan ja liikentee ala (16kpl)", tekniikanJaLiikenteenAla);

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
