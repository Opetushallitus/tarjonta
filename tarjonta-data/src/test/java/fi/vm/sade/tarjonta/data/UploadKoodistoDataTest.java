package fi.vm.sade.tarjonta.data;

import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import fi.vm.sade.koodisto.util.KoodiServiceSearchCriteriaBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import fi.vm.sade.oid.service.ExceptionMessage;
import fi.vm.sade.tarjonta.data.util.TarjontaDataKoodistoHelper;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.assertTrue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ContextConfiguration(locations = "classpath:spring/context.xml")
@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class UploadKoodistoDataTest {

    private final Logger LOG = LoggerFactory.getLogger(UploadKoodistoDataTest.class);
    @Autowired
    private UploadKoodistoData up;
    @Autowired
    private KoodiService koodiService;

    @Test
    public void testCreate() throws IOException, ExceptionMessage {
       // up.startFullImport();
        // assertTrue(true);
        String kuvausRyhmaUri =  "t2_koulutuslaji";//"921 Sosiaali- ja terveysalan perustutkinto, er";
        KoodiUriAndVersioType koodiUriAndVersionType = new KoodiUriAndVersioType();
        koodiUriAndVersionType.setKoodiUri(kuvausRyhmaUri);
        koodiUriAndVersionType.setVersio(1);
        koodiUriAndVersionType.setKoodiUri("t2_koulutuslaji");
        //        List<KoodiType> listKoodiByRelation = koodiService.listKoodiByRelation(koodiUriAndVersionType, false, SuhteenTyyppiType.SISALTYY);
        //        LOG.debug("{} has uris : {}", kuvausRyhmaUri, listKoodiByRelation);
        //        List<KoodiType> a = koodiService.listKoodiByRelation(koodiUriAndVersionType, true, SuhteenTyyppiType.SISALTYY);
        //        LOG.debug("{} has uris : {}", kuvausRyhmaUri, a);
        //        List<KoodiType> b = koodiService.listKoodiByRelation(koodiUriAndVersionType, true, SuhteenTyyppiType.RINNASTEINEN);
        //        LOG.debug("{} has uris : {}", kuvausRyhmaUri, b);
        //        List<KoodiType> c = koodiService.listKoodiByRelation(koodiUriAndVersionType, false, SuhteenTyyppiType.RINNASTEINEN);
        //        LOG.debug("{} has uris : {}", kuvausRyhmaUri, c);
        ////
        ////        kuvausRyhmaUri = "t2_hakukohde/612";
        ////        koodiUriAndVersionType = new KoodiUriAndVersioType();
        ////        koodiUriAndVersionType.setKoodiUri(kuvausRyhmaUri);
        ////        koodiUriAndVersionType.setVersio(1);
        //
        List<KoodiType> searchKoodisByKoodisto = koodiService.searchKoodisByKoodisto( KoodiServiceSearchCriteriaBuilder.koodisByKoodistoUri(kuvausRyhmaUri));
       LOG.debug("{} has uris : {}", kuvausRyhmaUri, searchKoodisByKoodisto);
//        List<KoodiType> listKoodiByRelation1 = koodiService.listKoodiByRelation(koodiUriAndVersionType, false, SuhteenTyyppiType.SISALTYY);
//        LOG.debug("{} has uris : {}", kuvausRyhmaUri, listKoodiByRelation1);
//        List<KoodiType> a1 = koodiService.listKoodiByRelation(koodiUriAndVersionType, true, SuhteenTyyppiType.SISALTYY);
//        LOG.debug("{} has uris : {}", kuvausRyhmaUri, a1);
//        List<KoodiType> b1 = koodiService.listKoodiByRelation(koodiUriAndVersionType, true, SuhteenTyyppiType.RINNASTEINEN);
//        LOG.debug("{} has uris : {}", kuvausRyhmaUri, b1);
//        List<KoodiType> c1 = koodiService.listKoodiByRelation(koodiUriAndVersionType, false, SuhteenTyyppiType.RINNASTEINEN);
//        LOG.debug("{} has uris : {}", kuvausRyhmaUri, c1);
    }
}
