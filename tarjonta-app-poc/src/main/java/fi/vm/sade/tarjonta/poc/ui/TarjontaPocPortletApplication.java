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
package fi.vm.sade.tarjonta.poc.ui;

import com.github.wolfie.blackboard.Blackboard;
import com.vaadin.Application;
import com.vaadin.terminal.Sizeable;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.ui.app.AbstractSadePortletApplication;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.util.UiBaseUtil;
import java.util.Locale;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author mlyly
 */
@Configurable(preConstruction = false)
public class TarjontaPocPortletApplication extends AbstractSadePortletApplication {

    private TarjontaWindow win;

    @Override
    public void init() {
        log.info("init() - TarjontaPocPortletApplication!");
        super.init();

        win = new TarjontaWindow();
        setMainWindow(win);
        //win.getContent().setHeight(850, Sizeable.UNITS_PIXELS);
        //setTheme("tarjonta");
        setTheme("oph-app-tarjonta");
    }

    @Override
    public void transactionStart(Application application, Object transactionData) {
        super.transactionStart(application, transactionData);
    }

    @Override
    public void transactionEnd(Application application, Object transactionData) {
        super.transactionEnd(application, transactionData);

    }

    @Override
    protected void registerListeners(Blackboard blackboard) {
        log.info("registerListeners() - for blackboard.");
        blackboard.enableLogging();
    }

    @Override
    public void onRequestStart(PortletRequest portletRequest, PortletResponse portletResponse) {
        log.info("In onRequestStart");
        super.onRequestStart(portletRequest, portletResponse);
    }

    @Override
    public void onRequestEnd(PortletRequest portletRequest, PortletResponse portletResponse) {
        log.info("In onRequestEnd");
        super.onRequestEnd(portletRequest, portletResponse);
    }

    protected String getParameter(Object req, String name) {
        PortletRequest request = (PortletRequest) req;
        return request.getParameter(name);
    }

    protected String requestInfo(Object req) {
        PortletRequest request = (PortletRequest) req;
        return " langParam: " + request.getPublicParameterMap().get("lang") + ", url: ***"
                + ", i18n.locale: " + I18N.getLocale() + ", default locale: " + Locale.getDefault();
    }
}
