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
package fi.vm.sade.tarjonta.ui.view.koulutus;

import java.util.*;


import fi.vm.sade.koodisto.service.types.common.KieliType;
import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.tarjonta.ui.enums.KoulutusasteType;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusOidNameViewModel;
import fi.vm.sade.tarjonta.ui.service.OrganisaatioContext;
import fi.vm.sade.tarjonta.ui.view.haku.MultipleHakuRemovalDialog;
import fi.vm.sade.tarjonta.ui.view.hakukohde.CreationDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetVastausTyyppi.KoulutusTulos;
import fi.vm.sade.tarjonta.ui.enums.KoulutusActiveTab;
import fi.vm.sade.tarjonta.ui.enums.RequiredRole;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.CategoryTreeView;
import fi.vm.sade.tarjonta.ui.view.common.TarjontaDialogWindow;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import fi.vm.sade.generic.ui.feature.UserFeature;
import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi;
import fi.vm.sade.tarjonta.service.types.KoodistoKoodiTyyppi.Nimi;
import fi.vm.sade.tarjonta.service.types.KoulutusTyyppi;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;

/**
 *
 * @author Markus
 */
@Configurable(preConstruction = true)
public class ListKoulutusView extends VerticalLayout {
    
    private static final Logger LOG = LoggerFactory.getLogger(ListKoulutusView.class);
    private static final long serialVersionUID = 2571418094927644189L;
    public static final String[] ORDER_BY = new String[]{I18N.getMessage("ListKoulutusView.jarjestys.Organisaatio")};
    public static final String COLUMN_A = "Kategoriat";
    public static final String COLUMN_TUTKINTONIMIKE = "Tutkintonimike";
    public static final String COLUMN_PVM = "Ajankohta";
    public static final String COLUMN_KOULUTUSLAJI = "Koulutuslaji";
    public static final String COLUMN_TILA = "Tila";
    /**
     * Presenter object for the Hakukohde listing.
     */
    @Autowired(required = true)
    private TarjontaPresenter presenter;
    private Window createHakukohdeDialog;
    /**
     * Button for creating a hakukohde object.
     */
    private Button luoHakukohdeB;
    /**
     * Button for creating a koulutus object.
     */
    private Button luoKoulutusB;
    /**
     * Component for selecting desired sorting/grouping criteria for listed
     * Hakukohde objects.
     */
    private ComboBox cbJarjestys;
    /**
     * TreeTable component to display the Hakukohde objects in a
     * grouped/hierarchical manner.
     */
    private CategoryTreeView categoryTree;
    /**
     * Checkbox for selecting all the Hakukohde objects in the list.
     */
    private CheckBox valKaikki;
    private TarjontaDialogWindow koulutusDialog;
    private CreationDialog<KoulutusOidNameViewModel> createDialog;
    //private Button btnPoista;
    //private Button btnMuokkaa;
    private Button btnSiirraJaKopioi;
    private transient I18NHelper i18n = new I18NHelper(this);
    private boolean isAttached = false;
    @Autowired(required = true)
    private transient TarjontaUIHelper uiHelper;
    
    public ListKoulutusView() {
        setWidth(100, UNITS_PERCENTAGE);
        setHeight(-1, UNITS_PIXELS);
        setMargin(true);
        
        if (presenter == null) {
            LOG.error("Why am I creating new presenter??");
            presenter = new TarjontaPresenter();
        }
    }
    
