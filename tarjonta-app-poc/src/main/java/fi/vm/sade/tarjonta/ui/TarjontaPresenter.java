/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.vm.sade.tarjonta.ui;

import fi.vm.sade.tarjonta.ui.model.view.EditKoulutusView;
import fi.vm.sade.tarjonta.ui.model.view.MainKoulutusView;
import fi.vm.sade.tarjonta.ui.model.view.MainSearchView;
import fi.vm.sade.tarjonta.ui.model.view.ShowKoulutusView;
import fi.vm.sade.tarjonta.ui.poc.TarjontaWindow;
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

    public void saveKoulutusPerustiedot(boolean isComplete) {
        LOG.info("saveKoulutusPerustiedot(): complete: {}", isComplete);
        // TODO Get from model, validate any extra stuff needed and call service save
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


    /**
     * Switch to main search view.
     */
    public void showMainKoulutusView() {
        LOG.info("showMainKoulutusView()");
        _tarjontaWindow.getMainSplitPanel().getMainRightLayout().removeAllComponents();
        _tarjontaWindow.getMainSplitPanel().getMainRightLayout().addComponent(new MainKoulutusView());
    }

    public void showShowKoulutusView() {
        // TODO show "show koulutus view"
        _tarjontaWindow.getMainSplitPanel().getMainRightLayout().removeAllComponents();
        _tarjontaWindow.getMainSplitPanel().getMainRightLayout().addComponent(new ShowKoulutusView());
    }

    public void showEditKolutusView() {
        _tarjontaWindow.getMainSplitPanel().getMainRightLayout().removeAllComponents();
        _tarjontaWindow.getMainSplitPanel().getMainRightLayout().addComponent(new EditKoulutusView());
    }

    private TarjontaWindow _tarjontaWindow;

    public void setTarjontaWindow(TarjontaWindow aThis) {
        _tarjontaWindow = aThis;
    }


}
