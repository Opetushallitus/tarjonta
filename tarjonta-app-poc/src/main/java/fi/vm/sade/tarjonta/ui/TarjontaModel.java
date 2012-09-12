/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.vm.sade.tarjonta.ui;

import fi.vm.sade.tarjonta.ui.model.KoulutusPerustiedotDTO;
import fi.vm.sade.tarjonta.ui.model.KoulutusSearchSpesificationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author mlyly
 */
@Configurable(preConstruction=false)
public class TarjontaModel {

    private static final Logger LOG = LoggerFactory.getLogger(TarjontaModel.class);

    public TarjontaModel() {
        LOG.info("TarjontaModel()");
    }

    // Show label that shows last modification
    @Value("${showAppIdentifier:true}")
    private Boolean _showIdentifier;
    @Value("${tarjonta-app.identifier:APPLICATION IDENTIFIER NOT AVAILABLE}")
    private String _identifier;

    private KoulutusSearchSpesificationDTO searchSpesification;
    private KoulutusPerustiedotDTO _koulutusPerustiedot;

    /**
     * Search spesification for Koulutus offerings.
     *
     * @return
     */
    public KoulutusSearchSpesificationDTO getSearchSpesification() {
        if (searchSpesification == null) {
            searchSpesification = new KoulutusSearchSpesificationDTO();
        }
        return searchSpesification;
    }

    /**
     * True if app identifier should be shown.
     *
     * @return
     */
    public Boolean getShowIdentifier() {
        return _showIdentifier;
    }

    /**
     * Get APP identifier.
     *
     * @return
     */
    public String getIdentifier() {
        return _identifier;
    }

    public KoulutusPerustiedotDTO getKoulutusPerustiedot() {
        if (_koulutusPerustiedot == null) {
            _koulutusPerustiedot = new KoulutusPerustiedotDTO();
        }
        return _koulutusPerustiedot;
    }

    public void setKoulutusPerustiedot(KoulutusPerustiedotDTO _koulutusPerustiedot) {
        this._koulutusPerustiedot = _koulutusPerustiedot;
    }

}
