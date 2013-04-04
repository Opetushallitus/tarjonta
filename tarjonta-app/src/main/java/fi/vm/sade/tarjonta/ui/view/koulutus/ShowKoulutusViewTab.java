package fi.vm.sade.tarjonta.ui.view.koulutus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
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
import fi.vm.sade.tarjonta.service.types.LueKoulutusVastausTyyppi;
import fi.vm.sade.tarjonta.ui.enums.CommonTranslationKeys;
import fi.vm.sade.tarjonta.ui.enums.KoulutusActiveTab;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.SimpleHakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.MonikielinenTekstiModel;
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
	
	private final Locale locale;
	
	//if provided locale is one of these use it instead of null
	private final Set<String> allowedKoodistoLocales = ImmutableSet.copyOf(new String[]{"fi", "sv", "en"});
	
	private final OrganisaatioContext context;
	private final LueKoulutusVastausTyyppi koulutus;

	/**
	 * 
	 * @param language is used to match monikielinen teksi
	 * @param locale is used for koodi values
	 * @param koulutus the koulutus to display
	 */
	public ShowKoulutusViewTab(final String language, final Locale locale, final LueKoulutusVastausTyyppi koulutus) {
		Preconditions.checkNotNull(language, "Language cannot be null");
		Preconditions.checkNotNull(presenter.getTarjoaja(), "Tarjoaja cannot be null");
		Preconditions.checkNotNull(presenter.getTarjoaja().getOrganisationOid(), "Tarjoaja organisaatioOid cannot be null");
		this.language = language;
		this.locale = getKoodistoLocale(locale);
		this.koulutus = koulutus;
		this.context = OrganisaatioContext.getContext(presenter.getTarjoaja().getOrganisationOid());
		final VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setMargin(true);
		setCompositionRoot(layout);
		build(layout);
	}
	
	/**
	 * Return locale based on submitted locale 
	 * @param locale2 if one of fi,en,sv return it, in other cases return "fi" 
	 * @return
	 */
	private Locale getKoodistoLocale(final Locale locale) {
		if(allowedKoodistoLocales.contains(locale.getLanguage().toLowerCase())) {
			return locale;
		}
		return new Locale("fi");
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

    private GridLayout buildHeaderLayout(String title, String btnCaption, Button.ClickListener listener, boolean hide, boolean showTime) {
        final GridLayout layout = new GridLayout(showTime?3:2,  1);
        final Label titleLabel = UiUtil.label(layout, title);
        titleLabel.setStyleName(Oph.LABEL_H2);

        //TODO get real date
        final Date modifiedDate = new Date();
        
        if(showTime) {
        	final Label modifiedLabel = UiUtil.label(layout, i18n.getMessage("tallennettuLabel", uiHelper.formatDate(modifiedDate), uiHelper.formatTime(modifiedDate)));
        	modifiedLabel.setStyleName(Oph.LABEL_SMALL);
        }

        if (btnCaption != null) {
            Button button = UiBuilder.buttonSmallPrimary(layout, btnCaption, listener);

            // Add default click listener so that we can show that action has not been implemented as of yet
            if (listener == null) {
                button.addListener(new Button.ClickListener() {
                    private static final long serialVersionUID = 5019806363620874205L;

                    @Override
                    public void buttonClick(ClickEvent event) {
                        getWindow().showNotification("Toiminnallisuutta ei vielä toteutettu");
                    }
                });
            }
            layout.setComponentAlignment(button, Alignment.TOP_RIGHT);

        }
        layout.setWidth("100%");
        return layout;
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
        }, presenter.getPermission().userCanCreateHakukohde(context), false));

        final CategoryTreeView categoryTree = new CategoryTreeView();
        categoryTree.setHeight("100px");
        categoryTree.setContainerDataSource(createHakukohdelistContainer(presenter.getModel().getKoulutusPerustiedotModel().getKoulutuksenHakukohteet()));
        final String[] visibleColumns = {"nimiBtn", "poistaBtn"};
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
				}, presenter.getPermission().userCanUpdateKoulutus(context), false));

		final KoulutusLisatietoModel lisatietoForLang = lisatiedotModel
				.getLisatiedot().get(language);

        final KoulutusToisenAsteenPerustiedotViewModel model = presenter.getModel().getKoulutusPerustiedotModel();

        final KoulutuskoodiModel koodiModel = model.getKoulutuskoodiModel();

        final MonikielinenTekstiModel koulutuksenRakenne = koodiModel.getKoulutuksenRakenne();

		final GridLayout grid = new GridLayout(2, 1);
		grid.setWidth("100%");
		grid.setMargin(true);

        addItemToGrid(grid, "tutkinnonKoulutuksellisetJaAmmatillisetTavoitteet", getText(model.getTavoitteet()));
        addItemToGrid(grid, "koulutuksenKoulutuksellisetJaAmmatillisetTavoitteet", getText(model.getKoulutusohjelmaTavoitteet()));
		addItemToGrid(grid, "koulutusohjelmanValinta",
				buildLabel(lisatietoForLang.getKoulutusohjelmanValinta()));
		addItemToGrid(grid, "koulutuksenSisalto",
				buildLabel(lisatietoForLang.getSisalto()));
		addItemToGrid(grid, "koulutuksenRakenne",
				uiHelper.getKoodiNimi(koulutuksenRakenne.getKoodistoUri(), locale));
		addItemToGrid(grid, "tutkinnonKansainvalistyminen",
				buildLabel(lisatietoForLang.getKansainvalistyminen()));
		addItemToGrid(grid, "tutkinnonSijoittuminenTyoelamaan",
				buildLabel(lisatietoForLang.getSijoittuminenTyoelamaan()));
        addItemToGrid(grid, "ammattinimikkeet", uiHelper.getKoodiNimi(presenter.getModel().getKoulutusLisatiedotModel().getAmmattinimikkeet(), locale));
        addItemToGrid(grid, "jatkoOpintomahdollisuudet", getText(model.getJatkoopintomahdollisuudet()));

		grid.setColumnExpandRatio(1, 1f);

		layout.addComponent(grid);
		layout.setComponentAlignment(grid, Alignment.TOP_LEFT);
	}
    
	/**
	 * Return localized (uses koodistoLocale) text (if available)
	 */
	private String getText(MonikielinenTekstiModel koulutusohjelmaTavoitteet) {
		if (locale != null && koulutusohjelmaTavoitteet != null) {
			Set<KielikaannosViewModel> kaannokset = koulutusohjelmaTavoitteet
					.getKielikaannos();
			for (KielikaannosViewModel kaannos : kaannokset) {
				if (kaannos.getKielikoodi()
						.equals(locale.getLanguage())) {
					return kaannos.getNimi();
				}
			}
		}
		return "";
	}

	private void buildKoulutuksenPerustiedot(VerticalLayout layout) {
    	
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
        },presenter.getPermission().userCanUpdateKoulutus(context), true));
        final GridLayout grid = new GridLayout(2, 1);
        grid.setMargin(true);
        
        final KoulutuskoodiModel koodiModel = model.getKoulutuskoodiModel();
        final KoodiModel koulutusala = koodiModel.getKoulutusala();//   model.getKoulutusala();
        final KoodiModel tutkintonimike = model.getKoulutusohjelmaModel().getTutkintonimike();
        final KoodiModel opintoala = koodiModel.getOpintoala();
        final KoodiModel opintojenLaajuus = koodiModel.getOpintojenLaajuus();
        final KoodiModel opintojenLaajuusyksikko = koodiModel.getOpintojenLaajuusyksikko();

        //TODO get org name for current language
        addItemToGrid(grid, "organisaatio", presenter.getTarjoaja().getOrganisationName());
        addItemToGrid(grid, "koulutusTutkinto", uiHelper.getKoodiNimi(koulutus.getKoulutusKoodi().getUri(), locale));
        addItemToGrid(grid, "koulutusohjelma", uiHelper.getKoodiNimi(koulutus.getKoulutusohjelmaKoodi().getUri(), locale));
        addItemToGrid(grid, "koulutusaste", uiHelper.getKoodiNimi(koulutus.getKoulutusaste().getUri(), locale));
        addItemToGrid(grid, "koulutusala", uiHelper.getKoodiNimi(koulutusala.getKoodistoUri(), locale));
        addItemToGrid(grid, "opintoala", uiHelper.getKoodiNimi(opintoala.getKoodistoUri(), locale));
        addItemToGrid(grid, "tutkintonimike", uiHelper.getKoodiNimi(tutkintonimike.getKoodistoUri(), locale));
        addItemToGrid(grid, "opintojenLaajuus", uiHelper.getKoodiNimi(opintojenLaajuus.getKoodi(), locale) + "/" + uiHelper.getKoodiNimi(opintojenLaajuusyksikko.getKoodistoUri(), locale));
        addItemToGrid(grid, "koulutuslaji", uiHelper.getKoodiNimi(koulutus.getKoulutuslaji().get(0).getUri(), locale));
        addItemToGrid(grid, "pohjakoulutusvaatimus", uiHelper.getKoodiNimi(koulutus.getPohjakoulutusvaatimus().getUri(), locale));
        addItemToGrid(grid, "koulutuksenAlkamisPvm", uiHelper.formatDate(model.getKoulutuksenAlkamisPvm()));
        addItemToGrid(grid, "suunniteltuKesto", getSuunniteltuKesto(model));
        addItemToGrid(grid, "opetuskieli", uiHelper.getKoodiNimi(model.getOpetuskieli(), locale));
        addItemToGrid(grid, "opetusmuoto", uiHelper.getKoodiNimi(model.getOpetusmuoto(), locale));
        addItemToGrid(grid, "linkkiOpetussuunnitelmaan", getLinkkiOpetussuunnitelmaan());
        addItemToGrid(grid, "koulutuksenYhteyshenkilo", getYhteyshenkilo());
        grid.setColumnExpandRatio(0, 0.5f);
        grid.setColumnExpandRatio(1, 0.5f);

        layout.addComponent(grid);
        layout.setComponentAlignment(grid, Alignment.TOP_LEFT);
    }


    /**
     * Opetussuunnitelman linkki
     */
	private String getLinkkiOpetussuunnitelmaan() {
		return presenter.getModel().getKoulutusPerustiedotModel().getOpsuLinkki();
	}


	/**
	 * Yhteyshenkilö
	 */
	private String getYhteyshenkilo() {
		final List<fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi> yhteyshenkilot = koulutus.getYhteyshenkilo();
		
		if(yhteyshenkilot.size()<1) {
			return "";
		}
		final fi.vm.sade.tarjonta.service.types.YhteyshenkiloTyyppi yhteyshenkilo = yhteyshenkilot.get(0);
		return yhteyshenkilo.getEtunimet() + " " + yhteyshenkilo.getSukunimi() + ", " + yhteyshenkilo.getTitteli() + ", " + yhteyshenkilo.getPuhelin() + ", " + yhteyshenkilo.getSahkoposti();
	}


	private void buildKoulutuksenSisaltyvatOpintokokonaisuudet(VerticalLayout layout) {
		return;
//        // TODO get number of included(?) Koulutus entries
//        int numberOfIncludedOpintokokonaisuus = 1;
//        layout.addComponent(buildHeaderLayout(i18n.getMessage("sisaltyvatOpintokokonaisuudet", numberOfIncludedOpintokokonaisuus), i18n.getMessage(CommonTranslationKeys.MUOKKAA), null, presenter.getPermission().userCanUpdateKoulutus(context)));
    }

	private Label buildLabel(String text) {
		final Label label = UiUtil.label(null, text);
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

    private String getSuunniteltuKesto(KoulutusToisenAsteenPerustiedotViewModel model) {
        // Build suunniteltu kesto and kesto tyyppi as string
        String tmp = "";
        if (model.getSuunniteltuKesto() != null) {
            tmp = model.getSuunniteltuKesto();
            tmp += " ";

            String kestotyyppi = uiHelper.getKoodiNimi(model.getSuunniteltuKestoTyyppi(), locale);
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
