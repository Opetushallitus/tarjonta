package fi.vm.sade.tarjonta.widget;

import com.vaadin.data.Item;
import com.vaadin.data.Validator;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliDTO;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliSearchDTO;
import fi.vm.sade.tarjonta.service.KoulutusmoduuliAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addon.customfield.CustomField;

import java.util.List;

/**
 * @author Antti Salonen
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class KoulutusmoduuliComponent extends CustomField {

    @Autowired
    protected KoulutusmoduuliAdminService koulutusmoduuliService;

    protected AbstractSelect field;

    protected Layout root;

    protected final IndexedContainer container = new IndexedContainer();

    protected KoulutusmoduuliSearchDTO searchSpecification;

    public KoulutusmoduuliComponent(KoulutusmoduuliSearchDTO searchSpecification) {
        this.searchSpecification = searchSpecification;
        root = new HorizontalLayout();
        setCompositionRoot(root);
    }

    @Override
    public Class<?> getType() {
        return String.class; // because value is koulutusmoduuli's oid string
    }

    public void setKoulutusmoduuliService(KoulutusmoduuliAdminService koulutusmoduuliService) {
        this.koulutusmoduuliService = koulutusmoduuliService;
    }

    public AbstractSelect getField() {
        return field;
    }

    public void setField(AbstractSelect field) {
        this.field = field;
        container.removeAllItems();
        container.addContainerProperty("fieldCaption", String.class, "");
        field.setContainerDataSource(container);
        field.setItemCaptionPropertyId("fieldCaption");
        root.addComponent(field);
    }

    @Override
    public void attach() {
        super.attach();
        setFieldValues();
    }

    protected void setFieldValues() {
        List<KoulutusmoduuliDTO> koulutusmoduulis = koulutusmoduuliService.find(searchSpecification);
        for (KoulutusmoduuliDTO koulutusmoduuli : koulutusmoduulis) {
            Object value = koulutusmoduuli.getOid(); // TODO: fieldValueFormatter
            Item item = container.addItem(value);
            String formattedCaption = koulutusmoduuli.getNimi(); // TODO: captionFormatter
            item.getItemProperty("fieldCaption").setValue(formattedCaption);
        }
        container.sort(new Object[] {"fieldCaption"}, new boolean[] {true});
    }

    @Override
    public Object getValue() {
        return field.getValue();
    }

    @Override
    public void commit() throws SourceException, Validator.InvalidValueException {
        //super.commit(); vai tarttisko?
        field.commit();
    }

    @Override
    public void setImmediate(boolean immediate) {
        field.setImmediate(immediate);
    }
}
