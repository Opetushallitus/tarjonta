package fi.vm.sade.tarjonta.ui.view.hakukohde.tabs;/*
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


import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.google.common.collect.Sets;
import com.vaadin.data.Validator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.service.types.SisaltoTyyppi;
import fi.vm.sade.tarjonta.ui.enums.SaveButtonState;
import fi.vm.sade.tarjonta.ui.model.HakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractEditLayoutView;

/**
 * Created by: Tuomas Katva Date: 15.1.2013
 *
 * @author Timo Santasalo / Teknokala Ky
 */
@Configurable(preConstruction = true)
public class HakukohdePerustiedotViewImpl extends AbstractEditLayoutView<HakukohdeViewModel, PerustiedotViewImpl> {
    
    private static final long serialVersionUID = 1L;
    @Autowired(required = true)
    private TarjontaPresenter presenter;
    private PerustiedotViewImpl formView;
    
    public HakukohdePerustiedotViewImpl(String oid) {
        super(oid, SisaltoTyyppi.HAKUKOHDE);
        setMargin(true);
        setHeight(-1, UNITS_PIXELS);
    }
    
    @Override
    protected void buildLayout(VerticalLayout layout) {
        super.buildLayout(layout); //init base navigation here
        formView = new PerustiedotViewImpl(presenter, getUiBuilder());
        buildFormLayout(presenter, layout, presenter.getModel().getHakukohde(), formView);
    }
    
    @Override
    protected void eventBack(Button.ClickEvent event) {
        presenter.showMainDefaultView();
        presenter.getHakukohdeListView().reload();
    }
    
    @Override
    public void actionNext(ClickEvent event) {
        if (getHakukohdeOid() != null) {
            presenter.showHakukohdeViewImpl(getHakukohdeOid());
        }
    }
    
    @Override
    public boolean isformDataLoaded() {
        return isLoaded();
    }
    
    @Override
    public String actionSave(SaveButtonState tila, ClickEvent event) throws Exception {
        HakukohdeViewModel hakukohde = presenter.getModel().getHakukohde();
        formView.reloadLisatiedot(hakukohde.getLisatiedot());
        hakukohde.setHakuaika(formView.getSelectedHakuaika());
        if (!formView.isSahkoinenToimOsoiteChecked()) {
            hakukohde.setLiitteidenSahkoinenToimitusOsoite("");
        }
        // TODO call subform to perform validation (weigthed stdies can FAIL and still the save succeeds)
        // formView.validateExtraData();
        for (TextField tf : formView.getPainotettavat()) {
            tf.validate();
        }
        Set<Object> usedOppiaineet = Sets.newHashSet();
        GridLayout painotettavat = formView.getPainotettavatOppiaineet();
        if (painotettavat != null) {
            for (int i = 0; i < painotettavat.getRows(); i++) {
                Object component = painotettavat.getComponent(0, i);
                if (component instanceof KoodistoComponent) {
                    Object oppiaine = ((KoodistoComponent) component).getValue();
                    if (oppiaine != null) {
                        if (usedOppiaineet.contains(oppiaine)) {
                            throw new Validator.InvalidValueException(I18N.getMessage("validation.PerustiedotView.painotettavat.duplicate"));
                        }
                        usedOppiaineet.add(oppiaine);
                    }
                }
            }
        }
        
        presenter.saveHakuKohde(tila);
        return getHakukohdeOid();
    }
    
    private String getHakukohdeOid() {
        return presenter.getModel().getHakukohde() != null ? presenter.getModel().getHakukohde().getOid() : null;
    }
    
    private boolean isLoaded() {
        return getHakukohdeOid() != null ? true : false;
    }
}
