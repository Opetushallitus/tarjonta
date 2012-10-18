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
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.ui.helper.BeanItemMapper;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.OhjePopupComponent;
import fi.vm.sade.tarjonta.ui.model.KoulutusLinkkiViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusPerustiedotViewModel;
import fi.vm.sade.vaadin.constants.LabelStyleEnum;
import fi.vm.sade.tarjonta.ui.model.KoulutusYhteyshenkiloViewModel;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalNavigationLayout;
import fi.vm.sade.tarjonta.ui.view.common.DialogDataTable;
import fi.vm.sade.vaadin.constants.StyleEnum;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
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
public class EditKoulutusPerustiedotToinenAsteView extends AbstractVerticalNavigationLayout {

    private static final Logger LOG = LoggerFactory.getLogger(EditKoulutusPerustiedotToinenAsteView.class);

    @Autowired(required = true)
    private TarjontaPresenter presenter;

    private BeanItemMapper<KoulutusPerustiedotViewModel, EditKoulutusPerustiedotToinenAsteView> bim;

    public EditKoulutusPerustiedotToinenAsteView() {
        super();
        setMargin(true);
        setSpacing(true);
        setHeight(-1, UNITS_PIXELS);
    }

    @Override
    protected void buildLayout(VerticalLayout layout) {

        addNavigationButton("", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                presenter.showMainDefaultView();
            }
        }, StyleEnum.STYLE_BUTTON_BACK);

        addNavigationButton(T("tallennaLuonnoksena"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                presenter.saveKoulutusLuonnoksenaModel();
            }
        });

        addNavigationButton(T("tallennaValmiina"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                presenter.saveKoulutusValmiina();
            }
        });

        addNavigationButton(T("jatka"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                // TODO if changes, ask if really wants to navigate away
                presenter.showShowKoulutusView(null);
            }
        });

        initialize(getLayout()); //add layout to navigation container
    }

    //
    // Define data fields
    //
    private void initialize(AbstractLayout layout) {
        LOG.info("initialize() {}", presenter);

        bim = new BeanItemMapper<KoulutusPerustiedotViewModel, EditKoulutusPerustiedotToinenAsteView>(presenter.getKoulutusToisenAsteenPerustiedotViewModel(),
                getI18n(), this);
        bim.label(layout, "KoulutuksenPerustiedot", LabelStyleEnum.H2);

        UiUtil.hr(layout);

        GridLayout grid = new GridLayout(2, 1);
        grid.setSizeFull();
        grid.setColumnExpandRatio(0, 10l);
        grid.setColumnExpandRatio(1, 20l);

        layout.addComponent(grid);
        buildGridKoulutusRow(grid, "KoulutusTaiTutkinto");
        buildGridKoulutusohjelmaRow(grid, "Koulutusohjelma");

        //Build a label section, the data for labes are
        //received from koodisto (KOMO).
        gridLabelRow(grid, "koulutusTyyppi");
        gridLabelRow(grid, "koulutusala");
        gridLabelRow(grid, "tutkinto");
        gridLabelRow(grid, "tutkintonimike");
        gridLabelRow(grid, "opintojenLaajuusyksikko");
        gridLabelRow(grid, "opintojenLaajuus");
        gridLabelRow(grid, "opintoala");

        buildGridLanguageRow(grid, "Opetuskieli");
        buildGridDatesRow(grid, "KoulutuksenAlkamisPvm");
        buildGridKestoRow(grid, "SuunniteltuKesto");
        //Added later
        // buildPainotus(grid, "AdditionalInformation");

        buildGridOpetusmuotoRow(grid, "Opetusmuoto");
        buildGridKoulutuslajiRow(grid, "Koulutuslaji");
        buildGridAvainsanatTeemaRow(grid, "Avainsanat");
//        Not needed in 2.aste
//        buildMaksullistaCheckBox(grid, "KoulutusOnMaksullista");
//        buildStipendiCheckBox(grid, "StipendiMahdollisuus");

        UiUtil.hr(layout);
        addYhteyshenkiloSelectorAndEditor(layout);
        UiUtil.hr(layout);
        addLinkkiSelectorAndEditor(layout);
    }

    private void gridLabel(GridLayout grid, final String propertyKey) {
        if (propertyKey == null) {
            throw new RuntimeException("Application error - label caption cannot be null!");
        }
        HorizontalLayout layout = UiUtil.horizontalLayout(true, UiMarginEnum.RIGHT);
        layout.setSizeFull();
        Label label = new Label(T(propertyKey));
        label.setSizeUndefined();
        layout.addComponent(label);
        layout.setComponentAlignment(label, Alignment.TOP_RIGHT);
        grid.addComponent(layout);
    }

    private void gridLabelRow(GridLayout grid, final String propertyKey) {
        if (propertyKey == null) {
            throw new RuntimeException("Application error - label caption cannot be null!");
        }

        gridLabel(grid, propertyKey);
        grid.addComponent(bim.addLabel(null, propertyKey));
        grid.newLine();
        buildSpacingGridRow(grid);
    }

    private void buildGridKoulutusRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);

        HorizontalLayout hl = UiUtil.horizontalLayout();
        KoodistoComponent kc = bim.addKoodistoComboBox(hl, KoodistoURIHelper.KOODISTO_KOULUTUS_URI, "koulutus", "koulutusTaiTutkinto.prompt");
        OhjePopupComponent ohjePopupComponent = new OhjePopupComponent(T("LOREMIPSUM"), "500px", "300px");
        hl.addComponent(ohjePopupComponent);
        hl.setExpandRatio(kc, 1l);

        grid.addComponent(hl);
        grid.newLine();

        buildSpacingGridRow(grid);
    }

    private void buildGridKoulutusohjelmaRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);
        grid.addComponent(bim.addKoodistoComboBox(null, KoodistoURIHelper.KOODISTO_KOULUTUSOHJELMA_URI, "koulutusohjelma", "koulutusohjelma.prompt"));
        grid.newLine();

        buildSpacingGridRow(grid);
    }

    private void buildGridLanguageRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);
        KoodistoComponent kc = bim.addKoodistoTwinColSelect(null, KoodistoURIHelper.KOODISTO_KIELI_URI, "opetuskielet");
        kc.addListener(bim.getValueChangeListener("doOpetuskieletChanged"));

