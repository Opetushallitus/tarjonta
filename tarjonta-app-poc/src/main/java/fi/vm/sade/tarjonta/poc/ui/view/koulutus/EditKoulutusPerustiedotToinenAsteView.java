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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.poc.ui.TarjontaPresenter;
import fi.vm.sade.tarjonta.poc.ui.components.OhjePopupComponent;
import fi.vm.sade.tarjonta.poc.ui.enums.Notification;
import fi.vm.sade.tarjonta.poc.ui.helper.BeanItemMapper;
import fi.vm.sade.tarjonta.poc.ui.model.KoulutusLinkkiDTO;
import fi.vm.sade.tarjonta.poc.ui.model.KoulutusPerustiedotDTO;
import fi.vm.sade.tarjonta.poc.ui.model.KoulutusYhteyshenkiloDTO;
import fi.vm.sade.tarjonta.poc.ui.helper.I18NHelper;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.constants.LabelStyleEnum;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.tarjonta.poc.ui.model.KoulutusToisenAsteenPerustiedotDTO;
import fi.vm.sade.vaadin.util.UiUtil;
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
public class EditKoulutusPerustiedotToinenAsteView extends VerticalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(EditKoulutusPerustiedotToinenAsteView.class);
    private I18NHelper i18n = new I18NHelper(this);
    @Autowired(required = true)
    private TarjontaPresenter _presenter;
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
    private KoulutusToisenAsteenPerustiedotDTO _dto = new KoulutusToisenAsteenPerustiedotDTO();
    private BeanItemMapper<KoulutusPerustiedotDTO, EditKoulutusPerustiedotToinenAsteView> bim;

    public EditKoulutusPerustiedotToinenAsteView() {

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
        bim = new BeanItemMapper<KoulutusPerustiedotDTO, EditKoulutusPerustiedotToinenAsteView>(_dto, i18n, this);
        bim.label(this, "KoulutuksenPerustiedot", LabelStyleEnum.H2);

        UiUtil.hr(this);

        GridLayout grid = new GridLayout(4, 1);
        grid.setSizeFull();
        grid.setColumnExpandRatio(0, 0l);
        grid.setColumnExpandRatio(1, 1l);
        grid.setColumnExpandRatio(2, 20l);
        grid.setColumnExpandRatio(3, 1l);
        
        addComponent(grid);
        buildKoulutus(grid, "KoulutusTaiTutkinto");
        buildKoulutusohjelma(grid, "Koulutusohjelma");
        buildKoulutuksenTyyppi(grid, "KoulutuksenTyyppi");
        buildStaticLabelSection(grid);
        buildLanguage(grid, "Opetuskieli");
        buildDates(grid, "KoulutuksenAlkamisPvm");
        buildPainotus(grid, "AdditionalInformation");
        buildKesto(grid, "SuunniteltuKesto");
        buildTeema(grid, "Teema");
        buildOpetusmuoto(grid, "Opetusmuoto");
        buildKoulutuslaji(grid, "Koulutuslaji");
        buildMaksullistaCheckBox(grid, "KoulutusOnMaksullista");
        buildStipendiCheckBoxes(grid, "StipendiMahdollisuus");

        UiUtil.hr(this);
        addYhteyshenkiloSelectorAndEditor(this);
        UiUtil.hr(this);
        addLinkkiSelectorAndEditor(this, bim.getBeanItem());
    }

    private void label(GridLayout grid, String propertyKey) {
        Label label = bim.label(null, propertyKey);
        grid.addComponent(label);
        grid.setComponentAlignment(label, Alignment.TOP_RIGHT);
        grid.addComponent(new Label(""));
    }

    private void buildKoulutus(GridLayout grid, String propertyKey) {
        label(grid, propertyKey);
        KoodistoComponent kc = bim.addKoodistoComboBox(null, _koodistoUriKoulutus, "koulutus", "koulutusTaiTutkinto.prompt");
        grid.addComponent(kc);
        grid.addComponent(new OhjePopupComponent(i18n.getMessage("LOREMIPSUM"), "500px", "300px"));
        grid.newLine();
    }

    private void buildKoulutuksenTyyppi(GridLayout grid, String propertyKey) {
        label(grid, propertyKey);
        grid.addComponent(bim.addLabel(null, "koulutuksenTyyppi"));
        grid.newLine();
    }

    private void buildKoulutusohjelma(GridLayout grid, String propertyKey) {
        label(grid, propertyKey);
        grid.addComponent(bim.addKoodistoComboBox(null, _koodistoUriKoulutus, "koulutusohjelma", "koulutusohjelma.prompt"));
        grid.newLine();
    }

    private void buildStaticLabelSection(GridLayout grid) {
        label(grid, "Koulutusala");
        grid.addComponent(bim.addLabel(null, "koulutusala"));
        grid.newLine();

        label(grid, "Tutkinto");
        grid.addComponent(bim.addLabel(null, "tutkinto"));
        grid.newLine();

        label(grid, "Tutkintonimike");
        grid.addComponent(bim.addLabel(null, "tutkintonimike"));
        grid.newLine();

        label(grid, "OpintojenLaajuusyksikko");
        grid.addComponent(bim.addLabel(null, "opintojenlaajuusyksikko"));
        grid.newLine();

        label(grid, "OpintojenLaajuus");
        grid.addComponent(bim.addLabel(null, "opintojenlaajuus"));
        grid.newLine();

        label(grid, "Opintoala");
        grid.addComponent(bim.addLabel(null, "opintoala"));
        grid.newLine();
    }

    private void buildLanguage(GridLayout grid, String propertyKey) {
        label(grid, propertyKey);
        VerticalLayout vl = UiUtil.verticalLayout();

        KoodistoComponent kc = bim.addKoodistoTwinColSelect(null, _koodistoUriKieli, "opetuskielet");
        vl.addComponent(kc);
        kc.addListener(bim.getValueChangeListener("doOpetuskieletChanged"));

        vl.addComponent(bim.addCheckBox(null, "Opetuskieli.ValitseKaikki", "opetuskieletKaikki", "doOpetuskieletSelectAll"));
        grid.addComponent(vl);

        grid.newLine();
    }

    private void buildDates(GridLayout grid, String propertyKey) {
        label(grid, propertyKey);
        grid.addComponent(bim.addDate(null, "koulutuksenAlkamisPvm"));
        grid.newLine();
    }

    private void buildPainotus(GridLayout grid, String propertyKey) {
        label(grid, propertyKey);
        Button painotus = UiUtil.buttonLink(
                null,
                "Painotus",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        _presenter.showKoulutusAdditionalInfoView();
                    }
                });
        grid.addComponent(painotus);
        grid.newLine();
    }

    private void buildKesto(GridLayout grid, String propertyKey) {
        label(grid, propertyKey);
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        hl.addComponent(bim.addTextField(null, "suunniteltuKesto", "SuunniteltuKesto.prompt", null));
        KoodistoComponent kc = bim.addKoodistoComboBox(hl, _koodistoUriSuunniteltuKesto, "suunniteltuKestoTyyppi", "SuunniteltuKesto.tyyppi.prompt");
        grid.addComponent(hl);
        grid.newLine();

    }

    private void buildTeema(GridLayout grid, String propertyKey) {
        label(grid, propertyKey);

        VerticalLayout vl = UiUtil.verticalLayout();
        vl.addComponent(bim.label(null, "ValitseTeemat"));
        KoodistoComponent kc = bim.addKoodistoTwinColSelect(null, _koodistoUriTeema, "teemat");
        vl.addComponent(kc);
        grid.addComponent(vl);

        grid.newLine();
    }

    private void buildOpetusmuoto(GridLayout grid, String propertyKey) {
        label(grid, propertyKey);

        grid.addComponent(bim.addKoodistoComboBox(null, _koodistoUriOpetusmuoto, "opetusmuoto", "Opetusmuoto.prompt"));
        grid.newLine();

    }

    private void buildKoulutuslaji(GridLayout grid, String propertyKey) {
        label(grid, propertyKey);

        grid.addComponent(bim.addKoodistoComboBox(null, _koodistoUriKoulutuslaji, "koulutuslaji", "Koulutuslaji.prompt"));
        grid.newLine();
    }

    private void buildMaksullistaCheckBox(GridLayout grid, String propertyKey) {
        label(grid, propertyKey);
        grid.addComponent(bim.addCheckBox(this, "KoulutusOnMaksullista.checkbox", "koulutusOnMaksullista", null));
        grid.newLine();
    }

    private void buildStipendiCheckBoxes(GridLayout grid, String propertyKey) {
        label(grid, propertyKey);
        grid.addComponent(bim.addCheckBox(this, "StipendiMahdollisuus.checkbox", "koulutusStipendimahdollisuus", null));
        grid.newLine();
    }

    /**
     * Create yhteystiedot part of the form.
     *
     * @param layout
     */
    private void addYhteyshenkiloSelectorAndEditor(VerticalLayout layout) {
        layout.addComponent(bim.label(null, "Yhteyshenkilo"));

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

        Button b = bim.addButton(null, "Yhteyshenkilo.LisaaUusi", null, null);
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

        layout.addComponent(bim.label(null, "Linkit"));
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


        Button btnAddNewLinkki = bim.addButton(null, "Linkki.LisaaUusi", null, null);
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