    @Override
    public void attach() {
        super.attach();
        
        if (isAttached) {
            LOG.debug("already attached : ListKoulutusView()");
            return;
        }
        LOG.debug("attach : ListKoulutusView()");
        isAttached = true;

        //Initialization of the view layout

        //Creation of the button bar above the Hakukohde hierarchical/grouped list.
        HorizontalLayout buildMiddleResultLayout = buildMiddleResultLayout();
        addComponent(buildMiddleResultLayout);

        //Adding the select all checkbox.
        CssLayout wrapper = UiUtil.cssLayout(UiMarginEnum.BOTTOM);
        valKaikki = new CheckBox(i18n.getMessage("ValitseKaikki"));
        valKaikki.setImmediate(true);
        valKaikki.addListener(new Property.ValueChangeListener() {
            private static final long serialVersionUID = -382717228031608542L;
            
            @Override
            public void valueChange(ValueChangeEvent event) {
                toggleHakuSelections(valKaikki.booleanValue());
            }
        });
        wrapper.addComponent(valKaikki);
        addComponent(wrapper);

        //Adding the actual Hakukohde-listing component.
        categoryTree = new CategoryTreeView();
        addComponent(categoryTree);
        setExpandRatio(categoryTree, 1f);
        
        categoryTree.addContainerProperty(COLUMN_A, KoulutusResultRow.class, new KoulutusResultRow());
        categoryTree.addContainerProperty(COLUMN_TUTKINTONIMIKE, String.class, "");
        categoryTree.addContainerProperty(COLUMN_PVM, String.class, "");
        categoryTree.addContainerProperty(COLUMN_KOULUTUSLAJI,String.class,"");
        categoryTree.addContainerProperty(COLUMN_TILA, String.class, "");
        
        categoryTree.setColumnExpandRatio(COLUMN_A, 2.2f);
        categoryTree.setColumnExpandRatio(COLUMN_TUTKINTONIMIKE, 0.5f);
        categoryTree.setColumnExpandRatio(COLUMN_PVM, 0.3f);
        categoryTree.setColumnExpandRatio(COLUMN_KOULUTUSLAJI,0.3f);
        categoryTree.setColumnExpandRatio(COLUMN_TILA, 0.3f);

        /**
         * Sets the datasource for the hierarchical listing of Koulutus objects.
         */
        luoKoulutusB.setEnabled(presenter.getNavigationOrganisation().isOrganisationSelected());
        luoHakukohdeB.setEnabled(!presenter.getModel().getSelectedKoulutukset().isEmpty());      
    }

    /**
     * Creates the vaadin HierarchicalContainer datasource for the Koulutus
     * listing based on data provided by the presenter.
     *
     * @param map the data map provided by the presenter.
     * @return the hierarchical container for Koulutus listing.
     */
    private Container createDataSource(Map<String, List<KoulutusTulos>> map) {
        
        Set<Map.Entry<String, List<KoulutusTulos>>> set = map.entrySet();
        
        HierarchicalContainer hc = new HierarchicalContainer();
        KoulutusResultRow rowStyleDef = new KoulutusResultRow();
        
        hc.addContainerProperty(COLUMN_A, KoulutusResultRow.class, rowStyleDef.format("", false));
        hc.addContainerProperty(COLUMN_TUTKINTONIMIKE, String.class, "");
        hc.addContainerProperty(COLUMN_PVM, String.class, "");
        hc.addContainerProperty(COLUMN_KOULUTUSLAJI,String.class,"");
        hc.addContainerProperty(COLUMN_TILA, String.class, "");
        
        
        for (Map.Entry<String, List<KoulutusTulos>> e : set) {
            LOG.debug("getTreeDataSource()" + e.getKey());
            KoulutusResultRow rowStyle = new KoulutusResultRow();
            
            Object rootItem = hc.addItem();
            
            hc.getContainerProperty(rootItem, COLUMN_A).setValue(rowStyle.format(buildOrganisaatioCaption(e), false));
            
            for (KoulutusTulos curKoulutus : e.getValue()) {
                KoulutusResultRow rowStyleInner = new KoulutusResultRow(curKoulutus, getKoulutusNimi(curKoulutus));
                hc.addItem(curKoulutus);
                hc.setParent(curKoulutus, rootItem);
                hc.getContainerProperty(curKoulutus, COLUMN_A).setValue(rowStyleInner.format(getKoulutusNimi(curKoulutus), true));
                hc.getContainerProperty(curKoulutus, COLUMN_TUTKINTONIMIKE).setValue(getKoulutusTutkintoNimike(curKoulutus));
                hc.getContainerProperty(curKoulutus, COLUMN_PVM).setValue(getAjankohtaStr(curKoulutus));
                hc.getContainerProperty(curKoulutus, COLUMN_KOULUTUSLAJI).setValue(getKoulutuslaji(curKoulutus));
                hc.getContainerProperty(curKoulutus, COLUMN_TILA).setValue(getTilaStr(curKoulutus.getKoulutus().getTila().name()));
                hc.setChildrenAllowed(curKoulutus, false);
            }
        }
        return hc;
    }

