package fi.vm.sade.tarjonta.ui.view.hakukohde;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.data.Container;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetVastausTyyppi.HakukohdeTulos;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetVastausTyyppi.KoulutusTulos;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.CategoryTreeView;
import fi.vm.sade.vaadin.util.UiUtil;

public class ShowKoulutuksetDialog extends VerticalLayout {

    private static final long serialVersionUID = 6521526287528256527L;
    
    public static final String[] ORDER_BY = new String[]{I18N.getMessage("ListHakukohdeViewImpl.jarjestys.Organisaatio")};
    public static final String COLUMN_A = "Kategoriat";
    public static final String COLUMN_PVM = "Ajankohta";
    public static final String COLUMN_TILA = "Tila";
    
    private CategoryTreeView koulutusTree;
    
    private List<KoulutusTulos> koulutukset;
    private HakukohdeTulos selectedHakukohde;
    private TarjontaPresenter presenter;
    private transient I18NHelper i18n = new I18NHelper(this);
    
    public ShowKoulutuksetDialog(List<KoulutusTulos> koulutukset, HakukohdeTulos selectedHakukohde, TarjontaPresenter presenter) {
        setSizeUndefined();
        setWidth("700px");
        this.koulutukset = koulutukset;
        this.selectedHakukohde = selectedHakukohde;
        this.presenter = presenter;
        buildLayout();
    }
    
    private void buildLayout() {
        Label dialogLabel = new Label();
        dialogLabel.setValue(T("otsikko"));
        addComponent(dialogLabel);
        Label hakukohdeNimi = new Label();
        hakukohdeNimi.setValue(resolveHakukohdeNimi());
        addComponent(hakukohdeNimi);
        
        buildKoulutusTree();
        
    }
    
    private void buildKoulutusTree() {
        koulutusTree = new CategoryTreeView();
        addComponent(koulutusTree);
        setExpandRatio(koulutusTree, 1f);
        
        koulutusTree.addContainerProperty(COLUMN_A, CaptionItem.class, new CaptionItem());
        koulutusTree.addContainerProperty(COLUMN_PVM, String.class, "");
        koulutusTree.addContainerProperty(COLUMN_TILA, String.class, "");
        
        koulutusTree.setColumnExpandRatio(COLUMN_A, 1f);
        koulutusTree.setColumnExpandRatio(COLUMN_PVM, 0.3f);
        koulutusTree.setColumnExpandRatio(COLUMN_TILA, 0.3f);
        
       populateTree();
    }
    
    private void populateTree() {
       Set<Map.Entry<String, List<KoulutusTulos>>> set = createDataMap();
       HierarchicalContainer hc = new HierarchicalContainer();
       hc.addContainerProperty(COLUMN_A, CaptionItem.class, new CaptionItem());
       hc.addContainerProperty(COLUMN_PVM, String.class, "");
       hc.addContainerProperty(COLUMN_TILA, String.class, "");
       
       for (Map.Entry<String, List<KoulutusTulos>>e : set) {
           
           Object rootItem = hc.addItem();
           
           hc.getContainerProperty(rootItem, COLUMN_A).setValue(new CaptionItem(e.getKey(), false, null));
           System.out.println("Added tarjoaja: " + e.getKey());
           for (KoulutusTulos curKoulutus : e.getValue()) {
               
               hc.addItem(curKoulutus);
               hc.setParent(curKoulutus, rootItem);
               CaptionItem ci = new CaptionItem(
                       TarjontaUIHelper.getClosestMonikielinenTekstiTyyppiName(I18N.getLocale(), curKoulutus.getKoulutus().getNimi()).getValue(), 
                       true, 
                       curKoulutus);
               ci.getLinkButton().addListener( new Button.ClickListener() {

                   private static final long serialVersionUID = -4104837426848884996L;

                   @Override
                       public void buttonClick(ClickEvent event) {
                           showSummaryView();
                       }
                   });
               hc.getContainerProperty(curKoulutus, COLUMN_A).setValue(
                       ci);
               hc.getContainerProperty(curKoulutus, COLUMN_PVM).setValue(getKoulutusAjankohtaStr(curKoulutus));
               hc.getContainerProperty(curKoulutus, COLUMN_TILA).setValue(T(curKoulutus.getKoulutus().getTila().value()));
               hc.setChildrenAllowed(curKoulutus, false);
               System.out.println("Added koulutus: " + curKoulutus.getKoulutus().getKomotoOid());
           }
       }
       
       koulutusTree.setContainerDataSource(hc);
       
       for (KoulutusTulos curTulos : koulutukset) {
           koulutusTree.setCollapsed(koulutusTree.getParent(curTulos), false);
       }
    }
    
