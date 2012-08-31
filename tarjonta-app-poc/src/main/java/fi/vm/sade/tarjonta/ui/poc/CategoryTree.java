package fi.vm.sade.tarjonta.ui.poc;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;

import com.vaadin.event.Action;

public class CategoryTree extends TreeTable {

    private static String[] LABEL_ACTIONS = {"Valitse muokkaustoiminto",
        "N�yt� kohteet", "Poista"};
    private static final Action SHOW_ITEM_ACTION = new Action(LABEL_ACTIONS[1]);
    private static final Action REMOVE_ITEM_ACTION = new Action(
            LABEL_ACTIONS[2]);
    private static final String COLUMN_A = "Kategoriat";
    private static final String COLUMN_C = "Toiminnot";
    private final String[] kultturiala = {
        "K�si- ja taideteollisuusalan perustutkinto - Artesaani",
        "Tuotteen suunnittelu ja valmistamisen koulutusohjelma",
        "Ymp�rist�n suunnittelun ja rakentamisen koulutuohjelma"};
    private final String[] tekniikanJaLiikenteenAla = {
        "Autoala perustutkinto - Parturi-kampaajan, syksy 2012",
        "S�hk�- ja automaatitekniikan perustutkinto",
        "Tieto- ja tietliikenneteksniikan perustutkinto",
        "Kone- ja metallialan perustutkinto - ICT-asentaja",
        "Kone- ja metallialan perustutkinto - Koneistaja"};
    private final Map<String, String[]> map = new HashMap<String, String[]>();
    private NativeSelect selectBox = new NativeSelect();

    public CategoryTree() {
        super();

        init();

        this.addActionHandler(new Action.Handler() {
            /**
             *
             */
            private static final long serialVersionUID = 8773892537096736179L;

            public void handleAction(Action action, Object sender, Object target) {
                if (action == SHOW_ITEM_ACTION) {
                    // Create new item
                    Object item = addItem(new Object[]{"New Item", 0,
                                new Date()}, null);
                    setChildrenAllowed(item, false);
                    setParent(item, target);
                } else if (action == REMOVE_ITEM_ACTION) {
                    Object item = addItem(new Object[]{"New Category", 0,
                                new Date()}, null);
                    setParent(item, target);
                }
            }

            public Action[] getActions(Object target, Object sender) {

                if (target == null) {
                    // Context menu in an empty space -> add a new main category
                    return new Action[]{REMOVE_ITEM_ACTION};

                } else if (areChildrenAllowed(target)) {
                    // Context menu for a category
                    return new Action[]{REMOVE_ITEM_ACTION, SHOW_ITEM_ACTION};

                } else {
                    // Context menu for an item
                    return new Action[]{};
                }
            }
        });
    }

    private void init() {

        // SELECTBOX
        for (String str : LABEL_ACTIONS) {
            selectBox.addItem(str);
        }

        selectBox.setNullSelectionAllowed(false);
        selectBox.setValue(LABEL_ACTIONS[0]);
        selectBox.setImmediate(true);

        // OTHER

        this.setWidth(UI.PCT100);
        this.setHeight(UI.PCT100);
        this.setImmediate(false);

        populateTree();

    }

    private NativeSelect createNativeSelect() {
        // SELECTBOX
        NativeSelect sb = new NativeSelect();

        for (String str : LABEL_ACTIONS) {
            sb.addItem(str);
        }

        sb.setNullSelectionAllowed(false);
        sb.setValue(LABEL_ACTIONS[0]);
        sb.setImmediate(true);

        return sb;

    }

    private void populateTree() {
        map.put("Kulttuuriala (3kpl)", kultturiala);
        map.put("Tekniikan ja liikentee ala (16kpl)", tekniikanJaLiikenteenAla);

        Set<Entry<String, String[]>> set = map.entrySet();

        for (Entry<String, String[]> e : set) {
            this.addContainerProperty(COLUMN_A, String.class, e.getKey());

            this.addContainerProperty(COLUMN_C, NativeSelect.class, null, null,
                    new ThemeResource("img/test_icon.gif"), null);

            Object rootItem = this.addItem();

            this.getContainerProperty(rootItem, COLUMN_A).setValue(e.getKey());
            this.getContainerProperty(rootItem, COLUMN_C).setValue(createNativeSelect());

            for (String arr : e.getValue()) {

                Object subItem = this.addItem();

                this.setParent(subItem, rootItem);
                this.getContainerProperty(subItem, COLUMN_A).setValue(arr);
                this.getContainerProperty(subItem, COLUMN_C)
                        .setValue(createNativeSelect());
            }
        }
        // this.setColumnExpandRatio(COLUMN_A, 1);
    }
}
