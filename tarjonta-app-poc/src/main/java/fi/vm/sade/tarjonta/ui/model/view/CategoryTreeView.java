package fi.vm.sade.tarjonta.ui.model.view;

import fi.vm.sade.vaadin.oph.helper.UiBuilder;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import com.vaadin.ui.TreeTable;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import fi.vm.sade.tarjonta.ui.enums.TarjontaStyles;
import fi.vm.sade.tarjonta.ui.poc.RowMenuBar;
import fi.vm.sade.vaadin.Oph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CategoryTreeView extends TreeTable {

    private static final Logger LOG = LoggerFactory.getLogger(CategoryTreeView.class);
    private static final String COLUMN_A = "Kategoriat";
    private Map<String, String[]> map = new HashMap<String, String[]>();

    public CategoryTreeView() {
        super();

        init();
        addStyleName(TarjontaStyles.CATEGORY_TREE.getStyleName());
    }

    private void init() {
        this.addStyleName(Oph.TABLE_BORDERLESS); //TODO: TEEMATKAA!
        this.setColumnHeaderMode(COLUMN_HEADER_MODE_HIDDEN);

        // OTHER
        this.setSelectable(false);
        this.setImmediate(false);
        this.setSizeFull();

        populateTree();
    }

    private HorizontalLayout buildTreeRow(final String text) {
        CheckBox newCheckbox = UiBuilder.newCheckbox(null, null);
        Label label = new Label(text);
        label.setSizeUndefined(); // -1,-1
        HorizontalLayout horizontal = new HorizontalLayout();
        horizontal.setWidth(-1, UNITS_PIXELS);
        horizontal.setHeight(-1, UNITS_PIXELS); //Tämä toimii!!!

        horizontal.addComponent(newCheckbox);
        horizontal.addComponent(new RowMenuBar());
        horizontal.addComponent(label);

        horizontal.setExpandRatio(label, 1f); //default == 0

        return horizontal;
    }

    private void populateTree() {
        map.put("Kulttuuriala (3kpl)", UiBuilder.KULTTURIALA);
        map.put("Tekniikan ja liikentee ala (16kpl)", UiBuilder.TEKNIIIKAN_JA_LIIKENTEEN_ALA);

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
    }
}
