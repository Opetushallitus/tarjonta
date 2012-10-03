/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.tarjonta.poc.ui.view.koulutus;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.event.MouseEvents;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.poc.ui.TarjontaPresenter;
import fi.vm.sade.tarjonta.poc.ui.components.OhjePopupComponent;
import fi.vm.sade.tarjonta.poc.ui.enums.Notification;
import fi.vm.sade.tarjonta.poc.ui.model.KoulutusLinkkiDTO;
import fi.vm.sade.tarjonta.poc.ui.model.KoulutusPerustiedotDTO;
import fi.vm.sade.tarjonta.poc.ui.model.KoulutusYhteyshenkiloDTO;
import fi.vm.sade.tarjonta.poc.ui.helper.I18NHelper;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.constants.LabelStyleEnum;
import fi.vm.sade.vaadin.constants.StyleEnum;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.tarjonta.poc.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.poc.ui.view.common.AutoSizeVerticalLayout;
import fi.vm.sade.vaadin.util.UiUtil;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.vaadin.addon.formbinder.ViewBoundForm;

/**
 *
 * @author mlyly
 */
@Configurable(preConstruction = true)
public class EditKoulutusPerustiedotView extends AutoSizeVerticalLayout {

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
    // TODO should be set from outside/presenter
    private KoulutusPerustiedotDTO _dto = new KoulutusPerustiedotDTO();

