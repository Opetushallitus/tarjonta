package fi.vm.sade.tarjonta.ui.view.hakukohde.tabs;

import java.util.Collections;
import java.util.List;

import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;

public class HakukohteenKuvausTabSheet extends HakukohdeLanguageTabSheet {

	private static final long serialVersionUID = 1L;
	
	private final LisaaKuvausDialog.Mode mode;

	public HakukohteenKuvausTabSheet(TarjontaPresenter presenter, String width, String height, LisaaKuvausDialog.Mode mode) {
		super(presenter, true, width, height);
		this.mode = mode;
	}

	@Override
	protected List<KielikaannosViewModel> getTabData() {
		presenter.getModel().getHakukohde().getValintaPerusteidenKuvaus();
		switch (mode) {
		case SORA:
			return presenter.getModel().getHakukohde().getValintaPerusteidenKuvaus().getKaannokset();
		case VAPE:
			return presenter.getModel().getHakukohde().getSoraKuvaus().getKaannokset();
		default:
			return Collections.emptyList();
		}
	}

}