    private String getKoulutuslaji(KoulutusTulos tulos ) {
        List<String> uris = new ArrayList<String>();
        if (tulos.getKoulutus().getKoulutuslaji() != null) {
            uris.add(tulos.getKoulutus().getKoulutuslaji());

            return uiHelper.getKoodiNimi(uris,I18N.getLocale());
        }
        return "";
    }
    
    private String getAjankohtaStr(KoulutusTulos curKoulutus) {
        
        String[] ajankohtaParts = curKoulutus.getKoulutus().getAjankohta().split(" ");
        if (ajankohtaParts.length < 2) {
            return "";
        }
        return I18N.getMessage(ajankohtaParts[0]) + " " + ajankohtaParts[1];
    }
    
    private String getKoulutusTutkintoNimike(KoulutusTulos curKoulutus) {
        if (curKoulutus.getKoulutus().getTarjoaja() != null) {
            return getKoodiNimi(curKoulutus.getKoulutus().getTutkintonimike());
        }
        return "";
    }
    
    private String buildOrganisaatioCaption(Map.Entry<String, List<KoulutusTulos>> e) {
        return e.getKey() + " (" + e.getValue().size() + ")";
    }

    private String tryGetKoodistoLyhytNimi(Collection<KoodiType> koodis) {
        if (koodis == null || koodis.size() < 1) {
            return "";
        }
        List<KoodiType> koodisList = new ArrayList<KoodiType>(koodis);
        KoodiType koodi = koodisList.get(0);

        if (koodi != null) {
        String retval = koodi.getKoodiArvo();

        List<KoodiMetadataType> metas =  koodi.getMetadata();
        Locale locale = I18N.getLocale();
        for (KoodiMetadataType meta : metas) {
            if (meta.getKieli().equals(KieliType.FI) && locale.getLanguage().equals("fi")) {
                return ", " + meta.getLyhytNimi();
            } else if (meta.getKieli().equals(KieliType.SV) && locale.getLanguage().equals("sv")) {
                return ", "+  meta.getLyhytNimi();
            }
        }

        return retval;
        } else {
            return "";
        }
    }
    
    private String getKoulutusNimi(KoulutusTulos curKoulutus) {

        List<KoodiType> koodis = null;
        if (curKoulutus.getKoulutus().getPohjakoulutusVaatimus() != null) {
            koodis = uiHelper.getKoodis(curKoulutus.getKoulutus().getPohjakoulutusVaatimus());
        }
        if (koodis == null) {
            koodis = new ArrayList<KoodiType>();
        }
        if (curKoulutus.getKoulutus().getKoulutusohjelmakoodi() != null) {
            return getKoodiNimi(curKoulutus.getKoulutus().getKoulutusohjelmakoodi()) + tryGetKoodistoLyhytNimi(koodis) ;
        } else if (curKoulutus.getKoulutus().getKoulutuskoodi() != null) {
            return getKoodiNimi(curKoulutus.getKoulutus().getKoulutuskoodi()) + tryGetKoodistoLyhytNimi(koodis);
        }
        return "";
    }
    
    private String getTilaStr(String tilaUri) {
        String[] parts = tilaUri.split("\\/");
        return i18n.getMessage(parts[parts.length - 1]);
    }

