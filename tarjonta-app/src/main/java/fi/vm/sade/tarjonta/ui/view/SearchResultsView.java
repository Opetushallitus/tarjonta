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
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.ui.view.hakukohde.ListHakukohdeView;
import fi.vm.sade.tarjonta.ui.view.hakukohde.ListHakukohdeViewImpl;
import fi.vm.sade.tarjonta.ui.view.koulutus.ListKoulutusView;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author mlyly
 */
@Configurable
public class SearchResultsView extends VerticalLayout {

    private static final long serialVersionUID = -6602022577510112620L;
    boolean attached = false;
    private transient I18NHelper _i18n = new I18NHelper(this);
    private TabSheet tabs;
    private ListKoulutusView koulutusList;
    private ListHakukohdeViewImpl hakukohdeList;

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

        tabs = new TabSheet();
        tabs.setHeight(-1, UNITS_PIXELS);
        addComponent(tabs);

        setKoulutusList(new ListKoulutusView());
        getKoulutusList().addListener(new Listener() {
            private static final long serialVersionUID = -8696709317724642137L;

            @Override
            public void componentEvent(Event event) {
                fireEvent(event);

            }
        });

        tabs.addTab(getKoulutusList(), T("koulutukset"));//new EditKoulutusPerustiedotToinenAsteView(), T("koulutukset"));
        hakukohdeList = new ListHakukohdeViewImpl();
        tabs.addTab(hakukohdeList, T("hakuryhmat"));

    }
    
    public void setResultSizeForKoulutusTab(int size) {
        tabs.getTab(koulutusList).setCaption(T("koulutukset") + " (" + size + ")");
        
    }
    
    public void setResultSizeForHakukohdeTab(int size) {
        tabs.getTab(hakukohdeList).setCaption(T("hakuryhmat")+ " (" + size + ")");
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
    public ListHakukohdeViewImpl getHakukohdeList() {
        return this.hakukohdeList;
    }
}
