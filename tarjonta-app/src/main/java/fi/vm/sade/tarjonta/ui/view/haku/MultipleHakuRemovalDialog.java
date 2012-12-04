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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Button.ClickEvent;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import fi.vm.sade.tarjonta.ui.view.HakuPresenter;
import fi.vm.sade.tarjonta.ui.view.common.RemovalConfirmationDialog;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 * Confirmation dialog for removing multiple haku objects.
 * @author Markus
 *
 */
@Configurable
public class MultipleHakuRemovalDialog extends RemovalConfirmationDialog {
    
    private static final long serialVersionUID = 146724841122879266L;
    
    protected OptionGroup hakuOptions;
    protected List<HakuViewModel> selectedHaut = new ArrayList<HakuViewModel>();
    @Autowired
    private HakuPresenter presenter;
    

    public MultipleHakuRemovalDialog(String questionStr,
            List<HakuViewModel> selectedHaut, String removeStr,
            String noRemoveStr) {
        super(questionStr, null, removeStr, noRemoveStr, null,
                null);
        this.removeListener = new Button.ClickListener() {
            
            @Override
            public void buttonClick(ClickEvent event) {
                presenter.closeHakuRemovalDialog();
                removeSelectedHaut();
                
            }
        };
        this.noRemoveListener = new Button.ClickListener() {
            
            @Override
            public void buttonClick(ClickEvent event) {
                presenter.closeHakuRemovalDialog();
                
            }
        };
        this.selectedHaut = selectedHaut;
        
    }
    
    @Override
    protected void buildLayout() {
        setSizeUndefined();
        setSpacing(true);
        this.setMargin(true);
        UiUtil.label(this, questionStr);
        createOptionGroupLayout();
        HorizontalLayout hl = UiUtil.horizontalLayout();
        hl.setSizeFull();
        Button noRemoveB = UiUtil.buttonSmallPrimary(hl, noRemoveStr, noRemoveListener);
        Button removeB = UiUtil.buttonSmallPrimary(hl, removeStr, removeListener);
        addComponent(hl);
        hl.setComponentAlignment(noRemoveB, Alignment.MIDDLE_LEFT);
        hl.setComponentAlignment(removeB, Alignment.MIDDLE_RIGHT);
    }
    
    private void createOptionGroupLayout() {
        HorizontalLayout hl = UiUtil.horizontalLayout();
        hl.setMargin(true,false,false,false);
        BeanItemContainer<HakuViewModel> haut = new BeanItemContainer<HakuViewModel>(HakuViewModel.class, this.selectedHaut);

        hakuOptions = new OptionGroup(null,haut);
        hakuOptions.setMultiSelect(true);
        //Set all selected as default
        for (Object obj: hakuOptions.getItemIds()) {
            hakuOptions.select(obj);
        }
        Label lbl = new Label(I18N.getMessage("RemovalConfirmationDialog.valitutKoulutuksetOptionGroup"));
        hl.addComponent(lbl);
        hl.addComponent(hakuOptions);
        addComponent(hl);
    }
    
    private void removeSelectedHaut() {
        Object values = hakuOptions.getValue();
        Collection<HakuViewModel> selectedHakuOptions = null;
            if (values instanceof  Collection) {
             selectedHakuOptions = (Collection<HakuViewModel>)values;
             
             presenter.getSelectedhaut().clear();
             
             presenter.getSelectedhaut().clear();
             presenter.getSelectedhaut().addAll(selectedHakuOptions);
             presenter.removeSelectedHaut();
             
            }
    }



}
