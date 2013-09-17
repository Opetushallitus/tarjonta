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
package fi.vm.sade.tarjonta.ui.view;

import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Component.Listener;
import com.vaadin.ui.TabSheet;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.ui.view.common.css.CssHorizontalLayout;
import fi.vm.sade.tarjonta.ui.view.hakukohde.ListHakukohdeView;
import fi.vm.sade.tarjonta.ui.view.koulutus.ListKoulutusView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author mlyly
 */
@Configurable
public class SearchResultsView extends TabSheet {

    private transient static final Logger LOG = LoggerFactory.getLogger(SearchResultsView.class);
    private static final long serialVersionUID = -6602022577510112620L;
    boolean attached = false;
    private transient I18NHelper _i18n = new I18NHelper(this);
    private ListKoulutusView koulutusList;
    private ListHakukohdeView hakukohdeList;

    public SearchResultsView() {
        super();

        setSizeFull();
    }

    @Override
    public void attach() {
        super.attach();

        if (attached) {
            return;
        }
        attached = true;

        koulutusList = new ListKoulutusView();
        koulutusList.addListener(new Listener() {
            private static final long serialVersionUID = -8696709317724642137L;

            @Override
            public void componentEvent(Event event) {
                fireEvent(event);
                refreshTabs();
            }
        });
        this.addTab(koulutusList, T("koulutukset"));

        hakukohdeList = new ListHakukohdeView();
        this.addTab(hakukohdeList, T("hakuryhmat"));
    }

    public void setResultSizeForKoulutusTab(int size) {
        this.getTab(koulutusList).setCaption(T("koulutukset") + " (" + size + ")");

    }

    public void setResultSizeForHakukohdeTab(int size) {
        this.getTab(hakukohdeList).setCaption(T("hakuryhmat") + " (" + size + ")");
    }

    private String T(String key) {
        return _i18n.getMessage(key);
    }

    /**
     * @return the koulutusList
     */
    public ListKoulutusView getKoulutusList() {
        return koulutusList;
    }

    /**
     * @param koulutusList the koulutusList to set
     */
    public void setKoulutusList(ListKoulutusView koulutusList) {
        this.koulutusList = koulutusList;
    }

    /**
     * @return the hakukohdeList
     */
    public ListHakukohdeView getHakukohdeList() {
        return this.hakukohdeList;
    }

    public void refreshTabs() {
        this.setWidth("100%");

        if (koulutusList != null) {
            koulutusList.refreshLayout();
        }

        if (hakukohdeList != null) {
            hakukohdeList.refreshLayout();
        }
    }
}
