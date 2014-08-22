package fi.vm.sade.tarjonta.ui.view.koulutus;

import com.google.common.base.Preconditions;
import com.vaadin.data.Property;
import com.vaadin.ui.*;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.ui.validation.ErrorMessage;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.organisaatio.api.search.OrganisaatioPerustieto;
import fi.vm.sade.tarjonta.shared.KoodistoURI;
import fi.vm.sade.tarjonta.shared.TarjontaKoodistoHelper;
import fi.vm.sade.tarjonta.ui.enums.Koulutustyyppi;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.view.common.OrganisaatioSelectDialog;
import fi.vm.sade.vaadin.util.UiUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

/*
 *
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */
/**
 *
 * @author Tuomas Katva
 */
@Configurable(preConstruction = true)
public class UusiKoulutusDialog extends OrganisaatioSelectDialog {

    private static class KoodiContainer {

        KoodiType koodiType;

        public KoodiType getKoodiType() {
            return koodiType;
        }

        public KoodiContainer(KoodiType koodiType) {
            this.koodiType = koodiType;
        }

        @Override
        public String toString() {
            return TarjontaUIHelper.getKoodiMetadataForLanguage(koodiType, I18N.getLocale()).getNimi();
        }

        @Override
        public int hashCode() {
            return koodiType.hashCode();
        }
    }
    private static final long serialVersionUID = 6240999779746262735L;
    private Logger logger = LoggerFactory.getLogger(UusiKoulutusDialog.class);
    private List<String> organisaatioOids;
    private ComboBox koulutuksenTyyppiCombo;
    private ComboBox koulutusValintaCombo;
    Label pohjakoulutusvaatimusLbl;
    private ComboBox pohjakoulutusvaatimusCombo;
    @Autowired
    private TarjontaUIHelper helper;

