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

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;

import fi.vm.sade.tarjonta.ui.model.KoulutusSearchSpesificationViewModel;
import fi.vm.sade.tarjonta.ui.view.haku.EditHakuView;
import fi.vm.sade.tarjonta.ui.view.haku.ListHakuView;

import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.oid.service.types.NodeClassCode;

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
    private List<HakuViewModel> haut = new ArrayList<HakuViewModel>();

    private ListHakuView hakuList;
    
    private EditHakuView editHaku;
    
    private TarjontaRootView rootView;
    
    HakuViewModel hakuModel;
    
    @Autowired
    private OIDService oidService;
    
    @Autowired
    private KoodiService koodiService;



    public static final String COLUMN_A = "Kategoriat";
    
    public HakuPresenter() {
        
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
    public Map<String, List<HakuViewModel>> getTreeDataSource() {
         
        Map<String, List<HakuViewModel>> map = new HashMap<String, List<HakuViewModel>>();

        //Grouping the HakuViewModel objects based on hakutyyppi
        for (HakuViewModel curHaku : haut) {
            LOG.info("getTreeDataSource() curHaku: " + curHaku.getHakuOid() + ", hakutyyppi: " + curHaku.getHakutapa());
            if (!map.containsKey(curHaku.getHakutapa())) {
                LOG.info("Adding a new key to the map: " + curHaku.getHakutapa());
                List<HakuViewModel> haut = new ArrayList<HakuViewModel>();
                haut.add(curHaku);
                KoodiType hakutapaKoodi = this.koodiService.getKoodiByUri(curHaku.getHakutapa());
                map.put((hakutapaKoodi != null) ? hakutapaKoodi.getKoodiArvo() : curHaku.getHakutapa(), haut); 
            } else {
                KoodiType hakutapaKoodi = this.koodiService.getKoodiByUri(curHaku.getHakutapa());
                map.get((hakutapaKoodi != null) ?  hakutapaKoodi.getKoodiArvo() : curHaku.getHakutapa()).add(curHaku);
            }   
        }  
        return map;
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
    
    
    
    public void showShowHakukohdeView() {
        
    }
    
    /**
     * Sets the hakuModel, used in the edit form of haku.
     * @param hakuModelParam the hakuModel to set.
     */
    public void setHakuViewModel(HakuViewModel hakuModelParam) {
        hakuModel = hakuModelParam;
    }
    
    /**
     * Saves the haku as draft.
     */
    public void saveHakuLuonnoksenaModel() {
        if (hakuModel.getHakuOid() == null) {
            try {
                hakuModel.setHakuOid(oidService.newOid(NodeClassCode.TEKN_5));
            } catch (Exception ex) {
                LOG.error(ex.getMessage());
            }
            haut.add(hakuModel);
        }
        
        hakuModel.setHakuValmis(false);
        
        LOG.info("Haku tallennettu luonnoksena");
    }
    
    /**
     * Saves haku as ready.
     */
    public void saveHakuValmiina() {
        LOG.info("Hakutapa: " + hakuModel.getHakutapa());
        if (hakuModel.getHakuOid() == null) {
            try {
                hakuModel.setHakuOid(oidService.newOid(NodeClassCode.TEKN_5));
            } catch (Exception ex) {
                LOG.error(ex.getMessage());
            }
            
            haut.add(hakuModel);
            LOG.info("Haut size: " + haut.size());
        }
        hakuModel.setHakuValmis(true);
        LOG.info("Haku tallennettu valmiina");
    }

    /**
     * Sets the rootView
     * 
     * @param rootView the rootView to set.
     */
    public void setRootView(TarjontaRootView rootView) {
        this.rootView = rootView;
    }

    /**
     * Removes the haku given as parameter
     * @param haku the haku to remove.
     */
    public void removeHaku(HakuViewModel haku) {
        int index = -1;
        for (int i = 0; i < haut.size(); ++i) {
           if (haut.get(i).getHaunTunniste().equals(haku.getHaunTunniste())) {
               index = i;
           }
       }
        if (index > -1) {
            haut.remove(index);
        }
        hakuList.reload();
    }
    
    /**
     * Returns the haut.
     * @return
     */
    public List<HakuViewModel> getHaut() {
        return haut;
    }

    /**
     * @param editHaku the editHaku to set
     */
    public void setEditHaku(EditHakuView editHaku) {
        this.editHaku = editHaku;
    }

    /**
     * returns the editHaku
     * @return 
     */
    public EditHakuView getEditHaku() {
        return editHaku;
    }

    /**
     * Refreshes the hakulist.
     */
    public void refreshHakulist() {
        hakuList.reload();
    }
    
    /**
     * Creation of some initial mock data.
     */
    @PostConstruct
    private void createData() {
        haut = new ArrayList<HakuViewModel>();
        HakuViewModel hak1 = new HakuViewModel();
        try {
            hak1.setHakuOid(oidService.newOid(NodeClassCode.TEKN_5));
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
        }
        hak1.setNimiFi("Kevään 2013 yhteishaku");
        hak1.setHakutapa("Testiyhteishaut");
        hak1.setKaytetaanJarjestelmanHakulomaketta(true);
        
        HakuViewModel hak2 = new HakuViewModel();
        try {
            hak2.setHakuOid(oidService.newOid(NodeClassCode.TEKN_5));
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
        }
        hak2.setNimiFi("Syksyn 2013 yhteishaku");
        hak2.setHakutapa("Testiyhteishaut");
        hak2.setKaytetaanJarjestelmanHakulomaketta(false);
        
        HakuViewModel hak3 = new HakuViewModel();
        try {
            hak3.setHakuOid(oidService.newOid(NodeClassCode.TEKN_5));
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
        }
        hak3.setNimiFi("Kevään 2013 erillishaku");
        hak3.setHakutapa("Testierillishaut");
        hak3.setKaytetaanJarjestelmanHakulomaketta(true);
        
        haut.add(hak1);
        haut.add(hak2);
        haut.add(hak3);
    }
}
