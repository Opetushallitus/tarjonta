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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.service.search.HakukohdePerustieto;
import fi.vm.sade.tarjonta.service.search.KoulutusPerustieto;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.ShowRelatedObjectsDialog;
import fi.vm.sade.vaadin.util.UiUtil;

/**
 * 
 * @author Markus
 *
 */
public class ShowKoulutuksetDialog extends ShowRelatedObjectsDialog {

    private static final long serialVersionUID = 6521526287528256527L;
    
    private List<KoulutusPerustieto> koulutukset;
    private HakukohdePerustieto selectedHakukohde;
    
    public ShowKoulutuksetDialog(List<KoulutusPerustieto> koulutukset, HakukohdePerustieto selectedHakukohde, TarjontaPresenter presenter) {
        super(presenter);
        this.koulutukset = koulutukset;
        this.selectedHakukohde = selectedHakukohde;
        buildLayout();
    }
    
    private void buildLayout() {
        buildLayout(T("otsikko"), resolveHakukohdeNimi(), koulutukset.size() + 3);
        populateTree();
        VerticalLayout vl = new VerticalLayout();
        vl.setMargin(true, false, true, false);
        Button closeButton = UiUtil.button(vl, T("sulje"));
        vl.setComponentAlignment(closeButton, Alignment.BOTTOM_CENTER);
        closeButton.addListener(new Button.ClickListener() {

            private static final long serialVersionUID = 5434303513332786046L;

            @Override
            public void buttonClick(ClickEvent event) {
               presenter.closeHakukohdeRemovalDialog();
            }
            
        });
        addComponent(vl);
        setComponentAlignment(vl, Alignment.BOTTOM_CENTER);
    }
    
    protected void populateTree() {
       Set<Map.Entry<String, List<KoulutusPerustieto>>> set = createDataMap();
       HierarchicalContainer hc = new HierarchicalContainer();
       hc.addContainerProperty(COLUMN_A, CaptionItem.class, new CaptionItem());
       hc.addContainerProperty(COLUMN_PVM, String.class, "");
       hc.addContainerProperty(COLUMN_TILA, String.class, "");
       
       for (Map.Entry<String, List<KoulutusPerustieto>>e : set) {
           
           Object rootItem = hc.addItem();
           
           hc.getContainerProperty(rootItem, COLUMN_A).setValue(new CaptionItem(e.getKey(), false));
           for (final KoulutusPerustieto curKoulutus : e.getValue()) {
               
               hc.addItem(curKoulutus);
               hc.setParent(curKoulutus, rootItem);
               CaptionItem ci = new CaptionItem(
                       TarjontaUIHelper.getClosestMonikielinenNimi(I18N.getLocale(), curKoulutus.getNimi()), 
                       true);
               ci.getLinkButton().addListener( new Button.ClickListener() {

                   private static final long serialVersionUID = -4104837426848884996L;

                   @Override
                       public void buttonClick(ClickEvent event) {
                           showSummaryView(curKoulutus);
                       }
                   });
               hc.getContainerProperty(curKoulutus, COLUMN_A).setValue(
                       ci);
               hc.getContainerProperty(curKoulutus, COLUMN_PVM).setValue(presenter.getUiHelper().getAjankohtaStr(curKoulutus));
               hc.getContainerProperty(curKoulutus, COLUMN_TILA).setValue(T(curKoulutus.getTila().value()));
               hc.setChildrenAllowed(curKoulutus, false);
              
           }
       }
       
       tree.setContainerDataSource(hc);
       
       for (KoulutusPerustieto curTulos : koulutukset) {
           tree.setCollapsed(tree.getParent(curTulos), false);
       }
    }
    
    @SuppressWarnings("incomplete-switch")
    private void showSummaryView(KoulutusPerustieto koulutus) {
        final String komotoOid = koulutus.getKoulutusmoduuliToteutus();

        switch (koulutus.getKoulutusasteTyyppi()) {
            case AMMATILLINEN_PERUSKOULUTUS:
                presenter.closeHakukohdeRemovalDialog();
                presenter.showShowKoulutusView(komotoOid);
                break;
            case LUKIOKOULUTUS:
                presenter.closeHakukohdeRemovalDialog();
                presenter.getLukioPresenter().showSummaryKoulutusView(komotoOid);
                break;
        }
    }
    
    private Set<Map.Entry<String, List<KoulutusPerustieto>>> createDataMap() {
        Map<String, List<KoulutusPerustieto>> dataMap = new HashMap<String, List<KoulutusPerustieto>>();
        for (KoulutusPerustieto curKoulutus : this.koulutukset) {
            String koulutusKey = TarjontaUIHelper.getClosestMonikielinenNimi(I18N.getLocale(),curKoulutus.getTarjoaja().getNimi());
            if (!dataMap.containsKey(koulutusKey)) {
                List<KoulutusPerustieto> koulutuksetM = new ArrayList<KoulutusPerustieto>();
                koulutuksetM.add(curKoulutus);
                dataMap.put(koulutusKey, koulutuksetM);
            } else {
                dataMap.get(koulutusKey).add(curKoulutus);
            }
        }
        return dataMap.entrySet();
    }
    
    private String resolveHakukohdeNimi() {
        return TarjontaUIHelper.getClosestMonikielinenNimi(I18N.getLocale(), selectedHakukohde.getNimi()) 
                + ", " + getHakukohdeAjankohtaStr() 
                + ", " + TarjontaUIHelper.getClosestMonikielinenNimi(I18N.getLocale(), selectedHakukohde.getTarjoajaNimi());        
    }
    
    private String getHakukohdeAjankohtaStr() {
        return I18N.getMessage(selectedHakukohde.getKoulutuksenAlkamiskausi().getUri()) 
                +  " " + selectedHakukohde.getKoulutuksenAlkamisvuosi();
    }

}
