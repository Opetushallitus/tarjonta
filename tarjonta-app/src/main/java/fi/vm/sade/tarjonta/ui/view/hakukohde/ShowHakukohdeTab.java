package fi.vm.sade.tarjonta.ui.view.hakukohde;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import fi.vm.sade.tarjonta.service.types.LueKoulutusVastausTyyppi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.vaadin.data.Container;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.MonikielinenMetadataTyyppi;
import fi.vm.sade.tarjonta.ui.enums.CommonTranslationKeys;
import fi.vm.sade.tarjonta.ui.enums.MetaCategory;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import fi.vm.sade.tarjonta.ui.model.HakuaikaViewModel;
import fi.vm.sade.tarjonta.ui.model.HakukohdeLiiteViewModel;
import fi.vm.sade.tarjonta.ui.model.HakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusOidNameViewModel;
import fi.vm.sade.tarjonta.ui.model.LinkitettyTekstiModel;
import fi.vm.sade.tarjonta.ui.model.PainotettavaOppiaineViewModel;
import fi.vm.sade.tarjonta.ui.model.PisterajaRow;
import fi.vm.sade.tarjonta.ui.model.ValintakoeAikaViewModel;
import fi.vm.sade.tarjonta.ui.model.ValintakoeViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.CategoryTreeView;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.ShowHakukohdeValintakoeRow;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author : Tuomas Katva Date: 4/3/13
 * @author Timo Santasalo / Teknokala Ky
 */
@Configurable(preConstruction = true)
public class ShowHakukohdeTab extends VerticalLayout {

    private static final long serialVersionUID = 1L;
    private final String language;
    private final String datePattern = "dd.MM.yyyy HH:mm";
    @Autowired
    private TarjontaPresenter presenter;
    @Autowired
    private TarjontaUIHelper uiHelper;
    private I18NHelper i18n = new I18NHelper("ShowHakukohdeTab.");
    private CreationDialog<KoulutusOidNameViewModel> addlKoulutusDialog;
    private Window addlKoulutusDialogWindow;
    private boolean showPisterajaTable = true;

    @Value("${koodisto-uris.pohjakoulutusvaatimus_er}")
    private String pohjakoulutusVaatimusEr;

    public ShowHakukohdeTab(String language) {
        Preconditions.checkNotNull(language, "Language cannot be null");
        this.language = language;
        this.setMargin(true);
        buildPage(this);
    }

    private boolean isPersvako() {

        HakukohdeViewModel vm = presenter.getModel().getHakukohde();

        List<KoulutusasteTyyppi> persvaos = new ArrayList<KoulutusasteTyyppi>();
        persvaos.add(KoulutusasteTyyppi.VALMENTAVA_JA_KUNTOUTTAVA_OPETUS);
        //persvaos.add(KoulutusasteTyyppi.AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS);
        KoulutusasteTyyppi koulutusasteTyyppi = vm.getKoulutusasteTyyppi();

        if (persvaos.contains(koulutusasteTyyppi)) {
            return true;
        } else if (koulutusasteTyyppi.equals(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS)) {
            return checkErKomotos(loadHakukohdeKomotos(vm.getKomotoOids()));

        } else {
            return false;
        }

    }

    private boolean checkErKomotos(List<LueKoulutusVastausTyyppi> koulutukses) {

        for (LueKoulutusVastausTyyppi koulutus : koulutukses) {
            if (koulutus.getPohjakoulutusvaatimus().getUri().trim().equalsIgnoreCase(pohjakoulutusVaatimusEr)) {
                return true;
            }
        }

        return false;
    }

    private List<LueKoulutusVastausTyyppi> loadHakukohdeKomotos(List<String> komotoOids) {
        List<LueKoulutusVastausTyyppi> koulutukses = new ArrayList<LueKoulutusVastausTyyppi>();
        for (String komotoOid : komotoOids) {
            LueKoulutusVastausTyyppi koulutusVastausTyyppi = presenter.getKoulutusByOid(komotoOid);
            koulutukses.add(koulutusVastausTyyppi);

        }
        return koulutukses;
    }

    private void buildPage(VerticalLayout layout) {
        buildPerustiedotLayout(layout);
        addLayoutSplit(layout);
        buildValintakokeetLayout(layout);
        addLayoutSplit(layout);
        buildLiiteLayout(layout);
        addLayoutSplit(layout);
        buildKuvauksetLayout(layout);
        addLayoutSplit(layout);
        buildKoulutuksesLayout(layout);
    }

