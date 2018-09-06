package fi.vm.sade.tarjonta;

import fi.vm.sade.javautils.httpclient.*;

import java.io.IOException;

public class TestOphHttpClientProxy extends OphHttpClientProxy {

    private final OphHttpClientProxyRequest request;
    private final OphHttpResponse response;

    public TestOphHttpClientProxy(OphHttpResponse response) {
        this.request = new TestOphHttpClientProxyRequest(response);
        this.response = response;
    }

    @Override
    public OphHttpClientProxyRequest createRequest(OphRequestParameters requestParameters) {
        return request;
    }

    @Override
    public void close() throws Exception {
        response.close();
    }

    private static class TestOphHttpClientProxyRequest implements OphHttpClientProxyRequest {

        private final OphHttpResponse response;

        public TestOphHttpClientProxyRequest(OphHttpResponse response) {
            this.response = response;
        }

        @Override
        public <R> R execute(OphHttpResponseHandler<? extends R> handler) throws IOException {
            return handler.handleResponse(response);
        }

        @Override
        public OphHttpResponse handleManually() throws IOException {
            return response;
        }

    }

}
