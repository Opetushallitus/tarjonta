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
import fi.vm.sade.tarjonta.service.search.HakukohdePerustieto;
import fi.vm.sade.tarjonta.service.types.SisaltoTyyppi;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.auth.OrganisaatioContext;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.ui.enums.MenuBarActions;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.RemovalConfirmationDialog;
import fi.vm.sade.tarjonta.ui.view.common.TarjontaDialogWindow;
import fi.vm.sade.vaadin.ui.OphRowMenuBar;
import fi.vm.sade.vaadin.util.UiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * The component and functionality for showing a hakukohde object in hakukohde
 * search result list.
 *
 *
 * @author markus
 *
 */
@Configurable(preConstruction = false)
public class HakukohdeResultRow extends HorizontalLayout {

    private static final long serialVersionUID = 4163145140260915772L;
    private transient I18NHelper i18n = new I18NHelper(this);
    private static final SisaltoTyyppi HAKUKOHDE = SisaltoTyyppi.HAKUKOHDE;
    /**
     * The hakukohde to display on the row.
     */
    private HakukohdePerustieto hakukohde;
    private String hakukohdeNimi;
    /**
     * Checkbox to indicate if this row is selected.
     */
    private CheckBox isSelected;
    private Window removeHakukohdeDialog;
    private boolean hakuStarted = false;
    private String rowKey;
    /**
     * The presenter object for the component.
     */
    @Autowired(required = true)
    private TarjontaPresenter tarjontaPresenter;
    @Value("${koodisto-uris.erillishaku}")
    private String hakutapaErillishaku;
    private List<HakukohdePerustieto> children;

    public HakukohdeResultRow() {
        this.hakukohde = new HakukohdePerustieto();
    }