    private void showSummaryView() {
        getParent().getWindow().removeWindow(getWindow());
        presenter.showHakukohdeViewImpl(selectedHakukohde.getHakukohde().getOid());
    }
    
    private Set<Map.Entry<String, List<KoulutusTulos>>> createDataMap() {
        Map<String, List<KoulutusTulos>> dataMap = new HashMap<String, List<KoulutusTulos>>();
        for (KoulutusTulos curKoulutus : this.koulutukset) {
            String koulutusKey = TarjontaUIHelper.getClosestMonikielinenTekstiTyyppiName(I18N.getLocale(),curKoulutus.getKoulutus().getTarjoaja().getNimi()).getValue();
            if (!dataMap.containsKey(koulutusKey)) {
                List<KoulutusTulos> koulutuksetM = new ArrayList<KoulutusTulos>();
                koulutuksetM.add(curKoulutus);
                dataMap.put(koulutusKey, koulutuksetM);
            } else {
                dataMap.get(koulutusKey).add(curKoulutus);
            }
        }
        return dataMap.entrySet();
    }
    
    private String resolveHakukohdeNimi() {
        return TarjontaUIHelper.getClosestMonikielinenTekstiTyyppiName(I18N.getLocale(), selectedHakukohde.getHakukohde().getNimi()).getValue() 
                + ", " + getHakukohdeAjankohtaStr() 
                + ", " + TarjontaUIHelper.getClosestMonikielinenTekstiTyyppiName(I18N.getLocale(), selectedHakukohde.getHakukohde().getTarjoaja().getNimi()).getValue();        
    }
    
    private String getKoulutusAjankohtaStr(KoulutusTulos curKoulutus) {
        String[] ajankohtaParts = curKoulutus.getKoulutus().getAjankohta().split(" ");
        if (ajankohtaParts.length < 2) {
            return "";
        }
        return I18N.getMessage(ajankohtaParts[0]) + " " + ajankohtaParts[1];
    }
    
    
    private String getHakukohdeAjankohtaStr() {
        return I18N.getMessage(selectedHakukohde.getHakukohde().getKoulutuksenAlkamiskausiUri()) 
                +  " " + selectedHakukohde.getHakukohde().getKoulutuksenAlkamisvuosi();
    }
    
    private String T(String key, Object... args) {
        return i18n.getMessage(key, args);
    }
    
    public class CaptionItem extends HorizontalLayout {

        private static final long serialVersionUID = 312997606755010205L;
        
        private String caption;
        private KoulutusTulos koulutus;
        private Button linkButton;
        
        public CaptionItem() {
            
        }
       
        public CaptionItem(String caption, boolean withLink, KoulutusTulos koulutus) {
            this.caption = caption;
            this.koulutus = koulutus;
            buildLayout(withLink);
        }
        
        public Button getLinkButton() {
            return linkButton;
        }
        
        private void buildLayout(boolean withLink) {
            if (withLink) {
                linkButton = UiUtil.buttonLink(null, caption);
                linkButton.setStyleName("link-row");
                linkButton.setSizeUndefined();
                linkButton.setHeight(7, Sizeable.UNITS_PIXELS);
                addComponent(linkButton);
            } else {
                Label orgLabel = new Label();
                orgLabel.setValue(caption);
                addComponent(orgLabel);
            }
        }
        
    }   

}
