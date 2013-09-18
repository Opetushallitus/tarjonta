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

import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.tarjonta.service.search.HakukohteetVastaus;
import fi.vm.sade.tarjonta.service.search.HakukohteetVastaus.HakukohdeTulos;
import fi.vm.sade.tarjonta.service.search.KoulutuksetVastaus.KoulutusTulos;
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
    private KoulutusTulos koulutus;
    /**
     * Checkbox to indicate if this row is selected.
     */
    private CheckBox isSelected;
    /**
     * The name of the koulutus, displayed in removal confirmation dialog.
     */
    private String koulutusNimi;
    private String rowKey;
    private List<KoulutusTulos> children;
    
    private Window dialogWindow;

    private final TarjontaUIHelper tarjontaUIHelper;
    /**
     * The presenter object for the component.
     */
    @Autowired(required = true)
    private TarjontaPresenter tarjontaPresenter;

    public KoulutusResultRow(TarjontaUIHelper tarjontaUIHelper) {
    	this.tarjontaUIHelper = tarjontaUIHelper;
        this.koulutus = new KoulutusTulos();
        this.setHeight(-1, UNITS_PIXELS);
        this.setWidth(-1, UNITS_PIXELS);
    }

    public KoulutusResultRow(TarjontaUIHelper tarjontaUIHelper, KoulutusTulos koulutus, String koulutusNimi) {
    	this.tarjontaUIHelper = tarjontaUIHelper;
        this.koulutus = koulutus;
        this.koulutusNimi = koulutusNimi;
        formatKoulutusName();
    }

    private void formatKoulutusName() {
        if (this.koulutusNimi != null && koulutus.getKoulutus() != null && koulutus.getKoulutus().getPohjakoulutusVaatimus() != null && this.koulutusNimi.length() > 0) {
            List<KoodiType> koodis = tarjontaUIHelper.getKoodis(koulutus.getKoulutus().getPohjakoulutusVaatimus());
            if (koodis != null && koodis.size() > 0) {
                this.koulutusNimi = this.koulutusNimi + ", " + tryGetKoodistoLyhytNimi(koodis.get(0));
            }
        }
    }

    private String tryGetKoodistoLyhytNimi(KoodiType koodi) {
        String retval = koodi.getKoodiArvo();

        List<KoodiMetadataType> metas = koodi.getMetadata();
        Locale locale = I18N.getLocale();
        for (KoodiMetadataType meta : metas) {
            if (meta.getKieli().equals(KieliType.FI) && locale.getLanguage().equals("fi")) {
                return meta.getLyhytNimi();
            } else if (meta.getKieli().equals(KieliType.SV) && locale.getLanguage().equals("sv")) {
                return meta.getLyhytNimi();
            }
        }

        return retval;
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
        
        final TarjontaTila tila = TarjontaTila.valueOf(koulutus.getKoulutus().getTila());
        
        final OrganisaatioContext context = OrganisaatioContext.getContext(koulutus.getKoulutus().getTarjoaja().getTarjoajaOid());

        boolean hakuStarted = tarjontaPresenter.isHakuStartedForKoulutus(koulutus.getKoulutus().getKomotoOid());
        
        if (tarjontaPresenter.getPermission().userCanUpdateKoulutus(context, hakuStarted)) {
            rowMenuBar.addMenuCommand(i18n.getMessage(MenuBarActions.EDIT.key), menuCommand);
        }

        rowMenuBar.addMenuCommand(i18n.getMessage("naytaHakukohteet"), menuCommand);

        if (tila.isRemovable() && tarjontaPresenter.getPermission().userCanDeleteKoulutus(context, hakuStarted)) {
            rowMenuBar.addMenuCommand(i18n.getMessage(MenuBarActions.DELETE.key), menuCommand);
        }

        if (tila.equals(TarjontaTila.VALMIS) && tarjontaPresenter.getPermission().userCanPublishKoulutus(context, hakuStarted)) {
            rowMenuBar.addMenuCommand(i18n.getMessage(MenuBarActions.PUBLISH.key), menuCommand);
        } else if (tila.equals(TarjontaTila.JULKAISTU) && tarjontaPresenter.getPermission().userCanCancelKoulutusPublish(context, hakuStarted)) {
            rowMenuBar.addMenuCommand(i18n.getMessage(MenuBarActions.CANCEL.key), menuCommand);
        } else if (tila.equals(TarjontaTila.PERUTTU) && tarjontaPresenter.getPermission().userCanPublishCancelledKoulutus()) {
            rowMenuBar.addMenuCommand(i18n.getMessage(MenuBarActions.PUBLISH.key), menuCommand);
        }
        //menuLayout.requestRepaintAll();
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
            final String komotoOid = koulutus.getKoulutus().getKoulutusmoduuliToteutus();
            tarjontaPresenter.getTarjoaja().setSelectedResultRowOrganisationOid(koulutus.getKoulutus().getTarjoaja().getTarjoajaOid());

            switch (koulutus.getKoulutus().getKoulutustyyppi()) {
                case AMMATILLINEN_PERUSKOULUTUS:
                    tarjontaPresenter.showKoulutustEditView(komotoOid, KoulutusActiveTab.PERUSTIEDOT);
                    break;
                case LUKIOKOULUTUS:
                    tarjontaPresenter.getLukioPresenter().showEditKoulutusView(komotoOid, KoulutusActiveTab.PERUSTIEDOT);
                    break;
                case YLIOPISTOKOULUTUS:
                case AMMATTIKORKEAKOULUTUS:
                    tarjontaPresenter.getKorkeakouluPresenter().showEditKoulutusView(komotoOid, KoulutusActiveTab.PERUSTIEDOT);
                    break;
            }

        } else if (selection.equals(i18n.getMessage(MenuBarActions.DELETE.key))) {
            showRemoveDialog();
        } else if (selection.equals(i18n.getMessage(MenuBarActions.PUBLISH.key))) {
            tarjontaPresenter.changeStateToPublished(koulutus.getKoulutus().getKomotoOid(), KOMOTO);
            tarjontaPresenter.sendEvent(KoulutusContainerEvent.update(koulutus.getKoulutus().getKomotoOid()));
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
                tarjontaPresenter.changeStateToCancelled(koulutus.getKoulutus().getKomotoOid(), KOMOTO);
                tarjontaPresenter.sendEvent(KoulutusContainerEvent.update(koulutus.getKoulutus().getKomotoOid()));
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
                tarjontaPresenter.removeKoulutus(koulutus);
                tarjontaPresenter.sendEvent(KoulutusContainerEvent.delete(koulutus.getKoulutus().getKomotoOid()));
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
                        && koulutus.getKoulutus() != null
                        && isSelected.booleanValue()) {
                    tarjontaPresenter.getSelectedKoulutukset().add(koulutus);
                    tarjontaPresenter.getTarjoaja().setSelectedOrganisationOid(koulutus.getKoulutus().getTarjoaja().getTarjoajaOid());

                } else if (koulutus != null
                        && koulutus.getKoulutus() != null) {
                    tarjontaPresenter.getSelectedKoulutukset().remove(koulutus);
                }

                tarjontaPresenter.toggleCreateHakukohde();
                //tarjontaPresenter.togglePoistaKoulutusB();
            }
        });

        //newAddressBtn.addStyleName(StyleNames.B_PRIMARY_LARGE_PLUS);


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
            OphRowMenuBar menubar = newMenuBar();
            hl.addComponent(menubar);
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
    
    private void removeKoulutusSelection() {
        KoulutusTulos selectionToRemove = null;
        for (KoulutusTulos curKoul : tarjontaPresenter.getModel().getSelectedKoulutukset()) {
            if (curKoul.getKoulutus().getKomotoOid().equals(koulutus.getKoulutus().getKomotoOid())) {
                selectionToRemove = curKoul;
                break;
            }
        }
        if (selectionToRemove != null) {
            tarjontaPresenter.getSelectedKoulutukset().remove(selectionToRemove);
        }
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
        final String komotoOid = koulutus.getKoulutus().getKoulutusmoduuliToteutus();

        switch (koulutus.getKoulutus().getKoulutustyyppi()) {
            case AMMATILLINEN_PERUSKOULUTUS:
                tarjontaPresenter.showShowKoulutusView(komotoOid);
                break;
            case LUKIOKOULUTUS:
                tarjontaPresenter.getLukioPresenter().showSummaryKoulutusView(komotoOid);
                break;
            case AMMATTIKORKEAKOULUTUS:
                tarjontaPresenter.getKorkeakouluPresenter().showSummaryKoulutusView(komotoOid);
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
    public List<KoulutusTulos> getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(List<KoulutusTulos> children) {
        this.children = children;
    }

    public void reinitMenubar() {
        rowMenuBar.clear();
        commandsAdded=false;
    }


}
