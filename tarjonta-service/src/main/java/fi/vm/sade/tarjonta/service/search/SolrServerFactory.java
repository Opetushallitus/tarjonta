package fi.vm.sade.tarjonta.service.search;

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
@Profile("default")
public class SolrServerFactory implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Value("${tarjonta.solr.baseurl:}")
    protected String solrBaseUrl;

    public SolrServer getSolrServer(final String coreName) {
        final String url = solrBaseUrl + "/" + coreName;
        logger.info("instantiating new solr client with url {}", url);
        return new HttpSolrServer(url);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Preconditions.checkNotNull(solrBaseUrl,
                "Solr baseurl not specified, application will not work!");
    }

}
