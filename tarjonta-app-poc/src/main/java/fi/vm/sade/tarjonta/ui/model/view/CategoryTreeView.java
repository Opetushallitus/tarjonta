package fi.vm.sade.tarjonta.ui.model.view;

import fi.vm.sade.tarjonta.ui.poc.helper.UI;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import com.vaadin.ui.TreeTable;

import com.vaadin.event.Action;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import fi.vm.sade.tarjonta.ui.enums.TarjontaStyles;
import fi.vm.sade.vaadin.Oph;

public class CategoryTreeView extends TreeTable {

    private static String[] LABEL_ACTIONS = {"Valitse muokkaustoiminto",
        "Näytä kohteet", "Poista"};
    private static final Action SHOW_ITEM_ACTION = new Action(LABEL_ACTIONS[1]);
    private static final Action REMOVE_ITEM_ACTION = new Action(
            LABEL_ACTIONS[2]);
    private static final String COLUMN_A = "Kategoriat";
    private Map<String, String[]> map = new HashMap<String, String[]>();

    public CategoryTreeView() {
        super();

        init();
        addStyleName(TarjontaStyles.CATEGORY_TREE.getStyleName());

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
        this.setSelectable(true);
        this.addStyleName(Oph.TABLE_BORDERLESS); //TODO: TEEMATKAA!
        this.setColumnHeaderMode(COLUMN_HEADER_MODE_HIDDEN);

        // OTHER

        this.setWidth(UI.PCT100);
        this.setHeight(UI.PCT100);
        this.setImmediate(false);

        populateTree();
    }

    private HorizontalLayout buildTreeRow(final String text) {
        CheckBox newCheckbox = UI.newCheckbox(null, null);
        Button button = UI.newButton(null, null);
        button.setIcon(new ThemeResource("../../themes/oph/img/search.png"));
        button.addStyleName(Oph.BUTTON_SMALL); //OMA teema

        Label label = new Label(text);
        label.setSizeUndefined(); // -1,-1
        HorizontalLayout horizontal = new HorizontalLayout();
        horizontal.setWidth(-1, UNITS_PIXELS);
        horizontal.setHeight(-1, UNITS_PIXELS); //Tämä toimii!!!

        horizontal.addComponent(newCheckbox);
        horizontal.addComponent(button);
        horizontal.addComponent(label);

        horizontal.setExpandRatio(label, 1f); //default == 0

        return horizontal;
    }

    private void populateTree() {
        map.put("Kulttuuriala (3kpl)", UI.KULTTURIALA);
        map.put("Tekniikan ja liikentee ala (16kpl)", UI.TEKNIIIKAN_JA_LIIKENTEEN_ALA);

        Set<Entry<String, String[]>> set = map.entrySet();

        for (Entry<String, String[]> e : set) {

            this.addContainerProperty(COLUMN_A, HorizontalLayout.class, buildTreeRow(e.getKey()));
            Object rootItem = this.addItem();

            this.getContainerProperty(rootItem, COLUMN_A).setValue(buildTreeRow(e.getKey()));

            for (String strText : e.getValue()) {
                Object subItem = this.addItem();
                this.setParent(subItem, rootItem);
                this.getContainerProperty(subItem, COLUMN_A).setValue(buildTreeRow(strText));
            }
        }
        // this.setColumnExpandRatio(COLUMN_A, 1);
    }
}
