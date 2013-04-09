package fi.vm.sade.tarjonta.ui.view.hakukohde;

import com.google.common.base.Preconditions;
import com.vaadin.data.Container;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.*;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.tarjonta.ui.enums.CommonTranslationKeys;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.*;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.service.OrganisaatioContext;
import fi.vm.sade.tarjonta.ui.view.common.CategoryTreeView;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.ShowHakukohdeValintakoeRow;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.util.UiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : Tuomas Katva
 * Date: 4/3/13
 */

@Configurable(preConstruction = true)
public class ShowHakukohdeTab extends CustomComponent {

    @Autowired
    private TarjontaPresenter presenter;

    @Autowired
    TarjontaUIHelper uiHelper;

    private I18NHelper i18n = new I18NHelper("ShowHakukohdeTab.");

    private final String language;

    private final OrganisaatioContext context;

    private final String datePattern = "dd.MM.yyyy HH:mm";

    public ShowHakukohdeTab(String language) {
          Preconditions.checkNotNull(language,"Language cannot be null");
          this.language = language;
        this.context = OrganisaatioContext.getContext(presenter.getTarjoaja().getSelectedOrganisationOid());
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setMargin(true);
        setCompositionRoot(mainLayout);
        buildPage(mainLayout);
    }

    private void buildPage(VerticalLayout layout) {
         buildPerustiedotLayout(layout);
         addLayoutSplit(layout);
         buildValintakokeetLayout(layout);
         addLayoutSplit(layout);
         buildLiiteLayout(layout);
         addLayoutSplit(layout);
         buildKoulutuksesLayout(layout);
    }

    private void buildLiiteLayout(VerticalLayout layout) {
        VerticalLayout liiteLayout = new VerticalLayout();
        liiteLayout.setMargin(true);

        liiteLayout.addComponent(buildHeaderLayout(this.i18n.getMessage("liitteetTitle"),i18n.getMessage(CommonTranslationKeys.MUOKKAA),
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {

                    }
                }
                ,presenter.getPermission().userCanUpdateHakukohde(context)));

