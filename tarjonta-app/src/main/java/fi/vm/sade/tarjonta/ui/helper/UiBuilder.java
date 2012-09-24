package fi.vm.sade.tarjonta.ui.helper;

import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import fi.vm.sade.generic.ui.component.CaptionFormatter;
import fi.vm.sade.generic.ui.component.FieldValueFormatter;
import fi.vm.sade.koodisto.service.types.dto.KoodiDTO;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.koodisto.widget.factory.WidgetFactory;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 *
 * @author jani
 */
public class UiBuilder extends UiUtil{

    /**
     * Create new KoodistoComponent with ComboBox. Possible bind to a property.
     *
     * @param koodistoUri
     * @param psi
     * @param expression
     * @param prompt
     * @param layout
     * @return
     */
    public static KoodistoComponent koodistoComboBox(AbstractLayout layout, final String koodistoUri, PropertysetItem psi, String expression, String prompt) {

        // Koodisto displayed in ComboBox
        ComboBox combo = comboBox(null, null, null);

        combo.setFilteringMode(AbstractSelect.Filtering.FILTERINGMODE_CONTAINS);
        if (prompt != null) {
            combo.setInputPrompt(prompt);
        }

        final KoodistoComponent c = WidgetFactory.create(koodistoUri);

        // Wire koodisto to combobox
        c.setField(combo);

        // Set to be immediate
        // c.setImmediate(true);

        // DISPLAYED text
        c.setCaptionFormatter(new CaptionFormatter() {
            @Override
            public String formatCaption(Object dto) {
                if (dto instanceof KoodiDTO) {
                    KoodiDTO kdto = (KoodiDTO) dto;
                    return kdto.getKoodiArvo();
                } else {
                    return "!KoodiDTO - Don't know how to format this: " + dto;
                }
            }
        });

        // BOUND value
        c.setFieldValueFormatter(new FieldValueFormatter() {
            @Override
            public Object formatFieldValue(Object dto) {
                if (dto instanceof KoodiDTO) {
                    KoodiDTO kdto = (KoodiDTO) dto;
                    return kdto.getKoodiUri();
                } else {
                    return "" + dto;
                }
            }
        });

        // Selected data bound there
        if (psi != null && expression != null) {
            c.setPropertyDataSource(psi.getItemProperty(expression));
        }

        handleAddComponent(layout, c);

        return c;
    }

    public static KoodistoComponent koodistoTwinColSelect(AbstractLayout layout, final String koodistoUri, PropertysetItem psi, String expression) {

        // Koodisto displayed in TwinColSelect
        TwinColSelect c = twinColSelect(null, null, null);

        // Only multiple (Set<String>) values allowed!
        c.setMultiSelect(true);

        final KoodistoComponent kc = WidgetFactory.create(koodistoUri);

        // Wire koodisto to combobox
        kc.setField(c);

        // Set to be immediate
        // kc.setImmediate(true);

        // DISPLAYED text
        kc.setCaptionFormatter(new CaptionFormatter() {
            @Override
            public String formatCaption(Object dto) {
                if (dto instanceof KoodiDTO) {
                    KoodiDTO kdto = (KoodiDTO) dto;
                    return kdto.getKoodiArvo();
                } else {
                    return "!KoodiDTO?: " + dto;
                }
            }
        });

        // BOUND value
        kc.setFieldValueFormatter(new FieldValueFormatter() {
            @Override
            public Object formatFieldValue(Object dto) {
                if (dto instanceof KoodiDTO) {
                    KoodiDTO kdto = (KoodiDTO) dto;
                    return kdto.getKoodiUri();
                } else {
                    return "" + dto;
                }
            }
        });

        // Selected data bound there
        if (psi != null && expression != null) {
            kc.setPropertyDataSource(psi.getItemProperty(expression));
        }

        handleAddComponent(layout, kc);

        return kc;
    }

    public static VerticalSplitPanel newHR(VerticalLayout _layout) {
        VerticalSplitPanel sp = new VerticalSplitPanel();
        sp.setLocked(true);
        sp.setHeight("2px");

        handleAddComponent(_layout, sp);

        return sp;
    }
}
