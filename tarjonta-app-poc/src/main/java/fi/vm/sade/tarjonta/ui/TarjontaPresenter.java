/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.vm.sade.tarjonta.ui;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author mlyly
 */
@Configurable(preConstruction = false)
public class TarjontaPresenter {
    
    public static final Logger LOG = LoggerFactory.getLogger(TarjontaPresenter.class);
    
    @Autowired(required=true)
    private TarjontaModel _model;
    
    public TarjontaPresenter() {
        LOG.info("TarjontaPresenter() : model={}", _model);
    }
    
    @PostConstruct
    public void initialize() {
        LOG.info("initialize() : model={}", _model);
    }
    
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

    /*
     * APP Identifier.
     */
    public boolean showIdentifier() {
        return _model.getShowIdentifier();
    }
    
    public String getIdentifier() {
        return _model.getIdentifier();
    }
    
}