        final GridLayout grid = new GridLayout(2, 1);
        grid.setWidth("100%");
        grid.setMargin(true);
        for (HakukohdeLiiteViewModel liite: presenter.loadHakukohdeLiitteet()) {


           addHeaderToGrid(grid,"LiiteHdr");
           addItemToGrid(grid,"liiteoimMennessaLbl",getLiiteAika(liite));
           addItemToGrid(grid,"liiteToimOsoiteLbl",getLiiteOsoite(liite));
           Link liiteSahkToimOsoiteLink = new Link(liite.getSahkoinenToimitusOsoite(),new ExternalResource(liite.getSahkoinenToimitusOsoite()));
           liiteSahkToimOsoiteLink.setTargetName("_blank");
           addItemToGrid(grid, "sahkoinenToimOsoite", liiteSahkToimOsoiteLink);

           addRichTextToGrid(grid,"liiteKuvaus",getLanguageString(liite.getLiitteenSanallinenKuvaus()));
        }
        grid.setColumnExpandRatio(1,1f);
        liiteLayout.addComponent(grid);
        liiteLayout.setComponentAlignment(grid, Alignment.TOP_LEFT);
        layout.addComponent(liiteLayout);
    }

    private String getLiiteAika(HakukohdeLiiteViewModel liiteViewModel) {
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
        return sdf.format(liiteViewModel.getToimitettavaMennessa());
    }

    private String getLiiteOsoite(HakukohdeLiiteViewModel liiteViewModel) {
        StringBuilder sb = new StringBuilder();

        sb.append(liiteViewModel.getOsoiteRivi1());
        sb.append(", ");
        sb.append(getKoodiarvo(liiteViewModel.getPostinumero()));

        sb.append(", ");
        sb.append(uiHelper.getKoodiNimi(liiteViewModel.getPostinumero(), I18N.getLocale()));

        return sb.toString();
    }

    private String getHakukohdeLiiteOsoite(HakukohdeViewModel hakukohdeViewModel) {
        StringBuilder sb = new StringBuilder();

        sb.append(hakukohdeViewModel.getOsoiteRivi1());
        sb.append(", ");
        sb.append(getKoodiarvo(hakukohdeViewModel.getPostinumero()));
        sb.append(", ");
        sb.append(uiHelper.getKoodiNimi(hakukohdeViewModel.getPostinumero(),I18N.getLocale()));

        return sb.toString();
    }

    private String getKoodiarvo(String uri) {
        List<KoodiType> koodis = uiHelper.getKoodis(uri);
        if (koodis != null) {
            return koodis.get(0).getKoodiArvo();
        }
        return null;
    }

    private void buildValintakokeetLayout(VerticalLayout layout) {

        VerticalLayout valintakoeLayout = new VerticalLayout();
        valintakoeLayout.setMargin(true);

        valintakoeLayout.addComponent(buildHeaderLayout(this.i18n.getMessage("valintakokeetTitle"), i18n.getMessage(CommonTranslationKeys.MUOKKAA),
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {

                    }
                }
                , presenter.getPermission().userCanUpdateHakukohde(context)));

        VerticalLayout yetAnotherLayout = new VerticalLayout();
        yetAnotherLayout.setMargin(true);

        for (ValintakoeViewModel valintakoe:presenter.loadHakukohdeValintaKokees()) {

            final GridLayout grid = new GridLayout(2, 1);
            grid.setWidth("100%");


            addHeaderToGrid(grid, "valintakoeTitle");

            Label valintaKoeTiedot = new Label(getLanguageString(valintakoe.getSanallisetKuvaukset()));
            valintaKoeTiedot.setContentMode(Label.CONTENT_XHTML);
            addItemToGrid(grid,"sanallinenKuvaus",valintaKoeTiedot);

            addItemToGrid(grid,"valintakoeAjatTitle",buildValintakoeAikaTable(valintakoe));

            String lisanayttoKuvaus = getLanguageString(valintakoe.getLisanayttoKuvaukset());
            if (lisanayttoKuvaus != null && lisanayttoKuvaus.trim().length() > 0) {
            addHeaderToGrid(grid,"lisanaytotLabel");


            addRichTextToGrid(grid,"lisanayttoLabel",getLanguageString(valintakoe.getLisanayttoKuvaukset()));
            }
            grid.setColumnExpandRatio(1,1f);
            yetAnotherLayout.addComponent(grid);

        }

        valintakoeLayout.addComponent(yetAnotherLayout);
        layout.addComponent(valintakoeLayout);

    }


    private boolean checkValintakoeKieli(ValintakoeViewModel valintakoeViewModel) {


        for (KielikaannosViewModel kieli: valintakoeViewModel.getSanallisetKuvaukset()) {
            if (kieli.getKielikoodi().trim().equalsIgnoreCase(this.language)) {
                return true;
            }
        }

        return false;
    }

    private Table buildValintakoeAikaTable(ValintakoeViewModel valintakoe) {
        Table valintakoeAikaTable = new Table();

        valintakoeAikaTable.setContainerDataSource(createBeanContainer(valintakoe.getValintakoeAjat()));
        valintakoeAikaTable.setWidth(100,UNITS_PERCENTAGE);
        valintakoeAikaTable.setVisibleColumns(new String[]{"valintakoeSijainti","valintakoeAika","valintakoeLisatiedot"});
        valintakoeAikaTable.setColumnHeader("valintakoeSijainti",this.i18n.getMessage("tableValintakoeAikaSijainti"));
        valintakoeAikaTable.setColumnHeader("valintakoeAika",this.i18n.getMessage("tableValintakoeAikaAjankohta"));
        valintakoeAikaTable.setColumnHeader("valintakoeLisatiedot",this.i18n.getMessage("tableValintakoeAikaLisatietoja"));
        valintakoeAikaTable.setColumnExpandRatio("valintakoeSijainti",40);
        valintakoeAikaTable.setColumnExpandRatio("valintakoeAika",30);
        valintakoeAikaTable.setColumnExpandRatio("valintakoeLisatiedot",30);
        valintakoeAikaTable.setPageLength(valintakoe.getValintakoeAjat().size());

        return valintakoeAikaTable;
    }

    private BeanContainer<String, ShowHakukohdeValintakoeRow> createBeanContainer(List<ValintakoeAikaViewModel> valintaAikas) {
        BeanContainer<String, ShowHakukohdeValintakoeRow> container = new BeanContainer<String, ShowHakukohdeValintakoeRow>(ShowHakukohdeValintakoeRow.class);

        for (ValintakoeAikaViewModel valintakoeAika:valintaAikas) {
            ShowHakukohdeValintakoeRow row = new ShowHakukohdeValintakoeRow(valintakoeAika,this.language);
            container.addItem(new Integer(row.hashCode()).toString(),row);

        }

        return container;
    }

    private void buildKoulutuksesLayout(VerticalLayout layout) {

        VerticalLayout koulutuksesLayout = new VerticalLayout();
        koulutuksesLayout.setMargin(true);

        koulutuksesLayout.addComponent(buildHeaderLayout(this.i18n.getMessage("koulutuksetTitle"),i18n.getMessage(CommonTranslationKeys.MUOKKAA),
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {

                    }
                }
                ,presenter.getPermission().userCanUpdateHakukohde(context)));

        CategoryTreeView categoryTree = new CategoryTreeView();
        categoryTree.setHeight("100px");
        categoryTree.setContainerDataSource(createHakukohdeKoulutusDatasource(presenter.getModel().getHakukohde().getKoulukses()));
        String[] visibleColumns = {"nimiBtn", "poistaBtn"};
        categoryTree.setVisibleColumns(visibleColumns);
        for (Object item : categoryTree.getItemIds()) {
            categoryTree.setChildrenAllowed(item, false);
        }
        koulutuksesLayout.addComponent(categoryTree);
        layout.addComponent(koulutuksesLayout);
    }

    private Container createHakukohdeKoulutusDatasource(List<KoulutusOidNameViewModel> koulutukses) {
        BeanItemContainer<ShowHakukohdeKoulutusRow> container = new BeanItemContainer<ShowHakukohdeKoulutusRow>(ShowHakukohdeKoulutusRow.class);

        container.addAll(getRows(koulutukses));

        return container;
    }

    private List<ShowHakukohdeKoulutusRow> getRows(List<KoulutusOidNameViewModel> koulutukses) {
        List<ShowHakukohdeKoulutusRow> rows = new ArrayList<ShowHakukohdeKoulutusRow>();
        for (KoulutusOidNameViewModel koulutus : koulutukses) {
            ShowHakukohdeKoulutusRow row = new ShowHakukohdeKoulutusRow(koulutus);
            rows.add(row);
        }
        return rows;
    }

    private void buildPerustiedotLayout(VerticalLayout layout) {
        VerticalLayout hdrLayout = new VerticalLayout();
        hdrLayout.setMargin(true);


        hdrLayout.addComponent(buildHeaderLayout(this.i18n.getMessage("perustiedot"),i18n.getMessage(CommonTranslationKeys.MUOKKAA),
                 new Button.ClickListener() {
                     @Override
                     public void buttonClick(Button.ClickEvent clickEvent) {

                     }
                 }
                 ,presenter.getPermission().userCanUpdateHakukohde(context)));

        final GridLayout grid = new GridLayout(2, 1);
        grid.setWidth("100%");
        grid.setMargin(true);
        addItemToGrid(grid,"hakukohdeNimi",uiHelper.getKoodiNimi(presenter.getModel().getHakukohde().getHakukohdeNimi(),null));
        addItemToGrid(grid,"haku",tryGetLocalizedHakuNimi(presenter.getModel().getHakukohde().getHakuOid()));
        addItemToGrid(grid,"hakijoilleIlmoitetutAloituspaikat",new Integer(presenter.getModel().getHakukohde().getAloitusPaikat()).toString());
        addItemToGrid(grid,"valinnoissaKaytettavatAloituspaikat",new Integer(presenter.getModel().getHakukohde().getValinnoissaKaytettavatPaikat()).toString());
        addRichTextToGrid(grid,"hakukelpoisuusVaatimukset",getLanguageString(presenter.getModel().getHakukohde().getValintaPerusteidenKuvaus()));
        addRichTextToGrid(grid,"lisatietojaHakemisesta",getLanguageString(presenter.getModel().getHakukohde().getLisatiedot()));
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
        addItemToGrid(grid,"liitteetToimMennessa",sdf.format(presenter.getModel().getHakukohde().getLiitteidenToimitusPvm()));
        addItemToGrid(grid,"liitteidenToimitusOsoite",getHakukohdeLiiteOsoite(presenter.getModel().getHakukohde()));
        if (presenter.getModel().getHakukohde().getLiitteidenSahkoinenToimitusOsoite() != null && presenter.getModel().getHakukohde().getLiitteidenSahkoinenToimitusOsoite().trim().length() > 0) {
            Link sahkoinenToimOsoiteLink = new Link(presenter.getModel().getHakukohde().getLiitteidenSahkoinenToimitusOsoite(),new ExternalResource(presenter.getModel().getHakukohde().getLiitteidenSahkoinenToimitusOsoite()));
            sahkoinenToimOsoiteLink.setTargetName("_blank");
            addItemToGrid(grid,"sahkoinenToimOsoite",sahkoinenToimOsoiteLink);
        }
        //grid.setColumnExpandRatio(0,0.2f);
        grid.setColumnExpandRatio(1,1f);

        hdrLayout.addComponent(grid);
        hdrLayout.setComponentAlignment(grid, Alignment.TOP_LEFT);
        layout.addComponent(hdrLayout);

    }

    private void addItemToGrid(final GridLayout grid,
                               final String labelCaptionKey, final String labelCaptionValue) {
        addItemToGrid(grid, labelCaptionKey, new Label(labelCaptionValue));
    }

    private void addRichTextToGrid(final GridLayout grid,
                               final String labelCaptionKey, final String labelCaptionValue) {


        Label lbl = new Label(labelCaptionValue);
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

    private void addHeaderToGrid(final GridLayout grid,final String label) {
        if (grid != null) {
            final HorizontalLayout hl = UiUtil.horizontalLayout(false,
                    UiMarginEnum.RIGHT);
            hl.setSizeUndefined();
            Label hdrLbl = UiUtil.label(null,this.i18n.getMessage(label));
            hdrLbl.setStyleName(Oph.LABEL_H2);
            hl.addComponent(hdrLbl);
            grid.addComponent(hl);

            grid.setComponentAlignment(hl, Alignment.TOP_RIGHT);

            grid.newLine();
        }
    }

    private String getLanguageString(List<KielikaannosViewModel> tekstit) {
        for (KielikaannosViewModel teksti:tekstit) {
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
                    public void buttonClick(Button.ClickEvent event) {
                        getWindow().showNotification("Toiminnallisuutta ei vielä toteutettu");
                    }
                });
            }

            headerLayout.setExpandRatio(btn, 1f);
            headerLayout.setComponentAlignment(btn, Alignment.TOP_RIGHT);
        }
        return headerLayout;
    }

    private String tryGetLocalizedHakuNimi(HakuViewModel hakuViewModel) {

        Preconditions.checkNotNull(hakuViewModel,"HakuviewModel cannot be null");
        Preconditions.checkNotNull(this.language,"Language cannot be null");
        String haunNimi = null;

        if (this.language.trim().equalsIgnoreCase("en")) {
            haunNimi = hakuViewModel.getNimiEn();
        } else if (this.language.trim().equalsIgnoreCase("se")) {
            haunNimi = hakuViewModel.getNimiSe();
        }

        if (haunNimi == null || this.language.trim().equalsIgnoreCase("fi")) {
            haunNimi = hakuViewModel.getNimiFi();
        }

        return haunNimi;
    }
}
