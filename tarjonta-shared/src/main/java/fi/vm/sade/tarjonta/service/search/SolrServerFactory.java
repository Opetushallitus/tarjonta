package fi.vm.sade.tarjonta.service.search;

//import org.apache.http.client.HttpClient;
//import org.apache.http.impl.NoConnectionReuseStrategy;
//import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

@Component
@Profile(value = {"default", "solr"})
public class SolrServerFactory implements InitializingBean {

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
        PoolingClientConnectionManager mgr = new PoolingClientConnectionManager();
        DefaultHttpClient client = new DefaultHttpClient(mgr);
        HttpParams params = client.getParams();
        params.setBooleanParameter(AllClientPNames.STALE_CONNECTION_CHECK, Boolean.TRUE);
//        HttpConnectionParams.setConnectionTimeout(params, 30);
//        HttpConnectionParams.setSoTimeout(params, 60);     
        return new HttpSolrServer(url, client);
        
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Preconditions.checkNotNull(solrBaseUrl,
                "Solr baseurl not specified, application will not work!");
    }

}
