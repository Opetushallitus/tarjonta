/*
 *
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
package fi.vm.sade.tarjonta.ui.view.hakukohde.tabs;

import org.vaadin.addon.formbinder.FormFieldMatch;
import org.vaadin.addon.formbinder.FormView;
import org.vaadin.addon.formbinder.PropertyId;

import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TextField;

import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.ui.model.ValintakoeViewModel;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 * 
 * @author Markus
 *
 */
public class PisterajaTable extends GridLayout {

    private static final long serialVersionUID = -953111306438997409L;
    private transient I18NHelper _i18n;
    private ValintakoeViewModel valintakoe;
    
    private TextField pkAlinPM;
    
    private TextField pkYlinPM;
    
    private TextField pkAlinHyvaksyttyPM;
    
    private TextField lpAlinPM;
    
    private TextField lpYlinPM;
    
    private TextField lpAlinHyvaksyttyPM;
    
    private TextField kpAlinHyvaksyttyPM;
    
    public PisterajaTable(ValintakoeViewModel valintakoe) {
        super(5,4);
        setSpacing(true);
        setMargin(true);
        setWidth(60, UNITS_PERCENTAGE);
        this.valintakoe = valintakoe;
        buildLayout();
    }
    
    private void buildLayout() {
        //Header labels
        addComponent(UiUtil.label(null, T("alinPistemaaraHdr")), 1,0);
        addComponent(UiUtil.label(null, T("ylinPistemaaraHdr")), 3,0);
        addComponent(UiUtil.label(null, T("alinHyvaksyttyPistemaaraHdr")), 4,0);
        
        
        //Paasykoe row
        final CheckBox pkCb = UiUtil.checkbox(null, T("paasykoe"));
        pkCb.setImmediate(true);
        pkCb.addListener(new ClickListener() {

            private static final long serialVersionUID = 1150738260041209103L;

            @Override
            public void buttonClick(ClickEvent event) {
                adjustTextFields(PisterajaEvent.PAASYKOE, pkCb);
                fireEvent(pkCb, PisterajaEvent.PAASYKOE);
            }
            
        });
        addComponent(pkCb, 0, 1);
        pkAlinPM = UiUtil.textField(null);
        pkAlinPM.setImmediate(true);
        pkAlinPM.setEnabled(false);
        pkAlinPM.setPropertyDataSource(new NestedMethodProperty(valintakoe, "pkAlinPM"));
        addComponent(pkAlinPM, 1, 1);
        addComponent(UiUtil.label(null, T("hyphen")), 2, 1);
        pkYlinPM = UiUtil.textField(null);
        pkYlinPM.setImmediate(true);
        pkYlinPM.setEnabled(false);
        pkYlinPM.setPropertyDataSource(new NestedMethodProperty(valintakoe, "pkYlinPM"));
        addComponent(pkYlinPM, 3, 1);
        pkAlinHyvaksyttyPM = UiUtil.textField(null);
        pkAlinHyvaksyttyPM.setImmediate(true);
        pkAlinHyvaksyttyPM.setEnabled(false);
        pkAlinHyvaksyttyPM.setPropertyDataSource(new NestedMethodProperty(valintakoe, "pkAlinHyvaksyttyPM"));
        
        addComponent(pkAlinHyvaksyttyPM, 4, 1);
        
        //Lisapisteet row
        final CheckBox lpCb = UiUtil.checkbox(null, T("lisapisteet"));
        lpCb.setImmediate(true);
        lpCb.addListener(new ClickListener() {

            private static final long serialVersionUID = -3005335000646281070L;

            @Override
            public void buttonClick(ClickEvent event) {
                adjustTextFields(PisterajaEvent.LISAPISTEET, lpCb);
                fireEvent(lpCb, PisterajaEvent.LISAPISTEET);
            }
            
        });
        addComponent(lpCb, 0, 2);
        lpAlinPM = UiUtil.textField(null);
        lpAlinPM.setImmediate(true);
        lpAlinPM.setEnabled(false);
        lpAlinPM.setPropertyDataSource(new NestedMethodProperty(valintakoe, "lpAlinPM"));
        addComponent(lpAlinPM, 1, 2);
        addComponent(UiUtil.label(null, T("hyphen")), 2, 2);
        lpYlinPM = UiUtil.textField(null);
        lpYlinPM.setImmediate(true);
        lpYlinPM.setEnabled(false);
        lpYlinPM.setPropertyDataSource(new NestedMethodProperty(valintakoe, "lpYlinPM"));
        addComponent(lpYlinPM, 3, 2);
        lpAlinHyvaksyttyPM = UiUtil.textField(null);
        lpAlinHyvaksyttyPM.setImmediate(true);
        lpAlinHyvaksyttyPM.setEnabled(false);
        lpAlinHyvaksyttyPM.setPropertyDataSource(new NestedMethodProperty(valintakoe, "lpAlinHyvaksyttyPM"));
        addComponent(lpAlinHyvaksyttyPM, 4, 2);
        
        //Kokonaispisteet row
        addComponent(UiUtil.label(null, T("kokonaispisteet")), 0, 3);
        addComponent(UiUtil.label(null, T("hyphen")), 2, 3);
        addComponent(UiUtil.label(null, T("max10")), 3, 3);
        kpAlinHyvaksyttyPM = UiUtil.textField(null);
        kpAlinHyvaksyttyPM.setImmediate(true);
        kpAlinHyvaksyttyPM.setPropertyDataSource(new NestedMethodProperty(valintakoe, "kpAlinHyvaksyttyPM"));
        addComponent(kpAlinHyvaksyttyPM, 4, 3);
    }
    
