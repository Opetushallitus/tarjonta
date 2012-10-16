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
package fi.vm.sade.tarjonta.ui.view.koulutus;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.tarjonta.ui.view.common.AbstractVerticalInfoLayout;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.constants.StyleEnum;
import fi.vm.sade.vaadin.constants.UiMarginEnum;
import fi.vm.sade.vaadin.dto.PageNavigationDTO;
import fi.vm.sade.vaadin.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Show collected information about koulutus.
 *
 * @author mlyly
 */
@Configurable
public class ShowKoulutusView extends AbstractVerticalInfoLayout {

    private static final Logger LOG = LoggerFactory.getLogger(ShowKoulutusView.class);

    public ShowKoulutusView(String pageTitle, PageNavigationDTO pageNavigationDTO) {
        super(VerticalLayout.class, pageTitle, null, pageNavigationDTO);

        addNavigationButton("", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getPresenter().showMainDefaultView();
            }
        }, StyleEnum.STYLE_BUTTON_BACK);

        addNavigationButton("Poista", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getWindow().showNotification(T("poistoOnnistui"));
                getPresenter().showMainDefaultView();
            }
        });

        addNavigationButton("Kopioi uudeksi", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getWindow().showNotification(T("kopiointiUudeksiOnnistui"));
                getPresenter().showMainDefaultView();
            }
        });
    }

    @Override
    protected void buildLayout(VerticalLayout layout) {
        LOG.info("buildLayout()");

        addLayoutSplit();
        buildKoulutuksenPerustiedot(layout);
        addLayoutSplit();
        buildKoulutuksenKuvailevatTiedot(layout);
        addLayoutSplit();
        buildKoulutuksenSisaltyvatOpintokokonaisuudet(layout);
        addLayoutSplit();
        buildKoulutuksenHakukohteet(layout);
        addLayoutSplit();
    }

    private void buildKoulutuksenPerustiedot(VerticalLayout layout) {
        layout.addComponent(buildHeaderLayout(T("perustiedot"), T("muokkaa")));
    }

    private void buildKoulutuksenKuvailevatTiedot(VerticalLayout layout) {
        layout.addComponent(buildHeaderLayout(T("kuvailevatTiedot"), T("muokkaa")));
    }

    private void buildKoulutuksenSisaltyvatOpintokokonaisuudet(VerticalLayout layout) {
        layout.addComponent(buildHeaderLayout(T("sisaltyvatOpintokokonaisuudet", 3), T("muokkaa")));
    }

    private void buildKoulutuksenHakukohteet(VerticalLayout layout) {
        layout.addComponent(buildHeaderLayout(T("hakukohteet", 2), T("muokkaa")));
    }

    private HorizontalLayout buildHeaderLayout(String title, String btnCaption) {
        HorizontalLayout headerLayout = UiUtil.horizontalLayout(true, UiMarginEnum.NONE);
        Label titleLabel = UiUtil.label(headerLayout, title);
        titleLabel.setStyleName(Oph.LABEL_H2);

        if (btnCaption != null) {
            headerLayout.addComponent(titleLabel);
            Button btn = UiUtil.buttonSmallSecodary(headerLayout, btnCaption, new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    getWindow().showNotification("NOT IMPLEMENTED");
                }
            });

            headerLayout.setExpandRatio(btn, 1f);
            headerLayout.setComponentAlignment(btn, Alignment.TOP_RIGHT);
        }
        return headerLayout;
    }

}
