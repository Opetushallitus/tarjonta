package fi.vm.sade.tarjonta.shared;

import fi.vm.sade.javautils.httpclient.*;
import fi.vm.sade.koodisto.service.types.SearchKoodisCriteriaType;
import fi.vm.sade.koodisto.service.types.common.KoodiType;
import fi.vm.sade.koodisto.service.types.common.KoodiUriAndVersioType;
import fi.vm.sade.koodisto.service.types.common.SuhteenTyyppiType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.io.*;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class KoodiServiceImplTest {

    private KoodiServiceImpl koodiServiceImpl;

    private OphHttpClientProxy httpClientProxyMock;
    private OphHttpResponse httpResponseMock;

    @Before
    public void setup() throws IOException {
        OphHttpClientProxyRequest httpClientProxyRequestMock = mock(OphHttpClientProxyRequest.class);
        when(httpClientProxyRequestMock.execute(any())).thenAnswer(invocation
                -> ((OphHttpResponseHandler) invocation.getArguments()[0]).handleResponse(httpResponseMock));
        when(httpClientProxyRequestMock.handleManually()).thenReturn(httpResponseMock);
        httpClientProxyMock = mock(OphHttpClientProxy.class);
        when(httpClientProxyMock.createRequest(any())).thenReturn(httpClientProxyRequestMock);
        httpResponseMock = mock(OphHttpResponse.class);
        when(httpResponseMock.getStatusCode()).thenReturn(200);

        UrlConfiguration properties = new UrlConfiguration();
        properties.addOverride("host.virkailija", "localhost");
        OphHttpClient httpClient = new OphHttpClient(httpClientProxyMock, HttpClientConfiguration.CALLER_ID, properties);

        koodiServiceImpl = new KoodiServiceImpl(httpClient);
    }

    @Test
    public void listKoodiByRelationWithRinnasteinen() {
        when(httpResponseMock.asInputStream()).thenReturn(new ByteArrayInputStream("[]".getBytes()));
        KoodiUriAndVersioType koodiUriAndVersioType = new KoodiUriAndVersioType();
        koodiUriAndVersioType.setKoodiUri("uri1");
        koodiUriAndVersioType.setVersio(1);

        List<KoodiType> koodiTypes = koodiServiceImpl.listKoodiByRelation(koodiUriAndVersioType, false, SuhteenTyyppiType.RINNASTEINEN);

        assertTrue(koodiTypes.isEmpty());
        ArgumentCaptor<OphRequestParameters> requestParametersArgumentCaptor = ArgumentCaptor.forClass(OphRequestParameters.class);
        verify(httpClientProxyMock).createRequest(requestParametersArgumentCaptor.capture());
        OphRequestParameters requestParameters = requestParametersArgumentCaptor.getValue();
        assertEquals("https://localhost/koodisto-service/rest/json/relaatio/rinnasteinen/uri1?koodiVersio=1", requestParameters.url);
    }

    @Test
    public void listKoodiByRelationWithSisaltyyAndOnAlakoodiFalse() {
        when(httpResponseMock.asInputStream()).thenReturn(new ByteArrayInputStream("[]".getBytes()));
        KoodiUriAndVersioType koodiUriAndVersioType = new KoodiUriAndVersioType();
        koodiUriAndVersioType.setKoodiUri("uri1");
        koodiUriAndVersioType.setVersio(1);

        List<KoodiType> koodiTypes = koodiServiceImpl.listKoodiByRelation(koodiUriAndVersioType, false, SuhteenTyyppiType.SISALTYY);

        assertTrue(koodiTypes.isEmpty());
        ArgumentCaptor<OphRequestParameters> requestParametersArgumentCaptor = ArgumentCaptor.forClass(OphRequestParameters.class);
        verify(httpClientProxyMock).createRequest(requestParametersArgumentCaptor.capture());
        OphRequestParameters requestParameters = requestParametersArgumentCaptor.getValue();
        assertEquals("https://localhost/koodisto-service/rest/json/relaatio/sisaltyy-alakoodit/uri1?koodiVersio=1", requestParameters.url);
    }

    @Test
    public void listKoodiByRelationWithSisaltyyAndOnAlakoodiTrue() {
        when(httpResponseMock.asInputStream()).thenReturn(new ByteArrayInputStream("[]".getBytes()));
        KoodiUriAndVersioType koodiUriAndVersioType = new KoodiUriAndVersioType();
        koodiUriAndVersioType.setKoodiUri("uri1");
        koodiUriAndVersioType.setVersio(1);

        List<KoodiType> koodiTypes = koodiServiceImpl.listKoodiByRelation(koodiUriAndVersioType, true, SuhteenTyyppiType.SISALTYY);

        assertTrue(koodiTypes.isEmpty());
        ArgumentCaptor<OphRequestParameters> requestParametersArgumentCaptor = ArgumentCaptor.forClass(OphRequestParameters.class);
        verify(httpClientProxyMock).createRequest(requestParametersArgumentCaptor.capture());
        OphRequestParameters requestParameters = requestParametersArgumentCaptor.getValue();
        assertEquals("https://localhost/koodisto-service/rest/json/relaatio/sisaltyy-ylakoodit/uri1?koodiVersio=1", requestParameters.url);
    }

    @Test
    public void searchKoodisByKoodisto() {
        when(httpResponseMock.asInputStream()).thenReturn(new ByteArrayInputStream("[]".getBytes()));
        String koodistoUri = "koodisto1";
        String koodiArvo = "koodi1";

        List<KoodiType> koodiTypes = koodiServiceImpl.searchKoodisByKoodisto(koodistoUri, koodiArvo);

        assertTrue(koodiTypes.isEmpty());
        ArgumentCaptor<OphRequestParameters> requestParametersArgumentCaptor = ArgumentCaptor.forClass(OphRequestParameters.class);
        verify(httpClientProxyMock).createRequest(requestParametersArgumentCaptor.capture());
        OphRequestParameters requestParameters = requestParametersArgumentCaptor.getValue();
        assertEquals("https://localhost/koodisto-service/rest/json/koodisto1/koodi/arvo/koodi1", requestParameters.url);
    }

    @Test
    public void searchKoodisWithEmptyCriteria() {
        when(httpResponseMock.asInputStream()).thenReturn(new ByteArrayInputStream("[]".getBytes()));
        SearchKoodisCriteriaType searchCriteria = new SearchKoodisCriteriaType();

        List<KoodiType> koodiTypes = koodiServiceImpl.searchKoodis(searchCriteria);

        assertTrue(koodiTypes.isEmpty());
        ArgumentCaptor<OphRequestParameters> requestParametersArgumentCaptor = ArgumentCaptor.forClass(OphRequestParameters.class);
        verify(httpClientProxyMock).createRequest(requestParametersArgumentCaptor.capture());
        OphRequestParameters requestParameters = requestParametersArgumentCaptor.getValue();
        assertEquals("https://localhost/koodisto-service/rest/json/searchKoodis", requestParameters.url);
    }

    @Test
    public void searchKoodisWithValidAt() throws DatatypeConfigurationException {
        when(httpResponseMock.asInputStream()).thenReturn(new ByteArrayInputStream("[]".getBytes()));
        SearchKoodisCriteriaType searchCriteria = new SearchKoodisCriteriaType();
        GregorianCalendar gregorianCalendar = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));
        gregorianCalendar.set(2018, 8, 6, 12, 35, 52);
        gregorianCalendar.set(GregorianCalendar.MILLISECOND, 216);
        searchCriteria.setValidAt(DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar));

        List<KoodiType> koodiTypes = koodiServiceImpl.searchKoodis(searchCriteria);

        assertTrue(koodiTypes.isEmpty());
        ArgumentCaptor<OphRequestParameters> requestParametersArgumentCaptor = ArgumentCaptor.forClass(OphRequestParameters.class);
        verify(httpClientProxyMock).createRequest(requestParametersArgumentCaptor.capture());
        OphRequestParameters requestParameters = requestParametersArgumentCaptor.getValue();
        assertEquals("https://localhost/koodisto-service/rest/json/searchKoodis?validAt=2018-09-06T19%3A35%3A52.216Z", requestParameters.url);
    }

    @Test
    public void searchKoodisWithResponse() throws FileNotFoundException {
        when(httpResponseMock.asInputStream()).thenReturn(new FileInputStream(new File("src/test/resources/koodisto-kieli.json")));
        SearchKoodisCriteriaType searchCriteria = new SearchKoodisCriteriaType();

        List<KoodiType> koodiTypes = koodiServiceImpl.searchKoodis(searchCriteria);

        assertTrue(koodiTypes.size() == 2);
        ArgumentCaptor<OphRequestParameters> requestParametersArgumentCaptor = ArgumentCaptor.forClass(OphRequestParameters.class);
        verify(httpClientProxyMock).createRequest(requestParametersArgumentCaptor.capture());
        OphRequestParameters requestParameters = requestParametersArgumentCaptor.getValue();
        assertEquals("https://localhost/koodisto-service/rest/json/searchKoodis", requestParameters.url);
    }

}
