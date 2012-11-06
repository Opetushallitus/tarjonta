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
package fi.vm.sade.tarjonta.ui.model;

import fi.vm.sade.tarjonta.service.types.HaeHakukohteetVastausTyyppi.HakukohdeTulos;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetVastausTyyppi.KoulutusTulos;
import fi.vm.sade.tarjonta.ui.enums.DocumentStatus;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Contains the data and state of the Tarjonta UI.
 *
 * @author mlyly
 */
@Component
@Configurable
public class TarjontaModel extends BaseUIViewModel {

    // Show label that shows last modification
    @Value("${common.showAppIdentifier:true}")
    private Boolean _showIdentifier;
    @Value("${tarjonta-app.identifier:APPLICATION IDENTIFIER NOT AVAILABLE}")
    private String _identifier;
    private KoulutusSearchSpesificationViewModel _searchSpec = new KoulutusSearchSpesificationViewModel();
    private KoulutusToisenAsteenPerustiedotViewModel _koulutusPerustiedotModel;
    private KoulutusLisatiedotModel _koulutusLisatiedotModel;
    private List<HakukohdeTulos> _hakukohteet;
    private List<HakukohdeTulos> _selectedhakukohteet;
    private List<KoulutusTulos> _koulutukset;
    private List<KoulutusTulos> _selectedKoulutukset;
    private HakukohdeViewModel hakukohde;

    /*
     * Selected organisaatio data:
     */
    private String organisaatioName;
    private String organisaatioOid;

    public String getIdentifier() {
        return _identifier;
    }

    public void setIdentifier(String _identifier) {
        this._identifier = _identifier;
    }

    public boolean isShowIdentifier() {
        return _showIdentifier;
    }

    public void setShowIdentifier(boolean _showIdentifier) {
        this._showIdentifier = _showIdentifier;
    }

    public KoulutusSearchSpesificationViewModel getSearchSpec() {
        return _searchSpec;
    }

    public List<HakukohdeTulos> getHakukohteet() {
        if (_hakukohteet == null) {
            _hakukohteet = new ArrayList<HakukohdeTulos>();
        }
        return _hakukohteet;
    }

    public void setHakukohteet(List<HakukohdeTulos> hakukohteet) {
        this._hakukohteet = hakukohteet;
    }

    public List<HakukohdeTulos> getSelectedhakukohteet() {
        if (_selectedhakukohteet == null) {
            _selectedhakukohteet = new ArrayList<HakukohdeTulos>();
        }
        return _selectedhakukohteet;
    }

    public KoulutusToisenAsteenPerustiedotViewModel getKoulutusPerustiedotModel() {
        if (_koulutusPerustiedotModel == null) {
            _koulutusPerustiedotModel = new KoulutusToisenAsteenPerustiedotViewModel(DocumentStatus.NEW);
        }
        return _koulutusPerustiedotModel;
    }

    public void setKoulutusPerustiedotModel(KoulutusToisenAsteenPerustiedotViewModel koulutusPerustiedotModel) {
        _koulutusPerustiedotModel = koulutusPerustiedotModel;
    }

    /**
     * Gets the currently selected (in ListKoulutusView) koulutus objects.
     *
     * @return the selected koulutukset
     */
    public List<KoulutusTulos> getSelectedKoulutukset() {
        if (_selectedKoulutukset == null) {
            _selectedKoulutukset = new ArrayList<KoulutusTulos>();
        }
        return _selectedKoulutukset;
    }

    /**
     * Sets the koulutus objects that is the koulutus list used in
     * ListKoulutusView.
     *
     * @param koulutusTulos the koulutus objects to set
     */
    public void setKoulutukset(List<KoulutusTulos> koulutusTulos) {
        _koulutukset = koulutusTulos;
    }

    /**
     * Gets the koulutus objects that is the koulutus list used in
     * ListKoulutusView.
     *
     * @return
     */
    public List<KoulutusTulos> getKoulutukset() {
        if (_koulutukset == null) {
            _koulutukset = new ArrayList<KoulutusTulos>();
        }
        return _koulutukset;
    }

    /**
     * @return the hakukohde
     */
    public HakukohdeViewModel getHakukohde() {
        if (hakukohde == null) {
            hakukohde = new HakukohdeViewModel();
        }
        return hakukohde;
    }

    /**
     * @param hakukohde the hakukohde to set
     */
    public void setHakukohde(HakukohdeViewModel hakukohde) {
        this.hakukohde = hakukohde;
    }

    /**
     * Gets the name of the selected organisaatio
     *
     * @return the organisaatio name
     */
    public String getOrganisaatioName() {
        return organisaatioName;
    }

    /**
     * Sets the name of the selected organisaatio
     *
     * @param organisaatioName - the organisaatio name to set
     */
    public void setOrganisaatioName(String organisaatioName) {
        this.organisaatioName = organisaatioName;
    }

    /**
     * Gets the oid of the selected organisaatio
     *
     * @return the organisaatio oid
     */
    public String getOrganisaatioOid() {
        return organisaatioOid;
    }

    /**
     * Sets the oid of the selected organisaatio
     *
     * @param organisaatioOid - the organisaatio oid to set
     */
    public void setOrganisaatioOid(String organisaatioOid) {
        this.organisaatioOid = organisaatioOid;
    }


    public KoulutusLisatiedotModel getKoulutusLisatiedotModel() {
        if (_koulutusLisatiedotModel == null) {
            _koulutusLisatiedotModel = new KoulutusLisatiedotModel();
        }
        return _koulutusLisatiedotModel;
    }

    public void setKoulutusLisatiedotModel(KoulutusLisatiedotModel _koulutusLisatiedotModel) {
        this._koulutusLisatiedotModel = _koulutusLisatiedotModel;
    }

}