    public EditKoulutusPerustiedotView() {
        super(Type.PCT_100, Type.AUTOSIZE);
        setMargin(true, true, true, true);
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

        HorizontalLayout hlButtonsTop = UiUtil.horizontalLayout(true, UiMarginEnum.NONE);
        hlButtonsTop.setSizeUndefined();
        addComponent(hlButtonsTop);
        addButton("Peruuta", "doCancel", hlButtonsTop,  StyleEnum.STYLE_BUTTON_SECONDARY);
        addButton("TallennaLuonnoksena", "doSaveIncomplete", hlButtonsTop,  StyleEnum.STYLE_BUTTON_PRIMARY);
        addButton("TallennaValmiina", "doSaveComplete", hlButtonsTop,  StyleEnum.STYLE_BUTTON_PRIMARY);
        addButton("Jatka", "doContinue", hlButtonsTop,  StyleEnum.STYLE_BUTTON_PRIMARY);

        addComponent(addLabel("KoulutuksenPerustiedot", LabelStyleEnum.H2, null));

        UiUtil.hr(this);

        GridLayout grid = new GridLayout(3, 1);
        grid.setSizeFull();
        // grid.setSpacing(true);
        addComponent(grid);
        {
            grid.addComponent(addLabel("KoulutusTaiKoulutusOhjelma", null));

            KoodistoComponent kc = addKoodistoComboBox(_koodistoUriKoulutus, mi, "koulutus", "KoulutusTaiKoulutusOhjelma.prompt", null);
            grid.addComponent(kc);
            grid.addComponent(new OhjePopupComponent(i18n.getMessage("LOREMIPSUM"), "500px", "300px"));
            grid.newLine();
        }

        grid.addComponent(addLabel("KoulutuksenTyyppi", null));
        grid.addComponent(addKoodistoComboBox(_koodistoUriKoulutus, mi, "koulutusTyyppi", "KoulutuksenTyyppi.prompt", null));
        grid.newLine();

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

            VerticalLayout vl = UiUtil.verticalLayout();

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
            VerticalLayout vl = UiUtil.verticalLayout();
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

        {
            grid.addComponent(addLabel("KoulutusOnMaksullista", null));
            grid.addComponent(addCheckBox("KoulutusOnMaksullista.checkbox", mi, "koulutusOnMaksullista", null, this));
            grid.newLine();

            grid.addComponent(addLabel("StipendiMahdollisuus", null));
            grid.addComponent(addCheckBox("StipendiMahdollisuus.checkbox", mi, "koulutusStipendimahdollisuus", null, this));

        }
        UiUtil.hr(this);
        addYhteyshenkiloSelectorAndEditor(this);
        UiUtil.hr(this);
        addLinkkiSelectorAndEditor(this, mi);

        HorizontalLayout hlButtonsBottom = new HorizontalLayout();
        hlButtonsBottom.setSizeUndefined();
        hlButtonsBottom.setSpacing(true);
        addComponent(hlButtonsBottom);
        addButton("Peruuta", "doCancel", hlButtonsBottom, StyleEnum.STYLE_BUTTON_SECONDARY);
        addButton("TallennaLuonnoksena", "doSaveIncomplete", hlButtonsBottom,  StyleEnum.STYLE_BUTTON_PRIMARY);
        addButton("TallennaValmiina", "doSaveComplete", hlButtonsBottom, StyleEnum.STYLE_BUTTON_PRIMARY);
        addButton("Jatka", "doContinue", hlButtonsBottom,  StyleEnum.STYLE_BUTTON_PRIMARY);
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
    private TextField addTextField(PropertysetItem psi, String expression, String promptKey, String width, AbstractLayout layout) {
        TextField c = UiUtil.textField(layout,psi, expression, null, i18n.getMessage(promptKey));
        // c.setImmediate(true);

        if (width != null) {
            c.setWidth(width);
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
    private Button addButton(String captionKey, String onClickMethodName, AbstractOrderedLayout layout, StyleEnum styles) {
        Button c = UiUtil.button(layout, i18n.getMessage(captionKey));
        if (onClickMethodName != null) {
            c.addListener(getClickListener(onClickMethodName));
        }

        if (styles != null) {
            for (String style : styles.getStyles()) {
                c.addStyleName(style);
            }
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
        Label c = UiUtil.label(layout, i18n.getMessage(captionKey));
        return c;
    }

    /**
     * Create label with style.
     *
     * @param captionKey
     * @param style
     * @param layout
     * @return
     */
    private Label addLabel(String captionKey, LabelStyleEnum style, AbstractOrderedLayout layout) {
        Label c = UiUtil.label(layout,i18n.getMessage(captionKey), style);
        return c;
    }

    /**
     * Simple model bound label.
     *
     * @param psi
     * @param expression
     * @param layout
     * @return
     */
    private Label addLabel(PropertysetItem psi, String expression, AbstractOrderedLayout layout) {
        Label c = UiUtil.label(layout,psi, expression);
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
        DateField c = UiUtil.dateField(layout,null, null, psi, expression);
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
        CheckBox c = UiUtil.checkBox(layout, i18n.getMessage(captionKey), psi, expression);

        // Routes "clicks" to methods
        if (valueChangeListenerMethod != null) {
            c.addListener(getValueChangeListener(valueChangeListenerMethod));
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
        return UiBuilder.koodistoComboBox(layout, koodistoUri, psi, expression, i18n.getMessage(promptKey));
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
        return UiBuilder.koodistoTwinColSelect(layout, koodistoUri, psi, expression, null);
    }

    /**
     * Create yhteystiedot part of the form.
     *
     * @param layout
     */
    private void addYhteyshenkiloSelectorAndEditor(VerticalLayout layout) {
        layout.addComponent(addLabel("Yhteyshenkilo", null));

        final BeanItemContainer<KoulutusYhteyshenkiloDTO> yhteyshenkiloContainer =
                new BeanItemContainer<KoulutusYhteyshenkiloDTO>(KoulutusYhteyshenkiloDTO.class);
        yhteyshenkiloContainer.addAll(_dto.getYhteyshenkilot());

        HorizontalLayout hl = UiUtil.horizontalLayout(true, UiMarginEnum.NONE);
        hl.setSpacing(true);

        final Table t = new Table(null, yhteyshenkiloContainer);
        t.setPageLength(10);
        t.setSizeFull();
        t.setSelectable(true);
        t.setImmediate(true);

        hl.addComponent(t);

        t.setColumnHeader("email", "Sähköposti");
        t.setColumnHeader("puhelin", "Puhelin");
        t.setColumnHeader("nimi", "Nimi");
        t.setColumnHeader("kielet", "Pätee kielille");

        t.setVisibleColumns(new Object[]{"nimi", "titteli", "email", "puhelin"});

        t.setColumnWidth("email", 25);
        t.setColumnWidth("puhelin", 25);
        t.setColumnWidth("nimi", 25);
        t.setColumnWidth("kielet", 25);

        Button b = addButton("Yhteyshenkilo.LisaaUusi", null, null, null);
        b.addStyleName(Oph.BUTTON_DEFAULT);
        b.addStyleName(Oph.BUTTON_PLUS);
        b.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                KoulutusYhteyshenkiloDTO dto = new KoulutusYhteyshenkiloDTO();
                BeanItem<KoulutusYhteyshenkiloDTO> bi = yhteyshenkiloContainer.addItem(dto);
                t.select(dto);
            }
        });

        EditKoulutusPerustiedotYhteystietoView editor = new EditKoulutusPerustiedotYhteystietoView();

        final Form f = new ViewBoundForm(editor);
        f.setSizeFull();
        f.setWriteThrough(false);
        f.setEnabled(false);
        layout.addComponent(b);
        hl.addComponent(f);

        // Span editor and table to two columns
        layout.addComponent(hl);

        hl.setExpandRatio(f, 0.4f);
        hl.setExpandRatio(t, 0.6f);


        //
        // Table selection, update form to edit correct item
        //
        t.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                KoulutusYhteyshenkiloDTO selected = (KoulutusYhteyshenkiloDTO) event.getProperty().getValue();
                f.setEnabled(selected != null);
                if (selected != null) {
                    f.setItemDataSource(new BeanItem(selected));
                }
            }
        });

