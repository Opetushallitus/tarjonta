package fi.vm.sade.tarjonta.ui.view.koulutus;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;

import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.ui.enums.CommonTranslationKeys;
import fi.vm.sade.tarjonta.ui.enums.KoulutusActiveTab;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.SimpleHakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusLisatiedotModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusLisatietoModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.service.OrganisaatioContext;
import fi.vm.sade.tarjonta.ui.view.common.CategoryTreeView;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 * Display information in one language
 */
@Configurable(preConstruction = true)
public class ShowKoulutusViewTab extends CustomComponent {

	private static final long serialVersionUID = 1L;

	@Autowired
	private TarjontaPresenter presenter;

	@Autowired
	TarjontaUIHelper uiHelper;

	private I18NHelper i18n = new I18NHelper("ShowKoulutusView.");

	private final String language;
	
	private final OrganisaatioContext context;

	public ShowKoulutusViewTab(String language) {
		Preconditions.checkNotNull(language, "Language cannot be null");
		this.language = language;
		this.context = OrganisaatioContext.getContext(presenter.getTarjoaja().getOrganisationOid());
		final VerticalLayout layout = new VerticalLayout();
		setCompositionRoot(layout);
		build(layout);
	}
	

	/**
	 * Add label + component to grid layout.
	 * 
	 * @param grid
	 * @param labelCaptionKey
	 * @param component
	 */
	private void addItemToGrid(final GridLayout grid,
			final String labelCaptionKey, final Component component) {
		if (grid != null) {
			final HorizontalLayout hl = UiUtil.horizontalLayout(false,
					UiMarginEnum.RIGHT);
			hl.setSizeUndefined();
			UiUtil.label(hl, i18n.getMessage(labelCaptionKey));
			grid.addComponent(hl);

			final HorizontalLayout textArea = UiUtil.horizontalLayout(false,
					UiMarginEnum.NONE);
			textArea.addComponent(component);
			grid.addComponent(textArea);

			grid.setComponentAlignment(hl, Alignment.TOP_RIGHT);
			grid.setComponentAlignment(textArea, Alignment.TOP_LEFT);
			grid.newLine();
		}
	}

	/**
	 * Add line with label + textual label value to the grid.
	 * 
	 * @param grid
	 * @param labelCaptionKey
	 * @param labelCaptionValue
	 */
	private void addItemToGrid(final GridLayout grid,
			final String labelCaptionKey, final String labelCaptionValue) {
		addItemToGrid(grid, labelCaptionKey, new Label(labelCaptionValue));
	}

	private void addLayoutSplit(final AbstractLayout parent) {
		final VerticalSplitPanel split = new VerticalSplitPanel();
		split.setImmediate(false);
		split.setWidth("100%");
		split.setHeight("2px");
		split.setLocked(true);
		parent.addComponent(split);
	}

    private void build(final VerticalLayout parent) {
		//TODO add esikatsele here
    	//addLayoutSplit(parent);
		buildKoulutuksenPerustiedot(parent);
		addLayoutSplit(parent);
		buildKoulutuksenKuvailevatTiedot(parent);
		addLayoutSplit(parent);
		buildKoulutuksenSisaltyvatOpintokokonaisuudet(parent);
		addLayoutSplit(parent);
		buildKoulutuksenHakukohteet(parent);
		addLayoutSplit(parent);
	}

    private HorizontalLayout buildHeaderLayout(String title, String btnCaption, Button.ClickListener listener, boolean hide) {
        HorizontalLayout headerLayout = UiUtil.horizontalLayout(true, UiMarginEnum.NONE);
        Label titleLabel = UiUtil.label(headerLayout, title);
        titleLabel.setStyleName(Oph.LABEL_H2);


        if (btnCaption != null) {
            headerLayout.addComponent(titleLabel);
            Button btn = UiBuilder.buttonSmallPrimary(headerLayout, btnCaption, listener);

            // Add default click listener so that we can show that action has not been implemented as of yet
            if (listener == null) {
                btn.addListener(new Button.ClickListener() {
                    private static final long serialVersionUID = 5019806363620874205L;

                    @Override
                    public void buttonClick(ClickEvent event) {
                        getWindow().showNotification("Toiminnallisuutta ei vielä toteutettu");
                    }
                });
            }

            headerLayout.setExpandRatio(btn, 1f);
            headerLayout.setComponentAlignment(btn, Alignment.TOP_RIGHT);
        }
        return headerLayout;
    }
    
