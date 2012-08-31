package fi.vm.sade.tarjonta.ui.poc;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;

public class Main extends VerticalLayout {

    private HorizontalLayout rightBottomResultLayout;
    private Link link_1;
    private TabSheet searchResultTab;
    private Panel emptyPanel2;
    private Panel emptyPanel1;
    private VerticalLayout koulutukset;
    private Panel searchResultPanel;
    private HorizontalLayout searchResultLayout;
    private TreeTable categoryTree;
    private HorizontalLayout rightTopSearchLayout;
    private Button btnTyhjenna;
    private NativeSelect cbHaunKohdejoukko;
    private NativeSelect cbHakutyyppi;
    private NativeSelect cbHakutapa;
    private NativeSelect cbKoulutuksenAlkamiskausi;
    private NativeSelect cbHakukausi;
    private HorizontalLayout mainLeftLayout;
    private VerticalLayout mainRightLayout;
    private HorizontalSplitPanel splitPanel;
    private TextField tfSearch;
    
    private Button btnMuokkaa;
    private Button btnPoista;
    private Button btnLuoUusiHakukohde;
    private Button btnLuoUusiKoulutus;
    

    /**
     * The constructor should first build the main layout, set the composition
     * root and then do any custom initialization.
     */
    public Main() {

        buildMainLayout();
        // top-level component properties
        setWidth(UI.PCT100);
        setHeight("500px");
    }

    private void buildMainLayout() {
        link_1 = new Link();
        link_1.setCaption("Link4444");
        link_1.setImmediate(false);
        link_1.setWidth("-1px");
        link_1.setHeight("-1px");

        mainLeftLayout = UI.newHorizontalLayout(null, null);
        mainRightLayout = UI.newVerticalLayout(null, null);
        mainRightLayout.addComponent(buildTopSearchLayout());
        mainRightLayout.addComponent(buildBottomResultLayout());

        // Add a vertical SplitPanel to the lower area
        splitPanel = new HorizontalSplitPanel();
        splitPanel.setSplitPosition(25); // percent

        splitPanel.addComponent(mainLeftLayout);
        splitPanel.addComponent(mainRightLayout);
        this.addComponent(splitPanel);
    }

    private HorizontalLayout buildBottomResultLayout() {
        // right component:
        rightBottomResultLayout = UI.newHorizontalLayout(null, null);

        searchResultTab = new TabSheet();
        searchResultTab.setImmediate(true);
        searchResultTab.setWidth(UI.PCT100);
        searchResultTab.setHeight(UI.PCT100);

        searchResultPanel = buildSearchResultPanel();
        searchResultPanel.setHeight(UI.PCT100);
        
        searchResultTab.addTab(searchResultPanel, "Haut (2 kpl)", null);
        searchResultTab.setWidth(UI.PCT100);
        searchResultTab.setHeight(UI.PCT100);
        
        emptyPanel1 = buildEmptyTabPanel();
        searchResultTab.addTab(emptyPanel1, "Koulutukset (28 kpl)", null);

        emptyPanel2 = buildEmptyTabPanel();
        searchResultTab.addTab(emptyPanel2, "Hakukohteet (35 kpl)", null);

        rightBottomResultLayout.addComponent(searchResultTab);

        return rightBottomResultLayout;

    }

    private HorizontalLayout buildTopSearchLayout() {
        // common part: create layout
        rightTopSearchLayout = UI.newHorizontalLayout(null, null);

        tfSearch = new TextField("");
        tfSearch.setImmediate(false);
        tfSearch.setWidth("-1px");
        tfSearch.setHeight("-1px");
        rightTopSearchLayout.addComponent(tfSearch);

        cbHakukausi = UI.newCompobox("Hakukausi", new String[]{"Kevätkausi"}, rightTopSearchLayout);
        cbKoulutuksenAlkamiskausi = UI.newCompobox("Koulutuksen alkamiskausi", new String[]{"Syksy 2012"}, rightTopSearchLayout);
        cbHakutapa = UI.newCompobox("Hakutapa", new String[]{"Kaikki"}, rightTopSearchLayout);
        cbHakutyyppi = UI.newCompobox("Hakutyyppi", new String[]{"Kaikki"}, rightTopSearchLayout);
        cbHaunKohdejoukko = UI.newCompobox("Kohdejoukko", new String[]{"Kaikki"}, rightTopSearchLayout);
        btnTyhjenna = UI.newButton("Tyhjennä", rightTopSearchLayout);

        return rightTopSearchLayout;
    }

    private Panel buildSearchResultPanel() {
        // common part: create layout
        searchResultLayout = UI.newHorizontalLayout(null, null);

        categoryTree = new CategoryTree();
        searchResultLayout.addComponent(categoryTree);
        
        searchResultPanel = UI.newPanel(null, null, searchResultLayout);

        return searchResultPanel;
    }

    private Panel buildEmptyTabPanel() {
        // common part: create layout
        emptyPanel1 = UI.newPanel(null, null, null);

        // koulutukset
        koulutukset = UI.newVerticalLayout(null, null);
        emptyPanel1.setContent(koulutukset);

        return emptyPanel1;
    }
}
