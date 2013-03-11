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


import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetVastausTyyppi.KoulutusTulos;
import fi.vm.sade.tarjonta.service.types.KoulutusKoosteTyyppi;
import fi.vm.sade.tarjonta.ui.enums.CommonTranslationKeys;
import fi.vm.sade.tarjonta.ui.enums.RequiredRole;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.KoulutusLisatiedotModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusLisatietoModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.SimpleHakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.service.OrganisaatioContext;
import fi.vm.sade.tarjonta.ui.service.TarjontaPermissionServiceImpl;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalInfoLayout;
import fi.vm.sade.tarjonta.ui.view.common.CategoryTreeView;
import fi.vm.sade.tarjonta.ui.view.common.RemovalConfirmationDialog;
import fi.vm.sade.tarjonta.ui.view.common.TarjontaDialogWindow;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.constants.StyleEnum;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.dto.PageNavigationDTO;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.ArrayList;
import java.util.List;

/**
 * Show collected information about koulutus.
 *
 * @author mlyly
 */
@Configurable
public class ShowKoulutusView extends AbstractVerticalInfoLayout {

    private static final Logger LOG = LoggerFactory.getLogger(ShowKoulutusView.class);
    private static final long serialVersionUID = -4381256372874208231L;
    @Autowired(required = true)
    private TarjontaPresenter _presenter;
    @Autowired(required = true)
    private TarjontaUIHelper _tarjontaUIHelper;
    private TarjontaDialogWindow tarjontaDialog;

    public ShowKoulutusView(String pageTitle, PageNavigationDTO pageNavigationDTO) {
        super(VerticalLayout.class, pageTitle, null, pageNavigationDTO);
    }

