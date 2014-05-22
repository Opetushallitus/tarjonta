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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;

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
public class ShowHakukohteetDialog extends ShowRelatedObjectsDialog {
    
    private static final long serialVersionUID = -4621899734019988734L;
    
    private List<HakukohdePerustieto> hakukohteet;
    private KoulutusPerustieto koulutus;

    public ShowHakukohteetDialog(List<HakukohdePerustieto>hakukohteet, KoulutusPerustieto koulutus, TarjontaPresenter presenter) {
        super(presenter);
        this.hakukohteet = hakukohteet;
        this.koulutus = koulutus;
        buildLayout();
    }
    
    private void buildLayout() {
        if (hakukohteet.isEmpty()) {
            VerticalLayout vlEi = new VerticalLayout();
            vlEi.setMargin(true, false, true, true);
            Label eiHakukohteita = new Label();
            eiHakukohteita.setValue(T("eiHakukohteita"));
            vlEi.addComponent(eiHakukohteita);
            addComponent(vlEi);
        } else {
            buildLayout(T("otsikko"), resolveKoulutusNimi(), hakukohteet.size() + 2);
            populateTree();
        }
        VerticalLayout vl = new VerticalLayout();
        vl.setMargin(true, false, true, false);
        Button closeButton = UiUtil.button(vl, T("sulje"));
        vl.setComponentAlignment(closeButton, Alignment.BOTTOM_CENTER);
        closeButton.addListener(new Button.ClickListener() {

            private static final long serialVersionUID = 5434303513332786046L;

            @Override
            public void buttonClick(ClickEvent event) {
               presenter.closeKoulutusRemovalDialog();
            }
            
        });
        addComponent(vl);
        setComponentAlignment(vl, Alignment.BOTTOM_CENTER);
    }


    @Override
    protected void populateTree() {
        Set<Map.Entry<String, List<HakukohdePerustieto>>> set = createDataMap();
        HierarchicalContainer hc = new HierarchicalContainer();
        hc.addContainerProperty(COLUMN_A, CaptionItem.class, new CaptionItem());
        hc.addContainerProperty(COLUMN_PVM, String.class, "");
        hc.addContainerProperty(COLUMN_TILA, String.class, "");
        
        for (Map.Entry<String, List<HakukohdePerustieto>>e : set) {
            
            Object rootItem = hc.addItem();
            
            hc.getContainerProperty(rootItem, COLUMN_A).setValue(new CaptionItem(e.getKey(), false));
            for (final HakukohdePerustieto curHakukohde : e.getValue()) {
                
                hc.addItem(curHakukohde);
                hc.setParent(curHakukohde, rootItem);
                CaptionItem ci = new CaptionItem(
                        TarjontaUIHelper.getClosestMonikielinenNimi(I18N.getLocale(), curHakukohde.getNimi()), 
                        true);
                ci.getLinkButton().addListener( new Button.ClickListener() {

                    private static final long serialVersionUID = -4104837426848884996L;

                    @Override
                        public void buttonClick(ClickEvent event) {
                            showSummaryView(curHakukohde);
                        }
                    });
                hc.getContainerProperty(curHakukohde, COLUMN_A).setValue(
                        ci);
                hc.getContainerProperty(curHakukohde, COLUMN_PVM).setValue(getHakukohdeAjankohtaStr(curHakukohde));
                hc.getContainerProperty(curHakukohde, COLUMN_TILA).setValue(T(curHakukohde.getTila().value()));
                hc.setChildrenAllowed(curHakukohde, false);
            }
        }
        
        tree.setContainerDataSource(hc);
        
        for (HakukohdePerustieto curTulos : hakukohteet) {
            tree.setCollapsed(tree.getParent(curTulos), false);
        }
    }
    
    private Set<Map.Entry<String, List<HakukohdePerustieto>>> createDataMap() {
        Map<String, List<HakukohdePerustieto>> dataMap = new HashMap<String, List<HakukohdePerustieto>>();
        for (HakukohdePerustieto curHakukohde : hakukohteet) {
            String hakukohdeKey = TarjontaUIHelper.getClosestMonikielinenNimi(I18N.getLocale(),curHakukohde.getTarjoajaNimi());
            if (!dataMap.containsKey(hakukohdeKey)) {
                List<HakukohdePerustieto> hakukohteetM = new ArrayList<HakukohdePerustieto>();
                hakukohteetM.add(curHakukohde);
                dataMap.put(hakukohdeKey, hakukohteetM);
            } else {
                dataMap.get(hakukohdeKey).add(curHakukohde);
            }
        }
        return dataMap.entrySet();
    }
    
    private String getHakukohdeAjankohtaStr(HakukohdePerustieto hakukohde) {
        return I18N.getMessage(hakukohde.getKoulutuksenAlkamiskausi().getUri()) 
                +  " " + hakukohde.getKoulutuksenAlkamisvuosi();
    }
    

    private String resolveKoulutusNimi() {
        return presenter.getUiHelper().getKoulutusNimi(koulutus) 
                + ", " + presenter.getUiHelper().getAjankohtaStr(koulutus) 
                + ", " + presenter.getUiHelper().getKoulutuslaji(koulutus)
                + ", " + TarjontaUIHelper.getClosestMonikielinenNimi(I18N.getLocale(), koulutus.getTarjoaja().getNimi());
    }
    
    private void showSummaryView(HakukohdePerustieto hakukohde) {
        final String hakukohdeOid = hakukohde.getOid();
        presenter.closeKoulutusRemovalDialog();
        presenter.getTarjoaja().setSelectedResultRowOrganisationOid(hakukohde.getTarjoajaOid());
        presenter.showHakukohdeViewImpl(hakukohdeOid);

    }

}
