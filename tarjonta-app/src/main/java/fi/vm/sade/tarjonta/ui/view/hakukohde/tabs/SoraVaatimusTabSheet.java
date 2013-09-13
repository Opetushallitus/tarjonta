package fi.vm.sade.tarjonta.ui.view.hakukohde.tabs;

import java.util.Collections;
import java.util.List;

import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;

public class SoraVaatimusTabSheet extends HakukohdeLanguageTabSheet {

	private static final long serialVersionUID = 1L;

	public SoraVaatimusTabSheet(boolean useRichText, String width,
			String height) {
		super(useRichText, width, height);
	}

	public SoraVaatimusTabSheet(TarjontaPresenter presenter,
			boolean useRichText, String width, String height) {
		super(presenter, useRichText, width, height);
	}

	@Override
	protected List<KielikaannosViewModel> getTabData() {
		return Collections.emptyList();
	}

}