    private void buildKuvauksetLayout(VerticalLayout layout) {

        //tyypit joille valintaperustekuvaukset näytetään hakukohteessa...
        final List<KoulutusasteTyyppi> showValintaperusteetForTypes = Lists.newArrayList(KoulutusasteTyyppi.KORKEAKOULUTUS, KoulutusasteTyyppi.VALMENTAVA_JA_KUNTOUTTAVA_OPETUS);

        //tyypit joille sorakuvaukset näytetään hakukohteessa...
        final List<KoulutusasteTyyppi> showSoraForTypes = Lists.newArrayList(KoulutusasteTyyppi.KORKEAKOULUTUS);

        if (!showValintaperusteetForTypes.contains(presenter.getModel().getHakukohde().getKoulutusasteTyyppi())) {
            return;
        }

        //if (presenter.getModel().getHakukohde().getValintaPerusteidenKuvaus() != null && !presenter.getModel().getHakukohde().getValintaPerusteidenKuvaus().isEmpty()) {
        VerticalLayout valintaperusteetLayout = new VerticalLayout();
        valintaperusteetLayout.setMargin(true);
        final boolean canUserUpdateHakukohde = presenter.isHakukohdeEditableForCurrentUser();
        //if (checkForHaunAlkaminenAndType()) {
        valintaperusteetLayout.addComponent(buildHeaderLayout(this.i18n.getMessage("vapeSoraKuvauksetTitle"), i18n.getMessage(CommonTranslationKeys.MUOKKAA),
                new ClickListener() {
                    private static final long serialVersionUID = 5019806363620874205L;

                    @Override
                    public void buttonClick(ClickEvent clickEvent) {
                        getWindow().showNotification("Toiminnallisuutta ei ole viela toteuttettu");
                    }
                }, null, null, canUserUpdateHakukohde));
        //}

        final GridLayout grid = new GridLayout(2, 1);
        grid.setWidth("100%");
        grid.setMargin(true);

        addRichTextToGrid(grid, "valintaPerusteetTeksti", getLanguageString(presenter.getModel().getHakukohde().getValintaPerusteidenKuvaus(), MetaCategory.VALINTAPERUSTEKUVAUS));
        if (showSoraForTypes.contains(presenter.getModel().getHakukohde().getKoulutusasteTyyppi())) {
            addRichTextToGrid(grid, "soraKuvausTeksti", getLanguageString(presenter.getModel().getHakukohde().getSoraKuvaus(), MetaCategory.SORA_KUVAUS));
        }

        grid.setColumnExpandRatio(1, 1f);
        valintaperusteetLayout.addComponent(grid);
        layout.addComponent(valintaperusteetLayout);
        //}
    }

    private void buildLiiteLayout(VerticalLayout layout) {
        VerticalLayout liiteLayout = new VerticalLayout();
        liiteLayout.setMargin(true);
        final List<HakukohdeLiiteViewModel> loadHakukohdeLiitteet = presenter.loadHakukohdeLiitteet(false);

        Date lastUpdated = null;
        String lastupdateBy = null;

        //get the latest date
        for (HakukohdeLiiteViewModel liite : loadHakukohdeLiitteet) {
            if (lastUpdated == null || lastUpdated.before(liite.getViimeisinPaivitysPvm())) {
                lastUpdated = liite.getViimeisinPaivitysPvm();
                lastupdateBy = liite.getViimeisinPaivittaja();
            }
        }

        //if (checkForHaunAlkaminenAndType()) {
        liiteLayout.addComponent(buildHeaderLayout(this.i18n.getMessage("liitteetTitle"), i18n.getMessage(CommonTranslationKeys.MUOKKAA),
                new ClickListener() {
                    private static final long serialVersionUID = 5019806363620874205L;

                    @Override
                    public void buttonClick(ClickEvent clickEvent) {
                        presenter.showHakukohdeEditView(presenter.getModel().getHakukohde().getKomotoOids(),
                                presenter.getModel().getHakukohde().getOid(), null, TarjontaPresenter.LIITTEET_TAB_SELECT);
                    }
                }, lastUpdated, lastupdateBy, presenter.isHakukohdeEditableForCurrentUser()));
        //}

        final GridLayout grid = new GridLayout(2, 1);
        grid.setWidth("100%");
        grid.setMargin(true);
        for (HakukohdeLiiteViewModel liite : presenter.loadHakukohdeLiitteet(false)) {
            addTwoColumnRowToGrid(grid, getOphH2Label(uiHelper.getKoodiNimi(liite.getLiitteenTyyppi(), I18N.getLocale())));
            addTwoColumnRowToGrid(grid, getRichTxtLbl(getLanguageString(liite.getLiitteenSanallinenKuvaus())));
            addItemToGrid(grid, "liiteoimMennessaLbl", getLiiteAika(liite));
            addItemToGrid(grid, "liiteToimOsoiteLbl", getLiiteOsoite(liite));
            if (liite.getSahkoinenToimitusOsoite() != null) {
                Link liiteSahkToimOsoiteLink = new Link(liite.getSahkoinenToimitusOsoite(), new ExternalResource(liite.getSahkoinenToimitusOsoite()));
                liiteSahkToimOsoiteLink.setTargetName("_blank");
                addItemToGrid(grid, "sahkoinenToimOsoite", liiteSahkToimOsoiteLink);
            }
        }
        grid.setColumnExpandRatio(1, 1f);
        liiteLayout.addComponent(grid);
        liiteLayout.setComponentAlignment(grid, Alignment.TOP_LEFT);
        layout.addComponent(liiteLayout);
    }

