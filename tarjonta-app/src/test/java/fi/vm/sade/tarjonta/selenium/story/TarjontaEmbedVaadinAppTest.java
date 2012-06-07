package fi.vm.sade.tarjonta.selenium.story;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.support.selenium.AbstractEmbedVaadinAppTest;
import fi.vm.sade.tarjonta.ui.TarjontaApplication;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import static fi.vm.sade.support.selenium.SeleniumUtils.waitForText;

/**
 * @author Antti Salonen
 */
@ContextConfiguration("classpath:spring/application-context.xml")
public class TarjontaEmbedVaadinAppTest extends AbstractEmbedVaadinAppTest<TarjontaApplication> {

    @Override
    public void initPageObjects() {
    }

    @Test
    public void tarjontaApplicationWorks() throws Exception {
        waitForText(I18N.getMessage("tarjonta.tabs.koulutusmoduulit"));
    }

}
