/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.vm.sade.tarjonta.ui.model.view;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.event.MouseEvents;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.ui.component.CaptionFormatter;
import fi.vm.sade.generic.ui.component.FieldValueFormatter;
import fi.vm.sade.koodisto.service.types.dto.KoodiDTO;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.koodisto.widget.factory.WidgetFactory;
import fi.vm.sade.tarjonta.ui.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.model.KoulutusPerustiedotDTO;
import fi.vm.sade.tarjonta.ui.poc.helper.I18NHelper;
import fi.vm.sade.tarjonta.ui.poc.helper.KeyValueBean;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.oph.helper.UiBuilder;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author mlyly
 */
@Configurable(preConstruction = true)
public class EditKoulutusPerustiedotView extends VerticalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(EditKoulutusPerustiedotView.class);
    @Autowired(required = true)
    private TarjontaPresenter _presenter;
    private I18NHelper i18n = new I18NHelper(this);
    @Value("${koodisto-uris.kieli:http://kieli}")
    private String _koodistoUriKieli;
    @Value("${koodisto-uris.kieli:http://teema}")
    private String _koodistoUriTeema;
    @Value("${koodisto-uris.koulutus:http://koulutus}")
    private String _koodistoUriKoulutus;
    @Value("${koodisto-uris.suunniteltuKesto:http://suunniteltuKesto}")
    private String _koodistoUriSuunniteltuKesto;
    @Value("${koodisto-uris.opetusmuoto:http://opetusmuoto}")
    private String _koodistoUriOpetusmuoto;
    @Value("${koodisto-uris.koulutuslaji:http://koulutuslaji}")
    private String _koodistoUriKoulutuslaji;
    // TODO should be set from outside
    private KoulutusPerustiedotDTO _dto = new KoulutusPerustiedotDTO();

    public EditKoulutusPerustiedotView() {
        super();
        setSizeFull();
        setMargin(true, false, true, true);
        setSpacing(true);

        // Remove, when we get data from outside - then build the UI.
        initialize();
    }

    //
    // Define data fields
    //
    private void initialize() {
        LOG.info("initialize()");

        // Data model
        BeanItem<KoulutusPerustiedotDTO> mi = new BeanItem<KoulutusPerustiedotDTO>(_dto);

        removeAllComponents();

        HorizontalLayout hlButtonsTop = new HorizontalLayout();
        hlButtonsTop.setSpacing(true);
        addComponent(hlButtonsTop);
        addButton("Peruuta", "doCancel", hlButtonsTop);
        addButton("TallennaLuonnoksena", "doSaveIncomplete", hlButtonsTop);
        addButton("TallennaValmiina", "doSaveComplete", hlButtonsTop);
        addButton("Jatka", "doContinue", hlButtonsTop);

        addComponent(new Label(i18n.getMessage("KoulutuksenPerustiedot")));

        GridLayout grid = new GridLayout(3, 1);
        grid.setSizeFull();
        // grid.setSpacing(true);
        addComponent(grid);
        {
            grid.addComponent(addLabel("KoulutusTaiKoulutusOhjelma", null));

            KoodistoComponent kc = addKoodistoComboBox(_koodistoUriKoulutus, mi, "koulutus", "KoulutusTaiKoulutusOhjelma.prompt", null);
            grid.addComponent(kc);
            // grid.addComponent(addHelpIcon("http://png.findicons.com/files/icons/2360/spirit20/20/system_question_alt_02.png", "onTopHelpClicked"));
            grid.newLine();
        }


        grid.addComponent(addLabel("Koulutusala", null));
        grid.addComponent(addLabel(mi, "koulutusala", null));
        grid.newLine();
        grid.addComponent(addLabel("Tutkinto", null));
        grid.addComponent(addLabel(mi, "tutkinto", null));
        grid.newLine();
        grid.addComponent(addLabel("Tutkintonimike", null));
        grid.addComponent(addLabel(mi, "tutkintonimike", null));
        grid.newLine();
        grid.addComponent(addLabel("OpintojenLaajuusyksikko", null));
        grid.addComponent(addLabel(mi, "opintojenlaajuusyksikko", null));
        grid.newLine();
        grid.addComponent(addLabel("OpintojenLaajuus", null));
        grid.addComponent(addLabel(mi, "opintojenlaajuus", null));
        grid.newLine();
        grid.addComponent(addLabel("Opintoala", null));
        grid.addComponent(addLabel(mi, "opintoala", null));
        grid.newLine();

        {
            grid.addComponent(addLabel("Opetuskieli", null));

            VerticalLayout vl = UiBuilder.newVerticalLayout();

            KoodistoComponent kc = addKoodistoTwinColSelect(_koodistoUriKieli, mi, "opetuskielet", null);
            vl.addComponent(kc);
            kc.addListener(getValueChangeListener("doOpetuskieletChanged"));

            vl.addComponent(addCheckBox("Opetuskieli.ValitseKaikki", mi, "opetuskieletKaikki", "doOpetuskieletSelectAll", null));
            grid.addComponent(vl);

            grid.newLine();
        }

        grid.addComponent(addLabel("KoulutuksenAlkamisPvm", null));
        grid.addComponent(addDate(mi, "koulutuksenAlkamisPvm", null));
        grid.newLine();

        {
            grid.addComponent(addLabel("SuunniteltuKesto", null));
            HorizontalLayout hl = new HorizontalLayout();
            hl.setSpacing(true);
            hl.addComponent(addTextField(mi, "suunniteltuKesto", "SuunniteltuKesto.prompt", null, null));
            KoodistoComponent kc = addKoodistoComboBox(_koodistoUriSuunniteltuKesto, mi, "suunniteltuKestoTyyppi", "SuunniteltuKesto.tyyppi.prompt", hl);
            grid.addComponent(hl);
            grid.newLine();
        }

        {
            grid.addComponent(addLabel("Teema", null));
            VerticalLayout vl = UiBuilder.newVerticalLayout();
            vl.addComponent(addLabel("ValitseTeemat", null));
            KoodistoComponent kc = addKoodistoTwinColSelect(_koodistoUriTeema, mi, "teemat", null);
            vl.addComponent(kc);
            grid.addComponent(vl);

            grid.newLine();
        }
        grid.addComponent(addLabel("SuuntautumisvaihtoehtoTaiPainotus", null));
        grid.newLine();
        {
            grid.addComponent(addLabel("Opetusmuoto", null));
            grid.addComponent(addKoodistoComboBox(_koodistoUriOpetusmuoto, mi, "opetusmuoto", "Opetusmuoto.prompt", null));
            grid.newLine();
        }
        {
            grid.addComponent(addLabel("Koulutuslaji", null));
            grid.addComponent(addKoodistoComboBox(_koodistoUriKoulutuslaji, mi, "koulutuslaji", "Koulutuslaji.prompt", null));
            grid.newLine();
        }

        if (true) {
            // TODO - dynamic addition with plus!
            // TODO - contack persons for given languages
            grid.addComponent(addLabel("Yhteyshenkilo", null));

            HorizontalLayout hl = new HorizontalLayout();
            hl.setSpacing(true);
            VerticalLayout vl = UiBuilder.newVerticalLayout();
            vl.setSpacing(true);

            mi.addItemProperty("yhteyshenkilo.nimi", new NestedMethodProperty(_dto, "yhteyshenkilo.nimi"));
            mi.addItemProperty("yhteyshenkilo.titteli", new NestedMethodProperty(_dto, "yhteyshenkilo.titteli"));
            mi.addItemProperty("yhteyshenkilo.email", new NestedMethodProperty(_dto, "yhteyshenkilo.email"));
            mi.addItemProperty("yhteyshenkilo.puhelin", new NestedMethodProperty(_dto, "yhteyshenkilo.puhelin"));
            mi.addItemProperty("yhteyshenkilo.kielet", new NestedMethodProperty(_dto, "yhteyshenkilo.kielet"));

            vl.addComponent(addTextField(mi, "yhteyshenkilo.nimi", "Yhteyshenkilo.Nimi.prompt", null, null));
            vl.addComponent(addTextField(mi, "yhteyshenkilo.titteli", "Yhteyshenkilo.Titteli.prompt", null, null));
            vl.addComponent(addTextField(mi, "yhteyshenkilo.email", "Yhteyshenkilo.Email.prompt", null, null));
            vl.addComponent(addTextField(mi, "yhteyshenkilo.puhelin", "Yhteyshenkilo.Puhelin.prompt", null, null));

            hl.addComponent(vl);
            hl.addComponent(addKoodistoTwinColSelect(_koodistoUriKieli, mi, "yhteyshenkilo.kielet", null));

            Button b = addButton("Yhteyshenkilo.LisaaUusi", "onAddNewYhteyshenkilo", null);
            b.setStyleName(Oph.BUTTON_PLUS);
            hl.addComponent(b);

            grid.addComponent(hl);

            grid.newLine();
        }

        {
            // TODO multiple
            grid.addComponent(addLabel("LinkkiOpetussunnitelmaan", null));
            VerticalLayout vl = UiBuilder.newVerticalLayout();
            vl.addComponent(addTextField(mi, "linkkiOpetussuunnitelma", "LinkkiOpetussunnitelmaan.prompt", null, null));
            vl.addComponent(addCheckBox("LinkkiOpetussunnitelmaan.eriOpetusKielet", mi, null, "doMultipleLinksForOpetussuunnitelma", null));
            grid.addComponent(vl);
            grid.newLine();
        }

        {
            // TODO multiple
            grid.addComponent(addLabel("LinkkiOppilaitokseen", null));
            VerticalLayout vl = UiBuilder.newVerticalLayout();
            vl.addComponent(addTextField(mi, "linkkiOppilaitokseen", "LinkkiOppilaitokseen.prompt", null, null));
            vl.addComponent(addCheckBox("LinkkiOppilaitokseen.eriOpetusKielet", mi, null, "doMultipleLinksForOppilaitos", null));
            grid.addComponent(vl);
            grid.newLine();
        }

        {
            // TODO multiple
            grid.addComponent(addLabel("LinkkiSOME", null));
            VerticalLayout vl = UiBuilder.newVerticalLayout();
            vl.addComponent(addTextField(mi, "linkkiSOME", "LinkkiSOME.prompt", null, null));
            vl.addComponent(addCheckBox("LinkkiSOME.eriOpetusKielet", mi, null, "doMultipleLinksForSOME", null));
            grid.addComponent(vl);
            grid.newLine();
        }

        {
            // TODO multiple
            grid.addComponent(addLabel("LinkkiMultimedia", null));
            VerticalLayout vl = UiBuilder.newVerticalLayout();
            vl.addComponent(addTextField(mi, "linkkiMultimedia", "LinkkiMultimedia.prompt", null, null));
            vl.addComponent(addCheckBox("LinkkiMultimedia.eriOpetusKielet", mi, null, "doMultipleLinksForMultimedia", null));
            grid.addComponent(vl);
            grid.newLine();
        }

        {
            // TODO multiple
            grid.addComponent(addCheckBox("KoulutusOnMaksullista", mi, "koulutusOnMaksullista", null, this));
            VerticalLayout vl = UiBuilder.newVerticalLayout();
            vl.addComponent(addTextField(mi, "linkkiMaksullisuus", "KoulutusOnMaksullista.prompt", null, null));
            vl.addComponent(addCheckBox("KoulutusOnMaksullista.eriOpetusKielet", mi, null, "doMultipleLinksForMaksullisuus", null));
            grid.addComponent(vl);
            grid.newLine();
        }

        {
            // TODO multiple
            grid.addComponent(addCheckBox("StipendiMahdollisuus", mi, "koulutusStipendiMahdollisuus", null, this));
            VerticalLayout vl = UiBuilder.newVerticalLayout();
            vl.addComponent(addTextField(mi, "linkkiStipendiMahdollisuus", "StipendiMahdollisuus.prompt", null, null));
            vl.addComponent(addCheckBox("StipendiMahdollisuus.eriOpetusKielet", mi, null, "doMultipleLinksForStipendi", null));
            grid.addComponent(vl);
            grid.newLine();
        }


        HorizontalLayout hlButtonsBottom = new HorizontalLayout();
        hlButtonsBottom.setSpacing(true);
        addComponent(hlButtonsBottom);
        addButton("Peruuta", "doCancel", hlButtonsBottom);
        addButton("TallennaLuonnoksena", "doSaveIncomplete", hlButtonsBottom);
        addButton("TallennaValmiina", "doSaveComplete", hlButtonsBottom);
        addButton("Jatka", "doContinue", hlButtonsBottom);
    }


    /*
     * UI HELPERS TO CREATE COMPONENTS
     */
    /**
     * Add basic TextField with bound data.
     *
     * @param psi
     * @param expression
     * @param promptKey
     * @param width
     * @param layout
     * @return
     */
    private TextField addTextField(PropertysetItem psi, String expression, String promptKey, String width, AbstractOrderedLayout layout) {
        TextField c = new TextField();

        if (psi != null && expression != null) {
            c.setPropertyDataSource(psi.getItemProperty(expression));
        }

        // tf.setImmediate(true);
        c.setNullRepresentation("");
        c.addStyleName(Oph.TEXTFIELD_SEARCH);

        if (width != null) {
            c.setWidth(width);
        }


        if (promptKey != null) {
            c.setInputPrompt(i18n.getMessage(promptKey));
        }

        if (layout != null) {
            layout.addComponent(c);
        }


        return c;
    }

    /**
     * Create a button.
     *
     * @param captionKey
     * @param onClickMethodName
     * @param layout
     * @return
     */
    private Button addButton(String captionKey, String onClickMethodName, AbstractOrderedLayout layout) {
        Button c = new Button(i18n.getMessage(captionKey));
        c.setImmediate(true);

        if (onClickMethodName != null) {
            c.addListener(getClickListener(onClickMethodName));
        }

        c.addStyleName(Oph.BUTTON_PRIMARY);
        c.addStyleName(Oph.BUTTON_SMALL);

        if (layout != null) {
            layout.addComponent(c);
        }

        return c;
    }

    /**
     * Static localized Label.
     *
     * @param captionKey
     * @param layout
     * @return
     */
    private Label addLabel(String captionKey, AbstractOrderedLayout layout) {
        Label c = new Label(i18n.getMessage(captionKey));
        // c.addStyleName(Oph.LABEL_SMALL);

        if (layout != null) {
            layout.addComponent(c);
        }

        return c;
    }

    /**
     * Simple model bould label.
     *
     * @param psi
     * @param expression
     * @param layout
     * @return
     */
    private Label addLabel(PropertysetItem psi, String expression, AbstractOrderedLayout layout) {
        Label c = new Label();

        if (psi != null && expression != null) {
            c.setPropertyDataSource(psi.getItemProperty(expression));
        }

        // c.addStyleName(Oph.LABEL_SMALL);

        if (layout != null) {
            layout.addComponent(c);
        }

        return c;
    }

    /**
     * Create DateField, bind to model. By default format is "dd.MM.yyyy".
     *
     * @param psi
     * @param expression
     * @param layout
     * @return
     */
    private DateField addDate(PropertysetItem psi, String expression, AbstractOrderedLayout layout) {
        DateField c = new DateField();
        c.setDateFormat("dd.MM.yyyy");

        if (psi != null && expression != null) {
            c.setPropertyDataSource(psi.getItemProperty(expression));
        }

        // TODO what is the style?
        // df.addStyleName(Oph.DATE);

        if (layout != null) {
            layout.addComponent(c);
        }

        return c;
    }

    /**
     * Create ComboBox and bind to model.
     *
     * @param psi
     * @param expression
     * @param inputPromptKey
     * @param container
     * @param valueChangeListenerMethod
     * @param layout
     * @return
     */
    private ComboBox addComboBox(PropertysetItem psi, String expression, String inputPromptKey, Container container, String valueChangeListenerMethod, AbstractOrderedLayout layout) {
        ComboBox c = new ComboBox();
        // TODO cb.addStyleName(Oph.COMBOBOX);
        c.setImmediate(true);

        if (valueChangeListenerMethod != null) {
            c.addListener(getValueChangeListener(valueChangeListenerMethod));
        }

        if (inputPromptKey != null) {
            c.setInputPrompt(i18n.getMessage(inputPromptKey));
        }

        // Data to be shown
        if (container != null) {
            c.setContainerDataSource(container);
        }

        // Selected data
        if (psi != null && expression != null) {
            c.setPropertyDataSource(psi.getItemProperty(expression));
        }

        if (layout != null) {
            layout.addComponent(c);
        }

        c.setItemCaptionPropertyId(KeyValueBean.VALUE);

        return c;
    }

    /**
     * Create TwinColSelect and bind it to a model.
     *
     * @param psi
     * @param expression
     * @param container
     * @param valueChangeListenerMethod
     * @param layout
     * @return
     */
    private TwinColSelect addTwinColSelect(PropertysetItem psi, String expression, Container container, String valueChangeListenerMethod, AbstractOrderedLayout layout) {
        TwinColSelect c = new TwinColSelect();

        // List values only!
        c.setMultiSelect(true);

        // TODO s.addStyleName(Oph.TWINCOLSELECT);

        c.setImmediate(true);

        if (valueChangeListenerMethod != null) {
            c.addListener(getValueChangeListener(valueChangeListenerMethod));
        }

        // Data to be shown
        if (container != null) {
            c.setContainerDataSource(container);
        }

        // Selected data
        if (psi != null && expression != null) {
            c.setPropertyDataSource(psi.getItemProperty(expression));
        }

        if (layout != null) {
            layout.addComponent(c);
        }

        c.setItemCaptionPropertyId(KeyValueBean.VALUE);

        return c;
    }

    /**
     * Create CheckBox and bind it to model.
     *
     * @param captionKey
     * @param psi
     * @param expression
     * @param valueChangeListenerMethod
     * @param layout
     * @return
     */
    private CheckBox addCheckBox(String captionKey, PropertysetItem psi, String expression, String valueChangeListenerMethod, AbstractOrderedLayout layout) {
        CheckBox c = new CheckBox(i18n.getMessage(captionKey));

        // Selected data
        if (psi != null && expression != null) {
            c.setPropertyDataSource(psi.getItemProperty(expression));
        }

        // Routes "clicks" to methods
        if (valueChangeListenerMethod != null) {
            c.addListener(getValueChangeListener(valueChangeListenerMethod));
        }

        if (layout != null) {
            layout.addComponent(c);
        }

        c.setImmediate(true);

        return c;
    }

    /**
     * Create icon as Embedded external resources.
     *
     * @param iconUrl
     * @param onClickListenerMethod
     * @return
     */
    private Embedded addHelpIcon(String iconUrl, String onClickListenerMethod) {
        Embedded helpIcon1 = new Embedded("", new ExternalResource(iconUrl));
        helpIcon1.setImmediate(true);

        if (onClickListenerMethod != null) {
            helpIcon1.addListener(getMouseClickListener(onClickListenerMethod));
        }

        return helpIcon1;
    }

    /**
     * Create KoodistoComponent with CompboBox as displaying widget and bind to
     * model.
     *
     * @param koodistoUri
     * @param psi
     * @param expression
     * @param promptKey
     * @param layout
     * @return
     */
    private KoodistoComponent addKoodistoComboBox(final String koodistoUri, PropertysetItem psi, String expression, String promptKey, AbstractOrderedLayout layout) {

        LOG.debug("addKoodistoComboBox({}, ...)", koodistoUri);

        // Koodisto displayed in ComboBox
        ComboBox combo = new ComboBox();
        combo.setFilteringMode(AbstractSelect.Filtering.FILTERINGMODE_CONTAINS);
        combo.setInputPrompt(i18n.getMessage(promptKey));
        // TODO combo.addStyleName(Oph.XXX);

        final KoodistoComponent c = WidgetFactory.create(koodistoUri);

        // Wire koodisto to combobox
        c.setField(combo);

        // Set to be immediate
        c.setImmediate(true);

        // DISPLAYED text
        c.setCaptionFormatter(new CaptionFormatter() {
            @Override
            public String formatCaption(Object dto) {
                LOG.debug("formatCaption({})", dto);

                if (dto instanceof KoodiDTO) {
                    KoodiDTO kdto = (KoodiDTO) dto;
                    return kdto.getKoodiArvo();
                } else {
                    LOG.info("! KoodiDTO???");
                    return "" + dto;
                }
            }
        });

        // BOUND value
        c.setFieldValueFormatter(new FieldValueFormatter() {
            @Override
            public Object formatFieldValue(Object dto) {
                LOG.debug("formatFieldValue({})", dto);

                if (dto instanceof KoodiDTO) {
                    KoodiDTO kdto = (KoodiDTO) dto;
                    return kdto.getKoodiUri();
                } else {
                    LOG.info("! KoodiDTO???");
                    return "" + dto;
                }
            }
        });

        // Selected data bound there
        if (psi != null && expression != null) {
            c.setPropertyDataSource(psi.getItemProperty(expression));
        }

        if (layout != null) {
            layout.addComponent(c);
        }

        return c;
    }

    /**
     * Create KoodistoComponent with TwinColSelect as widget and bind to model.
     *
     * @param koodistoUri
     * @param psi
     * @param expression
     * @param layout
     * @return
     */
    private KoodistoComponent addKoodistoTwinColSelect(final String koodistoUri, PropertysetItem psi, String expression, AbstractOrderedLayout layout) {

        LOG.debug("addKoodistoTwinColSelect({}, ...)", koodistoUri);

        // Koodisto displayed in TwinColSelect
        TwinColSelect combo = new TwinColSelect();

        // Only multiple (list) values!
        combo.setMultiSelect(true);

        final KoodistoComponent c = WidgetFactory.create(koodistoUri);

        // Wire koodisto to combobox
        c.setField(combo);

        // Set to be immediate
        c.setImmediate(true);

        // DISPLAYED text
        c.setCaptionFormatter(new CaptionFormatter() {
            @Override
            public String formatCaption(Object dto) {
                LOG.debug("formatCaption({})", dto);

                if (dto instanceof KoodiDTO) {
                    KoodiDTO kdto = (KoodiDTO) dto;
                    return kdto.getKoodiArvo();
                } else {
                    LOG.info("! KoodiDTO???");
                    return "" + dto;
                }
            }
        });

        // BOUND value
        c.setFieldValueFormatter(new FieldValueFormatter() {
            @Override
            public Object formatFieldValue(Object dto) {
                LOG.debug("formatFieldValue({})", dto);

                if (dto instanceof KoodiDTO) {
                    KoodiDTO kdto = (KoodiDTO) dto;
                    return kdto.getKoodiUri();
                } else {
                    LOG.info("! KoodiDTO???");
                    return "" + dto;
                }
            }
        });

        // Selected data bound there
        if (psi != null && expression != null) {
            c.setPropertyDataSource(psi.getItemProperty(expression));
        }

        if (layout != null) {
            layout.addComponent(c);
        }

        return c;
    }

    /*
     * Wiring the view actions with reflection listeners, ie. call methods is this class by name.
     */
    /**
     * Creates a click listener that calls method <string>methodName</string> in
     * this instance.
     *
     * For buttons.
     *
     * @param methodName
     * @return
     */
    private Button.ClickListener getClickListener(final String methodName) {
        final EditKoulutusPerustiedotView target = this;
        final Method m = getMethod(methodName);
        return new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    m.invoke(target);
                } catch (Throwable ex) {
                    LOG.error("invoke of method {} failed, ex={}", methodName, ex);
                }
            }
        };
    }

    /**
     * Creates a mouse click listener that calls method
     * <string>methodName</string> in this instance.
     *
     * For icons etc.
     *
     * @param methodName
     * @return
     */
    private MouseEvents.ClickListener getMouseClickListener(final String methodName) {
        final EditKoulutusPerustiedotView target = this;
        final Method m = getMethod(methodName);

        return new MouseEvents.ClickListener() {
            @Override
            public void click(MouseEvents.ClickEvent event) {
                try {
                    m.invoke(target);
                } catch (Throwable ex) {
                    LOG.error("invoke of method {} failed, ex={}", methodName, ex);
                    LOG.error("", ex);
                }
            }
        };
    }

    /**
     * Creates a value change listener that calls method
     * <string>methodName</string> in this instance.
     *
     * Used for data related "events".
     *
     * @param methodName
     * @return
     */
    private Property.ValueChangeListener getValueChangeListener(final String methodName) {
        final EditKoulutusPerustiedotView target = this;
        final Method m = getMethod(methodName);
        return new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                try {
                    m.invoke(target);
                } catch (Throwable ex) {
                    LOG.error("invoke of method {} failed, ex={}", methodName, ex);
                    LOG.error("", ex);
                }
            }
        };
    }

    /**
     * Get method by name.
     *
     * @param methodName
     * @return
     */
    private Method getMethod(String methodName) {
        try {
            return this.getClass().getMethod(methodName);
        } catch (Throwable ex) {
            LOG.error("Failed to get method: {}", methodName, ex);
            LOG.error("", ex);
            return null;
        }
    }


    /*
     * VIEW ACTIONS IN THE PAGE
     */
    public void doCancel() {
        LOG.info("doCancel()");
    }

    public void doSaveIncomplete() {
        LOG.info("doSaveIncomplete(): dto={}", _dto);
    }

    public void doSaveComplete() {
        LOG.info("doSaveComplete(): dto={}", _dto);
    }

    public void doContinue() {
        LOG.info("doCantinue()");
    }

    public void doMultipleLinksForOpetussuunnitelma() {
        LOG.info("doMultipleLinksForOpetussuunnitelma()");
    }

    public void doMultipleLinksForOppilaitos() {
        LOG.info("doMultipleLinksForOppilaitos()");
    }

    public void doMultipleLinksForSOME() {
        LOG.info("doMultipleLinksForSOME()");
    }

    public void doMultipleLinksForMultimedia() {
        LOG.info("doMultipleLinksForMultimedia()");
    }

    public void doMultipleLinksForMaksullisuus() {
        LOG.info("doMultipleLinksForMaksullisuus()");
    }

    public void doMultipleLinksForStipendi() {
        LOG.info("doMultipleLinksForStipendi()");
    }

    /*
     * Opetuskieli selection has been changed
     */
    public void doOpetuskieletChanged() {
        LOG.info("doOpetuskieletChanged()");
    }

    public void doOpetuskieletSelectAll() {
        LOG.info("doOpetuskieletSelectAll()");
    }

    public void onTopHelpClicked() {
        LOG.info("onTopHelpClicked()");
    }

    public void onBottomHelpClicked() {
        LOG.info("onBottomHelpClicked()");
    }

    public void onAddNewYhteyshenkilo() {
        LOG.info("onAddNewYhteyshenkilo()");
    }
}
