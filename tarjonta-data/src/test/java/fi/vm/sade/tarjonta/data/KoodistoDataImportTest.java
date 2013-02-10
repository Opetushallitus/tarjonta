package fi.vm.sade.tarjonta.data;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import fi.vm.sade.oid.service.ExceptionMessage;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;

@ContextConfiguration(locations = "classpath:spring/context.xml")
@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class KoodistoDataImportTest {

    @Autowired
    private UploadKoodistoData up;

    @Test
    public void testCreate() throws IOException, ExceptionMessage {
        up.startFullImport();
    }
}
