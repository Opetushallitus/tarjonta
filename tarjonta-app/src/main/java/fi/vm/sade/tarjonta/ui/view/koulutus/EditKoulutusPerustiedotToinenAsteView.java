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
package fi.vm.sade.tarjonta.ui.view.koulutus;

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
import fi.vm.sade.tarjonta.ui.helper.BeanItemMapper;
import fi.vm.sade.tarjonta.ui.helper.I18NHelper;
import fi.vm.sade.tarjonta.ui.helper.OhjePopupComponent;
import fi.vm.sade.tarjonta.ui.model.KoulutusLinkkiViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusPerustiedotViewModel;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.constants.LabelStyleEnum;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.tarjonta.ui.model.KoulutusYhteyshenkiloViewModel;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.vaadin.util.UiUtil;
import javax.annotation.PostConstruct;
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
    private BeanItemMapper<KoulutusPerustiedotViewModel, EditKoulutusPerustiedotToinenAsteView> bim;
    private Table yhteyshenkilo;

    public EditKoulutusPerustiedotToinenAsteView() {
        setMargin(true);
        setSpacing(true);
        setHeight(-1, UNITS_PIXELS);
        // Remove, when we get data from outside - then build the UI.
        _presenter.initKoulutusYhteystietoModel();
        initialize();


    }

    //
    // Define data fields
    //
    private void initialize() {
        LOG.info("initialize()");
        bim = new BeanItemMapper<KoulutusPerustiedotViewModel, EditKoulutusPerustiedotToinenAsteView>(_presenter.getKoulutusToisenAsteenPerustiedotViewModel(), i18n, this);
        bim.label(this, "KoulutuksenPerustiedot", LabelStyleEnum.H2);

        UiUtil.hr(this);

        GridLayout grid = new GridLayout(3, 1);
        grid.setSizeFull();
        grid.setColumnExpandRatio(0, 0l);
        grid.setColumnExpandRatio(1, 1l);
        grid.setColumnExpandRatio(2, 20l);

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
        buildStipendiCheckBox(grid, "StipendiMahdollisuus");

        UiUtil.hr(this);
        addYhteyshenkiloSelectorAndEditor(this);
        UiUtil.hr(this);
        addLinkkiSelectorAndEditor(this, bim.getBeanItem());
    }

    @PostConstruct
    public void setDataSource() {
        _presenter.initKoulutusYhteystietoModel();
    }

    private void label(GridLayout grid, String propertyKey) {
        Label label = bim.label(null, propertyKey);
        grid.addComponent(label);
        grid.setComponentAlignment(label, Alignment.TOP_RIGHT);
        grid.addComponent(new Label(""));
    }

    private void buildKoulutus(GridLayout grid, String propertyKey) {
        label(grid, propertyKey);

        HorizontalLayout hl = UiUtil.horizontalLayout();
        KoodistoComponent kc = bim.addKoodistoComboBox(hl, _koodistoUriKoulutus, "koulutus", "koulutusTaiTutkinto.prompt");
        OhjePopupComponent ohjePopupComponent = new OhjePopupComponent(i18n.getMessage("LOREMIPSUM"), "500px", "300px");
        hl.addComponent(ohjePopupComponent);
        hl.setExpandRatio(kc, 1l);

        grid.addComponent(hl);
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
        VerticalLayout vl = UiUtil.verticalLayout(true, UiMarginEnum.TOP_BOTTOM);

        KoodistoComponent kc = bim.addKoodistoTwinColSelect(null, _koodistoUriKieli, "opetuskielet");
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
                        //TODO: handle painotus button click event. 
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

        VerticalLayout vl = UiUtil.verticalLayout(true, UiMarginEnum.TOP_BOTTOM);
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

    private void buildStipendiCheckBox(GridLayout grid, String propertyKey) {
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

        final BeanItemContainer<KoulutusYhteyshenkiloViewModel> yhteyshenkiloContainer =
                new BeanItemContainer<KoulutusYhteyshenkiloViewModel>(KoulutusYhteyshenkiloViewModel.class);
        yhteyshenkiloContainer.addAll(_presenter.getKoulutusToisenAsteenPerustiedotViewModel().getYhteyshenkilot());
        yhteyshenkilo = new Table(null, yhteyshenkiloContainer);
        yhteyshenkilo.setPageLength(10);
        yhteyshenkilo.setSizeFull();
        yhteyshenkilo.setSelectable(true);
        yhteyshenkilo.setImmediate(true);

        layout.addComponent(yhteyshenkilo);

        yhteyshenkilo.setColumnHeader("email", "Sähköposti");
        yhteyshenkilo.setColumnHeader("puhelin", "Puhelin");
        yhteyshenkilo.setColumnHeader("nimi", "Nimi");
        yhteyshenkilo.setColumnHeader("kielet", "Pätee kielille");

        yhteyshenkilo.setVisibleColumns(new Object[]{"nimi", "titteli", "email", "puhelin"});
        yhteyshenkilo.setColumnWidth("email", 25);
        yhteyshenkilo.setColumnWidth("puhelin", 25);
        yhteyshenkilo.setColumnWidth("nimi", 25);
        yhteyshenkilo.setColumnWidth("kielet", 25);

        Button b = bim.addButton(layout, "Yhteyshenkilo.LisaaUusi", null, null);
        b.addStyleName(Oph.BUTTON_DEFAULT);
        b.addStyleName(Oph.BUTTON_PLUS);
        b.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                final DialogKoulutusYhteystiedotView dialogKoulutusYhteystiedot = new DialogKoulutusYhteystiedotView("Lisää uusi yhteystieto");
                getWindow().addWindow(dialogKoulutusYhteystiedot);

                final Form f = dialogKoulutusYhteystiedot.getForm();
                //
                // Editor actions, commit form and refresh tabel data
                //
                dialogKoulutusYhteystiedot.getEditKoulutusPerustiedotYhteystietoView().addListener(new Listener() {
                    @Override
                    public void componentEvent(Event event) {
                        if (event instanceof EditKoulutusPerustiedotYhteystietoView.CancelEvent) {
                            f.discard();
                        }
                        if (event instanceof EditKoulutusPerustiedotYhteystietoView.SaveEvent) {
                            KoulutusYhteyshenkiloViewModel dto = new KoulutusYhteyshenkiloViewModel();
                            BeanItem<KoulutusYhteyshenkiloViewModel> bi = yhteyshenkiloContainer.addItem(dto);
                            yhteyshenkilo.select(dto);
                            f.commit();
                            yhteyshenkilo.refreshRowCache();

                        }
                        if (event instanceof EditKoulutusPerustiedotYhteystietoView.DeleteEvent) {
                            KoulutusYhteyshenkiloViewModel dto = (KoulutusYhteyshenkiloViewModel) yhteyshenkilo.getValue();
                            if (dto != null) {
                                yhteyshenkiloContainer.removeItem(dto);
                                f.setItemDataSource(null);
                                f.setEnabled(false);
                            }
                            getWindow().showNotification(i18n.getMessage("Poistettu"));

                            // Autoselect in table
                            if (yhteyshenkiloContainer.firstItemId() != null) {
                                yhteyshenkilo.setValue(yhteyshenkiloContainer.firstItemId());
                            }
                        }
                        getWindow().removeWindow(dialogKoulutusYhteystiedot);
                    }
                });


            }
        });
    }

    private void addLinkkiSelectorAndEditor(VerticalLayout layout, PropertysetItem mi) {
        // Datasource
        final BeanItemContainer<KoulutusLinkkiViewModel> linkkiContainer = new BeanItemContainer<KoulutusLinkkiViewModel>(KoulutusLinkkiViewModel.class);
        linkkiContainer.addAll(_presenter.getKoulutusToisenAsteenPerustiedotViewModel().getKoulutusLinkit());

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
                KoulutusLinkkiViewModel dto = new KoulutusLinkkiViewModel();
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
                KoulutusLinkkiViewModel selected = (KoulutusLinkkiViewModel) event.getProperty().getValue();
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
                    KoulutusLinkkiViewModel dto = (KoulutusLinkkiViewModel) t.getValue();
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
        if (!_presenter.getKoulutusToisenAsteenPerustiedotViewModel().getKoulutusLinkit().isEmpty()) {
            t.setValue(_presenter.getKoulutusToisenAsteenPerustiedotViewModel().getKoulutusLinkit().get(0));
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

    }

    public void doSaveIncomplete() {
        LOG.info("doSaveIncomplete(): dto={}", _presenter.getKoulutusToisenAsteenPerustiedotViewModel());
        // TODO validate
    }

    public void doSaveComplete() {
        LOG.info("doSaveComplete(): dto={}", _presenter.getKoulutusToisenAsteenPerustiedotViewModel());
        // TODO validate
    }

    public void doContinue() {
        LOG.info("doContinue()");
        // TODO check for changes, ask to save if any
        // TODO go to "overview page"?
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
