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

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import fi.vm.sade.tarjonta.ui.enums.CommonTranslationKeys;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.model.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalInfoLayout;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.constants.StyleEnum;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.dto.PageNavigationDTO;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Show collected information about koulutus.
 *
 * @author mlyly
 */
@Configurable
public class ShowKoulutusView extends AbstractVerticalInfoLayout {

    private static final Logger LOG = LoggerFactory.getLogger(ShowKoulutusView.class);
    @Autowired(required = true)
    private TarjontaPresenter _presenter;
    @Autowired(required = true)
    private TarjontaUIHelper _tarjontaUIHelper;

    public ShowKoulutusView(String pageTitle, PageNavigationDTO pageNavigationDTO) {
        super(VerticalLayout.class, pageTitle, null, pageNavigationDTO);
    }

    @Override
    protected void buildLayout(VerticalLayout layout) {
        LOG.info("buildLayout(): hakutyyppi uri={}", KoodistoURIHelper.KOODISTO_HAKUTYYPPI_URI);

        if (_presenter == null) {
            _presenter = new TarjontaPresenter();
        }

        if (_tarjontaUIHelper == null) {
            _tarjontaUIHelper = new TarjontaUIHelper();
        }

        layout.removeAllComponents();

        VerticalLayout vl = UiUtil.verticalLayout(true, UiMarginEnum.ALL);
        Panel panel = new Panel();

        panel.setContent(vl);
        layout.addComponent(panel);

        addNavigationButtons(vl);
        addLayoutSplit(vl);
        buildKoulutuksenPerustiedot(vl);
        addLayoutSplit(vl);
        buildKoulutuksenKuvailevatTiedot(vl);
        addLayoutSplit(vl);
        buildKoulutuksenSisaltyvatOpintokokonaisuudet(vl);
        addLayoutSplit(vl);
        buildKoulutuksenHakukohteet(vl);
        addLayoutSplit(vl);
    }

