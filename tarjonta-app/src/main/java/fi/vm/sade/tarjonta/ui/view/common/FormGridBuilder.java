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

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.vaadin.Oph;

/**
 * Form-tyylinen näkymä.
 * 
 * 
 * @author Timo Santasalo / Teknokala Ky
 */
public class FormGridBuilder extends GridLayout {
	
	public static final float RATIO = 0.2f;

	private static final long serialVersionUID = 1L;
	
	public FormGridBuilder(FieldInfo... fields) {
		this(RATIO, fields);
	}

	public FormGridBuilder(float ratio, FieldInfo... fields) {
		super(2, fields.length > 0 ? fields.length : 1);
    	setSizeFull();
    	setColumnExpandRatio(0, ratio);
    	setColumnExpandRatio(1, 1-ratio);
    	addStyleName(Oph.SPACING_BOTTOM_30);
    	setSpacing(true);
    	
    	for (FieldInfo fi : fields) {
    		add(fi);
    	}
    }
	
	/**
	 * Add header. Header is a {@link AbstractComponent} that occupies two columns.
	 * @param header
	 */
	public void addHeader(AbstractComponent header) {
		if(getCursorX()==1) {
			newLine(); //make sure there is room for header
		}
		
		//SIGH hack to add new row
		final Label placeHolder=new Label("ph");
		addComponent(placeHolder);
		removeComponent(placeHolder);
		final int y=getCursorY();
		addComponent(header, 0, y, 1, y);
	}
	
	public void add(FieldInfo fi) {
		addComponent(fi.titleLabel);
		addComponent(fi.valueLabel);
		fi.titleLabel.addStyleName(Oph.TEXT_ALIGN_RIGHT);
	}

    public static class FieldInfo {
    	
    	private final Label titleLabel;
    	private final Label valueLabel;

		public FieldInfo(String titleKey) {
			this(titleKey, Label.CONTENT_TEXT);
		}
		
		public FieldInfo(String titleKey, Label valueLabel) {
			this(titleKey, valueLabel, Label.CONTENT_TEXT);
		}
		
		public FieldInfo(String titleKey, int cmode) {
			this(titleKey, (Label)null, cmode);
		}

		public FieldInfo(String titleKey, String value, int mode) {
			this(titleKey, new Label(value, mode));
		}

		public FieldInfo(String titleKey, Label valueLabel, int cmode) {
			super();			
			this.valueLabel = valueLabel==null ? new Label() : valueLabel;
			this.valueLabel.setContentMode(cmode);
			
			titleLabel = new Label();
			titleLabel.setValue(I18N.getMessage(titleKey));
		}
		
		public Label getTitleLabel() {
			return titleLabel;
		}
		
		public Label getValueLabel() {
			return valueLabel;
		}
    	
		public static FieldInfo text(String key, String value) {
			return new FieldInfo(key, new Label(value), Label.CONTENT_TEXT);
		}

		public static FieldInfo xhtml(String key, String value) {
			return new FieldInfo(key, new Label(value), Label.CONTENT_XHTML);
		}
    }
    
}