    private void buildKoulutuksenHakukohteet(VerticalLayout layout) {
        int numberOfApplicationTargets = presenter.getModel().getKoulutusPerustiedotModel().getKoulutuksenHakukohteet().size();

        layout.addComponent(buildHeaderLayout(i18n.getMessage("hakukohteet", numberOfApplicationTargets), i18n.getMessage("luoUusiHakukohdeBtn"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(ClickEvent event) {
                List<String> koulutus = new ArrayList<String>();
                koulutus.add(presenter.getModel().getKoulutusPerustiedotModel().getOid());
                presenter.showHakukohdeEditView(koulutus, null,null);
            }
        }, presenter.getPermission().userCanCreateHakukohde(context)));

        CategoryTreeView categoryTree = new CategoryTreeView();
        categoryTree.setHeight("100px");
        categoryTree.setContainerDataSource(createHakukohdelistContainer(presenter.getModel().getKoulutusPerustiedotModel().getKoulutuksenHakukohteet()));
        String[] visibleColumns = {"nimiBtn", "poistaBtn"};
        categoryTree.setVisibleColumns(visibleColumns);
        for (Object item : categoryTree.getItemIds()) {
            categoryTree.setChildrenAllowed(item, false);
        }
        layout.addComponent(categoryTree);

    }


	
    /**
	 * Localized descriptive data about the koulutus.
	 * 
	 * @param layout
	 */
	private void buildKoulutuksenKuvailevatTiedot(final VerticalLayout layout) {
		//tutkinnon koulutukselliset ja ammatilliset tavopitteet
		//koulutuksen koulutukselliset ja ammatilliset tavoitteet
		//koulutusohjelman valinta
		//koulutuksen sisältö
		//koulutuksen rakenne
		//kansainvälisyys
		//Sijoittuminen työelämään
		//ammattinimikkeet
		//Jatko-opintomahdollisuudet
		
		Preconditions.checkNotNull(layout, "Layout cannot be null");
		final KoulutusLisatiedotModel lisatiedotModel = presenter.getModel()
				.getKoulutusLisatiedotModel();

		layout.addComponent(buildHeaderLayout(
				i18n.getMessage("kuvailevatTiedot"),
				i18n.getMessage(CommonTranslationKeys.MUOKKAA),
				new Button.ClickListener() {
					private static final long serialVersionUID = 5019806363620874205L;

					@Override
					public void buttonClick(ClickEvent event) {
						presenter.showKoulutustEditView(getEditViewOid(),
								KoulutusActiveTab.LISATIEDOT);
					}
				}, presenter.getPermission().userCanUpdateKoulutus(context)));

		final KoulutusLisatietoModel lisatietoForLang = lisatiedotModel
				.getLisatiedot().get(language);
		

		final GridLayout grid = new GridLayout(2, 1);
		grid.setWidth("100%");
		grid.setMargin(true);

        addItemToGrid(grid, "tutkinnonKoulutuksellisetJaAmmatillisetTavoitteet", uiHelper.getKoodiNimi(presenter.getModel().getKoulutusLisatiedotModel().getAmmattinimikkeet(), null));
        addItemToGrid(grid, "koulutuksenKoulutuksellisetJaAmmatillisetTavoitteet", uiHelper.getKoodiNimi(presenter.getModel().getKoulutusLisatiedotModel().getAmmattinimikkeet(), null));
		addItemToGrid(grid, "koulutusohjelmanValinta",
				buildLabel(lisatietoForLang.getKoulutusohjelmanValinta()));
		addItemToGrid(grid, "koulutuksenSisalto",
				buildLabel(lisatietoForLang.getSisalto()));
		addItemToGrid(grid, "koulutuksenRakenne",
				buildLabel(lisatietoForLang.getKuvailevatTiedot()));
		addItemToGrid(grid, "tutkinnonKansainvalistyminen",
				buildLabel(lisatietoForLang.getKansainvalistyminen()));
		addItemToGrid(grid, "tutkinnonSijoittuminenTyoelamaan",
				buildLabel(lisatietoForLang.getSijoittuminenTyoelamaan()));
        addItemToGrid(grid, "ammattinimikkeet", uiHelper.getKoodiNimi(presenter.getModel().getKoulutusLisatiedotModel().getAmmattinimikkeet(), null));
        addItemToGrid(grid, "jatkoOpintomahdollisuudet", uiHelper.getKoodiNimi(presenter.getModel().getKoulutusLisatiedotModel().getAmmattinimikkeet(), null));
		
		
//		addItemToGrid(grid, "tutkinnonKuvailevatTiedot",
//				buildLabel(lisatietoForLang.getKuvailevatTiedot()));
//		addItemToGrid(grid, "tutkinnonYhteistyoMuidenToimijoidenKanssa",
//				buildLabel(lisatietoForLang
//						.getYhteistyoMuidenToimijoidenKanssa()));


		grid.setColumnExpandRatio(1, 1f);

		layout.addComponent(grid);
		layout.setComponentAlignment(grid, Alignment.TOP_LEFT);
	}
    
