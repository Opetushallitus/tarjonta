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

import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.TextField;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.ui.model.HakuaikaViewModel;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 * Components for sisainen hakuaika.
 * @author Markus
 */
public class HakuajatView {

	/**
	 * Start date for sisainen hakuaika.
	 */
	private DateField alkuPvm;

	/**
	 * End date for sisainen hakuaika.
	 */
	private DateField loppuPvm;

	/**
	 * Description of the sisainen hakuaika.
	 */
	private TextField kuvaus;
	private Button poistaB;

	private transient I18NHelper i18n = new I18NHelper(this);

	private HakuaikaViewModel model;

	public HakuajatView(HakuaikaViewModel model) {
		this.model = model;

		kuvaus = UiUtil.textField(null, "", i18n.getMessage("hakuajanKuvaus"), false);
		kuvaus.setPropertyDataSource(new NestedMethodProperty(model, "hakuajanKuvaus"));
		kuvaus.setImmediate(true);
                kuvaus.setHeight("25px");
                kuvaus.setWidth("300px");

		alkuPvm = UiUtil.dateField();
		alkuPvm.setDateFormat("dd.MM.yyyy HH:mm");
		
		alkuPvm.setPropertyDataSource(new NestedMethodProperty(model, "alkamisPvm"));
		alkuPvm.setImmediate(true);
		alkuPvm.setWidth("235px");
		alkuPvm.setResolution(DateField.RESOLUTION_MIN);

		loppuPvm = UiUtil.dateField();
		loppuPvm.setDateFormat("dd.MM.yyyy HH:mm");
		loppuPvm.setPropertyDataSource(new NestedMethodProperty(model, "paattymisPvm"));
		loppuPvm.setImmediate(true);
		loppuPvm.setWidth("235px");
		loppuPvm.setResolution(DateField.RESOLUTION_MIN);

		poistaB = UiUtil.buttonSmallPrimary(null, i18n.getMessage("minus"));
		//poistaB.setEnabled(false);
	}

	public DateField getAlkuPvm() {
		return alkuPvm;
	}

	public void setAlkuPvm(DateField alkuPvm) {
		this.alkuPvm = alkuPvm;
	}

	public DateField getLoppuPvm() {
		return loppuPvm;
	}

	public void setLoppuPvm(DateField loppuPvm) {
		this.loppuPvm = loppuPvm;
	}

	public TextField getKuvaus() {
		return kuvaus;
	}

	public void setKuvaus(TextField kuvaus) {
		this.kuvaus = kuvaus;
	}

	public HakuaikaViewModel getModel() {
		return model;
	}

	public void setModel(HakuaikaViewModel model) {
		this.model = model;
	}

	public Button getPoistaB() {
		return poistaB;
	}

	public void setPoistaB(Button poistaB) {
		this.poistaB = poistaB;
	}


}
