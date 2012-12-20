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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import com.vaadin.ui.Window;

import fi.vm.sade.tarjonta.ui.enums.UserNotification;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.model.HakuViewModel;
import fi.vm.sade.tarjonta.ui.model.HakuaikaViewModel;
import fi.vm.sade.tarjonta.ui.model.HakukohdeViewModel;

import fi.vm.sade.tarjonta.ui.model.KoulutusSearchSpesificationViewModel;
import fi.vm.sade.tarjonta.ui.view.haku.EditHakuView;
import fi.vm.sade.tarjonta.ui.view.haku.ListHakuView;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import fi.vm.sade.koodisto.util.KoodistoHelper;
import fi.vm.sade.oid.service.OIDService;
import fi.vm.sade.oid.service.types.NodeClassCode;
import fi.vm.sade.tarjonta.service.GenericFault;
import fi.vm.sade.tarjonta.service.TarjontaAdminService;
import fi.vm.sade.tarjonta.service.TarjontaPublicService;
import fi.vm.sade.tarjonta.service.types.ListaaHakuTyyppi;
import fi.vm.sade.tarjonta.service.types.HakuTyyppi;
import fi.vm.sade.tarjonta.service.types.ListHakuVastausTyyppi;

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
    private List<HakuViewModel> selectedhaut = new ArrayList<HakuViewModel>();
    private ListHakuView hakuList;
    private EditHakuView editHaku;
    private HakuViewModel hakuModel;
    @Autowired
    private OIDService oidService;
    @Autowired
    private KoodiService koodiService;
    @Autowired
    private TarjontaPublicService tarjontaPublicService;
    @Autowired
    private TarjontaAdminService tarjontaAdminService;
    public static final String COLUMN_A = "Kategoriat";

    public HakuPresenter() {
    }

    /**
     * Performs the search according to searchSpec and reloads the hakuList
     * view.
     */
    public void doSearch() {
        hakuList.reload();
    }

    private Map<String, List<HakuViewModel>> groupHakus(List<HakuViewModel> hakus) {
        Map<String, List<HakuViewModel>> map = new HashMap<String, List<HakuViewModel>>();

        //Grouping the HakuViewModel objects based on hakutapa
        for (HakuViewModel curHaku : hakus) {
            LOG.info("getTreeDataSource() curHaku: " + curHaku.getHakuOid() + ", hakutyyppi: " + curHaku.getHakutapa());
            String hakuKey = "";
            try {
                String hakutapaUri = parseKoodiUri(curHaku.getHakutapa());
                SearchKoodisCriteriaType searchCriteria = KoodiServiceSearchCriteriaBuilder.latestValidAcceptedKoodiByUri(hakutapaUri);
                List<KoodiType> result = this.koodiService.searchKoodis(searchCriteria);
                if (result.size() != 1) {
                    throw new RuntimeException("No valid accepted koodi found for URI " + curHaku.getHakutapa());
                }

                KoodiType hakutapaKoodi = result.get(0);
                hakuKey = KoodistoHelper.getKoodiMetadataForLanguage(hakutapaKoodi, KoodistoHelper.getKieliForLocale(I18N.getLocale())).getNimi();
            } catch (Exception ex) {
                hakuKey = curHaku.getHakutapa();
            }

            if (!map.containsKey(hakuKey)) {
                LOG.info("Adding a new key to the map: " + curHaku.getHakutapa());
                List<HakuViewModel> hautM = new ArrayList<HakuViewModel>();
                hautM.add(curHaku);
                map.put(hakuKey, hautM);
            } else {
                map.get(hakuKey).add(curHaku);
            }
        }

        return map;
    }

    /**
     *
     * Gets the datasource for hakuList.
     *
     * @return
     */
    public Map<String, List<HakuViewModel>> getTreeDataSource() {



        haut = retrieveHaut();

        return groupHakus(haut);

    }

    public void loadListDataWithSearchCriteria(KoulutusSearchSpesificationViewModel searchVm) {
        hakuList.setDataSource(getTreeDataSourceWithSearchCriteria(searchVm));
    }

    private Map<String, List<HakuViewModel>> getTreeDataSourceWithSearchCriteria(KoulutusSearchSpesificationViewModel searchVm) {
        Map<String, List<HakuViewModel>> returnVal = new HashMap<String, List<HakuViewModel>>();
        if (searchVm.getSearchStr() != null) {
            ListaaHakuTyyppi req = new ListaaHakuTyyppi();
            req.setHakuSana(searchVm.getSearchStr());
            req.setHakuSanaKielikoodi(I18N.getLocale().getLanguage());
            List<HakuViewModel> hakuses = retrieveHaut(req);
            returnVal = groupHakus(hakuses);
        }
        return returnVal;
    }

    private String parseKoodiUri(String koodiUriAndVersion) {
        int index = koodiUriAndVersion.lastIndexOf('#');
        if (index > -1) {
            return koodiUriAndVersion.substring(0, index);
        }
        return koodiUriAndVersion;
    }

    /**
     * Gets the searchSpec object.
     *
     * @return
     */
    public KoulutusSearchSpesificationViewModel getSearchSpec() {
        return searchSpec;
    }

    /**
     * Sets the hakuList view.
     *
     * @param hakuList the hakuList to set
     */
    public void setHakuList(ListHakuView hakuList) {
        this.hakuList = hakuList;
    }

    /**
     * Sets the hakuModel, used in the edit form of haku.
     *
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
            tarjontaAdminService.lisaaHaku(hakuModel.getHakuDto());
        } else {
            tarjontaAdminService.paivitaHaku(hakuModel.getHakuDto());
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
            tarjontaAdminService.lisaaHaku(hakuModel.getHakuDto());
        } else {
            tarjontaAdminService.paivitaHaku(hakuModel.getHakuDto());
        }
        LOG.info("Haku tallennettu valmiina");
    }

    /**
     * Removes the haku given as parameter
     *
     * @param haku the haku to remove.
     */
    public void removeHaku(HakuViewModel haku) {
        try {
        tarjontaAdminService.poistaHaku(haku.getHakuDto());
        } catch (Exception exp) {
            if (exp.getMessage().contains("fi.vm.sade.tarjonta.service.business.exception.HakuUsedException"))  {
                hakuList.showErrorMessage(I18N.getMessage("notification.error.haku.used"));
            }
        }
        hakuList.reload();
    }

    /**
     * Returns the haut.
     *
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
     *
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
     * Gets the hakuModel.
     *
     * @return the hakuModel to return
     */
    public HakuViewModel getHakuModel() {
        return hakuModel;
    }

    /**
     *
     * @return the inner hakuajat for a haku.
     */
    public List<HakuaikaViewModel> getSisaisetHautSource() {
        List<HakuaikaViewModel> sisHaut = new ArrayList<HakuaikaViewModel>();
        return sisHaut;
    }

    /**
     *
     * @return the hakukohde obects velonging to the hakuModel haku.
     */
    public List<HakukohdeViewModel> getHakukohteet() {
        List<HakukohdeViewModel> hakukohteet = new ArrayList<HakukohdeViewModel>();
        return hakukohteet;
    }

    /**
     * Gets the currently selectedHaut.
     *
     * @return
     */
    public List<HakuViewModel> getSelectedhaut() {
        return selectedhaut;
    }

    public void setSelectedHaut(List<HakuViewModel> selectedHaut) {
        this.selectedhaut = selectedHaut;
    }

    /**
     * Removes the selected haku objects from the database.
     */
    public void removeSelectedHaut() {
        int removalLaskuri = 0;
        String errorNotes = "";
        for (HakuViewModel curHaku : selectedhaut) {
            try {
                tarjontaAdminService.poistaHaku(curHaku.getHakuDto());
                ++removalLaskuri;
            } catch (Throwable e) {
                if (e.getMessage().contains("fi.vm.sade.tarjonta.service.business.exception.HakuUsedException")) {
                    errorNotes += I18N.getMessage("notification.deleted.haku.used.multiple", TarjontaUIHelper.getClosestHakuName(I18N.getLocale(), curHaku)) + "<br/>";
                } else {
                    LOG.error(e.getMessage());
                }
            }
        }

        String notificationMessage = "<br />" + I18N.getMessage("notification.deleted.haut", removalLaskuri) + "<br />" + errorNotes;

        selectedhaut.clear();
        hakuList.reload();
        this.hakuList.showNotification(I18N.getMessage("notification.deleted.haut.title"),
                                        notificationMessage,
                                        Window.Notification.TYPE_HUMANIZED_MESSAGE);
    }

    private List<HakuViewModel> retrieveHaut(ListaaHakuTyyppi req) {
        List<HakuViewModel> hakus = new ArrayList<HakuViewModel>();
        ListHakuVastausTyyppi vastaus = tarjontaPublicService.listHaku(req);
        if (vastaus != null && vastaus.getResponse() != null) {
            for (HakuTyyppi curHaku : vastaus.getResponse()) {
                hakus.add(new HakuViewModel(curHaku));
            }
        }
        return hakus;
    }

    private List<HakuViewModel> retrieveHaut() {
        List<HakuViewModel> haut = new ArrayList<HakuViewModel>();
        ListHakuVastausTyyppi vastaus = tarjontaPublicService.listHaku(new ListaaHakuTyyppi());
        if (vastaus != null && vastaus.getResponse() != null) {
            for (HakuTyyppi curHaku : vastaus.getResponse()) {
                haut.add(new HakuViewModel(curHaku));
            }
        }
        return haut;
    }

    public void closeHakuRemovalDialog() {
        this.hakuList.closeHakuRemovalDialog();

    }

    public void selectHaku(HakuViewModel haku) {
        selectedhaut.add(haku);
        this.hakuList.toggleRemoveButton(true);
    }

    public void unSelectHaku(HakuViewModel haku) {
        LOG.info("unSelectHaku({})", haku);
        selectedhaut.remove(haku);
        this.hakuList.toggleRemoveButton(!selectedhaut.isEmpty());
    }


}
