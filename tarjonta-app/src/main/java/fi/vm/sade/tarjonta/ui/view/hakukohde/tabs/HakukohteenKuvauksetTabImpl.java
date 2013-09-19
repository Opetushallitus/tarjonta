package fi.vm.sade.tarjonta.ui.view.hakukohde.tabs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.types.MonikielinenMetadataTyyppi;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.LinkitettyTekstiModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalNavigationLayout;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.LisaaKuvausDialog.LisaaKuvausListener;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.LisaaKuvausDialog.Mode;
import fi.vm.sade.vaadin.Oph;

public class HakukohteenKuvauksetTabImpl extends
		AbstractVerticalNavigationLayout {
	
	private static final long serialVersionUID = 1L;

	private final HakukohteenKuvausContainer vapeTabs;
	private final HakukohteenKuvausContainer soraTabs;

	private final TarjontaPresenter presenter;
	
	@Autowired
	private TarjontaAdminService tarjontaService;
	
	public HakukohteenKuvauksetTabImpl(TarjontaPresenter presenter) {
		super();
		this.presenter = presenter;
		vapeTabs = new HakukohteenKuvausContainer(presenter, Mode.VAPE);
		soraTabs = new HakukohteenKuvausContainer(presenter, Mode.SORA);
	}

	@Override
	protected void buildLayout(VerticalLayout layout) {
			
		createKuvausLayout(layout, "vape", vapeTabs, LisaaKuvausDialog.Mode.VAPE);
		createKuvausLayout(layout, "sora", soraTabs, LisaaKuvausDialog.Mode.SORA);
		
	}
	
	private void createKuvausLayout(VerticalLayout layout, String prefix, final HakukohteenKuvausContainer comp, final LisaaKuvausDialog.Mode mode) {
		Label title = new Label(T(prefix));
		title.addStyleName(Oph.LABEL_H1);
		title.addStyleName(Oph.SPACING_BOTTOM_20);
		if (mode==Mode.SORA) {
			title.addStyleName(Oph.SPACING_TOP_30);
		}
		layout.addComponent(title);

		Label descr = new Label(T(prefix+".ohje"));
		descr.addStyleName(Oph.LABEL_SMALL);
		descr.setWidth(HakukohteenKuvausContainer.FIELD_WIDTH);
		layout.addComponent(descr);

		Button btn = new Button(T(prefix+".haku"));
		btn.setWidth(HakukohteenKuvausContainer.FIELD_WIDTH);
		btn.addStyleName(Oph.BUTTON_LINK);
		btn.addStyleName(Oph.TEXT_ALIGN_RIGHT);
		btn.addListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				LisaaKuvausDialog dialog = new LisaaKuvausDialog(presenter, mode, new LisaaKuvausListener() {
					
					@Override
					public void onTemplate(String kuvausUri, Set<String> langUris) {
						useTemplate(mode, kuvausUri, langUris);
						comp.refresh();
					}
					
					@Override
					public void onReference(String kuvausUri) {
						useReference(mode, kuvausUri);
						comp.refresh();
					}
				});
				presenter.getRootView().addWindow(dialog);				
			}
		});
		layout.addComponent(btn);
		
		layout.addComponent(comp);
		comp.refresh();
	}
	
	private void useTemplate(Mode mode, String kuvausUri, Set<String> langUris) {
		
		List<KielikaannosViewModel> lms = new ArrayList<KielikaannosViewModel>();
		List<MonikielinenMetadataTyyppi> md = presenter.haeMetadata(kuvausUri, mode.category().toString());

		for (MonikielinenMetadataTyyppi mmt : md) {
			if (langUris.contains(mmt.getKieli())) {
				lms.add(new KielikaannosViewModel(mmt.getKieli(), mmt.getArvo()));
			}
		}
		
		setModel(mode, new LinkitettyTekstiModel(lms));
	}
	
	private void useReference(Mode mode, String kuvausUri) {
		setModel(mode, new LinkitettyTekstiModel(kuvausUri));
	}
	
	private void setModel(Mode mode, LinkitettyTekstiModel model) {
		switch(mode) {
		case SORA:
			presenter.getModel().getHakukohde().setSoraKuvaus(model);
			break;			
		case VAPE:
			presenter.getModel().getHakukohde().setValintaPerusteidenKuvaus(model);
			break;
		}
	}
	

}