        //
        // Editor actions, commit form and refresh tabel data
        //
        editor.addListener(new Listener() {
            @Override
            public void componentEvent(Event event) {
                if (event instanceof EditKoulutusPerustiedotYhteystietoView.CancelEvent) {
                    f.discard();
                }
                if (event instanceof EditKoulutusPerustiedotYhteystietoView.SaveEvent) {
                    f.commit();
                    t.refreshRowCache();
                }
                if (event instanceof EditKoulutusPerustiedotYhteystietoView.DeleteEvent) {
                    KoulutusYhteyshenkiloDTO dto = (KoulutusYhteyshenkiloDTO) t.getValue();
                    if (dto != null) {
                        yhteyshenkiloContainer.removeItem(dto);
                        f.setItemDataSource(null);
                        f.setEnabled(false);
                    }
                    getWindow().showNotification(i18n.getMessage("Poistettu"));

                    // Autoselect in table
                    if (yhteyshenkiloContainer.firstItemId() != null) {
                        t.setValue(yhteyshenkiloContainer.firstItemId());
                    }
                }
            }
        });

        // Autoseelect first
        if (!_dto.getYhteyshenkilot().isEmpty()) {
            t.setValue(_dto.getYhteyshenkilot().get(0));
        }

    }

    private void addLinkkiSelectorAndEditor(VerticalLayout layout, PropertysetItem mi) {


        // Datasource
        final BeanItemContainer<KoulutusLinkkiDTO> linkkiContainer = new BeanItemContainer<KoulutusLinkkiDTO>(KoulutusLinkkiDTO.class);
        linkkiContainer.addAll(_dto.getKoulutusLinkit());

        layout.addComponent(addLabel("Linkit", null));
        HorizontalLayout hl = UiUtil.horizontalLayout(true, UiMarginEnum.NONE);
        hl.setSpacing(true);

        final Table t = new Table(null, linkkiContainer);
        t.setPageLength(10);
        t.setSizeFull();
        t.setSelectable(true);
        t.setImmediate(true);


        hl.addComponent(t);

        t.setColumnHeader("linkkityyppi", i18n.getMessage("Linkkityyppi"));
        t.setColumnHeader("url", i18n.getMessage("LinkkiURL"));
        t.setColumnHeader("kielet", i18n.getMessage("LinkkiKielet"));

        t.setVisibleColumns(new Object[]{"linkkityyppi", "url", "kielet"});


        Button btnAddNewLinkki = addButton("Linkki.LisaaUusi", null, null, null);
        btnAddNewLinkki.addStyleName(Oph.BUTTON_DEFAULT);
        btnAddNewLinkki.addStyleName(Oph.BUTTON_PLUS);
        btnAddNewLinkki.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                KoulutusLinkkiDTO dto = new KoulutusLinkkiDTO();
                linkkiContainer.addItem(dto);
                t.select(dto);
            }
        });
        layout.addComponent(btnAddNewLinkki);


        // Link data editing

        EditKoulutusPerustiedotLinkkiView editor = new EditKoulutusPerustiedotLinkkiView();
        final Form f = new ViewBoundForm(editor);
        f.setWriteThrough(false);
        f.setEnabled(false);
        f.setSizeFull();

        hl.addComponent(f);

        //
        // Table selection, update form to edit correct item
        //
        t.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                KoulutusLinkkiDTO selected = (KoulutusLinkkiDTO) event.getProperty().getValue();
                f.setEnabled(selected != null);
                if (selected != null) {
                    f.setItemDataSource(new BeanItem(selected));
                }
            }
        });


        //
        // Editor actions, commit form and refresh tabel data
        //
        editor.addListener(new Listener() {
            @Override
            public void componentEvent(Event event) {
                if (event instanceof EditKoulutusPerustiedotLinkkiView.CancelEvent) {
                    f.discard();
                }
                if (event instanceof EditKoulutusPerustiedotLinkkiView.SaveEvent) {
                    f.commit();
                    t.refreshRowCache();
                }
                if (event instanceof EditKoulutusPerustiedotLinkkiView.DeleteEvent) {
                    KoulutusLinkkiDTO dto = (KoulutusLinkkiDTO) t.getValue();
                    if (dto != null) {
                        linkkiContainer.removeItem(dto);
                        f.setItemDataSource(null);
                        f.setEnabled(false);
                    }
                    getWindow().showNotification(i18n.getMessage("Poistettu"));

                    // Autoselect in table
                    if (linkkiContainer.firstItemId() != null) {
                        t.setValue(linkkiContainer.firstItemId());
                    }
                }
            }
        });

        // Autoseelect first of initial data
        if (!_dto.getKoulutusLinkit().isEmpty()) {
            t.setValue(_dto.getKoulutusLinkit().get(0));
        }

        hl.setExpandRatio(f, 0.4f);
        hl.setExpandRatio(t, 0.6f);
        layout.addComponent(hl);
    }

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
        // TODO Check for changes, ask "really?" if any
        _presenter.showMainKoulutusView();
    }

    public void doSaveIncomplete() {
        LOG.info("doSaveIncomplete(): dto={}", _dto);
        // TODO validate
        _presenter.saveKoulutusPerustiedot(false);
        _presenter.demoInformation(Notification.SAVE_DRAFT);
    }

    public void doSaveComplete() {
        LOG.info("doSaveComplete(): dto={}", _dto);
        // TODO validate
        _presenter.saveKoulutusPerustiedot(true);
        _presenter.demoInformation(Notification.SAVE);
    }

    public void doContinue() {
        LOG.info("doContinue()");
        // TODO check for changes, ask to save if any
        // TODO go to "overview page"?
        _presenter.showShowKoulutusView();
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

    /*
     * Display some help
     */
    public void onTopHelpClicked() {
        LOG.info("onTopHelpClicked()");
    }

    public void onBottomHelpClicked() {
        LOG.info("onBottomHelpClicked()");
    }

    /*
     * Yhteyshenkilö area
     */
    public void onAddNewYhteyshenkilo() {
        LOG.info("onAddNewYhteyshenkilo()");
    }

    public void onRemoveYhteyshenkilo() {
        LOG.info("onAddNewYhteyshenkilo()");
    }
}