    private String getLiiteAika(HakukohdeLiiteViewModel liiteViewModel) {
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
        return sdf.format(liiteViewModel.getToimitettavaMennessa());
    }

    private Label getOphH2Label(String caption) {
        Label label = new Label(caption);
        label.setStyleName(Oph.LABEL_H2);
        return label;
    }

    private void addTwoColumnRowToGrid(final GridLayout grid, final Component component) {
        if (grid != null) {
            //Hack to add two column row to table dynamically
            final HorizontalLayout hl = UiUtil.horizontalLayout(false,
                    UiMarginEnum.RIGHT);

            Label placeHolder = new Label("PLACEHOLDER");

            grid.addComponent(placeHolder);
            grid.removeComponent(placeHolder);
            final int y = grid.getCursorY();
            hl.addComponent(component);
            grid.addComponent(hl, 0, y, 1, y);
        }
    }

    private Label getHdrH2Label(String caption) {
        Label hdrLbl = new Label(i18n.getMessage(caption));
        hdrLbl.setStyleName(Oph.LABEL_H2);
        return hdrLbl;

    }

    private HorizontalLayout getRichTxtLbl(String labelMsg) {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setWidth(100, UNITS_PERCENTAGE);
        Label richTxtLbl = new Label(labelMsg);
        richTxtLbl.setContentMode(Label.CONTENT_XHTML);
        hl.addComponent(richTxtLbl);
        hl.setMargin(true, false, false, false);
        return hl;
    }

    private String getLiiteOsoite(HakukohdeLiiteViewModel liiteViewModel) {
        StringBuilder sb = new StringBuilder();

        sb.append(liiteViewModel.getOsoiteRivi1());
        sb.append(", ");
        sb.append(getKoodiarvo(liiteViewModel.getPostinumero()));
        if (liiteViewModel.getOsoiteRivi2() != null && liiteViewModel.getOsoiteRivi2().length() > 1) {
            sb.append(liiteViewModel.getOsoiteRivi2());
            sb.append(", ");
        }
        sb.append(", ");
        sb.append(uiHelper.getKoodiNimi(liiteViewModel.getPostinumero(), I18N.getLocale()));

        return sb.toString();
    }

    private String getHakukohdeLiiteOsoite(HakukohdeViewModel hakukohdeViewModel) {
        StringBuilder sb = new StringBuilder();

        sb.append(hakukohdeViewModel.getOsoiteRivi1());
        sb.append(", ");
        if (hakukohdeViewModel.getOsoiteRivi2() != null && hakukohdeViewModel.getOsoiteRivi2().length() > 1) {
            sb.append(hakukohdeViewModel.getOsoiteRivi2());
            sb.append(", ");
        }
        sb.append(getKoodiarvo(hakukohdeViewModel.getPostinumero()));
        sb.append(", ");
        sb.append(uiHelper.getKoodiNimi(hakukohdeViewModel.getPostinumero(), I18N.getLocale()));

        return sb.toString();
    }

    private String getKoodiarvo(String uri) {
        if (uri != null) {
            List<KoodiType> koodis = uiHelper.getKoodis(uri);
            if (koodis != null) {
                return koodis.get(0).getKoodiArvo();
            }
        }
        return null;
    }