    protected void adjustTextFields(String tyyppi,CheckBox cb) {
        if (tyyppi.equals(PisterajaEvent.LISAPISTEET)) {
            lpAlinPM.setEnabled(cb.booleanValue());
            lpYlinPM.setEnabled(cb.booleanValue());
            lpAlinHyvaksyttyPM.setEnabled(cb.booleanValue());
        } else if (tyyppi.equals(PisterajaEvent.PAASYKOE)) {
            pkAlinPM.setEnabled(cb.booleanValue());
            pkYlinPM.setEnabled(cb.booleanValue());
            pkAlinHyvaksyttyPM.setEnabled(cb.booleanValue());
        }
    }

    public boolean validateInputs() {
        try {
            Integer.parseInt(valintakoe.getKpAlinHyvaksyttyPM() != null ? valintakoe.getKpAlinHyvaksyttyPM() : "0");
            Integer.parseInt(valintakoe.getPkAlinHyvaksyttyPM() != null ? valintakoe.getPkAlinHyvaksyttyPM() : "0");
            Integer.parseInt(valintakoe.getPkAlinPM() != null ? valintakoe.getPkAlinPM() : "0");
            Integer.parseInt(valintakoe.getPkYlinPM() != null ? valintakoe.getPkYlinPM() : "0");
            Integer.parseInt(valintakoe.getLpAlinPM() != null ? valintakoe.getLpAlinPM() : "0");
            Integer.parseInt(valintakoe.getLpYlinPM() != null ? valintakoe.getLpYlinPM() : "0");
            Integer.parseInt(valintakoe.getLpAlinHyvaksyttyPM() != null ? valintakoe.getLpAlinHyvaksyttyPM() : "0");
        } catch (Exception ex) {
            return false;
        }
        return true;
    }
    
    private void fireEvent(CheckBox cb, String type) {
        fireEvent(new PisterajaEvent(this, cb.booleanValue(), type));
    }
    
    protected String T(String key) {
        return getI18n().getMessage(key);
    }

    protected String T(String key, Object... args) {
        return getI18n().getMessage(key, args);
    }

    protected I18NHelper getI18n() {
        if (_i18n == null) {
            _i18n = new I18NHelper(this);
        }
        return _i18n;
    }
    
    public class PisterajaEvent extends Component.Event {

        private static final long serialVersionUID = -2453630663489983994L;
        public static final String PAASYKOE = "paasykoe";
        public static final String LISAPISTEET = "lisapisteet";

        private String type;
        private boolean selected;
        
        public PisterajaEvent(Component source, boolean selected, String type) {
            super(source);
            setType(type);
            setSelected(selected);
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

       
        
    }

}
