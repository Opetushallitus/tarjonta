/*
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software:  Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.tarjonta.ui.view.koulutus.lukio;

import java.text.SimpleDateFormat;
import java.util.*;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import fi.vm.sade.tarjonta.ui.model.SimpleHakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoodiModel;
import fi.vm.sade.tarjonta.ui.view.common.*;
import fi.vm.sade.tarjonta.ui.view.koulutus.ShowKoulutusHakukohdeRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.google.common.base.Preconditions;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.auth.OrganisaatioContext;
import fi.vm.sade.tarjonta.shared.auth.TarjontaPermissionServiceImpl;

import fi.vm.sade.tarjonta.ui.enums.CommonTranslationKeys;
import fi.vm.sade.tarjonta.ui.enums.KoulutusActiveTab;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.koulutus.KoulutusKoodistoModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.MonikielinenTekstiModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.aste2.KoulutusLisatietoModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.KoulutusLukioKuvailevatTiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.KoulutusLukioPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.KoulutusRelaatioModel;
import fi.vm.sade.tarjonta.ui.model.koulutus.lukio.YhteyshenkiloModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.koulutus.ShowKoulutusView;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.constants.StyleEnum;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.dto.PageNavigationDTO;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 * Show collected information about koulutus.
 *
 * @author mlyly
 */
@Configurable
public class ShowKoulutusSummaryView extends AbstractVerticalInfoLayout {

    private static final Logger LOG = LoggerFactory.getLogger(ShowKoulutusView.class);
    private static final long serialVersionUID = -4381256372874208231L;
    @Autowired(required = true)
    private TarjontaPresenter _presenter;
    @Autowired(required = true)
    private TarjontaUIHelper _tarjontaUIHelper;
    @Autowired
    private TarjontaUIHelper uiHelper;
    private TarjontaDialogWindow tarjontaDialog;
    private final String datePattern = "dd.MM.yyyy HH:mm";
    private OrganisaatioContext context;

    public ShowKoulutusSummaryView(String pageTitle, PageNavigationDTO pageNavigationDTO) {
        super(VerticalLayout.class, pageTitle, null, pageNavigationDTO);
    }

    @Override
    protected void buildLayout(VerticalLayout layout) {
        context = OrganisaatioContext.getContext(_presenter.getTarjoaja().getSelectedOrganisationOid());
        LOG.debug("buildLayout(): hakutyyppi uri={}", KoodistoURI.KOODISTO_HAKUTYYPPI_URI);
        final KoulutusLukioPerustiedotViewModel perustiedot = _presenter.getModel().getKoulutusLukioPerustiedot();
        if (_presenter == null) {
            _presenter = new TarjontaPresenter();
        }

        if (_tarjontaUIHelper == null) {
            _tarjontaUIHelper = new TarjontaUIHelper();
        }

        layout.removeAllComponents();

        Set<String> langs = getLanguages();
        addNavigationButtons(layout, context);

        if (langs.size() < 1) {
            LOG.debug("buildLayout(): No languages, this should happen only for imported koulutukset");
            Panel panel = new Panel();
            panel.setContent(buildMainLayout(perustiedot, T("lang.fi")));
            layout.addComponent(panel);
        } else if (langs.size() == 1) {
            LOG.debug("buildLayout(): Only one lang");
            Panel panel = new Panel();
            panel.setContent(buildMainLayout(perustiedot, langs.iterator().next()));
            layout.addComponent(panel);
        } else {
            TabSheet ts = new TabSheet();
            layout.addComponent(ts);
            for (String lang : langs) {
                ts.addTab(buildMainLayout(perustiedot, lang), _tarjontaUIHelper.getKoodiNimi(lang));
            }
        }

        /*
         VerticalLayout vl = UiUtil.verticalLayout(true, UiMarginEnum.ALL);
         Panel panel = new Panel();

         panel.setContent(vl);
         layout.addComponent(panel);

         addNavigationButtons(vl, OrganisaatioContext.getContext(_presenter.getTarjoaja().getOrganisationOid()));

         buildPerustiedot(vl, perustiedot, "lukiolinja", perustiedot.getLukiolinja());
         addLayoutSplit(vl);
         buildKuvailevatTiedot(vl);*/
    }

