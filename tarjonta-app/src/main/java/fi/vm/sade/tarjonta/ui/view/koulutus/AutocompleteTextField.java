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

import com.vaadin.data.Property;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

import fi.vm.sade.authentication.service.types.dto.HenkiloFatType;
import fi.vm.sade.authentication.service.types.dto.HenkiloType;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 * Autocomplete text field component for adding yhteyshenkilo for koulutus. Uses
 * UserService from authentication-api to search for available users.
 *
 * @author Markus
 *
 */
public class AutocompleteTextField extends TextField implements Handler {

    private static final long serialVersionUID = 6906639431420923L;
    private VerticalLayout vl;
    /* The suggestion list of users. */
    private ListSelect suggestionList;
    /* The button to clear current values in yhteyshenkilo fields.*/
    private Button clearYhtHenkiloB;
    private KoulutusToisenAsteenPerustiedotViewModel koulutusModel;
    private TarjontaPresenter presenter;
    /* The current list of users in the suggestionList*/
    private List<HenkiloType> henkilos;
    /*The currently selected index in the henkilos list. */
    private int selectedIndex = -1;
    /*The text typed by the user. */
    private String typedText;
    private Action arrowDownAction = new ShortcutAction("Arrow down", ShortcutAction.KeyCode.ARROW_DOWN, null);
    private Action arrowUpAction = new ShortcutAction("Arrow up", ShortcutAction.KeyCode.ARROW_UP, null);
    private Action enterAction = new ShortcutAction("Enter", ShortcutAction.KeyCode.ENTER, null);
    private Action tabAction = new ShortcutAction("Tab", ShortcutAction.KeyCode.TAB, null);
    private I18NHelper _i18n = new I18NHelper(this);
    private boolean isFocused = false;

    public AutocompleteTextField(VerticalLayout vl,
            String inputPrompt,
            String nullRepresentation,
            TarjontaPresenter presenter,
            KoulutusToisenAsteenPerustiedotViewModel koulutusModel) {
        super();
        this.vl = vl;
        setNullRepresentation(nullRepresentation);
        setInputPrompt(inputPrompt);
        this.presenter = presenter;
        this.koulutusModel = koulutusModel;
        setImmediate(true);
        buildLayout();

    }

    /*
     * Builds the layout of this component, i.e. the text field and the suggestion list
     * with listeners.
     */
    private void buildLayout() {
        buildTextField();
        buildSuggestionList();
    }

    /*
     * Builds the text field with listeners
     */
    private void buildTextField() {
        HorizontalLayout hl = UiUtil.horizontalLayout();
        hl.setSizeUndefined();
        hl.setSpacing(true);

        //Adding the listener for listening to the chars entered by the user.
        //The suggestion list is updated after each char.
        addListener(new TextChangeListener() {
            private static final long serialVersionUID = -2079651800984069901L;

            @Override
            public void textChange(TextChangeEvent event) {
                typedText = event.getText();
                populateYhtHenkiloSuggestions(presenter.searchYhteyshenkilo(event.getText()));
            }
        });


        addListener(new BlurListener() {
            private static final long serialVersionUID = 255329698847125307L;

            @Override
            public void blur(BlurEvent event) {
                isFocused = false;
            }
        });

        addListener(new FocusListener() {
            private static final long serialVersionUID = 1872174705046291119L;

            @Override
            public void focus(FocusEvent event) {
                isFocused = true;

            }
        });

        hl.addComponent(this);

        //The clear button. When the button is pressed the yhteyshenkilo fields are cleared.
        clearYhtHenkiloB = UiUtil.buttonLink(hl, T("tyhjenna"), new Button.ClickListener() {
            private static final long serialVersionUID = -6386527358361971773L;

            @Override
            public void buttonClick(ClickEvent event) {
                handleClearButtonClick();

            }
        });
        hl.setComponentAlignment(this, Alignment.MIDDLE_LEFT);
        hl.setComponentAlignment(clearYhtHenkiloB, Alignment.MIDDLE_LEFT);
        vl.addComponent(hl);
    }

    /*
     * Builds the suggestion list with listeners.
     */
    private void buildSuggestionList() {
        suggestionList = new ListSelect();

        suggestionList.setSizeUndefined();
        suggestionList.setWidth("175px");
        suggestionList.setNullSelectionAllowed(false);
        suggestionList.setImmediate(true);
        suggestionList.setVisible(false);

        //Adding listener to value change. On value change the yhteyshenkilo 
        //fields are updated according to the selected user.
        suggestionList.addListener(new Property.ValueChangeListener() {
            private static final long serialVersionUID = 3743367454230254280L;

            @Override
            public void valueChange(
                    com.vaadin.data.Property.ValueChangeEvent event) {
                handleValueChange();

            }
        });
        vl.addComponent(suggestionList);
    }


    /*Handling of clear button click. Setting the text field to null and fireing event.
     * Event is catched by EditKoulutusPerustiedotForm which sets other yhteyshenkilo fields to null. */
    private void handleClearButtonClick() {
        this.setValue(null);
        clearYhteyshenkiloOid();
        fireEvent(new HenkiloAutocompleteEvent(this, null, HenkiloAutocompleteEvent.CLEAR));
    }


