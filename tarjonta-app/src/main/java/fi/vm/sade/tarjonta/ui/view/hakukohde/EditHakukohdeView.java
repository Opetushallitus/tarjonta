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
package fi.vm.sade.tarjonta.ui.view.hakukohde;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.google.common.collect.Sets;
import com.vaadin.data.Validator;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.authentication.service.UserService;
import fi.vm.sade.authentication.service.types.dto.HenkiloType;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.service.types.SisaltoTyyppi;
import fi.vm.sade.tarjonta.service.types.TarjontaTila;
import fi.vm.sade.tarjonta.ui.enums.HakukohdeActiveTab;
import fi.vm.sade.tarjonta.ui.enums.SaveButtonState;
import fi.vm.sade.tarjonta.ui.enums.UserNotification;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.HakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusOidNameViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractEditLayoutView;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.HakukohdeValintakoeTabImpl;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.HakukohteenKuvauksetTabImpl;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.HakukohteenLiitteetTabImpl;
import fi.vm.sade.tarjonta.ui.view.hakukohde.tabs.PerustiedotViewImpl;
import fi.vm.sade.vaadin.Oph;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author Tuomas Katva
 */
@Configurable(preConstruction = true)
public class EditHakukohdeView extends AbstractEditLayoutView<HakukohdeViewModel, PerustiedotViewImpl> {

    private static final Logger LOG = LoggerFactory.getLogger(EditHakukohdeView.class);
    private static final long serialVersionUID = 8806220426371090907L;
    @Autowired
    private TarjontaPresenter presenter;
    @Autowired(required = true)
    private transient UiBuilder uiBuilder;
    @Autowired(required = true)
    private TarjontaUIHelper uiHelper;
    @Autowired(required = true)
    private UserService userService;
    private TabSheet tabs;
    private TabSheet.Tab perustiedotTab;
    private TabSheet.Tab liitteetTab;
    private TabSheet.Tab valintakokeetTab;
    private TabSheet.Tab kuvauksetTab;
    private HakukohteenLiitteetTabImpl liitteet;
    private HakukohdeValintakoeTabImpl valintakokeet;
    private PerustiedotViewImpl perustiedot;
    private HakukohteenKuvauksetTabImpl kuvaukset;
    private HakukohdeActiveTab activeTab = HakukohdeActiveTab.PERUSTIEDOT;

    @Value("${koodisto-uris.yhteishaku}")
    private String hakutapaYhteishakuUrl;

    @Value("${koodisto-uris.lisahaku}")
    private String hakutyyppiLisahakuUrl;

    @Value("${koodisto-uris.erillishaku}")
    private String hakutapaErillishaku;
    
    
    private VerticalLayout hl;
    public static final String DATE_PATTERN = "dd.MM.yyyy HH:mm";

    public EditHakukohdeView(String oid) {
        super(oid, SisaltoTyyppi.HAKUKOHDE);
        addTopInfoMessage(oid);
        setMargin(true);
        setHeight(-1, UNITS_PIXELS);

    }

    private void enableDeEnableSaveButtonsForHaku() {


      final HakukohdeViewModel hakukohde = presenter.getModel().getHakukohde();
      
      final fi.vm.sade.tarjonta.service.types.TarjontaTila tila = hakukohde!=null?hakukohde.getTila():null;
      
      
      if (hakukohde != null) {
          
          final boolean hasEditPermission = presenter.isHakukohdeEditableForCurrentUser();
          
              
              //saa tallentaa draftina vain jos tila draft tai null
              final boolean saveableAsDraft = tila==null || TarjontaTila.LUONNOS.equals(tila)
                                                || TarjontaTila.KOPIOITU.equals(tila);
              
              enableButtonByListener(clickListenerSaveAsDraft, saveableAsDraft && hasEditPermission); 
              enableButtonByListener(clickListenerSaveAsReady, hasEditPermission);
      }



    }

