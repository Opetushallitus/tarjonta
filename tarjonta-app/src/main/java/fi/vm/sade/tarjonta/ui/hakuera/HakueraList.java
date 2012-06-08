package fi.vm.sade.tarjonta.ui.hakuera;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.ui.component.MultiLingualTextImpl;
import fi.vm.sade.tarjonta.service.HakueraService;
import fi.vm.sade.tarjonta.service.types.dto.HakueraSimpleDTO;
import fi.vm.sade.tarjonta.service.types.dto.SearchCriteriaDTO;
import fi.vm.sade.tarjonta.ui.haku.HakuEditForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Antti
 */
@Configurable(preConstruction = true)
public class HakueraList extends CustomComponent {

    @Autowired
    private HakueraService hakueraService;

    private Layout layout = new VerticalLayout();
    private Table table = new Table();
    private CheckBox paattyneet = checkbox("HakueraList.cb_paattyneet");
    private CheckBox meneillaan = checkbox("HakueraList.cb_meneillaan");
    private CheckBox tulevat = checkbox("HakueraList.cb_tulevat");

    public HakueraList() {
        table.setSelectable(true);
        table.setImmediate(true);

        reload();

        layout.addComponent(paattyneet);
        layout.addComponent(meneillaan);
        layout.addComponent(tulevat);
        layout.addComponent(table);
        setCompositionRoot(layout);
    }

    private void reload() {
        List<HakueraSimpleDTO> hakueraDtos = hakueraService.findAll(buildSearchCriteria());
        List<HakueraSimple> hakueras = new ArrayList<HakueraSimple>();
        for (HakueraSimpleDTO dto : hakueraDtos) {
            hakueras.add(new HakueraSimple(dto));
        }
        final BeanItemContainer<HakueraSimple> tableContainer = new BeanItemContainer<HakueraSimple>(HakueraSimple.class, hakueras);
        tableContainer.addNestedContainerProperty("nimi");
        //tableContainer.setColumnHeader("nimi", I18N.getMessage("HakueraList.nimi"));
        table.setContainerDataSource(tableContainer);
        table.setVisibleColumns(new Object[] {"nimi"});
    }

    private SearchCriteriaDTO buildSearchCriteria() {
        SearchCriteriaDTO searchCriteria = new SearchCriteriaDTO();
        searchCriteria.setPaattyneet(Boolean.TRUE.equals(paattyneet.getValue()));
        searchCriteria.setMeneillaan(Boolean.TRUE.equals(meneillaan.getValue()));
        searchCriteria.setTulevat(Boolean.TRUE.equals(tulevat.getValue()));
        searchCriteria.setLang(Locale.getDefault().getLanguage());
        return searchCriteria;
    }

    public Table getTable() {
        return table;
    }

    public CheckBox getPaattyneet() {
        return paattyneet;
    }

    public CheckBox getMeneillaan() {
        return meneillaan;
    }

    public CheckBox getTulevat() {
        return tulevat;
    }

    private CheckBox checkbox(String captionKey) {
        CheckBox checkBox = new CheckBox(I18N.getMessage(captionKey), true);
        checkBox.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                reload();
            }
        });
        checkBox.setImmediate(true);
        return checkBox;
    }

    public static class HakueraSimple {
        private HakueraSimpleDTO dto;
        private MultiLingualTextImpl nimiMl;
        private String nimi;
        private String oid;

        public HakueraSimple(HakueraSimpleDTO dto) {
            this.dto = dto;
            nimi = new MultiLingualTextImpl(dto, "nimi").getClosest(Locale.getDefault());
        }

        public String getNimi() {
            return nimi;
        }

        public String getOid() {
            return oid;
        }
    }
}
