package fi.vm.sade.tarjonta.ui.view.hakukohde;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.generic.common.I18NHelper;
import fi.vm.sade.tarjonta.service.types.HaeHakukohteetVastausTyyppi.HakukohdeTulos;
import fi.vm.sade.tarjonta.service.types.HaeKoulutuksetVastausTyyppi.KoulutusTulos;
import fi.vm.sade.tarjonta.ui.helper.TarjontaUIHelper;
import fi.vm.sade.tarjonta.ui.presenter.TarjontaPresenter;
import fi.vm.sade.tarjonta.ui.view.common.CategoryTreeView;
import fi.vm.sade.tarjonta.ui.view.common.ShowRelatedObjectsDialog;

public class ShowKoulutuksetDialog extends ShowRelatedObjectsDialog {

    private static final long serialVersionUID = 6521526287528256527L;
    
    private CategoryTreeView koulutusTree;
    
    private List<KoulutusTulos> koulutukset;
    private HakukohdeTulos selectedHakukohde;
    private transient I18NHelper i18n = new I18NHelper(this);
    
    public ShowKoulutuksetDialog(List<KoulutusTulos> koulutukset, HakukohdeTulos selectedHakukohde, TarjontaPresenter presenter) {
        super(presenter);
        this.koulutukset = koulutukset;
        this.selectedHakukohde = selectedHakukohde;
        buildLayout();
    }
    
    private void buildLayout() {
        buildLayout(T("otsikko"), resolveHakukohdeNimi());
        populateTree();
    }
    
    protected void populateTree() {
       Set<Map.Entry<String, List<KoulutusTulos>>> set = createDataMap();
       HierarchicalContainer hc = new HierarchicalContainer();
       hc.addContainerProperty(COLUMN_A, CaptionItem.class, new CaptionItem());
       hc.addContainerProperty(COLUMN_PVM, String.class, "");
       hc.addContainerProperty(COLUMN_TILA, String.class, "");
       
       for (Map.Entry<String, List<KoulutusTulos>>e : set) {
           
           Object rootItem = hc.addItem();
           
           hc.getContainerProperty(rootItem, COLUMN_A).setValue(new CaptionItem(e.getKey(), false));
           System.out.println("Added tarjoaja: " + e.getKey());
           for (final KoulutusTulos curKoulutus : e.getValue()) {
               
               hc.addItem(curKoulutus);
               hc.setParent(curKoulutus, rootItem);
               CaptionItem ci = new CaptionItem(
                       TarjontaUIHelper.getClosestMonikielinenTekstiTyyppiName(I18N.getLocale(), curKoulutus.getKoulutus().getNimi()).getValue(), 
                       true);
               ci.getLinkButton().addListener( new Button.ClickListener() {

                   private static final long serialVersionUID = -4104837426848884996L;

                   @Override
                       public void buttonClick(ClickEvent event) {
                           showSummaryView(curKoulutus);
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
    
    private void showSummaryView(KoulutusTulos koulutus) {
        final String komotoOid = koulutus.getKoulutus().getKoulutusmoduuliToteutus();

        switch (koulutus.getKoulutus().getKoulutustyyppi()) {
            case AMMATILLINEN_PERUSKOULUTUS:
                presenter.closeHakukohdeRemovalDialog();
                presenter.showShowKoulutusView(komotoOid);
                break;
            case LUKIOKOULUTUS:
                presenter.closeHakukohdeRemovalDialog();
                presenter.getLukioPresenter().showSummaryKoulutusView(komotoOid);
                break;
        }
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

}
