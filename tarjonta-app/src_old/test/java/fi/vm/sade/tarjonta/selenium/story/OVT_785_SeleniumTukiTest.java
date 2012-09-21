package fi.vm.sade.tarjonta.selenium.story;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.tarjonta.selenium.TarjontaEmbedComponentTstSupport;
import fi.vm.sade.tarjonta.selenium.pageobject.KoulutusmoduuliEditViewPageObject;
import fi.vm.sade.tarjonta.ui.MainWindow;
import org.junit.Test;

import static fi.vm.sade.support.selenium.SeleniumUtils.waitForText;
import static org.junit.Assert.assertNotNull;

/**
 * @author Antti Salonen
 */
public class OVT_785_SeleniumTukiTest extends TarjontaEmbedComponentTstSupport<MainWindow> {

    @Test
    public void tarjontaMainWindowWorksInEmbedVaadinSeleniumTest() {
        assertNotNull(mainWindowPageObject.getComponent());
        assertNotNull(mainWindowPageObject.getComponent().getKoulutusmoduuliEditView());

        waitForText(I18N.getMessage("tarjonta.tabs.koulutusmoduulit"));
        KoulutusmoduuliEditViewPageObject editor = mainWindowPageObject.selectLuoUusiTutkintoonJohtava();
        waitForText(I18N.getMessage("TutkintoOhjelmaFormModel.organisaatioStatus.notSaved"));
    }

}
