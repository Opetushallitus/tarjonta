package fi.vm.sade.tarjonta.selenium.story;

import static fi.vm.sade.support.selenium.SeleniumUtils.waitForText;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.selenium.TarjontaEmbedComponentTstSupport;
import fi.vm.sade.tarjonta.selenium.pageobject.KoulutusmoduuliEditViewPageObject;
import fi.vm.sade.tarjonta.ui.MainWindow;

public class OVT_641_LuoMuokkaaHakuTest extends TarjontaEmbedComponentTstSupport<MainWindow> {
    
    @Test
    public void luoMuokkaaHakuFormOpensTest() {
        assertNotNull(mainWindowPageObject.getComponent());
        assertNotNull(mainWindowPageObject.getComponent().getHakuView());

        waitForText(I18N.getMessage("tarjonta.tabs.koulutusmoduulit"));
        //KoulutusmoduuliEditViewPageObject editor = mainWindowPageObject.selectLuoUusiTutkintoonJohtava();
        mainWindowPageObject.openHakuTab();
        waitForText(I18N.getMessage("haku.otsikko"));
    }

}
