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
package fi.vm.sade.tarjonta.ui.view.haku;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.ui.enums.CommonTranslationKeys;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.HakuHakukohdeResultRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Container;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;

import fi.vm.sade.tarjonta.ui.model.HakuaikaViewModel;
import fi.vm.sade.tarjonta.ui.presenter.HakuPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalInfoLayout;
import fi.vm.sade.tarjonta.ui.view.common.CategoryTreeView;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.constants.StyleEnum;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.dto.PageNavigationDTO;
import fi.vm.sade.vaadin.util.UiUtil;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Component for viewing Haku objects.
 *
 * @author markus
 *
 */
@Configurable(preConstruction = true)
public class ShowHakuView extends AbstractVerticalInfoLayout {

    private static final Logger LOG = LoggerFactory.getLogger(ShowHakuView.class);

    @Autowired(required=true)
    private HakuPresenter hakuPresenter;

    @Autowired(required=true)
    private TarjontaUIHelper _tarjontaUIHelper;

    private HakuViewModel model;

    private I18NHelper i18n = new I18NHelper("ShowHakuView.");

    private final String datePattern = "dd.MM.yyyy HH:mm";

    public ShowHakuView(String pageTitle, String message, PageNavigationDTO dto) {
        super(VerticalLayout.class, pageTitle, "", dto);
    }

