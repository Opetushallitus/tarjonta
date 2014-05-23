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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Window;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.service.search.KoulutusPerustieto;
import fi.vm.sade.tarjonta.service.types.SisaltoTyyppi;
import fi.vm.sade.tarjonta.shared.auth.OrganisaatioContext;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.ui.enums.KoulutusActiveTab;
import fi.vm.sade.tarjonta.ui.enums.MenuBarActions;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.RemovalConfirmationDialog;
import fi.vm.sade.tarjonta.ui.view.common.TarjontaDialogWindow;
import fi.vm.sade.vaadin.ui.OphRowMenuBar;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 *
 * @author Markus
 */
@Configurable(preConstruction = true)
public class KoulutusResultRow extends HorizontalLayout {

    private static final long serialVersionUID = -1498887965250483214L;
    private transient I18NHelper i18n = new I18NHelper(this);
    private static final SisaltoTyyppi KOMOTO = SisaltoTyyppi.KOMOTO;
    /**
     * The koulutus to display on the row.
     */
    private KoulutusPerustieto koulutus;
    /**
     * Checkbox to indicate if this row is selected.
     */
    private CheckBox isSelected;
    /**
     * The name of the koulutus, displayed in removal confirmation dialog.
     */
    private String koulutusNimi;
    private String rowKey;
    private List<KoulutusPerustieto> children;
    
    private Window dialogWindow;

    private final TarjontaUIHelper tarjontaUIHelper;
    /**
     * The presenter object for the component.
     */
    @Autowired(required = true)
    private TarjontaPresenter tarjontaPresenter;

    public KoulutusResultRow(TarjontaUIHelper tarjontaUIHelper) {
    	this.tarjontaUIHelper = tarjontaUIHelper;
        this.koulutus = new KoulutusPerustieto();
        this.setHeight(-1, UNITS_PIXELS);
        this.setWidth(-1, UNITS_PIXELS);
    }

    public KoulutusResultRow(TarjontaUIHelper tarjontaUIHelper, KoulutusPerustieto koulutus, String koulutusNimi) {
    	this.tarjontaUIHelper = tarjontaUIHelper;
        this.koulutus = koulutus;
        this.koulutusNimi = koulutusNimi;
        formatKoulutusName();
    }

    private void formatKoulutusName() {
        if (this.koulutusNimi != null && koulutus != null && koulutus.getPohjakoulutusvaatimus() != null && this.koulutusNimi.length() > 0) {
            this.koulutusNimi = this.koulutusNimi + ", " + TarjontaUIHelper.getClosestMonikielinenNimi(I18N.getLocale(), koulutus.getPohjakoulutusvaatimus().getNimi());
        }
    }

    /**
     * Command object for the row menubar. Starts operations based on user's
     * selection in the menu.
     */
    private MenuBar.Command menuCommand = new MenuBar.Command() {
        private static final long serialVersionUID = 7160936162824727503L;

        @Override
        public void menuSelected(MenuBar.MenuItem selectedItem) {
            menuItemClicked(selectedItem.getText());
        }
    };
    OphRowMenuBar rowMenuBar;
    private boolean commandsAdded = false;

    private OphRowMenuBar newMenuBar() {
        rowMenuBar = new OphRowMenuBar("../oph/img/icon-treetable-button.png");
        reinitMenubar();
        return rowMenuBar;
    }
    
    private void addPermissionSpecificCommands() {
//        rowMenuBar.clear();

        
        final TarjontaTila tila = TarjontaTila.valueOf(koulutus.getTila());
        
        final OrganisaatioContext context = OrganisaatioContext.getContext(koulutus.getTarjoaja().getOid());

        if (tarjontaPresenter.getPermission().userCanUpdateKoulutus(context)) {
            rowMenuBar.addMenuCommand(i18n.getMessage(MenuBarActions.EDIT.key), menuCommand);
        }

        rowMenuBar.addMenuCommand(i18n.getMessage("naytaHakukohteet"), menuCommand);

        if (tila.isRemovable() && tarjontaPresenter.getPermission().userCanDeleteKoulutus(context)) {
            rowMenuBar.addMenuCommand(i18n.getMessage(MenuBarActions.DELETE.key), menuCommand);
        }

        if ((tila.equals(TarjontaTila.VALMIS) ||tila.equals(TarjontaTila.PERUTTU)) && tarjontaPresenter.getPermission().userCanPublishKoulutus(context)) {
            rowMenuBar.addMenuCommand(i18n.getMessage(MenuBarActions.PUBLISH.key), menuCommand);
        }
        
        if (tila.equals(TarjontaTila.JULKAISTU) && tarjontaPresenter.getPermission().userCanUpdateHakukohde(context)) {
            rowMenuBar.addMenuCommand(i18n.getMessage(MenuBarActions.CANCEL.key), menuCommand);
        }
        
        rowMenuBar.requestRepaint();
        this.getWindow().getApplication().getMainWindow().executeJavaScript("javascript:vaadin.forceSync();");
        commandsAdded = true;
    }