    private void buildValintakokeetLayout(VerticalLayout layout) {

        VerticalLayout valintakoeLayout = new VerticalLayout();
        valintakoeLayout.setMargin(true);
        List<ValintakoeViewModel> loadHakukohdeValintaKokees = presenter.loadHakukohdeValintaKokees();
        Date lastUpdated = null;

        //get the latest date
        for (ValintakoeViewModel valintakoe : loadHakukohdeValintaKokees) {
            if (lastUpdated == null || lastUpdated.before(valintakoe.getViimeisinPaivitysPvm())) {
                lastUpdated = valintakoe.getViimeisinPaivitysPvm();
            }
        }
        //if (checkForHaunAlkaminenAndType()) {
        valintakoeLayout.addComponent(buildHeaderLayout(this.i18n.getMessage("valintakokeetTitle"), i18n.getMessage(CommonTranslationKeys.MUOKKAA),
                new ClickListener() {
                    private static final long serialVersionUID = 5019806363620874205L;

                    @Override
                    public void buttonClick(ClickEvent clickEvent) {
                        presenter.showHakukohdeEditView(presenter.getModel().getHakukohde().getKomotoOids(),
                                presenter.getModel().getHakukohde().getOid(), null, TarjontaPresenter.VALINTAKOE_TAB_SELECT);
                    }
                }, lastUpdated, presenter.getModel().getHakukohde().getViimeisinPaivittaja(), presenter.isHakukohdeEditableForCurrentUser()));
        //}

        VerticalLayout yetAnotherLayout = new VerticalLayout();
        yetAnotherLayout.setMargin(true);

        if (checkLukioKoulutus()) {
            Label pisterajaLbl = new Label(i18n.getMessage("valinnoissaKaytettavatPisterajatLbl"));
            pisterajaLbl.setStyleName(Oph.LABEL_H2);

            Table pisterajatTable = new Table();
            pisterajatTable.setContainerDataSource(createPisterajatContainer());
            pisterajatTable.setWidth(100, UNITS_PERCENTAGE);
            pisterajatTable.setVisibleColumns(new String[]{"pisteRajaTyyppi", "alinPistemaara", "ylinPistemaara", "alinHyvaksyttyPistemaara"});

            pisterajatTable.setColumnHeader("pisteRajaTyyppi", "");
            pisterajatTable.setColumnHeader("alinPistemaara", i18n.getMessage("alinPistemaaraLbl"));
            pisterajatTable.setColumnHeader("ylinPistemaara", i18n.getMessage("ylinPistemaaraLbl"));
            pisterajatTable.setColumnHeader("alinHyvaksyttyPistemaara", i18n.getMessage("alinHyvaksyttyPistemaaraLbl"));
            pisterajatTable.setPageLength(pisterajatTable.getContainerDataSource().size());
            if (showPisterajaTable) {
                yetAnotherLayout.addComponent(pisterajaLbl);
                yetAnotherLayout.addComponent(pisterajatTable);
            }
        }

        if (checkValintakoeAjat(loadHakukohdeValintaKokees)) {
            for (ValintakoeViewModel valintakoe : loadHakukohdeValintaKokees) {
                final GridLayout grid = new GridLayout(2, 1);
                grid.setWidth("100%");
                Label piesykoeLbl = new Label(i18n.getMessage("paasykoeTitle"));
                piesykoeLbl.setStyleName(Oph.LABEL_H2);
                addTwoColumnRowToGrid(grid, piesykoeLbl);

                addTwoColumnRowToGrid(grid, getOphH2Label(uiHelper.getKoodiNimi(valintakoe.getValintakoeTyyppi(), I18N.getLocale())));
                addTwoColumnRowToGrid(grid, getRichTxtLbl(getLanguageString(valintakoe.getSanallisetKuvaukset())));

                addTwoColumnRowToGrid(grid, buildValintakoeAikaTable(valintakoe));

                String lisanayttoKuvaus = getLanguageString(valintakoe.getLisanayttoKuvaukset());
                if (lisanayttoKuvaus != null && lisanayttoKuvaus.trim().length() > 0) {

                    VerticalLayout vl = new VerticalLayout();
                    vl.setMargin(true, false, false, false);
                    vl.addComponent(getHdrH2Label("lisanaytotLabel"));

                    vl.addComponent(getRichTxtLbl(lisanayttoKuvaus));
                    addTwoColumnRowToGrid(grid, vl);

                }
                grid.setColumnExpandRatio(1, 1f);
                grid.setMargin(true, false, false, false);
                yetAnotherLayout.addComponent(grid);

            }
        }

        valintakoeLayout.addComponent(yetAnotherLayout);
        layout.addComponent(valintakoeLayout);

    }

    private boolean checkValintakoeAjat(List<ValintakoeViewModel> hakukohdeValintaKokees) {
        boolean returnVal = true;
        if (hakukohdeValintaKokees != null) {

            for (ValintakoeViewModel valintakoeViewModel : hakukohdeValintaKokees) {
                if (valintakoeViewModel.getValintakoeAjat() != null && valintakoeViewModel.getValintakoeAjat().size() > 0) {
                    returnVal = true;
                } else {
                    returnVal = false;
                }
            }

        } else {
            returnVal = false;
        }
        return returnVal;
    }

    private Table buildValintakoeAikaTable(ValintakoeViewModel valintakoe) {
        Table valintakoeAikaTable = new Table();

        valintakoeAikaTable.setContainerDataSource(createBeanContainer(valintakoe.getValintakoeAjat()));
        valintakoeAikaTable.setWidth(100, UNITS_PERCENTAGE);
        valintakoeAikaTable.setVisibleColumns(new String[]{"valintakoeSijainti", "valintakoeAika", "valintakoeLisatiedot"});
        valintakoeAikaTable.setColumnHeader("valintakoeSijainti", this.i18n.getMessage("tableValintakoeAikaSijainti"));
        valintakoeAikaTable.setColumnHeader("valintakoeAika", this.i18n.getMessage("tableValintakoeAikaAjankohta"));
        valintakoeAikaTable.setColumnHeader("valintakoeLisatiedot", this.i18n.getMessage("tableValintakoeAikaLisatietoja"));
        valintakoeAikaTable.setColumnExpandRatio("valintakoeSijainti", 40);
        valintakoeAikaTable.setColumnExpandRatio("valintakoeAika", 30);
        valintakoeAikaTable.setColumnExpandRatio("valintakoeLisatiedot", 30);
        valintakoeAikaTable.setPageLength(valintakoe.getValintakoeAjat().size() + 1);

        return valintakoeAikaTable;
    }

