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
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetVastausTyyppi.HakukohdeTulos;
import fi.vm.sade.tarjonta.service.types.SisaltoTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.ui.enums.MenuBarActions;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.service.OrganisaatioContext;
import fi.vm.sade.tarjonta.ui.view.common.RemovalConfirmationDialog;
import fi.vm.sade.tarjonta.ui.view.common.TarjontaDialogWindow;
import fi.vm.sade.vaadin.ui.OphRowMenuBar;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Date;

/**
 * The component and functionality for showing a hakukohde object in hakukohde
 * search result list.
 *
 * TKatva, 25.3.2013. This should be refactored so that hakukohdeTulos-object would be passed to other views.
 * Now instead every view loads it again. For example "inpection"-view could use just the passed model. Edit view maybe should load model again.
 *
 * @author markus
 *
 */
@Configurable(preConstruction = false)
public class HakukohdeResultRow extends HorizontalLayout {

    private static final long serialVersionUID = 4163145140260915772L;
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

    private boolean hakuStarted = false;
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
        Date today = new Date();
        if (hakukohde!=null
        		&& hakukohde.getHaku() != null
        		&& hakukohde.getHaku().getHakuAlkamisPvm() != null
        		&& hakukohde.getHaku().getHakuAlkamisPvm().before(today)) {
            hakuStarted = true;
        }

        this.hakukohdeNimi = hakukohdeNimi;
    }
    /**
     * Command object for the row menubar. Starts operations based on user's
     * selection in the menu.
     */
    private MenuBar.Command menuCommand = new MenuBar.Command() {

        private static final long serialVersionUID = -3198339721387004359L;

        @Override
        public void menuSelected(MenuBar.MenuItem selectedItem) {
            //DEBUGSAWAY:LOG.debug(selectedItem.getText());
            menuItemClicked(selectedItem.getText());

        }
    };
    OphRowMenuBar rowMenuBar;

    private OphRowMenuBar newMenuBar() {
        final OrganisaatioContext context = OrganisaatioContext.getContext(this.hakukohde.getKoulutus().getTarjoaja());

        rowMenuBar = new OphRowMenuBar("../oph/img/icon-treetable-button.png");
        final TarjontaTila tila = hakukohde.getHakukohde().getTila();

        rowMenuBar.addMenuCommand(i18n.getMessage(MenuBarActions.SHOW.key), menuCommand);

        if (tarjontaPresenter.getPermission().userCanUpdateHakukohde(context) && !hakuStarted) {
            rowMenuBar.addMenuCommand(i18n.getMessage(MenuBarActions.EDIT.key), menuCommand);
        }

        rowMenuBar.addMenuCommand(i18n.getMessage("naytaKoulutukset"), menuCommand);

        if (tila.equals(TarjontaTila.LUONNOS) && tarjontaPresenter.getPermission().userCanDeleteHakukohde(context)) {
            rowMenuBar.addMenuCommand(i18n.getMessage(MenuBarActions.DELETE.key), menuCommand);
        }

        if (tila.equals(TarjontaTila.VALMIS) && tarjontaPresenter.getPermission().userCanPublishKoulutus(context)) {
            rowMenuBar.addMenuCommand(i18n.getMessage(MenuBarActions.PUBLISH.key), menuCommand);
        } else if (tila.equals(TarjontaTila.JULKAISTU) && tarjontaPresenter.getPermission().userCanCancelPublish(context)) {
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
            //Ugly, refactor when whole object is passed
            tarjontaPresenter.getModel().setSelectedHakuStarted(hakuStarted);
            tarjontaPresenter.showHakukohdeViewImpl(hakukohdeOid);
        } else if (selection.equals(i18n.getMessage(MenuBarActions.EDIT.key))) {
            tarjontaPresenter.showHakukohdeEditView(null, hakukohdeOid,null);
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

                    private static final long serialVersionUID = -4938403467167578650L;

            @Override
            public void buttonClick(ClickEvent event) {
                closeHakukohdeCreationDialog();
                tarjontaPresenter.removeHakukohde(hakukohde);
            }
        },
                new Button.ClickListener() {

                    private static final long serialVersionUID = 8488147921050732676L;

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

            private static final long serialVersionUID = -613501895557976455L;

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
        setHeight(-1, Sizeable.UNITS_PIXELS);

        addComponent(isSelected);
        if (withMenuBar) {
            Button nimiB = null;
            if (text.length() > 75) {
                String labelText = text.substring(0,  75) + "...";
                nimiB = UiUtil.buttonLink(null, labelText);
                nimiB.setDescription(text);
            } else {
                nimiB = UiUtil.buttonLink(null, text);
            }
            
            nimiB.addListener(new Button.ClickListener() {

                private static final long serialVersionUID = 7334263722794344559L;

                @Override
                public void buttonClick(ClickEvent event) {
                    tarjontaPresenter.showHakukohdeViewImpl(hakukohde.getHakukohde().getOid());
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
