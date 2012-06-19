package fi.vm.sade.tarjonta.selenium.story;

import static fi.vm.sade.support.selenium.SeleniumUtils.waitForText;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.widget.KoodistoComponent;
import fi.vm.sade.tarjonta.selenium.TarjontaEmbedComponentTstSupport;
import fi.vm.sade.tarjonta.selenium.pageobject.KoulutusmoduuliToteutusEditViewPageObject;
import fi.vm.sade.tarjonta.selenium.pageobject.KoulutusmoduuliToteutusListPageObject;
import fi.vm.sade.tarjonta.service.KoulutusmoduuliAdminService;
import fi.vm.sade.tarjonta.ui.MainWindow;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.toteutus.KoulutusModuulinToteutusTable;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.toteutus.KoulutusmoduuliToteutusEditView;
import fi.vm.sade.tarjonta.ui.koulutusmoduuli.toteutus.KoulutusmoduuliToteutusListView;

public class OVT_941_KomotonPaivittaminenTest extends TarjontaEmbedComponentTstSupport<MainWindow> {

    private KoulutusmoduuliToteutusListPageObject listPageObject;

    @Autowired
    KoulutusmoduuliAdminService koulutusmoduuliAdminService;

    
    @Override
    public void initPageObjects() {
        super.initPageObjects();    
        listPageObject = new KoulutusmoduuliToteutusListPageObject(driver,getComponentByType(KoulutusmoduuliToteutusListView.class));
        
    }
    
    @Test
    public void testOpenKomotoFromList() {
        assertNotNull(mainWindowPageObject.getComponent());
        waitForText(I18N.getMessage("tarjonta.tabs.koulutusmoduulit"));
        //KoulutusmoduuliEditViewPageObject editor = mainWindowPageObject.selectLuoUusiTutkintoonJohtava();
        mainWindowPageObject.openKomotoTab();
        waitForText("KOMOTOt");
        
        listPageObject.clickItemInTable(listPageObject.getSuunnitteillaOlevat(), 2);
        
        KoulutusModuulinToteutusTable suunnitteilla = listPageObject.getSuunnitteillaOlevat();
        KoulutusmoduuliToteutusEditView edit = suunnitteilla.getKomotoEdit();
        KoodistoComponent suunniteltuKesto = edit.getSuunniteltuKestoTextfield();
        KoodistoComponent koulutusLaji = edit.getKoulutusLajiKoodisto();
        
        assertTrue(suunniteltuKesto.getValue().equals("6 kuukautta"));
        assertTrue(koulutusLaji.getValue().equals("Nuorten koulutus"));
        
    }
    
}