    private void addItemToGrid(final GridLayout grid,
                               final String labelCaptionKey, final Component component) {
        if (grid != null) {
            final HorizontalLayout hl = UiUtil.horizontalLayout(false,
                    UiMarginEnum.RIGHT);
            hl.setSizeUndefined();
            if (labelCaptionKey != null) {
            UiUtil.label(hl, i18n.getMessage(labelCaptionKey));
            } else {
            UiUtil.label(hl,"");
            }
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

    @Override
    public void buildLayout(VerticalLayout layout) {
        addNavigationButton("", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                backFired();
            }
        }, StyleEnum.STYLE_BUTTON_BACK);

        Button poista = addNavigationButton(T("Poista"), new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (model != null)  {
                try {
                hakuPresenter.removeHaku(model);
                backFired();
                } catch (Exception exp ) {
                    if (exp.getMessage().contains("fi.vm.sade.tarjonta.service.business.exception.HakuUsedException")) {
                    getWindow().showNotification(I18N.getMessage("notification.error.haku.used"));
                    }
                }

                }
            }
        });

        addLayoutSplit();
        buildLayoutMiddleTop(layout);  //loads the model!!

        //permissions
        poista.setVisible(hakuPresenter.getPermission().userCanDeleteHaku(model.getHakuOid()));

        //addLayoutSplit();
        //buildLayoutMiddleMid2(layout);
        //addLayoutSplit();
        //buildLayoutMiddleBottom(layout);
        addLayoutSplit();
    }


    private HorizontalLayout buildHeaderLayout(String title, String btnCaption, Button.ClickListener listener, Label lastUpdatedLabel ,boolean showButton) {
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
                headerLayout.setComponentAlignment(lastUpdatedLabel,Alignment.TOP_CENTER);
            }
        }
        return headerLayout;
    }

    private void addRichTextToGrid(final GridLayout grid,
                                   final String labelCaptionKey, final String labelCaptionValue) {


        Label lbl = new Label(labelCaptionValue);
        lbl.setContentMode(Label.CONTENT_XHTML);

        addItemToGrid(grid, labelCaptionKey, lbl);
    }

    private Label buildTallennettuLabel(Date date) {
        SimpleDateFormat sdp = new SimpleDateFormat(datePattern);
        Label lastUpdLbl = new Label("( " + hakuPresenter.getHakuModel().getHaunTila() + " ," + i18n.getMessage("tallennettuLbl") + " " + sdp.format(date) + " )");
        return lastUpdLbl;
    }


    private void buildLayoutMiddleTop(VerticalLayout layout) {
        VerticalLayout hdrLayout = new VerticalLayout();
        hdrLayout.setMargin(true);

        model = hakuPresenter.getHakuModel();

        Label lastUpdLbl = null;

        if (hakuPresenter.getHakuModel().getViimeisinPaivitysPvm() != null) {
           lastUpdLbl = buildTallennettuLabel(model.getViimeisinPaivitysPvm());

        }

        hdrLayout.addComponent(buildHeaderLayout(this.i18n.getMessage("perustiedot"),i18n.getMessage(CommonTranslationKeys.MUOKKAA),
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent clickEvent) {
                         hakuPresenter.showHakuEdit(hakuPresenter.getHakuModel());
                    }
                }
                ,lastUpdLbl , this.hakuPresenter.getPermission().userCanUpdateHaku(hakuPresenter.getModel().getHakuOid())));

        final GridLayout grid = new GridLayout(2, 1);
        grid.setWidth("100%");
        grid.setMargin(true);

        addItemToGrid(grid, "Hakutyyppi", _tarjontaUIHelper.getKoodiNimi(model.getHakutyyppi()));
        addItemToGrid(grid, "HakukausiJaVuosi", _tarjontaUIHelper.getKoodiNimi(model.getHakukausi()) + " " + model.getHakuvuosi());
        addItemToGrid(grid, "KoulutuksenAlkamiskausi",_tarjontaUIHelper.getKoodiNimi(model.getKoulutuksenAlkamisKausi()) + "  " + model.getKoulutuksenAlkamisvuosi());
        addItemToGrid(grid, "HakuKohdejoukko", _tarjontaUIHelper.getKoodiNimi(model.getHaunKohdejoukko()) );
        addItemToGrid(grid, "Hakutapa", _tarjontaUIHelper.getKoodiNimi(model.getHakutapa()));
        addItemToGrid(grid, "HaunTunniste", model.getHaunTunniste());
        addItemToGrid(grid ,"Hakuaika", _tarjontaUIHelper.formatDate(model.getAlkamisPvm()) + " - " + _tarjontaUIHelper.formatDate(model.getPaattymisPvm()));
        if (model.getSisaisetHakuajat() != null && model.getSisaisetHakuajat().size() > 1) {
            VerticalLayout hakuajatArea = UiUtil.verticalLayout();
            for (HakuaikaViewModel hakuaika: model.getSisaisetHakuajat()) {
                UiUtil.label(hakuajatArea, ((hakuaika.getHakuajanKuvaus() != null) ? hakuaika.getHakuajanKuvaus() + ", " : "") + _tarjontaUIHelper.formatDate(hakuaika.getAlkamisPvm()) + " - " + _tarjontaUIHelper.formatDate(hakuaika.getPaattymisPvm()));
            }
            addItemToGrid(grid, "Hakuajat", hakuajatArea);
        }
        String hakulomakeStr = hakuPresenter.getHakuModel().isKaytetaanJarjestelmanHakulomaketta()
                ? T("KaytetaanJarjestelmanHakulomaketta")
                : hakuPresenter.getHakuModel().getHakuLomakeUrl();
        addItemToGrid(grid,"Hakulomake",hakulomakeStr);

        grid.setColumnExpandRatio(1,1f);

        hdrLayout.addComponent(grid);
        hdrLayout.setComponentAlignment(grid, Alignment.TOP_LEFT);

        layout.addComponent(hdrLayout);
    }