    /*
     * Populates the henkilo suggestions under the yhtHenkKokoNimi field in according
     * to current search results from UserService.
     */
    private void populateYhtHenkiloSuggestions(List<HenkiloType> henkilos) {
        this.henkilos = henkilos;
        selectedIndex = -1;
        if (!henkilos.isEmpty()) {
            getWindow().addActionHandler(this);
            suggestionList.setVisible(true);
            suggestionList.removeAllItems();
            suggestionList.setRows(henkilos.size() + 1);

            for (HenkiloType curHenkilo : henkilos) {
                suggestionList.addItem(curHenkilo);
                suggestionList.setItemCaption(curHenkilo, curHenkilo.getEtunimet() + " " + curHenkilo.getSukunimi());
            }
        } else {
            getWindow().removeActionHandler(this);
            suggestionList.setVisible(false);
            suggestionList.removeAllItems();
            clearYhteyshenkiloOid();
            fireEvent(new HenkiloAutocompleteEvent(this, null, HenkiloAutocompleteEvent.NOT_SELECTED));
        }
        focus();
    }
    /*
     * Handling of value change event. Fires an event witch is listened by EditKoulutusPerustiedotForm which updates the yhteyshenkilo fields. 
     */

    private void handleValueChange() {
        HenkiloType henkiloType = (HenkiloType) (suggestionList.getValue());

        fireEvent(new HenkiloAutocompleteEvent(this, henkiloType , HenkiloAutocompleteEvent.SELECTED));
        if (!isFocused) {
            handleEnter();
        }
    }

    /*
     * Handling of arrow down key events. If the suggestion list is currently visible
     * and there are suggestions in the list, the next user in the list is selected.
     */
    private void arrowDownHandler() {
        if (!suggestionList.isVisible() || henkilos == null || henkilos.isEmpty()) {
            return;
        }

        //If last user is already selected index is not updated.
        if (selectedIndex < this.henkilos.size() - 1) {
            ++selectedIndex;
        }
        HenkiloType selectedHenkilo = henkilos.get(selectedIndex);
        suggestionList.select(selectedHenkilo);
    }

    /*
     * Handling of arrow up key events. If the list is currently visible
     * and there are suggestions in the list, the previous user in the list is selected.
     */
    private void arrowUpHandler() {
        if (!suggestionList.isVisible() || henkilos == null || henkilos.isEmpty()) {
            return;
        }
        --selectedIndex;
        //If the currently selected user is not the first in the list the previous
        //user is selected, otherwise the selection is removed and a user not selected.
        //is fired and the string typed by the user is set as the value of the text field. 
        if (selectedIndex >= 0) {
            HenkiloType selectedHenkilo = henkilos.get(selectedIndex);
            suggestionList.select(selectedHenkilo);
        } else {
            setValue(typedText);
            suggestionList.unselect(henkilos.get(selectedIndex + 1));
            fireEvent(new HenkiloAutocompleteEvent(this, null, HenkiloAutocompleteEvent.NOT_SELECTED));
        }
    }

    /*
     * Handling of of enter key events. The suggestion list is hidden if it is visible.
     */
    private void handleEnter() {
        if (!suggestionList.isVisible() || henkilos == null || henkilos.isEmpty()) {
            return;
        }
        suggestionList.removeAllItems();
        suggestionList.setVisible(false);
        getWindow().removeActionHandler(this);
    }

    //Handler interface methods. The interface is implemented to be able to listen to keyboard events.
    @Override
    public Action[] getActions(Object target, Object sender) {
        return new Action[]{arrowDownAction, arrowUpAction, enterAction, tabAction};
    }

    @Override
    public void handleAction(Action action, Object sender, Object target) {
        if (action == arrowDownAction) {
            arrowDownHandler();
        }
        if (action == arrowUpAction) {
            arrowUpHandler();
        }
        if (action == enterAction || action == tabAction) {
            handleEnter();
        }
    }

    protected void clearYhteyshenkiloOid() {
        this.koulutusModel.setYhtHenkiloOid(null);
    }

    private String T(String key) {
        return _i18n.getMessage(key);
    }

    protected List<HenkiloType> searchYhteyshenkilo(TextChangeEvent event) {
        return presenter.searchYhteyshenkilo(event.getText());
    }

    /**
     * Event class to signal events in the autocomplete text field.
     *
     * @author Markus
     */
    public class HenkiloAutocompleteEvent extends Component.Event {

        private static final long serialVersionUID = -7164075226636500573L;
        /**
         * A user is selected in the suggestion list
         */
        public static final int SELECTED = 0;
        /**
         * User selection is removed from the suggestion list.
         */
        public static final int NOT_SELECTED = 1;
        /**
         * The clear button is pressed.
         */
        public static final int CLEAR = 2;
        /**
         * Event type. One of the above.
         */
        private int eventType;
        /**
         * The user selected.
         */
        private HenkiloType henkilo;

        public HenkiloAutocompleteEvent(Component source, HenkiloType henkilo, int eventType) {
            super(source);
            this.setEventType(eventType);
            this.setHenkilo(henkilo);
        }

        public HenkiloType getHenkilo() {
            return henkilo;
        }

        public void setHenkilo(HenkiloType henkilo) {
            this.henkilo = henkilo;
        }

        public int getEventType() {
            return eventType;
        }

        public void setEventType(int eventType) {
            this.eventType = eventType;
        }
    }
}
