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
package fi.vm.sade.tarjonta.ui.view.hakukohde.tabs;

import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.tarjonta.service.types.KoulutusasteTyyppi;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.TarjontaWindow;
import static fi.vm.sade.tarjonta.ui.view.common.TarjontaWindow.WINDOW_TITLE_PROPERTY;

/**
 *
 * @author Jani Wilén
 */
public class HakukohdeAmmatillinenValintakoeDialog extends TarjontaWindow {

    private static final String WINDOW_HEIGHT = "700px";
    private static final String WINDOW_WIDTH = "864px";
    private static final long serialVersionUID = 1893982332664363368L;
    private TarjontaPresenter presenter;
    private UiBuilder uiBuilder;
    private HakukohdeValintakoeViewImpl view;
    private final String WINDOW_UPDATE_TITLE = "dialogEditTitle";

    public HakukohdeAmmatillinenValintakoeDialog(TarjontaPresenter presenter, UiBuilder uiBuilder) {
        super(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.presenter = presenter;
        this.uiBuilder = uiBuilder;
        setCaption(T(WINDOW_TITLE_PROPERTY));
    }

    public HakukohdeAmmatillinenValintakoeDialog(TarjontaPresenter presenter, UiBuilder uiBuilder, boolean isNew) {
        super(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.presenter = presenter;
        this.uiBuilder = uiBuilder;
        if (isNew) {
        setCaption(T(WINDOW_TITLE_PROPERTY));
        } else {
        setCaption(T(WINDOW_UPDATE_TITLE));
        }
    }

    @Override
    public void buildLayout(VerticalLayout layout) {
        layout.addComponent(view.getForm());
    }

    public void windowClose() {
        presenter.getRootView().removeWindow(this);
    }

    @Override
    public void windowClose(CloseEvent e) {
        windowClose();
    }

    public void windowOpen() {
        view = new HakukohdeValintakoeViewImpl(getErrorView(), presenter, uiBuilder, KoulutusasteTyyppi.AMMATILLINEN_PERUSKOULUTUS);
        view.reloadValintakoeAikasTableData();

        presenter.getRootView().addWindow(this);
    }
}
