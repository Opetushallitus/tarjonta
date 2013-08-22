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
package fi.vm.sade.tarjonta.ui.view.koulutus.aste2;

import com.vaadin.data.Validator;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;

import fi.vm.sade.tarjonta.ui.enums.KoulutusActiveTab;
import fi.vm.sade.tarjonta.ui.enums.UserNotification;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.org.OrganisationOidNamePair;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractEditLayoutView;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalLayout;
import fi.vm.sade.vaadin.constants.LabelStyleEnum;
import fi.vm.sade.vaadin.util.UiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author mlyly
 */
@Configurable
public class EditKoulutusView extends AbstractVerticalLayout {

    private static final long serialVersionUID = -1074453323245469183L;
    @Autowired(required = true)
    private TarjontaPresenter presenter;
    private static final String LABEL_FORMAT_NEW = "title.new";
    private static final String LABEL_FORMAT_EDIT = "title.edit";
    private static final String DEMO_DATA = "tutkintoon johtavaa koulutusta";
    private Label title;  //formated title label
    private String koulutusOid;
    private KoulutusActiveTab activeTab = KoulutusActiveTab.PERUSTIEDOT;
    private TabSheet.Tab liitteetTab;

    public EditKoulutusView(String koulutusOid) {
        this.koulutusOid = koulutusOid;
    }

    public EditKoulutusView(String koulutusOid, KoulutusActiveTab activeTab) {
        this.koulutusOid = koulutusOid;
        this.activeTab = activeTab;
    }

    @Override
    protected void buildLayout() {

        String organisaatioName = getOrganisaationNames();

        if (presenter.getModel().getKoulutusPerustiedotModel().isLoaded()) {
            title = UiUtil.label((AbsoluteLayout) null, T(LABEL_FORMAT_EDIT),
                    LabelStyleEnum.TEXT_RAW,
                    DEMO_DATA,
                    presenter.getKoodiNimi(presenter.getModel().getKoulutusPerustiedotModel().getPohjakoulutusvaatimus()),
                    organisaatioName);
        } else {
            title = UiUtil.label((AbsoluteLayout) null,
                    T(LABEL_FORMAT_NEW),
                    LabelStyleEnum.TEXT_RAW,
                    DEMO_DATA,
                    presenter.getKoodiNimi(presenter.getModel().getKoulutusPerustiedotModel().getPohjakoulutusvaatimus()),
                    organisaatioName);
        }
        HorizontalLayout hlLabelWrapper = new HorizontalLayout();
        hlLabelWrapper.setMargin(false, false, true, true);
        hlLabelWrapper.addComponent(title);
        addComponent(hlLabelWrapper);

        final TabSheet tabs = UiBuilder.tabSheet(this);
        final EditKoulutusPerustiedotToinenAsteView perustiedotView = new EditKoulutusPerustiedotToinenAsteView(koulutusOid);
        tabs.addTab(perustiedotView, T("perustiedot"));

        final EditKoulutusLisatiedotToinenAsteView lisatiedotView = new EditKoulutusLisatiedotToinenAsteView(koulutusOid);

        liitteetTab = tabs.addTab(lisatiedotView, T("lisatiedot"));
        liitteetTab.setEnabled(presenter.getModel().getKoulutusPerustiedotModel().isLoaded());
        
        this.presenter.setLisatiedotView(lisatiedotView);

        if (KoulutusActiveTab.PERUSTIEDOT.equals(activeTab)) {
            tabs.setSelectedTab(perustiedotView);
        } else {
            tabs.setSelectedTab(lisatiedotView);
        }
        
        tabs.addListener(new SelectedTabChangeListener() {

            private static final long serialVersionUID = -3995507767832431214L;

            @Override
            public void selectedTabChange(SelectedTabChangeEvent event) {
                if (KoulutusActiveTab.PERUSTIEDOT.equals(activeTab) && !isTabChangeable(perustiedotView)) {
                    tabs.setSelectedTab(perustiedotView);
                    presenter.showNotification(UserNotification.UNSAVED);
                } else if (KoulutusActiveTab.LISATIEDOT.equals(activeTab) && !isTabChangeable(lisatiedotView)) {
                    tabs.setSelectedTab(lisatiedotView);
                    presenter.showNotification(UserNotification.UNSAVED);
                } else if (KoulutusActiveTab.PERUSTIEDOT.equals(activeTab)) {
                    activeTab = KoulutusActiveTab.LISATIEDOT;
                } else {
                    activeTab = KoulutusActiveTab.PERUSTIEDOT;
                }
            }
            
        });
    }
    
    @SuppressWarnings("rawtypes")
    private boolean isTabChangeable(AbstractEditLayoutView tabView) {
        if (!tabView.isSaved()) {
            try {
                tabView.validateFormData();
            } catch (Validator.InvalidValueException e) {
                tabView.getErrorView().addError(e);
            }
            return false;
        }
        
        try {
            tabView.validateFormData();
            return true;
        } catch (Validator.InvalidValueException e) {
            tabView.getErrorView().addError(e);
            return false;
        }
    }

    public void enableLisatiedotTab() {
        liitteetTab.setEnabled(true);
    }

    private String getOrganisaationNames() {
        StringBuilder organisaatios = new StringBuilder();
        int counter = 0;
        for (OrganisationOidNamePair pair : presenter.getTarjoaja().getOrganisationOidNamePairs()) {
            if (counter != 0) {
                organisaatios.append(", ");
            }
            organisaatios.append(pair.getOrganisationName());
            counter++;
        }
        return organisaatios.toString();
    }
}
