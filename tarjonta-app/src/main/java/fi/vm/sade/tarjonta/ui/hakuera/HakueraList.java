package fi.vm.sade.tarjonta.ui.hakuera;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.service.HakueraService;
import fi.vm.sade.tarjonta.service.types.dto.HakueraSimpleDTO;
import fi.vm.sade.tarjonta.service.types.dto.SearchCriteriaDTO;
import fi.vm.sade.tarjonta.ui.AbstractSadeApplication;
import fi.vm.sade.tarjonta.ui.haku.HakuEditForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.List;

/**
 * @author Antti
 */
@Configurable(preConstruction = true)
public class HakueraList extends CustomComponent {

    @Autowired
    private HakueraService hakueraService; // TODO: ui-service kehiin

    private Layout layout = new VerticalLayout();
    private Table table = new Table();
    private CheckBox paattyneet = checkbox("HakueraList.cb_paattyneet");
    private CheckBox meneillaan = new CheckBox(I18N.getMessage("HakueraList.cb_meneillaan"), true);
    private CheckBox tulevat = new CheckBox(I18N.getMessage("HakueraList.cb_tulevat"), true);
    private HakuEditForm hakuEditForm;

    public HakueraList(final HakuEditForm hakuEditForm) {
        this.hakuEditForm = hakuEditForm; // TODO: replace with blackboard?

        table.setSelectable(true);
        table.setImmediate(true);
        table.addListener(new Property.ValueChangeListener() {
            public void valueChange(Property.ValueChangeEvent event) {
                hakuEditForm.populate((HakueraSimpleDTO)table.getValue());
            }
        });

        reload();

        layout.addComponent(paattyneet);
        layout.addComponent(meneillaan);
        layout.addComponent(tulevat);
        layout.addComponent(table);
        setCompositionRoot(layout);
    }

    private void reload() {
        List<HakueraSimpleDTO> hakueras = hakueraService.findAll(buildSearchCriteria());
        final BeanItemContainer<HakueraSimpleDTO> tableContainer = new BeanItemContainer<HakueraSimpleDTO>(HakueraSimpleDTO.class, hakueras);
        tableContainer.addNestedContainerProperty("nimi");
        //tableContainer.setColumnHeader("nimi", I18N.getMessage("HakueraList.nimi"));
        table.setContainerDataSource(tableContainer);
    }

    private SearchCriteriaDTO buildSearchCriteria() {
        SearchCriteriaDTO searchCriteria = new SearchCriteriaDTO();
        searchCriteria.setPaattyneet(Boolean.TRUE.equals(paattyneet.getValue()));
        searchCriteria.setMeneillaan(Boolean.TRUE.equals(meneillaan.getValue()));
        searchCriteria.setTulevat(Boolean.TRUE.equals(tulevat.getValue()));
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
}
