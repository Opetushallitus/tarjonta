package fi.vm.sade.tarjonta.ui.view.hakukohde.tabs;

import java.util.Set;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalNavigationLayout;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.LisaaKuvausDialog.LisaaKuvausListener;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.LisaaKuvausDialog.Mode;
import fi.vm.sade.vaadin.Oph;

public class HakukohteenKuvauksetTabImpl extends
		AbstractVerticalNavigationLayout {

	private static final String FIELD_WIDTH = "650px";
	private static final String FIELD_HEIGHT = "450px";
	
	private static final long serialVersionUID = 1L;

	private final ValintaPerusteKuvausTabSheet vapeTabs = new ValintaPerusteKuvausTabSheet(true, FIELD_WIDTH, FIELD_HEIGHT);
	private final ValintaPerusteKuvausTabSheet soraTabs = new ValintaPerusteKuvausTabSheet(true, FIELD_WIDTH, FIELD_HEIGHT);

	private final TarjontaPresenter presenter;
	
	public HakukohteenKuvauksetTabImpl(TarjontaPresenter presenter) {
		super();
		this.presenter = presenter;
	}

	@Override
	protected void buildLayout(VerticalLayout layout) {
			
		createKuvausLayout(layout, "vape", vapeTabs, LisaaKuvausDialog.Mode.VAPE);
		createKuvausLayout(layout, "sora", soraTabs, LisaaKuvausDialog.Mode.SORA);
		
	}
	
	private void createKuvausLayout(VerticalLayout layout, String prefix, HakukohdeLanguageTabSheet comp, final LisaaKuvausDialog.Mode mode) {
		Label title = new Label(T(prefix));
		title.addStyleName(Oph.LABEL_H1);
		title.addStyleName(Oph.SPACING_BOTTOM_20);
		if (mode==Mode.SORA) {
			title.addStyleName(Oph.SPACING_TOP_30);
		}
		layout.addComponent(title);

		Label descr = new Label(T(prefix+".ohje"));
		descr.addStyleName(Oph.LABEL_SMALL);
		descr.setWidth(FIELD_WIDTH);
		layout.addComponent(descr);

		Button btn = new Button(T(prefix+".haku"));
		btn.setWidth(FIELD_WIDTH);
		btn.addStyleName(Oph.BUTTON_LINK);
		btn.addStyleName(Oph.TEXT_ALIGN_RIGHT);
		btn.addListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				LisaaKuvausDialog dialog = new LisaaKuvausDialog(presenter, mode, new LisaaKuvausListener() {
					
					@Override
					public void lisaaKuvaukset(boolean asTemplate, String kuvaus,
							Set<String> langs) {
						System.out.println("LISÄÄ t="+asTemplate+", k="+kuvaus+", l="+langs);
						
					}
				});
				presenter.getRootView().addWindow(dialog);
				
			}
		});
		layout.addComponent(btn);
		
		layout.addComponent(comp);

	}
	
	

}
