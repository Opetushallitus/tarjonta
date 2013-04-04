package fi.vm.sade.tarjonta.ui.view.hakukohde;

import com.google.common.base.Preconditions;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.*;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.ui.enums.CommonTranslationKeys;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import fi.vm.sade.tarjonta.ui.model.KielikaannosViewModel;
import fi.vm.sade.tarjonta.ui.model.ValintakoeAikaViewModel;
import fi.vm.sade.tarjonta.ui.model.ValintakoeViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.service.OrganisaatioContext;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.ShowHakukohdeValintakoeRow;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.util.UiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

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

    public ShowHakukohdeTab(String language) {
          Preconditions.checkNotNull(language,"Language cannot be null");
          this.language = language;
        this.context = OrganisaatioContext.getContext(presenter.getTarjoaja().getOrganisationOid());
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setMargin(true);
        setCompositionRoot(mainLayout);
        buildPage(mainLayout);
    }

    private void buildPage(VerticalLayout layout) {
         buildPerustiedotLayout(layout);
         addLayoutSplit(layout);
         buildValintakokeetLayout(layout);

    }

    private void buildValintakokeetLayout(VerticalLayout layout) {

        layout.addComponent(buildHeaderLayout(this.i18n.getMessage("valintakokeetTitle"),i18n.getMessage(CommonTranslationKeys.MUOKKAA),
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {

                    }
                }
                ,presenter.getPermission().userCanUpdateHakukohde(context)));

        VerticalLayout yetAnotherLayout = new VerticalLayout();
        yetAnotherLayout.setMargin(true);

        for (ValintakoeViewModel valintakoe:presenter.loadHakukohdeValintaKokees()) {

            if (checkValintakoeKieli(valintakoe)) {

            Label titleLabel = UiUtil.label(null,i18n.getMessage("valintakoeTitle"));
            titleLabel.setStyleName(Oph.LABEL_H2);
            yetAnotherLayout.addComponent(titleLabel);

            Label valintaKoeTiedot = new Label(getLanguageString(valintakoe.getSanallisetKuvaukset()));
            valintaKoeTiedot.setContentMode(Label.CONTENT_XHTML);
            yetAnotherLayout.addComponent(valintaKoeTiedot);

            Label valintakoeAikaTitle = UiUtil.label(null,i18n.getMessage("valintakoeAjatTitle"));
            valintakoeAikaTitle.setStyleName(Oph.LABEL_H2);
            yetAnotherLayout.addComponent(valintakoeAikaTitle);
            yetAnotherLayout.addComponent(buildValintakoeAikaTable(valintakoe));

            String lisanayttoKuvaus = getLanguageString(valintakoe.getLisanayttoKuvaukset());
            if (lisanayttoKuvaus != null && lisanayttoKuvaus.trim().length() > 0) {
            Label lisanaytotLbl = UiUtil.label(null,i18n.getMessage("lisanaytotLabel"));
            lisanaytotLbl.setStyleName(Oph.LABEL_H2);
            yetAnotherLayout.addComponent(lisanaytotLbl);

            Label lisaNayttoLbl = new Label(getLanguageString(valintakoe.getLisanayttoKuvaukset()));
            lisaNayttoLbl.setContentMode(Label.CONTENT_XHTML);
            yetAnotherLayout.addComponent(lisaNayttoLbl);
            }

            }
        }

        layout.addComponent(yetAnotherLayout);


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

    private void buildPerustiedotLayout(VerticalLayout layout) {
         layout.addComponent(buildHeaderLayout(this.i18n.getMessage("perustiedot"),i18n.getMessage(CommonTranslationKeys.MUOKKAA),
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
        //grid.setColumnExpandRatio(0,0.2f);
        grid.setColumnExpandRatio(1,1f);

        layout.addComponent(grid);
        layout.setComponentAlignment(grid, Alignment.TOP_LEFT);

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
