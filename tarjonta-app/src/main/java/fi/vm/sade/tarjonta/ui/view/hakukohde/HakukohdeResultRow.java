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
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetVastausTyyppi.HakukohdeTulos;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.vaadin.ui.OphRowMenuBar;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The component and functionality for showing a hakukohde object in hakukohde search result list.
 *
 * @author markus
 *
 */
@Configurable(preConstruction = false)
public class HakukohdeResultRow extends HorizontalLayout {

	private static final Logger LOG = LoggerFactory.getLogger(HakukohdeResultRow.class);

    private I18NHelper i18n = new I18NHelper(this);
    /**
     * The hakukohde to display on the row.
     */
    private HakukohdeTulos hakukohde;

    /**
     * Checkbox to indicate if this row is selected.
     */
    private CheckBox isSelected;

    /**
     * The presenter object for the component.
     */
    @Autowired(required = true)
    private TarjontaPresenter tarjontaPresenter;

    public HakukohdeResultRow() {
        this.hakukohde = new HakukohdeTulos();
    }

    public HakukohdeResultRow(HakukohdeTulos hakukohde) {
        this.hakukohde = hakukohde;
    }

    /**
     * Command object for the row menubar. Starts operations
     * based on user's selection in the menu.
     */
    private MenuBar.Command menuCommand = new MenuBar.Command() {
        @Override
        public void menuSelected(MenuBar.MenuItem selectedItem) {
            LOG.debug(selectedItem.getText());
            menuItemClicked(selectedItem.getText());

        }

    };
    OphRowMenuBar rowMenuBar;

    private OphRowMenuBar newMenuBar() {
        rowMenuBar = new OphRowMenuBar("../oph/img/icon-treetable-button.png");
        rowMenuBar.addMenuCommand(i18n.getMessage("tarkastele"), menuCommand);
        rowMenuBar.addMenuCommand(i18n.getMessage("muokkaa"), menuCommand);
        rowMenuBar.addMenuCommand(i18n.getMessage("naytaKoulutukset"), menuCommand);
        rowMenuBar.addMenuCommand(i18n.getMessage("poista"), menuCommand);
        return rowMenuBar;
    }

    /**
     * Fires an event based on user's selection in the row's menubar.
     * @param selection the selection in the menu.
     */
    private void menuItemClicked(String selection) {
        if (selection.equals(i18n.getMessage("tarkastele"))) {
            fireEvent(new HakukohdeRowMenuEvent(this, hakukohde, HakukohdeRowMenuEvent.VIEW));
        } else if (selection.equals(i18n.getMessage("muokkaa"))) {
        	tarjontaPresenter.showHakukohdeEditView(null, hakukohde.getHakukohde().getOid());
        } else if (selection.equals(i18n.getMessage("poista"))) {
        	tarjontaPresenter.removeHakukohde(hakukohde);
        }
    }

    /**
     * Creation of the row component's layout.
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
        Label label = new Label(text);
        label.setSizeUndefined(); // -1,-1
        setWidth(-1, Sizeable.UNITS_PIXELS);
        setHeight(-1, Sizeable.UNITS_PIXELS); //Tämä toimii!!!

        addComponent(isSelected);
        if (withMenuBar) {
            addComponent(newMenuBar());
        }
        addComponent(label);

        setExpandRatio(label, 1f); //default == 0

        return this;
    }

    /**
     * Gets the isSelected checkbox component.
     * @return
     */
    public CheckBox getIsSelected() {
        return isSelected;
    }

    /**
     * Event to be fired by HakukohdeResultRow object when the user
     * makes a selection in the row's menubar.
     * @author Markus
     */
    public class HakukohdeRowMenuEvent extends Component.Event {

        public static final String REMOVE = "remove";
        public static final String EDIT = "edit";
        public static final String VIEW = "view";

        private HakukohdeTulos hakukohde;
        private String type;


        public HakukohdeRowMenuEvent(Component source, HakukohdeTulos hakukohde, String type) {
            super(source);
            this.hakukohde = hakukohde;
            this.type = type;
        }

        public HakukohdeRowMenuEvent(Component source) {
            super(source);
        }


        public HakukohdeTulos getHaku() {
            return hakukohde;
        }


        public String getType() {
            return type;
        }
    }

}