    private void buildKoulutuksenPerustiedot(VerticalLayout layout) {

        KoulutusToisenAsteenPerustiedotViewModel model = _presenter.getModel().getKoulutusPerustiedotModel();
        final String komotoOid = model.getOid();

        layout.addComponent(buildHeaderLayout(T("perustiedot"), T(CommonTranslationKeys.MUOKKAA), new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                _presenter.showKoulutusPerustiedotEditView(komotoOid);
            }
        }, true));
        GridLayout grid = new GridLayout(2, 1);
        grid.setMargin(true);
        grid.setHeight("100%");

        addItemToGrid(grid, "organisaatio", model.getOrganisaatioName());
        addItemToGrid(grid, "koulutuslaji", _tarjontaUIHelper.getKoodiNimi(model.getKoulutuslaji()));
        addItemToGrid(grid, "opetusmuoto", _tarjontaUIHelper.getKoodiNimi(model.getOpetusmuoto(), null));
        // addItemToGrid(grid, "avainsanat", _tarjontaUIHelper.getKoodiNimi(model.getAvainsanat(), null));
        addItemToGrid(grid, "koulutuksenAlkamisPvm", _tarjontaUIHelper.formatDate(model.getKoulutuksenAlkamisPvm()));
        addItemToGrid(grid, "suunniteltuKesto", suunniteltuKesto(model));
        addItemToGrid(grid, "opetuskieli", _tarjontaUIHelper.getKoodiNimi(model.getOpetuskielet(), null));

        grid.setColumnExpandRatio(0, 1f);
        grid.setColumnExpandRatio(1, 2f);

        layout.addComponent(grid);
        layout.setExpandRatio(grid, 1f);
    }

    private String suunniteltuKesto(KoulutusToisenAsteenPerustiedotViewModel model) {
        // Build suunniteltu kesto and kesto tyyppi as string
        String tmp = "";
        if (model.getSuunniteltuKesto() != null) {
            tmp = model.getSuunniteltuKesto();
            tmp += " ";
            tmp += _tarjontaUIHelper.getKoodiNimi(model.getSuunniteltuKestoTyyppi(), null);
        }
        return tmp;
    }

    private void addItemToGrid(GridLayout grid, String labelCaptionKey, String labelCaptionValue) {
        if (grid != null) {

            HorizontalLayout hl = UiUtil.horizontalLayout(false, UiMarginEnum.RIGHT);
            hl.setSizeFull();
            UiUtil.label(hl, T(labelCaptionKey));
            Label labelValue = UiUtil.label(null, labelCaptionValue);
            grid.addComponent(hl);
            grid.addComponent(labelValue);
            grid.setComponentAlignment(hl, Alignment.TOP_RIGHT);
            grid.setComponentAlignment(labelValue, Alignment.TOP_LEFT);
            grid.newLine();
        }
    }

    private void buildKoulutuksenKuvailevatTiedot(VerticalLayout layout) {
        layout.addComponent(buildHeaderLayout(T("kuvailevatTiedot"), T(CommonTranslationKeys.MUOKKAA), null, false));

        KoulutusToisenAsteenPerustiedotViewModel model = _presenter.getModel().getKoulutusPerustiedotModel();
        TabSheet tab = new TabSheet();
        tab.setSizeFull();

        // Loop all opetuskielet
        for (String opetuskieli : model.getOpetuskielet()) {
            GridLayout grid = new GridLayout(2, 1);
            grid.setMargin(true);
            grid.setHeight("100%");
            addItemToGrid(grid, "tutkinnonRakenne", "Tietoa ei ole vielä saatavilla");
            addItemToGrid(grid, "tavoitteet", "Tietoa ei ole vielä saatavilla");
            addItemToGrid(grid, "jatkoOpintoMahdollisuudet", "Tietoa ei ole vielä saatavilla");
            grid.setColumnExpandRatio(0, 1);
            grid.setColumnExpandRatio(1, 2);

            tab.addTab(grid, _tarjontaUIHelper.getKoodiNimi(opetuskieli));
        }

        layout.addComponent(tab);
        layout.setExpandRatio(tab, 1f);
    }

    private void buildKoulutuksenSisaltyvatOpintokokonaisuudet(VerticalLayout layout) {
        // TODO get number of included(?) Koulutus entries
        int numberOfIncludedOpintokokonaisuus = 1;

        layout.addComponent(buildHeaderLayout(T("sisaltyvatOpintokokonaisuudet", numberOfIncludedOpintokokonaisuus), T(CommonTranslationKeys.MUOKKAA), null, false));
    }

    private void buildKoulutuksenHakukohteet(VerticalLayout layout) {
        // TODO get number of application targets
        int numberOfApplicationTargets = 0;

        layout.addComponent(buildHeaderLayout(T("hakukohteet", numberOfApplicationTargets), T(CommonTranslationKeys.MUOKKAA), null, false));
    }

    private HorizontalLayout buildHeaderLayout(String title, String btnCaption, Button.ClickListener listener, boolean enable) {
        HorizontalLayout headerLayout = UiUtil.horizontalLayout(true, UiMarginEnum.NONE);
        Label titleLabel = UiUtil.label(headerLayout, title);
        titleLabel.setStyleName(Oph.LABEL_H2);

        if (btnCaption != null) {
            headerLayout.addComponent(titleLabel);
            Button btn = UiUtil.buttonSmallSecodary(headerLayout, btnCaption, listener);
            btn.setEnabled(enable);

            // Add default click listener so that we can show that action has not been implemented as of yet
            if (listener == null) {
                btn.addListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        getWindow().showNotification("Toiminnallisuutta ei vielä toteutettu");
                    }
                });
            }

            headerLayout.setExpandRatio(btn, 1f);
            headerLayout.setComponentAlignment(btn, Alignment.TOP_RIGHT);
        }
        return headerLayout;
    }

    private void addNavigationButtons(VerticalLayout layout) {
        addNavigationButton("", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                _presenter.showMainDefaultView();
            }
        }, StyleEnum.STYLE_BUTTON_BACK);

        addNavigationButton(T(CommonTranslationKeys.POISTA), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                // TODO ask confirmation
                getWindow().showNotification("Ei toteutettu");
            }
        }, StyleEnum.STYLE_BUTTON_PRIMARY);

        addNavigationButton(T(CommonTranslationKeys.KOPIOI_UUDEKSI), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getWindow().showNotification("Ei toteutettu");
            }
        }, StyleEnum.STYLE_BUTTON_PRIMARY);

        addNavigationButton(T("siirraOsaksiToistaKoulutusta"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getWindow().showNotification("Ei toteutettu");
            }
        }, StyleEnum.STYLE_BUTTON_PRIMARY);

        addNavigationButton(T("lisaaToteutus"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getWindow().showNotification("Ei toteutettu");
            }
        }, StyleEnum.STYLE_BUTTON_PRIMARY);

        addNavigationButton(T("esikatsele"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getWindow().showNotification("Ei toteutettu");
            }
        }, StyleEnum.STYLE_BUTTON_PRIMARY);

    }

    public void addLayoutSplit(VerticalLayout layout) {
        VerticalSplitPanel split = new VerticalSplitPanel();
        split.setImmediate(false);
        split.setWidth("100%");
        split.setHeight("2px");
        split.setLocked(true);

        layout.addComponent(split);
    }
}
