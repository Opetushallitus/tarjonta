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


import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TextField;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.ValintakoeViewModel;
import fi.vm.sade.tarjonta.ui.view.common.RemovalConfirmationDialog;
import fi.vm.sade.tarjonta.ui.view.common.TarjontaDialogWindow;
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
    
    private CheckBox lpCb;


    private CheckBox pkCb;
    
    TarjontaDialogWindow removalDialog;
    


    public PisterajaTable(ValintakoeViewModel valintakoe) {
        super(5,4);
        setSpacing(true);
        setMargin(true);
        setWidth(750, UNITS_PIXELS);
        setColumnExpandRatio(0, 1.0f);
        setColumnExpandRatio(1, 1.0f);
        setColumnExpandRatio(2, 0.5f);
        setColumnExpandRatio(3, 1.0f);
        setColumnExpandRatio(4, 1.0f);
        this.valintakoe = valintakoe;
        buildLayout();
    }
    
    private void buildLayout() {
        //Header labels
        addComponent(UiUtil.label(null, T("alinPistemaaraHdr").toUpperCase(I18N.getLocale())), 1,0);
        addComponent(UiUtil.label(null, T("ylinPistemaaraHdr").toUpperCase(I18N.getLocale())), 3,0);
        addComponent(UiUtil.label(null, T("alinHyvaksyttyPistemaaraHdr").toUpperCase(I18N.getLocale())), 4,0);
        
        
        //Paasykoe row
        pkCb = UiUtil.checkbox(null, T("paasykoe"));
        pkCb.setImmediate(true);
        pkCb.addListener(new ClickListener() {

            private static final long serialVersionUID = 1150738260041209103L;

            @Override
            public void buttonClick(ClickEvent event) {
                if (!pkCb.booleanValue()) {
                    createPkRemovalConfirmationDialog();
                } else {
                    adjustTextFields(PisterajaEvent.PAASYKOE, pkCb, false);
                    fireEvent(pkCb, PisterajaEvent.PAASYKOE);
                }
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
        lpCb = UiUtil.checkbox(null, T("lisapisteet"));
        lpCb.setImmediate(true);
        lpCb.addListener(new ClickListener() {

            private static final long serialVersionUID = -3005335000646281070L;

            @Override
            public void buttonClick(ClickEvent event) {
                if (!lpCb.booleanValue()) {
                    createLpRemovalConfirmationDialog();
                    //removalDialog = new TarjontaDialogWindow();
                } else {
                    adjustTextFields(PisterajaEvent.LISAPISTEET, lpCb, false);
                    fireEvent(lpCb, PisterajaEvent.LISAPISTEET);
                }
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
        kpAlinHyvaksyttyPM.setEnabled(false);
        kpAlinHyvaksyttyPM.setPropertyDataSource(new NestedMethodProperty(valintakoe, "kpAlinHyvaksyttyPM"));
        addComponent(kpAlinHyvaksyttyPM, 4, 3);
        
        if (isPaasykokeet()) {
            pkCb.setValue(Boolean.TRUE);
            adjustTextFields(PisterajaEvent.PAASYKOE, pkCb, false);
        } else {
            pkCb.setValue(Boolean.FALSE);
            adjustTextFields(PisterajaEvent.PAASYKOE, pkCb, false);
        }
        if (isLisapisteet()) {
            lpCb.setValue(Boolean.TRUE);
            adjustTextFields(PisterajaEvent.LISAPISTEET, lpCb, false);
        }
    }
    
    protected void createLpRemovalConfirmationDialog() {
        String lpLableStr = T("lisapisteetSmall");
        String questionStr = T("removalQuestion", lpLableStr);
        lpCb.setValue(Boolean.TRUE);
        Button.ClickListener removeListener = new Button.ClickListener() {

            private static final long serialVersionUID = -812378107447747350L;

            @Override
            public void buttonClick(ClickEvent event) {
                closeDialogWindow();
                lpCb.setValue(Boolean.FALSE);
                adjustTextFields(PisterajaEvent.LISAPISTEET, lpCb, true);
                fireEvent(lpCb, PisterajaEvent.LISAPISTEET);
                
            }
        };
        
        Button.ClickListener cancelListener = new Button.ClickListener() {

            private static final long serialVersionUID = 8262437722011639660L;

            @Override
            public void buttonClick(ClickEvent event) {
                closeDialogWindow();
                
            }
        };
        RemovalConfirmationDialog rcd = new RemovalConfirmationDialog(questionStr, null, T("remove"), T("cancel"), 
                                                                        removeListener, cancelListener);
        
        String windowCaption = T("removalCaption", lpLableStr);
        this.removalDialog = new TarjontaDialogWindow(rcd, windowCaption);
        this.getWindow().addWindow(removalDialog);   
    }
    
    protected void createPkRemovalConfirmationDialog() {
        String pkLableStr = T("paasykoe");
        String pkLableStrGen = T("paasykokeen");
        String questionStr = T("removalQuestion", pkLableStrGen);
        pkCb.setValue(Boolean.TRUE);
        Button.ClickListener removeListener = new Button.ClickListener() {

            private static final long serialVersionUID = 6202574198281231872L;

            @Override
            public void buttonClick(ClickEvent event) {
                closeDialogWindow();
                pkCb.setValue(Boolean.FALSE);
                adjustTextFields(PisterajaEvent.PAASYKOE, pkCb, true);
                fireEvent(pkCb, PisterajaEvent.PAASYKOE);
                
            }
        };
        
        Button.ClickListener cancelListener = new Button.ClickListener() {

            private static final long serialVersionUID = -1738347798661780556L;

            @Override
            public void buttonClick(ClickEvent event) {
                closeDialogWindow();
                
            }
        };
        RemovalConfirmationDialog rcd = new RemovalConfirmationDialog(questionStr, null, T("remove"), T("cancel"), 
                                                                        removeListener, cancelListener);
        
        String windowCaption = T("removalCaption", pkLableStr);
        this.removalDialog = new TarjontaDialogWindow(rcd, windowCaption);
        this.getWindow().addWindow(removalDialog);   
    }
    
    
    
    public void closeDialogWindow() {
        if (removalDialog != null) {
            this.getWindow().removeWindow(removalDialog);
            removalDialog = null;
        }
    }

    public void bindData(ValintakoeViewModel valintakoe) {
        valintakoe.setPkAlinPM((String)(pkAlinPM.getValue()));
        valintakoe.setPkYlinPM((String)(pkYlinPM.getValue()));
        valintakoe.setPkAlinHyvaksyttyPM((String)(pkAlinHyvaksyttyPM.getValue()));
        valintakoe.setLpAlinPM((String)(lpAlinPM.getValue()));
        valintakoe.setLpYlinPM((String)(lpYlinPM.getValue()));
        valintakoe.setLpAlinHyvaksyttyPM((String)(lpAlinHyvaksyttyPM.getValue()));
        valintakoe.setKpAlinHyvaksyttyPM((String)(kpAlinHyvaksyttyPM.getValue()));
    }
    
    public boolean isLisapisteet() {
       return (valintakoe.getLpAlinHyvaksyttyPM() != null && !valintakoe.getLpAlinHyvaksyttyPM().isEmpty())
               || (valintakoe.getLpAlinPM() != null && !valintakoe.getLpAlinPM().isEmpty())
               || (valintakoe.getLpYlinPM() != null && !valintakoe.getLpYlinPM().isEmpty());
    }
    
    public boolean isLisapisteetPisterajat() {
        return (valintakoe.getLpAlinHyvaksyttyPM() != null && !valintakoe.getLpAlinHyvaksyttyPM().isEmpty())
                || (valintakoe.getLpAlinPM() != null && !valintakoe.getLpAlinPM().isEmpty())
                || (valintakoe.getLpYlinPM() != null && !valintakoe.getLpYlinPM().isEmpty());
    }
    
    public boolean isLisapisteetSpecified() {
        if (valintakoe.getLisanayttoKuvaukset() == null || valintakoe.getLisanayttoKuvaukset().isEmpty()) {
            return false;
        }
        for (KielikaannosViewModel kaannos : valintakoe.getLisanayttoKuvaukset()) {
            if (kaannos.getNimi() != null && !kaannos.getNimi().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public boolean isPaasykokeet() {
        return (valintakoe.getPkAlinHyvaksyttyPM() != null && !valintakoe.getPkAlinHyvaksyttyPM().isEmpty())
                || (valintakoe.getPkAlinPM() != null && !valintakoe.getPkAlinPM().isEmpty())
                || (valintakoe.getPkYlinPM() != null && !valintakoe.getPkYlinPM().isEmpty());
                //|| (valintakoe.getValintakoeAjat() != null && !valintakoe.getValintakoeAjat().isEmpty());
    }

    public boolean isValintakoePisterajat() {
        return (valintakoe.getPkAlinHyvaksyttyPM() != null && !valintakoe.getPkAlinHyvaksyttyPM().isEmpty())
                || (valintakoe.getPkAlinPM() != null && !valintakoe.getPkAlinPM().isEmpty())
                || (valintakoe.getPkYlinPM() != null && !valintakoe.getPkYlinPM().isEmpty());
    }
    
    public boolean isValintakoeSpecified() {
        return (valintakoe.getValintakoeAjat() != null && !valintakoe.getValintakoeAjat().isEmpty());
    }

    protected void adjustTextFields(String tyyppi,CheckBox cb, boolean isRemoval) {
        if (tyyppi.equals(PisterajaEvent.LISAPISTEET)) {
            lpAlinPM.setEnabled(cb.booleanValue());
            lpYlinPM.setEnabled(cb.booleanValue());
            lpAlinHyvaksyttyPM.setEnabled(cb.booleanValue());
        } else if (tyyppi.equals(PisterajaEvent.PAASYKOE)) {
            pkAlinPM.setEnabled(cb.booleanValue());
            pkYlinPM.setEnabled(cb.booleanValue());
            pkAlinHyvaksyttyPM.setEnabled(cb.booleanValue());
        }
        kpAlinHyvaksyttyPM.setEnabled(pkCb.booleanValue() || lpCb.booleanValue());
        
        if (isRemoval && tyyppi.equals(PisterajaEvent.LISAPISTEET)) {
            clearLisapisteet();
        } else if (isRemoval && tyyppi.equals(PisterajaEvent.PAASYKOE)) {
            clearPaasykokeet();
        }
    }

    private void clearPaasykokeet() {
        pkAlinPM.setValue(null);
        pkYlinPM.setValue(null);
        pkAlinHyvaksyttyPM.setValue(null);
        kpAlinHyvaksyttyPM.setValue(null);
    }

    private void clearLisapisteet() {
        lpAlinPM.setValue(null);
        lpYlinPM.setValue(null);
        lpAlinHyvaksyttyPM.setValue(null);
        kpAlinHyvaksyttyPM.setValue(null);
    }

    public boolean validateInputTypes() {
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
    
    public boolean validateInputRestrictions() {
        try {
            int kpAlinH = Integer.parseInt(valintakoe.getKpAlinHyvaksyttyPM() != null ? valintakoe.getKpAlinHyvaksyttyPM() : "0");
            int pkAlinH = Integer.parseInt(valintakoe.getPkAlinHyvaksyttyPM() != null ? valintakoe.getPkAlinHyvaksyttyPM() : "0");
            int pkAlin = Integer.parseInt(valintakoe.getPkAlinPM() != null ? valintakoe.getPkAlinPM() : "0");
            int pkYlin = Integer.parseInt(valintakoe.getPkYlinPM() != null ? valintakoe.getPkYlinPM() : "0");
            int lpAlin = Integer.parseInt(valintakoe.getLpAlinPM() != null ? valintakoe.getLpAlinPM() : "0");
            int lpYlin = Integer.parseInt(valintakoe.getLpYlinPM() != null ? valintakoe.getLpYlinPM() : "0");
            int lpAlinH = Integer.parseInt(valintakoe.getLpAlinHyvaksyttyPM() != null ? valintakoe.getLpAlinHyvaksyttyPM() : "0");
            
            if (isOutOfRange(kpAlinH, pkAlinH, pkAlin, pkYlin, lpAlin, lpYlin, lpAlinH)) {
                return false;
            }
            
            if (sumsExceedMaximum(pkYlin, lpYlin)) {
                return false;
            }
            
            if (rowRestrictionsViolated(pkAlin, pkYlin, pkAlinH)) {
                return false;
            }
            
            if (rowRestrictionsViolated(lpAlin, lpYlin, lpAlinH)) {
                return false;
            }
            
            
        } catch (Exception ex) {
            return false;
        }
        return true;
    }
    
    private boolean rowRestrictionsViolated(int pkAlin, int pkYlin, int pkAlinH) {
        return (pkAlin > pkYlin) || (pkAlinH < pkAlin) || (pkAlinH > pkYlin);
    }

    private boolean sumsExceedMaximum(int pkYlin, int lpYlin) {
        
        return pkYlin + lpYlin > 10;
    }

    private boolean isOutOfRange(int kpAlinH, int pkAlinH, int pkAlin,
            int pkYlin, int lpAlin, int lpYlin, int lpAlinH) {
        return kpAlinH < 0 || kpAlinH > 10 || pkAlinH < 0 || pkAlinH > 10 || pkAlin < 0 || pkAlin > 10 || pkYlin < 0 || pkYlin > 10 
                || lpAlinH < 0 || lpAlinH > 10 || lpAlin < 0 || lpAlin > 10 || lpYlin < 0 || lpYlin > 10;      
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
    
    public CheckBox getLpCb() {
        return lpCb;
    }
    
    public CheckBox getPkCb() {
        return pkCb;
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

    public void bindValintakoeData(ValintakoeViewModel selectedValintaKoe) {
        selectedValintaKoe.setPkAlinPM(pkAlinPM.getValue() != null ? (String)(pkAlinPM.getValue()) : null);
        selectedValintaKoe.setPkYlinPM(pkYlinPM.getValue() != null ? (String)(pkYlinPM.getValue()) : null);
        selectedValintaKoe.setPkAlinHyvaksyttyPM(pkAlinHyvaksyttyPM.getValue() != null ? (String)(pkAlinHyvaksyttyPM.getValue()) : null);
        selectedValintaKoe.setKpAlinHyvaksyttyPM(kpAlinHyvaksyttyPM.getValue() != null ? (String)(kpAlinHyvaksyttyPM.getValue()) : null);
    }

    public void bindLisapisteData(ValintakoeViewModel selectedValintaKoe) {
        selectedValintaKoe.setLpAlinPM(lpAlinPM.getValue() != null ? (String)(lpAlinPM.getValue()) : null);
        selectedValintaKoe.setLpYlinPM(lpYlinPM.getValue() != null ? (String)(lpYlinPM.getValue()) : null);
        selectedValintaKoe.setLpAlinHyvaksyttyPM(lpAlinHyvaksyttyPM.getValue() != null ? (String)(lpAlinHyvaksyttyPM.getValue()) : null);
        selectedValintaKoe.setKpAlinHyvaksyttyPM(kpAlinHyvaksyttyPM.getValue() != null ? (String)(kpAlinHyvaksyttyPM.getValue()) : null);
    }
    
    
    public ValintakoeViewModel getValintakoe() {
        return valintakoe;
    }


}
