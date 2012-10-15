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

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.ui.helper.BeanItemMapper;
import fi.vm.sade.tarjonta.ui.helper.OhjePopupComponent;
import fi.vm.sade.tarjonta.ui.model.KoulutusLinkkiViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusPerustiedotViewModel;
import fi.vm.sade.vaadin.constants.LabelStyleEnum;
import fi.vm.sade.tarjonta.ui.model.KoulutusYhteyshenkiloViewModel;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.DialogDataTable;
import fi.vm.sade.vaadin.constants.StyleEnum;
import fi.vm.sade.vaadin.ui.OphAbstractNavigationLayout;
import fi.vm.sade.vaadin.util.UiUtil;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author mlyly
 * @author Jani Wilén
 */
@Configurable(preConstruction = true)
public class EditKoulutusPerustiedotToinenAsteView extends OphAbstractNavigationLayout<VerticalLayout> {

    private static final Logger LOG = LoggerFactory.getLogger(EditKoulutusPerustiedotToinenAsteView.class);

    @Autowired(required = true)
    private TarjontaPresenter presenter;

    //
    // Used koodisto's in this component
    //
    @Value("${koodisto-uris.kieli:http://kieli}")
    private String koodistoUriKieli;
    @Value("${koodisto-uris.kieli:http://teema}")
    private String koodistoUriTeema;
    @Value("${koodisto-uris.koulutus:http://koulutus}")
    private String koodistoUriKoulutus;
    @Value("${koodisto-uris.koulutusohjelma:http://koulutusohjelma}")
    private String koodistoUriKoulutusohjelma;
    @Value("${koodisto-uris.suunniteltuKesto:http://suunniteltuKesto}")
    private String koodistoUriSuunniteltuKesto;
    @Value("${koodisto-uris.opetusmuoto:http://opetusmuoto}")
    private String koodistoUriOpetusmuoto;
    @Value("${koodisto-uris.koulutuslaji:http://koulutuslaji}")
    private String koodistoUriKoulutuslaji;

    private BeanItemMapper<KoulutusPerustiedotViewModel, EditKoulutusPerustiedotToinenAsteView> bim;

    private transient I18NHelper i18n;

    public EditKoulutusPerustiedotToinenAsteView() {
        super(VerticalLayout.class, null);
        setMargin(true);
        setSpacing(true);
        setHeight(-1, UNITS_PIXELS);
    }

