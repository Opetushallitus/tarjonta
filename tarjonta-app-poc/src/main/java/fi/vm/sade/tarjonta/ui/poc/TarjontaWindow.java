package fi.vm.sade.tarjonta.ui.poc;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import fi.vm.sade.tarjonta.ui.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.model.view.CreateKoulutusView;
import fi.vm.sade.tarjonta.ui.model.view.EditKoulutusView;
import fi.vm.sade.tarjonta.ui.model.view.EditSiirraUudelleKaudelleView;
import fi.vm.sade.tarjonta.ui.model.view.MainResultView;
import fi.vm.sade.tarjonta.ui.model.view.MainSearchView;
import fi.vm.sade.tarjonta.ui.model.view.MainSplitPanelView;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.oph.demodata.DataSource;
import fi.vm.sade.vaadin.oph.demodata.row.MultiActionTableStyle;
import fi.vm.sade.vaadin.oph.helper.UiBuilder;
import fi.vm.sade.vaadin.oph.layout.AbstractDialogWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author jani
 */
@Configurable(preConstruction = true)
public class TarjontaWindow extends Window {

    private static final Logger LOG = LoggerFactory.getLogger(TarjontaWindow.class);
    @Autowired
    private TarjontaPresenter _presenter;
    private MainSplitPanelView main;
    private MainSearchView mainSearch;
    private MainResultView mainResult;
    private AbstractDialogWindow mainModalWindow;
    private ClickListener clLuoUusiKoulutusClickListener = new Button.ClickListener() {
        @Override
        public void buttonClick(ClickEvent event) {
            mainModalWindow = new CreateKoulutusView("Luo uusi koulutus");
            getWindow().addWindow(mainModalWindow);

            mainModalWindow.addDialogButton("Peruuta", new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    getWindow().removeWindow(mainModalWindow);
                    mainModalWindow.removeDialogButtons();
                    mainModalWindow = null;
                }
            });

            mainModalWindow.addDialogButton("Jatka", new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    LOG.info("buttonClick() - luo uusi koulutus click...");
                    EditKoulutusView f = new EditKoulutusView();
                    main.getMainRightLayout().removeAllComponents();
                    main.getMainRightLayout().addComponent(f);

                    getWindow().removeWindow(mainModalWindow);
                    mainModalWindow.removeDialogButtons();
                    mainModalWindow = null;
                }
            });

            mainModalWindow.buildDialogButtons();
        }
    };
    private ClickListener clSiirraUudelleKaudelleView = new Button.ClickListener() {
        @Override
        public void buttonClick(ClickEvent event) {
            mainModalWindow = new EditSiirraUudelleKaudelleView("Kopioi uudelle kaudelle");
            getWindow().addWindow(mainModalWindow);

            mainModalWindow.addDialogButton("Peruuta", new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    getWindow().removeWindow(mainModalWindow);
                    mainModalWindow.removeDialogButtons();
                    mainModalWindow = null;
                }
            });

            mainModalWindow.addDialogButton("Jatka", new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    LOG.info("buttonClick() - Siirra uudelle kaudelle click...");

                    EditKoulutusView f = new EditKoulutusView();
                    getWindow().removeAllComponents();
                    getWindow().addComponent(f);
                    getWindow().removeWindow(mainModalWindow);
                    mainModalWindow.removeDialogButtons();
                    mainModalWindow = null;
                }
            });

            mainModalWindow.buildDialogButtons();
        }
    };

    public TarjontaWindow() {
        super();
        LOG.info("TarjontaWindow(): {}", _presenter);

        VerticalLayout layout = UiBuilder.newVerticalLayout();
        layout.setSizeFull();
        setContent(layout); //window käyttää layouttia pohjana
        layout.addStyleName(Oph.CONTAINER_MAIN);

        mainSearch = new MainSearchView();
        mainResult = new MainResultView();

        mainResult.setBtnListenerLuoUusiKoulutus(clLuoUusiKoulutusClickListener);
        mainResult.setBtnListenerMuokkaa(clSiirraUudelleKaudelleView);
        mainResult.setCategoryDataSource(DataSource.treeTableData(new MultiActionTableStyle()));

        main = new MainSplitPanelView();
        main.getMainRightLayout().addComponent(mainSearch);
        main.getMainRightLayout().addComponent(mainResult);
        main.getMainRightLayout().setExpandRatio(mainSearch, 0.03f);
        main.getMainRightLayout().setExpandRatio(mainResult, 0.97f);

        if (_presenter != null && _presenter.showIdentifier()) {
            main.getMainRightLayout().addComponent(new Label("ID=" + _presenter.getIdentifier()));
        }

        layout.addComponent(main);
        layout.setExpandRatio(main, 1f);


    }
}
