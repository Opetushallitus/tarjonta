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

import java.util.List;
import fi.vm.sade.tarjonta.ui.model.KoulutusOidNameViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Button;
import fi.vm.sade.tarjonta.ui.view.TarjontaPresenter;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.util.UiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
/*
* Author: Tuomas Katva
*/
@Configurable
public class HakukohdeCreationDialog extends CustomComponent {


    private VerticalLayout rootLayout;
    private HorizontalLayout middleLayout;
    private HorizontalLayout buttonLayout;
    private boolean attached = false;
    @Autowired(required = true)
    private TarjontaPresenter tarjontaPresenter;
    private List<String> selectedOids;


    public HakukohdeCreationDialog(List<String> selectedOidsParam) {
        selectedOids = selectedOidsParam;
        rootLayout = new VerticalLayout();

        setCompositionRoot(rootLayout);

    }

    @Override
    public void attach() {
        super.attach();

        if(tarjontaPresenter != null) {
            //tarjontaPresenter.setHakukohdeCreationDialog(this);
        }

        if (attached) {
            return;
        }
        attached = true;

    }

    public void buildLayout(List<KoulutusOidNameViewModel> koulutusModel) {

    }


}