//    private void buildLayoutMiddleMid2(VerticalLayout layout) {
//        layout.addComponent(buildHeaderLayout(T("Sisaisethaut"), T("Muokkaa")));
//
//        CategoryTreeView categoryTree = new CategoryTreeView();
//        categoryTree.setHeight("100px");
//        categoryTree.setContainerDataSource(createHakuaikaTreeDataSource(hakuPresenter.getSisaisetHautSource()));
//        layout.addComponent(categoryTree);
//    }

    private void addItemToGrid(final GridLayout grid,
                               final String labelCaptionKey, final String labelCaptionValue) {
        addItemToGrid(grid, labelCaptionKey, new Label(labelCaptionValue));
    }

    private void buildLayoutMiddleBottom(VerticalLayout layout) {
        //layout.addComponent(buildBottomHeaderLayout(T("Hakukohteet"), T("LuoHakukohde")));

        VerticalLayout mBottomLayout = new VerticalLayout();
        mBottomLayout.setMargin(true);



        mBottomLayout.addComponent(buildHeaderLayout(this.i18n.getMessage("hakukohteet"),i18n.getMessage(CommonTranslationKeys.MUOKKAA),
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent clickEvent) {
                          ShowHakuView.this.getWindow().showNotification("Ei toteutettu");
                    }
                }
                ,null , hakuPresenter.getPermission().userCanUpdateHaku(model.getHakuOid())));

        CategoryTreeView categoryTree = new CategoryTreeView();
        categoryTree.setHeight("100px");
        categoryTree.setContainerDataSource(createHakukohteetTreeDataSource(model.getHakukohteet()));
        String[]  visibleColums = {"hakukohdeBtn"};
        categoryTree.setVisibleColumns(visibleColums);
        for (Object item : categoryTree.getItemIds()) {
            categoryTree.setChildrenAllowed(item, false);
        }
        mBottomLayout.addComponent(categoryTree);

        layout.addComponent(mBottomLayout);

    }



    private HorizontalLayout buildHeaderLayout(String title, String btnCaption) {
        HorizontalLayout headerLayout = UiUtil.horizontalLayout(true, UiMarginEnum.NONE);
        Label titleLabel = UiUtil.label(headerLayout, title);
        titleLabel.setStyleName(Oph.LABEL_H2);

        if (btnCaption != null) {
            headerLayout.addComponent(titleLabel);
            Button btn = UiUtil.buttonSmallSecodary(headerLayout, btnCaption, new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                   editFired();
                }
            });

            headerLayout.setExpandRatio(btn, 1f);
            headerLayout.setComponentAlignment(btn, Alignment.TOP_RIGHT);
        }
        return headerLayout;
    }

    private HorizontalLayout buildBottomHeaderLayout(String title, String btnCaption) {
        HorizontalLayout headerLayout = UiUtil.horizontalLayout(true, UiMarginEnum.NONE);
        Label titleLabel = UiUtil.label(headerLayout, title);
        titleLabel.setStyleName(Oph.LABEL_H2);

        if (btnCaption != null) {
            headerLayout.addComponent(titleLabel);
            Button btn = UiUtil.buttonSmallSecodary(headerLayout, btnCaption, new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                   LOG.info("Luo hakukohde to be implemented");
                }
            });

            headerLayout.setExpandRatio(btn, 1f);
            headerLayout.setComponentAlignment(btn, Alignment.TOP_RIGHT);
        }
        return headerLayout;
    }

    private Container createHakuaikaTreeDataSource(
            List<HakuaikaViewModel> sisaisetHautSource) {
        HierarchicalContainer hc = new HierarchicalContainer();
        return hc;
    }

    private Container createHakukohteetTreeDataSource(
            List<HakuHakukohdeResultRow> hakukohteet) {
        BeanItemContainer<HakuHakukohdeResultRow>  container = new BeanItemContainer<HakuHakukohdeResultRow>(HakuHakukohdeResultRow.class);

        container.addAll(hakukohteet);

        return container;
    }

    private void backFired() {
        fireEvent(new BackEvent(this));
    }

    private void editFired() {
        fireEvent(new EditEvent(this));
    }

    /**
     * Fired when Back is pressed.
     */
    public class BackEvent extends Component.Event {

        public BackEvent(Component source) {
            super(source);

        }
    }

    /**
     * Fired when Edit is pressed.
     */
    public class EditEvent extends Component.Event {

        public EditEvent(Component source) {
            super(source);

        }
    }

}
