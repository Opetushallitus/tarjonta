/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.vm.sade.tarjonta.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mlyly
 */
public class TarjontaPresenter {
    
    public static final Logger LOG = LoggerFactory.getLogger(TarjontaPresenter.class);
    
    
    public void searchKoulutus() {
        LOG.info("searchKoulutus()");
    }
    
    public void sortKoulutusSearchResult() {
        LOG.info("sortKoulutusSearchResult()");
    }
    
    public void editKoulutus() {
        LOG.info("editKoulutus()");
    }
    
    public void createNewKoulutus() {
        LOG.info("createNewKoulutus()");
    }
    
    public void deleteKoulutus() {
        LOG.info("deleteKoulutus()");
    }
    
    public void selectKoulutusAll() {
        LOG.info("selectKoulutusAll()");
    }
    
    public void saveKoulutusPerustiedot() {
        LOG.info("saveKoulutusPerustiedot()");
        // Get from model, validate any extra stuff needed and call service save
    }
    
    public void koulutuksenPerustiedotAddNewContactPerson() {
        LOG.info("koulutuksenPerustiedotAddNewContactPerson()");
    }
    
    

    
    
}
