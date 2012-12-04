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
package fi.vm.sade.tarjonta.ui.view.common;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;

import fi.vm.sade.vaadin.util.UiUtil;

/**
 * Component for getting user confirmation for removal of some tarjonta object.
 * 
 * @author Markus
 *
 */
public class RemovalConfirmationDialog extends AbstractVerticalLayout {

	protected String questionStr;
	protected String kohdenimi;
	protected ClickListener removeListener;
	protected ClickListener noRemoveListener;
	protected String removeStr;
	protected String noRemoveStr;
	
	public RemovalConfirmationDialog(String questionStr, String kohdenimi, String removeStr, String noRemoveStr, ClickListener removeListener, ClickListener noRemoveListener) {
		this.questionStr = questionStr;
		this.kohdenimi = kohdenimi;
		this.removeStr = removeStr;
		this.noRemoveStr = noRemoveStr;
		this.removeListener = removeListener;
		this.noRemoveListener = noRemoveListener;
	}
	
	
	@Override
	protected void buildLayout() {
	    setSizeUndefined();
	    setSpacing(true);
	    this.setMargin(true);
		UiUtil.label(this, questionStr);
		UiUtil.label(this, kohdenimi);
		HorizontalLayout hl = UiUtil.horizontalLayout();
		hl.setSizeFull();
		Button noRemoveB = UiUtil.buttonSmallPrimary(hl, noRemoveStr, noRemoveListener);
		Button removeB = UiUtil.buttonSmallPrimary(hl, removeStr, removeListener);
		addComponent(hl);
		hl.setComponentAlignment(noRemoveB, Alignment.MIDDLE_LEFT);
		hl.setComponentAlignment(removeB, Alignment.MIDDLE_RIGHT);
	}

}
