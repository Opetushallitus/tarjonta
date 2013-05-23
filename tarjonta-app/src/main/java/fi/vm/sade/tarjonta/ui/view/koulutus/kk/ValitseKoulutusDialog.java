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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.ui.validation.ValidatingViewBoundForm;
import fi.vm.sade.organisaatio.api.model.types.OrganisaatioPerustietoType;
import fi.vm.sade.tarjonta.ui.helper.UiBuilder;
import fi.vm.sade.tarjonta.ui.model.koulutus.kk.ValitseKoulutusModel;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.TarjontaWindow;
import fi.vm.sade.vaadin.util.UiUtil;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jani Wilén
 */
public class ValitseKoulutusDialog extends TarjontaWindow {

    private static transient final Logger LOG = LoggerFactory.getLogger(ValitseKoulutusDialog.class);
    private static final long serialVersionUID = -7357037259731478017L;
    private static final String WINDOW_HEIGHT = "550px";
    private static final String WINDOW_WIDTH = "700px";
    private TarjontaPresenter presenter;
    private UiBuilder uiBuilder;
    private Collection<OrganisaatioPerustietoType> orgs;
    private ValidatingViewBoundForm form;
    private Button btNext, btPrev;

    public ValitseKoulutusDialog(Collection<OrganisaatioPerustietoType> orgs, TarjontaPresenter presenter, UiBuilder uiBuilder) {
        super(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.presenter = presenter;
        this.uiBuilder = uiBuilder;
        this.orgs = orgs;
        setCaption(T(WINDOW_TITLE_PROPERTY));
    }

    @Override
    public void buildLayout(VerticalLayout layout) {
        layout.setMargin(false, true, true, true);
        ValitseKoulutusFormView view = new ValitseKoulutusFormView(presenter.getKorkeakouluPresenter(), uiBuilder);
        form = new ValidatingViewBoundForm(view);

        form.setValidationVisible(false);
        form.setValidationVisibleOnCommit(false);
        form.setSizeFull();

        ValitseKoulutusModel valitseKoulutus = presenter.getModel().getKorkeakouluPerustiedot().getValitseKoulutus();
        BeanItem<ValitseKoulutusModel> beanItem = new BeanItem<ValitseKoulutusModel>(valitseKoulutus);

        LOG.debug("buildLayout {}", valitseKoulutus);
        LOG.debug("beanItem '{}'", beanItem);

        form.setItemDataSource(beanItem);



        layout.addComponent(form);
        HorizontalLayout navi = buildNavigationButtonLayout();
        layout.addComponent(navi);

        layout.setExpandRatio(form, 0.98f);
        layout.setExpandRatio(navi, 0.02f);
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

    private HorizontalLayout buildNavigationButtonLayout() {
        final HorizontalLayout hl = UiUtil.horizontalLayout();
        btPrev = UiUtil.buttonSmallSecodary(hl, I18N.getMessage("peruuta"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                windowClose();
            }
        });


        btNext = UiUtil.buttonSmallSecodary(hl, I18N.getMessage("jatka"), new Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                presenter.getKorkeakouluPresenter().showKorkeakouluKoulutusEditView(orgs);
            }
        });

        hl.setExpandRatio(btNext, 1f);
        hl.setComponentAlignment(btPrev, Alignment.MIDDLE_LEFT);
        hl.setComponentAlignment(btNext, Alignment.MIDDLE_RIGHT);

        return hl;
    }
}
