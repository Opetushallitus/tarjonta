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

import com.google.common.base.Preconditions;
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

import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.KKAutocompleteModel;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 * Simple autocomplete text field.
 *
 * @author Jani
 *
 */
public class SimpleAutocompleteTextField extends TextField implements Handler {

    private static final long serialVersionUID = 8944710358662496253L;
    private VerticalLayout vl;
    /* The suggestion list of text string. */
    private ListSelect suggestionList;
    /* The button to clear current values in fields.*/
    private Button clear;
    private IAutocompleteSearch presenter;
    /* The current list of users in the suggestionList*/
    private List<IAutocompleteModel> inputText;
    /*The currently selected index in  list. */
    private int selectedIndex = -1;
    /*The text typed by the user. */
    private String typedText;
    private Action arrowDownAction = new ShortcutAction("Arrow down", ShortcutAction.KeyCode.ARROW_DOWN, null);
    private Action arrowUpAction = new ShortcutAction("Arrow up", ShortcutAction.KeyCode.ARROW_UP, null);
    private Action enterAction = new ShortcutAction("Enter", ShortcutAction.KeyCode.ENTER, null);
    private Action tabAction = new ShortcutAction("Tab", ShortcutAction.KeyCode.TAB, null);
    private I18NHelper _i18n = new I18NHelper(this);
    private boolean isFocused = false;

    public SimpleAutocompleteTextField(final VerticalLayout vl, final IAutocompleteSearch presenter, final String nullRepresentation) {
        super();
        Preconditions.checkNotNull(presenter, "Presenter object cannot be null.");
        Preconditions.checkNotNull(vl, "Vertical Layout object cannot be null.");

        this.vl = vl;
        setNullRepresentation(nullRepresentation);
        this.presenter = presenter;
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
                populateSuggestions(presenter.searchAutocompleteText(event.getText()));
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

        //The clear button. When the button is pressed the fields are cleared.
        clear = UiUtil.buttonLink(hl, T("tyhjenna"), new Button.ClickListener() {
            private static final long serialVersionUID = -6386527358361971773L;

            @Override
            public void buttonClick(ClickEvent event) {
                handleClearButtonClick();

            }
        });
        hl.setComponentAlignment(this, Alignment.MIDDLE_LEFT);
        hl.setComponentAlignment(clear, Alignment.MIDDLE_LEFT);
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


    /*Handling of clear button click. Setting the text field to null and fireing event. */
    private void handleClearButtonClick() {
        this.setValue(null);
        clearSelectedText();
        fireEvent(new SimpleTextAutocompleteEvent(this, null, SimpleTextAutocompleteEvent.CLEAR));
    }


    /*
     * Populates the suggestions.
     */
    private void populateSuggestions(List<IAutocompleteModel> models) {
        this.inputText = models;
        selectedIndex = -1;
        if (!models.isEmpty()) {
            getWindow().addActionHandler(this);
            suggestionList.setVisible(true);
            suggestionList.removeAllItems();
            suggestionList.setRows(models.size() + 1);

            for (IAutocompleteModel cur : models) {
                suggestionList.addItem(cur);
                suggestionList.setItemCaption(cur, cur.getText());
            }
        } else {
            getWindow().removeActionHandler(this);
            suggestionList.setVisible(false);
            suggestionList.removeAllItems();
            clearSelectedText();
            fireEvent(new SimpleTextAutocompleteEvent(this, null, SimpleTextAutocompleteEvent.NOT_SELECTED));
        }
        focus();
    }

    /*
     * Handling of value change event. 
     */
    private void handleValueChange() {
        fireEvent(new SimpleTextAutocompleteEvent(this, (IAutocompleteModel) (suggestionList.getValue()), SimpleTextAutocompleteEvent.SELECTED));
        if (!isFocused) {
            handleEnter();
        }
    }

    public void loadSelected(final String oid) {
        if (oid != null) {
            this.setValue(null);
            fireEvent(new SimpleTextAutocompleteEvent(this, presenter.loadSelected(oid), SimpleTextAutocompleteEvent.SELECTED));
            handleEnter();
        }
    }

    /*
     * Handling of arrow down key events. If the suggestion list is currently visible
     * and there are suggestions in the list, the next user in the list is selected.
     */
    private void arrowDownHandler() {
        if (!suggestionList.isVisible() || inputText == null || inputText.isEmpty()) {
            return;
        }

        //If last user is already selected index is not updated.
        if (selectedIndex < this.inputText.size() - 1) {
            ++selectedIndex;
        }
        IAutocompleteModel nextModel = inputText.get(selectedIndex);
        suggestionList.select(nextModel);
    }

    /*
     * Handling of arrow up key events. If the list is currently visible
     * and there are suggestions in the list, the previous user in the list is selected.
     */
    private void arrowUpHandler() {
        if (!suggestionList.isVisible() || inputText == null || inputText.isEmpty()) {
            return;
        }
        --selectedIndex;
        //If the currently selected user is not the first in the list the previous
        //user is selected, otherwise the selection is removed and a user not selected.
        //is fired and the string typed by the user is set as the value of the text field. 
        if (selectedIndex >= 0) {
            IAutocompleteModel prevModel = inputText.get(selectedIndex);
            suggestionList.select(prevModel);
        } else {
            setValue(typedText);
            suggestionList.unselect(inputText.get(selectedIndex + 1));
            fireEvent(new SimpleTextAutocompleteEvent(this, null, SimpleTextAutocompleteEvent.NOT_SELECTED));
        }
    }

    /*
     * Handling of of enter key events. The suggestion list is hidden if it is visible.
     */
    private void handleEnter() {
        if (!suggestionList.isVisible() || inputText == null || inputText.isEmpty()) {
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

    protected void clearSelectedText() {
        presenter.clearAutocompleteTextField();
    }

    private String T(String key) {
        return _i18n.getMessage(key);
    }

    protected List<IAutocompleteModel> searchText(TextChangeEvent event) {
        return presenter.searchAutocompleteText(event.getText());
    }

    /**
     * Event class to signal events in the autocomplete text field.
     *
     * @author Markus
     */
    public class SimpleTextAutocompleteEvent extends Component.Event {

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
        private IAutocompleteModel model;

        public SimpleTextAutocompleteEvent(Component source, IAutocompleteModel model, int eventType) {
            super(source);
            this.setEventType(eventType);
            this.setModel(model);
        }

        public IAutocompleteModel getModel() {
            return model;
        }

        public void setModel(IAutocompleteModel model) {
            this.model = model;
        }

        public int getEventType() {
            return eventType;
        }

        public void setEventType(int eventType) {
            this.eventType = eventType;
        }
    }

    public interface IAutocompleteSearch {

        public List<IAutocompleteModel> searchAutocompleteText(final String searchword);

        public void clearAutocompleteTextField();

        public KKAutocompleteModel loadSelected(final String oid);
    }

    public interface IAutocompleteModel {

        public String getText();
    }
}
