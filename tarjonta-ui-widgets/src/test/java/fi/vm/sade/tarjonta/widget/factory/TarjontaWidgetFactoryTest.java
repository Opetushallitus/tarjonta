package fi.vm.sade.tarjonta.widget.factory;

import com.vaadin.data.util.NestedMethodProperty;
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
import fi.vm.sade.tarjonta.service.mock.KoulutusmoduuliAdminServiceMock;
import fi.vm.sade.tarjonta.service.mock.KoulutusmoduuliStorage;
import static org.junit.Assert.assertEquals;
import org.junit.Before;

/**
 * @author Antti Salonen
 */
@ContextConfiguration("classpath:test-context.xml")
public class TarjontaWidgetFactoryTest extends AbstractEmbedVaadinTest<KoulutusmoduuliComponent> {

    private KoulutusmoduuliAdminService koulutusmoduuliAdminService = new KoulutusmoduuliAdminServiceMock(new KoulutusmoduuliStorage());

    @Autowired
    TarjontaWidgetFactory tarjontaWidgetFactory;

    TestDTO dto = new TestDTO();

    public TarjontaWidgetFactoryTest() {
        super(true, true);
    }

    @Override
    public void initPageObjects() {
    }
    
    
    @Override
    protected KoulutusmoduuliComponent createComponent() throws Exception {

        tarjontaWidgetFactory.setKoulutusmoduuliService(koulutusmoduuliAdminService);
        
        // init mock service with some data
        createKoulutusmoduuli("koulutusmoduuli0");
        createKoulutusmoduuli("koulutusmoduuli1");
        createKoulutusmoduuli("koulutusmoduuli2");

        // create component
        KoulutusmoduuliSearchDTO searchSpecification = new KoulutusmoduuliSearchDTO();
        searchSpecification.setNimi("ANYTHING_FROM_MOCK");
        KoulutusmoduuliComponent koulutusmoduuliComponent = tarjontaWidgetFactory.createKoulutusmoduuliComponentWithCombobox(searchSpecification);
        koulutusmoduuliComponent.setImmediate(true);
        koulutusmoduuliComponent.setPropertyDataSource(new NestedMethodProperty(dto, "koulutusmoduuliOid"));
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

        STEP("arvo päätyy myös dto:lle");
        component.commit();
        assertEquals("oid_koulutusmoduuli2", dto.getKoulutusmoduuliOid());

    }

    private void createKoulutusmoduuli(String nimi) {
        TutkintoOhjelmaDTO koulutusModuuli = new TutkintoOhjelmaDTO();
        koulutusModuuli.setNimi(nimi);
        koulutusModuuli.setOid("oid_" + nimi);
        koulutusmoduuliAdminService.save(koulutusModuuli);
    }

    public static class TestDTO {

        private String koulutusmoduuliOid;

        public String getKoulutusmoduuliOid() {
            return koulutusmoduuliOid;
        }

        public void setKoulutusmoduuliOid(String koulutusmoduuliOid) {
            this.koulutusmoduuliOid = koulutusmoduuliOid;
        }

    }


}