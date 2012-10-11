package fi.vm.sade.tarjonta.ui.view.haku;

import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.TextField;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.ui.helper.I18NHelper;
import fi.vm.sade.tarjonta.ui.model.HakuaikaViewModel;
import fi.vm.sade.vaadin.util.UiUtil;

public class HakuajatView {

	private DateField alkuPvm;
	private DateField loppuPvm;
	private TextField kuvaus;
	private Button poistaB;

	private I18NHelper i18n = new I18NHelper(this);
	
	private HakuaikaViewModel model;
	
	public HakuajatView(HakuaikaViewModel model) {
		this.model = model;
		
		kuvaus = UiUtil.textField(null);
		kuvaus.setPropertyDataSource(new NestedMethodProperty(model, "hakuajanKuvaus"));
		kuvaus.setImmediate(true);
		
		alkuPvm = UiUtil.dateField();
		alkuPvm.setPropertyDataSource(new NestedMethodProperty(model, "alkamisPvm"));
		alkuPvm.setImmediate(true);
		
		loppuPvm = UiUtil.dateField();
		loppuPvm.setPropertyDataSource(new NestedMethodProperty(model, "paattymisPvm"));
		loppuPvm.setImmediate(true);
		
		poistaB = UiUtil.buttonSmallPrimary(null, I18N.getMessage("minus"));
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
