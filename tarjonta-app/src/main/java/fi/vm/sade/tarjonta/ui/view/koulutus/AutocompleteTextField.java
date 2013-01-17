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

import fi.vm.sade.authentication.service.types.dto.HenkiloType;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.ui.model.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 * Autocomplete text field component for adding yhteyshenkilo for koulutus.
 * Uses UserService from authentication-api to search for available users.
 * @author Markus
 *
 */
public class AutocompleteTextField extends TextField implements Handler {
    
    private static final long serialVersionUID = 6906639431420923L;
    VerticalLayout vl;
    private ListSelect suggestionList;
    private Button clearYhtHenkiloB;
    private KoulutusToisenAsteenPerustiedotViewModel koulutusModel;
    private TarjontaPresenter presenter;
    private List<HenkiloType> henkilos;
    private int selectedIndex = -1;
    String typedText;
    
    private Action arrowDownAction = new ShortcutAction("Arrow down", ShortcutAction.KeyCode.ARROW_DOWN, null);
    private Action arrowUpAction = new ShortcutAction("Arrow up", ShortcutAction.KeyCode.ARROW_UP, null);
    private Action enterAction = new ShortcutAction("Enter", ShortcutAction.KeyCode.ENTER, null);
    
    private I18NHelper _i18n = new I18NHelper(this);
    
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
    
    private void buildLayout() {
        HorizontalLayout hl = UiUtil.horizontalLayout();
        hl.setSizeUndefined();
        hl.setSpacing(true);
        addListener(new TextChangeListener() {

            private static final long serialVersionUID = -2079651800984069901L;

            @Override
            public void textChange(TextChangeEvent event) {
                    typedText = event.getText();
                    populateYhtHenkiloSuggestions(presenter.searchYhteyshenkilo(event.getText()));
            }
            
        });
       
        this.addListener(new BlurListener() {

            private static final long serialVersionUID = 255329698847125307L;

            @Override
            public void blur(BlurEvent event) {
               handleEnter(); 
            }
            
            
        });
        hl.addComponent(this);
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
        
        suggestionList = new ListSelect();
        
        suggestionList.setSizeUndefined();
        suggestionList.setWidth("175px");
        suggestionList.setNullSelectionAllowed(false);
        suggestionList.setImmediate(true);
        suggestionList.setVisible(false);
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
    
    private void handleClearButtonClick() {
        this.setValue(null);
        this.koulutusModel.setYhtHenkiloOid(null);
        fireEvent(new HenkiloAutocompleteEvent(this, null, HenkiloAutocompleteEvent.CLEAR));
    }
 

    /**
     * Populates the henkilo suggestions under the yhtHenkKokoNimi field in according
     * to current search results from UserService.
     * @param henkilos
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
            this.koulutusModel.setYhtHenkiloOid(null);
            fireEvent(new HenkiloAutocompleteEvent(this, null, HenkiloAutocompleteEvent.NOT_SELECTED));
        }
    }
    
    private void handleValueChange() {
        fireEvent(new HenkiloAutocompleteEvent(this, (HenkiloType)(suggestionList.getValue()), HenkiloAutocompleteEvent.SELECTED));
    }
    
    private void arrowDownHandler() {
        if (!suggestionList.isVisible() || henkilos == null || henkilos.isEmpty()) {
            return;
        }
        if (selectedIndex < this.henkilos.size() - 1) {
            ++selectedIndex;
        }
        HenkiloType selectedHenkilo = henkilos.get(selectedIndex);
        suggestionList.select(selectedHenkilo);
    }
    
    private void arrowUpHandler() {
        if (!suggestionList.isVisible() || henkilos == null || henkilos.isEmpty()) {
            return;
        }
        --selectedIndex;
        if (selectedIndex >= 0) {
            HenkiloType selectedHenkilo = henkilos.get(selectedIndex);
            suggestionList.select(selectedHenkilo);
        } else {
            setValue(typedText);
            suggestionList.unselect(henkilos.get(selectedIndex+1));
            fireEvent(new HenkiloAutocompleteEvent(this, null, HenkiloAutocompleteEvent.NOT_SELECTED));
        }
    }
    
    private void handleEnter() {
        if (!suggestionList.isVisible() || henkilos == null || henkilos.isEmpty()) {
            return;
        }
        suggestionList.removeAllItems();
        suggestionList.setVisible(false);
        getWindow().removeActionHandler(this);
    }

    @Override
    public Action[] getActions(Object target, Object sender) {
        return new Action[]{arrowDownAction, arrowUpAction, enterAction};
    }

    @Override
    public void handleAction(Action action, Object sender, Object target) {
        if (action == arrowDownAction) {
            arrowDownHandler();
        }
        if (action == arrowUpAction) {
            arrowUpHandler();
        }
        if (action == enterAction) {
            handleEnter();
        }
    }
    
    
    private String T(String key) {
        return _i18n.getMessage(key);
    }
    
    public class HenkiloAutocompleteEvent extends Component.Event {


        
        private static final long serialVersionUID = -7164075226636500573L;
        public static final int SELECTED = 0;
        public static final int NOT_SELECTED = 1;
        public static final int CLEAR = 2;
        
        private int eventType;
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
