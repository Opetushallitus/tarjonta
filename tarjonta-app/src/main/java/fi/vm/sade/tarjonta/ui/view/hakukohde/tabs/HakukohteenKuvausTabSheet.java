package fi.vm.sade.tarjonta.ui.view.hakukohde.tabs;

import java.util.Collections;
import java.util.List;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;

import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;

public class HakukohteenKuvausTabSheet extends HakukohdeLanguageTabSheet {

	private static final long serialVersionUID = 1L;
	
	private final LisaaKuvausDialog.Mode mode;
	
	public HakukohteenKuvausTabSheet(TarjontaPresenter presenter, String width, String height, LisaaKuvausDialog.Mode mode) {
		super(presenter, true, width, height);
		this.mode = mode;
		
		addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void valueChange(ValueChangeEvent event) {
				getTabData().clear();
				getTabData().addAll(getKieliKaannokset());
			}
		});
	}
	
	@Override
	protected List<KielikaannosViewModel> getTabData() {
		presenter.getModel().getHakukohde().getValintaPerusteidenKuvaus();
		switch (mode) {
		case SORA:
			return presenter.getModel().getHakukohde().getSoraKuvaus().getKaannokset();
		case VAPE:
			return presenter.getModel().getHakukohde().getValintaPerusteidenKuvaus().getKaannokset();
		default:
			return Collections.emptyList();
		}
	}

}
