package fi.vm.sade.tarjonta.ui.model.view;

import com.vaadin.data.Container;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
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
    private VerticalLayout searchResul;
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
        setSizeFull();
        buildLayout();
    }

    private void buildLayout() {
        searchResultTab = new TabSheet();
        searchResultTab.setImmediate(true);
        searchResultTab.setSizeFull();

        searchResul = buildSearchResult();
        searchResul.setHeight(UiBuilder.PCT100);

        searchResultTab.addTab(searchResul, "Haut (2 kpl)", null);
        searchResultTab.setWidth(UiBuilder.PCT100);

        VerticalLayout newVerticalLayout = UiBuilder.newVerticalLayout();
        emptyPanel1 = buildEmptyTabPanel();
        searchResultTab.addTab(newVerticalLayout, "Koulutukset (28 kpl)", null);

        Label label = new Label("LABEL");
        label.setSizeFull();
        newVerticalLayout.addComponent(label);
        newVerticalLayout.setComponentAlignment(label, Alignment.BOTTOM_RIGHT);


        emptyPanel2 = buildEmptyTabPanel();
        searchResultTab.addTab(emptyPanel2, "Hakukohteet (35 kpl)", null);

        //SET SELECTED TAB!
        searchResultTab.setSelectedTab(emptyPanel1);
        this.addComponent(searchResultTab);
        setComponentAlignment(searchResultTab, Alignment.TOP_LEFT);


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

    private VerticalLayout buildSearchResult() {
        // common part: create layout
        VerticalLayout layout = UiBuilder.newVerticalLayout();
        layout.setWidth(UiBuilder.PCT100);
        HorizontalLayout buildMiddleResultLayout = buildMiddleResultLayout();
        layout.addComponent(buildMiddleResultLayout);

        CssLayout wrapper = UiBuilder.newCssLayout(UiMarginEnum.BOTTOM);
        wrapper.addComponent(new CheckBox(i18n.getMessage("ValitseKaikki")));
        layout.addComponent(wrapper);

        categoryTree = new CategoryTreeView();
        layout.addComponent(categoryTree);
        layout.setHeight(Sizeable.SIZE_UNDEFINED, 0);

        layout.setExpandRatio(wrapper, 0.07f);
        layout.setExpandRatio(categoryTree, 0.80f);
        layout.setMargin(true);
        
        
        
        return layout;
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
