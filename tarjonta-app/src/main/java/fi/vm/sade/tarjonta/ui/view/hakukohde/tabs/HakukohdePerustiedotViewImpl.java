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


import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.tarjonta.service.types.SisaltoTyyppi;
import fi.vm.sade.tarjonta.ui.enums.SaveButtonState;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.model.HakukohdeNameUriModel;
import fi.vm.sade.tarjonta.ui.model.HakukohdeViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.AbstractEditLayoutView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Created by: Tuomas Katva Date: 15.1.2013
 */
@Configurable(preConstruction = true)
public class HakukohdePerustiedotViewImpl extends AbstractEditLayoutView<HakukohdeViewModel, PerustiedotViewImpl> {

    @Autowired(required = true)
    private TarjontaPresenter presenter;
    @Autowired
    private TarjontaUIHelper tarjontaUIHelper;
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
        buildFormLayout("perustiedot", presenter, layout, presenter.getModel().getHakukohde(), formView);
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
        hakukohde.getLisatiedot().clear();
        hakukohde.getLisatiedot().addAll(formView.getLisatiedot());

        // TODO call subform to perform validation (weigthed stdies can FAIL and still the save succeeds)
        // formView.validateExtraData();

        HakukohdeNameUriModel selectedHakukohde = formView.getSelectedHakukohde();
        hakukohde.setHakukohdeNimi(getUriWithVersion(selectedHakukohde));

        for(TextField tf: formView.getPainotettavat()){
            tf.validate();
        }
        
        //XXXX validoi painotettavat

        
        presenter.saveHakuKohde(tila);
        return getHakukohdeOid();
    }

    private String getUriWithVersion(HakukohdeNameUriModel hakukohdeNameUriModel) {
        return hakukohdeNameUriModel.getHakukohdeUri() + TarjontaUIHelper.KOODI_URI_AND_VERSION_SEPARATOR + hakukohdeNameUriModel.getUriVersio();
    }

    private String getHakukohdeOid() {
        return presenter.getModel().getHakukohde() != null ? presenter.getModel().getHakukohde().getOid() : null;
    }

    private boolean isLoaded() {
        return getHakukohdeOid() != null ? true : false;
    }
}