    private void buildKoulutuksenPerustiedot(VerticalLayout layout) {
    	
        //organisaatio
        //koulutus/tutkinto
        //koulutusohjelma
        //koulutusaste
        //koulutusala
        //opintoala
        //tutkintonimike
        //opintojen laajuus
        //koulutuslaji
        //pohjakoulutusvaatimus
        //koulutuksen alkamispäivä
        //suunniteltu kesto
        //opetuskieli
        //opetusmuoto
        //linkki opetussuunnitelmaan
        //koulutuksen yhteyshenkilö

    	
    	Preconditions.checkNotNull(presenter, "presenter cannot be null");
    	Preconditions.checkNotNull(presenter.getModel(), "model cannot be null");
    	Preconditions.checkNotNull(presenter.getModel().getKoulutusPerustiedotModel(), "koulutusperustiedot model cannot be null");
        final KoulutusToisenAsteenPerustiedotViewModel model = presenter.getModel().getKoulutusPerustiedotModel();

        layout.addComponent(buildHeaderLayout(i18n.getMessage("perustiedot"), i18n.getMessage(CommonTranslationKeys.MUOKKAA), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(ClickEvent event) {
                presenter.showKoulutustEditView(getEditViewOid(), KoulutusActiveTab.PERUSTIEDOT);
            }
        },presenter.getPermission().userCanUpdateKoulutus(context)));
        final GridLayout grid = new GridLayout(2, 1);
        grid.setMargin(true);

