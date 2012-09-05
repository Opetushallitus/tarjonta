package fi.vm.sade.tarjonta.ui.model.view;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import fi.vm.sade.tarjonta.ui.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.poc.helper.UI;
import fi.vm.sade.vaadin.Oph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

@Configurable(preConstruction=true)
public class MainSplitPanelView extends HorizontalSplitPanel {

    private static final Logger LOG = LoggerFactory.getLogger(MainSplitPanelView.class);
    
    @Autowired
    private TarjontaPresenter _presenter;
    
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
    private ComboBox cbHaunKohdejoukko;
    private ComboBox cbHakutyyppi;
    private ComboBox cbHakutapa;
    private ComboBox cbKoulutuksenAlkamiskausi;
    private ComboBox cbHakukausi;
    private HorizontalLayout mainLeftLayout;
    private VerticalLayout mainRightLayout;
    private TextField tfSearch;
    private HorizontalLayout rightMiddleResultLayout;
    private Button btnMuokkaa;
    private Button btnPoista;
    private Button btnKopioiUudelleKaudelle;
    private Button btnLuoUusiKoulutus;
    private ComboBox cbJarjestys;
    
    /**
     * The constructor should first build the main layout, set the composition
     * root and then do any custom initialization.
     */
    public MainSplitPanelView() {
        buildMainLayout();
        setHeight(UI.PCT100);
    }

    private void buildMainLayout() {
        breadCrumb = new Link();
        breadCrumb.setCaption("Rantalohjan koulutuskuntayhtymä Rantalohjan ammattiopisto");
        breadCrumb.setImmediate(false);


        mainLeftLayout = UI.newHorizontalLayout(null, null, true);
        UI.newLabel("Organisaation valinta tähän", mainLeftLayout);

        mainRightLayout = UI.newVerticalLayout(null, null, true);

        HorizontalLayout breadCrumbLayout = UI.newHorizontalLayout(null, null, new Boolean[]{false, false, true, false});
        breadCrumbLayout.addComponent(breadCrumb);
        getMainRightLayout().addComponent(breadCrumbLayout);
        getMainRightLayout().addComponent(buildTopSearchLayout());
        getMainRightLayout().addComponent(buildBottomResultLayout());
        
        if (_presenter.showIdentifier()) {
            getMainRightLayout().addComponent(new Label("ID=" + _presenter.getIdentifier()));
        }

        // Add a vertical SplitPanel to the lower area

        setSplitPosition(1); // percent

        this.addComponent(mainLeftLayout);
        this.addComponent(getMainRightLayout()); 
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

    private HorizontalLayout buildMiddleResultLayout() {

        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidth("100%");
        layout.setHeight(-1, UNITS_PIXELS);
        layout.setMargin(true);

        // btnPoista.setEnabled(false);
        btnKopioiUudelleKaudelle = UI.newButton("Kopio uudelle kaudelle", layout);
        btnKopioiUudelleKaudelle.addStyleName(Oph.BUTTON_SMALL);
        btnLuoUusiKoulutus = UI.newButton("Luo uusi koulutus", layout);
        btnLuoUusiKoulutus.addStyleName(Oph.BUTTON_SMALL);
        btnPoista = UI.newButton("Poista", layout);
        btnPoista.addStyleName(Oph.BUTTON_SMALL);

        cbJarjestys = UI.newCompobox(null, new String[]{"Organisaatiorakenteen mukainen järjestys"}, layout);

        layout.setExpandRatio(cbJarjestys, 1f);
        layout.setComponentAlignment(cbJarjestys, Alignment.TOP_RIGHT);

        return layout;
    }

    private HorizontalLayout buildTopSearchLayout() {
        // common part: create layout
        rightTopSearchLayout = new HorizontalLayout();
        rightTopSearchLayout.setSpacing(true);
        rightTopSearchLayout.setWidth("100%");
        rightTopSearchLayout.setHeight(-1, UNITS_PIXELS); //Tämä toimii!!!

        tfSearch = new TextField("");
        tfSearch.addStyleName("search-box"); //TODO!!!!!!!!!!!!!!!!!!!!
        tfSearch.setImmediate(false);
        rightTopSearchLayout.addComponent(tfSearch);
        Button btnHaku = UI.newButton("Search", rightTopSearchLayout);
        btnHaku.addStyleName(Oph.BUTTON_SMALL);

        //TODO Koulutuksen alkamiskausi oma style viilaus!!!!
        cbKoulutuksenAlkamiskausi = UI.newCompobox("Koulutuksen alkamiskausi", new String[]{"Syksy 2012"}, rightTopSearchLayout);
        cbHakukausi = UI.newCompobox("Hakukausi", new String[]{"Kevätkausi"}, rightTopSearchLayout);
        cbHakutapa = UI.newCompobox("Hakutapa", new String[]{"Kaikki"}, rightTopSearchLayout);
        cbHakutyyppi = UI.newCompobox("Hakutyyppi", new String[]{"Kaikki"}, rightTopSearchLayout);
        cbHaunKohdejoukko = UI.newCompobox("Kohdejoukko", new String[]{"Kaikki"}, rightTopSearchLayout);
        btnTyhjenna = UI.newButton("Tyhjennä", rightTopSearchLayout);
        btnTyhjenna.addStyleName(Oph.BUTTON_SMALL);

        rightTopSearchLayout.setComponentAlignment(btnHaku, Alignment.BOTTOM_LEFT);
        rightTopSearchLayout.setComponentAlignment(cbKoulutuksenAlkamiskausi, Alignment.TOP_RIGHT);


        rightTopSearchLayout.setComponentAlignment(btnTyhjenna, Alignment.BOTTOM_RIGHT);
        rightTopSearchLayout.setExpandRatio(cbKoulutuksenAlkamiskausi, 1f); //default == 0

        return rightTopSearchLayout;
    }

    private Panel buildSearchResultPanel() {
        // common part: create layout
        searchVerticalResultLayout = UI.newVerticalLayout(null, null);
        searchVerticalResultLayout.addComponent(buildMiddleResultLayout());

        CssLayout wrapper = new CssLayout();
        wrapper.addComponent(new CheckBox("Valitse kaikki"));
        wrapper.setMargin(false, false, true, true);
 
        searchVerticalResultLayout.addComponent(wrapper);

        searchVerticalResultLayout.setSpacing(true); //komponentien väliin ilmaa

        searchHorizontalResultLayout = UI.newHorizontalLayout(null, null);
        categoryTree = new CategoryTreeView();
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

    /**
     * @param btnLuoUusiKoulutus the btnLuoUusiKoulutus to set
     */
    public void setBtnLuoUusiKoulutus(Button.ClickListener btnLuoUusiKoulutus) {
        this.btnLuoUusiKoulutus.addListener(btnLuoUusiKoulutus);
    }

    /**
     * @return the mainRightLayout
     */
    public VerticalLayout getMainRightLayout() {
        return mainRightLayout;
    }
}
