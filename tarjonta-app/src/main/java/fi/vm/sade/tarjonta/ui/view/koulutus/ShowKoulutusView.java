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

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
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
    private TarjontaPresenter presenter;

    @Autowired(required = true)
    private TarjontaUIHelper _tarjontaUIHelper;

    public ShowKoulutusView(String pageTitle, PageNavigationDTO pageNavigationDTO) {
        super(VerticalLayout.class, pageTitle, null, pageNavigationDTO);
    }

    @Override
    protected void buildLayout(VerticalLayout layout) {
        LOG.info("buildLayout(): hakutyyppi uri={}", KoodistoURIHelper.KOODISTO_HAKUTYYPPI_URI);

        layout.removeAllComponents();
        addNavigationButtons(layout);
        addLayoutSplit();
        buildKoulutuksenPerustiedot(layout);
        addLayoutSplit();
        buildKoulutuksenKuvailevatTiedot(layout);
        addLayoutSplit();
        buildKoulutuksenSisaltyvatOpintokokonaisuudet(layout);
        addLayoutSplit();
        buildKoulutuksenHakukohteet(layout);
        addLayoutSplit();
    }

    private void buildKoulutuksenPerustiedot(VerticalLayout layout) {
        layout.addComponent(buildHeaderLayout(T("perustiedot"), T(CommonTranslationKeys.MUOKKAA), null));

        KoulutusToisenAsteenPerustiedotViewModel model = presenter.getModel().getKoulutusPerustiedotModel();

        GridLayout grid = new GridLayout(2, 1);
        grid.setHeight("100%");
        grid.setWidth("800px");

        grid.addComponent(UiUtil.label(T("organisaatio")));
        grid.addComponent(UiUtil.label("XXXXXXXXXXXXXXXXXXXXXX"));
        grid.newLine();
        grid.addComponent(UiUtil.label(T("koulutuslaji")));
        grid.addComponent(UiUtil.label(_tarjontaUIHelper.getKoodiNimi(model.getKoulutuslaji())));
        grid.newLine();
        grid.addComponent(UiUtil.label(T("opetusmuoto")));
        grid.addComponent(UiUtil.label(_tarjontaUIHelper.getKoodiNimi(model.getOpetusmuoto())));
        grid.newLine();
        grid.addComponent(UiUtil.label(T("teemat")));
        grid.addComponent(UiUtil.label(_tarjontaUIHelper.getKoodiNimi(model.getTeemat(), null)));
        grid.newLine();
        grid.addComponent(UiUtil.label(T("koulutuksenAlkamisPvm")));
        grid.addComponent(UiUtil.label(_tarjontaUIHelper.formatDate(model.getKoulutuksenAlkamisPvm())));
        grid.newLine();
        {
            // Build suunniteltu kesto and kesto tyyppi as string
            String tmp = "";
            if (model.getSuunniteltuKesto() != null) {
                tmp = model.getSuunniteltuKesto();
                tmp += " ";
                tmp += _tarjontaUIHelper.getKoodiNimi(model.getSuunniteltuKestoTyyppi(), null);
            }

            grid.addComponent(UiUtil.label(T("suunniteltuKesto")));
            grid.addComponent(UiUtil.label(tmp));
            grid.newLine();
        }
        grid.addComponent(UiUtil.label(T("opetuskieli")));
        grid.addComponent(UiUtil.label(_tarjontaUIHelper.getKoodiNimi(model.getOpetuskielet(), null)));
        grid.newLine();
        grid.addComponent(UiUtil.label(T("opetuksenMaksullisuus")));
        grid.addComponent(UiUtil.label(T(model.isKoulutusOnMaksullista() ? CommonTranslationKeys.KYLLA : CommonTranslationKeys.EI)));
        grid.newLine();
        grid.addComponent(UiUtil.label(T("stipendiMahdollisuus")));
        grid.addComponent(UiUtil.label(T(model.isKoulutusStipendiMahdollisuus()? CommonTranslationKeys.KYLLA : CommonTranslationKeys.EI)));
        grid.newLine();

        grid.setColumnExpandRatio(0, 1);
        grid.setColumnExpandRatio(1, 2);

        for (int row = 0; row < grid.getRows(); row++) {
            //alignment code not working?
            Component c = grid.getComponent(0, row);
            grid.setComponentAlignment(c, Alignment.TOP_RIGHT);
        }

        layout.addComponent(grid);
        layout.setExpandRatio(grid, 1f);
    }

    private void buildKoulutuksenKuvailevatTiedot(VerticalLayout layout) {
        layout.addComponent(buildHeaderLayout(T("kuvailevatTiedot"), T(CommonTranslationKeys.MUOKKAA), null));

        KoulutusToisenAsteenPerustiedotViewModel model = presenter.getModel().getKoulutusPerustiedotModel();

        TabSheet tab = new TabSheet();
        tab.setSizeUndefined();

        GridLayout grid = new GridLayout(2, 1);
        grid.setHeight("100%");
        grid.setWidth("800px");

        grid.addComponent(UiUtil.label(T("tutkinnonRakenne")));
        grid.newLine();
        grid.addComponent(UiUtil.label(T("tavoitteet")));
        grid.newLine();
        grid.addComponent(UiUtil.label(T("jatkoOpintoMahdollisuudet")));
        grid.newLine();

        grid.setColumnExpandRatio(0, 1);
        grid.setColumnExpandRatio(1, 2);

        for (int row = 0; row < grid.getRows(); row++) {
            //alignment code not working?
            Component c = grid.getComponent(0, row);
            grid.setComponentAlignment(c, Alignment.TOP_RIGHT);
        }

        tab.addTab(grid, "TABI KAIKILLE KIELILLE?");

        layout.addComponent(tab);
        layout.setExpandRatio(tab, 1f);
    }

    private void buildKoulutuksenSisaltyvatOpintokokonaisuudet(VerticalLayout layout) {
        // TODO get number of included(?) Koulutus entries
        int numberOfIncludedOpintokokonaisuus = 1;

        layout.addComponent(buildHeaderLayout(T("sisaltyvatOpintokokonaisuudet", numberOfIncludedOpintokokonaisuus), T(CommonTranslationKeys.MUOKKAA), null));
    }

    private void buildKoulutuksenHakukohteet(VerticalLayout layout) {
        // TODO get number of application targets
        int numberOfApplicationTargets = 666;

        layout.addComponent(buildHeaderLayout(T("hakukohteet", numberOfApplicationTargets), T(CommonTranslationKeys.MUOKKAA), null));
    }

    private HorizontalLayout buildHeaderLayout(String title, String btnCaption, Button.ClickListener listener) {
        HorizontalLayout headerLayout = UiUtil.horizontalLayout(true, UiMarginEnum.NONE);
        Label titleLabel = UiUtil.label(headerLayout, title);
        titleLabel.setStyleName(Oph.LABEL_H2);

        if (btnCaption != null) {
            headerLayout.addComponent(titleLabel);
            Button btn = UiUtil.buttonSmallSecodary(headerLayout, btnCaption, listener);

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
                presenter.showMainDefaultView();
            }
        }, StyleEnum.STYLE_BUTTON_BACK);

        addNavigationButton(T(CommonTranslationKeys.POISTA), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                // TODO ask confirmation
                getWindow().showNotification("Ei toteutettu");
            }
        });

        addNavigationButton(T(CommonTranslationKeys.KOPIOI_UUDEKSI), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getWindow().showNotification("Ei toteutettu");
            }
        });

        addNavigationButton(T("siirraOsaksiToistaKoulutusta"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getWindow().showNotification("Ei toteutettu");
            }
        });

    }
}
