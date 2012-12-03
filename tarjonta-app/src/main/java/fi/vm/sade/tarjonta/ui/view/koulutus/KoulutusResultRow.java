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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;

import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetVastausTyyppi.KoulutusTulos;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.RemovalConfirmationDialog;
import fi.vm.sade.tarjonta.ui.view.common.TarjontaDialogWindow;
import fi.vm.sade.vaadin.ui.OphRowMenuBar;
import fi.vm.sade.vaadin.util.UiUtil;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author Markus
 */
@Configurable(preConstruction = true)
public class KoulutusResultRow extends HorizontalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(KoulutusResultRow.class);
    private I18NHelper i18n = new I18NHelper(this);
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
    
    private Window removeKoulutusDialog;
    
    /**
     * The presenter object for the component.
     */
    @Autowired(required = true)
    private TarjontaPresenter tarjontaPresenter;

    public KoulutusResultRow() {
        this.koulutus = new KoulutusTulos();
    }

    public KoulutusResultRow(KoulutusTulos koulutus, String koulutusNimi) {
        this.koulutus = koulutus;
        this.koulutusNimi = koulutusNimi;
    }
    /**
     * Command object for the row menubar. Starts operations based on user's
     * selection in the menu.
     */
    private MenuBar.Command menuCommand = new MenuBar.Command() {
        @Override
        public void menuSelected(MenuBar.MenuItem selectedItem) {
            //DEBUGSAWAY:LOG.debug(selectedItem.getText());
            menuItemClicked(selectedItem.getText());

        }
    };
    OphRowMenuBar rowMenuBar;

    private OphRowMenuBar newMenuBar() {
        rowMenuBar = new OphRowMenuBar("../oph/img/icon-treetable-button.png");
        rowMenuBar.addMenuCommand(i18n.getMessage("tarkastele"), menuCommand);
        if (tarjontaPresenter.getPermission().userCanCreateReadUpdateAndDelete()
                || tarjontaPresenter.getPermission().userCanReadAndUpdate()) {
            rowMenuBar.addMenuCommand(i18n.getMessage("muokkaa"), menuCommand);
        }
        rowMenuBar.addMenuCommand(i18n.getMessage("naytaHakukohteet"), menuCommand);
        if (tarjontaPresenter.getPermission().userCanCreateReadUpdateAndDelete()) {
            rowMenuBar.addMenuCommand(i18n.getMessage("poista"), menuCommand);
        }

        return rowMenuBar;
    }

    /**
     * Fires an event based on user's selection in the row's menubar.
     *
     * @param selection the selection in the menu.
     */
    private void menuItemClicked(String selection) {
        if (selection.equals(i18n.getMessage("tarkastele"))) {
            tarjontaPresenter.showShowKoulutusView(koulutus.getKoulutus().getKoulutusmoduuliToteutus());
        } else if (selection.equals(i18n.getMessage("muokkaa"))) {
            tarjontaPresenter.showKoulutusPerustiedotEditView(koulutus.getKoulutus().getKoulutusmoduuliToteutus());
        } else if (selection.equals(i18n.getMessage("poista"))) {
            
            showRemoveDialog();
        }
    }
    
    private void showRemoveDialog() {
        RemovalConfirmationDialog removeDialog = new RemovalConfirmationDialog(T("removeQ"), koulutusNimi, T("removeYes"), T("removeNo"), 
                new Button.ClickListener() {    
                        @Override
                        public void buttonClick(ClickEvent event) {
                            closeKoulutusCreationDialog();
                            tarjontaPresenter.removeKoulutus(koulutus);
                            tarjontaPresenter.getHakukohdeListView().reload();
                        }
                },
                new Button.ClickListener() {

                           @Override
                           public void buttonClick(ClickEvent event) {
                               closeKoulutusCreationDialog();

                           }});
        removeKoulutusDialog = new TarjontaDialogWindow(removeDialog, T("removeDialog"));
        getWindow().addWindow(removeKoulutusDialog);
    }

    public void closeKoulutusCreationDialog() {
        if (removeKoulutusDialog != null) {
            getWindow().removeWindow(removeKoulutusDialog);
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
            @Override
            public void valueChange(ValueChangeEvent event) {
                if (koulutus != null
                        && isSelected.booleanValue()) {
                    tarjontaPresenter.getSelectedKoulutukset().add(koulutus);
                    
                } else if (koulutus != null) {
                    tarjontaPresenter.getSelectedKoulutukset().remove(koulutus);
                }
                
                tarjontaPresenter.toggleCreateHakukohde();
            }
        });



        //newAddressBtn.addStyleName(StyleNames.B_PRIMARY_LARGE_PLUS);

        setWidth(-1, Sizeable.UNITS_PIXELS);
        setHeight(-1, Sizeable.UNITS_PIXELS); //Tämä toimii!!!

        addComponent(isSelected);
        if (withMenuBar) {
            Button nimiB = UiUtil.buttonLink(null, text);

            nimiB.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    tarjontaPresenter.showShowKoulutusView(koulutus.getKoulutus().getKoulutusmoduuliToteutus());
                }
            });
            nimiB.setSizeUndefined();
            nimiB.setHeight(7, Sizeable.UNITS_PIXELS);

            OphRowMenuBar menubar = newMenuBar();
            addComponent(menubar);
            addComponent(nimiB);
            setExpandRatio(nimiB, 1f); //default == 0
            setComponentAlignment(isSelected, Alignment.MIDDLE_LEFT);
            setComponentAlignment(rowMenuBar, Alignment.MIDDLE_LEFT);
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
}