    /**
     * Fires an event based on user's selection in the row's menubar.
     *
     * @param selection the selection in the menu.
     */
    @SuppressWarnings("incomplete-switch")
    private void menuItemClicked(String selection) {
        if (selection.equals(i18n.getMessage(MenuBarActions.SHOW.key))) {
            showSummaryView();
        } else if (selection.equals(i18n.getMessage(MenuBarActions.EDIT.key))) {
            final String komotoOid = koulutus.getKoulutusmoduuliToteutus();
            tarjontaPresenter.getTarjoaja().setSelectedResultRowOrganisationOid(koulutus.getTarjoaja().getOid());

            switch (koulutus.getKoulutustyyppi()) {
                case AMMATILLINEN_PERUSKOULUTUS:
                    tarjontaPresenter.showKoulutustEditView(komotoOid, KoulutusActiveTab.PERUSTIEDOT);
                    break;
                case VALMENTAVA_JA_KUNTOUTTAVA_OPETUS:
                    tarjontaPresenter.showKoulutustEditView(komotoOid, KoulutusActiveTab.PERUSTIEDOT);
                    break;
                case AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS:
                    tarjontaPresenter.showKoulutustEditView(komotoOid, KoulutusActiveTab.PERUSTIEDOT);
                    break;
                case MAAHANM_AMM_VALMISTAVA_KOULUTUS:
                    tarjontaPresenter.showKoulutustEditView(komotoOid, KoulutusActiveTab.PERUSTIEDOT);
                    break;
                case MAAHANM_LUKIO_VALMISTAVA_KOULUTUS:
                    tarjontaPresenter.showKoulutustEditView(komotoOid, KoulutusActiveTab.PERUSTIEDOT);
                    break;
                case PERUSOPETUKSEN_LISAOPETUS:
                    tarjontaPresenter.showKoulutustEditView(komotoOid, KoulutusActiveTab.PERUSTIEDOT);
                    break;
                case VAPAAN_SIVISTYSTYON_KOULUTUS:
                    tarjontaPresenter.showKoulutustEditView(komotoOid, KoulutusActiveTab.PERUSTIEDOT);
                    break;
                case LUKIOKOULUTUS:
                    tarjontaPresenter.getLukioPresenter().showEditKoulutusView(komotoOid, KoulutusActiveTab.PERUSTIEDOT);
                    break;
            }

        } else if (selection.equals(i18n.getMessage(MenuBarActions.DELETE.key))) {
            showRemoveDialog();
        } else if (selection.equals(i18n.getMessage(MenuBarActions.PUBLISH.key))) {
            tarjontaPresenter.changeStateToPublished(koulutus.getKomotoOid(), KOMOTO);
        } else if (selection.equals(i18n.getMessage(MenuBarActions.CANCEL.key))) {
            showPeruutaDialog();
        } else if (selection.equals(i18n.getMessage("naytaHakukohteet"))) {
            tarjontaPresenter.showHakukohteetForKoulutus(koulutus);
        }
    }

