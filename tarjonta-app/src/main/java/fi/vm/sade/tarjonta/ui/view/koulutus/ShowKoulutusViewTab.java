package fi.vm.sade.tarjonta.ui.view.koulutus;

import static fi.vm.sade.tarjonta.ui.view.common.FormGridBuilder.FieldInfo.text;
import static fi.vm.sade.tarjonta.ui.view.common.FormGridBuilder.FieldInfo.xhtml;

import java.text.SimpleDateFormat;
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
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;

import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.shared.auth.OrganisaatioContext;
import fi.vm.sade.tarjonta.ui.enums.CommonTranslationKeys;
import fi.vm.sade.tarjonta.ui.enums.KoulutusActiveTab;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.SimpleHakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutuskoodiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.MonikielinenTekstiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusLisatiedotModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusLisatietoModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusToisenAsteenPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.CategoryTreeView;
import fi.vm.sade.tarjonta.ui.view.common.FormGridBuilder;
import fi.vm.sade.tarjonta.ui.view.common.FormGridBuilder.FieldInfo;
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
//    private final LueKoulutusVastausTyyppi koulutus;
    private final String datePattern = "dd.MM.yyyy HH:mm";

    /**
     *
     * @param language is used to match monikielinen teksi
     * @param locale is used for koodi values
     *
     */
    public ShowKoulutusViewTab(final String language, final Locale locale) {
        Preconditions.checkNotNull(language, "Language cannot be null");
        Preconditions.checkNotNull(presenter.getModel().getTarjoajaModel(), "Tarjoaja cannot be null");
        Preconditions.checkNotNull(presenter.getModel().getTarjoajaModel().getSelectedOrganisationOid(), "Tarjoaja organisaatioOid cannot be null");
        this.language = language;
        this.locale = getKoodistoLocale(locale);
        this.context = OrganisaatioContext.getContext(presenter.getModel().getTarjoajaModel().getSelectedOrganisationOid());
        final VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setMargin(true);
        setCompositionRoot(layout);
        build(layout);
    }

    /**
     * Return locale based on submitted locale
     *
     * @return
     */
    private Locale getKoodistoLocale(final Locale locale) {
        if (allowedKoodistoLocales.contains(locale.getLanguage().toLowerCase())) {
            return locale;
        }
        return new Locale("fi");
    }

    private void insertLayoutSplit(final FormGridBuilder layout) {
        final VerticalSplitPanel split = new VerticalSplitPanel();
        split.setImmediate(false);
        split.setWidth("100%");
        split.setHeight("2px");
        split.setLocked(true);
        layout.addHeader(split);
    }

    private void build(final VerticalLayout parent) {
        FormGridBuilder layout = new FormGridBuilder();
        layout.setWidth("100%");
        parent.addComponent(layout);
        insertKoulutuksenmPerustiedot(layout);
        insertLayoutSplit(layout);
        insertKoulutuksenKuvailevatTiedot(layout);
        insertLayoutSplit(layout);
        insertKoulutuksenHakukohteet(layout);
    }

    private AbstractComponent buildHeaderLayout(String title, String btnCaption, Button.ClickListener listener, boolean buttonVisible, boolean showTime) {
        VerticalLayout vl = new VerticalLayout();
        final GridLayout grid = new GridLayout(showTime ? 3 : 2, 1);
        grid.setWidth("100%");
        vl.addComponent(grid);
        vl.setWidth("100%");

        final Label titleLabel = UiUtil.label(grid, title);
        titleLabel.setStyleName(Oph.LABEL_H2);

        final Date modifiedDate = presenter.getModel().getKoulutusPerustiedotModel().getViimeisinPaivitysPvm();

        if (showTime) {
            final Label modifiedLabel = UiUtil.label(grid, i18n.getMessage("tallennettuLabel", uiHelper.formatDate(modifiedDate), uiHelper.formatTime(modifiedDate)));
            modifiedLabel.setStyleName(Oph.LABEL_SMALL);
        }

        if (btnCaption != null) {
            Button button = UiBuilder.buttonSmallPrimary(grid, btnCaption, listener);
            button.setVisible(buttonVisible);

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
            grid.setComponentAlignment(button, Alignment.TOP_RIGHT);

        }
        return vl;
    }

    private HorizontalLayout buildHeaderLayout(String title, String btnCaption, Button.ClickListener listener, Label lastUpdatedLabel, boolean showButton) {
        HorizontalLayout headerLayout = UiUtil.horizontalLayout(true, UiMarginEnum.NONE);
        Label titleLabel = UiUtil.label(headerLayout, title);
        titleLabel.setStyleName(Oph.LABEL_H2);

        if (btnCaption != null) {
            headerLayout.addComponent(titleLabel);
            if (lastUpdatedLabel != null) {
                headerLayout.addComponent(lastUpdatedLabel);
            }

            Button btn = UiBuilder.buttonSmallPrimary(headerLayout, btnCaption, listener);
            btn.setVisible(showButton);

            // Add default click listener so that we can show that action has not been implemented as of yet
            if (listener == null) {
                btn.addListener(new Button.ClickListener() {
                    private static final long serialVersionUID = 5019806363620874205L;

                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        getWindow().showNotification("Toiminnallisuutta ei vielä toteutettu");
                    }
                });
            }

            //headerLayout.setExpandRatio(btn, 1f);
            headerLayout.setComponentAlignment(btn, Alignment.TOP_RIGHT);
            if (lastUpdatedLabel != null) {
                headerLayout.setComponentAlignment(lastUpdatedLabel, Alignment.TOP_CENTER);
            }
        }
        return headerLayout;
    }

    private void insertKoulutuksenHakukohteet(FormGridBuilder layout) {
        int numberOfApplicationTargets = presenter.getModel().getKoulutusPerustiedotModel().getKoulutuksenHakukohteet().size();

        layout.addHeader(buildHeaderLayout(i18n.getMessage("hakukohteet", numberOfApplicationTargets), i18n.getMessage("luoUusiHakukohdeBtn"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(ClickEvent event) {
                List<String> koulutus = new ArrayList<String>();
                koulutus.add(presenter.getModel().getKoulutusPerustiedotModel().getOid());
                presenter.showHakukohdeEditView(koulutus, null, null, null);
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
        layout.addHeader(categoryTree);
        layout.newLine();
    }

    /**
     * Localized descriptive data about the koulutus.
     *
     * @param layout
     */
    private void insertKoulutuksenKuvailevatTiedot(final FormGridBuilder layout) {
        Preconditions.checkNotNull(layout, "Layout cannot be null");
        final KoulutusLisatiedotModel lisatiedotModel = presenter.getModel()
                .getKoulutusLisatiedotModel();

        final boolean updatePermission = presenter.getPermission().userCanUpdateKoulutus(context);
        
        
        layout.addHeader(buildHeaderLayout(
                i18n.getMessage("kuvailevatTiedot"),
                i18n.getMessage(CommonTranslationKeys.MUOKKAA),
                new Button.ClickListener() {
                    private static final long serialVersionUID = 5019806363620874205L;

                    @Override
                    public void buttonClick(ClickEvent event) {
                        presenter.getTarjoaja().setSelectedResultRowOrganisationOid(null);
                        presenter.showKoulutustEditView(getEditViewOid(),
                                KoulutusActiveTab.LISATIEDOT);
                    }
                }, updatePermission, false));

        final KoulutusLisatietoModel lisatietoForLang = lisatiedotModel
                .getLisatiedot().get(language);

        final KoulutusToisenAsteenPerustiedotViewModel model = presenter.getModel().getKoulutusPerustiedotModel();

        layout.add(getXhtmlRow("tutkinnonKoulutuksellisetJaAmmatillisetTavoitteet", getText(language, model.getKoulutuskoodiModel().getTavoitteet())));
        layout.add(getXhtmlRow("koulutuksenKoulutuksellisetJaAmmatillisetTavoitteet", getText(language, model.getKoulutusohjelmaModel().getTavoitteet())));
        layout.addSpace();
        layout.add(getXhtmlRow("koulutusohjelmanValinta", lisatietoForLang == null ? null : lisatietoForLang.getKoulutusohjelmanValinta()));
        layout.add(getXhtmlRow("koulutuksenSisalto", lisatietoForLang == null ? null : lisatietoForLang.getSisalto()));
        layout.add(getXhtmlRow("koulutuksenRakenne", getText(language, model.getKoulutuskoodiModel().getKoulutuksenRakenne())));
        layout.add(getXhtmlRow("tutkinnonSijoittuminenTyoelamaan", lisatietoForLang == null ? null : lisatietoForLang.getSijoittuminenTyoelamaan()));
        layout.add(getXhtmlRow("tutkinnonKansainvalistyminen", lisatietoForLang == null ? null : lisatietoForLang.getKansainvalistyminen()));
        layout.add(getXhtmlRow("tutkinnonYhteistyoMuidenKanssa", lisatietoForLang == null ? null : lisatietoForLang.getYhteistyoMuidenToimijoidenKanssa()));
        layout.add(getXhtmlRow("ammattinimikkeet", uiHelper.getKoodiNimi(presenter.getModel().getKoulutusLisatiedotModel().getAmmattinimikkeet(), locale)));
        layout.add(getXhtmlRow("jatkoOpintomahdollisuudet", getText(language, model.getKoulutuskoodiModel().getJatkoopintomahdollisuudet())));
    }

    /**
     * Return localized (uses koodistoLocale) text (if available)
     */
    private String getText(final String tabLanguage, final MonikielinenTekstiModel text) {
        if (tabLanguage != null && text != null) {

            //1st search text by tab language:
            //If no result use the default language (text is in user language or fallback language).
            final String inTabLanguage = text.getTextByLangCode(TarjontaKoodistoHelper.getKoodiURIFromVersionedUri(tabLanguage));
            return inTabLanguage == null ? text.getNimi() : inTabLanguage;
        }
        return "";
    }



    private Label buildTallennettuLabel(Date date, String lastUpdatedBy) {
        SimpleDateFormat sdp = new SimpleDateFormat(datePattern);
        Label lastUpdLbl = null;
        if (lastUpdatedBy != null) {
            String lastUpdater = null;
            try {
                lastUpdater = uiHelper.tryGetViimPaivittaja(lastUpdatedBy);
            } catch (Exception exp) {

            }
            if (lastUpdater != null) {
                lastUpdLbl = new Label("( " + i18n.getMessage("tallennettuLbl") + " " + sdp.format(date) + ", " + lastUpdater + " )");
            } else {
                lastUpdLbl = new Label("( " + i18n.getMessage("tallennettuLbl") + " " + sdp.format(date) + " )");
            }

        } else {
            lastUpdLbl = new Label("( " + i18n.getMessage("tallennettuLbl") + " " + sdp.format(date) + " )");
        }
        return lastUpdLbl;
    }

    private void insertKoulutuksenmPerustiedot(FormGridBuilder layout) {

        Preconditions.checkNotNull(presenter, "presenter cannot be null");
        Preconditions.checkNotNull(presenter.getModel(), "model cannot be null");
        Preconditions.checkNotNull(presenter.getModel().getKoulutusPerustiedotModel(), "koulutusperustiedot model cannot be null");
        final KoulutusToisenAsteenPerustiedotViewModel model = presenter.getModel().getKoulutusPerustiedotModel();

        Label lastUpdDateLbl = null;
        if (model.getViimeisinPaivitysPvm() != null) {
            lastUpdDateLbl = buildTallennettuLabel(model.getViimeisinPaivitysPvm(), model.getViimeisinPaivittajaOid());
        }

        layout.addHeader(buildHeaderLayout(i18n.getMessage("perustiedot"), i18n.getMessage(CommonTranslationKeys.MUOKKAA), new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                presenter.getTarjoaja().setSelectedResultRowOrganisationOid(null);
                presenter.showKoulutustEditView(getEditViewOid(), KoulutusActiveTab.PERUSTIEDOT);
            }
        }, lastUpdDateLbl, presenter.getPermission().userCanUpdateKoulutus(context)));

        final KoulutuskoodiModel koodiModel = model.getKoulutuskoodiModel();
        final KoodiModel koulutusala = koodiModel.getKoulutusala();
        final KoodiModel tutkintonimike = model.getKoulutusohjelmaModel().getTutkintonimike();
        final KoodiModel opintoala = koodiModel.getOpintoala();
        String opintojenLaajuusTot = presenter.getModel().getKoulutusPerustiedotModel().getOpintojenLaajuusTot();
        final String opintojenLaajuusArvo = (opintojenLaajuusTot != null && !opintojenLaajuusTot.isEmpty()) ? opintojenLaajuusTot : koodiModel.getOpintojenLaajuus().getKoodi();
        String opintojenLaajYksTot = presenter.getModel().getKoulutusPerustiedotModel().getOpintojenLaajuusyksikkoTot();        
        final String laajuusyksikkoStr = (opintojenLaajYksTot != null && !opintojenLaajYksTot.isEmpty()) ?  uiHelper.getKoodiLyhytNimi(opintojenLaajYksTot, locale) : koodiModel.getOpintojenLaajuusyksikko().getNimi();
        final KoodiModel koulutusaste = koodiModel.getKoulutusaste();

        //TODO get org name for current language?
        layout.add(getTextRow("organisaatio", presenter.getTarjoaja().getSelectedOrganisation().getOrganisationName()));
        layout.add(getTextRow("koulutusTutkinto", uiHelper.getKoodiNimi(presenter.getModel().getKoulutusPerustiedotModel().getKoulutuskoodiModel().getKoodistoUri(), locale)));

        if (presenter.getModel().getKoulutusPerustiedotModel().getNimi() != null) {
            // show nimi if available
            layout.add(getTextRow("koulutusohjelma", presenter.getModel().getKoulutusPerustiedotModel().getNimi()));
        } else {
            //else show koodi
            layout.add(getTextRow("koulutusohjelma", uiHelper.getKoodiNimi(presenter.getModel().getKoulutusPerustiedotModel().getKoulutusohjelmaModel().getKoodistoUri(), locale)));
        }
        layout.addSpace();

        if (koulutusaste != null) {
            layout.add(getTextRow("koulutusaste", uiHelper.getKoodiNimi(koulutusaste.getKoodistoUri(), locale)));
        }
        layout.add(getTextRow("koulutusala", uiHelper.getKoodiNimi(koulutusala.getKoodistoUri(), locale)));
        layout.add(getTextRow("opintoala", uiHelper.getKoodiNimi(opintoala.getKoodistoUri(), locale)));
        if (tutkintonimike != null) {
            layout.add(getTextRow("tutkintonimike", uiHelper.getKoodiNimi(tutkintonimike.getKoodistoUri(), locale)));
        }
        layout.addSpace();

        if (opintojenLaajuusArvo != null && laajuusyksikkoStr != null) {
            layout.add(getTextRow("opintojenLaajuus", opintojenLaajuusArvo + " " +laajuusyksikkoStr));
        } else {
            layout.add(getTextRow("opintojenLaajuus", laajuusyksikkoStr));
        }

        layout.add(getTextRow("koulutuslaji", uiHelper.getKoodiNimi(presenter.getModel().getKoulutusPerustiedotModel().getKoulutuslaji(), locale)));
        layout.add(getTextRow("pohjakoulutusvaatimus", uiHelper.getKoodiNimi(presenter.getModel().getKoulutusPerustiedotModel().getPohjakoulutusvaatimus(), locale)));
        layout.add(getTextRow("koulutuksenAlkamisPvm", uiHelper.formatDate(model.getKoulutuksenAlkamisPvm())));
        layout.add(getTextRow("suunniteltuKesto", getSuunniteltuKesto(model)));
        layout.add(getTextRow("opetuskieli", uiHelper.getKoodiNimi(model.getOpetuskieli(), locale)));
        layout.add(getTextRow("opetusmuoto", uiHelper.getKoodiNimi(model.getOpetusmuoto(), locale)));
        layout.add(getTextRow("linkkiOpetussuunnitelmaan", getLinkkiOpetussuunnitelmaan()));
        layout.add(getTextRow("koulutuksenYhteyshenkilo", getYhteyshenkilo()));
    }

    private FieldInfo getTextRow(String label, String content) {
        return text(i18n.getMessage(label), content);
    }

    private FieldInfo getXhtmlRow(String label, String content) {
        return xhtml(i18n.getMessage(label), content);
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
        KoulutusToisenAsteenPerustiedotViewModel koulutus = presenter.getModel().getKoulutusPerustiedotModel();
        return getNonNull(koulutus.getYhtHenkKokoNimi()) + " " + getNonNull(koulutus.getYhtHenkTitteli()) + ", " + getNonNull(koulutus.getYhtHenkPuhelin()) + ", " + getNonNull(koulutus.getYhtHenkEmail());
    }

    private String getNonNull(String arvo) {
        return arvo != null ? arvo : "";
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