        addItemToGrid(grid, "organisaatio", presenter.getTarjoaja().getOrganisationName());
        addItemToGrid(grid, "koulutusTutkinto", presenter.getTarjoaja().getOrganisationName());
        addItemToGrid(grid, "koulutusohjelma", presenter.getTarjoaja().getOrganisationName());
        addItemToGrid(grid, "koulutusaste", presenter.getTarjoaja().getOrganisationName());
        addItemToGrid(grid, "koulutusala", presenter.getTarjoaja().getOrganisationName());
        addItemToGrid(grid, "opintoala", presenter.getTarjoaja().getOrganisationName());
        addItemToGrid(grid, "tutkintonimike", presenter.getTarjoaja().getOrganisationName());
        addItemToGrid(grid, "opintojenLaajuus", presenter.getTarjoaja().getOrganisationName());
        addItemToGrid(grid, "koulutuslaji", uiHelper.getKoodiNimi(model.getKoulutuslaji()));
        addItemToGrid(grid, "pohjakoulutusvaatimus", uiHelper.getKoodiNimi(model.getKoulutuslaji()));
        addItemToGrid(grid, "koulutuksenAlkamisPvm", uiHelper.formatDate(model.getKoulutuksenAlkamisPvm()));
        addItemToGrid(grid, "suunniteltuKesto", suunniteltuKesto(model));
        addItemToGrid(grid, "opetuskieli", uiHelper.getKoodiNimi(model.getOpetuskieli(), null));
        addItemToGrid(grid, "opetusmuoto", uiHelper.getKoodiNimi(model.getOpetusmuoto(), null));
        addItemToGrid(grid, "linkkiOpetussuunnitelmaan", uiHelper.getKoodiNimi(model.getOpetusmuoto(), null));
        addItemToGrid(grid, "koulutuksenYhteyshenkilo", uiHelper.getKoodiNimi(model.getOpetusmuoto(), null));
        grid.setColumnExpandRatio(0, 0.5f);
        grid.setColumnExpandRatio(1, 0.5f);

        layout.addComponent(grid);
        layout.setComponentAlignment(grid, Alignment.TOP_LEFT);
    }


	private void buildKoulutuksenSisaltyvatOpintokokonaisuudet(VerticalLayout layout) {
        // TODO get number of included(?) Koulutus entries
        int numberOfIncludedOpintokokonaisuus = 1;
        layout.addComponent(buildHeaderLayout(i18n.getMessage("sisaltyvatOpintokokonaisuudet", numberOfIncludedOpintokokonaisuus), i18n.getMessage(CommonTranslationKeys.MUOKKAA), null, presenter.getPermission().userCanUpdateKoulutus(context)));
    }

	private Label buildLabel(String text) {
		Label label = UiUtil.label(null, text);
		label.setContentMode(Label.CONTENT_XHTML);
		label.setSizeFull();
		return label;
	}

	private Container createHakukohdelistContainer(List<SimpleHakukohdeViewModel> hakukohdes) {
        final BeanItemContainer<ShowKoulutusHakukohdeRow> hakukohdeRows = new BeanItemContainer<ShowKoulutusHakukohdeRow>(ShowKoulutusHakukohdeRow.class);
        hakukohdeRows.addAll(getKoulutusHakukohdeRows(hakukohdes));
        return hakukohdeRows;
    }

	private String getEditViewOid() {
		final KoulutusToisenAsteenPerustiedotViewModel model = presenter
				.getModel().getKoulutusPerustiedotModel();
		return model.getOid();
	}

	private List<ShowKoulutusHakukohdeRow> getKoulutusHakukohdeRows(List<SimpleHakukohdeViewModel> hakukohdes) {
        final List<ShowKoulutusHakukohdeRow> rows = Lists.newArrayList();
        for (SimpleHakukohdeViewModel hakukohdeViewModel : hakukohdes) {
            final ShowKoulutusHakukohdeRow row = new ShowKoulutusHakukohdeRow(hakukohdeViewModel);
            rows.add(row);
        }
        return rows;
    }

    private String suunniteltuKesto(KoulutusToisenAsteenPerustiedotViewModel model) {
        // Build suunniteltu kesto and kesto tyyppi as string
        String tmp = "";
        if (model.getSuunniteltuKesto() != null) {
            tmp = model.getSuunniteltuKesto();
            tmp += " ";

            String kestotyyppi = uiHelper.getKoodiNimi(model.getSuunniteltuKestoTyyppi(), null);
            if (kestotyyppi != null) {
                tmp += kestotyyppi;
            } else {
                // Add uri if no translation ... just to show something.
                tmp += model.getSuunniteltuKestoTyyppi();
            }
        }
        return tmp;
    }


}
