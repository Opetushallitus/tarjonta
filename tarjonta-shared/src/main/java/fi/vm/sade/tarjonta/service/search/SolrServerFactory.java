package fi.vm.sade.tarjonta.service.search;

import com.google.common.base.Preconditions;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(value = {"default", "solr"})
public class SolrServerFactory implements InitializingBean {

  private static int TIMEOUT_MILLISECONDS = 10000;

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

  HttpRequestRetryHandler rh;

  private SolrServer getSolr(final String url) {
    PoolingClientConnectionManager mgr = new PoolingClientConnectionManager();
    mgr.setDefaultMaxPerRoute(20);
    mgr.setDefaultMaxPerRoute(100);
    DefaultHttpClient client = new DefaultHttpClient(mgr);
    HttpParams params = client.getParams();
    HttpConnectionParams.setStaleCheckingEnabled(params, true);
    HttpConnectionParams.setSoTimeout(params, TIMEOUT_MILLISECONDS);
    client.setHttpRequestRetryHandler(new StandardHttpRequestRetryHandler(3, true));
    return new HttpSolrServer(url, client);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    Preconditions.checkNotNull(
        solrBaseUrl, "Solr baseurl not specified, application will not work!");
  }
}
