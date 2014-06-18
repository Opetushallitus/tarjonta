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

import java.util.Collection;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Button.ClickEvent;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.service.search.KoodistoKoodi;
import fi.vm.sade.tarjonta.service.search.KoulutusPerustieto;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.RemovalConfirmationDialog;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 * 
 * @author Markus
 *
 */
public class MultipleKoulutusRemovalDialog extends RemovalConfirmationDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8006973486299776672L;

	protected OptionGroup koulutusOptions;
	private TarjontaPresenter presenter;


	public MultipleKoulutusRemovalDialog(String questionStr, String removeStr, String noRemoveStr, TarjontaPresenter pres) {
		super(questionStr, null, removeStr, noRemoveStr, null,
				null);
		
		this.presenter = pres;
		this.removeListener = new Button.ClickListener() {
			private static final long serialVersionUID = 5019806363620874205L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				removeSelectedKoulutukset();
			}
		};
		this.noRemoveListener = new Button.ClickListener() {
			
			private static final long serialVersionUID = 5019806363620874205L;

			@Override
			public void buttonClick(ClickEvent event) {
				presenter.closeKoulutusRemovalDialog();

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
        BeanItemContainer<KoulutusPerustieto> haut = new BeanItemContainer<KoulutusPerustieto>(KoulutusPerustieto.class, presenter.getSelectedKoulutukset());

        koulutusOptions = new OptionGroup(null,haut);
        koulutusOptions.setMultiSelect(true);
        //Set all selected as default
        for (Object obj: koulutusOptions.getItemIds()) {
        	KoulutusPerustieto curKoul = (KoulutusPerustieto)obj;
        	String nimi = curKoul.getKoulutusasteTyyppi().equals(KoulutusasteTyyppi.LUKIOKOULUTUS) 
        			? getKoodiNimi(curKoul.getKoulutuskoodi()) 
        					: getKoodiNimi(curKoul.getKoulutusohjelma());
        	koulutusOptions.setItemCaption(obj, nimi);
            koulutusOptions.select(obj);
        }
        Label lbl = new Label(I18N.getMessage("RemovalConfirmationDialog.valitutKoulutuksetOptionGroup"));
        hl.addComponent(lbl);
        hl.addComponent(koulutusOptions);
        addComponent(hl);
    }
    
    private void removeSelectedKoulutukset() {
    	Object values = koulutusOptions.getValue();
    	Collection<KoulutusPerustieto> selectedKoulutusOptions = null;
    	if (values instanceof  Collection) {
    		selectedKoulutusOptions = (Collection<KoulutusPerustieto>)values;
    		presenter.getSelectedKoulutukset().clear();
    		presenter.getSelectedKoulutukset().addAll(selectedKoulutusOptions);
    		presenter.removeSelectedKoulutukset();
    	}
    }
    
    /**
     * Returns the name of the koodi based on koodisto uri given.
     *
     * @param hakukohdeUri the koodisto uri given.
     * @return
     */
    private String getKoodiNimi(KoodistoKoodi koodistoKoodi) {
        return TarjontaUIHelper.getClosestMonikielinenNimi(I18N.getLocale(), koodistoKoodi.getNimi() );
    }
}
