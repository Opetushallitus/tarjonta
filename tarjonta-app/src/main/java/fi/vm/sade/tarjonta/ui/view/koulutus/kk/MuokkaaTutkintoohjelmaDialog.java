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
package fi.vm.sade.tarjonta.ui.view.koulutus.kk;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.ui.validation.ValidatingViewBoundForm;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.KorkeakouluPerustiedotViewModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.TarjontaWindow;
import static fi.vm.sade.tarjonta.ui.view.common.TarjontaWindow.WINDOW_TITLE_PROPERTY;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jani Wilén
 */
public class MuokkaaTutkintoohjelmaDialog extends TarjontaWindow {

    private static transient final Logger LOG = LoggerFactory.getLogger(MuokkaaTutkintoohjelmaDialog.class);
    private static final long serialVersionUID = -7357037259731478017L;
    private static final String WINDOW_HEIGHT = "250px";
    private static final String WINDOW_WIDTH = "700px";
    private TarjontaPresenter presenter;
    private ValidatingViewBoundForm form;
    private boolean mode;

    public MuokkaaTutkintoohjelmaDialog(TarjontaPresenter presenter) {
        super(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.presenter = presenter;
    }

    @Override
    public void buildLayout(VerticalLayout layout) {
        setCaption(T(mode() + WINDOW_TITLE_PROPERTY));
        layout.setMargin(false, true, true, true);
        MuokkaaTutkintoohjelmaFormView view = new MuokkaaTutkintoohjelmaFormView(presenter.getKorkeakouluPresenter(), this, mode);
        form = new ValidatingViewBoundForm(view);

        form.setValidationVisible(false);
        form.setValidationVisibleOnCommit(false);
        form.setSizeFull();
        KorkeakouluPerustiedotViewModel korkeakouluPerustiedot = presenter.getModel().getKorkeakouluPerustiedot();
        BeanItem<KorkeakouluPerustiedotViewModel> beanItem = new BeanItem<KorkeakouluPerustiedotViewModel>(korkeakouluPerustiedot);
        form.setItemDataSource(beanItem);
        layout.addComponent(form);
    }

    public void windowClose() {
        presenter.getRootView().removeWindow(this);
    }

    @Override
    public void windowClose(CloseEvent e) {
        windowClose();
    }

    public void windowOpen() {
        presenter.getRootView().addWindow(this);
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(boolean mode) {
        this.mode = mode;
    }

    private String mode() {
        return this.mode ? "edit." : "add.";
    }
}