    /*
     * Lazy initialization
     */
    @Override
    public void attach() {
        super.attach();
        initialize(getLayout()); //add layout to navigation container

        addNavigationButton("", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
            }
        }, StyleEnum.STYLE_BUTTON_BACK);

        addNavigationButton(i18n.getMessage("tallennaLuonnoksena"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                presenter.saveKoulutusLuonnoksenaModel();
            }
        });

        addNavigationButton(i18n.getMessage("tallennaValmiina"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                presenter.saveKoulutusValmiina();
            }
        });

        addNavigationButton(i18n.getMessage("jatka"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
            }
        });
    }

    //
    // Define data fields
    //
    private void initialize(AbstractLayout layout) {
        LOG.info("initialize()");
        bim = new BeanItemMapper<KoulutusPerustiedotViewModel, EditKoulutusPerustiedotToinenAsteView>(presenter.getKoulutusToisenAsteenPerustiedotViewModel(),
                getI18n(), this);
        bim.label(layout, "KoulutuksenPerustiedot", LabelStyleEnum.H2);

        UiUtil.hr(layout);

        GridLayout grid = new GridLayout(3, 1);
        grid.setSizeFull();
        grid.setColumnExpandRatio(0, 0l);
        grid.setColumnExpandRatio(1, 1l);
        grid.setColumnExpandRatio(2, 20l);

        layout.addComponent(grid);
        buildKoulutus(grid, "KoulutusTaiTutkinto");
        buildKoulutusohjelma(grid, "Koulutusohjelma");
        buildKoulutuksenTyyppi(grid, "KoulutuksenTyyppi");
        buildStaticLabelSection(grid);
        buildLanguage(grid, "Opetuskieli");
        buildDates(grid, "KoulutuksenAlkamisPvm");
        buildKesto(grid, "SuunniteltuKesto");

        //Added later
        // buildPainotus(grid, "AdditionalInformation");

        buildOpetusmuoto(grid, "Opetusmuoto");
        buildKoulutuslaji(grid, "Koulutuslaji");

        buildTeema(grid, "Avainsanat");

        //Not needed in 2.aste
        //buildMaksullistaCheckBox(grid, "KoulutusOnMaksullista");
        //buildStipendiCheckBox(grid, "StipendiMahdollisuus");

        UiUtil.hr(layout);
        addYhteyshenkiloSelectorAndEditor(layout);
        UiUtil.hr(layout);
        addLinkkiSelectorAndEditor(layout);
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
        KoodistoComponent kc = bim.addKoodistoComboBox(hl, koodistoUriKoulutus, "koulutus", "koulutusTaiTutkinto.prompt");
        OhjePopupComponent ohjePopupComponent = new OhjePopupComponent(T("LOREMIPSUM"), "500px", "300px");
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
        grid.addComponent(bim.addKoodistoComboBox(null, koodistoUriKoulutusohjelma, "koulutusohjelma", "koulutusohjelma.prompt"));
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
        KoodistoComponent kc = bim.addKoodistoTwinColSelect(null, koodistoUriKieli, "opetuskielet");
        kc.addListener(bim.getValueChangeListener("doOpetuskieletChanged"));

//        vl.addComponent(bim.addCheckBox(null, "Opetuskieli.ValitseKaikki", "opetuskieletKaikki", "doOpetuskieletSelectAll"));
        grid.addComponent(kc);

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
        KoodistoComponent kc = bim.addKoodistoComboBox(hl, koodistoUriSuunniteltuKesto, "suunniteltuKestoTyyppi", "SuunniteltuKesto.tyyppi.prompt");
        grid.addComponent(hl);
        grid.newLine();

    }

    private void buildTeema(GridLayout grid, String propertyKey) {
        label(grid, propertyKey);

        KoodistoComponent kc = bim.addKoodistoTwinColSelect(null, koodistoUriTeema, "teemat");
        grid.addComponent(kc);

        grid.newLine();
    }

    private void buildOpetusmuoto(GridLayout grid, String propertyKey) {
        label(grid, propertyKey);

        grid.addComponent(bim.addKoodistoComboBox(null, koodistoUriOpetusmuoto, "opetusmuoto", "Opetusmuoto.prompt"));
        grid.newLine();

    }

    private void buildKoulutuslaji(GridLayout grid, String propertyKey) {
        label(grid, propertyKey);
        KoodistoComponent kc = bim.addKoodistoTwinColSelect(null, koodistoUriKoulutuslaji, "koulutuslaji");
        grid.addComponent(kc);
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
    private void addYhteyshenkiloSelectorAndEditor(AbstractLayout layout) {
        layout.addComponent(bim.label(null, "Yhteyshenkilo"));

        //Attach data model to Vaadin bean container.
        final BeanItemContainer<KoulutusYhteyshenkiloViewModel> yhteyshenkiloContainer =
                new BeanItemContainer<KoulutusYhteyshenkiloViewModel>(KoulutusYhteyshenkiloViewModel.class);
        yhteyshenkiloContainer.addAll(presenter.getKoulutusToisenAsteenPerustiedotViewModel().getYhteyshenkilot());

        //Initialize dialog table with control buttons.
        DialogDataTable<KoulutusPerustiedotViewModel> ddt = new DialogDataTable<KoulutusPerustiedotViewModel>(KoulutusYhteyshenkiloViewModel.class, yhteyshenkiloContainer, bim);

        //Overide default button property
        ddt.setButtonProperties("DialogDataTable.LisaaUusi.Yhteyshenkilo");

        //Add form for dialog.
        ddt.buildByFormLayout(layout, "Luo uusi yhteystieto", 350, 450, new EditKoulutusPerustiedotYhteystietoView());

        //Add visible table columns.
        ddt.setColumnHeader("email", "Sähköposti");
        ddt.setColumnHeader("puhelin", "Puhelin");
        ddt.setColumnHeader("nimi", "Nimi");
        ddt.setColumnHeader("kielet", "Pätee kielille");
        ddt.setVisibleColumns(new Object[]{"nimi", "titteli", "email", "puhelin", "kielet"});
        layout.addComponent(ddt);
    }

    /**
     * Create linkkityyppi part of the form.
     *
     * @param layout
     */
    private void addLinkkiSelectorAndEditor(AbstractLayout layout) {
        final Class modelClass = KoulutusLinkkiViewModel.class;
        List<KoulutusLinkkiViewModel> koulutusLinkit =
                presenter.getKoulutusToisenAsteenPerustiedotViewModel().getKoulutusLinkit();

        final BeanItemContainer<KoulutusLinkkiViewModel> linkkiContainer =
                new BeanItemContainer<KoulutusLinkkiViewModel>(modelClass);

        linkkiContainer.addAll(koulutusLinkit);

        layout.addComponent(bim.label(null, "Linkit"));
        DialogDataTable<KoulutusPerustiedotViewModel> ddt =
                new DialogDataTable<KoulutusPerustiedotViewModel>(modelClass, linkkiContainer, bim);
        ddt.setButtonProperties("DialogDataTable.LisaaUusi.Linkkityyppi");
        ddt.buildByFormLayout(layout, "Luo uusi linkkityyppi", 400, 360, new EditKoulutusPerustiedotLinkkiView());
        ddt.setColumnHeader("linkkityyppi", T("Linkkityyppi"));
        ddt.setColumnHeader("url", T("LinkkiURL"));
        ddt.setColumnHeader("kielet", T("LinkkiKielet"));
        ddt.setVisibleColumns(new Object[]{"linkkityyppi", "url", "kielet"});
        layout.addComponent(ddt);
    }

    /*
     * VIEW ACTIONS IN THE PAGE
     */
    public void doCancel() {
        LOG.info("doCancel()");
        // TODO Check for changes, ask "really?" if any

    }

    public void doSaveIncomplete() {
        LOG.info("doSaveIncomplete(): dto={}", presenter.getKoulutusToisenAsteenPerustiedotViewModel());
        // TODO validate
    }

    public void doSaveComplete() {
        LOG.info("doSaveComplete(): dto={}", presenter.getKoulutusToisenAsteenPerustiedotViewModel());
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


    private String T(String key) {
        return getI18n().getMessage(key);
    }

    private I18NHelper getI18n() {
        if (i18n == null) {
            i18n = new I18NHelper(this);
        }
        return i18n;
    }

    @Override
    protected void initialization(Object obj) {
       //Currently not needed.
    }

    @Override
    protected void buildLayout(VerticalLayout layout) {
         //Currently not needed.
    }
}
