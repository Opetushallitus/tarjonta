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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import com.vaadin.data.Container;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.Label;

import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import fi.vm.sade.tarjonta.ui.model.KoulutusSearchSpesificationViewModel;
import fi.vm.sade.tarjonta.ui.view.haku.EditHakuView;
import fi.vm.sade.tarjonta.ui.view.haku.HakuResultRow;
import fi.vm.sade.tarjonta.ui.view.haku.ListHakuView;

/**
 * Presenter for searching, creating, editing, and viewing Haku objects.
 * 
 * @author markus
 *
 */
@Component
@Configurable(preConstruction = false)
public class HakuPresenter {
    
    private static final Logger LOG = LoggerFactory.getLogger(HakuPresenter.class);
    
    private KoulutusSearchSpesificationViewModel searchSpec = new KoulutusSearchSpesificationViewModel();
    private List<HakuViewModel> hakuDb = new ArrayList<HakuViewModel>();
    
    private ListHakuView hakuList;
    
    private EditHakuView editHaku;
    
    private TarjontaRootView rootView;
    
    HakuViewModel hakuModel;



    private static final String COLUMN_A = "Kategoriat";
    
    public HakuPresenter() {
        createData();    
    }
    
    /**
     * Performs the search according to searchSpec 
     * and reloads the hakuList view.
     */
    public void doSearch() { 
        hakuList.reload();                    
    }

    /**
     * 
     * Gets the datasource for hakuList.
     * @return
     */
    public Container getTreeDataSource() {
         
        Map<String, List<HakuViewModel>> map = new HashMap<String, List<HakuViewModel>>();

        //Grouping the HakuViewModel objects based on hakutyyppi
        for (HakuViewModel curHaku : hakuDb) {
            if (!map.containsKey(curHaku.getHakutyyppi())) {
                List<HakuViewModel> haut = new ArrayList<HakuViewModel>();
                haut.add(curHaku);
                map.put(curHaku.getHakutyyppi(), haut);
            } else {
                map.get(curHaku.getHakutyyppi()).add(curHaku);
            }   
        }
        
        Set<Map.Entry<String, List<HakuViewModel>>> set = map.entrySet();

        HierarchicalContainer hc = new HierarchicalContainer();
        

        for (Map.Entry<String, List<HakuViewModel>> e : set) {
            HakuResultRow rowStyle = new HakuResultRow();
            Object format = rowStyle.format("", false);
            hc.addContainerProperty(COLUMN_A, format.getClass(), rowStyle.format(e.getKey(), false));
            Object rootItem = hc.addItem();

            hc.getContainerProperty(rootItem, COLUMN_A).setValue(rowStyle.format(e.getKey(), false));

            for (HakuViewModel curHaku : e.getValue()) {
                HakuResultRow rowStyleInner = new HakuResultRow(curHaku);
                Object subItem = hc.addItem();
                hc.setParent(subItem, rootItem);
                hc.getContainerProperty(subItem, COLUMN_A).setValue(rowStyleInner.format(curHaku.getHaunTunniste(), true));
                hc.setChildrenAllowed(subItem, false);
            }
        }
        return hc;
    }

    public void showAddHakuDokumenttiView() {
        loadEditForm(new HakuViewModel());
        
    }
    
    public void loadViewForm(HakuViewModel haku) {
        LOG.info("loadViewForm()");

        rootView.getAppLeftLayout().removeAllComponents();
        rootView.getAppRightLayout().removeAllComponents();

        rootView.getAppLeftLayout().addComponent(new Label("LEFT"));

        rootView.getAppRightLayout().addComponent(rootView.getBreadcrumbsView());
        rootView.getAppRightLayout().addComponent(new Label("Tähän haun tarkastelunäyttö" + haku.getHaunTunniste()));
        
    }
    
    public void loadEditForm(HakuViewModel haku) {
        LOG.info("loadEditForm()");

        rootView.getAppLeftLayout().removeAllComponents();
        rootView.getAppRightLayout().removeAllComponents();

        rootView.getAppLeftLayout().addComponent(new Label("LEFT"));

        rootView.getAppRightLayout().addComponent(rootView.getBreadcrumbsView());
        rootView.getAppRightLayout().addComponent(new Label("Tähän haun muokkauslomake: " + haku.getHaunTunniste()));
        
    }

    /**
     * Gets the searchSpec object.
     * @return
     */
    public KoulutusSearchSpesificationViewModel getSearchSpec() {
        return searchSpec;
    }

    /**
     * Sets the hakuList view.
     * @param hakuList the hakuList to set
     */
    public void setHakuList(ListHakuView hakuList) {
        this.hakuList = hakuList;
    }
    
    /**
     * Creation of some initial mock data.
     */
    private void createData() {
        HakuViewModel hak1 = new HakuViewModel();
        hak1.setHaunTunniste("Kevään 2013 yhteishaku");
        hak1.setHakutyyppi("Yhteishaut");
        
        HakuViewModel hak2 = new HakuViewModel();
        hak2.setHaunTunniste("Syksyn 2013 yhteishaku");
        hak2.setHakutyyppi("Yhteishaut");
        
        HakuViewModel hak3 = new HakuViewModel();
        hak3.setHaunTunniste("Kevään 2013 erillishaku");
        hak3.setHakutyyppi("Erillishaut");
        
        hakuDb.add(hak1);
        hakuDb.add(hak2);
        hakuDb.add(hak3);
    }
    
    public void showShowHakukohdeView() {
        
    }
    
    public void setHakuViewModel(HakuViewModel hakuModelParam) {
        hakuModel = hakuModelParam;
    }
    
    public void saveHakuLuonnoksenaModel() {
        hakuModel.setHakuValmis(false);
        LOG.info("Haku tallennettu luonnoksena");
    }
    
    public void saveHakuValmiina() {
        hakuModel.setHakuValmis(true);
        LOG.info("Haku tallennettu valmiina");
    }

    public void setRootView(TarjontaRootView rootView) {
        this.rootView = rootView;
    }

    public void removeHaku(HakuViewModel haku) {
        int index = -1;
        for (int i = 0; i < hakuDb.size(); ++i) {
           if (hakuDb.get(i).getHaunTunniste().equals(haku.getHaunTunniste())) {
               index = i;
           }
       }
        if (index > -1) {
            hakuDb.remove(index);
        }
        hakuList.reload();
    }

    /**
     * @param editHaku the editHaku to set
     */
    public void setEditHaku(EditHakuView editHaku) {
        this.editHaku = editHaku;
    }

    public EditHakuView getEditHaku() {
        return editHaku;
    }

}
