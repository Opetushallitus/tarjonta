package fi.vm.sade.tarjonta.ui.model.view;

import com.vaadin.data.Container;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.tarjonta.ui.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.poc.helper.I18NHelper;
import fi.vm.sade.vaadin.Oph;
import fi.vm.sade.vaadin.oph.enums.UiMarginEnum;
import fi.vm.sade.vaadin.oph.helper.UiBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author jani
 */
@Configurable(preConstruction = true)
public class MainTabKoulutusView extends VerticalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(MainTabKoulutusView.class);
    private Button btnKopioiUudelleKaudelle;
    private Button btnPoista;
    private Button btnLuoUusiKoulutus;
    private ComboBox cbJarjestys;
    private CategoryTreeView categoryTree;
    private I18NHelper i18n = new I18NHelper(this);
    @Autowired(required = true)
    private TarjontaPresenter _presenter;

    public MainTabKoulutusView() {
        setWidth(UiBuilder.PCT100);
        HorizontalLayout buildMiddleResultLayout = buildMiddleResultLayout();
        addComponent(buildMiddleResultLayout);

        CssLayout wrapper = UiBuilder.newCssLayout(UiMarginEnum.BOTTOM);
        wrapper.addComponent(new CheckBox(i18n.getMessage("ValitseKaikki")));
        addComponent(wrapper);

        categoryTree = new CategoryTreeView();
        addComponent(categoryTree);
        setHeight(Sizeable.SIZE_UNDEFINED, 0);

        setExpandRatio(wrapper, 0.07f);
        setExpandRatio(categoryTree, 0.93f);
        setMargin(true);

        categoryTree.setContainerDataSource(_presenter.getTreeDataSource());
    }

    private HorizontalLayout buildMiddleResultLayout() {
        HorizontalLayout layout = UiBuilder.newHorizontalLayout(true, UiMarginEnum.BOTTOM);

        btnKopioiUudelleKaudelle = UiBuilder.newButtonSmallPrimary(i18n.getMessage("KopioUudelleKaudelle"), layout);
        btnKopioiUudelleKaudelle.addStyleName(Oph.BUTTON_SMALL);

        btnKopioiUudelleKaudelle.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {

                final EditSiirraUudelleKaudelleView modal = new EditSiirraUudelleKaudelleView(i18n.getMessage("KopioUudelleKaudelle"));
                getWindow().addWindow(modal);

                modal.addNavigationButton("Peruuta", new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        // Stay in same view
                        getWindow().removeWindow(modal);
                        modal.removeDialogButtons();
                    }
                }, UiBuilder.STYLE_BUTTON_SECONDARY);

                modal.addNavigationButton("Jatka", new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        getWindow().removeWindow(modal);
                        modal.removeDialogButtons();

                        _presenter.showMainKoulutusView();
                    }
                }, UiBuilder.STYLE_BUTTON_PRIMARY);

                modal.buildDialogButtons();
            }
        });


        btnLuoUusiKoulutus = UiBuilder.newButtonSmallPrimary(i18n.getMessage("LuoUusiKoulutus"), layout);
        btnLuoUusiKoulutus.addStyleName(Oph.BUTTON_SMALL);

        btnLuoUusiKoulutus.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {

                final CreateKoulutusView modal = new CreateKoulutusView(i18n.getMessage("LuoUusiKoulutus"));
                getWindow().addWindow(modal);

                modal.addNavigationButton("Peruuta", new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        LOG.debug("buttonClick() - peruuta click...");
                        // Stay in same view
                        getWindow().removeWindow(modal);
                        modal.removeDialogButtons();
                    }
                }, UiBuilder.STYLE_BUTTON_SECONDARY);

                modal.addNavigationButton("Jatka", new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        LOG.debug("buttonClick() - luo uusi koulutus click...");

                        getWindow().removeWindow(modal);
                        modal.removeDialogButtons();
                        LOG.debug("presenter : " + _presenter);
                        _presenter.showEditKolutusView();
                    }
                }, UiBuilder.STYLE_BUTTON_SECONDARY);

                modal.buildDialogButtons();
            }
        });


        btnPoista = UiBuilder.newButton(i18n.getMessage("Poista"), layout);
        btnPoista.addStyleName(Oph.BUTTON_SMALL);
        btnPoista.setEnabled(false);

        cbJarjestys = UiBuilder.newComboBox(null, new String[]{"Organisaatiorakenteen mukainen järjestys"}, layout);
        cbJarjestys.setSizeUndefined();
        
        layout.setExpandRatio(cbJarjestys, 1f);
        layout.setComponentAlignment(cbJarjestys, Alignment.TOP_RIGHT);

        Button btnInfo = new Button();
        btnInfo.addStyleName(Oph.BUTTON_INFO);
        layout.addComponent(btnInfo);

        return layout;
    }

    /**
     * @param btnLuoUusiKoulutus the btnLuoUusiKoulutus to set
     */
    public void setBtnListenerLuoUusiKoulutus(Button.ClickListener btnLuoUusiKoulutus) {
        this.btnLuoUusiKoulutus.addListener(btnLuoUusiKoulutus);
    }

    /**
     * @param btnListenerMuokkaa( the btnLuoUusiKoulutus to set
     */
    public void setBtnListenerMuokkaa(Button.ClickListener btnKopioiUudelleKaudelle) {
        this.btnKopioiUudelleKaudelle.addListener(btnKopioiUudelleKaudelle);
    }

    public void setCategoryDataSource(Container dataSource) {
        categoryTree.setContainerDataSource(dataSource);
    }
}
