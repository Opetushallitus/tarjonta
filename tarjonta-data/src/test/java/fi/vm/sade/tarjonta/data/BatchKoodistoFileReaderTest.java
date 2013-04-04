package fi.vm.sade.tarjonta.data;

import fi.vm.sade.koodisto.service.KoodiAdminService;
import fi.vm.sade.koodisto.service.KoodiService;
import fi.vm.sade.koodisto.service.KoodistoAdminService;
import fi.vm.sade.koodisto.service.KoodistoService;
import fi.vm.sade.koodisto.service.types.*;
import fi.vm.sade.koodisto.service.types.common.*;
import fi.vm.sade.tarjonta.data.loader.xls.KoodistoRelaatioExcelReader;
import fi.vm.sade.tarjonta.data.util.TarjontaDataKoodistoHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners(listeners = {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@ContextConfiguration("classpath:spring/test-context.xml")
public class BatchKoodistoFileReaderTest {
    private final Logger log = LoggerFactory.getLogger(BatchKoodistoFileReaderTest.class);

    @Autowired
    private BatchKoodistoFileReader reader;

    @Autowired
    private UploadKoodistoData uploadKoodistoData;

    @Autowired
    private TarjontaDataKoodistoHelper koodistoHelper;

    @Autowired
    private KoodistoRelaatioExcelReader koodistoRelaatioExcelReader;

    private KoodistoAdminService koodistoAdminService;
    private KoodiAdminService koodiAdminService;
    private KoodiService koodiService;
    private KoodistoService koodistoService;

    @Before
    public void setUp() {
        // override services with mocks
        koodistoAdminService = mock(KoodistoAdminService.class);
        ReflectionTestUtils.setField(koodistoHelper, "koodistoAdminService", koodistoAdminService);
        koodiAdminService = mock(KoodiAdminService.class);
        ReflectionTestUtils.setField(koodistoHelper, "koodiAdminService", koodiAdminService);
        koodiService = mock(KoodiService.class);
        ReflectionTestUtils.setField(koodistoHelper, "koodiService", koodiService);
        koodistoService = mock(KoodistoService.class);
        ReflectionTestUtils.setField(koodistoHelper, "koodistoService", koodistoService);

        ReflectionTestUtils.setField(reader, "uploadKoodistoData", uploadKoodistoData);
        ReflectionTestUtils.setField(koodistoRelaatioExcelReader, "koodistoHelper", koodistoHelper);
        ReflectionTestUtils.setField(uploadKoodistoData, "koodistoHelper", koodistoHelper);
        ReflectionTestUtils.setField(uploadKoodistoData, "koodistoRelaatioExcelReader", koodistoRelaatioExcelReader);
    }

    @Test
    public void testRead() {
        // setup mocked return values
        when(koodistoAdminService.createKoodisto(any(List.class), any(CreateKoodistoDataType.class))).thenReturn(new KoodistoType());
        when(koodistoAdminService.updateKoodisto(any(UpdateKoodistoDataType.class))).thenReturn(new KoodistoType());
        when(koodiAdminService.createKoodi(any(String.class), any(CreateKoodiDataType.class))).thenReturn(new KoodiType());
        when(koodiAdminService.updateKoodi(any(UpdateKoodiDataType.class))).thenReturn(new KoodiType());
        final SearchKoodistosCriteriaType searchKausiKoodisto = new SearchKoodistosCriteriaType();
        searchKausiKoodisto.setKoodistoVersioSelection(SearchKoodistosVersioSelectionType.LATEST);
        searchKausiKoodisto.getKoodistoUris().add("kausi");
        when(koodistoService.searchKoodistos(searchKausiKoodisto)).thenReturn(Collections.singletonList(new KoodistoType()));
        final SearchKoodistosCriteriaType searchKuntaKoodisto = new SearchKoodistosCriteriaType();
        searchKuntaKoodisto.setKoodistoVersioSelection(SearchKoodistosVersioSelectionType.LATEST);
        searchKuntaKoodisto.getKoodistoUris().add("kunta");
        when(koodistoService.searchKoodistos(searchKuntaKoodisto)).thenReturn(Collections.singletonList(new KoodistoType()));

        reader.read();

        // verify create koodistoUri
        final ArgumentCaptor<List> koodistoUriList = ArgumentCaptor.forClass(List.class);
        final ArgumentCaptor<CreateKoodistoDataType> createKoodisto = ArgumentCaptor.forClass(CreateKoodistoDataType.class);
        verify(koodistoAdminService, times(2)).createKoodisto(koodistoUriList.capture(), createKoodisto.capture());
        final String capturedCreateKoodistoUri = createKoodisto.getValue().getMetadataList().get(0).getNimi();
        log.info("captured create koodistoUri {}", capturedCreateKoodistoUri);
        assertTrue("create koodistoUri should be kausi or kunta",
                StringUtils.equals(capturedCreateKoodistoUri, "kausi")
                        || StringUtils.equals(capturedCreateKoodistoUri, "kunta"));
        log.info("captured create koodistoRyhmaUris [{}]", StringUtils.join(koodistoUriList.getValue(), ", "));
        assertTrue("create koodistoRyhmaUris should contain http://kaikkikoodistot and http://alueet",
                CollectionUtils.containsAny(koodistoUriList.getValue(), Collections.singletonList("http://kaikkikoodistot"))
                        && CollectionUtils.containsAny(koodistoUriList.getValue(), Collections.singletonList("http://alueet")));

        // verify update koodistoUri
        final ArgumentCaptor<UpdateKoodistoDataType> updateKoodisto = ArgumentCaptor.forClass(UpdateKoodistoDataType.class);
        verify(koodistoAdminService, times(2)).updateKoodisto(updateKoodisto.capture());
        final TilaType capturedTila = updateKoodisto.getValue().getTila();
        log.info("captured tila {}", capturedTila.value());
        assertEquals("HYVAKSYTTY", capturedTila.value());

        // verify create koodi arvo
        final ArgumentCaptor<CreateKoodiDataType> createKoodi = ArgumentCaptor.forClass(CreateKoodiDataType.class);
        verify(koodiAdminService, times(4)).createKoodi(any(String.class), createKoodi.capture());
        final String capturedCreateKoodiArvo = createKoodi.getValue().getKoodiArvo();
        log.info("captured create koodi arvo {}", capturedCreateKoodiArvo);
        assertTrue("create koodi arvo should be alpha-numeric-space", StringUtils.isAlphanumericSpace(capturedCreateKoodiArvo));

        // verify suhteentyyppi
        final ArgumentCaptor<SuhteenTyyppiType> createSuhteentyyppi = ArgumentCaptor.forClass(SuhteenTyyppiType.class);
        verify(koodiAdminService, times(4)).addRelationByAlakoodi(any(String.class), anyListOf(String.class), createSuhteentyyppi.capture());
        log.info("captured suhteentyyppi {}", createSuhteentyyppi.getValue().value());
        assertEquals("SISALTYY", createSuhteentyyppi.getValue().value());
    }

    @Test
    public void testProgress() {
        assertEquals("Progress: [##########          ] 50 % done", reader.getProgress(1, 2));
        assertEquals("Progress: [####################] 100 % done", reader.getProgress(10, 10));
        assertEquals("Progress: [####                ] 20 % done", reader.getProgress(20, 100));
        assertEquals("[invalid]", reader.getProgress(1, 0));
    }
}