    public UusiKoulutusDialog(String width, String height) {
        super(width, height, true);
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
                if (null == koulutuksenTyyppiCombo.getValue()) {
                    errorView.addError(_i18n.getMessage("valitseKoulutusTyyppi"));
                    return;
                }
                List<OrganisaatioPerustieto> orgs = new ArrayList<OrganisaatioPerustieto>(selectedOrgs.values());
                if (!checkOppilaitosTyyppi(orgs.get(0), ((KoodiContainer) koulutuksenTyyppiCombo.getValue()).koodiType.getKoodiUri())) {
                    errorView.addError(_i18n.getMessage("tarkistaOppilaitosJaKoulutusaste"));
                    return;
                }

                logger.info("koulutusAsteCombo : {} == {}", koulutuksenTyyppiCombo.getValue(), Koulutustyyppi.TOINEN_ASTE_AMMATILLINEN_KOULUTUS.getKoulutustyyppiUri());

                if (koulutuksenTyyppiCombo.getValue() instanceof KoodiContainer
                        && (((KoodiContainer) koulutuksenTyyppiCombo.getValue()).getKoodiType().getKoodiUri().contains(Koulutustyyppi.TOINEN_ASTE_AMMATILLINEN_KOULUTUS.getKoulutustyyppiUri())
                                || ((KoodiContainer) koulutuksenTyyppiCombo.getValue()).getKoodiType().getKoodiUri().contains(Koulutustyyppi.TOINEN_ASTE_AMMATILLINEN_ERITYISKOULUTUS.getKoulutustyyppiUri())
                                || ((KoodiContainer) koulutuksenTyyppiCombo.getValue()).getKoodiType().getKoodiUri().contains(Koulutustyyppi.TOINEN_ASTE_VALMENTAVA_KOULUTUS.getKoulutustyyppiUri())
                                || ((KoodiContainer) koulutuksenTyyppiCombo.getValue()).getKoodiType().getKoodiUri().contains(Koulutustyyppi.PERUSOPETUKSEN_LISAOPETUS.getKoulutustyyppiUri())
                                || ((KoodiContainer) koulutuksenTyyppiCombo.getValue()).getKoodiType().getKoodiUri().contains(Koulutustyyppi.AMMATILLISEEN_OHJAAVA_KOULUTUS.getKoulutustyyppiUri())
                                || ((KoodiContainer) koulutuksenTyyppiCombo.getValue()).getKoodiType().getKoodiUri().contains(Koulutustyyppi.MAMU_AMMATILLISEEN_OHJAAVA_KOULUTUS.getKoulutustyyppiUri())
                                || ((KoodiContainer) koulutuksenTyyppiCombo.getValue()).getKoodiType().getKoodiUri().contains(Koulutustyyppi.MAMU_LUKIOON_OHJAAVA_KOULUTUS.getKoulutustyyppiUri())
                                || ((KoodiContainer) koulutuksenTyyppiCombo.getValue()).getKoodiType().getKoodiUri().contains(Koulutustyyppi.VAPAAN_SIVISTYSTYON_KOULUTUS.getKoulutustyyppiUri()))
                        && pohjakoulutusvaatimusCombo.getValue() == null) {
                    errorView.addError(_i18n.getMessage("valitsePohjakoulutusvaatimus"));
                    return;
                }

                if (presenter.checkOrganisaatioOppilaitosTyyppimatches(selectedOrgs.values())) {
                    presenter.setAllSelectedOrganisaatios(selectedOrgs.values());

                    KoodiType type = koulutuksenTyyppiCombo.getValue() instanceof KoodiContainer ? ((KoodiContainer) (koulutuksenTyyppiCombo.getValue())).koodiType : null;

                    if (type != null) {
                        if (contains(type, Koulutustyyppi.TOINEN_ASTE_LUKIO)) {
                            presenter.getLukioPresenter().showLukioKoulutusEditView(selectedOrgs.values());
                            logger.info("lukiokoulutus()");
                            getParent().removeWindow(UusiKoulutusDialog.this);
                        } else if (contains(type, Koulutustyyppi.TOINEN_ASTE_AMMATILLINEN_KOULUTUS) 
                                || contains(type, Koulutustyyppi.TOINEN_ASTE_AMMATILLINEN_ERITYISKOULUTUS) 
                                || contains(type, Koulutustyyppi.TOINEN_ASTE_VALMENTAVA_KOULUTUS)
                                || contains(type, Koulutustyyppi.PERUSOPETUKSEN_LISAOPETUS)
                                || contains(type, Koulutustyyppi.AMMATILLISEEN_OHJAAVA_KOULUTUS)
                                || contains(type, Koulutustyyppi.MAMU_AMMATILLISEEN_OHJAAVA_KOULUTUS)
                                || contains(type, Koulutustyyppi.MAMU_LUKIOON_OHJAAVA_KOULUTUS)
                                || contains(type, Koulutustyyppi.VAPAAN_SIVISTYSTYON_KOULUTUS)) {

                            //
                            // Get selected pohjakoulutus
                            //
                            KoodiContainer koodiContainer = (KoodiContainer) pohjakoulutusvaatimusCombo.getValue();
                            String pohjakoulutusvaatimusUri = null;
                            if (koodiContainer != null) {
                                pohjakoulutusvaatimusUri = TarjontaKoodistoHelper.createKoodiUriWithVersion(koodiContainer.getKoodiType());
                            }

                            logger.info("Go to koulutus edit view with pk vaatimus: " + pohjakoulutusvaatimusUri);
                            
                            presenter.showKoulutusEditView(selectedOrgs.values(), pohjakoulutusvaatimusUri, type);

                            logger.info("  closing dialog.");
                            getParent().removeWindow(UusiKoulutusDialog.this);
                        } else {
                            showNotification("Ei toteutettu");
                        }
                    }
                } else {
                    addErrorMessage(_i18n.getMessage("oppilaitosTyyppiDoesNotMatch"));
                }
            }
        });
    }

    private static boolean contains(final KoodiType type, final Koulutustyyppi koulutustyyppi) {
        Preconditions.checkNotNull(koulutustyyppi, "Koulutustyyppi cannot be null.");
        return type.getKoodiUri().contains(koulutustyyppi.getKoulutustyyppiUri());
    }

    private boolean checkOppilaitosTyyppi(OrganisaatioPerustieto org, String tyyppiUri) {
        List<String> oppilaitosTyyppis = this.presenter.getOppilaitostyyppiUris(org.getOid());

        return helper.hasRelationKoulutustyyppiToOppilaitostyyppi(tyyppiUri, oppilaitosTyyppis);
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
        final GridLayout gridLayout = new GridLayout(3, 2);
        gridLayout.setColumnExpandRatio(0, 0.25f);
        gridLayout.setColumnExpandRatio(1, 0.10f);
        gridLayout.setColumnExpandRatio(2, 0.5f);
        //gridLayout.setColumnExpandRatio(3, 0.25f);
        gridLayout.setWidth("800");
        final Label valitseKoulutusLbl = new Label(_i18n.getMessage("valitseKoulutusLbl"));
        gridLayout.addComponent(valitseKoulutusLbl);
        koulutusValintaCombo = buildKoulutusValintaCombo();
        gridLayout.addComponent(koulutusValintaCombo);

        //koulutuksenTyyppiLbl = new Label(_i18n.getMessage("koulutuksenTyyppi"));
        //gridLayout.addComponent(koulutuksenTyyppiLbl);
        koulutuksenTyyppiCombo = uiBuilder.comboBox(null, null, null);//buildKoodistoCombobox(KoodistoURI.KOODISTO_TARJONTA_KOULUTUSTYYPPI);

        koulutuksenTyyppiCombo.setImmediate(true);
        List<String> oppilaitostyypit = super.presenter.getOppilaitostyyppiUris();
        buildKoulutustyyppiCombo(oppilaitostyypit);

        gridLayout.addComponent(koulutuksenTyyppiCombo);
        //gridLayout.addComponent(new Label());
        pohjakoulutusvaatimusLbl = new Label(_i18n.getMessage("Pohjakoulutusvaatimus"));
        pohjakoulutusvaatimusLbl.setVisible(false);
        gridLayout.addComponent(pohjakoulutusvaatimusLbl, 0, 1);

        //gridLayout.setComponentAlignment(pohjakoulutusvaatimusLbl, Alignment.MIDDLE_RIGHT);
        pohjakoulutusvaatimusCombo = uiBuilder.comboBox(null, null, null);//buildKoodistoCombobox(KoodistoURI.KOODISTO_POHJAKOULUTUSVAATIMUKSET_URI);
        
        pohjakoulutusvaatimusCombo.setVisible(false);

        koulutuksenTyyppiCombo.addListener(new Property.ValueChangeListener() {
            private static final long serialVersionUID = -8476437837944397351L;

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                handleKoulutuksenTyyppiChanged(event);
                
            }
        });

        //gridLayout.addComponent(kcPohjakoulutusvaatimus, 1, 1);
        //pohjakoulutusvaatimusCombo.setWidth("100%");
        pohjakoulutusvaatimusCombo.setWidth("200px");
        gridLayout.addComponent(pohjakoulutusvaatimusCombo, 1, 1, 2, 1);
        gridLayout.setMargin(false, false, false, true);

        gridLayout.setHeight("100px");

        return gridLayout;
    }
    
    private void handleKoulutuksenTyyppiChanged(Property.ValueChangeEvent event) {
        
        if (!(koulutuksenTyyppiCombo.getValue() instanceof KoodiContainer)) {
            pohjakoulutusvaatimusLbl.setVisible(false);
            pohjakoulutusvaatimusCombo.setVisible(false);
            return;
        }
        String koodiUri = ((KoodiContainer) koulutuksenTyyppiCombo.getValue()).koodiType.getKoodiUri();
        
        
        this.buildPohjakoulutusvaatimusCombo(koodiUri);
        
    }
    
    private void buildPohjakoulutusvaatimusCombo(String koodiUri) {
        Collection<KoodiType> koodit = helper.getKoodistoRelations(koodiUri, KoodistoURI.KOODISTO_POHJAKOULUTUSVAATIMUKSET_URI);
        if (koodit == null) {    
            return;
        }
        
        this.pohjakoulutusvaatimusCombo.removeAllItems();
        for(KoodiType curKoodi : koodit) {
            this.pohjakoulutusvaatimusCombo.addItem(new KoodiContainer(curKoodi));
        }
        
        if (this.pohjakoulutusvaatimusCombo.getItemIds().size() == 1) {
            this.pohjakoulutusvaatimusCombo.setValue(this.pohjakoulutusvaatimusCombo.getItemIds().iterator().next());
        } 
        boolean isVisible = (this.pohjakoulutusvaatimusCombo.getItemIds().size() > 1) 
                                && !koodiUri.contains(Koulutustyyppi.TOINEN_ASTE_LUKIO.getKoulutustyyppiUri());

        
        pohjakoulutusvaatimusLbl.setVisible(isVisible);
        pohjakoulutusvaatimusCombo.setVisible(isVisible);
    }

    private ComboBox buildKoulutusValintaCombo() {
        ComboBox koulutusValintaTmp = new ComboBox();

        koulutusValintaTmp.addItem("Koulutus");
        koulutusValintaTmp.select("Koulutus");
        koulutusValintaTmp.setNullSelectionAllowed(false);

        return koulutusValintaTmp;
    }

    private KoodistoComponent buildKoodistoCombobox(String koodistoUri) {
        return uiBuilder.koodistoComboBox(null, koodistoUri, null);
    }

    private AbstractLayout createLabelLayout() {
        GridLayout labelLayout = new GridLayout(2, 1);
        labelLayout.setColumnExpandRatio(0, 10);
        labelLayout.setColumnExpandRatio(1, 0.1f);
        labelLayout.setMargin(false, true, false, true);

        //HorizontalLayout labelLayout = new HorizontalLayout();
        Label ohjeteksti = new Label(_i18n.getMessage("dialog.ohjeTeksti"));
        //labelLayout.addComponent(ohjeteksti);
        labelLayout.addComponent(ohjeteksti, 0, 0);
        labelLayout.setSizeFull();

        return labelLayout;
    }

    /**
     * Popukate koulutustyyppi combo
     *     
* @param oppilaitostyyppiUrit
     */
    private void buildKoulutustyyppiCombo(List<String> oppilaitostyyppiUrit) {
        Set<KoodiType> koodis = new HashSet<KoodiType>();
        for (String oppilaitosTyyppi : oppilaitostyyppiUrit) {
            Collection<KoodiType> curKoodis = helper.getKoodistoRelations(oppilaitosTyyppi, KoodistoURI.KOODISTO_TARJONTA_KOULUTUSTYYPPI, false, SuhteenTyyppiType.SISALTYY);
            koodis.addAll(curKoodis);
        }

        //clear all items
        koulutuksenTyyppiCombo.removeAllItems();
        koulutuksenTyyppiCombo.setInputPrompt(_i18n.getMessage("koulutuksenTyyppi"));
        //add applicable data
        for (KoodiType koodi : koodis) {
            koulutuksenTyyppiCombo.addItem(new KoodiContainer(koodi));
        }

    }

    @Override
    public void addOrganisaatioToRight(OrganisaatioPerustieto org) {
        int selectedCount = selectedOrgs.values().size();
        super.addOrganisaatioToRight(org);
        if (selectedCount != 0) {
            return; // nothing new was selected
        }
        final OrganisaatioPerustieto organisaatio = selectedOrgs.values()
                .iterator().next();
        KoodiContainer currentSelectionKoulutustyyppi = (KoodiContainer) koulutuksenTyyppiCombo
                .getValue();
        
        KoodiContainer currentSelectionPohjakoulutus = (KoodiContainer) pohjakoulutusvaatimusCombo
                .getValue();

        List<String> oppilaitostyyppiUrit = super.presenter
                .getOppilaitostyyppiUris(organisaatio.getOid());

        buildKoulutustyyppiCombo(oppilaitostyyppiUrit);

        if (currentSelectionKoulutustyyppi != null
                && currentSelectionKoulutustyyppi instanceof KoodiContainer) {
            for (Object itemId : koulutuksenTyyppiCombo.getItemIds()) {
                KoodiContainer kc = (KoodiContainer) itemId;
                if (kc.getKoodiType().getKoodiUri().equals(currentSelectionKoulutustyyppi.koodiType.getKoodiUri())) {
                    koulutuksenTyyppiCombo.setValue(itemId);
                }
            }
        }
        if (currentSelectionPohjakoulutus != null
                && currentSelectionPohjakoulutus instanceof KoodiContainer) {
            for (Object itemId : pohjakoulutusvaatimusCombo.getItemIds()) {
                KoodiContainer kc = (KoodiContainer) itemId;
                if (kc.getKoodiType().getKoodiUri().equals(currentSelectionPohjakoulutus.koodiType.getKoodiUri())) {
                    pohjakoulutusvaatimusCombo.setValue(itemId);
                }
            }
        }
    }
}
