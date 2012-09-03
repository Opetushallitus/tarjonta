package fi.vm.sade.tarjonta.ui.poc;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Link;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import fi.oph.Oph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends VerticalLayout {
    
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    
    private HorizontalLayout rightBottomResultLayout;
    private Link breadCrumb;
    private TabSheet searchResultTab;
    private Panel emptyPanel2;
    private Panel emptyPanel1;
    private VerticalLayout koulutukset;
    private Panel searchResultPanel;
    private VerticalLayout searchVerticalResultLayout;
    private HorizontalLayout searchHorizontalResultLayout;
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
    private HorizontalLayout rightMiddleResultLayout;
    private Button btnMuokkaa;
    private Button btnPoista;
    private Button btnLuoUusiHakukohde;
    private Button btnLuoUusiKoulutus;
    private NativeSelect nsJarjestys;

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
        breadCrumb = new Link();
        breadCrumb.setCaption("Rantalohjan koulutuskuntayhtymä Rantalohjan ammattiopisto");
        breadCrumb.setImmediate(false);
        breadCrumb.setWidth("-1px");
        breadCrumb.setHeight("-1px");

        mainLeftLayout = UI.newHorizontalLayout(null, null, true);
        UI.newLabel("Organisaation valinta tähän", mainLeftLayout);
        
        mainRightLayout = UI.newVerticalLayout(null, null, true);
        
        HorizontalLayout breadCrumbLayout = UI.newHorizontalLayout(null, null, new Boolean[]{false, false, true, false});
        breadCrumbLayout.addComponent(breadCrumb);
        mainRightLayout.addComponent(breadCrumbLayout);
        mainRightLayout.addComponent(buildTopSearchLayout());
        mainRightLayout.addComponent(buildBottomResultLayout());

        // Add a vertical SplitPanel to the lower area
        splitPanel = new HorizontalSplitPanel();
        splitPanel.setSplitPosition(1); // percent

        splitPanel.addComponent(mainLeftLayout);
        splitPanel.addComponent(mainRightLayout);
        this.addComponent(splitPanel);
    }

    private HorizontalLayout buildBottomResultLayout() {
        // right component:
        rightBottomResultLayout = UI.newHorizontalLayout(null, null, new Boolean[]{true, false, true, false});

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

    private HorizontalLayout buildMiddleResultLayout() {
        rightMiddleResultLayout = UI.newHorizontalLayout(null, null, new Boolean[]{true, false, true, false});

        HorizontalLayout leftSide = UI.newHorizontalLayout("325px", null, new Boolean[]{false, true, false, true});
       
        final GridLayout grid = new GridLayout(2, 1);
        grid.setWidth(UI.PCT100);

        btnMuokkaa = UI.newButton("Muokkaa", leftSide);
        btnMuokkaa.setStyleName(Oph.BUTTON_PRIMARY);
        
        btnPoista = UI.newButton("Poista", leftSide);
        btnPoista.setStyleName(Oph.BUTTON_DEFAULT);

        btnLuoUusiHakukohde = UI.newButton("Luo uusi hakukohde", leftSide);
        btnLuoUusiHakukohde.addStyleName(Oph.BUTTON_PLUS);

        grid.addComponent(leftSide, 0, 0);
        grid.setComponentAlignment(leftSide, Alignment.MIDDLE_LEFT);
        
        HorizontalLayout rightSide = UI.newHorizontalLayout("450px", null, new Boolean[]{false, true, false, false});
        btnLuoUusiKoulutus = UI.newButton("luo uusi koulutus", rightSide);
        btnLuoUusiKoulutus.addListener(new Button.ClickListener() {
            
            @Override
            public void buttonClick(ClickEvent event) {
                LOG.info("buttonClick() - luo uusi koulutus click...");
                EditKoulutus f = new EditKoulutus();
                mainRightLayout.removeAllComponents();
                mainRightLayout.addComponent(f);
            }
        });
        
        
        nsJarjestys = UI.newCompobox(null, new String[]{"Organisaatiorakenteen mukainen järjestys"}, rightSide);
        grid.addComponent(rightSide, 1, 0);
        grid.setComponentAlignment(rightSide, Alignment.MIDDLE_CENTER);
        rightMiddleResultLayout.addComponent(grid);

        return rightMiddleResultLayout;
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
        searchVerticalResultLayout = UI.newVerticalLayout(null, null);
        searchVerticalResultLayout.addComponent(buildMiddleResultLayout());

        searchHorizontalResultLayout = UI.newHorizontalLayout(null, null);
        categoryTree = new CategoryTree();
        searchHorizontalResultLayout.addComponent(categoryTree);

        searchVerticalResultLayout.addComponent(searchHorizontalResultLayout);

        searchResultPanel = UI.newPanel(null, null, searchVerticalResultLayout);


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
