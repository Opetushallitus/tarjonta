package fi.vm.sade.tarjonta.poc.ui.helper;

import com.vaadin.data.Property;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.ui.component.CaptionFormatter;
import fi.vm.sade.generic.ui.component.FieldValueFormatter;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.koodisto.widget.factory.WidgetFactory;
import fi.vm.sade.vaadin.constants.UiConstant;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiBaseUtil;
import fi.vm.sade.vaadin.util.UiUtil;
import java.util.List;

/**
 *
 * @author jani
 */
public class UiBuilder extends UiUtil {

    
    
    private static final ThemeResource TAB_ICON_PLUS = new ThemeResource(UiConstant.RESOURCE_URL_OPH_IMG + "icon-add-black.png");

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
    public static KoodistoComponent koodistoComboBox(AbstractComponentContainer layout, final String koodistoUri, PropertysetItem psi, String expression, String prompt) {
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
                if (dto instanceof KoodiType) {
                    KoodiType kdto = (KoodiType) dto;
                    return kdto.getKoodiArvo();
                } else {
                    return "!KoodiType - Don't know how to format this: " + dto;
                }
            }
        });

        // BOUND value
        c.setFieldValueFormatter(new FieldValueFormatter() {
            @Override
            public Object formatFieldValue(Object dto) {
                if (dto instanceof KoodiType) {
                    KoodiType kdto = (KoodiType) dto;
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

        UiBaseUtil.handleAddComponent(layout, c);

        return c;
    }

    public static KoodistoComponent koodistoTwinColSelect(AbstractOrderedLayout layout, final String koodistoUri, PropertysetItem psi, String expression) {
        return koodistoTwinColSelect(layout, koodistoUri, psi, expression, null);
    }

    public static KoodistoComponent koodistoTwinColSelect(AbstractOrderedLayout layout, final String koodistoUri, PropertysetItem psi, String expression, Property.ValueChangeListener listener) {

        // Koodisto displayed in TwinColSelect
        TwinColSelect c = twinColSelect(null, null, listener);

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
                if (dto instanceof KoodiType) {
                    KoodiType kdto = (KoodiType) dto;
                    return kdto.getKoodiArvo();
                } else {
                    return "!KoodiType?: " + dto;
                }
            }
        });

        // BOUND value
        kc.setFieldValueFormatter(new FieldValueFormatter() {
            @Override
            public Object formatFieldValue(Object dto) {
                if (dto instanceof KoodiType) {
                    KoodiType kdto = (KoodiType) dto;
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

        UiBaseUtil.handleAddComponent(layout, kc);

        return kc;
    }

    public static TabSheet koodistoLanguageTabSheets(List<KoodiType> koodisto) {
        TabSheet tab = new TabSheet();

        if (koodisto != null) {
            for (KoodiType k : koodisto) {
                TextField textField = UiUtil.textField(null);
                textField.setHeight("100px");
                textField.setWidth(UiConstant.PCT100);

                tab.addTab(textField, k.getKoodiArvo(), null);
            }
        }
        VerticalLayout l3 = new VerticalLayout();
        l3.setMargin(true);
        tab.addTab(l3, "", TAB_ICON_PLUS);

        return tab;
    }

    public static TabSheet koodistoLanguageTabSheets(String koodistoUri, Property.ValueChangeListener valueChangeListener) {
        VerticalLayout vl = verticalLayout(true, UiMarginEnum.NONE);
        koodistoTwinColSelect(vl, koodistoUri, null, null, valueChangeListener);

        TabSheet tab = new TabSheet();
        tab.addTab(vl, "", TAB_ICON_PLUS);

        return tab;
    }
}
