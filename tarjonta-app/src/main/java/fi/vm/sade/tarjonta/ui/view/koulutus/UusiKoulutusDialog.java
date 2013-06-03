package fi.vm.sade.tarjonta.ui.view.koulutus;

import com.vaadin.data.Property;
import com.vaadin.ui.*;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioPerustietoType;
import fi.vm.sade.tarjonta.ui.helper.KoodistoURIHelper;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.view.common.OrganisaatioSelectDialog;
import fi.vm.sade.vaadin.util.UiUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

/*
 *
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
/**
 *
 * @author Tuomas Katva
 */
@Configurable(preConstruction = true)
public class UusiKoulutusDialog extends OrganisaatioSelectDialog {

    private static final long serialVersionUID = 6240999779746262735L;
    private Logger logger = LoggerFactory.getLogger(UusiKoulutusDialog.class);
    private List<String> organisaatioOids;
    private KoodistoComponent koulutuksenTyyppiCombo;
    private ComboBox koulutusValintaCombo;
    Label pohjakoulutusvaatimusLbl;
    private KoodistoComponent kcPohjakoulutusvaatimus;
    @Autowired
    private TarjontaUIHelper helper;
    private String KOULUTUSTYYPPI_AMM = "koulutustyyppi_1";
    private String KOULUTUSTYYPPI_KK = "koulutustyyppi_3";
    private String KOULUTUSTYYPPI_LUKIO = "koulutustyyppi_2";
    @Value("${tarjonta.showUnderConstruction:false}")
    private boolean underConstraction;
    private Label koulutuksenTyyppiLbl;

    public UusiKoulutusDialog(String width, String height) {
        super(width, height,true);
        //setSizeUndefined();
        //setWidth(width);
        setCaption(_i18n.getMessage("dialog.title"));


    }

    @Override
    protected Collection<String> getOrganisaatioOids() {
        if (organisaatioOids == null) {
            organisaatioOids = new ArrayList<String>();
        }
        organisaatioOids.add(presenter.getNavigationOrganisation().getOrganisationOid());
        return organisaatioOids;
    }

