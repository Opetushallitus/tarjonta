package fi.vm.sade.vaadin.oph.demodata;

import com.vaadin.data.util.HierarchicalContainer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jani
 */
public class DataSource {

    public static final String LOREM_IPSUM_SHORT = "Lorem ipsum dolor sit amet, consectetur "
            + "adipiscing elit. Ut ut massa eget erat dapibus sollicitudin. Vestibulum ante ipsum "
            + "primis in faucibus orci luctus et ultrices posuere cubilia Curae; Pellentesque a "
            + "augue. Praesent non elit. Duis sapien dolor, cursus eget, pulvinar eget, eleifend a, "
            + "est. Integer in nunc. Vivamus consequat ipsum id sapien. Duis eu elit vel libero "
            + "posuere luctus. Aliquam ac turpis. Aenean vitae justo in sem iaculis pulvinar. "
            + "Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus "
            + "mus. Aliquam sit amet mi. "
            + "<br/>"
            + "Aenean auctor, mi sit amet ultricies pulvinar, dui urna adipiscing odio, ut "
            + "faucibus odio mauris eget justo.";
    public static final String[] KULTTURIALA = {
        "Käsi- ja taideteollisuusalan perustutkinto - Artesaani",
        "Tuotteen suunnittelu ja valmistamisen koulutusohjelma",
        "Ympäristön suunnittelun ja rakentamisen koulutuohjelma", "Tuotteen suunnittelu ja valmistamisen koulutusohjelma",
        "Ympäristön suunnittelun ja rakentamisen koulutuohjelma", "Tuotteen suunnittelu ja valmistamisen koulutusohjelma",
        "Ympäristön suunnittelun ja rakentamisen koulutuohjelma", "Tuotteen suunnittelu ja valmistamisen koulutusohjelma",
        "Ympäristön suunnittelun ja rakentamisen koulutuohjelma"};
    public static final String[] TEKNIIIKAN_JA_LIIKENTEEN_ALA = {
        "Autoala perustutkinto - Parturi-kampaajan, syksy 2012",
        "Sähkä- ja automaatitekniikan perustutkinto",
        "Tieto- ja tietliikenneteksniikan perustutkinto",
        "Kone- ja metallialan perustutkinto - ICT-asentaja",
        "Kone- ja metallialan perustutkinto - Koneistaja"};
    private static final String COLUMN_A = "Kategoriat";

    public static HierarchicalContainer treeTableData(ITableRowFormat rowStyle) {
        Map<String, String[]> map = new HashMap<String, String[]>();

        map.put("Kulttuuriala (3kpl)", KULTTURIALA);
        map.put("Tekniikan ja liikentee ala (16kpl)", TEKNIIIKAN_JA_LIIKENTEEN_ALA);
        Set<Map.Entry<String, String[]>> set = map.entrySet();

        HierarchicalContainer hc = new HierarchicalContainer();
        Object format =  rowStyle.format("");

        for (Map.Entry<String, String[]> e : set) {

            hc.addContainerProperty(COLUMN_A, format.getClass(), rowStyle.format(e.getKey()));
            Object rootItem = hc.addItem();

            hc.getContainerProperty(rootItem, COLUMN_A).setValue(rowStyle.format(e.getKey()));

            for (String strText : e.getValue()) {
                Object subItem = hc.addItem();
                hc.setParent(subItem, rootItem);
                hc.getContainerProperty(subItem, COLUMN_A).setValue(rowStyle.format(strText));
                hc.setChildrenAllowed(subItem, false);
            }
        }

        return hc;
    }
}
