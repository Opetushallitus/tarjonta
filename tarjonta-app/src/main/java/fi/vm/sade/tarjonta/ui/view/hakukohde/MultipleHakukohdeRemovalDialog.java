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

import java.util.Collection;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Button.ClickEvent;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.service.search.HakukohdePerustieto;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.RemovalConfirmationDialog;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 * 
 * @author Markus
 *
 */
public class MultipleHakukohdeRemovalDialog extends RemovalConfirmationDialog {

	private static final long serialVersionUID = 8006973486299776672L;

	protected OptionGroup hakukohdeOptions;
	private TarjontaPresenter presenter;


	public MultipleHakukohdeRemovalDialog(String questionStr, String removeStr, String noRemoveStr, TarjontaPresenter pres) {
		super(questionStr, null, removeStr, noRemoveStr, null,
				null);
		
		this.presenter = pres;
		this.removeListener = new Button.ClickListener() {
			private static final long serialVersionUID = 5019806363620874205L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				removeSelectedHakukohteet();
			}
		};
		this.noRemoveListener = new Button.ClickListener() {
			
			private static final long serialVersionUID = 5019806363620874205L;

			@Override
			public void buttonClick(ClickEvent event) {
				presenter.closeHakukohdeRemovalDialog();

			}
		};
	}
	
	@Override
    protected void buildLayout() {
		setSizeUndefined();
		this.setWidth("600px");
        setSpacing(true);
        this.setMargin(true);
        UiUtil.label(this, questionStr);
        createOptionGroupLayout();
        HorizontalLayout hl = UiUtil.horizontalLayout();
        hl.setSizeFull();
        Button noRemoveB = UiUtil.buttonSmallSecodary(hl, noRemoveStr, noRemoveListener);
        Button removeB = UiUtil.buttonSmallSecodary(hl, removeStr, removeListener);
        addComponent(hl);
        hl.setComponentAlignment(noRemoveB, Alignment.MIDDLE_LEFT);
        hl.setComponentAlignment(removeB, Alignment.MIDDLE_RIGHT);
	}
	
    private void createOptionGroupLayout() {
        HorizontalLayout hl = UiUtil.horizontalLayout();
        hl.setMargin(true,false,false,false);
        BeanItemContainer<HakukohdePerustieto> hakukohteet = new BeanItemContainer<HakukohdePerustieto>(HakukohdePerustieto.class, presenter.getSelectedhakukohteet());

        hakukohdeOptions = new OptionGroup(null,hakukohteet);
        hakukohdeOptions.setMultiSelect(true);
        //Set all selected as default
        for (Object obj: hakukohdeOptions.getItemIds()) {
        	HakukohdePerustieto curHakuk = (HakukohdePerustieto)obj;
        	String nimi = TarjontaUIHelper.getClosestMonikielinenTekstiTyyppiName(I18N.getLocale(), curHakuk.getNimi()).getValue();
        	hakukohdeOptions.setItemCaption(obj, nimi);
            hakukohdeOptions.select(obj);
        }
        Label lbl = new Label(I18N.getMessage("RemovalConfirmationDialog.valitutHakukohteetOptionGroup"));
        hl.addComponent(lbl);
        hl.addComponent(hakukohdeOptions);
        addComponent(hl);
    }
    
    private void removeSelectedHakukohteet() {
    	Object values = hakukohdeOptions.getValue();
    	Collection<HakukohdePerustieto> selectedHakukohdeOptions = null;
    	if (values instanceof  Collection) {
    		selectedHakukohdeOptions = (Collection<HakukohdePerustieto>)values;
    		presenter.getSelectedhakukohteet().clear();
    		presenter.getSelectedhakukohteet().addAll(selectedHakukohdeOptions);
    		presenter.removeSelectedHakukohteet();
    	}
    }
}
