package fi.vm.sade.tarjonta.service.search;

import java.io.IOException;

import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

@Component
@Profile(value = {"default", "solr"})
public class SolrServerFactory implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Value("${tarjonta.solr.baseurl:}")
    protected String solrBaseUrl;
    
    @Value("${organisaatio.solr.url:}")
    protected String organisaatioSolrUrl;

    public SolrServer getOrganisaatioSolrServer() {
        return getSolr(organisaatioSolrUrl);
    }

    public SolrServer getSolrServer(final String coreName) {
        final String url = solrBaseUrl + "/" + coreName;
        return getSolr(url);
    }

    private SolrServer getSolr(final String url) {
        
        DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.addRequestInterceptor(new HttpRequestInterceptor() {
            
            @Override
            public void process(HttpRequest request, HttpContext context)
                    throws HttpException, IOException {
                request.removeHeaders("Connection");
                request.addHeader("Connection", "close");
            }
        });
        
        return new HttpSolrServer(url, httpclient);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Preconditions.checkNotNull(solrBaseUrl,
                "Solr baseurl not specified, application will not work!");
    }

}
