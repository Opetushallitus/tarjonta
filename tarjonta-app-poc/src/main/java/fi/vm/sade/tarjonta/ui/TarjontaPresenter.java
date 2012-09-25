/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.vm.sade.tarjonta.ui;

import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.tarjonta.ui.enums.Notification;
import fi.vm.sade.tarjonta.ui.model.view.AddHakuDokumenttiView;
import fi.vm.sade.tarjonta.ui.model.view.EditKoulutusView;
import fi.vm.sade.tarjonta.ui.model.view.EditSiirraHakukohteitaView;
import fi.vm.sade.tarjonta.ui.model.view.MainKoulutusView;
import fi.vm.sade.tarjonta.ui.model.view.ShowHakukohdeView;
import fi.vm.sade.tarjonta.ui.model.view.ShowKoulutusView;
import fi.vm.sade.tarjonta.ui.poc.TarjontaWindow;
import fi.vm.sade.vaadin.constants.StyleEnum;
import fi.vm.sade.vaadin.oph.demodata.DataSource;
import fi.vm.sade.vaadin.oph.demodata.row.MultiActionTableStyle;
import fi.vm.sade.vaadin.dto.ButtonDTO;
import fi.vm.sade.vaadin.dto.PageNavigationDTO;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

/**
 *
 * @author mlyly
 */
@Component
@Configurable(preConstruction = false)
public class TarjontaPresenter {

    public static final Logger LOG = LoggerFactory.getLogger(TarjontaPresenter.class);
    private static final int PAGE_MAX = 30;
    @Autowired(required = true)
    private TarjontaModel _model;
    private TarjontaWindow _tarjontaWindow;
    private int pageCurrent = 12;

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

    public void demoInformation(Notification msg) {
        LOG.info("Show notification : ", msg);
        _tarjontaWindow.showNotification("Viesti:", msg.getInfo());
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
        getRightLayout().removeAllComponents();
        getRightLayout().addComponent(new MainKoulutusView());
    }

    public void showShowKoulutusView() {
        // TODO show "show koulutus view"
        ButtonDTO btnPrev = new ButtonDTO("< Edellinen (Sosiaali- ja terveysalan lähitutkinto, pk)", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                showShowKoulutusView();
            }
        });

        ButtonDTO btnNext = new ButtonDTO("<(Sähkö- ja automaatiotekniikan perustutkinto, pk) seuraava >", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                showShowKoulutusView();
            }
        });

        PageNavigationDTO dto = new PageNavigationDTO(btnPrev, btnNext, "12 / 30");

        getRightLayout().removeAllComponents();
        getRightLayout().addComponent(new ShowKoulutusView("Autoalan perustutkinto, kv", null, dto));
    }

    public void showEditKolutusView() {
        LOG.debug("In showEditKolutusView");
        getRightLayout().removeAllComponents();
        getRightLayout().addComponent(new EditKoulutusView());
    }

    public void setTarjontaWindow(TarjontaWindow aThis) {
        _tarjontaWindow = aThis;
    }

    public void showAddHakuDokumenttiView() {
        LOG.debug("In showAddHakuDokumenttiView");
        getRightLayout().removeAllComponents();
        getRightLayout().addComponent(new AddHakuDokumenttiView());
    }

    public void showShowHakukohdeView() {
        ButtonDTO btnPrev = new ButtonDTO("< Edellinen (Sosiaali- ja terveysalan lähitutkinto, pk)", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (pageCurrent > 1) {
                    pageCurrent--;
                    showShowHakukohdeView();
                }
            }
        });

        ButtonDTO btnNext = new ButtonDTO("<(Sähkö- ja automaatiotekniikan perustutkinto, pk) seuraava >", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (pageCurrent < PAGE_MAX) {
                    ++pageCurrent;
                    showShowHakukohdeView();
                }
            }
        });

        PageNavigationDTO dto = new PageNavigationDTO(btnPrev, btnNext,  pageCurrent + " / " + PAGE_MAX);

        getRightLayout().removeAllComponents();
        getRightLayout().addComponent(new ShowHakukohdeView("Autoalan perustutkinto, kv", null, dto));
    }

    public void showEditSiirraHakukohteitaView() {
        LOG.debug("In showEditSiirraHakukohteitaView");

        final EditSiirraHakukohteitaView modal = new EditSiirraHakukohteitaView("Siirrä hakukohteita täydennyshakuun");
        _tarjontaWindow.getWindow().addWindow(modal);

        modal.addNavigationButton("Peruuta", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                // Stay in same view
                _tarjontaWindow.getWindow().removeWindow(modal);
                modal.removeDialogButtons();
            }
        }, StyleEnum.STYLE_BUTTON_SECONDARY);

        modal.addNavigationButton("Jatka", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                _tarjontaWindow.getWindow().removeWindow(modal);
                modal.removeDialogButtons();

                showShowHakukohdeView();
            }
        }, StyleEnum.STYLE_BUTTON_PRIMARY);

        modal.buildDialogButtons();
    }

    public HierarchicalContainer getTreeDataSource() {
        return DataSource.treeTableData(new MultiActionTableStyle());
    }

    /*
     * Get a right layout instance from the main split panel.
     */
    private VerticalLayout getRightLayout() {
        return _tarjontaWindow.getMainSplitPanel().getMainRightLayout();
    }
}
