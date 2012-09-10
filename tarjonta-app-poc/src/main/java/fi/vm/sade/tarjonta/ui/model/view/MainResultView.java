package fi.vm.sade.tarjonta.ui.model.view;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import fi.vm.sade.vaadin.oph.enums.UiMarginEnum;
import fi.vm.sade.vaadin.oph.layout.AbstractHorizontalLayout;
import fi.vm.sade.vaadin.oph.helper.UiBuilder;
import fi.vm.sade.vaadin.Oph;

/**
 *
 * @author jani
 */
public class MainResultView extends AbstractHorizontalLayout {

    private TabSheet searchResultTab;
    private Panel searchResultPanel;
    private Panel emptyPanel2;
    private Panel emptyPanel1;
    private VerticalLayout searchVerticalResultLayout;
    private HorizontalLayout searchHorizontalResultLayout;
    private CategoryTreeView categoryTree;
    private Button btnKopioiUudelleKaudelle;
    private Button btnPoista;
    private Button btnLuoUusiKoulutus;
    private ComboBox cbJarjestys;

    public MainResultView() {
        super(true, UiMarginEnum.BOTTOM_LEFT);

        buildLayout();
    }

    private void buildLayout() {
        searchResultTab = new TabSheet();
        searchResultTab.setImmediate(true);
        searchResultTab.setWidth(UiBuilder.PCT100);
        searchResultTab.setHeight(UiBuilder.PCT100);

        searchResultPanel = buildSearchResultPanel();
        searchResultPanel.setHeight(UiBuilder.PCT100);

        searchResultTab.addTab(searchResultPanel, "Haut (2 kpl)", null);
        searchResultTab.setWidth(UiBuilder.PCT100);
        searchResultTab.setHeight(UiBuilder.PCT100);
        

        emptyPanel1 = buildEmptyTabPanel();
        searchResultTab.addTab(emptyPanel1, "Koulutukset (28 kpl)", null);
        emptyPanel1.setHeight("600px");
        //emptyPanel1.setSizeFull();

        emptyPanel2 = buildEmptyTabPanel();
        searchResultTab.addTab(emptyPanel2, "Hakukohteet (35 kpl)", null);

        //SET SELECTED TAB!
        searchResultTab.setSelectedTab(emptyPanel1);
        
        this.addComponent(searchResultTab);
    }

    private HorizontalLayout buildMiddleResultLayout() {
        HorizontalLayout layout = UiBuilder.newHorizontalLayout(true, UiMarginEnum.BOTTOM);

        btnKopioiUudelleKaudelle = UiBuilder.newButton(i18n.getMessage("KopioUudelleKaudelle"), layout);
        btnKopioiUudelleKaudelle.addStyleName(Oph.BUTTON_SMALL);

        btnLuoUusiKoulutus = UiBuilder.newButton(i18n.getMessage("LuoUusiKoulutus"), layout);
        btnLuoUusiKoulutus.addStyleName(Oph.BUTTON_SMALL);

        btnPoista = UiBuilder.newButton(i18n.getMessage("Poista"), layout);
        btnPoista.addStyleName(Oph.BUTTON_SMALL);
        btnPoista.setEnabled(false);

        cbJarjestys = UiBuilder.newComboBox(null, new String[]{"Organisaatiorakenteen mukainen järjestys"}, layout);

        layout.setExpandRatio(cbJarjestys, 1f);
        layout.setComponentAlignment(cbJarjestys, Alignment.TOP_RIGHT);

        return layout;
    }

    private Panel buildSearchResultPanel() {
        // common part: create layout
        Panel newPanel = UiBuilder.newPanel();
        newPanel.setWidth(UiBuilder.PCT100);
        newPanel.addComponent(buildMiddleResultLayout());

        CssLayout wrapper = UiBuilder.newCssLayout(UiMarginEnum.BOTTOM);
        wrapper.addComponent(new CheckBox(i18n.getMessage("ValitseKaikki")));
        newPanel.addComponent(wrapper);

        newPanel.setScrollable(true);

        categoryTree = new CategoryTreeView();
        newPanel.addComponent(categoryTree);
        newPanel.setHeight(Sizeable.SIZE_UNDEFINED, 0);

        return newPanel;
    }

    private Panel buildEmptyTabPanel() {
        // common part: create layout
        Panel panel = UiBuilder.newPanel();
        panel.setSizeFull();
        panel.setContent(UiBuilder.newVerticalLayout());

        return panel;
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
