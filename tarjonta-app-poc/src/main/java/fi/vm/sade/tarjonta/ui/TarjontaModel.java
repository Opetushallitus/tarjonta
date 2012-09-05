/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.vm.sade.tarjonta.ui;

import fi.vm.sade.tarjonta.ui.model.KoulutusSearchSpesificationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

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
    
    // TODO KoulutusDTO?
    // TODO HakueraDTO?
    // TODO Search results
    
    // Search specification DTO
    private KoulutusSearchSpesificationDTO searchSpesification = new KoulutusSearchSpesificationDTO();
    
    public KoulutusSearchSpesificationDTO getSearchSpesification() {
        return searchSpesification;
    }
    
}
