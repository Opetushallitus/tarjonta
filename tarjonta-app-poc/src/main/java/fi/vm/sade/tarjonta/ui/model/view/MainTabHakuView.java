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
@Configurable(preConstruction=true)
public class MainTabHakuView extends VerticalLayout {
    
    private static final Logger LOG = LoggerFactory.getLogger(MainTabHakuView.class);
    private Button btnLuoUusiHaku;
    private Button btnPoista;
    private ComboBox cbJarjestys;
    private CategoryTreeView categoryTree;
    private I18NHelper i18n = new I18NHelper(this);
    @Autowired(required = true)
    private TarjontaPresenter _presenter;
    
    public MainTabHakuView() {
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
        
        btnPoista = UiBuilder.newButton(i18n.getMessage("Poista"), layout);
        btnPoista.addStyleName(Oph.BUTTON_SMALL);
        btnPoista.setEnabled(false);
        
        btnLuoUusiHaku = UiBuilder.newButtonSmallPrimary(i18n.getMessage("LuoUusiHaku"), layout);
        btnLuoUusiHaku.addStyleName(Oph.BUTTON_SMALL);
        
        btnLuoUusiHaku.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
               _presenter.showAddHakuDokumenttiView();                  
            }
        });
        
        cbJarjestys = UiBuilder.newComboBox(null, new String[]{"Organisaatiorakenteen mukainen järjestys"}, layout);
        cbJarjestys.setSizeUndefined();
        layout.setExpandRatio(btnLuoUusiHaku, 1f);
        layout.setComponentAlignment(btnLuoUusiHaku, Alignment.TOP_RIGHT);
        layout.setComponentAlignment(cbJarjestys, Alignment.TOP_RIGHT);
        
        Button btnInfo = new Button();
        btnInfo.addStyleName(Oph.BUTTON_INFO);
        layout.addComponent(btnInfo);
        
        return layout;
    }

    /**
     * @param btnListenerMuokkaa( the btnLuoUusiKoulutus to set
     */
    public void setBtnListenerMuokkaa(Button.ClickListener btnKopioiUudelleKaudelle) {
        this.btnLuoUusiHaku.addListener(btnKopioiUudelleKaudelle);
    }
}
