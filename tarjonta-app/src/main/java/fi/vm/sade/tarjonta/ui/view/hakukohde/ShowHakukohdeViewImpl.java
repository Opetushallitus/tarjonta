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
package fi.vm.sade.tarjonta.ui.view.hakukohde;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.ui.enums.CommonTranslationKeys;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.*;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.service.OrganisaatioContext;
import fi.vm.sade.tarjonta.ui.view.common.RemovalConfirmationDialog;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.constants.StyleEnum;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.dto.PageNavigationDTO;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Autowired;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalInfoLayout;
import fi.vm.sade.tarjonta.ui.view.common.CategoryTreeView;

import java.util.*;

/*
 * Author: Tuomas Katva
 */
@Configurable(preConstruction = true)
public class ShowHakukohdeViewImpl extends AbstractVerticalInfoLayout {

    private static final Logger LOG = LoggerFactory.getLogger(ShowHakukohdeViewImpl.class);
    private static final long serialVersionUID = -4485798240650803109L;
    @Autowired(required = true)
    private TarjontaPresenter tarjontaPresenterPresenter;
    @Autowired(required = true)
    private TarjontaUIHelper tarjontaUIHelper;
    private Window confirmationWindow;
    private CreationDialog<KoulutusOidNameViewModel> addlKoulutusDialog;
    private Window addlKoulutusDialogWindow;
    private final OrganisaatioContext context ;

    public ShowHakukohdeViewImpl(String pageTitle, String message, PageNavigationDTO dto) {
        super(VerticalLayout.class, pageTitle, message, dto);
        context = OrganisaatioContext.getContext(tarjontaPresenterPresenter.getTarjoaja().getOrganisationOid());
        LOG.debug(this.getClass().getName() + "()");
    }

    @Override
    public void buildLayout(VerticalLayout layout) {

        layout.removeAllComponents();

        VerticalLayout vl = UiUtil.verticalLayout(true, UiMarginEnum.ALL);
        Panel panel = new Panel();

        panel.setContent(vl);
        layout.addComponent(panel);

        //Build the layout

        //XXX oid not set
        addNavigationButtons(vl, OrganisaatioContext.getContext(tarjontaPresenterPresenter.getTarjoaja().getOrganisationOid()));
        Set<String> allLangs = getAllKielet();
        final TabSheet tabs = new TabSheet();
        for (String lang:allLangs) {
           ShowHakukohdeTab hakukohdeTab = new ShowHakukohdeTab(lang);
           tabs.addTab(hakukohdeTab, tarjontaUIHelper.getKoodiNimi(lang));
        }
        vl.addComponent(tabs);
        /*addLayoutSplit(vl);
        buildMiddleContentLayout(vl);
        addLayoutSplit(vl);
        buildKoulutuksesLayout(vl);
        buildLiitaUusiKoulutusButton(vl);*/

    }

    private Set<String> getAllKielet() {
        Set<String> kielet = new HashSet<String>();
        List<KielikaannosViewModel> lisatietoKielet = tarjontaPresenterPresenter.getModel().getHakukohde().getLisatiedot();
        for (KielikaannosViewModel kieli : lisatietoKielet) {
            kielet.add(kieli.getKielikoodi());
        }
        List<KielikaannosViewModel> valintakoeKielet = tarjontaPresenterPresenter.getModel().getHakukohde().getValintaPerusteidenKuvaus();
        for (KielikaannosViewModel kieli:valintakoeKielet) {
            kielet.add(kieli.getKielikoodi());
        }

        return kielet;
    }

    private void buildKoulutuksesLayout(VerticalLayout layout) {

        layout.addComponent(buildHeaderLayout(context, T("sisaltyvatKoulutukset"), null, null, true));

        CategoryTreeView categoryTree = new CategoryTreeView();
        categoryTree.setHeight("100px");
        categoryTree.setContainerDataSource(createHakukohdeKoulutusDatasource(tarjontaPresenterPresenter.getModel().getHakukohde().getKoulukses()));
        String[] visibleColumns = {"nimiBtn", "poistaBtn"};
        categoryTree.setVisibleColumns(visibleColumns);
        for (Object item : categoryTree.getItemIds()) {
            categoryTree.setChildrenAllowed(item, false);
        }
        layout.addComponent(categoryTree);
    }

