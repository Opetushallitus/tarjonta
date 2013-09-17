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
package fi.vm.sade.tarjonta.ui.view.haku;

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
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;

import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.service.types.SisaltoTyyppi;
import fi.vm.sade.tarjonta.shared.types.TarjontaTila;
import fi.vm.sade.tarjonta.ui.enums.MenuBarActions;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import fi.vm.sade.tarjonta.ui.presenter.HakuPresenter;
import fi.vm.sade.tarjonta.ui.view.common.RemovalConfirmationDialog;
import fi.vm.sade.tarjonta.ui.view.common.TarjontaDialogWindow;
import fi.vm.sade.vaadin.ui.OphRowMenuBar;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The component and functioality for showing a haku object in haku search
 * result list.
 *
 * @author markus
 *
 */
@Configurable(preConstruction = false)
public class HakuResultRow extends HorizontalLayout {

    private static final long serialVersionUID = 8295347976959013279L;
    private static final Logger LOG = LoggerFactory.getLogger(HakuResultRow.class);
    private transient I18NHelper i18n = new I18NHelper(this);
    private static final SisaltoTyyppi HAKU = SisaltoTyyppi.HAKU;
    private final HakuViewModel haku;
    private CheckBox isSelected;
    private String hakuNimi;
    private Window removeDialogWindow;
    @Autowired(required = true)
    private HakuPresenter hakuPresenter;

    public HakuResultRow() {
        this.haku = new HakuViewModel();
    }

    public HakuResultRow(HakuViewModel haku, String hakuNimi) {
        this.haku = haku;
        this.hakuNimi = hakuNimi;
    }
    private MenuBar.Command menuCommand = new MenuBar.Command() {

        private static final long serialVersionUID = -6595316501605366763L;

        @Override
        public void menuSelected(MenuBar.MenuItem selectedItem) {
            
            menuItemClicked(selectedItem.getText());

        }
    };
    OphRowMenuBar rowMenuBar;

    private OphRowMenuBar newMenuBar() {
        rowMenuBar = new OphRowMenuBar("../oph/img/icon-treetable-button.png");
        reinitMenubar();

        return rowMenuBar;
    }

    public void reinitMenubar() {
        final TarjontaTila tila = TarjontaTila.valueOf(haku.getHakuDto().getHaunTila());
        rowMenuBar.clear();

        rowMenuBar.addMenuCommand(i18n.getMessage(MenuBarActions.SHOW.key), menuCommand);

        if (hakuPresenter.getPermission().userCanUpdateHaku()) {
            rowMenuBar.addMenuCommand(i18n.getMessage(MenuBarActions.EDIT.key), menuCommand);
        }

        rowMenuBar.addMenuCommand(i18n.getMessage("naytaKohteet"), menuCommand);

        if (tila.isRemovable() && hakuPresenter.getPermission().userCanDeleteHaku()) {
            rowMenuBar.addMenuCommand(i18n.getMessage(MenuBarActions.DELETE.key), menuCommand);
        }

        if (tila.equals(TarjontaTila.VALMIS) && hakuPresenter.getPermission().userCanPublishHaku()) {
            rowMenuBar.addMenuCommand(i18n.getMessage(MenuBarActions.PUBLISH.key), menuCommand);
        } else if (tila.equals(TarjontaTila.JULKAISTU) && hakuPresenter.getPermission().userCanCancelHakuPublish()) {
            rowMenuBar.addMenuCommand(i18n.getMessage(MenuBarActions.CANCEL.key), menuCommand);
        }
    }

    private void menuItemClicked(String selection) {


        if (selection.equals(i18n.getMessage(MenuBarActions.SHOW.key))) {
            fireEvent(new HakuRowMenuEvent(this, haku, HakuRowMenuEvent.VIEW));
        } else if (selection.equals(i18n.getMessage(MenuBarActions.EDIT.key))) {
            fireEvent(new HakuRowMenuEvent(this, haku, HakuRowMenuEvent.EDIT));
        } else if (selection.equals(i18n.getMessage(MenuBarActions.DELETE.key))) {
            showRemoveDialog();
        } else if (selection.equals(i18n.getMessage(MenuBarActions.PUBLISH.key))) {
             showPublishDialog();
        } else if (selection.equals(i18n.getMessage(MenuBarActions.CANCEL.key))) {
            showCancelPublishDialog();
        }
    }