//        vl.addComponent(bim.addCheckBox(null, "Opetuskieli.ValitseKaikki", "opetuskieletKaikki", "doOpetuskieletSelectAll"));
        grid.addComponent(kc);

        grid.newLine();
        buildSpacingGridRow(grid);
    }

    private void buildGridDatesRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);
        grid.addComponent(bim.addDate(null, "koulutuksenAlkamisPvm"));
        grid.newLine();
        buildSpacingGridRow(grid);
    }

    private void buildGridPainotus(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);
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
        buildSpacingGridRow(grid);
    }

    private void buildGridKestoRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        hl.addComponent(bim.addTextField(null, "suunniteltuKesto", "SuunniteltuKesto.prompt", null));
        KoodistoComponent kc = bim.addKoodistoComboBox(hl, KoodistoURIHelper.KOODISTO_SUUNNITELTU_KESTO_URI, "suunniteltuKestoTyyppi", "SuunniteltuKesto.tyyppi.prompt");
        grid.addComponent(hl);
        grid.newLine();
        buildSpacingGridRow(grid);

    }

    private void buildGridAvainsanatTeemaRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);

        KoodistoComponent kc = bim.addKoodistoTwinColSelect(null, KoodistoURIHelper.KOODISTO_TEEMA_URI, "teemat");
        grid.addComponent(kc);

        grid.newLine();
        buildSpacingGridRow(grid);
    }

    private void buildGridOpetusmuotoRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);

        grid.addComponent(bim.addKoodistoComboBox(null, KoodistoURIHelper.KOODISTO_OPETUSMUOTO_URI, "opetusmuoto", "Opetusmuoto.prompt"));
        grid.newLine();
        buildSpacingGridRow(grid);

    }

    private void buildGridKoulutuslajiRow(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);
        KoodistoComponent kc = bim.addKoodistoTwinColSelect(null, KoodistoURIHelper.KOODISTO_KOULUTUSLAJI_URI, "koulutuslaji");
        grid.addComponent(kc);
        grid.newLine();
        buildSpacingGridRow(grid);
    }

    private void buildGridMaksullistaCheckBox(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);
        grid.addComponent(bim.addCheckBox(this, "KoulutusOnMaksullista.checkbox", "koulutusOnMaksullista", null));
        grid.newLine();
        buildSpacingGridRow(grid);
    }

    private void buildGridStipendiCheckBox(GridLayout grid, final String propertyKey) {
        gridLabel(grid, propertyKey);
        grid.addComponent(bim.addCheckBox(this, "StipendiMahdollisuus.checkbox", "koulutusStipendimahdollisuus", null));
        grid.newLine();
        buildSpacingGridRow(grid);
    }

    /**
     * Create yhteystiedot part of the form.
     *
     * @param layout
     */
    private void addYhteyshenkiloSelectorAndEditor(AbstractLayout layout) {
        headerLayout(layout, "Yhteyshenkilo");

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
        headerLayout(layout, "Linkit");

        final Class modelClass = KoulutusLinkkiViewModel.class;
        List<KoulutusLinkkiViewModel> koulutusLinkit =
                presenter.getKoulutusToisenAsteenPerustiedotViewModel().getKoulutusLinkit();

        final BeanItemContainer<KoulutusLinkkiViewModel> linkkiContainer =
                new BeanItemContainer<KoulutusLinkkiViewModel>(modelClass);

        linkkiContainer.addAll(koulutusLinkit);

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

    private void buildSpacingGridRow(GridLayout grid) {
        CssLayout cssLayout = new CssLayout();
        cssLayout.setHeight(4, UNITS_PIXELS);
        grid.addComponent(cssLayout);
        grid.newLine();
    }

    private void headerLayout(final AbstractLayout layout, final String i18nProperty) {
        CssLayout cssLayout = new CssLayout();
        cssLayout.setHeight(20, UNITS_PIXELS);
        cssLayout.addComponent(bim.label(null, i18nProperty));
        layout.addComponent(cssLayout);
    }

}
