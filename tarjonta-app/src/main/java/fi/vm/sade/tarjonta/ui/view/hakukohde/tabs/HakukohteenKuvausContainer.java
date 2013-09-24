package fi.vm.sade.tarjonta.ui.view.hakukohde.tabs;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.HorizontalLayout;

import fi.vm.sade.tarjonta.ui.model.HakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.model.LinkitettyTekstiModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.LisaaKuvausDialog.Mode;

public class HakukohteenKuvausContainer extends HorizontalLayout {

	public static final String FIELD_WIDTH = "650px";
	public static final String FIELD_HEIGHT = "450px";
	
	private static final long serialVersionUID = 1L;

	private final TarjontaPresenter presenter;
	private final LisaaKuvausDialog.Mode mode;
	
	private AbstractComponentContainer component;

	private List<ValueChangeListener> listeners = new ArrayList<ValueChangeListener>();
	
	public HakukohteenKuvausContainer(TarjontaPresenter presenter, LisaaKuvausDialog.Mode mode) {
		this.presenter = presenter;
		this.mode = mode;
	}
	
	@Override
	public void attach() {
		super.attach();
		refresh();
	}
	
	private LinkitettyTekstiModel getModel() {
		HakukohdeViewModel hvm = presenter.getModel().getHakukohde();
		return mode==Mode.SORA ? hvm.getSoraKuvaus() : hvm.getValintaPerusteidenKuvaus();
	}
	
	public void addListener(ValueChangeListener listener) {
		listeners.add(listener);
		if (component instanceof HakukohteenKuvausTabSheet) {
			((HakukohteenKuvausTabSheet) component).addValueChangeListener(listener);
		}
	}
	
	public void refresh() {
		boolean useLink = getModel().getUri()!=null;
		removeAllComponents();
		component = useLink
					? new HakukohteenKuvausPreviewSheet(presenter, mode)
					: new HakukohteenKuvausTabSheet(presenter, FIELD_WIDTH, FIELD_HEIGHT, mode);

		if (component instanceof HakukohteenKuvausTabSheet) {
			for (ValueChangeListener l : listeners) {
				((HakukohteenKuvausTabSheet) component).addValueChangeListener(l);
			}
		}

		addComponent(component);
	}
	
}