    private void showRemoveDialog() {
        RemovalConfirmationDialog removeDialog = new RemovalConfirmationDialog(T("removeQ"), hakuNimi, T("removeYes"), T("removeNo"),
                new Button.ClickListener() {

                    private static final long serialVersionUID = 6555227248805537953L;

            @Override
            public void buttonClick(ClickEvent event) {
                closeHakuRemovalDialog();
                startHakuRemoval();
            }
        },
                new Button.ClickListener() {

                    private static final long serialVersionUID = -3276341748051443397L;

            @Override
            public void buttonClick(ClickEvent event) {
                closeHakuRemovalDialog();

            }
        });
        removeDialogWindow = new TarjontaDialogWindow(removeDialog, T("removeDialog"));
        getWindow().addWindow(removeDialogWindow);
    }

    private void showCancelPublishDialog() {
        RemovalConfirmationDialog removeDialog = new RemovalConfirmationDialog(T("cancelPublishQ"), hakuNimi, T("removeYes"), T("removeNo"),
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        closeHakuRemovalDialog();
                        cancelHakuPublish();

                    }
                },
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        closeHakuRemovalDialog();

                    }
                });
        removeDialogWindow = new TarjontaDialogWindow(removeDialog, T("cancelPublishTitle"));
        removeDialogWindow.setResizable(false);
        getWindow().addWindow(removeDialogWindow);
    }

    private void showPublishDialog() {
        RemovalConfirmationDialog removeDialog = new RemovalConfirmationDialog(T("publishQ"), hakuNimi, T("removeYes"), T("removeNo"),
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        closeHakuRemovalDialog();
                        startHakuPublish();

                    }
                },
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        closeHakuRemovalDialog();

                    }
                });
        removeDialogWindow = new TarjontaDialogWindow(removeDialog, T("publishHakuTitle"));
        removeDialogWindow.setResizable(false);

        getWindow().addWindow(removeDialogWindow);
    }

    public void closeHakuRemovalDialog() {
        if (removeDialogWindow != null) {
            getWindow().removeWindow(removeDialogWindow);
        }
    }

    private void cancelHakuPublish() {
        hakuPresenter.changeStateToCancelled(haku.getHakuOid(), HAKU);
        hakuPresenter.sendEvent(HakuContainerEvent.update(haku.getHakuOid()));
    }

    private void startHakuPublish() {
        hakuPresenter.changeStateToPublished(haku.getHakuOid(), HAKU);
        hakuPresenter.sendEvent(HakuContainerEvent.update(haku.getHakuOid()));
    }

    private void startHakuRemoval() {
        fireEvent(new HakuRowMenuEvent(this, haku, HakuRowMenuEvent.REMOVE));
        hakuPresenter.sendEvent(HakuContainerEvent.delete(haku.getHakuOid()));
    }

    /**
     * Creation of the row component.
     *
     * @param text - the text to be shown on the row.
     * @return
     */
    public HakuResultRow format(String text, boolean withMenuBar) {
        isSelected = UiUtil.checkbox(null, null);
        isSelected.setImmediate(true);
        isSelected.addListener(new Property.ValueChangeListener() {

            private static final long serialVersionUID = 8857751432323545143L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (haku != null
                        && haku.getHakuOid() != null
                        && isSelected.booleanValue()) {
                    hakuPresenter.selectHaku(haku);
                } else if (haku != null
                        && haku.getHakuOid() != null) {
                    hakuPresenter.unSelectHaku(haku);
                    
                }
            }
        });
        setWidth(-1, Sizeable.UNITS_PIXELS);
        setHeight(-1, Sizeable.UNITS_PIXELS);

        addComponent(isSelected);
        if (withMenuBar) {
            Button nimiB = UiUtil.buttonLink(null, text);
            nimiB.addListener(new Button.ClickListener() {

                private static final long serialVersionUID = 6974472604937788133L;

                @Override
                public void buttonClick(ClickEvent event) {
                    menuItemClicked(i18n.getMessage("tarkastele"));
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

    public CheckBox getIsSelected() {
        return isSelected;
    }

    public class HakuRowMenuEvent extends Component.Event {

        private static final long serialVersionUID = 177504337415571268L;
        public static final String REMOVE = "remove";
        public static final String EDIT = "edit";
        public static final String VIEW = "view";
        private HakuViewModel haku;
        private String type;

        public HakuRowMenuEvent(Component source, HakuViewModel haku, String type) {
            super(source);
            this.haku = haku;
            this.type = type;
        }

        public HakuRowMenuEvent(Component source) {
            super(source);
        }

        public HakuViewModel getHaku() {
            return haku;
        }

        public String getType() {
            return type;
        }
    }

    private String T(String key, Object... args) {
        return i18n.getMessage(key, args);
    }
}