    public static Date getMinHakuAlkamisDate(Date hakualkamisPvm) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(hakualkamisPvm);
        cal.add(Calendar.DATE, -4);
        return cal.getTime();
    }
    /*
     *  Prints out the hakukohde name and last update date and updater
     */

    private void addTitleLayout() {
        try {
            boolean addLayout = false;
            if (hl != null) {
                hl.removeAllComponents();
                addLayout = false;
            } else {
                hl = new VerticalLayout();

                addLayout = true;
            }
            hl.setWidth("100%");
            hl.setSizeFull();
            if (presenter.getModel().getHakukohde() != null && presenter.getModel().getHakukohde().getOid() != null) {
                HakukohdeViewModel hkVM = presenter.getModel().getHakukohde();
                String hkLabelStr = hkVM.getKoulutusasteTyyppi().equals(KoulutusasteTyyppi.VAPAAN_SIVISTYSTYON_KOULUTUS) 
                        ? hkVM.getEditedHakukohdeNimi() : uiHelper.getKoodiNimi(hkVM.getHakukohdeNimi());
                Label hakukohdeNameLbl = new Label(hkLabelStr);
                hakukohdeNameLbl.setStyleName(Oph.LABEL_H1);

                StringBuilder tilaSb = new StringBuilder();
                tilaSb.append("( ");
                tilaSb.append(hkVM.getTila().value());
                tilaSb.append(" ,tallennettu ");
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
                tilaSb.append(sdf.format(hkVM.getViimeisinPaivitysPvm()));
                tilaSb.append(", ");


                tilaSb.append(tryGetViimPaivittaja(hkVM.getViimeisinPaivittaja()));
                tilaSb.append(")");
                Label tilaLbl = new Label(tilaSb.toString());

                HorizontalLayout hll = new HorizontalLayout();
                hl.setMargin(true, false, false, false);
                hll.setSizeFull();

                hll.addComponent(tilaLbl);


                hl.addComponent(hakukohdeNameLbl);
                hl.addComponent(hll);
                hl.setComponentAlignment(hakukohdeNameLbl, Alignment.MIDDLE_LEFT);
                hl.setComponentAlignment(hll, Alignment.MIDDLE_RIGHT);
                hl.setMargin(false, false, true, false);
                if (addLayout) {
                    addComponent(hl);
                }
                hll.setWidth("100%");
                hll.setComponentAlignment(tilaLbl, Alignment.MIDDLE_RIGHT);
            } else {
                Label uusiHakukohdeLbl = new Label(T("uusiHakukohdeLbl"));
                uusiHakukohdeLbl.setStyleName(Oph.LABEL_H1);
                hl.addComponent(uusiHakukohdeLbl);
                hl.setMargin(false, false, true, false);
                addComponent(hl);
            }

        } catch (Exception exp) {
            //No worries unable to create info layout, who cares. Log the exception and move on with your life
            LOG.warn("Unable to create hakukohde update info layout: {}", exp.toString());
        }
    }

    private String tryGetViimPaivittaja(String viimPaivittajaOid) {
        try {
            String userName = null;
            if(viimPaivittajaOid!=null) {
                HenkiloType henkilo = userService.findByOid(viimPaivittajaOid);

                if (henkilo.getEtunimet() != null && henkilo.getSukunimi() != null) {
                    userName = henkilo.getEtunimet() + " " + henkilo.getSukunimi();
                } else {
                    userName = henkilo.getKayttajatiedot().getUsername();
                }
            return userName;
            }
        } catch (Exception exp) {
            LOG.warn("Unable to get user with oid : {} exception : {}", viimPaivittajaOid, exp.toString());
        }

        //fall back to viimPaivittajaOid
        return viimPaivittajaOid;
    }
    /*
     * Prints out the hakukohde's attached koulutukset.
     */

    private void addTopInfoMessage(String oid) {

        try {
            VerticalLayout vl = new VerticalLayout();

            vl.setMargin(true);
            List<KoulutusOidNameViewModel> koulutusOidNameViewModels = null;
            if (oid != null) {
                koulutusOidNameViewModels = presenter.getHakukohdeKoulutukses(oid);
            } else {
                koulutusOidNameViewModels = presenter.getModel().getHakukohdeTitleKoulutukses();
            }

            vl.addComponent(buildKoulutuksetInfo(koulutusOidNameViewModels));
            vl.setMargin(false, false, true, false);
            super.setTopInfoLayout(vl);
        } catch (Exception exp) {
            //No worries unable to create info layout, who cares. Log the exception and move on with your life
            LOG.warn("Unable to create hakukohde koulutus info layout: {}", exp.toString());

        }

    }

    private AbstractLayout buildKoulutuksetInfo(List<KoulutusOidNameViewModel> koulutukses) {
        GridLayout gl = new GridLayout(2, 1);
        gl.setSizeFull();
        gl.setColumnExpandRatio(0, 0.13f);
        gl.setColumnExpandRatio(1, 0.87f);
        if (koulutukses != null) {
            Label firstLine = new Label(getI18n().getMessage("valitutKoulutuksetTitle") + " ");
            firstLine.setStyleName(Oph.LABEL_SMALL);
            gl.addComponent(firstLine);
            Label firstName = new Label(koulutukses.get(0).getKoulutusNimi());
            firstName.setStyleName(Oph.LABEL_SMALL);
            gl.addComponent(firstName);


            gl.newLine();
            int counter = 0;
            for (KoulutusOidNameViewModel oidNameViewModel : koulutukses) {
                if (counter == 0) {

                    counter++;
                } else {
                    Label empty = new Label("");
                    gl.addComponent(empty);
                    Label nameLbl = new Label(oidNameViewModel.getKoulutusNimi());
                    nameLbl.setStyleName(Oph.LABEL_SMALL);
                    gl.addComponent(nameLbl);

                    gl.newLine();
                    counter++;
                }
            }
        }
        return gl;
    }

    public void enableValintakokeetTab() {
        if (valintakokeetTab != null) {
            valintakokeetTab.setEnabled(true);
        }
    }

    public void enableLiitteetTab() {
        if (liitteetTab != null) {
            liitteetTab.setEnabled(true);
        }
    }

    public void loadLiiteTableWithData() {
        if (liitteet != null) {
            liitteet.loadTableWithData();
        }
    }

    public void loadValintakokees() {
        if (valintakokeet != null && valintakokeet.getFormView() != null) {
            //reload model data to layout
            valintakokeet.getFormView().reloadTableDataValintaKokees();
           
        }
    }

    public void closeValintakoeEditWindow() {
        if (valintakokeet != null) {
            valintakokeet.getFormView().closeValintakoeEditWindow();
        }
    }

    public void closeHakukohdeLiiteEditWindow() {
        if (liitteet != null) {
            liitteet.closeEditWindow();
        }
    }

    public void showHakukohdeValintakoeEditView(String valintakoeId) {
        if (valintakokeet != null) {
            valintakokeet.getFormView().showValintakoeEditWithId(valintakoeId);
        }
    }

    public void showHakukohdeEditWindow(String liiteId) {
        if (liitteet != null) {
            liitteet.showHakukohdeEditWindow(liiteId);
        }
    }

    @Override
    public boolean isformDataLoaded() {
        return isLoaded();
    }

    private boolean isLoaded() {
        return getHakukohdeOid() != null ? true : false;
    }

    private String getHakukohdeOid() {
        return presenter.getModel().getHakukohde() != null ? presenter.getModel().getHakukohde().getOid() : null;
    }

    /**
     * Handle lukio hakukohde save action. Tabs: - attachments - exam points
     *
     * @param tila
     * @param event
     * @return
     * @throws Exception
     */
    @Override
    public String actionSave(SaveButtonState tila, Button.ClickEvent event) throws Exception {
        HakukohdeViewModel hakukohde = presenter.getModel().getHakukohde();

        /*Date today = new Date();
        if (hakukohde.getLiitteidenToimitusPvm() != null && hakukohde.getLiitteidenToimitusPvm().before(today)) {
            errorView.addError(T("hakukohdeLiiteToimPvmMenneessa"));
            return null;
        }*/

        hakukohde.getLisatiedot().clear();
        hakukohde.getLisatiedot().addAll(perustiedot.getLisatiedot());
        hakukohde.getValintaPerusteidenKuvaus().getKaannokset().clear();
        hakukohde.getValintaPerusteidenKuvaus().getKaannokset().addAll(perustiedot.getValintaperusteet());
        hakukohde.setHakuaika(perustiedot.getSelectedHakuaika());
        if (hakukohde.getHakuaikaAlkuPvm() != null && hakukohde.getHakuaikaLoppuPvm() != null) {
          validateHakukohdeHakuaika(hakukohde);  
        } 
        if (presenter.isKoulutusNivelvaihe() || !perustiedot.isSahkoinenToimOsoiteChecked()) {
            hakukohde.setLiitteidenSahkoinenToimitusOsoite("");
        }
        // TODO call subform to perform validation (weigthed stdies can FAIL and still the save succeeds)
        // formView.validateExtraData();
        if (presenter.getModel().getHakukohde().getKoulutusasteTyyppi().equals(KoulutusasteTyyppi.LUKIOKOULUTUS)) {
            for (TextField tf : perustiedot.getPainotettavat()) {
                tf.validate();
            }

            Set<Object> usedOppiaineet = Sets.newHashSet();
            GridLayout painotettavat = perustiedot.getPainotettavatOppiaineet();
            if (painotettavat != null) {
                for (int i = 1; i < painotettavat.getRows(); i++) {
                    //the first row item is a label object, skip it or data type conversion fails.
                    final KoodistoComponent kc = (KoodistoComponent) painotettavat.getComponent(0, i);
                    final TextField tf = (TextField) painotettavat.getComponent(1, i);
                    if (kc instanceof KoodistoComponent) {
                        final String oppiaine = (String) kc.getValue();
                        final String painokerroin = (String) tf.getValue();
                        if ((oppiaine == null && painokerroin != null) || (oppiaine != null && oppiaine.isEmpty() && painokerroin != null)) {
                            throw new Validator.InvalidValueException(I18N.getMessage("validation.PerustiedotView.painotettavat.invalidCombination"));
                        } else if (oppiaine != null) {
                            if (usedOppiaineet.contains(oppiaine)) {
                                throw new Validator.InvalidValueException(I18N.getMessage("validation.PerustiedotView.painotettavat.duplicate"));
                            }
                            usedOppiaineet.add(oppiaine);
                        }
                    }
                }
            }

            if (valintakokeetTab.isEnabled()) {
                try {
                    this.valintakokeet.getFormView().getPisterajaTable().bindValintakoeData(null);
                    this.valintakokeet.getFormView().getPisterajaTable().bindLisapisteData(null);
                    valintakokeet.validateLukioValintakoeForm();
                } catch (Validator.InvalidValueException e) {
                    errorView.addError(T("tarkistaValintakoe"));
                    throw e;
                }
            }
        }

        /*
         * TODO: there should be only one save method with rollback functionality...
         */
        presenter.saveHakuKohde(tila);
        valintakokeet.actionSave(null, null);

        setModel(presenter.getModel().getHakukohde());

        addTitleLayout();
        return getHakukohdeOid();
    }
    
    private void validateHakukohdeHakuaika(HakukohdeViewModel hakukohde) {
        if (hakukohde.getHakuaika() == null) {
            throw new Validator.InvalidValueException(I18N.getMessage("validation.PerustiedotView.hakuaika.notSelected"));
        }
        Date specialAlku = hakukohde.getHakuaikaAlkuPvm();
        Date specialLoppu = hakukohde.getHakuaikaLoppuPvm();
        Date hakuaikaAlku = hakukohde.getHakuaika().getAlkamisPvm();
        Date hakuaikaLoppu = hakukohde.getHakuaika().getPaattymisPvm();
        if (specialAlku.before(hakuaikaAlku) || specialAlku.after(hakuaikaLoppu) || specialLoppu.before(hakuaikaAlku) || specialLoppu.after(hakuaikaLoppu)) {
            throw new Validator.InvalidValueException(I18N.getMessage("validation.PerustiedotView.hakuajat.outOfRange"));
        }   
    }

    @Override
    protected void eventBack(Button.ClickEvent event) {
        presenter.showMainDefaultView();
        presenter.getHakukohdeListView().reload();
    }

    @Override
    public void actionNext(Button.ClickEvent event) {

        if (getHakukohdeOid() != null) {
            presenter.showHakukohdeViewImpl(getHakukohdeOid());
        }
    }

    @Override
    protected void buildLayout(VerticalLayout layout) {
        super.buildLayout(layout);

        addTitleLayout();
        String hakukohdeOid = null;
        if (presenter.getModel().getHakukohde() != null && presenter.getModel().getHakukohde().getOid() != null) {
            hakukohdeOid = presenter.getModel().getHakukohde().getOid();
        }

        tabs = UiBuilder.tabSheet(layout);
        layout.setMargin(false, false, true, false);
        final VerticalLayout wrapperVl = new VerticalLayout();
        perustiedot = new PerustiedotViewImpl(presenter, uiBuilder);

        buildFormLayout(presenter, wrapperVl, presenter.getModel().getHakukohde(), perustiedot);

        liitteet = new HakukohteenLiitteetTabImpl(presenter);
        valintakokeet = new HakukohdeValintakoeTabImpl(hakukohdeOid, presenter.getModel().getHakukohde().getKoulutusasteTyyppi());
        kuvaukset = new HakukohteenKuvauksetTabImpl(presenter);
        
        perustiedotTab = tabs.addTab(wrapperVl, T("tabNimi"));
        valintakokeetTab = tabs.addTab(valintakokeet, T("valintakoeTab"));
        liitteetTab = tabs.addTab(liitteet, T("liitteetTab"));
        if (presenter.getModel().getHakukohde().getKoulutusasteTyyppi().equals(KoulutusasteTyyppi.KORKEAKOULUTUS)) {
            kuvauksetTab = tabs.addTab(kuvaukset, T("sovapeTab"));
        }
        
        
        liitteetTab.setEnabled(hakukohdeOid != null);
        valintakokeetTab.setEnabled(hakukohdeOid != null);
        
        //Listener to handle tab changes (whether to allow or not)
        tabs.addListener(new SelectedTabChangeListener() {

            private static final long serialVersionUID = -3995507767832431214L;

            @Override
            public void selectedTabChange(SelectedTabChangeEvent event) {
                switch (activeTab) {
                case PERUSTIEDOT :
                    handlePerustiedotTabChange(wrapperVl);
                    break;
                case VALINTAKOKEET:
                    handleValintakokeetTabChange(wrapperVl);
                    break;
                case KUVAUKSET:
                	handleKuvauksetTabChange(wrapperVl);
                 default:
                     changeTab(wrapperVl);
                }
            }
            
        });
        makeFormDataUnmodified();
        enableDeEnableSaveButtonsForHaku();
    }

    /*
     * Handles tab change in the case that user's current tab is perustiedot 
     */
    private void handlePerustiedotTabChange(VerticalLayout wrapperVl) {
        HakukohdeViewModel hakukohde = presenter.getModel().getHakukohde();
        hakukohde.getLisatiedot().clear();
        hakukohde.getLisatiedot().addAll(perustiedot.getLisatiedot());
        hakukohde.getValintaPerusteidenKuvaus().getKaannokset().clear();
        hakukohde.getValintaPerusteidenKuvaus().getKaannokset().addAll(perustiedot.getValintaperusteet());
        if (!this.canTabBeChanged()) {
            tabs.setSelectedTab(wrapperVl);
            presenter.showNotification(UserNotification.UNSAVED);
        } else {
            changeTab(wrapperVl);
        }
    }
    
    /*
     * Handles tab change in the case that user's current tab is valintakokeet
     */
    private void handleValintakokeetTabChange (VerticalLayout perustiedotContainer) {
        if (KoulutusasteTyyppi.LUKIOKOULUTUS.equals(valintakokeet.getModel().getKoulutusasteTyyppi()) 
                && !isLukioValintakoeTabChangeable() ) {
            tabs.setSelectedTab(valintakokeet);
            presenter.showNotification(UserNotification.UNSAVED);
        } else {
            changeTab(perustiedotContainer);
        }
    }
        
    /*
     * Handles tab change in the case that user's current tab is valintakokeet
     */
    private void handleKuvauksetTabChange (VerticalLayout perustiedotContainer) {
        if (kuvaukset.isChanged()) {
            tabs.setSelectedTab(kuvaukset);
            presenter.showNotification(UserNotification.UNSAVED);
        } else {
            changeTab(perustiedotContainer);
        }
    }
        
    /*
     * Changes the tab
     */
    private void changeTab(VerticalLayout perustiedotContainer) {
        if (tabs.getSelectedTab().equals(valintakokeet)) {
            activeTab = HakukohdeActiveTab.VALINTAKOKEET; 
            valintakokeet.makeFormDataUnmodified();
        } else if (tabs.getSelectedTab().equals(perustiedotContainer)) {
            activeTab = HakukohdeActiveTab.PERUSTIEDOT;
        } else if (tabs.getSelectedTab().equals(liitteet)) {
            activeTab = HakukohdeActiveTab.LIITTEET;   
        } else if (tabs.getSelectedTab().equals(kuvaukset)) {
        	activeTab = HakukohdeActiveTab.KUVAUKSET;
        }
        makeFormDataUnmodified();
    }
    
    @Override
    public int makeFormDataUnmodified() {
    	if (kuvaukset!=null) {
    		kuvaukset.setChanged(false);
    	}
    	return super.makeFormDataUnmodified();
    }

    /*
     * Checks whether there are unsaved changes in lukio valintakoe tab 
     */
    private boolean isLukioValintakoeTabChangeable() {
        this.valintakokeet.getFormView().getPisterajaTable().bindValintakoeData(presenter.getModel().getSelectedValintaKoe());
        this.valintakokeet.getFormView().getPisterajaTable().bindLisapisteData(presenter.getModel().getSelectedValintaKoe());
        presenter.getModel().getSelectedValintaKoe().setSanallisetKuvaukset(valintakokeet.getFormView().getLukioValintakoeView().getValintakokeenKuvaukset());
        presenter.getModel().getSelectedValintaKoe().setLisanayttoKuvaukset(this.valintakokeet.getFormView().getLisanayttoKuvaukset());
        if (valintakokeet.isSaved()) {
            try {
                this.valintakokeet.getFormView().getPisterajaTable().bindValintakoeData(null);
                this.valintakokeet.getFormView().getPisterajaTable().bindLisapisteData(null);
                valintakokeet.validateLukioValintakoeForm();
                return true;
            } catch (Validator.InvalidValueException ex) {
                valintakokeet.getErrorView().addError(ex);
                return false;
            } catch (Exception e) {
                LOG.error("An unknown application error - persist failed, message :  " + e.getMessage(), e);
                presenter.showNotification(UserNotification.TAB_CHANGE_UNEXPECTED_ERROR);
                return false;
            }
        } 
        return false;
    }
    
    /*
     * Checks whether perustiedot data is unchanged
     */
    private boolean canTabBeChanged() {
        if (!this.isSaved()) {
            try {
                this.validateFormData();
            } catch (Validator.InvalidValueException e) {
                errorView.addError(e);
            }
            return false;
        }
        
        try {
            validateFormData();
            return true;
        } catch (Validator.InvalidValueException e) {
            errorView.addError(e);
            return false;
        }
    }

    public void setValintakokeetTabSelected() {
        if (tabs != null && valintakokeetTab != null) {

            tabs.setSelectedTab(valintakokeetTab);
            activeTab = HakukohdeActiveTab.VALINTAKOKEET;
        }
        
    }

    public HakukohteenLiitteetTabImpl getLiitteetTab() {
        return liitteet;
    }

    public HakukohdeValintakoeTabImpl getValintakoeTab() {
        return valintakokeet;
    }

    public void setLiitteetTabSelected() {
        if (tabs != null && liitteetTab != null) {
            tabs.setSelectedTab(liitteetTab);
            activeTab = HakukohdeActiveTab.LIITTEET;
        }
    }

    public void refreshValintaKokeetLastUpdatedBy() {
        if (valintakokeet != null) {
            valintakokeet.refreshLastUpdatedBy();
        }
    }

    public void refreshOppiaineet() {
        if (perustiedot != null) {
            perustiedot.refreshOppiaineet();
        }
    }
}
