/**
 *
 * Copyright (c) 2012 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence. You may
 * obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the European Union Public Licence for more
 * details.
 */
package fi.vm.sade.tarjonta.ui;

import com.github.wolfie.blackboard.Blackboard;
import com.vaadin.ui.Window;
import fi.vm.sade.generic.ui.app.AbstractSadePortletApplication;
import fi.vm.sade.tarjonta.ui.model.view.MainSplitPanelView;
import fi.vm.sade.tarjonta.ui.poc.TarjontaWindow;
import fi.vm.sade.tarjonta.ui.poc.helper.UI;
import fi.vm.sade.vaadin.Oph;

/**
 *
 * @author mlyly
 */
public class TarjontaPocPortletApplication extends AbstractSadePortletApplication {

    @Override
    public void init() {
        log.info("init() - TarjontaPocPortletApplication");
        super.init();
        createMainWindow();
    }

    @Override
    protected void registerListeners(Blackboard blackboard) {
        log.info("registerListeners() - for blackboard.");
        blackboard.enableLogging();
    }

    private void createMainWindow() {
        TarjontaWindow win = new TarjontaWindow();
        setMainWindow(win);

        setTheme("tarjonta");
    }
}