    private BeanContainer<String, ShowHakukohdeValintakoeRow> createBeanContainer(List<ValintakoeAikaViewModel> valintaAikas) {
        BeanContainer<String, ShowHakukohdeValintakoeRow> container = new BeanContainer<String, ShowHakukohdeValintakoeRow>(ShowHakukohdeValintakoeRow.class);

        for (ValintakoeAikaViewModel valintakoeAika : valintaAikas) {
            ShowHakukohdeValintakoeRow row = new ShowHakukohdeValintakoeRow(valintakoeAika, this.language);
            container.addItem(new Integer(row.hashCode()).toString(), row);

        }

        return container;
    }

    private void buildKoulutuksesLayout(VerticalLayout layout) {

        VerticalLayout koulutuksesLayout = new VerticalLayout();
        koulutuksesLayout.setMargin(true);
        Label koulutuksetTitle = new Label(i18n.getMessage("koulutuksetTitle"));
        koulutuksetTitle.setStyleName(Oph.LABEL_H2);
        koulutuksesLayout.addComponent(koulutuksetTitle);

        CategoryTreeView categoryTree = new CategoryTreeView();
        categoryTree.setHeight("100px");
        categoryTree.setContainerDataSource(createHakukohdeKoulutusDatasource(presenter.getModel().getHakukohde().getKoulukses()));
        String[] visibleColumns = {"nimiBtn", "poistaBtn"};
        categoryTree.setVisibleColumns(visibleColumns);
        for (Object item : categoryTree.getItemIds()) {
            categoryTree.setChildrenAllowed(item, false);
        }
        koulutuksesLayout.addComponent(categoryTree);
        buildLiitaUusiKoulutusButton(koulutuksesLayout);
        layout.addComponent(koulutuksesLayout);
    }

