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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.ui.model.KoulutusOidNameViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.util.UiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/*
* Author: Tuomas Katva
*/

//TKatva, TODO try to make this generic so that it can be used from other windows
@Configurable
public class HakukohdeCreationDialog extends CustomComponent {


    private VerticalLayout rootLayout;
    private HorizontalLayout titleLayout;
    private HorizontalLayout middleLayout;
    private HorizontalLayout buttonLayout;
    private OptionGroup optionGroup;
    private boolean attached = false;
    @Autowired(required = true)
    private TarjontaPresenter tarjontaPresenter;
    private List<String> selectedOids;
    //TODO Use UiBuilder instead of UiUtil ???
    private Button peruutaBtn;
    private Button jatkaBtn;

    public HakukohdeCreationDialog(List<String> selectedOidsParam) {
        selectedOids = selectedOidsParam;
        rootLayout = new VerticalLayout();

        setCompositionRoot(rootLayout);

    }

    @Override
    public void attach() {
        super.attach();
        tarjontaPresenter.setHakukohdeCreationDialog(this);
        if(tarjontaPresenter != null) {
            tarjontaPresenter.loadKoulutusToteutusDialogWithOids(selectedOids);
        }

        if (attached) {
            return;
        }
        attached = true;

    }

    public void buildLayout(List<KoulutusOidNameViewModel> koulutusModel) {



        rootLayout.addComponent(createTitleLayout());
        rootLayout.addComponent(createOptionGroupLayout(koulutusModel));
        rootLayout.addComponent(createButtonLayout());

    }

    /*
    * Create top horizontal layout containing Dialog title
    */
    private HorizontalLayout createTitleLayout() {
        titleLayout = UiUtil.horizontalLayout();
        Label titleLabel = UiUtil.label(null, I18N.getMessage("HakukohdeCreationDialog.title"));
        titleLayout.addComponent(titleLabel);
        return titleLayout;
    }

    private HorizontalLayout createOptionGroupLayout(List<KoulutusOidNameViewModel> values) {
        middleLayout = UiUtil.horizontalLayout();

        BeanItemContainer<KoulutusOidNameViewModel> koulutukses = new BeanItemContainer<KoulutusOidNameViewModel>(KoulutusOidNameViewModel.class,values);

        optionGroup = new OptionGroup(I18N.getMessage("HakukohdeCreationDialog.valitutKoulutuksetOptionGroup"),koulutukses);
        optionGroup.setMultiSelect(true);
        //Set all selected as default
        for (Object obj: optionGroup.getItemIds()) {
            optionGroup.select(obj);
        }

        middleLayout.addComponent(optionGroup);

        return middleLayout;
    }

    private HorizontalLayout createButtonLayout() {
        buttonLayout = UiUtil.horizontalLayout();

        peruutaBtn = UiUtil.button(null,I18N.getMessage("HakukohdeCreationDialog.peruutaBtn"));
        jatkaBtn = UiUtil.button(null,I18N.getMessage("HakukohdeCreationDialog.jatkaBtn"));

        buttonLayout.addComponent(peruutaBtn);
        buttonLayout.addComponent(jatkaBtn);
        buttonLayout.setComponentAlignment(peruutaBtn, Alignment.MIDDLE_LEFT);
        buttonLayout.setComponentAlignment(jatkaBtn,Alignment.MIDDLE_RIGHT);
        createButtonListeners();
        return buttonLayout;
    }

    private void createButtonListeners() {
        if (peruutaBtn != null) {
            peruutaBtn.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    tarjontaPresenter.cancelHakukohdeCreationDialog();
                }
            });
        }

        if(jatkaBtn != null) {
            jatkaBtn.addListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    Object values = optionGroup.getValue();
                    Collection<KoulutusOidNameViewModel> selectedKoulutukses = null;
                        if (values instanceof  Collection) {
                        selectedKoulutukses = (Collection<KoulutusOidNameViewModel>)values;
                        }
                        /*else if (values instanceof KoulutusOidNameViewModel)  {
                        selectedKoulutukses = new ArrayList<KoulutusOidNameViewModel>();
                        selectedKoulutukses.add(((KoulutusOidNameViewModel)values));
                        } */
                        tarjontaPresenter.cancelHakukohdeCreationDialog();
                        tarjontaPresenter.showHakukohdeEditView(koulutusNameViewModelToOidList(selectedKoulutukses),null);

                }
            });
        }
    }

    private List<String> koulutusNameViewModelToOidList(Collection<KoulutusOidNameViewModel> models) {
        List<String> oids = new ArrayList<String>();
        for (KoulutusOidNameViewModel model : models) {
            oids.add(model.getKoulutusOid());
        }
        return oids;
    }

}
