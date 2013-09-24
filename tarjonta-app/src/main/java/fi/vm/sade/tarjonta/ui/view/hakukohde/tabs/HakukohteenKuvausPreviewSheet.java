package fi.vm.sade.tarjonta.ui.view.hakukohde.tabs;

import java.util.List;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;

import fi.vm.sade.tarjonta.service.types.MonikielinenMetadataTyyppi;
import fi.vm.sade.tarjonta.ui.model.LinkitettyTekstiModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.LisaaKuvausDialog.Mode;

public class HakukohteenKuvausPreviewSheet extends TabSheet {

	private static final long serialVersionUID = 1L;

	private final TarjontaPresenter presenter;
	private final LisaaKuvausDialog.Mode mode;

	public HakukohteenKuvausPreviewSheet(TarjontaPresenter presenter, LisaaKuvausDialog.Mode mode) {
		this.presenter = presenter;
		this.mode = mode;
	}
	
	private Component content(String content) {
		Panel ret = new Panel();
		ret.addComponent(new Label(content, Label.CONTENT_XHTML));
		
		ret.setWidth(HakukohteenKuvausContainer.FIELD_WIDTH);
		ret.setHeight(HakukohteenKuvausContainer.FIELD_HEIGHT);
		
		return ret;
	}
	
	@Override
	public void attach() {
		super.attach();
		
		LinkitettyTekstiModel ltm = mode==Mode.SORA
				? presenter.getModel().getHakukohde().getSoraKuvaus()
				: presenter.getModel().getHakukohde().getValintaPerusteidenKuvaus();
	
		List<MonikielinenMetadataTyyppi> txts = presenter.haeMetadata(ltm.getUri(), mode.category().toString());
		for (MonikielinenMetadataTyyppi txt : txts) {
			addTab(content(txt.getArvo()), presenter.getUiHelper().getKoodiNimi(txt.getKieli()));
		}
				
	}

}
