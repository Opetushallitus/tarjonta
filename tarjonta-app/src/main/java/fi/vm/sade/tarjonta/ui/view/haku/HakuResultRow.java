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
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import fi.vm.sade.tarjonta.ui.view.HakuPresenter;
import fi.vm.sade.vaadin.ui.OphRowMenuBar;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The component and functioality for showing a haku object in haku search result list.
 *
 * @author markus
 *
 */
@Configurable(preConstruction = false)
public class HakuResultRow  extends HorizontalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(HakuResultRow.class);

    private I18NHelper i18n = new I18NHelper(this);
    private HakuViewModel haku;
    private CheckBox isSelected;

    @Autowired(required = true)
    private HakuPresenter hakuPresenter;

    public HakuResultRow() {
        this.haku = new HakuViewModel();
    }

    public HakuResultRow(HakuViewModel haku) {
        this.haku = haku;
    }

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
        rowMenuBar.addMenuCommand(i18n.getMessage("naytaKohteet"), menuCommand);
        rowMenuBar.addMenuCommand(i18n.getMessage("poista"), menuCommand);
        return rowMenuBar;
    }

    private void menuItemClicked(String selection) {
        if (selection.equals(i18n.getMessage("tarkastele"))) {
            fireEvent(new HakuRowMenuEvent(this, haku, HakuRowMenuEvent.VIEW));
        } else if (selection.equals(i18n.getMessage("muokkaa"))) {
            fireEvent(new HakuRowMenuEvent(this, haku, HakuRowMenuEvent.EDIT));
        } else if (selection.equals(i18n.getMessage("poista"))) {
            fireEvent(new HakuRowMenuEvent(this, haku, HakuRowMenuEvent.REMOVE));
        }
    }

    /**
     * Creation of the row component.
     * @param text - the text to be shown on the row.
     * @return
     */
    public HakuResultRow format(String text, boolean withMenuBar) {
        isSelected = UiUtil.checkbox(null, null);
        isSelected.setImmediate(true);
        isSelected.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                if (haku != null
                        && isSelected.booleanValue()) {
                    hakuPresenter.getSelectedhaut().add(haku);
                } else if (haku != null) {
                    hakuPresenter.getSelectedhaut().remove(haku);
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

    public CheckBox getIsSelected() {
        return isSelected;
    }

    public class HakuRowMenuEvent extends Component.Event {

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

}