    /**
     * Selects or unselects all the objects in the Hakukohde listing.
     *
     * @param selected
     */
    private void toggleHakuSelections(boolean selected) {
        presenter.getSelectedhakukohteet().clear();
        HierarchicalContainer hc = (HierarchicalContainer) (this.categoryTree.getContainerDataSource());
        for (Object item : hc.getItemIds()) {
            KoulutusResultRow curRow = (KoulutusResultRow) (categoryTree.getContainerProperty(item, COLUMN_A).getValue());
            curRow.getIsSelected().setValue(selected);
        }
    }

    /**
     * Creation of the button bar part above the Koulutus-listing.
     *
     * @return
     */
    private HorizontalLayout buildMiddleResultLayout() {
        LOG.debug("buildMiddleResultLayout()");
        HorizontalLayout layout = UiUtil.horizontalLayout(true, UiMarginEnum.BOTTOM);
        layout.setSizeFull();

        //Creating the create hakukohde button
        //btnMuokkaa = UiBuilder.buttonSmallSecodary(layout, i18n.getMessage("muokkaa"));
        //btnMuokkaa.setEnabled(false);
        //btnPoista = UiBuilder.buttonSmallSecodary(layout, i18n.getMessage("poista"));
        //btnPoista.setEnabled(false);
        //btnPoista.addListener(new Button.ClickListener() {
			
			/**
			 * 
			 */
	/*		private static final long serialVersionUID = 4580339518654622579L;

			@Override
			public void buttonClick(ClickEvent event) {
				showRemoveDialog();
			}
		});*/
        btnSiirraJaKopioi = UiBuilder.buttonSmallSecodary(layout, i18n.getMessage("siirraTaiKopioi"));
        btnSiirraJaKopioi.setEnabled(false);
        btnSiirraJaKopioi.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(ClickEvent clickEvent) {

               List<KoulutusTulos> valitutKoulutukset = presenter.getSelectedKoulutukset();

                if (valitutKoulutukset != null && valitutKoulutukset.size() > 0 ) {
                    if (valitutKoulutukset.size() > 1) {
                        getWindow().showNotification(i18n.getMessage("yksiKopioitavaKoulutus"));
                    } else {
                           presenter.getModel().setSelectedKoulutusOid(valitutKoulutukset.get(0).getKoulutus().getKomotoOid());
                                KoulutusKopiointiDialog kopiointiDialog = new KoulutusKopiointiDialog("600px", "550px",valitutKoulutukset.get(0).getKoulutus().getKoulutustyyppi());

                                getWindow().addWindow(kopiointiDialog);

                    }

                }


            }
        });
        
        luoHakukohdeB = UiBuilder.buttonSmallSecodary(layout, i18n.getMessage("LuoHakukohde"));
        luoHakukohdeB.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;
            
            @Override
            public void buttonClick(Button.ClickEvent event) {
                IntTuple tuple = checkForLukioKoulutus();
                if (tuple.getValOne() < 1) {
                    List<String> selectedKoulutusOids = presenter.getSelectedKoulutusOids();
                    if (!selectedKoulutusOids.isEmpty()) {
                        showCreateHakukohdeDialog(selectedKoulutusOids);
                    }                    
                } else {
                    if (tuple.getValOne() > 1 || tuple.getValTwo() > tuple.getValOne()) {
                        final Window dialog = new Window();
                        NoKoulutusDialog koulutusDialog = new NoKoulutusDialog("lukioHakukohdeTooMany", new Button.ClickListener() {
                            @Override
                            public void buttonClick(ClickEvent clickEvent) {
                                getWindow().removeWindow(dialog);
                            }
                        });
                        dialog.setContent(koulutusDialog);
                        dialog.setWidth("500px");
                        dialog.setHeight("200px");
                        dialog.setModal(true);
                        dialog.center();
                        getWindow().addWindow(dialog);
                    } else {
                        presenter.showHakukohdeEditView(null, null, presenter.getSelectedKoulutusOidNameViewModels(),null);
                        presenter.getTarjoaja().setSelectedResultRowOrganisationOid(
                        		presenter.getModel().getSelectedKoulutukset().get(0).getKoulutus().getTarjoaja().getTarjoajaOid());
                    }
                }
            }
        });

        //Creating the create koulutus button
        luoKoulutusB = UiBuilder.buttonSmallSecodary(layout, i18n.getMessage("LuoKoulutus"));
        luoKoulutusB.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;
            
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (presenter.availableKoulutus()) {
                    List<String> organisaatioOids = new ArrayList<String>();
                    organisaatioOids.add(presenter.getNavigationOrganisation().getOrganisationOid());
                    UusiKoulutusDialog uusiKoulutusDialog = new UusiKoulutusDialog("800px", "600px");
                    
                    getWindow().addWindow(uusiKoulutusDialog);
                } else {
                    showNoKoulutusDialog("viesti");
                }
            }
        });

        //Creating the sorting options combobox
        cbJarjestys = UiUtil.comboBox(layout, null, ORDER_BY);
        cbJarjestys.setWidth("300px");
        layout.setExpandRatio(luoKoulutusB, 1f);
        layout.setComponentAlignment(luoKoulutusB, Alignment.TOP_RIGHT);
        layout.setComponentAlignment(cbJarjestys, Alignment.TOP_RIGHT);
        
        Button btnInfo = new Button();
        btnInfo.addStyleName(Oph.BUTTON_INFO);
        layout.addComponent(btnInfo);
        
        return layout;
    }
    
    /**
     * Showing the confirmation dialog for removing multiple haku objects.
     *
     */
    private void showRemoveDialog() {
        MultipleKoulutusRemovalDialog removeDialog = new  MultipleKoulutusRemovalDialog(T("removeQ"), T("removeYes"), T("removeNo"), presenter);
        koulutusDialog = new TarjontaDialogWindow(removeDialog, T("removeDialog"));
        getWindow().addWindow(koulutusDialog);
    }
    
    private IntTuple checkForLukioKoulutus() {
        int lukioKoulutusCounter = 0;
        int koulutusCounter = 0;
        for (KoulutusTulos koulutus : presenter.getSelectedKoulutukset()) {
            koulutusCounter++;
            if (koulutus.getKoulutus().getKoulutustyyppi() != null && koulutus.getKoulutus().getKoulutustyyppi().equals(KoulutusasteTyyppi.LUKIOKOULUTUS)) {
                lukioKoulutusCounter++;
            }
        }
        IntTuple tuple = new IntTuple();
        tuple.setValOne(lukioKoulutusCounter);
        tuple.setValTwo(koulutusCounter);
        return tuple;
    }
    
    private void showNoKoulutusDialog(String msg) {
        
        NoKoulutusDialog noKoulutusView = new NoKoulutusDialog(msg, new Button.ClickListener() {
            private static final long serialVersionUID = -5998239901946190160L;
            
            @Override
            public void buttonClick(ClickEvent event) {
                closeKoulutusDialog();
            }
        });
        koulutusDialog = new TarjontaDialogWindow(noKoulutusView, i18n.getMessage("noKoulutusLabel"));
        getWindow().addWindow(koulutusDialog);
    }
    
    public void closeKoulutusDialog() {
        if (koulutusDialog != null) {
            getWindow().removeWindow(koulutusDialog);
        }
    }
    
    private void showCreateHakukohdeDialog(List<String> oids) {
        createDialog = presenter.createHakukohdeCreationDialogWithKomotoOids(oids);
        createButtonListeners();
        createDialog.setWidth("800px");
        createHakukohdeDialog = new Window();
        createHakukohdeDialog.setContent(createDialog);
        createHakukohdeDialog.setModal(true);
        createHakukohdeDialog.center();
        createHakukohdeDialog.setCaption(I18N.getMessage("HakukohdeCreationDialog.windowTitle"));
        getWindow().addWindow(createHakukohdeDialog);
    }
    
    private void createButtonListeners() {
        
        createDialog.getPeruutaBtn().addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (createHakukohdeDialog != null) {
                    getWindow().removeWindow(createHakukohdeDialog);
                }
            }
        });
        
        
        createDialog.getJatkaBtn().addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                createDialog.removeErrorMessages();
                Object values = createDialog.getOptionGroup().getValue();
                Collection<KoulutusOidNameViewModel> selectedKoulutukses = null;
                if (values instanceof Collection) {
                    selectedKoulutukses = (Collection<KoulutusOidNameViewModel>) values;
                }
                
                
                if (selectedKoulutukses.size() > 0) {
                    List<String> validationErrors = presenter.validateKoulutusOidNameViewModel(selectedKoulutukses);
                    if (validationErrors != null && validationErrors.size() > 0) {
                        for (String validationError : validationErrors) {
                            createDialog.addErrorMessage(validationError);
                        }
                    } else {
                        getWindow().removeWindow(createHakukohdeDialog);
                        presenter.showHakukohdeEditView(koulutusNameViewModelToOidList(selectedKoulutukses), null, null,null);
                        
                    }
                    
                }
                
                
            }
        });
        
    }
    
    private List<String> koulutusNameViewModelToOidList(Collection<KoulutusOidNameViewModel> models) {
        List<String> oids = new ArrayList<String>();
        for (KoulutusOidNameViewModel model : models) {
            oids.add(model.getKoulutusOid());
        }
        return oids;
    }
    
    public void closeHakukohdeCreationDialog() {
        if (createHakukohdeDialog != null) {
            getWindow().removeWindow(createHakukohdeDialog);
        }
    }

    /**
     * Reloads the data to the Hakukohde list.
     */
    public void reload() {
        clearAllDataItems();
        //this.btnPoista.setEnabled(false);
        this.btnSiirraJaKopioi.setEnabled(false);
        this.luoHakukohdeB.setEnabled(false);
        categoryTree.setContainerDataSource(createDataSource(presenter.getKoulutusDataSource()));
    }
    
    public void toggleCreateKoulutusB(String organisaatioOid, boolean b) {
        luoKoulutusB.setEnabled(b && presenter.getPermission().userCanCreateKoulutus(OrganisaatioContext.getContext(organisaatioOid)));
    }
    
    public void toggleCreateHakukohdeB(String organisaatioOid, boolean b) {
        boolean enabled = b && presenter.getPermission().userCanCreateHakukohde(OrganisaatioContext.getContext(organisaatioOid));
        this.luoHakukohdeB.setEnabled(enabled);
        this.btnSiirraJaKopioi.setEnabled(enabled);
    }
    /*
    public void togglePoistaB(boolean b) {
    	btnPoista.setEnabled(b);
    }*/

    /**
     * Returns the name of the hakukohde based on koodisto uri given.
     *
     * @param koodistoKoodiTyyppi the koodisto uri given.
     * @return
     */
    private String getKoodiNimi(KoodistoKoodiTyyppi koodistoKoodiTyyppi) {
        String nimi = null;//presenter.getUiHelper().getKoodiNimi(koodistoKoodiTyyppi, I18N.getLocale());
        for (Nimi curNimi :koodistoKoodiTyyppi.getNimi()) {
            if (curNimi.getKieli().equals(I18N.getLocale().getLanguage())) {
                return curNimi.getValue();
            }
        }
        return koodistoKoodiTyyppi.getNimi().get(0).getValue();
    }

    /**
     * Clear all data items from a tree component.
     */
    public void clearAllDataItems() {
        categoryTree.removeAllItems();
    }
    
    private static class IntTuple {
        
        private int valOne;
        private int valTwo;
        
        public int getValOne() {
            return valOne;
        }
        
        public void setValOne(int valOne) {
            this.valOne = valOne;
        }
        
        public int getValTwo() {
            return valTwo;
        }
        
        public void setValTwo(int valTwo) {
            this.valTwo = valTwo;
        }
    }
	
    private String T(String key, Object... args) {
        return i18n.getMessage(key, args);
    }
}