    @Override
    protected void setButtonListeners() {
        peruutaBtn.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                getParent().getWindow().removeWindow(UusiKoulutusDialog.this);
            }
        });

        jatkaBtn.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                errorView.resetErrors();
                if (selectedOrgs == null || selectedOrgs.size() < 1 || selectedOrgs.size() > 1) {
                    errorView.addError(_i18n.getMessage("valitseVahintaanYksiOrganisaatio"));
                    return;
                }
                if(null==koulutuksenTyyppiCombo.getValue()) {
                    errorView.addError(_i18n.getMessage("valitseKoulutusTyyppi"));
                    return;
                }
                List<OrganisaatioPerustietoType> orgs = new ArrayList<OrganisaatioPerustietoType>(selectedOrgs.values());
                if (!checkOppilaitosTyyppi(orgs.get(0), (String) koulutuksenTyyppiCombo.getValue())) {
                    errorView.addError(_i18n.getMessage("tarkistaOppilaitosJaKoulutusaste"));
                    return;
                }

                logger.info("koulutusAsteCombo : {} == {}", koulutuksenTyyppiCombo.getValue(), KOULUTUSTYYPPI_AMM);

                if (koulutuksenTyyppiCombo.getValue() instanceof String
                        && ((String) koulutuksenTyyppiCombo.getValue()).contains(KOULUTUSTYYPPI_AMM)
                        && kcPohjakoulutusvaatimus.getValue() == null) {
                    errorView.addError(_i18n.getMessage("valitsePohjakoulutusvaatimus"));
                    return;
                }

                if (presenter.checkOrganisaatioOppilaitosTyyppimatches(selectedOrgs.values())) {
                    presenter.setAllSelectedOrganisaatios(selectedOrgs.values());

                    if (underConstraction && koulutuksenTyyppiCombo.getValue() instanceof String && ((String) koulutuksenTyyppiCombo.getValue()).contains(KOULUTUSTYYPPI_KK)) {
                        getParent().removeWindow(UusiKoulutusDialog.this);
                        presenter.getKorkeakouluPresenter().showValitseKoulutusDialog();
                      
                    } else if (koulutuksenTyyppiCombo.getValue() instanceof String && ((String) koulutuksenTyyppiCombo.getValue()).contains(KOULUTUSTYYPPI_LUKIO)) {
                        presenter.getLukioPresenter().showLukioKoulutusEditView(selectedOrgs.values());
                        logger.info("lukiokoulutus()");
                        getParent().removeWindow(UusiKoulutusDialog.this);
                    } else if (koulutuksenTyyppiCombo.getValue() instanceof String && ((String) koulutuksenTyyppiCombo.getValue()).contains(KOULUTUSTYYPPI_AMM)) {
                        presenter.showKoulutusEditView(selectedOrgs.values(), (String) kcPohjakoulutusvaatimus.getValue());
                        logger.info("ammatillinen peruskoulutus()");
                        getParent().removeWindow(UusiKoulutusDialog.this);
                    } else {
                        showNotification("Ei toteutettu");
                    }
                } else {
                    addErrorMessage(_i18n.getMessage("oppilaitosTyyppiDoesNotMatch"));
                }
            }
        });
    }

    private boolean checkOppilaitosTyyppi(OrganisaatioPerustietoType org, String tyyppi) {


        List<String> oppilaitosTyyppis = this.presenter.getOppilaitostyyppiUris();
        Collection<KoodiType> koodis = new ArrayList<KoodiType>();
        for (String oppilaitosTyyppi : oppilaitosTyyppis) {
            Collection<KoodiType> curKoodis = helper.getKoodistoRelations(oppilaitosTyyppi, KoodistoURIHelper.KOODISTO_TARJONTA_KOULUTUSASTE, false, SuhteenTyyppiType.SISALTYY);
            koodis.addAll(curKoodis);
        }
        String[] tyyppiUri = TarjontaUIHelper.splitKoodiURI(tyyppi);
        for (KoodiType koodi : koodis) {
            logger.info("koodiuri {} == {}", koodi.getKoodiUri(), tyyppiUri[0]);

            if (koodi.getKoodiUri().equals(tyyppiUri[0])) {
                return true;
            }
        }

        return false;

    }

    @Override
    protected VerticalLayout buildTopLayout() {
        VerticalLayout topLayout = new VerticalLayout();
        errorView = new ErrorMessage();
        topLayout.addComponent(errorView);
        topLayout.addComponent(createLabelLayout());
        topLayout.addComponent(createComboLayout());
        topLayout.setSizeFull();
        return topLayout;
    }

    private AbstractLayout createComboLayout() {
        final GridLayout gridLayout = new GridLayout(4, 2);
        gridLayout.setColumnExpandRatio(0, 0.15f);
        gridLayout.setColumnExpandRatio(1, 0.15f);
        gridLayout.setColumnExpandRatio(2, 0.15f);
        gridLayout.setColumnExpandRatio(3, 0.25f);
        gridLayout.setWidth("798");
        final Label valitseKoulutusLbl = new Label(_i18n.getMessage("valitseKoulutusLbl"));
        gridLayout.addComponent(valitseKoulutusLbl);
        koulutusValintaCombo = buildKoulutusValintaCombo();
        gridLayout.addComponent(koulutusValintaCombo);

        koulutuksenTyyppiLbl = new Label(_i18n.getMessage("koulutuksenTyyppi"));
        gridLayout.addComponent(koulutuksenTyyppiLbl);
        koulutuksenTyyppiCombo = buildKoodistoCombobox(KoodistoURIHelper.KOODISTO_TARJONTA_KOULUTUSASTE);
        koulutuksenTyyppiCombo.setImmediate(true);
        gridLayout.addComponent(koulutuksenTyyppiCombo);
   
        pohjakoulutusvaatimusLbl = new Label(_i18n.getMessage("Pohjakoulutusvaatimus"));
        pohjakoulutusvaatimusLbl.setEnabled(false);
        gridLayout.addComponent(pohjakoulutusvaatimusLbl);
        gridLayout.setComponentAlignment(pohjakoulutusvaatimusLbl, Alignment.MIDDLE_RIGHT);
        kcPohjakoulutusvaatimus = buildKoodistoCombobox(KoodistoURIHelper.KOODISTO_POHJAKOULUTUSVAATIMUKSET_URI);
        kcPohjakoulutusvaatimus.setEnabled(false);

        koulutuksenTyyppiCombo.addListener(new Property.ValueChangeListener() {
            private static final long serialVersionUID = -8476437837944397351L;

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                boolean isEnabled = koulutuksenTyyppiCombo.getValue() instanceof String && ((String) koulutuksenTyyppiCombo.getValue()).contains(KOULUTUSTYYPPI_AMM);
                pohjakoulutusvaatimusLbl.setEnabled(isEnabled);
                kcPohjakoulutusvaatimus.setEnabled(isEnabled);
            }
        });

        gridLayout.addComponent(kcPohjakoulutusvaatimus, 1, 1);

        gridLayout.setMargin(false, false, false, true);

        gridLayout.setHeight("100px");

        return gridLayout;
    }

    private ComboBox buildKoulutusValintaCombo() {
        ComboBox koulutusValintaTmp = new ComboBox();

        koulutusValintaTmp.addItem("Koulutus");
        koulutusValintaTmp.select("Koulutus");
        koulutusValintaTmp.setNullSelectionAllowed(false);

        return koulutusValintaTmp;
    }

    private KoodistoComponent buildKoodistoCombobox(String koodistoUri) {
        return uiBuilder.koodistoComboBox(null, koodistoUri, null);//KoodistoURIHelper.KOODISTO_TARJONTA_KOULUTUSASTE,null);
    }
    //kcPohjakoulutusvaatimus = uiBuilder.koodistoComboBox(null, KoodistoURIHelper.KOODISTO_POHJAKOULUTUSVAATIMUKSET_URI, true);

    private AbstractLayout createLabelLayout() {
        GridLayout labelLayout = new GridLayout(2, 1);
        labelLayout.setColumnExpandRatio(0, 10);
        labelLayout.setColumnExpandRatio(1, 0.1f);
        labelLayout.setMargin(false, true, false, true);

        //HorizontalLayout labelLayout = new HorizontalLayout();
        Label ohjeteksti = new Label(_i18n.getMessage("dialog.ohjeTeksti"));
        //labelLayout.addComponent(ohjeteksti);
        labelLayout.addComponent(ohjeteksti, 0, 0);
        Button ohjeBtn = UiUtil.buttonSmallInfo(null);
        labelLayout.addComponent(ohjeBtn, 1, 0);
        labelLayout.setSizeFull();

        return labelLayout;
    }
}
