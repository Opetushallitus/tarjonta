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
            Double.parseDouble((valintakoe.getKpAlinHyvaksyttyPM() != null && !valintakoe.getKpAlinHyvaksyttyPM().isEmpty()) ? valintakoe.getKpAlinHyvaksyttyPM().replace(',', '.') : "0");//"0"
            Double.parseDouble((valintakoe.getPkAlinHyvaksyttyPM() != null  && !valintakoe.getPkAlinHyvaksyttyPM().isEmpty()) ? valintakoe.getPkAlinHyvaksyttyPM().replace(',', '.') : "0");
            Double.parseDouble(valintakoe.getPkAlinPM() != null ? valintakoe.getPkAlinPM().replace(',', '.') : "0");
            Double.parseDouble(valintakoe.getPkYlinPM() != null ? valintakoe.getPkYlinPM().replace(',', '.') : "0");
            Double.parseDouble(valintakoe.getLpAlinPM() != null ? valintakoe.getLpAlinPM().replace(',', '.') : "0");
            Double.parseDouble(valintakoe.getLpYlinPM() != null ? valintakoe.getLpYlinPM().replace(',', '.') : "0");
            Double.parseDouble(valintakoe.getLpAlinHyvaksyttyPM() != null &&  !valintakoe.getLpAlinHyvaksyttyPM().isEmpty() ? valintakoe.getLpAlinHyvaksyttyPM().replace(',', '.') : "0");
            
            if (!correctPrecision()) {
                return false;
            }
            
        } catch (Exception ex) {
            return false;
        }
        return true;
    }
    
    private boolean correctPrecision() {
        return correctPrecision((valintakoe.getKpAlinHyvaksyttyPM() != null && !valintakoe.getKpAlinHyvaksyttyPM().isEmpty()) ? valintakoe.getKpAlinHyvaksyttyPM().replace(',', '.') : "0")
                && correctPrecision((valintakoe.getPkAlinHyvaksyttyPM() != null  && !valintakoe.getPkAlinHyvaksyttyPM().isEmpty()) ? valintakoe.getPkAlinHyvaksyttyPM().replace(',', '.') : "0")
                && correctPrecision(valintakoe.getPkAlinPM() != null ? valintakoe.getPkAlinPM().replace(',', '.') : "0")
                && correctPrecision(valintakoe.getPkYlinPM() != null ? valintakoe.getPkYlinPM().replace(',', '.') : "0")
                && correctPrecision(valintakoe.getLpAlinPM() != null ? valintakoe.getLpAlinPM().replace(',', '.') : "0")
                && correctPrecision(valintakoe.getLpYlinPM() != null ? valintakoe.getLpYlinPM().replace(',', '.') : "0")
                && correctPrecision(valintakoe.getLpAlinHyvaksyttyPM() != null &&  !valintakoe.getLpAlinHyvaksyttyPM().isEmpty() ? valintakoe.getLpAlinHyvaksyttyPM().replace(',', '.') : "0");
    }
    
    private boolean correctPrecision(String value) {
        int index = value.indexOf('.');
        if (index > 0 && index < (value.length() - 1)) {
            return value.substring(index + 1).length() < 3;
        } else {
            return true;
        }
    }
    
    public boolean validateInputRestrictions() {
        try {
            
            double pkAlin = Double.parseDouble(valintakoe.getPkAlinPM() != null ? valintakoe.getPkAlinPM().replace(',', '.') : "0.0");
            double pkYlin = Double.parseDouble(valintakoe.getPkYlinPM() != null ? valintakoe.getPkYlinPM().replace(',', '.') : "0.0");
            double lpAlin = Double.parseDouble(valintakoe.getLpAlinPM() != null ? valintakoe.getLpAlinPM().replace(',', '.') : "0.0");
            double lpYlin = Double.parseDouble(valintakoe.getLpYlinPM() != null ? valintakoe.getLpYlinPM().replace(',', '.') : "0.0");
            
            
            if (isOutOfRange(pkAlin, pkYlin, lpAlin, lpYlin)) {
                return false;
            }
            
            if (sumsExceedMaximum(pkYlin, lpYlin)) {
                return false;
            }
            
            if (valintakoe.getPkAlinHyvaksyttyPM() != null && !valintakoe.getPkAlinHyvaksyttyPM().isEmpty()) {
                double pkAlinH = Double.parseDouble(valintakoe.getPkAlinHyvaksyttyPM().replace(',', '.'));
                if (rowRestrictionsViolated(pkAlin, pkYlin, pkAlinH)) {
                    return false;
                }
            } else if (rowRestrictionsViolated(pkAlin, pkYlin)) {
                return false;
            }
            
            if (valintakoe.getLpAlinHyvaksyttyPM() != null && !valintakoe.getLpAlinHyvaksyttyPM().isEmpty()) { 
                double lpAlinH = Double.parseDouble(valintakoe.getLpAlinHyvaksyttyPM().replace(',', '.'));
                if (rowRestrictionsViolated(lpAlin, lpYlin, lpAlinH)) {
                    return false;
                }
            } else if (rowRestrictionsViolated(lpAlin, lpYlin)) {
                return false;
            }
            
            
            
        } catch (Exception ex) {
            return false;
        }
        return true;
    }
    
    public boolean validateKpAlinH() {
        try {
            double pkAlin = Double.parseDouble(valintakoe.getPkAlinPM() != null ? valintakoe.getPkAlinPM().replace(',', '.') : "0.0");
            double pkYlin = Double.parseDouble(valintakoe.getPkYlinPM() != null ? valintakoe.getPkYlinPM().replace(',', '.') : "0.0");
            double lpAlin = Double.parseDouble(valintakoe.getLpAlinPM() != null ? valintakoe.getLpAlinPM().replace(',', '.') : "0.0");
            double lpYlin = Double.parseDouble(valintakoe.getLpYlinPM() != null ? valintakoe.getLpYlinPM().replace(',', '.') : "0.0");
            
            
            if (valintakoe.getPkAlinHyvaksyttyPM() != null && !valintakoe.getPkAlinHyvaksyttyPM().isEmpty()) {
                pkAlin = Double.parseDouble(valintakoe.getPkAlinHyvaksyttyPM().replace(',', '.'));
            }
            if (valintakoe.getLpAlinHyvaksyttyPM() != null && !valintakoe.getLpAlinHyvaksyttyPM().isEmpty()) {
                lpAlin = Double.parseDouble(valintakoe.getLpAlinHyvaksyttyPM().replace(',', '.'));
            }
            
            

            if (valintakoe.getKpAlinHyvaksyttyPM() != null && !valintakoe.getKpAlinHyvaksyttyPM().isEmpty()) {
                double kpAlinH = Double.parseDouble(valintakoe.getKpAlinHyvaksyttyPM().replace(',', '.'));
                if (kpAlinHViolates(kpAlinH, pkAlin, pkYlin, lpAlin, lpYlin)) {
                    return false;
                }
            }
        } catch (Exception ex) {
            return false;
        }
        return true;
    }
    
    private boolean kpAlinHViolates(double kpAlinH, double pkAlin, double pkYlin,
            double lpAlin, double lpYlin) {
        return (kpAlinH < (pkAlin + lpAlin)) || kpAlinH > (pkYlin + lpYlin);
    }

    private boolean rowRestrictionsViolated(double pkAlin, double pkYlin, double pkAlinH) {
        return (pkAlin > pkYlin) || (pkAlinH < pkAlin) || (pkAlinH > pkYlin);
    }
    
    private boolean rowRestrictionsViolated(double pkAlin, double pkYlin) {
        return (pkAlin > pkYlin);
    }

    private boolean sumsExceedMaximum(double pkYlin, double lpYlin) {
        
        return pkYlin + lpYlin > 10;
    }

    private boolean isOutOfRange(double pkAlin,
            double pkYlin, double lpAlin, double lpYlin) {
        return pkAlin < 0 || pkAlin > 10 || pkYlin < 0 || pkYlin > 10 
                || lpAlin < 0 || lpAlin > 10 || lpYlin < 0 || lpYlin > 10;      
    }
    
    public String getPkAlinVal() {
        if (this.pkAlinPM != null) {
            return (String)(this.pkAlinPM.getValue());
        }
        return null;
    }
    
    public String getPkYlinVal() {
        if (this.pkYlinPM != null) {
            return (String)(this.pkYlinPM.getValue());
        }
        return null;
    }
    
    public String getPkAlinHyvVal() {
        if (this.pkAlinHyvaksyttyPM != null) {
            return (String)(this.pkAlinHyvaksyttyPM.getValue());
        }
        return null;
    }
    
    public String getLpAlinVal() {
        if (this.lpAlinPM != null) {
            return (String)(this.lpAlinPM.getValue());
        }
        return null;
    }
    
    public String getLpYlinVal() {
        if (this.lpYlinPM != null) {
            return (String)(this.lpYlinPM.getValue());
        }
        return null;
    }
    
    public String getLpAlinHyvVal() {
        if (this.lpAlinHyvaksyttyPM != null) {
            return (String)(this.lpAlinHyvaksyttyPM.getValue());
        }
        return null;
    }
    
    public String getKpAlinHyvVal() {
        if (this.kpAlinHyvaksyttyPM != null) {
            return (String)(this.kpAlinHyvaksyttyPM.getValue());
        }
        return null;
    }
    
    public void setValues(String pkAlin, String pkYlin, String pkAlinH, String lpAlin, String lpYlin, String lpAlinH, String kpAlinH) {
        valintakoe.setPkAlinPM(pkAlin);
        valintakoe.setPkYlinPM(pkYlin);
        valintakoe.setPkAlinHyvaksyttyPM(pkAlinH);
        valintakoe.setLpAlinPM(lpAlin);
        valintakoe.setLpYlinPM(lpYlin);
        valintakoe.setLpAlinHyvaksyttyPM(lpAlinH);
        valintakoe.setKpAlinHyvaksyttyPM(kpAlinH);
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
        if (selectedValintaKoe == null) {
            valintakoe.setPkAlinPM(pkAlinPM.getValue() != null ? (String)(pkAlinPM.getValue()) : null);
            valintakoe.setPkYlinPM(pkYlinPM.getValue() != null ? (String)(pkYlinPM.getValue()) : null);
            valintakoe.setPkAlinHyvaksyttyPM(pkAlinHyvaksyttyPM.getValue() != null ? (String)(pkAlinHyvaksyttyPM.getValue()) : null);
            valintakoe.setKpAlinHyvaksyttyPM(kpAlinHyvaksyttyPM.getValue() != null ? (String)(kpAlinHyvaksyttyPM.getValue()) : null);
        } else {
            selectedValintaKoe.setPkAlinPM(pkAlinPM.getValue() != null ? (String)(pkAlinPM.getValue()) : null);
            selectedValintaKoe.setPkYlinPM(pkYlinPM.getValue() != null ? (String)(pkYlinPM.getValue()) : null);
            selectedValintaKoe.setPkAlinHyvaksyttyPM(pkAlinHyvaksyttyPM.getValue() != null ? (String)(pkAlinHyvaksyttyPM.getValue()) : null);
            selectedValintaKoe.setKpAlinHyvaksyttyPM(kpAlinHyvaksyttyPM.getValue() != null ? (String)(kpAlinHyvaksyttyPM.getValue()) : null);
        }
    }

    public void bindLisapisteData(ValintakoeViewModel selectedValintaKoe) {
        if (selectedValintaKoe == null) {
            valintakoe.setLpAlinPM(lpAlinPM.getValue() != null ? (String)(lpAlinPM.getValue()) : null);
            valintakoe.setLpYlinPM(lpYlinPM.getValue() != null ? (String)(lpYlinPM.getValue()) : null);
            valintakoe.setLpAlinHyvaksyttyPM(lpAlinHyvaksyttyPM.getValue() != null ? (String)(lpAlinHyvaksyttyPM.getValue()) : null);
            valintakoe.setKpAlinHyvaksyttyPM(kpAlinHyvaksyttyPM.getValue() != null ? (String)(kpAlinHyvaksyttyPM.getValue()) : null);
        } else {
            selectedValintaKoe.setLpAlinPM(lpAlinPM.getValue() != null ? (String)(lpAlinPM.getValue()) : null);
            selectedValintaKoe.setLpYlinPM(lpYlinPM.getValue() != null ? (String)(lpYlinPM.getValue()) : null);
            selectedValintaKoe.setLpAlinHyvaksyttyPM(lpAlinHyvaksyttyPM.getValue() != null ? (String)(lpAlinHyvaksyttyPM.getValue()) : null);
            selectedValintaKoe.setKpAlinHyvaksyttyPM(kpAlinHyvaksyttyPM.getValue() != null ? (String)(kpAlinHyvaksyttyPM.getValue()) : null);
        }
    }
    
    
    public ValintakoeViewModel getValintakoe() {
        return valintakoe;
    }


}
