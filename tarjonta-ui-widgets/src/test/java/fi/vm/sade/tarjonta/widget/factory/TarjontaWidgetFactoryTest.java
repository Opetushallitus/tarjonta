package fi.vm.sade.tarjonta.widget.factory;

import fi.vm.sade.support.selenium.AbstractEmbedVaadinTest;
import fi.vm.sade.tarjonta.model.dto.KoulutusmoduuliSearchDTO;
import fi.vm.sade.tarjonta.model.dto.TutkintoOhjelmaDTO;
import fi.vm.sade.tarjonta.service.KoulutusmoduuliAdminService;
import fi.vm.sade.tarjonta.widget.KoulutusmoduuliComponent;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static fi.vm.sade.support.selenium.SeleniumUtils.*;
import static org.junit.Assert.assertEquals;

/**
 * @author Antti Salonen
 */
@ContextConfiguration("classpath:test-context.xml")
public class TarjontaWidgetFactoryTest extends AbstractEmbedVaadinTest<KoulutusmoduuliComponent> {

    @Autowired
    KoulutusmoduuliAdminService koulutusmoduuliAdminService;

    @Autowired
    TarjontaWidgetFactory tarjontaWidgetFactory;

    public TarjontaWidgetFactoryTest() {
        super(true, true);
    }

    @Override
    public void initPageObjects() {
    }

    @Override
    protected KoulutusmoduuliComponent createComponent() throws Exception {

        // init mock service with some data
        createKoulutusmoduuli("koulutusmoduuli0");
        createKoulutusmoduuli("koulutusmoduuli1");
        createKoulutusmoduuli("koulutusmoduuli2");

        // create component
        KoulutusmoduuliSearchDTO searchSpecification = new KoulutusmoduuliSearchDTO();
        searchSpecification.setNimi("ANYTHING_FROM_MOCK");
        KoulutusmoduuliComponent koulutusmoduuliComponent = tarjontaWidgetFactory.createKoulutusmoduuliComponentWithCombobox(searchSpecification);
        koulutusmoduuliComponent.setImmediate(true);
        return koulutusmoduuliComponent;
    }

    @Test
    public void testKoulutusmoduuliComponent() throws Throwable {

        STEP("KoulutusmoduuliComponent näyttää vaihtoehtoina servicestä löytyvien koulutusmoduulien nimet");
        List<String> options = getOptions(waitForElement(component.getField()));
        assertEquals(4, options.size());
        assertEquals(" ", options.get(0));
        assertEquals("koulutusmoduuli0", options.get(1));
        assertEquals("koulutusmoduuli1", options.get(2));
        assertEquals("koulutusmoduuli2", options.get(3));

        STEP("kun koulutusmoduulin valitsee, arvoksi tallennetaan koulutusmoduulin oid");
        select(component, "koulutusmoduuli2");
        waitAssert(new AssertionCallback() {
            @Override
            public void doAssertion() throws Throwable {
                assertEquals("oid_koulutusmoduuli2", component.getValue());
            }
        });

        STEP("arvo päätyy myös dto:lle"); // TODO: dto bindaus

    }

    private void createKoulutusmoduuli(String nimi) {
        TutkintoOhjelmaDTO koulutusModuuli = new TutkintoOhjelmaDTO();
        koulutusModuuli.setNimi(nimi);
        koulutusModuuli.setOid("oid_"+nimi);
        koulutusmoduuliAdminService.save(koulutusModuuli);
    }

}