    private VerticalLayout buildMainLayout(KoulutusLukioPerustiedotViewModel perustiedot, String lang) {
        VerticalLayout vl = UiUtil.verticalLayout(true, UiMarginEnum.ALL);

        buildPerustiedot(vl, perustiedot, "lukiolinja", perustiedot.getLukiolinja());
        addLayoutSplit(vl);
        buildKuvailevatTiedot(vl, lang);
        addLayoutSplit(vl);
        buildKoulutuksenHakukohteet(vl);
        addLayoutSplit(vl);
        return vl;
    }

    private Label buildTallennettuLabel(Date date, String viimPaivittajaOid) {
        SimpleDateFormat sdp = new SimpleDateFormat(datePattern);
        String viimPaivittaja = uiHelper.findUsernameWithOid(viimPaivittajaOid);
        Label lastUpdLbl = new Label("( " + T("tallennettuLbl") + " " + sdp.format(date) + ", " + viimPaivittaja + " )");
        return lastUpdLbl;
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

    private void buildPerustiedot(VerticalLayout vl, KoulutusRelaatioModel model, String otherCbLabel, MonikielinenTekstiModel otherCbKoodi) {
        Preconditions.checkNotNull(vl, "VericalLayout object cannot be null.");
        Preconditions.checkNotNull(model, "KoulutusRelaatioModel object cannot be null.");
        Preconditions.checkNotNull(otherCbLabel, "Combobox label key cannot be null.");
        Preconditions.checkNotNull(otherCbKoodi, "Combobox MonikielinenTekstiModel object cannot be null.");

        Label viimeisinPaivitysPvm = null;
        if (model instanceof KoulutusLukioPerustiedotViewModel && ((KoulutusLukioPerustiedotViewModel) model).getViimeisinPaivitysPvm() != null) {

            viimeisinPaivitysPvm = buildTallennettuLabel(((KoulutusLukioPerustiedotViewModel) model).getViimeisinPaivitysPvm(), ((KoulutusLukioPerustiedotViewModel) model).getViimeisinPaivittajaOid());
        }

        vl.addComponent(buildHeaderLayout(T("perustiedot"), T(CommonTranslationKeys.MUOKKAA), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(ClickEvent event) {
                _presenter.getLukioPresenter().showEditKoulutusView(getEditViewOid(), KoulutusActiveTab.PERUSTIEDOT);
            }
        }, viimeisinPaivitysPvm, _presenter.getPermission().userCanUpdateKoulutus(context)));

        FormGridBuilder grid = new FormGridBuilder(getClass());

        grid.addText("organisaatio", _presenter.getTarjoaja().getSelectedOrganisation().getOrganisationName())
                .addText("tutkinto", modelToStr(model.getKoulutuskoodiModel()))
                .addText(otherCbLabel, modelToStr(otherCbKoodi))
                .addSpace()
                .addText("koulutusaste", modelToStr(model.getKoulutusaste()))
                .addText("koulutusala", modelToStr(model.getKoulutusala()))
                .addText("opintoala", modelToStr(model.getOpintoala()))
                .addText("tutkintonimike", modelToStr(model.getTutkintonimike()))
                .addText("opintojenLaajuus", createLaajuusString(model))
                .addText("koulutuslaji", modelToStr(model.getKoulutuslaji()))
                .addText("pohjakoulutusvaatimus", findPohjakoulutusVaatimus(model.getPohjakoulutusvaatimus()))
                .addXhtml("koulutuksenRakenne", modelToStr(model.getKoulutuksenRakenne()))
                .addXhtml("tavoitteet", modelToStr(model.getTavoitteet()))
                .addXhtml("jatkoopintomahdollisuudet", modelToStr(model.getJatkoopintomahdollisuudet()))
                .addSpace();
        buildLukiokoulutuksenPerustiedot(grid);

        vl.addComponent(grid);
    }

