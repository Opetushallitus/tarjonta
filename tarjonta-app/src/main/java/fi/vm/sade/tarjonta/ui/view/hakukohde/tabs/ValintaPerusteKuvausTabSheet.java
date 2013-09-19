package fi.vm.sade.tarjonta.ui.view.hakukohde.tabs;

import java.util.Collections;
import java.util.List;

import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;

public class ValintaPerusteKuvausTabSheet extends HakukohdeLanguageTabSheet {

	private static final long serialVersionUID = 1L;


	public ValintaPerusteKuvausTabSheet(TarjontaPresenter presenter, String width, String height) {
		super(presenter, true, width, height);
		setEnabled(false);
		setReadOnly(true);
	}

	@Override
	protected List<KielikaannosViewModel> getTabData() {
		return Collections.emptyList();
	}

}
