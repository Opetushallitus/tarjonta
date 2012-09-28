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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import fi.vm.sade.tarjonta.service.TarjontaService;
import fi.vm.sade.tarjonta.service.types.ListaaHakuTyyppi;
import fi.vm.sade.tarjonta.service.types.tarjonta.HakuDto;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import fi.vm.sade.tarjonta.ui.model.HakuaikaViewModel;
import fi.vm.sade.tarjonta.ui.model.HakukohdeViewModel;

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
    
    @Autowired
    private TarjontaService tarjontaService;



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
        
        haut = retrieveHaut();

        //Grouping the HakuViewModel objects based on hakutyyppi
        for (HakuViewModel curHaku : haut) {
            LOG.info("getTreeDataSource() curHaku: " + curHaku.getHakuOid() + ", hakutyyppi: " + curHaku.getHakutapa());
            if (!map.containsKey(curHaku.getHakutapa())) {
                LOG.info("Adding a new key to the map: " + curHaku.getHakutapa());
                List<HakuViewModel> hautM = new ArrayList<HakuViewModel>();
                hautM.add(curHaku);
                KoodiType hakutapaKoodi = this.koodiService.getKoodiByUri(curHaku.getHakutapa());
                map.put((hakutapaKoodi != null) ? hakutapaKoodi.getKoodiArvo() : curHaku.getHakutapa(), hautM); 
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
        hakuModel.setHakuValmis(false);    
        if (hakuModel.getHakuOid() == null) {
            try {
                hakuModel.setHakuOid(oidService.newOid(NodeClassCode.TEKN_5));
                hakuModel.setHaunTunniste((hakuModel.getHaunTunniste() == null) ? hakuModel.getHakuOid() : hakuModel.getHaunTunniste());
            } catch (Exception ex) {
                LOG.error(ex.getMessage());
            }
            this.tarjontaService.lisaaHaku(hakuModel.getHakuDto());
        } else {
            this.tarjontaService.paivitaHaku(hakuModel.getHakuDto());
        }
        LOG.info("Haku tallennettu luonnoksena");
    }
    
    /**
     * Saves haku as ready.
     */
    public void saveHakuValmiina() {
        hakuModel.setHakuValmis(true);
        LOG.info("Hakutapa: " + hakuModel.getHakutapa());
        if (hakuModel.getHakuOid() == null) {
            try {
                hakuModel.setHakuOid(oidService.newOid(NodeClassCode.TEKN_5));
                hakuModel.setHaunTunniste((hakuModel.getHaunTunniste() == null) ? hakuModel.getHakuOid() : hakuModel.getHaunTunniste());
            } catch (Exception ex) {
                LOG.error(ex.getMessage());
            }
            this.tarjontaService.lisaaHaku(hakuModel.getHakuDto());
        } else {
            this.tarjontaService.paivitaHaku(hakuModel.getHakuDto());
        }
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
        this.tarjontaService.poistaHaku(haku.getHakuDto());
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
    
    public HakuViewModel getHakuModel() {
        return hakuModel;
    }

    public String getKoodiArvo(String koodiUri) {
        KoodiType koodi = this.koodiService.getKoodiByUri(koodiUri);
        return (koodi != null) ? koodi.getKoodiArvo() : koodiUri; 
    }

    public String getHakuaika() {
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        String startDateStr = (hakuModel.getAlkamisPvm() != null) ? formatter.format(hakuModel.getAlkamisPvm()) : "";
        String endDateStr = (hakuModel.getPaattymisPvm() != null) ? formatter.format(hakuModel.getPaattymisPvm()) : "";        
        return startDateStr  + " - " + endDateStr;
    }

    public List<HakuaikaViewModel> getSisaisetHautSource() {
        List<HakuaikaViewModel> sisHaut = new ArrayList<HakuaikaViewModel>();
        return sisHaut;
    }

    public List<HakukohdeViewModel> getHakukohteet() {
        List<HakukohdeViewModel> hakukohteet = new ArrayList<HakukohdeViewModel>();
        return hakukohteet;
    }
    
    private List<HakuViewModel> retrieveHaut() {
        List<HakuViewModel> haut = new ArrayList<HakuViewModel>();
        for (HakuDto curHaku : this.tarjontaService.listHaku(new ListaaHakuTyyppi()).getResponse()) {
            haut.add(new HakuViewModel(curHaku));
        }
        return haut;
    }
    
}