    private void buildLukiokoulutuksenPerustiedot(FormGridBuilder grid) {
        KoulutusLukioPerustiedotViewModel model = _presenter.getModel().getKoulutusLukioPerustiedot();

        grid.addText("opetuskieli", koodiToStr(model.getOpetuskieli()))
                .addText("koulutuksenAlkamisPvm", _tarjontaUIHelper.formatDate(model.getKoulutuksenAlkamisPvm()))
                .addText("suunniteltuKesto", suunniteltuKesto(model.getSuunniteltuKesto(), model.getSuunniteltuKestoTyyppi()))
                .add("opetusmuoto", getOpetusMuoto(model.getOpetusmuoto()), Label.CONTENT_PREFORMATTED);

        String lnk = null;
        if (model.getOpsuLinkki() != null && !model.getOpsuLinkki().isEmpty()) {
            lnk = "<a href=" + model.getOpsuLinkki() + ">" + model.getOpsuLinkki() + "</a>";
        }

        grid.addXhtml("linkki", lnk);

        buildYhteyshenkilo(grid, model.getYhteyshenkilo());
    }

    private String getOpetusMuoto(Set<String> oms) {
        StringBuffer ret = new StringBuffer();
        for (String om : oms) {
            ret.append(_tarjontaUIHelper.getKoodiNimi(om)).append('\n');
        }
        return ret.toString();
    }

    private Set<String> getLanguages() {
        KoulutusLukioKuvailevatTiedotViewModel kuvailevatTiedot = _presenter.getModel().getKoulutusLukioKuvailevatTiedot();
        return new TreeSet<String>(kuvailevatTiedot.getTekstikentat().keySet());
    }

    private void buildKuvailevatTiedot(VerticalLayout vl, String lang) {
        KoulutusLukioKuvailevatTiedotViewModel kuvailevatTiedot = _presenter.getModel().getKoulutusLukioKuvailevatTiedot();
        vl.addComponent(buildHeaderLayout(T("kuvailevatTiedot"), T(CommonTranslationKeys.MUOKKAA), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(ClickEvent event) {
                _presenter.getLukioPresenter().showEditKoulutusView(getEditViewOid(), KoulutusActiveTab.LISATIEDOT);
            }
        }));

        FormGridBuilder grid = new FormGridBuilder(getClass())
                .addText("kieliA", _tarjontaUIHelper.getKoodiNimi(kuvailevatTiedot.getKieliA(), null))
                .addText("kieliB1", _tarjontaUIHelper.getKoodiNimi(kuvailevatTiedot.getKieliB1(), null))
                .addText("kieliB2", _tarjontaUIHelper.getKoodiNimi(kuvailevatTiedot.getKieliB2(), null))
                .addText("kieliB3", _tarjontaUIHelper.getKoodiNimi(kuvailevatTiedot.getKieliB3(), null))
                .addText("kieletMuu", _tarjontaUIHelper.getKoodiNimi(kuvailevatTiedot.getKieletMuu(), null))
                .addText("lukiodiplomit", _tarjontaUIHelper.getKoodiNimi(kuvailevatTiedot.getDiplomit(), null));

        vl.addComponent(grid);

        KoulutusLisatietoModel tiedotModel = kuvailevatTiedot.getLisatiedot(lang);

        FormGridBuilder gridLang = new FormGridBuilder(getClass());

        gridLang
                .addXhtml("tutkinnonSisalto", tiedotModel.getSisalto())
                .addXhtml("tutkinnonKansainvalistyminen", tiedotModel.getKansainvalistyminen())
                .addXhtml("tutkinnonYhteistyoMuidenToimijoidenKanssa", tiedotModel.getYhteistyoMuidenToimijoidenKanssa());