    private void showPeruutaDialog() {
        RemovalConfirmationDialog cancelDialog = new RemovalConfirmationDialog(T("peruutaQ"), koulutusNimi, T("removeYes"), T("removeNo"),
                new Button.ClickListener() {
            private static final long serialVersionUID = -908351229767113315L;

            @Override
            public void buttonClick(ClickEvent event) {
                closeDialogWindow();
                tarjontaPresenter.changeStateToCancelled(koulutus.getKomotoOid(), KOMOTO);
            }
        },
                new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(ClickEvent event) {
                closeDialogWindow();

            }
        });
        dialogWindow = new TarjontaDialogWindow(cancelDialog, T("peruutaDialog"));
        getWindow().addWindow(dialogWindow);
    }

    private void showRemoveDialog() {
        RemovalConfirmationDialog removeDialog = new RemovalConfirmationDialog(T("removeQ"), koulutusNimi, T("removeYes"), T("removeNo"),
                new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(ClickEvent event) {
                closeDialogWindow();
                tarjontaPresenter.removeKoulutus(koulutus.getKomotoOid());
                tarjontaPresenter.sendEvent(KoulutusContainerEvent.delete(koulutus.getKomotoOid()));
            }
        },
                new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(ClickEvent event) {
                closeDialogWindow();

            }
        });
        dialogWindow = new TarjontaDialogWindow(removeDialog, T("removeDialog"));
        getWindow().addWindow(dialogWindow);
    }

    public void closeDialogWindow() {
        if (dialogWindow != null) {
            getWindow().removeWindow(dialogWindow);
        }
    }

    /**
     * Creation of the row component's layout.
     *
     * @param text - the text to be shown on the row.
     * @return
     */
    public KoulutusResultRow format(String text, boolean withMenuBar) {
        isSelected = UiUtil.checkbox(null, null);
        isSelected.setImmediate(true);
        isSelected.addListener(new Property.ValueChangeListener() {
            private static final long serialVersionUID = -382717228031608542L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (koulutus != null
                        && koulutus.getKomotoOid() != null
                        && isSelected.booleanValue()) {
                    tarjontaPresenter.getSelectedKoulutukset().add(koulutus);
                    tarjontaPresenter.getTarjoaja().setSelectedOrganisationOid(koulutus.getTarjoaja().getOid());

                } else if (koulutus != null
                        && koulutus != null) {
                    tarjontaPresenter.getSelectedKoulutukset().remove(koulutus);
                }

                tarjontaPresenter.toggleCreateHakukohde();
                //tarjontaPresenter.togglePoistaKoulutusB();
            }
        });


        addComponent(isSelected);
        if (withMenuBar) {
            Button nimiB = UiUtil.buttonLink(null, text, new Button.ClickListener() {
                private static final long serialVersionUID = 5019806363620874205L;

                @Override
                public void buttonClick(ClickEvent event) {
                    showSummaryView();
                }
            });
            nimiB.setStyleName("link-row");
            nimiB.setSizeUndefined();
            nimiB.setHeight(7, Sizeable.UNITS_PIXELS);

            HorizontalLayout hl = UiUtil.horizontalLayout();
            rowMenuBar = newMenuBar();
            hl.addComponent(rowMenuBar);
            hl.addListener(new LayoutClickListener() {

                private static final long serialVersionUID = -4622160054223438418L;

                @Override
                public void layoutClick(LayoutClickEvent event) {
                    if (!commandsAdded) {
                        addPermissionSpecificCommands();
                    }
                    
                }
                
            });
            addComponent(hl);
            addComponent(nimiB);
            setExpandRatio(nimiB, 1f); //default == 0
            setComponentAlignment(isSelected, Alignment.MIDDLE_LEFT);
            setComponentAlignment(hl, Alignment.MIDDLE_LEFT);
            setComponentAlignment(nimiB, Alignment.TOP_LEFT);
        } else {
            Label label = new Label(text);
            label.setSizeUndefined(); // -1,-1
            addComponent(label);
            setExpandRatio(label, 1f);
        }

        return this;
    }
    
    /**
     * Gets the isSelected checkbox component.
     *
     * @return
     */
    public CheckBox getIsSelected() {
        return isSelected;
    }

    private String T(String key, Object... args) {
        return i18n.getMessage(key, args);
    }

    private void showSummaryView() {
        final String komotoOid = koulutus.getKoulutusmoduuliToteutus();

        switch (koulutus.getKoulutustyyppi()) {
            case AMMATILLINEN_PERUSKOULUTUS:
                tarjontaPresenter.showShowKoulutusView(komotoOid);
                break;
            case VALMENTAVA_JA_KUNTOUTTAVA_OPETUS:
                tarjontaPresenter.showShowKoulutusView(komotoOid);
                break;
            case AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS:
                tarjontaPresenter.showShowKoulutusView(komotoOid);
                break;
            case MAAHANM_AMM_VALMISTAVA_KOULUTUS:
                tarjontaPresenter.showShowKoulutusView(komotoOid);
                break;
            case MAAHANM_LUKIO_VALMISTAVA_KOULUTUS:
                tarjontaPresenter.showShowKoulutusView(komotoOid);
                break;
            case PERUSOPETUKSEN_LISAOPETUS:
                tarjontaPresenter.showShowKoulutusView(komotoOid);
                break;
            case VAPAAN_SIVISTYSTYON_KOULUTUS:
                tarjontaPresenter.showShowKoulutusView(komotoOid);
                break;
            case LUKIOKOULUTUS:
                tarjontaPresenter.getLukioPresenter().showSummaryKoulutusView(komotoOid);
                break;

        }
    }

    /**
     * @return the rowKey
     */
    public String getRowKey() {
        return rowKey;
    }

    /**
     * @param rowKey the rowKey to set
     */
    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    /**
     * @return the children
     */
    public List<KoulutusPerustieto> getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(List<KoulutusPerustieto> children) {
        this.children = children;
    }

    public void reinitMenubar() {
        rowMenuBar.clear();
        rowMenuBar.addMenuCommand(i18n.getMessage(MenuBarActions.SHOW.key), menuCommand);

        
        commandsAdded=false;
        
    }


}
