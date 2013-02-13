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

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetVastausTyyppi.HakukohdeTulos;
import fi.vm.sade.tarjonta.service.types.SisaltoTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.ui.enums.MenuBarActions;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.RemovalConfirmationDialog;
import fi.vm.sade.tarjonta.ui.view.common.TarjontaDialogWindow;
import fi.vm.sade.vaadin.ui.OphRowMenuBar;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The component and functionality for showing a hakukohde object in hakukohde
 * search result list.
 *
 * @author markus
 *
 */
@Configurable(preConstruction = false)
public class HakukohdeResultRow extends HorizontalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(HakukohdeResultRow.class);
    private transient I18NHelper i18n = new I18NHelper(this);
    private static final SisaltoTyyppi HAKUKOHDE = SisaltoTyyppi.HAKUKOHDE;
    /**
     * The hakukohde to display on the row.
     */
    private HakukohdeTulos hakukohde;
    private String hakukohdeNimi;
    /**
     * Checkbox to indicate if this row is selected.
     */
    private CheckBox isSelected;
    private Window removeHakukohdeDialog;
    /**
     * The presenter object for the component.
     */
    @Autowired(required = true)
    private TarjontaPresenter tarjontaPresenter;

    public HakukohdeResultRow() {
        this.hakukohde = new HakukohdeTulos();
    }

    public HakukohdeResultRow(HakukohdeTulos hakukohde, String hakukohdeNimi) {
        this.hakukohde = hakukohde;
        this.hakukohdeNimi = hakukohdeNimi;
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
        final TarjontaTila tila = hakukohde.getHakukohde().getTila();

        rowMenuBar.addMenuCommand(i18n.getMessage(MenuBarActions.SHOW.key), menuCommand);

        if (tarjontaPresenter.getPermission().userCanCreateReadUpdateAndDelete()
                || tarjontaPresenter.getPermission().userCanReadAndUpdate()) {
            rowMenuBar.addMenuCommand(i18n.getMessage(MenuBarActions.EDIT.key), menuCommand);
        }

        rowMenuBar.addMenuCommand(i18n.getMessage("naytaKoulutukset"), menuCommand);

        if (tila.equals(TarjontaTila.LUONNOS) && tarjontaPresenter.getPermission().userCanCreateReadUpdateAndDelete()) {
            rowMenuBar.addMenuCommand(i18n.getMessage(MenuBarActions.DELETE.key), menuCommand);
        }

        if (tila.equals(TarjontaTila.VALMIS) && tarjontaPresenter.getPermission().userCanCreateReadUpdateAndDelete()) {
            rowMenuBar.addMenuCommand(i18n.getMessage(MenuBarActions.PUBLISH.key), menuCommand);
        } else if (tila.equals(TarjontaTila.JULKAISTU) && tarjontaPresenter.getPermission().userCanCreateReadUpdateAndDelete()) {
            rowMenuBar.addMenuCommand(i18n.getMessage(MenuBarActions.CANCEL.key), menuCommand);
        }

        return rowMenuBar;
    }

    /**
     * Fires an event based on user's selection in the row's menubar.
     *
     * @param selection the selection in the menu.
     */
    private void menuItemClicked(String selection) {
        final String hakukohdeOid = hakukohde.getHakukohde().getOid();

        if (selection.equals(i18n.getMessage(MenuBarActions.SHOW.key))) {
            tarjontaPresenter.showHakukohdeViewImpl(hakukohdeOid);
        } else if (selection.equals(i18n.getMessage(MenuBarActions.EDIT.key))) {
            tarjontaPresenter.showHakukohdeEditView(null, hakukohdeOid);
        } else if (selection.equals(i18n.getMessage(MenuBarActions.DELETE.key))) {
            showRemoveDialog();
        } else if (selection.equals(i18n.getMessage("naytaKoulutukset"))) {
            tarjontaPresenter.showKoulutuksetForHakukohde(hakukohdeOid);
        } else if (selection.equals(i18n.getMessage(MenuBarActions.PUBLISH.key))) {
            tarjontaPresenter.changeStateToPublished(hakukohdeOid, HAKUKOHDE);
        } else if (selection.equals(i18n.getMessage(MenuBarActions.CANCEL.key))) {
            tarjontaPresenter.changeStateToCancelled(hakukohdeOid, HAKUKOHDE);
        }

    }

    private void showRemoveDialog() {
        RemovalConfirmationDialog removeDialog = new RemovalConfirmationDialog(T("removeQ"), hakukohdeNimi, T("removeYes"), T("removeNo"),
                new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                closeHakukohdeCreationDialog();
                tarjontaPresenter.removeHakukohde(hakukohde);
            }
        },
                new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                closeHakukohdeCreationDialog();

            }
        });
        removeHakukohdeDialog = new TarjontaDialogWindow(removeDialog, T("removeDialog"));
        getWindow().addWindow(removeHakukohdeDialog);
    }

    public void closeHakukohdeCreationDialog() {
        if (removeHakukohdeDialog != null) {
            getWindow().removeWindow(removeHakukohdeDialog);
        }
    }

    /**
     * Creation of the row component's layout.
     *
     * @param text - the text to be shown on the row.
     * @return
     */
    public HakukohdeResultRow format(String text, boolean withMenuBar) {
        isSelected = UiUtil.checkbox(null, null);
        isSelected.setImmediate(true);
        isSelected.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                if (hakukohde != null
                        && isSelected.booleanValue()) {
                    tarjontaPresenter.getSelectedhakukohteet().add(hakukohde);
                } else if (hakukohde != null) {
                    tarjontaPresenter.getSelectedhakukohteet().remove(hakukohde);
                }
            }
        });

        setWidth(-1, Sizeable.UNITS_PIXELS);
        setHeight(-1, Sizeable.UNITS_PIXELS); //Tämä toimii!!!

        addComponent(isSelected);
        if (withMenuBar) {
            Button nimiB = UiUtil.buttonLink(null, text);
            nimiB.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    tarjontaPresenter.showShowHakukohdeView(hakukohde.getHakukohde().getOid());
                    //TODO poistetaan kun tarkastelu on toteutettu
                    getWindow().showNotification("Tarkastelua ei ole toteutettu");
                }
            });
            nimiB.setStyleName("link-row");
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

    /**
     * The hakukohde the data of which is showed on this row.
     *
     * @return
     */
    public HakukohdeTulos getHakukohde() {
        return hakukohde;
    }

    private String T(String key, Object... args) {
        return i18n.getMessage(key, args);
    }
}