        vl.addComponent(gridLang);
    }

    private void buildYhteyshenkilo(FormGridBuilder grid, YhteyshenkiloModel model) {
        grid.addText("yhteyshenkilo", noNullStr(model.getYhtHenkTitteli()) + " " + noNullStr(model.getYhtHenkKokoNimi()))
                .addText(null, noNullStr(model.getYhtHenkPuhelin()) + " " + noNullStr(model.getYhtHenkEmail()));
    }

    private String findPohjakoulutusVaatimus(KoodiModel pohjakoulutusVaatimusKoodi) {

        String pohjaKoulutusVaatimusNimi = null;

        if (pohjakoulutusVaatimusKoodi != null && pohjakoulutusVaatimusKoodi.getKoodi() != null) {
            pohjaKoulutusVaatimusNimi = _tarjontaUIHelper.getKoodiNimi(pohjakoulutusVaatimusKoodi.getKoodi());
        } else {
            pohjaKoulutusVaatimusNimi = "";
        }

        return pohjaKoulutusVaatimusNimi;

    }

    private String suunniteltuKesto(final String kestoUri, final String kestoTyyppiUri) {
        // Build suunniteltu kesto and kesto tyyppi as string
        String tmp = "";
        if (kestoUri != null) {
            tmp = kestoUri;
            tmp += " ";

            String kestotyyppi = _tarjontaUIHelper.getKoodiNimi(kestoTyyppiUri, null);
            if (kestotyyppi != null) {
                tmp += kestotyyppi;
            } else {
                // Add uri if no translation ... just to show something.
                tmp += kestoTyyppiUri;
            }
        }
        return tmp;
    }

    private void buildKoulutuksenHakukohteet(VerticalLayout layout) {
        int numberOfApplicationTargets = _presenter.getModel().getKoulutusLukioPerustiedot().getKoulutuksenHakukohteet().size();

        layout.addComponent(buildHeaderLayout(T("hakukohteet", numberOfApplicationTargets), T("luoUusiHakukohdeBtn"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(ClickEvent event) {
                List<String> koulutus = new ArrayList<String>();
                koulutus.add(_presenter.getModel().getKoulutusLukioPerustiedot().getKomotoOid());
                _presenter.showHakukohdeEditView(koulutus, null, null, null);
            }
        }));

        CategoryTreeView categoryTree = new CategoryTreeView();
        categoryTree.setHeight("100px");
        categoryTree.setContainerDataSource(createHakukohdelistContainer(_presenter.getModel().getKoulutusLukioPerustiedot().getKoulutuksenHakukohteet()));
        String[] visibleColumns = {"nimiBtn", "poistaBtn"};
        categoryTree.setVisibleColumns(visibleColumns);
        for (Object item : categoryTree.getItemIds()) {
            categoryTree.setChildrenAllowed(item, false);
        }
        layout.addComponent(categoryTree);
    }

    public void showHakukohdeRemovalDialog(final String hakukohdeOid, final String hakukohdeNimi) {
        final Window hakukohdeRemovalDialog = new Window();
        RemovalConfirmationDialog removalConfirmationDialog = new RemovalConfirmationDialog(T("removeHakukohdeFromKoulutusQ"), hakukohdeNimi, T("jatkaBtn"), T("peruutaBtn"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                getWindow().removeWindow(hakukohdeRemovalDialog);
                _presenter.removeLukioHakukohdeFromKoulutus(hakukohdeOid);

            }
        }, new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                getWindow().removeWindow(hakukohdeRemovalDialog);
            }
        });
        hakukohdeRemovalDialog.setContent(removalConfirmationDialog);
        hakukohdeRemovalDialog.setModal(true);
        hakukohdeRemovalDialog.center();
        getWindow().addWindow(hakukohdeRemovalDialog);

    }

    private Container createHakukohdelistContainer(List<SimpleHakukohdeViewModel> hakukohdes) {
        BeanItemContainer<ShowKoulutusHakukohdeRow> hakukohdeRows = new BeanItemContainer<ShowKoulutusHakukohdeRow>(ShowKoulutusHakukohdeRow.class);
        hakukohdeRows.addAll(getKoulutusHakukohdeRows(hakukohdes));
        return hakukohdeRows;
    }

    private List<ShowKoulutusHakukohdeRow> getKoulutusHakukohdeRows(List<SimpleHakukohdeViewModel> hakukohdes) {
        List<ShowKoulutusHakukohdeRow> rows = new ArrayList<ShowKoulutusHakukohdeRow>();
        for (SimpleHakukohdeViewModel hakukohdeViewModel : hakukohdes) {
            ShowKoulutusHakukohdeRow row = new ShowKoulutusHakukohdeRow(hakukohdeViewModel, true);
            rows.add(row);
        }
        return rows;
    }

    private HorizontalLayout buildHeaderLayout(String title, String btnCaption, Button.ClickListener listener) {
        HorizontalLayout headerLayout = UiUtil.horizontalLayout(true, UiMarginEnum.NONE);
        Label titleLabel = UiUtil.label(headerLayout, title);
        titleLabel.setStyleName(Oph.LABEL_H2);

        if (btnCaption != null) {
            headerLayout.addComponent(titleLabel);
            Button btn = UiBuilder.buttonSmallPrimary(headerLayout, btnCaption, listener);
            btn.setVisible(_presenter.getPermission().userCanUpdateKoulutus(context)); //OrganisaatioContext.getContext(_presenter.getNavigationOrganisation().getOrganisationOid()

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

    private void addNavigationButtons(VerticalLayout layout, OrganisaatioContext context) {
        addNavigationButton("", new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {

                _presenter.reloadAndShowMainDefaultView();
            }
        }, StyleEnum.STYLE_BUTTON_BACK);

        final Button poista = addNavigationButton(T(CommonTranslationKeys.POISTA), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                //getWindow().showNotification("Ei toteutettu");
                showRemoveDialog();

            }
        }, StyleEnum.STYLE_BUTTON_PRIMARY);
          //Uncomment when functionality is done
    /*    final Button kopioiUudeksi = addNavigationButton(T(CommonTranslationKeys.KOPIOI_UUDEKSI), new Button.ClickListener() {
         private static final long serialVersionUID = 5019806363620874205L;

         @Override
         public void buttonClick(Button.ClickEvent event) {
         getWindow().showNotification("Ei toteutettu");
         //KoulutusKopiointiDialog kopiointiDialog = new KoulutusKopiointiDialog("600px", "500px");
         //getWindow().addWindow(kopiointiDialog);
         }
         }, StyleEnum.STYLE_BUTTON_PRIMARY);

         final Button siirraOsaksiToista = addNavigationButton(T("siirraOsaksiToistaKoulutusta"), new Button.ClickListener() {
         private static final long serialVersionUID = 5019806363620874205L;

         @Override
         public void buttonClick(Button.ClickEvent event) {
         getWindow().showNotification("Ei toteutettu");
         }
         }, StyleEnum.STYLE_BUTTON_PRIMARY);

         final Button lisaaToteutus = addNavigationButton(T("lisaaToteutus"), new Button.ClickListener() {
         private static final long serialVersionUID = 5019806363620874205L;

         @Override
         public void buttonClick(Button.ClickEvent event) {
         getWindow().showNotification("Ei toteutettu");
         // _presenter.showLisaaRinnakkainenToteutusEditView(_presenter.getModel().getKoulutusLukioPerustiedot().getKomotoOid(),null);
         }
         }, StyleEnum.STYLE_BUTTON_PRIMARY);

         *//*final Button esikatsele = *//*addNavigationButton(T("esikatsele"), new Button.ClickListener() {
         private static final long serialVersionUID = 5019806363620874205L;

         @Override
         public void buttonClick(Button.ClickEvent event) {
         getWindow().showNotification("Ei toteutettu");
         }
         }, StyleEnum.STYLE_BUTTON_PRIMARY);*/


        //check permissions
        final TarjontaPermissionServiceImpl permissions = _presenter.getPermission();
        
        final boolean deletePermission = permissions.userCanDeleteKoulutus(context);

        final boolean deleteVisible = deletePermission && _presenter.getModel().getKoulutusLukioPerustiedot().getKoulutuksenHakukohteet().size()==0;
        
        poista.setVisible(deleteVisible);
    }

    public void addLayoutSplit(VerticalLayout layout) {
        VerticalSplitPanel split = new VerticalSplitPanel();
        split.setImmediate(false);
        split.setWidth("100%");
        split.setHeight("2px");
        split.setLocked(true);

        layout.addComponent(split);
    }

    //Uncomment when functionality is done
    private void showRemoveDialog() {
        RemovalConfirmationDialog removeDialog = new RemovalConfirmationDialog(T("removeQ"), "", T("removeYes"), T("removeNo"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(ClickEvent event) {
                closeKoulutusCreationDialog();
                boolean removeSuccess = _presenter.removeKoulutus(getEditViewOid());
                _presenter.getHakukohdeListView().reload();
                if (removeSuccess) {
                    _presenter.showMainDefaultView();
                }
            }
        }, new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(ClickEvent event) {
                closeKoulutusCreationDialog();

            }
        });
        tarjontaDialog = new TarjontaDialogWindow(removeDialog, T("removeDialog"));
        getWindow().addWindow(tarjontaDialog);
    }

    public void closeKoulutusCreationDialog() {
        if (tarjontaDialog != null) {
            getWindow().removeWindow(tarjontaDialog);
        }
    }

    private String getEditViewOid() {
        KoulutusLukioPerustiedotViewModel model = _presenter.getModel().getKoulutusLukioPerustiedot();
        return model.getKomotoOid();
    }

    /*
     private Label buildLabel(String text) {
     Label label = UiUtil.label(null, text);
     label.setContentMode(Label.CONTENT_XHTML);
     label.setSizeFull();
     return label;
     }
     */
    private static String modelToStr(KoulutusKoodistoModel model) {
        if (model != null) {
            return model.getNimi();
        }

        return "";
    }

    private String koodiToStr(final String koodiUri) {
        return _tarjontaUIHelper.getKoodiNimi(koodiUri);
    }

    private String noNullStr(String str) {
        if (str == null) {
            return "";
        }
        return str;
    }

    private String createLaajuusString(final KoulutusRelaatioModel model) {

        final boolean laajuudenYksikko = (model.getOpintojenLaajuusyksikko() != null
                && model.getOpintojenLaajuusyksikko().getKoodistoUri() != null
                && !model.getOpintojenLaajuusyksikko().getKoodistoUri().isEmpty());

        final boolean laajuudenArvo = (model.getOpintojenLaajuus() != null
                && model.getOpintojenLaajuus().getKoodistoUri() != null
                && !model.getOpintojenLaajuus().getKoodistoUri().isEmpty());

        String opintojenLaajuus = "";
        if (laajuudenArvo && laajuudenYksikko) {
            if (model.getOpintojenLaajuus().getNimi() != null && !model.getOpintojenLaajuus().getNimi().isEmpty()) {
                //IB-lukio has laajuus 'arvo' as a text string
                opintojenLaajuus = model.getOpintojenLaajuus().getNimi() + " " + model.getOpintojenLaajuusyksikko().getNimi();
            } else {
                //laajuus 'arvo' as a number
                opintojenLaajuus = model.getOpintojenLaajuus().getKoodi() + " " + model.getOpintojenLaajuusyksikko().getNimi();
            }
        } else if (laajuudenYksikko) {
            opintojenLaajuus = model.getOpintojenLaajuusyksikko().getNimi();
        } else if (laajuudenArvo) {
            if (model.getOpintojenLaajuus().getNimi() != null && !model.getOpintojenLaajuus().getNimi().isEmpty()) {
                //IB-lukio has laajuus 'arvo' as a text string
                opintojenLaajuus = model.getOpintojenLaajuus().getNimi();
            } else {
                //laajuus 'arvo' as a number
                opintojenLaajuus = model.getOpintojenLaajuus().getKoodi();
            }
        }

        return opintojenLaajuus;
    }
}