    @Override
    protected void buildLayout(VerticalLayout layout) {
        LOG.debug("buildLayout(): hakutyyppi uri={}", KoodistoURIHelper.KOODISTO_HAKUTYYPPI_URI);

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

        addNavigationButtons(vl, OrganisaatioContext.getContext(_presenter.getModel().getKoulutusPerustiedotModel().getOrganisaatioOid()));
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
            private static final long serialVersionUID = 5019806363620874205L;
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
        addItemToGrid(grid, "koulutuksenAlkamisPvm", _tarjontaUIHelper.formatDate(model.getKoulutuksenAlkamisPvm()));
        addItemToGrid(grid, "suunniteltuKesto", suunniteltuKesto(model));
        addItemToGrid(grid, "opetuskieli", _tarjontaUIHelper.getKoodiNimi(model.getOpetuskieli(), null));
        addItemToGrid(grid, "ammattinimikkeet", _tarjontaUIHelper.getKoodiNimi(_presenter.getModel().getKoulutusLisatiedotModel().getAmmattinimikkeet(), null));

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

            String kestotyyppi = _tarjontaUIHelper.getKoodiNimi(model.getSuunniteltuKestoTyyppi(), null);
            if (kestotyyppi != null) {
                tmp += kestotyyppi;
            } else {
                // Add uri if no translation ... just to show something.
                tmp += model.getSuunniteltuKestoTyyppi();
            }
        }
        return tmp;
    }

    /**
     * Add line with label + textual label value to the grid.
     *
     * @param grid
     * @param labelCaptionKey
     * @param labelCaptionValue
     */
    private void addItemToGrid(GridLayout grid, String labelCaptionKey, String labelCaptionValue) {
        addItemToGrid(grid, labelCaptionKey, new Label(labelCaptionValue));
    }

    /**
     * Add label + component to grid layout.
     *
     * @param grid
     * @param labelCaptionKey
     * @param component
     */
    private void addItemToGrid(GridLayout grid, String labelCaptionKey, Component component) {
        if (grid != null) {
            HorizontalLayout hl = UiUtil.horizontalLayout(false, UiMarginEnum.RIGHT);
            hl.setSizeFull();
            UiUtil.label(hl, T(labelCaptionKey));
            grid.addComponent(hl);
            grid.addComponent(component);
            grid.setComponentAlignment(hl, Alignment.TOP_RIGHT);
            grid.setComponentAlignment(component, Alignment.TOP_LEFT);
            grid.newLine();
        }
    }

    /**
     * Localized descriptive data about the koulutus.
     *
     * @param layout
     */
    private void buildKoulutuksenKuvailevatTiedot(VerticalLayout layout) {
        layout.addComponent(buildHeaderLayout(T("kuvailevatTiedot"), T(CommonTranslationKeys.MUOKKAA), null, false));
        KoulutusLisatiedotModel lisatiedotModel = _presenter.getModel().getKoulutusLisatiedotModel();

        TabSheet tab = new TabSheet();
        tab.setSizeFull();

        // Loop over all languages
        for (String languageUri : lisatiedotModel.getLisatiedot().keySet()) {
            KoulutusLisatietoModel tiedotModel = lisatiedotModel.getLisatiedot(languageUri);

            GridLayout grid = new GridLayout(2, 1);
            grid.setMargin(true);
            grid.setHeight("100%");
            addItemToGrid(grid, "tutkinnonSisalto", new Label(tiedotModel.getSisalto(), Label.CONTENT_XHTML));
            addItemToGrid(grid, "tutkinnonKuvailevatTiedot", new Label(tiedotModel.getKuvailevatTiedot(), Label.CONTENT_XHTML));
            addItemToGrid(grid, "tutkinnonSijoittuminenTyoelamaan", new Label(tiedotModel.getSijoittuminenTyoelamaan(), Label.CONTENT_XHTML));
            addItemToGrid(grid, "tutkinnonKansainvalistyminen", new Label(tiedotModel.getKansainvalistyminen(), Label.CONTENT_XHTML));
            addItemToGrid(grid, "tutkinnonYhteistyoMuidenToimijoidenKanssa", new Label(tiedotModel.getYhteistyoMuidenToimijoidenKanssa(), Label.CONTENT_XHTML));
            addItemToGrid(grid, "koulutusohjelmanValinta", new Label(tiedotModel.getKoulutusohjelmanValinta(), Label.CONTENT_XHTML));
            

            grid.setColumnExpandRatio(0, 1);
            grid.setColumnExpandRatio(1, 2);

            tab.addTab(grid, _tarjontaUIHelper.getKoodiNimi(languageUri));

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

        int numberOfApplicationTargets = _presenter.getModel().getKoulutusPerustiedotModel().getKoulutuksenHakukohteet().size();

        layout.addComponent(buildHeaderLayout(T("hakukohteet", numberOfApplicationTargets), T("luoUusiHakukohdeBtn"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;
                @Override
                public void buttonClick(ClickEvent event) {
                   List<String> koulutus = new ArrayList<String>();
                   koulutus.add(_presenter.getModel().getKoulutusPerustiedotModel().getOid());
                   _presenter.showHakukohdeEditView(koulutus,null);
                }
            }
                , false));

        CategoryTreeView categoryTree = new CategoryTreeView();
        categoryTree.setHeight("100px");
        categoryTree.setContainerDataSource(createHakukohdelistContainer(_presenter.getModel().getKoulutusPerustiedotModel().getKoulutuksenHakukohteet()));
        String[] visibleColumns = {"nimiBtn","poistaBtn"};
        categoryTree.setVisibleColumns(visibleColumns);
        for (Object item:categoryTree.getItemIds()) {
            categoryTree.setChildrenAllowed(item,false);
        }
        layout.addComponent(categoryTree);

    }

    public void showHakukohdeRemovalDialog(final String hakukohdeOid, final String hakukohdeNimi) {
        final Window hakukohdeRemovalDialog = new Window();
        RemovalConfirmationDialog removalConfirmationDialog = new RemovalConfirmationDialog(T("removeHakukohdeFromKoulutusQ"),hakukohdeNimi,T("jatkaBtn"),T("peruutaBtn"),new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                getWindow().removeWindow(hakukohdeRemovalDialog);
                _presenter.removeHakukohdeFromKoulutus(hakukohdeOid);

            }},new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                getWindow().removeWindow(hakukohdeRemovalDialog);
            } });
        hakukohdeRemovalDialog.setContent(removalConfirmationDialog);
        hakukohdeRemovalDialog.setModal(true);
        hakukohdeRemovalDialog.center();
        getWindow().addWindow(hakukohdeRemovalDialog);

    }

    private Container createHakukohdelistContainer(List<SimpleHakukohdeViewModel> hakukohdes) {
        BeanItemContainer<ShowKoulutusHakukohdeRow> hakukohdeRows = new BeanItemContainer<ShowKoulutusHakukohdeRow>(ShowKoulutusHakukohdeRow.class);
        hakukohdeRows.addAll(getKoulutusHakukohdeRows(hakukohdes));
        return hakukohdeRows;
    }

    private List<ShowKoulutusHakukohdeRow> getKoulutusHakukohdeRows(List<SimpleHakukohdeViewModel> hakukohdes) {
        List<ShowKoulutusHakukohdeRow> rows = new ArrayList<ShowKoulutusHakukohdeRow>();
        for (SimpleHakukohdeViewModel hakukohdeViewModel:hakukohdes) {
            ShowKoulutusHakukohdeRow row = new ShowKoulutusHakukohdeRow(hakukohdeViewModel);
            rows.add(row);
        }
        return rows;
    }

    private HorizontalLayout buildHeaderLayout(String title, String btnCaption, Button.ClickListener listener, boolean enable) {
        HorizontalLayout headerLayout = UiUtil.horizontalLayout(true, UiMarginEnum.NONE);
        Label titleLabel = UiUtil.label(headerLayout, title);
        titleLabel.setStyleName(Oph.LABEL_H2);

        System.out.println("title:" + title);
        if (btnCaption != null) {
            headerLayout.addComponent(titleLabel);
            Button btn = UiBuilder.buttonSmallPrimary(headerLayout, btnCaption, listener);
            btn.setVisible(_presenter.getPermission().userCanUpdateKoulutus(OrganisaatioContext.getContext(_presenter)));

            // Add default click listener so that we can show that action has not been implemented as of yet
            if (listener == null) {
                btn.addListener(new Button.ClickListener() {
                    private static final long serialVersionUID = 5019806363620874205L;
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

    private void addNavigationButtons(VerticalLayout layout, OrganisaatioContext context) {
        addNavigationButton("", new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;
            @Override
            public void buttonClick(Button.ClickEvent event) {

                _presenter.reloadAndShowMainDefaultView();
            }
        }, StyleEnum.STYLE_BUTTON_BACK);
        
        final Button poista = addNavigationButton(T(CommonTranslationKeys.POISTA), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;
            @Override
            public void buttonClick(Button.ClickEvent event) {
                showRemoveDialog();
                
            }
        }, StyleEnum.STYLE_BUTTON_PRIMARY);
        
        final Button kopioiUudeksi = addNavigationButton(T(CommonTranslationKeys.KOPIOI_UUDEKSI), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getWindow().showNotification("Ei toteutettu");
            }
        }, StyleEnum.STYLE_BUTTON_PRIMARY);

        final Button siirraOsaksiToista = addNavigationButton(T("siirraOsaksiToistaKoulutusta"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getWindow().showNotification("Ei toteutettu");
            }
        }, StyleEnum.STYLE_BUTTON_PRIMARY);

        final Button lisaaToteutus = addNavigationButton(T("lisaaToteutus"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;
            @Override
            public void buttonClick(Button.ClickEvent event) {
                _presenter.showLisaaRinnakkainenToteutusEditView(_presenter.getModel().getKoulutusPerustiedotModel().getOid());
            }
        }, StyleEnum.STYLE_BUTTON_PRIMARY);

        final Button esikatsele = addNavigationButton(T("esikatsele"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getWindow().showNotification("Ei toteutettu");
            }
        }, StyleEnum.STYLE_BUTTON_PRIMARY);


        //check permissions
        final TarjontaPermissionServiceImpl permissions = _presenter.getPermission(); 
        poista.setVisible(permissions.userCanDeleteKoulutus(context));
        kopioiUudeksi.setVisible(permissions.userCanCopyKoulutusAsNew(context));
        siirraOsaksiToista.setVisible(permissions.userCanMoveKoulutus(context));
        lisaaToteutus.setVisible(permissions.userCanAddKoulutusInstanceToKoulutus(context));
    }

    public void addLayoutSplit(VerticalLayout layout) {
        VerticalSplitPanel split = new VerticalSplitPanel();
        split.setImmediate(false);
        split.setWidth("100%");
        split.setHeight("2px");
        split.setLocked(true);

        layout.addComponent(split);
    }
    
    private void showRemoveDialog() {
        RemovalConfirmationDialog removeDialog = new RemovalConfirmationDialog(T("removeQ"), "", T("removeYes"), T("removeNo"), 
                new Button.ClickListener() {    
                        @Override
                        public void buttonClick(ClickEvent event) {
                            closeKoulutusCreationDialog();
                            KoulutusTulos koulutus = new KoulutusTulos();
                            KoulutusKoosteTyyppi koulutusKooste = new KoulutusKoosteTyyppi();
                            koulutusKooste.setKoulutusmoduuliToteutus(_presenter.getModel().getKoulutusPerustiedotModel().getOid());
                            koulutus.setKoulutus(koulutusKooste);
                            boolean removeSuccess = _presenter.removeKoulutus(koulutus);
                            _presenter.getHakukohdeListView().reload();
                            if (removeSuccess) {
                                _presenter.showMainDefaultView();
                            } 
                        }
                },
                new Button.ClickListener() {

                           @Override
                           public void buttonClick(ClickEvent event) {
                               closeKoulutusCreationDialog();

                           }});
        tarjontaDialog = new TarjontaDialogWindow(removeDialog, T("removeDialog"));
        getWindow().addWindow(tarjontaDialog);
    }

    public void closeKoulutusCreationDialog() {
        if (tarjontaDialog != null) {
            getWindow().removeWindow(tarjontaDialog);
        }
    }
}