    private void buildLiitaUusiKoulutusButton(VerticalLayout verticalLayout) {
        Button liitaUusiKoulutusBtn = UiBuilder.buttonSmallPrimary(null, T("liitaUusiKoulutusPainike"));
        liitaUusiKoulutusBtn.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                addlKoulutusDialog = tarjontaPresenterPresenter.createHakukohdeCreationDialogWithSelectedTarjoaja();
                createButtonListenersForDialog();
                addlKoulutusDialog.setWidth("700px");
                addlKoulutusDialogWindow = new Window();
                addlKoulutusDialogWindow.setContent(addlKoulutusDialog);
                addlKoulutusDialogWindow.setModal(true);
                addlKoulutusDialogWindow.center();
                addlKoulutusDialogWindow.setCaption(T("liitaUusiKoulutusDialogTitle"));
                getWindow().addWindow(addlKoulutusDialogWindow);
            }
        });

        liitaUusiKoulutusBtn.setVisible(tarjontaPresenterPresenter.getPermission().userCanAddKoulutusToHakukohde(OrganisaatioContext.getContext(tarjontaPresenterPresenter)));
        verticalLayout.addComponent(liitaUusiKoulutusBtn);
    }

    private void createButtonListenersForDialog() {
        if (addlKoulutusDialog != null) {
            addlKoulutusDialog.getPeruutaBtn().addListener(new Button.ClickListener() {
                private static final long serialVersionUID = 5019806363620874205L;

                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    if (addlKoulutusDialogWindow != null) {
                        getWindow().removeWindow(addlKoulutusDialogWindow);
                    }
                }
            });

            addlKoulutusDialog.getJatkaBtn().addListener(new Button.ClickListener() {
                private static final long serialVersionUID = 5019806363620874205L;

                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    Object values = addlKoulutusDialog.getOptionGroup().getValue();
                    Collection<KoulutusOidNameViewModel> selectedKoulutukses = null;
                    if (values instanceof Collection) {
                        selectedKoulutukses = (Collection<KoulutusOidNameViewModel>) values;
                    }
                    getWindow().removeWindow(addlKoulutusDialogWindow);
                    tarjontaPresenterPresenter.addKoulutuksesToHakukohde(selectedKoulutukses);
                }
            });
        }
    }

    private Container createHakukohdeKoulutusDatasource(List<KoulutusOidNameViewModel> koulutukses) {
        BeanItemContainer<ShowHakukohdeKoulutusRow> container = new BeanItemContainer<ShowHakukohdeKoulutusRow>(ShowHakukohdeKoulutusRow.class);

        container.addAll(getRows(koulutukses));

        return container;
    }

    private List<ShowHakukohdeKoulutusRow> getRows(List<KoulutusOidNameViewModel> koulutukses) {
        List<ShowHakukohdeKoulutusRow> rows = new ArrayList<ShowHakukohdeKoulutusRow>();
        for (KoulutusOidNameViewModel koulutus : koulutukses) {
            ShowHakukohdeKoulutusRow row = new ShowHakukohdeKoulutusRow(koulutus);
            rows.add(row);
        }
        return rows;
    }

    private void buildMiddleContentLayout(VerticalLayout layout) {

        if (!tarjontaPresenterPresenter.getModel().isSelectedHakuStarted()) {
            layout.addComponent(buildHeaderLayout(context, T("perustiedot"), T(CommonTranslationKeys.MUOKKAA), new Button.ClickListener() {
                private static final long serialVersionUID = 5019806363620874205L;

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    tarjontaPresenterPresenter.showHakukohdeEditView(tarjontaPresenterPresenter.getModel().getHakukohde().getKomotoOids(),
                            tarjontaPresenterPresenter.getModel().getHakukohde().getOid(), null);
                }
            }, true));
        }

        GridLayout grid = new GridLayout(2, 1);
        grid.setMargin(true);
        grid.setHeight("100%");


        HakukohdeViewModel model = tarjontaPresenterPresenter.getModel().getHakukohde();

        addItemToGrid(grid, "hakukohdeNimi", model.getHakukohdeKoodistoNimi());

        addItemToGrid(grid, "haunNimi", tryGetLocalizedHakuNimi(model.getHakuOid()));
        addItemToGrid(grid, "aloitusPaikat", new Integer(model.getAloitusPaikat()).toString());
        Label lisatiedotLabel = new Label(getLocalizedLisatiedot(model.getLisatiedot()));
        lisatiedotLabel.setContentMode(Label.CONTENT_XHTML);
        addItemToGrid(grid, "lisatiedot", lisatiedotLabel);

        grid.setColumnExpandRatio(0, 1f);
        grid.setColumnExpandRatio(1, 2f);

        layout.addComponent(grid);
        layout.setExpandRatio(grid, 1f);

    }

    private String tryGetLocalizedHakuNimi(HakuViewModel hakuViewModel) {
        String haunNimi = null;

        if (I18N.getLocale().getLanguage().trim().equalsIgnoreCase("en")) {
            haunNimi = hakuViewModel.getNimiEn();
        } else if (I18N.getLocale().getLanguage().trim().equalsIgnoreCase("se")) {
            haunNimi = hakuViewModel.getNimiSe();
        }

        if (haunNimi == null || I18N.getLocale().getLanguage().trim().equalsIgnoreCase("fi")) {
            haunNimi = hakuViewModel.getNimiFi();
        }

        return haunNimi;
    }

    private String getLocalizedLisatiedot(List<KielikaannosViewModel> kielet) {
        String reply = null;
        for (KielikaannosViewModel kieli : kielet) {
            reply = kieli.getNimi();
            if (kieli.getKielikoodi().trim().equalsIgnoreCase(T("default.tab"))) {
                reply = kieli.getNimi();
            }

        }
        return reply;
    }

    private HorizontalLayout buildHeaderLayout(OrganisaatioContext context, String title, String btnCaption, Button.ClickListener listener, boolean enable) {
        HorizontalLayout headerLayout = UiUtil.horizontalLayout(true, UiMarginEnum.NONE);
        Label titleLabel = UiUtil.label(headerLayout, title);
        titleLabel.setStyleName(Oph.LABEL_H2);

        if (btnCaption != null) {
            headerLayout.addComponent(titleLabel);
            Button btn = UiBuilder.buttonSmallPrimary(headerLayout, btnCaption, listener);
            btn.setVisible(tarjontaPresenterPresenter.getPermission().userCanUpdateHakukohde(OrganisaatioContext.getContext(tarjontaPresenterPresenter)));

            // Add default click listener so that we can show that action has not been implemented as of yet
            if (listener == null) {
                btn.addListener(new Button.ClickListener() {
                    private static final long serialVersionUID = 5019806363620874205L;

                    @Override
                    public void buttonClick(Button.ClickEvent event) {
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
                tarjontaPresenterPresenter.reloadAndShowMainDefaultView();
            }
        }, StyleEnum.STYLE_BUTTON_BACK);

        Button poista = addNavigationButton(T(CommonTranslationKeys.POISTA), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (checkHaunAlkaminen()) {
                    showConfirmationDialog();
                } else {
                    getWindow().showNotification(T("hakukohdePoistoEpaonnistui"), Window.Notification.TYPE_ERROR_MESSAGE);
                }

            }
        }, StyleEnum.STYLE_BUTTON_PRIMARY);

        Button kopioiUudeksi = addNavigationButton(T(CommonTranslationKeys.KOPIOI_UUDEKSI), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                getWindow().showNotification("Ei toteutettu");
            }
        }, StyleEnum.STYLE_BUTTON_PRIMARY);

        //permissions
        poista.setVisible(tarjontaPresenterPresenter.getPermission().userCanDeleteHakukohde(context));
        kopioiUudeksi.setVisible(tarjontaPresenterPresenter.getPermission().userCanCopyHakukohdAsNew(context));
    }

    private void showConfirmationDialog() {
        RemovalConfirmationDialog confirmationDialog = new RemovalConfirmationDialog(T("poistoVarmistus"),
                tarjontaPresenterPresenter.getModel().getHakukohde().getHakukohdeKoodistoNimi(), T("poistaPainike"), T("peruutaPainike"),
                new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                getWindow().removeWindow(confirmationWindow);
                tarjontaPresenterPresenter.removeSelectedHakukohde();


            }
        },
                new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (confirmationWindow != null) {
                    getWindow().removeWindow(confirmationWindow);
                }
            }
        });
        confirmationWindow = new Window();
        confirmationWindow.setContent(confirmationDialog);
        confirmationWindow.setModal(true);
        confirmationWindow.center();
        getWindow().addWindow(confirmationWindow);


    }

    public void showKoulutusRemovalDialog(final KoulutusOidNameViewModel koulutus) {
        final Window koulutusRemovalDialog = new Window();
        RemovalConfirmationDialog confirmationDialog = new RemovalConfirmationDialog(T("removeKoulutusFromHakukohde"), koulutus.getKoulutusNimi(), T("poistaRemoveKoulutusFromHakukohde"),
                T("peruutaRemoveKoulutusFromHakukohde"),
                new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                getWindow().removeWindow(koulutusRemovalDialog);
                tarjontaPresenterPresenter.removeKoulutusFromHakukohde(koulutus);
            }
        },
                new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                getWindow().removeWindow(koulutusRemovalDialog);
            }
        });
        koulutusRemovalDialog.setContent(confirmationDialog);
        koulutusRemovalDialog.setModal(true);
        koulutusRemovalDialog.center();
        getWindow().addWindow(koulutusRemovalDialog);
    }

    private boolean checkHaunAlkaminen() {
        tarjontaPresenterPresenter.loadHakukohdeHakuPvm();
        Date haunPaattymisPvm = tarjontaPresenterPresenter.getModel().getHakukohde().getHakuOid().getPaattymisPvm();
        Date haunAlkamisPvm = tarjontaPresenterPresenter.getModel().getHakukohde().getHakuOid().getAlkamisPvm();
        Date tanaan = new Date();
        if (tanaan.after(haunAlkamisPvm) && tanaan.before(haunPaattymisPvm)) {
            return false;
        } else {
            return true;
        }
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

    public void addLayoutSplit(VerticalLayout layout) {
        VerticalSplitPanel split = new VerticalSplitPanel();
        split.setImmediate(false);
        split.setWidth("100%");
        split.setHeight("2px");
        split.setLocked(true);

        layout.addComponent(split);
    }

    private void backFired() {
        fireEvent(new BackEvent(this));
    }

    private void editFired() {
        fireEvent(new EditEvent(this));
    }

    /**
     * Fired when Back is pressed.
     */
    public class BackEvent extends Component.Event {

        private static final long serialVersionUID = -1576894176022341609L;

        public BackEvent(Component source) {
            super(source);

        }
    }

    /**
     * Fired when Edit is pressed.
     */
    public class EditEvent extends Component.Event {

        private static final long serialVersionUID = -5412731409384095606L;

        public EditEvent(Component source) {
            super(source);

        }
    }
}