    private void buildLiitaUusiKoulutusButton(VerticalLayout verticalLayout) {
        Button liitaUusiKoulutusBtn = UiBuilder.buttonSmallPrimary(null, i18n.getMessage("liitaUusiKoulutusPainike"));
        boolean hakuEditable = presenter.isHakukohdeEditableForCurrentUser();
        if (hakuEditable) {
            liitaUusiKoulutusBtn.addListener(new Button.ClickListener() {
                private static final long serialVersionUID = 5019806363620874205L;

                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    addlKoulutusDialog = presenter.createHakukohdeCreationDialogWithSelectedTarjoaja();
                    createButtonListenersForDialog();
                    addlKoulutusDialog.setWidth("700px");
                    addlKoulutusDialogWindow = new Window();
                    addlKoulutusDialogWindow.setContent(addlKoulutusDialog);
                    addlKoulutusDialogWindow.setModal(true);
                    addlKoulutusDialogWindow.center();
                    addlKoulutusDialogWindow.setCaption(i18n.getMessage("liitaUusiKoulutusDialogTitle"));
                    getWindow().addWindow(addlKoulutusDialogWindow);
                }
            });

            liitaUusiKoulutusBtn.setVisible(hakuEditable);
            verticalLayout.addComponent(liitaUusiKoulutusBtn);
        }
    }

    private void createButtonListenersForDialog() {
        if (addlKoulutusDialog != null) {
            addlKoulutusDialog.getPeruutaBtn().addListener(new Button.ClickListener() {
                private static final long serialVersionUID = 5019806363620874205L;

                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    if (addlKoulutusDialogWindow != null) {
                        getWindow().removeWindow(addlKoulutusDialogWindow);
                    }
                }
            });

            addlKoulutusDialog.getJatkaBtn().addListener(new Button.ClickListener() {
                private static final long serialVersionUID = 5019806363620874205L;

                @SuppressWarnings("unchecked")
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    Object values = addlKoulutusDialog.getOptionGroup().getValue();
                    Collection<KoulutusOidNameViewModel> selectedKoulutukses = null;
                    if (values instanceof Collection) {
                        selectedKoulutukses = (Collection<KoulutusOidNameViewModel>) values;
                    }
                    getWindow().removeWindow(addlKoulutusDialogWindow);
                    presenter.addKoulutuksesToHakukohde(selectedKoulutukses);
                }
            });
        }
    }

    private Container createPisterajatContainer() {
        BeanItemContainer<PisterajaRow> pisterajatContainer = new BeanItemContainer<PisterajaRow>(PisterajaRow.class);

        pisterajatContainer.addAll(mapPisterajaList(presenter.loadHakukohdeValintaKokees()));

        return pisterajatContainer;
    }

    private List<PisterajaRow> mapPisterajaList(List<ValintakoeViewModel> valintakokees) {
        List<PisterajaRow> pisterajaRows = new ArrayList<PisterajaRow>();

        for (ValintakoeViewModel valintakoeV : valintakokees) {
            if (valintakoeV.getPkAlinPM() != null && valintakoeV.getPkYlinPM() != null || valintakoeV.getLpAlinPM() != null && valintakoeV.getLpYlinPM() != null) {
                showPisterajaTable = true;
            } else {
                showPisterajaTable = false;
            }
            PisterajaRow paasyKoePisteraja = new PisterajaRow();
            paasyKoePisteraja.setPisteRajaTyyppi(i18n.getMessage("paasykoe"));
            paasyKoePisteraja.setAlinPistemaara(valintakoeV.getPkAlinPM());
            paasyKoePisteraja.setYlinPistemaara(valintakoeV.getPkYlinPM());
            paasyKoePisteraja.setAlinHyvaksyttyPistemaara(valintakoeV.getPkAlinHyvaksyttyPM());

            pisterajaRows.add(paasyKoePisteraja);

            PisterajaRow lisaNaytotRow = new PisterajaRow();
            lisaNaytotRow.setPisteRajaTyyppi(i18n.getMessage("lisanaytot"));
            lisaNaytotRow.setAlinPistemaara(valintakoeV.getLpAlinPM());
            lisaNaytotRow.setYlinPistemaara(valintakoeV.getLpYlinPM());
            lisaNaytotRow.setAlinHyvaksyttyPistemaara(valintakoeV.getLpAlinHyvaksyttyPM());

            pisterajaRows.add(lisaNaytotRow);

            PisterajaRow kokonaisPisteet = new PisterajaRow();
            kokonaisPisteet.setPisteRajaTyyppi(i18n.getMessage("kokonaispisteet"));
            kokonaisPisteet.setYlinPistemaara(i18n.getMessage("ylinPisteMaara"));
            kokonaisPisteet.setAlinHyvaksyttyPistemaara(valintakoeV.getKpAlinHyvaksyttyPM());

            pisterajaRows.add(kokonaisPisteet);

            pisterajaRows.add(lisaNaytotRow);
        }

        return pisterajaRows;
    }

    private Container createHakukohdeKoulutusDatasource(List<KoulutusOidNameViewModel> koulutukses) {
        BeanItemContainer<ShowHakukohdeKoulutusRow> container = new BeanItemContainer<ShowHakukohdeKoulutusRow>(ShowHakukohdeKoulutusRow.class);

        container.addAll(getRows(koulutukses));

        return container;
    }

    private List<ShowHakukohdeKoulutusRow> getRows(List<KoulutusOidNameViewModel> koulutukses) {
        List<ShowHakukohdeKoulutusRow> rows = new ArrayList<ShowHakukohdeKoulutusRow>();
        for (KoulutusOidNameViewModel koulutus : koulutukses) {
            ShowHakukohdeKoulutusRow row = new ShowHakukohdeKoulutusRow(koulutus, presenter.isHakukohdeEditableForCurrentUser());
            rows.add(row);
        }
        return rows;
    }

    private Label buildTallennettuLabel(Date date, String viimPaivOid) {
        SimpleDateFormat sdp = new SimpleDateFormat(datePattern);
        String viimPaivittaja = null;
        try {

            viimPaivittaja = uiHelper.tryGetViimPaivittaja(viimPaivOid);
        } catch (Exception ep) {

        }
        Label lastUpdLbl = null;
        if (viimPaivittaja != null) {
            lastUpdLbl = new Label("( " + i18n.getMessage("tallennettuLbl") + " " + sdp.format(date) + ", " + viimPaivittaja + " )");
        } else {
            lastUpdLbl = new Label("( " + i18n.getMessage("tallennettuLbl") + " " + sdp.format(date) + ")");
        }

        return lastUpdLbl;
    }

    private String getHakuaikaStr() {
        HakukohdeViewModel hm = presenter.getModel().getHakukohde();
        if (hm.getHakuaikaAlkuPvm() != null && hm.getHakuaikaLoppuPvm() != null) {
            return HakuaikaViewModel.toString(hm.getHakuaikaAlkuPvm(), hm.getHakuaikaLoppuPvm());
        }
        HakuaikaViewModel hvm = hm.getHakuaika();
        return hvm == null ? HakuaikaViewModel.toString(hm.getHakuaikaAlkuPvm(), hm.getHakuaikaLoppuPvm()) : hvm.toString();
    }

    private void buildPerustiedotLayout(VerticalLayout layout) {
        VerticalLayout hdrLayout = new VerticalLayout();
        hdrLayout.setMargin(true);
        //if (checkForHaunAlkaminenAndType()) {
        hdrLayout.addComponent(buildHeaderLayout(this.i18n.getMessage("perustiedot"), i18n.getMessage(CommonTranslationKeys.MUOKKAA),
                new ClickListener() {
                    private static final long serialVersionUID = 5019806363620874205L;

                    @Override
                    public void buttonClick(ClickEvent clickEvent) {
                        presenter.showHakukohdeEditView(presenter.getModel().getHakukohde().getKomotoOids(),
                                presenter.getModel().getHakukohde().getOid(), null, null);
                    }
                }, presenter.getModel().getHakukohde().getViimeisinPaivitysPvm(), presenter.getModel().getHakukohde().getViimeisinPaivittaja(), presenter.isHakukohdeEditableForCurrentUser()));
        //}
        final GridLayout grid = new GridLayout(2, 1);
        grid.setWidth("100%");
        grid.setMargin(true);
        KoulutusasteTyyppi kTyyppi = presenter.getModel().getHakukohde().getKoulutusasteTyyppi();
        String hakukohdeNimiStr = kTyyppi.equals(KoulutusasteTyyppi.VAPAAN_SIVISTYSTYON_KOULUTUS) ? presenter.getModel().getHakukohde().getEditedHakukohdeNimi() : uiHelper.getKoodiNimi(presenter.getModel().getHakukohde().getHakukohdeNimi(), null);
        addItemToGrid(grid, "hakukohdeNimi", hakukohdeNimiStr);
        String yhTunnus = kTyyppi.equals(KoulutusasteTyyppi.VAPAAN_SIVISTYSTYON_KOULUTUS) ? "-" : uiHelper.getKoodis(presenter.getModel().getHakukohde().getHakukohdeNimi()).get(0).getKoodiArvo();
        addItemToGrid(grid, "yhteishaunKoulutustunnus", yhTunnus);
        addItemToGrid(grid, "haku", tryGetLocalizedHakuNimi(presenter.getModel().getHakukohde().getHakuViewModel()));
        addItemToGrid(grid, "hakuaika", getHakuaikaStr());
        addItemToGrid(grid, "hakijoilleIlmoitetutAloituspaikat", new Integer(presenter.getModel().getHakukohde().getAloitusPaikat()).toString());
        addItemToGrid(grid, "valinnoissaKaytettavatAloituspaikat", new Integer(presenter.getModel().getHakukohde().getValinnoissaKaytettavatPaikat()).toString());
        if (kTyyppi.equals(KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS)) {
            addItemToGrid(grid, "kaksoistutkino", presenter.getModel().getHakukohde().isKaksoisTutkinto() ? this.i18n.getMessage("kylla") : i18n.getMessage("ei"));
        }

        if (checkLukioKoulutus()) {

            addItemToGrid(grid, "alinHyvaksyttyvaKeskiarvo", presenter.getModel().getHakukohde().getAlinHyvaksyttavaKeskiarvo());
            addItemToGrid(grid, "painotettavatOppiaineet", getHakukohdeOppiaineet());

        }
        //addRichTextToGrid(grid, "hakukelpoisuusVaatimukset", getLanguageString(presenter.getModel().getHakukohde().getValintaPerusteidenKuvaus()));
        addRichTextToGrid(grid, "lisatietojaHakemisesta", getLanguageString(presenter.getModel().getHakukohde().getLisatiedot()));
        if (!isHakukohdeNivelvaihe() && !isPersvako()) {
            SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
            addItemToGrid(grid, "liitteetToimMennessa", presenter.getModel().getHakukohde().getLiitteidenToimitusPvm() == null ? null
                    : sdf.format(presenter.getModel().getHakukohde().getLiitteidenToimitusPvm()));
            addItemToGrid(grid, "liitteidenToimitusOsoite", getHakukohdeLiiteOsoite(presenter.getModel().getHakukohde()));
            if (presenter.getModel().getHakukohde().getLiitteidenSahkoinenToimitusOsoite() != null && presenter.getModel().getHakukohde().getLiitteidenSahkoinenToimitusOsoite().trim().length() > 0) {
                Link sahkoinenToimOsoiteLink = new Link(presenter.getModel().getHakukohde().getLiitteidenSahkoinenToimitusOsoite(), new ExternalResource(presenter.getModel().getHakukohde().getLiitteidenSahkoinenToimitusOsoite()));
                sahkoinenToimOsoiteLink.setTargetName("_blank");
                addItemToGrid(grid, "sahkoinenToimOsoite", sahkoinenToimOsoiteLink);
            }
        }
        //grid.setColumnExpandRatio(0,0.2f);
        grid.setColumnExpandRatio(1, 1f);

        hdrLayout.addComponent(grid);
        hdrLayout.setComponentAlignment(grid, Alignment.TOP_LEFT);
        layout.addComponent(hdrLayout);

    }

    private boolean isHakukohdeNivelvaihe() {
        KoulutusasteTyyppi kTyyppi = presenter.getModel().getHakukohde().getKoulutusasteTyyppi();
        return kTyyppi.equals(KoulutusasteTyyppi.AMM_OHJAAVA_JA_VALMISTAVA_KOULUTUS)
                || kTyyppi.equals(KoulutusasteTyyppi.MAAHANM_AMM_VALMISTAVA_KOULUTUS)
                || kTyyppi.equals(KoulutusasteTyyppi.MAAHANM_LUKIO_VALMISTAVA_KOULUTUS)
                || kTyyppi.equals(KoulutusasteTyyppi.PERUSOPETUKSEN_LISAOPETUS)
                || kTyyppi.equals(KoulutusasteTyyppi.VAPAAN_SIVISTYSTYON_KOULUTUS);
    }

    private String getHakukohdeOppiaineet() {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (PainotettavaOppiaineViewModel painotettavaOppiaine : presenter.getModel().getHakukohde().getPainotettavat()) {
            if (painotettavaOppiaine.getOppiaine() != null && painotettavaOppiaine.getOppiaine().trim().length() > 0) {
                if (!isFirst) {
                    sb.append(", ");
                }
                sb.append(uiHelper.getKoodiNimi(painotettavaOppiaine.getOppiaine()));
                sb.append("( ");
                sb.append(painotettavaOppiaine.getPainokerroin());
                sb.append(" )");
                isFirst = false;
            }
        }
        return sb.toString();
    }

    private boolean checkLukioKoulutus() {
        boolean returnVal = false;

        for (KoulutusOidNameViewModel koulutus : presenter.getModel().getHakukohde().getKoulukses()) {
            if (koulutus.getKoulutustyyppi().equals(KoulutusasteTyyppi.LUKIOKOULUTUS)) {
                returnVal = true;
            }
        }

        return returnVal;
    }

    private void addItemToGrid(final GridLayout grid,
            final String labelCaptionKey, final Object labelCaptionValue) {
        addItemToGrid(grid, labelCaptionKey, new Label(labelCaptionValue == null ? null : labelCaptionValue.toString()));
    }

    private void addRichTextToGrid(final GridLayout grid,
            final String labelCaptionKey, final Object labelCaptionValue) {

        Label lbl = new Label(labelCaptionValue == null ? null : labelCaptionValue.toString());
        lbl.setContentMode(Label.CONTENT_XHTML);

        addItemToGrid(grid, labelCaptionKey, lbl);
    }

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

    private void addKoodiHeaderToGrid(final GridLayout grid, final String label) {
        if (grid != null) {
            final HorizontalLayout hl = UiUtil.horizontalLayout(false,
                    UiMarginEnum.RIGHT);
            hl.setSizeUndefined();
            Label hdrLbl = UiUtil.label(null, label);
            hdrLbl.setStyleName(Oph.LABEL_H2);
            hl.addComponent(hdrLbl);
            grid.addComponent(hl);

            grid.setComponentAlignment(hl, Alignment.TOP_RIGHT);

            grid.newLine();
        }
    }

    private void addHeaderToGrid(final GridLayout grid, final String label) {
        if (grid != null) {
            final HorizontalLayout hl = UiUtil.horizontalLayout(false,
                    UiMarginEnum.RIGHT);
            hl.setSizeUndefined();
            Label hdrLbl = UiUtil.label(null, this.i18n.getMessage(label));
            hdrLbl.setStyleName(Oph.LABEL_H2);
            hl.addComponent(hdrLbl);
            grid.addComponent(hl);

            grid.setComponentAlignment(hl, Alignment.TOP_RIGHT);

            grid.newLine();
        }
    }

    private String getLanguageString(LinkitettyTekstiModel teksti, MetaCategory mc) {
        if (teksti.getUri() == null) {
            return getLanguageString(teksti.getKaannokset());
        } else {
            for (MonikielinenMetadataTyyppi mt : presenter.haeMetadata(teksti.getUri(), mc.toString())) {
                if (mt.getKieli().equals(language)) {
                    return mt.getArvo();
                }
            }
            return "-"; // TODO pitäiskö näyttää "ei kuvausta tällä kielellä tjsp.."
        }
    }

    private String getLanguageString(List<KielikaannosViewModel> tekstit) {

        for (KielikaannosViewModel teksti : tekstit) {
            if (teksti.getKielikoodi().trim().equalsIgnoreCase(this.language)) {
                return teksti.getNimi();
            }
        }
        return "";
    }

    private void addLayoutSplit(final AbstractLayout parent) {
        final VerticalSplitPanel split = new VerticalSplitPanel();
        split.setImmediate(false);
        split.setWidth("100%");
        split.setHeight("2px");
        split.setLocked(true);
        parent.addComponent(split);
    }

    private HorizontalLayout buildHeaderLayout(String title, String btnCaption, Button.ClickListener listener, Date lastUpdatedLabel, String lastUpdateBy, boolean showButton) {
        HorizontalLayout headerLayout = UiUtil.horizontalLayout(true, UiMarginEnum.NONE);
        final Label titleLabel = UiUtil.label(headerLayout, title);
        titleLabel.setStyleName(Oph.LABEL_H2);

        final Label buildTallennettuLabel = lastUpdatedLabel != null ? buildTallennettuLabel(lastUpdatedLabel, lastUpdateBy) : null;

        if (btnCaption != null) {
            headerLayout.addComponent(titleLabel);
            if (buildTallennettuLabel != null) {
                headerLayout.addComponent(buildTallennettuLabel);
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

            headerLayout.setComponentAlignment(btn, Alignment.TOP_RIGHT);
            if (buildTallennettuLabel != null) {
                headerLayout.setComponentAlignment(buildTallennettuLabel, Alignment.TOP_CENTER);
            }
        }
        return headerLayout;
    }

    private String tryGetLocalizedHakuNimi(HakuViewModel hakuViewModel) {
        Preconditions.checkNotNull(hakuViewModel, "HakuviewModel cannot be null");
        Preconditions.checkNotNull(this.language, "Language cannot be null");
        return TarjontaUIHelper.getClosestHakuName(I18N.getLocale(), hakuViewModel);
    }
}