    public HakukohdeResultRow(HakukohdePerustieto hakukohde, String hakukohdeNimi) {
        this.hakukohde = hakukohde;
        

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
    private OphRowMenuBar rowMenuBar;

    private OphRowMenuBar newMenuBar() {
        rowMenuBar = new OphRowMenuBar("../oph/img/icon-treetable-button.png");
        reinitMenubar();
        return rowMenuBar;
    }

    void reinitMenubar() {
        final OrganisaatioContext context = OrganisaatioContext.getContext(this.hakukohde.getTarjoajaOid());

        final TarjontaTila tila = TarjontaTila.valueOf(hakukohde.getTila());
        rowMenuBar.clear();
        rowMenuBar.addMenuCommand(i18n.getMessage(MenuBarActions.SHOW.key), menuCommand);
       

        //jos tila = luonnos/kopioitu niin saa muokata oikeuksien puitteissa vaikka haussa kiinni
        //jos tila muu ja käynnissä olevassa haussa kiinni -> oph saa muokata
        /*if ((tila.isMutable() && tarjontaPresenter.getPermission().userCanUpdateHakukohde(context))
                || tarjontaPresenter.getPermission().userCanUpdateHakukohde(context, hakuStarted)) {*/

        if (checkForErillishakuAndRights(context) || tarjontaPresenter.getPermission().userCanUpdateHakukohde(context, hakuStarted)) {
            rowMenuBar.addMenuCommand(i18n.getMessage(MenuBarActions.EDIT.key), menuCommand);
        }

        rowMenuBar.addMenuCommand(i18n.getMessage("naytaKoulutukset"), menuCommand);

        if (tila.isRemovable() && tarjontaPresenter.getPermission().userCanDeleteHakukohde(context, hakuStarted)) {
            rowMenuBar.addMenuCommand(i18n.getMessage(MenuBarActions.DELETE.key), menuCommand);
        }
        
        if (tila.equals(TarjontaTila.VALMIS) && tarjontaPresenter.getPermission().userCanPublishKoulutus(context, hakuStarted) || checkForErillishakuAndRights(context)) {
            rowMenuBar.addMenuCommand(i18n.getMessage(MenuBarActions.PUBLISH.key), menuCommand);
        } else if (tila.equals(TarjontaTila.JULKAISTU) && tarjontaPresenter.getPermission().userCanCancelKoulutusPublish(context, hakuStarted)) {
            rowMenuBar.addMenuCommand(i18n.getMessage(MenuBarActions.CANCEL.key), menuCommand);
        } else if (tila.equals(TarjontaTila.PERUTTU) && tarjontaPresenter.getPermission().userCanPublishCancelledKoulutus()) {
            rowMenuBar.addMenuCommand(i18n.getMessage(MenuBarActions.PUBLISH.key), menuCommand);
        }
    }

    private boolean checkForErillishakuAndRights(OrganisaatioContext context) {
        return hakukohde.getHakutapaKoodi().getUri().contains(hakutapaErillishaku) && tarjontaPresenter.getPermission().userCanUpdateHakukohde(context);
    }

    /**
     * Fires an event based on user's selection in the row's menubar.
     *
     * @param selection the selection in the menu.
     */
    private void menuItemClicked(String selection) {
        final String hakukohdeOid = hakukohde.getOid();

        if (selection.equals(i18n.getMessage(MenuBarActions.SHOW.key))) {
            openHakukohdeView();
        } else if (selection.equals(i18n.getMessage(MenuBarActions.EDIT.key))) {
            tarjontaPresenter.getTarjoaja().setSelectedOrganisationOid(hakukohde.getTarjoajaOid());
            tarjontaPresenter.showHakukohdeEditView(null, hakukohdeOid, null, null);
        } else if (selection.equals(i18n.getMessage(MenuBarActions.DELETE.key))) {
            //TODO päivitä entiteetin tila containerissa, älä lataa kokohakutulosta uudelleen
            showRemoveDialog();
        } else if (selection.equals(i18n.getMessage("naytaKoulutukset"))) {
            tarjontaPresenter.showKoulutuksetForHakukohde(hakukohde);
        } else if (selection.equals(i18n.getMessage(MenuBarActions.PUBLISH.key))) {
            //TODO päivitä entiteetin tila containerissa, älä lataa kokohakutulosta uudelleen
            tarjontaPresenter.changeStateToPublished(hakukohdeOid, HAKUKOHDE);

        } else if (selection.equals(i18n.getMessage(MenuBarActions.CANCEL.key))) {
            //TODO päivitä entiteetin tila containerissa, älä lataa kokohakutulosta uudelleen
            showPeruutaDialog();
        }
    }
    
    private void showPeruutaDialog() {
        String peruutaQ = T("peruutaQ", 
                hakukohdeNimi, hakukohde.getKoulutuksenAlkamiskausi().getUri() + " " + hakukohde.getKoulutuksenAlkamisvuosi());
        RemovalConfirmationDialog cancelDialog = new RemovalConfirmationDialog(peruutaQ, null, T("removeYes"), T("removeNo"),
                new Button.ClickListener() {

                    private static final long serialVersionUID = -908351229767113315L;

            @Override
            public void buttonClick(ClickEvent event) {
                closeHakukohdeCreationDialog();
                tarjontaPresenter.changeStateToCancelled(hakukohde.getOid(), HAKUKOHDE);

            }

        },
               new Button.ClickListener() {
                private static final long serialVersionUID = 5019806363620874205L;

                @Override
                public void buttonClick(ClickEvent event) {
                    closeHakukohdeCreationDialog();

                }
            });
        removeHakukohdeDialog = new TarjontaDialogWindow(cancelDialog, T("peruutaDialog"));
        getWindow().addWindow(removeHakukohdeDialog);
    } 

    private void showRemoveDialog() {
        RemovalConfirmationDialog removeDialog = new RemovalConfirmationDialog(T("removeQ"), hakukohdeNimi, T("removeYes"), T("removeNo"),
                new Button.ClickListener() {
            private static final long serialVersionUID = -4938403467167578650L;

            @Override
            public void buttonClick(ClickEvent event) {
                closeHakukohdeCreationDialog();
                tarjontaPresenter.removeHakukohde(hakukohde.getOid());
                tarjontaPresenter.sendEvent(HakukohdeContainerEvent.delete(hakukohde.getOid()));
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

    private Date getMinHakuAlkamisDate(Date hakualkamisPvm) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(hakualkamisPvm);
        cal.add(Calendar.DATE, -4);
        return cal.getTime();
    }

    private boolean checkHakuStarted(Date haunAlkamisPvm) {

        Date hakuAlkamisPvm = getMinHakuAlkamisDate(haunAlkamisPvm);

        if (tarjontaPresenter.getPermission().userIsOphCrud()) {
            return false;
        }

        if (new Date().after(hakuAlkamisPvm)) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Creation of the row component's layout.
     *
     * @param text - the text to be shown on the row.
     * @return
     */
    public HakukohdeResultRow format(String text, boolean withMenuBar) {
        
        Date today = new Date();
        //Haku has started if the start date of the haku is is in the past and if the haku is not a lisahaku
        if  ((hakukohde != null
                    && hakukohde != null
                    && hakukohde.getHakuAlkamisPvm() != null
                    && hakukohde.getHakuAlkamisPvm().before(today))
                && (!(KoodistoURI.KOODI_LISAHAKU_URI.equals(hakukohde
                        .getHakutyyppiUri()) || KoodistoURI.KOODI_ERILLISHAKU_URI
                        .equals(hakukohde.getHakutapaKoodi().getUri())))) {
            hakuStarted = true;
            
            System.out.println("" + hakukohde.getHakutyyppiUri());
            System.out.println("" + hakukohde.getHakutapaKoodi().getUri());
            
        }

        if (hakukohde.getHakuAlkamisPvm() != null) {
            hakuStarted = checkHakuStarted(hakukohde.getHakuAlkamisPvm());
        }
        
        isSelected = UiUtil.checkbox(null, null);
        isSelected.setImmediate(true);
        isSelected.addListener(new Property.ValueChangeListener() {
            private static final long serialVersionUID = -613501895557976455L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (hakukohde != null
                		&& hakukohde != null
                        && isSelected.booleanValue()) {
                    tarjontaPresenter.getSelectedhakukohteet().add(hakukohde);
                } else if (hakukohde != null 
                		&& hakukohde != null) {
                    tarjontaPresenter.getSelectedhakukohteet().remove(hakukohde);
                }
                //tarjontaPresenter.togglePoistaHakukohdeB();
            }
        });

        setWidth(-1, Sizeable.UNITS_PIXELS);
        setHeight(-1, Sizeable.UNITS_PIXELS);

        addComponent(isSelected);
        if (withMenuBar) {
            Button nimiB = null;
            if (text.length() > 75) {
                String labelText = text.substring(0, 75) + "...";
                nimiB = UiUtil.buttonLink(null, labelText);
                nimiB.setDescription(text);
            } else {
                nimiB = UiUtil.buttonLink(null, text);
            }

            nimiB.addListener(new Button.ClickListener() {
                private static final long serialVersionUID = 7334263722794344559L;

                @Override
                public void buttonClick(ClickEvent event) {
                    openHakukohdeView();
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


    private String T(String key, Object... args) {
        return i18n.getMessage(key, args);
    }

    private void openHakukohdeView() {
        tarjontaPresenter.getTarjoaja().setSelectedResultRowOrganisationOid(hakukohde.getTarjoajaOid());
        tarjontaPresenter.getModel().setSelectedHakuStarted(hakuStarted);
        tarjontaPresenter.showHakukohdeViewImpl(hakukohde.getOid());
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
    public List<HakukohdePerustieto> getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(List<HakukohdePerustieto> children) {
        this.children = children;
    }
}
